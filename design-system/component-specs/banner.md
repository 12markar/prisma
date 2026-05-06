# Banner

A persistent inline message that informs about state, errors, or system-wide announcements. Unlike Toast (transient, floats), Banner sits in the page flow and stays until dismissed or the condition changes.

## Anatomy

```
┌─────────────────────────────────────────────────────────────────┐
│ [ ! ]  Title                                              [ x ] │
│        Description text spanning the available width.           │
│                                          [ Action ] [ Other ]    │
└─────────────────────────────────────────────────────────────────┘
```

- **Leading icon** — 20×20 status-coloured icon. Optional, recommended for clarity.
- **Title** — `label.lg`, weight 600, `text.primary`. Required.
- **Description** — `body.sm`, `text.secondary`. Optional. Wraps freely; no max line count.
- **Trailing close** — 16×16 `x` icon, optional. Hidden if banner is non-dismissable (e.g., critical error).
- **Action area** — 0–2 buttons (Ghost variant), bottom-right. Optional.

## Variants

| Variant   | Icon          | Border / Bar          | Background                  | Use                                       |
|-----------|---------------|------------------------|------------------------------|-------------------------------------------|
| `info`    | info circle   | `status.info.default` | `status.info.subtle`        | Neutral notice, system info.              |
| `success` | check         | `status.success.default` | `status.success.subtle`  | Operation succeeded; persistent confirmation. |
| `warning` | exclamation   | `status.warning.default` | `status.warning.subtle`  | Non-blocking caution, recoverable.        |
| `danger`  | alert         | `status.danger.default`  | `status.danger.subtle`   | Error or critical state — usually non-dismissable. |

## Visual style

- **Padding** `spacing.4` all sides.
- **Radius** `radius.md`.
- **Left edge** — 3px `status.{variant}.default` strip (vertical bar) — primary variant marker.
- **Background** — `status.{variant}.subtle`. (Avoid full bg of `default` — too heavy.)
- **Border** `border.subtle` on top/right/bottom (left covered by the strip).

## States

- **Visible** — default.
- **Entering** — slide-down from negative offset 8px + fade, `motion.duration.default`, `motion.easing.decelerate`.
- **Exiting** — fade only, `motion.duration.fast`.
- **Reduced motion** — instant in/out, no slide.

## Tokens used

- `status.info.subtle/default`, `status.success.subtle/default`, `status.warning.subtle/default`, `status.danger.subtle/default`
- `text.primary`, `text.secondary`
- `border.subtle`
- `radius.md`
- `spacing.4`
- `label.lg`, `body.sm`
- `motion.duration.default`, `motion.duration.fast`
- `motion.easing.decelerate`

## Accessibility

- **Role** — `Role.Alert` for `warning` / `danger`; `Role.Status` for `info` / `success`.
- **Compose** — `Modifier.semantics { liveRegion = LiveRegionMode.Polite }` for info/success; `Assertive` for danger so screen readers announce immediately.
- **SwiftUI** — `.accessibilityAddTraits(.isStaticText)` plus `accessibilityLabel("\(variant). \(title). \(description)")`.
- **Close button** — `accessibilityLabel("Dismiss banner")`.
- **Action buttons** — labelled clearly per their effect, not "OK".

## Do / Don't

✅ **Do** use Banners for conditions that persist (validation errors, maintenance windows, version upgrade prompts).
✅ **Do** include a clear action when the user can act on the message ("Reload", "Update now").
✅ **Do** use `assertive` live region for `danger` banners — they need immediate attention.

❌ **Don't** use Banners for transient acknowledgements ("Saved!") — use Toast.
❌ **Don't** stack Banners. If two conditions need attention, prioritise one and queue the other.
❌ **Don't** make `danger` Banners dismissable when the underlying condition is unresolved — the user should fix the issue, not hide the warning.
