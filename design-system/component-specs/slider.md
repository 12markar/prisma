# Slider

Continuous or stepped numeric input via a draggable thumb on a track.

## Anatomy

```
●━━━━━━━━━━━━━━━━━━━○─────────────────
                    ▲ thumb (44x44 hit-area)
●━━━━━━━━━━━━━━━━━━━ filled track (accent.default)
                    ─ unfilled track (border.subtle)
```

- **Track** — 4px tall, `radius.full`, `border.subtle` background.
- **Filled portion** — `accent.default` from start to thumb position.
- **Thumb** — 20×20 circle, `surface.raised` fill, 2px `border.strong` stroke, `radius.full`. Hit-area 44×44 (extends beyond visual bounds).
- **Tick marks** (stepped mode) — 4×4 dots at each step, `border.default`. Hidden when value > 6 steps.

## Variants

### Single thumb

One value. Common for volume, brightness, opacity controls.

### Range

Two thumbs defining a min–max. Filled portion is between the thumbs. Each thumb tab-focusable independently.

## Sizes

| Size      | Track | Thumb | Hit-area |
|-----------|-------|-------|----------|
| `sm`      | 3     | 16    | 44       |
| `default` | 4     | 20    | 44       |

## States

- **Default** — track + thumb visible.
- **Hover** (pointer) — thumb scales to 1.1×, ring of `accent.subtle` (8px halo) fades in.
- **Focused** (keyboard) — 2px `border.focus` ring around thumb at +4px offset.
- **Dragging** — thumb scales to 1.2×, halo brightens to `accent.subtle`. Subtle haptic on each step crossing (stepped mode only).
- **Disabled** — track + thumb dropped to `text.disabled` opacity; not focusable.

## Tokens used

- `border.subtle` — unfilled track
- `accent.default` — filled track
- `surface.raised`, `border.strong` — thumb
- `border.focus` — focus ring
- `accent.subtle` — hover/drag halo
- `radius.full` — track ends, thumb
- `motion.duration.fast` — thumb scale, halo fade
- `motion.easing.standard`

## Keyboard

- `←` / `→` — move by 1 step (or 1% in continuous mode)
- `Shift + ←/→` — move by 10 steps / 10%
- `Home` / `End` — min / max
- `PgUp` / `PgDn` — move by 10 steps

## Accessibility

- **Role**: `slider` (Compose `Role.Slider`, SwiftUI `accessibilityValue` + `accessibilityAdjustableAction`).
- **Value announced** as the current numeric value plus unit (e.g., "65 percent", "12 minutes"). Use `progressBarRangeInfo` (Compose) / `accessibilityValue` (SwiftUI).
- **Range slider** announces both thumbs distinctly — "Minimum 30, maximum 80".
- **Min hit-area 44×44 pt / 48×48 dp** — required even when visual thumb is smaller. Never reduce.
- **Reduced motion** — drop thumb scale + halo animations; keep instant state change.

## Do / Don't

✅ **Do** show the current value next to the slider in `label.md` so users do not have to interact to read it.
✅ **Do** use stepped sliders when there's a meaningful discrete set (1–5 stars, font sizes); continuous when scrubbing.
✅ **Do** provide haptic feedback on step crossings for tactile precision.

❌ **Don't** use sliders for value precision below the thumb width — users cannot land on individual values. Use number input instead.
❌ **Don't** use sliders for selection between distinct categories (use Segmented Control or Radio).
❌ **Don't** auto-commit the value on every drag pixel — debounce to `motion.duration.fast` or commit on drag end.
