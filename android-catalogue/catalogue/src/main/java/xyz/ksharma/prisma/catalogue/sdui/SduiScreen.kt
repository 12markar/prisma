package xyz.ksharma.prisma.catalogue.sdui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Entry point for the catalogue's Server-Driven UI tab.
 *
 * Owns the [SduiClient] for the lifetime of the screen — opens the WebSocket
 * on first composition, closes it when the screen leaves. The connection
 * state is reflected in a top banner; the live document is rendered through
 * [SduiRender] beneath it.
 *
 * Hardcoded URL — change this constant for real-device testing. The Android
 * emulator reaches the host machine at 10.0.2.2.
 */
private const val SDUI_WS_URL: String = "ws://10.0.2.2:7331/ws"

@Composable
public fun SduiScreen(modifier: Modifier = Modifier) {
    val client = remember { SduiClient(SDUI_WS_URL) }
    DisposableEffect(client) {
        client.start()
        onDispose { client.stop() }
    }
    val state by client.state.collectAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        ConnectionBanner(state)
        Spacer(Modifier.height(PrismaSpacing.Sp4))
        when (val s = state) {
            is SduiState.Loaded         -> SduiRender(s.doc.root)
            is SduiState.InvalidPayload -> InvalidState(s.message)
            is SduiState.Connecting,
            is SduiState.Connected,
            is SduiState.Disconnected   -> WaitingState(state)
        }
    }
}

@Composable
private fun ConnectionBanner(state: SduiState) {
    val (label, dot) = when (state) {
        is SduiState.Connecting     -> "Connecting to $SDUI_WS_URL" to PrismaSemanticColors.StatusInfoDefault
        is SduiState.Connected      -> "Connected — waiting for first frame" to PrismaSemanticColors.StatusInfoDefault
        is SduiState.Loaded         -> "Live · changes to layout.json hot-reload" to PrismaSemanticColors.StatusSuccessDefault
        is SduiState.InvalidPayload -> "Invalid layout — see schema validation below" to PrismaSemanticColors.StatusDangerDefault
        is SduiState.Disconnected   -> "Disconnected — ${state.reason}" to PrismaSemanticColors.StatusWarningDefault
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(PrismaSemanticColors.SurfaceSunken.themed())
            .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dot.themed()),
        )
        Text(
            text = label,
            style = PrismaTypography.LabelMd,
            color = PrismaSemanticColors.TextSecondary.themed(),
        )
    }
}

@Composable
private fun WaitingState(state: SduiState) {
    val (headline, body) = when (state) {
        SduiState.Connecting        -> "Connecting…" to "Reaching $SDUI_WS_URL"
        SduiState.Connected         -> "Connected" to "Waiting for the first frame from the server"
        is SduiState.Disconnected   -> "Can't reach the dev server" to state.reason
        is SduiState.Loaded,
        is SduiState.InvalidPayload -> "No layout yet" to "" // unreachable in this composable
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PrismaRadius.Lg))
            .background(PrismaSemanticColors.SurfaceSunken.themed())
            .padding(PrismaSpacing.Sp5),
    ) {
        Text(
            text = headline,
            style = PrismaTypography.TitleLg,
            color = PrismaSemanticColors.TextPrimary.themed(),
        )
        if (body.isNotBlank()) {
            Spacer(Modifier.height(PrismaSpacing.Sp1))
            Text(
                text = body,
                style = PrismaTypography.BodyMd,
                color = PrismaSemanticColors.TextSecondary.themed(),
            )
        }
        Spacer(Modifier.height(PrismaSpacing.Sp4))
        Text(
            text = "1. Run ./startserver from the repo root.",
            style = PrismaTypography.BodySm,
            color = PrismaSemanticColors.TextTertiary.themed(),
        )
        Text(
            text = "2. Confirm http://localhost:7331 opens in a browser on the host.",
            style = PrismaTypography.BodySm,
            color = PrismaSemanticColors.TextTertiary.themed(),
        )
        Text(
            text = "3. WS URL on Android emulator is $SDUI_WS_URL — change SduiScreen.kt for a real device.",
            style = PrismaTypography.BodySm,
            color = PrismaSemanticColors.TextTertiary.themed(),
        )
        Text(
            text = "Logs: adb logcat -s SDUI:*",
            style = PrismaTypography.BodySm,
            color = PrismaSemanticColors.TextTertiary.themed(),
        )
    }
}

@Composable
private fun InvalidState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PrismaRadius.Lg))
            .background(PrismaSemanticColors.StatusDangerSubtle.themed())
            .padding(PrismaSpacing.Sp5),
    ) {
        Text(
            text = "Schema validation failed",
            style = PrismaTypography.TitleMd,
            color = PrismaSemanticColors.StatusDangerDefault.themed(),
        )
        Spacer(Modifier.height(PrismaSpacing.Sp2))
        Text(
            text = message,
            style = PrismaTypography.BodySm,
            color = PrismaSemanticColors.TextSecondary.themed(),
        )
    }
}
