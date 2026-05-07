import SwiftUI
import CoreUI

public enum PrismaButtonVariant: Equatable {
    case primary
    case secondary
    case outlined
    case ghost
    case icon
    case destructive
}

public enum PrismaButtonSize: Equatable {
    case small
    case `default`
    case large
}

/// Prisma's canonical pressable control. Implements the button.md contract:
/// 6 variants × 3 sizes × default / pressed / disabled / loading states.
///
/// For the `.icon` variant, `accessibilityLabel` is required — the visible
/// label is empty so screen readers need an explicit label.
public struct PrismaButton: View {
    private let text: String
    private let onClick: () -> Void
    private let variant: PrismaButtonVariant
    private let size: PrismaButtonSize
    private let enabled: Bool
    private let loading: Bool
    private let leadingIcon: AnyView?
    private let trailingIcon: AnyView?
    private let label: String?

    @Environment(\.colorScheme) private var scheme
    @State private var hapticTrigger: Int = 0

    public init(
        _ text: String,
        variant: PrismaButtonVariant = .primary,
        size: PrismaButtonSize = .default,
        enabled: Bool = true,
        loading: Bool = false,
        leadingIcon: (() -> AnyView)? = nil,
        trailingIcon: (() -> AnyView)? = nil,
        accessibilityLabel: String? = nil,
        action: @escaping () -> Void
    ) {
        precondition(
            variant != .icon || accessibilityLabel != nil,
            "PrismaButton(variant: .icon) requires an accessibilityLabel."
        )
        self.text = text
        self.variant = variant
        self.size = size
        self.enabled = enabled
        self.loading = loading
        self.leadingIcon = leadingIcon?()
        self.trailingIcon = trailingIcon?()
        self.label = accessibilityLabel
        self.onClick = action
    }

    public var body: some View {
        let spec = sizeSpec
        let palette = colorPalette

        Button(action: handleTap) {
            // Centered HStack so wrapping the button in `.frame(maxWidth:
            // .infinity)` actually centers the label rather than parking
            // it on the leading edge of a wide pill. Without an explicit
            // alignment, SwiftUI's HStack hugs its content.
            HStack(alignment: .center, spacing: PrismaSpacing.sp2) {
                if loading {
                    ProgressView()
                        .controlSize(.small)
                        .tint(palette.label.themed(scheme))
                        .frame(width: spec.iconSize, height: spec.iconSize)
                } else if let leadingIcon {
                    leadingIcon
                        .frame(width: spec.iconSize, height: spec.iconSize)
                }

                if variant != .icon {
                    Text(text)
                        .font(spec.textStyle.font)
                }

                if let trailingIcon, !loading {
                    trailingIcon
                        .frame(width: spec.iconSize, height: spec.iconSize)
                }
            }
            .foregroundStyle(palette.label.themed(scheme))
            .padding(.horizontal, spec.horizontalPadding)
            .frame(minHeight: max(spec.height, 44))      // min touch target
        }
        .buttonStyle(
            PrismaButtonRenderStyle(
                background: palette.background.themed(scheme),
                pressedBackground: palette.pressedBackground.themed(scheme),
                border: palette.border?.themed(scheme),
                cornerRadius: PrismaRadius.md
            )
        )
        .disabled(!enabled || loading)
        .sensoryFeedback(.impact(weight: .light), trigger: hapticTrigger)
        .accessibilityLabel(label ?? text)
        .accessibilityAddTraits(.isButton)
    }

    private func handleTap() {
        hapticTrigger &+= 1
        onClick()
    }

    // MARK: - Token mapping

    private var colorPalette: ButtonColors {
        guard enabled else {
            return ButtonColors(
                background: PrismaSemanticColors.surfaceSunken,
                pressedBackground: PrismaSemanticColors.surfaceSunken,
                label: PrismaSemanticColors.textDisabled,
                border: (variant == .outlined || variant == .secondary)
                    ? PrismaSemanticColors.borderSubtle : nil
            )
        }
        switch variant {
        case .primary:
            return ButtonColors(
                background: PrismaSemanticColors.accentDefault,
                pressedBackground: PrismaSemanticColors.accentPressed,
                label: PrismaSemanticColors.textOnAccent,
                border: nil
            )
        case .destructive:
            return ButtonColors(
                background: PrismaSemanticColors.statusDangerDefault,
                pressedBackground: PrismaSemanticColors.statusDangerDefault,
                label: PrismaSemanticColors.statusDangerOnStatus,
                border: nil
            )
        case .secondary:
            return ButtonColors(
                background: PrismaSemanticColors.surfaceRaised,
                pressedBackground: PrismaSemanticColors.surfaceSunken,
                label: PrismaSemanticColors.textPrimary,
                border: PrismaSemanticColors.borderDefault
            )
        case .outlined:
            return ButtonColors(
                background: Self.transparentSemantic,
                pressedBackground: PrismaSemanticColors.surfaceSunken,
                label: PrismaSemanticColors.textPrimary,
                border: PrismaSemanticColors.borderDefault
            )
        case .ghost, .icon:
            return ButtonColors(
                background: Self.transparentSemantic,
                pressedBackground: PrismaSemanticColors.surfaceSunken,
                label: PrismaSemanticColors.textPrimary,
                border: nil
            )
        }
    }

    private var sizeSpec: ButtonSizeSpec {
        switch size {
        case .small:    return .init(height: 32, horizontalPadding: PrismaSpacing.sp3, iconSize: 16, textStyle: PrismaTypography.labelMd)
        case .default:  return .init(height: 40, horizontalPadding: PrismaSpacing.sp4, iconSize: 18, textStyle: PrismaTypography.labelLg)
        case .large:    return .init(height: 48, horizontalPadding: PrismaSpacing.sp5, iconSize: 18, textStyle: PrismaTypography.labelLg)
        }
    }

    private static let transparentSemantic = PrismaSemanticColor(light: .clear, dark: .clear)
}

private struct ButtonColors {
    let background: PrismaSemanticColor
    let pressedBackground: PrismaSemanticColor
    let label: PrismaSemanticColor
    let border: PrismaSemanticColor?
}

private struct ButtonSizeSpec {
    let height: CGFloat
    let horizontalPadding: CGFloat
    let iconSize: CGFloat
    let textStyle: PrismaTypography.Style
}

private struct PrismaButtonRenderStyle: ButtonStyle {
    let background: Color
    let pressedBackground: Color
    let border: Color?
    let cornerRadius: CGFloat

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .background(configuration.isPressed ? pressedBackground : background)
            .overlay {
                if let border {
                    RoundedRectangle(cornerRadius: cornerRadius)
                        .strokeBorder(border, lineWidth: 1)
                }
            }
            .clipShape(RoundedRectangle(cornerRadius: cornerRadius))
            .animation(.easeInOut(duration: 0.12), value: configuration.isPressed)
    }
}
