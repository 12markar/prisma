package xyz.ksharma.prisma.components.taginput

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import xyz.ksharma.prisma.components.chip.PrismaChip
import xyz.ksharma.prisma.components.chip.PrismaChipVariant
import xyz.ksharma.prisma.components.textfield.PrismaTextField
import xyz.ksharma.prisma.tokens.PrismaSpacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun PrismaTagInput(
    tags: ImmutableList<String>,
    onTagsChange: (ImmutableList<String>) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "Type and press Enter…",
    enabled: Boolean = true,
) {
    var draft by rememberSaveable { mutableStateOf("") }
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
        PrismaTextField(
            value = draft,
            onValueChange = { value ->
                if (value.endsWith('\n') || value.endsWith(',')) {
                    val newTag = value.trimEnd('\n', ',').trim()
                    if (newTag.isNotEmpty() && newTag !in tags) onTagsChange((tags + newTag).toImmutableList())
                    draft = ""
                } else draft = value
            },
            label = label,
            placeholder = placeholder,
            enabled = enabled,
        )
        if (tags.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
                verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
            ) {
                tags.forEach { tag ->
                    PrismaChip(
                        label = tag,
                        onClick = {},
                        variant = PrismaChipVariant.Input,
                        onDismiss = { onTagsChange((tags - tag).toImmutableList()) },
                    )
                }
            }
        }
    }
}
