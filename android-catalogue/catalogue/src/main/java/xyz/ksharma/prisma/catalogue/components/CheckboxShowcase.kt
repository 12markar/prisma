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
import xyz.ksharma.prisma.catalogue.playground.A11yPanel
import xyz.ksharma.prisma.catalogue.playground.BoolKnobRow
import xyz.ksharma.prisma.catalogue.playground.PlaygroundScaffold
import xyz.ksharma.prisma.catalogue.playground.StateCell
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
        states = {
            StateCell("Unchecked") { PrismaCheckbox(checked = false, onCheckedChange = {}, label = "Default unchecked") }
            StateCell("Checked") { PrismaCheckbox(checked = true, onCheckedChange = {}, label = "Default checked") }
            StateCell("Indeterminate") { PrismaTriStateCheckbox(state = ToggleableState.Indeterminate, onClick = {}, label = "Indeterminate") }
            StateCell("Disabled checked") { PrismaCheckbox(checked = true, onCheckedChange = null, label = "Locked on", enabled = false) }
            StateCell("Disabled unchecked") { PrismaCheckbox(checked = false, onCheckedChange = null, label = "Locked off", enabled = false) }
            StateCell("Error") {
                PrismaCheckbox(
                    checked = false,
                    onCheckedChange = {},
                    label = "Required",
                    helperText = "This field is required.",
                    isError = true,
                )
            }
            StateCell("Group + parent") { GroupExample() }
        },
        a11y = {
            A11yPanel(
                role = "Checkbox / TriStateCheckbox",
                minTouchTarget = "48 × 48 dp",
                bullets = listOf(
                    "State (checked / unchecked / indeterminate) is announced; the label is read in the same pass.",
                    "Indeterminate is for parent groups — children remain individual booleans.",
                    "Error state appends \"required\" or the error message via stateDescription.",
                    "Disabled is conveyed via the role; the visual dimming alone is not sufficient.",
                ),
            )
        },
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
