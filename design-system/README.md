# Prisma Design System

A cross-platform design system foundation for native catalogue apps on Android (Jetpack Compose) and iOS (SwiftUI). Tokens are authored in [W3C DTCG](https://tr.designtokens.org/format/) JSON and fed through Style Dictionary to generate `Tokens.kt` and `Tokens.swift`.

---

## Design philosophy

**Technical-precise with editorial warmth.**

Most design systems collapse into one of two failure modes: the cold, generic "AI dashboard" (rounded rectangles, blurple accent, vibe-less neutrals) or the over-decorated brand showcase that fights the content. Prisma takes a third path — the visual register of a well-set technical journal. Confident type, generous whitespace, a restrained warm-neutral palette, and a single saturated accent that does real work.

Three principles drive every decision:

1. **Typography carries the design.** A strong type scale with deliberate weight/size pairings does most of the visual lifting. Color is used sparingly to mark state and intent, not to decorate.
2. **Surfaces have a temperature.** Backgrounds are warm-neutral (off-white in light, deep ink in dark) — never pure `#FFFFFF` or `#000000`. This reads as considered and is easier on the eye over long sessions.
3. **Idiomatic, not opinionated.** Tokens are designed to feel native on both platforms. Elevation uses iOS-style soft shadows in light mode and subtle glows + borders in dark mode (per HIG conventions). Spacing aligns to a 4pt grid that maps cleanly to both Compose `dp` and SwiftUI points.

---

## Font choices

| Role | Family | Why |
|---|---|---|
| Display & UI | **Instrument Sans** | A confident geometric sans with subtle editorial quirks (angled terminals on `t`, `f`, the elegant italic). Reads as both technical and warm. Free, Google Fonts. |
| Code & technical | **JetBrains Mono** | The clearest mono available — distinctive `0`, `l`, `1` glyphs, optional ligatures, six weights. Made for code; generous x-height keeps it legible at small sizes. |

The pairing works because both faces share a **squared geometric construction** but diverge in proportion — Instrument Sans is humanist-leaning, JetBrains Mono is engineered. The contrast is intentional: prose vs. data.

URLs: see [`fonts.md`](./fonts.md).

---

## Color system rationale

A two-layer system:

### Layer 1 — Primitives (`color.primitive.*`)

Raw scales never referenced directly by component code. Six ramps × 11 stops (50, 100, 200, …, 900, 950):

- `neutral` — warm-toned grayscale, the workhorse. Hue ~30° (slight bias toward earth tones).
- `accent` — saturated copper-orange (`oklch(67% 0.16 50)`). One accent only; used for primary actions, focus rings, selection.
- `success` / `warning` / `danger` / `info` — restrained semantic ramps. None are pure red/green/blue/yellow; each is desaturated and tonally aligned with the neutral ramp.

### Layer 2 — Semantic aliases (`color.semantic.*`)

What component code consumes. Every alias defines **both light and dark** values:

```
surface.{base, raised, sunken, overlay, inverse}
text.{primary, secondary, tertiary, disabled, onAccent, onInverse, link}
border.{subtle, default, strong, focus}
accent.{default, hover, pressed, subtle, onAccent}
status.{success, warning, danger, info}.{default, subtle, onStatus}
```

**Contrast:** every text-on-surface pairing meets **WCAG AA (4.5:1 for body, 3:1 for large text)**. AAA met for `text.primary` on `surface.base` in both modes. Verified pairings are documented in `tokens/color.json` comments and shown live on the showcase page.

---

## How to consume the tokens

1. Install [Style Dictionary](https://styledictionary.com/) v4+.
2. Point the source globs at `tokens/*.json`.
3. Use the platform configs:
   - **Compose** — output `compose/object` format → `Tokens.kt`
   - **SwiftUI** — output `ios-swift/class.swift` format → `Tokens.swift`
4. Components import from generated files — never hardcode values.

A minimal Style Dictionary config is included in `tokens/sd.config.json`.

---

## Folder structure

```
design-system-output/
├── tokens/                 # W3C DTCG token files
├── component-specs/        # Per-component implementation contracts
├── web-showcase/           # Interactive reference page (open index.html)
├── fonts.md
└── README.md
```

---

## Accessibility

- All text/surface pairs meet WCAG AA minimum.
- Focus rings use `border.focus` (3:1 contrast against any adjacent surface).
- Min touch target: **44×44pt** (iOS HIG) / **48×48dp** (Material). Components enforce the larger of the two.
- State changes (loading, error, success) are announced via platform-native a11y APIs — see component specs.
- Motion respects `prefers-reduced-motion` / `UIAccessibilityIsReduceMotionEnabled` / `Settings.Global.ANIMATOR_DURATION_SCALE`.
