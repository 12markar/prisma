package xyz.ksharma.prisma.components.autocomplete

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import xyz.ksharma.prisma.components.textfield.PrismaTextField
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

@Composable
public fun PrismaAutocomplete(
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: ImmutableList<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
) {
    val isDark = LocalPrismaIsDark.current
    Column(modifier = modifier.fillMaxWidth()) {
        PrismaTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            placeholder = placeholder,
        )
        if (value.isNotBlank() && suggestions.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .padding(top = PrismaSpacing.Sp1)
                    .clip(RoundedCornerShape(PrismaRadius.Md))
                    .background(PrismaSemanticColors.SurfaceRaised.resolve(isDark))
                    .border(1.dp, PrismaSemanticColors.BorderSubtle.resolve(isDark), RoundedCornerShape(PrismaRadius.Md))
                    .heightIn(max = 200.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                suggestions.forEach { suggestion ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(suggestion) }
                            .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp3),
                    ) {
                        Text(
                            text = suggestion,
                            style = PrismaTypography.BodyMd,
                            color = PrismaSemanticColors.TextPrimary.resolve(isDark),
                        )
                    }
                }
            }
        }
    }
}
