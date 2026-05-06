# Bottom Sheet

A surface that slides from the bottom of the screen, supporting one or more detents.

## Anatomy

```
═══════════ scrim ═══════════
┌─────────────────────────────┐
│            ─────             │  ← grabber
│  Title                       │
│                              │
│  Content                     │
│                              │
└─────────────────────────────┘
```

- **Grabber** — 36×4 pill, `border.strong`, centered. Indicates draggability.
- **Container** — `surface.raised`, `radius.xl` top corners only, `elevation.4`.
- **Title** (optional) — `title.lg`.
- **Content** — scrollable above the safe-area inset.

## Detents

| Detent     | Height                          | Use case                  |
|------------|----------------------------------|---------------------------|
| `small`    | ~25% of viewport                 | Quick actions, picker     |
| `medium`   | ~55% of viewport                 | Default                   |
| `large`    | full minus top safe area + 16px  | Long-form / scrollable    |

Snapping animation: `motion.duration.default` + `easing.emphasized`. The user can drag freely between detents; release snaps to the nearest.

## States & token mapping

- Scrim opacity scales with sheet height: 0% at small, 100% at medium+.
- `elevation.4` always.
- Container respects safe-area inset bottom; padding `spacing.6` (24).

## Accessibility

- **Role:** `dialog` with `aria-modal="true"` when at medium+ detents (interaction blocked behind). At `small`, behave as non-modal.
- **iOS:** use system `UISheetPresentationController` detents where possible — they handle a11y for free.
- **Android:** `ModalBottomSheet` from Compose Material3 (re-themed) provides correct semantics; or implement focus trap manually.
- **Grabber:** has `accessibilityLabel = "Resize sheet"` and exposes detent options as actions ("Expand", "Collapse").
- **Dismiss:** swipe-down AND a visible close affordance (icon button or scrim-tap) — never swipe-only.
- **Focus restore:** on dismiss, return focus to invoking element.

## Do / Don't

✅ **Do** use bottom sheets for context that's tied to a specific row/screen.
✅ **Do** provide multiple detents when content has both summary and detail views.

❌ **Don't** use a bottom sheet on a screen that already has a fixed bottom bar — it conflicts spatially.
❌ **Don't** make the sheet undismissable — there must always be a way out.
