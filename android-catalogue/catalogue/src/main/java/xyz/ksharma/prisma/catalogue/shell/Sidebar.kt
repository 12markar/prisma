package xyz.ksharma.prisma.catalogue.shell

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
// TODO: Replace Material icons with design-system-supplied icon set once
//   Foundation/Icons showcase lands (Phase 1). Tracked in docs/TODO.md.
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
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
    // Survives rotation, dark/light toggle, process death.
    var query by rememberSaveable { mutableStateOf("") }

    // Default: only Foundations expanded. List<String> is saveable by default.
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
        SearchField(
            query = query,
            onQueryChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(PrismaSpacing.Sp4),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = PrismaSpacing.Sp6),
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
                        title = section.title.uppercase(),
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
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search components", style = PrismaTypography.BodyMd) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = PrismaSemanticColors.TextTertiary.themed(),
            )
        },
        singleLine = true,
        textStyle = PrismaTypography.BodyMd,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = PrismaSemanticColors.SurfaceRaised.themed(),
            unfocusedContainerColor = PrismaSemanticColors.SurfaceRaised.themed(),
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
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
    } else {
        Modifier.fillMaxWidth()
    }
    Row(
        modifier = rowModifier
            .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
    ) {
        Icon(
            imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = PrismaSemanticColors.TextTertiary.themed(),
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = title,
            style = PrismaTypography.LabelSm,
            color = PrismaSemanticColors.TextSecondary.themed(),
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
    Box(
        modifier = Modifier
            .padding(horizontal = PrismaSpacing.Sp3, vertical = PrismaSpacing.Sp1)
            .fillMaxWidth()
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(background)
            .clickable(onClick = onSelect)
            .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp3),
    ) {
        Text(
            text = entry.title,
            style = PrismaTypography.BodyMd,
            color = color,
        )
    }
}
