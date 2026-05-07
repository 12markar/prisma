import SwiftUI
import CoreUI

/// Container the live-preview component renders inside.
///
/// Stripped to just top + bottom hairline dividers (no card chrome, no
/// rounded box) so wide components — Wizard, Pagination, Breadcrumb,
/// TimePicker — can use the full available width. Content is centered
/// horizontally and vertically inside the slot.
struct PreviewSurface<Content: View>: View {
    @Environment(\.colorScheme) private var scheme
    private let content: () -> Content

    init(@ViewBuilder content: @escaping () -> Content) {
        self.content = content
    }

    var body: some View {
        VStack(spacing: 0) {
            Rectangle()
                .fill(PrismaSemanticColors.borderSubtle.themed(scheme))
                .frame(height: 1)

            content()
                .frame(maxWidth: .infinity, alignment: .center)
                .frame(minHeight: 200)
                .padding(.vertical, PrismaSpacing.sp7)

            Rectangle()
                .fill(PrismaSemanticColors.borderSubtle.themed(scheme))
                .frame(height: 1)
        }
        .frame(maxWidth: .infinity)
    }
}
