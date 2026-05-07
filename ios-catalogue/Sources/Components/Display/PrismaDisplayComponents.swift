import SwiftUI
import CoreUI

// MARK: - Divider

public struct PrismaHorizontalDivider: View {
    public enum Weight { case subtle, defaultWeight, strong }
    private let weight: Weight
    private let inset: CGFloat
    @Environment(\.colorScheme) private var scheme

    public init(weight: Weight = .subtle, inset: CGFloat = 0) { self.weight = weight; self.inset = inset }

    public var body: some View {
        Rectangle()
            .fill(colorFor().themed(scheme))
            .frame(height: 1)
            .padding(.leading, inset)
    }

    private func colorFor() -> PrismaSemanticColor {
        switch weight {
        case .subtle: return PrismaSemanticColors.borderSubtle
        case .defaultWeight: return PrismaSemanticColors.borderDefault
        case .strong: return PrismaSemanticColors.borderStrong
        }
    }
}

// MARK: - Badge

public enum PrismaBadgeStatus { case accent, success, warning, danger, info }

public struct PrismaCountBadge: View {
    private let count: Int
    private let status: PrismaBadgeStatus
    @Environment(\.colorScheme) private var scheme

    public init(count: Int, status: PrismaBadgeStatus = .accent) { self.count = count; self.status = status }

    public var body: some View {
        let display = count > 99 ? "99+" : "\(count)"
        let (bg, fg) = badgeColors(status)
        Text(display)
            .font(.system(size: 11, weight: .semibold))
            .foregroundStyle(fg.themed(scheme))
            .padding(.horizontal, PrismaSpacing.sp2)
            .padding(.vertical, 1)
            .frame(minWidth: 18, minHeight: 18)
            .background(bg.themed(scheme))
            .clipShape(Capsule())
    }
}

public struct PrismaDotBadge: View {
    private let status: PrismaBadgeStatus
    @Environment(\.colorScheme) private var scheme

    public init(status: PrismaBadgeStatus = .accent) { self.status = status }

    public var body: some View {
        let (bg, _) = badgeColors(status)
        Circle().fill(bg.themed(scheme)).frame(width: 8, height: 8)
    }
}

private func badgeColors(_ status: PrismaBadgeStatus) -> (PrismaSemanticColor, PrismaSemanticColor) {
    switch status {
    case .accent: return (PrismaSemanticColors.accentDefault, PrismaSemanticColors.textOnAccent)
    case .success: return (PrismaSemanticColors.statusSuccessDefault, PrismaSemanticColors.statusSuccessOnStatus)
    case .warning: return (PrismaSemanticColors.statusWarningDefault, PrismaSemanticColors.statusWarningOnStatus)
    case .danger: return (PrismaSemanticColors.statusDangerDefault, PrismaSemanticColors.statusDangerOnStatus)
    case .info: return (PrismaSemanticColors.statusInfoDefault, PrismaSemanticColors.statusInfoOnStatus)
    }
}

// MARK: - Card

public enum PrismaCardVariant { case elevated, outlined, filled }

public struct PrismaCard<Content: View>: View {
    private let variant: PrismaCardVariant
    private let onTap: (() -> Void)?
    private let contentPadding: CGFloat
    private let content: () -> Content

    @Environment(\.colorScheme) private var scheme

    public init(
        variant: PrismaCardVariant = .outlined,
        onTap: (() -> Void)? = nil,
        contentPadding: CGFloat = PrismaSpacing.sp5,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.variant = variant; self.onTap = onTap; self.contentPadding = contentPadding; self.content = content
    }

    public var body: some View {
        let body = content()
            .padding(contentPadding)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(backgroundColor.themed(scheme))
            .overlay(borderOverlay)
            .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.lg))
            .shadow(color: shadowColor, radius: shadowRadius, x: 0, y: shadowY)
        if let onTap {
            Button(action: onTap) { body }.buttonStyle(.plain)
        } else {
            body
        }
    }

    private var backgroundColor: PrismaSemanticColor {
        switch variant {
        case .elevated: return PrismaSemanticColors.surfaceRaised
        case .outlined: return PrismaSemanticColors.surfaceBase
        case .filled:   return PrismaSemanticColors.surfaceSunken
        }
    }
    @ViewBuilder private var borderOverlay: some View {
        if variant == .outlined {
            RoundedRectangle(cornerRadius: PrismaRadius.lg)
                .strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
        }
    }
    private var shadowColor: Color { variant == .elevated ? .black.opacity(0.06) : .clear }
    private var shadowRadius: CGFloat { variant == .elevated ? 4 : 0 }
    private var shadowY: CGFloat { variant == .elevated ? 1 : 0 }
}

// MARK: - List item

public struct PrismaListItem<Leading: View, Trailing: View>: View {
    private let primary: String
    private let secondary: String?
    private let leading: () -> Leading
    private let trailing: () -> Trailing
    private let onTap: (() -> Void)?
    private let selected: Bool

    @Environment(\.colorScheme) private var scheme

    public init(
        primary: String,
        secondary: String? = nil,
        selected: Bool = false,
        onTap: (() -> Void)? = nil,
        @ViewBuilder leading: @escaping () -> Leading = { EmptyView() },
        @ViewBuilder trailing: @escaping () -> Trailing = { EmptyView() }
    ) {
        self.primary = primary; self.secondary = secondary; self.selected = selected; self.onTap = onTap
        self.leading = leading; self.trailing = trailing
    }

    public var body: some View {
        let inner = HStack(spacing: PrismaSpacing.sp4) {
            leading()
            VStack(alignment: .leading, spacing: 2) {
                Text(primary).font(PrismaTypography.bodyMd.font)
                    .foregroundStyle(selected ? PrismaSemanticColors.accentDefault.themed(scheme)
                                                : PrismaSemanticColors.textPrimary.themed(scheme))
                if let secondary {
                    Text(secondary).font(PrismaTypography.bodySm.font)
                        .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                        .lineLimit(2)
                }
            }
            Spacer()
            trailing()
        }
        .padding(.horizontal, PrismaSpacing.sp4)
        .padding(.vertical, PrismaSpacing.sp3)
        .frame(minHeight: 56)
        .background(selected ? PrismaSemanticColors.accentSubtle.themed(scheme)
                              : PrismaSemanticColors.surfaceBase.themed(scheme))
        if let onTap {
            Button(action: onTap) { inner }.buttonStyle(.plain)
        } else {
            inner
        }
    }
}

// MARK: - Avatar

public enum PrismaAvatarSize: CGFloat {
    case xs = 20
    case sm = 28
    case `default` = 40
    case lg = 56
    case xl = 96

    public var initialsScale: CGFloat {
        switch self {
        case .xs, .sm: return 0.45
        case .default: return 0.42
        case .lg: return 0.40
        case .xl: return 0.36
        }
    }
}

public enum PrismaAvatarStatus { case online, away, offline, busy, none }

public struct PrismaAvatar: View {
    private let seed: String
    private let initials: String
    private let size: PrismaAvatarSize
    private let status: PrismaAvatarStatus

    @Environment(\.colorScheme) private var scheme

    public init(seed: String, size: PrismaAvatarSize = .default, status: PrismaAvatarStatus = .none) {
        self.seed = seed; self.initials = Self.derivedInitials(seed); self.size = size; self.status = status
    }

    public var body: some View {
        let (bg, fg) = swatchFor(seed)
        ZStack(alignment: .bottomTrailing) {
            Circle().fill(bg.themed(scheme))
                .frame(width: size.rawValue, height: size.rawValue)
                .overlay {
                    Text(initials)
                        .font(.system(size: size.rawValue * size.initialsScale, weight: .semibold))
                        .foregroundStyle(fg.themed(scheme))
                }
            if status != .none, size.rawValue >= 28 {
                let dotDiameter = max(size.rawValue * 0.28, 8)
                Circle().fill(statusColor.themed(scheme))
                    .frame(width: dotDiameter, height: dotDiameter)
                    .overlay(Circle().stroke(PrismaSemanticColors.surfaceBase.themed(scheme), lineWidth: 2))
                    .offset(x: 1, y: 1)
            }
        }
    }

    private var statusColor: PrismaSemanticColor {
        switch status {
        case .online: return PrismaSemanticColors.statusSuccessDefault
        case .away:   return PrismaSemanticColors.statusWarningDefault
        case .busy:   return PrismaSemanticColors.statusDangerDefault
        case .offline: return PrismaSemanticColors.textTertiary
        case .none:   return PrismaSemanticColors.surfaceBase
        }
    }

    private static func derivedInitials(_ seed: String) -> String {
        let parts = seed.trimmingCharacters(in: .whitespaces).split(separator: " ")
        if parts.isEmpty { return "?" }
        if parts.count == 1 { return String(parts[0].prefix(1)).uppercased() }
        return String(parts.first!.prefix(1) + parts.last!.prefix(1)).uppercased()
    }

    private func swatchFor(_ seed: String) -> (PrismaSemanticColor, PrismaSemanticColor) {
        let hash = abs(seed.hashValue)
        switch hash % 6 {
        case 0: return (PrismaSemanticColors.accentSubtle, PrismaSemanticColors.accentDefault)
        case 1: return (PrismaSemanticColors.statusSuccessSubtle, PrismaSemanticColors.statusSuccessDefault)
        case 2: return (PrismaSemanticColors.statusWarningSubtle, PrismaSemanticColors.statusWarningDefault)
        case 3: return (PrismaSemanticColors.statusDangerSubtle, PrismaSemanticColors.statusDangerDefault)
        case 4: return (PrismaSemanticColors.statusInfoSubtle, PrismaSemanticColors.statusInfoDefault)
        default: return (PrismaSemanticColors.surfaceSunken, PrismaSemanticColors.textPrimary)
        }
    }
}

// MARK: - Avatar group

public struct PrismaAvatarGroup: View {
    private let seeds: [String]
    private let size: PrismaAvatarSize
    private let max: Int

    @Environment(\.colorScheme) private var scheme

    public init(seeds: [String], size: PrismaAvatarSize = .default, max: Int = 4) {
        self.seeds = seeds; self.size = size; self.max = max
    }

    public var body: some View {
        let visible = Array(seeds.prefix(max))
        let overflow = Swift.max(0, seeds.count - max)
        let overlap = size.rawValue * 0.35

        HStack(spacing: -overlap) {
            ForEach(Array(visible.enumerated()), id: \.offset) { _, seed in
                PrismaAvatar(seed: seed, size: size)
                    .overlay(Circle().stroke(PrismaSemanticColors.surfaceBase.themed(scheme), lineWidth: 2))
            }
            if overflow > 0 {
                ZStack {
                    Circle().fill(PrismaSemanticColors.surfaceSunken.themed(scheme))
                        .overlay(Circle().stroke(PrismaSemanticColors.surfaceBase.themed(scheme), lineWidth: 2))
                    Text("+\(overflow)").font(.system(size: 13, weight: .medium))
                        .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                }
                .frame(width: size.rawValue, height: size.rawValue)
            }
        }
    }
}
