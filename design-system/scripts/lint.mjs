// Token JSON schema lint — verifies W3C DTCG shape and naming conventions.
//
// Checks per token leaf:
//   - has $value
//   - has $type (or is inherited)
//   - $type is one of the known DTCG types we emit
//   - colour values look parseable (hex / rgba / reference / dual-mode object)
//   - no orphan references
//
// Run: npm run lint-tokens

import { readFileSync, readdirSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const TOKENS_DIR = join(__dirname, '..', 'tokens');

const KNOWN_TYPES = new Set([
  'color',
  'dimension',
  'duration',
  'cubicBezier',
  'fontFamily',
  'fontWeight',
  'typography',
  'shadow',
  'number',
  'string',
]);

const errors = [];
const warnings = [];

function* walkLeaves(node, path = []) {
  if (node && typeof node === 'object' && '$value' in node) {
    yield [path, node];
    return;
  }
  if (node && typeof node === 'object') {
    for (const [k, v] of Object.entries(node)) {
      if (k.startsWith('$')) continue;
      yield* walkLeaves(v, [...path, k]);
    }
  }
}

function pathStr(p) {
  return p.join('.');
}

function lintToken(file, path, token) {
  const id = `${file}: ${pathStr(path)}`;
  if (!('$value' in token)) errors.push(`${id} — missing $value`);
  if (!token.$type) warnings.push(`${id} — missing $type (could be inherited)`);
  else if (!KNOWN_TYPES.has(token.$type)) errors.push(`${id} — unknown $type "${token.$type}"`);

  if (token.$type === 'color' && typeof token.$value === 'string') {
    const v = token.$value;
    const ok =
      v === 'transparent' ||
      /^#[0-9a-fA-F]{3,8}$/.test(v) ||
      /^rgba?\(/.test(v) ||
      /^\{.+\}$/.test(v);
    if (!ok) errors.push(`${id} — colour value not parseable: ${JSON.stringify(v)}`);
  }
}

const merged = {};
const tokenFiles = readdirSync(TOKENS_DIR).filter((f) => f.endsWith('.json') && f !== 'sd.config.json');
for (const f of tokenFiles) {
  const j = JSON.parse(readFileSync(join(TOKENS_DIR, f), 'utf8'));
  Object.assign(merged, j);
  for (const [path, token] of walkLeaves(j)) lintToken(f, path, token);
}

// Resolve references — verify every {ref} resolves.
function findRefs(value, refs) {
  if (typeof value === 'string') {
    const m = value.match(/^\{(.+)\}$/);
    if (m) refs.push(m[1]);
  } else if (Array.isArray(value)) {
    value.forEach((v) => findRefs(v, refs));
  } else if (value && typeof value === 'object') {
    for (const v of Object.values(value)) findRefs(v, refs);
  }
}

function resolvePath(root, path) {
  let cur = root;
  for (const p of path.split('.')) cur = cur?.[p];
  return cur;
}

for (const f of tokenFiles) {
  const j = JSON.parse(readFileSync(join(TOKENS_DIR, f), 'utf8'));
  for (const [path, token] of walkLeaves(j)) {
    const refs = [];
    findRefs(token.$value, refs);
    for (const ref of refs) {
      const found = resolvePath(merged, ref);
      if (!found || (typeof found === 'object' && !('$value' in found))) {
        errors.push(`${f}: ${pathStr(path)} — unresolved reference {${ref}}`);
      }
    }
  }
}

if (warnings.length) {
  console.warn(`[lint-tokens] ${warnings.length} warnings:`);
  warnings.forEach((w) => console.warn(`  ${w}`));
}
if (errors.length) {
  console.error(`\n[lint-tokens] ${errors.length} errors:`);
  errors.forEach((e) => console.error(`  ${e}`));
  process.exit(1);
}
console.log(`[lint-tokens] OK — ${tokenFiles.length} files, all tokens valid DTCG.`);
