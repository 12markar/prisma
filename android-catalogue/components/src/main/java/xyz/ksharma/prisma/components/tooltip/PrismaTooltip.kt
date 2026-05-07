package xyz.ksharma.prisma.components.tooltip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun PrismaTooltip(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val isDark = LocalPrismaIsDark.current
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip(
                containerColor = PrismaSemanticColors.SurfaceInverse.resolve(isDark),
                contentColor = PrismaSemanticColors.TextOnInverse.resolve(isDark),
                shape = RoundedCornerShape(PrismaRadius.Sm),
            ) {
                Text(
                    text = text,
                    style = PrismaTypography.LabelSm,
                    modifier = Modifier.padding(horizontal = PrismaSpacing.Sp2, vertical = PrismaSpacing.Sp1),
                )
            }
        },
        state = rememberTooltipState(),
        modifier = modifier,
    ) { content() }
}
