# Prisma — Native Design System + Catalogue Apps

[![Site](https://img.shields.io/badge/site-ksharma--xyz.github.io%2Fprisma-c66524)](https://ksharma-xyz.github.io/prisma/)

A personal, world-class native design system + two catalogue apps:

- **Android** — Jetpack Compose, Kotlin 2.0, compileSdk 36, adaptive list-detail
- **iOS** — SwiftUI, iOS 17+, NavigationSplitView

Tokens are defined once in W3C DTCG JSON. Style Dictionary generates `PrismaTokens.kt` and `PrismaTokens.swift` from a single source — change a token, both apps update.

---

## Quick start

### Prerequisites
- **Node.js 20+** + **npm** (for the token build pipeline)
- **JDK 17** + **Android SDK** (Android Studio installs both)
- **Xcode 16+** + **`xcodegen`** (`brew install xcodegen`)

### One-time setup after fresh clone

```bash
cd design-system
npm install
npm run build-tokens         # regenerates PrismaTokens.kt + PrismaTokens.swift
```

### Run Android

```bash
cd android-catalogue
./gradlew :catalogue:installDebug    # plug phone in, USB debugging on
# Or open `android-catalogue/` in Android Studio → hit Run
```

### Run iOS

```bash
cd ios-catalogue
xcodegen generate            # generates Prisma.xcodeproj (gitignored)
open Prisma.xcodeproj        # then ⌘R in Xcode picks a simulator
```

For a real device, set Signing & Capabilities → Team in Xcode (free Apple ID works).

---

## What you'll see

- Sidebar with **search** and **collapsible sections** (Foundations, Inputs, Feedback, Navigation, Data display)
- **Adaptive layout**: phone → push navigation; tablet → list + detail side by side
- **Foundation showcases** built and ready to browse: Typography specimen, Color grid (primitive + semantic, light + dark), Spacing visualiser, Radius tiles, Elevation cards, interactive Motion playback
- **Theme** follows system; explicit toggle persists via `rememberSaveable` (Android) / `@AppStorage` (iOS); selection + sidebar state preserved via `@SceneStorage` (iOS) / `rememberSaveable` (Android)
- Component-detail pages for the remaining 25 components are **placeholders pending Phase 2+** implementations

---

## Repo layout

```
Prisma/
├── design-system/                  Source of truth
│   ├── tokens/                     W3C DTCG JSON (color, typography, spacing,
│   │                               radius, elevation, motion)
│   ├── fonts/                      Drop .ttf/.otf here; copy-fonts moves them
│   ├── component-specs/            25 component contracts (anatomy, states, a11y)
│   ├── site/                       Deployable web showcase (GitHub Pages)
│   ├── scripts/                    Style Dictionary build, contrast, lint, fonts
│   └── package.json
│
├── android-catalogue/              Native Android (Gradle multi-module)
│   ├── core-ui/                    Theme, fonts, generated tokens
│   ├── components/                 Component implementations (per phase)
│   └── catalogue/                  App: Nav3 + ListDetailPaneScaffold shell
│
├── ios-catalogue/                  Native iOS (SPM + Xcode app via xcodegen)
│   ├── Sources/CoreUI/             Theme, fonts, generated tokens
│   ├── Sources/Components/         Component implementations (per phase)
│   ├── App/                        App: NavigationSplitView shell
│   └── project.yml                 xcodegen config
│
├── references/                     Cloned for grep (gitignored): cvs-health iOS
│                                   a11y techniques, kiwicom orbit-compose
│
└── docs/
    ├── ARCHITECTURE.md             Distribution, navigation, testing, world-class
    │                               commitments, upgrade path
    └── TODO.md                     Phase-by-phase roadmap with status
```

---

## How tokens flow

1. Edit JSON under `design-system/tokens/`
2. Run `npm run build-tokens` — or let Gradle (`:core-ui:preBuild`) / Xcode (Run Script Build Phase) trigger it for you on every app build
3. Both apps recompile against the regenerated `PrismaTokens.{kt,swift}`

Generated files are **committed** to the repo as a snapshot. The pre-build hooks soft-fail if `npm` isn't on PATH so contributors without Node can still open and build the apps.

See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for the full rationale (monorepo vs package-registry, navigation choice, snapshot test plan, world-class commitments) and [`docs/TODO.md`](docs/TODO.md) for phase-by-phase status.

---

## Build pipeline scripts (in `design-system/`)

| Command | What it does |
|---|---|
| `npm run build-tokens` | Style Dictionary → `PrismaTokens.kt` + `PrismaTokens.swift` + `tokens.css` |
| `npm run lint-tokens` | Validate DTCG schema + reference resolution |
| `npm run check-contrast` | Verify every text/surface pair meets WCAG AA |
| `npm run copy-fonts` | Copy `fonts/*.ttf` → both apps' resource folders |

---

## Continuous integration

GitHub Actions workflows:

- `tokens-check` — DTCG lint + contrast + generated-files-in-sync gate
- `lint` — Detekt + slack/compose-lints (Android), SwiftLint (iOS)
- `android-ci` — `./gradlew assembleDebug` (Paparazzi snapshot tests in Phase 1.x)
- `ios-ci` — `swift build` + `xcodebuild build` (swift-snapshot-testing in Phase 1.x)
- `deploy-site` — publishes `design-system/site/` to GitHub Pages on push to `main`

Pages live at https://ksharma-xyz.github.io/prisma/.
