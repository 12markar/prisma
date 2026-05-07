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
            states: {
                StateCell("Unchecked") { PrismaCheckbox(checked: .constant(false), label: "Default unchecked") }
                StateCell("Checked") { PrismaCheckbox(checked: .constant(true), label: "Default checked") }
                StateCell("Indeterminate") { PrismaCheckbox(state: .constant(.indeterminate), label: "Indeterminate") }
                StateCell("Disabled checked") { PrismaCheckbox(checked: .constant(true), label: "Locked on", enabled: false) }
                StateCell("Disabled unchecked") { PrismaCheckbox(checked: .constant(false), label: "Locked off", enabled: false) }
                StateCell("Error") { PrismaCheckbox(checked: .constant(false), label: "Required", helperText: "This field is required.", isError: true) }
                StateCell("Group + parent") { GroupExample() }
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
            a11y: {
                A11yPanel(
                    role: ".isToggle (tri-state via accessibilityValue)",
                    minTouchTarget: "44 × 44 pt",
                    bullets: [
                        "State (checked / unchecked / indeterminate) announced; label read in same pass.",
                        "Indeterminate is for parent groups — children remain individual booleans.",
                        "Error appends \"required\" or the error message via accessibilityHint.",
                        "Disabled communicated via the role; visual dim is supporting, not primary."
                    ]
                )
            }
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
