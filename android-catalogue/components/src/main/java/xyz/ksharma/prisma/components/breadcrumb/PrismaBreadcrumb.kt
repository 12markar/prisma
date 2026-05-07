package xyz.ksharma.prisma.components.breadcrumb

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

public data class PrismaBreadcrumbItem(val label: String, val onClick: (() -> Unit)? = null)

@Composable
public fun PrismaBreadcrumb(
    items: ImmutableList<PrismaBreadcrumbItem>,
    modifier: Modifier = Modifier,
) {
    val isDark = LocalPrismaIsDark.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
    ) {
        items.forEachIndexed { i, item ->
            val isLast = i == items.lastIndex
            val color = if (isLast) PrismaSemanticColors.TextPrimary.resolve(isDark)
                        else PrismaSemanticColors.TextSecondary.resolve(isDark)
            if (item.onClick != null && !isLast) {
                Text(
                    text = item.label,
                    style = PrismaTypography.BodySm,
                    color = color,
                    modifier = Modifier.clickable(onClick = item.onClick),
                )
            } else {
                Text(text = item.label, style = PrismaTypography.BodySm, color = color)
            }
            if (!isLast) {
                Icon(
                    painter = painterResource(PrismaIcons.ChevronRight),
                    contentDescription = null,
                    tint = PrismaSemanticColors.TextTertiary.resolve(isDark),
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}
