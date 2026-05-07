import SwiftUI
import CoreUI

/// Grid of frozen, labelled component states. Sits below the knobs panel so
/// users can compare canonical states at a glance.
struct StatesGallery<Content: View>: View {
    private let content: () -> Content

    init(@ViewBuilder content: @escaping () -> Content) {
        self.content = content
    }

    var body: some View {
        FlowLayout(spacing: PrismaSpacing.sp3) {
            content()
        }
    }
}

struct StateCell<Content: View>: View {
    @Environment(\.colorScheme) private var scheme
    let label: String
    var minWidth: CGFloat = 200
    private let content: () -> Content

    init(_ label: String, minWidth: CGFloat = 200, @ViewBuilder content: @escaping () -> Content) {
        self.label = label
        self.minWidth = minWidth
        self.content = content
    }

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
            Text(label.uppercased())
                .font(PrismaTypography.labelSm.font)
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
            content()
                .frame(minHeight: 56, alignment: .leading)
        }
        .padding(PrismaSpacing.sp4)
        .frame(minWidth: minWidth, alignment: .leading)
        .background(PrismaSemanticColors.surfaceRaised.themed(scheme))
        .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
        .overlay(
            RoundedRectangle(cornerRadius: PrismaRadius.md)
                .strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
        )
    }
}
