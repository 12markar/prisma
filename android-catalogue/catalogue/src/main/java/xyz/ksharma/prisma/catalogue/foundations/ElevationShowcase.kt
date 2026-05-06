package xyz.ksharma.prisma.catalogue.foundations

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaElevation
import xyz.ksharma.prisma.tokens.PrismaElevations
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Each elevation level rendered as a card. Light + dark side-by-side so the
 * dual-mode token (light = drop shadow; dark = inset glow + border) is visible.
 *
 * Note: Compose's Modifier.shadow renders a single elevation, not the multi-layer
 * spec from PrismaElevations. We approximate with the largest layer's blur. A
 * real Modifier.prismaShadow(level) impl can render the full layered spec —
 * tracked as a Phase 1 polish task.
 */
@Composable
public fun ElevationShowcase() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
    ) {
        ElevationRow("elevation.0", PrismaElevations.Level0)
        ElevationRow("elevation.1", PrismaElevations.Level1)
        ElevationRow("elevation.2", PrismaElevations.Level2)
        ElevationRow("elevation.3", PrismaElevations.Level3)
        ElevationRow("elevation.4", PrismaElevations.Level4)
        ElevationRow("elevation.5", PrismaElevations.Level5)
    }
}

@Composable
private fun ElevationRow(name: String, level: PrismaElevation) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            style = PrismaTypography.LabelMd.copy(fontFamily = FontFamily.Monospace),
            color = PrismaSemanticColors.TextSecondary.themed(),
            modifier = Modifier.padding(end = PrismaSpacing.Sp2),
        )

        // Light card — drop shadow approximation via Modifier.shadow.
        ElevationCard(
            modifier = Modifier.size(120.dp, 80.dp),
            elevation = level.light.lastOrNull()?.blur ?: 0.dp,
            background = Color(0xFFFFFFFF),
            border = false,
        )

        // Dark card — inset glow approximated as a 1px border (proper inset
        // rendering requires a custom drawBehind; that is Phase 1 work).
        ElevationCard(
            modifier = Modifier.size(120.dp, 80.dp),
            elevation = 0.dp,
            background = Color(0xFF2A2620),
            border = level.dark.any { it.inset },
        )
    }
}

@Composable
private fun ElevationCard(
    modifier: Modifier,
    elevation: androidx.compose.ui.unit.Dp,
    background: Color,
    border: Boolean,
) {
    val shape = RoundedCornerShape(PrismaRadius.Md)
    Box(
        modifier = modifier
            .shadow(elevation, shape)
            .clip(shape)
            .background(background)
            .then(if (border) Modifier.border(1.dp, SolidColor(Color(0x14FFFFFF)), shape) else Modifier),
    )
}
