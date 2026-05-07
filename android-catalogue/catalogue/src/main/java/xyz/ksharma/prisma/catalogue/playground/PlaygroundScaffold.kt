package xyz.ksharma.prisma.catalogue.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.components.button.PrismaButton
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Storybook-style scaffold for an interactive component showcase.
 *
 * Pre-existing showcases call this with a `knobs` block, an optional
 * `states` block (which renders inside a flow gallery), a `code`
 * generator, and an optional `a11y` block. Knobs and a11y are now both
 * surfaced behind bottom sheets — opened by Edit / A11y action pills
 * directly under the preview — so the live preview never scrolls off
 * screen behind a long edit panel.
 *
 * The richer ButtonShowcase uses [PlaygroundScreen] (with a structured
 * [A11yReport] and a horizontal states pager) directly, bypassing this
 * scaffold. Both APIs coexist during the per-component rollout.
 */
@OptIn(ExperimentalMaterial3Api::class)
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
    var knobsOpen by rememberSaveable { mutableStateOf(false) }
    var a11yOpen by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp5),
    ) {
        PreviewSurface(modifier = Modifier.heightIn(min = 200.dp)) { preview() }

        if (knobs != null || a11y != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
            ) {
                if (knobs != null) {
                    ActionPill(
                        iconRes = PrismaIcons.Edit,
                        label = "Edit",
                        modifier = Modifier.weight(1f),
                        onClick = { knobsOpen = true },
                    )
                }
                if (a11y != null) {
                    ActionPill(
                        iconRes = PrismaIcons.Eye,
                        label = "A11y",
                        modifier = Modifier.weight(1f),
                        onClick = { a11yOpen = true },
                    )
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

        if (spec != null) {
            PlaygroundSection(label = "Spec") {
                spec()
            }
        }
    }

    if (knobsOpen && knobs != null) {
        ModalBottomSheet(
            onDismissRequest = { knobsOpen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = PrismaSemanticColors.SurfaceBase.themed(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PrismaSpacing.Sp5, vertical = PrismaSpacing.Sp4),
                verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
            ) {
                Text(
                    text = "Edit",
                    style = PrismaTypography.HeadlineSm,
                    color = PrismaSemanticColors.TextPrimary.themed(),
                )
                knobs()
                PrismaButton(
                    text = "Done",
                    onClick = { knobsOpen = false },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    if (a11yOpen && a11y != null) {
        ModalBottomSheet(
            onDismissRequest = { a11yOpen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = PrismaSemanticColors.SurfaceBase.themed(),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                a11y()
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

@Composable
private fun ActionPill(
    iconRes: Int,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(PrismaSemanticColors.SurfaceRaised.themed())
            .border(1.dp, PrismaSemanticColors.BorderSubtle.themed(), RoundedCornerShape(PrismaRadius.Md))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = PrismaSemanticColors.TextSecondary.themed(),
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = label,
            style = PrismaTypography.LabelMd,
            color = PrismaSemanticColors.TextPrimary.themed(),
        )
    }
}
