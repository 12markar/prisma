# Toast

A transient, non-blocking message that auto-dismisses.

## Anatomy

```
┌────────────────────────────────────────┐
│ [icon]  Title                  [ × ]   │
│         Description text (optional)    │
│                       [ Action ]       │
└────────────────────────────────────────┘
```

- **Container** — `surface.raised`, `radius.lg`, `elevation.5`.
- **Status bar** — 3px-wide vertical accent strip at the leading edge, colored by status.
- **Icon** — 20×20, status-colored.
- **Title** — `title.sm`.
- **Description** — `body.sm`, `text.secondary`.
- **Action** — optional ghost button.
- **Close** — icon button, always present.

## Variants & token mapping

| Variant   | Status strip / Icon color    | Subtle bg (optional) |
|-----------|------------------------------|----------------------|
| `info`    | `status.info.default`        | `status.info.subtle` |
| `success` | `status.success.default`     | `status.success.subtle` |
| `warning` | `status.warning.default`     | `status.warning.subtle` |
| `error`   | `status.danger.default`      | `status.danger.subtle` |

## Behaviour

- **Default duration:** 5s. **With action:** 8s. **Error variant:** sticky (no auto-dismiss).
- **Position:** bottom-center on mobile, bottom-right on tablet/desktop.
- **Stack:** max 3 visible; older ones collapse with a `+N more` count.
- **Enter:** `motion.duration.default` + `easing.decelerate`. Slide + fade.
- **Exit:** `motion.duration.fast` + `easing.accelerate`.

## Accessibility

- **Role:** `status` (info/success/warning) or `alert` (error). On Android, use `LiveRegion`. On iOS, post `.announcement` notification.
- **Focus management:** toast does NOT steal focus. Action button is reachable via screen-reader rotor / heading nav.
- **Dismiss:** swipe (mobile) AND a visible close button (always). Never swipe-only.
- **Reduced motion:** crossfade only, no slide.
- **Reduced timing sensitivity:** if the OS reports it, treat all toasts as sticky.

## Do / Don't

✅ **Do** keep messages to one line of title + at most one line of description.
✅ **Do** use `error` toast only for system-level failures the user can't act on inline.

❌ **Don't** use toasts for confirmation of destructive actions — use a modal.
❌ **Don't** stack more than 3 toasts; collapse older.
❌ **Don't** put critical info only in a toast — it disappears.
