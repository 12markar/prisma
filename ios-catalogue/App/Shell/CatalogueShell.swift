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

    /// Expanded sidebar sections, comma-separated. Default: "Foundations".
    /// (Comma-separated keeps it `String` for `@SceneStorage` compatibility.)
    @SceneStorage("catalogue.expanded") private var expandedRaw: String = "Foundations"

    private var expandedSet: Set<String> {
        get { Set(expandedRaw.split(separator: ",").map(String.init).filter { !$0.isEmpty }) }
    }

    private func toggleExpanded(_ section: CatalogueSection) {
        var set = expandedSet
        if set.contains(section.rawValue) {
            set.remove(section.rawValue)
        } else {
            set.insert(section.rawValue)
        }
        expandedRaw = set.sorted().joined(separator: ",")
    }

    var body: some View {
        NavigationSplitView {
            Sidebar(
                selectedKey: $selectedKey,
                query: $query,
                expandedSet: expandedSet,
                onToggleSection: toggleExpanded
            )
            .navigationTitle("Prisma")
        } detail: {
            DetailPane(entry: CatalogueRegistry.byKey(selectedKey))
        }
    }
}

#Preview {
    CatalogueShell()
}
