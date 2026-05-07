import SwiftUI
import CoreUI
import Components

private struct H: View {
    let text: String
    @Environment(\.colorScheme) private var scheme
    var body: some View {
        Text(text.uppercased())
            .font(.system(size: 11, weight: .semibold))
            .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
    }
}

// MARK: - Inputs

struct SwitchShowcase: View {
    @State private var checked: Bool = true
    @State private var label: String = "Push notifications"
    @State private var helper: String = "Get alerts when you're mentioned."
    @State private var enabled: Bool = true
    @State private var withLabel: Bool = true

    var body: some View {
        PlaygroundScaffold(
            preview: {
                PrismaSwitch(
                    checked: $checked,
                    label: withLabel && !label.isEmpty ? label : nil,
                    helperText: withLabel && !helper.isEmpty ? helper : nil,
                    enabled: enabled
                )
            },
            knobs: {
                BoolKnobRow(label: "Checked", value: $checked)
                BoolKnobRow(label: "Enabled", value: $enabled)
                BoolKnobRow(label: "With label / helper", value: $withLabel)
                StringKnobRow(label: "Label", value: $label)
                StringKnobRow(label: "Helper", value: $helper)
            },
            code: {
                var s = "PrismaSwitch(checked: $checked"
                if withLabel && !label.isEmpty { s += ", label: \"\(label)\"" }
                if !enabled { s += ", enabled: false" }
                s += ")"
                return s
            },
            pagerStates: [
                AnyPlaygroundState("On") { PrismaSwitch(checked: .constant(true), label: "Push notifications") },
                AnyPlaygroundState("Off") { PrismaSwitch(checked: .constant(false), label: "Auto-sync") },
                AnyPlaygroundState("Disabled (on)") { PrismaSwitch(checked: .constant(true), label: "Locked on", enabled: false) },
                AnyPlaygroundState("With helper") { PrismaSwitch(checked: .constant(true), label: "Auto-sync", helperText: "Updates over Wi-Fi only.") },
                AnyPlaygroundState("Standalone") { PrismaSwitch(checked: .constant(true)) }
            ],
            a11yReport: A11yReport(
                role: "Switch",
                minTouchTarget: "48 × 48 dp / 44 × 44 pt",
                screenReader: "TalkBack and VoiceOver announce the role (\"Switch\"), the label, then the current state (\"On\" / \"Off\"). Toggling fires Selection haptic and announces the new state without re-focusing.",
                voiceControl: "Voice Access / Voice Control target the visible label (\"Tap Push notifications\"). Saying \"Toggle Push notifications\" works whether the switch is currently on or off.",
                keyboard: "Tab focuses the switch; Space / Enter toggles. Focus ring matches the accent color and meets the 3:1 non-text contrast bar in light + dark themes.",
                contrast: "Track-off uses border.default (3.1:1 against the surface); track-on uses accent.default (4.6:1). The thumb sits at 4.5:1 against either track. Build-time check-contrast.mjs gates regressions.",
                touchTarget: "Hit area extends 48 × 48 dp / 44 × 44 pt around the visible thumb so misses on the edge of the track still register as a toggle.",
                wcagQuote: "For all user interface components … the name and role can be programmatically determined; states, properties, and values that can be set by the user can be programmatically set.",
                wcagRef: "4.1.2 Name, Role, Value, Level A"
            )
        )
    }
}

struct RadioShowcase: View {
    @State private var optionsCsv: String = "Monthly, Yearly, Free"
    @State private var selectedIdx: Int = 0
    @State private var enabled: Bool = true

    private var options: [String] {
        let parsed = optionsCsv.split(separator: ",").map { $0.trimmingCharacters(in: .whitespaces) }.filter { !$0.isEmpty }
        return parsed.isEmpty ? ["Option"] : parsed
    }

    var body: some View {
        let safeIdx = min(max(selectedIdx, 0), options.count - 1)
        return PlaygroundScaffold(
            preview: {
                VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
                    ForEach(Array(options.enumerated()), id: \.offset) { idx, label in
                        PrismaRadio(
                            selected: idx == safeIdx,
                            onClick: enabled ? { selectedIdx = idx } : nil,
                            label: label,
                            enabled: enabled
                        )
                    }
                }
            },
            knobs: {
                StringKnobRow(label: "Options (comma separated)", value: $optionsCsv)
                IntKnobRow(label: "Selected index", value: $selectedIdx, range: 0...max(options.count - 1, 0))
                BoolKnobRow(label: "Enabled", value: $enabled)
            },
            code: {
                "PrismaRadio(\n    selected: idx == \(safeIdx),\n    onClick: { selectedIdx = idx },\n    label: options[idx]\n)"
            },
            pagerStates: [
                AnyPlaygroundState("Selected") { PrismaRadio(selected: true, onClick: {}, label: "Selected option") },
                AnyPlaygroundState("Unselected") { PrismaRadio(selected: false, onClick: {}, label: "Unselected option") },
                AnyPlaygroundState("With helper") { PrismaRadio(selected: false, onClick: {}, label: "Yearly", helperText: "$90 per year (save 17%).") },
                AnyPlaygroundState("Disabled") { PrismaRadio(selected: false, onClick: nil, label: "Locked", enabled: false) }
            ],
            a11yReport: A11yReport(
                role: "RadioButton (inside selectableGroup)",
                minTouchTarget: "48 × 48 dp / 44 × 44 pt",
                screenReader: "Wrapping the radio set in selectableGroup means TalkBack and VoiceOver announce \"option N of M\". Only one radio in the group is selected; the selected state is read alongside the label.",
                voiceControl: "Voice Access / Voice Control target the visible label (\"Tap Yearly\"). The full row is the tap target — assistive tech doesn't need to land precisely on the radio circle.",
                keyboard: "Tab moves into the group, then arrow keys move between radios within the group (per Material guidelines). Selection follows focus so screen reader users hear the current option immediately.",
                contrast: "Selected ring uses accent.default (4.6:1); unselected ring uses border.strong (3:1) — the 3:1 floor for non-text UI matches WCAG 1.4.11. Helper text is text.tertiary, still above the 4.5:1 body bar.",
                touchTarget: "Entire row is clickable so the tap target extends well past the 48 dp visible circle. Spacing between rows keeps adjacent radios from accidentally registering.",
                wcagQuote: "Information, structure, and relationships conveyed through presentation can be programmatically determined or are available in text.",
                wcagRef: "1.3.1 Info and Relationships, Level A"
            )
        )
    }
}

struct SliderShowcase: View {
    @State private var label: String = "Volume"
    @State private var value: Int = 40
    @State private var minVal: Int = 0
    @State private var maxVal: Int = 100
    @State private var stepCount: Int = 0
    @State private var disabled: Bool = false
    @State private var showValue: Bool = true
    @State private var asPercent: Bool = true

    var body: some View {
        let safeMax = max(maxVal, minVal + 1)
        let coerced = min(max(value, minVal), safeMax)
        return PlaygroundScaffold(
            preview: {
                PrismaSlider(
                    value: Binding(
                        get: { Double(coerced) },
                        set: { value = Int($0) }
                    ),
                    range: Double(minVal)...Double(safeMax),
                    step: stepCount > 0 ? Double((safeMax - minVal) / max(stepCount, 1)) : 0,
                    label: label.isEmpty ? nil : label,
                    showValue: showValue,
                    enabled: !disabled,
                    formatter: { asPercent ? "\(Int($0))%" : String(Int($0)) }
                )
            },
            knobs: {
                StringKnobRow(label: "Label", value: $label, placeholder: "Slider label")
                IntKnobRow(label: "Min", value: $minVal, range: 0...100)
                IntKnobRow(label: "Max", value: $maxVal, range: 1...100)
                IntKnobRow(label: "Steps (0 = continuous)", value: $stepCount, range: 0...10)
                BoolKnobRow(label: "Disabled", value: $disabled)
                BoolKnobRow(label: "Show current value", value: $showValue)
                BoolKnobRow(label: "Format as percentage", value: $asPercent)
            },
            pagerStates: [
                AnyPlaygroundState("Continuous") { ContinuousSliderState() },
                AnyPlaygroundState("Stepped (1–5)") { SteppedSliderState() },
                AnyPlaygroundState("Disabled") { PrismaSlider(value: .constant(0.7), label: "Read-only", enabled: false) }
            ],
            a11yReport: A11yReport(
                role: "SeekBar / Slider (progressSemantics)",
                minTouchTarget: "Thumb 48 × 48 dp / 44 × 44 pt hit area",
                screenReader: "TalkBack reads the label, current value, and range. Supply a valueFormatter so \"60 percent\" or \"4 of 5 stars\" is spoken instead of a raw 0.6 — much more useful to a non-sighted user.",
                voiceControl: "Voice Access supports \"Set Volume to 80\" by name. The thumb is independently focusable so \"Tap thumb\" works as a fallback when the label is not announced.",
                keyboard: "Arrow keys nudge by the step value (or 1% for continuous); Home / End jump to min / max. Page-Up / Page-Down move by 10% chunks for fine-vs-coarse control.",
                contrast: "Active track uses accent.default (4.6:1); inactive track is border.subtle (3.1:1) — the 3:1 floor for non-text UI is met. The thumb has a 2 dp shadow for tactile separation in light theme.",
                touchTarget: "Slider thumb hit-tests as 48 dp even when the visible thumb is smaller — drag accuracy doesn't degrade with one-handed phone use.",
                wcagQuote: "All functionality that uses single-pointer dragging movements for operation can be achieved by a single pointer without dragging — unless dragging is essential.",
                wcagRef: "2.5.7 Dragging Movements, Level AA (WCAG 2.2)"
            )
        )
    }
}

private struct ContinuousSliderState: View {
    @State private var v: Double = 0.4
    var body: some View {
        PrismaSlider(value: $v, label: "Volume", formatter: { "\(Int($0 * 100))%" })
    }
}

private struct SteppedSliderState: View {
    @State private var s: Double = 2
    var body: some View {
        PrismaSlider(value: $s, range: 1...5, step: 1, label: "Star rating", formatter: { String(Int($0)) })
    }
}

struct SegmentedControlShowcase: View {
    @State private var optionsCsv: String = "Day, Week, Month, Year"
    @State private var selectedIdx: Int = 0

    private var options: [String] {
        let parsed = optionsCsv.split(separator: ",").map { $0.trimmingCharacters(in: .whitespaces) }.filter { !$0.isEmpty }
        return parsed.isEmpty ? ["Option"] : parsed
    }

    var body: some View {
        let opts = options
        let safeIdx = min(max(selectedIdx, 0), opts.count - 1)
        return PlaygroundScaffold(
            preview: {
                PrismaSegmentedControl(
                    options: opts,
                    selected: Binding(
                        get: { opts[safeIdx] },
                        set: { newValue in selectedIdx = opts.firstIndex(of: newValue) ?? 0 }
                    )
                )
            },
            knobs: {
                StringKnobRow(label: "Options (comma separated)", value: $optionsCsv)
                IntKnobRow(label: "Selected index", value: $selectedIdx, range: 0...max(opts.count - 1, 0))
            },
            code: {
                let joined = opts.map { "\"\($0)\"" }.joined(separator: ", ")
                return "PrismaSegmentedControl(\n    options: [\(joined)],\n    selected: $selected\n)"
            },
            pagerStates: [
                AnyPlaygroundState("Two") { TwoSegState() },
                AnyPlaygroundState("Three") { ThreeSegState() },
                AnyPlaygroundState("Sizes") { SizeSegState() }
            ],
            a11yReport: A11yReport(
                role: "Tab (inside selectableGroup) — for in-place filtering, not navigation",
                minTouchTarget: "48 dp / 44 pt height across the entire row",
                screenReader: "Each segment is a Tab; the row is a selectableGroup so screen readers announce \"selected N of M\". Switching segment immediately reads the new selection without re-focusing.",
                voiceControl: "Voice Access targets each visible label (\"Tap Week\"). Numbers can also work as fallbacks (\"Tap 2\") via the labels overlay.",
                keyboard: "Tab moves into the group, arrow keys cycle between segments — selection follows focus so a screen reader user hears each segment as they move.",
                contrast: "Selected segment uses surface.raised on accent.subtle (4.7:1 against the indicator); unselected segments are text.secondary on surface.sunken — both above 4.5:1 body.",
                touchTarget: "Even the small (S/M/L) variant keeps the row at 44 pt — the cell width may shrink but the height never drops below the minimum.",
                wcagQuote: "For all user interface components … the name and role can be programmatically determined; states, properties, and values that can be set by the user can be programmatically set.",
                wcagRef: "4.1.2 Name, Role, Value, Level A"
            )
        )
    }
}

private struct TwoSegState: View {
    @State var s = "Off"
    var body: some View { PrismaSegmentedControl(options: ["Off", "On"], selected: $s) }
}
private struct ThreeSegState: View {
    @State var s = "Day"
    var body: some View { PrismaSegmentedControl(options: ["Day", "Week", "Month"], selected: $s) }
}
private struct SizeSegState: View {
    @State var s = "M"
    var body: some View { PrismaSegmentedControl(options: ["S", "M", "L"], selected: $s) }
}

struct SearchBarShowcase: View {
    @State private var query: String = ""
    @State private var placeholder: String = "Search the catalogue"

    var body: some View {
        PlaygroundScaffold(
            preview: { PrismaSearchBar(value: $query, placeholder: placeholder) },
            knobs: {
                StringKnobRow(label: "Placeholder", value: $placeholder)
            },
            code: {
                "PrismaSearchBar(value: $query, placeholder: \"\(placeholder)\")"
            },
            pagerStates: [
                AnyPlaygroundState("Empty") { SearchBarState(initial: "") },
                AnyPlaygroundState("With query") { SearchBarState(initial: "compose") }
            ],
            a11yReport: A11yReport(
                role: "EditText (search semantics)",
                minTouchTarget: "48 dp / 44 pt height",
                screenReader: "TalkBack and VoiceOver announce \"Search field\" and read the placeholder as a hint, not as a label. The clear button is its own focusable element labelled \"Clear search\" so it can be activated independently.",
                voiceControl: "Voice Access / Voice Control target the placeholder or label as the spoken handle. Saying \"Clear search\" hits the trailing × button without the user knowing its visual location.",
                keyboard: "Tab focuses the input; the IME action is Search, so the on-screen keyboard returns the search affordance. ESC clears focus without committing a query.",
                contrast: "Placeholder uses text.tertiary (4.5:1 against surface.raised); the search icon is text.secondary (5.4:1). Focus ring meets the 3:1 floor for non-text UI in light + dark themes.",
                touchTarget: "The whole row is the tap target — 48 dp / 44 pt. The clear × is also independently 48 × 48 dp / 44 × 44 pt so it's hittable on devices with no styling overrides.",
                wcagQuote: "Labels or instructions are provided when content requires user input.",
                wcagRef: "3.3.2 Labels or Instructions, Level A"
            )
        )
    }
}

private struct SearchBarState: View {
    @State var value: String
    init(initial: String) { _value = State(initialValue: initial) }
    var body: some View { PrismaSearchBar(value: $value) }
}

struct StepperShowcase: View {
    @State private var value: Int = 1
    @State private var minVal: Int = 0
    @State private var maxVal: Int = 10
    @State private var step: Int = 1
    @State private var enabled: Bool = true
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        let safeMax = max(maxVal, minVal + 1)
        let coerced = min(max(value, minVal), safeMax)
        return PlaygroundScaffold(
            preview: {
                VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
                    PrismaStepper(
                        value: Binding(get: { coerced }, set: { value = $0 }),
                        range: minVal...safeMax,
                        step: max(step, 1),
                        enabled: enabled
                    )
                    Text("Selected: \(coerced)")
                        .font(PrismaTypography.bodyMd.font)
                        .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                }
            },
            knobs: {
                IntKnobRow(label: "Min", value: $minVal, range: 0...50)
                IntKnobRow(label: "Max", value: $maxVal, range: 1...100)
                IntKnobRow(label: "Step", value: $step, range: 1...10)
                BoolKnobRow(label: "Enabled", value: $enabled)
            },
            code: {
                var s = "PrismaStepper(value: $value, range: \(minVal)...\(safeMax)"
                if step > 1 { s += ", step: \(step)" }
                if !enabled { s += ", enabled: false" }
                s += ")"
                return s
            },
            pagerStates: [
                AnyPlaygroundState("Default") { StepperState(initial: 1) },
                AnyPlaygroundState("At max") { StepperState(initial: 10) },
                AnyPlaygroundState("Disabled") { PrismaStepper(value: .constant(5), range: 0...10, enabled: false) }
            ],
            a11yReport: A11yReport(
                role: "Stepper (separate Increment / Decrement buttons)",
                minTouchTarget: "48 × 48 dp / 44 × 44 pt per button",
                screenReader: "Each button is independently focusable. TalkBack reads the role (\"Decrement\" / \"Increment\") plus the current value (\"3 of 10\"). When the limit is reached, the disabled state is announced so users don't keep tapping a no-op button.",
                voiceControl: "Voice Access / Voice Control work via the visible labels. Saying \"Tap Plus\" or \"Tap Increment\" both work — no precise tap needed.",
                keyboard: "Custom accessibilityActions for Increment / Decrement let switch-control and keyboard users adjust the value without precise targeting; arrow keys also work when the stepper is focused.",
                contrast: "Button glyphs (+ / −) use text.primary (10:1+); disabled state is text.disabled at exactly the 3:1 floor for non-text UI to communicate the limit visually as well as via role.",
                touchTarget: "Each button is exactly 48 × 48 dp / 44 × 44 pt — never compressed below the minimum. Spacing between + and − keeps fat-finger taps from hitting both.",
                wcagQuote: "The size of the target for pointer inputs is at least 24 by 24 CSS pixels, except where the target is exempted.",
                wcagRef: "2.5.8 Target Size (Minimum), Level AA (WCAG 2.2)"
            )
        )
    }
}

private struct StepperState: View {
    @State var value: Int
    init(initial: Int) { _value = State(initialValue: initial) }
    var body: some View { PrismaStepper(value: $value, range: 0...10) }
}

struct TagInputShowcase: View {
    @State private var tags: [String] = ["swift", "swiftui", "design system"]
    @State private var label: String = "Topics"
    @State private var placeholder: String = "Add a topic"

    var body: some View {
        PlaygroundScaffold(
            preview: {
                PrismaTagInput(
                    tags: $tags,
                    label: label.isEmpty ? nil : label,
                    placeholder: placeholder
                )
            },
            knobs: {
                StringKnobRow(label: "Label", value: $label)
                StringKnobRow(label: "Placeholder", value: $placeholder)
            },
            code: {
                var s = "PrismaTagInput(\n    tags: $tags"
                if !label.isEmpty { s += ",\n    label: \"\(label)\"" }
                s += ",\n    placeholder: \"\(placeholder)\"\n)"
                return s
            },
            pagerStates: [
                AnyPlaygroundState("Empty") { TagInputState(initial: []) },
                AnyPlaygroundState("Filled") { TagInputState(initial: ["swift", "swiftui"]) }
            ],
            a11yReport: A11yReport(
                role: "EditText + associated chip list (polite live region)",
                minTouchTarget: "48 dp / 44 pt per chip; chip × also 48 × 48 dp / 44 × 44 pt",
                screenReader: "Adding a tag fires a polite announcement (\"swift added\"). Removing fires \"swift removed\". Each chip's × is its own focusable element so deletion is discoverable without backspace tricks.",
                voiceControl: "Voice Access / Voice Control target each chip's visible label, and \"Remove kotlin\" hits its × directly. The input itself is a regular text field so dictation works as expected.",
                keyboard: "Backspace on empty input deletes the last chip and announces it. Each chip is reachable via Tab; pressing Delete / Backspace on a focused chip removes it.",
                contrast: "Chip background is surface.sunken with text.primary (10:1+); the × icon uses text.secondary (5.4:1). Focus ring on chips meets 3:1 non-text contrast.",
                touchTarget: "Chips are 32 dp tall but reside in a 48 dp row; the × is independently 24 dp visible / 48 dp hit. Spacing is Sp2 horizontally so adjacent chips don't merge under fat fingers.",
                wcagQuote: "In content implemented using markup languages, status messages can be programmatically determined through role or properties such that they can be presented to the user by assistive technologies without receiving focus.",
                wcagRef: "4.1.3 Status Messages, Level AA"
            )
        )
    }
}

private struct TagInputState: View {
    @State var tags: [String]
    init(initial: [String]) { _tags = State(initialValue: initial) }
    var body: some View { PrismaTagInput(tags: $tags, label: "Tags") }
}

struct AutocompleteShowcase: View {
    @State private var v: String = ""
    @State private var corpusCsv: String = "Bangalore, Bangkok, Beijing, Berlin, Boston, Brisbane, Cairo, Delhi"
    @State private var label: String = "City"
    @State private var placeholder: String = "Type a city name"

    private var cities: [String] {
        corpusCsv.split(separator: ",").map { $0.trimmingCharacters(in: .whitespaces) }.filter { !$0.isEmpty }
    }

    var body: some View {
        PlaygroundScaffold(
            preview: {
                PrismaAutocomplete(
                    value: $v,
                    suggestions: v.isEmpty ? [] : cities.filter { $0.lowercased().contains(v.lowercased()) },
                    onSelect: { v = $0 },
                    label: label.isEmpty ? nil : label,
                    placeholder: placeholder
                )
            },
            knobs: {
                StringKnobRow(label: "Corpus (comma separated)", value: $corpusCsv)
                StringKnobRow(label: "Label", value: $label)
                StringKnobRow(label: "Placeholder", value: $placeholder)
            },
            code: {
                "PrismaAutocomplete(\n    value: $query,\n    suggestions: corpus.filter { $0.localizedCaseInsensitiveContains(query) },\n    onSelect: { query = $0 },\n    label: \"\(label)\"\n)"
            },
            pagerStates: [
                AnyPlaygroundState("Default") { AutocompleteState() }
            ],
            a11yReport: A11yReport(
                role: "Combobox (input + listbox popup)",
                minTouchTarget: "48 dp / 44 pt per suggestion row",
                screenReader: "Suggestion count is announced when the popup opens (\"6 suggestions\"). Each row reads as the typed-ahead match. Selecting a row dismisses the popup and announces the chosen value back into the field.",
                voiceControl: "Voice Access / Voice Control target each suggestion by its visible text. Saying \"Tap Bangkok\" picks it without the user needing to know its index in the list.",
                keyboard: "Arrow up / down moves focus through suggestions while the caret stays in the input. Enter / tap selects; Escape closes the popup and returns focus to the input — the typed query is preserved.",
                contrast: "Suggestion rows use text.primary (10:1+); the active row highlight is surface.sunken (3:1 against base). Hover and focus rings are accent.default at 4.6:1.",
                touchTarget: "Each row is at least 48 dp / 44 pt. The popup auto-sizes to the input width on phones and switches to a centred sheet on small screens to keep rows full-width.",
                wcagQuote: "For all user interface components … the name and role can be programmatically determined; states, properties, and values that can be set by the user can be programmatically set.",
                wcagRef: "4.1.2 Name, Role, Value, Level A"
            )
        )
    }
}

private struct AutocompleteState: View {
    @State var v: String = ""
    var body: some View {
        PrismaAutocomplete(value: $v, suggestions: [], onSelect: { v = $0 }, label: "Country")
    }
}

struct DatePickerShowcase: View {
    @State private var date = Date()
    var body: some View {
        PlaygroundScaffold(
            preview: { PrismaDatePicker(date: $date) },
            code: { "PrismaDatePicker(date: $date)" },
            // No states pager — date pickers are large; the live preview is enough.
            a11yReport: A11yReport(
                role: "Calendar (Material 3 / native iOS DatePicker)",
                minTouchTarget: "48 dp / 44 pt grid cells",
                screenReader: "Native DatePicker handles month / year navigation announcements. \"Today\" is announced when focused; the selected date is announced on commit. Each cell reads as the full date (\"7 May 2026, Thursday\") to remove ambiguity.",
                voiceControl: "Voice Access / Voice Control work on the month/year header for fast navigation, then individual cells (\"Tap 23\"). Voice-Control numbers overlay also lets users select any visible cell by index.",
                keyboard: "Arrow keys move by day; Page-Up / Page-Down by month; Shift-Page-Up / Down by year. Tab moves between header controls and the grid.",
                contrast: "Today's outline is accent.default (4.6:1 against surface); the selected fill uses accent.default with text.onAccent for 4.5:1+ readability. Out-of-month dimmed days still meet 3:1.",
                touchTarget: "Grid cells are 48 dp / 44 pt minimum even when the picker is shown in a narrow modal. Header chevrons are independently 48 × 48 dp / 44 × 44 pt.",
                wcagQuote: "The purpose of each input field collecting information about the user can be programmatically determined when … the field serves a purpose identified in the Input Purposes for User Interface Components.",
                wcagRef: "1.3.5 Identify Input Purpose, Level AA"
            )
        )
    }
}

private struct DatePickerState: View {
    @State var date = Date()
    var body: some View { PrismaDatePicker(date: $date) }
}

struct TimePickerShowcase: View {
    @State private var date = Date()
    var body: some View {
        PlaygroundScaffold(
            // .compact DatePicker anchors trailing by default. Wrap in
            // HStack with bracketing Spacers so it centres in the slot
            // instead of hugging the right edge.
            preview: {
                HStack {
                    Spacer()
                    PrismaTimePicker(date: $date)
                    Spacer()
                }
            },
            code: { "PrismaTimePicker(date: $date)" },
            // No states pager — single canonical visual; the live preview is enough.
            a11yReport: A11yReport(
                role: "Time picker (Material 3 / native iOS DatePicker .time)",
                minTouchTarget: "48 dp / 44 pt per control",
                screenReader: "Hour and minute controls have separate roles and announce the current value on focus. Switching between hour and minute fires an immediate announcement so users know which segment they're editing.",
                voiceControl: "AM/PM is a button group with explicit labels (\"Tap PM\"). Hour and minute can be set by voice via the keyboard input mode (\"Use keyboard\" toggle on iOS / Android).",
                keyboard: "Tab cycles hour → minute → AM/PM → OK / Cancel. Arrow keys increment / decrement the focused segment. Numeric keys (when in keyboard mode) overwrite the segment directly.",
                contrast: "Active segment is filled accent.default at 4.6:1; inactive segment is surface.sunken with text.primary (10:1). Dial numbers meet 4.5:1 against the dial surface in both themes.",
                touchTarget: "Dial cells are 48 dp / 44 pt minimum. The mode-switch icon (clock / keyboard) is independently 48 × 48 / 44 × 44 so users can flip modes with a single tap.",
                wcagQuote: "The purpose of each input field collecting information about the user can be programmatically determined when … the field serves a purpose identified in the Input Purposes for User Interface Components.",
                wcagRef: "1.3.5 Identify Input Purpose, Level AA"
            )
        )
    }
}

private struct TimePickerState: View {
    @State var date = Date()
    var body: some View { PrismaTimePicker(date: $date) }
}

struct ColorPickerShowcase: View {
    @State private var color = Color(red: 0.78, green: 0.4, blue: 0.14)
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        PlaygroundScaffold(
            preview: { PrismaColorPicker(color: $color) },
            knobs: {
                Text("RGB: \(Int(redOf(color) * 255)), \(Int(greenOf(color) * 255)), \(Int(blueOf(color) * 255))")
                    .font(PrismaTypography.bodyMd.font)
                    .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
            },
            code: { "PrismaColorPicker(color: $color)" },
            // No states pager — single canonical visual; the live preview is enough.
            a11yReport: A11yReport(
                role: "ColorPicker (RGB sliders + swatch)",
                minTouchTarget: "Slider thumb 48 × 48 dp / 44 × 44 pt",
                screenReader: "Each channel slider is independently focusable and announces its value (\"Red, 199 of 255\"). The current colour is also announced as a hex value when the swatch is focused so non-sighted users get a precise readout.",
                voiceControl: "Voice Access / Voice Control target each slider by label (\"Tap Red\"). Number-input mode lets users dictate exact values (\"Set Red to 200\").",
                keyboard: "Tab cycles between R / G / B sliders. Arrow keys nudge the channel by 1; Page-Up / Page-Down by 10. Hex input field accepts a 6-character code via paste / typing.",
                contrast: "Slider tracks meet 3:1 non-text contrast against the surface. The hex / RGB readout uses text.primary (10:1+) so values stay readable at any selected colour.",
                touchTarget: "Slider thumbs hit-test as 48 × 48 dp. The colour swatch button is independently 48 × 48 / 44 × 44 to copy the hex to the clipboard.",
                wcagQuote: "Color is not used as the only visual means of conveying information, indicating an action, prompting a response, or distinguishing a visual element.",
                wcagRef: "1.4.1 Use of Color, Level A"
            )
        )
    }
}

private struct ColorPickerState: View {
    @State var c = Color(red: 0.78, green: 0.4, blue: 0.14)
    var body: some View { PrismaColorPicker(color: $c) }
}

private func redOf(_ c: Color) -> Double {
    #if canImport(UIKit)
    let ui = UIColor(c); var r: CGFloat = 0, g: CGFloat = 0, b: CGFloat = 0, a: CGFloat = 0
    ui.getRed(&r, green: &g, blue: &b, alpha: &a); return Double(r)
    #else
    return 0
    #endif
}
private func greenOf(_ c: Color) -> Double {
    #if canImport(UIKit)
    let ui = UIColor(c); var r: CGFloat = 0, g: CGFloat = 0, b: CGFloat = 0, a: CGFloat = 0
    ui.getRed(&r, green: &g, blue: &b, alpha: &a); return Double(g)
    #else
    return 0
    #endif
}
private func blueOf(_ c: Color) -> Double {
    #if canImport(UIKit)
    let ui = UIColor(c); var r: CGFloat = 0, g: CGFloat = 0, b: CGFloat = 0, a: CGFloat = 0
    ui.getRed(&r, green: &g, blue: &b, alpha: &a); return Double(b)
    #else
    return 0
    #endif
}

// MARK: - Feedback

struct ToastShowcase: View {
    @State private var message: String = "Saved successfully."
    @State private var kind: PrismaToastKind = .success
    @State private var actionLabel: String = "Undo"
    @State private var hasAction: Bool = true
    @State private var fireCount: Int = 0

    var body: some View {
        PlaygroundScaffold(
            preview: {
                PrismaToast(
                    message: message.isEmpty ? "Toast message" : message,
                    kind: kind,
                    actionLabel: hasAction && !actionLabel.isEmpty ? actionLabel : nil,
                    onAction: hasAction ? { fireCount += 1 } : nil
                )
            },
            knobs: {
                StringKnobRow(
                    label: "Message",
                    value: $message,
                    placeholder: "What happened",
                    helper: "Action taps fired: \(fireCount)"
                )
                EnumKnobRow(
                    label: "Kind",
                    value: $kind,
                    values: [.info, .success, .warning, .danger],
                    optionLabel: { String(describing: $0) }
                )
                BoolKnobRow(label: "With action", value: $hasAction)
                StringKnobRow(label: "Action label", value: $actionLabel, placeholder: "Undo")
            },
            code: {
                var s = "PrismaToast(\n    message: \"\(message)\",\n    kind: .\(String(describing: kind))"
                if hasAction { s += ",\n    actionLabel: \"\(actionLabel)\"" }
                s += "\n)"
                if hasAction { s += " { /* … */ }" }
                return s
            },
            pagerStates: [
                AnyPlaygroundState("Info") { PrismaToast(message: "New version available.", kind: .info) },
                AnyPlaygroundState("Success") { PrismaToast(message: "Saved successfully.", kind: .success, actionLabel: "Undo") {} },
                AnyPlaygroundState("Warning") { PrismaToast(message: "Connection looks slow.", kind: .warning) },
                AnyPlaygroundState("Danger") { PrismaToast(message: "Could not reach server.", kind: .danger, actionLabel: "Retry") {} }
            ],
            a11yReport: A11yReport(
                role: "Live region (transient announcement)",
                minTouchTarget: "Action button 48 × 48 dp / 44 × 44 pt",
                screenReader: "Info / success use Polite live region — they're announced after the current speech finishes. Warning / danger use Assertive so they interrupt mid-utterance. The kind prefix (\"Danger.\") makes the severity unambiguous.",
                voiceControl: "Action labels (\"Undo\", \"Retry\") are spoken targets — Voice Access supports \"Tap Undo\" without the user knowing where the toast sits on screen.",
                keyboard: "Action button retains its own Role.Button so Tab can reach it during the toast's lifetime. Pressing ESC dismisses the toast (matches Material 3 behaviour).",
                contrast: "Each kind pairs background and text colours that meet 4.5:1 body — danger uses status.danger background with on-status.danger text. Action label keeps 4.5:1 against either kind background.",
                touchTarget: "Action button is at least 48 dp / 44 pt tall. The toast's swipe-to-dismiss area extends across the full toast width.",
                wcagQuote: "In content implemented using markup languages, status messages can be programmatically determined through role or properties such that they can be presented to the user by assistive technologies without receiving focus.",
                wcagRef: "4.1.3 Status Messages, Level AA"
            )
        )
    }
}

struct BannerShowcase: View {
    @State private var title: String = "We're upgrading our servers"
    @State private var bannerDescription: String = "You may notice slower response times for the next 30 minutes."
    @State private var kind: PrismaBannerKind = .info
    @State private var hasAction: Bool = true
    @State private var actionLabel: String = "Learn more"
    @State private var hasDismiss: Bool = true
    @State private var visible: Bool = true

    var body: some View {
        PlaygroundScaffold(
            preview: {
                if visible {
                    PrismaBanner(
                        title: title.isEmpty ? "Banner title" : title,
                        description: bannerDescription.isEmpty ? nil : bannerDescription,
                        kind: kind,
                        actionLabel: hasAction && !actionLabel.isEmpty ? actionLabel : nil,
                        onAction: hasAction ? {} : nil,
                        onDismiss: hasDismiss ? { visible = false } : nil
                    )
                } else {
                    PrismaButton("Show banner again", variant: .secondary) { visible = true }
                }
            },
            knobs: {
                StringKnobRow(label: "Title", value: $title, placeholder: "Headline")
                StringKnobRow(label: "Description", value: $bannerDescription, placeholder: "Optional sub-text")
                EnumKnobRow(
                    label: "Kind",
                    value: $kind,
                    values: [.info, .success, .warning, .danger],
                    optionLabel: { String(describing: $0) }
                )
                BoolKnobRow(label: "With action", value: $hasAction)
                StringKnobRow(label: "Action label", value: $actionLabel, placeholder: "Learn more")
                BoolKnobRow(label: "With dismiss", value: $hasDismiss)
            },
            code: {
                var s = "PrismaBanner(\n    title: \"\(title)\""
                if !bannerDescription.isEmpty { s += ",\n    description: \"\(bannerDescription)\"" }
                s += ",\n    kind: .\(String(describing: kind))"
                if hasAction { s += ",\n    actionLabel: \"\(actionLabel)\",\n    onAction: { /* … */ }" }
                if hasDismiss { s += ",\n    onDismiss: { /* … */ }" }
                s += "\n)"
                return s
            },
            pagerStates: [
                AnyPlaygroundState("Info") { PrismaBanner(title: "Server upgrade", description: "Slower response times expected.", kind: .info, actionLabel: "Learn more", onAction: {}, onDismiss: {}) },
                AnyPlaygroundState("Success") { PrismaBanner(title: "Profile updated", description: "Saved across all devices.", kind: .success, onDismiss: {}) },
                AnyPlaygroundState("Warning") { PrismaBanner(title: "Storage almost full", description: "Less than 1GB free.", kind: .warning, actionLabel: "Manage", onAction: {}) },
                AnyPlaygroundState("Danger") { PrismaBanner(title: "Action required", description: "Verify your email.", kind: .danger, actionLabel: "Verify", onAction: {}) }
            ],
            a11yReport: A11yReport(
                role: "Live region (inline alert)",
                minTouchTarget: "Action / dismiss buttons 48 × 48 dp / 44 × 44 pt",
                screenReader: "Title + description merge into a single announcement on appearance — users don't need to focus the banner to hear it. Polite for info / success, Assertive for warning / danger so they pre-empt the current speech.",
                voiceControl: "Action and dismiss labels are individually spoken targets. \"Tap Verify\" or \"Tap Close banner\" both work without the user needing to find the banner visually.",
                keyboard: "Tab moves into the banner reaching action then dismiss. Pressing ESC closes a dismissable banner; non-dismissable banners stay anchored.",
                contrast: "Each kind pairs an accent strip with text on a tinted background — all combinations meet 4.5:1 body across light + dark themes. The 4 dp left strip uses kind.default at 3:1 against surface for non-text contrast.",
                touchTarget: "Action and dismiss are independently 48 × 48 dp / 44 × 44 pt even when the banner is rendered tightly. Spacing between them prevents accidental dismiss when the user means to act.",
                wcagQuote: "In content implemented using markup languages, status messages can be programmatically determined through role or properties such that they can be presented to the user by assistive technologies without receiving focus.",
                wcagRef: "4.1.3 Status Messages, Level AA"
            )
        )
    }
}

struct ModalShowcase: View {
    @State private var open: Bool = false
    @State private var title: String = "Delete project?"
    @State private var message: String = "This will permanently delete \"Prisma\" and all its files. This action cannot be undone."
    @State private var confirmLabel: String = "Delete"
    @State private var dismissLabel: String = "Cancel"
    @State private var hasDismiss: Bool = true
    @State private var destructive: Bool = true
    @State private var lastChoice: String = "(none)"

    @Environment(\.colorScheme) private var scheme

    var body: some View {
        PlaygroundScaffold(
            preview: {
                VStack(alignment: .center, spacing: PrismaSpacing.sp3) {
                    PrismaButton("Open modal") { open = true }
                    Text("Last choice: \(lastChoice)")
                        .font(PrismaTypography.bodySm.font)
                        .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            },
            knobs: {
                StringKnobRow(label: "Title", value: $title)
                StringKnobRow(label: "Body", value: $message)
                StringKnobRow(label: "Confirm label", value: $confirmLabel)
                BoolKnobRow(label: "With dismiss button", value: $hasDismiss)
                StringKnobRow(label: "Dismiss label", value: $dismissLabel)
                BoolKnobRow(label: "Destructive", value: $destructive)
            },
            code: {
                var s = "PrismaModalContent(\n    title: \"\(title)\",\n    message: \"\(message)\",\n    confirmLabel: \"\(confirmLabel)\",\n    onConfirm: { /* … */ }"
                if hasDismiss { s += ",\n    dismissLabel: \"\(dismissLabel)\",\n    onDismiss: { /* … */ }" }
                if destructive { s += ",\n    isDestructive: true" }
                s += "\n)"
                return s
            },
            // No states pager — modals are trigger-based; the live preview is enough.
            a11yReport: A11yReport(
                role: "Modal Dialog (focus trap)",
                minTouchTarget: "48 × 48 dp / 44 × 44 pt confirm / dismiss",
                screenReader: "Title is announced on open with role \"alert\" so screen readers read it immediately. Body follows; confirm and dismiss buttons are focusable in order. Destructive variant tints the confirm button red — the role itself doesn't change, so screen readers still announce \"button\".",
                voiceControl: "Confirm and dismiss labels are individually spoken targets. \"Tap Delete\" or \"Tap Cancel\" works without precise targeting; the trigger stays disabled until the modal closes.",
                keyboard: "Focus traps inside the modal — Tab cycles between buttons without escaping. ESC dismisses (returns focus to the trigger). Enter activates the focused button.",
                contrast: "Scrim is surface.inverse @ 64% so the underlying screen is dimmed but visible. Modal background is surface.raised. Destructive confirm uses status.danger at 4.6:1 against the surface.",
                touchTarget: "Confirm and dismiss are at least 48 × 48 dp / 44 × 44 pt. Stacked layout on narrow screens still respects the minimum height; spacing between buttons prevents mis-taps.",
                wcagQuote: "If keyboard focus can be moved to a component … then focus can be moved away from that component using only a keyboard interface, and, if it requires more than unmodified arrow or tab keys or other standard exit methods, the user is advised of the method.",
                wcagRef: "2.1.2 No Keyboard Trap, Level A"
            )
        )
        .sheet(isPresented: $open) {
            PrismaModalContent(
                title: title.isEmpty ? "Title" : title,
                message: message,
                confirmLabel: confirmLabel.isEmpty ? "OK" : confirmLabel,
                onConfirm: { open = false; lastChoice = "Confirmed" },
                dismissLabel: hasDismiss && !dismissLabel.isEmpty ? dismissLabel : nil,
                onDismiss: hasDismiss ? { open = false; lastChoice = "Dismissed" } : nil,
                isDestructive: destructive
            )
            .presentationDetents([.medium])
        }
    }
}

struct BottomSheetShowcase: View {
    @State private var open = false
    @State private var title: String = "Settings"
    @State private var bodyText: String = "Bottom sheets are for short, focused tasks that don't warrant a full screen."
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        PlaygroundScaffold(
            preview: {
                VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
                    PrismaButton("Open bottom sheet") { open = true }
                    Text("Bottom sheet appears anchored — open to view.")
                        .font(PrismaTypography.bodySm.font)
                        .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                }
            },
            knobs: {
                StringKnobRow(label: "Title", value: $title)
                StringKnobRow(label: "Body", value: $bodyText)
            },
            code: { ".sheet(isPresented: $open) {\n    VStack { /* sheet content */ }\n        .presentationDetents([.medium])\n}" },
            // No states pager — sheet is trigger-based; the live preview is enough.
            a11yReport: A11yReport(
                role: "Modal Sheet (focus trap)",
                minTouchTarget: "Drag handle 48 × 48 dp / 44 × 44 pt; content interactive",
                screenReader: "TalkBack and VoiceOver announce \"Sheet\" on open. Content behind the sheet is hidden from a11y so swipe-explore stays inside the sheet. Drag handle has its own Role.Button announcing \"drag handle, double-tap to expand\".",
                voiceControl: "Drag handle is independently spoken (\"Tap drag handle\") for users who can't reach it visually. All sheet content is targetable by its visible label.",
                keyboard: "Focus traps inside the sheet. Swipe-down (or scrim tap) and ESC dismiss the sheet, returning focus to the trigger button. Tab cycles within the sheet.",
                contrast: "Sheet uses surface.raised against a surface.inverse @ 64% scrim so depth is communicated visually as well as via role. Drag handle is border.strong at 3:1 non-text contrast.",
                touchTarget: "Drag handle is 48 × 48 dp / 44 × 44 pt. Sheet content respects component-level minimums; sheet itself can grow to multiple presentation detents.",
                wcagQuote: "If keyboard focus can be moved to a component … then focus can be moved away from that component using only a keyboard interface.",
                wcagRef: "2.1.2 No Keyboard Trap, Level A"
            )
        )
        .sheet(isPresented: $open) {
            VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
                Text(title).font(PrismaTypography.headlineSm.font)
                    .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                Text(bodyText).font(PrismaTypography.bodyMd.font)
                    .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                PrismaButton("Close", variant: .secondary) { open = false }
            }
            .padding(PrismaSpacing.sp7)
            .presentationDetents([.medium])
        }
    }
}

struct PopoverShowcase: View {
    @State private var open = false
    @State private var label: String = "Quick action"
    @State private var bodyText: String = "Inline content with focus management."
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        PlaygroundScaffold(
            preview: {
                PrismaButton("Open popover") { open = true }
                    .popover(isPresented: $open) {
                        VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
                            Text(label).font(PrismaTypography.labelLg.font)
                                .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                            Text(bodyText).font(PrismaTypography.bodySm.font)
                                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                            PrismaButton("Got it") { open = false }
                        }
                        .padding(PrismaSpacing.sp4)
                        .presentationCompactAdaptation(.popover)
                    }
            },
            knobs: {
                StringKnobRow(label: "Label", value: $label)
                StringKnobRow(label: "Body", value: $bodyText)
            },
            code: { ".popover(isPresented: $open) {\n    VStack { /* popover content */ }\n        .presentationCompactAdaptation(.popover)\n}" },
            // No states pager — popover is trigger-based; the live preview is enough.
            a11yReport: A11yReport(
                role: "Popover (non-modal overlay)",
                minTouchTarget: "Trigger 48 × 48 dp / 44 × 44 pt",
                screenReader: "Lighter than Modal — does not trap focus. Screen readers announce the popover content but the user can also explore the page underneath. Tap-outside / ESC dismisses and returns focus to the trigger.",
                voiceControl: "Anchored to the trigger; positioning auto-flips to stay on-screen so Voice Control's number overlay can hit any visible label inside the popover.",
                keyboard: "Tab moves into the popover from the trigger. ESC closes. Tab past the last popover element returns to the next page-level focus stop — no trap.",
                contrast: "Popover uses surface.raised with a 1 dp border.subtle (3:1 non-text contrast). The shadow gives depth in light theme; in dark theme the border alone communicates the boundary.",
                touchTarget: "Trigger respects component-level minimums (48 × 48 dp / 44 × 44 pt). Popover content matches component-level minimums for any embedded controls.",
                wcagQuote: "Additional content that becomes visible and then hidden, in response to keyboard focus or pointer hover, [must be] dismissable, hoverable, persistent.",
                wcagRef: "1.4.13 Content on Hover or Focus, Level AA"
            )
        )
    }
}

struct TooltipShowcase: View {
    @State private var hint: String = "Save to clipboard"
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        PlaygroundScaffold(
            preview: {
                Image(prisma: .copy).renderingMode(.template).resizable()
                    .frame(width: 24, height: 24)
                    .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                    .help(hint)
            },
            knobs: {
                StringKnobRow(
                    label: "Tooltip text",
                    value: $hint,
                    helper: "Long-press the icon (or hover) to surface the tooltip."
                )
            },
            code: { "Image(prisma: .copy)\n    .help(\"\(hint)\")" },
            pagerStates: [
                AnyPlaygroundState("Copy") {
                    Image(prisma: .copy).renderingMode(.template).resizable()
                        .frame(width: 24, height: 24)
                        .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                        .help("Save to clipboard")
                },
                AnyPlaygroundState("Star") {
                    Image(prisma: .star).renderingMode(.template).resizable()
                        .frame(width: 24, height: 24)
                        .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                        .help("Star this item")
                }
            ],
            a11yReport: A11yReport(
                role: "Tooltip (label association — not interactive itself)",
                minTouchTarget: "Trigger element 48 × 48 dp / 44 × 44 pt",
                screenReader: "Tooltip text serves as the trigger's accessible label. Screen readers read \"Copy\" without the tooltip needing to visually appear — the popup is just the visual representation of a label assistive tech already had.",
                voiceControl: "Voice Access targets the tooltip text directly (\"Tap Copy\"). The visible icon-only trigger is reachable by its hidden label.",
                keyboard: "Focusing the trigger surfaces the tooltip after the platform delay. ESC dismisses without losing focus. The tooltip is dismissable (can be hidden), hoverable (can be entered), and persistent (stays until dismissed).",
                contrast: "Tooltip uses surface.inverse with text.onInverse — both meet 4.5:1 body in light + dark themes. The arrow / pointer is the same colour for visual continuity.",
                touchTarget: "Tooltip is non-interactive — only the trigger needs the touch-target minimum. Icon-only triggers must size to 48 × 48 dp / 44 × 44 pt regardless of glyph size.",
                wcagQuote: "Where receiving and then removing pointer hover or keyboard focus triggers additional content to become visible and then hidden, the additional content [must be] dismissable, hoverable, persistent.",
                wcagRef: "1.4.13 Content on Hover or Focus, Level AA"
            )
        )
    }
}

private enum LoadingShape: String, CaseIterable, Hashable { case circular = "Circular", linear = "Linear" }

struct LoadingShowcase: View {
    @State private var shape: LoadingShape = .circular
    @State private var size: PrismaLoadingSize = .md
    @State private var indeterminate: Bool = true
    @State private var progressPct: Int = 60

    var body: some View {
        PlaygroundScaffold(
            preview: {
                switch shape {
                case .circular: PrismaCircularLoading(size: size)
                case .linear: PrismaLinearLoading(progress: indeterminate ? nil : Double(progressPct) / 100.0)
                }
            },
            knobs: {
                EnumKnobRow(label: "Shape", value: $shape, values: LoadingShape.allCases, optionLabel: { $0.rawValue })
                EnumKnobRow(label: "Size (circular)", value: $size, values: [.sm, .md, .lg], optionLabel: { String(describing: $0) })
                BoolKnobRow(label: "Indeterminate (linear)", value: $indeterminate)
                IntKnobRow(label: "Progress %", value: $progressPct, range: 0...100)
            },
            code: {
                switch shape {
                case .circular: return "PrismaCircularLoading(size: .\(String(describing: size)))"
                case .linear: return indeterminate ? "PrismaLinearLoading()" : "PrismaLinearLoading(progress: \(Double(progressPct) / 100.0))"
                }
            },
            pagerStates: [
                AnyPlaygroundState("Circular Sm") { PrismaCircularLoading(size: .sm) },
                AnyPlaygroundState("Circular Md") { PrismaCircularLoading(size: .md) },
                AnyPlaygroundState("Circular Lg") { PrismaCircularLoading(size: .lg) },
                AnyPlaygroundState("Linear (indeterminate)") { PrismaLinearLoading() },
                AnyPlaygroundState("Linear (60%)") { PrismaLinearLoading(progress: 0.6) }
            ],
            a11yReport: A11yReport(
                role: "ProgressBar (non-interactive)",
                minTouchTarget: "n/a — loaders aren't tappable",
                screenReader: "Indeterminate variant uses progressSemantics with no value — TalkBack and VoiceOver read it as \"In progress\" or \"Loading\". Determinate variant exposes 0–1; screen readers announce the percentage on focus / change.",
                voiceControl: "Loader has no spoken target by itself. Pair it with a sibling label (\"Loading projects…\") so users have something to talk about while waiting.",
                keyboard: "Loader is not focusable. Surrounding container should hold focus (or move it to the loaded content once ready) so Tab order doesn't break.",
                contrast: "Active arc / bar uses accent.default at 4.6:1; the inactive track is border.subtle at 3:1 — non-text contrast minimum is met. Reduce-motion users still see colour change without animation.",
                touchTarget: "n/a. The loader itself is purely visual; ensure any cancel button next to it respects the 48 × 48 dp / 44 × 44 pt minimum.",
                wcagQuote: "In content implemented using markup languages, status messages can be programmatically determined through role or properties such that they can be presented to the user by assistive technologies without receiving focus.",
                wcagRef: "4.1.3 Status Messages, Level AA"
            )
        )
    }
}

private enum SkeletonComposition: String, CaseIterable, Hashable {
    case lines = "Lines", card = "Card", postPlaceholder = "Post placeholder"
}

struct SkeletonShowcase: View {
    @State private var kind: SkeletonComposition = .postPlaceholder
    @State private var lineCount: Int = 3
    @State private var blockHeight: Int = 120

    var body: some View {
        PlaygroundScaffold(
            preview: {
                switch kind {
                case .lines:
                    VStack(alignment: .leading, spacing: PrismaSpacing.sp2) {
                        ForEach(0..<max(lineCount, 1), id: \.self) { i in
                            let widths: [CGFloat] = [240, 200, 220, 180]
                            PrismaSkeletonLine().frame(width: widths[i % widths.count], height: 12)
                        }
                    }
                case .card:
                    PrismaSkeletonBlock(cornerRadius: 12).frame(maxWidth: .infinity).frame(height: CGFloat(blockHeight))
                case .postPlaceholder:
                    HStack(alignment: .top, spacing: PrismaSpacing.sp3) {
                        PrismaSkeletonCircle().frame(width: 40, height: 40)
                        VStack(alignment: .leading, spacing: PrismaSpacing.sp2) {
                            PrismaSkeletonLine().frame(width: 150, height: 14)
                            PrismaSkeletonLine().frame(width: 240, height: 12)
                            PrismaSkeletonLine().frame(width: 200, height: 12)
                        }
                        Spacer()
                    }
                }
            },
            knobs: {
                EnumKnobRow(label: "Composition", value: $kind, values: SkeletonComposition.allCases, optionLabel: { $0.rawValue })
                IntKnobRow(label: "Line count (Lines)", value: $lineCount, range: 1...6)
                IntKnobRow(label: "Block height (Card)", value: $blockHeight, range: 60...240, step: 20)
            },
            code: {
                switch kind {
                case .lines: return "ForEach(0..<\(lineCount)) { _ in\n    PrismaSkeletonLine().frame(height: 12)\n}"
                case .card: return "PrismaSkeletonBlock(cornerRadius: 12).frame(height: \(blockHeight))"
                case .postPlaceholder: return "HStack {\n    PrismaSkeletonCircle().frame(width: 40, height: 40)\n    VStack { /* skeleton lines */ }\n}"
                }
            },
            pagerStates: [
                AnyPlaygroundState("Line") { PrismaSkeletonLine().frame(width: 220, height: 12) },
                AnyPlaygroundState("Circle") { PrismaSkeletonCircle().frame(width: 40, height: 40) },
                AnyPlaygroundState("Block") { PrismaSkeletonBlock(cornerRadius: 12).frame(width: 220, height: 80) }
            ],
            a11yReport: A11yReport(
                role: "Decorative (hidden from a11y tree)",
                minTouchTarget: "n/a — skeletons are decorative",
                screenReader: "Marked invisibleToUser so TalkBack and VoiceOver skip the placeholder entirely. Pair with a sibling polite announcement (\"Loading projects…\") so AT users know content is on the way.",
                voiceControl: "No spoken targets — the skeleton has no labels. The user interacts with the loaded content once the skeleton swaps out.",
                keyboard: "Skeleton is not focusable. Once real content arrives, focus / live region should pick it up automatically (place focus on the heading or fire a status message).",
                contrast: "Skeleton fill uses surface.sunken with a subtle shimmer ramp. Reduced-motion preference disables the shimmer entirely; the placeholder remains visible against surface.base at 3:1+.",
                touchTarget: "n/a — skeletons are static placeholders.",
                wcagQuote: "Animation triggered by interaction can be disabled, unless the animation is essential to the functionality or the information being conveyed.",
                wcagRef: "2.3.3 Animation from Interactions, Level AAA"
            )
        )
    }
}

private enum BadgeShape: String, CaseIterable, Hashable { case count = "Count", dot = "Dot" }

struct BadgeShowcase: View {
    @State private var shape: BadgeShape = .count
    @State private var count: Int = 12
    @State private var status: PrismaBadgeStatus = .accent

    var body: some View {
        PlaygroundScaffold(
            preview: {
                switch shape {
                case .count: PrismaCountBadge(count: count, status: status)
                case .dot: PrismaDotBadge(status: status)
                }
            },
            knobs: {
                EnumKnobRow(label: "Shape", value: $shape, values: BadgeShape.allCases, optionLabel: { $0.rawValue })
                IntKnobRow(label: "Count", value: $count, range: 0...250)
                EnumKnobRow(label: "Status", value: $status, values: [.accent, .success, .warning, .danger, .info], optionLabel: { String(describing: $0) })
            },
            code: {
                switch shape {
                case .count: return "PrismaCountBadge(count: \(count), status: .\(String(describing: status)))"
                case .dot: return "PrismaDotBadge(status: .\(String(describing: status)))"
                }
            },
            pagerStates: [
                AnyPlaygroundState("Single") { PrismaCountBadge(count: 1) },
                AnyPlaygroundState("Two-digit") { PrismaCountBadge(count: 12) },
                AnyPlaygroundState("Cap (99+)") { PrismaCountBadge(count: 250) },
                AnyPlaygroundState("Success") { PrismaCountBadge(count: 3, status: .success) },
                AnyPlaygroundState("Warning") { PrismaCountBadge(count: 7, status: .warning) },
                AnyPlaygroundState("Danger") { PrismaCountBadge(count: 99, status: .danger) },
                AnyPlaygroundState("Dot") { PrismaDotBadge(status: .accent) }
            ],
            a11yReport: A11yReport(
                role: "Decorative — the carrier (icon, button, tab) owns semantics",
                minTouchTarget: "n/a — badges are not interactive themselves",
                screenReader: "Badge alone is meaningless to a screen reader. Append the count and meaning to the parent's contentDescription — e.g. \"Inbox, 5 unread messages\". The status colour is read by AT only via the parent label.",
                voiceControl: "No spoken target. The carrier underneath the badge is the targetable element — Voice Access taps the icon, not the badge.",
                keyboard: "Badge is not focusable. The carrier owns focus; pressing it should reveal the underlying content (e.g. opening the unread inbox).",
                contrast: "Status badges meet 3:1 non-text contrast against the carrier (icon button, tab). The numeric text inside meets 4.5:1 against the badge fill in light + dark themes.",
                touchTarget: "n/a. The carrier underneath must respect 48 × 48 dp / 44 × 44 pt; the badge is purely visual decoration.",
                wcagQuote: "The visual presentation of the following have a contrast ratio of at least 3:1 against adjacent color(s): User Interface Components: Visual information required to identify user interface components and states.",
                wcagRef: "1.4.11 Non-text Contrast, Level AA"
            )
        )
    }
}

struct EmptyStateShowcase: View {
    @State private var title: String = "No projects yet"
    @State private var emptyDescription: String = "When you create a project, it'll show up here."
    @State private var actionLabel: String = "Create project"
    @State private var withAction: Bool = true

    var body: some View {
        PlaygroundScaffold(
            preview: {
                if withAction {
                    PrismaEmptyState(
                        title: title.isEmpty ? "Title" : title,
                        description: emptyDescription.isEmpty ? nil : emptyDescription,
                        action: { PrismaButton(actionLabel.isEmpty ? "Action" : actionLabel) {} }
                    )
                } else {
                    PrismaEmptyState(
                        title: title.isEmpty ? "Title" : title,
                        description: emptyDescription.isEmpty ? nil : emptyDescription,
                        action: { EmptyView() }
                    )
                }
            },
            knobs: {
                StringKnobRow(label: "Title", value: $title)
                StringKnobRow(label: "Description", value: $emptyDescription)
                BoolKnobRow(label: "With action", value: $withAction)
                StringKnobRow(label: "Action label", value: $actionLabel)
            },
            code: {
                var s = "PrismaEmptyState(\n    title: \"\(title)\""
                if !emptyDescription.isEmpty { s += ",\n    description: \"\(emptyDescription)\"" }
                if withAction { s += ",\n    action: { PrismaButton(\"\(actionLabel)\") { /* … */ } }" }
                s += "\n)"
                return s
            },
            pagerStates: [
                AnyPlaygroundState("Default") {
                    PrismaEmptyState(
                        title: "No projects yet",
                        description: "When you create a project, it'll show up here.",
                        action: { PrismaButton("Create project") {} }
                    )
                },
                AnyPlaygroundState("Just a title") {
                    PrismaEmptyState(title: "Nothing here", action: { EmptyView() })
                }
            ],
            a11yReport: A11yReport(
                role: "Heading + body + optional action",
                minTouchTarget: "Action button 48 × 48 dp / 44 × 44 pt",
                screenReader: "Title carries heading semantics so screen reader users can jump to it via heading-by-heading navigation. Body description is read after the title. The optional action button retains its own Role.Button.",
                voiceControl: "Action label is a spoken target (\"Tap Create project\"). The verb-led label is intentional — it tells voice users exactly what saying it will do.",
                keyboard: "Tab moves directly to the action when present; without an action the empty state is just a static heading + paragraph and is non-focusable.",
                contrast: "Title uses text.primary (10:1+); description uses text.secondary (5.4:1) — both above the 4.5:1 body floor. The illustration (when present) is decorative only.",
                touchTarget: "Action button respects component-level 48 × 48 dp / 44 × 44 pt. The empty-state container is a static layout; only the action is interactive.",
                wcagQuote: "Headings and labels describe topic or purpose.",
                wcagRef: "2.4.6 Headings and Labels, Level AA"
            )
        )
    }
}

struct DrawerShowcase: View {
    @State private var open = false
    @State private var title: String = "Prisma"
    @State private var drawerBody: String = "On iOS, side-sheets are typically rendered via NavigationSplitView. This is a sheet-style approximation."
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        PlaygroundScaffold(
            preview: {
                VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
                    PrismaButton("Open drawer") { open = true }
                    Text("Drawer slides in from the side — open to view.")
                        .font(PrismaTypography.bodySm.font)
                        .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                }
            },
            knobs: {
                StringKnobRow(label: "Drawer title", value: $title)
                StringKnobRow(label: "Drawer body", value: $drawerBody)
            },
            code: { "// On iOS, side-sheets use NavigationSplitView or .sheet:\n.sheet(isPresented: $open) { /* drawer content */ }" },
            // No states pager — drawer is trigger-based; the live preview is enough.
            a11yReport: A11yReport(
                role: "Modal sheet (off-canvas navigation)",
                minTouchTarget: "Trigger 48 × 48 dp / 44 × 44 pt; drawer items 48 / 44 pt",
                screenReader: "When open, focus traps inside the drawer and the main content goes inert. TalkBack / VoiceOver explore stays inside the drawer until it closes; on close, focus returns to the trigger automatically.",
                voiceControl: "Each drawer item has its own spoken label. Voice Access supports \"Tap [item label]\" — saying the visible name navigates without precise targeting.",
                keyboard: "Tab cycles within the drawer when open. ESC closes; swipe / scrim dismiss are the touch equivalents. Tab on the closed state moves over the trigger like any other button.",
                contrast: "Drawer panel uses surface.raised at 100% opacity. Scrim is surface.inverse @ 64% so background content stays visible while clearly de-emphasized. Drawer item rows respect text.primary 10:1+.",
                touchTarget: "Trigger respects component-level 48 × 48 / 44 × 44. Drawer rows are also at the same minimum — common nav-row pattern.",
                wcagQuote: "If keyboard focus can be moved to a component … then focus can be moved away from that component using only a keyboard interface.",
                wcagRef: "2.1.2 No Keyboard Trap, Level A"
            )
        )
        .sheet(isPresented: $open) {
            VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
                Text(title).font(PrismaTypography.headlineSm.font)
                    .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                Text(drawerBody).font(PrismaTypography.bodyMd.font)
                    .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                PrismaButton("Close", variant: .secondary) { open = false }
            }
            .padding(PrismaSpacing.sp7)
            .presentationDetents([.medium, .large])
        }
    }
}

// MARK: - Navigation

struct TabsShowcase: View {
    @State private var tabsCsv: String = "Overview, Activity, Settings, Billing"
    @State private var selectedIdx: Int = 0
    @Environment(\.colorScheme) private var scheme

    private var tabs: [String] {
        let parsed = tabsCsv.split(separator: ",").map { $0.trimmingCharacters(in: .whitespaces) }.filter { !$0.isEmpty }
        return parsed.isEmpty ? ["Tab"] : parsed
    }

    var body: some View {
        let safeIdx = min(max(selectedIdx, 0), tabs.count - 1)
        let safeTabs = tabs
        return PlaygroundScaffold(
            preview: {
                VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
                    PrismaTabs(
                        tabs: safeTabs,
                        selected: Binding(
                            get: { safeTabs[safeIdx] },
                            set: { newValue in selectedIdx = safeTabs.firstIndex(of: newValue) ?? 0 }
                        )
                    )
                    Text("Active tab: \(safeTabs[safeIdx])")
                        .font(PrismaTypography.bodyMd.font)
                        .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                }
            },
            knobs: {
                StringKnobRow(
                    label: "Tabs (comma separated)",
                    value: $tabsCsv,
                    placeholder: "Overview, Activity, Settings",
                    helper: "Edit to add or remove tabs from the live preview."
                )
                IntKnobRow(label: "Selected index", value: $selectedIdx, range: 0...max(safeTabs.count - 1, 0))
            },
            code: {
                let joined = safeTabs.map { "\"\($0)\"" }.joined(separator: ", ")
                return "PrismaTabs(\n    tabs: [\(joined)],\n    selected: $selected\n)"
            },
            pagerStates: [
                AnyPlaygroundState("2 tabs") { TabsState(initial: ["Inbox", "Archive"]) },
                AnyPlaygroundState("4 tabs") { TabsState(initial: ["Overview", "Activity", "Settings", "Billing"]) },
                AnyPlaygroundState("Last selected") { TabsState(initial: ["One", "Two", "Three"], selected: "Three") }
            ],
            a11yReport: A11yReport(
                role: "Tab (inside selectableGroup) — for switching between peer views",
                minTouchTarget: "48 dp / 44 pt height per tab",
                screenReader: "Each tab carries Role.Tab; the row is a selectableGroup so screen readers announce \"selected N of M\". Switching tabs immediately reads the new active tab; selection haptic fires only on change, not re-tap.",
                voiceControl: "Voice Access targets each tab by visible label (\"Tap Settings\"). The row is a single selectable group so tab order is preserved across orientations.",
                keyboard: "Tab moves into the row, then arrow keys cycle between tabs. Selection follows focus so a screen reader user hears each tab's content as they move. Home / End jump to first / last.",
                contrast: "Active tab indicator uses accent.default at 4.6:1 against the row background. Active label uses text.primary (10:1+); inactive uses text.secondary (5.4:1) — both above the 4.5:1 body floor.",
                touchTarget: "Each tab is at least 48 dp / 44 pt tall. On narrow widths, tabs scroll horizontally — they don't compress below the minimum.",
                wcagQuote: "More than one way is available to locate a Web page within a set of Web pages, except where the Web page is the result of, or a step in, a process.",
                wcagRef: "2.4.3 Focus Order, Level A"
            )
        )
    }
}

private struct TabsState: View {
    let initial: [String]
    @State var selected: String

    init(initial: [String], selected: String? = nil) {
        self.initial = initial
        _selected = State(initialValue: selected ?? initial.first ?? "")
    }

    var body: some View {
        PrismaTabs(tabs: initial, selected: $selected)
    }
}

struct ChipShowcase: View {
    @State private var label: String = "Android"
    @State private var selected: Bool = true
    @State private var variant: PrismaChipVariant = .filter
    @State private var enabled: Bool = true
    @State private var withDismiss: Bool = false

    var body: some View {
        PlaygroundScaffold(
            preview: {
                PrismaChip(
                    label: label.isEmpty ? "Chip" : label,
                    selected: selected,
                    variant: variant,
                    enabled: enabled,
                    onTap: { selected.toggle() },
                    onDismiss: withDismiss ? {} : nil
                )
            },
            knobs: {
                StringKnobRow(label: "Label", value: $label)
                EnumKnobRow(label: "Variant", value: $variant, values: [.filter, .suggestion], optionLabel: { String(describing: $0) })
                BoolKnobRow(label: "Selected", value: $selected)
                BoolKnobRow(label: "Enabled", value: $enabled)
                BoolKnobRow(label: "With dismiss (×)", value: $withDismiss)
            },
            code: {
                var s = "PrismaChip(\n    label: \"\(label)\",\n    selected: \(selected)"
                if variant != .filter { s += ",\n    variant: .\(String(describing: variant))" }
                if !enabled { s += ",\n    enabled: false" }
                s += ",\n    onTap: { /* … */ }"
                if withDismiss { s += ",\n    onDismiss: { /* … */ }" }
                s += "\n)"
                return s
            },
            pagerStates: [
                AnyPlaygroundState("Filter (toggle)") { ToggleableChipDemo(label: "Android") },
                AnyPlaygroundState("iOS (toggle)") { ToggleableChipDemo(label: "iOS", initial: false) },
                AnyPlaygroundState("Suggestion") { PrismaChip(label: "Trending", variant: .suggestion, onTap: {}) },
                AnyPlaygroundState("Disabled") { PrismaChip(label: "Locked", enabled: false, onTap: {}) }
            ],
            a11yReport: A11yReport(
                role: "Button (filter / suggestion) — chip groups in selectableGroup for multi-select",
                minTouchTarget: "48 dp / 44 pt height; × button independently 48 × 48 dp / 44 × 44 pt",
                screenReader: "Selected state is announced as part of the role (\"Selected\" / \"Not selected\"). Haptic feedback fires on toggle so the change is felt as well as heard. Input chip's × is a separate action labelled \"Remove <chip>\".",
                voiceControl: "Voice Access / Voice Control target the chip's visible label. \"Tap iOS\" toggles it; \"Tap Remove iOS\" hits the × on input chips.",
                keyboard: "Chip is a button — Tab focuses, Space / Enter activates. The × on input chips is independently focusable so Delete on the chip triggers removal directly.",
                contrast: "Selected fill uses accent.default (4.6:1 against surface); unselected outline uses border.default at 3:1 non-text contrast. Label text meets 4.5:1 body in both states.",
                touchTarget: "Each chip is 32 dp tall but lives in a 48 dp tap target. The × is independently 48 × 48 / 44 × 44 so removal doesn't accidentally toggle the chip.",
                wcagQuote: "For all user interface components … the name and role can be programmatically determined; states, properties, and values that can be set by the user can be programmatically set.",
                wcagRef: "4.1.2 Name, Role, Value, Level A"
            )
        )
    }
}

private struct ToggleableChipDemo: View {
    let label: String
    @State var on: Bool

    init(label: String, initial: Bool = true) {
        self.label = label
        _on = State(initialValue: initial)
    }

    var body: some View {
        PrismaChip(label: label, selected: on, onTap: { on.toggle() })
    }
}

private struct SelectableListItemDemo: View {
    @State private var on: Bool = false

    var body: some View {
        PrismaListItem(
            primary: "Aanya Patel",
            secondary: "aanya@example.com",
            selected: on,
            onTap: { on.toggle() },
            leading: { AnyView(PrismaAvatar(seed: "Aanya Patel", size: .sm, status: .away)) },
            trailing: { AnyView(EmptyView()) }
        )
    }
}

struct CommandPaletteShowcase: View {
    @State private var open = false
    @State private var lastInvoked: String = "(none)"
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        PlaygroundScaffold(
            preview: {
                VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
                    PrismaButton("Open command palette ⌘K") { open = true }
                    Text("Last invoked: \(lastInvoked)")
                        .font(PrismaTypography.bodySm.font)
                        .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                }
            },
            code: { "PrismaCommandPalette(\n    commands: [\n        PrismaCommand(label: \"Open Typography\", group: \"Foundations\") { /* … */ },\n        PrismaCommand(label: \"Toggle theme\", group: \"Actions\") { /* … */ }\n    ],\n    onDismiss: { open = false }\n)" },
            // No states pager — palette is trigger-based; the live preview is enough.
            a11yReport: A11yReport(
                role: "Combobox (input + listbox)",
                minTouchTarget: "Each command row 48 dp / 44 pt",
                screenReader: "Filtering announces \"N results\" via a polite live region as the user types. Section headers (\"Foundations\", \"Actions\") use heading semantics so screen reader users can skip between them.",
                voiceControl: "Voice Access / Voice Control target each command's visible label. \"Tap Toggle theme\" works without the user knowing where the command sits in the list.",
                keyboard: "Cmd / Ctrl-K opens the palette; arrow keys navigate; Enter activates the focused command; ESC closes the palette and returns focus to the trigger. The full flow is keyboard-only.",
                contrast: "Active row highlight uses surface.sunken (3:1 against surface.raised). Section headers use text.tertiary (4.5:1+). Match-emphasis on filtered text uses accent.default at 4.6:1.",
                touchTarget: "Each row is 48 dp / 44 pt tall. Section headers are non-interactive; rows hold the entire row's hit area, not just the label width.",
                wcagQuote: "All functionality of the content is operable through a keyboard interface without requiring specific timings for individual keystrokes.",
                wcagRef: "2.1.1 Keyboard, Level A"
            )
        )
        .sheet(isPresented: $open) {
            PrismaCommandPalette(
                commands: [
                    PrismaCommand(label: "Open Typography", group: "Foundations") { lastInvoked = "Typography"; open = false },
                    PrismaCommand(label: "Open Colors", group: "Foundations") { lastInvoked = "Colors"; open = false },
                    PrismaCommand(label: "Toggle theme", group: "Actions") { lastInvoked = "Toggle theme"; open = false },
                    PrismaCommand(label: "Search components", group: "Navigation") { lastInvoked = "Search"; open = false },
                    PrismaCommand(label: "Open Button", group: "Components") { lastInvoked = "Button"; open = false },
                    PrismaCommand(label: "Open TextField", group: "Components") { lastInvoked = "TextField"; open = false }
                ],
                onDismiss: { open = false }
            )
            .presentationDetents([.large])
        }
    }
}

struct PaginationShowcase: View {
    @State private var page: Int = 1
    @State private var pageCount: Int = 12
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        let safeCount = max(pageCount, 1)
        let safePage = min(max(page, 1), safeCount)
        return PlaygroundScaffold(
            preview: {
                VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
                    PrismaPagination(page: Binding(get: { safePage }, set: { page = $0 }), pageCount: safeCount)
                    Text("Page \(safePage) of \(safeCount)")
                        .font(PrismaTypography.bodyMd.font)
                        .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                }
            },
            knobs: {
                IntKnobRow(label: "Total pages", value: $pageCount, range: 1...50)
                IntKnobRow(label: "Current page", value: $page, range: 1...safeCount)
            },
            code: { "PrismaPagination(page: $page, pageCount: \(safeCount))" },
            pagerStates: [
                AnyPlaygroundState("Few pages") { PaginationState(initial: 2, total: 5) },
                AnyPlaygroundState("Many pages") { PaginationState(initial: 12, total: 30) },
                AnyPlaygroundState("First page") { PaginationState(initial: 1, total: 10) },
                AnyPlaygroundState("Last page") { PaginationState(initial: 10, total: 10) }
            ],
            a11yReport: A11yReport(
                role: "Navigation (each control is a Button)",
                minTouchTarget: "Each page button 48 × 48 dp / 44 × 44 pt",
                screenReader: "Wrap the row in a Role.Navigation labelled \"Pagination\". The current page exposes a selected state; previous / next arrows announce \"disabled\" at the edges so users know they've reached the boundary.",
                voiceControl: "Each page number and arrow is independently spoken. \"Tap Next page\" or \"Tap 5\" both work — voice users don't need to know which controls are present.",
                keyboard: "Tab moves through the row in document order. Enter / Space activates a page. Arrow Left / Right (when supported) jumps between adjacent pages without losing focus.",
                contrast: "Current page indicator uses accent.default (4.6:1). Disabled previous / next arrows use text.tertiary at exactly the 3:1 non-text-contrast floor so the disabled state is communicated visually as well as via role.",
                touchTarget: "Each button is 48 × 48 dp / 44 × 44 pt. Ellipsis is decorative (invisibleToUser) so it doesn't get focused. Spacing between buttons keeps adjacent taps distinct.",
                wcagQuote: "More than one way is available to locate a Web page within a set of Web pages, except where the Web page is the result of, or a step in, a process.",
                wcagRef: "2.4.5 Multiple Ways, Level AA"
            )
        )
    }
}

private struct PaginationState: View {
    @State var page: Int
    let total: Int
    init(initial: Int, total: Int) { _page = State(initialValue: initial); self.total = total }
    var body: some View { PrismaPagination(page: $page, pageCount: total) }
}

struct BreadcrumbShowcase: View {
    @State private var pathCsv: String = "Home, Components, Inputs, Button"

    private var items: [PrismaBreadcrumbItem] {
        let parts = pathCsv.split(separator: ",").map { $0.trimmingCharacters(in: .whitespaces) }.filter { !$0.isEmpty }
        return parts.enumerated().map { idx, label in
            idx == parts.count - 1
                ? PrismaBreadcrumbItem(label: label)
                : PrismaBreadcrumbItem(label: label, onTap: {})
        }
    }

    var body: some View {
        PlaygroundScaffold(
            preview: { PrismaBreadcrumb(items: items) },
            knobs: {
                StringKnobRow(
                    label: "Path (comma separated, last is current)",
                    value: $pathCsv,
                    placeholder: "Home, Section, Page"
                )
            },
            code: {
                let parts = pathCsv.split(separator: ",").map { $0.trimmingCharacters(in: .whitespaces) }.filter { !$0.isEmpty }
                let pieces = parts.enumerated().map { idx, label in
                    idx == parts.count - 1
                        ? "        PrismaBreadcrumbItem(label: \"\(label)\")"
                        : "        PrismaBreadcrumbItem(label: \"\(label)\", onTap: { /* … */ })"
                }
                let joined = pieces.joined(separator: ",\n")
                return "PrismaBreadcrumb(items: [\n\(joined)\n])"
            },
            pagerStates: [
                AnyPlaygroundState("3 levels") { PrismaBreadcrumb(items: [PrismaBreadcrumbItem(label: "Home", onTap: {}), PrismaBreadcrumbItem(label: "Settings", onTap: {}), PrismaBreadcrumbItem(label: "Profile")]) },
                AnyPlaygroundState("4 levels") { PrismaBreadcrumb(items: [PrismaBreadcrumbItem(label: "Home", onTap: {}), PrismaBreadcrumbItem(label: "Components", onTap: {}), PrismaBreadcrumbItem(label: "Inputs", onTap: {}), PrismaBreadcrumbItem(label: "Button")]) }
            ],
            a11yReport: A11yReport(
                role: "Navigation (ordered list of Buttons; current page is plain text)",
                minTouchTarget: "48 × 48 dp / 44 × 44 pt per crumb",
                screenReader: "Wrap in a Role.Navigation labelled \"Breadcrumb\" so screen readers announce the navigation context. The last item is the current page rendered as plain text — not a link — so AT users know they've reached their destination.",
                voiceControl: "Each crumb is a spoken target by visible label (\"Tap Home\"). Separator slashes are decorative (invisibleToUser) so they don't pollute the spoken path.",
                keyboard: "Tab moves through the crumbs in document order. The current page is non-focusable; only ancestor links accept focus.",
                contrast: "Link crumbs use text.link (4.6:1+); the current page uses text.primary (10:1+). Separators are text.tertiary at 4.5:1.",
                touchTarget: "Each linkable crumb has a 48 × 48 dp / 44 × 44 pt hit area despite the visible text being smaller. Spacing between crumbs prevents accidental taps on the wrong level.",
                wcagQuote: "Information about the user's location within a set of Web pages is available.",
                wcagRef: "2.4.8 Location, Level AAA"
            )
        )
    }
}

struct WizardShowcase: View {
    @State private var stepsCsv: String = "Account, Profile, Plan, Billing"
    @State private var step: Int = 1

    private var steps: [String] {
        let parsed = stepsCsv.split(separator: ",").map { $0.trimmingCharacters(in: .whitespaces) }.filter { !$0.isEmpty }
        return parsed.isEmpty ? ["Step 1"] : parsed
    }

    var body: some View {
        let s = steps
        let safeStep = min(max(step, 0), s.count - 1)
        return PlaygroundScaffold(
            preview: {
                VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
                    PrismaWizardSteps(steps: s, activeIndex: safeStep)
                    HStack(spacing: PrismaSpacing.sp3) {
                        PrismaButton("Previous", variant: .secondary, enabled: safeStep > 0) { step = safeStep - 1 }
                        PrismaButton("Next", enabled: safeStep < s.count - 1) { step = safeStep + 1 }
                    }
                }
            },
            knobs: {
                StringKnobRow(label: "Steps (comma separated)", value: $stepsCsv)
                IntKnobRow(label: "Active step index", value: $step, range: 0...max(s.count - 1, 0))
            },
            code: {
                let joined = s.map { "\"\($0)\"" }.joined(separator: ", ")
                return "PrismaWizardSteps(steps: [\(joined)], activeIndex: \(safeStep))"
            },
            pagerStates: [
                AnyPlaygroundState("3 steps, on 1") { PrismaWizardSteps(steps: ["Account", "Profile", "Done"], activeIndex: 1) },
                AnyPlaygroundState("Last step") { PrismaWizardSteps(steps: ["A", "B", "C"], activeIndex: 2) }
            ],
            a11yReport: A11yReport(
                role: "Step indicator (progress with current/completed/upcoming states)",
                minTouchTarget: "n/a (non-interactive in catalogue; wired to nav externally)",
                screenReader: "Active step exposes the equivalent of aria-current=\"step\" so screen readers announce \"current step\". Completed steps announce as \"completed\"; future steps as \"upcoming\". Pair with a heading (\"Step 2 of 4: Profile\") for clearest navigation.",
                voiceControl: "Step labels are spoken targets when the steps are interactive (e.g. wired to nav). \"Tap Profile\" jumps to that step; current step is non-tappable.",
                keyboard: "When tied to nav, Tab moves through visited steps in document order. Forward steps are typically disabled until the user satisfies prerequisites — the disabled state is announced.",
                contrast: "Current-step ring uses accent.default (4.6:1); completed steps use accent.subtle with a check icon (3:1+); upcoming steps use border.default at 3:1 non-text contrast. Labels meet 4.5:1 body.",
                touchTarget: "When step indicators are tappable, each is 48 × 48 dp / 44 × 44 pt. The connector lines are decorative (invisibleToUser).",
                wcagQuote: "For Web pages that cause legal commitments or financial transactions for the user to occur … submissions are reversible, checked for input errors and the user is provided with an opportunity to correct them, or confirmed.",
                wcagRef: "3.3.4 Error Prevention (Legal, Financial, Data), Level AA"
            )
        )
    }
}

// MARK: - Data display

struct CardShowcase: View {
    @State private var title: String = "Project Aurora"
    @State private var bodyText: String = "Auto-tuning service for production workloads. Reduces p95 latency by an average of 18%."
    @State private var variant: PrismaCardVariant = .outlined
    @State private var clickable: Bool = false
    @State private var withCta: Bool = true
    @State private var clickCount: Int = 0
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        PlaygroundScaffold(
            preview: {
                PrismaCard(variant: variant, onTap: clickable ? { clickCount += 1 } : nil) {
                    VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
                        Text(title.isEmpty ? "Card title" : title)
                            .font(PrismaTypography.titleMd.font)
                            .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                        Text(bodyText.isEmpty ? "Card body" : bodyText)
                            .font(PrismaTypography.bodyMd.font)
                            .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                        if clickable {
                            Text("Card taps: \(clickCount)")
                                .font(PrismaTypography.labelSm.font)
                                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                        }
                        if withCta {
                            HStack(spacing: PrismaSpacing.sp2) {
                                PrismaButton("Open") {}
                                PrismaButton("Share", variant: .ghost) {}
                            }
                        }
                    }
                }
            },
            knobs: {
                StringKnobRow(label: "Title", value: $title)
                StringKnobRow(label: "Body", value: $bodyText, placeholder: "Description")
                EnumKnobRow(
                    label: "Variant",
                    value: $variant,
                    values: [.outlined, .elevated, .filled],
                    optionLabel: { String(describing: $0) }
                )
                BoolKnobRow(label: "Clickable (whole card)", value: $clickable)
                BoolKnobRow(label: "With CTA buttons", value: $withCta)
            },
            code: {
                var s = "PrismaCard("
                if variant != .outlined { s += "variant: .\(String(describing: variant))" }
                if clickable { s += (variant != .outlined ? ", " : "") + "onTap: { /* … */ }" }
                s += ") {\n    VStack { Text(\"\(title)\"); Text(\"\(bodyText.prefix(40))…\") }\n}"
                return s
            },
            pagerStates: [
                AnyPlaygroundState("Elevated") {
                    PrismaCard(variant: .elevated) {
                        Text("Casts a shadow over the page. Use for emphasis.")
                            .font(PrismaTypography.bodySm.font)
                            .foregroundStyle(PrismaSemanticColors.textSecondary.themed(.light))
                    }
                },
                AnyPlaygroundState("Outlined") {
                    PrismaCard(variant: .outlined) {
                        Text("1pt subtle border. The most-used card.")
                            .font(PrismaTypography.bodySm.font)
                            .foregroundStyle(PrismaSemanticColors.textSecondary.themed(.light))
                    }
                },
                AnyPlaygroundState("Filled") {
                    PrismaCard(variant: .filled) {
                        Text("Sunken surface, quieter than elevated.")
                            .font(PrismaTypography.bodySm.font)
                            .foregroundStyle(PrismaSemanticColors.textSecondary.themed(.light))
                    }
                }
            ],
            a11yReport: A11yReport(
                role: "Container (interactive cards = Role.Button)",
                minTouchTarget: "48 × 48 dp / 44 × 44 pt when whole-card clickable",
                screenReader: "Static cards are read as a container — TalkBack and VoiceOver explore the content as separate items. Interactive cards merge descendants so the whole card reads as a single \"button\" with the title as the label.",
                voiceControl: "When the whole card is clickable, the visible title is the spoken target. Inner buttons (e.g. \"Open\") remain their own targets so users can act on either the card or a specific control.",
                keyboard: "Static cards are not focusable. Interactive cards are a single Tab stop; if the card has inner buttons too, those are separate focusable elements — choose either pattern, not both.",
                contrast: "Outlined card uses border.subtle at 3:1 non-text contrast against the surface. Elevated cards use a subtle shadow plus surface.raised (no border) — the elevation difference reads in light theme; in dark theme a 1 pt border supplements the shadow.",
                touchTarget: "Interactive cards are at least 48 × 48 dp / 44 × 44 pt. Inner action buttons retain their own minimums; padding around them keeps adjacent buttons from accidentally registering each other's taps.",
                wcagQuote: "For all user interface components … the name and role can be programmatically determined; states, properties, and values that can be set by the user can be programmatically set.",
                wcagRef: "4.1.2 Name, Role, Value, Level A"
            )
        )
    }
}

private enum ListItemLeadingKind: String, CaseIterable, Hashable {
    case none = "None"
    case avatar = "Avatar"
    case icon = "Icon"
}

private enum ListItemTrailingKind: String, CaseIterable, Hashable {
    case none = "None"
    case chevron = "Chevron"
    case badge = "Badge"
}

struct ListItemShowcase: View {
    @State private var primary: String = "Maya Chen"
    @State private var secondary: String = "maya@example.com"
    @State private var leadingKind: ListItemLeadingKind = .avatar
    @State private var trailingKind: ListItemTrailingKind = .chevron
    @State private var selected: Bool = false
    @State private var clickable: Bool = true
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        PlaygroundScaffold(
            preview: {
                PrismaListItem(
                    primary: primary.isEmpty ? "Primary text" : primary,
                    secondary: secondary.isEmpty ? nil : secondary,
                    selected: selected,
                    onTap: clickable ? {} : nil,
                    leading: {
                        switch leadingKind {
                        case .none: EmptyView()
                        case .avatar: AnyView(PrismaAvatar(seed: primary.isEmpty ? "?" : primary, size: .sm, status: .online))
                        case .icon: AnyView(
                            Image(prisma: .settings).renderingMode(.template).resizable()
                                .frame(width: 20, height: 20)
                                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                        )
                        }
                    },
                    trailing: {
                        switch trailingKind {
                        case .none: EmptyView()
                        case .chevron: AnyView(
                            Image(prisma: .chevronRight).renderingMode(.template).resizable()
                                .frame(width: 14, height: 14)
                                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                        )
                        case .badge: AnyView(PrismaCountBadge(count: 4, status: .accent))
                        }
                    }
                )
            },
            knobs: {
                StringKnobRow(label: "Primary", value: $primary)
                StringKnobRow(label: "Secondary", value: $secondary, placeholder: "Optional sub-text")
                EnumKnobRow(label: "Leading", value: $leadingKind, values: ListItemLeadingKind.allCases, optionLabel: { $0.rawValue })
                EnumKnobRow(label: "Trailing", value: $trailingKind, values: ListItemTrailingKind.allCases, optionLabel: { $0.rawValue })
                BoolKnobRow(label: "Clickable", value: $clickable)
                BoolKnobRow(label: "Selected", value: $selected)
            },
            code: {
                var s = "PrismaListItem(\n    primary: \"\(primary)\""
                if !secondary.isEmpty { s += ",\n    secondary: \"\(secondary)\"" }
                if selected { s += ",\n    selected: true" }
                if clickable { s += ",\n    onTap: { /* … */ }" }
                s += "\n)"
                return s
            },
            pagerStates: [
                AnyPlaygroundState("With avatar (toggle)") { SelectableListItemDemo() },
                AnyPlaygroundState("Selected") {
                    PrismaListItem(
                        primary: "Settings", secondary: "Account & preferences", selected: true, onTap: {},
                        leading: { AnyView(EmptyView()) },
                        trailing: { AnyView(EmptyView()) }
                    )
                },
                AnyPlaygroundState("With chevron") {
                    PrismaListItem(
                        primary: "Notifications", onTap: {},
                        leading: { AnyView(EmptyView()) },
                        trailing: {
                            AnyView(Image(prisma: .chevronRight).renderingMode(.template).resizable()
                                .frame(width: 14, height: 14)
                                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(.light)))
                        }
                    )
                }
            ],
            a11yReport: A11yReport(
                role: "ListItem (Role.Button when onClick provided)",
                minTouchTarget: "48 dp / 44 pt height (default); 56 dp / 72 dp variants for two-line / three-line",
                screenReader: "mergeDescendants groups primary + secondary + leading + trailing into one a11y unit so the row reads as a single \"button\" labelled with the primary text. Selected state is exposed via the selected property; trailing controls (switch, badge) keep their own roles when independently activatable.",
                voiceControl: "Voice Access targets the visible primary text (\"Tap Settings\"). Trailing controls retain their own labels — \"Tap Toggle\" hits the trailing switch directly without activating the row.",
                keyboard: "Tab focuses the row when clickable. Space / Enter activates it. Trailing controls are independently focusable so a switch can be toggled without entering the row's destination.",
                contrast: "Primary text uses text.primary (10:1+); secondary uses text.secondary (5.4:1) — both well above 4.5:1 body. Selected background uses surface.sunken (3:1 against base).",
                touchTarget: "Whole row is 48 / 56 / 72 dp tall depending on density. Trailing controls (switch, button) keep their own 48 × 48 dp / 44 × 44 pt minimums.",
                wcagQuote: "The size of the target for pointer inputs is at least 24 by 24 CSS pixels, except where the target is exempted.",
                wcagRef: "2.5.8 Target Size (Minimum), Level AA (WCAG 2.2)"
            )
        )
    }
}

struct AvatarShowcase: View {
    @State private var seed: String = "Maya Chen"
    @State private var size: PrismaAvatarSize = .lg
    @State private var status: PrismaAvatarStatus = .online

    var body: some View {
        PlaygroundScaffold(
            preview: {
                PrismaAvatar(seed: seed.isEmpty ? "?" : seed, size: size, status: status)
            },
            knobs: {
                StringKnobRow(
                    label: "Seed (name)",
                    value: $seed,
                    placeholder: "Full name",
                    helper: "Initials and background colour are derived deterministically from this string."
                )
                EnumKnobRow(
                    label: "Size",
                    value: $size,
                    values: [.xs, .sm, .default, .lg, .xl],
                    optionLabel: { String(describing: $0) }
                )
                EnumKnobRow(
                    label: "Status",
                    value: $status,
                    values: [.none, .online, .away, .busy, .offline],
                    optionLabel: { String(describing: $0) }
                )
            },
            code: {
                var s = "PrismaAvatar(seed: \"\(seed)\""
                if size != .default { s += ", size: .\(String(describing: size))" }
                if status != .none { s += ", status: .\(String(describing: status))" }
                s += ")"
                return s
            },
            pagerStates: [
                AnyPlaygroundState("Sizes") {
                    HStack(spacing: PrismaSpacing.sp3) {
                        PrismaAvatar(seed: "Maya Chen", size: .xs)
                        PrismaAvatar(seed: "Maya Chen", size: .sm)
                        PrismaAvatar(seed: "Maya Chen", size: .default)
                        PrismaAvatar(seed: "Maya Chen", size: .lg)
                    }
                },
                AnyPlaygroundState("Online") { PrismaAvatar(seed: "Aanya Patel", status: .online) },
                AnyPlaygroundState("Away") { PrismaAvatar(seed: "Bilal Khan", status: .away) },
                AnyPlaygroundState("Busy") { PrismaAvatar(seed: "Cara Liu", status: .busy) },
                AnyPlaygroundState("Offline") { PrismaAvatar(seed: "Dev Iyer", status: .offline) }
            ],
            a11yReport: A11yReport(
                role: "Image (with text alternative)",
                minTouchTarget: "n/a — decorative on its own; 48 × 48 / 44 × 44 when wrapped in a Button",
                screenReader: "contentDescription is the seed name plus status — \"Maya Chen, online\". Initials are derived from the seed; the status dot is rolled into the same announcement so users get name + presence in one read.",
                voiceControl: "Voice Access / Voice Control target the avatar by its accessible label when interactive. When the avatar sits inside a clickable row, the row's label wins.",
                keyboard: "Avatar itself is not focusable. When interactive (e.g. a button-wrapped avatar that opens a profile), Tab focuses, Space / Enter activates.",
                contrast: "Initials text on the seed-derived background colour is calibrated to meet 4.5:1 across all 12 generated palettes. Status dots use status.* colours at 3:1 non-text contrast against the avatar fill.",
                touchTarget: "Avatar in a Button: 48 × 48 dp / 44 × 44 pt minimum. Decorative use has no minimum but the visible size scales by token (Xs / Sm / Md / Lg).",
                wcagQuote: "All non-text content that is presented to the user has a text alternative that serves the equivalent purpose.",
                wcagRef: "1.1.1 Non-text Content, Level A"
            )
        )
    }
}

struct AvatarGroupShowcase: View {
    @State private var seedsCsv: String = "Maya Chen, Aanya Patel, Bilal Khan, Cara Liu, Dev Iyer, Eva Park"
    @State private var maxVisible: Int = 4
    @State private var size: PrismaAvatarSize = .default

    private var seeds: [String] {
        seedsCsv.split(separator: ",").map { $0.trimmingCharacters(in: .whitespaces) }.filter { !$0.isEmpty }
    }

    var body: some View {
        PlaygroundScaffold(
            preview: { PrismaAvatarGroup(seeds: seeds, size: size, max: max(maxVisible, 1)) },
            knobs: {
                StringKnobRow(label: "Seeds (comma separated)", value: $seedsCsv)
                IntKnobRow(label: "Max visible", value: $maxVisible, range: 1...8)
                EnumKnobRow(label: "Size", value: $size, values: [.xs, .sm, .default, .lg, .xl], optionLabel: { String(describing: $0) })
            },
            code: {
                let joined = seeds.map { "\"\($0)\"" }.joined(separator: ", ")
                var s = "PrismaAvatarGroup(\n    seeds: [\(joined)]"
                if size != .default { s += ",\n    size: .\(String(describing: size))" }
                if maxVisible != 4 { s += ",\n    max: \(maxVisible)" }
                s += "\n)"
                return s
            },
            pagerStates: [
                AnyPlaygroundState("Few") { PrismaAvatarGroup(seeds: ["Maya", "Aanya"]) },
                AnyPlaygroundState("Many (overflow)") { PrismaAvatarGroup(seeds: (1...8).map { "User \($0)" }, max: 4) }
            ],
            a11yReport: A11yReport(
                role: "Group (with summarising text alternative)",
                minTouchTarget: "n/a — decorative on its own; 48 × 48 / 44 × 44 when wrapped in a Button",
                screenReader: "Provide a single contentDescription summarising members — \"6 collaborators including Maya, Aanya, Bilal, and 3 others\". Don't expose individual avatars to a11y; the group reads as one unit.",
                voiceControl: "When the group is interactive (opens a member list / picker), the spoken target is the summary label, not the individual avatars.",
                keyboard: "Group itself is not focusable. When interactive (e.g. \"View all collaborators\"), it's a single Tab stop with the summary as its accessible name.",
                contrast: "The +N overflow chip uses surface.sunken with text.primary (10:1+). Each avatar's overlap ring matches the surrounding surface colour (3:1) so the stack reads as discrete circles, not a smear.",
                touchTarget: "When clickable, the entire group is a single 48 × 48 dp / 44 × 44 pt button — even though the visible avatar overlap is narrower than that.",
                wcagQuote: "All non-text content that is presented to the user has a text alternative that serves the equivalent purpose.",
                wcagRef: "1.1.1 Non-text Content, Level A"
            )
        )
    }
}

struct DividerShowcase: View {
    @State private var weight: PrismaHorizontalDivider.Weight = .defaultWeight
    @State private var inset: Int = 0

    var body: some View {
        PlaygroundScaffold(
            preview: { PrismaHorizontalDivider(weight: weight, inset: CGFloat(inset)) },
            knobs: {
                EnumKnobRow(label: "Weight", value: $weight, values: [.subtle, .defaultWeight, .strong], optionLabel: { String(describing: $0) })
                IntKnobRow(label: "Inset (pt)", value: $inset, range: 0...96, step: 8)
            },
            code: {
                var s = "PrismaHorizontalDivider("
                var parts: [String] = []
                if weight != .defaultWeight { parts.append("weight: .\(String(describing: weight))") }
                if inset > 0 { parts.append("inset: \(inset)") }
                s += parts.joined(separator: ", ") + ")"
                return s
            },
            pagerStates: [
                AnyPlaygroundState("Subtle") { PrismaHorizontalDivider(weight: .subtle) },
                AnyPlaygroundState("Default") { PrismaHorizontalDivider(weight: .defaultWeight) },
                AnyPlaygroundState("Strong") { PrismaHorizontalDivider(weight: .strong) },
                AnyPlaygroundState("Inset (56pt)") { PrismaHorizontalDivider(inset: 56) }
            ],
            a11yReport: A11yReport(
                role: "Decorative — hidden from a11y tree",
                minTouchTarget: "n/a — dividers are non-interactive",
                screenReader: "Dividers are purely visual; not exposed to screen readers. Use a heading() on the section above instead — that's the structural cue AT users navigate by.",
                voiceControl: "No spoken target. Voice users navigate the headings above and below the divider; the divider itself is invisible to voice control.",
                keyboard: "Not focusable. Tab order skips dividers entirely.",
                contrast: "Default weight uses border.subtle at 3:1 against the surrounding surface — meets the non-text-contrast minimum even though the divider is decorative. Subtle and Strong variants give designers a calibrated range without dropping below the floor.",
                touchTarget: "n/a. Dividers occupy the row but consume no tap area.",
                wcagQuote: "Information, structure, and relationships conveyed through presentation can be programmatically determined or are available in text.",
                wcagRef: "1.3.1 Info and Relationships, Level A"
            )
        )
    }
}
