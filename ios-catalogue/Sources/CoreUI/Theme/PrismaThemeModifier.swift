import SwiftUI

/// Apply Prisma theming to a view subtree.
///
/// - When `isDarkOverride` is `nil` (default), the view follows the system
///   `colorScheme` — Prisma semantic colours resolve via `Environment(\.colorScheme)`.
/// - When set explicitly to `true`/`false`, `preferredColorScheme` is used so
///   the entire subtree (including system controls) reflects the override —
///   and the catalogue's theme toggle works without per-component plumbing.
///
/// SwiftUI equivalent of Compose's `LocalPrismaIsDark` + `PrismaTheme`.
public extension View {
    func prismaTheme(isDarkOverride: Bool? = nil) -> some View {
        modifier(PrismaThemeModifier(isDarkOverride: isDarkOverride))
    }
}

private struct PrismaThemeModifier: ViewModifier {
    let isDarkOverride: Bool?

    func body(content: Content) -> some View {
        if let isDark = isDarkOverride {
            content.preferredColorScheme(isDark ? .dark : .light)
        } else {
            content
        }
    }
}

/// Convenience for resolving a Prisma semantic colour from inside a view.
/// Use at call sites:
///
///     @Environment(\.colorScheme) private var scheme
///     ...
///     .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
public extension PrismaSemanticColor {
    func themed(_ scheme: ColorScheme) -> Color {
        resolve(scheme)
    }
}
