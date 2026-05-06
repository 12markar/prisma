# Segmented Control

A horizontal group of equal-width segments where exactly one is selected. Distinct from **Tabs** (Tabs change pages; Segmented Control is a form input that picks a value).

## Anatomy

```
┌─────────┬─────────┬─────────┐
│  Day    │  Week   │  Month  │
└─────────┴─────────┴─────────┘
              ▲ selected
```

- **Track** — `surface.sunken` background, `radius.md`, 2px internal padding.
- **Segments** — equal width, divider lines hidden when one is selected (the selection chip covers them).
- **Selection chip** — `surface.raised` fill, `radius.md` (slightly smaller than track), `elevation.1` light / `border.default` dark.
- Slides smoothly between segments when selection changes.

## Sizes

| Size      | Height | Segment label | Internal padding |
|-----------|--------|---------------|------------------|
| `sm`      | 32     | `label.sm`    | `spacing.2`      |
| `default` | 40     | `label.md`    | `spacing.3`      |

## Constraints

- **2 to 5 segments.** Past 5, switch to a Dropdown or Chip group.
- All segments equal width — content longer than the narrowest segment truncates with ellipsis.
- Segments are **labels only** by default; icon + label optional but reduces breakpoint space.

## States

- **Default** — text uses `text.secondary`.
- **Selected** — `text.primary` weight 600, on `surface.raised` chip.
- **Hover** (non-selected) — `text.primary` (full opacity), no background change.
- **Pressed** — chip dims briefly (`accent.subtle` background flash, `motion.duration.fast`).
- **Focused** (keyboard) — 2px `border.focus` ring around the entire control (not individual segments).
- **Disabled** — text drops to `text.disabled`; not focusable.

## Tokens used

- `surface.sunken`, `surface.raised`
- `text.primary`, `text.secondary`, `text.disabled`
- `border.default`, `border.focus`, `accent.subtle`
- `radius.md`
- `elevation.1`
- `label.sm`, `label.md`
- `motion.duration.default` (chip slide), `motion.duration.fast` (press)
- `motion.easing.standard`, `motion.easing.spring` (chip slide — gives the satisfying snap)

## Keyboard

- `←` / `→` — move selection (mirrored RTL).
- `Home` / `End` — first / last segment.
- `Space` / `Enter` — confirm (no-op when arrows already moved selection).

## Accessibility

- **Role** — `Role.RadioGroup` (Compose) / `accessibilityElement(children: .combine)` + `.accessibilityAddTraits(.isHeader)` per segment as `.isSelected` (SwiftUI).
- Each segment announces "Day, selected, 1 of 3".
- The chip animation is **decorative** — should not affect a11y state announcements (announce immediately on selection change).
- Min touch target preserved — entire control min height 32 means individual segment height matches; the parent wrapper provides 44pt/48dp via outer padding when standalone.
- **Reduced motion** — chip move is instant.

## Do / Don't

✅ **Do** use segmented controls for binary or few-option choices that affect immediate display (sort order, grouping, view mode).
✅ **Do** keep segment labels short — 1–2 words.
✅ **Do** animate chip slide using `spring` easing — it's the signature motion of this control.

❌ **Don't** use segmented controls for navigation between pages — that's Tabs.
❌ **Don't** use 6+ segments. Past 5, the segments shrink below tap-target requirements on phones.
❌ **Don't** mix icon-only and label segments in the same control — pick one.
