import SwiftUI
import CoreUI

/// Primitive ramps grouped by family, then semantic tokens with both light
/// and dark resolved values shown side-by-side.
struct ColorShowcase: View {
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp7) {
            sectionHeader("Primitives")
            primitiveRamp(name: "Neutral",  swatches: Self.neutralRamp)
            primitiveRamp(name: "Accent",   swatches: Self.accentRamp)
            primitiveRamp(name: "Success",  swatches: Self.successRamp)
            primitiveRamp(name: "Warning",  swatches: Self.warningRamp)
            primitiveRamp(name: "Danger",   swatches: Self.dangerRamp)
            primitiveRamp(name: "Info",     swatches: Self.infoRamp)

            sectionHeader("Semantic — surface")
            semanticRow(name: "surface.base",    color: PrismaSemanticColors.surfaceBase)
            semanticRow(name: "surface.raised",  color: PrismaSemanticColors.surfaceRaised)
            semanticRow(name: "surface.sunken",  color: PrismaSemanticColors.surfaceSunken)
            semanticRow(name: "surface.inverse", color: PrismaSemanticColors.surfaceInverse)

            sectionHeader("Semantic — text")
            semanticRow(name: "text.primary",   color: PrismaSemanticColors.textPrimary)
            semanticRow(name: "text.secondary", color: PrismaSemanticColors.textSecondary)
            semanticRow(name: "text.tertiary",  color: PrismaSemanticColors.textTertiary)
            semanticRow(name: "text.link",      color: PrismaSemanticColors.textLink)

            sectionHeader("Semantic — accent")
            semanticRow(name: "accent.default", color: PrismaSemanticColors.accentDefault)
            semanticRow(name: "accent.hover",   color: PrismaSemanticColors.accentHover)
            semanticRow(name: "accent.pressed", color: PrismaSemanticColors.accentPressed)
            semanticRow(name: "accent.subtle",  color: PrismaSemanticColors.accentSubtle)
        }
    }

    @ViewBuilder
    private func sectionHeader(_ text: String) -> some View {
        Text(text.uppercased())
            .font(.system(size: 11, weight: .semibold))
            .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
    }

    @ViewBuilder
    private func primitiveRamp(name: String, swatches: [(String, Color)]) -> some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp2) {
            Text(name)
                .font(.system(size: 13, weight: .medium))
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))

            HStack(spacing: PrismaSpacing.sp1) {
                ForEach(swatches.indices, id: \.self) { idx in
                    let (stop, color) = swatches[idx]
                    VStack(spacing: PrismaSpacing.sp1) {
                        RoundedRectangle(cornerRadius: PrismaRadius.sm)
                            .fill(color)
                            .frame(width: 38, height: 56)
                        Text(stop)
                            .font(.system(size: 10, weight: .medium, design: .monospaced))
                            .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                    }
                }
            }
        }
    }

    @ViewBuilder
    private func semanticRow(name: String, color: PrismaSemanticColor) -> some View {
        HStack(spacing: PrismaSpacing.sp3) {
            RoundedRectangle(cornerRadius: PrismaRadius.sm)
                .fill(color.light)
                .frame(width: 64, height: 40)
            RoundedRectangle(cornerRadius: PrismaRadius.sm)
                .fill(color.dark)
                .frame(width: 64, height: 40)
            Text(name)
                .font(.system(size: 14, design: .monospaced))
                .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
            Spacer()
        }
    }

    private static let neutralRamp: [(String, Color)] = [
        ("50", PrismaPrimitiveColors.neutral50),  ("100", PrismaPrimitiveColors.neutral100),
        ("200", PrismaPrimitiveColors.neutral200), ("300", PrismaPrimitiveColors.neutral300),
        ("400", PrismaPrimitiveColors.neutral400), ("500", PrismaPrimitiveColors.neutral500),
        ("600", PrismaPrimitiveColors.neutral600), ("700", PrismaPrimitiveColors.neutral700),
        ("800", PrismaPrimitiveColors.neutral800), ("900", PrismaPrimitiveColors.neutral900),
        ("950", PrismaPrimitiveColors.neutral950)
    ]
    private static let accentRamp: [(String, Color)] = [
        ("50", PrismaPrimitiveColors.accent50),  ("100", PrismaPrimitiveColors.accent100),
        ("200", PrismaPrimitiveColors.accent200), ("300", PrismaPrimitiveColors.accent300),
        ("400", PrismaPrimitiveColors.accent400), ("500", PrismaPrimitiveColors.accent500),
        ("600", PrismaPrimitiveColors.accent600), ("700", PrismaPrimitiveColors.accent700),
        ("800", PrismaPrimitiveColors.accent800), ("900", PrismaPrimitiveColors.accent900),
        ("950", PrismaPrimitiveColors.accent950)
    ]
    private static let successRamp: [(String, Color)] = [
        ("50", PrismaPrimitiveColors.success50),  ("100", PrismaPrimitiveColors.success100),
        ("200", PrismaPrimitiveColors.success200), ("300", PrismaPrimitiveColors.success300),
        ("400", PrismaPrimitiveColors.success400), ("500", PrismaPrimitiveColors.success500),
        ("600", PrismaPrimitiveColors.success600), ("700", PrismaPrimitiveColors.success700),
        ("800", PrismaPrimitiveColors.success800), ("900", PrismaPrimitiveColors.success900),
        ("950", PrismaPrimitiveColors.success950)
    ]
    private static let warningRamp: [(String, Color)] = [
        ("50", PrismaPrimitiveColors.warning50),  ("100", PrismaPrimitiveColors.warning100),
        ("200", PrismaPrimitiveColors.warning200), ("300", PrismaPrimitiveColors.warning300),
        ("400", PrismaPrimitiveColors.warning400), ("500", PrismaPrimitiveColors.warning500),
        ("600", PrismaPrimitiveColors.warning600), ("700", PrismaPrimitiveColors.warning700),
        ("800", PrismaPrimitiveColors.warning800), ("900", PrismaPrimitiveColors.warning900),
        ("950", PrismaPrimitiveColors.warning950)
    ]
    private static let dangerRamp: [(String, Color)] = [
        ("50", PrismaPrimitiveColors.danger50),  ("100", PrismaPrimitiveColors.danger100),
        ("200", PrismaPrimitiveColors.danger200), ("300", PrismaPrimitiveColors.danger300),
        ("400", PrismaPrimitiveColors.danger400), ("500", PrismaPrimitiveColors.danger500),
        ("600", PrismaPrimitiveColors.danger600), ("700", PrismaPrimitiveColors.danger700),
        ("800", PrismaPrimitiveColors.danger800), ("900", PrismaPrimitiveColors.danger900),
        ("950", PrismaPrimitiveColors.danger950)
    ]
    private static let infoRamp: [(String, Color)] = [
        ("50", PrismaPrimitiveColors.info50),  ("100", PrismaPrimitiveColors.info100),
        ("200", PrismaPrimitiveColors.info200), ("300", PrismaPrimitiveColors.info300),
        ("400", PrismaPrimitiveColors.info400), ("500", PrismaPrimitiveColors.info500),
        ("600", PrismaPrimitiveColors.info600), ("700", PrismaPrimitiveColors.info700),
        ("800", PrismaPrimitiveColors.info800), ("900", PrismaPrimitiveColors.info900),
        ("950", PrismaPrimitiveColors.info950)
    ]
}
