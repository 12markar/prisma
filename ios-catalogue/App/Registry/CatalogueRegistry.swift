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
        CatalogueEntry(key: "input.slider", title: "Slider", section: .inputs, tags: ["input", "range", "value"]),
        CatalogueEntry(key: "input.segmentedControl", title: "Segmented control", section: .inputs, tags: ["input", "selection", "tabs"]),
        CatalogueEntry(key: "input.searchBar", title: "Search bar", section: .inputs, tags: ["input", "search"]),
        CatalogueEntry(key: "input.autocomplete", title: "Autocomplete", section: .inputs, tags: ["input", "search", "combobox"]),
        CatalogueEntry(key: "input.stepper", title: "Stepper", section: .inputs, tags: ["input", "number"]),
        CatalogueEntry(key: "input.tagInput", title: "Tag input", section: .inputs, tags: ["input", "chip", "multi"]),
        CatalogueEntry(key: "input.datePicker", title: "Date picker", section: .inputs, tags: ["input", "date", "calendar"]),
        CatalogueEntry(key: "input.timePicker", title: "Time picker", section: .inputs, tags: ["input", "time", "clock"]),
        CatalogueEntry(key: "input.colorPicker", title: "Color picker", section: .inputs, tags: ["input", "color", "swatch"]),

        // Feedback
        CatalogueEntry(key: "feedback.toast", title: "Toast", section: .feedback, tags: ["feedback", "notification"]),
        CatalogueEntry(key: "feedback.banner", title: "Banner", section: .feedback, tags: ["feedback", "alert", "inline"]),
        CatalogueEntry(key: "feedback.modal", title: "Modal", section: .feedback, tags: ["feedback", "dialog"]),
        CatalogueEntry(key: "feedback.bottomSheet", title: "Bottom sheet", section: .feedback, tags: ["feedback", "sheet"]),
        CatalogueEntry(key: "feedback.popover", title: "Popover", section: .feedback, tags: ["feedback", "overlay"]),
        CatalogueEntry(key: "feedback.tooltip", title: "Tooltip", section: .feedback, tags: ["feedback", "hint"]),
        CatalogueEntry(key: "feedback.loading", title: "Loading", section: .feedback, tags: ["feedback", "progress"]),
        CatalogueEntry(key: "feedback.skeleton", title: "Skeleton", section: .feedback, tags: ["feedback", "placeholder", "loading"]),
        CatalogueEntry(key: "feedback.badge", title: "Badge", section: .feedback, tags: ["feedback", "indicator"]),
        CatalogueEntry(key: "feedback.emptyState", title: "Empty state", section: .feedback, tags: ["feedback", "empty"]),
        CatalogueEntry(key: "feedback.drawer", title: "Drawer", section: .feedback, tags: ["feedback", "side", "sheet"]),

        // Navigation
        CatalogueEntry(key: "navigation.tabs", title: "Tabs", section: .navigation, tags: ["navigation"]),
        CatalogueEntry(key: "navigation.chip", title: "Chip", section: .navigation, tags: ["selection", "filter"]),
        CatalogueEntry(key: "navigation.commandPalette", title: "Command palette", section: .navigation, tags: ["search", "shortcut", "overlay"]),
        CatalogueEntry(key: "navigation.pagination", title: "Pagination", section: .navigation, tags: ["pages", "navigation"]),
        CatalogueEntry(key: "navigation.breadcrumb", title: "Breadcrumb", section: .navigation, tags: ["navigation", "path"]),
        CatalogueEntry(key: "navigation.wizard", title: "Wizard", section: .navigation, tags: ["multi-step", "flow"]),

        // Data display
        CatalogueEntry(key: "data.card", title: "Card", section: .dataDisplay, tags: ["container", "surface"]),
        CatalogueEntry(key: "data.listItem", title: "List item", section: .dataDisplay, tags: ["row", "list"]),
        CatalogueEntry(key: "data.avatar", title: "Avatar", section: .dataDisplay, tags: ["user", "image"]),
        CatalogueEntry(key: "data.avatarGroup", title: "Avatar group", section: .dataDisplay, tags: ["user", "stack"]),
        CatalogueEntry(key: "data.divider", title: "Divider", section: .dataDisplay, tags: ["separator", "rule"]),
    ]

    public static func bySection(_ section: CatalogueSection) -> [CatalogueEntry] {
        entries.filter { $0.section == section }
    }

    public static func search(_ query: String) -> [CatalogueEntry] {
        let needle = query.trimmingCharacters(in: .whitespaces).lowercased()
        guard !needle.isEmpty else { return entries }
        return entries.filter { entry in
            entry.title.lowercased().contains(needle) ||
                entry.tags.contains { $0.lowercased().contains(needle) }
        }
    }

    public static func byKey(_ key: String?) -> CatalogueEntry? {
        guard let key else { return nil }
        return entries.first { $0.key == key }
    }
}
