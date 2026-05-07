# Breadcrumb

A horizontal trail showing the user's path through a hierarchy. Each crumb is a clickable ancestor; the current location is the rightmost, non-interactive segment.

## Anatomy
```
Home  /  Catalogues  /  Vinyl  /  ▸ Modern Classics
```
- **Container**: 32px tall row, `--font-sans` 13px, `--text-secondary` for ancestors, `--text-primary` for current.
- **Separator**: forward slash `/` at `--text-tertiary` opacity 50%, 8px horizontal margin on each side. Optional alternative: chevron `›` for a more "navigational" feel.
- **Crumb (ancestor)**: link styling. Underline on hover only.
- **Crumb (current)**: weight 500, `--text-primary`, no underline, not a link.

## Truncation
When the trail overflows:
1. Keep the **first** crumb (root).
2. Keep the **last 2** crumbs (parent + current).
3. Replace middle crumbs with an ellipsis button `…`.
4. Clicking the ellipsis opens a popover listing the hidden ancestors as a vertical list.

```
Home  /  …  /  Vinyl  /  ▸ Modern Classics
```

Threshold: collapse when total crumbs ≥ 4 OR rendered width > container width.

## Variants
- **Static** (default).
- **Editable**: click on the current crumb turns it into an inline text field for renaming the current node. Useful in file/document apps.
- **Dropdown crumbs**: each crumb is a dropdown trigger showing siblings — click the chevron to navigate laterally. Use sparingly; complicates the visual.

## States
- **Default**.
- **Hover** (ancestor): `--text-primary`, underline 1px, offset 3px.
- **Focus-visible**: 2px `--border-focus` ring, 2px offset, `--radius-sm`.
- **Active** (mid-press): no special treatment — the navigation happens instantly.
- **Loading** (fetching ancestors): show skeleton crumbs (3 of them) at `--surface-sunken` filling 60–100px each. Animate shimmer.

## Mobile
On narrow viewports (<480px):
- Show only `‹  Parent name` — a back-link with the immediate parent. The full trail collapses.
- Or, show only the current crumb with a leading "…" disclosure that opens the full path as a sheet/popover.

## Motion
- No motion on navigation between crumbs — page transitions handle this.
- Truncation popover: standard popover motion (220ms fade+scale).
- Inline editing transition: input materializes in place over the crumb (140ms fade), border draws in 80ms.

## Accessibility
- `<nav aria-label="Breadcrumb">` wrapping an `<ol>`.
- Each `<li>` contains a link except the last, which is an `aria-current="page"` element.
- Separators are decorative — `aria-hidden="true"`.
- Truncation button: `aria-label="Show 4 hidden ancestors"`, expanded popover is `role="menu"` with `menuitem` children.

## Tokens
| Property | Token |
|---|---|
| Ancestor text | `--text-secondary` |
| Current text | `--text-primary` |
| Separator | `--text-tertiary` 50% |
| Hover underline | `--text-primary` |
| Focus ring | `--border-focus` |

## Don'ts
- Don't include the current page as a link. `aria-current` and visual weight is the contract.
- Don't use breadcrumbs as the only navigation. They reflect hierarchy; they don't replace top-level nav.
- Don't show breadcrumbs on flat structures (single-level lists) — they read as decoration.
- Don't truncate by removing the first crumb. Users orient by it; ellipsis the middle instead.
