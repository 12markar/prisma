# Popover

A floating panel anchored to a host element, holding richer content than a Tooltip allows. Non-modal — does not block the rest of the page.

## Anatomy

```
        ┌────────────────────────────┐
        │  Heading                   │
        │  Body content. Can include │
        │  links, buttons, lists.    │
        │                  [ Action ] │
        └─────────▲──────────────────┘
                  │
               [ host ]
```

- **Container** — `surface.raised`, `radius.lg`, `elevation.3`. 1px `border.subtle` (helps in dark where shadow alone is subtle).
- **Padding** — `spacing.4` all sides.
- **Caret** — 8px triangle pointing to host, `surface.raised` fill, optional `border.subtle` outline matching the container.
- **Max width** 320; max height 480 with internal scroll if exceeded.

## Position

Same auto-flip rules as Tooltip — defaults `bottom` for popovers (since they often sit below a button), but flips to `top`/`left`/`right` when the viewport would clip.

## Triggers

| Trigger    | Behaviour                                                                  |
|------------|----------------------------------------------------------------------------|
| Click/tap  | Toggles. Click outside or `Esc` dismisses. **Default trigger.**            |
| Focus      | Optional — `focusWithin` opens; blur outside dismisses.                     |
| Hover      | Avoid for popovers — hover is for tooltips. Use click for popover.          |

## Animation

- Enter: `motion.duration.default`, `motion.easing.decelerate`. Fade + scale 0.96 → 1.0 from caret origin.
- Exit: `motion.duration.fast`, `motion.easing.accelerate`. Fade only.

## Tokens used

- `surface.raised`, `border.subtle`
- `radius.lg`
- `elevation.3`
- `spacing.4`
- `motion.duration.default`, `motion.duration.fast`
- `motion.easing.decelerate`, `motion.easing.accelerate`

## Accessibility

- **Compose** — wrap the host with `Box` carrying `Popup` (Compose's primitive). Manage focus with `FocusRequester` so opening shifts focus into the popover; closing returns focus to the host.
- **SwiftUI** — native `.popover(isPresented:)` modifier. Handles focus, sizing, and Esc dismissal natively.
- **Non-modal** — does not trap focus globally, but tabbing inside the popover is contained until dismiss.
- **Esc dismisses.** Click-outside dismisses. Focus returns to the trigger on dismiss.
- Caret is decorative — `accessibilityHidden`.

## Do / Don't

✅ **Do** use popovers for: contextual help, settings panels, color pickers, mini-forms.
✅ **Do** ensure popover content has its own logical heading or label so screen-reader users know where they landed.
✅ **Do** dismiss on outside click — this is the spatial cue most users expect.

❌ **Don't** use popovers for critical decisions or destructive confirmations — use Modal for that (focus-trapped, blocks page).
❌ **Don't** stack popovers — open one at a time. Replace, don't accumulate.
❌ **Don't** put a popover inside a list-item that's a tap target — the row's tap and the popover's caret-anchor will fight.
