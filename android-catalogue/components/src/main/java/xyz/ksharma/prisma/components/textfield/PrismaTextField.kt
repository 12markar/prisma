package xyz.ksharma.prisma.components.textfield

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaMotion
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColor
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

public enum class PrismaTextFieldVariant { Outlined, Filled }

public enum class PrismaTextFieldSize { Sm, Md, Lg }

private val MinTouchTarget: Dp = 48.dp

/**
 * Prisma's canonical text input. Implements the textfield.md contract:
 *
 *   - Outlined / Filled variants
 *   - Sm / Md / Lg sizes (per spec — heights 36/44/52, body sm/md/lg)
 *   - States: default, focused, error, disabled, read-only
 *   - Slots: leadingIcon, trailingIcon, label (above), helper / error
 *     (below), optional `counter` / `maxCount`
 *   - secureTextEntry password masking
 *
 * Built on `BasicTextField` so the visual decoration is fully Prisma-token-
 * driven. M3 selection colours are inherited via LocalTextSelectionColors.
 */
@Composable
public fun PrismaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    variant: PrismaTextFieldVariant = PrismaTextFieldVariant.Outlined,
    size: PrismaTextFieldSize = PrismaTextFieldSize.Md,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    secureTextEntry: Boolean = false,
    counter: Int? = null,
    maxCount: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
) {
    val isError = errorText != null
    val isDark = LocalPrismaIsDark.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val sizeSpec = sizeSpecFor(size)
    val palette = paletteFor(variant, enabled, readOnly, isError, isFocused)

    val animatedBorder by animateColorAsState(
        targetValue = palette.border.resolve(isDark),
        animationSpec = tween(durationMillis = PrismaMotion.Duration.Fast),
        label = "border",
    )

    val labelColor = palette.label.resolve(isDark)
    val containerFill = palette.fill.resolve(isDark)
    val textColor = if (enabled) PrismaSemanticColors.TextPrimary.resolve(isDark) else PrismaSemanticColors.TextDisabled.resolve(isDark)
    val placeholderColor = PrismaSemanticColors.TextTertiary.resolve(isDark)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp1),
    ) {
        // Label row.
        if (label != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = label,
                    style = PrismaTypography.LabelMd,
                    color = labelColor,
                )
            }
        }

        // Input box.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = sizeSpec.height.coerceAtLeast(MinTouchTarget))
                .clip(RoundedCornerShape(PrismaRadius.Md))
                .background(containerFill)
                .border(
                    width = if (isError || isFocused) 2.dp else 1.dp,
                    color = animatedBorder,
                    shape = RoundedCornerShape(PrismaRadius.Md),
                )
                .padding(horizontal = sizeSpec.horizontalPadding, vertical = sizeSpec.verticalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
        ) {
            if (leadingIcon != null) {
                Box(
                    modifier = Modifier.size(sizeSpec.iconSize),
                    contentAlignment = Alignment.Center,
                ) { leadingIcon() }
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        style = sizeSpec.textStyle,
                        color = placeholderColor,
                    )
                }
                CompositionLocalProvider(
                    LocalTextSelectionColors provides TextSelectionColors(
                        handleColor = PrismaSemanticColors.AccentDefault.resolve(isDark),
                        backgroundColor = PrismaSemanticColors.AccentSubtle.resolve(isDark),
                    ),
                ) {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enabled,
                        readOnly = readOnly,
                        textStyle = sizeSpec.textStyle.copy(color = textColor),
                        cursorBrush = SolidColor(PrismaSemanticColors.AccentDefault.resolve(isDark)),
                        visualTransformation = if (secureTextEntry) PasswordVisualTransformation() else VisualTransformation.None,
                        keyboardOptions = keyboardOptions,
                        singleLine = singleLine,
                        interactionSource = interactionSource,
                    )
                }
            }

            if (trailingIcon != null) {
                Box(
                    modifier = Modifier.size(sizeSpec.iconSize),
                    contentAlignment = Alignment.Center,
                ) { trailingIcon() }
            }
        }

        // Helper / error / counter row.
        val helperVisible = errorText != null || helperText != null || (counter != null)
        if (helperVisible) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = errorText ?: helperText ?: "",
                    style = PrismaTypography.BodySm,
                    color = if (isError) PrismaSemanticColors.StatusDangerDefault.resolve(isDark)
                            else PrismaSemanticColors.TextTertiary.resolve(isDark),
                )
                if (counter != null) {
                    val countText = if (maxCount != null) "$counter/$maxCount" else counter.toString()
                    Text(
                        text = countText,
                        style = PrismaTypography.BodySm,
                        color = PrismaSemanticColors.TextTertiary.resolve(isDark),
                    )
                }
            }
        }
    }
}

// region — token mapping

private data class TextFieldPalette(
    val border: PrismaSemanticColor,
    val fill: PrismaSemanticColor,
    val label: PrismaSemanticColor,
)

private val transparentSemantic = PrismaSemanticColor(light = Color.Transparent, dark = Color.Transparent)

private fun paletteFor(
    variant: PrismaTextFieldVariant,
    enabled: Boolean,
    readOnly: Boolean,
    isError: Boolean,
    isFocused: Boolean,
): TextFieldPalette {
    val fill = if (variant == PrismaTextFieldVariant.Filled || !enabled || readOnly) {
        PrismaSemanticColors.SurfaceSunken
    } else {
        transparentSemantic
    }

    val border = when {
        !enabled || readOnly -> PrismaSemanticColors.BorderSubtle
        isError -> PrismaSemanticColors.StatusDangerDefault
        isFocused -> PrismaSemanticColors.BorderFocus
        else -> PrismaSemanticColors.BorderDefault
    }

    val label = when {
        !enabled -> PrismaSemanticColors.TextDisabled
        isError -> PrismaSemanticColors.StatusDangerDefault
        isFocused -> PrismaSemanticColors.TextPrimary
        else -> PrismaSemanticColors.TextSecondary
    }

    return TextFieldPalette(border = border, fill = fill, label = label)
}

private data class TextFieldSizeSpec(
    val height: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val iconSize: Dp,
    val textStyle: androidx.compose.ui.text.TextStyle,
)

private fun sizeSpecFor(size: PrismaTextFieldSize): TextFieldSizeSpec = when (size) {
    PrismaTextFieldSize.Sm -> TextFieldSizeSpec(
        height = 36.dp,
        horizontalPadding = PrismaSpacing.Sp3,
        verticalPadding = PrismaSpacing.Sp2,
        iconSize = 16.dp,
        textStyle = PrismaTypography.BodySm,
    )
    PrismaTextFieldSize.Md -> TextFieldSizeSpec(
        height = 44.dp,
        horizontalPadding = PrismaSpacing.Sp4,
        verticalPadding = PrismaSpacing.Sp3,
        iconSize = 18.dp,
        textStyle = PrismaTypography.BodyMd,
    )
    PrismaTextFieldSize.Lg -> TextFieldSizeSpec(
        height = 52.dp,
        horizontalPadding = PrismaSpacing.Sp4,
        verticalPadding = PrismaSpacing.Sp4,
        iconSize = 20.dp,
        textStyle = PrismaTypography.BodyLg,
    )
}

// endregion
