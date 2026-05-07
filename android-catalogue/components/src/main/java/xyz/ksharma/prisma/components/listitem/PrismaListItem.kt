package xyz.ksharma.prisma.components.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

public enum class PrismaListItemSize(public val minHeight: Dp, public val verticalPadding: Dp) {
    Sm(44.dp, PrismaSpacing.Sp2),
    Default(56.dp, PrismaSpacing.Sp3),
    Lg(72.dp, PrismaSpacing.Sp4),
}

@Composable
public fun PrismaListItem(
    primary: String,
    modifier: Modifier = Modifier,
    secondary: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    size: PrismaListItemSize = PrismaListItemSize.Default,
) {
    val isDark = LocalPrismaIsDark.current
    val bg = if (selected) PrismaSemanticColors.AccentSubtle.resolve(isDark)
             else PrismaSemanticColors.SurfaceBase.resolve(isDark)
    val primaryColor = when {
        !enabled -> PrismaSemanticColors.TextDisabled.resolve(isDark)
        selected -> PrismaSemanticColors.AccentDefault.resolve(isDark)
        else -> PrismaSemanticColors.TextPrimary.resolve(isDark)
    }
    val secondaryColor = if (enabled) PrismaSemanticColors.TextSecondary.resolve(isDark)
                        else PrismaSemanticColors.TextDisabled.resolve(isDark)

    var rowModifier: Modifier = modifier
        .fillMaxWidth()
        .background(bg)
        .defaultMinSize(minHeight = size.minHeight)
        .padding(horizontal = PrismaSpacing.Sp4, vertical = size.verticalPadding)
        .semantics(mergeDescendants = true) {
            this.selected = selected
        }
    if (onClick != null && enabled) {
        rowModifier = rowModifier.clickable(role = Role.Button, onClick = onClick)
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
    ) {
        if (leading != null) Box(contentAlignment = Alignment.Center) { leading() }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = primary, style = PrismaTypography.BodyMd, color = primaryColor)
            if (secondary != null) {
                Text(
                    text = secondary,
                    style = PrismaTypography.BodySm,
                    color = secondaryColor,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
            }
        }
        if (trailing != null) Box(contentAlignment = Alignment.Center) { trailing() }
    }
}
