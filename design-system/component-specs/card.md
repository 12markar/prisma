# Card

A grouped surface for content + actions related to a single entity.

## Variants

| Variant     | Background          | Border               | Shadow              |
|-------------|---------------------|----------------------|---------------------|
| `elevated` | `surface.raised`    | none                 | `elevation.1` (rest) → `elevation.2` (hover) |
| `outlined` | `surface.base`      | 1px `border.subtle`  | none                |
| `filled`   | `surface.sunken`    | none                 | none                |

## Anatomy

```
┌───────────────────────────────┐
│ [ media (optional) ]          │
│                               │
│ Title                         │
│ Subtitle                      │
│                               │
│ Body                          │
│                               │
│ ─────────                     │
│ [Action]   [Action]           │
└───────────────────────────────┘
```

- **Container** — radius `radius.lg`, padding `spacing.6` (24) default.
- **Media** — full-bleed top, `radius.lg` top corners; aspect ratio per use.
- **Title** — `title.lg`.
- **Subtitle** — `body.sm`, `text.secondary`.
- **Body** — `body.md`.
- **Actions** — bottom-right or full-width row; ghost or secondary buttons.

## States (interactive cards)

| State    | Effect                                              |
|----------|-----------------------------------------------------|
| hover    | shadow → `elevation.2`; subtle scale 1.005          |
| pressed  | shadow → `elevation.1`; scale 0.998                 |
| focused  | 2px ring `border.focus` outside, 2px offset         |
| disabled | opacity 60%; no hover/press effects                 |

Outlined and filled variants don't change shadow on hover; instead, the border darkens to `border.default` and bg shifts to `surface.sunken`.

## Accessibility

- If the entire card is a link/button, give it a single `role="link"` / `role="button"` with an a11y label that summarises the card's title + status (e.g. "Open Project Aurora, status: in progress").
- Don't nest interactive elements inside an interactive card without a clear "primary tap target" — screen readers and keyboard users will get confused. If you must, expose the secondary actions outside the main tap region.
- Min touch target for an entire interactive card is implicit (the card is large), but inline action buttons within still need 44pt / 48dp.

## Do / Don't

✅ **Do** use `elevated` for the primary catalogue grid where cards float on a sunken surface.
✅ **Do** use `outlined` when cards sit on `surface.raised` (avoids stacking shadows).
✅ **Do** maintain consistent padding across all cards on a screen — visual rhythm matters.

❌ **Don't** mix all three card variants on one screen.
❌ **Don't** add shadows on top of shadows (e.g. elevated card on elevated surface) — choose `outlined` or `filled` for nested contexts.
