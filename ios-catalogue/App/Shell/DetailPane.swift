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

                // Phase 0 placeholder. Phase 1+ replaces this with real
                // foundation showcases and component detail layouts (live demo,
                // variants, states, tokens used, a11y notes, code snippet,
                // interactive playground).
                Text("Phase 0 placeholder — implementation lands per-phase per docs/TODO.md.")
                    .font(PrismaTypography.bodyMd.font)
                    .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                    .padding(.top, PrismaSpacing.sp2)

                Spacer(minLength: PrismaSpacing.sp7)
            }
            .padding(PrismaSpacing.sp7)
            .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}
