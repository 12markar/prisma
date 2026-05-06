package xyz.ksharma.prisma.tokens

import androidx.compose.ui.text.font.FontFamily

/**
 * Font family registration for Prisma. Hand-written companion to the generated
 * PrismaTokens.kt — co-located in the same package so the generated typography
 * tokens reference [Sans]/[Mono] without needing an import.
 *
 * Phase 0 fallback: until Instrument Sans + JetBrains Mono `.ttf` files are
 * dropped into `design-system/fonts/` and `npm run copy-fonts` is run, we use
 * platform fallbacks. Once font resources land in `core-ui/src/main/res/font/`,
 * replace the bodies below — the generated code does not need to change.
 */
public object PrismaFonts {

    public val Sans: FontFamily = FontFamily.SansSerif
    // Once fonts are present in res/font/, replace with:
    // public val Sans: FontFamily = FontFamily(
    //   Font(R.font.instrument_sans_regular, FontWeight.Normal),
    //   Font(R.font.instrument_sans_medium, FontWeight.Medium),
    //   Font(R.font.instrument_sans_semibold, FontWeight.SemiBold),
    //   Font(R.font.instrument_sans_bold, FontWeight.Bold),
    // )

    public val Mono: FontFamily = FontFamily.Monospace
}
