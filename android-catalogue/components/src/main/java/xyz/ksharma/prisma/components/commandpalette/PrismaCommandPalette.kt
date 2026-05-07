package xyz.ksharma.prisma.components.commandpalette

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.collections.immutable.ImmutableList
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.components.textfield.PrismaTextField
import xyz.ksharma.prisma.components.textfield.PrismaTextFieldVariant
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

public data class PrismaCommand(val label: String, val group: String, val onAction: () -> Unit)

@Composable
public fun PrismaCommandPalette(
    onDismissRequest: () -> Unit,
    commands: ImmutableList<PrismaCommand>,
    modifier: Modifier = Modifier,
) {
    val isDark = LocalPrismaIsDark.current
    var query by remember { mutableStateOf("") }

    val filtered = remember(query) {
        if (query.isBlank()) commands
        else commands.filter { it.label.lowercase().contains(query.trim().lowercase()) }
    }
    val grouped = filtered.groupBy { it.group }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = modifier
                .widthIn(min = 480.dp, max = 720.dp)
                .fillMaxWidth(0.9f)
                .heightIn(max = 600.dp)
                .shadow(16.dp, RoundedCornerShape(PrismaRadius.Xl))
                .clip(RoundedCornerShape(PrismaRadius.Xl))
                .background(PrismaSemanticColors.SurfaceRaised.resolve(isDark)),
        ) {
            Box(modifier = Modifier.padding(PrismaSpacing.Sp4)) {
                PrismaTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "Type a command or search…",
                    variant = PrismaTextFieldVariant.Filled,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(PrismaIcons.Search),
                            contentDescription = null,
                            tint = PrismaSemanticColors.TextTertiary.resolve(isDark),
                            modifier = Modifier.size(18.dp),
                        )
                    },
                )
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = PrismaSpacing.Sp2,
                    vertical = PrismaSpacing.Sp2,
                ),
            ) {
                grouped.forEach { (group, items) ->
                    item(key = "group_$group") {
                        Text(
                            text = group.uppercase(),
                            style = PrismaTypography.LabelSm,
                            color = PrismaSemanticColors.TextTertiary.resolve(isDark),
                            modifier = Modifier.padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp2),
                        )
                    }
                    items(items, key = { it.label }) { command ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(PrismaRadius.Md))
                                .clickable {
                                    command.onAction()
                                    onDismissRequest()
                                }
                                .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp3),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = command.label,
                                style = PrismaTypography.BodyMd,
                                color = PrismaSemanticColors.TextPrimary.resolve(isDark),
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrismaSemanticColors.SurfaceSunken.resolve(isDark))
                    .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp2),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = "↑↓ navigate · ↵ select · esc close",
                    style = PrismaTypography.LabelSm.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                    color = PrismaSemanticColors.TextTertiary.resolve(isDark),
                )
            }
        }
    }
}
