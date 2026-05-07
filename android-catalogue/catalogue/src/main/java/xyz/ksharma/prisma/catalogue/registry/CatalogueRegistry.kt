package xyz.ksharma.prisma.catalogue.registry

import androidx.compose.runtime.Composable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import xyz.ksharma.prisma.catalogue.components.AutocompleteShowcase
import xyz.ksharma.prisma.catalogue.components.AvatarGroupShowcase
import xyz.ksharma.prisma.catalogue.components.AvatarShowcase
import xyz.ksharma.prisma.catalogue.components.BadgeShowcase
import xyz.ksharma.prisma.catalogue.components.BannerShowcase
import xyz.ksharma.prisma.catalogue.components.BottomSheetShowcase
import xyz.ksharma.prisma.catalogue.components.BreadcrumbShowcase
import xyz.ksharma.prisma.catalogue.components.ButtonShowcase
import xyz.ksharma.prisma.catalogue.components.CardShowcase
import xyz.ksharma.prisma.catalogue.components.CheckboxShowcase
import xyz.ksharma.prisma.catalogue.components.ChipShowcase
import xyz.ksharma.prisma.catalogue.components.ColorPickerShowcase
import xyz.ksharma.prisma.catalogue.components.CommandPaletteShowcase
import xyz.ksharma.prisma.catalogue.components.DatePickerShowcase
import xyz.ksharma.prisma.catalogue.components.DividerShowcase
import xyz.ksharma.prisma.catalogue.components.DrawerShowcase
import xyz.ksharma.prisma.catalogue.components.EmptyStateShowcase
import xyz.ksharma.prisma.catalogue.components.ListItemShowcase
import xyz.ksharma.prisma.catalogue.components.LoadingShowcase
import xyz.ksharma.prisma.catalogue.components.ModalShowcase
import xyz.ksharma.prisma.catalogue.components.PaginationShowcase
import xyz.ksharma.prisma.catalogue.components.PopoverShowcase
import xyz.ksharma.prisma.catalogue.components.RadioShowcase
import xyz.ksharma.prisma.catalogue.components.SearchBarShowcase
import xyz.ksharma.prisma.catalogue.components.SegmentedControlShowcase
import xyz.ksharma.prisma.catalogue.components.SkeletonShowcase
import xyz.ksharma.prisma.catalogue.components.SliderShowcase
import xyz.ksharma.prisma.catalogue.components.StepperShowcase
import xyz.ksharma.prisma.catalogue.components.SwitchShowcase
import xyz.ksharma.prisma.catalogue.components.TabsShowcase
import xyz.ksharma.prisma.catalogue.components.TagInputShowcase
import xyz.ksharma.prisma.catalogue.components.TextFieldShowcase
import xyz.ksharma.prisma.catalogue.components.TimePickerShowcase
import xyz.ksharma.prisma.catalogue.components.ToastShowcase
import xyz.ksharma.prisma.catalogue.components.TooltipShowcase
import xyz.ksharma.prisma.catalogue.components.WizardShowcase
import xyz.ksharma.prisma.catalogue.foundations.ColorShowcase
import xyz.ksharma.prisma.catalogue.foundations.ElevationShowcase
import xyz.ksharma.prisma.catalogue.foundations.IconsShowcase
import xyz.ksharma.prisma.catalogue.foundations.MotionShowcase
import xyz.ksharma.prisma.catalogue.foundations.RadiusShowcase
import xyz.ksharma.prisma.catalogue.foundations.SpacingShowcase
import xyz.ksharma.prisma.catalogue.foundations.TypographyShowcase
import xyz.ksharma.prisma.catalogue.sdui.SduiScreen

public enum class CatalogueSection(public val title: String) {
    Foundations("Foundations"),
    Inputs("Inputs"),
    Feedback("Feedback"),
    Navigation("Navigation"),
    DataDisplay("Data display"),
    ServerDriven("Server-driven"),
}

public data class CatalogueEntry(
    val key: String,
    val title: String,
    val section: CatalogueSection,
    val tags: ImmutableList<String>,
    val content: (@Composable () -> Unit)? = null,
)

public object CatalogueRegistry {

    public val entries: ImmutableList<CatalogueEntry> = persistentListOf(
        // Foundations
        entry("foundation.typography", "Typography", CatalogueSection.Foundations, listOf("type", "specimen"))     { TypographyShowcase() },
        entry("foundation.colors",     "Colors",     CatalogueSection.Foundations, listOf("color", "palette"))     { ColorShowcase() },
        entry("foundation.icons",      "Icons",      CatalogueSection.Foundations, listOf("icon", "symbols"))      { IconsShowcase() },
        entry("foundation.spacing",    "Spacing",    CatalogueSection.Foundations, listOf("spacing", "layout"))    { SpacingShowcase() },
        entry("foundation.elevation",  "Elevation",  CatalogueSection.Foundations, listOf("shadow", "depth"))      { ElevationShowcase() },
        entry("foundation.motion",     "Motion",     CatalogueSection.Foundations, listOf("animation", "duration")) { MotionShowcase() },
        entry("foundation.radius",     "Radius",     CatalogueSection.Foundations, listOf("corner", "radius"))     { RadiusShowcase() },

        // Inputs
        entry("input.button",            "Button",            CatalogueSection.Inputs, listOf("button", "action"))                  { ButtonShowcase() },
        entry("input.textfield",         "TextField",         CatalogueSection.Inputs, listOf("input", "form"))                     { TextFieldShowcase() },
        entry("input.checkbox",          "Checkbox",          CatalogueSection.Inputs, listOf("input", "form", "selection"))        { CheckboxShowcase() },
        entry("input.radio",             "Radio",             CatalogueSection.Inputs, listOf("input", "form", "selection"))        { RadioShowcase() },
        entry("input.switch",            "Switch",            CatalogueSection.Inputs, listOf("input", "form", "toggle"))           { SwitchShowcase() },
        entry("input.slider",            "Slider",            CatalogueSection.Inputs, listOf("input", "range", "value"))           { SliderShowcase() },
        entry("input.segmentedControl",  "Segmented control", CatalogueSection.Inputs, listOf("input", "selection", "tabs"))        { SegmentedControlShowcase() },
        entry("input.searchBar",         "Search bar",        CatalogueSection.Inputs, listOf("input", "search"))                   { SearchBarShowcase() },
        entry("input.autocomplete",      "Autocomplete",      CatalogueSection.Inputs, listOf("input", "search", "combobox"))       { AutocompleteShowcase() },
        entry("input.stepper",           "Stepper",           CatalogueSection.Inputs, listOf("input", "number"))                   { StepperShowcase() },
        entry("input.tagInput",          "Tag input",         CatalogueSection.Inputs, listOf("input", "chip", "multi"))            { TagInputShowcase() },
        entry("input.datePicker",        "Date picker",       CatalogueSection.Inputs, listOf("input", "date", "calendar"))         { DatePickerShowcase() },
        entry("input.timePicker",        "Time picker",       CatalogueSection.Inputs, listOf("input", "time", "clock"))            { TimePickerShowcase() },
        entry("input.colorPicker",       "Color picker",      CatalogueSection.Inputs, listOf("input", "color", "swatch"))          { ColorPickerShowcase() },

        // Feedback
        entry("feedback.toast",       "Toast",        CatalogueSection.Feedback, listOf("feedback", "notification"))           { ToastShowcase() },
        entry("feedback.banner",      "Banner",       CatalogueSection.Feedback, listOf("feedback", "alert", "inline"))        { BannerShowcase() },
        entry("feedback.modal",       "Modal",        CatalogueSection.Feedback, listOf("feedback", "dialog"))                 { ModalShowcase() },
        entry("feedback.bottomSheet", "Bottom sheet", CatalogueSection.Feedback, listOf("feedback", "sheet"))                  { BottomSheetShowcase() },
        entry("feedback.popover",     "Popover",      CatalogueSection.Feedback, listOf("feedback", "overlay"))                { PopoverShowcase() },
        entry("feedback.tooltip",     "Tooltip",      CatalogueSection.Feedback, listOf("feedback", "hint"))                   { TooltipShowcase() },
        entry("feedback.loading",     "Loading",      CatalogueSection.Feedback, listOf("feedback", "progress"))               { LoadingShowcase() },
        entry("feedback.skeleton",    "Skeleton",     CatalogueSection.Feedback, listOf("feedback", "placeholder", "loading")) { SkeletonShowcase() },
        entry("feedback.badge",       "Badge",        CatalogueSection.Feedback, listOf("feedback", "indicator"))              { BadgeShowcase() },
        entry("feedback.emptyState",  "Empty state",  CatalogueSection.Feedback, listOf("feedback", "empty"))                  { EmptyStateShowcase() },
        entry("feedback.drawer",      "Drawer",       CatalogueSection.Feedback, listOf("feedback", "side", "sheet"))          { DrawerShowcase() },

        // Navigation
        entry("navigation.tabs",            "Tabs",            CatalogueSection.Navigation, listOf("navigation"))                    { TabsShowcase() },
        entry("navigation.chip",            "Chip",            CatalogueSection.Navigation, listOf("selection", "filter"))           { ChipShowcase() },
        entry("navigation.commandPalette",  "Command palette", CatalogueSection.Navigation, listOf("search", "shortcut", "overlay")) { CommandPaletteShowcase() },
        entry("navigation.pagination",      "Pagination",      CatalogueSection.Navigation, listOf("pages", "navigation"))           { PaginationShowcase() },
        entry("navigation.breadcrumb",      "Breadcrumb",      CatalogueSection.Navigation, listOf("navigation", "path"))            { BreadcrumbShowcase() },
        entry("navigation.wizard",          "Wizard",          CatalogueSection.Navigation, listOf("multi-step", "flow"))            { WizardShowcase() },

        // Data display
        entry("data.card",        "Card",         CatalogueSection.DataDisplay, listOf("container", "surface")) { CardShowcase() },
        entry("data.listItem",    "List item",    CatalogueSection.DataDisplay, listOf("row", "list"))          { ListItemShowcase() },
        entry("data.avatar",      "Avatar",       CatalogueSection.DataDisplay, listOf("user", "image"))        { AvatarShowcase() },
        entry("data.avatarGroup", "Avatar group", CatalogueSection.DataDisplay, listOf("user", "stack"))        { AvatarGroupShowcase() },
        entry("data.divider",     "Divider",      CatalogueSection.DataDisplay, listOf("separator", "rule"))    { DividerShowcase() },

        // Server-driven UI live demo — connects to ws://10.0.2.2:7331/ws
        entry("sdui.live", "SDUI live demo", CatalogueSection.ServerDriven, listOf("server", "json", "live", "websocket")) { SduiScreen() },
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
