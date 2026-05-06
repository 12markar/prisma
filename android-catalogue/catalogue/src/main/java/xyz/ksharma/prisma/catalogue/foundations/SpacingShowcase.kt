package xyz.ksharma.prisma.catalogue.foundations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

/** Visualises every spacing token as a horizontal bar at its actual width. */
@Composable
public fun SpacingShowcase() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
    ) {
        SpacingRow("spacing.0",  PrismaSpacing.Sp0)
        SpacingRow("spacing.1",  PrismaSpacing.Sp1)
        SpacingRow("spacing.2",  PrismaSpacing.Sp2)
        SpacingRow("spacing.3",  PrismaSpacing.Sp3)
        SpacingRow("spacing.4",  PrismaSpacing.Sp4)
        SpacingRow("spacing.5",  PrismaSpacing.Sp5)
        SpacingRow("spacing.6",  PrismaSpacing.Sp6)
        SpacingRow("spacing.7",  PrismaSpacing.Sp7)
        SpacingRow("spacing.8",  PrismaSpacing.Sp8)
        SpacingRow("spacing.9",  PrismaSpacing.Sp9)
        SpacingRow("spacing.10", PrismaSpacing.Sp10)
        SpacingRow("spacing.11", PrismaSpacing.Sp11)
        SpacingRow("spacing.12", PrismaSpacing.Sp12)
    }
}

@Composable
private fun SpacingRow(name: String, value: Dp) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            style = PrismaTypography.LabelSm.copy(fontFamily = FontFamily.Monospace),
            color = PrismaSemanticColors.TextTertiary.themed(),
            modifier = Modifier.width(72.dp),
        )
        Box(
            modifier = Modifier
                .height(20.dp)
                .width(if (value.value < 2f) 2.dp else value)
                .clip(RoundedCornerShape(PrismaRadius.Sm))
                .background(PrismaSemanticColors.AccentDefault.themed()),
        )
        Text(
            text = "${value.value.toInt()}dp",
            style = PrismaTypography.LabelSm.copy(fontFamily = FontFamily.Monospace),
            color = PrismaSemanticColors.TextSecondary.themed(),
            modifier = Modifier
                .width(56.dp)
                .padding(start = PrismaSpacing.Sp2),
        )
    }
}
