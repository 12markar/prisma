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
import xyz.ksharma.prisma.catalogue.playground.A11yReport
import xyz.ksharma.prisma.catalogue.playground.BoolKnobRow
import xyz.ksharma.prisma.catalogue.playground.EnumKnobRow
import xyz.ksharma.prisma.catalogue.playground.IntKnobRow
import xyz.ksharma.prisma.catalogue.playground.PlaygroundScaffold
import xyz.ksharma.prisma.catalogue.playground.PlaygroundState
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
        pagerStates = listOf(
            PlaygroundState("On") { PrismaSwitch(checked = true, onCheckedChange = {}, label = "Push notifications") },
            PlaygroundState("Off") { PrismaSwitch(checked = false, onCheckedChange = {}, label = "Auto-sync") },
            PlaygroundState("Disabled (on)") { PrismaSwitch(checked = true, onCheckedChange = null, label = "Locked on", enabled = false) },
            PlaygroundState("With helper") {
                PrismaSwitch(checked = true, onCheckedChange = {}, label = "Auto-sync", helperText = "Updates over Wi-Fi only.")
            },
            PlaygroundState("Standalone") { PrismaSwitch(checked = true, onCheckedChange = {}) },
        ),
        a11yReport = A11yReport(
            role = "Switch",
            minTouchTarget = "48 × 48 dp / 44 × 44 pt",
            screenReader = "TalkBack and VoiceOver announce the role (\"Switch\"), the label, then the current state (\"On\" / \"Off\"). Toggling fires Selection haptic and announces the new state without re-focusing.",
            voiceControl = "Voice Access / Voice Control target the visible label (\"Tap Push notifications\"). Saying \"Toggle Push notifications\" works whether the switch is currently on or off.",
            keyboard = "Tab focuses the switch; Space / Enter toggles. Focus ring matches the accent color and meets the 3:1 non-text contrast bar in light + dark themes.",
            contrast = "Track-off uses border.default (3.1:1 against the surface); track-on uses accent.default (4.6:1). The thumb sits at 4.5:1 against either track. Build-time check-contrast.mjs gates regressions.",
            touchTarget = "Hit area extends 48 × 48 dp / 44 × 44 pt around the visible thumb so misses on the edge of the track still register as a toggle.",
            wcagQuote = "For all user interface components … the name and role can be programmatically determined; states, properties, and values that can be set by the user can be programmatically set.",
            wcagRef = "4.1.2 Name, Role, Value, Level A",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Selected") { PrismaRadio(selected = true, onClick = {}, label = "Selected option") },
            PlaygroundState("Unselected") { PrismaRadio(selected = false, onClick = {}, label = "Unselected option") },
            PlaygroundState("With helper") { PrismaRadio(selected = false, onClick = {}, label = "Yearly", helperText = "$90 per year (save 17%).") },
            PlaygroundState("Disabled") { PrismaRadio(selected = false, onClick = null, label = "Locked", enabled = false) },
        ),
        a11yReport = A11yReport(
            role = "RadioButton (inside selectableGroup)",
            minTouchTarget = "48 × 48 dp / 44 × 44 pt",
            screenReader = "Wrapping the radio set in selectableGroup means TalkBack and VoiceOver announce \"option N of M\". Only one radio in the group is selected; the selected state is read alongside the label.",
            voiceControl = "Voice Access / Voice Control target the visible label (\"Tap Yearly\"). The full row is the tap target — assistive tech doesn't need to land precisely on the radio circle.",
            keyboard = "Tab moves into the group, then arrow keys move between radios within the group (per Material guidelines). Selection follows focus so screen reader users hear the current option immediately.",
            contrast = "Selected ring uses accent.default (4.6:1); unselected ring uses border.strong (3:1) — the 3:1 floor for non-text UI matches WCAG 1.4.11. Helper text is text.tertiary, still above the 4.5:1 body bar.",
            touchTarget = "Entire row is clickable so the tap target extends well past the 48 dp visible circle. Spacing between rows keeps adjacent radios from accidentally registering.",
            wcagQuote = "Information, structure, and relationships conveyed through presentation can be programmatically determined or are available in text.",
            wcagRef = "1.3.1 Info and Relationships, Level A",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Continuous") {
                var v by rememberSaveable { mutableStateOf(0.4f) }
                PrismaSlider(value = v, onValueChange = { v = it }, label = "Volume", valueFormatter = { "${(it * 100).toInt()}%" })
            },
            PlaygroundState("Stepped (1–5)") {
                var s by rememberSaveable { mutableStateOf(2f) }
                PrismaSlider(value = s, onValueChange = { s = it }, valueRange = 1f..5f, steps = 3, label = "Rating", valueFormatter = { it.toInt().toString() })
            },
            PlaygroundState("Disabled") {
                PrismaSlider(value = 0.7f, onValueChange = {}, label = "Read-only", enabled = false)
            },
        ),
        a11yReport = A11yReport(
            role = "SeekBar / Slider (progressSemantics)",
            minTouchTarget = "Thumb 48 × 48 dp / 44 × 44 pt hit area",
            screenReader = "TalkBack reads the label, current value, and range. Supply a valueFormatter so \"60 percent\" or \"4 of 5 stars\" is spoken instead of a raw 0.6 — much more useful to a non-sighted user.",
            voiceControl = "Voice Access supports \"Set Volume to 80\" by name. The thumb is independently focusable so \"Tap thumb\" works as a fallback when the label is not announced.",
            keyboard = "Arrow keys nudge by the step value (or 1% for continuous); Home / End jump to min / max. Page-Up / Page-Down move by 10% chunks for fine-vs-coarse control.",
            contrast = "Active track uses accent.default (4.6:1); inactive track is border.subtle (3.1:1) — the 3:1 floor for non-text UI is met. The thumb has a 2 dp shadow for tactile separation in light theme.",
            touchTarget = "Slider thumb hit-tests as 48 dp even when the visible thumb is smaller — drag accuracy doesn't degrade with one-handed phone use.",
            wcagQuote = "All functionality that uses single-pointer dragging movements for operation can be achieved by a single pointer without dragging — unless dragging is essential.",
            wcagRef = "2.5.7 Dragging Movements, Level AA (WCAG 2.2)",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Two") {
                var s by rememberSaveable { mutableStateOf("Off") }
                PrismaSegmentedControl(options = persistentListOf("Off", "On"), selected = s, onSelect = { s = it })
            },
            PlaygroundState("Three") {
                var s by rememberSaveable { mutableStateOf("Day") }
                PrismaSegmentedControl(options = persistentListOf("Day", "Week", "Month"), selected = s, onSelect = { s = it })
            },
            PlaygroundState("Sizes") {
                var s by rememberSaveable { mutableStateOf("M") }
                PrismaSegmentedControl(options = persistentListOf("S", "M", "L"), selected = s, onSelect = { s = it })
            },
        ),
        a11yReport = A11yReport(
            role = "Tab (inside selectableGroup) — for in-place filtering, not navigation",
            minTouchTarget = "48 dp / 44 pt height across the entire row",
            screenReader = "Each segment is a Tab; the row is a selectableGroup so screen readers announce \"selected N of M\". Switching segment immediately reads the new selection without re-focusing.",
            voiceControl = "Voice Access targets each visible label (\"Tap Week\"). Numbers can also work as fallbacks (\"Tap 2\") via the labels overlay.",
            keyboard = "Tab moves into the group, arrow keys cycle between segments — selection follows focus so a screen reader user hears each segment as they move.",
            contrast = "Selected segment uses surface.raised on accent.subtle (4.7:1 against the indicator); unselected segments are text.secondary on surface.sunken — both above 4.5:1 body.",
            touchTarget = "Even the small (S/M/L) variant keeps the row at 44 pt — the cell width may shrink but the height never drops below the minimum.",
            wcagQuote = "For all user interface components … the name and role can be programmatically determined; states, properties, and values that can be set by the user can be programmatically set.",
            wcagRef = "4.1.2 Name, Role, Value, Level A",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Empty") {
                var s by rememberSaveable { mutableStateOf("") }
                PrismaSearchBar(value = s, onValueChange = { s = it }, placeholder = "Search…")
            },
            PlaygroundState("With query") {
                var s by rememberSaveable { mutableStateOf("compose") }
                PrismaSearchBar(value = s, onValueChange = { s = it })
            },
            PlaygroundState("Disabled") {
                PrismaSearchBar(value = "Read-only", onValueChange = {}, enabled = false)
            },
        ),
        a11yReport = A11yReport(
            role = "EditText (search semantics)",
            minTouchTarget = "48 dp / 44 pt height",
            screenReader = "TalkBack and VoiceOver announce \"Search field\" and read the placeholder as a hint, not as a label. The clear button is its own focusable element labelled \"Clear search\" so it can be activated independently.",
            voiceControl = "Voice Access / Voice Control target the placeholder or label as the spoken handle. Saying \"Clear search\" hits the trailing × button without the user knowing its visual location.",
            keyboard = "Tab focuses the input; the IME action is Search, so the on-screen keyboard returns the search affordance. ESC clears focus without committing a query.",
            contrast = "Placeholder uses text.tertiary (4.5:1 against surface.raised); the search icon is text.secondary (5.4:1). Focus ring meets the 3:1 floor for non-text UI in light + dark themes.",
            touchTarget = "The whole row is the tap target — 48 dp / 44 pt. The clear × is also independently 48 × 48 dp / 44 × 44 pt so it's hittable on devices with no styling overrides.",
            wcagQuote = "Labels or instructions are provided when content requires user input.",
            wcagRef = "3.3.2 Labels or Instructions, Level A",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Default") {
                var v by rememberSaveable { mutableStateOf(1) }
                PrismaStepper(value = v, onValueChange = { v = it }, range = 0..10)
            },
            PlaygroundState("At max") {
                var v by rememberSaveable { mutableStateOf(10) }
                PrismaStepper(value = v, onValueChange = { v = it }, range = 0..10)
            },
            PlaygroundState("Disabled") { PrismaStepper(value = 5, onValueChange = {}, range = 0..10, enabled = false) },
        ),
        a11yReport = A11yReport(
            role = "Stepper (separate Increment / Decrement buttons)",
            minTouchTarget = "48 × 48 dp / 44 × 44 pt per button",
            screenReader = "Each button is independently focusable. TalkBack reads the role (\"Decrement\" / \"Increment\") plus the current value (\"3 of 10\"). When the limit is reached, the disabled state is announced so users don't keep tapping a no-op button.",
            voiceControl = "Voice Access / Voice Control work via the visible labels. Saying \"Tap Plus\" or \"Tap Increment\" both work — no precise tap needed.",
            keyboard = "Custom accessibilityActions for Increment / Decrement let switch-control and keyboard users adjust the value without precise targeting; arrow keys also work when the stepper is focused.",
            contrast = "Button glyphs (+ / −) use text.primary (10:1+); disabled state is text.disabled at exactly the 3:1 floor for non-text UI to communicate the limit visually as well as via role.",
            touchTarget = "Each button is exactly 48 × 48 dp / 44 × 44 pt — never compressed below the minimum. Spacing between + and − keeps fat-finger taps from hitting both.",
            wcagQuote = "The size of the target for pointer inputs is at least 24 by 24 CSS pixels, except where the target is exempted.",
            wcagRef = "2.5.8 Target Size (Minimum), Level AA (WCAG 2.2)",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Empty") {
                var t: ImmutableList<String> by rememberSaveable { mutableStateOf(persistentListOf()) }
                PrismaTagInput(tags = t, onTagsChange = { t = it }, label = "Tags", placeholder = "Type and Enter")
            },
            PlaygroundState("Filled") {
                var t: ImmutableList<String> by rememberSaveable { mutableStateOf(persistentListOf("swift", "swiftui")) }
                PrismaTagInput(tags = t, onTagsChange = { t = it }, label = "Topics")
            },
        ),
        a11yReport = A11yReport(
            role = "EditText + associated chip list (polite live region)",
            minTouchTarget = "48 dp / 44 pt per chip; chip × also 48 × 48 dp / 44 × 44 pt",
            screenReader = "Adding a tag fires a polite announcement (\"swift added\"). Removing fires \"swift removed\". Each chip's × is its own focusable element so deletion is discoverable without backspace tricks.",
            voiceControl = "Voice Access / Voice Control target each chip's visible label, and \"Remove kotlin\" hits its × directly. The input itself is a regular text field so dictation works as expected.",
            keyboard = "Backspace on empty input deletes the last chip and announces it. Each chip is reachable via Tab; pressing Delete / Backspace on a focused chip removes it.",
            contrast = "Chip background is surface.sunken with text.primary (10:1+); the × icon uses text.secondary (5.4:1). Focus ring on chips meets 3:1 non-text contrast.",
            touchTarget = "Chips are 32 dp tall but reside in a 48 dp row; the × is independently 24 dp visible / 48 dp hit. Spacing is Sp2 horizontally so adjacent chips don't merge under fat fingers.",
            wcagQuote = "In content implemented using markup languages, status messages can be programmatically determined through role or properties such that they can be presented to the user by assistive technologies without receiving focus.",
            wcagRef = "4.1.3 Status Messages, Level AA",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Default") {
                var s by rememberSaveable { mutableStateOf("") }
                PrismaAutocomplete(value = s, onValueChange = { s = it }, suggestions = persistentListOf(), onSelect = { s = it }, label = "Country")
            },
        ),
        a11yReport = A11yReport(
            role = "Combobox (input + listbox popup)",
            minTouchTarget = "48 dp / 44 pt per suggestion row",
            screenReader = "Suggestion count is announced when the popup opens (\"6 suggestions\"). Each row reads as the typed-ahead match. Selecting a row dismisses the popup and announces the chosen value back into the field.",
            voiceControl = "Voice Access / Voice Control target each suggestion by its visible text. Saying \"Tap Bangkok\" picks it without the user needing to know its index in the list.",
            keyboard = "Arrow up / down moves focus through suggestions while the caret stays in the input. Enter / tap selects; Escape closes the popup and returns focus to the input — the typed query is preserved.",
            contrast = "Suggestion rows use text.primary (10:1+); the active row highlight is surface.sunken (3:1 against base). Hover and focus rings are accent.default at 4.6:1.",
            touchTarget = "Each row is at least 48 dp / 44 pt. The popup auto-sizes to the input width on phones and switches to a centred sheet on small screens to keep rows full-width.",
            wcagQuote = "For all user interface components … the name and role can be programmatically determined; states, properties, and values that can be set by the user can be programmatically set.",
            wcagRef = "4.1.2 Name, Role, Value, Level A",
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable public fun DatePickerShowcase() {
    // No states pager — date pickers are large and there's only one
    // canonical visual; the live preview is enough.
    PlaygroundScaffold(
        preview = { PrismaDatePicker() },
        code = { "val state = rememberDatePickerState()\nPrismaDatePicker(state = state)" },
        a11yReport = A11yReport(
            role = "Calendar (Material 3 / native iOS DatePicker)",
            minTouchTarget = "48 dp / 44 pt grid cells",
            screenReader = "M3 DatePicker handles month / year navigation announcements. \"Today\" is announced when focused; the selected date is announced on commit. Each cell reads as the full date (\"7 May 2026, Thursday\") to remove ambiguity.",
            voiceControl = "Voice Access / Voice Control work on the month/year header for fast navigation, then individual cells (\"Tap 23\"). Voice-Control numbers overlay also lets users select any visible cell by index.",
            keyboard = "Arrow keys move by day; Page-Up / Page-Down by month; Shift-Page-Up / Down by year. Tab moves between header controls and the grid.",
            contrast = "Today's outline is accent.default (4.6:1 against surface); the selected fill uses accent.default with text.onAccent for 4.5:1+ readability. Out-of-month dimmed days still meet 3:1.",
            touchTarget = "Grid cells are 48 dp / 44 pt minimum even when the picker is shown in a narrow modal. Header chevrons are independently 48 × 48 dp / 44 × 44 pt.",
            wcagQuote = "The purpose of each input field collecting information about the user can be programmatically determined when … the field serves a purpose identified in the Input Purposes for User Interface Components.",
            wcagRef = "1.3.5 Identify Input Purpose, Level AA",
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable public fun TimePickerShowcase() {
    // No states pager — single canonical visual; the live preview is enough.
    PlaygroundScaffold(
        preview = { PrismaTimePicker() },
        code = { "val state = rememberTimePickerState()\nPrismaTimePicker(state = state)" },
        a11yReport = A11yReport(
            role = "Time picker (Material 3 / native iOS DatePicker .time)",
            minTouchTarget = "48 dp / 44 pt per control",
            screenReader = "Hour and minute controls have separate roles and announce the current value on focus. Switching between hour and minute fires an immediate announcement so users know which segment they're editing.",
            voiceControl = "AM/PM is a button group with explicit labels (\"Tap PM\"). Hour and minute can be set by voice via the keyboard input mode (\"Use keyboard\" toggle on iOS / Android).",
            keyboard = "Tab cycles hour → minute → AM/PM → OK / Cancel. Arrow keys increment / decrement the focused segment. Numeric keys (when in keyboard mode) overwrite the segment directly.",
            contrast = "Active segment is filled accent.default at 4.6:1; inactive segment is surface.sunken with text.primary (10:1). Dial numbers meet 4.5:1 against the dial surface in both themes.",
            touchTarget = "Dial cells are 48 dp / 44 pt minimum. The mode-switch icon (clock / keyboard) is independently 48 × 48 / 44 × 44 so users can flip modes with a single tap.",
            wcagQuote = "The purpose of each input field collecting information about the user can be programmatically determined when … the field serves a purpose identified in the Input Purposes for User Interface Components.",
            wcagRef = "1.3.5 Identify Input Purpose, Level AA",
        ),
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
        // No states pager — single canonical visual; the live preview is enough.
        a11yReport = A11yReport(
            role = "ColorPicker (RGB sliders + swatch)",
            minTouchTarget = "Slider thumb 48 × 48 dp / 44 × 44 pt",
            screenReader = "Each channel slider is independently focusable and announces its value (\"Red, 199 of 255\"). The current colour is also announced as a hex value when the swatch is focused so non-sighted users get a precise readout.",
            voiceControl = "Voice Access / Voice Control target each slider by label (\"Tap Red\"). Number-input mode lets users dictate exact values (\"Set Red to 200\").",
            keyboard = "Tab cycles between R / G / B sliders. Arrow keys nudge the channel by 1; Page-Up / Page-Down by 10. Hex input field accepts a 6-character code via paste / typing.",
            contrast = "Slider tracks meet 3:1 non-text contrast against the surface. The hex / RGB readout uses text.primary (10:1+) so values stay readable at any selected colour.",
            touchTarget = "Slider thumbs hit-test as 48 × 48 dp. The colour swatch button is independently 48 × 48 / 44 × 44 to copy the hex to the clipboard.",
            wcagQuote = "Color is not used as the only visual means of conveying information, indicating an action, prompting a response, or distinguishing a visual element.",
            wcagRef = "1.4.1 Use of Color, Level A",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Info") { PrismaToast(message = "New version available.", kind = PrismaToastKind.Info) },
            PlaygroundState("Success") { PrismaToast(message = "Saved successfully.", kind = PrismaToastKind.Success, actionLabel = "Undo", onAction = {}) },
            PlaygroundState("Warning") { PrismaToast(message = "Connection looks slow.", kind = PrismaToastKind.Warning) },
            PlaygroundState("Danger") { PrismaToast(message = "Could not reach server.", kind = PrismaToastKind.Danger, actionLabel = "Retry", onAction = {}) },
        ),
        a11yReport = A11yReport(
            role = "Live region (transient announcement)",
            minTouchTarget = "Action button 48 × 48 dp / 44 × 44 pt",
            screenReader = "Info / success use Polite live region — they're announced after the current speech finishes. Warning / danger use Assertive so they interrupt mid-utterance. The kind prefix (\"Danger.\") makes the severity unambiguous.",
            voiceControl = "Action labels (\"Undo\", \"Retry\") are spoken targets — Voice Access supports \"Tap Undo\" without the user knowing where the toast sits on screen.",
            keyboard = "Action button retains its own Role.Button so Tab can reach it during the toast's lifetime. Pressing ESC dismisses the toast (matches Material 3 behaviour).",
            contrast = "Each kind pairs background and text colours that meet 4.5:1 body — danger uses status.danger background with on-status.danger text. Action label keeps 4.5:1 against either kind background.",
            touchTarget = "Action button is at least 48 dp / 44 pt tall. The toast's swipe-to-dismiss area extends across the full toast width.",
            wcagQuote = "In content implemented using markup languages, status messages can be programmatically determined through role or properties such that they can be presented to the user by assistive technologies without receiving focus.",
            wcagRef = "4.1.3 Status Messages, Level AA",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Info") { PrismaBanner(title = "Server upgrade", description = "Slower response times expected.", kind = PrismaBannerKind.Info, onDismiss = {}, actionLabel = "Learn more", onAction = {}) },
            PlaygroundState("Success") { PrismaBanner(title = "Profile updated", description = "Saved across all devices.", kind = PrismaBannerKind.Success, onDismiss = {}) },
            PlaygroundState("Warning") { PrismaBanner(title = "Storage almost full", description = "Less than 1GB free.", kind = PrismaBannerKind.Warning, actionLabel = "Manage", onAction = {}) },
            PlaygroundState("Danger") { PrismaBanner(title = "Action required", description = "Verify your email.", kind = PrismaBannerKind.Danger, actionLabel = "Verify", onAction = {}) },
        ),
        a11yReport = A11yReport(
            role = "Live region (inline alert)",
            minTouchTarget = "Action / dismiss buttons 48 × 48 dp / 44 × 44 pt",
            screenReader = "Title + description merge into a single announcement on appearance — users don't need to focus the banner to hear it. Polite for info / success, Assertive for warning / danger so they pre-empt the current speech.",
            voiceControl = "Action and dismiss labels are individually spoken targets. \"Tap Verify\" or \"Tap Close banner\" both work without the user needing to find the banner visually.",
            keyboard = "Tab moves into the banner reaching action then dismiss. Pressing ESC closes a dismissable banner; non-dismissable banners stay anchored.",
            contrast = "Each kind pairs an accent strip with text on a tinted background — all combinations meet 4.5:1 body across light + dark themes. The 4 dp left strip uses kind.default at 3:1 against surface for non-text contrast.",
            touchTarget = "Action and dismiss are independently 48 × 48 dp / 44 × 44 pt even when the banner is rendered tightly. Spacing between them prevents accidental dismiss when the user means to act.",
            wcagQuote = "In content implemented using markup languages, status messages can be programmatically determined through role or properties such that they can be presented to the user by assistive technologies without receiving focus.",
            wcagRef = "4.1.3 Status Messages, Level AA",
        ),
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
        // No states pager — modals are trigger-based; the live preview's
        // "Open modal" button is the only meaningful canonical view.
        a11yReport = A11yReport(
            role = "Modal Dialog (focus trap)",
            minTouchTarget = "48 × 48 dp / 44 × 44 pt confirm / dismiss",
            screenReader = "Title is announced on open with role \"alert\" so screen readers read it immediately. Body follows; confirm and dismiss buttons are focusable in order. Destructive variant tints the confirm button red — the role itself doesn't change, so screen readers still announce \"button\".",
            voiceControl = "Confirm and dismiss labels are individually spoken targets. \"Tap Delete\" or \"Tap Cancel\" works without precise targeting; the trigger stays disabled until the modal closes.",
            keyboard = "Focus traps inside the modal — Tab cycles between buttons without escaping. ESC dismisses (returns focus to the trigger). Enter activates the focused button.",
            contrast = "Scrim is surface.inverse @ 64% so the underlying screen is dimmed but visible. Modal background is surface.raised. Destructive confirm uses status.danger at 4.6:1 against the surface.",
            touchTarget = "Confirm and dismiss are at least 48 × 48 dp / 44 × 44 pt. Stacked layout on narrow screens still respects the minimum height; spacing between buttons prevents mis-taps.",
            wcagQuote = "If keyboard focus can be moved to a component … then focus can be moved away from that component using only a keyboard interface, and, if it requires more than unmodified arrow or tab keys or other standard exit methods, the user is advised of the method.",
            wcagRef = "2.1.2 No Keyboard Trap, Level A",
        ),
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
        // No states pager — sheet is trigger-based; the live preview is enough.
        a11yReport = A11yReport(
            role = "Modal Sheet (focus trap)",
            minTouchTarget = "Drag handle 48 × 48 dp / 44 × 44 pt; content interactive",
            screenReader = "TalkBack and VoiceOver announce \"Sheet\" on open. Content behind the sheet is hidden from a11y so swipe-explore stays inside the sheet. Drag handle has its own Role.Button announcing \"drag handle, double-tap to expand\".",
            voiceControl = "Drag handle is independently spoken (\"Tap drag handle\") for users who can't reach it visually. All sheet content is targetable by its visible label.",
            keyboard = "Focus traps inside the sheet. Swipe-down (or scrim tap) and ESC dismiss the sheet, returning focus to the trigger button. Tab cycles within the sheet.",
            contrast = "Sheet uses surface.raised against a surface.inverse @ 64% scrim so depth is communicated visually as well as via role. Drag handle is border.strong at 3:1 non-text contrast.",
            touchTarget = "Drag handle is 48 × 48 dp / 44 × 44 pt. Sheet content respects component-level minimums; sheet itself can grow to multiple presentation detents.",
            wcagQuote = "If keyboard focus can be moved to a component … then focus can be moved away from that component using only a keyboard interface.",
            wcagRef = "2.1.2 No Keyboard Trap, Level A",
        ),
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
        // No states pager — popover is trigger-based; the live preview is enough.
        a11yReport = A11yReport(
            role = "Popover (non-modal overlay)",
            minTouchTarget = "Trigger 48 × 48 dp / 44 × 44 pt",
            screenReader = "Lighter than Modal — does not trap focus. Screen readers announce the popover content but the user can also explore the page underneath. Tap-outside / ESC dismisses and returns focus to the trigger.",
            voiceControl = "Anchored to the trigger; positioning auto-flips to stay on-screen so Voice Control's number overlay can hit any visible label inside the popover.",
            keyboard = "Tab moves into the popover from the trigger. ESC closes. Tab past the last popover element returns to the next page-level focus stop — no trap.",
            contrast = "Popover uses surface.raised with a 1 dp border.subtle (3:1 non-text contrast). The shadow gives depth in light theme; in dark theme the border alone communicates the boundary.",
            touchTarget = "Trigger respects component-level minimums (48 × 48 dp / 44 × 44 pt). Popover content matches component-level minimums for any embedded controls.",
            wcagQuote = "Additional content that becomes visible and then hidden, in response to keyboard focus or pointer hover, [must be] dismissable, hoverable, persistent.",
            wcagRef = "1.4.13 Content on Hover or Focus, Level AA",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Copy") {
                PrismaTooltip(text = "Save to clipboard") {
                    Icon(painter = painterResource(PrismaIcons.Copy), contentDescription = "Copy", tint = PrismaSemanticColors.TextPrimary.themed(), modifier = Modifier.size(24.dp))
                }
            },
            PlaygroundState("Star") {
                PrismaTooltip(text = "Star this item") {
                    Icon(painter = painterResource(PrismaIcons.Star), contentDescription = "Star", tint = PrismaSemanticColors.TextPrimary.themed(), modifier = Modifier.size(24.dp))
                }
            },
        ),
        a11yReport = A11yReport(
            role = "Tooltip (label association — not interactive itself)",
            minTouchTarget = "Trigger element 48 × 48 dp / 44 × 44 pt",
            screenReader = "Tooltip text serves as the trigger's accessible label. Screen readers read \"Copy\" without the tooltip needing to visually appear — the popup is just the visual representation of a label assistive tech already had.",
            voiceControl = "Voice Access targets the tooltip text directly (\"Tap Copy\"). The visible icon-only trigger is reachable by its hidden label.",
            keyboard = "Focusing the trigger surfaces the tooltip after the platform delay. ESC dismisses without losing focus. The tooltip is dismissable (can be hidden), hoverable (can be entered), and persistent (stays until dismissed).",
            contrast = "Tooltip uses surface.inverse with text.onInverse — both meet 4.5:1 body in light + dark themes. The arrow / pointer is the same colour for visual continuity.",
            touchTarget = "Tooltip is non-interactive — only the trigger needs the touch-target minimum. Icon-only triggers must size to 48 × 48 dp / 44 × 44 pt regardless of glyph size.",
            wcagQuote = "Where receiving and then removing pointer hover or keyboard focus triggers additional content to become visible and then hidden, the additional content [must be] dismissable, hoverable, persistent.",
            wcagRef = "1.4.13 Content on Hover or Focus, Level AA",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Circular Sm") { PrismaCircularLoading(size = PrismaLoadingSize.Sm) },
            PlaygroundState("Circular Md") { PrismaCircularLoading(size = PrismaLoadingSize.Md) },
            PlaygroundState("Circular Lg") { PrismaCircularLoading(size = PrismaLoadingSize.Lg) },
            PlaygroundState("Linear (indeterminate)") { PrismaLinearLoading(modifier = Modifier.fillMaxWidth()) },
            PlaygroundState("Linear (60%)") { PrismaLinearLoading(modifier = Modifier.fillMaxWidth(), progress = 0.6f) },
        ),
        a11yReport = A11yReport(
            role = "ProgressBar (non-interactive)",
            minTouchTarget = "n/a — loaders aren't tappable",
            screenReader = "Indeterminate variant uses progressSemantics with no value — TalkBack and VoiceOver read it as \"In progress\" or \"Loading\". Determinate variant exposes 0–1; screen readers announce the percentage on focus / change.",
            voiceControl = "Loader has no spoken target by itself. Pair it with a sibling label (\"Loading projects…\") so users have something to talk about while waiting.",
            keyboard = "Loader is not focusable. Surrounding container should hold focus (or move it to the loaded content once ready) so Tab order doesn't break.",
            contrast = "Active arc / bar uses accent.default at 4.6:1; the inactive track is border.subtle at 3:1 — non-text contrast minimum is met. Reduce-motion users still see colour change without animation.",
            touchTarget = "n/a. The loader itself is purely visual; ensure any cancel button next to it respects the 48 × 48 dp / 44 × 44 pt minimum.",
            wcagQuote = "In content implemented using markup languages, status messages can be programmatically determined through role or properties such that they can be presented to the user by assistive technologies without receiving focus.",
            wcagRef = "4.1.3 Status Messages, Level AA",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Line") { PrismaSkeletonLine(modifier = Modifier.fillMaxWidth().height(12.dp)) },
            PlaygroundState("Circle") { PrismaSkeletonCircle(modifier = Modifier.size(40.dp)) },
            PlaygroundState("Block") { PrismaSkeletonBlock(modifier = Modifier.fillMaxWidth().height(80.dp), cornerRadius = 12.dp) },
        ),
        a11yReport = A11yReport(
            role = "Decorative (hidden from a11y tree)",
            minTouchTarget = "n/a — skeletons are decorative",
            screenReader = "Marked invisibleToUser so TalkBack and VoiceOver skip the placeholder entirely. Pair with a sibling polite announcement (\"Loading projects…\") so AT users know content is on the way.",
            voiceControl = "No spoken targets — the skeleton has no labels. The user interacts with the loaded content once the skeleton swaps out.",
            keyboard = "Skeleton is not focusable. Once real content arrives, focus / live region should pick it up automatically (place focus on the heading or fire a status message).",
            contrast = "Skeleton fill uses surface.sunken with a subtle shimmer ramp. Reduced-motion preference disables the shimmer entirely; the placeholder remains visible against surface.base at 3:1+.",
            touchTarget = "n/a — skeletons are static placeholders.",
            wcagQuote = "Animation triggered by interaction can be disabled, unless the animation is essential to the functionality or the information being conveyed.",
            wcagRef = "2.3.3 Animation from Interactions, Level AAA",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Single") { PrismaCountBadge(count = 1) },
            PlaygroundState("Two-digit") { PrismaCountBadge(count = 12) },
            PlaygroundState("Cap (99+)") { PrismaCountBadge(count = 250) },
            PlaygroundState("Success") { PrismaCountBadge(count = 3, status = PrismaBadgeStatus.Success) },
            PlaygroundState("Warning") { PrismaCountBadge(count = 7, status = PrismaBadgeStatus.Warning) },
            PlaygroundState("Danger") { PrismaCountBadge(count = 99, status = PrismaBadgeStatus.Danger) },
            PlaygroundState("Dot") { PrismaDotBadge(status = PrismaBadgeStatus.Accent) },
        ),
        a11yReport = A11yReport(
            role = "Decorative — the carrier (icon, button, tab) owns semantics",
            minTouchTarget = "n/a — badges are not interactive themselves",
            screenReader = "Badge alone is meaningless to a screen reader. Append the count and meaning to the parent's contentDescription — e.g. \"Inbox, 5 unread messages\". The status colour is read by AT only via the parent label.",
            voiceControl = "No spoken target. The carrier underneath the badge is the targetable element — Voice Access taps the icon, not the badge.",
            keyboard = "Badge is not focusable. The carrier owns focus; pressing it should reveal the underlying content (e.g. opening the unread inbox).",
            contrast = "Status badges meet 3:1 non-text contrast against the carrier (icon button, tab). The numeric text inside meets 4.5:1 against the badge fill in light + dark themes.",
            touchTarget = "n/a. The carrier underneath must respect 48 × 48 dp / 44 × 44 pt; the badge is purely visual decoration.",
            wcagQuote = "The visual presentation of the following have a contrast ratio of at least 3:1 against adjacent color(s): User Interface Components: Visual information required to identify user interface components and states.",
            wcagRef = "1.4.11 Non-text Contrast, Level AA",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Default") {
                PrismaEmptyState(title = "No projects yet", description = "When you create a project, it'll show up here.", action = { PrismaButton(text = "Create project", onClick = {}) })
            },
            PlaygroundState("Just a title") { PrismaEmptyState(title = "Nothing here") },
        ),
        a11yReport = A11yReport(
            role = "Heading + body + optional action",
            minTouchTarget = "Action button 48 × 48 dp / 44 × 44 pt",
            screenReader = "Title carries heading semantics so screen reader users can jump to it via heading-by-heading navigation. Body description is read after the title. The optional action button retains its own Role.Button.",
            voiceControl = "Action label is a spoken target (\"Tap Create project\"). The verb-led label is intentional — it tells voice users exactly what saying it will do.",
            keyboard = "Tab moves directly to the action when present; without an action the empty state is just a static heading + paragraph and is non-focusable.",
            contrast = "Title uses text.primary (10:1+); description uses text.secondary (5.4:1) — both above the 4.5:1 body floor. The illustration (when present) is decorative only.",
            touchTarget = "Action button respects component-level 48 × 48 dp / 44 × 44 pt. The empty-state container is a static layout; only the action is interactive.",
            wcagQuote = "Headings and labels describe topic or purpose.",
            wcagRef = "2.4.6 Headings and Labels, Level AA",
        ),
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
        // No states pager — drawer is trigger-based; the live preview is enough.
        a11yReport = A11yReport(
            role = "Modal sheet (off-canvas navigation)",
            minTouchTarget = "Trigger 48 × 48 dp / 44 × 44 pt; drawer items 48 / 44 pt",
            screenReader = "When open, focus traps inside the drawer and the main content goes inert. TalkBack / VoiceOver explore stays inside the drawer until it closes; on close, focus returns to the trigger automatically.",
            voiceControl = "Each drawer item has its own spoken label. Voice Access supports \"Tap [item label]\" — saying the visible name navigates without precise targeting.",
            keyboard = "Tab cycles within the drawer when open. ESC closes; swipe / scrim dismiss are the touch equivalents. Tab on the closed state moves over the trigger like any other button.",
            contrast = "Drawer panel uses surface.raised at 100% opacity. Scrim is surface.inverse @ 64% so background content stays visible while clearly de-emphasized. Drawer item rows respect text.primary 10:1+.",
            touchTarget = "Trigger respects component-level 48 × 48 / 44 × 44. Drawer rows are also at the same minimum — common nav-row pattern.",
            wcagQuote = "If keyboard focus can be moved to a component … then focus can be moved away from that component using only a keyboard interface.",
            wcagRef = "2.1.2 No Keyboard Trap, Level A",
        ),
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
        pagerStates = listOf(
            PlaygroundState("2 tabs") {
                var s by rememberSaveable { mutableStateOf("Inbox") }
                PrismaTabs(tabs = persistentListOf("Inbox", "Archive"), selected = s, onSelect = { s = it })
            },
            PlaygroundState("4 tabs") {
                var s by rememberSaveable { mutableStateOf("Overview") }
                PrismaTabs(tabs = persistentListOf("Overview", "Activity", "Settings", "Billing"), selected = s, onSelect = { s = it })
            },
            PlaygroundState("Last selected") {
                var s by rememberSaveable { mutableStateOf("Three") }
                PrismaTabs(tabs = persistentListOf("One", "Two", "Three"), selected = s, onSelect = { s = it })
            },
        ),
        a11yReport = A11yReport(
            role = "Tab (inside selectableGroup) — for switching between peer views",
            minTouchTarget = "48 dp / 44 pt height per tab",
            screenReader = "Each tab carries Role.Tab; the row is a selectableGroup so screen readers announce \"selected N of M\". Switching tabs immediately reads the new active tab; selection haptic fires only on change, not re-tap.",
            voiceControl = "Voice Access targets each tab by visible label (\"Tap Settings\"). The row is a single selectable group so tab order is preserved across orientations.",
            keyboard = "Tab moves into the row, then arrow keys cycle between tabs. Selection follows focus so a screen reader user hears each tab's content as they move. Home / End jump to first / last.",
            contrast = "Active tab indicator uses accent.default at 4.6:1 against the row background. Active label uses text.primary (10:1+); inactive uses text.secondary (5.4:1) — both above the 4.5:1 body floor.",
            touchTarget = "Each tab is at least 48 dp / 44 pt tall. On narrow widths, tabs scroll horizontally — they don't compress below the minimum.",
            wcagQuote = "More than one way is available to locate a Web page within a set of Web pages, except where the Web page is the result of, or a step in, a process.",
            wcagRef = "2.4.3 Focus Order, Level A",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Filter (toggle)") { ToggleableChipDemo("Android") },
            PlaygroundState("iOS (toggle)") { ToggleableChipDemo("iOS", initial = false) },
            PlaygroundState("Suggestion") { PrismaChip(label = "Trending", onClick = {}, variant = PrismaChipVariant.Suggestion) },
            PlaygroundState("Disabled") { PrismaChip(label = "Locked", onClick = {}, enabled = false) },
        ),
        a11yReport = A11yReport(
            role = "Button (filter / suggestion) — chip groups in selectableGroup for multi-select",
            minTouchTarget = "48 dp / 44 pt height; × button independently 48 × 48 dp / 44 × 44 pt",
            screenReader = "Selected state is announced as part of the role (\"Selected\" / \"Not selected\"). Haptic feedback fires on toggle so the change is felt as well as heard. Input chip's × is a separate action labelled \"Remove <chip>\".",
            voiceControl = "Voice Access / Voice Control target the chip's visible label. \"Tap iOS\" toggles it; \"Tap Remove iOS\" hits the × on input chips.",
            keyboard = "Chip is a button — Tab focuses, Space / Enter activates. The × on input chips is independently focusable so Delete on the chip triggers removal directly.",
            contrast = "Selected fill uses accent.default (4.6:1 against surface); unselected outline uses border.default at 3:1 non-text contrast. Label text meets 4.5:1 body in both states.",
            touchTarget = "Each chip is 32 dp tall but lives in a 48 dp tap target. The × is independently 48 × 48 / 44 × 44 so removal doesn't accidentally toggle the chip.",
            wcagQuote = "For all user interface components … the name and role can be programmatically determined; states, properties, and values that can be set by the user can be programmatically set.",
            wcagRef = "4.1.2 Name, Role, Value, Level A",
        ),
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
        // No states pager — palette is trigger-based; the live preview is enough.
        a11yReport = A11yReport(
            role = "Combobox (input + listbox)",
            minTouchTarget = "Each command row 48 dp / 44 pt",
            screenReader = "Filtering announces \"N results\" via a polite live region as the user types. Section headers (\"Foundations\", \"Actions\") use heading semantics so screen reader users can skip between them.",
            voiceControl = "Voice Access / Voice Control target each command's visible label. \"Tap Toggle theme\" works without the user knowing where the command sits in the list.",
            keyboard = "Cmd / Ctrl-K opens the palette; arrow keys navigate; Enter activates the focused command; ESC closes the palette and returns focus to the trigger. The full flow is keyboard-only.",
            contrast = "Active row highlight uses surface.sunken (3:1 against surface.raised). Section headers use text.tertiary (4.5:1+). Match-emphasis on filtered text uses accent.default at 4.6:1.",
            touchTarget = "Each row is 48 dp / 44 pt tall. Section headers are non-interactive; rows hold the entire row's hit area, not just the label width.",
            wcagQuote = "All functionality of the content is operable through a keyboard interface without requiring specific timings for individual keystrokes.",
            wcagRef = "2.1.1 Keyboard, Level A",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Few pages") {
                var p by rememberSaveable { mutableStateOf(2) }
                PrismaPagination(page = p, pageCount = 5, onPageChange = { p = it })
            },
            PlaygroundState("Many pages") {
                var p by rememberSaveable { mutableStateOf(12) }
                PrismaPagination(page = p, pageCount = 30, onPageChange = { p = it })
            },
            PlaygroundState("First page") {
                PrismaPagination(page = 1, pageCount = 10, onPageChange = {})
            },
            PlaygroundState("Last page") {
                PrismaPagination(page = 10, pageCount = 10, onPageChange = {})
            },
        ),
        a11yReport = A11yReport(
            role = "Navigation (each control is a Button)",
            minTouchTarget = "Each page button 48 × 48 dp / 44 × 44 pt",
            screenReader = "Wrap the row in a Role.Navigation labelled \"Pagination\". The current page exposes a selected state; previous / next arrows announce \"disabled\" at the edges so users know they've reached the boundary.",
            voiceControl = "Each page number and arrow is independently spoken. \"Tap Next page\" or \"Tap 5\" both work — voice users don't need to know which controls are present.",
            keyboard = "Tab moves through the row in document order. Enter / Space activates a page. Arrow Left / Right (when supported) jumps between adjacent pages without losing focus.",
            contrast = "Current page indicator uses accent.default (4.6:1). Disabled previous / next arrows use text.tertiary at exactly the 3:1 non-text-contrast floor so the disabled state is communicated visually as well as via role.",
            touchTarget = "Each button is 48 × 48 dp / 44 × 44 pt. Ellipsis is decorative (invisibleToUser) so it doesn't get focused. Spacing between buttons keeps adjacent taps distinct.",
            wcagQuote = "More than one way is available to locate a Web page within a set of Web pages, except where the Web page is the result of, or a step in, a process.",
            wcagRef = "2.4.5 Multiple Ways, Level AA",
        ),
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
        pagerStates = listOf(
            PlaygroundState("3 levels") { PrismaBreadcrumb(items = persistentListOf(PrismaBreadcrumbItem("Home", onClick = {}), PrismaBreadcrumbItem("Settings", onClick = {}), PrismaBreadcrumbItem("Profile"))) },
            PlaygroundState("4 levels") { PrismaBreadcrumb(items = persistentListOf(PrismaBreadcrumbItem("Home", onClick = {}), PrismaBreadcrumbItem("Components", onClick = {}), PrismaBreadcrumbItem("Inputs", onClick = {}), PrismaBreadcrumbItem("Button"))) },
        ),
        a11yReport = A11yReport(
            role = "Navigation (ordered list of Buttons; current page is plain text)",
            minTouchTarget = "48 × 48 dp / 44 × 44 pt per crumb",
            screenReader = "Wrap in a Role.Navigation labelled \"Breadcrumb\" so screen readers announce the navigation context. The last item is the current page rendered as plain text — not a link — so AT users know they've reached their destination.",
            voiceControl = "Each crumb is a spoken target by visible label (\"Tap Home\"). Separator slashes are decorative (invisibleToUser) so they don't pollute the spoken path.",
            keyboard = "Tab moves through the crumbs in document order. The current page is non-focusable; only ancestor links accept focus.",
            contrast = "Link crumbs use text.link (4.6:1+); the current page uses text.primary (10:1+). Separators are text.tertiary at 4.5:1.",
            touchTarget = "Each linkable crumb has a 48 × 48 dp / 44 × 44 pt hit area despite the visible text being smaller. Spacing between crumbs prevents accidental taps on the wrong level.",
            wcagQuote = "Information about the user's location within a set of Web pages is available.",
            wcagRef = "2.4.8 Location, Level AAA",
        ),
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
        pagerStates = listOf(
            PlaygroundState("3 steps, on 1") { PrismaWizardSteps(steps = persistentListOf("Account", "Profile", "Done"), activeIndex = 1) },
            PlaygroundState("Last step") { PrismaWizardSteps(steps = persistentListOf("A", "B", "C"), activeIndex = 2) },
        ),
        a11yReport = A11yReport(
            role = "Step indicator (progress with current/completed/upcoming states)",
            minTouchTarget = "n/a (non-interactive in catalogue; wired to nav externally)",
            screenReader = "Active step exposes the equivalent of aria-current=\"step\" so screen readers announce \"current step\". Completed steps announce as \"completed\"; future steps as \"upcoming\". Pair with a heading (\"Step 2 of 4: Profile\") for clearest navigation.",
            voiceControl = "Step labels are spoken targets when the steps are interactive (e.g. wired to nav). \"Tap Profile\" jumps to that step; current step is non-tappable.",
            keyboard = "When tied to nav, Tab moves through visited steps in document order. Forward steps are typically disabled until the user satisfies prerequisites — the disabled state is announced.",
            contrast = "Current-step ring uses accent.default (4.6:1); completed steps use accent.subtle with a check icon (3:1+); upcoming steps use border.default at 3:1 non-text contrast. Labels meet 4.5:1 body.",
            touchTarget = "When step indicators are tappable, each is 48 × 48 dp / 44 × 44 pt. The connector lines are decorative (invisibleToUser).",
            wcagQuote = "For Web pages that cause legal commitments or financial transactions for the user to occur … submissions are reversible, checked for input errors and the user is provided with an opportunity to correct them, or confirmed.",
            wcagRef = "3.3.4 Error Prevention (Legal, Financial, Data), Level AA",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Elevated") {
                PrismaCard(variant = PrismaCardVariant.Elevated) {
                    Text("Casts a shadow over the page. Use for emphasis.", style = PrismaTypography.BodySm, color = PrismaSemanticColors.TextSecondary.themed())
                }
            },
            PlaygroundState("Outlined") {
                PrismaCard(variant = PrismaCardVariant.Outlined) {
                    Text("1px subtle border. The most-used card.", style = PrismaTypography.BodySm, color = PrismaSemanticColors.TextSecondary.themed())
                }
            },
            PlaygroundState("Filled") {
                PrismaCard(variant = PrismaCardVariant.Filled) {
                    Text("Sunken surface, quieter than elevated.", style = PrismaTypography.BodySm, color = PrismaSemanticColors.TextSecondary.themed())
                }
            },
        ),
        a11yReport = A11yReport(
            role = "Container (interactive cards = Role.Button)",
            minTouchTarget = "48 × 48 dp / 44 × 44 pt when whole-card clickable",
            screenReader = "Static cards are read as a container — TalkBack and VoiceOver explore the content as separate items. Interactive cards merge descendants so the whole card reads as a single \"button\" with the title as the label.",
            voiceControl = "When the whole card is clickable, the visible title is the spoken target. Inner buttons (e.g. \"Open\") remain their own targets so users can act on either the card or a specific control.",
            keyboard = "Static cards are not focusable. Interactive cards are a single Tab stop; if the card has inner buttons too, those are separate focusable elements — choose either pattern, not both.",
            contrast = "Outlined card uses border.subtle at 3:1 non-text contrast against the surface. Elevated cards use a subtle shadow plus surface.raised (no border) — the elevation difference reads in light theme; in dark theme a 1 dp border supplements the shadow.",
            touchTarget = "Interactive cards are at least 48 × 48 dp / 44 × 44 pt. Inner action buttons retain their own minimums; padding around them keeps adjacent buttons from accidentally registering each other's taps.",
            wcagQuote = "For all user interface components … the name and role can be programmatically determined; states, properties, and values that can be set by the user can be programmatically set.",
            wcagRef = "4.1.2 Name, Role, Value, Level A",
        ),
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
        pagerStates = listOf(
            PlaygroundState("With avatar (toggle selected)") { SelectableListItemDemo() },
            PlaygroundState("Selected") {
                PrismaListItem(primary = "Settings", secondary = "Account & preferences", selected = true, onClick = {})
            },
            PlaygroundState("With chevron") {
                PrismaListItem(primary = "Notifications", trailing = { Icon(painter = painterResource(PrismaIcons.ChevronRight), contentDescription = null, tint = PrismaSemanticColors.TextTertiary.themed()) }, onClick = {})
            },
            PlaygroundState("Disabled") {
                PrismaListItem(primary = "Coming soon", secondary = "Locked feature", enabled = false)
            },
        ),
        a11yReport = A11yReport(
            role = "ListItem (Role.Button when onClick provided)",
            minTouchTarget = "48 dp / 44 pt height (default); 56 dp / 72 dp variants for two-line / three-line",
            screenReader = "mergeDescendants groups primary + secondary + leading + trailing into one a11y unit so the row reads as a single \"button\" labelled with the primary text. Selected state is exposed via the selected property; trailing controls (switch, badge) keep their own roles when independently activatable.",
            voiceControl = "Voice Access targets the visible primary text (\"Tap Settings\"). Trailing controls retain their own labels — \"Tap Toggle\" hits the trailing switch directly without activating the row.",
            keyboard = "Tab focuses the row when clickable. Space / Enter activates it. Trailing controls are independently focusable so a switch can be toggled without entering the row's destination.",
            contrast = "Primary text uses text.primary (10:1+); secondary uses text.secondary (5.4:1) — both well above 4.5:1 body. Selected background uses surface.sunken (3:1 against base).",
            touchTarget = "Whole row is 48 / 56 / 72 dp tall depending on density. Trailing controls (switch, button) keep their own 48 × 48 dp / 44 × 44 pt minimums.",
            wcagQuote = "The size of the target for pointer inputs is at least 24 by 24 CSS pixels, except where the target is exempted.",
            wcagRef = "2.5.8 Target Size (Minimum), Level AA (WCAG 2.2)",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Sizes") {
                Row(horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
                    PrismaAvatar(seed = "Maya Chen", size = PrismaAvatarSize.Xs)
                    PrismaAvatar(seed = "Maya Chen", size = PrismaAvatarSize.Sm)
                    PrismaAvatar(seed = "Maya Chen", size = PrismaAvatarSize.Default)
                    PrismaAvatar(seed = "Maya Chen", size = PrismaAvatarSize.Lg)
                }
            },
            PlaygroundState("Online") { PrismaAvatar(seed = "Aanya Patel", status = PrismaAvatarStatus.Online) },
            PlaygroundState("Away") { PrismaAvatar(seed = "Bilal Khan", status = PrismaAvatarStatus.Away) },
            PlaygroundState("Busy") { PrismaAvatar(seed = "Cara Liu", status = PrismaAvatarStatus.Busy) },
            PlaygroundState("Offline") { PrismaAvatar(seed = "Dev Iyer", status = PrismaAvatarStatus.Offline) },
        ),
        a11yReport = A11yReport(
            role = "Image (with text alternative)",
            minTouchTarget = "n/a — decorative on its own; 48 × 48 / 44 × 44 when wrapped in a Button",
            screenReader = "contentDescription is the seed name plus status — \"Maya Chen, online\". Initials are derived from the seed; the status dot is rolled into the same announcement so users get name + presence in one read.",
            voiceControl = "Voice Access / Voice Control target the avatar by its accessible label when interactive. When the avatar sits inside a clickable row, the row's label wins.",
            keyboard = "Avatar itself is not focusable. When interactive (e.g. a button-wrapped avatar that opens a profile), Tab focuses, Space / Enter activates.",
            contrast = "Initials text on the seed-derived background colour is calibrated to meet 4.5:1 across all 12 generated palettes. Status dots use status.* colours at 3:1 non-text contrast against the avatar fill.",
            touchTarget = "Avatar in a Button: 48 × 48 dp / 44 × 44 pt minimum. Decorative use has no minimum but the visible size scales by token (Xs / Sm / Md / Lg).",
            wcagQuote = "All non-text content that is presented to the user has a text alternative that serves the equivalent purpose.",
            wcagRef = "1.1.1 Non-text Content, Level A",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Few") { PrismaAvatarGroup(seeds = persistentListOf("Maya", "Aanya")) },
            PlaygroundState("Many (overflow)") { PrismaAvatarGroup(seeds = (1..8).map { "User $it" }.toImmutableList(), max = 4) },
        ),
        a11yReport = A11yReport(
            role = "Group (with summarising text alternative)",
            minTouchTarget = "n/a — decorative on its own; 48 × 48 / 44 × 44 when wrapped in a Button",
            screenReader = "Provide a single contentDescription summarising members — \"6 collaborators including Maya, Aanya, Bilal, and 3 others\". Don't expose individual avatars to a11y; the group reads as one unit.",
            voiceControl = "When the group is interactive (opens a member list / picker), the spoken target is the summary label, not the individual avatars.",
            keyboard = "Group itself is not focusable. When interactive (e.g. \"View all collaborators\"), it's a single Tab stop with the summary as its accessible name.",
            contrast = "The +N overflow chip uses surface.sunken with text.primary (10:1+). Each avatar's overlap ring matches the surrounding surface colour (3:1) so the stack reads as discrete circles, not a smear.",
            touchTarget = "When clickable, the entire group is a single 48 × 48 dp / 44 × 44 pt button — even though the visible avatar overlap is narrower than that.",
            wcagQuote = "All non-text content that is presented to the user has a text alternative that serves the equivalent purpose.",
            wcagRef = "1.1.1 Non-text Content, Level A",
        ),
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
        pagerStates = listOf(
            PlaygroundState("Subtle") { PrismaHorizontalDivider(weight = PrismaDividerWeight.Subtle) },
            PlaygroundState("Default") { PrismaHorizontalDivider(weight = PrismaDividerWeight.Default) },
            PlaygroundState("Strong") { PrismaHorizontalDivider(weight = PrismaDividerWeight.Strong) },
            PlaygroundState("Inset (56dp)") { PrismaHorizontalDivider(inset = 56.dp) },
        ),
        a11yReport = A11yReport(
            role = "Decorative — hidden from a11y tree",
            minTouchTarget = "n/a — dividers are non-interactive",
            screenReader = "Dividers are purely visual; not exposed to screen readers. Use a heading() on the section above instead — that's the structural cue AT users navigate by.",
            voiceControl = "No spoken target. Voice users navigate the headings above and below the divider; the divider itself is invisible to voice control.",
            keyboard = "Not focusable. Tab order skips dividers entirely.",
            contrast = "Default weight uses border.subtle at 3:1 against the surrounding surface — meets the non-text-contrast minimum even though the divider is decorative. Subtle and Strong variants give designers a calibrated range without dropping below the floor.",
            touchTarget = "n/a. Dividers occupy the row but consume no tap area.",
            wcagQuote = "Information, structure, and relationships conveyed through presentation can be programmatically determined or are available in text.",
            wcagRef = "1.3.1 Info and Relationships, Level A",
        ),
    )
}

// endregion
