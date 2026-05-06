package xyz.ksharma.prisma.catalogue.shell

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
                )
            }
        },
        detailPane = {
            AnimatedPane {
                DetailPane(entry = CatalogueRegistry.byKey(selectedKey))
            }
        },
    )
}
