import SwiftUI
import CoreUI

public enum PrismaTextFieldVariant: Equatable {
    case outlined
    case filled
}

public enum PrismaTextFieldSize: Equatable {
    case small
    case medium
    case large
}

/// Prisma's canonical text input. Mirrors the Android PrismaTextField API:
///
///   - outlined / filled variants
///   - small / medium / large sizes (heights 36 / 44 / 52)
///   - states: default, focused, error, disabled, readOnly
///   - slots: leadingIcon, trailingIcon, label (above), helper / error (below)
///   - secure entry; optional counter / maxCount
public struct PrismaTextField: View {
    @Binding private var text: String
    private let label: String?
    private let placeholder: String?
    private let helperText: String?
    private let errorText: String?
    private let enabled: Bool
    private let readOnly: Bool
    private let variant: PrismaTextFieldVariant
    private let size: PrismaTextFieldSize
    private let leadingIcon: AnyView?
    private let trailingIcon: AnyView?
    private let secureTextEntry: Bool
    private let counter: Int?
    private let maxCount: Int?

    @Environment(\.colorScheme) private var scheme
    @FocusState private var focused: Bool

    public init(
        text: Binding<String>,
        label: String? = nil,
        placeholder: String? = nil,
        helperText: String? = nil,
        errorText: String? = nil,
        enabled: Bool = true,
        readOnly: Bool = false,
        variant: PrismaTextFieldVariant = .outlined,
        size: PrismaTextFieldSize = .medium,
        leadingIcon: (() -> AnyView)? = nil,
        trailingIcon: (() -> AnyView)? = nil,
        secureTextEntry: Bool = false,
        counter: Int? = nil,
        maxCount: Int? = nil
    ) {
        self._text = text
        self.label = label
        self.placeholder = placeholder
        self.helperText = helperText
        self.errorText = errorText
        self.enabled = enabled
        self.readOnly = readOnly
        self.variant = variant
        self.size = size
        self.leadingIcon = leadingIcon?()
        self.trailingIcon = trailingIcon?()
        self.secureTextEntry = secureTextEntry
        self.counter = counter
        self.maxCount = maxCount
    }

    public var body: some View {
        let isError = errorText != nil
        let palette = paletteFor(isError: isError, isFocused: focused)
        let spec = sizeSpec

        VStack(alignment: .leading, spacing: PrismaSpacing.sp1) {
            if let label {
                Text(label)
                    .font(PrismaTypography.labelMd.font)
                    .foregroundStyle(palette.label.themed(scheme))
            }

            HStack(spacing: PrismaSpacing.sp2) {
                if let leadingIcon {
                    leadingIcon.frame(width: spec.iconSize, height: spec.iconSize)
                }

                Group {
                    if secureTextEntry {
                        SecureField(placeholder ?? "", text: $text)
                    } else {
                        TextField(placeholder ?? "", text: $text)
                    }
                }
                .focused($focused)
                .disabled(!enabled || readOnly)
                .font(spec.textStyle.font)
                .foregroundStyle(textColor)
                .tint(PrismaSemanticColors.accentDefault.themed(scheme))

                if let trailingIcon {
                    trailingIcon.frame(width: spec.iconSize, height: spec.iconSize)
                }
            }
            .padding(.horizontal, spec.horizontalPadding)
            .frame(height: max(spec.height, 44))
            .background(palette.fill.themed(scheme))
            .overlay(
                RoundedRectangle(cornerRadius: PrismaRadius.md)
                    .strokeBorder(
                        palette.border.themed(scheme),
                        lineWidth: (isError || focused) ? 2 : 1
                    )
            )
            .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
            .animation(
                .easeInOut(duration: PrismaMotion.Duration.fast),
                value: focused
            )

            if helperText != nil || errorText != nil || counter != nil {
                HStack {
                    if let displayed = errorText ?? helperText {
                        Text(displayed)
                            .font(PrismaTypography.bodySm.font)
                            .foregroundStyle(
                                isError
                                    ? PrismaSemanticColors.statusDangerDefault.themed(scheme)
                                    : PrismaSemanticColors.textTertiary.themed(scheme)
                            )
                    }
                    Spacer()
                    if let counter {
                        let countText = maxCount.map { "\(counter)/\($0)" } ?? "\(counter)"
                        Text(countText)
                            .font(PrismaTypography.bodySm.font)
                            .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                    }
                }
            }
        }
    }

    // MARK: - Token mapping

    private struct Palette {
        let border: PrismaSemanticColor
        let fill: PrismaSemanticColor
        let label: PrismaSemanticColor
    }

    private static let transparentSemantic = PrismaSemanticColor(light: .clear, dark: .clear)

    private func paletteFor(isError: Bool, isFocused: Bool) -> Palette {
        let fill: PrismaSemanticColor =
            (variant == .filled || !enabled || readOnly) ? PrismaSemanticColors.surfaceSunken : Self.transparentSemantic

        let border: PrismaSemanticColor
        if !enabled || readOnly {
            border = PrismaSemanticColors.borderSubtle
        } else if isError {
            border = PrismaSemanticColors.statusDangerDefault
        } else if isFocused {
            border = PrismaSemanticColors.borderFocus
        } else {
            border = PrismaSemanticColors.borderDefault
        }

        let label: PrismaSemanticColor
        if !enabled {
            label = PrismaSemanticColors.textDisabled
        } else if isError {
            label = PrismaSemanticColors.statusDangerDefault
        } else if isFocused {
            label = PrismaSemanticColors.textPrimary
        } else {
            label = PrismaSemanticColors.textSecondary
        }

        return Palette(border: border, fill: fill, label: label)
    }

    private var textColor: Color {
        if !enabled {
            return PrismaSemanticColors.textDisabled.themed(scheme)
        }
        return PrismaSemanticColors.textPrimary.themed(scheme)
    }

    private struct SizeSpec {
        let height: CGFloat
        let horizontalPadding: CGFloat
        let iconSize: CGFloat
        let textStyle: PrismaTypography.Style
    }

    private var sizeSpec: SizeSpec {
        switch size {
        case .small:  return .init(height: 36, horizontalPadding: PrismaSpacing.sp3, iconSize: 16, textStyle: PrismaTypography.bodySm)
        case .medium: return .init(height: 44, horizontalPadding: PrismaSpacing.sp4, iconSize: 18, textStyle: PrismaTypography.bodyMd)
        case .large:  return .init(height: 52, horizontalPadding: PrismaSpacing.sp4, iconSize: 20, textStyle: PrismaTypography.bodyLg)
        }
    }
}
