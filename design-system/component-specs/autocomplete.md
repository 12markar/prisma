# Autocomplete

A text input that suggests matching values as the user types. Distinguished from a select: the input is freely editable, suggestions are advisory, and (in some variants) the user can submit a value not in the list.

## Anatomy
```
┌──────────────────────────────────┐
│ 🔍 catal█                    ⌫   │  ← Input
└──────────────────────────────────┘
   ┌──────────────────────────────┐
   │ ▸ Catalogues                 │  ← Active row
   │   Catalog of items           │
   │   Catalogue · 23 items       │
   │   Catalonia                  │
   └──────────────────────────────┘
```
- **Input**: matches text-field component spec — same height, padding, border. Leading icon optional.
- **Suggestion list**: popover, anchored to input start, width = input width (min). `--surface-raised`, 1px `--border-subtle`, `--radius-md`, `--shadow-3`. Max-height 280px (≈7 rows), then scroll.
- **Row**: 36px tall (default), 44px (touch). Padding 8px 12px. Same active/hover model as command palette.

## Match highlighting
Highlight the matched substring in each row using `--text-primary` weight 600 against unmatched `--text-secondary`. Highlight all occurrences, not just the first. For multi-word queries, highlight every matched word independently.

Don't use a colored background for highlighting — the weight contrast is enough and survives theme switching cleanly.

## States
- **Empty input**: list closed (default) OR shows recent picks (opt-in via `showRecentsOnFocus`).
- **Typing**: list opens. Debounce **120ms** before calling out to async sources; render synchronous filtering immediately.
- **No matches**: list shows a single non-interactive row "No results for '<query>'" + (optional) "Use as new value →" if `allowFreeEntry`.
- **Loading** (async): the input shows a spinner where the clear button would be. List shows skeleton rows (3 rows of shimmer) — never an empty box.

## Selection commit
Three ways to commit:
1. Click a row.
2. Press Enter on the active row.
3. (Free-entry mode) Press Enter with no row active — commits the current input text.

On commit, the list closes and focus stays on the input.

## Free-entry vs strict
- **Strict** (default): user must pick an existing value. Submitting an unmatched query shows the "no matches" row only.
- **Free-entry**: any text is acceptable. The list is advisory. Show a hint row: "Press Enter to use '<query>'".

## Async data
- Debounce 120ms after last keystroke before firing the request.
- Cancel in-flight requests when the input changes — don't render stale results.
- Show the cached previous result with a subtle dimming (75% opacity) while the new request is in flight, instead of clearing to skeletons. Reduces flicker on fast typing.

## Motion
- List open: 160ms fade + 4px slide-in along the popover's caret axis (here always downward).
- List close: 100ms fade only.
- Active-row indicator: snap, no slide. Same rationale as command palette.

## Accessibility
- `role="combobox"` on input, `aria-expanded`, `aria-controls`, `aria-activedescendant`.
- `role="listbox"` on the suggestion list, `role="option"` on rows, `aria-selected` on active row.
- Live region announces match count: "12 matches" (debounced 350ms).
- Escape closes the list without committing. Pressing Escape twice clears the input.

## Tokens
| Property | Token |
|---|---|
| List bg | `--surface-raised` |
| Active row bg | `--accent-subtle` |
| Active row accent bar | `--accent-default` |
| Highlight text | `--text-primary` weight 600 |
| Skeleton fill | `--surface-sunken` |

## Don'ts
- Don't auto-select on hover. Active state is keyboard-driven.
- Don't render more than ~50 rows; virtualize beyond that.
- Don't use autocomplete for ≤5 known options — use a select.
- Don't auto-commit on blur unless you're absolutely sure the user wants the active row. Default = revert to last committed value.
