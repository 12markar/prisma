# Tag Input

A text field that converts typed values into chips. Used for entering multiple discrete values: tags, recipients, keywords.

## Anatomy
```
┌─────────────────────────────────────────────┐
│ [vinyl ×] [jazz ×] [60s ×]  modern█         │
└─────────────────────────────────────────────┘
                                ↑ caret in pending input
```
- **Frame**: matches text-field spec for height (40px default), padding (8px). Min height 40px, grows in 8px increments as wrapping requires. `--radius-md`.
- **Chip**: 24px tall, `--surface-sunken` background, `--radius-sm` (6px), `--font-sans` 12px weight 500. Padding 4px 8px. Max-width 200px (truncate with ellipsis).
- **Chip remove "×"**: 16×16 button at `--text-tertiary`, brightens to `--text-primary` on hover.
- **Pending input**: blends in — no border, no background. `--font-sans` matching surrounding chips. `flex: 1; min-width: 80px`.

## Commit & uncommit

### Commit
A pending input becomes a chip on:
1. **Enter** (or Return).
2. **Comma** (or Tab) — configurable per use case. Email recipients = comma; freeform tags = Enter only.
3. **Blur** with non-empty content (configurable via `commitOnBlur`).

After commit, the pending input clears and stays focused.

### Uncommit
- **Backspace** on empty input: removes the last chip and **re-loads its value into the pending input** for editing. Don't just delete — most users want to fix a typo.
- **Click ×** on a chip: removes silently.
- **Backspace twice in a row** on empty input: deletes the previous chip outright (no re-edit).

## Validation per chip
- A chip can be valid, invalid, or warning. Reflected via:
  - Valid: `--surface-sunken`.
  - Warning: 1px `--warning-default` border, no fill change.
  - Invalid: `--danger-subtle` background, `--danger-default` text. Tooltip on hover with reason.
- Invalid chips remain in the field — never silently rejected — so the user can see what went wrong.

## Async tag suggestions
When paired with autocomplete:
- The pending input opens a suggestion list as the user types.
- Selecting a suggestion commits it as a chip (same path as Enter on the typed value).
- See autocomplete.md for list anatomy.

## Variants
- **Free entry** (default): any text becomes a chip.
- **From-list only**: only suggestions can be committed. Pressing Enter on unmatched input shows an inline error.
- **Mixed**: suggestions are preferred but free entry allowed. Suggested chips and free chips can have different visual styles (e.g. dot indicator on free chips).

## States
- **Default**.
- **Focus** (frame): 2px `--border-focus` ring on the frame, 2px offset.
- **Drag-reorder** (optional): chips can be dragged to reorder. Pressed chip rises 2px with `--shadow-2`. Drop target shown as a vertical 2px `--accent-default` line at the insertion point.
- **Disabled**: 38% opacity, all chip × hidden.

## Motion
- Chip in: 140ms — translate from input position with a 0.9→1 scale, opacity 0→1.
- Chip out: 120ms — opacity fade + 0.85 scale + slight horizontal translate (12px).
- Frame height changes: 200ms `--ease-emphasized`.

## Paste behavior
- Pasting text containing commas, semicolons, or newlines: split into multiple chips on those delimiters.
- Each created chip runs through normal validation; invalid ones still appear (with their invalid styling) so the user sees what was rejected.

## Accessibility
- Frame: `role="group"` with `aria-label="Tags"`.
- Each chip: `role="listitem"` with an inner remove button labeled `"Remove tag <value>"`.
- Pending input: `role="textbox"` with `aria-describedby` pointing to a hidden helper element listing current chips count.
- Live region: announces chip add/remove ("Added vinyl") debounced 200ms.
- Keyboard navigation: ←/→ moves focus between chips when input is empty; Delete on a focused chip removes it.

## Tokens
| Property | Token |
|---|---|
| Frame | `--border-default` (focus → `--border-focus`) |
| Chip bg | `--surface-sunken` |
| Chip remove | `--text-tertiary` |
| Invalid chip bg | `--danger-subtle` |
| Drop indicator | `--accent-default` |

## Don'ts
- Don't auto-commit on every keystroke. The user is mid-thought.
- Don't visually distinguish chip "types" with shape — only with color/border. Shape diffs make the row feel inconsistent.
- Don't use tag input for ≤2 values — a regular text field with a delimiter is simpler.
- Don't allow duplicate chips silently. Either dedupe on commit (preferred) or surface a "Already added" warning.
