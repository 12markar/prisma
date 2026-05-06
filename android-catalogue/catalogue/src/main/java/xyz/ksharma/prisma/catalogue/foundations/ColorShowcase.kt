package xyz.ksharma.prisma.catalogue.foundations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaPrimitiveColors
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColor
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Color showcase — primitive ramps grouped by family, then semantic tokens
 * with both light and dark resolved values shown side-by-side.
 */
@Composable
public fun ColorShowcase() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp7),
    ) {
        SectionHeader("Primitives")
        PrimitiveRamp("Neutral",   neutralRamp())
        PrimitiveRamp("Accent",    accentRamp())
        PrimitiveRamp("Success",   successRamp())
        PrimitiveRamp("Warning",   warningRamp())
        PrimitiveRamp("Danger",    dangerRamp())
        PrimitiveRamp("Info",      infoRamp())

        SectionHeader("Semantic — surface")
        SemanticRow("surface.base",    PrismaSemanticColors.SurfaceBase)
        SemanticRow("surface.raised",  PrismaSemanticColors.SurfaceRaised)
        SemanticRow("surface.sunken",  PrismaSemanticColors.SurfaceSunken)
        SemanticRow("surface.inverse", PrismaSemanticColors.SurfaceInverse)

        SectionHeader("Semantic — text")
        SemanticRow("text.primary",    PrismaSemanticColors.TextPrimary)
        SemanticRow("text.secondary",  PrismaSemanticColors.TextSecondary)
        SemanticRow("text.tertiary",   PrismaSemanticColors.TextTertiary)
        SemanticRow("text.link",       PrismaSemanticColors.TextLink)

        SectionHeader("Semantic — accent")
        SemanticRow("accent.default",  PrismaSemanticColors.AccentDefault)
        SemanticRow("accent.hover",    PrismaSemanticColors.AccentHover)
        SemanticRow("accent.pressed",  PrismaSemanticColors.AccentPressed)
        SemanticRow("accent.subtle",   PrismaSemanticColors.AccentSubtle)
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        style = PrismaTypography.LabelSm,
        color = PrismaSemanticColors.TextTertiary.themed(),
    )
}

@Composable
private fun PrimitiveRamp(name: String, swatches: List<Pair<String, Color>>) {
    Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2)) {
        Text(
            text = name,
            style = PrismaTypography.LabelMd,
            color = PrismaSemanticColors.TextSecondary.themed(),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp1),
            modifier = Modifier.fillMaxWidth(),
        ) {
            swatches.forEach { (stop, color) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp1),
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 44.dp, height = 56.dp)
                            .clip(RoundedCornerShape(PrismaRadius.Sm))
                            .background(color),
                    )
                    Text(
                        text = stop,
                        style = PrismaTypography.LabelSm.copy(fontFamily = FontFamily.Monospace),
                        color = PrismaSemanticColors.TextTertiary.themed(),
                    )
                }
            }
        }
    }
}

@Composable
private fun SemanticRow(name: String, color: PrismaSemanticColor) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Light swatch
        Box(
            modifier = Modifier
                .size(width = 64.dp, height = 40.dp)
                .clip(RoundedCornerShape(PrismaRadius.Sm))
                .background(color.light),
        )
        // Dark swatch
        Box(
            modifier = Modifier
                .size(width = 64.dp, height = 40.dp)
                .clip(RoundedCornerShape(PrismaRadius.Sm))
                .background(color.dark),
        )
        Spacer(Modifier.width(PrismaSpacing.Sp2))
        Text(
            text = name,
            style = PrismaTypography.BodyMd.copy(fontFamily = FontFamily.Monospace),
            color = PrismaSemanticColors.TextPrimary.themed(),
        )
    }
}

private fun neutralRamp() = listOf(
    "50"  to PrismaPrimitiveColors.Neutral50,
    "100" to PrismaPrimitiveColors.Neutral100,
    "200" to PrismaPrimitiveColors.Neutral200,
    "300" to PrismaPrimitiveColors.Neutral300,
    "400" to PrismaPrimitiveColors.Neutral400,
    "500" to PrismaPrimitiveColors.Neutral500,
    "600" to PrismaPrimitiveColors.Neutral600,
    "700" to PrismaPrimitiveColors.Neutral700,
    "800" to PrismaPrimitiveColors.Neutral800,
    "900" to PrismaPrimitiveColors.Neutral900,
    "950" to PrismaPrimitiveColors.Neutral950,
)

private fun accentRamp() = listOf(
    "50"  to PrismaPrimitiveColors.Accent50,
    "100" to PrismaPrimitiveColors.Accent100,
    "200" to PrismaPrimitiveColors.Accent200,
    "300" to PrismaPrimitiveColors.Accent300,
    "400" to PrismaPrimitiveColors.Accent400,
    "500" to PrismaPrimitiveColors.Accent500,
    "600" to PrismaPrimitiveColors.Accent600,
    "700" to PrismaPrimitiveColors.Accent700,
    "800" to PrismaPrimitiveColors.Accent800,
    "900" to PrismaPrimitiveColors.Accent900,
    "950" to PrismaPrimitiveColors.Accent950,
)

private fun successRamp() = listOf(
    "50"  to PrismaPrimitiveColors.Success50,
    "100" to PrismaPrimitiveColors.Success100,
    "200" to PrismaPrimitiveColors.Success200,
    "300" to PrismaPrimitiveColors.Success300,
    "400" to PrismaPrimitiveColors.Success400,
    "500" to PrismaPrimitiveColors.Success500,
    "600" to PrismaPrimitiveColors.Success600,
    "700" to PrismaPrimitiveColors.Success700,
    "800" to PrismaPrimitiveColors.Success800,
    "900" to PrismaPrimitiveColors.Success900,
    "950" to PrismaPrimitiveColors.Success950,
)

private fun warningRamp() = listOf(
    "50"  to PrismaPrimitiveColors.Warning50,
    "100" to PrismaPrimitiveColors.Warning100,
    "200" to PrismaPrimitiveColors.Warning200,
    "300" to PrismaPrimitiveColors.Warning300,
    "400" to PrismaPrimitiveColors.Warning400,
    "500" to PrismaPrimitiveColors.Warning500,
    "600" to PrismaPrimitiveColors.Warning600,
    "700" to PrismaPrimitiveColors.Warning700,
    "800" to PrismaPrimitiveColors.Warning800,
    "900" to PrismaPrimitiveColors.Warning900,
    "950" to PrismaPrimitiveColors.Warning950,
)

private fun dangerRamp() = listOf(
    "50"  to PrismaPrimitiveColors.Danger50,
    "100" to PrismaPrimitiveColors.Danger100,
    "200" to PrismaPrimitiveColors.Danger200,
    "300" to PrismaPrimitiveColors.Danger300,
    "400" to PrismaPrimitiveColors.Danger400,
    "500" to PrismaPrimitiveColors.Danger500,
    "600" to PrismaPrimitiveColors.Danger600,
    "700" to PrismaPrimitiveColors.Danger700,
    "800" to PrismaPrimitiveColors.Danger800,
    "900" to PrismaPrimitiveColors.Danger900,
    "950" to PrismaPrimitiveColors.Danger950,
)

private fun infoRamp() = listOf(
    "50"  to PrismaPrimitiveColors.Info50,
    "100" to PrismaPrimitiveColors.Info100,
    "200" to PrismaPrimitiveColors.Info200,
    "300" to PrismaPrimitiveColors.Info300,
    "400" to PrismaPrimitiveColors.Info400,
    "500" to PrismaPrimitiveColors.Info500,
    "600" to PrismaPrimitiveColors.Info600,
    "700" to PrismaPrimitiveColors.Info700,
    "800" to PrismaPrimitiveColors.Info800,
    "900" to PrismaPrimitiveColors.Info900,
    "950" to PrismaPrimitiveColors.Info950,
)
