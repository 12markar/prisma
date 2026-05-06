package xyz.ksharma.prisma.catalogue.registry

import androidx.compose.runtime.Composable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import xyz.ksharma.prisma.catalogue.components.ButtonShowcase
import xyz.ksharma.prisma.catalogue.foundations.ColorShowcase
import xyz.ksharma.prisma.catalogue.foundations.ElevationShowcase
import xyz.ksharma.prisma.catalogue.foundations.MotionShowcase
import xyz.ksharma.prisma.catalogue.foundations.RadiusShowcase
import xyz.ksharma.prisma.catalogue.foundations.SpacingShowcase
import xyz.ksharma.prisma.catalogue.foundations.TypographyShowcase

/** A section grouping in the sidebar. Order here drives display order. */
public enum class CatalogueSection(public val title: String) {
    Foundations("Foundations"),
    Inputs("Inputs"),
    Feedback("Feedback"),
    Navigation("Navigation"),
    DataDisplay("Data display"),
}

/** A single catalogue entry (a foundation showcase or a component detail page).
 *  `content` is null when the entry is still a placeholder (no implementation yet). */
public data class CatalogueEntry(
    val key: String,
    val title: String,
    val section: CatalogueSection,
    val tags: ImmutableList<String>,
    val content: (@Composable () -> Unit)? = null,
)

/**
 * Static registry — order here drives display order within each section.
 * Adding a new component = adding one entry. Foundations have bespoke
 * showcase composables (Phase 1); component entries are placeholders until
 * their phase ships.
 */
public object CatalogueRegistry {

    public val entries: ImmutableList<CatalogueEntry> = persistentListOf(
        // Foundations — bespoke showcase pages (Phase 1)
        entry("foundation.typography", "Typography", CatalogueSection.Foundations, listOf("type", "specimen"))     { TypographyShowcase() },
        entry("foundation.colors",     "Colors",     CatalogueSection.Foundations, listOf("color", "palette"))     { ColorShowcase() },
        entry("foundation.icons",      "Icons",      CatalogueSection.Foundations, listOf("icon", "symbols")),
        entry("foundation.spacing",    "Spacing",    CatalogueSection.Foundations, listOf("spacing", "layout"))    { SpacingShowcase() },
        entry("foundation.elevation",  "Elevation",  CatalogueSection.Foundations, listOf("shadow", "depth"))      { ElevationShowcase() },
        entry("foundation.motion",     "Motion",     CatalogueSection.Foundations, listOf("animation", "duration")) { MotionShowcase() },
        entry("foundation.radius",     "Radius",     CatalogueSection.Foundations, listOf("corner", "radius"))     { RadiusShowcase() },

        // Inputs
        entry("input.button",     "Button",     CatalogueSection.Inputs, listOf("button", "action"))   { ButtonShowcase() },
        entry("input.textfield",  "TextField",  CatalogueSection.Inputs, listOf("input", "form")),
        entry("input.checkbox",   "Checkbox",   CatalogueSection.Inputs, listOf("input", "form", "selection")),
        entry("input.radio",      "Radio",      CatalogueSection.Inputs, listOf("input", "form", "selection")),
        entry("input.switch",     "Switch",     CatalogueSection.Inputs, listOf("input", "form", "toggle")),

        // Feedback
        entry("feedback.toast",       "Toast",        CatalogueSection.Feedback, listOf("feedback", "notification")),
        entry("feedback.modal",       "Modal",        CatalogueSection.Feedback, listOf("feedback", "dialog")),
        entry("feedback.bottomSheet", "Bottom sheet", CatalogueSection.Feedback, listOf("feedback", "sheet")),
        entry("feedback.loading",     "Loading",      CatalogueSection.Feedback, listOf("feedback", "progress")),
        entry("feedback.badge",       "Badge",        CatalogueSection.Feedback, listOf("feedback", "indicator")),

        // Navigation
        entry("navigation.tabs", "Tabs", CatalogueSection.Navigation, listOf("navigation")),
        entry("navigation.chip", "Chip", CatalogueSection.Navigation, listOf("selection", "filter")),

        // Data display
        entry("data.card", "Card", CatalogueSection.DataDisplay, listOf("container", "surface")),
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
        tags: List<String> = emptyList(),
        content: (@Composable () -> Unit)? = null,
    ): CatalogueEntry = CatalogueEntry(
        key = key,
        title = title,
        section = section,
        tags = tags.toImmutableList(),
        content = content,
    )
}
