package xyz.ksharma.prisma.components.radio

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaMotion
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Single-selection radio button. Pair multiple inside a `selectableGroup`
 * (or a custom Column with one selected at a time) to model exclusive choice.
 *
 * Anatomy: 20dp outer ring, 10dp inner dot when selected.
 */
@Composable
public fun PrismaRadio(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    label: String? = null,
    helperText: String? = null,
    enabled: Boolean = true,
) {
    val isDark = LocalPrismaIsDark.current

    val ringColor by animateColorAsState(
        targetValue = when {
            !enabled -> PrismaSemanticColors.BorderSubtle.resolve(isDark)
            selected -> PrismaSemanticColors.AccentDefault.resolve(isDark)
            else -> PrismaSemanticColors.BorderStrong.resolve(isDark)
        },
        animationSpec = tween(durationMillis = PrismaMotion.Duration.Fast),
        label = "ring",
    )

    val dotColor: Color = if (!enabled) {
        PrismaSemanticColors.TextDisabled.resolve(isDark)
    } else {
        PrismaSemanticColors.AccentDefault.resolve(isDark)
    }

    val rowModifier = if (onClick != null) {
        modifier.selectable(
            selected = selected,
            onClick = onClick,
            enabled = enabled,
            role = Role.RadioButton,
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
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
                .clip(CircleShape)
                .border(2.dp, ringColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(dotColor),
                )
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
