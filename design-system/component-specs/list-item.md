# List Item

A single row in a list. The most-reused container in the catalogue itself.

## Anatomy

```
┌────────────────────────────────────────────────────────┐
│  [leading]  Primary text                  [trailing]   │
│             Secondary text (optional)                  │
└────────────────────────────────────────────────────────┘
```

- **Leading** (optional) — icon (24×24), avatar (32 / 40), or status dot. Slot is left-aligned.
- **Primary** — `body.md`, `text.primary`, weight 500. Required.
- **Secondary** (optional) — `body.sm`, `text.secondary`. Truncates after 2 lines with ellipsis.
- **Trailing** (optional) — chevron, switch, badge, value text (`body.sm`, `text.tertiary`), or icon-button. Right-aligned.

## Sizes

| Size      | Min height | Vertical padding | Horizontal padding |
|-----------|------------|------------------|--------------------|
| `sm`      | 44         | `spacing.2`      | `spacing.4`        |
| `default` | 56         | `spacing.3`      | `spacing.4`        |
| `lg`      | 72         | `spacing.4`      | `spacing.4`        |

Min-height enforces the 44pt / 48dp touch target even when content is small.

## Variants

- **Default** — read-only display row.
- **Tappable** — entire row is the hit target. Adds a hover/pressed background per state.
- **Selectable** — has a checked state; selected row uses `accent.subtle` background and `accent.default` left-edge accent strip (3px).
- **Toggle** — trailing slot is a `Switch`. Tapping anywhere on the row toggles.

## States

- **Default** — `surface.base` (or `surface.raised` inside a card).
- **Hover** (pointer) — `surface.sunken` background fade, `motion.duration.fast`.
- **Pressed** — `accent.subtle` background flash (`motion.duration.fast` in/out).
- **Focused** (keyboard) — 2px `border.focus` ring inset 2px.
- **Selected** (selectable variant) — `accent.subtle` background, `accent.default` 3px left strip.
- **Disabled** — content drops to `text.disabled`, no hit response.

## Tokens used

- `body.md`, `body.sm`, `text.primary`, `text.secondary`, `text.tertiary`, `text.disabled`
- `surface.base`, `surface.sunken`, `accent.subtle`, `accent.default`
- `border.focus`
- `spacing.2`, `spacing.3`, `spacing.4`
- `motion.duration.fast`, `motion.easing.standard`

## Accessibility

- **Tappable** — `Role.Button` (Compose) / `accessibilityAddTraits(.isButton)` (SwiftUI). Combined accessibility element so screen reader reads "Primary, Secondary" as one focus stop, not three.
- **Toggle** — `Role.Switch` / `.isToggle`; announces "On"/"Off" state via `accessibilityValue`.
- **Selectable** — `Role.Checkbox` (single-selection list) or `selectableGroup` parent + `selected` state.
- **Min hit target 44×44 / 48×48 dp** — `sm` height 44 assumes adequate top/bottom padding around the touch area. Never below.
- **Inset divider** between rows (see `divider.md` — inset variant).

## Do / Don't

✅ **Do** combine accessibility children so VoiceOver/TalkBack reads the row as a single navigable unit.
✅ **Do** use chevron (trailing) to signal "this row navigates somewhere"; omit if it's an inline action.
✅ **Do** truncate secondary text — never wrap to 3+ lines. Move that content to a detail screen.

❌ **Don't** put two interactive elements in one row without distinct hit areas — users will mis-tap.
❌ **Don't** mix sizes within a single list — pick one and keep the rhythm consistent.
❌ **Don't** apply elevation — list items are surfaces, not cards. The container they sit in carries elevation.
