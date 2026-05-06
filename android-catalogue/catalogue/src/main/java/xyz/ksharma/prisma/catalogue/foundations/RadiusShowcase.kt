package xyz.ksharma.prisma.catalogue.foundations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/** Squares with each radius applied — visual scale comparison. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun RadiusShowcase() {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
    ) {
        RadiusTile("none", PrismaRadius.None)
        RadiusTile("sm",   PrismaRadius.Sm)
        RadiusTile("md",   PrismaRadius.Md)
        RadiusTile("lg",   PrismaRadius.Lg)
        RadiusTile("xl",   PrismaRadius.Xl)
        RadiusTile("full", 48.dp) // visual cap — actual full radius is 9999dp ≈ circle
    }
}

@Composable
private fun RadiusTile(name: String, radius: Dp) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(radius))
                .background(PrismaSemanticColors.AccentDefault.themed()),
        )
        Text(
            text = "radius.$name",
            style = PrismaTypography.LabelSm.copy(fontFamily = FontFamily.Monospace),
            color = PrismaSemanticColors.TextSecondary.themed(),
        )
    }
}
