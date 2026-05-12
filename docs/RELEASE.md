# Releasing Prisma Catalogue

The release workflow builds a debug APK (Android) and a simulator `.app` (iOS) and publishes them as assets on a GitHub Release — no local build environment needed.

## Trigger via git tag

```bash
git tag v1.0.0
git push origin v1.0.0
```

This publishes a release immediately with both artifacts attached.

## Trigger manually (any branch)

1. Go to **Actions → release → Run workflow**
2. Select the branch you want to build from (defaults to `main`)
3. Enter a tag name, e.g. `v1.0.0`
4. Click **Run workflow**

Manual runs create a **draft** release so you can review it before making it public.

## Artifacts

| File | Platform | Notes |
|------|----------|-------|
| `prisma-catalogue-<tag>.apk` | Android | Debug build, install directly on an emulator or device with USB debugging |
| `prisma-catalogue-<tag>-simulator.zip` | iOS | Simulator-only build, no signing required |

### Installing the APK on an Android emulator

```bash
adb install prisma-catalogue-<tag>.apk
```

### Installing the .app on an iOS Simulator

```bash
unzip prisma-catalogue-<tag>-simulator.zip
xcrun simctl install booted Prisma.app
xcrun simctl launch booted xyz.ksharma.prisma.catalogue
```

## Notes

- The `releases/` directory is git-ignored — never commit binaries to the repo.
- The workflow file lives at `.github/workflows/release.yml`.
