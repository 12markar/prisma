package xyz.ksharma.prisma.components.skeleton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaSemanticColors

/** Shape primitive for a skeleton placeholder, with shimmer animation.
 *  Skeletons are decorative — wrap the loading region in your own
 *  `Modifier.semantics { liveRegion = LiveRegionMode.Polite }` to announce
 *  completion. Each individual skeleton is hidden from screen readers so they
 *  don't read empty rectangles. */
@Composable
public fun PrismaSkeletonBlock(modifier: Modifier = Modifier, cornerRadius: androidx.compose.ui.unit.Dp = 4.dp) {
    PrismaSkeletonImpl(modifier = modifier.clip(RoundedCornerShape(cornerRadius)))
}

@Composable
public fun PrismaSkeletonLine(modifier: Modifier = Modifier) {
    PrismaSkeletonImpl(modifier = modifier.clip(RoundedCornerShape(4.dp)))
}

@Composable
public fun PrismaSkeletonCircle(modifier: Modifier = Modifier) {
    PrismaSkeletonImpl(modifier = modifier.clip(CircleShape))
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PrismaSkeletonImpl(modifier: Modifier) {
    val isDark = LocalPrismaIsDark.current
    val base = PrismaSemanticColors.SurfaceSunken.resolve(isDark)
    val highlight = PrismaSemanticColors.BorderSubtle.resolve(isDark)
    val transition = rememberInfiniteTransition(label = "skeleton")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "phase",
    )
    val shimmerWidth = 200f
    val brush = Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset(phase * 1000f - shimmerWidth, 0f),
        end = Offset(phase * 1000f, 0f),
    )
    Box(
        modifier = modifier
            .semantics { invisibleToUser() }
            .background(brush),
    )
}

internal val Int.dp get() = androidx.compose.ui.unit.Dp(this.toFloat())
