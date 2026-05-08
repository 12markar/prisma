#!/usr/bin/env node
// Interactive demo helper. Pick a layout, then edit individual text /
// button / badge / list-item labels by number — press Enter to keep the
// existing value (shown in brackets) or type a new one. The running dev
// server picks up the file change and broadcasts immediately.
//
// Usage: npm run demo
import { createInterface } from 'node:readline/promises';
import { stdin as input, stdout as output } from 'node:process';
import { copyFileSync, readFileSync, writeFileSync, existsSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const SAMPLES   = resolve(__dirname, '..', 'samples');
const LAYOUT    = resolve(__dirname, '..', 'layout.json');
const GROUPS = [
    ['paywall-a',    'paywall-b'],
    ['onboarding-a', 'onboarding-b'],
    ['feed-a',       'feed-b'],
    ['profile-a',    'profile-b'],
    ['promo-a',      'promo-b'],
];
const ORDER = GROUPS.flat();

const rl = createInterface({ input, output });

const ask = async (prompt, dflt) => {
    const hint = dflt !== undefined ? ` [${truncate(String(dflt), 60)}]` : '';
    const answer = await rl.question(`${prompt}${hint}: `);
    return answer.trim() === '' ? dflt : answer;
};

const truncate = (s, n) => s.length > n ? s.slice(0, n - 1) + '…' : s;

// 1. Pick a layout
console.log('\nLayouts:');
GROUPS.forEach((group, gi) => {
    group.forEach((name, vi) => {
        console.log(`  ${gi + 1}${'abcdefgh'[vi]}. ${name}`);
    });
});
const pick = (await rl.question('\nPick layout (e.g. 1a, 2b, Enter to keep current): ')).trim();

if (pick !== '') {
    const m = /^([0-9]+)([a-z]?)$/.exec(pick);
    if (!m) {
        console.error(`Invalid pick "${pick}". Use ids like 1a, 2b.`);
        rl.close();
        process.exit(1);
    }
    const gi = parseInt(m[1], 10) - 1;
    const vi = m[2] ? m[2].charCodeAt(0) - 97 : 0;
    if (gi < 0 || gi >= GROUPS.length || vi < 0 || vi >= GROUPS[gi].length) {
        console.error(`Out of range "${pick}".`);
        rl.close();
        process.exit(1);
    }
    const target = GROUPS[gi][vi];
    copyFileSync(resolve(SAMPLES, `${target}.json`), LAYOUT);
    console.log(`✓ switched to ${gi + 1}${'abcdefgh'[vi]}. ${target}\n`);
}

if (!existsSync(LAYOUT)) {
    console.error('layout.json not found — run `npm run set 1` first.');
    rl.close();
    process.exit(1);
}

// 2. Walk the tree, collect every editable label and its current value
const doc = JSON.parse(readFileSync(LAYOUT, 'utf8'));
const fields = [];
collect(doc.root);

if (fields.length === 0) {
    console.log('Nothing editable in this layout.');
    rl.close();
    process.exit(0);
}

// 3. Menu loop — pick a field by number, type a new value, repeat
while (true) {
    console.log('\nEditable fields:');
    fields.forEach((f, i) => {
        console.log(`  ${String(i + 1).padStart(2, ' ')}. ${f.kind.padEnd(16)} ${truncate(f.get(), 70)}`);
    });

    const choice = (await rl.question('\nPick a number to edit, or Enter to save & quit: ')).trim();
    if (choice === '' || choice === 'q' || choice === 'Q') break;

    const i = parseInt(choice, 10);
    if (!Number.isFinite(i) || i < 1 || i > fields.length) {
        console.log(`Pick a number 1–${fields.length}.`);
        continue;
    }
    const f = fields[i - 1];
    const updated = await ask(`New ${f.kind}`, f.get());
    f.set(String(updated));
    console.log(`✓ updated #${i}`);
}

// 4. Write back — chokidar on the running server broadcasts immediately
writeFileSync(LAYOUT, JSON.stringify(doc, null, 2) + '\n');
console.log(`\n✓ saved to layout.json — apps hot-reload now.\n`);
rl.close();

// --- helpers ---

function collect(node) {
    if (!node || typeof node !== 'object') return;
    switch (node.type) {
        case 'text':
            fields.push({ kind: 'text',     get: () => node.value, set: v => { node.value = v; } });
            break;
        case 'button':
            fields.push({ kind: 'button',   get: () => node.label, set: v => { node.label = v; } });
            break;
        case 'badge':
            fields.push({ kind: 'badge',    get: () => node.label, set: v => { node.label = v; } });
            break;
        case 'listItem':
            fields.push({ kind: 'list.title', get: () => node.title, set: v => { node.title = v; } });
            if (typeof node.subtitle === 'string') {
                fields.push({ kind: 'list.subtitle', get: () => node.subtitle, set: v => { node.subtitle = v; } });
            }
            if (typeof node.trailingText === 'string') {
                fields.push({ kind: 'list.trailing', get: () => node.trailingText, set: v => { node.trailingText = v; } });
            }
            break;
    }
    if (Array.isArray(node.children)) node.children.forEach(collect);
    if (Array.isArray(node.items)) node.items.forEach(it => { collect(it.content); });
}
