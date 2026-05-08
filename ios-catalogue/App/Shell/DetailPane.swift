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
        // The SDUI demo is meant to look like a real product screen — no
        // breadcrumb, no entry-title chrome, no catalogue horizontal padding.
        // The JSON document owns its own padding via the root column's
        // `padding` property, so we render edge-to-edge and let the JSON
        // dictate inset.
        if entry.key == "sdui.live" {
            return AnyView(
                ScrollView {
                    showcase(for: entry)
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
            )
        }
        return AnyView(ScrollView {
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
        })
    }

    private func hasShowcase(_ entry: CatalogueEntry) -> Bool {
        // Every entry in the registry has a real showcase now.
        return true
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
        // Foundations
        case "foundation.typography": TypographyShowcase()
        case "foundation.colors":     ColorShowcase()
        case "foundation.spacing":    SpacingShowcase()
        case "foundation.radius":     RadiusShowcase()
        case "foundation.elevation":  ElevationShowcase()
        case "foundation.motion":     MotionShowcase()
        case "foundation.icons":      IconsShowcase()
        // Inputs
        case "input.button":            ButtonShowcase()
        case "input.textfield":         TextFieldShowcase()
        case "input.checkbox":          CheckboxShowcase()
        case "input.radio":             RadioShowcase()
        case "input.switch":            SwitchShowcase()
        case "input.slider":            SliderShowcase()
        case "input.segmentedControl":  SegmentedControlShowcase()
        case "input.searchBar":         SearchBarShowcase()
        case "input.autocomplete":      AutocompleteShowcase()
        case "input.stepper":           StepperShowcase()
        case "input.tagInput":          TagInputShowcase()
        case "input.datePicker":        DatePickerShowcase()
        case "input.timePicker":        TimePickerShowcase()
        case "input.colorPicker":       ColorPickerShowcase()
        // Feedback
        case "feedback.toast":       ToastShowcase()
        case "feedback.banner":      BannerShowcase()
        case "feedback.modal":       ModalShowcase()
        case "feedback.bottomSheet": BottomSheetShowcase()
        case "feedback.popover":     PopoverShowcase()
        case "feedback.tooltip":     TooltipShowcase()
        case "feedback.loading":     LoadingShowcase()
        case "feedback.skeleton":    SkeletonShowcase()
        case "feedback.badge":       BadgeShowcase()
        case "feedback.emptyState":  EmptyStateShowcase()
        case "feedback.drawer":      DrawerShowcase()
        // Navigation
        case "navigation.tabs":            TabsShowcase()
        case "navigation.chip":            ChipShowcase()
        case "navigation.commandPalette":  CommandPaletteShowcase()
        case "navigation.pagination":      PaginationShowcase()
        case "navigation.breadcrumb":      BreadcrumbShowcase()
        case "navigation.wizard":          WizardShowcase()
        // Data display
        case "data.card":        CardShowcase()
        case "data.listItem":    ListItemShowcase()
        case "data.avatar":      AvatarShowcase()
        case "data.avatarGroup": AvatarGroupShowcase()
        case "data.divider":     DividerShowcase()
        // Server-driven
        case "sdui.live":        SDUIScreen()
        default:
            Text("Phase 0 placeholder — implementation lands per-phase per docs/TODO.md.")
                .font(PrismaTypography.bodyMd.font)
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
        }
    }
}
