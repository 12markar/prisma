package xyz.ksharma.prisma.catalogue.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

public data class PlaygroundState(
    public val label: String,
    public val content: @Composable () -> Unit,
)

/**
 * Horizontal pager of canonical component states. Page height is fixed —
 * crucial UX detail per feedback that variable-height pages caused jarring
 * layout shifts as the user swiped through. Each page renders inside a
 * card with a state label across the top.
 */
@Composable
public fun StatesPager(
    states: List<PlaygroundState>,
    modifier: Modifier = Modifier,
    pageHeight: Dp = 220.dp,
) {
    val pagerState = rememberPagerState(pageCount = { states.size })
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
    ) {
        // No contentPadding so each page is exactly the container's width —
        // matches the preview surface above so the two cards line up
        // edge-for-edge instead of looking like irregular shapes.
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(pageHeight),
        ) { index ->
            val state = states[index]
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(pageHeight)
                    .clip(RoundedCornerShape(PrismaRadius.Md))
                    .background(PrismaSemanticColors.SurfaceRaised.themed())
                    .border(
                        1.dp,
                        PrismaSemanticColors.BorderSubtle.themed(),
                        RoundedCornerShape(PrismaRadius.Md),
                    )
                    .padding(PrismaSpacing.Sp5),
                verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
            ) {
                Text(
                    text = state.label.uppercase(),
                    style = PrismaTypography.LabelSm,
                    color = PrismaSemanticColors.TextTertiary.themed(),
                )
                // Centered horizontally + vertically so a small button or
                // chip doesn't sit in the top-left corner of a 220dp cell —
                // the component is the focal point, not the label.
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    state.content()
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(states.size) { idx ->
                val active = idx == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(width = if (active) 18.dp else 6.dp, height = 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (active) PrismaSemanticColors.AccentDefault.themed()
                            else PrismaSemanticColors.BorderDefault.themed(),
                        ),
                )
            }
        }
    }
}
