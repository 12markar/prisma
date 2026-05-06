# Prisma — Onboarding Hero Imagery

## Concept
First-launch screens. Three hero compositions for a paged onboarding flow ("welcome → core idea → invite to start"). Same illustration vocabulary as the empty-states (geometric outline + violet→magenta wash), but **larger scale, more atmospheric, more layered**.

## Files
| File | Slot | Headline |
|---|---|---|
| `hero-welcome.svg` | Welcome / first launch | "Catalogue at rest." |
| `hero-organize.svg` | Core value: organisation | "A place for every thing." |
| `hero-invite.svg` | Final / call-to-action | "Bring the rest of the team." |

## Format
- 360×360 viewBox (hits comfortably on iPhone SE through iPad).
- Compositions are vertically centered with breathing room — pair with a headline + body + primary CTA below.
- Self-contained SVG. No external fonts, no rasters. Tints with text color where geometry uses `currentColor`.

## Composition rules
1. **One focal subject.** A single object the eye lands on (a stack, a prism, a constellation).
2. **Three to five floating ornaments.** Tiny dashed shapes, dots, or sparks — placed to balance the composition, never to decorate.
3. **A single gradient wash.** Violet → magenta, 14–22% opacity, applied to the focal subject only.
4. **Optional grid floor.** Dashed perspective lines that disappear into the bottom edge — anchors the subject without over-explaining the space.

## Pairing template
```
[ Hero illustration · 280px ]
"Catalogue at rest."          <- display.lg
"Prisma keeps your library    <- body
 organized — quietly, on every
 device you own."
[ Get started → ]             <- btn-primary
                              <- secondary action below: "I already have an account"
```
