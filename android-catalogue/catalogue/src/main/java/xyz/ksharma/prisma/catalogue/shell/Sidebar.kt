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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    modifier: Modifier = Modifier,
) {
    // Explicit saveable keys so state survives pane swaps in
    // NavigableListDetailPaneScaffold (the list pane is destroyed and
    // recreated on compact widths when navigating to detail and back).
    var query by rememberSaveable(key = "prisma.sidebar.query") { mutableStateOf("") }
    var expanded by rememberSaveable(key = "prisma.sidebar.expandedSections") {
        // All sections expanded by default — easier to discover on first launch
        // and matches what the user sees after their first toggle session.
        mutableStateOf(CatalogueRegistry.sections.map { it.name })
    }

    // Subtle staggered entrance — only the *very first* time the sidebar
    // mounts in this process. The saveable flag persists across
    // configuration changes and (critically) across detail-pane swaps in
    // NavigableListDetailPaneScaffold, which would otherwise re-run the
    // animation on every back nav and feel like a jerk.
    var entered by rememberSaveable(key = "prisma.sidebar.entered") { mutableStateOf(false) }
    if (!entered) {
        LaunchedEffect(Unit) { entered = true }
    }

    val results by remember(query) { derivedStateOf { CatalogueRegistry.search(query) } }
    val isSearching = query.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrismaSemanticColors.SurfaceSunken.themed()),
    ) {
        EntranceWrapper(visible = entered, delayMs = 0) {
            ChromeRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = PrismaSpacing.Sp5, end = PrismaSpacing.Sp4, top = PrismaSpacing.Sp5, bottom = PrismaSpacing.Sp3),
            )
        }

        EntranceWrapper(visible = entered, delayMs = 60) {
            SearchField(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp1),
            )
        }

        // Hoist scroll state via rememberSaveable, keyed explicitly so it
        // survives detail-pane swaps in NavigableListDetailPaneScaffold AND
        // process death. Without an explicit key, the default
        // rememberLazyListState was being dropped when the list pane was
        // recomposed after back-nav, scrolling the user back to top.
        val listScrollState = rememberSaveable(
            saver = LazyListState.Saver,
            key = "prisma.sidebar.scroll",
        ) { LazyListState() }
        LazyColumn(
            state = listScrollState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = PrismaSpacing.Sp3, bottom = PrismaSpacing.Sp7),
        ) {
            CatalogueRegistry.sections.forEachIndexed { index, section ->
                val sectionEntries = if (isSearching) {
                    results.filter { it.section == section }
                } else {
                    CatalogueRegistry.bySection(section)
                }
                if (sectionEntries.isEmpty()) return@forEachIndexed

                val isExpanded = isSearching || section.name in expanded
                // Sections enter ~120ms after search field, then stagger 50ms each
                // so the eye walks down the list naturally.
                val sectionDelayMs = 120 + index * 50

                item(key = "header_${section.name}") {
                    EntranceWrapper(visible = entered, delayMs = sectionDelayMs) {
                        SectionHeader(
                            title = section.title,
                            count = sectionEntries.size,
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

/**
 * Wraps a single sidebar slot with a one-time fade + 8dp slide-up entrance
 * keyed off [visible]. The combined ~250ms duration with per-slot stagger
 * gives the sidebar a deliberate-but-quick entrance without feeling slow.
 */
@Composable
private fun EntranceWrapper(
    visible: Boolean,
    delayMs: Int,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 240, delayMillis = delayMs, easing = FastOutSlowInEasing),
        ) + slideInVertically(
            animationSpec = tween(durationMillis = 280, delayMillis = delayMs, easing = FastOutSlowInEasing),
            initialOffsetY = { offset -> offset / 6 },
        ),
        exit = fadeOut(),
    ) {
        content()
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
private fun SectionHeader(
    title: String,
    count: Int,
    expanded: Boolean,
    enabled: Boolean,
    onToggle: () -> Unit,
) {
    // Chevron rotates 90° on toggle — single icon, animated.
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(durationMillis = PrismaMotion.Duration.Default),
        label = "chevron",
    )
    val rowModifier = if (enabled) {
        Modifier.fillMaxWidth().clickable(onClick = onToggle)
    } else {
        Modifier.fillMaxWidth()
    }
    Row(
        modifier = rowModifier.padding(
            start = PrismaSpacing.Sp4,
            end = PrismaSpacing.Sp4,
            top = PrismaSpacing.Sp5,
            bottom = PrismaSpacing.Sp2,
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
    ) {
        Text(
            text = title,
            style = PrismaTypography.TitleMd,
            color = PrismaSemanticColors.TextPrimary.themed(),
            modifier = Modifier.weight(1f),
        )
        // Count chip — uses surface.raised on collapsed sections so it stands out
        // as the user's "what's hiding here" cue; subtler when expanded.
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(PrismaRadius.Full))
                .background(
                    if (expanded) PrismaSemanticColors.SurfaceSunken.themed()
                    else PrismaSemanticColors.SurfaceRaised.themed(),
                )
                .padding(horizontal = PrismaSpacing.Sp2, vertical = 2.dp),
        ) {
            Text(
                text = count.toString(),
                style = PrismaTypography.LabelSm.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                color = PrismaSemanticColors.TextSecondary.themed(),
            )
        }
        Icon(
            painter = painterResource(PrismaIcons.ChevronRight),
            contentDescription = null,
            tint = PrismaSemanticColors.TextTertiary.themed(),
            modifier = Modifier
                .size(18.dp)
                .graphicsLayer { rotationZ = rotation },
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
