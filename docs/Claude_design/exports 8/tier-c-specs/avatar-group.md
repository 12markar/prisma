# Avatar Group

A horizontal stack of overlapping avatars representing a set of people on something — collaborators, attendees, attribution. Compresses into a "+N" chip when the set exceeds the visible cap.

## Anatomy
```
( A )( B )( C )( D )(+12)
```
- **Avatar**: circle, default 32px diameter (also 24, 40, 48). 1.5px border `--surface-base` (creates the "punched out of background" look on overlap).
- **Overlap**: each avatar is offset to the left by **30%** of its diameter (e.g. 32px avatar = -10px margin). Last avatar has 0 offset.
- **+N chip**: same dimensions as avatars. `--surface-sunken` fill, `--text-secondary` text, `--font-sans` 12px weight 600 (size scales: 11/12/14/16 for the four sizes), tabular-nums.

## Stacking order
Visually, the **rightmost avatar is on top**. CSS-wise this means:
- Either reverse the source order and don't set z-index, OR
- Set explicit z-index per avatar where the right-most has the highest.

The +N chip should always be rightmost regardless of order.

## Cap & overflow
- Configurable `max` — default 4. After `max - 1` real avatars, show a +N chip representing the remainder. Examples: `max=4`, total=12 → 3 avatars + "+9". `max=4`, total=4 → 4 avatars, no chip.
- Edge case: when total = max + 1, you can either show all avatars (no chip) or hide one with "+1". Choose **show all** unless space is at a premium — a "+1" chip is wasteful.

## Hover & interaction
- **Hover an avatar**: lift 2px (translate-y -2px), shadow `--shadow-2`. Tooltip shows full name. Other avatars in the stack stay put.
- **Click**: opens a popover listing everyone (full names, optional roles, links). The click target is the +N chip OR the entire stack (configurable).
- **Hover +N**: shows tooltip with the next 3 hidden names + "and N more".

## Variants
- **Plain** (default): just circles.
- **With status dot**: a 8px (relative to 32px avatar = 25%) status dot in the bottom-right of each avatar, ringed in `--surface-base`. Use for online/offline/away.
- **Square**: `--radius-md` instead of circles. For non-people (e.g. apps in an OAuth grant). Don't mix with circle in the same stack.

## Initials & fallback
When an image isn't available:
- Show initials: 1 letter for size 24, 2 letters for size 32+.
- Background color: deterministic from the user's identity (hash → hue). Fixed saturation/lightness from a curated set of 8 hues so no palette feels off-brand. Text on the background uses `--on-accent` or `--on-neutral` based on luminance.

## Motion
- Avatar load (image arriving): 140ms fade from initials to image. Avoids jarring flip.
- Hover lift: 120ms `--ease-standard`.
- Group expand (popover): 200ms standard popover motion.

## Accessibility
- `role="group"` with `aria-label="N people working on this"`.
- Each avatar has `role="img"` with `aria-label="<name>"` (also on the <img alt>).
- The +N chip is a real button with `aria-label="Show all N people"` and `aria-haspopup="dialog"`.
- The tooltip on hover IS the accessible name when no labeled text near the avatar — so don't rely on the tooltip alone for users on screen readers.

## Sizes
| Size | Diameter | Border | Initial font |
|---|---|---|---|
| xs | 24px | 1px | 10px |
| sm | 32px | 1.5px | 12px |
| md | 40px | 2px | 14px |
| lg | 48px | 2px | 16px |

## Tokens
| Property | Token |
|---|---|
| Border | `--surface-base` |
| +N bg | `--surface-sunken` |
| +N text | `--text-secondary` |
| Hover shadow | `--shadow-2` |
| Status online | `--success-default` |
| Status away | `--warning-default` |

## Don'ts
- Don't use avatar groups for >20 people without a "see all" link. The visual ceases to communicate identity past that point.
- Don't put an avatar group inside a row that the avatar tooltip would overflow. Test on narrow viewports.
- Don't animate the stacking on initial mount. Snap to position; only animate on add/remove.
- Don't mix avatar shapes (circle + square) in the same group.
