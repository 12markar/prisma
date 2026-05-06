# Build TODO — Prisma

The actionable, step-by-step build list. For architectural rationale and tradeoffs, see [`ARCHITECTURE.md`](./ARCHITECTURE.md).

**Status legend:** `[ ]` not started · `[~]` in progress · `[x]` done · `[?]` decision needed

---

## 0. Pre-build housekeeping ✅

- [x] Project + folder + repo all renamed to `Prisma`
- [x] Duplicate `docs/prisma/design-system-output/` removed
- [x] Site moved to `design-system/site/`
- [x] Detekt config decision: sensible defaults (Slack-style + compose-lints) until krail config shared
- [x] GitHub repo created at https://github.com/ksharma-xyz/prisma (public)
- [x] Reference repos cloned to `references/` (gitignored): cvs-health iOS a11y, kiwicom orbit-compose

---

## Phase 0 — Scaffold (the foundation the rest hangs on)

Goal: token pipeline working end-to-end on both platforms; both apps build and render a placeholder showcase using real Prisma tokens; adaptive shell visible on phone (single-pane) and tablet (two-pane).

### 0.1 Fill in the missing component specs (I write these in matching Prisma format)

- [ ] `skeleton.md` — content-shaped placeholders, shimmer animation, reduce-motion handling
- [ ] `divider.md` — horizontal/vertical, inset/full, weight variants
- [ ] `list-item.md` — leading icon, primary + secondary text, trailing element, dividers, selectable state
- [ ] `avatar.md` — sizes, image/initials/icon fallback, status indicator dot
- [ ] `slider.md` — single-thumb, range, tick marks, continuous mode (design-sensitive — would have benefited from a Claude Design pass; I'll write best-effort + flag for review)
- [ ] `tooltip.md` — desktop hover / touch long-press / keyboard focus, auto-flip positioning
- [ ] `popover.md` — richer content variant of tooltip
- [ ] `segmented-control.md` — 2–5 options, single selection, distinct from Tabs
- [ ] `banner.md` — persistent inline message (info/success/warning/danger), distinct from Toast
- [ ] `empty-state.md` — illustration slot + title + description + optional action button
- [ ] `command-palette.md` — modal ⌘K interface, fuzzy search, sectioned results, keyboard nav

### 0.2 Style Dictionary token pipeline ✅

- [x] `cd design-system && npm install` (Style Dictionary v4 installed)
- [x] Custom Compose format `prisma/compose` — emits `Color(0xFFxxxxxx)`, `Dp`, `sp`, `PrismaSemanticColor` data class with `@Composable resolve()`, multi-layer `PrismaShadow` + `PrismaElevation`, `TextStyle` referencing `PrismaFonts`, `FloatArray` cubic-bezier easing
- [x] Custom SwiftUI format `prisma/swiftui` — emits `Color(.sRGB, red:green:blue:opacity:)`, `CGFloat`, `PrismaSemanticColor` struct with `resolve(_ scheme:)`, multi-layer `PrismaShadow` + `PrismaElevation`, `Font.custom` via `PrismaFonts.sans/mono(size:weight:)`, cubic-bezier tuples
- [x] `npm run build-tokens` runs end-to-end:
  - `PrismaTokens.kt` → `android-catalogue/core-ui/src/main/java/xyz/ksharma/prisma/tokens/` (453 lines)
  - `PrismaTokens.swift` → `ios-catalogue/CoreUI/Sources/CoreUI/Tokens/` (420 lines)
  - `tokens.css` → `design-system/build/css/` (sanity check — gitignored)
- [x] `scripts/check-contrast.mjs` — real WCAG AA check, 24 semantic text/surface pairs verified, all pass
- [x] `scripts/lint.mjs` — DTCG schema + reference resolution, 6 token files all valid
- [x] `scripts/copy-fonts.mjs` — copies any `.ttf`/`.otf` from `design-system/fonts/` to both apps' resource folders
- [ ] **Pending: drop actual `.ttf` files for Instrument Sans + JetBrains Mono into `design-system/fonts/`** (not blocking — apps fall back to system fonts until provided)
- [ ] **Pending: update iOS `Info.plist` UIAppFonts after fonts dropped in

### 0.3 Android scaffold

- [ ] Create Gradle multi-module project at `android-catalogue/`
  - [ ] Root `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, wrapper
  - [ ] AGP 8.6+, Kotlin 2.0+, Compose Compiler plugin, KSP
  - [ ] `compileSdk 36`, `minSdk 26`, target Java 17
- [ ] `:core-ui` module
  - [ ] `PrismaTheme` composable wrapping `MaterialTheme` (or fully custom CompositionLocal)
  - [ ] `LocalPrismaColors`, `LocalPrismaTypography`, `LocalPrismaSpacing` providers
  - [ ] Light/dark token resolution
  - [ ] Font registration (Instrument Sans + JetBrains Mono)
- [ ] `:components` module (empty initially, placeholder package)
- [ ] `:catalogue` app module
  - [ ] `MainActivity` with `setContent { PrismaTheme { CatalogueApp() } }`
  - [ ] Navigation 3 (`androidx.navigation3`) — `NavDisplay` with back-stack-as-state
  - [ ] `NavigableListDetailPaneScaffold` from `material3.adaptive`
  - [ ] Sidebar: `LazyColumn` with sticky headers, collapsible sections via `AnimatedVisibility`
  - [ ] Search field at top (`derivedStateOf` filter)
  - [ ] Detail pane shows placeholder text initially
- [ ] Wire `npm run build-tokens` as Gradle task `:core-ui:generateTokens`, depended on by `compileKotlin`
- [ ] Configure Detekt + slack/compose-lints + ktlint
  - [ ] `detekt.yml` (mirror krail or sensible default)
  - [ ] `kotlinx-collections-immutable` dependency added
- [ ] Verify `./gradlew assembleDebug` succeeds, app launches in emulator showing styled "Prisma" label + adaptive shell

### 0.4 iOS scaffold

- [ ] Create Swift Package at `ios-catalogue/`
  - [ ] `Package.swift` with `CoreUI` and `Components` library targets
  - [ ] `swift-tools-version:5.10`, `iOS(.v17)`
- [ ] `CoreUI` target
  - [ ] `PrismaTheme` `EnvironmentValue`
  - [ ] `Color(light:dark:)` extension reading `colorScheme`
  - [ ] Font registration (Instrument Sans + JetBrains Mono via `Resources/Fonts/`)
  - [ ] `Font.prisma(.body, weight: .regular)` extension
- [ ] `Components` target (empty initially)
- [ ] Create Xcode app project `CatalogueApp/CatalogueApp.xcodeproj`
  - [ ] Depends on local Swift Package via `.package(path: "..")`
  - [ ] `App.swift` with `@main` and `WindowGroup { CatalogueRoot() }`
  - [ ] `CatalogueRoot` uses `NavigationSplitView`
  - [ ] Sidebar: `List` with `DisclosureGroup` for sections, `.searchable(text:)`
  - [ ] Detail view shows placeholder initially
- [ ] Wire `npm run build-tokens` as Run Script Build Phase before "Compile Sources"
- [ ] Configure SwiftLint + SwiftFormat
  - [ ] `.swiftlint.yml` with strict rules + custom SwiftUI rules
  - [ ] `.swiftformat` config
  - [ ] Pre-commit hook
- [ ] Verify `xcodebuild` succeeds, app launches in simulator showing styled "Prisma" label + adaptive shell on iPhone (compact) and iPad (split)

### 0.5 CI/CD

- [ ] `.github/workflows/tokens-check.yml` — runs `npm run build-tokens`, fails on uncommitted diff + WCAG contrast check
- [ ] `.github/workflows/lint.yml` — Detekt + compose-lints (Android) and SwiftLint + SwiftFormat (iOS)
- [ ] `.github/workflows/android-ci.yml` — `./gradlew assembleDebug` (snapshot tests added in Phase 1 once components exist)
- [ ] `.github/workflows/ios-ci.yml` — `xcodebuild build` (snapshot tests added in Phase 1)
- [ ] `.github/workflows/deploy-site.yml` — GitHub Pages deploy from `docs/prisma/site/` on push to main
- [ ] First commit + push, verify all workflows green
- [ ] GitHub Pages enabled and site live

### 0.6 Phase 0 acceptance criteria

- [ ] Both apps build cleanly
- [ ] Both apps render a styled label using Prisma typography + color tokens
- [ ] Theme toggle works (light/dark) on both
- [ ] Adaptive shell visible: phone single-pane, tablet two-pane
- [ ] All CI workflows green
- [ ] Site deployed to GitHub Pages

---

## Phase 1 — Foundation showcase + Button (the first real component)

### 1.1 Foundation showcase pages — bespoke designs (not generic detail layout)

Each foundation gets its own visually-distinctive showcase page on both platforms.

- [ ] **Typography specimen** — every type token rendered at full size, label + family + weight + size + line height + letter spacing alongside each
- [ ] **Color grid** — primitive scale on left, semantic tokens on right, light/dark side-by-side, contrast ratio badge per swatch, tap to copy token name
- [ ] **Icon grid** — searchable, all icons at uniform size, tap to copy import code
- [ ] **Spacing visualizer** — stacked horizontal bars showing actual width per token
- [ ] **Elevation showcase** — cards demonstrating each elevation token, light + dark side-by-side
- [ ] **Motion demo** — interactive — tap to trigger an animation using each duration/easing token
- [ ] **Radius showcase** — squares with each radius applied

### 1.2 Button component (the canonical example — every other component follows this template)

- [ ] **Android implementation**
  - [ ] All variants: primary, secondary, outlined, ghost, icon, destructive
  - [ ] All sizes: small, default, large
  - [ ] All states: default, pressed, focused, disabled, loading
  - [ ] Haptic feedback on press
  - [ ] `@ShowkaseComposable` annotations for every variant × size combination
  - [ ] Paparazzi snapshot tests (states × theme × font scale matrix)
  - [ ] A11y tests (`Role.Button`, `contentDescription`, min 48dp touch target)
- [ ] **iOS implementation**
  - [ ] Same variants, sizes, states
  - [ ] `UIImpactFeedbackGenerator` on press
  - [ ] `swift-snapshot-testing` snapshots
  - [ ] XCUITest a11y assertions (`accessibilityLabel`, `accessibilityTraits = .isButton`, min 44pt touch target)
- [ ] **Catalogue detail page** (both platforms)
  - [ ] Live demo at top
  - [ ] Variants showcase (all rendered side-by-side)
  - [ ] States showcase (interactive — tap to see pressed, etc.)
  - [ ] "Tokens used" — read from spec
  - [ ] "Accessibility" — read from spec
  - [ ] Code snippet (live, token-aware)
  - [ ] Interactive prop playground (toggle disabled, switch variant, edit label, swap icon)

### 1.3 Phase 1 acceptance criteria

- [ ] All 7 foundation showcase pages live in catalogue (Android + iOS)
- [ ] Button shipped on both platforms, all variants × sizes × states
- [ ] Snapshot test suite green for Button (≥ 8 PNGs per platform per variant)
- [ ] A11y tests green
- [ ] Theme toggle propagates correctly through everything
- [ ] CI green on both platforms

---

## Phase 2 — Inputs

- [ ] TextField (default, focused, error, disabled, with helper text — all states)
- [ ] Checkbox (unchecked, checked, indeterminate, disabled)
- [ ] Radio (selectable group semantics)
- [ ] Switch / Toggle
- [ ] Slider (single-thumb + range)
- [ ] Segmented Control

Each: Android + iOS implementation, snapshot tests, a11y tests, catalogue detail page with playground.

---

## Phase 3 — Feedback & Overlay

- [ ] Loading (circular, linear)
- [ ] Skeleton
- [ ] Toast / Snackbar
- [ ] Banner / Inline Alert
- [ ] Modal / Alert Dialog
- [ ] Bottom Sheet (with detents)
- [ ] Tooltip
- [ ] Popover
- [ ] Badge (count, dot)
- [ ] Empty State

---

## Phase 4 — Navigation & Data Display

- [ ] Tabs (top + scrollable variants)
- [ ] Chip (filter, input, suggestion)
- [ ] Card (elevated, outlined, filled)
- [ ] List Item / Row
- [ ] Avatar
- [ ] Divider

---

## Phase 5 — Catalogue polish (the world-class layer)

- [ ] Showkase browser polished inside adaptive shell (Android)
- [ ] `NavigationSplitView` browser polished (iOS)
- [ ] Code snippet viewer per component (syntax-highlighted, copy button)
- [ ] **Interactive prop playground** for every component (Storybook-style controls)
- [ ] **Command Palette (⌘K)** — fuzzy-search any component instantly
- [ ] **A11y overlay toggle** — visualises every component's a11y tree
- [ ] **Inspector panel** — view hierarchy + applied tokens + computed contrast ratios
- [ ] Adaptive layout snapshot variants in CI (compact + expanded widths)
- [ ] Polished empty states everywhere
- [ ] First-launch onboarding screen
- [ ] Smooth theme transition (animated, no flash)
- [ ] Choreographed entrance animations on sidebar
- [ ] Haptics audit across every interaction
- [ ] Performance audit — cold start < 1.5 s, zero memory leaks, stable composables verified

---

## Future upgrades (not blocking)

- [ ] **Migrate to AGP 9** when stable — currently using AGP 8.7.2. AGP 9 is in alpha as of 2025-05; revisit once it ships stable.
- [ ] Migrate to **Jetpack Navigation 3** (`androidx.navigation3`) when stable — Phase 0 uses `NavigableListDetailPaneScaffold`'s built-in back stack, which is sufficient for a list-detail catalogue. Nav 3 becomes valuable for deeper navigation graphs.
- [ ] **Replace Material icons in catalogue chrome with design-system-supplied icon set.** Currently `Icons.Default.Search`, `Icons.Default.KeyboardArrowDown`, `Icons.AutoMirrored.Filled.KeyboardArrowRight` (Sidebar.kt). The Foundations / Icons showcase page (Phase 1) will define the canonical icon set; once it lands, the catalogue chrome should consume from there too. iOS equivalent: replace SF Symbols defaults with the same canonical set bridged via SFSafeSymbols.

---

## Open questions / decisions queued

These don't block Phase 0 but should be resolved before they become blockers:

- [ ] **Krail Detekt config** — share URL?
- [ ] **GitHub username/org** for Option C migration (not blocking)
- [ ] **App Store / Play Store distribution intent** — affects metadata, signing setup
- [ ] **Custom illustrations** for empty states / onboarding — bespoke art or geometric/typographic?
- [ ] **Icon set** — SF Symbols on iOS is decided; on Android, do we use `Icons.Default.*` from M3, Lucide, or a bespoke set?
- [ ] **Whether to spec Command Palette as a reusable component** vs build into catalogue chrome only

---

## Working agreement

- We work phase by phase. Don't start Phase N+1 until N's acceptance criteria are green.
- Within a phase, we work component by component. Each component lands fully done (impl + snapshot + a11y + catalogue page + playground) before the next starts.
- TODO updates: I check items off as we complete them. New items get added to the relevant phase as they're discovered.
- For UI work I can verify visually (web showcase site), I will. For native UI I can't run, I'll say so explicitly rather than claim it works.
