# Drawer

A panel that slides in from a viewport edge, holding secondary content or a workflow. Distinguished from a modal: edge-anchored (not centered), keeps the user oriented to the page behind, often non-blocking.

## Anatomy
```
                   ┌───────────────────┐
                   │ Title         × │ │
                   ├─────────────────┤ │
                   │                 │ │  ← Page content
                   │  Body content   │ │     visible behind
                   │                 │ │     on the left
                   │                 │ │
                   ├─────────────────┤ │
                   │   [Cancel] [Save]│ │
                   └───────────────────┘
                       ↑ scrim (lower opacity than modal)
```
- **Panel**: anchored to one edge. `--surface-base`, 1px `--border-subtle` on the inner edge, `--shadow-5` on the inner edge only (the outer edge is flush with the viewport). No outer rounding; inner edge optionally rounded `--radius-lg` for a "card-attached" feel.
- **Header**: 56px tall, sticky. Title `--font-sans` 18px weight 600, close button 32×32 at `--text-secondary`.
- **Body**: scrollable. 24px padding default. Has its own scroll context — body scrolls, page behind stays put.
- **Footer**: 64px tall, sticky to bottom, separator above (1px `--border-subtle`). Right-aligned action buttons.
- **Scrim**: `rgba(0,0,0, 0.32)` (light) / `rgba(0,0,0, 0.48)` (dark). Lower than modal scrim — drawer is less interrupting.

## Sizes
| Anchor | Width / Height | Notes |
|---|---|---|
| Right (default) | 480px | Forms, filters, details. |
| Right (wide) | 640px | Multi-section content. |
| Right (full) | 100vw | Mobile. |
| Left | 320px | Navigation. |
| Bottom | 60vh | Mobile actions, picker sheets. |
| Top | 240px | Notifications panel, command results. |

Width/height are clamped to the smaller of the configured value and viewport minus 64px gutter.

## Modal vs non-modal
- **Modal drawer**: scrim is opaque to clicks (closes drawer on click). Page behind is `aria-hidden`. Focus trapped.
- **Non-modal drawer** (also called "side panel"): no scrim, page remains interactive. Common for inspector panels.

Choose modal when the drawer represents a workflow that should complete; non-modal when it's a persistent secondary view.

## Motion
- **Open**: 280ms `--ease-emphasized`. Panel translates in from its edge (translate100% → 0). Scrim fades 0 → target opacity over the same duration.
- **Close**: 200ms `--ease-standard`. Reverse.
- **Drag-to-close** (touch): the panel follows the finger 1:1; on release, snap-close if dragged past 30% of panel size, otherwise snap back. Velocity-based override: if release velocity > 1500px/s in the close direction, close regardless of position.

## Stacking
Multiple drawers can stack from the same edge — each new one slides over the previous, the previous shifts 32px deeper into the viewport. Esc closes top-most only. Limit to 3.

## States
- **Hidden** (default).
- **Opening**.
- **Open**.
- **Closing**.

Only one transition state at a time — interrupting an open mid-flight reverses cleanly.

## Accessibility
- `role="dialog"` (modal) or `role="region"` (non-modal) with `aria-modal` reflecting the state.
- Always has an accessible name via `aria-labelledby` (pointing to the title) or `aria-label`.
- Focus management:
  - On open: focus moves to the first interactive element, falling back to the close button.
  - On close: focus returns to the trigger.
  - Focus trap (modal only): Tab cycles within drawer.
- Escape closes (modal). Configurable for non-modal — usually no, since they're persistent.
- Body scroll lock on modal drawers (page behind doesn't scroll on wheel).

## Tokens
| Property | Token |
|---|---|
| Panel bg | `--surface-base` |
| Inner border | `--border-subtle` |
| Shadow | `--shadow-5` |
| Scrim | `rgba(0,0,0, 0.32)` light · `0.48` dark |
| Header sep | `--border-subtle` |

## Don'ts
- Don't use a drawer when a modal is the right shape. If the workflow blocks all other action and the user can't see the page behind, use a modal.
- Don't put primary destructive actions inside a non-modal drawer's footer — they need a confirm step.
- Don't auto-close on success without confirmation. The user might have wanted to do another action; close is an explicit choice.
- Don't put more than 1 drawer per edge open at a time except in stacking mode. Two drawers fighting for the same space is broken.
