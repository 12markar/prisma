// Copies font files from design-system/fonts/ into each catalogue app's resource folder.
//
// Sources expected:
//   design-system/fonts/instrument_sans/*.ttf  (or any .ttf/.otf in design-system/fonts/)
//   design-system/fonts/jetbrains_mono/*.ttf
//
// Targets:
//   Android — android-catalogue/core-ui/src/main/res/font/  (lowercase snake_case, no spaces)
//   iOS     — ios-catalogue/CoreUI/Sources/CoreUI/Resources/Fonts/  (preserve original names)
//
// Run: npm run copy-fonts

import { readdirSync, mkdirSync, copyFileSync, existsSync, statSync } from 'node:fs';
import { dirname, join, basename, extname } from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const ROOT = join(__dirname, '..');
const SRC = join(ROOT, 'fonts');
const ANDROID_OUT = join(ROOT, '../android-catalogue/core-ui/src/main/res/font/');
const IOS_OUT = join(ROOT, '../ios-catalogue/CoreUI/Sources/CoreUI/Resources/Fonts/');

const FONT_EXT = new Set(['.ttf', '.otf']);

function* walkFonts(dir) {
  if (!existsSync(dir)) return;
  for (const entry of readdirSync(dir)) {
    const full = join(dir, entry);
    if (statSync(full).isDirectory()) yield* walkFonts(full);
    else if (FONT_EXT.has(extname(entry).toLowerCase())) yield full;
  }
}

const fonts = [...walkFonts(SRC)];

if (fonts.length === 0) {
  console.warn(`[copy-fonts] No fonts found in ${SRC}.`);
  console.warn('  Place .ttf/.otf files in design-system/fonts/ before running.');
  console.warn('  Recommended:');
  console.warn('    Instrument Sans  → https://fonts.google.com/specimen/Instrument+Sans');
  console.warn('    JetBrains Mono   → https://fonts.google.com/specimen/JetBrains+Mono');
  process.exit(0);
}

mkdirSync(ANDROID_OUT, { recursive: true });
mkdirSync(IOS_OUT, { recursive: true });

const androidName = (file) =>
  basename(file)
    .toLowerCase()
    .replace(/[^a-z0-9_.-]/g, '_')
    .replace(/-+/g, '_')
    .replace(/_+/g, '_');

let copied = 0;
for (const src of fonts) {
  copyFileSync(src, join(ANDROID_OUT, androidName(src)));
  copyFileSync(src, join(IOS_OUT, basename(src)));
  copied += 1;
}

console.log(`[copy-fonts] Copied ${copied} font files.`);
console.log(`  Android → ${ANDROID_OUT}`);
console.log(`  iOS     → ${IOS_OUT}`);
console.log(`\n  ⚠ iOS still needs UIAppFonts entries in CatalogueApp/Info.plist for each font filename.`);
