package xyz.ksharma.prisma.components.searchbar

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.components.textfield.PrismaTextField
import xyz.ksharma.prisma.components.textfield.PrismaTextFieldVariant
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaSemanticColors

/**
 * Search-styled TextField. Filled variant, leading magnifier, no label —
 * ready to drop into chrome.
 */
@Composable
public fun PrismaSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    enabled: Boolean = true,
) {
    PrismaTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        variant = PrismaTextFieldVariant.Filled,
        enabled = enabled,
        leadingIcon = {
            Icon(
                painter = painterResource(PrismaIcons.Search),
                contentDescription = null,
                tint = PrismaSemanticColors.TextTertiary.themed(),
                modifier = Modifier.size(18.dp),
            )
        },
    )
}
