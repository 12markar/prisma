import SwiftUI
import CoreUI
import Components

/// Pilot redesign of the playground UX. Replaces the long scrollable
/// "knobs above states above code" stack with a focused screen:
///
///   1. Live preview surface (large, breathing)
///   2. Action pill row — Edit · A11y
///   3. States horizontal pager (fixed height, page dots)
///   4. Inline code snippet
///
/// Knobs and the accessibility report each open in their own sheet so
/// the live preview never gets pushed off-screen by a long edit panel.
struct ButtonShowcase: View {
    @Environment(\.colorScheme) private var scheme

    @State private var text: String = "Save changes"
    @State private var variant: PrismaButtonVariant = .primary
    @State private var size: PrismaButtonSize = .default
    /// Single "Enabled" toggle replaces the separate Disabled bool — clearer
    /// mental model: ON means the component is enabled.
    @State private var enabled: Bool = true
    @State private var loading: Bool = false
    @State private var leadingIcon: PrismaIcon? = nil
    @State private var trailingIcon: PrismaIcon? = nil
    @State private var tapCount: Int = 0
    @State private var knobsOpen: Bool = false
    @State private var a11yOpen: Bool = false

    private let iconOptions: [PrismaIcon] = [
        .plus, .search, .heart, .arrowRight, .arrowLeft,
        .check, .close, .settings, .edit, .trash, .share, .upload, .download
    ]

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp5) {
            // 1. Live preview — taller and centered for breathing room.
            PreviewSurface {
                PrismaButton(
                    loading ? "Saving…" : (text.isEmpty ? " " : text),
                    variant: variant,
                    size: size,
                    enabled: enabled,
                    loading: loading,
                    leadingIcon: leadingIcon.map { icon in
                        { AnyView(Image(prisma: icon).renderingMode(.template).resizable().frame(width: 18, height: 18)) }
                    },
                    trailingIcon: trailingIcon.map { icon in
                        { AnyView(Image(prisma: icon).renderingMode(.template).resizable().frame(width: 18, height: 18)) }
                    },
                    accessibilityLabel: variant == .icon ? (text.isEmpty ? "Action" : text) : nil
                ) {
                    // Loading is purely driven by the knob toggle now —
                    // tap only increments the tap counter. Previously a tap
                    // also forced loading=true for 2s, which made the
                    // button feel "always loading" on the first tap.
                    guard enabled, !loading else { return }
                    tapCount += 1
                }
            }
            .frame(minHeight: 240)

            // 2. Action pills — always above the fold so the user never has
            //    to hunt for controls.
            HStack(spacing: PrismaSpacing.sp3) {
                actionPill(icon: .edit, label: "Edit") { knobsOpen = true }
                actionPill(icon: .eye, label: "A11y") { a11yOpen = true }
            }

            // 3. States horizontal pager — fixed height per cell so swiping
            //    between states feels stable.
            sectionLabel("States — swipe to compare")
            StatesPager(states: ButtonShowcase.states())

            // 4. Inline code snippet for the current preview configuration.
            sectionLabel("Usage")
            CodeBlock(code: snippet)

            Text("Live preview taps: \(tapCount)")
                .font(PrismaTypography.bodySm.font)
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                .padding(.top, PrismaSpacing.sp2)
        }
        .sheet(isPresented: $knobsOpen) {
            knobsSheet
                .presentationDetents([.medium, .large])
                .presentationDragIndicator(.visible)
        }
        .sheet(isPresented: $a11yOpen) {
            A11ySheetContent(report: ButtonShowcase.a11yReport)
                .presentationDetents([.large])
                .presentationDragIndicator(.visible)
        }
    }

    private var knobsSheet: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
                Text("Edit")
                    .font(PrismaTypography.headlineSm.font)
                    .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                StringKnobRow(label: "Text", value: $text, placeholder: "Button label")
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
                BoolKnobRow(label: "Enabled", value: $enabled, helper: "Off renders the component in its disabled state.")
                BoolKnobRow(label: "Loading", value: $loading, helper: "Toggle on to render the spinner state.")
                IconKnobRow(label: "Leading icon", value: $leadingIcon, options: iconOptions)
                IconKnobRow(label: "Trailing icon", value: $trailingIcon, options: iconOptions)
                PrismaButton("Done") { knobsOpen = false }
                    .frame(maxWidth: .infinity)
            }
            .padding(.horizontal, PrismaSpacing.sp5)
            .padding(.vertical, PrismaSpacing.sp4)
        }
    }

    @ViewBuilder
    private func sectionLabel(_ text: String) -> some View {
        Text(text.uppercased())
            .font(PrismaTypography.labelSm.font)
            .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
    }

    @ViewBuilder
    private func actionPill(icon: PrismaIcon, label: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            HStack(spacing: PrismaSpacing.sp2) {
                Image(prisma: icon)
                    .renderingMode(.template)
                    .resizable()
                    .frame(width: 16, height: 16)
                    .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                Text(label)
                    .font(PrismaTypography.labelMd.font)
                    .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
            }
            .frame(maxWidth: .infinity)
            .padding(.horizontal, PrismaSpacing.sp4)
            .padding(.vertical, PrismaSpacing.sp3)
            .background(PrismaSemanticColors.surfaceRaised.themed(scheme))
            .overlay(
                RoundedRectangle(cornerRadius: PrismaRadius.md)
                    .strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
            )
            .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
        }
        .buttonStyle(.plain)
    }

    private var snippet: String {
        var lines: [String] = ["PrismaButton("]
        lines.append("    \"\(text)\",")
        if variant != .primary { lines.append("    variant: .\(String(describing: variant)),") }
        if size != .default { lines.append("    size: .\(String(describing: size)),") }
        if !enabled { lines.append("    enabled: false,") }
        if loading { lines.append("    loading: true,") }
        if leadingIcon != nil { lines.append("    leadingIcon: { AnyView(Image(prisma: .x).renderingMode(.template)) },") }
        if trailingIcon != nil { lines.append("    trailingIcon: { AnyView(Image(prisma: .x).renderingMode(.template)) },") }
        lines.append(") { /* … */ }")
        return lines.joined(separator: "\n")
    }

    private static func states() -> [AnyPlaygroundState] {
        [
            AnyPlaygroundState("Default") { PrismaButton("Save changes") {} },
            AnyPlaygroundState("Secondary") { PrismaButton("Cancel", variant: .secondary) {} },
            AnyPlaygroundState("Outlined") { PrismaButton("Discard", variant: .outlined) {} },
            AnyPlaygroundState("Ghost") { PrismaButton("Skip", variant: .ghost) {} },
            AnyPlaygroundState("Destructive") { PrismaButton("Delete", variant: .destructive) {} },
            AnyPlaygroundState("Disabled") { PrismaButton("Save", enabled: false) {} },
            AnyPlaygroundState("Loading") { PrismaButton("Saving…", loading: true) {} },
            AnyPlaygroundState("Icon-only (heart)") { LikeHeartButton() }
        ]
    }

    private static let a11yReport = A11yReport(
        role: ".isButton",
        minTouchTarget: "44 × 44 pt",
        screenReader: "The visible label IS the accessible label. For .icon variants, accessibilityLabel is required and enforced as a precondition. Loading state announces \"Loading\"; the click is no-op while loading but the label still reads.",
        voiceControl: "Voice Control targets the role and label, so \"Tap Save changes\" or \"Tap 5\" (with number overlay) both activate the right button. The icon variant relies on its accessibilityLabel for the spoken target.",
        keyboard: "Tab focuses; Space / Return activates. SwiftUI's default focus order matches the visual order. Disabled state is reachable but unactivated. No custom shortcuts — use the surrounding screen's shortcuts (e.g. Modal's confirm button as default).",
        contrast: "All button variants verified at WCAG AA in light + dark themes — body label ≥ 4.5:1 against the background, hover/pressed states stay above 4.5:1 too. Build-time contrast checker (scripts/check-contrast.mjs) gates this.",
        touchTarget: "Hit area expands to 44 × 44 pt on iOS even when the visual size (small) is smaller. Never shrink the touch target to match the visual; the visual is purely cosmetic.",
        wcagQuote: "The size of the target for pointer inputs is at least 24 by 24 CSS pixels — Prisma exceeds this with 44 × 44 pt across every variant.",
        wcagRef: "2.5.8 Target Size (Minimum), Level AA"
    )
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
