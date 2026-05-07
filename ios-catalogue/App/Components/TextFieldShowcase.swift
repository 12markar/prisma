import SwiftUI
import CoreUI
import Components

private enum ValidationKind: String, CaseIterable, Hashable {
    case none = "None"
    case email = "Email"
    case min8 = "Min 8 chars"
}

/// TextField playground.
///
/// - Knobs cover label, placeholder, helper, variant, size, disabled, readOnly,
///   secure, leadingIcon, plus a *live* validation pipeline (None / Email /
///   Min8) that paints `errorText` based on what the user actually types.
/// - Critical UX fix vs. the old showcase: `maxCount` no longer gates input.
///   Users can keep typing past max — the counter goes red and an error
///   message appears.
struct TextFieldShowcase: View {
    @State private var value: String = ""
    @State private var label: String = "Email"
    @State private var placeholder: String = "you@example.com"
    @State private var helper: String = "We'll never share it."
    @State private var variant: PrismaTextFieldVariant = .outlined
    @State private var size: PrismaTextFieldSize = .medium
    @State private var disabled: Bool = false
    @State private var readOnly: Bool = false
    @State private var secure: Bool = false
    @State private var maxCount: Int = 0
    @State private var leadingIcon: PrismaIcon? = nil
    @State private var validation: ValidationKind = .none

    private let iconOptions: [PrismaIcon] = [
        .search, .mail, .user, .lock, .phone, .calendar, .tag, .link
    ]

    private var derivedError: String? {
        guard !value.isEmpty else { return nil }
        if validation == .email && !value.contains("@") {
            return "Enter a valid email address."
        }
        if validation == .min8 && value.count < 8 {
            return "At least 8 characters required."
        }
        if maxCount > 0 && value.count > maxCount {
            return "Too long: \(value.count)/\(maxCount)."
        }
        return nil
    }

    var body: some View {
        PlaygroundScaffold(
            preview: {
                PrismaTextField(
                    text: $value,
                    label: label.isEmpty ? nil : label,
                    placeholder: placeholder.isEmpty ? nil : placeholder,
                    helperText: helper.isEmpty ? nil : helper,
                    errorText: derivedError,
                    enabled: !disabled,
                    readOnly: readOnly,
                    variant: variant,
                    size: size,
                    leadingIcon: leadingIcon.map { icon in
                        {
                            AnyView(
                                Image(prisma: icon)
                                    .renderingMode(.template)
                                    .resizable()
                                    .frame(width: 18, height: 18)
                                    .foregroundStyle(PrismaSemanticColors.textTertiary.themed(.light))
                            )
                        }
                    },
                    secureTextEntry: secure,
                    counter: maxCount > 0 ? value.count : nil,
                    maxCount: maxCount > 0 ? maxCount : nil
                )
            },
            knobs: {
                StringKnobRow(label: "Label", value: $label, placeholder: "Field label")
                StringKnobRow(label: "Placeholder", value: $placeholder, placeholder: "Hint shown when empty")
                StringKnobRow(label: "Helper", value: $helper, placeholder: "Sub-text below the field")
                EnumKnobRow(
                    label: "Variant",
                    value: $variant,
                    values: [.outlined, .filled],
                    optionLabel: { String(describing: $0) }
                )
                EnumKnobRow(
                    label: "Size",
                    value: $size,
                    values: [.small, .medium, .large],
                    optionLabel: { String(describing: $0) }
                )
                EnumKnobRow(
                    label: "Validation",
                    value: $validation,
                    values: ValidationKind.allCases,
                    optionLabel: { $0.rawValue }
                )
                IntKnobRow(label: "Max characters (0 = off)", value: $maxCount, range: 0...200, step: 10)
                BoolKnobRow(label: "Disabled", value: $disabled)
                BoolKnobRow(label: "Read only", value: $readOnly)
                BoolKnobRow(label: "Secure entry", value: $secure)
                IconKnobRow(label: "Leading icon", value: $leadingIcon, options: iconOptions)
            },
            states: {
                StateCell("Empty") {
                    PrismaTextField(text: .constant(""), label: "Email", placeholder: "you@example.com")
                }
                StateCell("Filled") {
                    PrismaTextField(text: .constant("karan@example.com"), label: "Email")
                }
                StateCell("Error") {
                    PrismaTextField(text: .constant("j@"), label: "Email", errorText: "Enter a valid email address.")
                }
                StateCell("Disabled") {
                    PrismaTextField(text: .constant("karan@example.com"), label: "Email", enabled: false)
                }
                StateCell("Read only") {
                    PrismaTextField(text: .constant("karan@example.com"), label: "Email", readOnly: true)
                }
                StateCell("Counter (live)") {
                    CounterExample()
                }
            },
            code: {
                var lines: [String] = ["PrismaTextField("]
                lines.append("    text: $value,")
                if !label.isEmpty { lines.append("    label: \"\(label)\",") }
                if !placeholder.isEmpty { lines.append("    placeholder: \"\(placeholder)\",") }
                if !helper.isEmpty { lines.append("    helperText: \"\(helper)\",") }
                if variant != .outlined { lines.append("    variant: .\(String(describing: variant)),") }
                if size != .medium { lines.append("    size: .\(String(describing: size)),") }
                if disabled { lines.append("    enabled: false,") }
                if readOnly { lines.append("    readOnly: true,") }
                if secure { lines.append("    secureTextEntry: true,") }
                if maxCount > 0 { lines.append("    counter: value.count, maxCount: \(maxCount),") }
                lines.append(")")
                return lines.joined(separator: "\n")
            },
            a11y: {
                A11yPanel(
                    role: "TextField",
                    minTouchTarget: "44 pt height",
                    bullets: [
                        "Label is associated with the field — VoiceOver reads label, then value, then helper.",
                        "Error: errorText announced via accessibilityValue; border + helper colour shift to danger.",
                        "Counter is a hint, not a constraint — typing past maxCount shows the error but never blocks input.",
                        "Secure entry: characters announced as \"dot\"; auto-fill / password manager respected.",
                        "ReadOnly conveys \"read-only\" while keeping focus and copyable text."
                    ]
                )
            }
        )
    }
}

/// Counter example demonstrating the no-gate input rule: typing past the cap
/// shows the counter turn red and surfaces an explicit error rather than
/// silently dropping keystrokes.
private struct CounterExample: View {
    @State private var value: String = "Hello"

    var body: some View {
        PrismaTextField(
            text: $value,
            label: "Tagline",
            helperText: "Keep it concise.",
            errorText: value.count > 24 ? "Too long: \(value.count)/24." : nil,
            counter: value.count,
            maxCount: 24
        )
    }
}
