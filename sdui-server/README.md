# Prisma SDUI dev server

Tiny WebSocket server that broadcasts a JSON UI document to the Prisma catalogue apps. Save `layout.json` and both apps re-render instantly.

## Run

```bash
cd sdui-server
npm install
npm start                 # http://localhost:7331  ·  ws://localhost:7331/ws
```

Open the SDUI screen in either catalogue app — it connects on launch.

## Switch between sample layouts

```bash
npm run list              # see available samples
npm run set paywall       # copy samples/paywall.json → layout.json (server hot-reloads)
npm run set onboarding
npm run set feed
npm run set profile
npm run set promo
```

Or just edit `layout.json` by hand — chokidar watches it and broadcasts on save.

## How it works

```
layout.json ──(chokidar 100ms debounce)──▶ Ajv validate ──▶ wss.broadcast
                                                                  │
                                              ┌───────────────────┼───────────────────┐
                                              ▼                                       ▼
                                  iOS app (URLSessionWebSocket)            Android app (OkHttp WS)
                                              ▼                                       ▼
                                       SwiftUI re-render                   Compose re-render
```

- **Schema**: `schema/sdui.schema.json`. New JSON is validated before broadcast; invalid docs send an `error` frame instead of a `layout` frame so apps don't crash.
- **Wire format**: `{ "kind": "layout", "doc": <document> }` or `{ "kind": "error", "errors": [...] }`.
- **Reconnect**: clients reconnect with backoff. On reconnect the server immediately replays the current document.

## Supported nodes

| Node | Purpose |
|---|---|
| `column` / `row` | Stacks with `spacing`, `padding`, `alignment` |
| `text` | Typed text — `displayLg/Md/Sm`, `headlineLg/Md/Sm`, `titleLg/Md/Sm`, `bodyLg/Md/Sm`, `labelLg/Md/Sm` |
| `image` | Remote URL with `width`, `height`, `cornerRadius`, `contentMode` |
| `button` | `primary` / `secondary` / `outlined` / `ghost` / `destructive`, three sizes, optional `fullWidth` |
| `card` | Padded surface that holds children |
| `tabs` | Top-bar tabs with one node per tab as content |
| `listItem` | Title + subtitle + optional trailing text and chevron |
| `badge` | Small pill — `neutral` / `accent` / `success` / `warning` / `danger` |
| `divider` / `spacer` | Thin rule / vertical gap |

Unknown node types render as a small "Unsupported: X" placeholder — they don't crash the app.

## The 5 demo layouts

| Sample | Style |
|---|---|
| `paywall` | Subscription paywall: hero, feature list, plan tabs, CTA stack |
| `onboarding` | Centered welcome step: illustration, title, step badges, CTA stack |
| `feed` | Article feed: tabs + image-headed cards with read CTAs |
| `profile` | Account / settings: avatar, badges, three list-item cards, destructive sign-out |
| `promo` | Limited-time promo: hero card with badges, price comparison, CTAs, social proof |

## Caveats

- Hardcoded URL on the clients (`ws://localhost:7331/ws` on iOS Simulator and Mac, `ws://10.0.2.2:7331/ws` on Android emulator). Real-device demos need your machine's LAN IP — change the constant.
- iOS needs `NSAllowsLocalNetworking = true` in the catalogue Info.plist (already configured).
- Android needs cleartext for `10.0.2.2` (configured via `network_security_config.xml`).
- `version` is currently `1`. Bump it the day a breaking schema change ships, and have renderers refuse older/newer versions with a friendly message instead of crashing.
