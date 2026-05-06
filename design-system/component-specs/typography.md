# Typography

The type system. See `tokens/typography.json` for canonical values.

## Scale

| Token              | Size / Line   | Family            | Weight | Use                                        |
|--------------------|---------------|-------------------|--------|--------------------------------------------|
| `display.lg`       | 56 / 1.05     | Instrument Sans   | 600    | Hero, marketing-only                       |
| `display.md`       | 44 / 1.08     | Instrument Sans   | 600    | Major page heroes                          |
| `display.sm`       | 36 / 1.10     | Instrument Sans   | 600    | Sub-hero                                   |
| `headline.lg`      | 30 / 1.20     | Instrument Sans   | 600    | Page H1                                    |
| `headline.md`      | 26 / 1.22     | Instrument Sans   | 600    | Section H2                                 |
| `headline.sm`      | 22 / 1.25     | Instrument Sans   | 600    | Sub-section H3                             |
| `title.lg`         | 20 / 1.30     | Instrument Sans   | 500    | Card titles, modal titles                  |
| `title.md`         | 17 / 1.35     | Instrument Sans   | 500    | List row primary, sheet title              |
| `title.sm`         | 15 / 1.40     | Instrument Sans   | 500    | Inline titles, emphasised body             |
| `body.lg`          | 17 / 1.55     | Instrument Sans   | 400    | Reading-density body                       |
| `body.md`          | 15 / 1.55     | Instrument Sans   | 400    | Default body                               |
| `body.sm`          | 13 / 1.50     | Instrument Sans   | 400    | Captions, helpers                          |
| `label.lg`         | 15 / 1.30     | Instrument Sans   | 500    | Button labels (md/lg)                      |
| `label.md`         | 13 / 1.30     | Instrument Sans   | 500    | Form labels, chips                         |
| `label.sm`         | 11 / 1.30     | Instrument Sans   | 500    | Overlines, all-caps eyebrow                |
| `code.md`          | 14 / 1.50     | JetBrains Mono    | 400    | Code blocks                                |
| `code.sm`          | 12 / 1.50     | JetBrains Mono    | 400    | Inline code, tabular numbers, IDs          |

## Dynamic Type / scaling

- **iOS:** map each token to a `UIFont.TextStyle`. `body.md` → `.body`, `title.lg` → `.title3`, etc. Honor user's preferred content size by scaling token sizes via the closest TextStyle's metrics. **Cap** at AccessibilityLarge for display tokens to prevent clipping.
- **Android:** declare `fontSize` in `sp`, not `dp` — Compose handles user font scale automatically. Test with system font scale up to 200%.
- **Web showcase:** sizes in `px`; respect `prefers-reduced-motion` for layout shifts on scale changes.

## Color pairings

All sizes pair with `text.primary` by default. Allowed alternates:

- `text.secondary` for de-emphasised supporting copy
- `text.tertiary` for `body.sm` / `label.sm` only (captions, metadata)
- `text.disabled` for disabled UI exclusively
- `text.link` underlined or with hover underline for inline links
- Status colors for inline status text — pair only with `body.sm`, `label.md`, `label.sm`

Never pair body or label with `accent.default` for emphasis — use weight or `headline.sm` instead.

## Accessibility

- Body text contrast ≥ 4.5:1. Display ≥ 3:1.
- Line length: aim for 50–75 chars.
- Don't justify body text. Left-align (LTR) / right-align (RTL).
- Avoid all-caps for >3 words — kills readability and screen-reader pronunciation.
