import SwiftUI
import CoreUI
import Components

/// Reusable playground screen — same shell ButtonShowcase pioneered, now
/// extracted for every other component. Handles preview surface, Edit /
/// A11y action pills, knobs sheet, a11y sheet, states pager, and inline
/// code snippet so individual showcases stay focused on declaring their
/// data (state list, knob rows, snippet).
struct PlaygroundScreen<Preview: View, Knobs: View, Footer: View>: View {
    @Environment(\.colorScheme) private var scheme
    let preview: () -> Preview
    let knobs: () -> Knobs
    let states: [AnyPlaygroundState]
    let code: () -> String
    let a11y: A11yReport
    var previewMinHeight: CGFloat = 240
    let footer: () -> Footer

    init(
        @ViewBuilder preview: @escaping () -> Preview,
        @ViewBuilder knobs: @escaping () -> Knobs,
        states: [AnyPlaygroundState] = [],
        code: @escaping () -> String,
        a11y: A11yReport,
        previewMinHeight: CGFloat = 240,
        @ViewBuilder footer: @escaping () -> Footer = { EmptyView() }
    ) {
        self.preview = preview
        self.knobs = knobs
        self.states = states
        self.code = code
        self.a11y = a11y
        self.previewMinHeight = previewMinHeight
        self.footer = footer
    }

    @State private var knobsOpen = false
    @State private var a11yOpen = false
    @State private var codeOpen = false

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp5) {
            PreviewSurface { preview() }
                .frame(minHeight: previewMinHeight)

            // Three action pills — Edit, A11y, Code — each opens its own
            // bottom sheet so the playground stays focused on the live
            // preview.
            HStack(spacing: PrismaSpacing.sp3) {
                actionPill(icon: .edit, label: "Edit") { knobsOpen = true }
                actionPill(icon: .eye, label: "A11y") { a11yOpen = true }
                actionPill(icon: .doc, label: "Code") { codeOpen = true }
            }

            if !states.isEmpty {
                sectionLabel("States — swipe to compare")
                StatesPager(states: states)
            }

            footer()
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
            A11ySheetContent(report: a11y)
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
                    CodeBlock(code: code())
                }
                .padding(.horizontal, PrismaSpacing.sp5)
                .padding(.vertical, PrismaSpacing.sp4)
            }
            .presentationDetents([.medium, .large])
            .presentationDragIndicator(.visible)
        }
    }

    @ViewBuilder
    private func sectionLabel(_ text: String) -> some View {
        Text(text.uppercased())
            .font(PrismaTypography.labelSm.font)
            .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
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
