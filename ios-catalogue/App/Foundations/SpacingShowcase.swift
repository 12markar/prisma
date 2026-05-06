import SwiftUI
import CoreUI

/// Visualises every spacing token as a horizontal accent bar at its actual width.
struct SpacingShowcase: View {
    @Environment(\.colorScheme) private var scheme

    private static let rows: [(String, CGFloat)] = [
        ("spacing.0", PrismaSpacing.sp0),
        ("spacing.1", PrismaSpacing.sp1),
        ("spacing.2", PrismaSpacing.sp2),
        ("spacing.3", PrismaSpacing.sp3),
        ("spacing.4", PrismaSpacing.sp4),
        ("spacing.5", PrismaSpacing.sp5),
        ("spacing.6", PrismaSpacing.sp6),
        ("spacing.7", PrismaSpacing.sp7),
        ("spacing.8", PrismaSpacing.sp8),
        ("spacing.9", PrismaSpacing.sp9),
        ("spacing.10", PrismaSpacing.sp10),
        ("spacing.11", PrismaSpacing.sp11),
        ("spacing.12", PrismaSpacing.sp12)
    ]

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
            ForEach(Self.rows.indices, id: \.self) { idx in
                let (name, value) = Self.rows[idx]
                spacingRow(name: name, value: value)
            }
        }
    }

    @ViewBuilder
    private func spacingRow(name: String, value: CGFloat) -> some View {
        HStack(spacing: PrismaSpacing.sp3) {
            Text(name)
                .font(.system(size: 11, weight: .medium, design: .monospaced))
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                .frame(width: 72, alignment: .leading)

            RoundedRectangle(cornerRadius: PrismaRadius.sm)
                .fill(PrismaSemanticColors.accentDefault.themed(scheme))
                .frame(width: max(value, 2), height: 20)

            Text("\(Int(value))pt")
                .font(.system(size: 11, weight: .medium, design: .monospaced))
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))

            Spacer()
        }
    }
}
