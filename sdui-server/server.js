import { readFileSync, existsSync, copyFileSync, readdirSync } from 'node:fs';
import { dirname, resolve, basename, extname } from 'node:path';
import { fileURLToPath } from 'node:url';
import { createServer } from 'node:http';

import express from 'express';
import { WebSocketServer } from 'ws';
import chokidar from 'chokidar';
import Ajv from 'ajv/dist/2020.js';
import addFormats from 'ajv-formats';

const __dirname  = dirname(fileURLToPath(import.meta.url));
const PORT       = 7331;
const LAYOUT     = resolve(__dirname, 'layout.json');
const SAMPLES    = resolve(__dirname, 'samples');
const SCHEMA     = resolve(__dirname, 'schema', 'sdui.schema.json');

// Seed layout.json from the paywall sample on first boot so the server
// always has something to broadcast.
if (!existsSync(LAYOUT)) {
    const seed = resolve(SAMPLES, 'paywall.json');
    copyFileSync(seed, LAYOUT);
    console.log(`[sdui] seeded layout.json from ${basename(seed)}`);
}

const ajv = new Ajv({ allErrors: true, strict: false });
addFormats(ajv);
const validate = ajv.compile(JSON.parse(readFileSync(SCHEMA, 'utf8')));

let currentDoc = null;
let currentRaw = null;

function loadLayout() {
    try {
        const raw  = readFileSync(LAYOUT, 'utf8');
        const json = JSON.parse(raw);
        if (!validate(json)) {
            console.warn('[sdui] schema errors:', ajv.errorsText(validate.errors));
            return { ok: false, errors: validate.errors, raw };
        }
        return { ok: true, doc: json, raw };
    } catch (err) {
        console.warn('[sdui] parse error:', err.message);
        return { ok: false, errors: [{ message: err.message }], raw: null };
    }
}

function refresh(reason) {
    const result = loadLayout();
    if (result.ok) {
        currentDoc = result.doc;
        currentRaw = result.raw;
        broadcast({ kind: 'layout', doc: currentDoc });
        console.log(`[sdui] ${reason} → broadcast (${wss.clients.size} client${wss.clients.size === 1 ? '' : 's'})`);
    } else {
        broadcast({ kind: 'error', errors: result.errors });
        console.log(`[sdui] ${reason} → invalid, sent error frame`);
    }
}

function broadcast(payload) {
    const msg = JSON.stringify(payload);
    for (const client of wss.clients) {
        if (client.readyState === client.OPEN) client.send(msg);
    }
}

// HTTP — small status page + raw layout fetch.
const app = express();
app.get('/', (_req, res) => {
    const samples = readdirSync(SAMPLES)
        .filter(f => extname(f) === '.json')
        .map(f => basename(f, '.json'));
    res.type('html').send(`<!doctype html>
<meta charset="utf-8">
<title>Prisma SDUI dev server</title>
<style>
 body { font: 14px/1.5 -apple-system, system-ui, sans-serif; max-width: 640px; margin: 40px auto; padding: 0 20px; color: #1a1a1a; }
 code { background: #f4f4f4; padding: 2px 6px; border-radius: 4px; font-size: 13px; }
 pre  { background: #f4f4f4; padding: 12px; border-radius: 8px; overflow: auto; font-size: 12px; }
 ul   { padding-left: 20px; }
 .pill { display: inline-block; padding: 3px 10px; border-radius: 12px; background: #e8f3ee; color: #157347; font-size: 12px; font-weight: 600; }
</style>
<h1>Prisma SDUI dev server <span class="pill">running</span></h1>
<p>WebSocket: <code>ws://localhost:${PORT}/ws</code></p>
<p>HTTP layout: <code>GET /layout</code></p>
<p>Samples available:</p>
<ul>${samples.map(s => `<li><code>${s}</code></li>`).join('')}</ul>
<p>Switch with <code>npm run set ${samples[0] ?? 'paywall'}</code> or edit <code>layout.json</code> directly — both apps will hot-reload.</p>
`);
});
app.get('/layout', (_req, res) => {
    if (!currentDoc) return res.status(503).type('text').send('no layout loaded');
    res.json(currentDoc);
});

const server = createServer(app);
const wss    = new WebSocketServer({ server, path: '/ws' });

wss.on('connection', (socket, req) => {
    console.log(`[sdui] client connected (${req.socket.remoteAddress})`);
    if (currentDoc) {
        socket.send(JSON.stringify({ kind: 'layout', doc: currentDoc }));
    }
    socket.on('close', () => console.log('[sdui] client disconnected'));
});

// Watch layout.json with a 100ms debounce so a Cmd+S that triggers two
// events (rename + add on some editors) collapses into one broadcast.
let debounce;
chokidar.watch(LAYOUT, { ignoreInitial: false }).on('all', (event) => {
    clearTimeout(debounce);
    debounce = setTimeout(() => refresh(`fs.${event}`), 100);
});

server.listen(PORT, () => {
    console.log(`[sdui] http://localhost:${PORT}  ws://localhost:${PORT}/ws`);
});
