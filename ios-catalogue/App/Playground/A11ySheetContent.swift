import SwiftUI
import CoreUI
import Components

/// Per-component a11y dossier. Sections cover screen reader, voice control,
/// keyboard, contrast, touch target, with a WCAG 2.2 quote anchor.
public struct A11yReport {
    let role: String
    let minTouchTarget: String
    let screenReader: String
    let voiceControl: String
    let keyboard: String
    let contrast: String
    let touchTarget: String
    let wcagQuote: String
    let wcagRef: String

    public init(
        role: String,
        minTouchTarget: String,
        screenReader: String,
        voiceControl: String,
        keyboard: String,
        contrast: String,
        touchTarget: String,
        wcagQuote: String,
        wcagRef: String
    ) {
        self.role = role
        self.minTouchTarget = minTouchTarget
        self.screenReader = screenReader
        self.voiceControl = voiceControl
        self.keyboard = keyboard
        self.contrast = contrast
        self.touchTarget = touchTarget
        self.wcagQuote = wcagQuote
        self.wcagRef = wcagRef
    }
}

struct A11ySheetContent: View {
    @Environment(\.colorScheme) private var scheme
    let report: A11yReport

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: PrismaSpacing.sp6) {
                VStack(alignment: .leading, spacing: PrismaSpacing.sp1) {
                    Text("Accessibility")
                        .font(PrismaTypography.headlineSm.font)
                        .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                    Text("How this component reads, talks, and responds across assistive tech.")
                        .font(PrismaTypography.bodyMd.font)
                        .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                }

                HStack(spacing: PrismaSpacing.sp3) {
                    quickFact(label: "Role", value: report.role)
                    quickFact(label: "Min target", value: report.minTouchTarget)
                }

                section(icon: .message, title: "Screen reader", subtitle: "TalkBack · VoiceOver", body: report.screenReader)
                section(icon: .phone, title: "Voice control", subtitle: "Voice Access · Voice Control", body: report.voiceControl)
                section(icon: .grid, title: "Keyboard", subtitle: "External keyboard · Tab navigation", body: report.keyboard)
                section(icon: .eye, title: "Color contrast", subtitle: "WCAG AA · 4.5:1 body, 3:1 large text", body: report.contrast)
                section(icon: .scan, title: "Touch target", subtitle: "48 × 48 dp Android · 44 × 44 pt iOS", body: report.touchTarget)

                VStack(alignment: .leading, spacing: PrismaSpacing.sp2) {
                    HStack(spacing: PrismaSpacing.sp2) {
                        Image(prisma: .doc)
                            .renderingMode(.template)
                            .resizable()
                            .frame(width: 14, height: 14)
                            .foregroundStyle(PrismaSemanticColors.accentDefault.themed(scheme))
                        Text("WCAG 2.2 — \(report.wcagRef)")
                            .font(.system(.caption, design: .monospaced).weight(.medium))
                            .foregroundStyle(PrismaSemanticColors.accentDefault.themed(scheme))
                    }
                    Text("“\(report.wcagQuote)”")
                        .font(PrismaTypography.bodyMd.font)
                        .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                }
                .padding(PrismaSpacing.sp4)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(PrismaSemanticColors.accentSubtle.themed(scheme))
                .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
            }
            .padding(.horizontal, PrismaSpacing.sp5)
            .padding(.vertical, PrismaSpacing.sp5)
        }
    }

    @ViewBuilder
    private func quickFact(label: String, value: String) -> some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(label.uppercased())
                .font(PrismaTypography.labelSm.font)
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
            Text(value)
                .font(PrismaTypography.bodyMd.font)
                .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
        }
        .padding(.horizontal, PrismaSpacing.sp4)
        .padding(.vertical, PrismaSpacing.sp3)
        .background(PrismaSemanticColors.surfaceSunken.themed(scheme))
        .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
    }

    @ViewBuilder
    private func section(icon: PrismaIcon, title: String, subtitle: String, body: String) -> some View {
        HStack(alignment: .top, spacing: PrismaSpacing.sp4) {
            Image(prisma: icon)
                .renderingMode(.template)
                .resizable()
                .frame(width: 18, height: 18)
                .foregroundStyle(PrismaSemanticColors.accentDefault.themed(scheme))
                .frame(width: 36, height: 36)
                .background(PrismaSemanticColors.accentSubtle.themed(scheme))
                .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
                .overlay(
                    RoundedRectangle(cornerRadius: PrismaRadius.md)
                        .strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
                )
            VStack(alignment: .leading, spacing: PrismaSpacing.sp1) {
                Text(title)
                    .font(PrismaTypography.titleSm.font)
                    .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                Text(subtitle)
                    .font(PrismaTypography.labelSm.font)
                    .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                Text(body)
                    .font(PrismaTypography.bodyMd.font)
                    .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                    .padding(.top, PrismaSpacing.sp1)
            }
        }
    }
}
