package xyz.ksharma.prisma.catalogue.shell

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.components.button.PrismaButton
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * First-launch welcome overlay. Shown once, dismissed forever after.
 * Lives at the catalogue root so it covers both panes in tablet mode.
 */
@Composable
public fun OnboardingOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + scaleIn(initialScale = 0.92f),
                exit = fadeOut() + scaleOut(targetScale = 0.92f),
            ) {
                OnboardingCard(onDismiss = onDismiss)
            }
        }
    }
}

@Composable
private fun OnboardingCard(onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .widthIn(max = 480.dp)
            .padding(PrismaSpacing.Sp7)
            .shadow(16.dp, RoundedCornerShape(PrismaRadius.Lg))
            .clip(RoundedCornerShape(PrismaRadius.Lg))
            .background(PrismaSemanticColors.SurfaceRaised.themed())
            .padding(PrismaSpacing.Sp8)
            .clickable(enabled = false, onClick = {}),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp5),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(PrismaSemanticColors.AccentSubtle.themed()),
        )
        Text(
            text = "Welcome to Prisma",
            style = PrismaTypography.HeadlineMd,
            color = PrismaSemanticColors.TextPrimary.themed(),
        )
        Text(
            text = "An interactive catalogue of every component, foundation, and pattern in the design system.",
            style = PrismaTypography.BodyMd,
            color = PrismaSemanticColors.TextSecondary.themed(),
        )
        Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
            Tip("Tap any entry on the left to open its playground.")
            Tip("Edit knobs to drive the live preview — no rebuild needed.")
            Tip("Long-press the theme pill to follow system; tap to override.")
            Tip("Each component ships with a copy-able usage snippet.")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
            PrismaButton(text = "Get started", onClick = onDismiss)
        }
    }
}

@Composable
private fun Tip(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(PrismaSemanticColors.AccentDefault.themed()),
        )
        Text(
            text = text,
            style = PrismaTypography.BodyMd,
            color = PrismaSemanticColors.TextSecondary.themed(),
            modifier = Modifier.width(0.dp).widthIn(min = 0.dp, max = 380.dp),
        )
    }
}
