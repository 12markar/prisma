# Tooltip

Brief, context-providing text that appears on hover/focus/press of an element. Non-blocking, dismissible, never required reading.

## Anatomy
```
       ┌──────────────┐
       │ Tooltip text │
       └──────┬───────┘
              ▼ caret
       [ trigger element ]
```
- **Body**: `--surface-raised` (light) / `--neutral-800` (dark), 8px vertical / 10px horizontal padding, `--radius-md` (6px), `--shadow-3`.
- **Caret**: 8×4 triangle, same fill as body, snapped to body edge with 1px overlap to avoid hairline gap.
- **Text**: `--font-sans`, 13px, `--text-primary` on raised surface. Max 2 lines / 32 words.

## Caret geometry
- Width 8px, height 4px (2:1 ratio).
- Caret tip touches the trigger's bounding box at **8px offset** from trigger edge.
- Caret never enters the corner radius zone — minimum 12px from any rounded corner of the body.
- On placement flip (e.g. top→bottom because of viewport), the caret rotates by SVG transform, not via re-render — keeps it pixel-stable.

## Position math
Place anchored to `trigger`, with `body` placed at the chosen side:
```
side: 'top'    → body.bottom = trigger.top - 8px (gap)
side: 'bottom' → body.top    = trigger.bottom + 8px
side: 'left'   → body.right  = trigger.left - 8px
side: 'right'  → body.left   = trigger.right + 8px
```
Cross-axis: align caret to **trigger center**, then clamp body's cross-axis position to viewport with 8px margin.

**Flip strategy**: try preferred side first. If the body would clip viewport on that side, flip to the opposite side. If both axes clip, prefer flipping along the longer free space.

## States
- **Hidden** (default).
- **Showing**: opacity 0→1, translate 4px→0 along caret axis, 180ms `--ease-emphasized`.
- **Hiding**: reverse, 120ms `--ease-standard`.

## Timing
- **Show delay**: 600ms hover, 0ms focus.
- **Hide delay**: 100ms.
- **Skip delay** (rapid traversal): if any tooltip showed in the last 1500ms, the next tooltip shows instantly.

## Variants
- **Description** (default): plain text.
- **Shortcut**: text + a `kbd` chip on the right (e.g. "Save · ⌘S").
- **Rich** (use sparingly): allows a single `strong` and a single inline link. Never lists, never images.

## Accessibility
- `role="tooltip"`, `aria-describedby` on the trigger.
- Visible on **focus**, not just hover.
- Dismiss on Escape — global listener while shown.
- Never put critical or interactive content inside. Tooltips are advisory.

## Don'ts
- Don't use a tooltip for content the user *needs* to read. Use inline help text.
- Don't put links, buttons, or form fields inside.
- Don't pin a tooltip open ("?" icons that toggle persistent labels). Use a popover for that.
