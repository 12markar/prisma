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

    // Both sidebar and detail-pane scroll states are hoisted here —
    // outside the AnimatedPane that destroys on pane swap.
    // NavigableListDetailPaneScaffold's SaveableStateHolder doesn't
    // reliably restore LazyListState / ScrollState on back-nav (a known
    // material3-adaptive quirk on compact widths), so we own them at the
    // always-existing parent. Detail-pane scroll positions are kept per
    // entry.key in a remembered map so revisiting a component restores
    // exactly where you left off.
    val sidebarScrollState = rememberSaveable(
        saver = LazyListState.Saver,
        key = "prisma.sidebar.scroll",
    ) { LazyListState() }
    val detailScrollOffsets = remember { mutableStateMapOf<String, Int>() }

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
