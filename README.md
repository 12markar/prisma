# Prisma — Native Design System + Catalogue Apps

A personal design system that powers two native catalogue apps:

- **Android** — Jetpack Compose, latest SDK
- **iOS** — SwiftUI, latest target

Tokens are defined once in the design system (W3C DTCG JSON), and Style Dictionary generates platform-specific source files (`Tokens.kt`, `Tokens.swift`) that each app compiles against.

## Repo layout

```
Prisma/
├── design-system/         Source of truth — tokens, fonts, component specs
│   ├── tokens/            W3C DTCG JSON (color, typography, spacing, etc.)
│   ├── fonts/             Custom font files (.ttf / .otf)
│   ├── component-specs/   Per-component markdown (anatomy, states, a11y)
│   ├── style-dictionary.config.js
│   └── package.json       `npm run build-tokens`
│
├── android-catalogue/     Native Android app — Compose + Showkase catalogue
├── ios-catalogue/         Native iOS app — SwiftUI catalogue
└── docs/
    └── ARCHITECTURE.md    Why monorepo today, package-registry path tomorrow
```

## How tokens flow

1. Edit a JSON file under `design-system/tokens/`
2. Run `npm run build-tokens` (or let Gradle / Xcode build phases run it for you)
3. Style Dictionary regenerates `Tokens.kt` (Android) and `Tokens.swift` (iOS) into each app's source tree
4. Both apps recompile and pick up the new values

See `docs/ARCHITECTURE.md` for the full architectural rationale and the upgrade path to package-registry distribution.

## Status

| Stage | State |
|---|---|
| Architecture decided (monorepo, Style Dictionary) | Done |
| Design system content (tokens, fonts, specs) | In progress (Claude Design) |
| Style Dictionary wiring | Pending tokens |
| Android catalogue app | Pending tokens |
| iOS catalogue app | Pending tokens |
