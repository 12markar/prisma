package xyz.ksharma.prisma.catalogue.foundations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.shape.CircleShape
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Searchable grid of all 64 Prisma icons. Each tile shows the icon at 24dp
 * with its name underneath. Tap-to-copy support deferred to next polish pass.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun IconsShowcase() {
    var query by rememberSaveable { mutableStateOf("") }

    val filtered = remember(query) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) PrismaIcons.all else PrismaIcons.all.filter { it.first.contains(q) }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp5),
    ) {
        // Style spec footer.
        Text(
            text = "${PrismaIcons.all.size} icons · 24×24 grid · 1.75px stroke · currentColor",
            style = PrismaTypography.BodySm.copy(fontFamily = FontFamily.Monospace),
            color = PrismaSemanticColors.TextTertiary.themed(),
        )

        // Filter field.
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Filter icons by name",
                    style = PrismaTypography.BodyMd,
                    color = PrismaSemanticColors.TextTertiary.themed(),
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(PrismaIcons.Search),
                    contentDescription = null,
                    tint = PrismaSemanticColors.TextTertiary.themed(),
                    modifier = Modifier.size(18.dp),
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(PrismaRadius.Md),
            textStyle = PrismaTypography.BodyMd.copy(color = PrismaSemanticColors.TextPrimary.themed()),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = PrismaSemanticColors.SurfaceRaised.themed(),
                unfocusedContainerColor = PrismaSemanticColors.SurfaceRaised.themed(),
                focusedBorderColor = PrismaSemanticColors.BorderDefault.themed(),
                unfocusedBorderColor = PrismaSemanticColors.BorderSubtle.themed(),
                cursorColor = PrismaSemanticColors.AccentDefault.themed(),
            ),
        )

        // Icon grid via FlowRow — wraps inside DetailPane's vertical scroll.
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
            verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
        ) {
            filtered.forEach { (name, drawableRes) ->
                IconTile(name = name, drawableRes = drawableRes)
            }
        }

        if (filtered.isEmpty() && query.isNotBlank()) {
            EmptyResultsRow(query = query)
        }
    }
}

@Composable
private fun IconTile(name: String, drawableRes: Int) {
    Column(
        modifier = Modifier
            .width(96.dp)
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(PrismaSemanticColors.SurfaceSunken.themed())
            .padding(vertical = PrismaSpacing.Sp4, horizontal = PrismaSpacing.Sp2),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PrismaSemanticColors.SurfaceRaised.themed()),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(drawableRes),
                contentDescription = name,
                tint = PrismaSemanticColors.TextPrimary.themed(),
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = name,
            style = PrismaTypography.LabelSm.copy(fontFamily = FontFamily.Monospace),
            color = PrismaSemanticColors.TextSecondary.themed(),
        )
    }
}

@Composable
private fun EmptyResultsRow(query: String) {
    Text(
        text = "No icons match '$query'.",
        style = PrismaTypography.BodyMd,
        color = PrismaSemanticColors.TextTertiary.themed(),
    )
}
