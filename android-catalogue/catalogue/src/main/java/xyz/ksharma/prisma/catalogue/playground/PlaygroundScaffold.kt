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
 * Storybook-style scaffold shared by every legacy showcase. Three pills sit
 * directly under the preview — Edit / A11y / Code — each opening its own
 * bottom sheet. State galleries (legacy) and structured A11yReports (new)
 * are both supported so showcases can adopt the new pattern progressively.
 *
 * `pagerStates` (preferred) renders a horizontal pager of canonical states.
 * `a11yReport` (preferred) opens A11ySheetContent on the A11y pill. The
 * legacy `states` and `a11y` slots remain for incomplete migrations.
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
    pagerStates: List<PlaygroundState>? = null,
    a11yReport: A11yReport? = null,
    modifier: Modifier = Modifier,
) {
    var knobsOpen by rememberSaveable { mutableStateOf(false) }
    var a11yOpen by rememberSaveable { mutableStateOf(false) }
    var codeOpen by rememberSaveable { mutableStateOf(false) }

    val hasA11y = a11yReport != null || a11y != null

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp5),
    ) {
        PreviewSurface(modifier = Modifier.heightIn(min = 200.dp)) { preview() }

        if (knobs != null || hasA11y || code != null) {
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
                if (hasA11y) {
                    ActionPill(
                        iconRes = PrismaIcons.Eye,
                        label = "A11y",
                        modifier = Modifier.weight(1f),
                        onClick = { a11yOpen = true },
                    )
                }
                if (code != null) {
                    ActionPill(
                        iconRes = PrismaIcons.Doc,
                        label = "Code",
                        modifier = Modifier.weight(1f),
                        onClick = { codeOpen = true },
                    )
                }
            }
        }

        // Pager wins over legacy gallery — the swipeable layout is the new
        // default per the playground rollout brief.
        when {
            pagerStates != null && pagerStates.isNotEmpty() -> {
                PlaygroundSection(label = "States — swipe to compare") {
                    StatesPager(states = pagerStates)
                }
            }
            states != null -> {
                PlaygroundSection(label = "States") {
                    StatesGallery { states() }
                }
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

    if (a11yOpen && hasA11y) {
        ModalBottomSheet(
            onDismissRequest = { a11yOpen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = PrismaSemanticColors.SurfaceBase.themed(),
        ) {
            if (a11yReport != null) {
                A11ySheetContent(report = a11yReport)
            } else if (a11y != null) {
                Column(modifier = Modifier.fillMaxWidth()) { a11y() }
            }
        }
    }

    if (codeOpen && code != null) {
        ModalBottomSheet(
            onDismissRequest = { codeOpen = false },
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
                    text = "Usage",
                    style = PrismaTypography.HeadlineSm,
                    color = PrismaSemanticColors.TextPrimary.themed(),
                )
                Text(
                    text = "Drop this in to render the component as it appears above. Only knobs that differ from defaults are shown.",
                    style = PrismaTypography.BodySm,
                    color = PrismaSemanticColors.TextSecondary.themed(),
                )
                CodeBlock(code = code())
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
