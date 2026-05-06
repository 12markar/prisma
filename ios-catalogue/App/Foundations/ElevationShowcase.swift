import SwiftUI
import CoreUI

/// Each elevation level rendered as a card with light + dark side-by-side
/// so the dual-mode token (light = drop shadow; dark = inset glow + border)
/// is visible.
///
/// Note: SwiftUI's `.shadow` renders a single layer. To respect the
/// multi-layer Prisma elevation spec faithfully, render each PrismaShadow
/// in `level.light` / `level.dark` as a stacked `.shadow` modifier
/// (SwiftUI composes them). A custom `Modifier.prismaShadow(level)`
/// abstraction is Phase 1.x polish.
struct ElevationShowcase: View {
    @Environment(\.colorScheme) private var scheme

    private static let rows: [(String, PrismaElevation)] = [
        ("elevation.0", PrismaElevations.level0),
        ("elevation.1", PrismaElevations.level1),
        ("elevation.2", PrismaElevations.level2),
        ("elevation.3", PrismaElevations.level3),
        ("elevation.4", PrismaElevations.level4),
        ("elevation.5", PrismaElevations.level5)
    ]

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
            ForEach(Self.rows.indices, id: \.self) { idx in
                let (name, level) = Self.rows[idx]
                row(name: name, level: level)
            }
        }
    }

    @ViewBuilder
    private func row(name: String, level: PrismaElevation) -> some View {
        HStack(spacing: PrismaSpacing.sp4) {
            Text(name)
                .font(.system(size: 13, weight: .medium, design: .monospaced))
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                .frame(width: 100, alignment: .leading)

            // Light card on a light surface.
            elevationCard(layers: level.light, surface: PrismaPrimitiveColors.neutral50)

            // Dark card on a dark surface.
            elevationCard(layers: level.dark, surface: PrismaPrimitiveColors.neutral800)

            Spacer()
        }
    }

    @ViewBuilder
    private func elevationCard(layers: [PrismaShadow], surface: Color) -> some View {
        // Compose multiple .shadow modifiers — SwiftUI stacks them so the
        // multi-layer Prisma shadow renders faithfully.
        let card = RoundedRectangle(cornerRadius: PrismaRadius.md)
            .fill(surface)
            .frame(width: 120, height: 80)

        card
            .modifier(LayeredShadows(layers: layers))
            // Subtle border for dark inset-glow approximation on supported levels.
            .overlay {
                RoundedRectangle(cornerRadius: PrismaRadius.md)
                    .strokeBorder(layers.first(where: { $0.inset })?.color ?? .clear, lineWidth: 1)
            }
    }
}

private struct LayeredShadows: ViewModifier {
    let layers: [PrismaShadow]

    func body(content: Content) -> some View {
        layers.filter { !$0.inset }.reduce(AnyView(content)) { partial, layer in
            AnyView(partial.shadow(color: layer.color, radius: layer.blur / 2, x: 0, y: layer.offsetY))
        }
    }
}
