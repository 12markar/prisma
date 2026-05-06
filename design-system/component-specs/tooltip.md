# Tooltip

A small floating label that explains an icon-only or terse interactive element.

## Anatomy

```
        ┌─────────────────┐
        │  Copy to clip…  │
        └────────▲────────┘
                 │
              [ ⧉ ]
```

- **Bubble** — `surface.inverse` fill, 8px horizontal padding, 4px vertical, `radius.sm`.
- **Caret** — 6px equilateral triangle, same fill as bubble, points at the host element.
- **Text** — `label.sm`, `text.onInverse`, max 200 single-line; never wraps. Use Popover for longer content.

## Position

Auto-flip behaviour:

- Default `top` (above the host).
- Flip to `bottom` if it would clip the viewport top.
- Flip to `right`/`left` if both vertical positions clip.
- Caret always points at the host's centre.

## Triggers

| Platform   | Trigger                                                                      |
|------------|------------------------------------------------------------------------------|
| Pointer    | Hover for ≥ 500ms; dismiss on hover-out, click outside, or `Esc`.            |
| Touch      | Long-press ≥ 500ms; dismiss on release or tap outside.                        |
| Keyboard   | Focus shows tooltip immediately; blur hides.                                  |

Show delay 500ms; hide delay 100ms so quick re-hover does not flicker.

## Animation

- Enter: `motion.duration.fast`, `motion.easing.decelerate`. Fade + slide 4px from the host edge.
- Exit: `motion.duration.fast`, `motion.easing.accelerate`. Fade only.
- **Reduced motion** — instant in/out, no slide.

## Tokens used

- `surface.inverse`, `text.onInverse`
- `label.sm`
- `radius.sm`
- `elevation.2` — soft drop shadow (light only; dark uses border + glow per token)
- `motion.duration.fast`, `motion.easing.decelerate`, `motion.easing.accelerate`
- `spacing.1`, `spacing.2`

## Accessibility

- **Compose** — `Modifier.semantics { contentDescription = "Copy" }` on the host plus `tooltipBoxState` (Material 3 has a `TooltipBox` API). Avoid making the tooltip itself focusable.
- **SwiftUI** — `.help("Copy to clipboard")` on the host. SwiftUI handles the rest natively.
- **Tooltip text duplicates the host's `accessibilityLabel`** — do not rely on the tooltip alone for screen readers; assistive tech may not surface tooltip text.
- **Non-modal** — does not trap focus; Esc dismisses.

## Do / Don't

✅ **Do** use tooltips for icon-only buttons, truncated text, and obscure controls.
✅ **Do** keep text < 80 characters. Anything longer means the affordance is wrong.
✅ **Do** ensure the host's `accessibilityLabel` matches the tooltip text.

❌ **Don't** put interactive elements inside a tooltip — use Popover.
❌ **Don't** show tooltips on hover for elements that already have a visible label — it's redundant noise.
❌ **Don't** auto-dismiss tooltips after a fixed timeout when hover is active. Let the user dismiss.
