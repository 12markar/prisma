package xyz.ksharma.prisma.catalogue.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import xyz.ksharma.prisma.catalogue.playground.A11yPanel
import xyz.ksharma.prisma.catalogue.playground.BoolKnobRow
import xyz.ksharma.prisma.catalogue.playground.EnumKnobRow
import xyz.ksharma.prisma.catalogue.playground.IntKnobRow
import xyz.ksharma.prisma.catalogue.playground.PlaygroundScaffold
import xyz.ksharma.prisma.catalogue.playground.StateCell
import xyz.ksharma.prisma.catalogue.playground.StringKnobRow
import xyz.ksharma.prisma.components.autocomplete.PrismaAutocomplete
import xyz.ksharma.prisma.components.avatar.PrismaAvatar
import xyz.ksharma.prisma.components.avatar.PrismaAvatarSize
import xyz.ksharma.prisma.components.avatar.PrismaAvatarStatus
import xyz.ksharma.prisma.components.avatargroup.PrismaAvatarGroup
import xyz.ksharma.prisma.components.badge.PrismaBadgeStatus
import xyz.ksharma.prisma.components.badge.PrismaCountBadge
import xyz.ksharma.prisma.components.badge.PrismaDotBadge
import xyz.ksharma.prisma.components.banner.PrismaBanner
import xyz.ksharma.prisma.components.banner.PrismaBannerKind
import xyz.ksharma.prisma.components.bottomsheet.PrismaBottomSheet
import xyz.ksharma.prisma.components.breadcrumb.PrismaBreadcrumb
import xyz.ksharma.prisma.components.breadcrumb.PrismaBreadcrumbItem
import xyz.ksharma.prisma.components.button.PrismaButton
import xyz.ksharma.prisma.components.button.PrismaButtonVariant
import xyz.ksharma.prisma.components.card.PrismaCard
import xyz.ksharma.prisma.components.card.PrismaCardVariant
import xyz.ksharma.prisma.components.chip.PrismaChip
import xyz.ksharma.prisma.components.chip.PrismaChipVariant
import xyz.ksharma.prisma.components.colorpicker.PrismaColorPicker
import xyz.ksharma.prisma.components.commandpalette.PrismaCommand
import xyz.ksharma.prisma.components.commandpalette.PrismaCommandPalette
import xyz.ksharma.prisma.components.datetime.PrismaDatePicker
import xyz.ksharma.prisma.components.datetime.PrismaTimePicker
import xyz.ksharma.prisma.components.divider.PrismaDividerWeight
import xyz.ksharma.prisma.components.divider.PrismaHorizontalDivider
import xyz.ksharma.prisma.components.drawer.PrismaDrawer
import xyz.ksharma.prisma.components.empty.PrismaEmptyState
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.components.listitem.PrismaListItem
import xyz.ksharma.prisma.components.loading.PrismaCircularLoading
import xyz.ksharma.prisma.components.loading.PrismaLinearLoading
import xyz.ksharma.prisma.components.loading.PrismaLoadingSize
import xyz.ksharma.prisma.components.modal.PrismaModal
import xyz.ksharma.prisma.components.pagination.PrismaPagination
import xyz.ksharma.prisma.components.popover.PrismaPopover
import xyz.ksharma.prisma.components.radio.PrismaRadio
import xyz.ksharma.prisma.components.searchbar.PrismaSearchBar
import xyz.ksharma.prisma.components.segmented.PrismaSegmentedControl
import xyz.ksharma.prisma.components.skeleton.PrismaSkeletonBlock
import xyz.ksharma.prisma.components.skeleton.PrismaSkeletonCircle
import xyz.ksharma.prisma.components.skeleton.PrismaSkeletonLine
import xyz.ksharma.prisma.components.slider.PrismaSlider
import xyz.ksharma.prisma.components.stepper.PrismaStepper
import xyz.ksharma.prisma.components.switchctl.PrismaSwitch
import xyz.ksharma.prisma.components.tabs.PrismaTabs
import xyz.ksharma.prisma.components.taginput.PrismaTagInput
import xyz.ksharma.prisma.components.toast.PrismaToast
import xyz.ksharma.prisma.components.toast.PrismaToastKind
import xyz.ksharma.prisma.components.tooltip.PrismaTooltip
import xyz.ksharma.prisma.components.wizard.PrismaWizardSteps
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

private val Stack: @Composable (List<@Composable () -> Unit>) -> Unit = { content ->
    Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4), modifier = Modifier.fillMaxWidth()) {
        content.forEach { it() }
    }
}

@Composable private fun H(text: String) {
    Text(text.uppercase(), style = PrismaTypography.LabelSm, color = PrismaSemanticColors.TextTertiary.themed())
}

// region — Inputs

@Composable public fun SwitchShowcase() {
    var checked by rememberSaveable { mutableStateOf(true) }
    var label by rememberSaveable { mutableStateOf("Push notifications") }
    var helper by rememberSaveable { mutableStateOf("Get alerts when you're mentioned.") }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var withLabel by rememberSaveable { mutableStateOf(true) }

    PlaygroundScaffold(
        preview = {
            PrismaSwitch(
                checked = checked,
                onCheckedChange = if (enabled) ({ checked = it }) else null,
                label = label.takeIf { withLabel && it.isNotBlank() },
                helperText = helper.takeIf { withLabel && it.isNotBlank() },
                enabled = enabled,
            )
        },
        knobs = {
            BoolKnobRow("Checked", checked, { checked = it })
            BoolKnobRow("Enabled", enabled, { enabled = it })
            BoolKnobRow("With label / helper", withLabel, { withLabel = it })
            StringKnobRow("Label", label, { label = it })
            StringKnobRow("Helper", helper, { helper = it })
        },
        code = {
            "PrismaSwitch(checked = $checked, onCheckedChange = { /* … */ }" +
                (if (withLabel && label.isNotBlank()) ", label = \"$label\"" else "") +
                (if (!enabled) ", enabled = false" else "") + ")"
        },
        states = {
            StateCell("On") { PrismaSwitch(checked = true, onCheckedChange = {}, label = "Push notifications") }
            StateCell("Off") { PrismaSwitch(checked = false, onCheckedChange = {}, label = "Auto-sync") }
            StateCell("Disabled (on)") { PrismaSwitch(checked = true, onCheckedChange = null, label = "Locked on", enabled = false) }
            StateCell("Disabled (off)") { PrismaSwitch(checked = false, onCheckedChange = null, label = "Locked off", enabled = false) }
            StateCell("Standalone") { PrismaSwitch(checked = true, onCheckedChange = {}) }
        },
        a11y = {
            A11yPanel(
                role = "Switch",
                minTouchTarget = "48 × 48 dp",
                bullets = listOf(
                    "On/off state is announced as part of the role; the label is read alongside.",
                    "Selection haptic fires on each toggle so the change is felt as well as heard.",
                    "Disabled state communicated by the role; visual dim is supporting, not primary.",
                ),
            )
        },
    )
}

@Composable public fun RadioShowcase() {
    var optionsCsv by rememberSaveable { mutableStateOf("Monthly, Yearly, Free") }
    var selectedIdx by rememberSaveable { mutableStateOf(0) }
    var enabled by rememberSaveable { mutableStateOf(true) }

    val options = remember(optionsCsv) {
        optionsCsv.split(',').map { it.trim() }.filter { it.isNotEmpty() }.ifEmpty { listOf("Option") }
    }
    val safeIdx = selectedIdx.coerceIn(0, options.lastIndex)

    PlaygroundScaffold(
        preview = {
            Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
                options.forEachIndexed { idx, label ->
                    PrismaRadio(
                        selected = idx == safeIdx,
                        onClick = if (enabled) ({ selectedIdx = idx }) else null,
                        label = label,
                        enabled = enabled,
                    )
                }
            }
        },
        knobs = {
            StringKnobRow("Options (comma separated)", optionsCsv, { optionsCsv = it; selectedIdx = 0 })
            IntKnobRow("Selected index", safeIdx, range = 0..options.lastIndex.coerceAtLeast(0), onChange = { selectedIdx = it })
            BoolKnobRow("Enabled", enabled, { enabled = it })
        },
        code = {
            "PrismaRadio(\n" +
                "    selected = idx == $safeIdx,\n" +
                "    onClick = { selectedIdx = idx },\n" +
                "    label = \"\${options[idx]}\",\n" +
                (if (!enabled) "    enabled = false,\n" else "") +
                ")"
        },
        states = {
            StateCell("Selected") { PrismaRadio(selected = true, onClick = {}, label = "Selected option") }
            StateCell("Unselected") { PrismaRadio(selected = false, onClick = {}, label = "Unselected option") }
            StateCell("With helper") { PrismaRadio(selected = false, onClick = {}, label = "Yearly", helperText = "$90 per year (save 17%).") }
            StateCell("Disabled") { PrismaRadio(selected = false, onClick = null, label = "Locked", enabled = false) }
        },
        a11y = {
            A11yPanel(
                role = "RadioButton (within selectableGroup)",
                minTouchTarget = "48 × 48 dp",
                bullets = listOf(
                    "Wrap the radio set in selectableGroup so screen readers announce \"option N of M\".",
                    "Only one radio in a group can be selected; the selected state is announced.",
                    "Helper text is read after the label; keep helpers brief.",
                ),
            )
        },
    )
}

@Composable public fun SliderShowcase() {
    var label by rememberSaveable { mutableStateOf("Volume") }
    var value by rememberSaveable { mutableStateOf(40) }
    var min by rememberSaveable { mutableStateOf(0) }
    var max by rememberSaveable { mutableStateOf(100) }
    var stepCount by rememberSaveable { mutableStateOf(0) }
    var disabled by rememberSaveable { mutableStateOf(false) }
    var showValue by rememberSaveable { mutableStateOf(true) }
    var asPercent by rememberSaveable { mutableStateOf(true) }

    val safeMax = if (max > min) max else min + 1
    val coercedValue = value.coerceIn(min, safeMax)

    PlaygroundScaffold(
        preview = {
            PrismaSlider(
                value = coercedValue.toFloat(),
                onValueChange = { value = it.toInt() },
                valueRange = min.toFloat()..safeMax.toFloat(),
                steps = stepCount,
                enabled = !disabled,
                label = label.takeIf { it.isNotBlank() },
                showValue = showValue,
                valueFormatter = {
                    if (asPercent) "${it.toInt()}%" else it.toInt().toString()
                },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        knobs = {
            StringKnobRow("Label", label, { label = it }, placeholder = "Slider label")
            IntKnobRow("Min", min, range = 0..100, onChange = { min = it })
            IntKnobRow("Max", max, range = 1..100, onChange = { max = it })
            IntKnobRow(
                label = "Steps (0 = continuous)",
                value = stepCount,
                range = 0..10,
                onChange = { stepCount = it },
            )
            BoolKnobRow("Disabled", disabled, { disabled = it })
            BoolKnobRow("Show current value", showValue, { showValue = it })
            BoolKnobRow("Format as percentage", asPercent, { asPercent = it })
        },
        code = {
            "PrismaSlider(\n    value = $coercedValue.toFloat(),\n    onValueChange = { /* … */ },\n    valueRange = ${min}f..${safeMax}f,\n" +
                (if (stepCount > 0) "    steps = $stepCount,\n" else "") +
                (if (label.isNotBlank()) "    label = \"$label\",\n" else "") +
                (if (!showValue) "    showValue = false,\n" else "") +
                (if (disabled) "    enabled = false,\n" else "") +
                ")"
        },
        states = {
            StateCell("Continuous") {
                var v by rememberSaveable { mutableStateOf(0.4f) }
                PrismaSlider(value = v, onValueChange = { v = it }, label = "Volume", valueFormatter = { "${(it * 100).toInt()}%" })
            }
            StateCell("Stepped (1–5)") {
                var s by rememberSaveable { mutableStateOf(2f) }
                PrismaSlider(value = s, onValueChange = { s = it }, valueRange = 1f..5f, steps = 3, label = "Rating", valueFormatter = { it.toInt().toString() })
            }
            StateCell("Disabled") {
                PrismaSlider(value = 0.7f, onValueChange = {}, label = "Read-only", enabled = false)
            }
        },
        a11y = {
            A11yPanel(
                role = "SeekBar / Slider",
                minTouchTarget = "Thumb 48 × 48 dp hit area",
                bullets = listOf(
                    "Value is announced via progressSemantics; supply a valueFormatter for percent/star/etc.",
                    "Min and max are exposed so screen readers can announce \"5 of 10\" or \"60 percent\".",
                    "Stepped sliders snap; assistive tech increments / decrements by step value.",
                ),
            )
        },
    )
}

@Composable public fun SegmentedControlShowcase() {
    var optionsCsv by rememberSaveable { mutableStateOf("Day, Week, Month, Year") }
    var selectedIdx by rememberSaveable { mutableStateOf(0) }
    val options: ImmutableList<String> = remember(optionsCsv) {
        optionsCsv.split(',').map { it.trim() }.filter { it.isNotEmpty() }.ifEmpty { listOf("Option") }.toImmutableList()
    }
    val safeIdx = selectedIdx.coerceIn(0, options.lastIndex)
    PlaygroundScaffold(
        preview = {
            PrismaSegmentedControl(options = options, selected = options[safeIdx], onSelect = { selectedIdx = options.indexOf(it).coerceAtLeast(0) })
        },
        knobs = {
            StringKnobRow("Options (comma separated)", optionsCsv, { optionsCsv = it; selectedIdx = 0 })
            IntKnobRow("Selected index", safeIdx, range = 0..options.lastIndex.coerceAtLeast(0), onChange = { selectedIdx = it })
        },
        code = {
            "PrismaSegmentedControl(\n    options = persistentListOf(${options.joinToString { "\"$it\"" }}),\n    selected = \"${options[safeIdx]}\",\n    onSelect = { /* … */ },\n)"
        },
        states = {
            StateCell("Two") {
                var s by rememberSaveable { mutableStateOf("Off") }
                PrismaSegmentedControl(options = persistentListOf("Off", "On"), selected = s, onSelect = { s = it })
            }
            StateCell("Three") {
                var s by rememberSaveable { mutableStateOf("Day") }
                PrismaSegmentedControl(options = persistentListOf("Day", "Week", "Month"), selected = s, onSelect = { s = it })
            }
            StateCell("Sizes") {
                var s by rememberSaveable { mutableStateOf("M") }
                PrismaSegmentedControl(options = persistentListOf("S", "M", "L"), selected = s, onSelect = { s = it })
            }
        },
        a11y = {
            A11yPanel(
                role = "Tab (within selectableGroup)",
                minTouchTarget = "48 dp height across the segment",
                bullets = listOf(
                    "Each segment is a Tab; the row is a selectableGroup so \"selected N of M\" is announced.",
                    "Distinct from PrismaTabs: segmented control is for filtering content in place, not navigation.",
                    "Avoid >5 options — at that point use a Picker / dropdown instead.",
                ),
            )
        },
    )
}

@Composable public fun SearchBarShowcase() {
    var query by rememberSaveable { mutableStateOf("") }
    var placeholder by rememberSaveable { mutableStateOf("Search the catalogue") }
    var enabled by rememberSaveable { mutableStateOf(true) }
    PlaygroundScaffold(
        preview = { PrismaSearchBar(value = query, onValueChange = { query = it }, placeholder = placeholder, enabled = enabled) },
        knobs = {
            StringKnobRow("Placeholder", placeholder, { placeholder = it })
            BoolKnobRow("Enabled", enabled, { enabled = it })
        },
        code = {
            "PrismaSearchBar(value = query, onValueChange = { query = it }, placeholder = \"$placeholder\"" +
                (if (!enabled) ", enabled = false" else "") + ")"
        },
        states = {
            StateCell("Empty") {
                var s by rememberSaveable { mutableStateOf("") }
                PrismaSearchBar(value = s, onValueChange = { s = it }, placeholder = "Search…")
            }
            StateCell("With query") {
                var s by rememberSaveable { mutableStateOf("compose") }
                PrismaSearchBar(value = s, onValueChange = { s = it })
            }
            StateCell("Disabled") {
                PrismaSearchBar(value = "Read-only", onValueChange = {}, enabled = false)
            }
        },
        a11y = {
            A11yPanel(
                role = "EditText (search semantics)",
                minTouchTarget = "48 dp height",
                bullets = listOf(
                    "Placeholder is announced as a hint, not as the label — supply explicit label when needed.",
                    "IME action set to Search so the keyboard returns the search affordance.",
                    "Clearing the field is a single action read as \"Clear search\".",
                ),
            )
        },
    )
}

@Composable public fun StepperShowcase() {
    var qty by rememberSaveable { mutableStateOf(1) }
    var min by rememberSaveable { mutableStateOf(0) }
    var max by rememberSaveable { mutableStateOf(10) }
    var step by rememberSaveable { mutableStateOf(1) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    val safeMax = if (max > min) max else min + 1
    val safeStep = step.coerceAtLeast(1)
    PlaygroundScaffold(
        preview = {
            Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
                PrismaStepper(value = qty.coerceIn(min, safeMax), onValueChange = { qty = it }, range = min..safeMax, step = safeStep, enabled = enabled)
                Text("Selected: $qty", style = PrismaTypography.BodyMd, color = PrismaSemanticColors.TextSecondary.themed())
            }
        },
        knobs = {
            IntKnobRow("Min", min, range = 0..50, onChange = { min = it })
            IntKnobRow("Max", max, range = 1..100, onChange = { max = it })
            IntKnobRow("Step", step, range = 1..10, onChange = { step = it })
            BoolKnobRow("Enabled", enabled, { enabled = it })
        },
        code = {
            "PrismaStepper(value = qty, onValueChange = { qty = it }, range = $min..$safeMax" +
                (if (safeStep != 1) ", step = $safeStep" else "") +
                (if (!enabled) ", enabled = false" else "") + ")"
        },
        states = {
            StateCell("Default") {
                var v by rememberSaveable { mutableStateOf(1) }
                PrismaStepper(value = v, onValueChange = { v = it }, range = 0..10)
            }
            StateCell("At max") {
                var v by rememberSaveable { mutableStateOf(10) }
                PrismaStepper(value = v, onValueChange = { v = it }, range = 0..10)
            }
            StateCell("Disabled") { PrismaStepper(value = 5, onValueChange = {}, range = 0..10, enabled = false) }
        },
        a11y = {
            A11yPanel(
                role = "Stepper (Increment/Decrement actions)",
                minTouchTarget = "48 × 48 dp per button",
                bullets = listOf(
                    "Each button is independently focusable; current value is announced when changed.",
                    "Disabled at min/max — buttons individually disabled, value still readable.",
                    "Custom actions (\"increment\", \"decrement\") allow keyboard / switch-control invocation.",
                ),
            )
        },
    )
}

@Composable public fun TagInputShowcase() {
    var tags: ImmutableList<String> by rememberSaveable { mutableStateOf(persistentListOf("kotlin", "compose", "design system")) }
    var label by rememberSaveable { mutableStateOf("Topics") }
    var placeholder by rememberSaveable { mutableStateOf("Add a topic") }
    var enabled by rememberSaveable { mutableStateOf(true) }
    PlaygroundScaffold(
        preview = {
            PrismaTagInput(tags = tags, onTagsChange = { tags = it }, label = label.takeIf { it.isNotBlank() }, placeholder = placeholder, enabled = enabled)
        },
        knobs = {
            StringKnobRow("Label", label, { label = it })
            StringKnobRow("Placeholder", placeholder, { placeholder = it })
            BoolKnobRow("Enabled", enabled, { enabled = it })
        },
        code = {
            "PrismaTagInput(\n    tags = tags,\n    onTagsChange = { tags = it },\n" +
                (if (label.isNotBlank()) "    label = \"$label\",\n" else "") +
                "    placeholder = \"$placeholder\",\n" +
                (if (!enabled) "    enabled = false,\n" else "") +
                ")"
        },
        states = {
            StateCell("Empty") {
                var t: ImmutableList<String> by rememberSaveable { mutableStateOf(persistentListOf()) }
                PrismaTagInput(tags = t, onTagsChange = { t = it }, label = "Tags", placeholder = "Type and Enter")
            }
            StateCell("Filled") {
                var t: ImmutableList<String> by rememberSaveable { mutableStateOf(persistentListOf("swift", "swiftui")) }
                PrismaTagInput(tags = t, onTagsChange = { t = it }, label = "Topics")
            }
        },
        a11y = {
            A11yPanel(
                role = "EditText with associated chip list",
                minTouchTarget = "48 dp per chip; chip × also 48 × 48 dp",
                bullets = listOf(
                    "Adding a tag triggers a polite live-region announcement (\"swift added\").",
                    "Each chip's × is its own focusable element; remove announces (\"swift removed\").",
                    "Backspace on empty input deletes the last chip and announces it.",
                ),
            )
        },
    )
}

@Composable public fun AutocompleteShowcase() {
    var corpusCsv by rememberSaveable { mutableStateOf("Bangalore, Bangkok, Beijing, Berlin, Boston, Brisbane, Cairo, Delhi, Edinburgh") }
    var label by rememberSaveable { mutableStateOf("City") }
    var placeholder by rememberSaveable { mutableStateOf("Type a city name") }
    var v by rememberSaveable { mutableStateOf("") }
    val all: ImmutableList<String> = remember(corpusCsv) {
        corpusCsv.split(',').map { it.trim() }.filter { it.isNotEmpty() }.toImmutableList()
    }
    PlaygroundScaffold(
        preview = {
            PrismaAutocomplete(
                value = v,
                onValueChange = { v = it },
                suggestions = if (v.isBlank()) persistentListOf() else all.filter { it.lowercase().contains(v.lowercase()) }.toImmutableList(),
                onSelect = { v = it },
                label = label.takeIf { it.isNotBlank() },
                placeholder = placeholder,
            )
        },
        knobs = {
            StringKnobRow("Corpus (comma separated)", corpusCsv, { corpusCsv = it })
            StringKnobRow("Label", label, { label = it })
            StringKnobRow("Placeholder", placeholder, { placeholder = it })
        },
        code = {
            "PrismaAutocomplete(\n    value = query,\n    onValueChange = { query = it },\n    suggestions = corpus.filter { it.contains(query, ignoreCase = true) },\n    onSelect = { query = it },\n    label = \"$label\",\n)"
        },
        states = {
            StateCell("Default") {
                var s by rememberSaveable { mutableStateOf("") }
                PrismaAutocomplete(value = s, onValueChange = { s = it }, suggestions = persistentListOf(), onSelect = { s = it }, label = "Country")
            }
        },
        a11y = {
            A11yPanel(
                role = "Combobox (input + listbox popup)",
                minTouchTarget = "48 dp per suggestion row",
                bullets = listOf(
                    "Suggestion count is announced when the popup opens (\"6 suggestions\").",
                    "Arrow up/down moves focus through suggestions while keeping caret in the input.",
                    "Enter / tap selects; Escape closes the popup and returns focus to input.",
                ),
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable public fun DatePickerShowcase() {
    PlaygroundScaffold(
        preview = { PrismaDatePicker() },
        code = { "val state = rememberDatePickerState()\nPrismaDatePicker(state = state)" },
        states = {
            StateCell("Default", minWidth = androidx.compose.ui.unit.Dp(360f)) { PrismaDatePicker() }
        },
        a11y = {
            A11yPanel(
                role = "Calendar (M3 native a11y)",
                minTouchTarget = "Per Material3 spec — 48dp grid cells",
                bullets = listOf(
                    "M3 DatePicker handles month / year navigation announcements.",
                    "Today is announced; selected date announced on commit.",
                    "Arrow keys move by day; PageUp / PageDown by month.",
                ),
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable public fun TimePickerShowcase() {
    PlaygroundScaffold(
        preview = { PrismaTimePicker() },
        code = { "val state = rememberTimePickerState()\nPrismaTimePicker(state = state)" },
        states = {
            StateCell("Default", minWidth = androidx.compose.ui.unit.Dp(280f)) { PrismaTimePicker() }
        },
        a11y = {
            A11yPanel(
                role = "Time picker (M3 native a11y)",
                minTouchTarget = "Per Material3 spec",
                bullets = listOf(
                    "Hour and minute controls have separate roles and announce current value.",
                    "AM/PM toggle is a button group; current selection is announced.",
                    "Keyboard / switch-control increment supported via custom actions.",
                ),
            )
        },
    )
}

@Composable public fun ColorPickerShowcase() {
    var c by rememberSaveable(stateSaver = androidx.compose.runtime.saveable.Saver(
        save = { listOf(it.red, it.green, it.blue) },
        restore = { Color(it[0], it[1], it[2]) },
    )) { mutableStateOf(Color(0.78f, 0.4f, 0.14f)) }

    PlaygroundScaffold(
        preview = { PrismaColorPicker(color = c, onColorChange = { c = it }) },
        code = { "PrismaColorPicker(color = color, onColorChange = { color = it })" },
        knobs = {
            Text(
                text = "Hex: #${"%02X".format((c.red * 255).toInt())}${"%02X".format((c.green * 255).toInt())}${"%02X".format((c.blue * 255).toInt())}",
                style = PrismaTypography.BodyMd,
                color = PrismaSemanticColors.TextSecondary.themed(),
            )
        },
        states = {
            StateCell("Orange") {
                var x by rememberSaveable(stateSaver = androidx.compose.runtime.saveable.Saver(save = { listOf(it.red, it.green, it.blue) }, restore = { Color(it[0], it[1], it[2]) })) { mutableStateOf(Color(0.78f, 0.4f, 0.14f)) }
                PrismaColorPicker(color = x, onColorChange = { x = it })
            }
        },
        a11y = {
            A11yPanel(
                role = "ColorPicker (custom)",
                minTouchTarget = "Slider thumb 48 × 48 dp",
                bullets = listOf(
                    "Current colour announced as RGB or hex; consider naming common colours for clarity.",
                    "Each channel slider is independently focusable and announces its value.",
                    "Avoid colour-only meaning — pair the swatch with a hex / name label.",
                ),
            )
        },
    )
}

// endregion

// region — Feedback

@Composable public fun ToastShowcase() {
    var message by rememberSaveable { mutableStateOf("Saved successfully.") }
    var kind by rememberSaveable { mutableStateOf(PrismaToastKind.Success) }
    var actionLabel by rememberSaveable { mutableStateOf("Undo") }
    var hasAction by rememberSaveable { mutableStateOf(true) }
    var fireCount by rememberSaveable { mutableStateOf(0) }

    PlaygroundScaffold(
        preview = {
            PrismaToast(
                message = message.ifBlank { "Toast message" },
                kind = kind,
                actionLabel = actionLabel.takeIf { hasAction && it.isNotBlank() },
                onAction = if (hasAction) {
                    { fireCount++ }
                } else null,
            )
        },
        knobs = {
            StringKnobRow(
                label = "Message",
                value = message,
                onChange = { message = it },
                placeholder = "What happened",
                helper = "Action taps fired: $fireCount",
            )
            EnumKnobRow(
                label = "Kind",
                value = kind,
                values = PrismaToastKind.values().toList(),
                onChange = { kind = it },
                optionLabel = { it.name },
            )
            BoolKnobRow("With action", hasAction, { hasAction = it })
            StringKnobRow(
                label = "Action label",
                value = actionLabel,
                onChange = { actionLabel = it },
                placeholder = "Undo",
            )
        },
        code = {
            "PrismaToast(\n    message = \"$message\",\n    kind = PrismaToastKind.${kind.name},\n" +
                (if (hasAction) "    actionLabel = \"$actionLabel\",\n    onAction = { /* … */ },\n" else "") +
                ")"
        },
        states = {
            StateCell("Info") { PrismaToast(message = "New version available.", kind = PrismaToastKind.Info) }
            StateCell("Success") { PrismaToast(message = "Saved successfully.", kind = PrismaToastKind.Success, actionLabel = "Undo", onAction = {}) }
            StateCell("Warning") { PrismaToast(message = "Connection looks slow.", kind = PrismaToastKind.Warning) }
            StateCell("Danger") { PrismaToast(message = "Could not reach server.", kind = PrismaToastKind.Danger, actionLabel = "Retry", onAction = {}) }
        },
        a11y = {
            A11yPanel(
                role = "Live region",
                minTouchTarget = "Action button 48 × 48 dp",
                bullets = listOf(
                    "Info / success use Polite live region; warning / danger use Assertive so they interrupt.",
                    "contentDescription combines kind + message — e.g. \"Danger. Could not reach server.\"",
                    "Action button retains its own Role.Button semantics for direct activation.",
                ),
            )
        },
    )
}

@Composable public fun BannerShowcase() {
    var title by rememberSaveable { mutableStateOf("We're upgrading our servers") }
    var description by rememberSaveable { mutableStateOf("You may notice slower response times for the next 30 minutes.") }
    var kind by rememberSaveable { mutableStateOf(PrismaBannerKind.Info) }
    var withAction by rememberSaveable { mutableStateOf(true) }
    var actionLabel by rememberSaveable { mutableStateOf("Learn more") }
    var withDismiss by rememberSaveable { mutableStateOf(true) }
    var visible by rememberSaveable { mutableStateOf(true) }

    PlaygroundScaffold(
        preview = {
            if (visible) {
                PrismaBanner(
                    title = title.ifBlank { "Banner title" },
                    description = description.takeIf { it.isNotBlank() },
                    kind = kind,
                    onDismiss = if (withDismiss) {
                        { visible = false }
                    } else null,
                    actionLabel = actionLabel.takeIf { withAction && it.isNotBlank() },
                    onAction = if (withAction) {
                        {}
                    } else null,
                )
            } else {
                PrismaButton(text = "Show banner again", variant = PrismaButtonVariant.Secondary, onClick = { visible = true })
            }
        },
        knobs = {
            StringKnobRow("Title", title, { title = it }, placeholder = "Headline")
            StringKnobRow("Description", description, { description = it }, placeholder = "Optional sub-text")
            EnumKnobRow(
                label = "Kind",
                value = kind,
                values = PrismaBannerKind.values().toList(),
                onChange = { kind = it },
                optionLabel = { it.name },
            )
            BoolKnobRow("With action", withAction, { withAction = it })
            StringKnobRow("Action label", actionLabel, { actionLabel = it }, placeholder = "Learn more")
            BoolKnobRow("With dismiss", withDismiss, { withDismiss = it })
        },
        code = {
            "PrismaBanner(\n    title = \"$title\",\n" +
                (if (description.isNotBlank()) "    description = \"$description\",\n" else "") +
                "    kind = PrismaBannerKind.${kind.name},\n" +
                (if (withAction) "    actionLabel = \"$actionLabel\",\n    onAction = { /* … */ },\n" else "") +
                (if (withDismiss) "    onDismiss = { /* … */ },\n" else "") +
                ")"
        },
        states = {
            StateCell("Info") { PrismaBanner(title = "Server upgrade", description = "Slower response times expected.", kind = PrismaBannerKind.Info, onDismiss = {}, actionLabel = "Learn more", onAction = {}) }
            StateCell("Success") { PrismaBanner(title = "Profile updated", description = "Saved across all devices.", kind = PrismaBannerKind.Success, onDismiss = {}) }
            StateCell("Warning") { PrismaBanner(title = "Storage almost full", description = "Less than 1GB free.", kind = PrismaBannerKind.Warning, actionLabel = "Manage", onAction = {}) }
            StateCell("Danger") { PrismaBanner(title = "Action required", description = "Verify your email.", kind = PrismaBannerKind.Danger, actionLabel = "Verify", onAction = {}) }
        },
        a11y = {
            A11yPanel(
                role = "Live region (inline alert)",
                minTouchTarget = "Action / dismiss buttons 48 × 48 dp",
                bullets = listOf(
                    "Title + description merge into one announcement; no need to focus the banner.",
                    "Polite for info / success; Assertive for warning / danger so they interrupt.",
                    "Dismiss is read as \"Close banner\"; reappearance only on relevant state change.",
                ),
            )
        },
    )
}

@Composable public fun ModalShowcase() {
    var title by rememberSaveable { mutableStateOf("Delete project?") }
    var body by rememberSaveable { mutableStateOf("This will permanently delete \"Prisma\" and all its files. This action cannot be undone.") }
    var confirmLabel by rememberSaveable { mutableStateOf("Delete") }
    var dismissLabel by rememberSaveable { mutableStateOf("Cancel") }
    var destructive by rememberSaveable { mutableStateOf(true) }
    var hasDismiss by rememberSaveable { mutableStateOf(true) }
    var open by rememberSaveable { mutableStateOf(false) }
    var lastChoice by rememberSaveable { mutableStateOf("(none)") }

    PlaygroundScaffold(
        preview = {
            Column(
                verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
                modifier = Modifier.fillMaxWidth(),
            ) {
                PrismaButton(text = "Open modal", onClick = { open = true })
                Text(
                    text = "Last choice: $lastChoice",
                    style = PrismaTypography.BodySm,
                    color = PrismaSemanticColors.TextSecondary.themed(),
                )
                if (open) {
                    PrismaModal(
                        onDismissRequest = { open = false; lastChoice = "Dismissed (scrim)" },
                        title = title.ifBlank { "Title" },
                        body = body,
                        confirmLabel = confirmLabel.ifBlank { "OK" },
                        onConfirm = { open = false; lastChoice = "Confirmed" },
                        dismissLabel = dismissLabel.takeIf { hasDismiss && it.isNotBlank() },
                        onDismiss = if (hasDismiss) {
                            { open = false; lastChoice = "Dismissed" }
                        } else null,
                        isDestructive = destructive,
                    )
                }
            }
        },
        knobs = {
            StringKnobRow("Title", title, { title = it })
            StringKnobRow("Body", body, { body = it })
            StringKnobRow("Confirm label", confirmLabel, { confirmLabel = it })
            BoolKnobRow("With dismiss button", hasDismiss, { hasDismiss = it })
            StringKnobRow("Dismiss label", dismissLabel, { dismissLabel = it })
            BoolKnobRow("Destructive", destructive, { destructive = it })
        },
        code = {
            "PrismaModal(\n    onDismissRequest = { open = false },\n    title = \"$title\",\n    body = \"\${body.replace(\"\\\"\", \"\\\\\\\"\")}\",\n    confirmLabel = \"$confirmLabel\",\n    onConfirm = { /* … */ },\n" +
                (if (hasDismiss) "    dismissLabel = \"$dismissLabel\",\n    onDismiss = { open = false },\n" else "") +
                (if (destructive) "    isDestructive = true,\n" else "") +
                ")"
        },
        states = {
            StateCell("Confirm action") {
                Text("Tap the live preview's button to open.\nDestructive variant uses red confirm.", style = PrismaTypography.BodySm, color = PrismaSemanticColors.TextSecondary.themed())
            }
            StateCell("Without dismiss") {
                Text("Set 'With dismiss button' off to render a single-action modal (e.g. 'Got it').", style = PrismaTypography.BodySm, color = PrismaSemanticColors.TextSecondary.themed())
            }
        },
        a11y = {
            A11yPanel(
                role = "Modal Dialog",
                minTouchTarget = "48 × 48 dp confirm / dismiss",
                bullets = listOf(
                    "Focus is trapped inside the modal; ESC / scrim tap dismisses and returns focus to the trigger.",
                    "Title is read on open; body follows; confirm + dismiss buttons are focusable in order.",
                    "Destructive variant tints the confirm button danger; the *role* doesn't change.",
                ),
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable public fun BottomSheetShowcase() {
    var title by rememberSaveable { mutableStateOf("Settings") }
    var body by rememberSaveable { mutableStateOf("Bottom sheets are for short, focused tasks that don't warrant a full screen.") }
    var open by rememberSaveable { mutableStateOf(false) }
    PlaygroundScaffold(
        preview = {
            Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
                PrismaButton(text = "Open bottom sheet", onClick = { open = true })
                Text("Bottom sheet appears anchored to the bottom — open to view.", style = PrismaTypography.BodySm, color = PrismaSemanticColors.TextSecondary.themed())
            }
            if (open) {
                PrismaBottomSheet(onDismissRequest = { open = false }) {
                    Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
                        Text(title, style = PrismaTypography.HeadlineSm, color = PrismaSemanticColors.TextPrimary.themed())
                        Text(body, style = PrismaTypography.BodyMd, color = PrismaSemanticColors.TextSecondary.themed())
                        PrismaButton(text = "Close", variant = PrismaButtonVariant.Secondary, onClick = { open = false })
                    }
                }
            }
        },
        knobs = {
            StringKnobRow("Title", title, { title = it })
            StringKnobRow("Body", body, { body = it })
        },
        code = { "PrismaBottomSheet(onDismissRequest = { open = false }) {\n    Column { /* sheet content */ }\n}" },
        a11y = {
            A11yPanel(
                role = "Modal sheet",
                minTouchTarget = "Drag handle 48 × 48 dp; content interactive",
                bullets = listOf(
                    "Focus traps inside the sheet; swipe-down or scrim dismisses and returns focus.",
                    "Drag handle has its own Role.Button announcing \"drag handle, double-tap to expand\".",
                    "Hide content behind the sheet from screen readers (uses Material3 Modal semantics).",
                ),
            )
        },
    )
}

@Composable public fun PopoverShowcase() {
    var label by rememberSaveable { mutableStateOf("Quick action") }
    var body by rememberSaveable { mutableStateOf("Inline content with focus management.") }
    var open by rememberSaveable { mutableStateOf(false) }
    PlaygroundScaffold(
        preview = {
            Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
                PrismaButton(text = "Open popover", onClick = { open = true })
                if (open) {
                    PrismaPopover(onDismissRequest = { open = false }) {
                        Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
                            Text(label, style = PrismaTypography.LabelLg, color = PrismaSemanticColors.TextPrimary.themed())
                            Text(body, style = PrismaTypography.BodySm, color = PrismaSemanticColors.TextSecondary.themed())
                            PrismaButton(text = "Got it", onClick = { open = false })
                        }
                    }
                }
            }
        },
        knobs = {
            StringKnobRow("Label", label, { label = it })
            StringKnobRow("Body", body, { body = it })
        },
        code = { "PrismaPopover(onDismissRequest = { open = false }) {\n    Column { /* popover content */ }\n}" },
        a11y = {
            A11yPanel(
                role = "Popover (non-modal overlay)",
                minTouchTarget = "Trigger 48 × 48 dp",
                bullets = listOf(
                    "Lighter than Modal — does not trap focus; tap outside or ESC dismisses.",
                    "Anchored to the trigger; positioning auto-flips to stay on-screen.",
                    "For destructive / blocking flows use Modal, not Popover.",
                ),
            )
        },
    )
}

@Composable public fun TooltipShowcase() {
    var hint by rememberSaveable { mutableStateOf("Save to clipboard") }
    PlaygroundScaffold(
        preview = {
            PrismaTooltip(text = hint) {
                Icon(painter = painterResource(PrismaIcons.Copy), contentDescription = "Copy", tint = PrismaSemanticColors.TextPrimary.themed(), modifier = Modifier.size(24.dp))
            }
        },
        knobs = {
            StringKnobRow("Tooltip text", hint, { hint = it }, helper = "Long-press the icon (or hover) to surface the tooltip.")
        },
        code = { "PrismaTooltip(text = \"$hint\") {\n    Icon(painter = painterResource(PrismaIcons.Copy), contentDescription = \"Copy\")\n}" },
        states = {
            StateCell("Copy") {
                PrismaTooltip(text = "Save to clipboard") {
                    Icon(painter = painterResource(PrismaIcons.Copy), contentDescription = "Copy", tint = PrismaSemanticColors.TextPrimary.themed(), modifier = Modifier.size(24.dp))
                }
            }
            StateCell("Star") {
                PrismaTooltip(text = "Star this item") {
                    Icon(painter = painterResource(PrismaIcons.Star), contentDescription = "Star", tint = PrismaSemanticColors.TextPrimary.themed(), modifier = Modifier.size(24.dp))
                }
            }
        },
        a11y = {
            A11yPanel(
                role = "Tooltip (label association)",
                minTouchTarget = "Trigger element 48 × 48 dp",
                bullets = listOf(
                    "Tooltip text serves as the trigger's accessible label — never use as the only source of meaning.",
                    "Long-press / hover surfaces the tooltip; screen readers read the label without it appearing.",
                    "Don't put critical info in tooltips alone — keyboard / touch users may never trigger them.",
                ),
            )
        },
    )
}

private enum class LoadingShape { Circular, Linear }

@Composable public fun LoadingShowcase() {
    var shape by rememberSaveable { mutableStateOf(LoadingShape.Circular) }
    var size by rememberSaveable { mutableStateOf(PrismaLoadingSize.Md) }
    var indeterminate by rememberSaveable { mutableStateOf(true) }
    var progress by rememberSaveable { mutableStateOf(60) }
    PlaygroundScaffold(
        preview = {
            when (shape) {
                LoadingShape.Circular -> PrismaCircularLoading(size = size)
                LoadingShape.Linear -> PrismaLinearLoading(modifier = Modifier.fillMaxWidth(), progress = if (indeterminate) null else progress / 100f)
            }
        },
        knobs = {
            EnumKnobRow("Shape", shape, LoadingShape.values().toList(), { shape = it }, optionLabel = { it.name })
            EnumKnobRow("Size (circular)", size, PrismaLoadingSize.values().toList(), { size = it }, optionLabel = { it.name })
            BoolKnobRow("Indeterminate (linear)", indeterminate, { indeterminate = it })
            IntKnobRow("Progress %", progress, range = 0..100, onChange = { progress = it })
        },
        code = {
            when (shape) {
                LoadingShape.Circular -> "PrismaCircularLoading(size = PrismaLoadingSize.${size.name})"
                LoadingShape.Linear -> if (indeterminate) "PrismaLinearLoading()" else "PrismaLinearLoading(progress = ${progress / 100f}f)"
            }
        },
        states = {
            StateCell("Circular Sm") { PrismaCircularLoading(size = PrismaLoadingSize.Sm) }
            StateCell("Circular Md") { PrismaCircularLoading(size = PrismaLoadingSize.Md) }
            StateCell("Circular Lg") { PrismaCircularLoading(size = PrismaLoadingSize.Lg) }
            StateCell("Linear (indeterminate)") { PrismaLinearLoading(modifier = Modifier.fillMaxWidth()) }
            StateCell("Linear (60%)") { PrismaLinearLoading(modifier = Modifier.fillMaxWidth(), progress = 0.6f) }
        },
        a11y = {
            A11yPanel(
                role = "ProgressBar",
                minTouchTarget = "n/a (non-interactive)",
                bullets = listOf(
                    "Indeterminate variant uses progressSemantics with no value — read as \"loading\".",
                    "Determinate variant exposes 0–1 progress; screen readers announce percentage.",
                    "Pair with a textual label (\"Loading projects…\") for context — the role alone isn't enough.",
                ),
            )
        },
    )
}

private enum class SkeletonComposition { Lines, Card, PostPlaceholder }

@Composable public fun SkeletonShowcase() {
    var kind by rememberSaveable { mutableStateOf(SkeletonComposition.PostPlaceholder) }
    var lineCount by rememberSaveable { mutableStateOf(3) }
    var blockHeight by rememberSaveable { mutableStateOf(120) }

    PlaygroundScaffold(
        preview = {
            when (kind) {
                SkeletonComposition.Lines -> Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2), modifier = Modifier.fillMaxWidth()) {
                    repeat(lineCount.coerceAtLeast(1)) { i ->
                        val frac = listOf(0.95f, 0.78f, 0.85f, 0.6f)[i % 4]
                        PrismaSkeletonLine(modifier = Modifier.fillMaxWidth(frac).height(12.dp))
                    }
                }
                SkeletonComposition.Card -> PrismaSkeletonBlock(modifier = Modifier.fillMaxWidth().height(blockHeight.dp), cornerRadius = 12.dp)
                SkeletonComposition.PostPlaceholder -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
                    PrismaSkeletonCircle(modifier = Modifier.size(40.dp))
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2)) {
                        PrismaSkeletonLine(modifier = Modifier.fillMaxWidth(0.5f).height(14.dp))
                        PrismaSkeletonLine(modifier = Modifier.fillMaxWidth(0.85f).height(12.dp))
                        PrismaSkeletonLine(modifier = Modifier.fillMaxWidth(0.7f).height(12.dp))
                    }
                }
            }
        },
        knobs = {
            EnumKnobRow("Composition", kind, SkeletonComposition.values().toList(), { kind = it }, optionLabel = { it.name })
            IntKnobRow("Line count (Lines)", lineCount, range = 1..6, onChange = { lineCount = it })
            IntKnobRow("Block height (Card)", blockHeight, range = 60..240, step = 20, onChange = { blockHeight = it })
        },
        code = {
            when (kind) {
                SkeletonComposition.Lines -> "Column {\n    repeat($lineCount) { PrismaSkeletonLine(modifier = Modifier.fillMaxWidth().height(12.dp)) }\n}"
                SkeletonComposition.Card -> "PrismaSkeletonBlock(modifier = Modifier.fillMaxWidth().height(${blockHeight}.dp), cornerRadius = 12.dp)"
                SkeletonComposition.PostPlaceholder -> "Row {\n    PrismaSkeletonCircle(modifier = Modifier.size(40.dp))\n    Column { /* skeleton lines */ }\n}"
            }
        },
        states = {
            StateCell("Line") { PrismaSkeletonLine(modifier = Modifier.fillMaxWidth().height(12.dp)) }
            StateCell("Circle") { PrismaSkeletonCircle(modifier = Modifier.size(40.dp)) }
            StateCell("Block") { PrismaSkeletonBlock(modifier = Modifier.fillMaxWidth().height(80.dp), cornerRadius = 12.dp) }
        },
        a11y = {
            A11yPanel(
                role = "Decorative (hidden from a11y)",
                minTouchTarget = "n/a",
                bullets = listOf(
                    "Marked invisibleToUser so screen readers skip the placeholder entirely.",
                    "Pair with a sibling \"Loading…\" announcement so AT users still know content is coming.",
                    "When real content arrives, focus / live region should pick it up automatically.",
                ),
            )
        },
    )
}

private enum class BadgeShape { Count, Dot }

@Composable public fun BadgeShowcase() {
    var shape by rememberSaveable { mutableStateOf(BadgeShape.Count) }
    var count by rememberSaveable { mutableStateOf(12) }
    var status by rememberSaveable { mutableStateOf(PrismaBadgeStatus.Accent) }
    PlaygroundScaffold(
        preview = {
            when (shape) {
                BadgeShape.Count -> PrismaCountBadge(count = count, status = status)
                BadgeShape.Dot -> PrismaDotBadge(status = status)
            }
        },
        knobs = {
            EnumKnobRow("Shape", shape, BadgeShape.values().toList(), { shape = it }, optionLabel = { it.name })
            IntKnobRow("Count", count, range = 0..250, onChange = { count = it })
            EnumKnobRow("Status", status, PrismaBadgeStatus.values().toList(), { status = it }, optionLabel = { it.name })
        },
        code = {
            when (shape) {
                BadgeShape.Count -> "PrismaCountBadge(count = $count" + (if (status != PrismaBadgeStatus.Accent) ", status = PrismaBadgeStatus.${status.name}" else "") + ")"
                BadgeShape.Dot -> "PrismaDotBadge(" + (if (status != PrismaBadgeStatus.Accent) "status = PrismaBadgeStatus.${status.name}" else "") + ")"
            }
        },
        states = {
            StateCell("Single") { PrismaCountBadge(count = 1) }
            StateCell("Two-digit") { PrismaCountBadge(count = 12) }
            StateCell("Cap (99+)") { PrismaCountBadge(count = 250) }
            StateCell("Success") { PrismaCountBadge(count = 3, status = PrismaBadgeStatus.Success) }
            StateCell("Warning") { PrismaCountBadge(count = 7, status = PrismaBadgeStatus.Warning) }
            StateCell("Danger") { PrismaCountBadge(count = 99, status = PrismaBadgeStatus.Danger) }
            StateCell("Dot") { PrismaDotBadge(status = PrismaBadgeStatus.Accent) }
        },
        a11y = {
            A11yPanel(
                role = "Decorative; carrier owns semantics",
                minTouchTarget = "n/a (badges are not interactive)",
                bullets = listOf(
                    "Badge alone is meaningless — append \"5 unread\" to the parent's contentDescription.",
                    "Cap at 99+ visually; announce the actual count if known.",
                    "Status colour is supportive only; the carrier's text label conveys success/danger meaning.",
                ),
            )
        },
    )
}

@Composable public fun EmptyStateShowcase() {
    var title by rememberSaveable { mutableStateOf("No projects yet") }
    var description by rememberSaveable { mutableStateOf("When you create a project, it'll show up here.") }
    var actionLabel by rememberSaveable { mutableStateOf("Create project") }
    var withAction by rememberSaveable { mutableStateOf(true) }
    PlaygroundScaffold(
        preview = {
            PrismaEmptyState(
                title = title.ifBlank { "Title" },
                description = description.takeIf { it.isNotBlank() },
                action = if (withAction) ({ PrismaButton(text = actionLabel.ifBlank { "Action" }, onClick = {}) }) else null,
            )
        },
        knobs = {
            StringKnobRow("Title", title, { title = it })
            StringKnobRow("Description", description, { description = it })
            BoolKnobRow("With action", withAction, { withAction = it })
            StringKnobRow("Action label", actionLabel, { actionLabel = it })
        },
        code = {
            "PrismaEmptyState(\n    title = \"$title\",\n" +
                (if (description.isNotBlank()) "    description = \"$description\",\n" else "") +
                (if (withAction) "    action = { PrismaButton(text = \"$actionLabel\", onClick = { /* … */ }) },\n" else "") +
                ")"
        },
        states = {
            StateCell("Default") {
                PrismaEmptyState(title = "No projects yet", description = "When you create a project, it'll show up here.", action = { PrismaButton(text = "Create project", onClick = {}) })
            }
            StateCell("Just a title") { PrismaEmptyState(title = "Nothing here") }
        },
        a11y = {
            A11yPanel(
                role = "Heading + body + optional action",
                minTouchTarget = "Action button 48 × 48 dp",
                bullets = listOf(
                    "Title carries heading() semantics so screen reader users can jump to it.",
                    "Body description is read after the title; keep it under two short sentences.",
                    "Action label should be a verb that resolves the empty state (\"Create project\").",
                ),
            )
        },
    )
}

@Composable public fun DrawerShowcase() {
    val state = androidx.compose.material3.rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var title by rememberSaveable { mutableStateOf("Prisma") }
    var body by rememberSaveable { mutableStateOf("Drawer content goes here. Use for secondary contexts (filters, account menu).") }

    PlaygroundScaffold(
        preview = {
            PrismaDrawer(
                drawerState = state,
                drawerContent = {
                    Column(modifier = Modifier.fillMaxWidth().padding(PrismaSpacing.Sp7), verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
                        Text(title.ifBlank { "Title" }, style = PrismaTypography.HeadlineSm, color = PrismaSemanticColors.TextPrimary.themed())
                        Text(body, style = PrismaTypography.BodyMd, color = PrismaSemanticColors.TextSecondary.themed())
                        PrismaButton(text = "Close", variant = PrismaButtonVariant.Secondary, onClick = { scope.launch { state.close() } })
                    }
                },
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(PrismaSpacing.Sp4), verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
                    PrismaButton(text = "Open drawer", onClick = { scope.launch { state.open() } })
                    Text("Tap the button to open the drawer over this content.", style = PrismaTypography.BodyMd, color = PrismaSemanticColors.TextSecondary.themed())
                }
            }
        },
        knobs = {
            StringKnobRow("Drawer title", title, { title = it })
            StringKnobRow("Drawer body", body, { body = it })
        },
        code = {
            "PrismaDrawer(\n    drawerState = state,\n    drawerContent = { /* drawer body */ },\n) {\n    /* main content */\n}"
        },
        a11y = {
            A11yPanel(
                role = "Modal sheet (off-canvas)",
                minTouchTarget = "Trigger 48 × 48 dp; drawer items 48 dp",
                bullets = listOf(
                    "When open, focus traps inside the drawer; main content goes inert.",
                    "Swipe / scrim dismisses; ESC closes from external keyboards.",
                    "Use for secondary navigation; primary nav stays in the adaptive shell.",
                ),
            )
        },
    )
}

// endregion

// region — Navigation

@Composable public fun TabsShowcase() {
    var tabsCsv by rememberSaveable { mutableStateOf("Overview, Activity, Settings, Billing") }
    var selectedIdx by rememberSaveable { mutableStateOf(0) }

    val tabs: ImmutableList<String> = remember(tabsCsv) {
        tabsCsv.split(',').map { it.trim() }.filter { it.isNotEmpty() }.ifEmpty { listOf("Tab") }.toImmutableList()
    }
    val safeIdx = selectedIdx.coerceIn(0, tabs.lastIndex)

    PlaygroundScaffold(
        preview = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
            ) {
                PrismaTabs(
                    tabs = tabs,
                    selected = tabs[safeIdx],
                    onSelect = { selectedIdx = tabs.indexOf(it).coerceAtLeast(0) },
                )
                Text(
                    text = "Active tab: ${tabs[safeIdx]}",
                    style = PrismaTypography.BodyMd,
                    color = PrismaSemanticColors.TextSecondary.themed(),
                )
            }
        },
        knobs = {
            StringKnobRow(
                label = "Tabs (comma separated)",
                value = tabsCsv,
                onChange = { tabsCsv = it; selectedIdx = 0 },
                placeholder = "Overview, Activity, Settings",
                helper = "Edit to add or remove tabs from the live preview.",
            )
            IntKnobRow(
                label = "Selected index",
                value = safeIdx,
                range = 0..tabs.lastIndex.coerceAtLeast(0),
                onChange = { selectedIdx = it },
            )
        },
        code = {
            "PrismaTabs(\n    tabs = persistentListOf(${tabs.joinToString { "\"$it\"" }}),\n    selected = \"${tabs[safeIdx]}\",\n    onSelect = { /* … */ },\n)"
        },
        states = {
            StateCell("2 tabs") {
                var s by rememberSaveable { mutableStateOf("Inbox") }
                PrismaTabs(tabs = persistentListOf("Inbox", "Archive"), selected = s, onSelect = { s = it })
            }
            StateCell("4 tabs") {
                var s by rememberSaveable { mutableStateOf("Overview") }
                PrismaTabs(tabs = persistentListOf("Overview", "Activity", "Settings", "Billing"), selected = s, onSelect = { s = it })
            }
            StateCell("Last selected") {
                var s by rememberSaveable { mutableStateOf("Three") }
                PrismaTabs(tabs = persistentListOf("One", "Two", "Three"), selected = s, onSelect = { s = it })
            }
        },
        a11y = {
            A11yPanel(
                role = "Tab (within selectableGroup)",
                minTouchTarget = "48 dp height per tab",
                bullets = listOf(
                    "Each tab carries Role.Tab; the row is a selectableGroup so position is announced.",
                    "Selection haptic fires only when a different tab is chosen, not on re-tap.",
                    "Tabs are for switching between peer views — distinct from SegmentedControl filtering.",
                ),
            )
        },
    )
}

@Composable public fun ChipShowcase() {
    var label by rememberSaveable { mutableStateOf("Android") }
    var selected by rememberSaveable { mutableStateOf(true) }
    var variant by rememberSaveable { mutableStateOf(PrismaChipVariant.Filter) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var withDismiss by rememberSaveable { mutableStateOf(false) }
    PlaygroundScaffold(
        preview = {
            PrismaChip(
                label = label.ifBlank { "Chip" },
                selected = selected,
                variant = variant,
                enabled = enabled,
                onClick = { selected = !selected },
                onDismiss = if (withDismiss) ({}) else null,
            )
        },
        knobs = {
            StringKnobRow("Label", label, { label = it })
            EnumKnobRow("Variant", variant, PrismaChipVariant.values().toList(), { variant = it }, optionLabel = { it.name })
            BoolKnobRow("Selected", selected, { selected = it })
            BoolKnobRow("Enabled", enabled, { enabled = it })
            BoolKnobRow("With dismiss (×)", withDismiss, { withDismiss = it })
        },
        code = {
            "PrismaChip(\n    label = \"$label\",\n    selected = $selected,\n" +
                (if (variant != PrismaChipVariant.Filter) "    variant = PrismaChipVariant.${variant.name},\n" else "") +
                (if (!enabled) "    enabled = false,\n" else "") +
                "    onClick = { /* … */ },\n" +
                (if (withDismiss) "    onDismiss = { /* … */ },\n" else "") +
                ")"
        },
        states = {
            StateCell("Filter (toggle)") { ToggleableChipDemo("Android") }
            StateCell("iOS (toggle)") { ToggleableChipDemo("iOS", initial = false) }
            StateCell("Suggestion") { PrismaChip(label = "Trending", onClick = {}, variant = PrismaChipVariant.Suggestion) }
            StateCell("Disabled") { PrismaChip(label = "Locked", onClick = {}, enabled = false) }
        },
        a11y = {
            A11yPanel(
                role = "Button (filter) / Listbox option (suggestion)",
                minTouchTarget = "48 dp height; × 48 × 48 dp",
                bullets = listOf(
                    "Selected state announced as part of the role; haptic on toggle.",
                    "Input variant exposes its × as a separate action labelled \"Remove <chip>\".",
                    "Chip groups should be wrapped in selectableGroup when filter chips behave like multi-select.",
                ),
            )
        },
    )
}

/**
 * Tap-to-toggle chip cell so the gallery clearly demonstrates that filter
 * chips are stateful, not decorative.
 */
@Composable
private fun ToggleableChipDemo(label: String, initial: Boolean = true) {
    var on by rememberSaveable { mutableStateOf(initial) }
    PrismaChip(label = label, selected = on, onClick = { on = !on })
}

/** Tap-to-toggle list item — selected swatch flips on tap. */
@Composable
private fun SelectableListItemDemo() {
    var on by rememberSaveable { mutableStateOf(false) }
    PrismaListItem(
        primary = "Aanya Patel",
        secondary = "aanya@example.com",
        leading = { PrismaAvatar(seed = "Aanya Patel", size = PrismaAvatarSize.Sm, status = PrismaAvatarStatus.Away) },
        selected = on,
        onClick = { on = !on },
    )
}

@Composable public fun CommandPaletteShowcase() {
    var open by rememberSaveable { mutableStateOf(false) }
    var lastInvoked by rememberSaveable { mutableStateOf("(none)") }
    PlaygroundScaffold(
        code = {
            "PrismaCommandPalette(\n    onDismissRequest = { open = false },\n    commands = listOf(\n        PrismaCommand(\"Open Typography\", \"Foundations\") { /* … */ },\n        PrismaCommand(\"Toggle theme\", \"Actions\") { /* … */ },\n    ),\n)"
        },
        a11y = {
            A11yPanel(
                role = "Combobox (input + listbox)",
                minTouchTarget = "Each command row 48 dp",
                bullets = listOf(
                    "Filtering announces \"N results\" via polite live region as the user types.",
                    "Section headers (\"Foundations\", \"Actions\") use heading() so they're skippable.",
                    "Enter activates the focused command; ESC closes the palette and returns focus.",
                ),
            )
        },
        preview = {
            Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
                PrismaButton(text = "Open command palette ⌘K", onClick = { open = true })
                Text("Last invoked: $lastInvoked", style = PrismaTypography.BodySm, color = PrismaSemanticColors.TextSecondary.themed())
                if (open) {
                    PrismaCommandPalette(
                        onDismissRequest = { open = false },
                        commands = persistentListOf(
                            PrismaCommand("Open Typography", "Foundations") { lastInvoked = "Typography"; open = false },
                            PrismaCommand("Open Colors", "Foundations") { lastInvoked = "Colors"; open = false },
                            PrismaCommand("Toggle theme", "Actions") { lastInvoked = "Toggle theme"; open = false },
                            PrismaCommand("Search components", "Navigation") { lastInvoked = "Search"; open = false },
                            PrismaCommand("Open Button", "Components") { lastInvoked = "Button"; open = false },
                            PrismaCommand("Open TextField", "Components") { lastInvoked = "TextField"; open = false },
                        ),
                    )
                }
            }
        },
    )
}

@Composable public fun PaginationShowcase() {
    var page by rememberSaveable { mutableStateOf(1) }
    var pageCount by rememberSaveable { mutableStateOf(12) }
    val safeCount = pageCount.coerceAtLeast(1)
    val safePage = page.coerceIn(1, safeCount)
    PlaygroundScaffold(
        preview = {
            Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4)) {
                PrismaPagination(page = safePage, pageCount = safeCount, onPageChange = { page = it })
                Text("Page $safePage of $safeCount", style = PrismaTypography.BodyMd, color = PrismaSemanticColors.TextSecondary.themed())
            }
        },
        knobs = {
            IntKnobRow("Total pages", pageCount, range = 1..50, onChange = { pageCount = it })
            IntKnobRow("Current page", safePage, range = 1..safeCount, onChange = { page = it })
        },
        code = { "PrismaPagination(page = $safePage, pageCount = $safeCount, onPageChange = { /* … */ })" },
        states = {
            StateCell("Few pages") {
                var p by rememberSaveable { mutableStateOf(2) }
                PrismaPagination(page = p, pageCount = 5, onPageChange = { p = it })
            }
            StateCell("Many pages") {
                var p by rememberSaveable { mutableStateOf(12) }
                PrismaPagination(page = p, pageCount = 30, onPageChange = { p = it })
            }
            StateCell("First page") {
                PrismaPagination(page = 1, pageCount = 10, onPageChange = {})
            }
            StateCell("Last page") {
                PrismaPagination(page = 10, pageCount = 10, onPageChange = {})
            }
        },
        a11y = {
            A11yPanel(
                role = "Navigation (each control is a Button)",
                minTouchTarget = "Each page button 48 × 48 dp",
                bullets = listOf(
                    "Wrap the row in Role.Navigation with contentDescription \"Pagination\".",
                    "Current page exposes a selected state; arrows announce \"Previous / Next page, disabled\" at edges.",
                    "Ellipsis is decorative — give it invisibleToUser so it doesn't get focused.",
                ),
            )
        },
    )
}

@Composable public fun BreadcrumbShowcase() {
    var pathCsv by rememberSaveable { mutableStateOf("Home, Components, Inputs, Button") }
    val items: ImmutableList<PrismaBreadcrumbItem> = remember(pathCsv) {
        val parts = pathCsv.split(',').map { it.trim() }.filter { it.isNotEmpty() }
        parts.mapIndexed { idx, label ->
            if (idx == parts.lastIndex) PrismaBreadcrumbItem(label) else PrismaBreadcrumbItem(label, onClick = {})
        }.toImmutableList()
    }
    PlaygroundScaffold(
        preview = { PrismaBreadcrumb(items = items) },
        knobs = {
            StringKnobRow(
                label = "Path (comma separated, last is current)",
                value = pathCsv,
                onChange = { pathCsv = it },
                placeholder = "Home, Section, Page",
            )
        },
        code = {
            val parts = pathCsv.split(',').map { it.trim() }.filter { it.isNotEmpty() }
            "PrismaBreadcrumb(\n    items = listOf(\n" +
                parts.mapIndexed { idx, label ->
                    if (idx == parts.lastIndex) "        PrismaBreadcrumbItem(\"$label\")"
                    else "        PrismaBreadcrumbItem(\"$label\", onClick = { /* … */ })"
                }.joinToString(",\n") +
                ",\n    ),\n)"
        },
        states = {
            StateCell("3 levels") { PrismaBreadcrumb(items = persistentListOf(PrismaBreadcrumbItem("Home", onClick = {}), PrismaBreadcrumbItem("Settings", onClick = {}), PrismaBreadcrumbItem("Profile"))) }
            StateCell("4 levels") { PrismaBreadcrumb(items = persistentListOf(PrismaBreadcrumbItem("Home", onClick = {}), PrismaBreadcrumbItem("Components", onClick = {}), PrismaBreadcrumbItem("Inputs", onClick = {}), PrismaBreadcrumbItem("Button"))) }
        },
        a11y = {
            A11yPanel(
                role = "Navigation (ordered list of Buttons; current page is text)",
                minTouchTarget = "48 × 48 dp per crumb",
                bullets = listOf(
                    "Wrap in Role.Navigation labelled \"Breadcrumb\".",
                    "The last item is the current page — render as plain text, not a link.",
                    "Separators (\"/\") are decorative; mark invisibleToUser so the path reads cleanly.",
                ),
            )
        },
    )
}

@Composable public fun WizardShowcase() {
    var stepsCsv by rememberSaveable { mutableStateOf("Account, Profile, Plan, Billing") }
    var step by rememberSaveable { mutableStateOf(1) }
    val steps: ImmutableList<String> = remember(stepsCsv) {
        stepsCsv.split(',').map { it.trim() }.filter { it.isNotEmpty() }.ifEmpty { listOf("Step 1") }.toImmutableList()
    }
    val safeStep = step.coerceIn(0, steps.lastIndex)
    PlaygroundScaffold(
        preview = {
            Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4), modifier = Modifier.fillMaxWidth()) {
                PrismaWizardSteps(steps = steps, activeIndex = safeStep)
                Row(horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
                    PrismaButton(text = "Previous", variant = PrismaButtonVariant.Secondary, enabled = safeStep > 0, onClick = { step = safeStep - 1 })
                    PrismaButton(text = "Next", enabled = safeStep < steps.lastIndex, onClick = { step = safeStep + 1 })
                }
            }
        },
        knobs = {
            StringKnobRow("Steps (comma separated)", stepsCsv, { stepsCsv = it; step = 0 })
            IntKnobRow("Active step index", safeStep, range = 0..steps.lastIndex.coerceAtLeast(0), onChange = { step = it })
        },
        code = {
            "PrismaWizardSteps(steps = persistentListOf(${steps.joinToString { "\"$it\"" }}), activeIndex = $safeStep)"
        },
        states = {
            StateCell("3 steps, on 1") { PrismaWizardSteps(steps = persistentListOf("Account", "Profile", "Done"), activeIndex = 1) }
            StateCell("Last step") { PrismaWizardSteps(steps = persistentListOf("A", "B", "C"), activeIndex = 2) }
        },
        a11y = {
            A11yPanel(
                role = "Step indicator (progress)",
                minTouchTarget = "n/a (non-interactive in catalogue; wired to nav externally)",
                bullets = listOf(
                    "Active step exposes ariaCurrent=\"step\" so screen readers announce \"current step\".",
                    "Completed steps announce as \"completed\"; future steps as \"upcoming\".",
                    "Pair with a heading (\"Step 2 of 4: Profile\") for clearest navigation.",
                ),
            )
        },
    )
}

// endregion

// region — Data display

@Composable public fun CardShowcase() {
    var title by rememberSaveable { mutableStateOf("Project Aurora") }
    var body by rememberSaveable { mutableStateOf("Auto-tuning service for production workloads. Reduces p95 latency by an average of 18%.") }
    var variant by rememberSaveable { mutableStateOf(PrismaCardVariant.Outlined) }
    var clickable by rememberSaveable { mutableStateOf(false) }
    var withCta by rememberSaveable { mutableStateOf(true) }
    var clickCount by rememberSaveable { mutableStateOf(0) }

    PlaygroundScaffold(
        preview = {
            PrismaCard(
                variant = variant,
                onClick = if (clickable) {
                    { clickCount++ }
                } else null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
                    Text(title.ifBlank { "Card title" }, style = PrismaTypography.TitleMd, color = PrismaSemanticColors.TextPrimary.themed())
                    Text(body.ifBlank { "Card body" }, style = PrismaTypography.BodyMd, color = PrismaSemanticColors.TextSecondary.themed())
                    if (clickable) {
                        Text("Card taps: $clickCount", style = PrismaTypography.LabelSm, color = PrismaSemanticColors.TextTertiary.themed())
                    }
                    if (withCta) {
                        Row(horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2)) {
                            PrismaButton(text = "Open", onClick = {})
                            PrismaButton(text = "Share", variant = PrismaButtonVariant.Ghost, onClick = {})
                        }
                    }
                }
            }
        },
        knobs = {
            StringKnobRow("Title", title, { title = it })
            StringKnobRow("Body", body, { body = it }, placeholder = "Description")
            EnumKnobRow(
                label = "Variant",
                value = variant,
                values = PrismaCardVariant.values().toList(),
                onChange = { variant = it },
                optionLabel = { it.name },
            )
            BoolKnobRow("Clickable (whole card)", clickable, { clickable = it })
            BoolKnobRow("With CTA buttons", withCta, { withCta = it })
        },
        code = {
            "PrismaCard(\n" +
                (if (variant != PrismaCardVariant.Outlined) "    variant = PrismaCardVariant.${variant.name},\n" else "") +
                (if (clickable) "    onClick = { /* … */ },\n" else "") +
                ") {\n    Column { Text(\"$title\"); Text(\"\${body.take(40)}…\") }\n}"
        },
        states = {
            StateCell("Elevated") {
                PrismaCard(variant = PrismaCardVariant.Elevated) {
                    Text("Casts a shadow over the page. Use for emphasis.", style = PrismaTypography.BodySm, color = PrismaSemanticColors.TextSecondary.themed())
                }
            }
            StateCell("Outlined") {
                PrismaCard(variant = PrismaCardVariant.Outlined) {
                    Text("1px subtle border. The most-used card.", style = PrismaTypography.BodySm, color = PrismaSemanticColors.TextSecondary.themed())
                }
            }
            StateCell("Filled") {
                PrismaCard(variant = PrismaCardVariant.Filled) {
                    Text("Sunken surface, quieter than elevated.", style = PrismaTypography.BodySm, color = PrismaSemanticColors.TextSecondary.themed())
                }
            }
        },
        a11y = {
            A11yPanel(
                role = "Container (interactive cards = Role.Button)",
                minTouchTarget = "48 × 48 dp when whole-card clickable",
                bullets = listOf(
                    "Interior interactive elements (CTA buttons) keep their own focus and roles.",
                    "If the whole card is clickable, mergeDescendants so the card is one a11y unit.",
                    "Don't double-up: either the card OR the inner button is the action, not both.",
                ),
            )
        },
    )
}

private enum class ListItemLeading { None, Avatar, Icon }
private enum class ListItemTrailing { None, Chevron, Badge, Switch }

@Composable public fun ListItemShowcase() {
    var primary by rememberSaveable { mutableStateOf("Maya Chen") }
    var secondary by rememberSaveable { mutableStateOf("maya@example.com") }
    var leading by rememberSaveable { mutableStateOf(ListItemLeading.Avatar) }
    var trailing by rememberSaveable { mutableStateOf(ListItemTrailing.Chevron) }
    var selected by rememberSaveable { mutableStateOf(false) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var clickable by rememberSaveable { mutableStateOf(true) }
    var switchOn by rememberSaveable { mutableStateOf(true) }

    PlaygroundScaffold(
        preview = {
            PrismaListItem(
                primary = primary.ifBlank { "Primary text" },
                secondary = secondary.takeIf { it.isNotBlank() },
                leading = when (leading) {
                    ListItemLeading.None -> null
                    ListItemLeading.Avatar -> ({
                        PrismaAvatar(seed = primary.ifBlank { "?" }, size = PrismaAvatarSize.Sm, status = PrismaAvatarStatus.Online)
                    })
                    ListItemLeading.Icon -> ({
                        Icon(painter = painterResource(PrismaIcons.Settings), contentDescription = null, tint = PrismaSemanticColors.TextSecondary.themed(), modifier = Modifier.size(20.dp))
                    })
                },
                trailing = when (trailing) {
                    ListItemTrailing.None -> null
                    ListItemTrailing.Chevron -> ({
                        Icon(painter = painterResource(PrismaIcons.ChevronRight), contentDescription = null, tint = PrismaSemanticColors.TextTertiary.themed())
                    })
                    ListItemTrailing.Badge -> ({
                        PrismaCountBadge(count = 4, status = PrismaBadgeStatus.Accent)
                    })
                    ListItemTrailing.Switch -> ({
                        PrismaSwitch(checked = switchOn, onCheckedChange = { switchOn = it })
                    })
                },
                onClick = if (clickable) {
                    {}
                } else null,
                selected = selected,
                enabled = enabled,
            )
        },
        knobs = {
            StringKnobRow("Primary", primary, { primary = it })
            StringKnobRow("Secondary", secondary, { secondary = it }, placeholder = "Optional sub-text")
            EnumKnobRow("Leading", leading, ListItemLeading.values().toList(), { leading = it }, optionLabel = { it.name })
            EnumKnobRow("Trailing", trailing, ListItemTrailing.values().toList(), { trailing = it }, optionLabel = { it.name })
            BoolKnobRow("Clickable", clickable, { clickable = it })
            BoolKnobRow("Selected", selected, { selected = it })
            BoolKnobRow("Enabled", enabled, { enabled = it })
        },
        code = {
            "PrismaListItem(\n    primary = \"$primary\",\n" +
                (if (secondary.isNotBlank()) "    secondary = \"$secondary\",\n" else "") +
                (if (selected) "    selected = true,\n" else "") +
                (if (!enabled) "    enabled = false,\n" else "") +
                (if (clickable) "    onClick = { /* … */ },\n" else "") +
                ")"
        },
        states = {
            StateCell("With avatar (toggle selected)") { SelectableListItemDemo() }
            StateCell("Selected") {
                PrismaListItem(primary = "Settings", secondary = "Account & preferences", selected = true, onClick = {})
            }
            StateCell("With chevron") {
                PrismaListItem(primary = "Notifications", trailing = { Icon(painter = painterResource(PrismaIcons.ChevronRight), contentDescription = null, tint = PrismaSemanticColors.TextTertiary.themed()) }, onClick = {})
            }
            StateCell("Disabled") {
                PrismaListItem(primary = "Coming soon", secondary = "Locked feature", enabled = false)
            }
        },
        a11y = {
            A11yPanel(
                role = "ListItem (Button when onClick provided)",
                minTouchTarget = "48 dp height (default) / 56 dp / 72 dp",
                bullets = listOf(
                    "mergeDescendants groups primary + secondary + leading + trailing into one a11y unit.",
                    "Selected state is exposed via the selected property in semantics.",
                    "Trailing actions (switch, badge) keep their own roles when independently activatable.",
                ),
            )
        },
    )
}

@Composable public fun AvatarShowcase() {
    var seed by rememberSaveable { mutableStateOf("Maya Chen") }
    var size by rememberSaveable { mutableStateOf(PrismaAvatarSize.Lg) }
    var status by rememberSaveable { mutableStateOf(PrismaAvatarStatus.Online) }

    PlaygroundScaffold(
        preview = {
            PrismaAvatar(seed = seed.ifBlank { "?" }, size = size, status = status)
        },
        knobs = {
            StringKnobRow(
                label = "Seed (name)",
                value = seed,
                onChange = { seed = it },
                placeholder = "Full name",
                helper = "Initials and background colour are derived deterministically from this string.",
            )
            EnumKnobRow("Size", size, PrismaAvatarSize.values().toList(), { size = it }, optionLabel = { it.name })
            EnumKnobRow("Status", status, PrismaAvatarStatus.values().toList(), { status = it }, optionLabel = { it.name })
        },
        code = {
            "PrismaAvatar(seed = \"$seed\"" +
                (if (size != PrismaAvatarSize.Default) ", size = PrismaAvatarSize.${size.name}" else "") +
                (if (status != PrismaAvatarStatus.None) ", status = PrismaAvatarStatus.${status.name}" else "") +
                ")"
        },
        states = {
            StateCell("Sizes") {
                Row(horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
                    PrismaAvatar(seed = "Maya Chen", size = PrismaAvatarSize.Xs)
                    PrismaAvatar(seed = "Maya Chen", size = PrismaAvatarSize.Sm)
                    PrismaAvatar(seed = "Maya Chen", size = PrismaAvatarSize.Default)
                    PrismaAvatar(seed = "Maya Chen", size = PrismaAvatarSize.Lg)
                }
            }
            StateCell("Online") { PrismaAvatar(seed = "Aanya Patel", status = PrismaAvatarStatus.Online) }
            StateCell("Away") { PrismaAvatar(seed = "Bilal Khan", status = PrismaAvatarStatus.Away) }
            StateCell("Busy") { PrismaAvatar(seed = "Cara Liu", status = PrismaAvatarStatus.Busy) }
            StateCell("Offline") { PrismaAvatar(seed = "Dev Iyer", status = PrismaAvatarStatus.Offline) }
        },
        a11y = {
            A11yPanel(
                role = "Image / decorative",
                minTouchTarget = "n/a (decorative); wraps interactive when used in lists",
                bullets = listOf(
                    "contentDescription is the seed name plus status — \"Maya Chen, online\".",
                    "Initials are derived from the seed; the status dot is not announced separately.",
                    "When used inside a clickable row, the row's onClick takes precedence; avatar becomes decorative.",
                ),
            )
        },
    )
}

@Composable public fun AvatarGroupShowcase() {
    var seedsCsv by rememberSaveable { mutableStateOf("Maya Chen, Aanya Patel, Bilal Khan, Cara Liu, Dev Iyer, Eva Park") }
    var maxVisible by rememberSaveable { mutableStateOf(4) }
    var size by rememberSaveable { mutableStateOf(PrismaAvatarSize.Default) }
    val seeds: ImmutableList<String> = remember(seedsCsv) {
        seedsCsv.split(',').map { it.trim() }.filter { it.isNotEmpty() }.ifEmpty { listOf("?") }.toImmutableList()
    }
    PlaygroundScaffold(
        preview = { PrismaAvatarGroup(seeds = seeds, size = size, max = maxVisible.coerceAtLeast(1)) },
        knobs = {
            StringKnobRow("Seeds (comma separated)", seedsCsv, { seedsCsv = it })
            IntKnobRow("Max visible", maxVisible, range = 1..8, onChange = { maxVisible = it })
            EnumKnobRow("Size", size, PrismaAvatarSize.values().toList(), { size = it }, optionLabel = { it.name })
        },
        code = {
            "PrismaAvatarGroup(\n    seeds = persistentListOf(${seeds.joinToString { "\"$it\"" }}),\n" +
                (if (size != PrismaAvatarSize.Default) "    size = PrismaAvatarSize.${size.name},\n" else "") +
                (if (maxVisible != 4) "    max = $maxVisible,\n" else "") +
                ")"
        },
        states = {
            StateCell("Few") { PrismaAvatarGroup(seeds = persistentListOf("Maya", "Aanya")) }
            StateCell("Many (overflow)") { PrismaAvatarGroup(seeds = (1..8).map { "User $it" }.toImmutableList(), max = 4) }
        },
        a11y = {
            A11yPanel(
                role = "Group (decorative or labelled)",
                minTouchTarget = "n/a unless wrapped in a Button",
                bullets = listOf(
                    "Provide a single contentDescription summarising members — \"6 collaborators including Maya, Aanya, Bilal, and 3 others\".",
                    "Don't expose individual avatars to a11y; the group is one focus stop.",
                    "When clickable, wrap the entire group in a single labelled Role.Button.",
                ),
            )
        },
    )
}

@Composable public fun DividerShowcase() {
    var weight by rememberSaveable { mutableStateOf(PrismaDividerWeight.Default) }
    var inset by rememberSaveable { mutableStateOf(0) }
    PlaygroundScaffold(
        preview = { PrismaHorizontalDivider(weight = weight, inset = inset.dp, modifier = Modifier.fillMaxWidth()) },
        knobs = {
            EnumKnobRow("Weight", weight, PrismaDividerWeight.values().toList(), { weight = it }, optionLabel = { it.name })
            IntKnobRow("Inset (dp)", inset, range = 0..96, step = 8, onChange = { inset = it })
        },
        code = {
            "PrismaHorizontalDivider(" +
                (if (weight != PrismaDividerWeight.Default) "weight = PrismaDividerWeight.${weight.name}" else "") +
                (if (inset > 0) (if (weight != PrismaDividerWeight.Default) ", " else "") + "inset = ${inset}.dp" else "") +
                ")"
        },
        states = {
            StateCell("Subtle") { PrismaHorizontalDivider(weight = PrismaDividerWeight.Subtle) }
            StateCell("Default") { PrismaHorizontalDivider(weight = PrismaDividerWeight.Default) }
            StateCell("Strong") { PrismaHorizontalDivider(weight = PrismaDividerWeight.Strong) }
            StateCell("Inset (56dp)") { PrismaHorizontalDivider(inset = 56.dp) }
        },
        a11y = {
            A11yPanel(
                role = "Decorative",
                minTouchTarget = "n/a",
                bullets = listOf(
                    "Dividers are purely visual; do not expose them to screen readers.",
                    "Use heading() on the section title above instead — that's the structural cue AT users navigate.",
                    "Inset variants reinforce hierarchy visually; semantics are the same.",
                ),
            )
        },
    )
}

// endregion
