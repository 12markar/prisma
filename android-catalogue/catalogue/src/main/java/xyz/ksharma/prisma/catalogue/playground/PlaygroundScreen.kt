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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.components.button.PrismaButton
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Reusable playground screen — same shell ButtonShowcase pioneered, now
 * extracted for every other component to consume. Handles preview surface,
 * Edit / A11y action pills, knobs sheet, a11y sheet, states pager, and
 * inline code snippet so individual showcases stay focused on declaring
 * their data (state list, knob rows, snippet).
 *
 * `previewMinHeight` lets components that need full-width breathing space
 * (Wizard, Pagination, Breadcrumb) ask for taller / shorter preview cells
 * without each having to redefine the chrome.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun PlaygroundScreen(
    preview: @Composable () -> Unit,
    knobs: @Composable () -> Unit,
    states: List<PlaygroundState>,
    code: () -> String,
    a11y: A11yReport,
    modifier: Modifier = Modifier,
    previewMinHeight: Dp = 240.dp,
    footer: (@Composable () -> Unit)? = null,
) {
    var knobsOpen by rememberSaveable { mutableStateOf(false) }
    var a11yOpen by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp5),
    ) {
        PreviewSurface(modifier = Modifier.heightIn(min = previewMinHeight)) { preview() }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
        ) {
            ActionPill(
                iconRes = PrismaIcons.Edit,
                label = "Edit",
                modifier = Modifier.weight(1f),
                onClick = { knobsOpen = true },
            )
            ActionPill(
                iconRes = PrismaIcons.Eye,
                label = "A11y",
                modifier = Modifier.weight(1f),
                onClick = { a11yOpen = true },
            )
        }

        if (states.isNotEmpty()) {
            SectionLabel("States — swipe to compare")
            StatesPager(states = states)
        }

        SectionLabel("Usage")
        CodeBlock(code = code())

        if (footer != null) footer()
    }

    if (knobsOpen) {
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

    if (a11yOpen) {
        ModalBottomSheet(
            onDismissRequest = { a11yOpen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = PrismaSemanticColors.SurfaceBase.themed(),
        ) {
            A11ySheetContent(report = a11y)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = PrismaTypography.LabelSm,
        color = PrismaSemanticColors.TextTertiary.themed(),
    )
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
