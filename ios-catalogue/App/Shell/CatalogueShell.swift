import SwiftUI
import CoreUI

/// Adaptive list-detail shell.
///
/// Uses `NavigationSplitView` which collapses to single-pane on iPhone (compact
/// horizontal size class) and expands to sidebar + detail on iPad / large devices.
/// Selection, search query, and expanded sections all use `@SceneStorage` so
/// rotation / scene restoration / dark-light toggle preserves state.
struct CatalogueShell: View {
    /// Selected component key — preserved across scene termination.
    @SceneStorage("catalogue.selectedKey") private var selectedKey: String?

    /// Sidebar search query — preserved across scene termination.
    @SceneStorage("catalogue.query") private var query: String = ""

    var body: some View {
        // NavigationLink(value:) in the sidebar drives this
        // navigationDestination; updating `selectedKey` in onAppear of
        // each destination keeps the rest of the app (DetailPane queries,
        // scroll preservation maps) in sync with what's actually showing.
        NavigationSplitView {
            Sidebar(selectedKey: $selectedKey, query: $query)
                .navigationTitle("")
                .navigationDestination(for: String.self) { key in
                    DetailPane(entry: CatalogueRegistry.byKey(key))
                        .onAppear { selectedKey = key }
                }
        } detail: {
            DetailPane(entry: CatalogueRegistry.byKey(selectedKey))
        }
    }
}

#Preview { CatalogueShell() }
