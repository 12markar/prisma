# Prisma — Icon Set

A canonical icon library for the Prisma Design System. **64 icons** delivered.

## Style spec
- **Grid:** 24×24 px, 2px live padding (icons fit in 20×20 visual bounds)
- **Stroke:** 1.75px, rounded line caps + joins
- **Corner radius:** 2px on outer geometry, 1px on inner detail
- **Fill rule:** Outline-only (no filled variants in v1)
- **Optical sizing:** Tested at 16, 20, 24, 32 px
- **Color:** Inherits `currentColor` — never bake fill colors

## Categories
| Category | Count | Examples |
|---|---|---|
| Navigation | 8 | arrow-up, arrow-down, arrow-left, arrow-right, chevron-*, back |
| Actions | 12 | plus, minus, edit, trash, copy, share, download, upload, save, refresh, more, close |
| Status | 8 | check, x, info, warning, error, success, help, alert |
| Objects | 12 | folder, file, image, video, doc, archive, link, tag, bookmark, calendar, clock, bell |
| Catalogue | 10 | grid, list, sort, filter, search, scan, qr, barcode, label, layers |
| Social | 8 | user, users, heart, star, message, mail, phone, location |
| System | 6 | settings, lock, unlock, eye, eye-off, sync |

## Usage
```html
<svg class="icon"><use href="prisma-icons.svg#plus"/></svg>
```
```kotlin
Icon(painter = painterResource(R.drawable.ic_prisma_plus), contentDescription = "Add")
```
```swift
Image(systemName: "prisma.plus")
```

## Files
- `prisma-icons.svg` — sprite sheet (all 64 icons, `<symbol>` per icon)
- `index.html` — visual index with search + size testing
- `individual/*.svg` — one file per icon for build pipelines
