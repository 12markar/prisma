# Empty State

The deliberate UI for "no data yet" — turns a void into a destination. Used for empty searches, fresh accounts, drained feeds.

## Anatomy

```
            ┌──────────────┐
            │              │
            │  [ visual ]  │
            │              │
            └──────────────┘

           No results found

       Try adjusting filters or
       searching for a different term.

           [ Clear filters ]
```

- **Visual slot** — illustration, icon (40+), or large emoji. Optional but strongly recommended. Phase 0 fallback: typographic glyph at 64px in `text.tertiary`.
- **Title** — `headline.sm`, `text.primary`, weight 600. Required. 1 line, max 6 words.
- **Description** — `body.md`, `text.secondary`. Optional. 1–2 sentences, max 20 words. Centered.
- **Action** — primary button (recommended) and/or secondary text-link. Optional. Drives the user to a meaningful next step.

## Layout

- Centered horizontally and vertically within its container.
- Max content width 360 — prevents lines getting too wide on tablets.
- Vertical rhythm — `spacing.5` between visual / title / description / action group.

## Variants

| Variant       | When                                              | Visual treatment                                       |
|---------------|---------------------------------------------------|--------------------------------------------------------|
| `default`     | True empty state — no data has ever existed       | Illustration + descriptive title.                      |
| `no-results`  | Filtered or searched, nothing matches             | Icon (search/filter), title "No results found", action: clear/refine. |
| `error`       | Load failed                                       | Icon (alert), title states the error, action: retry.   |
| `permission`  | Feature requires permission user has not granted  | Icon (lock), action: trigger permission flow.          |

## Sizes

| Size      | Visual size | Title          |
|-----------|-------------|----------------|
| `default` | 80          | `headline.sm`  |
| `compact` | 48          | `title.lg`     |

`compact` for empty states inside cards or smaller surfaces; `default` for full-page empties.

## Tokens used

- `text.primary`, `text.secondary`, `text.tertiary`
- `headline.sm`, `title.lg`, `body.md`
- `spacing.5`
- Button primary and Button secondary (per `button.md`)

## Accessibility

- **Role** — none specifically; the title becomes the heading of the region (`accessibilityAddTraits(.isHeader)` on title).
- **Visual** — decorative `accessibilityHidden`. Title + description carry meaning.
- **Reading order** — visual hidden, title first, description, then action(s). Matches visual top-to-bottom.
- **Action label** — describes the destination, not "click here". E.g. "Clear filters", "Try again".

## Do / Don't

✅ **Do** offer a meaningful next action — the empty state's job is to unstick the user.
✅ **Do** match the empty state's tone to the surrounding product: "No tasks yet — start with one" feels different from "Inbox empty 🎉".
✅ **Do** prefer custom illustrations once the design system grows; geometric / typographic placeholders are Phase 0 fallback.

❌ **Don't** show an empty state with no action when there's a clear next step the user could take.
❌ **Don't** use empty states for loading — that's Skeleton territory.
❌ **Don't** make the action destructive ("Delete account") — empty states should encourage forward motion.
