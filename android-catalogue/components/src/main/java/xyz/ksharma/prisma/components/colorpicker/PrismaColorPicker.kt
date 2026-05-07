package xyz.ksharma.prisma.components.colorpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.components.slider.PrismaSlider
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Minimal RGB colour picker — three sliders + preview swatch + hex display.
 * Production uses HSL / HSV pickers; this is the catalogue-friendly minimum.
 */
@Composable
public fun PrismaColorPicker(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = LocalPrismaIsDark.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4)) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(PrismaRadius.Md))
                    .background(color)
                    .border(1.dp, PrismaSemanticColors.BorderSubtle.resolve(isDark), RoundedCornerShape(PrismaRadius.Md)),
            )
            Text(
                text = color.toHexString(),
                style = PrismaTypography.CodeMd,
                color = PrismaSemanticColors.TextPrimary.resolve(isDark),
            )
        }
        PrismaSlider(
            value = color.red,
            onValueChange = { onColorChange(color.copy(red = it)) },
            label = "Red",
            valueFormatter = { "%.0f".format(it * 255f) },
        )
        PrismaSlider(
            value = color.green,
            onValueChange = { onColorChange(color.copy(green = it)) },
            label = "Green",
            valueFormatter = { "%.0f".format(it * 255f) },
        )
        PrismaSlider(
            value = color.blue,
            onValueChange = { onColorChange(color.copy(blue = it)) },
            label = "Blue",
            valueFormatter = { "%.0f".format(it * 255f) },
        )
    }
}

private fun Color.toHexString(): String {
    val r = (red * 255).toInt().coerceIn(0, 255)
    val g = (green * 255).toInt().coerceIn(0, 255)
    val b = (blue * 255).toInt().coerceIn(0, 255)
    return "#%02X%02X%02X".format(r, g, b)
}
