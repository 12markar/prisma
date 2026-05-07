import SwiftUI
import CoreUI

// MARK: - Chip

public enum PrismaChipVariant { case filter, suggestion, input }

public struct PrismaChip: View {
    private let label: String
    private let selected: Bool
    private let variant: PrismaChipVariant
    private let enabled: Bool
    private let onTap: () -> Void
    private let onDismiss: (() -> Void)?
    private let leadingIcon: (() -> AnyView)?

    @Environment(\.colorScheme) private var scheme

    public init(
        label: String,
        selected: Bool = false,
        variant: PrismaChipVariant = .filter,
        enabled: Bool = true,
        leadingIcon: (() -> AnyView)? = nil,
        onTap: @escaping () -> Void,
        onDismiss: (() -> Void)? = nil
    ) {
        self.label = label; self.selected = selected; self.variant = variant; self.enabled = enabled
        self.leadingIcon = leadingIcon; self.onTap = onTap; self.onDismiss = onDismiss
    }

    public var body: some View {
        let (bg, fg, border) = colors
        Button(action: { prismaSelectionHaptic(); onTap() }) {
            HStack(spacing: PrismaSpacing.sp2) {
                if let leadingIcon { leadingIcon().frame(width: 14, height: 14) }
                Text(label).font(PrismaTypography.labelMd.font).foregroundStyle(fg.themed(scheme))
                if variant == .input, let onDismiss {
                    Button(action: onDismiss) {
                        Image(prisma: .x).renderingMode(.template).resizable()
                            .frame(width: 14, height: 14)
                            .foregroundStyle(fg.themed(scheme))
                    }.buttonStyle(.plain)
                }
            }
            .padding(.horizontal, PrismaSpacing.sp3)
            .padding(.vertical, 6)
            .background(bg.themed(scheme))
            .overlay(Capsule().strokeBorder(border.themed(scheme), lineWidth: 1))
            .clipShape(Capsule())
        }
        .buttonStyle(.plain)
        .disabled(!enabled)
    }

    private var colors: (PrismaSemanticColor, PrismaSemanticColor, PrismaSemanticColor) {
        if !enabled { return (PrismaSemanticColors.surfaceSunken, PrismaSemanticColors.textDisabled, PrismaSemanticColors.borderSubtle) }
        if selected { return (PrismaSemanticColors.accentSubtle, PrismaSemanticColors.accentDefault, PrismaSemanticColors.accentDefault) }
        return (PrismaSemanticColors.surfaceRaised, PrismaSemanticColors.textPrimary, PrismaSemanticColors.borderDefault)
    }
}

// MARK: - Tabs

public struct PrismaTabs<T: Hashable>: View {
    private let tabs: [T]
    @Binding private var selected: T
    private let label: (T) -> String
    @Environment(\.colorScheme) private var scheme

    public init(tabs: [T], selected: Binding<T>, label: @escaping (T) -> String = { String(describing: $0) }) {
        self.tabs = tabs; self._selected = selected; self.label = label
    }

    public var body: some View {
        VStack(spacing: 0) {
            HStack(spacing: 0) {
                ForEach(Array(tabs.enumerated()), id: \.offset) { _, tab in
                    let isSelected = tab == selected
                    Button(action: {
                        if !isSelected { prismaSelectionHaptic() }
                        selected = tab
                    }) {
                        VStack(spacing: PrismaSpacing.sp2) {
                            Text(label(tab))
                                .font(PrismaTypography.labelLg.font)
                                .foregroundStyle(isSelected ? PrismaSemanticColors.textPrimary.themed(scheme)
                                                              : PrismaSemanticColors.textSecondary.themed(scheme))
                            Rectangle()
                                .fill(isSelected ? PrismaSemanticColors.accentDefault.themed(scheme) : .clear)
                                .frame(height: 2)
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, PrismaSpacing.sp3)
                    }.buttonStyle(.plain)
                }
            }
            Rectangle().fill(PrismaSemanticColors.borderSubtle.themed(scheme)).frame(height: 1)
        }
    }
}

// MARK: - Pagination

public struct PrismaPagination: View {
    @Binding private var page: Int
    private let pageCount: Int
    @Environment(\.colorScheme) private var scheme

    public init(page: Binding<Int>, pageCount: Int) { self._page = page; self.pageCount = pageCount }

    /// Arrows are pinned to the left / right edges; the page-number row
    /// scrolls horizontally between them when there isn't enough width. This
    /// fixes the "right arrow disappears at certain pages" feedback —
    /// previously the whole HStack could overflow on narrow screens.
    public var body: some View {
        HStack(spacing: PrismaSpacing.sp1) {
            arrow(prisma: .chevronLeft, enabled: page > 1) { page -= 1 }
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: PrismaSpacing.sp1) {
                    ForEach(pagesToShow(), id: \.self) { p in
                        if p == -1 {
                            Text("…").frame(width: 36, height: 36)
                                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                        } else {
                            pageButton(p)
                        }
                    }
                }
                .frame(maxWidth: .infinity)
            }
            arrow(prisma: .chevronRight, enabled: page < pageCount) { page += 1 }
        }
    }

    @ViewBuilder
    private func pageButton(_ p: Int) -> some View {
        let selected = p == page
        Button(action: { page = p }) {
            Text("\(p)")
                .font(PrismaTypography.labelMd.font)
                .foregroundStyle(selected ? PrismaSemanticColors.textOnAccent.themed(scheme)
                                          : PrismaSemanticColors.textPrimary.themed(scheme))
                .frame(width: 36, height: 36)
                .background(selected ? PrismaSemanticColors.accentDefault.themed(scheme)
                                      : PrismaSemanticColors.surfaceRaised.themed(scheme))
                .overlay(
                    RoundedRectangle(cornerRadius: PrismaRadius.md)
                        .strokeBorder(selected ? .clear : PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
                )
                .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
        }.buttonStyle(.plain)
    }

    @ViewBuilder
    private func arrow(prisma icon: PrismaIcon, enabled: Bool, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Image(prisma: icon).renderingMode(.template).resizable()
                .frame(width: 16, height: 16)
                .foregroundStyle(enabled ? PrismaSemanticColors.textPrimary.themed(scheme)
                                          : PrismaSemanticColors.textDisabled.themed(scheme))
                .frame(width: 36, height: 36)
                .background(PrismaSemanticColors.surfaceRaised.themed(scheme))
                .overlay(
                    RoundedRectangle(cornerRadius: PrismaRadius.md)
                        .strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
                )
                .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
        }
        .buttonStyle(.plain)
        .disabled(!enabled)
    }

    private func pagesToShow() -> [Int] {
        if pageCount <= 7 { return Array(1...pageCount) }
        var out: [Int] = [1]
        if page > 3 { out.append(-1) }
        let start = max(2, page - 1); let end = min(pageCount - 1, page + 1)
        out.append(contentsOf: Array(start...end))
        if page < pageCount - 2 { out.append(-1) }
        out.append(pageCount)
        return out
    }
}

// MARK: - Breadcrumb

public struct PrismaBreadcrumbItem: Identifiable {
    public let id = UUID()
    public let label: String
    public let onTap: (() -> Void)?
    public init(label: String, onTap: (() -> Void)? = nil) { self.label = label; self.onTap = onTap }
}

public struct PrismaBreadcrumb: View {
    private let items: [PrismaBreadcrumbItem]
    @Environment(\.colorScheme) private var scheme

    public init(items: [PrismaBreadcrumbItem]) { self.items = items }

    public var body: some View {
        HStack(spacing: PrismaSpacing.sp2) {
            ForEach(Array(items.enumerated()), id: \.offset) { i, item in
                let isLast = i == items.count - 1
                let color = isLast ? PrismaSemanticColors.textPrimary : PrismaSemanticColors.textSecondary
                if let onTap = item.onTap, !isLast {
                    Button(action: onTap) {
                        Text(item.label).font(PrismaTypography.bodySm.font)
                            .foregroundStyle(color.themed(scheme))
                    }.buttonStyle(.plain)
                } else {
                    Text(item.label).font(PrismaTypography.bodySm.font)
                        .foregroundStyle(color.themed(scheme))
                }
                if !isLast {
                    Image(prisma: .chevronRight).renderingMode(.template).resizable()
                        .frame(width: 14, height: 14)
                        .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                }
            }
        }
    }
}

// MARK: - Wizard

/// Step indicator with `done / active / future` states.
///
/// Layout: each step is a fixed-width cell that wraps to the next visual
/// line when the screen is too narrow — implemented via the SwiftUI
/// `FlowLayout` shipped in the catalogue's playground module. Connector
/// lines render between steps that land on the same row; wrapped rows
/// drop the connector by design.
public struct PrismaWizardSteps: View {
    private let steps: [String]
    private let activeIndex: Int
    @Environment(\.colorScheme) private var scheme

    public init(steps: [String], activeIndex: Int) { self.steps = steps; self.activeIndex = activeIndex }

    public var body: some View {
        WizardFlowLayout(spacing: PrismaSpacing.sp3, lineSpacing: PrismaSpacing.sp4) {
            ForEach(Array(steps.enumerated()), id: \.offset) { i, step in
                let state: StepState = i < activeIndex ? .done : i == activeIndex ? .active : .future
                HStack(spacing: PrismaSpacing.sp2) {
                    VStack(spacing: PrismaSpacing.sp2) {
                        ZStack {
                            Circle().fill(state == .future ? PrismaSemanticColors.surfaceSunken.themed(scheme)
                                                              : PrismaSemanticColors.accentDefault.themed(scheme))
                                .frame(width: 28, height: 28)
                            if state == .done {
                                Image(prisma: .check).renderingMode(.template).resizable()
                                    .frame(width: 16, height: 16)
                                    .foregroundStyle(PrismaSemanticColors.textOnAccent.themed(scheme))
                            } else {
                                Text("\(i + 1)").font(PrismaTypography.labelMd.font)
                                    .foregroundStyle(state == .active ? PrismaSemanticColors.textOnAccent.themed(scheme)
                                                                        : PrismaSemanticColors.textTertiary.themed(scheme))
                            }
                        }
                        Text(step).font(PrismaTypography.labelSm.font)
                            .foregroundStyle(state == .future ? PrismaSemanticColors.textTertiary.themed(scheme)
                                                                : PrismaSemanticColors.textPrimary.themed(scheme))
                            .multilineTextAlignment(.center)
                    }
                    .frame(minWidth: 88, idealWidth: 110, maxWidth: 140)
                    if i < steps.count - 1 {
                        Rectangle()
                            .fill(i < activeIndex ? PrismaSemanticColors.accentDefault.themed(scheme)
                                                    : PrismaSemanticColors.borderSubtle.themed(scheme))
                            .frame(width: 24, height: 2)
                    }
                }
            }
        }
    }

    private enum StepState { case done, active, future }
}

/// Lightweight flow layout used by [PrismaWizardSteps]. Distinct from the
/// app-side `FlowLayout` in App/Playground so the component module stays
/// independent of the catalogue.
private struct WizardFlowLayout: Layout {
    let spacing: CGFloat
    let lineSpacing: CGFloat

    init(spacing: CGFloat = 8, lineSpacing: CGFloat = 12) {
        self.spacing = spacing
        self.lineSpacing = lineSpacing
    }

    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        let maxWidth = proposal.width ?? .infinity
        var lineWidth: CGFloat = 0
        var lineHeight: CGFloat = 0
        var totalHeight: CGFloat = 0
        var totalWidth: CGFloat = 0
        for subview in subviews {
            let s = subview.sizeThatFits(.unspecified)
            if lineWidth + s.width > maxWidth, lineWidth > 0 {
                totalHeight += lineHeight + lineSpacing
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
            if x + s.width > bounds.maxX, x > bounds.minX {
                x = bounds.minX
                y += lineHeight + lineSpacing
                lineHeight = 0
            }
            subview.place(at: CGPoint(x: x, y: y), proposal: ProposedViewSize(s))
            x += s.width + spacing
            lineHeight = max(lineHeight, s.height)
        }
    }
}

// MARK: - Toast

public enum PrismaToastKind { case info, success, warning, danger }

public struct PrismaToast: View {
    private let message: String
    private let kind: PrismaToastKind
    private let actionLabel: String?
    private let onAction: (() -> Void)?
    @Environment(\.colorScheme) private var scheme

    public init(message: String, kind: PrismaToastKind = .info, actionLabel: String? = nil, onAction: (() -> Void)? = nil) {
        self.message = message; self.kind = kind; self.actionLabel = actionLabel; self.onAction = onAction
    }

    public var body: some View {
        HStack(spacing: PrismaSpacing.sp3) {
            Image(prisma: iconFor()).renderingMode(.template).resizable()
                .frame(width: 18, height: 18)
                .foregroundStyle(PrismaSemanticColors.textOnInverse.themed(scheme))
            Text(message).font(PrismaTypography.bodyMd.font)
                .foregroundStyle(PrismaSemanticColors.textOnInverse.themed(scheme))
            if let actionLabel, let onAction {
                Button(action: onAction) {
                    Text(actionLabel).font(PrismaTypography.labelMd.font)
                        .foregroundStyle(PrismaSemanticColors.accentDefault.themed(scheme))
                }.buttonStyle(.plain)
            }
        }
        .padding(.horizontal, PrismaSpacing.sp4)
        .padding(.vertical, PrismaSpacing.sp3)
        .background(PrismaSemanticColors.surfaceInverse.themed(scheme))
        .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
        .shadow(color: .black.opacity(0.2), radius: 8, x: 0, y: 4)
    }

    private func iconFor() -> PrismaIcon {
        switch kind {
        case .info: return .info
        case .success: return .success
        case .warning: return .warning
        case .danger: return .alert
        }
    }
}

// MARK: - Banner

public enum PrismaBannerKind { case info, success, warning, danger }

public struct PrismaBanner: View {
    private let title: String
    private let description: String?
    private let kind: PrismaBannerKind
    private let actionLabel: String?
    private let onAction: (() -> Void)?
    private let onDismiss: (() -> Void)?
    @Environment(\.colorScheme) private var scheme

    public init(
        title: String,
        description: String? = nil,
        kind: PrismaBannerKind = .info,
        actionLabel: String? = nil,
        onAction: (() -> Void)? = nil,
        onDismiss: (() -> Void)? = nil
    ) {
        self.title = title; self.description = description; self.kind = kind
        self.actionLabel = actionLabel; self.onAction = onAction; self.onDismiss = onDismiss
    }

    public var body: some View {
        let (bg, accent) = colors
        HStack(alignment: .top, spacing: 0) {
            Rectangle().fill(accent.themed(scheme)).frame(width: 3)
            HStack(alignment: .top, spacing: PrismaSpacing.sp3) {
                Image(prisma: iconFor()).renderingMode(.template).resizable()
                    .frame(width: 20, height: 20)
                    .foregroundStyle(accent.themed(scheme))
                VStack(alignment: .leading, spacing: PrismaSpacing.sp1) {
                    Text(title).font(PrismaTypography.labelLg.font)
                        .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                    if let description {
                        Text(description).font(PrismaTypography.bodySm.font)
                            .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                    }
                    if let actionLabel, let onAction {
                        Button(action: onAction) {
                            Text(actionLabel).font(PrismaTypography.labelMd.font)
                                .foregroundStyle(accent.themed(scheme))
                        }.buttonStyle(.plain).padding(.top, 4)
                    }
                }
                Spacer()
                if let onDismiss {
                    Button(action: onDismiss) {
                        Image(prisma: .x).renderingMode(.template).resizable()
                            .frame(width: 16, height: 16)
                            .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                    }.buttonStyle(.plain)
                }
            }
            .padding(PrismaSpacing.sp4)
        }
        .background(bg.themed(scheme))
        .overlay(
            RoundedRectangle(cornerRadius: PrismaRadius.md)
                .strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
    }

    private var colors: (PrismaSemanticColor, PrismaSemanticColor) {
        switch kind {
        case .info: return (PrismaSemanticColors.statusInfoSubtle, PrismaSemanticColors.statusInfoDefault)
        case .success: return (PrismaSemanticColors.statusSuccessSubtle, PrismaSemanticColors.statusSuccessDefault)
        case .warning: return (PrismaSemanticColors.statusWarningSubtle, PrismaSemanticColors.statusWarningDefault)
        case .danger: return (PrismaSemanticColors.statusDangerSubtle, PrismaSemanticColors.statusDangerDefault)
        }
    }

    private func iconFor() -> PrismaIcon {
        switch kind {
        case .info: return .info
        case .success: return .success
        case .warning: return .warning
        case .danger: return .alert
        }
    }
}

// MARK: - Loading

public enum PrismaLoadingSize {
    case sm, md, lg
    public var diameter: CGFloat {
        switch self { case .sm: return 16; case .md: return 24; case .lg: return 40 }
    }
    public var stroke: CGFloat {
        switch self { case .sm: return 2; case .md: return 2.5; case .lg: return 3 }
    }
}

public struct PrismaCircularLoading: View {
    private let size: PrismaLoadingSize
    @Environment(\.colorScheme) private var scheme

    public init(size: PrismaLoadingSize = .md) { self.size = size }

    public var body: some View {
        ProgressView()
            .progressViewStyle(.circular)
            .tint(PrismaSemanticColors.accentDefault.themed(scheme))
            .frame(width: size.diameter, height: size.diameter)
    }
}

public struct PrismaLinearLoading: View {
    private let progress: Double?
    @Environment(\.colorScheme) private var scheme

    public init(progress: Double? = nil) { self.progress = progress }

    public var body: some View {
        if let progress {
            ProgressView(value: max(0, min(progress, 1)))
                .progressViewStyle(.linear)
                .tint(PrismaSemanticColors.accentDefault.themed(scheme))
        } else {
            ProgressView()
                .progressViewStyle(.linear)
                .tint(PrismaSemanticColors.accentDefault.themed(scheme))
        }
    }
}

// MARK: - Skeleton

public struct PrismaSkeletonBlock: View {
    private let cornerRadius: CGFloat
    @Environment(\.colorScheme) private var scheme
    @State private var phase: Double = 0

    public init(cornerRadius: CGFloat = 4) { self.cornerRadius = cornerRadius }

    public var body: some View {
        skeletonShape.clipShape(RoundedRectangle(cornerRadius: cornerRadius))
    }

    private var skeletonShape: some View {
        let base = PrismaSemanticColors.surfaceSunken.themed(scheme)
        let highlight = PrismaSemanticColors.borderSubtle.themed(scheme)
        return Rectangle()
            .fill(LinearGradient(colors: [base, highlight, base], startPoint: .leading, endPoint: .trailing))
            .onAppear {
                withAnimation(.linear(duration: 1.5).repeatForever(autoreverses: false)) {
                    phase = 1
                }
            }
    }
}

public struct PrismaSkeletonLine: View {
    public init() {}
    public var body: some View { PrismaSkeletonBlock(cornerRadius: 4) }
}

public struct PrismaSkeletonCircle: View {
    public init() {}
    public var body: some View { PrismaSkeletonBlock(cornerRadius: 9999) }
}

// MARK: - Empty state

public struct PrismaEmptyState<Action: View>: View {
    private let title: String
    private let description: String?
    private let visual: AnyView?
    private let action: () -> Action

    @Environment(\.colorScheme) private var scheme

    public init(
        title: String,
        description: String? = nil,
        visual: (() -> AnyView)? = nil,
        @ViewBuilder action: @escaping () -> Action = { EmptyView() }
    ) {
        self.title = title; self.description = description
        self.visual = visual?(); self.action = action
    }

    public var body: some View {
        VStack(spacing: PrismaSpacing.sp5) {
            if let visual { visual }
            Text(title).font(PrismaTypography.headlineSm.font)
                .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                .multilineTextAlignment(.center)
            if let description {
                Text(description).font(PrismaTypography.bodyMd.font)
                    .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                    .multilineTextAlignment(.center)
                    .frame(maxWidth: 360)
            }
            action()
        }
        .padding(PrismaSpacing.sp7)
        .frame(maxWidth: .infinity)
    }
}

// MARK: - Tooltip / Popover (.help / .popover modifiers wrap-style)

/// Use SwiftUI's native `.help(text:)` modifier on the trigger view.
public struct PrismaTooltipTrigger: View {
    private let text: String
    private let content: AnyView

    public init(text: String, @ViewBuilder content: () -> AnyView) {
        self.text = text; self.content = content()
    }

    public var body: some View {
        content.help(text)
    }
}

/// Convenience wrapper to apply `.popover(isPresented:)` consistently with Prisma styling.
public struct PrismaPopoverContent<Content: View>: View {
    private let content: () -> Content
    @Environment(\.colorScheme) private var scheme

    public init(@ViewBuilder content: @escaping () -> Content) { self.content = content }

    public var body: some View {
        content()
            .padding(PrismaSpacing.sp4)
            .background(PrismaSemanticColors.surfaceRaised.themed(scheme))
    }
}

// MARK: - Modal / Bottom sheet helpers

public struct PrismaModalContent: View {
    private let title: String
    private let message: String
    private let confirmLabel: String
    private let onConfirm: () -> Void
    private let dismissLabel: String?
    private let onDismiss: (() -> Void)?
    private let isDestructive: Bool
    @Environment(\.colorScheme) private var scheme
    @Environment(\.dismiss) private var dismissAction

    public init(
        title: String,
        message: String,
        confirmLabel: String,
        onConfirm: @escaping () -> Void,
        dismissLabel: String? = nil,
        onDismiss: (() -> Void)? = nil,
        isDestructive: Bool = false
    ) {
        self.title = title; self.message = message; self.confirmLabel = confirmLabel
        self.onConfirm = onConfirm; self.dismissLabel = dismissLabel
        self.onDismiss = onDismiss; self.isDestructive = isDestructive
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
            Text(title).font(PrismaTypography.headlineSm.font)
                .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
            Text(message).font(PrismaTypography.bodyMd.font)
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
            HStack {
                Spacer()
                if let dismissLabel {
                    PrismaButton(dismissLabel, variant: .ghost) {
                        (onDismiss ?? { dismissAction() })()
                    }
                }
                PrismaButton(confirmLabel, variant: isDestructive ? .destructive : .primary) {
                    onConfirm()
                }
            }
        }
        .padding(PrismaSpacing.sp7)
    }
}

// MARK: - Drawer (NavigationSplitView wrapper) — SwiftUI provides this natively;
// we expose a stateless content view consumed inside `.sheet` / `.fullScreenCover`.

public struct PrismaDrawerContent<Content: View>: View {
    private let content: () -> Content
    @Environment(\.colorScheme) private var scheme

    public init(@ViewBuilder content: @escaping () -> Content) { self.content = content }

    public var body: some View {
        content().padding(PrismaSpacing.sp7)
            .background(PrismaSemanticColors.surfaceRaised.themed(scheme).ignoresSafeArea())
    }
}

// MARK: - Command palette

public struct PrismaCommand: Identifiable {
    public let id = UUID()
    public let label: String
    public let group: String
    public let onAction: () -> Void
    public init(label: String, group: String, onAction: @escaping () -> Void) {
        self.label = label; self.group = group; self.onAction = onAction
    }
}

public struct PrismaCommandPalette: View {
    private let commands: [PrismaCommand]
    private let onDismiss: () -> Void
    @State private var query: String = ""
    @Environment(\.colorScheme) private var scheme

    public init(commands: [PrismaCommand], onDismiss: @escaping () -> Void) {
        self.commands = commands; self.onDismiss = onDismiss
    }

    public var body: some View {
        let filtered = query.isEmpty ? commands :
            commands.filter { $0.label.lowercased().contains(query.lowercased()) }
        let grouped = Dictionary(grouping: filtered, by: { $0.group })

        VStack(spacing: 0) {
            PrismaTextField(
                text: $query,
                placeholder: "Type a command or search…",
                variant: PrismaTextFieldVariant.filled,
                leadingIcon: { () -> AnyView in
                    AnyView(
                        Image(prisma: .search).renderingMode(.template).resizable()
                            .frame(width: 18, height: 18)
                            .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                    )
                }
            )
            .padding(PrismaSpacing.sp4)
            ScrollView {
                LazyVStack(alignment: .leading, spacing: 0) {
                    ForEach(Array(grouped.keys), id: \.self) { group in
                        Text(group.uppercased())
                            .font(PrismaTypography.labelSm.font)
                            .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                            .padding(.horizontal, PrismaSpacing.sp4)
                            .padding(.vertical, PrismaSpacing.sp2)
                        ForEach(grouped[group] ?? []) { command in
                            Button(action: { command.onAction(); onDismiss() }) {
                                HStack {
                                    Text(command.label).font(PrismaTypography.bodyMd.font)
                                        .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                                    Spacer()
                                }
                                .padding(.horizontal, PrismaSpacing.sp4)
                                .padding(.vertical, PrismaSpacing.sp3)
                                .contentShape(Rectangle())
                            }.buttonStyle(.plain)
                        }
                    }
                }
            }
            HStack {
                Spacer()
                Text("↑↓ navigate · ↵ select · esc close")
                    .font(.system(size: 11, design: .monospaced))
                    .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
            }
            .padding(.horizontal, PrismaSpacing.sp4)
            .padding(.vertical, PrismaSpacing.sp2)
            .background(PrismaSemanticColors.surfaceSunken.themed(scheme))
        }
        .background(PrismaSemanticColors.surfaceRaised.themed(scheme))
        .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.xl))
        .shadow(color: .black.opacity(0.4), radius: 24, x: 0, y: 8)
    }
}
