# TextField

A single- or multi-line text input.

## Anatomy

```
Label                                       [ optional flag ]
┌──────────────────────────────────────────┐
│ leadingIcon  placeholder / value   trail │
└──────────────────────────────────────────┘
Helper text  ·  N/M characters
```

- **Label** — always visible above the field. `typography.label.md`.
- **Container** — bordered or filled rectangle holding the input.
- **Leading / trailing slot** — icons, units, action buttons (clear, reveal-password).
- **Helper text** — `typography.body.sm`, `text.tertiary`. Becomes error text in error state.
- **Counter** (optional) — right-aligned, `body.sm`, `text.tertiary`.

## Variants

| Variant   | Container                                  | Use case                              |
|-----------|--------------------------------------------|---------------------------------------|
| `outlined`| 1px `border.default`, transparent fill     | Default. Forms, settings.             |
| `filled`  | `surface.sunken` fill, no border           | Dense UI, search bars.                |

## Sizes

| Size | Height | Padding (h × v) | Body          | Radius      |
|------|--------|-----------------|---------------|-------------|
| `sm` | 36     | 12 × 8          | `body.sm`     | `radius.md` |
| `md` | 44     | 14 × 10         | `body.md`     | `radius.md` |
| `lg` | 52     | 16 × 14         | `body.lg`     | `radius.md` |

## States & token mapping

| State    | Border                            | Fill                  | Label color        | Helper color       |
|----------|-----------------------------------|-----------------------|--------------------|--------------------|
| default  | `border.default`                  | per variant           | `text.secondary`   | `text.tertiary`    |
| hover    | `border.strong`                   | per variant           | `text.secondary`   | `text.tertiary`    |
| focused  | `border.focus` 2px + 3px halo at `accent.subtle` | per variant | `text.primary` | `text.tertiary` |
| filled   | (value present) `border.default`  | per variant           | `text.secondary`   | `text.tertiary`    |
| error    | `status.danger.default` 2px       | per variant           | `status.danger.default` | `status.danger.default` |
| disabled | `border.subtle`                   | `surface.sunken`      | `text.disabled`    | `text.disabled`    |
| read-only| `border.subtle`                   | `surface.sunken`      | `text.secondary`   | `text.tertiary`    |

## Accessibility

- **Role:** `textbox` / `TextField` / `UITextField`. Use `secureTextEntry` for passwords.
- **Label association:** every input MUST have a programmatically-associated label (HTML `for=`, Compose `Modifier.semantics { contentDescription = … }` if no visual label, SwiftUI `accessibilityLabel`).
- **Error announcement:** when error state activates, announce the helper-text message via `aria-live="polite"` / `accessibilityNotification(.announcement)` — never rely on color alone.
- **Min touch target:** 44pt / 48dp. The visual height can be 36 only if the touch target is enlarged externally.
- **Required fields:** mark with the word "Required" or `*` paired with an a11y label, never with color/asterisk alone.

## Do / Don't

✅ **Do** show errors inline below the field and on submit attempt.
✅ **Do** keep labels above the field for predictable layout, especially on small screens.
✅ **Do** use `filled` for compact toolbars; `outlined` for forms.

❌ **Don't** use placeholder text as the only label — it disappears on focus.
❌ **Don't** validate on every keystroke for fields the user is still typing into; validate on blur or submit.
❌ **Don't** color the entire field red — only the border/helper. Heavy red fills feel hostile.
