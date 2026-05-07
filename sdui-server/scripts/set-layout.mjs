#!/usr/bin/env node
import { copyFileSync, readdirSync, existsSync } from 'node:fs';
import { dirname, resolve, basename, extname } from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const SAMPLES   = resolve(__dirname, '..', 'samples');
const LAYOUT    = resolve(__dirname, '..', 'layout.json');

const args = process.argv.slice(2);

function listSamples() {
    return readdirSync(SAMPLES)
        .filter(f => extname(f) === '.json')
        .map(f => basename(f, '.json'))
        .sort();
}

if (args.length === 0 || args[0] === '--list' || args[0] === '-l') {
    const samples = listSamples();
    console.log('Available samples:');
    for (const s of samples) console.log(`  - ${s}`);
    console.log('\nUsage: npm run set <name>');
    process.exit(0);
}

const name = args[0];
const src  = resolve(SAMPLES, `${name}.json`);
if (!existsSync(src)) {
    console.error(`Sample "${name}" not found. Available: ${listSamples().join(', ')}`);
    process.exit(1);
}

copyFileSync(src, LAYOUT);
console.log(`✓ layout.json ← samples/${name}.json — running server will broadcast immediately`);
