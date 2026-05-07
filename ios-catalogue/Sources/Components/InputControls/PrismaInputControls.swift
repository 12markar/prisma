import SwiftUI
import CoreUI
#if canImport(UIKit)
import UIKit
#endif

/// Light haptic for "this thing changed" — toggles, tab switches, chip toggles.
/// No-op on non-UIKit platforms (macOS) so the file still compiles cross-platform.
@inline(__always)
internal func prismaSelectionHaptic() {
    #if canImport(UIKit)
    UISelectionFeedbackGenerator().selectionChanged()
    #endif
}

// MARK: - Switch

public struct PrismaSwitch: View {
    @Binding private var checked: Bool
    private let label: String?
    private let helperText: String?
    private let enabled: Bool

    @Environment(\.colorScheme) private var scheme

    public init(checked: Binding<Bool>, label: String? = nil, helperText: String? = nil, enabled: Bool = true) {
        self._checked = checked; self.label = label; self.helperText = helperText; self.enabled = enabled
    }

    /// Wraps the user-supplied binding so the haptic fires on every toggle
    /// — whether triggered by tap on the switch itself or on the row.
    private var hapticBinding: Binding<Bool> {
        Binding(
            get: { checked },
            set: { newValue in
                if newValue != checked { prismaSelectionHaptic() }
                checked = newValue
            }
        )
    }

    public var body: some View {
        if let label {
            HStack(alignment: .top, spacing: PrismaSpacing.sp4) {
                VStack(alignment: .leading, spacing: 2) {
                    Text(label)
                        .font(PrismaTypography.bodyMd.font)
                        .foregroundStyle(enabled ? PrismaSemanticColors.textPrimary.themed(scheme)
                                                  : PrismaSemanticColors.textDisabled.themed(scheme))
                    if let helperText {
                        Text(helperText).font(PrismaTypography.bodySm.font)
                            .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                    }
                }
                Spacer()
                Toggle("", isOn: hapticBinding)
                    .labelsHidden()
                    .disabled(!enabled)
                    .tint(PrismaSemanticColors.accentDefault.themed(scheme))
            }
        } else {
            Toggle("", isOn: hapticBinding)
                .labelsHidden()
                .disabled(!enabled)
                .tint(PrismaSemanticColors.accentDefault.themed(scheme))
        }
    }
}

// MARK: - Radio

public struct PrismaRadio: View {
    private let selected: Bool
    private let onClick: (() -> Void)?
    private let label: String?
    private let helperText: String?
    private let enabled: Bool

    @Environment(\.colorScheme) private var scheme

    public init(selected: Bool, onClick: (() -> Void)?, label: String? = nil, helperText: String? = nil, enabled: Bool = true) {
        self.selected = selected; self.onClick = onClick; self.label = label; self.helperText = helperText; self.enabled = enabled
    }

    public var body: some View {
        Button(action: { onClick?() }) {
            HStack(alignment: .top, spacing: PrismaSpacing.sp3) {
                ZStack {
                    Circle()
                        .strokeBorder(
                            !enabled ? PrismaSemanticColors.borderSubtle.themed(scheme)
                            : selected ? PrismaSemanticColors.accentDefault.themed(scheme)
                            : PrismaSemanticColors.borderStrong.themed(scheme),
                            lineWidth: 2
                        )
                        .frame(width: 20, height: 20)
                    if selected {
                        Circle()
                            .fill(enabled ? PrismaSemanticColors.accentDefault.themed(scheme)
                                          : PrismaSemanticColors.textDisabled.themed(scheme))
                            .frame(width: 10, height: 10)
                    }
                }
                .padding(.top, 2)
                if let label {
                    VStack(alignment: .leading, spacing: 2) {
                        Text(label).font(PrismaTypography.bodyMd.font)
                            .foregroundStyle(enabled ? PrismaSemanticColors.textPrimary.themed(scheme)
                                                      : PrismaSemanticColors.textDisabled.themed(scheme))
                        if let helperText {
                            Text(helperText).font(PrismaTypography.bodySm.font)
                                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                        }
                    }
                }
                Spacer(minLength: 0)
            }
            .frame(maxWidth: .infinity, minHeight: 44, alignment: .topLeading)
            .padding(.vertical, PrismaSpacing.sp2)
            .contentShape(Rectangle())   // Whole row is the hit target.
        }
        .buttonStyle(.plain)
        .disabled(!enabled)
    }
}

// MARK: - Slider

public struct PrismaSlider: View {
    @Binding private var value: Double
    private let range: ClosedRange<Double>
    private let step: Double
    private let label: String?
    private let showValue: Bool
    private let enabled: Bool
    private let formatter: (Double) -> String

    @Environment(\.colorScheme) private var scheme

    public init(
        value: Binding<Double>,
        range: ClosedRange<Double> = 0...1,
        step: Double = 0,
        label: String? = nil,
        showValue: Bool = true,
        enabled: Bool = true,
        formatter: @escaping (Double) -> String = { String(format: "%.2f", $0) }
    ) {
        self._value = value; self.range = range; self.step = step
        self.label = label; self.showValue = showValue; self.enabled = enabled; self.formatter = formatter
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp1) {
            if label != nil || showValue {
                HStack {
                    if let label {
                        Text(label).font(PrismaTypography.labelMd.font)
                            .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                    }
                    Spacer()
                    if showValue {
                        Text(formatter(value)).font(.system(size: 13, design: .monospaced))
                            .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                    }
                }
            }
            if step == 0 {
                Slider(value: $value, in: range).tint(PrismaSemanticColors.accentDefault.themed(scheme)).disabled(!enabled)
            } else {
                Slider(value: $value, in: range, step: step).tint(PrismaSemanticColors.accentDefault.themed(scheme)).disabled(!enabled)
            }
        }
    }
}

// MARK: - Segmented Control

public struct PrismaSegmentedControl<T: Hashable>: View {
    private let options: [T]
    @Binding private var selected: T
    private let label: (T) -> String

    @Environment(\.colorScheme) private var scheme

    public init(options: [T], selected: Binding<T>, label: @escaping (T) -> String = { String(describing: $0) }) {
        self.options = options; self._selected = selected; self.label = label
    }

    public var body: some View {
        Picker("", selection: $selected) {
            ForEach(options, id: \.self) { option in
                Text(label(option)).tag(option)
            }
        }
        .pickerStyle(.segmented)
    }
}

// MARK: - Search Bar

public struct PrismaSearchBar: View {
    @Binding private var value: String
    private let placeholder: String
    @Environment(\.colorScheme) private var scheme

    public init(value: Binding<String>, placeholder: String = "Search") {
        self._value = value; self.placeholder = placeholder
    }

    public var body: some View {
        PrismaTextField(
            text: $value,
            placeholder: placeholder,
            variant: PrismaTextFieldVariant.filled,
            leadingIcon: { () -> AnyView in
                AnyView(
                    Image(prisma: .search).renderingMode(.template).resizable()
                        .frame(width: 18, height: 18)
                        .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                )
            }
        )
    }
}

// MARK: - Stepper

public struct PrismaStepper: View {
    @Binding private var value: Int
    private let range: ClosedRange<Int>
    private let step: Int
    private let enabled: Bool

    @Environment(\.colorScheme) private var scheme

    public init(value: Binding<Int>, range: ClosedRange<Int> = 0...99, step: Int = 1, enabled: Bool = true) {
        self._value = value; self.range = range; self.step = step; self.enabled = enabled
    }

    public var body: some View {
        HStack(spacing: 0) {
            stepButton(symbol: .minus, enabled: enabled && value > range.lowerBound) {
                value = max(range.lowerBound, value - step)
            }
            Text("\(value)")
                .font(PrismaTypography.labelLg.font)
                .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                .frame(minWidth: 48)
                .padding(.horizontal, PrismaSpacing.sp2)
            stepButton(symbol: .plus, enabled: enabled && value < range.upperBound) {
                value = min(range.upperBound, value + step)
            }
        }
        .frame(height: 40)
        .background(PrismaSemanticColors.surfaceRaised.themed(scheme))
        .overlay(
            RoundedRectangle(cornerRadius: PrismaRadius.md)
                .strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
    }

    @ViewBuilder
    private func stepButton(symbol: PrismaIcon, enabled: Bool, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Image(prisma: symbol).renderingMode(.template).resizable()
                .frame(width: 18, height: 18)
                .foregroundStyle(enabled ? PrismaSemanticColors.textPrimary.themed(scheme)
                                          : PrismaSemanticColors.textDisabled.themed(scheme))
                .frame(width: 40, height: 40)
        }
        .buttonStyle(.plain)
        .disabled(!enabled)
    }
}

// MARK: - Tag Input

public struct PrismaTagInput: View {
    @Binding private var tags: [String]
    @State private var draft: String = ""
    private let label: String?
    private let placeholder: String

    public init(tags: Binding<[String]>, label: String? = nil, placeholder: String = "Type and press return…") {
        self._tags = tags; self.label = label; self.placeholder = placeholder
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp3) {
            PrismaTextField(text: $draft, label: label, placeholder: placeholder)
                .onSubmit {
                    let tag = draft.trimmingCharacters(in: .whitespaces)
                    if !tag.isEmpty && !tags.contains(tag) { tags.append(tag) }
                    draft = ""
                }
            if !tags.isEmpty {
                FlowLayout(spacing: PrismaSpacing.sp2) {
                    ForEach(tags, id: \.self) { tag in
                        PrismaChip(label: tag, variant: .input) {} onDismiss: {
                            tags.removeAll { $0 == tag }
                        }
                    }
                }
            }
        }
    }
}

// MARK: - Autocomplete

public struct PrismaAutocomplete: View {
    @Binding private var value: String
    private let suggestions: [String]
    private let onSelect: (String) -> Void
    private let label: String?
    private let placeholder: String?

    @Environment(\.colorScheme) private var scheme

    public init(
        value: Binding<String>,
        suggestions: [String],
        onSelect: @escaping (String) -> Void,
        label: String? = nil,
        placeholder: String? = nil
    ) {
        self._value = value; self.suggestions = suggestions; self.onSelect = onSelect
        self.label = label; self.placeholder = placeholder
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp1) {
            PrismaTextField(text: $value, label: label, placeholder: placeholder)
            if !value.isEmpty && !suggestions.isEmpty {
                VStack(alignment: .leading, spacing: 0) {
                    ForEach(suggestions, id: \.self) { suggestion in
                        Button(action: { onSelect(suggestion) }) {
                            HStack {
                                Text(suggestion).font(PrismaTypography.bodyMd.font)
                                    .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                                Spacer()
                            }
                            .padding(.horizontal, PrismaSpacing.sp4)
                            .padding(.vertical, PrismaSpacing.sp3)
                            .contentShape(Rectangle())
                        }
                        .buttonStyle(.plain)
                    }
                }
                .frame(maxWidth: .infinity)
                .background(PrismaSemanticColors.surfaceRaised.themed(scheme))
                .overlay(
                    RoundedRectangle(cornerRadius: PrismaRadius.md)
                        .strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
                )
                .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
            }
        }
    }
}

// MARK: - Date / Time / Color picker (native wrappers)

public struct PrismaDatePicker: View {
    @Binding private var date: Date
    @Environment(\.colorScheme) private var scheme

    public init(date: Binding<Date>) { self._date = date }

    public var body: some View {
        DatePicker("", selection: $date, displayedComponents: [.date])
            .datePickerStyle(.graphical)
            .tint(PrismaSemanticColors.accentDefault.themed(scheme))
    }
}

public struct PrismaTimePicker: View {
    @Binding private var date: Date
    @Environment(\.colorScheme) private var scheme

    public init(date: Binding<Date>) { self._date = date }

    public var body: some View {
        // .labelsHidden() collapses the empty label slot so the compact pill
        // sizes to its own content; without it the pill anchors trailing inside
        // a flex row and a wrapping HStack/Spacer can't centre it.
        DatePicker("Time", selection: $date, displayedComponents: [.hourAndMinute])
            .datePickerStyle(.compact)
            .labelsHidden()
            .tint(PrismaSemanticColors.accentDefault.themed(scheme))
    }
}

public struct PrismaColorPicker: View {
    @Binding private var color: Color
    @Environment(\.colorScheme) private var scheme

    public init(color: Binding<Color>) { self._color = color }

    public var body: some View {
        ColorPicker("Color", selection: $color, supportsOpacity: false)
            .font(PrismaTypography.labelMd.font)
            .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
    }
}

// MARK: - Helpers

/// Minimal flow layout that wraps children to a new line — used by TagInput's chip stack.
private struct FlowLayout: Layout {
    let spacing: CGFloat
    init(spacing: CGFloat) { self.spacing = spacing }

    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        let maxWidth = proposal.width ?? .infinity
        var x: CGFloat = 0; var y: CGFloat = 0; var lineHeight: CGFloat = 0
        for s in subviews {
            let size = s.sizeThatFits(.unspecified)
            if x + size.width > maxWidth {
                x = 0; y += lineHeight + spacing; lineHeight = 0
            }
            x += size.width + spacing
            lineHeight = max(lineHeight, size.height)
        }
        return CGSize(width: maxWidth, height: y + lineHeight)
    }

    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        var x: CGFloat = bounds.minX; var y: CGFloat = bounds.minY; var lineHeight: CGFloat = 0
        for s in subviews {
            let size = s.sizeThatFits(.unspecified)
            if x + size.width > bounds.maxX {
                x = bounds.minX; y += lineHeight + spacing; lineHeight = 0
            }
            s.place(at: CGPoint(x: x, y: y), proposal: ProposedViewSize(size))
            x += size.width + spacing
            lineHeight = max(lineHeight, size.height)
        }
    }
}
