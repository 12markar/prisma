import SwiftUI
import CoreUI

/// Container that surrounds the live-preview component.
/// Themed surface, subtle border, generous padding so taller components
/// can spread without the surface flicking between heights.
struct PreviewSurface<Content: View>: View {
    @Environment(\.colorScheme) private var scheme
    private let content: () -> Content

    init(@ViewBuilder content: @escaping () -> Content) {
        self.content = content
    }

    var body: some View {
        content()
            .frame(maxWidth: .infinity, minHeight: 200)
            .padding(PrismaSpacing.sp7)
            .background(PrismaSemanticColors.surfaceRaised.themed(scheme))
            .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.lg))
            .overlay(
                RoundedRectangle(cornerRadius: PrismaRadius.lg)
                    .strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
            )
    }
}
