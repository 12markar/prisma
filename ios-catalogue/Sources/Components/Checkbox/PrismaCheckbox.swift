import SwiftUI
import CoreUI

public enum PrismaCheckboxState: Equatable {
    case unchecked
    case checked
    case indeterminate
}

/// Tri-state checkbox per checkbox.md spec. Glyph is a check (Image(prisma: .check))
/// when checked, a horizontal bar when indeterminate, absent otherwise.
public struct PrismaCheckbox: View {
    @Binding private var state: PrismaCheckboxState
    private let label: String?
    private let helperText: String?
    private let enabled: Bool
    private let isError: Bool

    @Environment(\.colorScheme) private var scheme

    public init(
        state: Binding<PrismaCheckboxState>,
        label: String? = nil,
        helperText: String? = nil,
        enabled: Bool = true,
        isError: Bool = false
    ) {
        self._state = state
        self.label = label
        self.helperText = helperText
        self.enabled = enabled
        self.isError = isError
    }

    /// Convenience for binary-only checkboxes — bridges Bool to the tri-state.
    public init(
        checked: Binding<Bool>,
        label: String? = nil,
        helperText: String? = nil,
        enabled: Bool = true,
        isError: Bool = false
    ) {
        self._state = Binding(
            get: { checked.wrappedValue ? .checked : .unchecked },
            set: { newValue in checked.wrappedValue = (newValue == .checked) }
        )
        self.label = label
        self.helperText = helperText
        self.enabled = enabled
        self.isError = isError
    }

    public var body: some View {
        Button(action: toggle) {
            HStack(alignment: .top, spacing: PrismaSpacing.sp3) {
                box
                    .padding(.top, 2)
                if let label {
                    VStack(alignment: .leading, spacing: 2) {
                        Text(label)
                            .font(PrismaTypography.bodyMd.font)
                            .foregroundStyle(
                                enabled
                                    ? PrismaSemanticColors.textPrimary.themed(scheme)
                                    : PrismaSemanticColors.textDisabled.themed(scheme)
                            )
                        if let helperText {
                            Text(helperText)
                                .font(PrismaTypography.bodySm.font)
                                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                        }
                    }
                }
                Spacer(minLength: 0)
            }
            .padding(.vertical, PrismaSpacing.sp1)
            .frame(minHeight: 44, alignment: .topLeading)
        }
        .buttonStyle(.plain)
        .disabled(!enabled)
        .accessibilityLabel(label ?? "")
        .accessibilityValue(accessibilityValue)
        .accessibilityAddTraits(.isButton)
    }

    private var box: some View {
        ZStack {
            RoundedRectangle(cornerRadius: PrismaRadius.sm)
                .fill(palette.fill.themed(scheme))
            if let border = palette.border {
                RoundedRectangle(cornerRadius: PrismaRadius.sm)
                    .strokeBorder(border.themed(scheme), lineWidth: 2)
            }
            switch state {
            case .checked:
                Image(prisma: .check)
                    .renderingMode(.template)
                    .resizable()
                    .frame(width: 14, height: 14)
                    .foregroundStyle(palette.glyph.themed(scheme))
            case .indeterminate:
                RoundedRectangle(cornerRadius: 1)
                    .fill(palette.glyph.themed(scheme))
                    .frame(width: 10, height: 2)
            case .unchecked:
                EmptyView()
            }
        }
        .frame(width: 20, height: 20)
    }

    private func toggle() {
        switch state {
        case .checked, .indeterminate: state = .unchecked
        case .unchecked: state = .checked
        }
    }

    private var accessibilityValue: String {
        switch state {
        case .checked: return "checked"
        case .indeterminate: return "mixed"
        case .unchecked: return "unchecked"
        }
    }

    // MARK: - Token mapping

    private struct Palette {
        let fill: PrismaSemanticColor
        let border: PrismaSemanticColor?
        let glyph: PrismaSemanticColor
    }

    private static let transparentSemantic = PrismaSemanticColor(light: .clear, dark: .clear)

    private var palette: Palette {
        if !enabled {
            switch state {
            case .unchecked:
                return .init(fill: PrismaSemanticColors.surfaceSunken, border: PrismaSemanticColors.borderSubtle, glyph: PrismaSemanticColors.textDisabled)
            case .checked, .indeterminate:
                return .init(fill: PrismaSemanticColors.textDisabled, border: nil, glyph: PrismaSemanticColors.surfaceBase)
            }
        }
        if isError && state == .unchecked {
            return .init(
                fill: Self.transparentSemantic,
                border: PrismaSemanticColors.statusDangerDefault,
                glyph: PrismaSemanticColors.statusDangerDefault
            )
        }
        switch state {
        case .unchecked:
            return .init(fill: Self.transparentSemantic, border: PrismaSemanticColors.borderStrong, glyph: PrismaSemanticColors.textPrimary)
        case .checked, .indeterminate:
            return .init(fill: PrismaSemanticColors.accentDefault, border: nil, glyph: PrismaSemanticColors.textOnAccent)
        }
    }
}
