# Command Palette

A modal overlay invoked by `⌘K` / `Ctrl+K` that lets the user fuzzy-search and execute any command instantly. The keyboard-first power-user surface.

## Anatomy

```
┌──────────────────────────────────────────────────────┐
│  🔍  Search commands...                          ⏎    │
├──────────────────────────────────────────────────────┤
│  RECENT                                              │
│  → Toggle dark mode                                  │
│  → Go to Typography                                  │
│                                                      │
│  COMPONENTS                                          │
│    Button                                            │
│    Checkbox                                          │
│  ▶ Bottom sheet           ⌥B                         │
│    ...                                               │
│                                                      │
│  FOUNDATIONS                                         │
│    Colors                                            │
│    Spacing                                           │
└──────────────────────────────────────────────────────┘
```

- **Container** — fixed-width 560 (max), centered, `surface.raised`, `radius.lg`, `elevation.4`. Top-aligned 96px below viewport top.
- **Search field** — large input with leading magnifier icon, `body.lg`, no border (full-width). 1px bottom `border.subtle` divider.
- **Result list** — sectioned, scrollable. Each section header `label.sm`, weight 600, `text.tertiary`, uppercase.
- **Result row** — 40 height, `body.md`, padding `spacing.3` horizontal. Optional trailing keyboard shortcut chip in `code.sm` `text.tertiary`.
- **Active row** — `accent.subtle` background, leading triangle/chevron pointer, `body.md` weight 500 `text.primary`.
- **Empty state** — when no matches, centered "No commands match `query`" in `body.md` `text.tertiary` + suggestion: "Try fewer words" or recent commands.
- **Backdrop** — `surface.overlay` covering the rest of the page; click dismisses.

## Sub-states

- **Empty query** — shows "RECENT" (last ~8 commands) + suggested groups (e.g. all Foundations). No filtering.
- **With results** — sections collapse when empty; first result auto-selected.
- **No results** — empty state pattern (see `empty-state.md`).

## Triggers

- `⌘K` (macOS / iPad) / `Ctrl+K` (other) — global shortcut, opens immediately.
- Tap on the search affordance in catalogue chrome — opens.
- `Esc` dismisses; click on backdrop dismisses.

## Animation

- Enter: `motion.duration.default`, `motion.easing.decelerate`. Backdrop fade + container fade + scale 0.96 → 1.0.
- Exit: `motion.duration.fast`, `motion.easing.accelerate`. Fade only.
- **Reduced motion** — instant in/out.

## Keyboard

- `↑` / `↓` — move active row.
- `Enter` — execute active command.
- `Esc` — dismiss.
- `Tab` — focuses input (default); should not cycle out of the modal.
- Section navigation — `⌘↑` / `⌘↓` jumps to previous / next section header.

## Tokens used

- `surface.raised`, `surface.overlay`
- `border.subtle`
- `accent.subtle`
- `text.primary`, `text.tertiary`
- `body.lg`, `body.md`, `label.sm`, `code.sm`
- `radius.lg`, `radius.md`
- `elevation.4`
- `spacing.3`
- `motion.duration.default`, `motion.duration.fast`
- `motion.easing.decelerate`, `motion.easing.accelerate`

## Accessibility

- **Role** — `Role.Dialog` on the container; `accessibilityViewIsModal: true` (SwiftUI) / `Modifier.semantics { isContainer = true }` (Compose).
- **Focus trap** — open shifts focus into the search field; Tab cycles only inside the palette.
- **Search field** — `accessibilityLabel("Search commands")`.
- **Result rows** — `Role.Button`. Active row exposes `selected = true` so screen readers announce position ("Bottom sheet, 3 of 12, selected").
- **Live region** — result count announced when query changes ("12 results"). Polite priority.
- **Esc** dismisses; focus returns to the trigger element.
- Section headers `accessibilityAddTraits(.isHeader)`.

## Fuzzy search

- Match algorithm: substring match against title (priority 1) and tags (priority 2). Recent commands surfaced first when query empty.
- Scoring biases: starts-with > word-boundary > anywhere. Exact title match always #1.
- Highlight matched substrings within result text using `accent.default`.

## Do / Don't

✅ **Do** make the palette discoverable — visible affordance in chrome ("Search components ⌘K") so first-time users know it exists.
✅ **Do** include keyboard shortcut chips for commands that have them — teaches the user.
✅ **Do** persist recent commands across sessions (last 10).

❌ **Don't** make the palette the only way to access common functions. It's a power-user accelerator, not a primary nav.
❌ **Don't** show > 60 results without virtualisation — the list can get huge with tags. Cap at 30 visible, "show more" toggle.
❌ **Don't** delay opening — palette must feel instant. Pre-mount the component if needed.
