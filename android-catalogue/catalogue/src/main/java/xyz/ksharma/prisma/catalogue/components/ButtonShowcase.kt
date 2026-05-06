package xyz.ksharma.prisma.catalogue.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import xyz.ksharma.prisma.components.button.PrismaButton
import xyz.ksharma.prisma.components.button.PrismaButtonSize
import xyz.ksharma.prisma.components.button.PrismaButtonVariant
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

@Composable
public fun ButtonShowcase() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp7),
    ) {
        SectionHeader("Variants")
        Row(horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
            PrismaButton(text = "Primary",     variant = PrismaButtonVariant.Primary,     onClick = {})
            PrismaButton(text = "Secondary",   variant = PrismaButtonVariant.Secondary,   onClick = {})
            PrismaButton(text = "Outlined",    variant = PrismaButtonVariant.Outlined,    onClick = {})
        }
        Row(horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
            PrismaButton(text = "Ghost",       variant = PrismaButtonVariant.Ghost,       onClick = {})
            PrismaButton(text = "Destructive", variant = PrismaButtonVariant.Destructive, onClick = {})
            PrismaButton(
                text = "",
                variant = PrismaButtonVariant.Icon,
                onClick = {},
                contentDescription = "Like",
                leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null) },
            )
        }

        SectionHeader("Sizes")
        Row(
            horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            PrismaButton(text = "Small",   size = PrismaButtonSize.Sm,      onClick = {})
            PrismaButton(text = "Default", size = PrismaButtonSize.Default, onClick = {})
            PrismaButton(text = "Large",   size = PrismaButtonSize.Lg,      onClick = {})
        }

        SectionHeader("With icons")
        Row(horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
            PrismaButton(
                text = "Add",
                onClick = {},
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
            )
            PrismaButton(
                text = "Continue",
                onClick = {},
                trailingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null) },
            )
        }

        SectionHeader("States")
        Row(horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
            PrismaButton(text = "Default",  onClick = {})
            PrismaButton(text = "Disabled", onClick = {}, enabled = false)
            PrismaButton(text = "Loading",  onClick = {}, loading = true)
        }

        SectionHeader("Interactive")
        InteractivePlayground()
    }
}

@Composable
private fun InteractivePlayground() {
    var clickCount by rememberSaveable { mutableStateOf(0) }
    var loading by rememberSaveable { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
        Text(
            text = "Tap count: $clickCount",
            style = PrismaTypography.BodyMd,
            color = PrismaSemanticColors.TextSecondary.themed(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
            PrismaButton(
                text = if (loading) "Saving…" else "Save changes",
                loading = loading,
                onClick = {
                    clickCount++
                    loading = true
                },
            )
            PrismaButton(
                text = "Reset",
                variant = PrismaButtonVariant.Ghost,
                enabled = !loading,
                onClick = {
                    clickCount = 0
                    loading = false
                },
            )
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        style = PrismaTypography.LabelSm,
        color = PrismaSemanticColors.TextTertiary.themed(),
    )
}
