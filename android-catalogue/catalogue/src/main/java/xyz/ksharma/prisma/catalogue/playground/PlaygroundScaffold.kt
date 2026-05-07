package xyz.ksharma.prisma.catalogue.playground

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Storybook-style scaffold for an interactive component showcase.
 *
 *  ┌─────────────────────────┐
 *  │  Live preview surface   │
 *  ├─────────────────────────┤
 *  │  CONTROLS               │
 *  │   knob rows…            │
 *  ├─────────────────────────┤
 *  │  STATES                 │
 *  │   state-cell grid…      │
 *  └─────────────────────────┘
 *
 * Each slot is optional; when omitted the section header is skipped too.
 */
@Composable
public fun PlaygroundScaffold(
    preview: @Composable () -> Unit,
    knobs: (@Composable () -> Unit)? = null,
    states: (@Composable () -> Unit)? = null,
    code: (() -> String)? = null,
    a11y: (@Composable () -> Unit)? = null,
    spec: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp7),
    ) {
        PreviewSurface { preview() }

        if (knobs != null) {
            PlaygroundSection(label = "Controls") {
                Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4)) {
                    knobs()
                }
            }
        }

        if (states != null) {
            PlaygroundSection(label = "States") {
                StatesGallery { states() }
            }
        }

        if (code != null) {
            PlaygroundSection(label = "Usage") {
                CodeBlock(code = code())
            }
        }

        if (a11y != null) {
            PlaygroundSection(label = "Accessibility") {
                a11y()
            }
        }

        if (spec != null) {
            PlaygroundSection(label = "Spec") {
                spec()
            }
        }
    }
}

@Composable
private fun PlaygroundSection(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4)) {
        Text(
            text = label.uppercase(),
            style = PrismaTypography.LabelSm,
            color = PrismaSemanticColors.TextTertiary.themed(),
        )
        content()
    }
}
