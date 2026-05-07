package xyz.ksharma.prisma.components.empty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

@Composable
public fun PrismaEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    visual: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val isDark = LocalPrismaIsDark.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(PrismaSpacing.Sp7),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp5),
    ) {
        if (visual != null) {
            Box(modifier = Modifier.padding(bottom = PrismaSpacing.Sp2), contentAlignment = Alignment.Center) {
                visual()
            }
        }
        Text(
            text = title,
            style = PrismaTypography.HeadlineSm,
            color = PrismaSemanticColors.TextPrimary.resolve(isDark),
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        if (description != null) {
            Text(
                text = description,
                style = PrismaTypography.BodyMd,
                color = PrismaSemanticColors.TextSecondary.resolve(isDark),
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 360.dp),
            )
        }
        if (action != null) {
            Box(modifier = Modifier.padding(top = PrismaSpacing.Sp2)) { action() }
        }
    }
}
