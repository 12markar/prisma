# Android Catalogue

Native Jetpack Compose catalogue app. Will be scaffolded once the design system tokens land.

## Planned stack

| Concern | Choice |
|---|---|
| Language | Kotlin 2.0+ |
| Build | AGP 8.6+, Gradle 8.x, KSP |
| compileSdk | 36 (Android 16) |
| minSdk | 26 (Android 8.0) |
| UI | Jetpack Compose (BOM, latest stable) |
| Navigation | **Jetpack Navigation 3** (`androidx.navigation3`) |
| Adaptive layouts | **`androidx.compose.material3.adaptive`** with `ListDetailPaneScaffold` for tablet two-pane UI |
| Catalogue browser | airbnb/Showkase, wrapped inside the adaptive shell |
| Bottom sheet / modal / tabs | composables/compose-unstyled |
| Skeleton | valentinilk/compose-shimmer |
| Lints | slack/compose-lints |
| Snapshot tests | Square Paparazzi |

## Planned module structure

```
android-catalogue/
├── core-ui/              Tokens (generated), theme, font registration, color provider
├── components/           Button, TextField, Checkbox, Radio, Switch, Toast, Modal,
│                         BottomSheet, Loading, Skeleton, Badge, Chip, Tabs, Card
└── catalogue/            The app — Nav3 + ListDetailPaneScaffold adaptive shell
                          hosting the Showkase component browser
```

## Adaptive layout

- **Compact** (phone, < 600 dp width) — single-pane. Component list pushes to detail.
- **Medium / Expanded** (tablet, foldable) — two-pane. Persistent left pane shows grouped component list (Foundations, Inputs, Feedback, Navigation, Data Display); right pane shows the selected component's detail (live demo, variants, tokens used, a11y notes, code snippet).
- Implementation: `NavigableListDetailPaneScaffold` reading `currentWindowAdaptiveInfo()`.

## How tokens arrive

`design-system/scripts/build.mjs` writes generated Kotlin into:
```
core-ui/src/main/java/xyz/ksharma/prisma/tokens/
├── Colors.kt
├── Typography.kt
├── Spacing.kt
├── Radius.kt
├── Elevation.kt
└── Motion.kt
```

A Gradle task in `:core-ui` will call `npm run build-tokens` from `../design-system/` as a dependency of `compileKotlin` so tokens regenerate automatically on every build.

## Status

Pending token output from the design phase.
