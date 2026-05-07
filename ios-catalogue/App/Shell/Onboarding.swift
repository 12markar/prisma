import SwiftUI
import CoreUI
import Components

private struct OnboardingPage {
    let eyebrow: String
    let title: String
    let titleAccent: String
    let body: String
    let icon: PrismaIcon
    let tags: [String]
}

private let onboardingPages: [OnboardingPage] = [
    OnboardingPage(
        eyebrow: "01 — Welcome",
        title: "Catalogues, ",
        titleAccent: "built quietly.",
        body: "Prisma is a cross-platform design system for native catalogue apps on iOS and Android. Tap any component on the left to open its playground.",
        icon: .layers,
        tags: ["SwiftUI", "Jetpack Compose"]
    ),
    OnboardingPage(
        eyebrow: "02 — Tokens",
        title: "Two layers. ",
        titleAccent: "One source of truth.",
        body: "Eleven-stop primitive ramps, semantic aliases for components. Authored as W3C DTCG JSON, compiled to Tokens.kt and Tokens.swift through Style Dictionary — never copy-pasted.",
        icon: .grid,
        tags: ["W3C DTCG", "Style Dictionary", "Light + Dark"]
    ),
    OnboardingPage(
        eyebrow: "03 — Adaptive",
        title: "List on phone, ",
        titleAccent: "two-pane on tablet.",
        body: "Adaptive layout via Material3 list-detail (Android) and NavigationSplitView (iOS). State preserved across pane swaps; theme follows system or your override.",
        icon: .list,
        tags: ["List-detail", "SceneStorage"]
    ),
    OnboardingPage(
        eyebrow: "04 — Accessible",
        title: "Designed precisely. ",
        titleAccent: "Every state announced.",
        body: "WCAG AA contrast verified at build time. Live regions, headings, progress and group semantics on every interactive component. Keyboard, voice, and screen-reader compatible by default.",
        icon: .eye,
        tags: ["WCAG AA", "TalkBack", "VoiceOver"]
    ),
]

/// First-launch onboarding — horizontal pager with editorial brand copy
/// lifted from the marketing site. Replaces the earlier dialog-style
/// welcome card per UX feedback that it felt like an afterthought.
struct OnboardingOverlay: View {
    @Environment(\.colorScheme) private var scheme
    @Binding var visible: Bool
    @State private var pageIndex: Int = 0

    var body: some View {
        ZStack {
            if visible {
                PrismaSemanticColors.surfaceBase.themed(scheme)
                    .ignoresSafeArea()
                    .transition(.opacity)

                VStack(spacing: 0) {
                    header()
                    TabView(selection: $pageIndex) {
                        ForEach(Array(onboardingPages.enumerated()), id: \.offset) { idx, page in
                            OnboardingPageView(page: page).tag(idx)
                        }
                    }
                    .tabViewStyle(.page(indexDisplayMode: .never))
                    .indexViewStyle(.page(backgroundDisplayMode: .never))
                    .frame(maxWidth: .infinity, maxHeight: .infinity)

                    footer()
                }
                .transition(.opacity)
            }
        }
        .animation(.easeInOut(duration: 0.25), value: visible)
    }

    @ViewBuilder
    private func header() -> some View {
        HStack {
            HStack(spacing: PrismaSpacing.sp2) {
                RoundedRectangle(cornerRadius: 6)
                    .fill(
                        LinearGradient(
                            colors: [
                                Color(red: 0.569, green: 0.451, blue: 1.0),
                                Color(red: 0.463, green: 0.318, blue: 0.961),
                                Color(red: 0.878, green: 0.188, blue: 0.533)
                            ],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        )
                    )
                    .frame(width: 24, height: 24)
                Text("Prisma")
                    .font(PrismaTypography.labelLg.font)
                    .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                    .tracking(-0.4)
            }
            Spacer()
            Button(action: { visible = false }) {
                Text("Skip")
                    .font(PrismaTypography.labelMd.font)
                    .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                    .padding(.horizontal, PrismaSpacing.sp3)
                    .padding(.vertical, PrismaSpacing.sp2)
            }
            .buttonStyle(.plain)
        }
        .padding(.horizontal, PrismaSpacing.sp5)
        .padding(.vertical, PrismaSpacing.sp4)
    }

    @ViewBuilder
    private func footer() -> some View {
        VStack(spacing: PrismaSpacing.sp4) {
            HStack(spacing: 4) {
                ForEach(0..<onboardingPages.count, id: \.self) { idx in
                    let active = idx == pageIndex
                    Capsule()
                        .fill(
                            active
                                ? PrismaSemanticColors.accentDefault.themed(scheme)
                                : PrismaSemanticColors.borderDefault.themed(scheme)
                        )
                        .frame(width: active ? 24 : 8, height: 8)
                        .animation(.easeInOut(duration: 0.2), value: pageIndex)
                        .onTapGesture {
                            withAnimation { pageIndex = idx }
                        }
                }
            }
            PrismaButton(pageIndex == onboardingPages.count - 1 ? "Get started" : "Next") {
                if pageIndex == onboardingPages.count - 1 {
                    visible = false
                } else {
                    withAnimation { pageIndex += 1 }
                }
            }
            .frame(maxWidth: .infinity)
        }
        .padding(.horizontal, PrismaSpacing.sp5)
        .padding(.vertical, PrismaSpacing.sp4)
    }
}

private struct OnboardingPageView: View {
    @Environment(\.colorScheme) private var scheme
    let page: OnboardingPage

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp5) {
            RoundedRectangle(cornerRadius: PrismaRadius.lg)
                .fill(PrismaSemanticColors.accentSubtle.themed(scheme))
                .frame(width: 72, height: 72)
                .overlay(
                    Image(prisma: page.icon)
                        .renderingMode(.template)
                        .resizable()
                        .frame(width: 32, height: 32)
                        .foregroundStyle(PrismaSemanticColors.accentDefault.themed(scheme))
                )

            Text(page.eyebrow)
                .font(.system(.footnote, design: .monospaced).weight(.medium))
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))

            (Text(page.title)
                .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
            +
            Text(page.titleAccent)
                .foregroundStyle(PrismaSemanticColors.accentDefault.themed(scheme))
            )
            .font(PrismaTypography.headlineMd.font)
            .tracking(-0.5)
            .multilineTextAlignment(.leading)

            Text(page.body)
                .font(PrismaTypography.bodyLg.font)
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: PrismaSpacing.sp2) {
                    ForEach(page.tags, id: \.self) { tag in
                        Text(tag)
                            .font(.system(.caption, design: .monospaced))
                            .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                            .padding(.horizontal, PrismaSpacing.sp3)
                            .padding(.vertical, PrismaSpacing.sp2)
                            .background(PrismaSemanticColors.surfaceRaised.themed(scheme))
                            .overlay(
                                Capsule()
                                    .strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
                            )
                            .clipShape(Capsule())
                    }
                }
            }
            Spacer()
        }
        .padding(.horizontal, PrismaSpacing.sp7)
        .padding(.vertical, PrismaSpacing.sp6)
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}
