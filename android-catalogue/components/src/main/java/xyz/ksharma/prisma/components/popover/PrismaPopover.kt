package xyz.ksharma.prisma.components.popover

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing

@Composable
public fun PrismaPopover(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val isDark = LocalPrismaIsDark.current
    Popup(
        onDismissRequest = onDismissRequest,
        offset = IntOffset(0, 8),
        properties = PopupProperties(focusable = true),
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = modifier
                .widthIn(min = 240.dp, max = 480.dp)
                .shadow(8.dp, RoundedCornerShape(PrismaRadius.Lg))
                .clip(RoundedCornerShape(PrismaRadius.Lg))
                .background(PrismaSemanticColors.SurfaceRaised.resolve(isDark))
                .border(1.dp, PrismaSemanticColors.BorderSubtle.resolve(isDark), RoundedCornerShape(PrismaRadius.Lg))
                .padding(PrismaSpacing.Sp4),
        ) { content() }
    }
}
