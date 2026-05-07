package xyz.ksharma.prisma.components.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaSemanticColor
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaTypography

public enum class PrismaAvatarSize(public val diameter: Dp, public val initialsScale: Float) {
    Xs(20.dp, 0.45f),
    Sm(28.dp, 0.45f),
    Default(40.dp, 0.42f),
    Lg(56.dp, 0.40f),
    Xl(96.dp, 0.36f),
}

public enum class PrismaAvatarStatus { Online, Away, Offline, Busy, None }

/**
 * Avatar — initials or fallback. Background colour derived deterministically
 * from the seed (typically the user name). Optional status indicator dot.
 *
 * Image variant deferred until Coil/runtime image loading is wired.
 */
@Composable
public fun PrismaAvatar(
    seed: String,
    modifier: Modifier = Modifier,
    initials: String = derivedInitials(seed),
    size: PrismaAvatarSize = PrismaAvatarSize.Default,
    status: PrismaAvatarStatus = PrismaAvatarStatus.None,
) {
    val isDark = LocalPrismaIsDark.current
    val (bg, fg) = swatchFor(seed)

    val statusLabel = when (status) {
        PrismaAvatarStatus.Online -> ", online"
        PrismaAvatarStatus.Away -> ", away"
        PrismaAvatarStatus.Busy -> ", busy"
        PrismaAvatarStatus.Offline -> ", offline"
        PrismaAvatarStatus.None -> ""
    }
    Box(
        modifier = modifier
            .size(size.diameter)
            .semantics(mergeDescendants = true) {
                contentDescription = "$seed$statusLabel"
            },
    ) {
        Box(
            modifier = Modifier
                .size(size.diameter)
                .clip(CircleShape)
                .background(bg.resolve(isDark)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials,
                style = PrismaTypography.LabelMd.copy(
                    fontSize = androidx.compose.ui.unit.TextUnit(
                        size.diameter.value * size.initialsScale,
                        androidx.compose.ui.unit.TextUnitType.Sp,
                    ),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                ),
                color = fg.resolve(isDark),
            )
        }
        if (status != PrismaAvatarStatus.None && size.diameter >= 28.dp) {
            val dotDiameter = (size.diameter.value * 0.28f).coerceAtLeast(8f).dp
            val statusColor = when (status) {
                PrismaAvatarStatus.Online -> PrismaSemanticColors.StatusSuccessDefault
                PrismaAvatarStatus.Away -> PrismaSemanticColors.StatusWarningDefault
                PrismaAvatarStatus.Busy -> PrismaSemanticColors.StatusDangerDefault
                PrismaAvatarStatus.Offline -> PrismaSemanticColors.TextTertiary
                PrismaAvatarStatus.None -> PrismaSemanticColors.SurfaceBase
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(dotDiameter)
                    .clip(CircleShape)
                    .border(2.dp, PrismaSemanticColors.SurfaceBase.resolve(isDark), CircleShape)
                    .background(statusColor.resolve(isDark))
                    .offset(x = 1.dp, y = 1.dp),
            )
        }
    }
}

private fun derivedInitials(seed: String): String {
    val parts = seed.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(1).uppercase()
        else -> "${parts.first().first().uppercase()}${parts.last().first().uppercase()}"
    }
}

/** Deterministic swatch derived from the seed's hash. 6 muted backgrounds. */
private fun swatchFor(seed: String): Pair<PrismaSemanticColor, PrismaSemanticColor> {
    val choice = (seed.hashCode().rem(6) + 6).rem(6)
    return when (choice) {
        0 -> PrismaSemanticColors.AccentSubtle to PrismaSemanticColors.AccentDefault
        1 -> PrismaSemanticColors.StatusSuccessSubtle to PrismaSemanticColors.StatusSuccessDefault
        2 -> PrismaSemanticColors.StatusWarningSubtle to PrismaSemanticColors.StatusWarningDefault
        3 -> PrismaSemanticColors.StatusDangerSubtle to PrismaSemanticColors.StatusDangerDefault
        4 -> PrismaSemanticColors.StatusInfoSubtle to PrismaSemanticColors.StatusInfoDefault
        else -> PrismaSemanticColors.SurfaceSunken to PrismaSemanticColors.TextPrimary
    }
}
