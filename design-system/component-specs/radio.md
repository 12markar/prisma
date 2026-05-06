# Radio

A control for picking one option from a small mutually-exclusive set (≤6).

## Anatomy

```
( • )  Label
       Helper text (optional)
```

- **Outer ring** — 20×20, circular, 2px stroke.
- **Inner dot** — 10×10, circular. Visible only when selected.

## States & token mapping

| State                | Ring                    | Dot                |
|----------------------|-------------------------|--------------------|
| unselected           | `border.strong`         | —                  |
| unselected + hover   | `border.strong` + `surface.sunken` halo | —     |
| selected             | `accent.default`        | `accent.default`   |
| selected + hover     | `accent.hover`          | `accent.hover`     |
| focused              | 2px ring `border.focus` outside, 2px offset    |
| disabled (unselected)| `border.subtle`         | —                  |
| disabled (selected)  | `text.disabled`         | `text.disabled`    |
| error (group level)  | `status.danger.default` | per selection      |

## Accessibility

- **Role:** `radio` inside `radiogroup` (HTML), `selectable` Modifier with `Role.RadioButton` (Compose), `Picker` or custom `accessibilityRole(.radio)` (SwiftUI).
- **Group label is REQUIRED** — radios are meaningless alone. The group needs a programmatic label.
- **Keyboard:** arrow keys move within group; `Tab` enters/exits group (NOT between radios).
- **Min touch target:** 44pt / 48dp.

## Do / Don't

✅ **Do** preselect a sensible default if one exists; never leave required groups blank when a default is obvious.
✅ **Do** stack vertically when labels exceed ~12 chars; horizontally only for short, predictable options.

❌ **Don't** use radios for >6 options — use a select/menu.
❌ **Don't** mix radios and checkboxes in the same group.
