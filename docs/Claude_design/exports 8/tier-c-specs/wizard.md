# Wizard (multi-step flow)

A linear or branching multi-step process with persistent progress indicator and per-step validation. Distinguished from a stepper-input: a wizard is a *page-level pattern* for completing a task in chunks, not a control.

## Anatomy
```
┌──────────────────────────────────────────────┐
│  ●─────●─────●─────○─────○                   │  ← Step rail
│  ✓     ✓    ▸ 3   4     5                    │
│  Done  Done   Now  Next  Last                │
├──────────────────────────────────────────────┤
│                                              │
│  Step 3: Set permissions                     │  ← Step body
│  ────────                                    │
│  …                                           │
│                                              │
├──────────────────────────────────────────────┤
│  ‹ Back                  Save & continue ›   │  ← Footer
└──────────────────────────────────────────────┘
```

## Step rail
- **Layout**: horizontal at desktop (≥720px), vertical at mobile.
- **Step circle**: 28×28, `--radius-pill`. States:
  - **Complete**: `--accent-default` fill, `--on-accent` checkmark icon.
  - **Current**: 2px `--accent-default` ring, transparent fill, `--accent-default` number text.
  - **Upcoming**: 1px `--border-default` ring, transparent fill, `--text-tertiary` number text.
  - **Error** (validation failed for that step): `--danger-default` fill, white "!" icon.
- **Connector line**: between circles. 2px tall horizontal (or 2px wide vertical). `--accent-default` for completed segments, `--border-subtle` for upcoming.
- **Step label**: below circle (or right of, vertical). `--font-mono` 11px uppercase for "STEP 3" identifier, `--font-sans` 13px for the title. Current step's title is bold.

## Step types
- **Required** (default).
- **Optional**: marked with "(optional)" in the title. Skip button in footer.
- **Branching**: the next step depends on this step's choice. Render branches as a tree only when the user has chosen — speculative branches stay collapsed.
- **Review**: the final step shows a summary of all prior selections with "Edit" links per section. Always read-only at first; clicking Edit jumps to that step with state preserved.

## Footer
- **Back button**: secondary style. Disabled on step 1.
- **Skip button**: ghost style, only on optional steps. To the right of Back.
- **Primary button**: right-aligned. Label changes per step:
  - Default: `Continue`.
  - Last step: `Submit` / `Finish` / domain-specific verb.
  - With validation pending: `Continue` with a small spinner inline.
- **Save & exit** (optional): secondary text link, far right of footer, for long flows where users may want to resume later.

## Persistence & resume
For flows ≥4 steps:
- Auto-save state on each step transition.
- "Save & exit" returns user to entry point.
- On re-entry, show a banner: "Resume your previous progress? [ Resume ] [ Start over ]".
- Wizard state has TTL (e.g. 7 days) — show TTL in the resume banner: "Saved 2 days ago".

## Validation
- **Per-step**: validate on Continue. Errors appear inline next to fields and as a summary at top of step body ("3 issues to fix") on first failure.
- **Cross-step**: only validate at Submit. Surface as an error step indicator on the rail with a click-to-jump.
- Don't allow forward navigation past an invalid step. Allow backward navigation always.

## Motion
- Step transition: 240ms `--ease-emphasized`. Outgoing step body translates -16px and fades; incoming translates from +16px and fades. The rail's progress fills with a 200ms width transition over the connector segment.
- Step circle "tick" animation: when transitioning a step from current → complete, the checkmark draws in via SVG `stroke-dashoffset` over 280ms, with a 1.05× scale-pop on the circle.
- Error shake: invalid Continue triggers a 220ms shake on the primary button (translate ±4px, 3 cycles).

## Variants
- **Linear**: classic, no jumping ahead. Default.
- **Free-roam**: user can click any step in the rail at any time. Use for forms where step order doesn't matter.
- **Vertical**: rail on the left, body on the right. For very long flows or when step titles are long.

## Accessibility
- The rail: `<ol>` with `role="list"`, each step is an `<li>` containing a `<button>` (or non-interactive in linear-only mode).
- Step buttons: `aria-current="step"` on the current step, `aria-disabled` on upcoming steps in linear mode.
- Live region: announces "Step 3 of 5: Set permissions" on transition.
- Footer primary: when in loading state, set `aria-busy="true"` and disable the button.
- Esc inside a wizard: ask to confirm exit if user has unsaved progress.

## Tokens
| Property | Token |
|---|---|
| Complete fill | `--accent-default` |
| Current ring | `--accent-default` |
| Upcoming ring | `--border-default` |
| Connector complete | `--accent-default` |
| Connector upcoming | `--border-subtle` |
| Error fill | `--danger-default` |

## Don'ts
- Don't show ≥7 steps. Group into phases or break the flow into separate wizards.
- Don't hide the rail to "save space". The rail is the orientation contract — without it the wizard is just a series of pages.
- Don't auto-advance on field completion in non-trivial flows. The user wants to review their answers before continuing.
- Don't lose state on browser back. Hook into history; treat back as "previous step", not "exit wizard".
