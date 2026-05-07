# Performance — Prisma

How to measure Prisma's runtime performance, audit composable stability, and chase memory leaks. Targets and tooling first; procedure second.

---

## Targets

| Metric | Target | How measured |
|---|---|---|
| Cold start (P50, mid-range device) | ≤ 1.5 s | `am start -W` Total time (Android); Instruments Time Profiler from app launch (iOS) |
| Cold start (P95) | ≤ 2.0 s | same |
| Detail-pane open after sidebar tap | ≤ 200 ms | Trace begin → first frame |
| Theme toggle frame | ≤ 16 ms | Layout Inspector recomposition timing |
| Memory after 5 min idle | No leaks | LeakCanary clean (Android); Instruments Leaks (iOS) |
| Recomposition count (typical interaction) | ≤ 1 per visible composable | Layout Inspector recomp counts |

---

## Android — Compose Compiler stability reports

Stability reports show which composables Compose can skip during recomposition (stable) versus those it always re-runs (unstable).

```bash
./gradlew :catalogue:assembleDebug -PcomposeCompilerReports=true
./gradlew :components:assembleDebug -PcomposeCompilerReports=true
```

Outputs land in:

- `catalogue/build/compose_compiler/`
- `components/build/compose_compiler/`

Inspect:

- `*-classes.txt` — which classes are stable / unstable. Aim for `stable class` on every domain type used as a Composable parameter.
- `*-composables.txt` — per-function recomposability. Look for `restartable skippable` (good) vs `restartable` (re-runs even when args don't change).
- `*-composables.csv` — same data, easier to grep.

### What to fix

- **Mutable collections as params** → switch to `kotlinx.collections.immutable` (`ImmutableList`, `PersistentList`).
- **Stable holders** → annotate `@Stable` or `@Immutable` on data classes only when guaranteed.
- **Lambda captures** → avoid capturing non-stable values; lift `remember(...) { lambda }` where useful.

The Prisma codebase already uses `ImmutableList` for `CatalogueRegistry.entries` and `tags` in `CatalogueEntry` for this reason.

---

## Android — LeakCanary

Wired as a `debugImplementation` on `:catalogue`. Boots automatically; toast appears when a leak is detected, full heap dump in Logcat.

```bash
./gradlew :catalogue:installDebug
adb shell am start -n xyz.ksharma.prisma.catalogue/.MainActivity
# Use the app, navigate sections, change theme, leave for 5 minutes.
# Watch Logcat for: "LEAK FOUND"
adb logcat | grep -i leakcanary
```

Run the standard "soak test" before any release: cold-launch → cycle every catalogue entry → toggle theme 10 times → background app → return. Expect zero leaks.

---

## Android — cold start measurement

```bash
# 1. Force-stop and clear caches
adb shell am force-stop xyz.ksharma.prisma.catalogue

# 2. Cold start with timing (run 5 times, average the "TotalTime")
for i in 1 2 3 4 5; do
  adb shell am force-stop xyz.ksharma.prisma.catalogue
  adb shell am start -W -n xyz.ksharma.prisma.catalogue/.MainActivity
done | grep TotalTime
```

`TotalTime` is what we report. Discard the first run (warm caches), take the median of the next 4.

For deeper trace: `adb shell perfetto -o /data/misc/perfetto-traces/cold-start.pftrace -t 5s` then open in <https://ui.perfetto.dev>.

---

## iOS — Instruments Time Profiler (cold start)

1. Quit the simulator app fully.
2. Xcode → Product → Profile (`⌘I`) → Time Profiler.
3. Hit record, launch the app from Springboard.
4. Stop after the first detail pane is interactive.
5. Inspect the call tree filtered to `Prisma`. Anything > 16 ms on the main thread should be questioned.

For memory: same path, choose Leaks instead. Run for 60s of normal use; expect zero leaks.

---

## Recomposition profiling (Android)

Use Android Studio's **Layout Inspector**:

1. Run the app in debug.
2. Tools → Layout Inspector → attach to `xyz.ksharma.prisma.catalogue`.
3. Toggle "Show Recomposition Counts" in the toolbar.
4. Interact with the app. Numbers next to composables increment as they recompose.

Watch for surprises: a `Text` next to a counter shouldn't recompose when an unrelated knob changes. If it does, hoist state higher or memoise the non-state-dependent slice.

---

## Catalogue-specific perf considerations

- **Knob state is per-showcase** — switching components disposes the previous showcase's state, which is intentional. Don't try to lift knob state into a shared holder; you'd lose `rememberSaveable` keys.
- **PreviewSurface + StatesGallery render simultaneously** — components like `BottomSheet` showcases trigger their sheet from the preview, but the gallery cells reference frozen state. Verify you're not paying for the modal/popover overlay multiple times.
- **`SidebarSearchField` recomposes on every keystroke** — the filtering uses `derivedStateOf` so child rows only recompose when the filtered list actually changes.

---

## Audit findings — 2026-05 baseline

Run with the wiring above:

```
./gradlew clean :catalogue:assembleDebug :components:assembleDebug -PcomposeCompilerReports=true --rerun-tasks
```

### Composable skippability

- **`:components`** — 47 / 47 composables `restartable skippable`. ✅
- **`:catalogue`** — 95 / 95 composables `restartable skippable`. ✅

### Class stability

- **`:components`** — 1 unstable class: `PrismaIcons`. Acceptable: it's an `object` carrying `@DrawableRes Int` constants; never used as a Composable parameter directly.

### Unstable parameters (skippability preserved, but referential-equality required to skip)

8 component functions take a raw `List<T>` parameter. Compose still skips them when callers pass the same instance, but switching to `ImmutableList<T>` from `kotlinx.collections.immutable` would let Compose skip on structural equality too.

| Component | Unstable param |
|---|---|
| `PrismaAutocomplete` | `suggestions: List<String>` |
| `PrismaAvatarGroup` | `seeds: List<String>` |
| `PrismaBreadcrumb` | `items: List<PrismaBreadcrumbItem>` |
| `PrismaCommandPalette` | `commands: List<PrismaCommand>` |
| `PrismaSegmentedControl` | `options: List<T>` |
| `PrismaTabs` | `tabs: List<T>` |
| `PrismaTagInput` | `tags: List<String>` |
| `PrismaWizardSteps` | `steps: List<String>` |

**Fix path** (queued, not blocking): change each public param to `ImmutableList<T>` and update callers to wrap with `persistentListOf(...)` or `.toImmutableList()`.

---

## Acceptance for "perf audit pass"

Before declaring a perf milestone done, verify:

- [ ] All `@Composable` functions in `:components` show as `restartable skippable` in stability reports.
- [ ] No `unstable class` warnings for any type used as a Composable parameter.
- [ ] Median cold start ≤ 1.5 s on a Pixel 7 / iPhone 13.
- [ ] LeakCanary clean after 5-min soak test.
- [ ] Instruments Leaks clean after 60 s of normal use.
- [ ] No composable recomposes more than once per relevant state change in Layout Inspector.
