import SwiftUI
import CoreUI

/// Squares with each radius token applied — visual scale comparison.
struct RadiusShowcase: View {
    @Environment(\.colorScheme) private var scheme

    private static let tiles: [(String, CGFloat)] = [
        ("none", PrismaRadius.none),
        ("sm", PrismaRadius.sm),
        ("md", PrismaRadius.md),
        ("lg", PrismaRadius.lg),
        ("xl", PrismaRadius.xl),
        ("full", 48)   // visual cap; actual full is 9999 ≈ circle
    ]

    var body: some View {
        let columns = [
            GridItem(.adaptive(minimum: 120), spacing: PrismaSpacing.sp4)
        ]

        LazyVGrid(columns: columns, spacing: PrismaSpacing.sp4) {
            ForEach(Self.tiles.indices, id: \.self) { idx in
                let (name, radius) = Self.tiles[idx]
                tile(name: name, radius: radius)
            }
        }
    }

    @ViewBuilder
    private func tile(name: String, radius: CGFloat) -> some View {
        VStack(spacing: PrismaSpacing.sp2) {
            RoundedRectangle(cornerRadius: radius)
                .fill(PrismaSemanticColors.accentDefault.themed(scheme))
                .frame(width: 96, height: 96)

            Text("radius.\(name)")
                .font(.system(size: 11, weight: .medium, design: .monospaced))
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
        }
    }
}
