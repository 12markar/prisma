package xyz.ksharma.prisma.components.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaSemanticColor
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

public enum class PrismaBadgeStatus { Accent, Success, Warning, Danger, Info }

@Composable
public fun PrismaCountBadge(
    count: Int,
    modifier: Modifier = Modifier,
    status: PrismaBadgeStatus = PrismaBadgeStatus.Accent,
) {
    val isDark = LocalPrismaIsDark.current
    val (bg, fg) = bgFgFor(status)
    val display = if (count > 99) "99+" else count.toString()
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
            .clip(CircleShape)
            .background(bg.resolve(isDark))
            .padding(horizontal = PrismaSpacing.Sp2, vertical = 1.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = display,
            style = PrismaTypography.LabelSm.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            ),
            color = fg.resolve(isDark),
        )
    }
}

@Composable
public fun PrismaDotBadge(
    modifier: Modifier = Modifier,
    status: PrismaBadgeStatus = PrismaBadgeStatus.Accent,
) {
    val isDark = LocalPrismaIsDark.current
    val (bg, _) = bgFgFor(status)
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(bg.resolve(isDark)),
    )
}

private fun bgFgFor(status: PrismaBadgeStatus): Pair<PrismaSemanticColor, PrismaSemanticColor> = when (status) {
    PrismaBadgeStatus.Accent -> PrismaSemanticColors.AccentDefault to PrismaSemanticColors.TextOnAccent
    PrismaBadgeStatus.Success -> PrismaSemanticColors.StatusSuccessDefault to PrismaSemanticColors.StatusSuccessOnStatus
    PrismaBadgeStatus.Warning -> PrismaSemanticColors.StatusWarningDefault to PrismaSemanticColors.StatusWarningOnStatus
    PrismaBadgeStatus.Danger -> PrismaSemanticColors.StatusDangerDefault to PrismaSemanticColors.StatusDangerOnStatus
    PrismaBadgeStatus.Info -> PrismaSemanticColors.StatusInfoDefault to PrismaSemanticColors.StatusInfoOnStatus
}
