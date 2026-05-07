import SwiftUI
import CoreUI

struct Sidebar: View {
    @Binding var selectedKey: String?
    @Binding var query: String
    let expandedSet: Set<String>
    let onToggleSection: (CatalogueSection) -> Void

    @Environment(\.colorScheme) private var scheme

    /// Shared with PrismaApp via the same UserDefaults key.
    /// "" = follow system, "light" = forced light, "dark" = forced dark.
    @AppStorage("prisma.isDarkOverride") private var isDarkOverrideRaw: String = ""

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
        .toolbar {
            ToolbarItem(placement: .principal) {
                HStack(spacing: PrismaSpacing.sp3) {
                    // Brand gradient mark — squircle filled with the official
                    // Prisma violet→magenta prism gradient. Full SVG render
                    // queued for asset-pipeline integration in next session.
                    RoundedRectangle(cornerRadius: 8)
                        .fill(
                            LinearGradient(
                                colors: [
                                    Color(red: 0.569, green: 0.451, blue: 1.0),
                                    Color(red: 0.463, green: 0.318, blue: 0.961),
                                    Color(red: 0.878, green: 0.188, blue: 0.533)
                                ],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            )
                        )
                        .frame(width: 24, height: 24)
                    Text("Prisma")
                        .font(PrismaTypography.titleLg.font)
                        .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                        .tracking(-0.4)
                }
            }
            ToolbarItem(placement: .topBarTrailing) {
                // Single chrome action: theme toggle. The earlier multi-icon
                // chrome (contrast badge + inspector + a11y grid) added noise
                // to the daily user surface — those are engineer-targeted and
                // can return as a debug menu later.
                Button(action: cycleTheme) {
                    Image(systemName: themeIconName)
                }
                .accessibilityLabel(themeAccessibilityLabel)
            }
        }
        .toolbarTitleDisplayMode(.inline)
    }

    // MARK: - Theme toggle

    /// Cycles: system → dark → light → system.
    private func cycleTheme() {
        switch isDarkOverrideRaw {
        case "":      isDarkOverrideRaw = "dark"
        case "dark":  isDarkOverrideRaw = "light"
        default:      isDarkOverrideRaw = ""
        }
    }

    private var themeIconName: String {
        switch isDarkOverrideRaw {
        case "dark":  "moon.fill"
        case "light": "sun.max.fill"
        default:      "circle.lefthalf.filled"   // follow system
        }
    }

    private var themeAccessibilityLabel: String {
        switch isDarkOverrideRaw {
        case "dark":  "Theme: dark. Tap to switch to light."
        case "light": "Theme: light. Tap to follow system."
        default:      "Theme: follows system. Tap to force dark."
        }
    }

    private func entriesFor(_ section: CatalogueSection) -> [CatalogueEntry] {
        if isSearching {
            return filteredEntries.filter { $0.section == section }
        }
        return CatalogueRegistry.bySection(section)
    }

    @ViewBuilder
    private func sectionView(_ section: CatalogueSection, entries: [CatalogueEntry]) -> some View {
        // When searching, all sections are forced-expanded; otherwise honour user state.
        let isExpanded = isSearching || expandedSet.contains(section.rawValue)

        // Use Section + Button-as-header so taps on the header *only* toggle
        // expansion. Previously DisclosureGroup inside List(selection:)
        // could fire a row-selection on label tap, mis-feeling like nav.
        Section {
            if isExpanded {
                ForEach(entries) { entry in
                    rowView(entry)
                }
            }
        } header: {
            Button(action: { if !isSearching { onToggleSection(section) } }) {
                sectionLabel(section: section, count: entries.count, expanded: isExpanded)
            }
            .buttonStyle(.plain)
            .disabled(isSearching)
        }
    }

    /// Section header — prominent (title.md / text.primary) instead of the
    /// default subtle uppercase. Trailing count chip shows how many items
    /// the section contains.
    @ViewBuilder
    private func sectionLabel(section: CatalogueSection, count: Int, expanded: Bool) -> some View {
        HStack(spacing: PrismaSpacing.sp3) {
            Text(section.rawValue)
                .font(PrismaTypography.titleMd.font)
                .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
            Spacer()
            Text("\(count)")
                .font(.system(size: 11, weight: .medium, design: .monospaced))
                .foregroundStyle(PrismaSemanticColors.textSecondary.themed(scheme))
                .padding(.horizontal, PrismaSpacing.sp2)
                .padding(.vertical, 2)
                .background(
                    expanded
                        ? PrismaSemanticColors.surfaceSunken.themed(scheme)
                        : PrismaSemanticColors.surfaceRaised.themed(scheme)
                )
                .clipShape(Capsule())
            Image(systemName: expanded ? "chevron.down" : "chevron.right")
                .font(.system(size: 12, weight: .semibold))
                .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
        }
        .contentShape(Rectangle())   // Whole row is the hit target.
        .textCase(nil)
        .padding(.vertical, 4)
    }

    @ViewBuilder
    private func rowView(_ entry: CatalogueEntry) -> some View {
        Text(entry.title)
            .font(PrismaTypography.bodyMd.font)
            .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
            .tag(entry.key)
    }
}
