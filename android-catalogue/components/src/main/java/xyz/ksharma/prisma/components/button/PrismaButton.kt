package xyz.ksharma.prisma.components.button

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaMotion
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColor
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

private val MinTouchTarget: Dp = 48.dp

public enum class PrismaButtonVariant { Primary, Secondary, Outlined, Ghost, Icon, Destructive }

public enum class PrismaButtonSize { Sm, Default, Lg }

/**
 * Prisma's canonical pressable control. Implements the full button.md contract:
 * 6 variants × 3 sizes × default/pressed/disabled/loading states.
 *
 * For the icon-only variant ([PrismaButtonVariant.Icon]), [contentDescription]
 * is required — the visible label is empty so screen readers need an explicit
 * label. The check is enforced at runtime to fail fast in debug builds.
 */
@Composable
public fun PrismaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PrismaButtonVariant = PrismaButtonVariant.Primary,
    size: PrismaButtonSize = PrismaButtonSize.Default,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    contentDescription: String? = null,
) {
    require(variant != PrismaButtonVariant.Icon || contentDescription != null) {
        "PrismaButton(variant = Icon) requires a contentDescription for screen-reader accessibility."
    }

    val haptics = LocalHapticFeedback.current
    val isDark = LocalPrismaIsDark.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val sizeSpec = sizeSpec(size)
    val colors = colorsFor(variant, enabled, loading, pressed)

    val animatedBg by animateColorAsState(
        targetValue = colors.background.resolve(isDark),
        animationSpec = tween(durationMillis = PrismaMotion.Duration.Fast),
        label = "bg",
    )
    val labelColor: Color = colors.label.resolve(isDark)
    val borderColor: Color? = colors.border?.resolve(isDark)
    val shape = RoundedCornerShape(PrismaRadius.Md)

    val click: () -> Unit = remember(onClick, haptics) {
        {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        }
    }

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = MinTouchTarget)
            .heightIn(min = sizeSpec.height)
            .clip(shape)
            .background(animatedBg)
            .let { if (borderColor != null) it.border(1.dp, borderColor, shape) else it }
            .clickable(
                enabled = enabled && !loading,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = click,
            )
            .padding(horizontal = sizeSpec.horizontalPadding)
            .let { mod ->
                if (variant == PrismaButtonVariant.Icon && contentDescription != null) {
                    mod.semantics { this.contentDescription = contentDescription }
                } else {
                    mod
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        // Centered when the caller stretches the button via fillMaxWidth —
        // otherwise content sits at the start, which looks like a tiny label
        // hugging the leading edge of a wide pill.
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2, Alignment.CenterHorizontally),
    ) {
        CompositionLocalProvider(LocalContentColor provides labelColor) {
            // Loading spinner replaces leading icon when loading.
            when {
                loading -> CircularProgressIndicator(
                    modifier = Modifier.size(sizeSpec.iconSize),
                    color = labelColor,
                    strokeWidth = 2.dp,
                )
                leadingIcon != null -> Box(
                    modifier = Modifier.size(sizeSpec.iconSize),
                    contentAlignment = Alignment.Center,
                ) { leadingIcon() }
            }

            if (variant != PrismaButtonVariant.Icon) {
                Text(text = text, style = sizeSpec.textStyle, color = labelColor)
            }

            if (trailingIcon != null && !loading) {
                Box(
                    modifier = Modifier.size(sizeSpec.iconSize),
                    contentAlignment = Alignment.Center,
                ) { trailingIcon() }
            }
        }
    }
}

// region — variant + state token mapping

private data class ButtonColors(
    val background: PrismaSemanticColor,
    val label: PrismaSemanticColor,
    val border: PrismaSemanticColor?,
)

@Composable
private fun colorsFor(
    variant: PrismaButtonVariant,
    enabled: Boolean,
    loading: Boolean,
    pressed: Boolean,
): ButtonColors {
    if (!enabled) {
        return ButtonColors(
            background = PrismaSemanticColors.SurfaceSunken,
            label = PrismaSemanticColors.TextDisabled,
            border = if (variant == PrismaButtonVariant.Outlined || variant == PrismaButtonVariant.Secondary) {
                PrismaSemanticColors.BorderSubtle
            } else {
                null
            },
        )
    }

    return when (variant) {
        PrismaButtonVariant.Primary -> ButtonColors(
            background = if (pressed) PrismaSemanticColors.AccentPressed else PrismaSemanticColors.AccentDefault,
            label = PrismaSemanticColors.TextOnAccent,
            border = null,
        )
        PrismaButtonVariant.Destructive -> ButtonColors(
            background = if (pressed) PrismaSemanticColors.StatusDangerDefault else PrismaSemanticColors.StatusDangerDefault,
            label = PrismaSemanticColors.StatusDangerOnStatus,
            border = null,
        )
        PrismaButtonVariant.Secondary -> ButtonColors(
            background = if (pressed) PrismaSemanticColors.SurfaceSunken else PrismaSemanticColors.SurfaceRaised,
            label = PrismaSemanticColors.TextPrimary,
            border = PrismaSemanticColors.BorderDefault,
        )
        PrismaButtonVariant.Outlined -> ButtonColors(
            background = if (pressed) PrismaSemanticColors.SurfaceSunken else transparentSemantic,
            label = PrismaSemanticColors.TextPrimary,
            border = PrismaSemanticColors.BorderDefault,
        )
        PrismaButtonVariant.Ghost,
        PrismaButtonVariant.Icon -> ButtonColors(
            background = if (pressed) PrismaSemanticColors.SurfaceSunken else transparentSemantic,
            label = PrismaSemanticColors.TextPrimary,
            border = null,
        )
    }.let { base -> if (loading) base.copy(label = base.label) else base }
}

/** A semantic colour that resolves to fully-transparent in both modes — used for ghost/outlined defaults. */
private val transparentSemantic = PrismaSemanticColor(
    light = Color.Transparent,
    dark = Color.Transparent,
)

private data class ButtonSizeSpec(
    val height: Dp,
    val horizontalPadding: Dp,
    val iconSize: Dp,
    val textStyle: TextStyle,
)

@Composable
private fun sizeSpec(size: PrismaButtonSize): ButtonSizeSpec = when (size) {
    PrismaButtonSize.Sm -> ButtonSizeSpec(
        height = 32.dp,
        horizontalPadding = PrismaSpacing.Sp3,
        iconSize = 16.dp,
        textStyle = PrismaTypography.LabelMd,
    )
    PrismaButtonSize.Default -> ButtonSizeSpec(
        height = 40.dp,
        horizontalPadding = PrismaSpacing.Sp4,
        iconSize = 18.dp,
        textStyle = PrismaTypography.LabelLg,
    )
    PrismaButtonSize.Lg -> ButtonSizeSpec(
        height = 48.dp,
        horizontalPadding = PrismaSpacing.Sp5,
        iconSize = 18.dp,
        textStyle = PrismaTypography.LabelLg,
    )
}

// endregion
