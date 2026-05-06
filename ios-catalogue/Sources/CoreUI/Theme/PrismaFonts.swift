import SwiftUI

/// Font registration for Prisma — hand-written companion to the generated
/// PrismaTokens.swift, which references `PrismaFonts.sans(size:weight:)` /
/// `PrismaFonts.mono(size:weight:)`.
///
/// Phase 0 fallback: until Instrument Sans + JetBrains Mono `.ttf` files are
/// dropped into `design-system/fonts/` and copied into a Resources/Fonts/
/// directory of an app target (with `UIAppFonts` entries in Info.plist), we
/// use platform fallbacks (system font + monospaced variant). Once font files
/// are registered, replace the bodies below — generated code does not change.
public enum PrismaFonts {

    public static func sans(size: CGFloat, weight: Font.Weight) -> Font {
        // System fallback. Once registered:
        // .custom("InstrumentSans-\(suffix(weight))", size: size)
        return Font.system(size: size, weight: weight, design: .default)
    }

    public static func mono(size: CGFloat, weight: Font.Weight) -> Font {
        // System monospaced fallback. Once registered:
        // .custom("JetBrainsMono-\(suffix(weight))", size: size)
        return Font.system(size: size, weight: weight, design: .monospaced)
    }
}
