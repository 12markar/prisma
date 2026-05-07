import SwiftUI
import CoreUI
import Components

private struct KnobLabel: View {
    @Environment(\.colorScheme) private var scheme
    let text: String
    var hint: String? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(text)
                .font(PrismaTypography.labelMd.font)
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
            if let hint {
                Text(hint)
                    .font(PrismaTypography.bodySm.font)
                    .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
            }
        }
    }
}

/// Editable text knob, bound to a `PrismaTextField`.
struct StringKnobRow: View {
    let label: String
    @Binding var value: String
    var placeholder: String = ""
    var helper: String? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp2) {
            KnobLabel(text: label, hint: helper)
            PrismaTextField(text: $value, placeholder: placeholder.isEmpty ? nil : placeholder)
        }
    }
}

/// Boolean knob — label on the left, toggle on the right.
struct BoolKnobRow: View {
    @Environment(\.colorScheme) private var scheme
    let label: String
    @Binding var value: Bool
    var helper: String? = nil

    var body: some View {
        HStack(spacing: PrismaSpacing.sp4) {
            KnobLabel(text: label, hint: helper)
            Spacer()
            Toggle("", isOn: $value)
                .labelsHidden()
                .tint(PrismaSemanticColors.accentDefault.themed(scheme))
        }
    }
}

/// Numeric knob backed by a slider.
struct IntKnobRow: View {
    @Environment(\.colorScheme) private var scheme
    let label: String
    @Binding var value: Int
    let range: ClosedRange<Int>
    var step: Int = 1

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp2) {
            HStack {
                KnobLabel(text: label)
                Spacer()
                Text("\(value)")
                    .font(PrismaTypography.labelMd.font)
                    .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
            }
            Slider(
                value: Binding(
                    get: { Double(value) },
                    set: { value = Int($0) }
                ),
                in: Double(range.lowerBound)...Double(range.upperBound),
                step: Double(step)
            )
            .tint(PrismaSemanticColors.accentDefault.themed(scheme))
        }
    }
}

/// Enum knob rendered as a chip row.
struct EnumKnobRow<Value: Hashable>: View {
    let label: String
    @Binding var value: Value
    let values: [Value]
    var optionLabel: (Value) -> String = { String(describing: $0) }

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp2) {
            KnobLabel(text: label)
            FlowLayout(spacing: PrismaSpacing.sp2) {
                ForEach(values, id: \.self) { v in
                    EnumChip(
                        label: optionLabel(v),
                        selected: v == value,
                        action: { value = v }
                    )
                }
            }
        }
    }
}

private struct EnumChip: View {
    @Environment(\.colorScheme) private var scheme
    let label: String
    let selected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(label)
                .font(PrismaTypography.labelSm.font)
                .padding(.horizontal, PrismaSpacing.sp3)
                .padding(.vertical, PrismaSpacing.sp2)
                .foregroundStyle(
                    selected
                        ? PrismaSemanticColors.textOnAccent.themed(scheme)
                        : PrismaSemanticColors.textPrimary.themed(scheme)
                )
                .background(
                    selected
                        ? PrismaSemanticColors.accentDefault.themed(scheme)
                        : PrismaSemanticColors.surfaceRaised.themed(scheme)
                )
                .clipShape(Capsule())
                .overlay(
                    Capsule()
                        .strokeBorder(
                            selected ? Color.clear : PrismaSemanticColors.borderSubtle.themed(scheme),
                            lineWidth: 1
                        )
                )
        }
        .buttonStyle(.plain)
    }
}

/// Icon picker knob. The first tile is "None" (returns nil) when [nullable] is true.
struct IconKnobRow: View {
    let label: String
    @Binding var value: PrismaIcon?
    let options: [PrismaIcon]
    var nullable: Bool = true

    var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp2) {
            KnobLabel(text: label)
            FlowLayout(spacing: PrismaSpacing.sp2) {
                if nullable {
                    IconTile(selected: value == nil, action: { value = nil }, icon: nil, fallback: "—")
                }
                ForEach(options, id: \.self) { icon in
                    IconTile(selected: value == icon, action: { value = icon }, icon: icon, fallback: icon.rawValue)
                }
            }
        }
    }
}

private struct IconTile: View {
    @Environment(\.colorScheme) private var scheme
    let selected: Bool
    let action: () -> Void
    let icon: PrismaIcon?
    let fallback: String

    var body: some View {
        Button(action: action) {
            Group {
                if let icon {
                    Image(prisma: icon)
                        .renderingMode(.template)
                        .resizable()
                        .frame(width: 18, height: 18)
                        .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                } else {
                    Text(fallback)
                        .font(PrismaTypography.labelMd.font)
                        .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                }
            }
            .frame(width: 40, height: 40)
            .background(
                selected
                    ? PrismaSemanticColors.accentSubtle.themed(scheme)
                    : PrismaSemanticColors.surfaceRaised.themed(scheme)
            )
            .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
            .overlay(
                RoundedRectangle(cornerRadius: PrismaRadius.md)
                    .strokeBorder(
                        selected
                            ? PrismaSemanticColors.accentDefault.themed(scheme)
                            : PrismaSemanticColors.borderSubtle.themed(scheme),
                        lineWidth: selected ? 2 : 1
                    )
            )
        }
        .buttonStyle(.plain)
    }
}

// MARK: - FlowLayout

/// Minimal flow layout (SwiftUI's stock HStack doesn't wrap). Used by knob
/// controls that produce a variable number of chip-like children.
struct FlowLayout: Layout {
    let spacing: CGFloat

    init(spacing: CGFloat = 8) {
        self.spacing = spacing
    }

    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        let maxWidth = proposal.width ?? .infinity
        var lineWidth: CGFloat = 0
        var lineHeight: CGFloat = 0
        var totalHeight: CGFloat = 0
        var totalWidth: CGFloat = 0
        for subview in subviews {
            let s = subview.sizeThatFits(.unspecified)
            if lineWidth + s.width > maxWidth {
                totalHeight += lineHeight + spacing
                totalWidth = max(totalWidth, lineWidth - spacing)
                lineWidth = s.width + spacing
                lineHeight = s.height
            } else {
                lineWidth += s.width + spacing
                lineHeight = max(lineHeight, s.height)
            }
        }
        totalHeight += lineHeight
        totalWidth = max(totalWidth, lineWidth - spacing)
        return CGSize(width: totalWidth, height: totalHeight)
    }

    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        var x = bounds.minX
        var y = bounds.minY
        var lineHeight: CGFloat = 0
        for subview in subviews {
            let s = subview.sizeThatFits(.unspecified)
            if x + s.width > bounds.maxX {
                x = bounds.minX
                y += lineHeight + spacing
                lineHeight = 0
            }
            subview.place(at: CGPoint(x: x, y: y), proposal: ProposedViewSize(s))
            x += s.width + spacing
            lineHeight = max(lineHeight, s.height)
        }
    }
}
