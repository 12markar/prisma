package xyz.ksharma.prisma.catalogue.playground

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Live, copyable code snippet showing how to use the component with the
 * current knob values. Sits below the playground's States gallery.
 */
@Composable
public fun CodeBlock(
    code: String,
    modifier: Modifier = Modifier,
    language: String = "kotlin",
) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(1500)
            copied = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(PrismaSemanticColors.SurfaceSunken.themed())
            .border(
                1.dp,
                PrismaSemanticColors.BorderSubtle.themed(),
                RoundedCornerShape(PrismaRadius.Md),
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp2),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = language.uppercase(),
                style = PrismaTypography.LabelSm,
                color = PrismaSemanticColors.TextTertiary.themed(),
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(PrismaRadius.Sm))
                    .clickable(
                        role = Role.Button,
                        onClick = {
                            clipboard.setText(AnnotatedString(code))
                            copied = true
                        },
                    )
                    .padding(horizontal = PrismaSpacing.Sp2, vertical = PrismaSpacing.Sp1),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp1),
            ) {
                Icon(
                    painter = painterResource(if (copied) PrismaIcons.Check else PrismaIcons.Copy),
                    contentDescription = if (copied) "Copied" else "Copy",
                    tint = if (copied) PrismaSemanticColors.StatusSuccessDefault.themed()
                           else PrismaSemanticColors.TextSecondary.themed(),
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = if (copied) "Copied" else "Copy",
                    style = PrismaTypography.LabelSm,
                    color = if (copied) PrismaSemanticColors.StatusSuccessDefault.themed()
                            else PrismaSemanticColors.TextSecondary.themed(),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrismaSemanticColors.SurfaceBase.themed())
                .horizontalScroll(rememberScrollState())
                .padding(PrismaSpacing.Sp4),
        ) {
            Text(
                text = code,
                style = PrismaTypography.BodySm.copy(fontFamily = FontFamily.Monospace),
                color = PrismaSemanticColors.TextPrimary.themed(),
            )
        }
    }
}
