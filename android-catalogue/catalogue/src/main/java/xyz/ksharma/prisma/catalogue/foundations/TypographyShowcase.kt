package xyz.ksharma.prisma.catalogue.foundations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Type specimen — every typography token rendered in real sample text with
 * its name, family, weight, size, line-height, and letter-spacing alongside.
 */
@Composable
public fun TypographyShowcase() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp7),
    ) {
        TypeRow("display.lg",  PrismaTypography.DisplayLg,  "The quick brown fox")
        TypeRow("display.md",  PrismaTypography.DisplayMd,  "The quick brown fox")
        TypeRow("display.sm",  PrismaTypography.DisplaySm,  "The quick brown fox")
        TypeRow("headline.lg", PrismaTypography.HeadlineLg, "Confident geometric sans")
        TypeRow("headline.md", PrismaTypography.HeadlineMd, "Confident geometric sans")
        TypeRow("headline.sm", PrismaTypography.HeadlineSm, "Confident geometric sans")
        TypeRow("title.lg",    PrismaTypography.TitleLg,    "Section heading")
        TypeRow("title.md",    PrismaTypography.TitleMd,    "Section heading")
        TypeRow("title.sm",    PrismaTypography.TitleSm,    "Section heading")
        TypeRow("body.lg",     PrismaTypography.BodyLg,     "Considered, restrained, used sparingly. Color marks state and intent — not decoration.")
        TypeRow("body.md",     PrismaTypography.BodyMd,     "Considered, restrained, used sparingly. Color marks state and intent — not decoration.")
        TypeRow("body.sm",     PrismaTypography.BodySm,     "Considered, restrained, used sparingly. Color marks state and intent — not decoration.")
        TypeRow("label.lg",    PrismaTypography.LabelLg,    "ACTION LABEL")
        TypeRow("label.md",    PrismaTypography.LabelMd,    "ACTION LABEL")
        TypeRow("label.sm",    PrismaTypography.LabelSm,    "ACTION LABEL")
        TypeRow("code.md",     PrismaTypography.CodeMd,     "Color(0xFFC66524) // accent.500")
        TypeRow("code.sm",     PrismaTypography.CodeSm,     "Color(0xFFC66524) // accent.500")
    }
}

@Composable
private fun TypeRow(
    name: String,
    style: TextStyle,
    sample: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
    ) {
        // Token meta — small label above the specimen.
        Text(
            text = "$name  •  ${style.fontSize.value.toInt()}/${style.lineHeight.value.toInt()}  •  ${style.fontWeight.weightLabel()}  •  ${style.letterSpacing.value.formatTracking()}",
            style = PrismaTypography.LabelSm.copy(fontFamily = FontFamily.Monospace),
            color = PrismaSemanticColors.TextTertiary.themed(),
        )
        Text(
            text = sample,
            style = style,
            color = PrismaSemanticColors.TextPrimary.themed(),
            modifier = Modifier.padding(top = PrismaSpacing.Sp1),
        )
    }
}

private fun FontWeight?.weightLabel(): String = when (this?.weight) {
    null -> "—"
    400 -> "regular"
    500 -> "medium"
    600 -> "semibold"
    700 -> "bold"
    else -> "w${this.weight}"
}

private fun Float.formatTracking(): String =
    if (this == 0f) "0" else "${(this * 100).toInt() / 100f}sp"
