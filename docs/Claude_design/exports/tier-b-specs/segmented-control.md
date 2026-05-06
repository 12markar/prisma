# Segmented Control

A row of mutually exclusive options sharing a single visual track. Use for **2–5 options** where all options should be visible at once. For 6+, use a select.

## Anatomy
```
┌─────────────────────────────────────┐
│ ╭──────────╮                        │
│ │ Selected │   Option 2   Option 3  │
│ ╰──────────╯                        │
└─────────────────────────────────────┘
```
- **Track**: `--surface-sunken`, 1px `--border-subtle`, `--radius-md` (8px), 4px internal padding.
- **Chip** (selected): `--surface-raised`, `--shadow-1`, 1px `--border-subtle`, `--radius-sm` (6px). Sits inside the track on a transform — never via repaint.
- **Segments**: equal-width by default, `flex: 1`. Text 13px, weight 500. Selected: `--text-primary`. Unselected: `--text-secondary`.
- **Heights**: 32px (default), 28px (compact), 40px (touch).

## Chip slide easing
- **Position transition**: 280ms `--ease-emphasized` (`cubic-bezier(0.2, 0, 0, 1)`).
- **Width transition**: 280ms same easing — width can change when option labels differ.
- The chip uses `transform: translate3d` + width change, not `left`/`width` keyframes — keeps it on the GPU.
- On rapid clicks, the chip **interrupts** the previous animation cleanly (no queueing). Use the FLIP technique or the Web Animations API's `animate()` with `composite: replace`.

## Pressed flash
When user mousedowns/taps on a non-selected segment:
1. **Immediate** (0ms): the target segment text dims to 60% opacity for 80ms.
2. **0ms**: chip starts sliding to target.
3. **80ms**: target segment text returns to full opacity, now styled as selected.
4. **280ms**: chip arrives.

The flash is what gives the control its "alive" feel — without it, the slide reads as inert. Don't skip it.

On the **previously-selected** segment, run the inverse: text fades from `--text-primary` to `--text-secondary` over the chip's slide duration, with a 40ms overlap window where both segments read as "selected" — eliminates the visual gap.

## States
- **Default**: chip at selected position.
- **Hover** (unselected segment): text → `--text-primary`, no chip movement.
- **Focus-visible** (any segment): 2px `--border-focus` ring, 2px offset, drawn on the **whole track** — not the segment, since segments don't have their own borders.
- **Disabled**: 38% opacity, no chip movement.

## Variants
- **Default**: track + chip as described.
- **Underline**: no chip background; selected segment shows a 2px `--accent-default` underline that slides between segments instead. Use when the control sits inline with body text.
- **Icon-only**: square segments (32×32 default), tooltip required for each.

## Accessibility
- `role="radiogroup"` on track, `role="radio"` on each segment, `aria-checked` reflects state.
- Arrow keys move selection (not focus). Tab enters and exits the group as a single stop.
- The chip slide never blocks input — even if mid-animation, the next click is honored.

## Don'ts
- Don't mix segmented controls with primary buttons in the same row. They visually compete.
- Don't use for navigation (router-driven views) when there are more than 3 destinations — use tabs instead. Segmented controls imply parity; tabs imply hierarchy.
- Don't animate the track itself (border, background). Only the chip moves.
