package xyz.ksharma.prisma.components.divider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaSemanticColor
import xyz.ksharma.prisma.tokens.PrismaSemanticColors

public enum class PrismaDividerWeight { Subtle, Default, Strong }

@Composable
public fun PrismaHorizontalDivider(
    modifier: Modifier = Modifier,
    weight: PrismaDividerWeight = PrismaDividerWeight.Subtle,
    inset: Dp = 0.dp,
) {
    val isDark = LocalPrismaIsDark.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = inset)
            .height(1.dp)
            .background(weightToColor(weight).resolve(isDark)),
    )
}

@Composable
public fun PrismaVerticalDivider(
    modifier: Modifier = Modifier,
    weight: PrismaDividerWeight = PrismaDividerWeight.Subtle,
) {
    val isDark = LocalPrismaIsDark.current
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(1.dp)
            .background(weightToColor(weight).resolve(isDark)),
    )
}

private fun weightToColor(weight: PrismaDividerWeight): PrismaSemanticColor = when (weight) {
    PrismaDividerWeight.Subtle -> PrismaSemanticColors.BorderSubtle
    PrismaDividerWeight.Default -> PrismaSemanticColors.BorderDefault
    PrismaDividerWeight.Strong -> PrismaSemanticColors.BorderStrong
}
