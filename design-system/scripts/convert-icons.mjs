// Convert Prisma SVG icons → Android VectorDrawable XMLs + iOS asset catalog.
//
// The Prisma icon set uses a uniform style: 24×24 viewBox, stroke-only paths,
// 1.75px stroke, `currentColor` stroke, rounded line cap/join. Elements seen:
// <circle>, <path>, <rect> (no <line>, <polyline>, <polygon>, <ellipse>).
//
// Outputs:
//   - Android: ../android-catalogue/components/src/main/res/drawable/ic_prisma_<name>.xml
//   - iOS:     ../ios-catalogue/Sources/Components/Resources/PrismaIcons.xcassets/<name>.imageset/{name.svg, Contents.json}
//   - Codegen: PrismaIcons.kt (Android) and PrismaIcons.swift (iOS) — type-safe registry
//
// Run: npm run build-icons

import { readFileSync, readdirSync, mkdirSync, writeFileSync, existsSync, rmSync, copyFileSync } from 'node:fs';
import { dirname, join, basename } from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const ROOT = join(__dirname, '..', '..');
const SRC = join(ROOT, 'docs/Claude_design/exports 7/tier-a-icons/individual');

const ANDROID_DRAWABLE_OUT = join(ROOT, 'android-catalogue/components/src/main/res/drawable');
const IOS_ASSETS_OUT = join(ROOT, 'ios-catalogue/Sources/Components/Resources/PrismaIcons.xcassets');

if (!existsSync(SRC)) {
  console.error(`[build-icons] Source not found: ${SRC}`);
  process.exit(1);
}

mkdirSync(ANDROID_DRAWABLE_OUT, { recursive: true });
mkdirSync(IOS_ASSETS_OUT, { recursive: true });

// Asset-catalog root marker
writeFileSync(
  join(IOS_ASSETS_OUT, 'Contents.json'),
  JSON.stringify({ info: { author: 'xcode', version: 1 } }, null, 2),
);

const drawableName = (svgName) => `ic_prisma_${svgName.replace('.svg', '').replace(/-/g, '_')}`;
const pascalCase = (s) =>
  s.replace('.svg', '').split('-').map((w) => w.charAt(0).toUpperCase() + w.slice(1)).join('');
const camelCase = (s) => {
  const p = pascalCase(s);
  return p.charAt(0).toLowerCase() + p.slice(1);
};

// Convert SVG <circle>/<rect>/<path> to a single VectorDrawable, stroke-only.
function svgToVectorDrawable(svgText) {
  const vbMatch = svgText.match(/viewBox="(\d+)\s+(\d+)\s+(\d+)\s+(\d+)"/);
  const [vw, vh] = vbMatch ? [vbMatch[3], vbMatch[4]] : ['24', '24'];

  const paths = [];

  // <circle cx cy r />
  for (const m of svgText.matchAll(/<circle\s+cx="([\d.]+)"\s+cy="([\d.]+)"\s+r="([\d.]+)"\s*\/?>/g)) {
    const cx = parseFloat(m[1]), cy = parseFloat(m[2]), r = parseFloat(m[3]);
    paths.push(`M${cx - r},${cy} a${r},${r} 0 1,0 ${r * 2},0 a${r},${r} 0 1,0 -${r * 2},0 Z`);
  }

  // <rect x y width height [rx] [ry] />
  for (const m of svgText.matchAll(
    /<rect\s+x="([\d.]+)"\s+y="([\d.]+)"\s+width="([\d.]+)"\s+height="([\d.]+)"(?:\s+rx="([\d.]+)")?(?:\s+ry="([\d.]+)")?\s*\/?>/g,
  )) {
    const x = parseFloat(m[1]), y = parseFloat(m[2]), w = parseFloat(m[3]), h = parseFloat(m[4]);
    const rx = m[5] ? parseFloat(m[5]) : 0;
    const ry = m[6] ? parseFloat(m[6]) : rx;
    if (rx > 0) {
      paths.push(
        `M${x + rx},${y} h${w - rx * 2} ` +
          `a${rx},${ry} 0 0 1 ${rx},${ry} ` +
          `v${h - ry * 2} ` +
          `a${rx},${ry} 0 0 1 -${rx},${ry} ` +
          `h-${w - rx * 2} ` +
          `a${rx},${ry} 0 0 1 -${rx},-${ry} ` +
          `v-${h - ry * 2} ` +
          `a${rx},${ry} 0 0 1 ${rx},-${ry} Z`,
      );
    } else {
      paths.push(`M${x},${y} h${w} v${h} h-${w} Z`);
    }
  }

  // <path d="..." />
  for (const m of svgText.matchAll(/<path\b[^>]*\bd="([^"]+)"/g)) {
    paths.push(m[1]);
  }

  const pathBlocks = paths.map((d) => `    <path
        android:strokeColor="#000000"
        android:strokeWidth="1.75"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:fillColor="@android:color/transparent"
        android:pathData="${d.replace(/"/g, '&quot;')}" />`).join('\n');

  return `<?xml version="1.0" encoding="utf-8"?>
<!-- AUTO-GENERATED from Prisma icon set. Do not hand-edit; run npm run build-icons. -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="${vw}"
    android:viewportHeight="${vh}">
${pathBlocks}
</vector>
`;
}

// --- Process icons ---

const svgFiles = readdirSync(SRC).filter((f) => f.endsWith('.svg')).sort();
const entries = []; // { svg: 'arrow-right.svg', kotlin: 'ArrowRight', drawable: 'ic_prisma_arrow_right', swift: 'arrowRight', asset: 'arrow-right' }

for (const svgFile of svgFiles) {
  const fullSrc = join(SRC, svgFile);
  const svgText = readFileSync(fullSrc, 'utf8');

  // Android — VectorDrawable
  const drawable = drawableName(svgFile);
  writeFileSync(join(ANDROID_DRAWABLE_OUT, `${drawable}.xml`), svgToVectorDrawable(svgText));

  // iOS — asset catalog imageset
  const assetBase = svgFile.replace('.svg', '');
  const imageset = join(IOS_ASSETS_OUT, `${assetBase}.imageset`);
  mkdirSync(imageset, { recursive: true });
  copyFileSync(fullSrc, join(imageset, svgFile));
  writeFileSync(
    join(imageset, 'Contents.json'),
    JSON.stringify(
      {
        images: [{ filename: svgFile, idiom: 'universal' }],
        info: { author: 'xcode', version: 1 },
        properties: {
          'preserves-vector-representation': true,
          'template-rendering-intent': 'template',
        },
      },
      null,
      2,
    ),
  );

  entries.push({
    svg: svgFile,
    kotlin: pascalCase(svgFile),
    drawable,
    swift: camelCase(svgFile),
    asset: assetBase,
  });
}

// --- Generate type-safe registries ---

const kotlinRegistry = `package xyz.ksharma.prisma.components.icons

// AUTO-GENERATED from Prisma icon set (${entries.length} icons). Do not hand-edit;
// run \`npm run build-icons\` from design-system/.
//
// Usage in Compose:
//   Icon(painter = painterResource(PrismaIcons.Search), contentDescription = "Search",
//        tint = PrismaSemanticColors.TextPrimary.themed())

import androidx.annotation.DrawableRes
import xyz.ksharma.prisma.components.R

public object PrismaIcons {
${entries.map((e) => `    @DrawableRes public val ${e.kotlin}: Int = R.drawable.${e.drawable}`).join('\n')}

    /**
     * Every icon in the set as (name, drawableRes) pairs.
     * Iteration order mirrors alphabetical filename order.
     */
    public val all: List<Pair<String, Int>> = listOf(
${entries.map((e) => `        "${e.asset}" to ${e.kotlin}`).join(',\n')},
    )
}
`;

const kotlinDir = join(ROOT, 'android-catalogue/components/src/main/java/xyz/ksharma/prisma/components/icons');
mkdirSync(kotlinDir, { recursive: true });
writeFileSync(join(kotlinDir, 'PrismaIcons.kt'), kotlinRegistry);

const swiftRegistry = `import SwiftUI

// AUTO-GENERATED from Prisma icon set (${entries.length} icons). Do not hand-edit;
// run \`npm run build-icons\` from design-system/.
//
// Usage in SwiftUI:
//   Image(prisma: .search)
//       .renderingMode(.template)
//       .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))

public enum PrismaIcon: String, CaseIterable {
${entries.map((e) => `    case ${e.swift} = "${e.asset}"`).join('\n')}
}

public extension Image {
    /// Initialise an Image from a Prisma icon symbol. Asset catalog
    /// PrismaIcons.xcassets is bundled with the Components SPM target.
    init(prisma icon: PrismaIcon) {
        self.init(icon.rawValue, bundle: .module)
    }
}
`;

mkdirSync(join(ROOT, 'ios-catalogue/Sources/Components/Icons'), { recursive: true });
writeFileSync(
  join(ROOT, 'ios-catalogue/Sources/Components/Icons/PrismaIcons.swift'),
  swiftRegistry,
);

console.log(`[build-icons] Done.`);
console.log(`  ${entries.length} icons → Android VectorDrawables in: ${ANDROID_DRAWABLE_OUT}`);
console.log(`  ${entries.length} icons → iOS asset catalog: ${IOS_ASSETS_OUT}`);
console.log(`  Generated PrismaIcons.kt and PrismaIcons.swift type-safe registries.`);
