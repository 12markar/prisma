# Date Picker

A calendar surface for selecting one or more dates. Used inline (embedded) or as a popover anchored to a text field.

## Anatomy
```
┌──────────────────────────────────┐
│  ‹  September 2026  ›   [Today]  │  ← Header
├──────────────────────────────────┤
│  S   M   T   W   T   F   S       │  ← Weekday rail
│  ·   ·   1   2   3   4   5       │
│  6   7   8   ●   10  11  12      │  ← Days; ● = today, ◉ = selected
│  …                               │
├──────────────────────────────────┤
│  [ Clear ]            [ Apply ]  │  ← Footer (optional)
└──────────────────────────────────┘
```
- **Frame**: `--surface-raised`, 1px `--border-subtle`, `--radius-lg` (12px), `--shadow-4`. Width 320px (single month) / 640px (range, two months).
- **Header**: 48px tall, month label `--font-sans` 16px weight 500, prev/next chevrons 32×32 ghost buttons. "Today" link on the right when current view ≠ current month.
- **Weekday rail**: `--font-mono` 11px `--text-tertiary`, uppercase, 0.04em letter-spacing.
- **Day cell**: 40×40 (default), 36×36 (compact), 44×44 (touch). Number centered, weight 400. Min hit target 44×44 always — increase cell padding when shrinking visual size.

## Day cell states
| State | Treatment |
|---|---|
| Out-of-month | `--text-tertiary` opacity 50% — not the same as disabled. |
| Default | `--text-primary`. |
| Hover | `--surface-sunken` background, full radius circle. |
| Today | 1px `--border-default` ring around the number, no fill. |
| Selected | `--accent-default` fill, `--on-accent` text. |
| In range | `--accent-subtle` background, square shape (no radius), only on day cells **between** range endpoints. |
| Range start/end | `--accent-default` fill + half-radius (rounded toward the outside). |
| Disabled | `--text-tertiary` 38% opacity, strike-through diagonal at 1px `--border-subtle`. |
| Focus-visible | 2px `--border-focus` ring, 2px offset, on top of any other state. |

## Range visualization
The selected range fills with `--accent-subtle`. Endpoint cells get the **solid accent fill** with a partial radius — left endpoint rounds the left side only, right endpoint rounds the right side only. The middle cells are square so the range reads as one continuous bar.

## Variants
- **Single date** (default).
- **Range** (two thumbs, hover preview shows live range from start to cursor).
- **Multi-date** (independent toggles, no range fill — selected cells are circles).
- **Inline** vs **popover** — popover follows the popover spec for position/dismissal.

## Motion
- Month transition: 220ms `--ease-emphasized`. Old month slides out / new slides in along the prev-next axis. Use `transform: translateX` + opacity, not layout shift.
- Day hover halo: 120ms `--ease-standard`.
- Selection lock-in: a 280ms scale-pop on the selected cell (0.9 → 1.05 → 1.0) — gives a "stamped" feel.

## Accessibility
- `role="grid"` on the day grid; `role="gridcell"` on cells; `role="row"` on weekday rail and each week.
- Arrow keys move focus by day; PgUp/PgDn by month; ⇧PgUp/⇧PgDn by year; Home/End by week.
- `aria-selected` on selected cells. `aria-current="date"` on today.
- Live region announces month change: "September 2026, focused on day 14".
- Min touch 44×44 even at compact sizing — pad the hit area, not the visual.

## Tokens
| Property | Token |
|---|---|
| Frame bg | `--surface-raised` |
| Day hover | `--surface-sunken` |
| Selected fill | `--accent-default` |
| Selected text | `--on-accent` |
| In-range fill | `--accent-subtle` |
| Today ring | `--border-default` |
| Disabled diag | `--border-subtle` |

## Don'ts
- Don't auto-close on single-date selection in **range** mode — wait for the second click.
- Don't grey out days outside the current month if the user can't navigate to them; just hide them. Otherwise show them at reduced opacity.
- Don't put time selection inside the date picker. Use a paired time picker.
- Don't animate range fills on hover — only animate them once on apply.
