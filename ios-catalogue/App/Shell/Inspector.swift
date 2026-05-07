import SwiftUI
import CoreUI
import Components
#if canImport(UIKit)
import UIKit
#endif

/// Right-side runtime token reference. Lists every semantic colour with its
/// resolved hex for the active theme, plus the spacing / radius scales.
/// A stop-gap for "what's this token actually rendering as right now?"
/// questions during development.
struct InspectorOverlay: View {
    @Environment(\.colorScheme) private var scheme
    @Binding var open: Bool

    var body: some View {
        ZStack {
            if open {
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .transition(.opacity)
                    .onTapGesture { open = false }

                HStack(spacing: 0) {
                    Spacer()
                    InspectorPanel(onClose: { open = false })
                        .frame(maxWidth: 420)
                        .frame(maxHeight: .infinity)
                        .transition(.move(edge: .trailing))
                }
                .ignoresSafeArea()
            }
        }
        .animation(.easeInOut(duration: 0.25), value: open)
    }
}

private struct InspectorPanel: View {
    @Environment(\.colorScheme) private var scheme
    let onClose: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack {
                Text("Inspector")
                    .font(PrismaTypography.titleMd.font)
                    .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                Spacer()
                Button(action: onClose) {
                    Image(prisma: .close)
                        .renderingMode(.template)
                        .resizable()
                        .frame(width: 16, height: 16)
                        .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                        .frame(width: 28, height: 28)
                }
                .buttonStyle(.plain)
                .accessibilityLabel("Close inspector")
            }
            .padding(.horizontal, PrismaSpacing.sp5)
            .padding(.vertical, PrismaSpacing.sp4)

            Divider().background(PrismaSemanticColors.borderSubtle.themed(scheme))

            ScrollView {
                VStack(alignment: .leading, spacing: PrismaSpacing.sp7) {
                    group("Surface") {
                        colorRow("surfaceBase", PrismaSemanticColors.surfaceBase)
                        colorRow("surfaceRaised", PrismaSemanticColors.surfaceRaised)
                        colorRow("surfaceSunken", PrismaSemanticColors.surfaceSunken)
                        colorRow("surfaceInverse", PrismaSemanticColors.surfaceInverse)
                        colorRow("surfaceOverlay", PrismaSemanticColors.surfaceOverlay)
                    }
                    group("Text") {
                        colorRow("textPrimary", PrismaSemanticColors.textPrimary)
                        colorRow("textSecondary", PrismaSemanticColors.textSecondary)
                        colorRow("textTertiary", PrismaSemanticColors.textTertiary)
                        colorRow("textDisabled", PrismaSemanticColors.textDisabled)
                        colorRow("textOnAccent", PrismaSemanticColors.textOnAccent)
                        colorRow("textLink", PrismaSemanticColors.textLink)
                    }
                    group("Border") {
                        colorRow("borderSubtle", PrismaSemanticColors.borderSubtle)
                        colorRow("borderDefault", PrismaSemanticColors.borderDefault)
                        colorRow("borderStrong", PrismaSemanticColors.borderStrong)
                        colorRow("borderFocus", PrismaSemanticColors.borderFocus)
                    }
                    group("Accent") {
                        colorRow("accentDefault", PrismaSemanticColors.accentDefault)
                        colorRow("accentHover", PrismaSemanticColors.accentHover)
                        colorRow("accentPressed", PrismaSemanticColors.accentPressed)
                        colorRow("accentSubtle", PrismaSemanticColors.accentSubtle)
                    }
                    group("Status") {
                        colorRow("Success", PrismaSemanticColors.statusSuccessDefault)
                        colorRow("Warning", PrismaSemanticColors.statusWarningDefault)
                        colorRow("Danger", PrismaSemanticColors.statusDangerDefault)
                        colorRow("Info", PrismaSemanticColors.statusInfoDefault)
                    }
                    group("Spacing") {
                        scalarRow("sp1", "\(Int(PrismaSpacing.sp1)) pt")
                        scalarRow("sp2", "\(Int(PrismaSpacing.sp2)) pt")
                        scalarRow("sp3", "\(Int(PrismaSpacing.sp3)) pt")
                        scalarRow("sp4", "\(Int(PrismaSpacing.sp4)) pt")
                        scalarRow("sp5", "\(Int(PrismaSpacing.sp5)) pt")
                        scalarRow("sp6", "\(Int(PrismaSpacing.sp6)) pt")
                        scalarRow("sp7", "\(Int(PrismaSpacing.sp7)) pt")
                        scalarRow("sp8", "\(Int(PrismaSpacing.sp8)) pt")
                    }
                    group("Radius") {
                        scalarRow("sm", "\(Int(PrismaRadius.sm)) pt")
                        scalarRow("md", "\(Int(PrismaRadius.md)) pt")
                        scalarRow("lg", "\(Int(PrismaRadius.lg)) pt")
                        scalarRow("full", "\(Int(PrismaRadius.full)) pt")
                    }
                    Spacer().frame(height: PrismaSpacing.sp7)
                }
                .padding(.horizontal, PrismaSpacing.sp5)
                .padding(.vertical, PrismaSpacing.sp5)
            }
        }
        .background(PrismaSemanticColors.surfaceBase.themed(scheme))
        .overlay(
            Rectangle()
                .frame(width: 1)
                .foregroundStyle(PrismaSemanticColors.borderSubtle.themed(scheme)),
            alignment: .leading
        )
    }

    @ViewBuilder
    private func group<Content: View>(_ label: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
            Text(label.uppercased())
                .font(PrismaTypography.labelSm.font)
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
            VStack(alignment: .leading, spacing: PrismaSpacing.sp2) {
                content()
            }
        }
    }

    @ViewBuilder
    private func colorRow(_ label: String, _ token: PrismaSemanticColor) -> some View {
        let resolved = token.themed(scheme)
        let hex = hexString(of: resolved)
        HStack(spacing: PrismaSpacing.sp3) {
            RoundedRectangle(cornerRadius: 4)
                .fill(resolved)
                .frame(width: 20, height: 20)
                .overlay(
                    RoundedRectangle(cornerRadius: 4)
                        .strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
                )
            Text(label)
                .font(PrismaTypography.bodySm.font)
                .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                .frame(width: 140, alignment: .leading)
            Text(hex)
                .font(.system(.footnote, design: .monospaced))
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
        }
    }

    @ViewBuilder
    private func scalarRow(_ label: String, _ value: String) -> some View {
        HStack(spacing: PrismaSpacing.sp3) {
            Text(label)
                .font(PrismaTypography.bodySm.font)
                .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                .frame(width: 80, alignment: .leading)
            Text(value)
                .font(.system(.footnote, design: .monospaced))
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
        }
    }
}

private func hexString(of color: Color) -> String {
    #if canImport(UIKit)
    var r: CGFloat = 0, g: CGFloat = 0, b: CGFloat = 0, a: CGFloat = 0
    UIColor(color).getRed(&r, green: &g, blue: &b, alpha: &a)
    return String(format: "#%02X%02X%02X", Int(r * 255), Int(g * 255), Int(b * 255))
    #else
    return ""
    #endif
}
