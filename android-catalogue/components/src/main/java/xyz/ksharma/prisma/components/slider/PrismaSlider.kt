package xyz.ksharma.prisma.components.slider

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Continuous-value selection control. Wraps M3 Slider with Prisma-tokened
 * colours; bespoke geometry per slider.md (4px track, 20px thumb, halo)
 * deferred — the M3 wrapper ships the behaviour and accessibility correctly.
 */
@Composable
public fun PrismaSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
    label: String? = null,
    showValue: Boolean = true,
    valueFormatter: (Float) -> String = { String.format("%.2f", it) },
) {
    val isDark = LocalPrismaIsDark.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp1),
    ) {
        if (label != null || showValue) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (label != null) {
                    Text(
                        text = label,
                        style = PrismaTypography.LabelMd,
                        color = if (enabled) {
                            PrismaSemanticColors.TextSecondary.resolve(isDark)
                        } else {
                            PrismaSemanticColors.TextDisabled.resolve(isDark)
                        },
                    )
                }
                if (showValue) {
                    Text(
                        text = valueFormatter(value),
                        style = PrismaTypography.LabelMd.copy(fontFamily = FontFamily.Monospace),
                        color = PrismaSemanticColors.TextPrimary.resolve(isDark),
                    )
                }
            }
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            enabled = enabled,
            colors = SliderDefaults.colors(
                thumbColor = PrismaSemanticColors.AccentDefault.resolve(isDark),
                activeTrackColor = PrismaSemanticColors.AccentDefault.resolve(isDark),
                inactiveTrackColor = PrismaSemanticColors.SurfaceSunken.resolve(isDark),
                disabledThumbColor = PrismaSemanticColors.TextDisabled.resolve(isDark),
                disabledActiveTrackColor = PrismaSemanticColors.TextDisabled.resolve(isDark),
                disabledInactiveTrackColor = PrismaSemanticColors.SurfaceSunken.resolve(isDark),
                activeTickColor = PrismaSemanticColors.TextOnAccent.resolve(isDark),
                inactiveTickColor = PrismaSemanticColors.TextTertiary.resolve(isDark),
            ),
        )
    }
}
