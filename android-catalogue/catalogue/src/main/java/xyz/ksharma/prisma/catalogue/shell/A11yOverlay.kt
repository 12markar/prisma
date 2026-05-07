package xyz.ksharma.prisma.catalogue.shell

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * Drives the touch-target debug overlay. When [enabled] flips to true, the
 * detail pane gets a magenta 48-dp grid drawn over its content so the
 * developer can eyeball whether tappable elements meet the 48 × 48 minimum
 * without invasive per-component instrumentation.
 */
public class A11yOverlayController(
    public val enabled: Boolean,
    public val toggle: () -> Unit,
)

public val LocalA11yOverlayController = compositionLocalOf<A11yOverlayController> {
    error("A11yOverlayController not provided. Wrap your tree in CatalogueRoot.")
}

/**
 * Wraps content with a translucent 48-dp grid when the overlay is on. The
 * grid sits in front of the content (subtle, dashed) so it reads over both
 * light and dark surfaces but doesn't dominate the layout.
 */
@Composable
public fun A11yOverlayLayer(
    enabled: Boolean,
    content: @Composable () -> Unit,
) {
    if (!enabled) {
        content()
        return
    }
    val density = LocalDensity.current
    val gridPx = with(density) { 48.dp.toPx() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                drawContent()
                val color = Color(0xFFE03088).copy(alpha = 0.35f)
                val dash = PathEffect.dashPathEffect(floatArrayOf(4f, 6f), 0f)
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = color,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f,
                        pathEffect = dash,
                    )
                    x += gridPx
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = color,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f,
                        pathEffect = dash,
                    )
                    y += gridPx
                }
            },
    ) { content() }
}
