package xyz.ksharma.prisma.components.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

@Composable
public fun PrismaModal(
    onDismissRequest: () -> Unit,
    title: String,
    body: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    dismissLabel: String? = null,
    onDismiss: (() -> Unit)? = null,
    isDestructive: Boolean = false,
) {
    val isDark = LocalPrismaIsDark.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(PrismaRadius.Lg))
                .background(PrismaSemanticColors.SurfaceRaised.resolve(isDark))
                .padding(PrismaSpacing.Sp7),
            verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
        ) {
            Text(
                text = title,
                style = PrismaTypography.HeadlineSm,
                color = PrismaSemanticColors.TextPrimary.resolve(isDark),
            )
            Text(
                text = body,
                style = PrismaTypography.BodyMd,
                color = PrismaSemanticColors.TextSecondary.resolve(isDark),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3, alignment = androidx.compose.ui.Alignment.End),
            ) {
                if (dismissLabel != null) {
                    Text(
                        text = dismissLabel,
                        style = PrismaTypography.LabelLg,
                        color = PrismaSemanticColors.TextSecondary.resolve(isDark),
                        modifier = Modifier
                            .clip(RoundedCornerShape(PrismaRadius.Md))
                            .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp3)
                            .clickable { (onDismiss ?: onDismissRequest)() },
                    )
                }
                val confirmColor = if (isDestructive) {
                    PrismaSemanticColors.StatusDangerDefault.resolve(isDark)
                } else {
                    PrismaSemanticColors.AccentDefault.resolve(isDark)
                }
                Text(
                    text = confirmLabel,
                    style = PrismaTypography.LabelLg,
                    color = if (isDestructive) PrismaSemanticColors.StatusDangerOnStatus.resolve(isDark)
                            else PrismaSemanticColors.TextOnAccent.resolve(isDark),
                    modifier = Modifier
                        .clip(RoundedCornerShape(PrismaRadius.Md))
                        .background(confirmColor)
                        .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp3)
                        .clickable { onConfirm() },
                )
            }
        }
    }
}
