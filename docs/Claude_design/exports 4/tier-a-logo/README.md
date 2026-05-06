# Prisma — Logo / Wordmark / Favicon

## Concept
The mark is built from a **prism** — a triangular geometry refracting white into the violet→magenta gradient. Three planes converge at a centerpoint, suggesting both refraction and the system's three layers (tokens · components · platforms).

## Files
| File | Use |
|---|---|
| `logo-mark.svg` | Square mark (icon-only). Use for app icons, favicon, social avatars. |
| `logo-mark-mono.svg` | Monochrome version — uses `currentColor`. Use on busy backgrounds. |
| `wordmark.svg` | "Prisma" wordmark with the mark. Use in headers, docs, README. |
| `wordmark-mono.svg` | Monochrome wordmark. |
| `favicon.svg` | 32×32 optimized favicon (SVG, scales). |
| `favicon-32.png` | 32×32 raster fallback for older clients. |
| `og-image.svg` | 1200×630 social preview. |

## Spacing rules
- **Clear space:** ½ × the mark's height on all sides — no other elements may enter this zone.
- **Minimum size:** 16px (favicon), 24px (UI), 80px wide for the wordmark.
- **Backgrounds:** prefers `--surface-base` (warm-neutral). On photos, use the mono variant inside a contrasting plate.

## Typography
The wordmark uses **Instrument Sans 600** at -2.5% letter-spacing, hand-tuned for the i/s ligature feel. Don't recreate from the live font — use the SVG outline.

## Color
- Primary gradient: `#7651F5 → #E03088` (violet → magenta), 120deg.
- Solid alt: `#5E36DC` (accent-600).
- Mono: `currentColor`.

## Don'ts
- Do not rotate the mark.
- Do not change the gradient angle or stops.
- Do not place the mark inside another shape (no badges, no chips). It already has internal geometry.
- Do not stretch, recolor outside palette, or apply drop shadows to the wordmark.
