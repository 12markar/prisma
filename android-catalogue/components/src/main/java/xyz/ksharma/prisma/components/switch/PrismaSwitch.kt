package xyz.ksharma.prisma.components.switchctl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

@Composable
public fun PrismaSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    label: String? = null,
    helperText: String? = null,
    enabled: Boolean = true,
) {
    val isDark = LocalPrismaIsDark.current
    val haptics = LocalHapticFeedback.current
    // Wrap the user-supplied callback so every toggle fires a textual,
    // platform-appropriate "this thing changed" haptic. No-op when the
    // switch is read-only (onCheckedChange null) or disabled.
    val onChange: ((Boolean) -> Unit)? = onCheckedChange?.let { cb ->
        { checked ->
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            cb(checked)
        }
    }
    val colors = SwitchDefaults.colors(
        checkedThumbColor = PrismaSemanticColors.SurfaceBase.resolve(isDark),
        checkedTrackColor = PrismaSemanticColors.AccentDefault.resolve(isDark),
        checkedBorderColor = PrismaSemanticColors.AccentDefault.resolve(isDark),
        uncheckedThumbColor = PrismaSemanticColors.SurfaceBase.resolve(isDark),
        uncheckedTrackColor = PrismaSemanticColors.SurfaceSunken.resolve(isDark),
        uncheckedBorderColor = PrismaSemanticColors.BorderStrong.resolve(isDark),
        disabledCheckedThumbColor = PrismaSemanticColors.SurfaceBase.resolve(isDark),
        disabledCheckedTrackColor = PrismaSemanticColors.TextDisabled.resolve(isDark),
        disabledUncheckedThumbColor = PrismaSemanticColors.SurfaceBase.resolve(isDark),
        disabledUncheckedTrackColor = PrismaSemanticColors.SurfaceSunken.resolve(isDark),
        disabledUncheckedBorderColor = PrismaSemanticColors.BorderSubtle.resolve(isDark),
    )

    if (label == null) {
        Switch(checked = checked, onCheckedChange = onChange, modifier = modifier, enabled = enabled, colors = colors)
        return
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(end = PrismaSpacing.Sp4),
        ) {
            Text(
                text = label,
                style = PrismaTypography.BodyMd,
                color = if (enabled) PrismaSemanticColors.TextPrimary.resolve(isDark)
                        else PrismaSemanticColors.TextDisabled.resolve(isDark),
            )
            if (helperText != null) {
                Text(
                    text = helperText,
                    style = PrismaTypography.BodySm,
                    color = PrismaSemanticColors.TextTertiary.resolve(isDark),
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onChange, enabled = enabled, colors = colors)
    }
}
