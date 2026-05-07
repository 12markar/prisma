package xyz.ksharma.prisma.components.avatargroup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import xyz.ksharma.prisma.components.avatar.PrismaAvatar
import xyz.ksharma.prisma.components.avatar.PrismaAvatarSize
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaTypography

@Composable
public fun PrismaAvatarGroup(
    seeds: ImmutableList<String>,
    modifier: Modifier = Modifier,
    size: PrismaAvatarSize = PrismaAvatarSize.Default,
    max: Int = 4,
) {
    val isDark = LocalPrismaIsDark.current
    val visible = seeds.take(max)
    val overflow = (seeds.size - max).coerceAtLeast(0)
    val overlap = (size.diameter.value * 0.35f).dp

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        visible.forEachIndexed { i, seed ->
            Box(
                modifier = Modifier
                    .offset(x = if (i == 0) 0.dp else (-overlap * i))
                    .clip(CircleShape)
                    .border(2.dp, PrismaSemanticColors.SurfaceBase.resolve(isDark), CircleShape),
            ) {
                PrismaAvatar(seed = seed, size = size)
            }
        }
        if (overflow > 0) {
            Box(
                modifier = Modifier
                    .offset(x = -overlap * visible.size)
                    .size(size.diameter)
                    .clip(CircleShape)
                    .border(2.dp, PrismaSemanticColors.SurfaceBase.resolve(isDark), CircleShape)
                    .background(PrismaSemanticColors.SurfaceSunken.resolve(isDark)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "+$overflow",
                    style = PrismaTypography.LabelMd,
                    color = PrismaSemanticColors.TextPrimary.resolve(isDark),
                )
            }
        }
    }
}
