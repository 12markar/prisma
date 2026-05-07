import SwiftUI
import CoreUI

public struct PlaygroundState<Content: View> {
    let label: String
    let content: () -> Content

    public init(_ label: String, @ViewBuilder content: @escaping () -> Content) {
        self.label = label
        self.content = content
    }
}

/// Horizontal pager of canonical component states. Page height is fixed —
/// crucial UX detail per feedback that variable-height pages caused
/// jarring layout shifts as the user swiped between states.
struct StatesPager: View {
    @Environment(\.colorScheme) private var scheme
    let states: [AnyPlaygroundState]
    var pageHeight: CGFloat = 220
    @State private var index: Int = 0

    var body: some View {
        VStack(spacing: PrismaSpacing.sp3) {
            TabView(selection: $index) {
                ForEach(Array(states.enumerated()), id: \.offset) { idx, state in
                    VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
                        Text(state.label.uppercased())
                            .font(PrismaTypography.labelSm.font)
                            .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                        state.content
                            .frame(maxWidth: .infinity, alignment: .leading)
                        Spacer(minLength: 0)
                    }
                    .padding(PrismaSpacing.sp5)
                    .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
                    .background(PrismaSemanticColors.surfaceRaised.themed(scheme))
                    .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
                    .overlay(
                        RoundedRectangle(cornerRadius: PrismaRadius.md)
                            .strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
                    )
                    .padding(.horizontal, PrismaSpacing.sp4)
                    .tag(idx)
                }
            }
            .tabViewStyle(.page(indexDisplayMode: .never))
            .frame(height: pageHeight)

            HStack(spacing: 6) {
                ForEach(0..<states.count, id: \.self) { i in
                    let active = i == index
                    Capsule()
                        .fill(
                            active
                                ? PrismaSemanticColors.accentDefault.themed(scheme)
                                : PrismaSemanticColors.borderDefault.themed(scheme)
                        )
                        .frame(width: active ? 18 : 6, height: 6)
                        .animation(.easeInOut(duration: 0.2), value: index)
                }
            }
        }
    }
}

/// Type-erased playground state so a single-typed `[AnyPlaygroundState]`
/// array can mix differently-typed cell content.
public struct AnyPlaygroundState {
    let label: String
    let content: AnyView

    public init<Content: View>(_ label: String, @ViewBuilder content: () -> Content) {
        self.label = label
        self.content = AnyView(content())
    }
}
