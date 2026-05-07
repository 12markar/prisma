package xyz.ksharma.prisma.catalogue.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing

/**
 * Container the live-preview component renders inside.
 *
 * Centered, themed surface with a subtle border. Sized so taller components
 * (modals, sheets) can spread without the surface flicking between heights.
 */
@Composable
internal fun PreviewSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp)
            .clip(RoundedCornerShape(PrismaRadius.Lg))
            .background(PrismaSemanticColors.SurfaceRaised.themed())
            .border(
                1.dp,
                PrismaSemanticColors.BorderSubtle.themed(),
                RoundedCornerShape(PrismaRadius.Lg),
            )
            .padding(PrismaSpacing.Sp7),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
