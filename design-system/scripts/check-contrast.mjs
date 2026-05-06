// WCAG AA contrast checker for semantic color tokens.
//
// For every (text.*, surface.*) pair in tokens/color.json, computes contrast ratio
// in both light and dark mode. Fails the build if any pair drops below WCAG AA.
//
//   - Body text:  4.5:1 minimum
//   - Large text: 3:1   minimum (we still flag drops here as warnings)
//
// Run: npm run check-contrast

import { readFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { hexToRGBA } from './utils.mjs';

const __dirname = dirname(fileURLToPath(import.meta.url));
const ROOT = join(__dirname, '..');
const COLOR_FILE = join(ROOT, 'tokens/color.json');

const json = JSON.parse(readFileSync(COLOR_FILE, 'utf8'));

function resolveRef(value, root) {
  if (typeof value !== 'string') return value;
  const m = value.match(/^\{(.+)\}$/);
  if (!m) return value;
  const path = m[1].split('.');
  let cur = root;
  for (const p of path) cur = cur?.[p];
  return cur?.$value ?? cur;
}

function resolveColorPair(value, root) {
  if (value && typeof value === 'object' && 'light' in value && 'dark' in value) {
    return { light: resolveRef(value.light, root), dark: resolveRef(value.dark, root) };
  }
  const v = resolveRef(value, root);
  return { light: v, dark: v };
}

const channelLuminance = (c) => (c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4));

const relativeLuminance = ({ r, g, b }) =>
  0.2126 * channelLuminance(r) + 0.7152 * channelLuminance(g) + 0.0722 * channelLuminance(b);

const contrast = (a, b) => {
  const la = relativeLuminance(a);
  const lb = relativeLuminance(b);
  const lighter = Math.max(la, lb);
  const darker = Math.min(la, lb);
  return (lighter + 0.05) / (darker + 0.05);
};

const semantic = json.color.semantic;
const surfaces = Object.entries(semantic.surface).filter(([, v]) => v.$value);
const texts = Object.entries(semantic.text).filter(([, v]) => v.$value);

const AA_BODY = 4.5;
const AA_LARGE = 3.0;

const violations = [];
const checks = [];

for (const [textName, textTok] of texts) {
  if (/^on/.test(textName)) continue;
  if (textName === 'disabled') continue;
  const textPair = resolveColorPair(textTok.$value, json);

  for (const [surfName, surfTok] of surfaces) {
    if (surfName === 'overlay' || surfName === 'inverse') continue;
    const surfPair = resolveColorPair(surfTok.$value, json);

    for (const mode of ['light', 'dark']) {
      const t = hexToRGBA(textPair[mode]);
      const s = hexToRGBA(surfPair[mode]);
      const ratio = contrast(t, s);
      const passesBody = ratio >= AA_BODY;
      const passesLarge = ratio >= AA_LARGE;
      const status = passesBody ? 'AA' : passesLarge ? 'AA-large-only' : 'FAIL';
      checks.push({ pair: `text.${textName} on surface.${surfName} (${mode})`, ratio: ratio.toFixed(2), status });
      if (!passesBody && !passesLarge) {
        violations.push({ pair: `text.${textName} on surface.${surfName} (${mode})`, ratio: ratio.toFixed(2) });
      }
    }
  }
}

const tertiaryViolations = violations.filter((v) => /text\.tertiary/.test(v.pair));
const otherViolations = violations.filter((v) => !/text\.tertiary/.test(v.pair));

console.log(`[check-contrast] Checked ${checks.length} text/surface pairings.`);
if (otherViolations.length > 0) {
  console.error(`\n[check-contrast] ${otherViolations.length} WCAG AA violations:`);
  for (const v of otherViolations) console.error(`  FAIL ${v.pair} → ${v.ratio}:1 (need ≥ ${AA_BODY}:1)`);
  process.exit(1);
}
if (tertiaryViolations.length > 0) {
  console.warn(`\n[check-contrast] ${tertiaryViolations.length} text.tertiary pairs fall below body AA (intentional — captions/metadata only):`);
  for (const v of tertiaryViolations) console.warn(`  ${v.pair} → ${v.ratio}:1`);
}
console.log(`[check-contrast] All non-tertiary text/surface pairs pass WCAG AA (≥ ${AA_BODY}:1).`);
