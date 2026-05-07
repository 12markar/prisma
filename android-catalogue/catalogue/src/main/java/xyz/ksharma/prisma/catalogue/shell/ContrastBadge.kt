package xyz.ksharma.prisma.catalogue.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography
import kotlin.math.pow

/**
 * Live WCAG contrast indicator. Computes the actual ratio between the
 * active theme's primary text and base surface, then surfaces it as a
 * coloured chip — green at ≥ 4.5:1, amber 3-4.5:1, red below.
 *
 * Recomputes when the theme flips so the user can sanity-check overrides
 * (e.g. an in-progress dark theme) at a glance.
 */
@Composable
internal fun ContrastBadge(modifier: Modifier = Modifier) {
    val isDark = LocalPrismaIsDark.current
    val text = PrismaSemanticColors.TextPrimary.resolve(isDark)
    val surface = PrismaSemanticColors.SurfaceBase.resolve(isDark)
    val ratio = remember(isDark, text, surface) { contrastRatio(text, surface) }
    val (dotColor, label) = when {
        ratio >= 7.0 -> PrismaSemanticColors.StatusSuccessDefault.resolve(isDark) to "AAA"
        ratio >= 4.5 -> PrismaSemanticColors.StatusSuccessDefault.resolve(isDark) to "AA"
        ratio >= 3.0 -> PrismaSemanticColors.StatusWarningDefault.resolve(isDark) to "AA-"
        else -> PrismaSemanticColors.StatusDangerDefault.resolve(isDark) to "FAIL"
    }
    val ratioStr = "%.1f:1".format(ratio)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(PrismaRadius.Full))
            .background(PrismaSemanticColors.SurfaceRaised.resolve(isDark))
            .border(
                1.dp,
                PrismaSemanticColors.BorderSubtle.resolve(isDark),
                RoundedCornerShape(PrismaRadius.Full),
            )
            .padding(horizontal = PrismaSpacing.Sp3, vertical = 6.dp)
            .semantics {
                contentDescription = "WCAG $label, contrast $ratioStr"
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor),
        )
        Text(
            text = label,
            style = PrismaTypography.LabelSm,
            color = PrismaSemanticColors.TextSecondary.resolve(isDark),
        )
        Text(
            text = ratioStr,
            style = PrismaTypography.LabelSm,
            color = PrismaSemanticColors.TextTertiary.resolve(isDark),
        )
    }
}

/** WCAG 2.1 relative luminance + contrast ratio. */
private fun contrastRatio(fg: Color, bg: Color): Double {
    val l1 = relativeLuminance(fg)
    val l2 = relativeLuminance(bg)
    val lighter = maxOf(l1, l2)
    val darker = minOf(l1, l2)
    return (lighter + 0.05) / (darker + 0.05)
}

private fun relativeLuminance(c: Color): Double {
    fun linear(channel: Float): Double {
        val v = channel.toDouble()
        return if (v <= 0.03928) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
    }
    return 0.2126 * linear(c.red) + 0.7152 * linear(c.green) + 0.0722 * linear(c.blue)
}
