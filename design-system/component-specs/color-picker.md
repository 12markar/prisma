# Color Picker

A surface for selecting a color. Three layers of fidelity — swatch grid, full picker (HSL), and direct hex/RGB input — exposed based on user intent.

## Anatomy
```
┌─────────────────────────────────────┐
│ ┌─────────────────────────────────┐ │
│ │                                 │ │  ← Saturation/Value pad
│ │              ◯                  │ │
│ │                                 │ │
│ └─────────────────────────────────┘ │
│ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │  ← Hue rail
│ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │  ← Alpha rail (optional)
│ HEX [#7651F5]   R [118] G [81] B[245] │  ← Numeric inputs
│ ┌──┐┌──┐┌──┐┌──┐┌──┐┌──┐┌──┐┌──┐    │  ← Swatch grid
└─────────────────────────────────────┘
```
- **Frame**: `--surface-raised`, 1px `--border-subtle`, `--radius-lg`, `--shadow-4`. Width 280px.
- **SV pad**: 240×160. Top-left = saturation 0/value 100; top-right = saturation 100/value 100; bottom = value 0. Selector is a 16px circle with 2px white border + 1px black inner border (visible on any background).
- **Hue rail**: 240×12, full hue spectrum. Thumb same as slider thumb spec.
- **Alpha rail**: 240×12, checker background showing transparency. Thumb same shape.
- **Numeric inputs**: 4 cells (HEX | R | G | B) or (HEX | H | S | L) toggleable. `--font-mono` 12px.
- **Swatch grid**: 8 columns × 2 rows of 24×24 squares with `--radius-sm` (4px). Recent + curated.

## Layers (fidelity tiers)

### 1. Swatch-only (default for "tweak" controls)
- Just the 8×2 swatch grid. Use when the design system curates the palette and the user shouldn't go off-system.
- Click selects, no other UI shown.
- Active swatch: 2px `--text-primary` ring, 2px offset.

### 2. Picker (default for "user content" controls)
- SV pad + hue rail + hex input + swatch grid as recents.
- Alpha rail hidden unless `allowAlpha: true`.

### 3. Full (advanced)
- Adds RGB/HSL toggle, eyedropper button (uses `EyeDropper` API on supported browsers), gradient editor (out of scope here).

## States
- **Default**.
- **Dragging on SV pad**: cursor stays as `crosshair`, the selector follows pointer with no transition.
- **Dragging on rails**: cursor `grabbing`. Same model as slider press state — selector scales 1.2×.
- **Hex input invalid**: ring `--danger-default`, value reverts on blur if not a valid 3/6/8-digit hex.
- **Focus-visible** on any input: 2px `--border-focus` ring with 2px offset.

## Eyedropper
- Button at the right of the hex input. Icon = `eye`.
- On click, calls `EyeDropper.open()` (Chrome/Edge); on unsupported browsers, button is hidden — never disabled with a tooltip.

## Motion
- SV pad selector: no transition during drag (snappy = accurate). Animates on programmatic changes only.
- Hue rail: same.
- Swatch select: 120ms ring fade-in.
- Layer expansion (swatch → picker): height transitions 200ms, content fades 100ms after to avoid layout-shift flicker.

## Accessibility
- SV pad: `role="application"` with `aria-label="Saturation and value"`. Arrow keys move 1%, ⇧Arrow 10%.
- Hue / alpha rails: standard `slider` semantics.
- Hex input: `role="textbox"`, validates pattern.
- Always show the selected color **as text** (hex value) — color alone is not accessible.
- Color blindness: never rely on color names for state. The hex value is the canonical identity.
- Contrast warning: when alpha < 100% or color is too low-contrast against the surface where it'll be used (if known via prop), show an inline warning chip with the contrast ratio.

## Tokens
| Property | Token |
|---|---|
| Frame bg | `--surface-raised` |
| Rail track | (rendered, gradient-based) |
| Selector outline | `#FFFFFF` + `#000000` (1px each) |
| Active swatch ring | `--text-primary` |
| Invalid input | `--danger-default` |

## Don'ts
- Don't show the picker without the hex input. Power users always want direct entry.
- Don't auto-apply the color while the picker is open — wait for explicit confirm OR provide a clear "Apply" button. Live-applying disorients users who are exploring.
- Don't force HSL when users typed a hex. Auto-detect input format from what they entered.
- Don't color the picker frame itself based on the selected color. The frame stays neutral; only a small preview chip changes.
