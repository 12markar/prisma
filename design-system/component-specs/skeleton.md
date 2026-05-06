# Skeleton

Content-shaped placeholders shown while real data loads. Distinct from `loading.md` indicators — skeletons preserve layout so the page does not jump when data arrives.

## Anatomy

```
┌──────────────────┐    ▓▓▓▓▓▓▓▓▓▓▓▓▓
│ ▓▓▓▓▓ ▓▓▓▓▓▓▓▓▓ │    ▓▓▓▓▓▓▓▓▓▓▓▓▓
│ ▓▓▓▓▓▓▓▓ ▓▓▓▓▓  │    ▓▓▓▓▓▓▓▓
│ ▓▓▓▓▓▓▓▓▓▓▓▓▓   │
└──────────────────┘
```

Skeletons are composed of three primitive shapes:

- **Block** — rectangular fill with optional `radius.sm`/`md`. Use for cards, images, hero areas.
- **Line** — pill-shaped fill, height matching the eventual text token (e.g. `body.md` line-height). Use for text rows. Render variable widths (60%–95%) across rows to feel natural.
- **Circle** — for avatars and icons. Sized to match the real element it replaces.

## Variants

### Static (reduced-motion)

Solid fill at `surface.sunken` (or `border.subtle` on top of `surface.sunken`). No animation. **Default for `prefers-reduced-motion: reduce`.**

### Shimmer (default)

Diagonal gradient sweep — `surface.sunken` → `border.subtle` → `surface.sunken` — traversing left-to-right over 1.5s, looping with a 200ms hold at the end of each cycle. Easing `motion.easing.standard`.

## Sizes & spacing

- Match the eventual element's bounds exactly. Skeleton text rows use the same `lineHeight` as their target typography token.
- Stack with the same gap as the final layout (typically `spacing.3` between rows).
- Always render at least **2 rows** for text — a single row reads as a real loaded element.

## States

- Visible while data is loading.
- **Crossfade out** over `motion.duration.fast` when data arrives, so the eye does not snap.

## Tokens used

- `surface.sunken` — base fill
- `border.subtle` — shimmer highlight band
- `radius.sm` / `radius.md` / `radius.full` — match eventual element shape
- `motion.duration.fast` — exit crossfade
- `motion.easing.standard` — shimmer easing

## Accessibility

- Skeletons are decorative. Wrap the loading region in `aria-busy="true"` and apply `aria-hidden="true"` to the skeletons themselves so screen readers do not announce empty rectangles.
- Compose: `Modifier.semantics { invisibleToUser() }` plus `Modifier.semantics { liveRegion = LiveRegionMode.Polite }` on the parent so completion can be announced.
- SwiftUI: `.accessibilityHidden(true)` on each skeleton; `.accessibilityElement(children: .combine)` with `accessibilityLabel("Loading")` on the parent.
- **Reduced motion:** drop the shimmer to a static fill (`surface.sunken`). Do not pulse opacity either — that's still motion.

## Do / Don't

✅ **Do** make skeletons match the final layout's footprint exactly so the page does not reflow.
✅ **Do** use skeletons for content surfaces (cards, list items, paragraphs); spinner for actions (button loading).
✅ **Do** delay skeleton appearance by 100ms — flashing skeletons for fast loads is jarring.

❌ **Don't** show a single skeleton row alone — it reads as broken UI.
❌ **Don't** mix skeleton blocks with real content in the same row — choose one state per region.
❌ **Don't** animate the shimmer faster than 1.2s — anything quicker reads as a strobe.
