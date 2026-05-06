# Prisma — Empty-State Illustrations

## Style spec
- **Construction:** geometric outline shapes + a single soft gradient wash (violet→magenta).
- **Stroke:** 1.5px solid (`--text-secondary`), 1px dashed accents for grid/floor lines.
- **Fill:** never flat — only gradient fills at 14–22% opacity for atmospheric depth.
- **Composition:** 320×240 viewBox, subject occupies center 60%, with 2–3 floating geometric ornaments for visual rhythm.
- **Tone:** quietly optimistic. No mascots, no emoji, no faces. The product is the subject.

## Set (8 illustrations)
| ID | Use case | Tagline |
|---|---|---|
| `empty-catalogue.svg` | First-launch / no items yet | "Nothing here yet." |
| `empty-search.svg` | Search returned no results | "No matches." |
| `empty-favorites.svg` | Saved/bookmarked is empty | "Save items to find them later." |
| `empty-archive.svg` | Archive is empty | "Archive is clear." |
| `empty-network.svg` | Offline / connection lost | "We can't reach the server." |
| `empty-error.svg` | Generic error fallback | "Something went sideways." |
| `empty-permissions.svg` | Permission denied | "We need camera access to scan." |
| `empty-collaboration.svg` | No teammates yet | "Invite someone to start." |

## Pairing
Always pair with a **headline** (`title.lg`), one **body** line, and ideally **one primary action**. The illustration is never decoration — it earns its place by acknowledging the user's state.

```
[ Illustration · 240px high ]
"Nothing here yet."         <- headline
"Add your first item to get started."  <- body
[ + Add item ]              <- primary CTA
```

## Files
Each illustration is a single self-contained SVG at 320×240, using `currentColor` where possible so they tint with text color. Drop them into the `web-showcase/empty-states/` folder of any platform target.
