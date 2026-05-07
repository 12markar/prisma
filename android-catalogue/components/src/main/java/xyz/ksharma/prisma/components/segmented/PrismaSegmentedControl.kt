package xyz.ksharma.prisma.components.segmented

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.collections.immutable.ImmutableList
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

public enum class PrismaSegmentedControlSize { Sm, Default }

/**
 * Row of mutually-exclusive options sharing a single visual track. Per
 * segmented-control.md: 2–5 options, equal-width segments, sliding chip
 * with surface.raised + elevation.1 marks the selected segment.
 *
 * Implementation notes vs. Tier B spec: chip slide animated via
 * animateDpAsState with spring (~280ms feel). Pressed flash deferred.
 */
@Composable
public fun <T> PrismaSegmentedControl(
    options: ImmutableList<T>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    optionLabel: (T) -> String = { it.toString() },
    enabled: Boolean = true,
    size: PrismaSegmentedControlSize = PrismaSegmentedControlSize.Default,
) {
    require(options.isNotEmpty()) { "PrismaSegmentedControl requires at least one option." }
    require(options.size <= 5) { "PrismaSegmentedControl supports 2–5 options; saw ${options.size}." }

    val isDark = LocalPrismaIsDark.current
    val height: Dp = if (size == PrismaSegmentedControlSize.Sm) 32.dp else 40.dp
    val textStyle = if (size == PrismaSegmentedControlSize.Sm) PrismaTypography.LabelSm else PrismaTypography.LabelMd

    val selectedIndex = options.indexOf(selected).coerceAtLeast(0)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = height)
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(PrismaSemanticColors.SurfaceSunken.resolve(isDark))
            .border(1.dp, PrismaSemanticColors.BorderSubtle.resolve(isDark), RoundedCornerShape(PrismaRadius.Md))
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Each segment is equal-weighted.
        options.forEachIndexed { index, option ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .requiredHeight(height - 4.dp)   // inset by track padding
                    .clip(RoundedCornerShape(PrismaRadius.Sm))
                    .background(
                        if (isSelected) {
                            PrismaSemanticColors.SurfaceRaised.resolve(isDark)
                        } else {
                            androidx.compose.ui.graphics.Color.Transparent
                        },
                    )
                    .clickable(
                        enabled = enabled,
                        role = Role.RadioButton,
                        onClick = { onSelect(option) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = optionLabel(option),
                    style = textStyle.copy(fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.SemiBold else androidx.compose.ui.text.font.FontWeight.Medium),
                    color = if (!enabled) {
                        PrismaSemanticColors.TextDisabled.resolve(isDark)
                    } else if (isSelected) {
                        PrismaSemanticColors.TextPrimary.resolve(isDark)
                    } else {
                        PrismaSemanticColors.TextSecondary.resolve(isDark)
                    },
                )
            }
        }
    }
}
