package xyz.ksharma.prisma.catalogue.shell

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColor
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Drives the right-side Inspector panel. Toggleable from chrome; visibility
 * is conversation-scoped (no rememberSaveable) because it's a transient
 * debug aid — coming back from the background should land cleanly closed.
 */
public class InspectorController(
    public val isOpen: Boolean,
    public val toggle: () -> Unit,
    public val close: () -> Unit,
)

public val LocalInspectorController = compositionLocalOf<InspectorController> {
    error("InspectorController not provided. Wrap your tree in CatalogueRoot.")
}

/**
 * Right-side runtime token reference. Lists every semantic colour with its
 * resolved hex for the active theme, plus the spacing / radius / typography
 * scales. A stop-gap for "what's this token actually rendering as right now?"
 * questions during development.
 */
@Composable
public fun InspectorOverlay(
    controller: InspectorController,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = controller.isOpen,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(onClick = controller.close),
        ) {
            AnimatedVisibility(
                visible = controller.isOpen,
                enter = slideInHorizontally { it },
                exit = slideOutHorizontally { it },
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                InspectorPanel(onClose = controller.close)
            }
        }
    }
}

@Composable
private fun InspectorPanel(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .widthIn(min = 320.dp, max = 420.dp)
            .background(PrismaSemanticColors.SurfaceBase.themed())
            .border(
                width = 1.dp,
                color = PrismaSemanticColors.BorderSubtle.themed(),
            )
            .clickable(enabled = false, onClick = {}),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PrismaSpacing.Sp5, vertical = PrismaSpacing.Sp4),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Inspector",
                style = PrismaTypography.TitleMd,
                color = PrismaSemanticColors.TextPrimary.themed(),
            )
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(PrismaIcons.Close),
                    contentDescription = "Close inspector",
                    tint = PrismaSemanticColors.TextSecondary.themed(),
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PrismaSpacing.Sp5)
                .background(PrismaSemanticColors.BorderSubtle.themed())
                .padding(top = 1.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = PrismaSpacing.Sp5, vertical = PrismaSpacing.Sp5),
            verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp7),
        ) {
            Group("Surface") {
                ColorRow("SurfaceBase", PrismaSemanticColors.SurfaceBase)
                ColorRow("SurfaceRaised", PrismaSemanticColors.SurfaceRaised)
                ColorRow("SurfaceSunken", PrismaSemanticColors.SurfaceSunken)
                ColorRow("SurfaceInverse", PrismaSemanticColors.SurfaceInverse)
                ColorRow("SurfaceOverlay", PrismaSemanticColors.SurfaceOverlay)
            }
            Group("Text") {
                ColorRow("TextPrimary", PrismaSemanticColors.TextPrimary)
                ColorRow("TextSecondary", PrismaSemanticColors.TextSecondary)
                ColorRow("TextTertiary", PrismaSemanticColors.TextTertiary)
                ColorRow("TextDisabled", PrismaSemanticColors.TextDisabled)
                ColorRow("TextOnAccent", PrismaSemanticColors.TextOnAccent)
                ColorRow("TextLink", PrismaSemanticColors.TextLink)
            }
            Group("Border") {
                ColorRow("BorderSubtle", PrismaSemanticColors.BorderSubtle)
                ColorRow("BorderDefault", PrismaSemanticColors.BorderDefault)
                ColorRow("BorderStrong", PrismaSemanticColors.BorderStrong)
                ColorRow("BorderFocus", PrismaSemanticColors.BorderFocus)
            }
            Group("Accent") {
                ColorRow("AccentDefault", PrismaSemanticColors.AccentDefault)
                ColorRow("AccentHover", PrismaSemanticColors.AccentHover)
                ColorRow("AccentPressed", PrismaSemanticColors.AccentPressed)
                ColorRow("AccentSubtle", PrismaSemanticColors.AccentSubtle)
            }
            Group("Status") {
                ColorRow("Success", PrismaSemanticColors.StatusSuccessDefault)
                ColorRow("Warning", PrismaSemanticColors.StatusWarningDefault)
                ColorRow("Danger", PrismaSemanticColors.StatusDangerDefault)
                ColorRow("Info", PrismaSemanticColors.StatusInfoDefault)
            }
            Group("Spacing") {
                ScalarRow("Sp1", "${PrismaSpacing.Sp1.value.toInt()} dp")
                ScalarRow("Sp2", "${PrismaSpacing.Sp2.value.toInt()} dp")
                ScalarRow("Sp3", "${PrismaSpacing.Sp3.value.toInt()} dp")
                ScalarRow("Sp4", "${PrismaSpacing.Sp4.value.toInt()} dp")
                ScalarRow("Sp5", "${PrismaSpacing.Sp5.value.toInt()} dp")
                ScalarRow("Sp6", "${PrismaSpacing.Sp6.value.toInt()} dp")
                ScalarRow("Sp7", "${PrismaSpacing.Sp7.value.toInt()} dp")
                ScalarRow("Sp8", "${PrismaSpacing.Sp8.value.toInt()} dp")
            }
            Group("Radius") {
                ScalarRow("Sm", "${PrismaRadius.Sm.value.toInt()} dp")
                ScalarRow("Md", "${PrismaRadius.Md.value.toInt()} dp")
                ScalarRow("Lg", "${PrismaRadius.Lg.value.toInt()} dp")
                ScalarRow("Full", "${PrismaRadius.Full.value.toInt()} dp")
            }
            Spacer(modifier = Modifier.size(PrismaSpacing.Sp7))
        }
    }
}

@Composable
private fun Group(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
        Text(
            text = label.uppercase(),
            style = PrismaTypography.LabelSm,
            color = PrismaSemanticColors.TextTertiary.themed(),
        )
        Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2)) {
            content()
        }
    }
}

@Composable
private fun ColorRow(label: String, token: PrismaSemanticColor) {
    val isDark = LocalPrismaIsDark.current
    val resolved = token.resolve(isDark)
    val hex = "#%02X%02X%02X".format(
        (resolved.red * 255).toInt(),
        (resolved.green * 255).toInt(),
        (resolved.blue * 255).toInt(),
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(resolved)
                .border(1.dp, PrismaSemanticColors.BorderSubtle.themed(), RoundedCornerShape(4.dp)),
        )
        Text(
            text = label,
            style = PrismaTypography.BodySm,
            color = PrismaSemanticColors.TextPrimary.themed(),
            modifier = Modifier.width(140.dp),
        )
        Text(
            text = hex,
            style = PrismaTypography.BodySm.copy(fontFamily = FontFamily.Monospace),
            color = PrismaSemanticColors.TextTertiary.themed(),
        )
    }
}

@Composable
private fun ScalarRow(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
    ) {
        Text(
            text = label,
            style = PrismaTypography.BodySm,
            color = PrismaSemanticColors.TextPrimary.themed(),
            modifier = Modifier.width(80.dp),
        )
        Text(
            text = value,
            style = PrismaTypography.BodySm.copy(fontFamily = FontFamily.Monospace),
            color = PrismaSemanticColors.TextTertiary.themed(),
        )
    }
}
