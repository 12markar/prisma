# Architecture

This document records the architectural decisions for Prisma — a native design system powering Jetpack Compose (Android) and SwiftUI (iOS) catalogue apps.

---

## 1. Distribution model — Option A (monorepo) for now, Option C (package registries) as the upgrade path

### What we chose: Option A — monorepo with generated source files

All three projects (`design-system/`, `android-catalogue/`, `ios-catalogue/`) live in a single git repo. Style Dictionary writes generated `Tokens.kt` and `Tokens.swift` files directly into each app's source tree. The generated files are committed alongside the JSON tokens.

```
Prisma/                                  (single git repo)
├── design-system/
│   ├── tokens/                           SOURCE OF TRUTH (W3C DTCG JSON)
│   ├── fonts/
│   ├── component-specs/
│   └── style-dictionary.config.js        Knows where to write outputs
│
├── android-catalogue/
│   └── core-ui/src/main/java/xyz/ksharma/prisma/tokens/
│       ├── Colors.kt                     GENERATED, COMMITTED
│       ├── Typography.kt
│       └── ...
│
└── ios-catalogue/
    └── CoreUI/Sources/CoreUI/Tokens/
        ├── Colors.swift                  GENERATED, COMMITTED
        └── ...
```

**Workflow:**
1. Edit JSON in `design-system/tokens/`
2. `npm run build-tokens` regenerates platform sources (also auto-triggered by Gradle pre-build hook on Android and Run Script build phase on iOS)
3. Both apps recompile and pick up new values
4. Commit captures JSON + generated files atomically

**Why this for now:**
- Solo development — no team coordination problem to solve
- Maximum iteration speed — token edit → rebuild loop is one command
- Atomic PRs — token change and platform impact visible together
- Zero infrastructure setup — no Maven Central, no GPG keys, no SPM registry

### What we deferred: Option C — separate repos + published packages

The "production-grade" model used by Spotify Encore, Airbnb DLS, Shopify Polaris.

```
prisma (own repo)                  prisma-android (own repo)
       │                                    │
       │ push to main                       │ build.gradle.kts:
       ▼                                    │   implementation("xyz.ksharma.prisma:tokens:1.2.0")
┌──────────────────────┐                    ▼
│ GitHub Actions:      │      ┌──────────────────────┐
│ 1. style-dictionary  │─────▶│  Maven Central       │
│ 2. semver bump       │      │  (or GH Packages)    │
│ 3. publish artifacts │      └──────────────────────┘
│ 4. tag git release   │                    ▲
└──────────────────────┘                    │
       │                       prisma-ios (own repo)
       ▼                                    │ Package.swift:
┌──────────────────────┐                    │   .package(url: "...", from: "1.2.0")
│  GitHub Release tag  │
│  (SPM consumes)      │
└──────────────────────┘
```

### When to migrate from A to C

Triggers that justify the upgrade:
- The design system needs to be consumed by **more than two apps**
- **Other people** (teammates, OSS users) want to install it
- You want **versioned releases** with changelogs and explicit upgrade ceremony
- The catalogue apps and design system want **independent release cadences**

### How to migrate from A to C (the upgrade plan)

The token JSON format does not change. Only distribution changes. Steps:

1. Extract `design-system/` into its own repo (`prisma`). Carry the git history with `git filter-repo --subdirectory-filter design-system`.
2. Add a CI workflow to `prisma`:
   - On push to `main`, run `npm run build-tokens`
   - Use `semantic-release` (or `changesets`) to compute the next version from commit messages
   - **Android publish step:** `./gradlew publishToMavenCentral` (requires Sonatype account + GPG key) or `publishToGitHubPackages` (simpler, scoped to your account)
   - **iOS publish step:** create a git tag `v1.2.0` — SPM resolves tags directly, no registry needed
3. Extract `android-catalogue/` into its own repo. Replace path-based dependency with:
   ```kotlin
   implementation("xyz.ksharma.prisma:design-tokens:1.2.0")
   ```
4. Extract `ios-catalogue/` into its own repo. Replace path-based dependency with:
   ```swift
   .package(url: "https://github.com/<user>/prisma-swift", from: "1.2.0")
   ```
5. **Preserve fast dev loop** during local development:
   - Android: publish to `mavenLocal()` and have `build.gradle` resolve from there first when a `LOCAL_DS=true` flag is set
   - iOS: use `.package(path: "../prisma")` for local override during dev, swap to URL for releases
6. Wire up Renovate or Dependabot on the app repos to get auto-PRs when new design system versions ship.

This migration is intentionally non-disruptive: the tokens, generated outputs, fonts, and component specs all remain identical. Only the distribution mechanism changes.

---

## 2. What is shared vs. what is platform-native

| Artefact | Shared? | Where |
|---|---|---|
| Token JSON (color, typography, spacing, radius, elevation, motion) | ✅ Single source of truth | `design-system/tokens/` |
| Custom font files (.ttf / .otf) | ✅ Same files copied to both apps | `design-system/fonts/` → copied by build script |
| Component specs (anatomy, states, a11y rules) | ✅ Markdown, read by humans | `design-system/component-specs/` |
| Generated `Tokens.kt` | ✅ Generated from JSON | `android-catalogue/core-ui/src/main/java/xyz/ksharma/prisma/tokens/` |
| Generated `Tokens.swift` | ✅ Generated from JSON | `ios-catalogue/CoreUI/Sources/CoreUI/Tokens/` |
| Component implementation code | ❌ NOT shared | Each platform has its own idiomatic Compose / SwiftUI implementation |
| Layout / navigation / app shell | ❌ NOT shared | Each app builds its own catalogue UI |

**Key principle:** share **tokens and specs**, do not share **component code**. Sharing component code across native iOS / Android is what KMP / Compose Multiplatform tries to solve — that is a different architecture with different tradeoffs and is explicitly out of scope here.

---

## 3. Token pipeline — Style Dictionary v4

Style Dictionary v4 is used because it has native W3C DTCG support, a clean v4 transforms API, and is the de-facto industry standard.

### Output transforms

Custom transforms generate idiomatic platform code:

**Android (Compose):**
- Colors → `androidx.compose.ui.graphics.Color(0xFF...)` constants
- Typography → `androidx.compose.ui.text.TextStyle(...)` declarations
- Spacing → `androidx.compose.ui.unit.dp` extension values
- Light/dark resolved at runtime via `@Composable` color provider that reads `MaterialTheme` `isLight`

**iOS (SwiftUI):**
- Colors → `Color(light:dark:)` extension that picks based on `UITraitCollection.userInterfaceStyle`
- Typography → `Font.custom(name, size:).weight(...)` constants
- Spacing → `CGFloat` constants
- Dynamic Type respected via `.font(.body)` + custom modifier that scales via UIFontMetrics

### Build-time integration

- **Android**: a Gradle task in `:core-ui/build.gradle.kts` runs `npm run build-tokens` and is wired as a `dependsOn` for `compileKotlin`. Edit a token, hit Run, tokens regenerate.
- **iOS**: a Run Script build phase in the Xcode app target runs `npm run build-tokens`. Same flow.
- **Local dev safety**: Style Dictionary checks if outputs would change and skips writing if not (avoids unnecessary recompilation cascades).

---

## 4. Android catalogue stack

| Concern | Choice |
|---|---|
| Language | Kotlin 2.0+ |
| Build | AGP 8.6+, Gradle 8.x, KSP for annotation processors |
| compileSdk | 36 (Android 16) |
| minSdk | 26 (Android 8.0) |
| UI framework | Jetpack Compose (BOM, latest stable) |
| Navigation | **Jetpack Navigation 3** (`androidx.navigation3`) — back stack as explicit state |
| Adaptive layouts | **`androidx.compose.material3.adaptive`** — `WindowSizeClass` + `ListDetailPaneScaffold` for tablet/foldable two-pane UI |
| Module structure | `:core-ui` (tokens, theme, fonts), `:components`, `:catalogue` (app) |
| Catalogue browser | airbnb/Showkase (annotation-driven, auto-browses `@ShowkaseComposable` previews) |
| Bottom sheet / modal / tabs | composables/compose-unstyled (per OSS analysis md — superior to M3) |
| Skeleton | valentinilk/compose-shimmer |
| Static analysis | **Detekt** + **slack/compose-lints** + Android Lint (see Section 5c) |
| Collection types in composables | **`kotlinx.collections.immutable`** — `ImmutableList<T>` everywhere a list crosses a `@Composable` boundary (compose-lints `ComposeUnstableCollections` rule enforces) |
| Snapshot tests | Square Paparazzi (renders Compose without emulator) |
| Behavior tests | androidx.compose.ui:ui-test-junit4 |

---

## 5. iOS catalogue stack

| Concern | Choice |
|---|---|
| Language | Swift 5.10+ |
| IDE | Xcode 16+ |
| Deployment target | iOS 17.0+ |
| UI framework | SwiftUI (no UIKit bridging where avoidable) |
| Package structure | SPM package with `CoreUI` and `Components` targets, plus a separate Xcode app project (`CatalogueApp`) depending on the package |
| Navigation | `NavigationStack` (compact) + **`NavigationSplitView`** (regular size class) |
| Adaptive layouts | `NavigationSplitView` auto-collapses on iPhone, expands on iPad. `@Environment(\.horizontalSizeClass)` for explicit overrides where needed. |
| Catalogue browser | Custom `NavigationSplitView`-based browser (no Showkase equivalent for SwiftUI) |
| Icons | SFSafeSymbols (type-safe SF Symbols) |
| Loading | exyte/ActivityIndicatorView |
| Skeleton | CSolanaM/SkeletonUI |
| Toast | elai950/AlertToast |
| Bottom sheet | Native `.presentationDetents` (iOS 17+) — no backport needed at this deployment target |
| Selective utilities | SwiftUIX (only where missing primitives are unavoidable) |
| Static analysis | **SwiftLint** + **SwiftFormat** (see Section 5c) |
| Snapshot tests | pointfreeco/swift-snapshot-testing |
| Behavior tests | XCTest |
| A11y tests | XCUITest assertions on accessibility traits |

---

## 5a. Navigation & adaptive layouts (phone vs tablet)

The catalogue is a list-detail experience. The same content is laid out differently depending on form factor:

| Form factor | Layout |
|---|---|
| **Phone** (Compact horizontal size class) | Single-pane. List of components → tap pushes to detail. Standard back stack. |
| **Tablet / foldable** (Medium / Expanded) | Two-pane. Persistent left pane = grouped component list (Foundations, Inputs, Feedback, Navigation, Data Display). Right pane = selected component's detail (live demo, variants, tokens used, a11y notes, code snippet). |

### Android — Navigation 3 + Material 3 Adaptive

- **Jetpack Navigation 3** (`androidx.navigation3`) for the back-stack and routing. We use it instead of Nav2 because it models the back stack as plain state, which composes cleanly with adaptive layouts (the back stack diverges between compact/expanded — Nav3 makes that natural).
- **`androidx.compose.material3.adaptive`** for the responsive shell. Specifically:
  - `currentWindowAdaptiveInfo()` to read window size class
  - `ListDetailPaneScaffold` — the official adaptive primitive that switches between single-pane and two-pane based on width
  - `NavigableListDetailPaneScaffold` to integrate with Nav3's back stack so back-press behaviour is correct in both layouts
- Showkase's auto-generated browser is wrapped inside this adaptive shell — Showkase provides the *content*, our shell provides the *layout*.

### iOS — NavigationSplitView

- Native SwiftUI `NavigationSplitView` is built for exactly this pattern. iPhone collapses it to push-style automatically; iPad shows sidebar + detail.
- Three-column form (`sidebar / content / detail`) is overkill for a catalogue — we use the two-column form (`sidebar / detail`) where the sidebar is the grouped component list.
- `@Environment(\.horizontalSizeClass)` is read for the (rare) cases we want to deviate from default adaptive behaviour.
- No platform-specific code branching — the same `NavigationSplitView` body adapts on its own.

### Snapshot test implications

The snapshot matrix from Section 6 is extended for screens where the adaptive layout matters (the catalogue shell, list-detail screens):
- Compact (phone width: 360 dp / 390 pt)
- Expanded (tablet width: 800 dp / 1024 pt)

Per-component snapshots (Button, TextField, etc.) stay form-factor-agnostic — those are component-level, not layout-level.

---

## 5b. Catalogue browser UX

The catalogue is more than a list of components — it is a showcase. Two distinct kinds of pages live in the detail pane:

### 5b.1 Sidebar (left pane)

```
┌─────────────────────────────┐
│  🔍 Search components...    │  ← real-time filter
├─────────────────────────────┤
│  ▼ FOUNDATIONS              │  ← collapsible
│      Typography             │
│      Colors                 │
│      Icons                  │
│      Spacing                │
│      Elevation              │
│      Motion                 │
│      Radius                 │
│                             │
│  ▼ INPUTS                   │  ← collapsible
│      Button                 │
│      TextField              │
│      Checkbox               │
│      Radio                  │
│      Switch                 │
│      Slider                 │
│                             │
│  ▶ FEEDBACK                 │  ← collapsed
│  ▶ NAVIGATION               │
│  ▶ DATA DISPLAY             │
└─────────────────────────────┘
```

- Search field at the top filters items as you type. Search matches both display name and tags (so typing "input" finds TextField, Checkbox, Switch).
- Section headers are tap-to-collapse. Default state: Foundations expanded, the rest collapsed (for quick orientation on first launch).
- Selected item is highlighted with `accent.default` token; on tablet, selection also drives the right pane.
- Single source of truth for the structure: a `CatalogueRegistry` data structure (one per platform) that lists every entry with its category, tags, and the screen builder. Adding a new component = adding one entry.

### 5b.2 Foundation showcase pages (the "beautifully designed" detail screens)

Each foundation gets a bespoke detail screen — these are not generic component detail screens. They demonstrate the design system's visual identity. The Claude Design web showcase output is the visual reference; we implement these faithfully on each platform.

| Foundation | Showcase design |
|---|---|
| **Typography** | Hero specimen — every type token rendered at full size in real sample text, with token name, font family, weight, size, line height, letter spacing displayed alongside. Display tokens at the top, scaling down to label/code at the bottom. |
| **Colors** | Two-column grid — primitive scale on the left (e.g., `blue.50`–`blue.900`), semantic tokens on the right (`surface.primary`, `text.onSurface`, `accent.default`, etc.). Each swatch shows token name, hex value, and contrast indicator. Light + dark mode tabs at the top. Tap to copy token name. |
| **Icons** | Searchable grid — every icon (SF Symbols on iOS, your icon set on Android) at uniform size with the icon's name underneath. Search bar filters the grid in real time. Tap to copy import code (`Image(systemSymbol: .heart)` / `Icon(DemoIcons.Heart)`). |
| **Spacing** | Visualizer — stacked horizontal bars showing each spacing token's actual width, labeled with token name + value. |
| **Elevation** | Cards demonstrating each elevation token side-by-side, light + dark mode versions side-by-side (dark mode often uses borders + glow, not drop shadows). |
| **Motion** | Interactive demo — tap to trigger an animation using each duration/easing token; a timeline below shows curve and duration. |
| **Radius** | Squares with each radius token applied, visually demonstrating the curvature scale. |

### 5b.3 Component detail pages (the standard layout)

Every component shares one layout, generated from the component spec:

```
┌──────────────────────────────────────────┐
│  Button                                  │  ← H1 with category breadcrumb
│  Inputs / Button                         │
├──────────────────────────────────────────┤
│  [ Live demo area ]                      │  ← interactive
│                                          │
│  Variants ─────────────────────────       │
│  Primary  Secondary  Outlined  Ghost     │  ← rendered side-by-side
│                                          │
│  States ───────────────────────────       │
│  Default  Pressed  Focused  Disabled     │
│  Loading                                  │
│                                          │
│  Tokens used ──────────────────────       │
│  • surface.accent.default                │
│  • text.onAccent                         │
│  • radius.md                             │
│  • typography.label.lg                   │
│                                          │
│  Accessibility ───────────────────       │
│  • Role: button                          │
│  • Min touch target: 48 dp / 44 pt       │
│  • State announcements: pressed,         │
│    disabled, loading                     │
│                                          │
│  Code ────────────────────────────       │
│  ```                                      │
│  PrimaryButton(                          │
│    text = "Click me",                    │
│    onClick = { ... }                     │
│  )                                       │
│  ```                                      │
└──────────────────────────────────────────┘
```

The "Tokens used" and "Accessibility" sections are read directly from the component spec markdown — no duplication of contract documentation.

### 5b.4 Implementation notes per platform

**Android:**
- Sidebar: `LazyColumn` with sticky headers and animated collapse via `AnimatedVisibility`
- Search: a `TextField` at the top backed by a `derivedStateOf` filter over the registry
- Selection state: hoisted to the adaptive scaffold's destination, so back-press and rotation preserve it
- Component registry: a `sealed class CatalogueEntry` enumerated at compile time + Showkase metadata for the auto-generated entries

**iOS:**
- Sidebar: `List` with `DisclosureGroup` for collapsible sections (built-in)
- Search: `.searchable(text:)` modifier on the sidebar — native, gets the iOS-standard look-and-feel for free
- Selection state: `@State` on the parent view with a binding into `NavigationSplitView`
- Component registry: a static array of `CatalogueEntry` structs

---

## 5c. Static analysis & code quality

Performance and correctness lints are non-negotiable. They are also the cheapest form of review available.

### Android

| Tool | What it catches |
|---|---|
| **Detekt** | Kotlin code smells, complexity, formatting, naming. Configured to mirror krail's setup if shared; otherwise sensible defaults (Slack-style rule set). |
| **slack/compose-lints** (Detekt plugin) | Compose-specific rules: `ComposeUnstableCollections` (use `ImmutableList`, not `List`), `ComposeModifierMissing`, `ComposeModifierReused`, `ComposeNaming`, `ComposeMutableParameters`, `ComposeViewModelInjection`, `ComposeMultipleContentEmitters`. |
| **Android Lint** | Resource issues, deprecations, accessibility lints (touch target size, content description requirements, contrast warnings). |
| **`kotlinx.collections.immutable`** | Runtime/API support for `ImmutableList` and `PersistentList` — every public component API that takes a list types it as `ImmutableList<T>`. |
| **ktlint** (via Detekt plugin) | Auto-formatting, import ordering, indentation. |

### iOS

| Tool | What it catches |
|---|---|
| **SwiftLint** | ~200+ rules: `force_unwrapping`, `force_try`, `large_tuple`, `cyclomatic_complexity`, `file_length`, `function_body_length`, `redundant_void_return`, `unused_closure_parameter`, etc. Configured to be strict. |
| **SwiftFormat** | Auto-formatting; runs on save and pre-commit. Complementary to SwiftLint. |
| Custom SwiftLint rules | SwiftUI-specific patterns: prefer `@Observable` over `ObservableObject` (iOS 17+), prefer typed `Color(light:dark:)` extension over `colorScheme` branching, ban `Image(systemName:)` in favour of `Image(systemSymbol:)` from SFSafeSymbols. |

### Cross-platform principles enforced

- **Collection stability** — Android: `ImmutableList<T>`. iOS: Swift's `Array<T>` (CoW value-type, equivalent guarantee for free).
- **No force operations** — Android: no `!!`. iOS: no `try!`, no `as!`, no `value!`.
- **Public-API surface discipline** — every public component has explicit access modifiers and a stable parameter contract typed against tokens, not raw colours/sizes.
- **Token-only styling** — lints reject hardcoded colours or sizes in component code (custom Detekt rule + custom SwiftLint regex rule).

### Where they run

- **Locally**: pre-commit hook (lint-staged-style), runs Detekt on changed `.kt` and SwiftLint on changed `.swift`. Fast.
- **CI**: dedicated `lint.yml` workflow on every PR — separate from snapshot-test workflows so a lint failure does not mask a snapshot-test failure or vice versa.

---

## 5d. World-class commitments

The bar for this catalogue and design system is "best in the world". This section makes the bar concrete and enforceable, not aspirational. Every item below is treated as a hard requirement, not a stretch goal.

### Visual & interaction craft

- **Choreographed entrance animations** — sidebar items reveal with a subtle stagger on first launch / route change. Honoured `prefersReducedMotion` / `accessibilityReduceMotion` (instant on, no motion).
- **Smooth theme transition** — toggling light/dark animates token colours via `crossfade` (Android) / `withAnimation` (iOS) over `motion.duration.default`. Never a hard flash.
- **Tactile feedback** — Android: `HapticFeedbackConstants` on toggles, button press, and selection. iOS: `UIImpactFeedbackGenerator` (light/medium per interaction class), `UISelectionFeedbackGenerator` for picker-style controls.
- **60/120 fps** — every screen scrolls and animates at native refresh rate. No jank. Verified with Android GPU profiler / Instruments Time Profiler.
- **Polished empty states** — search returns nothing → bespoke empty illustration (not a system label). All "no data" surfaces in the catalogue are designed.
- **Pressed/hover/focus states everywhere** — every interactive surface in the catalogue itself responds visually. Lint rule rejects `Modifier.clickable` without a corresponding indication.

### Catalogue features that elevate beyond a normal showcase

- **Interactive prop playground** — every component detail page includes a Storybook-style controls panel: change the button label, toggle disabled, select variant, and the live demo updates instantly. Implemented via a typed `ControlSpec` per component.
- **Command palette (⌘K / Ctrl+K)** — instant fuzzy-search jump to any component. Keyboard-only navigation. iPad/Android tablet keyboard support.
- **Live token-aware code snippets** — the snippet shown for each component reads from the actual token registry, so when a token name changes, the snippet auto-updates. No drift between displayed code and reality.
- **Accessibility overlay mode** — toggle in the catalogue chrome that visually overlays every component's accessibility tree (role, label, value, traits). Like browser inspect-element for a11y.
- **Inspector panel** — each component detail page can show a side panel with: rendered DOM/view hierarchy, applied tokens, computed contrast ratios for current state. Toggle on/off.
- **Theme editor (stretch)** — if scope allows, a hidden dev-mode panel lets you tweak token values at runtime to preview changes. Resets on app relaunch.

### Performance & quality non-negotiables

- **Cold start < 1.5 s** on a mid-range device (Pixel 7, iPhone 13).
- **Zero memory leaks** — verified with Android Studio Profiler (Compose recomposition counter) and Xcode Instruments (Leaks + Allocations).
- **Stable composables / equatable views** — every component is a stable composable (Compose) and has an explicit `Equatable` conformance where it carries non-trivial state.
- **A11y AAA where possible, AA mandatory** — every text-on-surface pair passes WCAG AA (4.5:1) with AAA (7:1) preferred for body copy. Catalogue includes a contrast badge on every color swatch making this visible.
- **Localisation-ready from day one** — every string in a resource file (Android `strings.xml`, iOS `.strings` / `.xcstrings`). RTL-safe layouts (`Modifier.padding(start = ...)` on Android, `.leading`/`.trailing` on iOS — never `left`/`right`).

### Documentation & shipping bar

- **Each component ships with a README** that includes: anatomy diagram, prop table, usage code, do/don't, a11y notes. Auto-linked from the catalogue detail page.
- **A "Getting Started" foundation page** in the catalogue itself — explains the design philosophy, font choices, color system rationale, when to use what.
- **Versioned changelog** even in monorepo mode — `CHANGELOG.md` updated per phase, ready to be consumed when we graduate to Option C.

---

## 6. Testing strategy

### 6.1 Snapshot regression tests

**Why:** they catch unintended visual changes (a token edit unexpectedly changes 14 components → CI shows 14 image diffs) and they catch a11y regressions (font-scale variants are part of the snapshot matrix).

**Snapshot matrix per component** (both platforms):
- **States**: default, pressed, focused, disabled, loading, error (where applicable)
- **Theme**: light + dark
- **Font scale / Dynamic Type**: 1.0× and 1.3× (catches a11y layout breakage)
- ≈ 8–12 PNGs per component. Committed to the repo as the visual baseline.

**Android — Paparazzi:**
- Lives in `:core-ui/src/test/` and `:components/src/test/`
- `@Test fun primaryButton_default_light()` style tests
- Reference PNGs in `src/test/snapshots/images/`
- CI runs `./gradlew verifyPaparazziDebug`

**iOS — swift-snapshot-testing:**
- Lives in `Tests/ComponentsTests/`
- `assertSnapshot(of: PrimaryButton(), as: .image(traits: .init(userInterfaceStyle: .dark)))`
- Reference PNGs in `__Snapshots__/`
- CI runs `swift test`

### 6.2 Behavior + a11y tests

- **Android**: `composeTestRule.onNodeWithContentDescription(...)`, `assertIsToggleable()`, `assertIsSelected()` per component
- **iOS**: XCUITest probes for `accessibilityLabel`, `accessibilityValue`, `.isToggle`, `.isHeader`, `.updatesFrequently` per component

### 6.3 Token-level CI checks

- **Generated-files-in-sync**: PR fails if tokens JSON changed but generated `Tokens.kt` / `Tokens.swift` don't reflect the change
- **WCAG AA contrast**: a script asserts 4.5:1 for body text and 3:1 for large text on every semantic colour pair (text-on-surface combinations); fails the build on violation
- **Token schema validation**: ensure DTCG `$type` and `$value` shapes are correct

### 6.4 Manual acceptance per PART 4 of the OSS analysis md

The accessibility checklist (touch target 44×44pt iOS / 48×48dp Android, state announcements, focus traps, etc.) is run manually with TalkBack / VoiceOver before any component is marked complete in the phased roadmap.

---

## 7. CI/CD (Option A — minimal, build + snapshot only)

GitHub Actions workflows under `.github/workflows/`. Scope: **build the app + run snapshot tests**. Behaviour and a11y test suites are out of CI scope for now (they exist locally and run manually as the matrix from the OSS analysis md PART 4).

| Workflow | Trigger | What it does |
|---|---|---|
| `tokens-check.yml` | PRs touching `design-system/**` | Runs `npm run build-tokens`, fails on uncommitted diff. WCAG contrast check. |
| `lint.yml` | PRs touching any source | Detekt + slack/compose-lints (Android) and SwiftLint + SwiftFormat (iOS). Run separately so a lint failure does not mask snapshot regressions or vice versa. |
| `android-ci.yml` | PRs touching `android-catalogue/**` or `design-system/tokens/**` | `./gradlew assembleDebug verifyPaparazziDebug` — builds the app and runs Paparazzi snapshot tests only. Failed snapshots upload as PR artefacts (PNG diff). |
| `ios-ci.yml` | PRs touching `ios-catalogue/**` or `design-system/tokens/**` | `xcodebuild build` for the app target + `swift test --filter SnapshotTests` against the SPM package. Failed snapshot diffs uploaded as artefacts. |

**Caching** is enabled on both platforms (Gradle dependencies, SPM artefacts) to keep CI under ~5 min per run.

That is the entirety of CI/CD for Option A. Nothing publishes; nothing versions. The Option C migration adds publish workflows on top.

---

## 8. Phased build roadmap (mirrors PART 5 of the OSS analysis md)

| Phase | Scope |
|---|---|
| **0 — Scaffold** | Token pipeline working end-to-end with placeholder colors. Android builds. iOS builds. Both render a single styled label proving the pipeline. **Adaptive navigation shell wired up** — Nav3 + `ListDetailPaneScaffold` on Android, `NavigationSplitView` on iOS — both showing a placeholder grouped list (left) and a detail pane (right) on tablet, single-pane on phone. |
| **1 — Foundation** | All token categories complete. Button (primary, secondary, outlined, ghost, icon, loading state). Typography specimen page. Theme toggle (light/dark). Each foundation slotted into the adaptive catalogue shell. |
| **2 — Inputs** | Checkbox (incl. indeterminate), Radio, Switch, TextField (all states), Slider. |
| **3 — Feedback & Overlay** | Loading (circular, linear, skeleton), Toast / Snackbar, Modal / Alert Dialog, Bottom Sheet (with detents), Badge. |
| **4 — Navigation & Data Display** | Tabs, Chip (filter, input, suggestion), Card (elevated, outlined, filled), List Item / Cell, Divider. |
| **5 — Catalogue polish** | Showkase browser hooked up (Android) inside the adaptive shell, `NavigationSplitView` browser polished (iOS), code snippet viewer per component, a11y overlay toggle, adaptive-layout snapshot variants (compact + expanded widths) green in CI. |

Each phase ends with green CI: build + snapshot regression + behavior tests + manual a11y check from PART 4.
