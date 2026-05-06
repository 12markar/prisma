# Pagination

Numbered navigation across pages of a result set. Use when total count is known and ≥ ~50; below that, "load more" reads better.

## Anatomy
```
   ‹  1  …  4  5  ●6  7  8  …  42  ›
```
- **Container**: 40px tall, horizontal flex, gap 4px, `--font-mono` 13px (numbers feel ledger-like in mono).
- **Page button**: 32×32, `--radius-md`, transparent background. Active: `--accent-default` fill, `--on-accent` text. Hover: `--surface-sunken` background.
- **Prev/Next chevrons**: same size, label hidden visually (icon only, aria-label "Previous page" / "Next page"). Disabled at first/last page (38% opacity, no hover).
- **Ellipsis**: `…` 32×32, non-interactive, `--text-tertiary`.
- **Optional trailing widgets**: "Page 6 of 42" label · per-page select · jump-to-page input.

## Window logic
The visible buttons are always: `[‹] [1] [...] [P-1] [P] [P+1] [...] [N] [›]`.
- Always show first and last page.
- Show ±1 around current page.
- Replace the gap with `…` when not adjacent.
- Edge cases: when current page is within 3 of the start, expand the leading window (`[1][2][3][4][...][N]`); same for end.
- For ≤7 pages, show all pages, no ellipses.

## Variants
- **Numbered** (default).
- **Compact**: just `‹ Page 6 of 42 ›` — for narrow widths.
- **Cursor-based**: `‹ Newer · Older ›` — when total count isn't known. No numbered buttons.

## States
- **Default**.
- **Hover**: `--surface-sunken` background. Cursor pointer.
- **Focus-visible**: 2px `--border-focus` ring, 2px offset.
- **Active** (current page): solid `--accent-default` fill, text `--on-accent`. Not interactive (`aria-disabled` rather than disabled — keeps it focusable for orientation).
- **Disabled** (prev at p1 / next at pN): chevron at 38% opacity, no pointer events.
- **Loading**: prev/next chevrons replaced by a 16px spinner; numbered buttons stay clickable. Only the just-clicked button shows a brief halo.

## Per-page selector
Optional segmented control or select trailing the pagination: `Show: 25 · 50 · 100`. Changing it resets to page 1, never preserves position.

## Jump-to input
Optional small text field `Page __` with width 56px. Submitting via Enter navigates. Invalid values revert on blur with a 200ms shake animation (translate ±4px, 3 cycles).

## Motion
- Page change: instant for numbered button click. The page content below should crossfade with a 120ms `--ease-standard` (handled by the consuming view, not pagination itself).
- Active-page move: snap. Don't animate the active fill across buttons.

## Accessibility
- `<nav role="navigation" aria-label="Pagination">` wrapping the whole control.
- Each page button: `<a>` (real link if state in URL) with `aria-current="page"` on active.
- Live region: "Page 6 of 42" — announce on change, debounced 250ms.
- Min hit target 44×44 even when visual button is 32×32 (pad).

## Tokens
| Property | Token |
|---|---|
| Active fill | `--accent-default` |
| Active text | `--on-accent` |
| Hover bg | `--surface-sunken` |
| Ellipsis text | `--text-tertiary` |
| Disabled opacity | 38% |

## Don'ts
- Don't use pagination for infinite feeds (social, search-as-you-scroll). Use cursor or infinite-scroll with a "back to top".
- Don't change page count when filters change unless you're sure — invalidates user mental map.
- Don't put pagination at the top **and** bottom on short lists (< 1.5 viewports). Bottom only is enough.
- Don't show the per-page selector with fewer than 2 plausible values.
