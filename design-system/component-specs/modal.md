# Modal (Alert Dialog)

A blocking, focus-trapping window for confirmations and critical input.

## Anatomy

```
═══════════ scrim ═══════════
        ┌─────────────────────────┐
        │ Title                    │
        │                          │
        │ Description / body.      │
        │                          │
        │      [ Cancel ] [ OK ]   │
        └─────────────────────────┘
═════════════════════════════
```

- **Scrim** — `surface.overlay` over the full viewport.
- **Container** — `surface.raised`, `radius.lg`, `elevation.4`. Max width 480, min width 280.
- **Title** — `headline.sm`.
- **Body** — `body.md`, `text.secondary`.
- **Actions** — right-aligned (LTR). Primary on the right. Min 1, max 3 buttons.

## States & token mapping

- Container shadow: `elevation.4`.
- Scrim: `surface.overlay`.
- Enter motion: 200ms scale 0.96→1 + fade. `easing.emphasized`.
- Exit motion: 150ms scale 1→0.98 + fade. `easing.accelerate`.

## Accessibility

- **Role:** `dialog` with `aria-modal="true"`. SwiftUI `.alert` / `.sheet` w/ `.accessibilityAddTraits(.isModal)`. Compose `Dialog`.
- **Focus trap:** focus is trapped inside until dismissed. First focusable element receives focus on open (NOT the destructive button).
- **Escape / back:** dismisses (treats as Cancel) UNLESS the action is irreversible.
- **Title association:** `aria-labelledby` on the dialog points to the title element. Body via `aria-describedby`.
- **Restore focus:** when dismissed, return focus to the element that opened it.
- **Reduced motion:** instant fade only.

## Do / Don't

✅ **Do** lead with a verb-first action ("Delete account", not "OK").
✅ **Do** make the dismiss action explicit. Don't hide cancel behind only the scrim tap.
✅ **Do** restore prior focus on close.

❌ **Don't** stack modals. If you think you need to, the flow is wrong.
❌ **Don't** put long forms in a modal — use a full screen or sheet.
❌ **Don't** auto-dismiss a modal after a timeout.
