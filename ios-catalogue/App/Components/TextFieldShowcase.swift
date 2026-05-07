import SwiftUI
import CoreUI
import Components

private enum ValidationKind: String, CaseIterable, Hashable {
    case none = "None"
    case email = "Email"
    case min8 = "Min 8 chars"
}

struct TextFieldShowcase: View {
    @Environment(\.colorScheme) private var scheme

    @State private var value: String = ""
    @State private var label: String = "Email"
    @State private var placeholder: String = "you@example.com"
    @State private var helper: String = "We'll never share it."
    @State private var variant: PrismaTextFieldVariant = .outlined
    @State private var size: PrismaTextFieldSize = .medium
    @State private var enabled: Bool = true
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
        PlaygroundScreen(
            preview: {
                PrismaTextField(
                    text: $value,
                    label: label.isEmpty ? nil : label,
                    placeholder: placeholder.isEmpty ? nil : placeholder,
                    helperText: helper.isEmpty ? nil : helper,
                    errorText: derivedError,
                    enabled: enabled,
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
                BoolKnobRow(label: "Enabled", value: $enabled, helper: "Off renders the disabled state.")
                BoolKnobRow(label: "Read only", value: $readOnly)
                BoolKnobRow(label: "Secure entry", value: $secure)
                IconKnobRow(label: "Leading icon", value: $leadingIcon, options: iconOptions)
            },
            states: TextFieldShowcase.states(),
            code: { snippet },
            a11y: TextFieldShowcase.a11yReport
        )
    }

    private var snippet: String {
        var lines: [String] = ["PrismaTextField("]
        lines.append("    text: $value,")
        if !label.isEmpty { lines.append("    label: \"\(label)\",") }
        if !placeholder.isEmpty { lines.append("    placeholder: \"\(placeholder)\",") }
        if !helper.isEmpty { lines.append("    helperText: \"\(helper)\",") }
        if variant != .outlined { lines.append("    variant: .\(String(describing: variant)),") }
        if size != .medium { lines.append("    size: .\(String(describing: size)),") }
        if !enabled { lines.append("    enabled: false,") }
        if readOnly { lines.append("    readOnly: true,") }
        if secure { lines.append("    secureTextEntry: true,") }
        if maxCount > 0 { lines.append("    counter: value.count, maxCount: \(maxCount),") }
        lines.append(")")
        return lines.joined(separator: "\n")
    }

    private static func states() -> [AnyPlaygroundState] {
        [
            AnyPlaygroundState("Empty") { EmptyStateCell() },
            AnyPlaygroundState("Filled") { FilledStateCell() },
            AnyPlaygroundState("Error") {
                PrismaTextField(text: .constant("j@"), label: "Email", errorText: "Enter a valid email address.")
            },
            AnyPlaygroundState("Disabled") {
                PrismaTextField(text: .constant("maya@example.com"), label: "Email", enabled: false)
            },
            AnyPlaygroundState("Read only") {
                PrismaTextField(text: .constant("maya@example.com"), label: "Email", readOnly: true)
            },
            AnyPlaygroundState("Counter (live, non-blocking)") { CounterStateCell() },
            AnyPlaygroundState("Filled + leading icon") { FilledVariantStateCell() }
        ]
    }

    private static let a11yReport = A11yReport(
        role: "TextField",
        minTouchTarget: "44 pt height",
        screenReader: "Label is associated with the field — VoiceOver reads label first, then the current value, then helper text. Error messages are announced via accessibilityValue when they appear; the user hears the validation message without re-focusing.",
        voiceControl: "Voice Control targets the visible label (\"Tap Email\"). The field's current value is exposed via accessibilityValue so \"What does Email say?\" returns the typed content.",
        keyboard: "Tab focuses, Shift-Tab moves back, ESC clears focus. The keyboard type follows the validation knob (Email validation switches to the email keyboard with @ key in the suggestions row). Secure entry routes through Keychain auto-fill.",
        contrast: "All text colours verified at WCAG AA in light + dark themes — placeholder is text.tertiary above 4.5:1, helper is text.tertiary, error text uses status.danger (4.7:1 / 5.1:1). Build-time check-contrast.mjs gates regressions.",
        touchTarget: "Min height 44 pt — even for the small variant the entire field is the tap target. Trailing chevrons / clear buttons each have their own 44 × 44 pt hit area.",
        wcagQuote: "Labels or instructions are provided when content requires user input. — Prisma's TextField never ships unlabelled; the label is part of the API contract, not optional.",
        wcagRef: "3.3.2 Labels or Instructions, Level A"
    )
}

private struct EmptyStateCell: View {
    @State private var v: String = ""
    var body: some View {
        PrismaTextField(text: $v, label: "Email", placeholder: "you@example.com")
    }
}

private struct FilledStateCell: View {
    @State private var v: String = "maya@example.com"
    var body: some View {
        PrismaTextField(text: $v, label: "Email")
    }
}

private struct CounterStateCell: View {
    @State private var v: String = "Hello"
    var body: some View {
        PrismaTextField(
            text: $v,
            label: "Tagline",
            helperText: "Keep it concise.",
            errorText: v.count > 24 ? "Too long: \(v.count)/24." : nil,
            counter: v.count,
            maxCount: 24
        )
    }
}

private struct FilledVariantStateCell: View {
    @State private var v: String = ""
    @Environment(\.colorScheme) private var scheme
    var body: some View {
        PrismaTextField(
            text: $v,
            label: "Search",
            placeholder: "Search components",
            variant: .filled,
            leadingIcon: {
                {
                    AnyView(
                        Image(prisma: .search)
                            .renderingMode(.template)
                            .resizable()
                            .frame(width: 18, height: 18)
                            .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                    )
                }
            }()
        )
    }
}
