package xyz.ksharma.prisma.catalogue.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing

/**
 * Container the live-preview component renders inside.
 *
 * Stripped to just top + bottom hairline dividers (no card chrome, no
 * rounded box) so wide components — Wizard, Pagination, Breadcrumb —
 * can use the full available width. Content is centered horizontally
 * and vertically inside the slot; the slot itself is fillMaxWidth and
 * pads vertically to give breathing room without constraining
 * horizontal extent.
 */
@Composable
internal fun PreviewSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(PrismaSemanticColors.BorderSubtle.themed()),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
                .padding(vertical = PrismaSpacing.Sp7),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(PrismaSemanticColors.BorderSubtle.themed()),
        )
    }
}
