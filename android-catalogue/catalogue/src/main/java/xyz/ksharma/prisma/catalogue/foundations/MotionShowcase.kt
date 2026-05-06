package xyz.ksharma.prisma.catalogue.foundations

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaMotion
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/** Tap a row to play that easing × duration token combination. */
@Composable
public fun MotionShowcase() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
    ) {
        Text(
            text = "Tap a row to play the animation.",
            style = PrismaTypography.BodySm,
            color = PrismaSemanticColors.TextTertiary.themed(),
        )

        MotionRow("standard / default", PrismaMotion.Easing.Standard,    PrismaMotion.Duration.Default)
        MotionRow("decelerate / default", PrismaMotion.Easing.Decelerate,  PrismaMotion.Duration.Default)
        MotionRow("accelerate / fast",    PrismaMotion.Easing.Accelerate,  PrismaMotion.Duration.Fast)
        MotionRow("emphasized / slow",    PrismaMotion.Easing.Emphasized,  PrismaMotion.Duration.Slow)
        MotionRow("spring / slower",      PrismaMotion.Easing.Spring,      PrismaMotion.Duration.Slower)
        MotionRow("linear / default",     PrismaMotion.Easing.Linear,      PrismaMotion.Duration.Default)
    }
}

@Composable
private fun MotionRow(
    name: String,
    easingPoints: FloatArray,
    durationMs: Int,
) {
    var play by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (play) 1f else 0f,
        animationSpec = tween(
            durationMillis = durationMs,
            easing = CubicBezierEasing(easingPoints[0], easingPoints[1], easingPoints[2], easingPoints[3]),
        ),
        finishedListener = { play = false },
        label = name,
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(PrismaSemanticColors.SurfaceSunken.themed())
            .clickable { play = true }
            .padding(PrismaSpacing.Sp4),
    ) {
        Column(modifier = Modifier.padding(end = PrismaSpacing.Sp4)) {
            Text(
                text = name,
                style = PrismaTypography.LabelMd.copy(fontFamily = FontFamily.Monospace),
                color = PrismaSemanticColors.TextPrimary.themed(),
            )
            Text(
                text = "${durationMs}ms",
                style = PrismaTypography.LabelSm,
                color = PrismaSemanticColors.TextTertiary.themed(),
            )
        }

        // Track + animated dot
        Box(
            modifier = Modifier
                .padding(start = PrismaSpacing.Sp4)
                .clip(RoundedCornerShape(PrismaRadius.Full))
                .background(PrismaSemanticColors.BorderSubtle.themed())
                .size(width = 200.dp, height = 4.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer {
                        translationX = progress * (200f - 16f) * density
                    }
                    .clip(RoundedCornerShape(PrismaRadius.Full))
                    .background(PrismaSemanticColors.AccentDefault.themed()),
            )
        }
    }
}
