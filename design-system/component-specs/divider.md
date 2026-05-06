# Divider

A thin line that separates content into distinct regions.

## Variants

### Horizontal

```
─────────────────────────────────
```

Full-width 1px line. Default visual weight `border.subtle`.

### Vertical

```
│
│
│
```

1px column. Used inside rows (toolbar separators, button group spacers).

### Inset

```
       ─────────────────────────
```

Horizontal, but starts after a left inset matching the leading content of the row above (typically 56–72 — same as leading icon + gap). Used inside list-items so the divider does not collide with avatars/icons.

## Visual weights

| Weight     | Color            | Use                                         |
|------------|------------------|---------------------------------------------|
| `subtle`   | `border.subtle`  | Default. Inside cards, between list rows.   |
| `default`  | `border.default` | Section breaks within a page.               |
| `strong`   | `border.strong`  | Major structural boundaries (sidebar edge). |

## Spacing

- A divider's vertical margin is **inherited from the parent layout** — never set its own padding. Stacked items relying on divider as a separator should set their gap to `0`.
- Inset value matches the leading column of the host (e.g. 56dp/pt for an avatar + 8 gap, 72 for avatar + larger gap).

## Tokens used

- `border.subtle` / `border.default` / `border.strong`

## Accessibility

- Decorative. **No semantic role.**
- Compose: render as `Box` with background; do not annotate.
- SwiftUI: native `Divider()` is fine for default; use `Rectangle` for custom thickness/color so it inherits the no-accessibility treatment.
- Decorative dividers are skipped by screen readers automatically.

## Do / Don't

✅ **Do** use the lightest weight that still establishes the boundary — most cases need only `subtle`.
✅ **Do** use inset variants inside list-items containing leading visuals so the line does not run under the avatar.

❌ **Don't** stack dividers — if you need stronger separation, use spacing or a card surface change instead.
❌ **Don't** make a divider load-bearing semantically. Use a heading + spacing for screen-reader-perceptible separation.
❌ **Don't** apply opacity to dividers — use the `border.subtle` token instead, which already accounts for surface contrast.
