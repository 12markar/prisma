# Loading

Indicators that communicate ongoing background work.

## Variants

### Circular (indeterminate)

A 360° rotating arc.
- Sizes: `sm` 16, `md` 24, `lg` 40.
- Stroke: 2px (sm), 2.5px (md), 3px (lg).
- Color: `accent.default` on neutral surfaces; `text.onAccent` when sitting on `accent.default`.
- Rotation: 1 full turn / 1.2s, linear.

### Linear

A horizontal track with an animated fill.
- Track height: 3px. Track color: `border.subtle`. Fill: `accent.default`.
- **Determinate:** width = `progress%`, transitions over `motion.duration.default`.
- **Indeterminate:** ribbon traverses 0→100% in 1.6s, eases.

### Skeleton

Placeholder shapes shown while real content loads.
- Fill: `surface.sunken` with a 1.5s shimmer of `border.subtle` → `surface.sunken`.
- Shapes match the final layout: text rows are `body.md` height, avatars match real avatar size.
- Always render at least 2 rows; never show one alone.

## Accessibility

- **Role:** `progressbar`. For determinate, expose `aria-valuenow` / `aria-valuemin` / `aria-valuemax` (Compose `Modifier.progressSemantics(progress)`, SwiftUI `accessibilityValue`).
- **Label:** required. "Loading", or more specific ("Saving changes").
- **Skeletons:** wrap in `aria-busy="true"` while loading; hidden from screen readers (`aria-hidden="true"`) so they don't announce empty boxes.
- **Reduced motion:** disable shimmer + spin; show a static muted state instead. Determinate progress still animates fill changes (because the value is meaningful), but use `motion.duration.fast`.

## Do / Don't

✅ **Do** prefer skeleton for content; spinner for actions.
✅ **Do** show progress percentage if the operation takes >2s and progress is calculable.
✅ **Do** add a "Cancel" affordance for any operation expected to take >5s.

❌ **Don't** show a spinner for <300ms — it flashes. Delay spinner appearance.
❌ **Don't** stack multiple loading indicators in one region.
