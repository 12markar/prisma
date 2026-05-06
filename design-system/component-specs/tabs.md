# Tabs

A horizontal navigation pattern for switching between sibling views.

## Anatomy

```
─────────────────────────────────────
 Overview    Activity    Settings
 ━━━━━━━━━                              ← active indicator
─────────────────────────────────────
```

- **Tab list** — horizontal row, optionally scrollable.
- **Tab item** — label (`label.lg`, `text.secondary` default; `text.primary` selected). Optional leading icon.
- **Active indicator** — 2px line under the active tab, color `accent.default`. Animates between tabs.
- **Divider** — 1px `border.subtle` along the bottom.

## Variants

| Variant      | Behaviour                                                |
|--------------|----------------------------------------------------------|
| `fixed`      | Tabs distribute equally across container width. Mobile.  |
| `scrollable` | Tabs sit at natural width; row scrolls horizontally.     |

## Sizes

| Size | Tab height | Padding (h) | Label       |
|------|------------|-------------|-------------|
| `md` | 44         | 16          | `label.lg`  |
| `sm` | 36         | 12          | `label.md`  |

## States & token mapping

| State        | Label color        | Indicator          |
|--------------|--------------------|--------------------|
| inactive     | `text.secondary`   | none               |
| inactive hover | `text.primary`   | none               |
| active       | `text.primary`     | `accent.default`   |
| focused      | label color + 2px ring `border.focus` |     |
| disabled     | `text.disabled`    | none               |

Indicator translates between tabs over `motion.duration.default` + `easing.standard`.

## Accessibility

- **Roles:** `tablist`, `tab`, `tabpanel` (HTML); Compose `TabRow` + `Tab`; SwiftUI `TabView` or custom with `accessibilityElement` + `.isHeader`.
- **Keyboard:** arrow keys move between tabs (no `Tab`). `Enter` / `Space` activates. Roving tabindex pattern.
- **`aria-selected`:** active tab.
- **Tab → panel association:** `aria-controls` / `aria-labelledby`.
- **Min touch target:** 44pt / 48dp.

## Do / Don't

✅ **Do** keep tabs to ≤6 — beyond that, scrolling is unavoidable and discoverability suffers.
✅ **Do** persist the selected tab when returning to a screen.

❌ **Don't** put primary destinations in tabs; use bottom navigation instead.
❌ **Don't** use tabs for unrelated content sections — tabs imply siblings.
