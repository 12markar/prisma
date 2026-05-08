#!/usr/bin/env node
import { copyFileSync, readFileSync, existsSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const SAMPLES   = resolve(__dirname, '..', 'samples');
const LAYOUT    = resolve(__dirname, '..', 'layout.json');

// Each layout has multiple variants (a, b, …) — same screen identity, different
// content / ordering / copy. Useful for live demos: `npm run set 1a` → paywall
// variant A, `npm run set 1b` → variant B, both apps re-render simultaneously.
//
// Fixed groups so the "1", "2", … numbering is stable. Within a group, the
// variant letter is the suffix on the filename.
const GROUPS = [
    ['paywall-a',    'paywall-b'],
    ['onboarding-a', 'onboarding-b'],
    ['feed-a',       'feed-b'],
    ['profile-a',    'profile-b'],
    ['promo-a',      'promo-b'],
];
const FLAT = GROUPS.flat();

const args = process.argv.slice(2);
const subcommand = args[0];

if (subcommand === undefined || subcommand === '--list' || subcommand === '-l') {
    const current = readCurrent();
    console.log('Available samples:');
    GROUPS.forEach((group, i) => {
        group.forEach((name, j) => {
            const id = `${i + 1}${'abcdefgh'[j]}`;
            const marker = name === current ? ' ←' : '';
            console.log(`  ${id}. ${name}${marker}`);
        });
    });
    console.log('\nUsage:');
    console.log('  npm run set <id|name>   # 1a, 1b, 2a, paywall-a, …');
    console.log('  npm run set 1           # shorthand for 1a');
    console.log('  npm run next            # cycle to the next variant');
    process.exit(0);
}

let target;

if (subcommand === '--next' || subcommand === 'next') {
    const current = readCurrent();
    const idx = current ? FLAT.indexOf(current) : -1;
    target = FLAT[(idx + 1) % FLAT.length];
} else {
    const m = /^([0-9]+)([a-z]?)$/.exec(subcommand);
    if (m) {
        const groupIdx = parseInt(m[1], 10) - 1;
        const variantIdx = m[2] ? m[2].charCodeAt(0) - 97 : 0;
        if (groupIdx < 0 || groupIdx >= GROUPS.length) {
            console.error(`Group out of range. Pick 1–${GROUPS.length}.`);
            process.exit(1);
        }
        const group = GROUPS[groupIdx];
        if (variantIdx < 0 || variantIdx >= group.length) {
            console.error(`Variant out of range for group ${groupIdx + 1}. Pick a–${'abcdefgh'[group.length - 1]}.`);
            process.exit(1);
        }
        target = group[variantIdx];
    } else {
        target = subcommand;
    }
}

const src = resolve(SAMPLES, `${target}.json`);
if (!existsSync(src)) {
    console.error(`Sample "${target}" not found. Available: ${FLAT.join(', ')}`);
    process.exit(1);
}

copyFileSync(src, LAYOUT);
const id = idFor(target);
console.log(`✓ layout.json ← ${id}. ${target} — running server will broadcast immediately`);

function idFor(name) {
    for (let i = 0; i < GROUPS.length; i++) {
        const j = GROUPS[i].indexOf(name);
        if (j >= 0) return `${i + 1}${'abcdefgh'[j]}`;
    }
    return '?';
}

// Try to identify the currently active sample by content match. Cheap and
// robust enough for ten small files; avoids a sidecar pointer file.
function readCurrent() {
    if (!existsSync(LAYOUT)) return null;
    const layoutText = readFileSync(LAYOUT, 'utf8');
    for (const name of FLAT) {
        const path = resolve(SAMPLES, `${name}.json`);
        if (existsSync(path) && readFileSync(path, 'utf8') === layoutText) return name;
    }
    return null;
}
