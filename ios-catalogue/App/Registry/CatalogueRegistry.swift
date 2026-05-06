import Foundation

public enum CatalogueSection: String, CaseIterable, Identifiable, Hashable {
    case foundations = "Foundations"
    case inputs = "Inputs"
    case feedback = "Feedback"
    case navigation = "Navigation"
    case dataDisplay = "Data display"

    public var id: String { rawValue }
}

public struct CatalogueEntry: Identifiable, Hashable {
    public let key: String
    public let title: String
    public let section: CatalogueSection
    public let tags: [String]

    public var id: String { key }
}

public enum CatalogueRegistry {
    public static let entries: [CatalogueEntry] = [
        // Foundations
        CatalogueEntry(key: "foundation.typography", title: "Typography", section: .foundations, tags: ["type", "specimen"]),
        CatalogueEntry(key: "foundation.colors", title: "Colors", section: .foundations, tags: ["color", "palette", "swatches"]),
        CatalogueEntry(key: "foundation.icons", title: "Icons", section: .foundations, tags: ["icon", "symbols"]),
        CatalogueEntry(key: "foundation.spacing", title: "Spacing", section: .foundations, tags: ["spacing", "layout"]),
        CatalogueEntry(key: "foundation.elevation", title: "Elevation", section: .foundations, tags: ["shadow", "depth"]),
        CatalogueEntry(key: "foundation.motion", title: "Motion", section: .foundations, tags: ["animation", "duration", "easing"]),
        CatalogueEntry(key: "foundation.radius", title: "Radius", section: .foundations, tags: ["corner", "radius"]),

        // Inputs
        CatalogueEntry(key: "input.button", title: "Button", section: .inputs, tags: ["button", "action"]),
        CatalogueEntry(key: "input.textfield", title: "TextField", section: .inputs, tags: ["input", "form"]),
        CatalogueEntry(key: "input.checkbox", title: "Checkbox", section: .inputs, tags: ["input", "form", "selection"]),
        CatalogueEntry(key: "input.radio", title: "Radio", section: .inputs, tags: ["input", "form", "selection"]),
        CatalogueEntry(key: "input.switch", title: "Switch", section: .inputs, tags: ["input", "form", "toggle"]),

        // Feedback
        CatalogueEntry(key: "feedback.toast", title: "Toast", section: .feedback, tags: ["feedback", "notification"]),
        CatalogueEntry(key: "feedback.modal", title: "Modal", section: .feedback, tags: ["feedback", "dialog"]),
        CatalogueEntry(key: "feedback.bottomSheet", title: "Bottom sheet", section: .feedback, tags: ["feedback", "sheet"]),
        CatalogueEntry(key: "feedback.loading", title: "Loading", section: .feedback, tags: ["feedback", "progress"]),
        CatalogueEntry(key: "feedback.badge", title: "Badge", section: .feedback, tags: ["feedback", "indicator"]),

        // Navigation
        CatalogueEntry(key: "navigation.tabs", title: "Tabs", section: .navigation, tags: ["navigation"]),
        CatalogueEntry(key: "navigation.chip", title: "Chip", section: .navigation, tags: ["selection", "filter"]),

        // Data display
        CatalogueEntry(key: "data.card", title: "Card", section: .dataDisplay, tags: ["container", "surface"]),
    ]

    public static func bySection(_ section: CatalogueSection) -> [CatalogueEntry] {
        entries.filter { $0.section == section }
    }

    public static func search(_ query: String) -> [CatalogueEntry] {
        let q = query.trimmingCharacters(in: .whitespaces).lowercased()
        guard !q.isEmpty else { return entries }
        return entries.filter { entry in
            entry.title.lowercased().contains(q) ||
                entry.tags.contains { $0.lowercased().contains(q) }
        }
    }

    public static func byKey(_ key: String?) -> CatalogueEntry? {
        guard let key else { return nil }
        return entries.first { $0.key == key }
    }
}
