package xyz.ksharma.prisma.catalogue.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Inlines the relevant slice of each component's accessibility contract
 * — role, min touch target, focus/announce behaviour — directly under the
 * playground so callers see what they're committing to without leaving
 * the catalogue.
 */
@Composable
public fun A11yPanel(
    role: String,
    minTouchTarget: String,
    bullets: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(PrismaSemanticColors.SurfaceSunken.themed())
            .border(
                1.dp,
                PrismaSemanticColors.BorderSubtle.themed(),
                RoundedCornerShape(PrismaRadius.Md),
            )
            .padding(PrismaSpacing.Sp5),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4)) {
            A11yField(label = "Role", value = role)
            A11yField(label = "Min target", value = minTouchTarget)
        }
        Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2)) {
            bullets.forEach { Bullet(it) }
        }
    }
}

@Composable
private fun A11yField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label.uppercase(),
            style = PrismaTypography.LabelSm,
            color = PrismaSemanticColors.TextTertiary.themed(),
        )
        Text(
            text = value,
            style = PrismaTypography.BodyMd,
            color = PrismaSemanticColors.TextPrimary.themed(),
        )
    }
}

@Composable
private fun Bullet(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(PrismaSemanticColors.AccentDefault.themed()),
        )
        Text(
            text = text,
            style = PrismaTypography.BodySm,
            color = PrismaSemanticColors.TextSecondary.themed(),
        )
    }
}
