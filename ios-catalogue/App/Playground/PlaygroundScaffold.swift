import SwiftUI
import CoreUI
import Components

/// Storybook-style scaffold for an interactive component showcase.
///
/// Pre-existing showcases call this with a `knobs` block, an optional
/// `states` block (rendered as a flow gallery), a `code` generator, and
/// an optional `a11y` block. Knobs and a11y are now both surfaced behind
/// bottom sheets — opened by Edit / A11y action pills directly under the
/// preview — so the live preview never scrolls off screen behind a long
/// edit panel.
///
/// The richer ButtonShowcase uses [PlaygroundScreen] (with a structured
/// [A11yReport] and a horizontal states pager) directly, bypassing this
/// scaffold. Both APIs coexist during the per-component rollout.
struct PlaygroundScaffold<Preview: View, Knobs: View, States: View, A11y: View>: View {
    @Environment(\.colorScheme) private var scheme
    private let preview: () -> Preview
    private let knobs: () -> Knobs
    private let states: () -> States
    private let code: (() -> String)?
    private let a11y: () -> A11y

    init(
        @ViewBuilder preview: @escaping () -> Preview,
        @ViewBuilder knobs: @escaping () -> Knobs = { EmptyView() },
        @ViewBuilder states: @escaping () -> States = { EmptyView() },
        code: (() -> String)? = nil,
        @ViewBuilder a11y: @escaping () -> A11y = { EmptyView() }
    ) {
        self.preview = preview
        self.knobs = knobs
        self.states = states
        self.code = code
        self.a11y = a11y
    }

    @State private var knobsOpen = false
    @State private var a11yOpen = false

    private var hasKnobs: Bool { Knobs.self != EmptyView.self }
    private var hasA11y: Bool { A11y.self != EmptyView.self }

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp5) {
            PreviewSurface { preview() }
                .frame(minHeight: 200)

            if hasKnobs || hasA11y {
                HStack(spacing: PrismaSpacing.sp3) {
                    if hasKnobs {
                        actionPill(icon: .edit, label: "Edit") { knobsOpen = true }
                    }
                    if hasA11y {
                        actionPill(icon: .eye, label: "A11y") { a11yOpen = true }
                    }
                }
            }

            if States.self != EmptyView.self {
                section(label: "States") {
                    StatesGallery { states() }
                }
            }

            if let code {
                section(label: "Usage") {
                    CodeBlock(code: code())
                }
            }
        }
        .sheet(isPresented: $knobsOpen) {
            ScrollView {
                VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
                    Text("Edit")
                        .font(PrismaTypography.headlineSm.font)
                        .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                    knobs()
                    PrismaButton("Done") { knobsOpen = false }
                        .frame(maxWidth: .infinity)
                }
                .padding(.horizontal, PrismaSpacing.sp5)
                .padding(.vertical, PrismaSpacing.sp4)
            }
            .presentationDetents([.medium, .large])
            .presentationDragIndicator(.visible)
        }
        .sheet(isPresented: $a11yOpen) {
            ScrollView {
                VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
                    a11y()
                }
                .padding(.horizontal, PrismaSpacing.sp5)
                .padding(.vertical, PrismaSpacing.sp4)
            }
            .presentationDetents([.large])
            .presentationDragIndicator(.visible)
        }
    }

    @ViewBuilder
    private func section<Body: View>(label: String, @ViewBuilder content: () -> Body) -> some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
            Text(label.uppercased())
                .font(PrismaTypography.labelSm.font)
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
            content()
        }
    }

    @ViewBuilder
    private func actionPill(icon: PrismaIcon, label: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            HStack(spacing: PrismaSpacing.sp2) {
                Image(prisma: icon)
                    .renderingMode(.template)
                    .resizable()
                    .frame(width: 16, height: 16)
                    .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                Text(label)
                    .font(PrismaTypography.labelMd.font)
                    .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
            }
            .frame(maxWidth: .infinity)
            .padding(.horizontal, PrismaSpacing.sp4)
            .padding(.vertical, PrismaSpacing.sp3)
            .background(PrismaSemanticColors.surfaceRaised.themed(scheme))
            .overlay(
                RoundedRectangle(cornerRadius: PrismaRadius.md)
                    .strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
            )
            .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
        }
        .buttonStyle(.plain)
    }
}
