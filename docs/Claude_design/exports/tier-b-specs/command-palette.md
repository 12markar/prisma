# Command Palette

A keyboard-first search-and-act surface. Floats above the app, accepts a query, returns ranked results across multiple groups, executes the chosen action and dismisses.

## Anatomy
```
┌─────────────────────────────────────────┐
│ 🔍  Type a command or search…    ⌘K     │  ← Input
├─────────────────────────────────────────┤
│ RECENT                                  │  ← Group label
│   ◷ Open last catalogue                 │  ← Result row
│   ⤴ Share with team                     │
├─────────────────────────────────────────┤
│ NAVIGATION (3)                          │
│ ▸ Settings              ⌘ ,             │
│   Inventory                             │
│   Reports                               │
├─────────────────────────────────────────┤
│ ACTIONS                                 │
│   + New item                            │
└─────────────────────────────────────────┘
   ↑↓ navigate    ↵ select    esc close
```
- **Frame**: `--surface-raised`, `--radius-xl` (16px), `--shadow-5`. Width clamp(480px, 60vw, 720px). Max height 60vh.
- **Position**: top-centered with 12vh from top edge of viewport. On mobile, expands to full-width sheet (see bottom-sheet.md).
- **Input**: 56px tall, no border, font-size 16px (prevents iOS zoom). Leading search icon, trailing kbd chip.
- **Footer**: `--surface-sunken`, 12px tall, `--font-mono` 11px, hint text. Hide on mobile.

## Visual hierarchy of grouped results

### Group label
- `--font-mono`, 11px, `--text-tertiary`, uppercase, 0.08em letter-spacing.
- Sticky to its group's top while scrolling through long groups (`position: sticky`).
- Optional count suffix: `NAVIGATION (3)`. Only when filtering to a subset of group items.
- 12px top padding on the group, 8px bottom — labels breathe.

### Result row
- 40px tall (default), 48px (touch).
- **Leading**: 20px icon at `--text-tertiary` opacity 70%, brightens to 100% when active.
- **Title**: 14px, `--text-primary`, weight 400. Truncate at one line.
- **Subtitle** (optional): 12px, `--text-secondary`. Same line as title when narrow, second line when there's space.
- **Trailing**: kbd shortcut OR badge OR chevron — never two of these.
- **Active row**: `--accent-subtle` background (light) / `--accent-900` (dark), 1px left border `--accent-default` (3px wide).
- **Hover**: `--surface-sunken` background, no border accent. Hover loses to keyboard active state.

## Three states

### 1. Recent state
- Shown when input is **empty** AND user has prior commands.
- Group label: `RECENT` only — no other groups visible.
- Max 5 items. Reverse-chronological. Each item has a subtle dismiss-x on hover.
- Storage: localStorage (web) / UserDefaults (iOS) / DataStore (Android).

### 2. Results state
- Shown when input has **≥1 character**.
- Groups appear in order: **Navigation → Actions → Items → Help**. Within a group, results are ranked by fuzzy-match score.
- Score weight: title hit (1.0) > subtitle hit (0.6) > tag hit (0.3). Bonus for exact prefix (+0.5). Bonus for word-boundary match (+0.2).
- Max 7 items per group; show `(N more)` ghost row at bottom of capped groups, focusable, expands the group when activated.

### 3. No-results state
- Shown when input has ≥1 char AND no group has any matches.
- Single centered illustration (small, 64px — not the full empty-state piece).
- Headline: `No matches for "<query>".`
- Body: `Try a different word, or press Tab to search the web.`
- A single ghost action: `Search "<query>" on the web →` — gets active state by default, so Enter still does something.

## Motion
- **Open**: backdrop fade 0→1 (140ms), frame translate -12px→0 + scale 0.98→1 + opacity 0→1 (200ms `--ease-emphasized`).
- **Close**: reverse, 120ms `--ease-standard`.
- **Group transition** (recent ↔ results ↔ no-results): cross-fade 120ms only on the result region, never on the input or footer. Avoid layout thrash by using a constant inner height during transition.

## Keyboard
| Key | Action |
|---|---|
| ⌘K / Ctrl+K | Open from anywhere |
| Esc | Close (or clear input if non-empty) |
| ↑/↓ | Move active row, wraps |
| ⌘↑/⌘↓ | Jump to group start/end |
| ↵ | Activate row |
| ⇥ | Jump to "search the web" fallback |
| ⌫ on empty input | Close (mobile only) |

## Accessibility
- `role="combobox"` on input with `aria-expanded`, `aria-controls`, `aria-activedescendant`.
- `role="listbox"` on results region; `role="option"` on rows; `aria-selected` reflects active state.
- Group labels: `role="presentation"` — they're visual scaffolding, not landmarks.
- Live announcement on input change: `"23 results, navigation"` — debounce 350ms.

## Don'ts
- Don't mix navigation + destructive actions in the same group. Destructive actions go to a `DANGER` group at the bottom, with a confirmation step before execution.
- Don't show all groups when results state is sparse — hide empty groups, never render their labels.
- Don't animate the active-row indicator across rows (no traveling pill). Snap it. The arrow keys are fast; animation reads as lag.
- Don't auto-execute on a single match. Always require Enter — the user is in flow, surprise execution breaks it.
