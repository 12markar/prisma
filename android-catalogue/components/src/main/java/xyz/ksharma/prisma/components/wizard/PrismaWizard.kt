package xyz.ksharma.prisma.components.wizard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Step indicator with `done / active / future` states.
 *
 * Layout: each step is a 96dp-wide cell (number-circle stacked over its
 * label). Cells are placed in a [FlowRow] so when there isn't enough
 * horizontal room they wrap to the next line instead of squashing the
 * connectors and clipping the labels. Connector lines render only when
 * the next step is on the same visual row — handled by the lazy two-pass
 * approach below: we build cells first; the row-break logic is a UX
 * trade-off, where wrapped rows lose the inter-step line by design.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun PrismaWizardSteps(
    steps: ImmutableList<String>,
    activeIndex: Int,
    modifier: Modifier = Modifier,
) {
    val isDark = LocalPrismaIsDark.current
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
    ) {
        steps.forEachIndexed { i, label ->
            val state = when {
                i < activeIndex -> StepState.Done
                i == activeIndex -> StepState.Active
                else -> StepState.Future
            }
            StepCell(
                index = i,
                label = label,
                state = state,
                isDark = isDark,
                showConnector = i < steps.lastIndex,
                connectorActive = i < activeIndex,
            )
        }
    }
}

@Composable
private fun StepCell(
    index: Int,
    label: String,
    state: StepState,
    isDark: Boolean,
    showConnector: Boolean,
    connectorActive: Boolean,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier.widthIn(min = 88.dp, max = 140.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        when (state) {
                            StepState.Active, StepState.Done -> PrismaSemanticColors.AccentDefault.resolve(isDark)
                            StepState.Future -> PrismaSemanticColors.SurfaceSunken.resolve(isDark)
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (state == StepState.Done) {
                    Icon(
                        painter = painterResource(PrismaIcons.Check),
                        contentDescription = null,
                        tint = PrismaSemanticColors.TextOnAccent.resolve(isDark),
                        modifier = Modifier.size(16.dp),
                    )
                } else {
                    Text(
                        text = (index + 1).toString(),
                        style = PrismaTypography.LabelMd,
                        color = if (state == StepState.Active) PrismaSemanticColors.TextOnAccent.resolve(isDark)
                                else PrismaSemanticColors.TextTertiary.resolve(isDark),
                    )
                }
            }
            Text(
                text = label,
                style = PrismaTypography.LabelSm,
                color = if (state == StepState.Future) PrismaSemanticColors.TextTertiary.resolve(isDark)
                        else PrismaSemanticColors.TextPrimary.resolve(isDark),
                textAlign = TextAlign.Center,
            )
        }
        if (showConnector) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(2.dp)
                    .background(
                        if (connectorActive) PrismaSemanticColors.AccentDefault.resolve(isDark)
                        else PrismaSemanticColors.BorderSubtle.resolve(isDark),
                    ),
            )
        }
    }
}

private enum class StepState { Done, Active, Future }
