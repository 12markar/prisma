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
        // Switched from List(selection:) + tag-based selection to an
        // explicit NavigationLink(value:) per row. The previous setup
        // intermittently "stuck" on row taps (selection updated but detail
        // didn't push on compact widths) and the Section + Button-as-header
        // pattern mixed gesture types in a way that interfered with hit
        // testing. NavigationLink(value:) is the documented stable path
        // for split-view navigation in iOS 17+.
        List {
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
        // Default placement (no .navigationBarDrawer) — the always-visible
        // drawer placement was contributing to the keyboard-delay on tap
        // because the system was animating the drawer in alongside the
        // keyboard. Default placement opens cleanly.
        .searchable(text: $query, prompt: "Search components")
        .toolbar {
            ToolbarItem(placement: .principal) {
                HStack(spacing: PrismaSpacing.sp3) {
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
        default:      "circle.lefthalf.filled"
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
        let isExpanded = isSearching || expandedSet.contains(section.rawValue)

        Section {
            if isExpanded {
                ForEach(entries) { entry in
                    NavigationLink(value: entry.key) {
                        Text(entry.title)
                            .font(PrismaTypography.bodyMd.font)
                            .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                    }
                }
            }
        } header: {
            sectionLabel(section: section, count: entries.count, expanded: isExpanded)
                .contentShape(Rectangle())
                .onTapGesture {
                    if !isSearching { onToggleSection(section) }
                }
        }
    }

    /// Section header — prominent (title.md / text.primary) instead of the
    /// default subtle uppercase. Trailing count chip shows how many items
    /// the section contains. Plain HStack now (was a Button before, which
    /// fought List's gesture handling); tap is via .onTapGesture.
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
        .frame(minHeight: 44)
        .textCase(nil)
    }
}
