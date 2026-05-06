# Avatar

A circular representation of a user, entity, or initial.

## Anatomy

```
( IMG )    ( KS )    ( ☺ )
```

Three fallbacks, in order of priority:

1. **Image** — square cropped to `radius.full`. Subject should be face/icon centered.
2. **Initials** — 1 or 2 letters derived from the name. `label.md` weight 600. Centered.
3. **Icon** — when neither image nor name is available, a generic person/object icon at 60% of avatar size.

## Sizes

| Size      | Diameter | Initials weight | Initials font   |
|-----------|----------|-----------------|-----------------|
| `xs`      | 20       | 600             | `label.sm`      |
| `sm`      | 28       | 600             | `label.sm`      |
| `default` | 40       | 600             | `label.md`      |
| `lg`      | 56       | 600             | `title.sm`      |
| `xl`      | 96       | 600             | `headline.sm`   |

## Color (initials variant)

Background derived deterministically from the user's name (or seed) — pick one of 6 muted swatches drawn from `accent.subtle`, `status.success.subtle`, `status.warning.subtle`, `status.danger.subtle`, `status.info.subtle`, `surface.sunken`. Foreground always uses the matching `*.default` for the same family (or `text.primary` for the neutral swatch). This keeps initials at AA contrast minimum across both light and dark.

## Status indicator (optional)

Small dot at the avatar's lower-right.

- 25% of avatar diameter, min 8.
- Border: 2px ring of `surface.base` to separate from the avatar fill.
- Color tokens: `status.success.default` (online), `status.warning.default` (away), `text.tertiary` (offline), `status.danger.default` (busy).

## Tokens used

- `radius.full`
- `accent.subtle`, `status.*.subtle`, `status.*.default`, `surface.sunken`
- `text.primary`, `text.tertiary`
- `surface.base` (status indicator border)
- `label.sm`, `label.md`, `title.sm`, `headline.sm`

## Accessibility

- **Image variant** — `accessibilityLabel` describes the person ("Karan Sharma"). The image element itself uses `contentDescription` / `accessibilityLabel`; the visual is otherwise treated as decorative.
- **Initials / icon variant** — same — describes the represented entity, not the visual ("Avatar of Karan Sharma" is redundant; "Karan Sharma" is enough).
- **Status dot** — included in the avatar's accessibility text: "Karan Sharma, online". Don't expose the dot as a separate focusable element.
- **Decorative** — when the avatar is purely visual chrome (e.g. trailing element with no semantic meaning), use `accessibilityHidden` / null content description.

## Do / Don't

✅ **Do** derive initials background colour deterministically — same name → same colour every time.
✅ **Do** include the status indicator's meaning in the avatar's accessibility text, not as a separate node.
✅ **Do** fall back gracefully: image fails → initials → icon. Never show a broken image.

❌ **Don't** crop human faces tightly — keep ~10% padding so eyes are not at the edge.
❌ **Don't** use status indicators on avatars smaller than `sm` (28) — the dot becomes invisible.
❌ **Don't** apply elevation or borders to avatars by default — only when explicitly stacked (avatar group context).
