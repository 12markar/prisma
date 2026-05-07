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
            states: {
                StateCell("On") { PrismaSwitch(checked: .constant(true), label: "Push notifications") }
                StateCell("Off") { PrismaSwitch(checked: .constant(false), label: "Auto-sync") }
                StateCell("Disabled (on)") { PrismaSwitch(checked: .constant(true), label: "Locked on", enabled: false) }
                StateCell("Disabled (off)") { PrismaSwitch(checked: .constant(false), label: "Locked off", enabled: false) }
                StateCell("Standalone") { PrismaSwitch(checked: .constant(true)) }
            },
            code: {
                var s = "PrismaSwitch(checked: $checked"
                if withLabel && !label.isEmpty { s += ", label: \"\(label)\"" }
                if !enabled { s += ", enabled: false" }
                s += ")"
                return s
            },
            a11y: {
                A11yPanel(
                    role: ".isToggle",
                    minTouchTarget: "44 × 44 pt",
                    bullets: [
                        "On/off state announced as part of the role; label read alongside.",
                        "Selection haptic fires on each toggle so the change is felt as well as heard.",
                        "Disabled communicated by the role; visual dim is supporting, not primary."
                    ]
                )
            }
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
            states: {
                StateCell("Selected") { PrismaRadio(selected: true, onClick: {}, label: "Selected option") }
                StateCell("Unselected") { PrismaRadio(selected: false, onClick: {}, label: "Unselected option") }
                StateCell("With helper") { PrismaRadio(selected: false, onClick: {}, label: "Yearly", helperText: "$90 per year (save 17%).") }
                StateCell("Disabled") { PrismaRadio(selected: false, onClick: nil, label: "Locked", enabled: false) }
            },
            code: {
                "PrismaRadio(\n    selected: idx == \(safeIdx),\n    onClick: { selectedIdx = idx },\n    label: options[idx]\n)"
            },
            a11y: {
                A11yPanel(
                    role: ".isButton + accessibilityAddTraits(.isSelected) when on",
                    minTouchTarget: "44 × 44 pt",
                    bullets: [
                        "Wrap the radio set in accessibilityElement(children: .contain) so position-in-set is announced.",
                        "Only one radio in a group can be selected; the selected state is announced.",
                        "Helper text is read after the label; keep helpers brief."
                    ]
                )
            }
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
            states: {
                StateCell("Continuous") { ContinuousSliderState() }
                StateCell("Stepped (1–5)") { SteppedSliderState() }
                StateCell("Disabled") { PrismaSlider(value: .constant(0.7), label: "Read-only", enabled: false) }
            }
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
            states: {
                StateCell("Two") { TwoSegState() }
                StateCell("Three") { ThreeSegState() }
                StateCell("Sizes") { SizeSegState() }
            },
            code: {
                let joined = opts.map { "\"\($0)\"" }.joined(separator: ", ")
                return "PrismaSegmentedControl(\n    options: [\(joined)],\n    selected: $selected\n)"
            },
            a11y: {
                A11yPanel(
                    role: "SwiftUI Picker (.isSelected on chosen segment)",
                    minTouchTarget: "44 pt height across the segment",
                    bullets: [
                        "Each segment is a button; selection is announced with position-in-set.",
                        "Distinct from PrismaTabs: segmented control filters content in place, not navigation.",
                        "Avoid >5 options — at that point use a Picker / dropdown instead."
                    ]
                )
            }
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
            states: {
                StateCell("Empty") { SearchBarState(initial: "") }
                StateCell("With query") { SearchBarState(initial: "compose") }
            },
            code: {
                "PrismaSearchBar(value: $query, placeholder: \"\(placeholder)\")"
            },
            a11y: {
                A11yPanel(
                    role: "TextField (search semantics)",
                    minTouchTarget: "44 pt height",
                    bullets: [
                        "Placeholder is announced as a hint, not as the label — supply explicit label when needed.",
                        "Submit on return; the keyboard returns the search affordance.",
                        "Clearing the field is a single action read as \"Clear search\"."
                    ]
                )
            }
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
            states: {
                StateCell("Default") { StepperState(initial: 1) }
                StateCell("At max") { StepperState(initial: 10) }
                StateCell("Disabled") { PrismaStepper(value: .constant(5), range: 0...10, enabled: false) }
            },
            code: {
                var s = "PrismaStepper(value: $value, range: \(minVal)...\(safeMax)"
                if step > 1 { s += ", step: \(step)" }
                if !enabled { s += ", enabled: false" }
                s += ")"
                return s
            },
            a11y: {
                A11yPanel(
                    role: ".adjustable (custom Increment / Decrement actions)",
                    minTouchTarget: "44 × 44 pt per button",
                    bullets: [
                        "Each button is independently focusable; current value announced when changed.",
                        "Disabled at min/max — buttons individually disabled, value still readable.",
                        "VoiceOver users adjust via swipe-up / swipe-down on the value."
                    ]
                )
            }
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
            states: {
                StateCell("Empty") { TagInputState(initial: []) }
                StateCell("Filled") { TagInputState(initial: ["swift", "swiftui"]) }
            },
            code: {
                var s = "PrismaTagInput(\n    tags: $tags"
                if !label.isEmpty { s += ",\n    label: \"\(label)\"" }
                s += ",\n    placeholder: \"\(placeholder)\"\n)"
                return s
            },
            a11y: {
                A11yPanel(
                    role: "TextField with associated chip list",
                    minTouchTarget: "44 pt per chip; chip × also 44 × 44 pt",
                    bullets: [
                        "Adding a tag triggers an announcement (\"swift added\") via accessibilityAnnouncement.",
                        "Each chip's × is its own focusable element; remove announces (\"swift removed\").",
                        "Backspace on empty input deletes the last chip and announces it."
                    ]
                )
            }
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
            states: {
                StateCell("Default") { AutocompleteState() }
            },
            code: {
                "PrismaAutocomplete(\n    value: $query,\n    suggestions: corpus.filter { $0.localizedCaseInsensitiveContains(query) },\n    onSelect: { query = $0 },\n    label: \"\(label)\"\n)"
            },
            a11y: {
                A11yPanel(
                    role: "Combobox (input + listbox popup)",
                    minTouchTarget: "44 pt per suggestion row",
                    bullets: [
                        "Suggestion count announced when popup opens (\"6 suggestions\") via accessibilityAnnouncement.",
                        "Arrow up/down moves focus through suggestions while keeping caret in input.",
                        "Return / tap selects; Escape closes the popup and returns focus to input."
                    ]
                )
            }
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
            states: {
                StateCell("Default", minWidth: 360) { DatePickerState() }
            },
            code: { "PrismaDatePicker(date: $date)" },
            a11y: {
                A11yPanel(
                    role: "SwiftUI DatePicker (.graphical)",
                    minTouchTarget: "Per Apple HIG — 44pt grid cells",
                    bullets: [
                        "SwiftUI DatePicker handles month / year navigation announcements.",
                        "Today is announced; selected date announced on commit.",
                        "VoiceOver rotor exposes day / month / year as separate adjustables."
                    ]
                )
            }
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
            preview: { PrismaTimePicker(date: $date) },
            states: {
                StateCell("Default", minWidth: 280) { TimePickerState() }
            },
            code: { "PrismaTimePicker(date: $date)" },
            a11y: {
                A11yPanel(
                    role: "SwiftUI DatePicker (.compact, .hourAndMinute)",
                    minTouchTarget: "Per Apple HIG",
                    bullets: [
                        "Hour and minute exposed as separate adjustables.",
                        "AM/PM toggle is a button group; current selection announced.",
                        "Increment / decrement supported via swipe-up / swipe-down."
                    ]
                )
            }
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
            states: {
                StateCell("Orange") { ColorPickerState() }
            },
            code: { "PrismaColorPicker(color: $color)" },
            a11y: {
                A11yPanel(
                    role: "SwiftUI ColorPicker",
                    minTouchTarget: "Slider thumb 44 × 44 pt",
                    bullets: [
                        "Current colour announced as RGB or hex; consider naming common colours.",
                        "Each channel slider is independently focusable and announces its value.",
                        "Avoid colour-only meaning — pair the swatch with a hex / name label."
                    ]
                )
            }
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
            states: {
                StateCell("Info") { PrismaToast(message: "New version available.", kind: .info) }
                StateCell("Success") { PrismaToast(message: "Saved successfully.", kind: .success, actionLabel: "Undo") {} }
                StateCell("Warning") { PrismaToast(message: "Connection looks slow.", kind: .warning) }
                StateCell("Danger") { PrismaToast(message: "Could not reach server.", kind: .danger, actionLabel: "Retry") {} }
            },
            code: {
                var s = "PrismaToast(\n    message: \"\(message)\",\n    kind: .\(String(describing: kind))"
                if hasAction { s += ",\n    actionLabel: \"\(actionLabel)\"" }
                s += "\n)"
                if hasAction { s += " { /* … */ }" }
                return s
            },
            a11y: {
                A11yPanel(
                    role: "Live region (.updatesFrequently)",
                    minTouchTarget: "Action button 44 × 44 pt",
                    bullets: [
                        "Info / success use polite announcement; warning / danger use assertive (.priority).",
                        "accessibilityLabel combines kind + message — e.g. \"Danger. Could not reach server.\"",
                        "Action button keeps its own .isButton trait for direct activation."
                    ]
                )
            }
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
            states: {
                StateCell("Info") { PrismaBanner(title: "Server upgrade", description: "Slower response times expected.", kind: .info, actionLabel: "Learn more", onAction: {}, onDismiss: {}) }
                StateCell("Success") { PrismaBanner(title: "Profile updated", description: "Saved across all devices.", kind: .success, onDismiss: {}) }
                StateCell("Warning") { PrismaBanner(title: "Storage almost full", description: "Less than 1GB free.", kind: .warning, actionLabel: "Manage", onAction: {}) }
                StateCell("Danger") { PrismaBanner(title: "Action required", description: "Verify your email.", kind: .danger, actionLabel: "Verify", onAction: {}) }
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
            a11y: {
                A11yPanel(
                    role: "Live region (inline alert)",
                    minTouchTarget: "Action / dismiss buttons 44 × 44 pt",
                    bullets: [
                        "Title + description merge into one announcement; no need to focus the banner.",
                        "Polite for info / success; assertive for warning / danger.",
                        "Dismiss read as \"Close banner\"; reappearance only on relevant state change."
                    ]
                )
            }
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
                VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
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
            states: {
                StateCell("Confirm action") {
                    Text("Tap the live preview's button to open.\nDestructive variant uses red confirm.")
                        .font(PrismaTypography.bodySm.font)
                        .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                }
                StateCell("Single action") {
                    Text("Set 'With dismiss button' off to render a single-action modal (e.g. 'Got it').")
                        .font(PrismaTypography.bodySm.font)
                        .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                }
            },
            code: {
                var s = "PrismaModalContent(\n    title: \"\(title)\",\n    message: \"\(message)\",\n    confirmLabel: \"\(confirmLabel)\",\n    onConfirm: { /* … */ }"
                if hasDismiss { s += ",\n    dismissLabel: \"\(dismissLabel)\",\n    onDismiss: { /* … */ }" }
                if destructive { s += ",\n    isDestructive: true" }
                s += "\n)"
                return s
            },
            a11y: {
                A11yPanel(
                    role: ".isModal",
                    minTouchTarget: "44 × 44 pt confirm / dismiss",
                    bullets: [
                        "Focus traps inside the sheet; swipe-down or scrim dismisses and returns focus.",
                        "Title is read on open; body follows; confirm + dismiss are focusable in order.",
                        "Destructive variant tints the confirm button danger; the role doesn't change."
                    ]
                )
            }
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
            a11y: {
                A11yPanel(
                    role: ".isModal (sheet)",
                    minTouchTarget: "Drag handle 44 × 44 pt; content interactive",
                    bullets: [
                        "Focus traps inside the sheet; swipe-down or scrim dismisses and returns focus.",
                        "Drag handle has its own .isButton announcing \"drag handle\".",
                        "Hide content behind the sheet from VoiceOver via .accessibilityHidden(true)."
                    ]
                )
            }
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
            a11y: {
                A11yPanel(
                    role: "Popover (non-modal overlay)",
                    minTouchTarget: "Trigger 44 × 44 pt",
                    bullets: [
                        "Lighter than Modal — does not trap focus; tap outside or ESC dismisses.",
                        "Anchored to the trigger; positioning auto-flips to stay on-screen.",
                        "For destructive / blocking flows use Modal, not Popover."
                    ]
                )
            }
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
            states: {
                StateCell("Copy") {
                    Image(prisma: .copy).renderingMode(.template).resizable()
                        .frame(width: 24, height: 24)
                        .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                        .help("Save to clipboard")
                }
                StateCell("Star") {
                    Image(prisma: .star).renderingMode(.template).resizable()
                        .frame(width: 24, height: 24)
                        .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                        .help("Star this item")
                }
            },
            code: { "Image(prisma: .copy)\n    .help(\"\(hint)\")" },
            a11y: {
                A11yPanel(
                    role: "Tooltip (label association via .help)",
                    minTouchTarget: "Trigger element 44 × 44 pt",
                    bullets: [
                        "Tooltip text serves as the trigger's accessibilityLabel — never use as the only source of meaning.",
                        "Long-press / hover surfaces the tooltip; VoiceOver reads the label without it appearing.",
                        "Don't put critical info in tooltips alone — keyboard / touch users may never trigger them."
                    ]
                )
            }
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
            states: {
                StateCell("Circular Sm") { PrismaCircularLoading(size: .sm) }
                StateCell("Circular Md") { PrismaCircularLoading(size: .md) }
                StateCell("Circular Lg") { PrismaCircularLoading(size: .lg) }
                StateCell("Linear (indeterminate)") { PrismaLinearLoading() }
                StateCell("Linear (60%)") { PrismaLinearLoading(progress: 0.6) }
            },
            code: {
                switch shape {
                case .circular: return "PrismaCircularLoading(size: .\(String(describing: size)))"
                case .linear: return indeterminate ? "PrismaLinearLoading()" : "PrismaLinearLoading(progress: \(Double(progressPct) / 100.0))"
                }
            },
            a11y: {
                A11yPanel(
                    role: "ProgressView",
                    minTouchTarget: "n/a (non-interactive)",
                    bullets: [
                        "Indeterminate variant has no value — read as \"loading\".",
                        "Determinate variant exposes 0–1 progress; VoiceOver announces percentage.",
                        "Pair with a textual label (\"Loading projects…\") for context — the role alone isn't enough."
                    ]
                )
            }
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
            states: {
                StateCell("Line") { PrismaSkeletonLine().frame(width: 220, height: 12) }
                StateCell("Circle") { PrismaSkeletonCircle().frame(width: 40, height: 40) }
                StateCell("Block") { PrismaSkeletonBlock(cornerRadius: 12).frame(width: 220, height: 80) }
            },
            code: {
                switch kind {
                case .lines: return "ForEach(0..<\(lineCount)) { _ in\n    PrismaSkeletonLine().frame(height: 12)\n}"
                case .card: return "PrismaSkeletonBlock(cornerRadius: 12).frame(height: \(blockHeight))"
                case .postPlaceholder: return "HStack {\n    PrismaSkeletonCircle().frame(width: 40, height: 40)\n    VStack { /* skeleton lines */ }\n}"
                }
            },
            a11y: {
                A11yPanel(
                    role: "Decorative (.accessibilityHidden(true))",
                    minTouchTarget: "n/a",
                    bullets: [
                        "Skeleton placeholders are hidden from VoiceOver entirely.",
                        "Pair with a sibling \"Loading…\" announcement so AT users still know content is coming.",
                        "When real content arrives, focus / announcement should pick it up automatically."
                    ]
                )
            }
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
            states: {
                StateCell("Single") { PrismaCountBadge(count: 1) }
                StateCell("Two-digit") { PrismaCountBadge(count: 12) }
                StateCell("Cap (99+)") { PrismaCountBadge(count: 250) }
                StateCell("Success") { PrismaCountBadge(count: 3, status: .success) }
                StateCell("Warning") { PrismaCountBadge(count: 7, status: .warning) }
                StateCell("Danger") { PrismaCountBadge(count: 99, status: .danger) }
                StateCell("Dot") { PrismaDotBadge(status: .accent) }
            },
            code: {
                switch shape {
                case .count: return "PrismaCountBadge(count: \(count), status: .\(String(describing: status)))"
                case .dot: return "PrismaDotBadge(status: .\(String(describing: status)))"
                }
            },
            a11y: {
                A11yPanel(
                    role: "Decorative; carrier owns semantics",
                    minTouchTarget: "n/a (badges are not interactive)",
                    bullets: [
                        "Badge alone is meaningless — append \"5 unread\" to the parent's accessibilityLabel.",
                        "Cap at 99+ visually; announce the actual count if known.",
                        "Status colour is supportive only; the carrier's label conveys success/danger meaning."
                    ]
                )
            }
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
            states: {
                StateCell("Default") {
                    PrismaEmptyState(
                        title: "No projects yet",
                        description: "When you create a project, it'll show up here.",
                        action: { PrismaButton("Create project") {} }
                    )
                }
                StateCell("Just a title") {
                    PrismaEmptyState(title: "Nothing here", action: { EmptyView() })
                }
            },
            code: {
                var s = "PrismaEmptyState(\n    title: \"\(title)\""
                if !emptyDescription.isEmpty { s += ",\n    description: \"\(emptyDescription)\"" }
                if withAction { s += ",\n    action: { PrismaButton(\"\(actionLabel)\") { /* … */ } }" }
                s += "\n)"
                return s
            },
            a11y: {
                A11yPanel(
                    role: "Heading + body + optional action",
                    minTouchTarget: "Action button 44 × 44 pt",
                    bullets: [
                        "Title carries .isHeader trait so VoiceOver users can jump to it.",
                        "Body description is read after the title; keep it under two short sentences.",
                        "Action label should be a verb that resolves the empty state (\"Create project\")."
                    ]
                )
            }
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
            a11y: {
                A11yPanel(
                    role: ".isModal (sheet) on iOS; NavigationSplitView sidebar elsewhere",
                    minTouchTarget: "Trigger 44 × 44 pt; drawer items 44 pt",
                    bullets: [
                        "When open, focus traps inside the drawer; main content goes accessibilityHidden.",
                        "Swipe-down or scrim dismisses; ESC closes from external keyboards.",
                        "Use for secondary navigation; primary nav stays in NavigationSplitView."
                    ]
                )
            }
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
            states: {
                StateCell("2 tabs") { TabsState(initial: ["Inbox", "Archive"]) }
                StateCell("4 tabs") { TabsState(initial: ["Overview", "Activity", "Settings", "Billing"]) }
                StateCell("Last selected") { TabsState(initial: ["One", "Two", "Three"], selected: "Three") }
            },
            code: {
                let joined = safeTabs.map { "\"\($0)\"" }.joined(separator: ", ")
                return "PrismaTabs(\n    tabs: [\(joined)],\n    selected: $selected\n)"
            },
            a11y: {
                A11yPanel(
                    role: ".isButton + .isSelected on chosen tab",
                    minTouchTarget: "44 pt height per tab",
                    bullets: [
                        "Each tab carries .isButton; selected tab adds .isSelected so position is announced.",
                        "Selection haptic fires only when a different tab is chosen, not on re-tap.",
                        "Tabs are for switching between peer views — distinct from SegmentedControl filtering."
                    ]
                )
            }
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
            states: {
                StateCell("Filter (toggle)") { ToggleableChipDemo(label: "Android") }
                StateCell("iOS (toggle)") { ToggleableChipDemo(label: "iOS", initial: false) }
                StateCell("Suggestion") { PrismaChip(label: "Trending", variant: .suggestion, onTap: {}) }
                StateCell("Disabled") { PrismaChip(label: "Locked", enabled: false, onTap: {}) }
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
            a11y: {
                A11yPanel(
                    role: ".isButton + .isSelected when on",
                    minTouchTarget: "44 pt height; × 44 × 44 pt",
                    bullets: [
                        "Selected state announced as part of the role; haptic on toggle.",
                        "Input variant exposes its × as a separate action labelled \"Remove <chip>\".",
                        "Filter chip groups should be wrapped in a labelled container for grouping context."
                    ]
                )
            }
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
            a11y: {
                A11yPanel(
                    role: "Combobox (input + listbox)",
                    minTouchTarget: "Each command row 44 pt",
                    bullets: [
                        "Filtering announces \"N results\" via accessibilityAnnouncement as the user types.",
                        "Section headers (\"Foundations\", \"Actions\") use .isHeader so they're skippable.",
                        "Return activates the focused command; ESC closes the palette and returns focus."
                    ]
                )
            }
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
            states: {
                StateCell("Few pages") { PaginationState(initial: 2, total: 5) }
                StateCell("Many pages") { PaginationState(initial: 12, total: 30) }
            },
            code: { "PrismaPagination(page: $page, pageCount: \(safeCount))" },
            a11y: {
                A11yPanel(
                    role: "Navigation (each control is .isButton)",
                    minTouchTarget: "Each page button 44 × 44 pt",
                    bullets: [
                        "Wrap the row in accessibilityElement(children: .contain) labelled \"Pagination\".",
                        "Current page exposes .isSelected; arrows announce \"Previous / Next page, disabled\" at edges.",
                        "Ellipsis is decorative — accessibilityHidden so it doesn't get focused."
                    ]
                )
            }
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
            states: {
                StateCell("3 levels") { PrismaBreadcrumb(items: [PrismaBreadcrumbItem(label: "Home", onTap: {}), PrismaBreadcrumbItem(label: "Settings", onTap: {}), PrismaBreadcrumbItem(label: "Profile")]) }
                StateCell("4 levels") { PrismaBreadcrumb(items: [PrismaBreadcrumbItem(label: "Home", onTap: {}), PrismaBreadcrumbItem(label: "Components", onTap: {}), PrismaBreadcrumbItem(label: "Inputs", onTap: {}), PrismaBreadcrumbItem(label: "Button")]) }
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
            a11y: {
                A11yPanel(
                    role: "Navigation (ordered list of buttons; current page is text)",
                    minTouchTarget: "44 × 44 pt per crumb",
                    bullets: [
                        "Wrap in accessibilityElement(children: .contain) labelled \"Breadcrumb\".",
                        "The last item is the current page — render as plain text, not a link.",
                        "Separators (\"/\") are decorative; accessibilityHidden so the path reads cleanly."
                    ]
                )
            }
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
            states: {
                StateCell("3 steps, on 1") { PrismaWizardSteps(steps: ["Account", "Profile", "Done"], activeIndex: 1) }
                StateCell("Last step") { PrismaWizardSteps(steps: ["A", "B", "C"], activeIndex: 2) }
            },
            code: {
                let joined = s.map { "\"\($0)\"" }.joined(separator: ", ")
                return "PrismaWizardSteps(steps: [\(joined)], activeIndex: \(safeStep))"
            },
            a11y: {
                A11yPanel(
                    role: "Step indicator (progress)",
                    minTouchTarget: "n/a in catalogue; wired to nav externally",
                    bullets: [
                        "Active step exposes accessibilityValue \"current step\".",
                        "Completed steps announce as \"completed\"; future steps as \"upcoming\".",
                        "Pair with a heading (\"Step 2 of 4: Profile\") for clearest navigation."
                    ]
                )
            }
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
            states: {
                StateCell("Elevated") {
                    PrismaCard(variant: .elevated) {
                        Text("Casts a shadow over the page. Use for emphasis.")
                            .font(PrismaTypography.bodySm.font)
                            .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                    }
                }
                StateCell("Outlined") {
                    PrismaCard(variant: .outlined) {
                        Text("1pt subtle border. The most-used card.")
                            .font(PrismaTypography.bodySm.font)
                            .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                    }
                }
                StateCell("Filled") {
                    PrismaCard(variant: .filled) {
                        Text("Sunken surface, quieter than elevated.")
                            .font(PrismaTypography.bodySm.font)
                            .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                    }
                }
            },
            code: {
                var s = "PrismaCard("
                if variant != .outlined { s += "variant: .\(String(describing: variant))" }
                if clickable { s += (variant != .outlined ? ", " : "") + "onTap: { /* … */ }" }
                s += ") {\n    VStack { Text(\"\(title)\"); Text(\"\(bodyText.prefix(40))…\") }\n}"
                return s
            },
            a11y: {
                A11yPanel(
                    role: "Container (interactive cards = .isButton)",
                    minTouchTarget: "44 × 44 pt when whole-card clickable",
                    bullets: [
                        "Interior interactive elements (CTA buttons) keep their own focus and roles.",
                        "If the whole card is clickable, accessibilityElement(children: .combine) so it's one a11y unit.",
                        "Don't double-up: either the card OR the inner button is the action, not both."
                    ]
                )
            }
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
    @State private var secondary: String = "karan@example.com"
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
            states: {
                StateCell("With avatar (toggle)") { SelectableListItemDemo() }
                StateCell("Selected") {
                    PrismaListItem(
                        primary: "Settings", secondary: "Account & preferences", selected: true, onTap: {},
                        leading: { AnyView(EmptyView()) },
                        trailing: { AnyView(EmptyView()) }
                    )
                }
                StateCell("With chevron") {
                    PrismaListItem(
                        primary: "Notifications", onTap: {},
                        leading: { AnyView(EmptyView()) },
                        trailing: {
                            AnyView(Image(prisma: .chevronRight).renderingMode(.template).resizable()
                                .frame(width: 14, height: 14)
                                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme)))
                        }
                    )
                }
            },
            code: {
                var s = "PrismaListItem(\n    primary: \"\(primary)\""
                if !secondary.isEmpty { s += ",\n    secondary: \"\(secondary)\"" }
                if selected { s += ",\n    selected: true" }
                if clickable { s += ",\n    onTap: { /* … */ }" }
                s += "\n)"
                return s
            },
            a11y: {
                A11yPanel(
                    role: "ListItem (.isButton when onTap provided)",
                    minTouchTarget: "44 pt height (default) / 56 pt / 72 pt",
                    bullets: [
                        ".accessibilityElement(children: .combine) groups primary + secondary + leading + trailing into one a11y unit.",
                        "Selected state exposed via .accessibilityAddTraits(.isSelected) when on.",
                        "Trailing actions (switch, badge) keep their own roles when independently activatable."
                    ]
                )
            }
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
            states: {
                StateCell("Sizes") {
                    HStack(spacing: PrismaSpacing.sp3) {
                        PrismaAvatar(seed: "Maya Chen", size: .xs)
                        PrismaAvatar(seed: "Maya Chen", size: .sm)
                        PrismaAvatar(seed: "Maya Chen", size: .default)
                        PrismaAvatar(seed: "Maya Chen", size: .lg)
                    }
                }
                StateCell("Online") { PrismaAvatar(seed: "Aanya Patel", status: .online) }
                StateCell("Away") { PrismaAvatar(seed: "Bilal Khan", status: .away) }
                StateCell("Busy") { PrismaAvatar(seed: "Cara Liu", status: .busy) }
                StateCell("Offline") { PrismaAvatar(seed: "Dev Iyer", status: .offline) }
            },
            code: {
                var s = "PrismaAvatar(seed: \"\(seed)\""
                if size != .default { s += ", size: .\(String(describing: size))" }
                if status != .none { s += ", status: .\(String(describing: status))" }
                s += ")"
                return s
            },
            a11y: {
                A11yPanel(
                    role: "Image / decorative",
                    minTouchTarget: "n/a (decorative); wraps interactive when used in lists",
                    bullets: [
                        "accessibilityLabel is the seed name plus status — \"Maya Chen, online\".",
                        "Initials are derived from the seed; the status dot is not announced separately.",
                        "When used inside a clickable row, the row's onTap takes precedence; avatar becomes decorative."
                    ]
                )
            }
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
            states: {
                StateCell("Few") { PrismaAvatarGroup(seeds: ["Maya", "Aanya"]) }
                StateCell("Many (overflow)") { PrismaAvatarGroup(seeds: (1...8).map { "User \($0)" }, max: 4) }
            },
            code: {
                let joined = seeds.map { "\"\($0)\"" }.joined(separator: ", ")
                var s = "PrismaAvatarGroup(\n    seeds: [\(joined)]"
                if size != .default { s += ",\n    size: .\(String(describing: size))" }
                if maxVisible != 4 { s += ",\n    max: \(maxVisible)" }
                s += "\n)"
                return s
            },
            a11y: {
                A11yPanel(
                    role: "Group (decorative or labelled)",
                    minTouchTarget: "n/a unless wrapped in a Button",
                    bullets: [
                        "Provide a single accessibilityLabel summarising members — \"6 collaborators including Maya, Aanya, Bilal, and 3 others\".",
                        "Don't expose individual avatars — the group is one focus stop.",
                        "When clickable, wrap the entire group in a single labelled .isButton."
                    ]
                )
            }
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
            states: {
                StateCell("Subtle") { PrismaHorizontalDivider(weight: .subtle) }
                StateCell("Default") { PrismaHorizontalDivider(weight: .defaultWeight) }
                StateCell("Strong") { PrismaHorizontalDivider(weight: .strong) }
                StateCell("Inset (56pt)") { PrismaHorizontalDivider(inset: 56) }
            },
            code: {
                var s = "PrismaHorizontalDivider("
                var parts: [String] = []
                if weight != .defaultWeight { parts.append("weight: .\(String(describing: weight))") }
                if inset > 0 { parts.append("inset: \(inset)") }
                s += parts.joined(separator: ", ") + ")"
                return s
            },
            a11y: {
                A11yPanel(
                    role: "Decorative (.accessibilityHidden(true))",
                    minTouchTarget: "n/a",
                    bullets: [
                        "Dividers are purely visual; do not expose them to VoiceOver.",
                        "Use .isHeader on the section title above instead — that's the structural cue AT users navigate.",
                        "Inset variants reinforce hierarchy visually; semantics are the same."
                    ]
                )
            }
        )
    }
}
