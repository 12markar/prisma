// Style Dictionary v4 build entrypoint for Prisma tokens.
//
// Reads W3C DTCG token JSON from ./tokens/, resolves references, and writes:
//   - ../android-catalogue/core-ui/src/main/java/xyz/ksharma/prisma/tokens/PrismaTokens.kt
//   - ../ios-catalogue/CoreUI/Sources/CoreUI/Tokens/PrismaTokens.swift
//   - ./build/css/tokens.css                   (sanity check / web reference)

import StyleDictionary from 'style-dictionary';
import { mkdirSync, existsSync, readdirSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { generateComposeFile } from './format-compose.mjs';
import { generateSwiftUIFile } from './format-swiftui.mjs';

const __dirname = dirname(fileURLToPath(import.meta.url));
const ROOT = join(__dirname, '..');
const TOKENS_DIR = join(ROOT, 'tokens');

if (
  !existsSync(TOKENS_DIR) ||
  readdirSync(TOKENS_DIR).filter((f) => f.endsWith('.json') && f !== 'sd.config.json').length === 0
) {
  console.log('[build-tokens] No tokens found — skipping.');
  process.exit(0);
}

const COMPOSE_OUT = join(ROOT, '../android-catalogue/core-ui/src/main/java/xyz/ksharma/prisma/tokens/');
const SWIFTUI_OUT = join(ROOT, '../ios-catalogue/Sources/CoreUI/Tokens/');
const CSS_OUT = join(ROOT, 'build/css/');

mkdirSync(COMPOSE_OUT, { recursive: true });
mkdirSync(SWIFTUI_OUT, { recursive: true });
mkdirSync(CSS_OUT, { recursive: true });

StyleDictionary.registerFormat({
  name: 'prisma/compose',
  format: ({ dictionary }) => generateComposeFile(dictionary.tokens),
});

StyleDictionary.registerFormat({
  name: 'prisma/swiftui',
  format: ({ dictionary }) => generateSwiftUIFile(dictionary.tokens),
});

const sd = new StyleDictionary({
  source: [
    'tokens/color.json',
    'tokens/typography.json',
    'tokens/spacing.json',
    'tokens/radius.json',
    'tokens/elevation.json',
    'tokens/motion.json',
  ],
  log: { verbosity: 'default', warnings: 'warn' },
  platforms: {
    compose: {
      buildPath: COMPOSE_OUT,
      files: [{ destination: 'PrismaTokens.kt', format: 'prisma/compose' }],
    },
    swiftui: {
      buildPath: SWIFTUI_OUT,
      files: [{ destination: 'PrismaTokens.swift', format: 'prisma/swiftui' }],
    },
    css: {
      transformGroup: 'css',
      buildPath: CSS_OUT,
      files: [{ destination: 'tokens.css', format: 'css/variables' }],
    },
  },
});

await sd.buildAllPlatforms();
console.log('[build-tokens] Done.');
console.log(`  Compose → ${COMPOSE_OUT}PrismaTokens.kt`);
console.log(`  SwiftUI → ${SWIFTUI_OUT}PrismaTokens.swift`);
console.log(`  CSS     → ${CSS_OUT}tokens.css`);
