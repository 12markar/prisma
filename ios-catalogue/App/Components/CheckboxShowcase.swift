import SwiftUI
import CoreUI
import Components

struct CheckboxShowcase: View {
    @State private var checked: Bool = false
    @State private var label: String = "Email me product updates"
    @State private var helper: String = "Roughly twice a month. No spam."
    @State private var enabled: Bool = true
    @State private var error: Bool = false

    var body: some View {
        PlaygroundScaffold(
            preview: {
                PrismaCheckbox(
                    checked: $checked,
                    label: label.isEmpty ? nil : label,
                    helperText: helper.isEmpty ? nil : helper,
                    enabled: enabled,
                    isError: error
                )
            },
            knobs: {
                BoolKnobRow(label: "Checked", value: $checked)
                BoolKnobRow(label: "Enabled", value: $enabled)
                BoolKnobRow(label: "Error", value: $error)
                StringKnobRow(label: "Label", value: $label)
                StringKnobRow(label: "Helper", value: $helper)
            },
            code: {
                var lines: [String] = ["PrismaCheckbox("]
                lines.append("    checked: $checked,")
                if !label.isEmpty { lines.append("    label: \"\(label)\",") }
                if !helper.isEmpty { lines.append("    helperText: \"\(helper)\",") }
                if !enabled { lines.append("    enabled: false,") }
                if error { lines.append("    isError: true,") }
                lines.append(")")
                return lines.joined(separator: "\n")
            },
            pagerStates: [
                AnyPlaygroundState("Unchecked") { PrismaCheckbox(checked: .constant(false), label: "Default unchecked") },
                AnyPlaygroundState("Checked") { PrismaCheckbox(checked: .constant(true), label: "Default checked") },
                AnyPlaygroundState("Indeterminate") { PrismaCheckbox(state: .constant(.indeterminate), label: "Indeterminate") },
                AnyPlaygroundState("Disabled checked") { PrismaCheckbox(checked: .constant(true), label: "Locked on", enabled: false) },
                AnyPlaygroundState("Error") { PrismaCheckbox(checked: .constant(false), label: "Required", helperText: "This field is required.", isError: true) },
                AnyPlaygroundState("Group + parent") { GroupExample() }
            ],
            a11yReport: A11yReport(
                role: "Checkbox / TriStateCheckbox (parent-child group)",
                minTouchTarget: "48 × 48 dp / 44 × 44 pt — full row is the tap target",
                screenReader: "TalkBack and VoiceOver announce the role (\"Checkbox\"), the label, then the state (\"Checked\" / \"Not checked\" / \"Partially checked\"). The label is read in the same pass — no re-focus needed. Indeterminate is reserved for parent rows whose children disagree.",
                voiceControl: "Voice Access targets the visible label (\"Tap Mentions\"). Saying the label toggles the box without precise targeting. The full row is clickable, not just the box itself.",
                keyboard: "Tab focuses, Space toggles. Enter does not toggle (matches platform convention). Disabled boxes are skipped from Tab order via the role; visual dimming is supporting, not primary.",
                contrast: "Unchecked outline uses border.strong (3:1 non-text contrast); checked fill uses accent.default at 4.6:1; the check glyph itself meets 4.5:1 against the fill. Error border uses status.danger at 4.7:1.",
                touchTarget: "Whole row is the tap target — at least 48 × 48 dp / 44 × 44 pt — even when the visible box is 20 dp. Spacing between adjacent checkboxes prevents fat-finger toggles on the wrong row.",
                wcagQuote: "For all user interface components … the name and role can be programmatically determined; states, properties, and values that can be set by the user can be programmatically set.",
                wcagRef: "4.1.2 Name, Role, Value, Level A"
            )
        )
    }
}

private struct GroupExample: View {
    @State private var alpha: Bool = true
    @State private var beta: Bool = false
    @State private var gamma: Bool = true

    private var parent: PrismaCheckboxState {
        if alpha && beta && gamma { return .checked }
        if !alpha && !beta && !gamma { return .unchecked }
        return .indeterminate
    }

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp1) {
            PrismaCheckbox(
                state: Binding(
                    get: { parent },
                    set: { _ in
                        let n = parent != .checked
                        alpha = n; beta = n; gamma = n
                    }
                ),
                label: "Notifications"
            )
            VStack(alignment: .leading, spacing: PrismaSpacing.sp1) {
                PrismaCheckbox(checked: $alpha, label: "Mentions")
                PrismaCheckbox(checked: $beta, label: "DMs")
                PrismaCheckbox(checked: $gamma, label: "Releases")
            }
            .padding(.leading, PrismaSpacing.sp7)
        }
    }
}
