import SwiftUI
import CoreUI
import Components

/// Searchable grid of all 64 Prisma icons rendered from the bundled
/// PrismaIcons.xcassets. Iteration order mirrors Android's PrismaIcons.all
/// for parity.
struct IconsShowcase: View {
    @Environment(\.colorScheme) private var scheme
    @State private var query: String = ""

    private var filtered: [PrismaIcon] {
        let needle = query.trimmingCharacters(in: .whitespaces).lowercased()
        guard !needle.isEmpty else { return PrismaIcon.allCases }
        return PrismaIcon.allCases.filter { $0.rawValue.contains(needle) }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp5) {
            Text("\(PrismaIcon.allCases.count) icons · 24×24 grid · 1.75pt stroke · currentColor")
                .font(.system(size: 13, design: .monospaced))
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))

            // Filter field — uses native rounded text field; PrismaTextField
            // arrives in Phase 2 and this gets swapped.
            HStack(spacing: PrismaSpacing.sp2) {
                Image(prisma: .search)
                    .renderingMode(.template)
                    .resizable()
                    .frame(width: 18, height: 18)
                    .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                TextField("Filter icons by name", text: $query)
                    .textFieldStyle(.plain)
                    .font(PrismaTypography.bodyMd.font)
                    .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
            }
            .padding(.horizontal, PrismaSpacing.sp4)
            .padding(.vertical, PrismaSpacing.sp3)
            .background(PrismaSemanticColors.surfaceRaised.themed(scheme))
            .overlay(
                RoundedRectangle(cornerRadius: PrismaRadius.md)
                    .strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
            )
            .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))

            let columns = [GridItem(.adaptive(minimum: 96), spacing: PrismaSpacing.sp3)]
            LazyVGrid(columns: columns, spacing: PrismaSpacing.sp3) {
                ForEach(filtered, id: \.self) { icon in
                    iconTile(icon: icon)
                }
            }

            if filtered.isEmpty && !query.isEmpty {
                Text("No icons match '\(query)'.")
                    .font(PrismaTypography.bodyMd.font)
                    .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
            }
        }
    }

    @ViewBuilder
    private func iconTile(icon: PrismaIcon) -> some View {
        VStack(spacing: PrismaSpacing.sp2) {
            ZStack {
                Circle()
                    .fill(PrismaSemanticColors.surfaceRaised.themed(scheme))
                    .frame(width: 40, height: 40)
                Image(prisma: icon)
                    .renderingMode(.template)
                    .resizable()
                    .frame(width: 20, height: 20)
                    .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
            }
            Text(icon.rawValue)
                .font(.system(size: 11, design: .monospaced))
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                .lineLimit(1)
        }
        .padding(.vertical, PrismaSpacing.sp4)
        .padding(.horizontal, PrismaSpacing.sp2)
        .frame(maxWidth: .infinity)
        .background(PrismaSemanticColors.surfaceSunken.themed(scheme))
        .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
    }
}
