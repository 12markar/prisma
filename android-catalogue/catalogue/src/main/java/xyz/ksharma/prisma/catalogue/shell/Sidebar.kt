package xyz.ksharma.prisma.catalogue.shell

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
// TODO: Replace Material icons with design-system-supplied icon set once
//   Foundation/Icons showcase lands (Phase 1). Tracked in docs/TODO.md.
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.ksharma.prisma.catalogue.LocalThemeController
import xyz.ksharma.prisma.catalogue.registry.CatalogueEntry
import xyz.ksharma.prisma.catalogue.registry.CatalogueRegistry
import xyz.ksharma.prisma.catalogue.registry.CatalogueSection
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

@Composable
public fun Sidebar(
    selectedKey: String?,
    onSelect: (CatalogueEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable {
        mutableStateOf(listOf(CatalogueSection.Foundations.name))
    }

    val results by remember(query) { derivedStateOf { CatalogueRegistry.search(query) } }
    val isSearching = query.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrismaSemanticColors.SurfaceSunken.themed()),
    ) {
        ChromeRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = PrismaSpacing.Sp5, end = PrismaSpacing.Sp4, top = PrismaSpacing.Sp5, bottom = PrismaSpacing.Sp3),
        )

        SearchField(
            query = query,
            onQueryChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp1),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = PrismaSpacing.Sp3, bottom = PrismaSpacing.Sp7),
        ) {
            CatalogueRegistry.sections.forEach { section ->
                val sectionEntries = if (isSearching) {
                    results.filter { it.section == section }
                } else {
                    CatalogueRegistry.bySection(section)
                }
                if (sectionEntries.isEmpty()) return@forEach

                val isExpanded = isSearching || section.name in expanded

                item(key = "header_${section.name}") {
                    SectionHeader(
                        title = section.title,
                        expanded = isExpanded,
                        enabled = !isSearching,
                        onToggle = {
                            expanded = if (section.name in expanded) {
                                expanded - section.name
                            } else {
                                expanded + section.name
                            }
                        },
                    )
                }

                if (isExpanded) {
                    items(items = sectionEntries, key = { it.key }) { entry ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically(),
                        ) {
                            SidebarRow(
                                entry = entry,
                                selected = entry.key == selectedKey,
                                onSelect = { onSelect(entry) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChromeRow(modifier: Modifier = Modifier) {
    val controller = LocalThemeController.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
        ) {
            // Brand mark — gradient prism square (proper SVG rendering pending Coil/asset
            // integration in next session; this captures the violet→magenta brand colour
            // and the squircle silhouette of the official logo).
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF9173FF),
                                Color(0xFF7651F5),
                                Color(0xFFE03088),
                            ),
                        ),
                    ),
            )
            Text(
                text = "Prisma",
                style = PrismaTypography.HeadlineMd.copy(letterSpacing = (-0.4).sp),
                color = PrismaSemanticColors.TextPrimary.themed(),
            )
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PrismaSemanticColors.SurfaceRaised.themed())
                .border(1.dp, PrismaSemanticColors.BorderSubtle.themed(), CircleShape)
                .clickable(onClick = controller.toggle),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (controller.isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = if (controller.isDark) "Switch to light mode" else "Switch to dark mode",
                tint = PrismaSemanticColors.TextSecondary.themed(),
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = {
            Text(
                "Search components",
                style = PrismaTypography.BodyMd,
                color = PrismaSemanticColors.TextTertiary.themed(),
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
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
}

@Composable
private fun SectionHeader(
    title: String,
    expanded: Boolean,
    enabled: Boolean,
    onToggle: () -> Unit,
) {
    val rowModifier = if (enabled) {
        Modifier.fillMaxWidth().clickable(onClick = onToggle)
    } else {
        Modifier.fillMaxWidth()
    }
    Row(
        modifier = rowModifier
            .padding(start = PrismaSpacing.Sp4, end = PrismaSpacing.Sp4, top = PrismaSpacing.Sp4, bottom = PrismaSpacing.Sp2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
    ) {
        Icon(
            imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = PrismaSemanticColors.TextTertiary.themed(),
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = title.uppercase(),
            style = PrismaTypography.LabelSm.copy(letterSpacing = 0.8.sp),
            color = PrismaSemanticColors.TextTertiary.themed(),
        )
    }
}

@Composable
private fun SidebarRow(
    entry: CatalogueEntry,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    val background = if (selected) {
        PrismaSemanticColors.AccentSubtle.themed()
    } else {
        PrismaSemanticColors.SurfaceSunken.themed()
    }
    val color = if (selected) {
        PrismaSemanticColors.AccentDefault.themed()
    } else {
        PrismaSemanticColors.TextPrimary.themed()
    }
    Row(
        modifier = Modifier
            .padding(horizontal = PrismaSpacing.Sp3, vertical = PrismaSpacing.Sp1)
            .fillMaxWidth()
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(background)
            .clickable(onClick = onSelect),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Selection indicator strip — 3px accent bar on the left when selected.
        Box(
            modifier = Modifier
                .padding(vertical = PrismaSpacing.Sp1)
                .width(3.dp)
                .height(16.dp)
                .background(
                    if (selected) PrismaSemanticColors.AccentDefault.themed()
                    else androidx.compose.ui.graphics.Color.Transparent,
                ),
        )
        Text(
            text = entry.title,
            style = PrismaTypography.BodyMd,
            color = color,
            modifier = Modifier.padding(
                start = PrismaSpacing.Sp4,
                end = PrismaSpacing.Sp4,
                top = PrismaSpacing.Sp3,
                bottom = PrismaSpacing.Sp3,
            ),
        )
    }
}
