# Chip

A compact, interactive token representing a discrete value, filter, or suggestion.

## Variants

| Variant      | Behaviour                                                         |
|--------------|-------------------------------------------------------------------|
| `filter`     | Toggleable. Applies/removes a filter. Selected state visible.     |
| `input`      | Removable token (e.g. recipient pill). Has a trailing × button.   |
| `suggestion` | Single-tap chip that performs an action; not toggleable.          |

## Anatomy

```
[ leadingIcon ]  Label  [ trailingIcon | × ]
```

- Container: height 32, horiz padding 12, `radius.full`.
- Label: `label.md`.
- Leading icon: 16×16 (optional). For `filter` selected, becomes a check.

## States & token mapping

### Filter chip

| State                 | Border             | Fill             | Label / icon           |
|-----------------------|--------------------|------------------|------------------------|
| unselected            | `border.default`   | transparent      | `text.primary`         |
| unselected + hover    | `border.strong`    | `surface.sunken` | `text.primary`         |
| selected              | none               | `accent.default` | `text.onAccent`        |
| selected + hover      | none               | `accent.hover`   | `text.onAccent`        |
| focused (any state)   | 2px ring `border.focus` outside, 2px offset                |
| disabled              | `border.subtle`    | transparent      | `text.disabled`        |

### Input chip

- Always renders with `border.subtle` + `surface.sunken` fill.
- Trailing × is an icon button at 16×16 with its own 24×24 hit target.

### Suggestion chip

- Same defaults as unselected filter chip but with `border.subtle`.

## Accessibility

- **Role:** `button` (filter, suggestion); `button` group with descriptive label per chip (input).
- **Filter:** announce `aria-pressed`. "Filter: Available, selected".
- **Input remove:** the × is a separate focusable button with `accessibilityLabel="Remove {value}"`.
- **Group:** chip rows are `role="group"` with a label like "Filters".
- **Min touch target:** the chip itself meets 44pt by external hit-area expansion (visual height stays 32).

## Do / Don't

✅ **Do** wrap chip rows. Don't horizontally scroll a row of chips on mobile unless the row is very long and you provide an "expand" affordance.
✅ **Do** sort filter chips by frequency or alphabetically — pick one per surface and stick.

❌ **Don't** use chips as primary navigation. Use tabs or list items.
❌ **Don't** mix selectable filter chips and non-selectable suggestion chips in the same row.
