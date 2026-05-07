package xyz.ksharma.prisma.components.banner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColor
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

public enum class PrismaBannerKind { Info, Success, Warning, Danger }

@Composable
public fun PrismaBanner(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    kind: PrismaBannerKind = PrismaBannerKind.Info,
    onDismiss: (() -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val isDark = LocalPrismaIsDark.current
    val (bgToken, accentToken) = paletteFor(kind)
    val iconRes = iconFor(kind)

    val urgency = when (kind) {
        PrismaBannerKind.Danger, PrismaBannerKind.Warning -> LiveRegionMode.Assertive
        else -> LiveRegionMode.Polite
    }
    val a11ySummary = listOfNotNull(kind.name, title, description).joinToString(". ")
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                liveRegion = urgency
                contentDescription = a11ySummary
            }
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(bgToken.resolve(isDark))
            .border(1.dp, PrismaSemanticColors.BorderSubtle.resolve(isDark), RoundedCornerShape(PrismaRadius.Md)),
        verticalAlignment = Alignment.Top,
    ) {
        // Left accent strip.
        Box(
            modifier = Modifier
                .width(3.dp)
                .background(accentToken.resolve(isDark)),
        ) {
            Box(modifier = Modifier.size(width = 3.dp, height = 80.dp)) // anchors a min strip height
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(PrismaSpacing.Sp4),
            horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = accentToken.resolve(isDark),
                modifier = Modifier.size(20.dp),
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp1)) {
                Text(
                    text = title,
                    style = PrismaTypography.LabelLg,
                    color = PrismaSemanticColors.TextPrimary.resolve(isDark),
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = PrismaTypography.BodySm,
                        color = PrismaSemanticColors.TextSecondary.resolve(isDark),
                    )
                }
                if (actionLabel != null && onAction != null) {
                    Text(
                        text = actionLabel,
                        style = PrismaTypography.LabelMd,
                        color = accentToken.resolve(isDark),
                        modifier = Modifier
                            .padding(top = PrismaSpacing.Sp2)
                            .clickable(onClick = onAction),
                    )
                }
            }
            if (onDismiss != null) {
                Icon(
                    painter = painterResource(PrismaIcons.X),
                    contentDescription = "Dismiss",
                    tint = PrismaSemanticColors.TextTertiary.resolve(isDark),
                    modifier = Modifier
                        .size(16.dp)
                        .clickable(onClick = onDismiss),
                )
            }
        }
    }
}

private fun paletteFor(kind: PrismaBannerKind): Pair<PrismaSemanticColor, PrismaSemanticColor> = when (kind) {
    PrismaBannerKind.Info -> PrismaSemanticColors.StatusInfoSubtle to PrismaSemanticColors.StatusInfoDefault
    PrismaBannerKind.Success -> PrismaSemanticColors.StatusSuccessSubtle to PrismaSemanticColors.StatusSuccessDefault
    PrismaBannerKind.Warning -> PrismaSemanticColors.StatusWarningSubtle to PrismaSemanticColors.StatusWarningDefault
    PrismaBannerKind.Danger -> PrismaSemanticColors.StatusDangerSubtle to PrismaSemanticColors.StatusDangerDefault
}

private fun iconFor(kind: PrismaBannerKind): Int = when (kind) {
    PrismaBannerKind.Info -> PrismaIcons.Info
    PrismaBannerKind.Success -> PrismaIcons.Success
    PrismaBannerKind.Warning -> PrismaIcons.Warning
    PrismaBannerKind.Danger -> PrismaIcons.Alert
}
