import SwiftUI
import CoreUI
import Components

/// Recursive renderer for an SDUI tree. The outer `switch` is exhaustive on
/// the enum so adding a new node type forces a compile-time update here.
struct SDUIRender: View {
    let node: SDUINode
    @Environment(\.colorScheme) private var scheme
    @Environment(\.openURL) private var openURL

    var body: some View {
        switch node {
        case .column(let n):   columnView(n)
        case .row(let n):      rowView(n)
        case .text(let n):     textView(n)
        case .image(let n):    imageView(n)
        case .button(let n):   buttonView(n)
        case .card(let n):     cardView(n)
        case .tabs(let n):     SDUITabsView(node: n)
        case .divider:         dividerView
        case .spacer(let n):   Spacer().frame(height: CGFloat(n.size ?? 8))
        case .badge(let n):    badgeView(n)
        case .listItem(let n): listItemView(n)
        case .unsupported(let t):
            Text("Unsupported node: \(t)")
                .font(PrismaTypography.labelMd.font)
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                .padding(PrismaSpacing.sp3)
        }
    }

    // MARK: - containers

    private func columnView(_ n: SDUIColumn) -> some View {
        VStack(alignment: alignmentFor(n.alignment), spacing: CGFloat(n.spacing ?? 0)) {
            ForEach(n.children.indices, id: \.self) { i in
                SDUIRender(node: n.children[i])
            }
        }
        .padding(CGFloat(n.padding ?? 0))
        .frame(maxWidth: .infinity, alignment: frameAlignmentFor(n.alignment))
    }

    private func rowView(_ n: SDUIRow) -> some View {
        HStack(alignment: .center, spacing: CGFloat(n.spacing ?? 0)) {
            ForEach(n.children.indices, id: \.self) { i in
                SDUIRender(node: n.children[i])
            }
        }
        .frame(maxWidth: .infinity, alignment: frameAlignmentFor(n.alignment))
    }

    private func cardView(_ n: SDUICard) -> some View {
        PrismaCard(variant: .elevated, contentPadding: CGFloat(n.padding ?? 0)) {
            VStack(alignment: .leading, spacing: CGFloat(n.spacing ?? 0)) {
                ForEach(n.children.indices, id: \.self) { i in
                    SDUIRender(node: n.children[i])
                }
            }
        }
    }

    // MARK: - leaves

    private func textView(_ n: SDUIText) -> some View {
        Text(n.value)
            .font(typographyFor(n.variant).font)
            .foregroundStyle(textColorFor(n.color).themed(scheme))
            .multilineTextAlignment(textAlignFor(n.align))
            .frame(maxWidth: n.align == nil ? nil : .infinity, alignment: frameAlignmentFor(n.align))
    }

    private func imageView(_ n: SDUIImage) -> some View {
        let url = URL(string: n.url)
        let radius = CGFloat(n.cornerRadius ?? 0)
        return AsyncImage(url: url) { phase in
            switch phase {
            case .success(let img):
                img.resizable().aspectRatio(contentMode: contentModeFor(n.contentMode))
            case .failure:
                Rectangle().fill(PrismaSemanticColors.surfaceSunken.themed(scheme))
            case .empty:
                Rectangle().fill(PrismaSemanticColors.surfaceSunken.themed(scheme))
            @unknown default:
                Rectangle().fill(PrismaSemanticColors.surfaceSunken.themed(scheme))
            }
        }
        .frame(width: n.width.map { CGFloat($0) }, height: n.height.map { CGFloat($0) })
        .frame(maxWidth: n.width == nil ? .infinity : nil)
        .clipShape(RoundedRectangle(cornerRadius: radius))
    }

    private func buttonView(_ n: SDUIButton) -> some View {
        let action = { dispatch(n.action) }
        let button = PrismaButton(
            n.label,
            variant: buttonVariantFor(n.variant),
            size: buttonSizeFor(n.size),
            enabled: n.disabled != true,
            action: action
        )
        return Group {
            if n.fullWidth == true {
                button.frame(maxWidth: .infinity)
            } else {
                button
            }
        }
    }

    private var dividerView: some View {
        Rectangle()
            .fill(PrismaSemanticColors.borderSubtle.themed(scheme))
            .frame(height: 1)
            .frame(maxWidth: .infinity)
    }

    private func badgeView(_ n: SDUIBadge) -> some View {
        let (bg, fg) = badgeTones(n.tone)
        return Text(n.label)
            .font(PrismaTypography.labelSm.font)
            .foregroundStyle(fg.themed(scheme))
            .padding(.horizontal, PrismaSpacing.sp3)
            .padding(.vertical, PrismaSpacing.sp1)
            .background(bg.themed(scheme))
            .clipShape(Capsule())
    }

    private func listItemView(_ n: SDUIListItem) -> some View {
        PrismaListItem(
            primary: n.title,
            secondary: n.subtitle,
            onTap: n.action.map { a in { dispatch(a) } },
            leading: {
                if let urlString = n.leadingIconUrl, let url = URL(string: urlString) {
                    AsyncImage(url: url) { img in
                        img.resizable().aspectRatio(contentMode: .fill)
                    } placeholder: {
                        Rectangle().fill(PrismaSemanticColors.surfaceSunken.themed(scheme))
                    }
                    .frame(width: 32, height: 32)
                    .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.sm))
                } else {
                    EmptyView()
                }
            },
            trailing: {
                HStack(spacing: PrismaSpacing.sp2) {
                    if let trailing = n.trailingText {
                        Text(trailing)
                            .font(PrismaTypography.labelMd.font)
                            .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                    }
                    if n.showChevron == true {
                        Image(systemName: "chevron.right")
                            .font(.system(size: 12, weight: .semibold))
                            .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                    }
                }
            }
        )
    }

    // MARK: - actions

    private func dispatch(_ action: SDUIAction?) {
        switch action {
        case .openUrl(let url): openURL(url)
        case .log(let id):      print("[sdui] action.log id=\(id)")
        case .noop, .none:      break
        }
    }

    // MARK: - mappings

    private func alignmentFor(_ name: String?) -> HorizontalAlignment {
        switch name {
        case "center": return .center
        case "end":    return .trailing
        default:       return .leading
        }
    }

    private func frameAlignmentFor(_ name: String?) -> Alignment {
        switch name {
        case "center": return .center
        case "end":    return .trailing
        default:       return .leading
        }
    }

    private func contentModeFor(_ name: String?) -> ContentMode {
        switch name {
        case "fit": return .fit
        default:    return .fill
        }
    }

    private func textAlignFor(_ name: String?) -> TextAlignment {
        switch name {
        case "center": return .center
        case "end":    return .trailing
        default:       return .leading
        }
    }

    private func typographyFor(_ name: String?) -> PrismaTypography.Style {
        switch name {
        case "displayLg":  return PrismaTypography.displayLg
        case "displayMd":  return PrismaTypography.displayMd
        case "displaySm":  return PrismaTypography.displaySm
        case "headlineLg": return PrismaTypography.headlineLg
        case "headlineMd": return PrismaTypography.headlineMd
        case "headlineSm": return PrismaTypography.headlineSm
        case "titleLg":    return PrismaTypography.titleLg
        case "titleMd":    return PrismaTypography.titleMd
        case "titleSm":    return PrismaTypography.titleSm
        case "bodyLg":     return PrismaTypography.bodyLg
        case "bodyMd":     return PrismaTypography.bodyMd
        case "bodySm":     return PrismaTypography.bodySm
        case "labelLg":    return PrismaTypography.labelLg
        case "labelMd":    return PrismaTypography.labelMd
        case "labelSm":    return PrismaTypography.labelSm
        default:           return PrismaTypography.bodyMd
        }
    }

    private func textColorFor(_ name: String?) -> PrismaSemanticColor {
        switch name {
        case "primary":   return PrismaSemanticColors.textPrimary
        case "secondary": return PrismaSemanticColors.textSecondary
        case "tertiary":  return PrismaSemanticColors.textTertiary
        case "accent":    return PrismaSemanticColors.accentDefault
        case "danger":    return PrismaSemanticColors.statusDangerDefault
        default:          return PrismaSemanticColors.textPrimary
        }
    }

    private func buttonVariantFor(_ name: String?) -> PrismaButtonVariant {
        switch name {
        case "primary":     return .primary
        case "secondary":   return .secondary
        case "outlined":    return .outlined
        case "ghost":       return .ghost
        case "destructive": return .destructive
        default:            return .primary
        }
    }

    private func buttonSizeFor(_ name: String?) -> PrismaButtonSize {
        switch name {
        case "sm": return .small
        case "lg": return .large
        default:   return .default
        }
    }

    private func badgeTones(_ tone: String?) -> (PrismaSemanticColor, PrismaSemanticColor) {
        switch tone {
        case "accent":  return (PrismaSemanticColors.accentDefault, PrismaSemanticColors.textOnAccent)
        case "success": return (PrismaSemanticColors.statusSuccessDefault, PrismaSemanticColors.statusSuccessOnStatus)
        case "warning": return (PrismaSemanticColors.statusWarningDefault, PrismaSemanticColors.statusWarningOnStatus)
        case "danger":  return (PrismaSemanticColors.statusDangerDefault, PrismaSemanticColors.statusDangerOnStatus)
        default:        return (PrismaSemanticColors.surfaceSunken, PrismaSemanticColors.textSecondary)
        }
    }
}

// Tabs is the only node with internal state, so it lives in its own View.
private struct SDUITabsView: View {
    let node: SDUITabs
    @State private var selected: Int = 0
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        let safeIndex = min(max(selected, 0), max(node.items.count - 1, 0))
        VStack(alignment: .leading, spacing: PrismaSpacing.sp4) {
            HStack(spacing: 4) {
                ForEach(node.items.indices, id: \.self) { i in
                    let isSelected = i == safeIndex
                    Button(action: { selected = i }) {
                        Text(node.items[i].label)
                            .font(PrismaTypography.labelMd.font)
                            .foregroundStyle((isSelected ? PrismaSemanticColors.textPrimary : PrismaSemanticColors.textSecondary).themed(scheme))
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, PrismaSpacing.sp2)
                            .background((isSelected ? PrismaSemanticColors.surfaceRaised : PrismaSemanticColors.surfaceSunken).themed(scheme))
                            .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.sm))
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(4)
            .background(PrismaSemanticColors.surfaceSunken.themed(scheme))
            .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))

            if let item = node.items.indices.contains(safeIndex) ? node.items[safeIndex] : nil {
                SDUIRender(node: item.content)
            }
        }
        .onAppear {
            // Honor the server-provided default the first time this tabs node mounts.
            selected = min(max(node.selectedIndex ?? 0, 0), max(node.items.count - 1, 0))
        }
    }
}
