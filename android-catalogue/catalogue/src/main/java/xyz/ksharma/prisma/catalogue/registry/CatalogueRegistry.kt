package xyz.ksharma.prisma.catalogue.registry

import androidx.compose.runtime.Composable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/** A section grouping in the sidebar. Order here drives display order. */
public enum class CatalogueSection(public val title: String) {
    Foundations("Foundations"),
    Inputs("Inputs"),
    Feedback("Feedback"),
    Navigation("Navigation"),
    DataDisplay("Data display"),
}

/** A single catalogue entry (a foundation showcase or a component detail page). */
public data class CatalogueEntry(
    val key: String,
    val title: String,
    val section: CatalogueSection,
    val tags: ImmutableList<String>,
    val content: @Composable () -> Unit,
)

/**
 * Static registry — order here drives display order within each section.
 * Adding a new component = adding one entry. Phase 0 ships placeholder content
 * for every entry; real component implementations replace `content` per phase.
 */
public object CatalogueRegistry {

    public val entries: ImmutableList<CatalogueEntry> = persistentListOf(
        // Foundations
        entry("foundation.typography", "Typography", CatalogueSection.Foundations, "type", "specimen"),
        entry("foundation.colors", "Colors", CatalogueSection.Foundations, "color", "palette", "swatches"),
        entry("foundation.icons", "Icons", CatalogueSection.Foundations, "icon", "symbols"),
        entry("foundation.spacing", "Spacing", CatalogueSection.Foundations, "spacing", "layout"),
        entry("foundation.elevation", "Elevation", CatalogueSection.Foundations, "shadow", "depth"),
        entry("foundation.motion", "Motion", CatalogueSection.Foundations, "animation", "duration", "easing"),
        entry("foundation.radius", "Radius", CatalogueSection.Foundations, "corner", "radius"),

        // Inputs
        entry("input.button", "Button", CatalogueSection.Inputs, "button", "action"),
        entry("input.textfield", "TextField", CatalogueSection.Inputs, "input", "form"),
        entry("input.checkbox", "Checkbox", CatalogueSection.Inputs, "input", "form", "selection"),
        entry("input.radio", "Radio", CatalogueSection.Inputs, "input", "form", "selection"),
        entry("input.switch", "Switch", CatalogueSection.Inputs, "input", "form", "toggle"),

        // Feedback
        entry("feedback.toast", "Toast", CatalogueSection.Feedback, "feedback", "notification"),
        entry("feedback.modal", "Modal", CatalogueSection.Feedback, "feedback", "dialog"),
        entry("feedback.bottomSheet", "Bottom sheet", CatalogueSection.Feedback, "feedback", "sheet"),
        entry("feedback.loading", "Loading", CatalogueSection.Feedback, "feedback", "progress"),
        entry("feedback.badge", "Badge", CatalogueSection.Feedback, "feedback", "indicator"),

        // Navigation
        entry("navigation.tabs", "Tabs", CatalogueSection.Navigation, "navigation"),
        entry("navigation.chip", "Chip", CatalogueSection.Navigation, "selection", "filter"),

        // Data display
        entry("data.card", "Card", CatalogueSection.DataDisplay, "container", "surface"),
    )

    public val sections: ImmutableList<CatalogueSection> =
        entries.map { it.section }.distinct().toImmutableList()

    public fun bySection(section: CatalogueSection): ImmutableList<CatalogueEntry> =
        entries.filter { it.section == section }.toImmutableList()

    public fun search(query: String): ImmutableList<CatalogueEntry> {
        if (query.isBlank()) return entries
        val q = query.trim().lowercase()
        return entries
            .filter { entry ->
                entry.title.lowercase().contains(q) || entry.tags.any { it.lowercase().contains(q) }
            }
            .toImmutableList()
    }

    public fun byKey(key: String?): CatalogueEntry? =
        if (key == null) null else entries.firstOrNull { it.key == key }

    private fun entry(
        key: String,
        title: String,
        section: CatalogueSection,
        vararg tags: String,
    ): CatalogueEntry = CatalogueEntry(
        key = key,
        title = title,
        section = section,
        tags = tags.toList().toImmutableList(),
        content = { /* Placeholder until Phase 1+ implementations land. */ },
    )
}
