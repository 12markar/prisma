import SwiftUI
import CoreUI

/// Storybook-style scaffold for an interactive component showcase.
///
///  ┌─────────────────────────┐
///  │  Live preview surface   │
///  ├─────────────────────────┤
///  │  CONTROLS               │
///  │   knob rows…            │
///  ├─────────────────────────┤
///  │  STATES                 │
///  │   state-cell grid…      │
///  └─────────────────────────┘
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

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp7) {
            PreviewSurface { preview() }

            if Knobs.self != EmptyView.self {
                section(label: "Controls") {
                    VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
                        knobs()
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

            if A11y.self != EmptyView.self {
                section(label: "Accessibility") {
                    a11y()
                }
            }
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
}
