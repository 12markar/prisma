# Badge

A compact label indicating count or status.

## Variants

### Count badge

```
( 12 )      ( 99+ )
```

- Pill, `radius.full`, height 18, min-width 18, horiz padding 6.
- Text: `label.sm`, `accent.onAccent`, weight 600.
- Fill: `accent.default` (or `status.danger.default` for unread/alert).
- Numbers >99 display as `99+`.

### Dot badge

```
( • )
```

- 8×8 circle, `accent.default` (or status color).
- Used to indicate "unread" / "available" without a count.

## Positioning

- Sits at the top-right of the host element (icon, avatar, tab).
- Offset: -4px / -4px from the host bounds.
- A 2px ring of the parent surface color is rendered behind the badge to separate it visually from the host.

## Accessibility

- Counts MUST be announced. Use `aria-label="12 unread"` rather than letting the screen reader read the number alone.
- For dot badges, announce semantically: "New" / "Unread" — never read out the visual decoration.
- Don't rely on color alone for status — pair the dot with text where possible (e.g. "Online •").

## Do / Don't

✅ **Do** keep counts to <4 chars; use `99+` past 99.
✅ **Do** use status colors only when the count represents that status (red = errors / unread urgent).

❌ **Don't** use count badges for non-numeric content. Use chips for that.
❌ **Don't** apply a badge to a button that already has a visible state — it doubles up signal.
