import SwiftUI
import CoreUI
import Components

struct ButtonShowcase: View {
    @Environment(\.colorScheme) private var scheme
    @State private var clickCount: Int = 0
    @State private var loading: Bool = false

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp7) {
            sectionHeader("Variants")
            HStack(spacing: PrismaSpacing.sp3) {
                PrismaButton("Primary", variant: .primary) {}
                PrismaButton("Secondary", variant: .secondary) {}
                PrismaButton("Outlined", variant: .outlined) {}
            }
            HStack(spacing: PrismaSpacing.sp3) {
                PrismaButton("Ghost", variant: .ghost) {}
                PrismaButton("Destructive", variant: .destructive) {}
                PrismaButton(
                    "",
                    variant: .icon,
                    leadingIcon: { AnyView(Image(prisma: .heart).renderingMode(.template)) },
                    accessibilityLabel: "Like"
                ) {}
            }

            sectionHeader("Sizes")
            HStack(spacing: PrismaSpacing.sp3) {
                PrismaButton("Small", size: .small) {}
                PrismaButton("Default", size: .default) {}
                PrismaButton("Large", size: .large) {}
            }

            sectionHeader("With icons")
            HStack(spacing: PrismaSpacing.sp3) {
                PrismaButton(
                    "Add",
                    leadingIcon: { AnyView(Image(prisma: .plus).renderingMode(.template)) }
                ) {}
                PrismaButton(
                    "Continue",
                    trailingIcon: { AnyView(Image(prisma: .arrowRight).renderingMode(.template)) }
                ) {}
            }

            sectionHeader("States")
            HStack(spacing: PrismaSpacing.sp3) {
                PrismaButton("Default") {}
                PrismaButton("Disabled", enabled: false) {}
                PrismaButton("Loading", loading: loading) {}
            }

            sectionHeader("Interactive")
            VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
                Text("Tap count: \(clickCount)")
                    .font(PrismaTypography.bodyMd.font)
                    .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))

                HStack(spacing: PrismaSpacing.sp3) {
                    PrismaButton(loading ? "Saving…" : "Save changes", loading: loading) {
                        clickCount += 1
                        loading = true
                    }
                    PrismaButton("Reset", variant: .ghost, enabled: !loading) {
                        clickCount = 0
                        loading = false
                    }
                }
            }
        }
    }

    @ViewBuilder
    private func sectionHeader(_ text: String) -> some View {
        Text(text.uppercased())
            .font(.system(size: 11, weight: .semibold))
            .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
    }
}
