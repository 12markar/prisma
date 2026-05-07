package xyz.ksharma.prisma.components.pagination

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

@Composable
public fun PrismaPagination(
    page: Int,
    pageCount: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp1),
    ) {
        ArrowButton(iconRes = PrismaIcons.ChevronLeft, enabled = page > 1) { onPageChange(page - 1) }
        // Compact: shows current/total. Sophisticated number list deferred.
        for (p in pagesToShow(page, pageCount)) {
            if (p == null) Ellipsis() else PageButton(p, p == page) { onPageChange(p) }
        }
        ArrowButton(iconRes = PrismaIcons.ChevronRight, enabled = page < pageCount) { onPageChange(page + 1) }
    }
}

private fun pagesToShow(page: Int, pageCount: Int): List<Int?> {
    if (pageCount <= 7) return (1..pageCount).toList()
    val out = mutableListOf<Int?>(1)
    if (page > 3) out.add(null)
    val start = maxOf(2, page - 1)
    val end = minOf(pageCount - 1, page + 1)
    for (p in start..end) out.add(p)
    if (page < pageCount - 2) out.add(null)
    out.add(pageCount)
    return out
}

@Composable
private fun PageButton(p: Int, selected: Boolean, onClick: () -> Unit) {
    val isDark = LocalPrismaIsDark.current
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(
                if (selected) PrismaSemanticColors.AccentDefault.resolve(isDark)
                else PrismaSemanticColors.SurfaceRaised.resolve(isDark),
            )
            .let {
                if (!selected) it.border(1.dp, PrismaSemanticColors.BorderSubtle.resolve(isDark), RoundedCornerShape(PrismaRadius.Md))
                else it
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = p.toString(),
            style = PrismaTypography.LabelMd,
            color = if (selected) PrismaSemanticColors.TextOnAccent.resolve(isDark)
                    else PrismaSemanticColors.TextPrimary.resolve(isDark),
        )
    }
}

@Composable
private fun Ellipsis() {
    val isDark = LocalPrismaIsDark.current
    Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
        Text("…", style = PrismaTypography.LabelMd, color = PrismaSemanticColors.TextTertiary.resolve(isDark))
    }
}

@Composable
private fun ArrowButton(iconRes: Int, enabled: Boolean, onClick: () -> Unit) {
    val isDark = LocalPrismaIsDark.current
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(PrismaSemanticColors.SurfaceRaised.resolve(isDark))
            .border(1.dp, PrismaSemanticColors.BorderSubtle.resolve(isDark), RoundedCornerShape(PrismaRadius.Md))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = if (enabled) PrismaSemanticColors.TextPrimary.resolve(isDark)
                   else PrismaSemanticColors.TextDisabled.resolve(isDark),
            modifier = Modifier.size(16.dp),
        )
    }
}
