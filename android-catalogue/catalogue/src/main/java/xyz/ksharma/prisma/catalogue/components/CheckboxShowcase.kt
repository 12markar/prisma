package xyz.ksharma.prisma.catalogue.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import xyz.ksharma.prisma.catalogue.playground.A11yReport
import xyz.ksharma.prisma.catalogue.playground.BoolKnobRow
import xyz.ksharma.prisma.catalogue.playground.PlaygroundScaffold
import xyz.ksharma.prisma.catalogue.playground.PlaygroundState
import xyz.ksharma.prisma.catalogue.playground.StringKnobRow
import xyz.ksharma.prisma.components.checkbox.PrismaCheckbox
import xyz.ksharma.prisma.components.checkbox.PrismaTriStateCheckbox
import xyz.ksharma.prisma.tokens.PrismaSpacing

@Composable
public fun CheckboxShowcase() {
    var checked by rememberSaveable { mutableStateOf(false) }
    var label by rememberSaveable { mutableStateOf("Email me product updates") }
    var helper by rememberSaveable { mutableStateOf("Roughly twice a month. No spam.") }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var error by rememberSaveable { mutableStateOf(false) }

    PlaygroundScaffold(
        preview = {
            PrismaCheckbox(
                checked = checked,
                onCheckedChange = if (enabled) ({ checked = it }) else null,
                label = label.takeIf { it.isNotBlank() },
                helperText = helper.takeIf { it.isNotBlank() },
                enabled = enabled,
                isError = error,
            )
        },
        knobs = {
            BoolKnobRow("Checked", checked, { checked = it })
            BoolKnobRow("Enabled", enabled, { enabled = it })
            BoolKnobRow("Error", error, { error = it })
            StringKnobRow("Label", label, { label = it })
            StringKnobRow("Helper", helper, { helper = it })
        },
        code = {
            buildString {
                append("PrismaCheckbox(\n")
                append("    checked = checked,\n    onCheckedChange = { checked = it },\n")
                if (label.isNotBlank()) append("    label = \"${label}\",\n")
                if (helper.isNotBlank()) append("    helperText = \"${helper}\",\n")
                if (!enabled) append("    enabled = false,\n")
                if (error) append("    isError = true,\n")
                append(")")
            }
        },
        pagerStates = listOf(
            PlaygroundState("Unchecked") { PrismaCheckbox(checked = false, onCheckedChange = {}, label = "Default unchecked") },
            PlaygroundState("Checked") { PrismaCheckbox(checked = true, onCheckedChange = {}, label = "Default checked") },
            PlaygroundState("Indeterminate") { PrismaTriStateCheckbox(state = ToggleableState.Indeterminate, onClick = {}, label = "Indeterminate") },
            PlaygroundState("Disabled checked") { PrismaCheckbox(checked = true, onCheckedChange = null, label = "Locked on", enabled = false) },
            PlaygroundState("Error") {
                PrismaCheckbox(
                    checked = false,
                    onCheckedChange = {},
                    label = "Required",
                    helperText = "This field is required.",
                    isError = true,
                )
            },
            PlaygroundState("Group + parent") { GroupExample() },
        ),
        a11yReport = A11yReport(
            role = "Checkbox / TriStateCheckbox (parent-child group)",
            minTouchTarget = "48 × 48 dp / 44 × 44 pt — full row is the tap target",
            screenReader = "TalkBack and VoiceOver announce the role (\"Checkbox\"), the label, then the state (\"Checked\" / \"Not checked\" / \"Partially checked\"). The label is read in the same pass — no re-focus needed. Indeterminate is reserved for parent rows whose children disagree.",
            voiceControl = "Voice Access targets the visible label (\"Tap Mentions\"). Saying the label toggles the box without precise targeting. The full row is clickable, not just the box itself.",
            keyboard = "Tab focuses, Space toggles. Enter does not toggle (matches platform convention). Disabled boxes are skipped from Tab order via the role; visual dimming is supporting, not primary.",
            contrast = "Unchecked outline uses border.strong (3:1 non-text contrast); checked fill uses accent.default at 4.6:1; the check glyph itself meets 4.5:1 against the fill. Error border uses status.danger at 4.7:1.",
            touchTarget = "Whole row is the tap target — at least 48 × 48 dp / 44 × 44 pt — even when the visible box is 20 dp. Spacing between adjacent checkboxes prevents fat-finger toggles on the wrong row.",
            wcagQuote = "For all user interface components … the name and role can be programmatically determined; states, properties, and values that can be set by the user can be programmatically set.",
            wcagRef = "4.1.2 Name, Role, Value, Level A",
        ),
    )
}

@Composable
private fun GroupExample() {
    var alpha by rememberSaveable { mutableStateOf(true) }
    var beta by rememberSaveable { mutableStateOf(false) }
    var gamma by rememberSaveable { mutableStateOf(true) }

    val parent = when {
        alpha && beta && gamma -> ToggleableState.On
        !alpha && !beta && !gamma -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }

    Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp1)) {
        PrismaTriStateCheckbox(
            state = parent,
            onClick = {
                val newValue = parent != ToggleableState.On
                alpha = newValue; beta = newValue; gamma = newValue
            },
            label = "Notifications",
        )
        Column(modifier = Modifier.padding(start = PrismaSpacing.Sp7)) {
            PrismaCheckbox(checked = alpha, onCheckedChange = { alpha = it }, label = "Mentions")
            PrismaCheckbox(checked = beta, onCheckedChange = { beta = it }, label = "DMs")
            PrismaCheckbox(checked = gamma, onCheckedChange = { gamma = it }, label = "Releases")
        }
    }
}
