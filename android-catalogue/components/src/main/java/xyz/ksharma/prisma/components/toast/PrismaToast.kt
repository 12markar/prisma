package xyz.ksharma.prisma.components.toast

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

public enum class PrismaToastKind { Info, Success, Warning, Danger }

/**
 * Visual representation of a transient toast. Pair with a [SnackbarHost]-style
 * coordinator to actually show/dismiss in your app shell. Kept stateless so
 * it can be embedded in catalogues, snackbars, or custom overlays.
 */
@Composable
public fun PrismaToast(
    message: String,
    modifier: Modifier = Modifier,
    kind: PrismaToastKind = PrismaToastKind.Info,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val isDark = LocalPrismaIsDark.current
    val iconRes = when (kind) {
        PrismaToastKind.Info -> PrismaIcons.Info
        PrismaToastKind.Success -> PrismaIcons.Success
        PrismaToastKind.Warning -> PrismaIcons.Warning
        PrismaToastKind.Danger -> PrismaIcons.Alert
    }
    val urgency = when (kind) {
        PrismaToastKind.Danger, PrismaToastKind.Warning -> LiveRegionMode.Assertive
        else -> LiveRegionMode.Polite
    }
    Row(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                liveRegion = urgency
                contentDescription = "${kind.name}. $message"
            }
            .shadow(8.dp, RoundedCornerShape(PrismaRadius.Md))
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(PrismaSemanticColors.SurfaceInverse.resolve(isDark))
            .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = PrismaSemanticColors.TextOnInverse.resolve(isDark),
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = message,
            style = PrismaTypography.BodyMd,
            color = PrismaSemanticColors.TextOnInverse.resolve(isDark),
        )
        if (actionLabel != null && onAction != null) {
            Text(
                text = actionLabel,
                style = PrismaTypography.LabelMd,
                color = PrismaSemanticColors.AccentDefault.resolve(isDark),
                modifier = Modifier.clickable(onClick = onAction),
            )
        }
    }
}
