package xyz.ksharma.prisma.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing

public enum class PrismaCardVariant { Elevated, Outlined, Filled }

@Composable
public fun PrismaCard(
    modifier: Modifier = Modifier,
    variant: PrismaCardVariant = PrismaCardVariant.Outlined,
    onClick: (() -> Unit)? = null,
    contentPadding: androidx.compose.ui.unit.Dp = PrismaSpacing.Sp5,
    content: @Composable () -> Unit,
) {
    val isDark = LocalPrismaIsDark.current
    val shape = RoundedCornerShape(PrismaRadius.Lg)
    val bg = when (variant) {
        PrismaCardVariant.Elevated -> PrismaSemanticColors.SurfaceRaised.resolve(isDark)
        PrismaCardVariant.Outlined -> PrismaSemanticColors.SurfaceBase.resolve(isDark)
        PrismaCardVariant.Filled -> PrismaSemanticColors.SurfaceSunken.resolve(isDark)
    }
    val borderColor = when (variant) {
        PrismaCardVariant.Outlined -> PrismaSemanticColors.BorderSubtle.resolve(isDark)
        else -> null
    }
    val elevation = if (variant == PrismaCardVariant.Elevated) 2.dp else 0.dp

    var m = modifier
        .shadow(elevation, shape)
        .clip(shape)
        .background(bg)
    if (borderColor != null) m = m.border(1.dp, borderColor, shape)
    if (onClick != null) {
        m = m.clickable(role = Role.Button, onClick = onClick)
    }

    Box(modifier = m.padding(contentPadding)) { content() }
}
