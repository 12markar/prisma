# Time Picker

Selection of an hour and minute (and optionally seconds, AM/PM). Used inline or in a popover paired with a date picker.

## Anatomy
```
┌──────────────────────────┐
│   09  :  45  :  00       │  ← Display
│   ▲      ▲      ▲        │
│   ▼      ▼      ▼        │  ← Spinners (or columns on touch)
│   [ AM ] [ PM ]          │  ← Period (12h only)
└──────────────────────────┘
```

## Variants

### Inline spinner (default — desktop)
- Three (or four) numeric inputs side-by-side: HH : MM (: SS).
- Each cell: 56×72 with up/down chevron buttons stacked above/below the number.
- Number font: `--font-sans` 28px weight 500, tabular-nums.
- Up arrow increments, down arrow decrements. Wraps (23 → 00). Holding accelerates.
- Period: segmented control (AM / PM), shown only when `format: 12`.

### Wheel (touch — iOS pattern)
- Three vertical scrolling columns. Each cell 44px tall, the centered cell is the selected value.
- Centered cell highlighted with two horizontal `--border-default` 1px lines (no fill — the lines do the framing work).
- Adjacent cells fade: 100% → 60% → 30% opacity moving away from center.
- Inertial scroll, snaps to row on release. Haptic tick (iOS) on each row passing center.

### Compact text input
- A single `HH:MM` masked text field. For when users prefer keyboard. Validates on blur.
- Use this in dense forms where the spinner/wheel would dominate.

## States
- **Default**: number visible, chevrons at `--text-tertiary` opacity 60%.
- **Hover** (chevron): chevron → `--text-primary` opacity 100%, background `--surface-sunken` on the chevron's hit area.
- **Active** (incrementing): chevron press shows a 4px halo at `--accent-subtle`. Auto-repeat after 350ms hold, accelerating to 6 ticks/sec at the 1500ms mark.
- **Focus-visible**: 2px `--border-focus` ring around the *whole picker*, not individual cells — keyboard users navigate within it via arrow keys.
- **Invalid**: cell ring `--danger-default`, helper text below: "Hours must be 0–23".

## Step
- Default minute step = 1.
- Common alternative: 5-minute step. The minute cell shows only multiples of 5; up/down moves by 5; manual typing snaps to the nearest 5 on blur.
- Seconds cell defaults to step 1, hidden unless `precision: seconds`.

## Motion
- Spinner number change: 180ms ease — old number slides up/down 30%, new number slides in from the opposite direction. Use a single overflow:hidden cell so the motion is contained.
- Wheel scroll: native momentum on touch, `cubic-bezier(0.1, 0.9, 0.2, 1)` on snap.

## Accessibility
- `role="spinbutton"` on each number cell, `aria-valuemin/max/now/text`.
- Arrow keys: ±1 step. ⇧Arrow: ±5. PgUp/PgDn: ±10.
- The whole picker has an `aria-label` describing what it edits.
- Wheel variant: each column gets `role="listbox"`; cells `role="option"`. Live announcement on snap.
- Min touch 44×44 on every chevron / wheel cell.

## Tokens
| Property | Token |
|---|---|
| Number text | `--text-primary` |
| Chevron default | `--text-tertiary` |
| Chevron active | `--accent-default` |
| Cell hover | `--surface-sunken` |
| Wheel center lines | `--border-default` |
| Invalid ring | `--danger-default` |

## Don'ts
- Don't combine a wheel and spinner in one viewport — pick one based on platform/size.
- Don't show seconds by default. Add only when the use case explicitly requires it (timers, audio editing).
- Don't separate AM/PM with a dropdown when a segmented control fits — segmented controls show both options, dropdowns hide one.
- Don't auto-advance focus from HH to MM on two-digit entry. Many users type "9" and expect to keep editing — auto-advance after 700ms idle instead, or only on Enter/Tab.
