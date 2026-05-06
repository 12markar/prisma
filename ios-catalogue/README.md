# iOS Catalogue

Native SwiftUI catalogue app. Will be scaffolded once the design system tokens land.

## Planned stack

| Concern | Choice |
|---|---|
| Language | Swift 5.10+ |
| IDE | Xcode 16+ |
| Deployment target | iOS 17.0+ |
| UI | SwiftUI |
| Package | SPM (`CoreUI`, `Components` targets) + separate Xcode app project (`CatalogueApp`) |
| Navigation | `NavigationStack` (compact) + **`NavigationSplitView`** (regular size class) |
| Adaptive layouts | `NavigationSplitView` collapses on iPhone, expands on iPad — no platform-specific code branching |
| Icons | SFSafeSymbols |
| Loading | exyte/ActivityIndicatorView |
| Skeleton | CSolanaM/SkeletonUI |
| Toast | elai950/AlertToast |
| Bottom sheet | Native `.presentationDetents` (iOS 17+) |
| Snapshot tests | pointfreeco/swift-snapshot-testing |

## Planned package structure

```
ios-catalogue/
├── CoreUI/                            SPM target — tokens (generated), theme, fonts
│   └── Sources/CoreUI/
│       ├── Tokens/                    Colors.swift, Typography.swift, etc.
│       └── Resources/Fonts/           .ttf / .otf
├── Components/                        SPM target — Button, TextField, ...
│   └── Sources/Components/
├── CatalogueApp/                      Xcode app project, depends on the SPM package
│   ├── CatalogueApp.xcodeproj
│   └── CatalogueApp/                  ContentView, NavigationSplitView catalogue browser
└── Tests/
    └── ComponentsTests/               swift-snapshot-testing snapshots
```

## How tokens arrive

`design-system/scripts/build.mjs` writes generated Swift into:
```
CoreUI/Sources/CoreUI/Tokens/
├── Colors.swift
├── Typography.swift
├── Spacing.swift
├── Radius.swift
├── Elevation.swift
└── Motion.swift
```

A Run Script Build Phase in the Xcode app target calls `npm run build-tokens` from `../design-system/` so tokens regenerate automatically on every build.

## Adaptive layout

- **iPhone** (compact horizontal size class) — `NavigationSplitView` collapses to push-style. Component list pushes to detail screen.
- **iPad** (regular horizontal size class) — sidebar (component list grouped by Foundations, Inputs, Feedback, Navigation, Data Display) + detail pane (live demo, variants, tokens used, a11y notes, code snippet) shown side-by-side.
- One implementation, no branching — SwiftUI handles the responsive transition automatically.

## Status

Pending token output from the design phase.
