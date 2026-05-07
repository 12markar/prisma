package xyz.ksharma.prisma.components.stepper

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

@Composable
public fun PrismaStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    range: IntRange = 0..99,
    step: Int = 1,
    enabled: Boolean = true,
) {
    val isDark = LocalPrismaIsDark.current
    Row(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(PrismaSemanticColors.SurfaceRaised.resolve(isDark))
            .border(1.dp, PrismaSemanticColors.BorderSubtle.resolve(isDark), RoundedCornerShape(PrismaRadius.Md)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepButton(
            iconRes = PrismaIcons.Minus,
            enabled = enabled && value > range.first,
            onClick = { onValueChange((value - step).coerceAtLeast(range.first)) },
        )
        Box(modifier = Modifier.widthIn(min = 48.dp).padding(horizontal = PrismaSpacing.Sp2), contentAlignment = Alignment.Center) {
            Text(
                text = value.toString(),
                style = PrismaTypography.LabelLg,
                color = PrismaSemanticColors.TextPrimary.resolve(isDark),
            )
        }
        StepButton(
            iconRes = PrismaIcons.Plus,
            enabled = enabled && value < range.last,
            onClick = { onValueChange((value + step).coerceAtMost(range.last)) },
        )
    }
}

@Composable
private fun StepButton(iconRes: Int, enabled: Boolean, onClick: () -> Unit) {
    val isDark = LocalPrismaIsDark.current
    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = if (enabled) PrismaSemanticColors.TextPrimary.resolve(isDark)
                   else PrismaSemanticColors.TextDisabled.resolve(isDark),
            modifier = Modifier.size(18.dp),
        )
    }
}
