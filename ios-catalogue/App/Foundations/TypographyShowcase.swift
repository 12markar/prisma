import SwiftUI
import CoreUI

/// Type specimen — every typography token rendered in real sample text with
/// its name, size, weight, and tracking shown alongside in mono.
struct TypographyShowcase: View {
    @Environment(\.colorScheme) private var scheme

    private static let rows: [(String, PrismaTypography.Style, String)] = [
        ("display.lg",  PrismaTypography.displayLg,  "The quick brown fox"),
        ("display.md",  PrismaTypography.displayMd,  "The quick brown fox"),
        ("display.sm",  PrismaTypography.displaySm,  "The quick brown fox"),
        ("headline.lg", PrismaTypography.headlineLg, "Confident geometric sans"),
        ("headline.md", PrismaTypography.headlineMd, "Confident geometric sans"),
        ("headline.sm", PrismaTypography.headlineSm, "Confident geometric sans"),
        ("title.lg",    PrismaTypography.titleLg,    "Section heading"),
        ("title.md",    PrismaTypography.titleMd,    "Section heading"),
        ("title.sm",    PrismaTypography.titleSm,    "Section heading"),
        ("body.lg",     PrismaTypography.bodyLg,     "Considered, restrained, used sparingly. Color marks state and intent."),
        ("body.md",     PrismaTypography.bodyMd,     "Considered, restrained, used sparingly. Color marks state and intent."),
        ("body.sm",     PrismaTypography.bodySm,     "Considered, restrained, used sparingly. Color marks state and intent."),
        ("label.lg",    PrismaTypography.labelLg,    "ACTION LABEL"),
        ("label.md",    PrismaTypography.labelMd,    "ACTION LABEL"),
        ("label.sm",    PrismaTypography.labelSm,    "ACTION LABEL"),
        ("code.md",     PrismaTypography.codeMd,     "Color(red: 0.78, green: 0.40, blue: 0.14)  // accent.500"),
        ("code.sm",     PrismaTypography.codeSm,     "Color(red: 0.78, green: 0.40, blue: 0.14)  // accent.500")
    ]

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp7) {
            ForEach(Self.rows.indices, id: \.self) { index in
                let row = Self.rows[index]
                typeRow(name: row.0, style: row.1, sample: row.2)
            }
        }
    }

    @ViewBuilder
    private func typeRow(name: String, style: PrismaTypography.Style, sample: String) -> some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp2) {
            Text(name)
                .font(.system(size: 11, weight: .medium, design: .monospaced))
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))

            Text(sample)
                .font(style.font)
                .lineSpacing(style.lineHeight - 0)
                .tracking(style.letterSpacing)
                .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
        }
    }
}
