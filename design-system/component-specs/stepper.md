# Stepper (numeric input)

A small numeric input with increment/decrement buttons. Use for counts and small ranges — quantities, paginated input, etc. Not for arbitrary numbers (use a text field) and not for continuous ranges (use a slider).

## Anatomy
```
┌────┬────────┬────┐
│  − │   3    │  + │
└────┴────────┴────┘
```
- **Frame**: 36px tall (default), 28px (compact), 44px (touch). 1px `--border-default`, `--radius-md` (6px), `--surface-base`.
- **Buttons**: 36×36 squared with rounded outer corners only (left button rounds left side, right rounds right). `--text-secondary` icon at rest, `--text-primary` on hover.
- **Number cell**: flex-1, min-width 40px, text-align center, `--font-mono` 14px, tabular-nums. Editable in default variant; non-editable in `inputMode: 'buttons-only'`.
- **Vertical separator**: 1px `--border-subtle` between cell and each button.

## States
- **Default**.
- **Button hover**: `--surface-sunken` background on the button only.
- **Button active (pressed)**: `--accent-subtle` background flash for 80ms, then back to hover state.
- **Number cell focus**: 2px `--border-focus` ring on the **whole frame** (not just the cell), `--border-focus` color overrides default border.
- **Disabled**: 38% opacity on the entire frame, no interaction.
- **At limits**: when value = min, the `−` button is disabled (38% opacity); when value = max, `+` is disabled.

## Hold-to-repeat
- Hold a button: first repeat at 350ms, then accelerate from 4/sec to 12/sec by 1500ms.
- Release stops repetition.
- Acceleration is **linear**, not exponential — exponential makes the user overshoot.

## Step
- Default = 1.
- Configurable. When step ≥ 5, labels for the buttons can show the step (`+5`, `−5`) — only at default and touch sizing, not compact.

## Variants
- **Inline** (default): single row, button-cell-button.
- **Stacked**: + above, − below; useful in dense tables. 28px tall total, 14px per button. Only shown on hover/focus of the row.
- **Buttons-only**: no editable cell — used in tweaks panels where typing is unnecessary.

## Motion
- Number change: 100ms ease — the digit translates 30% in the direction of change, fading; new digit translates in. Single overflow-hidden cell.
- Press flash: 80ms `--ease-standard`.

## Validation
- Out-of-range typed value: revert on blur with a 200ms shake. Or clamp on blur (configurable via `clampOnBlur`).
- Non-numeric input: ignore character entry beyond what matches `^-?\d*\.?\d*$`.

## Accessibility
- `role="spinbutton"` on the frame, `aria-valuemin/max/now/text`.
- Buttons get explicit `aria-label="Increase quantity"` / `"Decrease quantity"`.
- Arrow Up/Down on the focused cell: ±1 step. Shift+Arrow: ±10. Home/End: min/max.
- Min touch target 44×44 — pad if visual is smaller.

## Tokens
| Property | Token |
|---|---|
| Frame border | `--border-default` |
| Button hover | `--surface-sunken` |
| Press flash | `--accent-subtle` |
| Focus ring | `--border-focus` |
| Number text | `--text-primary` |
| Disabled opacity | 38% |

## Don'ts
- Don't use a stepper for ranges with > ~30 steps end-to-end. Use a slider or text field.
- Don't put a stepper inside a row that scrolls horizontally — touch users will trigger scroll instead of a tap.
- Don't show units inside the number cell (`3 hrs`). Put units adjacent in the form layout, not inside the input.
- Don't auto-decrement past 0 if `min: 0`. Disabled state at limit is the contract.
