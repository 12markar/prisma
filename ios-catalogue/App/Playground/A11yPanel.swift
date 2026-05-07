import SwiftUI
import CoreUI

/// Inlines the relevant slice of each component's accessibility contract
/// — role, min touch target, focus/announce behaviour — directly under the
/// playground so callers see what they're committing to without leaving
/// the catalogue.
struct A11yPanel: View {
    @Environment(\.colorScheme) private var scheme
    let role: String
    let minTouchTarget: String
    let bullets: [String]

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
            HStack(spacing: PrismaSpacing.sp4) {
                field(label: "Role", value: role)
                field(label: "Min target", value: minTouchTarget)
            }
            VStack(alignment: .leading, spacing: PrismaSpacing.sp2) {
                ForEach(bullets, id: \.self) { bullet in
                    HStack(alignment: .top, spacing: PrismaSpacing.sp3) {
                        Circle()
                            .fill(PrismaSemanticColors.accentDefault.themed(scheme))
                            .frame(width: 6, height: 6)
                            .padding(.top, 6)
                        Text(bullet)
                            .font(PrismaTypography.bodySm.font)
                            .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                    }
                }
            }
        }
        .padding(PrismaSpacing.sp5)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(PrismaSemanticColors.surfaceSunken.themed(scheme))
        .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
        .overlay(
            RoundedRectangle(cornerRadius: PrismaRadius.md)
                .strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
        )
    }

    @ViewBuilder
    private func field(label: String, value: String) -> some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(label.uppercased())
                .font(PrismaTypography.labelSm.font)
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
            Text(value)
                .font(PrismaTypography.bodyMd.font)
                .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
        }
    }
}
