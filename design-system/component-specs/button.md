# Button

A pressable control that performs an action.

## Anatomy

```
[ leadingIcon ]  Label  [ trailingIcon ]
```

- **Container** — touch target wrapper. Min 44×44pt (iOS) / 48×48dp (Android).
- **Label** — `typography.label.lg` for default and large; `typography.label.md` for small.
- **Leading icon** (optional) — 18×18 default, 16×16 small.
- **Trailing icon** (optional) — same sizing as leading.
- **Loading spinner** — replaces the leading icon when `isLoading`.

## Variants

| Variant     | Use case                                                | Container fill                  | Label color                | Border                          |
|-------------|---------------------------------------------------------|---------------------------------|----------------------------|---------------------------------|
| `primary`   | Single primary action per screen                        | `accent.default`                | `text.onAccent`            | none                            |
| `secondary` | Common alternate action                                 | `surface.raised`                | `text.primary`             | `border.default`                |
| `outlined`  | Tertiary action; lower visual weight than secondary     | transparent                     | `text.primary`             | `border.default`                |
| `ghost`     | Embedded in dense surfaces; toolbars                    | transparent                     | `text.primary`             | none                            |
| `icon`      | Icon-only; needs `accessibilityLabel`                   | transparent (or per parent)     | `text.primary`             | none                            |
| `destructive` | Confirms a destructive action                         | `status.danger.default`         | `status.danger.onStatus`   | none                            |

## Sizes

| Size  | Height | Horiz padding   | Label             | Radius        |
|-------|--------|-----------------|-------------------|---------------|
| `sm`  | 32     | `spacing.3` (12)| `label.md`        | `radius.md`   |
| `md`  | 40     | `spacing.4` (16)| `label.lg`        | `radius.md`   |
| `lg`  | 48     | `spacing.5` (20)| `label.lg`        | `radius.md`   |

## States & token mapping

| State     | Primary                          | Secondary / Outlined / Ghost                         |
|-----------|----------------------------------|------------------------------------------------------|
| default   | bg `accent.default`              | bg per variant; label `text.primary`                 |
| hover     | bg `accent.hover`                | bg `surface.sunken` (overlay 4% on raised)           |
| pressed   | bg `accent.pressed`              | bg `surface.sunken` darker (overlay 8%)              |
| focused   | + 2px ring `border.focus`, 2px offset | + 2px ring `border.focus`, 2px offset           |
| disabled  | bg `surface.sunken`, label `text.disabled`, no shadow | label `text.disabled`, border `border.subtle` |
| loading   | spinner replaces leadingIcon; click no-op; label retained for screen readers | same |

## Accessibility

- **Role:** `button` (HTML), `Button` (Compose), `.isButton` trait (SwiftUI).
- **Label:** visible label IS the a11y label. For `icon` variant, **`accessibilityLabel` is required** — never ship without one.
- **Min touch target:** 44×44pt / 48×48dp. If visually smaller, expand the hit area, do not change visual size.
- **State announcements:** loading must announce "Loading"; disabled must convey "dimmed/unavailable".
- **Focus ring:** visible at all times when keyboard-focused; never suppress with `outline: none` without an equivalent token-driven replacement.
- **Reduced motion:** suppress press scale / ripple animation; instant background swap only.

## Do / Don't

✅ **Do** use exactly **one `primary`** per screen — it represents the screen's main action.
✅ **Do** use `destructive` only inside a confirmation dialog or when the action is genuinely irreversible.
✅ **Do** keep labels to 1–3 words; verb-first ("Save changes", "Delete account").

❌ **Don't** stack two `primary` buttons next to each other — promote one, demote the other to `secondary`.
❌ **Don't** use color alone to indicate disabled — opacity/contrast change must be reinforced by removing pointer affordance.
❌ **Don't** put `destructive` as the default-focused button in a dialog. Default focus belongs on the safe action.
