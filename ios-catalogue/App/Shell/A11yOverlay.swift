import SwiftUI
import CoreUI

/// Wraps content with a translucent 44-pt grid when the overlay is on. The
/// grid sits in front of the content (subtle, dashed) so it reads over both
/// light and dark surfaces but doesn't dominate the layout.
struct A11yOverlayLayer<Content: View>: View {
    let enabled: Bool
    @ViewBuilder let content: () -> Content

    var body: some View {
        if enabled {
            content().overlay(GridOverlay())
        } else {
            content()
        }
    }
}

private struct GridOverlay: View {
    var body: some View {
        GeometryReader { geo in
            let step: CGFloat = 44   // iOS HIG min touch target
            let color = Color(red: 0.878, green: 0.188, blue: 0.533).opacity(0.35)
            Path { path in
                var x: CGFloat = 0
                while x < geo.size.width {
                    path.move(to: CGPoint(x: x, y: 0))
                    path.addLine(to: CGPoint(x: x, y: geo.size.height))
                    x += step
                }
                var y: CGFloat = 0
                while y < geo.size.height {
                    path.move(to: CGPoint(x: 0, y: y))
                    path.addLine(to: CGPoint(x: geo.size.width, y: y))
                    y += step
                }
            }
            .stroke(color, style: StrokeStyle(lineWidth: 1, dash: [4, 6]))
        }
        .allowsHitTesting(false)
    }
}
