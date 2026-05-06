# Open Source Component Library — Design System Reference Guide
### Jetpack Compose (Android) · SwiftUI (iOS)
**Prepared for: Karan · May 2026**
**Purpose: Personal Component Library & Catalogue App**

---

## Overview & Purpose

This document analyses the best open-source repositories for native Jetpack Compose (Android) and SwiftUI (iOS) UI components. Goal: cherry-pick, adapt, and combine these into your own design system library and catalogue/showcase app.

Focus areas:
- Core everyday components: Button, Typography, Modal, Bottom Sheet, Loading/Skeleton, Checkbox, Radio, Switch, Toast, Badge, Chip, TextField
- Production-grade A11y — VoiceOver/TalkBack semantics, content descriptions, keyboard nav
- Zero third-party frameworks — 100% native Compose/SwiftUI
- Open licences that allow commercial use (MIT, Apache 2.0)

---

# PART 1 — JETPACK COMPOSE (ANDROID)

## 1. Compose Unstyled — `composables/compose-unstyled`
**GitHub:** https://github.com/composables/compose-unstyled  
**Licence:** Apache 2.0 · **Stars:** ~4k+

The single most recommended library in the Compose community for building design systems. Fully **renderless, fully accessible primitives** — zero visual opinion, pure behaviour. You bring your own styling.

| Attribute | Detail |
|---|---|
| Key Components | Modal Bottom Sheet, Modal, Menu, Select, Scroll Area, Tabs, Slider, Checkbox, Radio Group, Text Field |
| A11y | Full semantic roles, keyboard nav, screen-reader traits built in at the primitive level |
| Why Use It | Best-in-class Bottom Sheet — community favourite over M3 ModalBottomSheet which has known bugs. Renders above system bars, soft keyboard support, nested scroll. |
| Licence | Apache 2.0 |

> **Community signal:** Developers repeatedly call its Bottom Sheet _"the only one that actually works"_ and have migrated away from Material 3's version specifically for API clarity and edge-case handling.

---

## 2. Showkase — `airbnb/Showkase`
**GitHub:** https://github.com/airbnb/Showkase  
**Licence:** Apache 2.0 · **Stars:** ~3.3k

A must-have annotation-processor library for building your **catalogue app**. Add `@ShowkaseComposable` to any composable and Showkase auto-generates a browsable, searchable showcase screen — no manual wiring needed.

| Attribute | Detail |
|---|---|
| Purpose | Auto-generates a component browser/catalogue UI from annotations |
| Key Features | `@ShowkaseComposable`, `@ShowkaseColor`, `@ShowkaseTypography`; searchable browser; deep links; Paparazzi screenshot testing |
| A11y | Preview includes dark mode & font-scale variants so you can test A11y visually |
| Licence | Apache 2.0 |

---

## 3. Slack's `compose-rules`
**GitHub:** https://github.com/slackhq/compose-lints  
**Licence:** Apache 2.0

Slack open-sourced a set of static analysis rules for enforcing Compose best practices. Their internal design system is 100% Compose-native. The rules are excellent reference material for component design conventions — things like when to hoist state, Modifier ordering, slot API patterns.

---

## 4. Material Design 3 for Compose — Source Reference
**GitHub:** https://github.com/material-components/material-components-android  
**Licence:** Apache 2.0

Even if you're building a custom design system, studying the M3 Compose source is essential. It shows how Google implements theming (`MaterialTheme.colorScheme`, typography), semantic roles, and state layers.

| Component to Study | Why It Matters |
|---|---|
| Button / IconButton | State layer, ripple, contentDescription patterns |
| Checkbox / Switch / RadioButton | Tri-state checkbox A11y semantics, `toggleable` modifier |
| ModalBottomSheet | Understand its known limitations so you can replace with Compose Unstyled |
| CircularProgressIndicator | `semantics { progressBarRangeInfo }` pattern |
| Scaffold / NavigationBar | System bars, padding, window insets handling |

---

## 5. Orbit Compose — `kiwicom/orbit-compose` (Archived Reference)
**GitHub:** https://github.com/kiwicom/orbit-compose  
**Licence:** MIT

Kiwi.com's design system — archived as of July 2025 but still fully readable and excellent as a real-world reference. Shows how a production team structured a full design system on top of M3.

- How they layered colour tokens over M3 colour schemes
- Alert, Badge, ButtonLink, Tag — components not found elsewhere
- Gradle multi-module structure for a publishable library

---

## 6. SimformSolutions Component Repos
**GitHub:** https://github.com/SimformSolutionsPvtLtd  
**Licence:** MIT

Focused standalone Compose component libraries, each covering one component. Ideal for lifting out specific implementations:

| Repo | Component |
|---|---|
| SSJetPackComposeProgressButton | Button with multiple loading animations |
| SSJetpackComposeSwipeableView | Swipeable rows with edit/delete actions |
| SSComposeFoodRecipeCard | Card component pattern reference |

---

## Jetpack Compose — Component Coverage Map

| Component | Primary Source | A11y Notes |
|---|---|---|
| Button / Icon Button | M3 Compose source | `contentDescription`, `Role.Button` |
| Typography System | M3 `MaterialTheme.typography` | Dynamic font scale tested via Showkase |
| Bottom Sheet (Modal) | **Compose Unstyled** | Best API; dismiss semantics built-in |
| Bottom Sheet (Persistent) | **Compose Unstyled** | Detents, soft keyboard, nested scroll |
| Loading — Circular | M3 `CircularProgressIndicator` | `progressBarRangeInfo` semantics |
| Loading — Linear | M3 `LinearProgressIndicator` | Indeterminate uses `contentDescription` |
| Skeleton Loading | `valentinilk/compose-shimmer` | Reduce motion via `WindowInfo` |
| Checkbox | **Compose Unstyled** / M3 | Tri-state semantics, `toggleable` role |
| Radio Button | **Compose Unstyled** / M3 | `selectableGroup` semantics |
| Switch / Toggle | M3 Switch | `Role.Switch`, checked state announced |
| Modal / Dialog | **Compose Unstyled** Modal | `DialogProperties`, focus trap |
| Toast / Snackbar | M3 `SnackbarHost` | `LiveRegion` — announces on show |
| Text Field | M3 `OutlinedTextField` | Error semantics, label association |
| Chip / Filter Chip | M3 `FilterChip` / `SuggestionChip` | Selected state, `Role.Checkbox` |
| Badge | Orbit Compose reference | `contentDescription` for count |
| Tabs | **Compose Unstyled** Tabs | Selected tab, `tabIndex` semantics |
| Slider | **Compose Unstyled** Slider | `RangeInfo`, step description |
| Catalogue App | **Showkase** annotations | Auto-browse, search, dark mode previews |

---

# PART 2 — SWIFTUI (iOS)

## 1. SwiftUIX — `SwiftUIX/SwiftUIX`
**GitHub:** https://github.com/SwiftUIX/SwiftUIX  
**Licence:** MIT · **Stars:** ~6.5k+

The most comprehensive extension library for SwiftUI — often described as _"the missing standard library"_. Backfills hundreds of APIs across all iOS versions, provides UIKit-parity components, 100% native with no third-party dependencies.

| Attribute | Detail |
|---|---|
| Key Components | `PaginationView`, `VisualEffectView`, `CocoaTextField`, `SearchBar`, `ActivityIndicator`, `ProgressBar`, Toast, `WindowOverlay` (renders above TabBar) |
| A11y | Uses native SwiftUI modifiers; `WindowOverlay` preserves focus management |
| Why Use It | Reference implementation for bridging UIKit components into SwiftUI natively. Source shows idiomatic SwiftUI wrapper patterns. |
| Licence | MIT |

---

## 2. ActivityIndicatorView — `exyte/ActivityIndicatorView`
**GitHub:** https://github.com/exyte/ActivityIndicatorView  
**Licence:** MIT · **Stars:** ~1.7k

Multiple preset loading/activity indicator styles — all pure SwiftUI, no `UIActivityIndicatorView` bridging. Essential for loading states in your design system.

| Indicator Styles | A11y |
|---|---|
| ArrowSpin, Bars, BallPulse, BallBeat, CircleRotateChase, LineSpinFade, TripleRings, and more | Add `.accessibilityLabel("Loading")` and `.accessibilityAddTraits(.updatesFrequently)` |

---

## 3. SkeletonUI — `CSolanaM/SkeletonUI`
**GitHub:** https://github.com/CSolanaM/SkeletonUI  
**Licence:** MIT

Elegant skeleton/shimmer loading animations in pure SwiftUI using Combine. Apply `.skeleton()` modifier to any view — cards, lists, text blocks.

- Modifier-based API: `.skeleton(with: isLoading)`
- Supports shape customisation: rectangles, circles, capsules
- **Combine-driven — pauses animation when reduce-motion accessibility setting is on**

---

## 4. iOS SwiftUI Accessibility Techniques — `cvs-health`
**GitHub:** https://github.com/cvs-health/ios-swiftui-accessibility-techniques  
**Licence:** Apache 2.0

The most comprehensive SwiftUI accessibility reference available. Maintained by the CVS Health accessibility team — live good/bad examples for every WCAG 2.2 pattern, testable with VoiceOver. **Your A11y Bible for SwiftUI.**

| Pattern Covered | What You Learn |
|---|---|
| Buttons & Links | `accessibilityLabel`, `accessibilityHint`, minimum touch target (44×44pt) |
| Checkboxes / Toggles | `accessibilityValue("checked"/"unchecked")`, `accessibilityAddTraits(.isToggle)` |
| Loading States | `accessibilityAddTraits(.updatesFrequently)`, live region announcements |
| Modal Sheets | `accessibilityViewIsModal`, focus management on present/dismiss |
| Text Fields | Label vs placeholder, error announcements |
| Images | Decorative vs informative, `isImage` trait |
| Custom Actions | `accessibilityCustomAction` for swipe actions in lists |
| Headings | `.accessibilityAddTraits(.isHeader)` for VoiceOver navigation |

---

## 5. AlertToast — `elai950/AlertToast`
**GitHub:** https://github.com/elai950/AlertToast  
**Licence:** MIT · **Stars:** ~2.4k

Pure SwiftUI toast/alert library — Apple-native style toasts for success, error, warning, system image. No UIKit bridging. Modifier-based API.

```swift
.toast(isPresenting: $show) {
  AlertToast(type: .complete(.green), title: "Done")
}
```

- Supports position: top or bottom
- Customisable duration, background, font
- A11y: pair with `UIAccessibility.post(notification: .announcement, argument: "Done")` for VoiceOver users

---

## 6. BottomSheets — `c-villain/BottomSheets`
**GitHub:** https://github.com/c-villain/BottomSheets  
**Licence:** MIT

Backported native SwiftUI bottom sheet for iOS 14+. Uses native `presentationDetents` on iOS 16.4+ and falls back to a custom implementation — zero behaviour gap.

- Detents: `.height(244)`, `.medium`, `.fraction(0.6)`, `.large`
- Clean API: `.bottomSheet(isPresented: $show) { Content() }`
- Mirror's native API so migrating at iOS 16.4 target is trivial

---

## 7. SFSafeSymbols — `SFSafeSymbols/SFSafeSymbols`
**GitHub:** https://github.com/SFSafeSymbols/SFSafeSymbols  
**Licence:** MIT · **Stars:** ~1.6k

Type-safe access to SF Symbols — essential for icon usage across your design system. Prevents runtime crashes from mistyped symbol names, compile-time availability checking per iOS version.

```swift
Image(systemSymbol: .heart) // instead of Image(systemName: "heart")
```

---

## SwiftUI — Component Coverage Map

| Component | Primary Source | A11y Notes |
|---|---|---|
| Button / Icon Button | Native SwiftUI + SFSafeSymbols | `accessibilityLabel`, 44pt min target |
| Typography System | Native `Font` + custom `ViewModifier` | Dynamic Type: `.font(.body)` scales automatically |
| Bottom Sheet (Modal) | **c-villain/BottomSheets** or native iOS 16+ | `accessibilityViewIsModal`, focus on present |
| Loading — Circular | **ActivityIndicatorView (exyte)** | `updatesFrequently` trait, `accessibilityLabel` |
| Loading — Linear | Native `ProgressView` | `.progressViewStyle` + `accessibilityValue` |
| Skeleton Loading | **SkeletonUI (CSolanaM)** | Respects reduce-motion; label views hidden |
| Checkbox | Custom SwiftUI (cvs-health reference) | `accessibilityValue` checked/unchecked, `.isToggle` |
| Radio Button | Custom SwiftUI | `accessibilityAddTraits(.isSelected)` |
| Switch / Toggle | Native SwiftUI `Toggle` | `.isToggle` trait, label is VoiceOver focus |
| Modal / Dialog | Native `.sheet` / `.fullScreenCover` | `accessibilityViewIsModal: true` |
| Toast | **AlertToast** + `.accessibilityAnnouncement` | `UIAccessibility.post(notification: .announcement)` |
| Text Field | Native `TextField` + custom wrapper | `accessibilityLabel`, error as `accessibilityHint` |
| Chip / Tag | Custom `ViewModifier` | `.isButton` or `.isSelected` trait |
| Badge | Custom overlay + `ZStack` | `accessibilityLabel` includes count in spoken text |
| Tabs | Native `TabView` or custom | `.isSelected` on active tab |
| Catalogue App | Custom `NavigationStack` list | `.isHeader` trait per section |

---

# PART 3 — CATALOGUE APP ARCHITECTURE

## Module Structure

Structure your project as a multi-module Gradle project (Android) / Swift Package (iOS) so the library is independently publishable:

| Module / Target | Contents |
|---|---|
| `:core-ui` / `CoreUI` | Token definitions — colour, typography, spacing, shape, elevation |
| `:components` / `Components` | Individual composables/views — Button, Checkbox, BottomSheet, etc. |
| `:catalogue` / `CatalogueApp` | Showcase app — one screen per component, live demo + code snippet |
| `:testing` / `Screenshots` | Screenshot tests with Paparazzi / swift-snapshot-testing |

## Android Catalogue — Showkase Integration

```kotlin
@ShowkaseComposable(name = "Primary Button", group = "Buttons")
@Preview
@Composable
fun PrimaryButtonPreview() { PrimaryButton("Click me") }
```

- Showkase auto-generates a searchable browser; wire it as a debug-only launcher in your catalogue app
- Every component is previewed at all text sizes and dark/light mode
- Pair with **Paparazzi** for screenshot regression — catches A11y visual regressions automatically

## iOS Catalogue — Navigation Structure

Since SwiftUI doesn't have a Showkase equivalent, build a `NavigationStack` list:

- Group by category: Inputs, Feedback, Navigation, Layout, Data Display
- Each row → detail screen with: live demo, variants, token values shown
- Include VoiceOver label callout per component screen
- Add a global environment toggle for Large Text / Increased Contrast testing (`@Environment(\.accessibilityReduceMotion)`)

---

# PART 4 — ACCESSIBILITY CHECKLIST

Before marking any component done, verify all of the following:

| # | Criterion | Tool |
|---|---|---|
| ☐ | Touch target ≥ 44×44 pt (iOS) / 48×48 dp (Android) | Accessibility Scanner / A11y Inspector |
| ☐ | All interactive elements have `contentDescription` / `accessibilityLabel` | TalkBack / VoiceOver |
| ☐ | State changes announced: loading, error, success | Live Region / `.updatesFrequently` |
| ☐ | Checkbox/Switch/Radio announce checked state verbally | TalkBack / VoiceOver |
| ☐ | Loading indicators announce "Loading" and do not loop focus | TalkBack / VoiceOver |
| ☐ | Modal/Bottom Sheet traps focus, dismisses cleanly | TalkBack / VoiceOver |
| ☐ | Colour contrast ≥ 4.5:1 for normal text / 3:1 for large text | Colour Contrast Analyser |
| ☐ | Works with increased text size (up to 200%) | System Settings |
| ☐ | Reduce motion: animations disabled when setting is on | System Settings |
| ☐ | Decorative images have empty/null description | TalkBack / VoiceOver |

---

# PART 5 — PHASED BUILD ROADMAP

## Phase 1 — Foundation (Week 1–2)
- Token system: colours (light/dark), typography scale, spacing, shape, elevation
- Button: Primary, Secondary, Outlined, Ghost, Icon — with loading state
- Typography: Display, Headline, Title, Body, Label — all levels

## Phase 2 — Inputs (Week 3–4)
- Checkbox — with indeterminate state, full A11y
- Radio Group
- Switch / Toggle
- Text Field — outlined, filled, error/helper text states
- Slider

## Phase 3 — Feedback & Overlay (Week 5–6)
- Loading — Circular, Linear, Skeleton/Shimmer
- Toast / Snackbar with dismiss
- Modal / Alert Dialog
- Bottom Sheet — draggable with detents
- Badge / Notification dot

## Phase 4 — Navigation & Data Display (Week 7–8)
- Tabs — top / scrollable
- Chip — filter, input, suggestion
- Card — elevated, outlined, filled
- List Item / Cell — leading icon, trailing action, subtitle
- Divider, Spacer tokens

## Phase 5 — Catalogue App Polish
- Integrate Showkase (Android) / NavigationStack browser (iOS)
- Code snippet viewer per component
- A11y overlay: toggle to show semantic labels on screen
- Screenshot regression test suite
- README + contribution guide for Maven Central / SPM publishing

---

## Key Takeaways

- **Compose Unstyled** is the single most important dependency for Android — especially for Bottom Sheet and Modal. Start here.
- **Showkase** gives you the Android catalogue app nearly for free. Annotate as you build.
- **cvs-health/ios-swiftui-accessibility-techniques** is your complete A11y playbook for iOS — study every pattern before finalising components.
- **ActivityIndicatorView + SkeletonUI** together cover all loading states on iOS natively.
- Structure as a multi-module library from Day 1 so you can publish to Maven Central / Swift Package Index later.
- Study M3 Compose source code and Orbit Compose (archived) for real-world design system architecture patterns even if you don't use them as dependencies.
