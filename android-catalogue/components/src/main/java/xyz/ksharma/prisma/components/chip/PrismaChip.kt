package xyz.ksharma.prisma.components.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

public enum class PrismaChipVariant { Filter, Suggestion, Input }

@Composable
public fun PrismaChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    variant: PrismaChipVariant = PrismaChipVariant.Filter,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    val isDark = LocalPrismaIsDark.current
    val haptics = LocalHapticFeedback.current
    val onClickWithHaptic: () -> Unit = {
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        onClick()
    }
    val (bg, fg, border) = when {
        !enabled -> Triple(PrismaSemanticColors.SurfaceSunken, PrismaSemanticColors.TextDisabled, PrismaSemanticColors.BorderSubtle)
        selected -> Triple(PrismaSemanticColors.AccentSubtle, PrismaSemanticColors.AccentDefault, PrismaSemanticColors.AccentDefault)
        else -> Triple(PrismaSemanticColors.SurfaceRaised, PrismaSemanticColors.TextPrimary, PrismaSemanticColors.BorderDefault)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(PrismaRadius.Full))
            .background(bg.resolve(isDark))
            .border(1.dp, border.resolve(isDark), RoundedCornerShape(PrismaRadius.Full))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClickWithHaptic)
            .padding(horizontal = PrismaSpacing.Sp3, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
    ) {
        if (leadingIcon != null) {
            Box(modifier = Modifier.size(14.dp), contentAlignment = Alignment.Center) { leadingIcon() }
        }
        Text(
            text = label,
            style = PrismaTypography.LabelMd,
            color = fg.resolve(isDark),
        )
        if (variant == PrismaChipVariant.Input && onDismiss != null) {
            Icon(
                painter = painterResource(PrismaIcons.X),
                contentDescription = "Dismiss",
                tint = fg.resolve(isDark),
                modifier = Modifier
                    .size(14.dp)
                    .clickable(enabled = enabled, onClick = onDismiss),
            )
        }
    }
}
