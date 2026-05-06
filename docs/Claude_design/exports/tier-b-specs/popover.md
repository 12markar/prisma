# Popover

A floating panel anchored to a trigger, holding **interactive** content (forms, menus, controls). Distinguished from tooltips by: (1) interactive contents allowed, (2) persistent until explicitly dismissed, (3) can take focus.

## Anatomy
```
   ┌───────────────────────┐
   │  ┌─────────────────┐  │
   │  │ Header (opt)    │  │
   │  ├─────────────────┤  │
   │  │ Body            │  │
   │  │ ...             │  │
   │  ├─────────────────┤  │
   │  │ Footer (opt)    │  │
   │  └─────────────────┘  │
   └───────┬───────────────┘
           ▼ caret (optional)
        [ trigger ]
```
- **Body**: `--surface-raised`, 1px `--border-subtle`, `--radius-lg` (12px), `--shadow-4`. Min width 240px, max 480px.
- **Padding**: 16px default, 8px when contents are a menu (rows have their own padding).
- **Caret**: 12×6 (matches tooltip ratio at 1.5× scale). Optional — omit when popover is wide and acts more as a panel.

## Caret geometry
- Width 12px, height 6px.
- Tip touches trigger bbox at 8px offset.
- Caret border: matches `--border-subtle`. Use a 2-path SVG (fill + stroke) to maintain a consistent border alongside the body.
- Caret hidden when popover is **wider than 360px** — the geometric tie becomes optical noise; the shadow does the anchoring work instead.

## Position math
Same flip strategy as tooltip, but with these differences:
- Default offset: **10px** from trigger (vs 8px for tooltip).
- Cross-axis alignment: **trigger start** by default (left-align in LTR), not center.
- When user explicitly sets `align: center` and the popover would clip, flip the alignment edge before flipping the side.

## States
- **Hidden**.
- **Open**: opacity 0→1 + scale 0.96→1 + translate 4px→0. 220ms `--ease-emphasized`. Origin = the caret tip.
- **Closing**: opacity 1→0, 140ms `--ease-standard`. No scale on close — only fade.

## Dismissal
A popover closes on:
1. Escape key.
2. Click outside (mousedown captured at document level).
3. Trigger re-clicked (toggle).
4. An interactive child completing its action (e.g. menu item selected) — opt-in via `dismissOnAction`.

## Focus management
- On open: focus moves to first focusable child. If none, focus stays on trigger; popover gets `tabindex="-1"` and role `dialog`.
- Tab is trapped within the popover.
- On close: focus returns to trigger.

## Variants
- **Menu**: list of actions. No header/footer. Min width 200px. Use the `menuitem` role pattern.
- **Form**: contains inputs. Always has a footer with primary/secondary actions.
- **Info**: read-only rich content (think profile cards). Has a close button in the top-right.

## Accessibility
- `role="dialog"` for forms, `role="menu"` for menus.
- `aria-expanded` on trigger reflects open state.
- Trap focus when modal-style; allow focus to escape via Tab when used as a transient menu (more native-feeling).

## Don'ts
- Don't put a popover inside another popover. Use a modal for nested workflows.
- Don't auto-open on hover. Always require an explicit user action.
- Don't omit the focus ring on the trigger when popover is open — users need to know which trigger this popover belongs to.
