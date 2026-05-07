# Search Bar

The primary search affordance for an app. Sits in the header or as a hero element. Distinguished from autocomplete (which is a generic pattern) by being **opinionated** about its placement, scope, and result presentation.

## Anatomy
```
┌───────────────────────────────────────────────┐
│ 🔍  Search your library…              ⌘K  │  ⌫ │
└───────────────────────────────────────────────┘
```
- **Frame**: 40px tall (default), 48px (hero), 36px (compact). `--surface-sunken` background, 1px `--border-subtle` (default state) or no border + the surface fill alone (header variant). `--radius-lg` (12px) for hero, `--radius-md` for header/compact.
- **Search icon**: 18px, `--text-tertiary`, 12px from left edge.
- **Input**: `--font-sans` 15px (16px on mobile to prevent iOS zoom), `--text-primary`. Placeholder at `--text-tertiary`.
- **Trailing chips** (optional): keyboard hint chip (`⌘K`) when bar acts as a launcher; clear button (`⌫`) when input has text. Both 24px tall, separated by 4px.

## Variants

### 1. Header bar
- Lives in app chrome. Always visible.
- 40px tall, `--surface-sunken` background, no border.
- On focus: expands inline (no overlay). Suggestion list appears below as a popover (see autocomplete spec).

### 2. Hero search
- Center stage on a search-first surface (homepage, dashboard).
- 56px tall, `--surface-raised`, 1px `--border-default`, `--shadow-2`.
- On focus: shadow deepens to `--shadow-3`, border to `--border-focus`.
- Suggestions appear in a panel below, full-width with a 4px gap.

### 3. Launcher (cmd+K)
- A search bar that, when focused or when ⌘K pressed, opens a command palette overlay (see command-palette.md). The bar itself is a trigger; the actual search lives in the palette.
- Show the `⌘K` keyboard chip on the right at all times.

### 4. In-page filter
- Lives above a list it filters. 36px tall, ghost border. Results filter live; no popover.
- Has a result count chip on the right when filtering: `42 of 1,283`.

## States
- **Default**.
- **Hover**: border `--border-default`, cursor text.
- **Focus**: 2px `--border-focus` ring, 2px offset.
- **Active typing**: clear button visible, suggestions popover open (see autocomplete spec for popover behavior).
- **Loading** (async results): a 16px spinner replaces the search icon for the duration. Don't shift layout — same hit area.
- **Disabled**: 38% opacity.

## Scope chips (optional)
For multi-scope search ("Items / People / Files"):
- Below or to the left of the input, a row of 24px segmented-control chips selecting the search scope.
- Active scope shapes the placeholder text: "Search items…" → "Search people…".
- Tab key in the input cycles through scopes when input is empty.

## Recents & saved searches
- On focus with empty input: show a popover with recent queries (max 5) and saved searches (if user has any).
- Recent rows: 32px tall, leading clock icon, dismiss-x on hover.
- Saved searches: bookmark icon leading, count badge trailing.

## Voice & camera input (mobile)
- Trailing icon button (microphone) when device supports speech recognition.
- After voice icon, optional camera/scan icon for visual search if supported.
- Both are hidden on desktop and on devices where APIs aren't available.

## Motion
- Focus expansion (header variant when expanding inline): 200ms `--ease-emphasized` width transition.
- Suggestion popover: 160ms fade + 4px slide.
- Shake on no results: 220ms shake (translate ±3px, 3 cycles) on the input border — only on explicit submit (Enter), not on each keystroke.
- Clear button: instant clear, no animation. Animation here feels like lag.

## Accessibility
- The input is `role="searchbox"` (or `role="combobox"` if it has suggestions).
- `aria-label="Search your library"` (or use `<label>`).
- Submit on Enter triggers a real navigation to a search results page (header/hero variants), or applies the filter (in-page).
- Clear button has `aria-label="Clear search"`.
- Live region for result count: "42 results" debounced 350ms.
- ⌘K chip is `aria-hidden` (decorative) — the keyboard shortcut is registered globally.

## Tokens
| Property | Token |
|---|---|
| Frame bg | `--surface-sunken` (header) · `--surface-raised` (hero) |
| Border | `--border-subtle` / `--border-default` / `--border-focus` |
| Icon | `--text-tertiary` |
| Placeholder | `--text-tertiary` |
| Loading spinner | `--accent-default` |

## Don'ts
- Don't add a "Search" submit button next to the input. Enter is the contract; a button doubles the affordance.
- Don't use a hero search bar AND a header search bar on the same page. Pick one.
- Don't auto-submit on each keystroke for **navigational** searches (Enter required). Auto-submit is for **filter** searches only.
- Don't put helper text below the search bar. Use the placeholder; users don't read paragraphs above the fold.
