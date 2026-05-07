package xyz.ksharma.prisma.catalogue.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
 * Grid of frozen, labelled component states. Sits below the knobs panel so
 * users can compare canonical states at a glance — answers "what does
 * disabled look like?" without knob wrangling.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun StatesGallery(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
    ) {
        content()
    }
}

@Composable
public fun StateCell(
    label: String,
    modifier: Modifier = Modifier,
    minWidth: androidx.compose.ui.unit.Dp = 160.dp,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .widthIn(min = minWidth)
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(PrismaSemanticColors.SurfaceRaised.themed())
            .border(
                1.dp,
                PrismaSemanticColors.BorderSubtle.themed(),
                RoundedCornerShape(PrismaRadius.Md),
            )
            .padding(PrismaSpacing.Sp4),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
    ) {
        Text(
            text = label.uppercase(),
            style = PrismaTypography.LabelSm,
            color = PrismaSemanticColors.TextTertiary.themed(),
        )
        Box(
            modifier = Modifier.heightIn(min = 56.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            content()
        }
    }
}
