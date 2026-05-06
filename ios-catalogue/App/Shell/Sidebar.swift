import SwiftUI
import CoreUI

struct Sidebar: View {
    @Binding var selectedKey: String?
    @Binding var query: String
    let expandedSet: Set<String>
    let onToggleSection: (CatalogueSection) -> Void

    @Environment(\.colorScheme) private var scheme

    private var isSearching: Bool { !query.trimmingCharacters(in: .whitespaces).isEmpty }

    private var filteredEntries: [CatalogueEntry] {
        CatalogueRegistry.search(query)
    }

    var body: some View {
        List(selection: $selectedKey) {
            ForEach(CatalogueSection.allCases) { section in
                let entries = entriesFor(section)
                if !entries.isEmpty {
                    sectionView(section, entries: entries)
                }
            }
        }
        .listStyle(.sidebar)
        .scrollContentBackground(.hidden)
        .background(PrismaSemanticColors.surfaceSunken.themed(scheme))
        .searchable(
            text: $query,
            placement: .navigationBarDrawer(displayMode: .always),
            prompt: "Search components"
        )
    }

    private func entriesFor(_ section: CatalogueSection) -> [CatalogueEntry] {
        if isSearching {
            return filteredEntries.filter { $0.section == section }
        }
        return CatalogueRegistry.bySection(section)
    }

    @ViewBuilder
    private func sectionView(_ section: CatalogueSection, entries: [CatalogueEntry]) -> some View {
        // When searching, all sections are expanded; otherwise honour user state.
        let isExpanded = isSearching || expandedSet.contains(section.rawValue)

        if isSearching {
            // No collapsing during search — flat list per section.
            Section {
                ForEach(entries) { entry in
                    rowView(entry)
                }
            } header: {
                Text(section.rawValue.uppercased())
                    .font(PrismaTypography.labelSm.font)
                    .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
            }
        } else {
            // DisclosureGroup gives us the collapsible chevron animation natively.
            DisclosureGroup(
                isExpanded: Binding(
                    get: { isExpanded },
                    set: { _ in onToggleSection(section) }
                )
            ) {
                ForEach(entries) { entry in
                    rowView(entry)
                }
            } label: {
                Text(section.rawValue.uppercased())
                    .font(PrismaTypography.labelSm.font)
                    .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
            }
        }
    }

    @ViewBuilder
    private func rowView(_ entry: CatalogueEntry) -> some View {
        Text(entry.title)
            .font(PrismaTypography.bodyMd.font)
            .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
            .tag(entry.key)
    }
}
