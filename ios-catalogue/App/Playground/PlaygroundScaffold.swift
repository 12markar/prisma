import SwiftUI
import CoreUI
import Components

/// Storybook-style scaffold shared by every legacy showcase. Three pills
/// sit directly under the preview — Edit / A11y / Code — each opening its
/// own bottom sheet. State galleries (legacy) and structured A11yReports
/// (new) are both supported so showcases can adopt the new pattern
/// progressively.
///
/// `pagerStates` (preferred) renders a horizontal pager of canonical
/// states. `a11yReport` (preferred) opens A11ySheetContent on the A11y
/// pill. The legacy `states` and `a11y` slots remain for incomplete
/// migrations.
struct PlaygroundScaffold<Preview: View, Knobs: View, States: View, A11y: View>: View {
    @Environment(\.colorScheme) private var scheme
    private let preview: () -> Preview
    private let knobs: () -> Knobs
    private let states: () -> States
    private let code: (() -> String)?
    private let a11y: () -> A11y
    private let pagerStates: [AnyPlaygroundState]?
    private let a11yReport: A11yReport?

    init(
        @ViewBuilder preview: @escaping () -> Preview,
        @ViewBuilder knobs: @escaping () -> Knobs = { EmptyView() },
        @ViewBuilder states: @escaping () -> States = { EmptyView() },
        code: (() -> String)? = nil,
        @ViewBuilder a11y: @escaping () -> A11y = { EmptyView() },
        pagerStates: [AnyPlaygroundState]? = nil,
        a11yReport: A11yReport? = nil
    ) {
        self.preview = preview
        self.knobs = knobs
        self.states = states
        self.code = code
        self.a11y = a11y
        self.pagerStates = pagerStates
        self.a11yReport = a11yReport
    }

    @State private var knobsOpen = false
    @State private var a11yOpen = false
    @State private var codeOpen = false

    private var hasKnobs: Bool { Knobs.self != EmptyView.self }
    private var hasA11y: Bool { a11yReport != nil || A11y.self != EmptyView.self }
    private var hasCode: Bool { code != nil }

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp5) {
            PreviewSurface { preview() }
                .frame(minHeight: 200)

            if hasKnobs || hasA11y || hasCode {
                HStack(spacing: PrismaSpacing.sp3) {
                    if hasKnobs {
                        actionPill(icon: .edit, label: "Edit") { knobsOpen = true }
                    }
                    if hasA11y {
                        actionPill(icon: .eye, label: "A11y") { a11yOpen = true }
                    }
                    if hasCode {
                        actionPill(icon: .doc, label: "Code") { codeOpen = true }
                    }
                }
            }

            // Pager wins over legacy gallery — the swipeable layout is the
            // new default per the playground rollout brief.
            if let pagerStates, !pagerStates.isEmpty {
                section(label: "States — swipe to compare") {
                    StatesPager(states: pagerStates)
                }
            } else if States.self != EmptyView.self {
                section(label: "States") {
                    StatesGallery { states() }
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
            // A11ySheetContent already wraps its body in a ScrollView with
            // its own padding, so render it directly. Nesting it inside
            // another ScrollView caused odd horizontal padding on the
            // QuickFacts row and section bodies. Only the legacy a11y()
            // ViewBuilder needs the outer scroll + padding.
            Group {
                if let a11yReport {
                    A11ySheetContent(report: a11yReport)
                } else {
                    ScrollView {
                        VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
                            a11y()
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.horizontal, PrismaSpacing.sp5)
                        .padding(.vertical, PrismaSpacing.sp4)
                    }
                }
            }
            .presentationDetents([.large])
            .presentationDragIndicator(.visible)
        }
        .sheet(isPresented: $codeOpen) {
            ScrollView {
                VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
                    Text("Usage")
                        .font(PrismaTypography.headlineSm.font)
                        .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                    Text("Drop this in to render the component as it appears above. Only knobs that differ from defaults are shown.")
                        .font(PrismaTypography.bodySm.font)
                        .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                    if let code {
                        CodeBlock(code: code())
                    }
                }
                .padding(.horizontal, PrismaSpacing.sp5)
                .padding(.vertical, PrismaSpacing.sp4)
            }
            .presentationDetents([.medium, .large])
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
