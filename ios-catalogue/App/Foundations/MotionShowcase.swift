import SwiftUI
import CoreUI

/// Tap a row to play that easing × duration token combination.
/// Demonstrates that motion tokens drive real Animation curves on iOS.
struct MotionShowcase: View {
    @Environment(\.colorScheme) private var scheme

    private static let rows: [(String, (Double, Double, Double, Double), Double)] = [
        ("standard / default", PrismaMotion.Easing.standard, PrismaMotion.Duration.default),
        ("decelerate / default", PrismaMotion.Easing.decelerate, PrismaMotion.Duration.default),
        ("accelerate / fast", PrismaMotion.Easing.accelerate, PrismaMotion.Duration.fast),
        ("emphasized / slow", PrismaMotion.Easing.emphasized, PrismaMotion.Duration.slow),
        ("spring / slower", PrismaMotion.Easing.spring, PrismaMotion.Duration.slower),
        ("linear / default", PrismaMotion.Easing.linear, PrismaMotion.Duration.default)
    ]

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
            Text("Tap a row to play the animation.")
                .font(PrismaTypography.bodySm.font)
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))

            ForEach(Self.rows.indices, id: \.self) { idx in
                let (name, easing, duration) = Self.rows[idx]
                MotionRow(name: name, easing: easing, durationSeconds: duration)
            }
        }
    }
}

private struct MotionRow: View {
    let name: String
    let easing: (Double, Double, Double, Double)
    let durationSeconds: Double

    @Environment(\.colorScheme) private var scheme
    @State private var play = false

    private var dotOffset: CGFloat { play ? 200 - 16 : 0 }

    var body: some View {
        Button(action: trigger) {
            HStack(alignment: .center, spacing: PrismaSpacing.sp4) {
                VStack(alignment: .leading, spacing: 2) {
                    Text(name)
                        .font(.system(size: 13, weight: .medium, design: .monospaced))
                        .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                    Text("\(Int(durationSeconds * 1000))ms")
                        .font(.system(size: 11, design: .monospaced))
                        .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                }
                .frame(width: 180, alignment: .leading)

                ZStack(alignment: .leading) {
                    Capsule()
                        .fill(PrismaSemanticColors.borderSubtle.themed(scheme))
                        .frame(width: 200, height: 4)

                    Circle()
                        .fill(PrismaSemanticColors.accentDefault.themed(scheme))
                        .frame(width: 16, height: 16)
                        .offset(x: dotOffset)
                }

                Spacer()
            }
            .padding(PrismaSpacing.sp4)
            .background(PrismaSemanticColors.surfaceSunken.themed(scheme))
            .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
        }
        .buttonStyle(.plain)
    }

    private func trigger() {
        let curve = Animation.timingCurve(easing.0, easing.1, easing.2, easing.3, duration: durationSeconds)
        withAnimation(curve) { play.toggle() }
        // Auto-reset after duration so the next tap replays cleanly.
        DispatchQueue.main.asyncAfter(deadline: .now() + durationSeconds + 0.1) {
            withAnimation(.linear(duration: 0)) { play = false }
        }
    }
}
