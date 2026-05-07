package xyz.ksharma.prisma.catalogue.shell

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
// Theme toggle uses Material moon/sun (no equivalent in Prisma 64-icon set).
// All other chrome icons come from PrismaIcons.
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import xyz.ksharma.prisma.components.icons.PrismaIcons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.ksharma.prisma.catalogue.LocalThemeController
import xyz.ksharma.prisma.catalogue.registry.CatalogueEntry
import xyz.ksharma.prisma.catalogue.registry.CatalogueRegistry
import xyz.ksharma.prisma.catalogue.registry.CatalogueSection
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaMotion
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

@Composable
public fun Sidebar(
    selectedKey: String?,
    onSelect: (CatalogueEntry) -> Unit,
    listScrollState: androidx.compose.foundation.lazy.LazyListState,
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // All state hoisted to CatalogueShell — Sidebar is pure presentation.
    // Pane destruction during list/detail swaps no longer wipes state.
    //
    // All sections are always expanded — no toggle. The previous
    // expand/collapse logic added an AnimatedVisibility around each
    // section's row list which destabilised LazyColumn's measure pass
    // and contributed to the "first row tap takes time to load" sluggishness.

    val results by remember(query) { derivedStateOf { CatalogueRegistry.search(query) } }
    val isSearching = query.isNotBlank()

    // statusBarsPadding pushes the chrome row clear of the transparent
    // status bar; navigationBarsPadding is applied to the LazyColumn's
    // contentPadding below so list rows can scroll under the nav bar
    // while the bottom padding keeps the last row reachable.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrismaSemanticColors.SurfaceSunken.themed())
            .statusBarsPadding(),
    ) {
        ChromeRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = PrismaSpacing.Sp5, end = PrismaSpacing.Sp4, top = PrismaSpacing.Sp3, bottom = PrismaSpacing.Sp3),
        )

        SearchField(
            query = query,
            onQueryChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp1),
        )

        // listScrollState is hoisted from CatalogueShell — it lives outside
        // the AnimatedPane that gets destroyed on detail-pane swaps, so the
        // scroll position survives back-nav cleanly. Doing it inside this
        // composable via rememberSaveable was unreliable: material3-adaptive
        // doesn't fully preserve saveable scope across compact-width pane
        // destructions.
        // Each section is one rounded surface-raised card, mirroring iOS's
        // inset-grouped sidebar style — rather than the previous flat
        // header-then-rows layout that looked unstructured next to iOS.
        // Card edges and inter-row dividers are subtle so the eye still
        // groups items by section quickly.
        // Nav-bar inset added to bottom contentPadding so the last row stays
        // reachable above the (transparent) navigation bar — content can
        // scroll under it but rest position keeps the last row clear.
        val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        LazyColumn(
            state = listScrollState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = PrismaSpacing.Sp4,
                end = PrismaSpacing.Sp4,
                top = PrismaSpacing.Sp3,
                bottom = PrismaSpacing.Sp7 + navBarBottom,
            ),
        ) {
            val visibleSections = CatalogueRegistry.sections.mapIndexed { idx, section ->
                Triple(idx, section, if (isSearching) {
                    results.filter { it.section == section }
                } else {
                    CatalogueRegistry.bySection(section)
                })
            }.filter { it.third.isNotEmpty() }

            visibleSections.forEach { (_, section, sectionEntries) ->
                item(key = "section_${section.name}") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = PrismaSpacing.Sp1)
                            .clip(RoundedCornerShape(PrismaRadius.Lg))
                            .background(PrismaSemanticColors.SurfaceRaised.themed())
                            .border(
                                1.dp,
                                PrismaSemanticColors.BorderSubtle.themed(),
                                RoundedCornerShape(PrismaRadius.Lg),
                            ),
                    ) {
                        SectionHeader(title = section.title, count = sectionEntries.size)
                        androidx.compose.foundation.layout.Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(PrismaSemanticColors.BorderSubtle.themed()),
                        )
                        sectionEntries.forEachIndexed { rowIdx, entry ->
                            SidebarRow(
                                entry = entry,
                                selected = entry.key == selectedKey,
                                onSelect = { onSelect(entry) },
                            )
                            if (rowIdx < sectionEntries.lastIndex) {
                                androidx.compose.foundation.layout.Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = PrismaSpacing.Sp4)
                                        .height(0.5.dp)
                                        .background(PrismaSemanticColors.BorderSubtle.themed()),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
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

        // Single chrome action: theme toggle. Tap cycles light/dark; long-press
        // returns to "follow system". Stripped back from the earlier multi-icon
        // chrome (contrast badge + inspector + a11y grid) — those were
        // engineer-targeted and added noise to the daily user surface.
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PrismaSemanticColors.SurfaceRaised.themed())
                .border(1.dp, PrismaSemanticColors.BorderSubtle.themed(), CircleShape)
                .combinedClickable(
                    onClick = controller.toggle,
                    onLongClick = controller.followSystem,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (controller.isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = if (controller.isDark) "Tap: switch to light. Long-press: follow system."
                                     else "Tap: switch to dark. Long-press: follow system.",
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
}

@Composable
private fun SectionHeader(title: String, count: Int) {
    // Plain (non-clickable) section label — no toggle, no chevron, no
    // animated rotation. The previous expand/collapse on tap was
    // contributing to first-tap sluggishness via gesture conflict with
    // the row clickable below.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
    ) {
        Text(
            text = title,
            style = PrismaTypography.TitleMd,
            color = PrismaSemanticColors.TextPrimary.themed(),
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(PrismaRadius.Full))
                .background(PrismaSemanticColors.SurfaceSunken.themed())
                .padding(horizontal = PrismaSpacing.Sp2, vertical = 2.dp),
        ) {
            Text(
                text = count.toString(),
                style = PrismaTypography.LabelSm.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                color = PrismaSemanticColors.TextSecondary.themed(),
            )
        }
    }
}

@Composable
private fun SidebarRow(
    entry: CatalogueEntry,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    // Lives inside a surface.raised section card now, so the row has no
    // own background — selected state uses an accent.subtle tint that sits
    // visibly on the card surface. Min 48dp tap target.
    val background = if (selected) PrismaSemanticColors.AccentSubtle.themed()
                     else androidx.compose.ui.graphics.Color.Transparent
    val color = if (selected) PrismaSemanticColors.AccentDefault.themed()
                else PrismaSemanticColors.TextPrimary.themed()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .background(background)
            .clickable(onClick = onSelect),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Selection indicator strip — 3px accent bar on the left when selected.
        Box(
            modifier = Modifier
                .padding(vertical = PrismaSpacing.Sp1)
                .width(3.dp)
                .height(20.dp)
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
