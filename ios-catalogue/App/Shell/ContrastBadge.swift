import SwiftUI
import CoreUI
#if canImport(UIKit)
import UIKit
#endif

/// Live WCAG contrast indicator. Computes the actual ratio between the
/// active theme's primary text and base surface, then surfaces it as a
/// coloured chip — green at ≥ 4.5:1, amber 3-4.5:1, red below.
///
/// Recomputes when the theme flips so the user can sanity-check overrides
/// (e.g. an in-progress dark theme) at a glance.
struct ContrastBadge: View {
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        let textColor = PrismaSemanticColors.textPrimary.themed(scheme)
        let surfaceColor = PrismaSemanticColors.surfaceBase.themed(scheme)
        let ratio = contrastRatio(fg: textColor, bg: surfaceColor)
        let (dotColor, label): (Color, String) = {
            switch ratio {
            case 7.0...:        return (PrismaSemanticColors.statusSuccessDefault.themed(scheme), "AAA")
            case 4.5..<7.0:     return (PrismaSemanticColors.statusSuccessDefault.themed(scheme), "AA")
            case 3.0..<4.5:     return (PrismaSemanticColors.statusWarningDefault.themed(scheme), "AA-")
            default:            return (PrismaSemanticColors.statusDangerDefault.themed(scheme), "FAIL")
            }
        }()
        let ratioStr = String(format: "%.1f:1", ratio)

        HStack(spacing: PrismaSpacing.sp2) {
            Circle().fill(dotColor).frame(width: 8, height: 8)
            Text(label)
                .font(PrismaTypography.labelSm.font)
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
            Text(ratioStr)
                .font(PrismaTypography.labelSm.font)
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
        }
        .padding(.horizontal, PrismaSpacing.sp3)
        .padding(.vertical, 6)
        .background(PrismaSemanticColors.surfaceRaised.themed(scheme))
        .overlay(
            Capsule().strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
        )
        .clipShape(Capsule())
        .accessibilityElement(children: .combine)
        .accessibilityLabel("WCAG \(label), contrast \(ratioStr)")
    }
}

private func contrastRatio(fg: Color, bg: Color) -> Double {
    let l1 = relativeLuminance(fg)
    let l2 = relativeLuminance(bg)
    let lighter = max(l1, l2)
    let darker = min(l1, l2)
    return (lighter + 0.05) / (darker + 0.05)
}

private func relativeLuminance(_ color: Color) -> Double {
    #if canImport(UIKit)
    var r: CGFloat = 0, g: CGFloat = 0, b: CGFloat = 0, a: CGFloat = 0
    UIColor(color).getRed(&r, green: &g, blue: &b, alpha: &a)
    func channel(_ v: CGFloat) -> Double {
        let d = Double(v)
        return d <= 0.03928 ? d / 12.92 : pow((d + 0.055) / 1.055, 2.4)
    }
    return 0.2126 * channel(r) + 0.7152 * channel(g) + 0.0722 * channel(b)
    #else
    return 0
    #endif
}
