import SwiftUI
import CoreUI

struct DetailPane: View {
    let entry: CatalogueEntry?

    @Environment(\.colorScheme) private var scheme

    var body: some View {
        ZStack {
            PrismaSemanticColors.surfaceBase.themed(scheme).ignoresSafeArea()
            if let entry {
                entryContent(entry)
            } else {
                emptyContent
            }
        }
    }

    private var emptyContent: some View {
        Text("Select a component to see its showcase.")
            .font(PrismaTypography.bodyLg.font)
            .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
            .multilineTextAlignment(.center)
            .padding(PrismaSpacing.sp7)
    }

    private func entryContent(_ entry: CatalogueEntry) -> some View {
        ScrollView {
            VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
                Text(entry.section.rawValue)
                    .font(PrismaTypography.labelSm.font)
                    .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))

                Text(entry.title)
                    .font(PrismaTypography.headlineLg.font)
                    .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))

                showcase(for: entry)
                    .padding(.top, PrismaSpacing.sp4)

                Spacer(minLength: PrismaSpacing.sp7)
            }
            .padding(PrismaSpacing.sp7)
            .frame(maxWidth: .infinity, alignment: .leading)
        }
    }

    @ViewBuilder
    private func showcase(for entry: CatalogueEntry) -> some View {
        switch entry.key {
        case "foundation.typography": TypographyShowcase()
        case "foundation.colors":     ColorShowcase()
        case "foundation.spacing":    SpacingShowcase()
        case "foundation.radius":     RadiusShowcase()
        default:
            Text("Phase 0 placeholder — implementation lands per-phase per docs/TODO.md.")
                .font(PrismaTypography.bodyMd.font)
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
        }
    }
}
