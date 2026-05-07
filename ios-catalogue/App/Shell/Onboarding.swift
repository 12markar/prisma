import SwiftUI
import CoreUI
import Components

/// First-launch welcome overlay. Shown once, dismissed forever after.
/// Lives at the catalogue root so it covers both panes in tablet mode.
struct OnboardingOverlay: View {
    @Environment(\.colorScheme) private var scheme
    @Binding var visible: Bool

    var body: some View {
        ZStack {
            if visible {
                Color.black.opacity(0.45)
                    .ignoresSafeArea()
                    .transition(.opacity)
                    .onTapGesture { dismiss() }

                OnboardingCard(onDismiss: dismiss)
                    .transition(.scale(scale: 0.92).combined(with: .opacity))
            }
        }
        .animation(.easeInOut(duration: 0.25), value: visible)
    }

    private func dismiss() {
        visible = false
    }
}

private struct OnboardingCard: View {
    @Environment(\.colorScheme) private var scheme
    let onDismiss: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp5) {
            Circle()
                .fill(PrismaSemanticColors.accentSubtle.themed(scheme))
                .frame(width: 56, height: 56)

            Text("Welcome to Prisma")
                .font(PrismaTypography.headlineMd.font)
                .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))

            Text("An interactive catalogue of every component, foundation, and pattern in the design system.")
                .font(PrismaTypography.bodyMd.font)
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))

            VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
                Tip(text: "Tap any entry on the left to open its playground.")
                Tip(text: "Edit knobs to drive the live preview — no rebuild needed.")
                Tip(text: "Long-press the theme pill to follow system; tap to override.")
                Tip(text: "Each component ships with a copy-able usage snippet.")
            }

            HStack {
                PrismaButton("Get started") { onDismiss() }
            }
        }
        .padding(PrismaSpacing.sp8)
        .frame(maxWidth: 480)
        .background(PrismaSemanticColors.surfaceRaised.themed(scheme))
        .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.lg))
        .shadow(radius: 16, y: 8)
        .padding(PrismaSpacing.sp7)
    }
}

private struct Tip: View {
    @Environment(\.colorScheme) private var scheme
    let text: String

    var body: some View {
        HStack(alignment: .top, spacing: PrismaSpacing.sp3) {
            Circle()
                .fill(PrismaSemanticColors.accentDefault.themed(scheme))
                .frame(width: 6, height: 6)
                .padding(.top, 6)
            Text(text)
                .font(PrismaTypography.bodyMd.font)
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                .multilineTextAlignment(.leading)
        }
    }
}
