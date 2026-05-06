# Checkbox

A binary or tri-state selection control.

## Anatomy

```
[ ☐ ]  Label
        Helper text (optional)
```

- **Box** — 20×20 square, `radius.sm`.
- **Glyph** — checkmark (checked) or horizontal bar (indeterminate).
- **Label** — `typography.body.md`, `text.primary`. Tappable.
- **Helper** — `body.sm`, `text.tertiary`.

## States & token mapping

| State                  | Box border         | Box fill                | Glyph                |
|------------------------|--------------------|-------------------------|----------------------|
| unchecked              | `border.strong`    | transparent             | —                    |
| unchecked + hover      | `border.strong`    | `surface.sunken`        | —                    |
| checked                | none               | `accent.default`        | `accent.onAccent`    |
| checked + hover        | none               | `accent.hover`          | `accent.onAccent`    |
| indeterminate          | none               | `accent.default`        | `accent.onAccent` (bar) |
| focused (any value)    | 2px ring `border.focus` 2px offset                |                       |
| disabled (unchecked)   | `border.subtle`    | `surface.sunken`        | —                    |
| disabled (checked)     | none               | `text.disabled`         | `surface.base`       |
| error                  | `status.danger.default` | transparent        | —                    |

## Accessibility

- **Role:** `checkbox`. Tri-state uses `aria-checked="mixed"` / Compose `ToggleableState.Indeterminate` / SwiftUI custom `accessibilityValue`.
- **Label:** entire row (label + helper) is the hit target. Tapping label toggles.
- **Min touch target:** 44×44pt. The 20×20 visual sits inside an enlarged tap area.
- **Keyboard:** `Space` toggles. `Tab` enters/exits.
- **Group labelling:** when multiple checkboxes form a group, wrap in a `fieldset` / `Modifier.semantics(mergeDescendants = false)` group with a group label.

## Do / Don't

✅ **Do** use indeterminate only on a parent that summarises a partially-selected list of children.
✅ **Do** keep labels consistent (all positive phrasing — "Email me updates", not "Don't email me updates").

❌ **Don't** use a checkbox for mutually-exclusive options — use radio.
❌ **Don't** put a checkbox inside a row that also has its own tap action; pick one.
