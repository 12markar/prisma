import SwiftUI
import CoreUI
import Components

/// Button playground.
///
/// - Knobs: text, variant, size, disabled, loading, leadingIcon, trailingIcon.
/// - Tapping the live preview fires a 2s loading cycle so users feel the
///   full lifecycle without leaving the loading toggle on permanently.
/// - States gallery pins the canonical states (default, secondary, outlined,
///   ghost, destructive, disabled, loading, icon-only) for at-a-glance compare.
struct ButtonShowcase: View {
    @State private var text: String = "Save changes"
    @State private var variant: PrismaButtonVariant = .primary
    @State private var size: PrismaButtonSize = .default
    @State private var disabled: Bool = false
    @State private var loading: Bool = false
    @State private var leadingIcon: PrismaIcon? = nil
    @State private var trailingIcon: PrismaIcon? = nil
    @State private var tapCount: Int = 0

    private let iconOptions: [PrismaIcon] = [
        .plus, .search, .heart, .arrowRight, .arrowLeft,
        .check, .close, .settings, .edit, .trash, .share, .download
    ]

    var body: some View {
        PlaygroundScaffold(
            preview: {
                PrismaButton(
                    loading ? "Saving…" : (text.isEmpty ? " " : text),
                    variant: variant,
                    size: size,
                    enabled: !disabled,
                    loading: loading,
                    leadingIcon: leadingIcon.map { icon in
                        { AnyView(Image(prisma: icon).renderingMode(.template).resizable().frame(width: 18, height: 18)) }
                    },
                    trailingIcon: trailingIcon.map { icon in
                        { AnyView(Image(prisma: icon).renderingMode(.template).resizable().frame(width: 18, height: 18)) }
                    },
                    accessibilityLabel: variant == .icon ? (text.isEmpty ? "Action" : text) : nil
                ) {
                    guard !disabled, !loading else { return }
                    tapCount += 1
                    loading = true
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) { loading = false }
                }
            },
            knobs: {
                StringKnobRow(
                    label: "Text",
                    value: $text,
                    placeholder: "Button label",
                    helper: "Tap the live preview to fire a 2s loading cycle (taps so far: \(tapCount))."
                )
                EnumKnobRow(
                    label: "Variant",
                    value: $variant,
                    values: [.primary, .secondary, .outlined, .ghost, .icon, .destructive],
                    optionLabel: { String(describing: $0) }
                )
                EnumKnobRow(
                    label: "Size",
                    value: $size,
                    values: [.small, .default, .large],
                    optionLabel: { String(describing: $0) }
                )
                BoolKnobRow(label: "Disabled", value: $disabled)
                BoolKnobRow(label: "Loading", value: $loading, helper: "Toggle on to see the spinner; auto-resolves in 2s.")
                IconKnobRow(label: "Leading icon", value: $leadingIcon, options: iconOptions)
                IconKnobRow(label: "Trailing icon", value: $trailingIcon, options: iconOptions)
            },
            states: {
                StateCell("Default") { PrismaButton("Save") {} }
                StateCell("Secondary") { PrismaButton("Cancel", variant: .secondary) {} }
                StateCell("Outlined") { PrismaButton("Discard", variant: .outlined) {} }
                StateCell("Ghost") { PrismaButton("Skip", variant: .ghost) {} }
                StateCell("Destructive") { PrismaButton("Delete", variant: .destructive) {} }
                StateCell("Disabled") { PrismaButton("Save", enabled: false) {} }
                StateCell("Loading") { PrismaButton("Saving…", loading: true) {} }
                StateCell("Icon-only (heart)") { LikeHeartButton() }
            },
            code: {
                var lines: [String] = ["PrismaButton("]
                lines.append("    \"\(text)\",")
                if variant != .primary { lines.append("    variant: .\(String(describing: variant)),") }
                if size != .default { lines.append("    size: .\(String(describing: size)),") }
                if disabled { lines.append("    enabled: false,") }
                if loading { lines.append("    loading: true,") }
                if leadingIcon != nil { lines.append("    leadingIcon: { AnyView(Image(prisma: .x).renderingMode(.template)) },") }
                if trailingIcon != nil { lines.append("    trailingIcon: { AnyView(Image(prisma: .x).renderingMode(.template)) },") }
                lines.append(") { /* … */ }")
                return lines.joined(separator: "\n")
            },
            a11y: {
                A11yPanel(
                    role: ".isButton",
                    minTouchTarget: "44 × 44 pt",
                    bullets: [
                        "The visible label IS the a11y label. Icon variant requires explicit accessibilityLabel.",
                        "Loading state announces \"Loading\"; click is no-op while loading but the label is still read.",
                        "Disabled state communicated by the role; visual dim is supporting, not primary.",
                        "Hit area expands to 44pt even when visual size (small) is smaller — never shrink the touch target.",
                        "Reduce Motion: press scale / ripple suppressed; background swap remains."
                    ]
                )
            }
        )
    }
}

/// Heart-fill toggle so the gallery cell visibly responds to taps.
private struct LikeHeartButton: View {
    @State private var liked: Bool = false
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        PrismaButton(
            "",
            variant: .icon,
            leadingIcon: {
                AnyView(
                    Image(prisma: .heart)
                        .renderingMode(.template)
                        .resizable()
                        .frame(width: 18, height: 18)
                        .foregroundStyle(
                            liked
                                ? PrismaSemanticColors.statusDangerDefault.themed(scheme)
                                : PrismaSemanticColors.textPrimary.themed(scheme)
                        )
                )
            },
            accessibilityLabel: liked ? "Unlike" : "Like"
        ) { liked.toggle() }
    }
}
