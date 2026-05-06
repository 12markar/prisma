# Slider

A continuous-value selection control for ranges where typing a number would feel heavy.

## Anatomy
```
┌─ Track ─────●───────────────────┐
              ↑ Thumb
   ─── Active fill ───
```
- **Track**: 4px tall, `--surface-sunken` background, `--radius-pill`.
- **Active fill**: from track start to thumb, `--accent-default` solid (light) / `--accent-300` (dark).
- **Thumb**: 20px circle, `--surface-base` fill, 1.5px border `--border-strong`, `--shadow-2`. Scales to **24px on press** (1.2× ratio).
- **Tick marks** (optional): 2×6px verticals at `--text-tertiary`, only when steps ≤ 10.

## Sizing ratios
| Element | Default | Compact | Touch (iOS/Android) |
|---|---|---|---|
| Track height | 4px | 3px | 4px |
| Thumb diameter | 20px | 16px | 24px |
| Hit area | 44×44 | 36×36 | 48×48 |
| Press scale | 1.20× | 1.15× | 1.15× |

## States
- **Default**: thumb at rest, no shadow elevation change.
- **Hover**: thumb scales 1.05×, halo `--accent-subtle` 24px diameter at 30% opacity.
- **Focus-visible**: 3px `--border-focus` ring at 2px offset.
- **Active/Press**: thumb scales 1.20×, halo expands to 32px at 40% opacity, value tooltip floats above (see tooltip.md).
- **Disabled**: track and thumb at 38% opacity, no interaction.

## Motion
- Thumb scale: 200ms `--ease-emphasized`.
- Halo opacity: 240ms `--ease-standard`.
- Value-snap settle: 320ms spring (stiffness 320, damping 28).

## Variants
- **Single thumb** (default).
- **Range**: two thumbs; the segment between them is the active fill. Thumbs cannot cross — they push.
- **Stepped**: discrete values with ticks. Thumb snaps; value changes only at step boundaries.

## Accessibility
- Role `slider`, `aria-valuemin/max/now`, `aria-valuetext` for non-numeric values ("Medium", "1.5×").
- Arrow keys: ±1 step. Shift+Arrow: ±10. Home/End: min/max.
- Live announcement on value change with 250ms debounce so screen readers don't flood.
- Min hit target 44×44 even when visual thumb is smaller.

## Token map
| Property | Token |
|---|---|
| Track bg | `--surface-sunken` |
| Active fill | `--accent-default` |
| Thumb fill | `--surface-base` |
| Thumb border | `--border-strong` |
| Thumb shadow | `--shadow-2` |
| Halo | `--accent-subtle` |
| Focus ring | `--border-focus` |

## Don'ts
- Don't use sliders for **count-style** inputs (3 items, 5 items) — use a stepper.
- Don't show numeric value inside the thumb at rest. It crowds the geometry. Use a tooltip on press only.
- Don't animate the active fill on initial mount. Snap to value, animate only on user interaction.
