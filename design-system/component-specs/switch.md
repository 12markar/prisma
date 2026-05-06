# Switch

A binary on/off toggle for an immediate setting (no save step).

## Anatomy

```
Label                              [ ●━━ ]   ← off
                                   [ ━━● ]   ← on
Helper text (optional)
```

- **Track** — 44×26 pill, `radius.full`.
- **Thumb** — 22×22 circle, 2px inset from track edge.

## States & token mapping

| State           | Track                     | Thumb                |
|-----------------|---------------------------|----------------------|
| off             | `border.strong`           | `surface.raised`     |
| off + hover     | `border.strong` + sunken halo | `surface.raised` |
| on              | `accent.default`          | `accent.onAccent`    |
| on + hover      | `accent.hover`            | `accent.onAccent`    |
| focused         | 2px ring `border.focus` outside |                |
| disabled (off)  | `border.subtle`           | `surface.sunken`     |
| disabled (on)   | `text.disabled`            | `surface.base`      |

Animation: thumb translation uses `motion.duration.fast` + `motion.easing.spring`. Reduced-motion: instant.

## Accessibility

- **Role:** `switch` (HTML `role="switch"`, Compose `Role.Switch`, SwiftUI `Toggle`).
- **Label:** describes the SETTING, not the action ("Notifications", not "Turn on notifications").
- **State announcement:** "On" / "Off" — never just an icon.
- **Min touch target:** 44pt / 48dp. The 44×26 track sits inside a larger hit area.

## Do / Don't

✅ **Do** apply changes immediately. If a Save step is required, use a checkbox instead.
✅ **Do** label the setting; let the switch state communicate the value.

❌ **Don't** wrap a switch in a confirmation dialog ("Are you sure you want to enable notifications?") — defeats the purpose.
❌ **Don't** use a switch for selecting between two equivalent options — that's a segmented control.
