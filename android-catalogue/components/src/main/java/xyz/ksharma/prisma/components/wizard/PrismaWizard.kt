package xyz.ksharma.prisma.components.wizard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

@Composable
public fun PrismaWizardSteps(
    steps: ImmutableList<String>,
    activeIndex: Int,
    modifier: Modifier = Modifier,
) {
    val isDark = LocalPrismaIsDark.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        steps.forEachIndexed { i, label ->
            val state = when {
                i < activeIndex -> StepState.Done
                i == activeIndex -> StepState.Active
                else -> StepState.Future
            }
            Column(
                modifier = Modifier.weight(1f),
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
                            text = (i + 1).toString(),
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
                )
            }
            if (i < steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .height(2.dp)
                        .background(
                            if (i < activeIndex) PrismaSemanticColors.AccentDefault.resolve(isDark)
                            else PrismaSemanticColors.BorderSubtle.resolve(isDark),
                        ),
                )
            }
        }
    }
}

private enum class StepState { Done, Active, Future }
