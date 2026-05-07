package xyz.ksharma.prisma.catalogue.shell

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import xyz.ksharma.prisma.catalogue.registry.CatalogueRegistry

/**
 * Adaptive list-detail shell.
 *
 * Compact (phone): single-pane — list pushes to detail.
 * Medium / Expanded (tablet, foldable): two-pane — list + detail side-by-side.
 *
 * The navigator handles back-press and pane swap automatically; selection is
 * persisted via [rememberSaveable] so rotation, dark/light toggle, and process
 * death all preserve which component is showing.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
public fun CatalogueShell() {
    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()
    var selectedKey by rememberSaveable { mutableStateOf<String?>(null) }

    // ALL state that must survive AnimatedPane destruction is hoisted here.
    // NavigableListDetailPaneScaffold's internal SaveableStateHolder doesn't
    // reliably restore state when a pane is destroyed on compact widths
    // (known material3-adaptive quirk). Owning it at the always-existing
    // parent removes that variable: scroll position, entrance flag,
    // expanded sections, and search query all live here so the sidebar
    // re-mount is just a re-bind, not a fresh start.
    val sidebarScrollState = rememberSaveable(
        saver = LazyListState.Saver,
        key = "prisma.sidebar.scroll",
    ) { LazyListState() }
    val detailScrollOffsets = remember { mutableStateMapOf<String, Int>() }

    var sidebarEntered by rememberSaveable(key = "prisma.sidebar.entered") { mutableStateOf(false) }
    var sidebarQuery by rememberSaveable(key = "prisma.sidebar.query") { mutableStateOf("") }
    var sidebarExpanded by rememberSaveable(key = "prisma.sidebar.expanded") {
        mutableStateOf(CatalogueRegistry.sections.map { it.name })
    }

    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane {
                Sidebar(
                    selectedKey = selectedKey,
                    onSelect = { entry ->
                        selectedKey = entry.key
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                    },
                    listScrollState = sidebarScrollState,
                    entered = sidebarEntered,
                    onEnteredChange = { sidebarEntered = it },
                    query = sidebarQuery,
                    onQueryChange = { sidebarQuery = it },
                    expanded = sidebarExpanded,
                    onExpandedChange = { sidebarExpanded = it },
                )
            }
        },
        detailPane = {
            AnimatedPane {
                DetailPane(
                    entry = CatalogueRegistry.byKey(selectedKey),
                    scrollOffsets = detailScrollOffsets,
                )
            }
        },
    )
}
