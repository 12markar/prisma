import SwiftUI
import CoreUI
import Components

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
        let total = CatalogueRegistry.entries.count
        let implemented = 7   // 6 foundations + Button — keep in sync with implementation phases

        return VStack(spacing: PrismaSpacing.sp5) {
            Spacer()
            // Decorative accent ring.
            Circle()
                .fill(PrismaSemanticColors.accentSubtle.themed(scheme))
                .frame(width: 72, height: 72)

            Text("Prisma")
                .font(PrismaTypography.displaySm.font)
                .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))

            Text("Pick a component from the sidebar to see its showcase.")
                .font(PrismaTypography.bodyLg.font)
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                .multilineTextAlignment(.center)

            HStack(spacing: PrismaSpacing.sp7) {
                stat(label: "ENTRIES", value: "\(total)")
                stat(label: "IMPLEMENTED", value: "\(implemented)")
                stat(label: "PHASE", value: "1")
            }
            .padding(.top, PrismaSpacing.sp4)
            Spacer()
        }
        .padding(PrismaSpacing.sp7)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    @ViewBuilder
    private func stat(label: String, value: String) -> some View {
        VStack(spacing: PrismaSpacing.sp1) {
            Text(value)
                .font(.system(size: 26, weight: .semibold, design: .monospaced))
                .foregroundStyle(PrismaSemanticColors.accentDefault.themed(scheme))
            Text(label)
                .font(.system(size: 11, weight: .semibold))
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
        }
    }

    private func entryContent(_ entry: CatalogueEntry) -> some View {
        ScrollView {
            VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
                // Section breadcrumb pill.
                Text(entry.section.rawValue)
                    .font(.system(size: 11, weight: .semibold))
                    .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                    .padding(.horizontal, PrismaSpacing.sp3)
                    .padding(.vertical, PrismaSpacing.sp1)
                    .background(PrismaSemanticColors.surfaceSunken.themed(scheme))
                    .clipShape(Capsule())

                Text(entry.title)
                    .font(PrismaTypography.displaySm.font)
                    .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))

                if !hasShowcase(entry) {
                    Text("Spec ready · implementation pending in upcoming phase.")
                        .font(PrismaTypography.bodyMd.font)
                        .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                }

                Rectangle()
                    .fill(PrismaSemanticColors.borderSubtle.themed(scheme))
                    .frame(height: 1)
                    .padding(.vertical, PrismaSpacing.sp1)

                if hasShowcase(entry) {
                    showcase(for: entry)
                        .padding(.top, PrismaSpacing.sp4)
                } else {
                    placeholderState(entry: entry)
                }

                Spacer(minLength: PrismaSpacing.sp7)
            }
            .padding(.horizontal, PrismaSpacing.sp8)
            .padding(.vertical, PrismaSpacing.sp7)
            .frame(maxWidth: .infinity, alignment: .leading)
        }
    }

    private func hasShowcase(_ entry: CatalogueEntry) -> Bool {
        switch entry.key {
        case "foundation.typography", "foundation.colors", "foundation.spacing",
             "foundation.radius", "foundation.elevation", "foundation.motion",
             "foundation.icons", "input.button":
            return true
        default:
            return false
        }
    }

    @ViewBuilder
    private func placeholderState(entry: CatalogueEntry) -> some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
            Text("Implementation queued")
                .font(PrismaTypography.titleMd.font)
                .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))

            Text("The spec for \(entry.title.lowercased()) is complete. Component code lands in the upcoming phase per docs/TODO.md.")
                .font(PrismaTypography.bodyMd.font)
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))

            if !entry.tags.isEmpty {
                HStack(spacing: PrismaSpacing.sp2) {
                    ForEach(Array(entry.tags.prefix(4)), id: \.self) { tag in
                        Text(tag)
                            .font(.system(size: 11, weight: .semibold))
                            .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                            .padding(.horizontal, PrismaSpacing.sp3)
                            .padding(.vertical, PrismaSpacing.sp1)
                            .background(PrismaSemanticColors.surfaceRaised.themed(scheme))
                            .clipShape(Capsule())
                    }
                }
            }
        }
        .padding(PrismaSpacing.sp7)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(PrismaSemanticColors.surfaceSunken.themed(scheme))
        .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.lg))
    }

    @ViewBuilder
    private func showcase(for entry: CatalogueEntry) -> some View {
        switch entry.key {
        case "foundation.typography": TypographyShowcase()
        case "foundation.colors":     ColorShowcase()
        case "foundation.spacing":    SpacingShowcase()
        case "foundation.radius":     RadiusShowcase()
        case "foundation.elevation":  ElevationShowcase()
        case "foundation.motion":     MotionShowcase()
        case "foundation.icons":      IconsShowcase()
        case "input.button":          ButtonShowcase()
        default:
            Text("Phase 0 placeholder — implementation lands per-phase per docs/TODO.md.")
                .font(PrismaTypography.bodyMd.font)
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
        }
    }
}
