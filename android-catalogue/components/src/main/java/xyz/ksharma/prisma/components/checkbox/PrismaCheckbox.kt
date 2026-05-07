package xyz.ksharma.prisma.components.checkbox

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColor
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Binary checkbox. Use [PrismaTriStateCheckbox] for parent-selection
 * indeterminate states.
 */
@Composable
public fun PrismaCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    label: String? = null,
    helperText: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
) {
    PrismaTriStateCheckbox(
        state = if (checked) ToggleableState.On else ToggleableState.Off,
        onClick = if (onCheckedChange != null) {
            { onCheckedChange(!checked) }
        } else null,
        modifier = modifier,
        label = label,
        helperText = helperText,
        enabled = enabled,
        isError = isError,
    )
}

/**
 * Tri-state checkbox per checkbox.md spec — supports unchecked / checked /
 * indeterminate. The glyph is a check (PrismaIcons.Check) when On, a
 * horizontal bar when Indeterminate, and absent when Off.
 *
 * The entire row is the hit target (label + helper inclusive); 44dp min
 * touch target enforced via defaultMinSize.
 */
@Composable
public fun PrismaTriStateCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    label: String? = null,
    helperText: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
) {
    val isDark = LocalPrismaIsDark.current
    val palette = paletteFor(state, enabled, isError)

    val rowModifier = if (onClick != null) {
        modifier.triStateToggleable(
            state = state,
            onClick = onClick,
            enabled = enabled,
            role = Role.Checkbox,
        )
    } else {
        modifier
    }

    Row(
        modifier = rowModifier
            .defaultMinSize(minHeight = 44.dp)
            .padding(vertical = PrismaSpacing.Sp1),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
    ) {
        // 20×20 box, padded down 2dp so it aligns with the first line cap of the label.
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
                .clip(RoundedCornerShape(PrismaRadius.Sm))
                .background(palette.fill.resolve(isDark))
                .let { mod ->
                    val borderColor = palette.border?.resolve(isDark)
                    if (borderColor != null) {
                        mod.border(2.dp, borderColor, RoundedCornerShape(PrismaRadius.Sm))
                    } else {
                        mod
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                ToggleableState.On -> Icon(
                    painter = painterResource(PrismaIcons.Check),
                    contentDescription = null,
                    tint = palette.glyph.resolve(isDark),
                    modifier = Modifier.size(14.dp),
                )
                ToggleableState.Indeterminate -> Box(
                    modifier = Modifier
                        .width(10.dp)
                        .height(2.dp)
                        .background(palette.glyph.resolve(isDark), RoundedCornerShape(1.dp)),
                )
                ToggleableState.Off -> Unit
            }
        }

        if (label != null) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = label,
                    style = PrismaTypography.BodyMd,
                    color = if (enabled) {
                        PrismaSemanticColors.TextPrimary.resolve(isDark)
                    } else {
                        PrismaSemanticColors.TextDisabled.resolve(isDark)
                    },
                )
                if (helperText != null) {
                    Text(
                        text = helperText,
                        style = PrismaTypography.BodySm,
                        color = PrismaSemanticColors.TextTertiary.resolve(isDark),
                    )
                }
            }
        }
    }
}

// region — token mapping

private data class CheckboxPalette(
    val fill: PrismaSemanticColor,
    val border: PrismaSemanticColor?,
    val glyph: PrismaSemanticColor,
)

private val transparentSemantic = PrismaSemanticColor(light = Color.Transparent, dark = Color.Transparent)

private fun paletteFor(state: ToggleableState, enabled: Boolean, isError: Boolean): CheckboxPalette {
    if (!enabled) {
        return when (state) {
            ToggleableState.Off -> CheckboxPalette(
                fill = PrismaSemanticColors.SurfaceSunken,
                border = PrismaSemanticColors.BorderSubtle,
                glyph = PrismaSemanticColors.TextDisabled,
            )
            else -> CheckboxPalette(
                fill = PrismaSemanticColors.TextDisabled,
                border = null,
                glyph = PrismaSemanticColors.SurfaceBase,
            )
        }
    }

    if (isError && state == ToggleableState.Off) {
        return CheckboxPalette(
            fill = transparentSemantic,
            border = PrismaSemanticColors.StatusDangerDefault,
            glyph = PrismaSemanticColors.StatusDangerDefault,
        )
    }

    return when (state) {
        ToggleableState.Off -> CheckboxPalette(
            fill = transparentSemantic,
            border = PrismaSemanticColors.BorderStrong,
            glyph = PrismaSemanticColors.TextPrimary,
        )
        ToggleableState.On,
        ToggleableState.Indeterminate -> CheckboxPalette(
            fill = PrismaSemanticColors.AccentDefault,
            border = null,
            glyph = PrismaSemanticColors.TextOnAccent,
        )
    }
}

// endregion
