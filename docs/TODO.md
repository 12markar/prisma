# Build TODO — Prisma

The actionable, step-by-step build list. For architectural rationale and tradeoffs, see [`ARCHITECTURE.md`](./ARCHITECTURE.md).

**Status legend:** `[ ]` not started · `[~]` in progress · `[x]` done · `[?]` decision needed

---

## 0. Pre-build housekeeping ✅

- [x] Project + folder + repo all renamed to `Prisma`
- [x] Site moved to `design-system/site/`
- [x] Detekt config: sensible defaults (Slack-style + compose-lints)
- [x] GitHub repo created: https://github.com/ksharma-xyz/prisma
- [x] Reference repos cloned to `references/` (gitignored)

---

## Phase 0 — Scaffold ✅

- [x] **0.1 Component specs** — all 38 specs in `design-system/component-specs/`
- [x] **0.2 Style Dictionary token pipeline** — Compose + SwiftUI emitters, contrast checker, lint, font copy script
- [x] **0.3 Android scaffold** — `:core-ui`, `:components`, `:catalogue` modules; AGP 8.7.2 / compileSdk 36 / Kotlin 2; Material3 adaptive `NavigableListDetailPaneScaffold`; `PrismaTheme` + `LocalPrismaIsDark`
- [x] **0.4 iOS scaffold** — Swift Package + xcodegen-generated Xcode project; `NavigationSplitView` adaptive shell
- [x] **0.5 CI/CD** — `tokens-check`, `lint`, `android-ci`, `ios-ci`, `deploy-site` workflows in place
- [x] **0.6 Acceptance** — both apps build, theme toggle works, adaptive shell on phone (single) and tablet (two-pane)

### Outstanding (cosmetic, not blocking)

- [ ] Drop `Instrument Sans` + `JetBrains Mono` `.ttf` files into `design-system/fonts/` (apps fall back to system fonts)
- [ ] Update iOS `Info.plist` `UIAppFonts` after fonts dropped
- [ ] Extend `android-ci.yml` and `ios-ci.yml` with snapshot test invocations once snapshot suites land

---

## Phase 1 — Foundation showcase + Button ✅

- [x] All 7 foundation showcases live (Typography, Color, Icon, Spacing, Elevation, Motion, Radius) on both platforms
- [x] Button shipped on both platforms — 6 variants × 3 sizes × all states (default / pressed / disabled / loading), haptic feedback wired
- [x] Catalogue detail page (live preview, variants, states, interactive playground)

### Outstanding

- [ ] Paparazzi snapshot tests for Button (Android)
- [ ] `swift-snapshot-testing` snapshots for Button (iOS)
- [ ] A11y XCUITest assertions (touch target, traits)

---

## Phase 2 — Inputs ✅

All shipped on both platforms with playgrounds. TextField, Checkbox, Radio, Switch, Slider, SegmentedControl, SearchBar, Autocomplete, Stepper, TagInput, DatePicker, TimePicker, ColorPicker.

### Outstanding

- [ ] Snapshot tests per input component
- [ ] A11y assertions per input component

---

## Phase 3 — Feedback & Overlay ✅

All shipped on both platforms with playgrounds. Toast, Banner, Modal, BottomSheet, Popover, Tooltip, Loading, Skeleton, Badge, EmptyState, Drawer.

### Outstanding

- [ ] Snapshot tests per feedback component

---

## Phase 4 — Navigation & Data Display ✅

All shipped on both platforms with playgrounds. Tabs, Chip, CommandPalette, Pagination, Breadcrumb, Wizard, Card, ListItem, Avatar, AvatarGroup, Divider.

### Outstanding

- [ ] Snapshot tests per nav/display component

---

## Phase 5 — Catalogue polish (the world-class layer)

### Done

- [x] Material3 adaptive list-detail shell (Android) + NavigationSplitView (iOS)
- [x] Theme toggle wired everywhere — system-following + user override; Material3 ColorScheme derived from Prisma tokens so M3 internals follow in-app theme
- [x] State preserved across pane transitions (`rememberSaveable` keys)
- [x] **Interactive prop playground** for every component — Storybook-style controls, live preview, states gallery
- [x] Command Palette (⌘K) component implemented; sidebar search field
- [x] Component-level a11y semantics: live regions on Toast/Banner, progress semantics on Loading, `invisibleToUser` on Skeleton, `heading()` on titles, `selectableGroup` on Tabs, merged descendants on Avatar/ListItem
- [x] Polished empty detail-pane state (with stats strip)
- [x] **Code snippet viewer per component** — copy button, live (reflects current knob values), wired into all 36 showcases on both platforms
- [x] **First-launch onboarding** screen — both platforms; `rememberSaveable` (Android) / `@AppStorage` (iOS) so it appears once
- [x] **Smooth animated theme transition** — root surface `animateColorAsState` (Android) + `.animation(value: scheme)` (iOS), 300ms ease
- [x] **Haptics audit** — selection feedback added to Switch, Tabs, Chip on both platforms (Button already had haptics)
- [x] Heart / pill / chip / list-item state-gallery cells are now stateful (tap to toggle), not frozen
- [x] **Per-component A11y panel** — `A11yPanel` reusable view rendering role / min-touch-target / behaviour bullets. Wired into all 36 playgrounds on both platforms.
- [x] **Live theme contrast indicator in chrome** — `ContrastBadge` computes WCAG ratio between `TextPrimary` and `SurfaceBase` for the active theme; AAA / AA / AA- / FAIL chip with live ratio.
- [x] **`ImmutableList<T>` migration** — 8 component public APIs converted; all 47 component composables now skip on structural equality (zero unstable params).
- [x] **Sidebar entrance animations** — staggered fade + 8dp slide-up on first appearance, ~280ms total. Both platforms.
- [x] **Inspector panel** — toggleable right-side overlay listing the active theme's resolved tokens (Surface / Text / Border / Accent / Status colours with hex, plus Spacing and Radius scales). Chrome button (`Layers` icon).
- [x] **A11y touch-target overlay** — toggleable 48dp/44pt magenta grid drawn over the catalogue so devs can eyeball touch-target compliance. Chrome button (`Grid` icon).

### Outstanding

- [ ] **Snapshot test suites** — Paparazzi (Android) + swift-snapshot-testing (iOS); golden images committed; CI fails on diff
- [ ] **Adaptive layout snapshot variants** — compact + medium + expanded widths
- [x] **Performance audit baseline** — Compose Compiler stability reports wired (`-PcomposeCompilerReports=true`); LeakCanary on debug; cold-start procedure documented; baseline written to [`docs/PERFORMANCE.md`](./PERFORMANCE.md). All 142 composables across `:components` + `:catalogue` are `restartable skippable`. Zero unstable parameters after `ImmutableList<T>` migration.

---

## Future upgrades (not blocking)

- [ ] Migrate to AGP 9 when stable (currently 8.7.2)
- [ ] Migrate to Jetpack Navigation 3 when stable
- [x] ~~Replace Material icons in catalogue chrome with Prisma icon set~~ — done; `PrismaIcons` registry on both platforms (64 icons)

---

## Open questions / decisions queued

- [ ] Krail Detekt config — share URL?
- [ ] App Store / Play Store distribution intent — affects metadata, signing setup
- [ ] Custom illustrations for empty states / onboarding — bespoke art or geometric/typographic?

---

## Working agreement

- We work phase by phase. Don't start Phase N+1 until N's acceptance criteria are green.
- Within a phase, we work component by component. Each component lands fully done (impl + snapshot + a11y + catalogue page + playground) before the next starts.
- TODO updates: I check items off as we complete them. New items get added to the relevant phase as they're discovered.
- For UI work I can verify visually (web showcase site), I will. For native UI I can't run, I'll say so explicitly rather than claim it works.
