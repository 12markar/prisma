# Fonts

## Display & UI — Instrument Sans

- **Family:** `Instrument Sans`
- **Weights used:** 400 (Regular), 500 (Medium), 600 (Semibold), 700 (Bold)
- **License:** SIL Open Font License 1.1
- **Google Fonts URL:** https://fonts.google.com/specimen/Instrument+Sans
- **CSS import:**
  ```css
  @import url('https://fonts.googleapis.com/css2?family=Instrument+Sans:ital,wght@0,400..700;1,400..700&display=swap');
  ```
- **iOS:** download `.ttf` files, add to `Info.plist` under `UIAppFonts`. Reference as `Font.custom("InstrumentSans-Regular", size: …)`.
- **Android:** drop `.ttf` files under `app/src/main/res/font/`. Reference as `FontFamily(Font(R.font.instrument_sans_regular))`.

## Code & technical — JetBrains Mono

- **Family:** `JetBrains Mono`
- **Weights used:** 400 (Regular), 500 (Medium), 700 (Bold)
- **License:** SIL Open Font License 1.1
- **Google Fonts URL:** https://fonts.google.com/specimen/JetBrains+Mono
- **CSS import:**
  ```css
  @import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:ital,wght@0,400..700;1,400..700&display=swap');
  ```
- **iOS / Android:** same procedure as Instrument Sans.

## Pairing rationale

Both faces share a **squared geometric construction** but differ in proportion: Instrument Sans is humanist (varied widths, generous counters), JetBrains Mono is engineered (fixed cell, tall x-height). That tension is deliberate — prose vs. data — and prevents the two from blurring into a single voice.

Use **Instrument Sans for everything UI** (display, headlines, titles, body, labels). Use **JetBrains Mono only for code, identifiers, raw values, and tabular numbers**. Never mix the two within a single sentence of running prose.
