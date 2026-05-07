package xyz.ksharma.prisma.catalogue.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import xyz.ksharma.prisma.catalogue.playground.A11yPanel
import xyz.ksharma.prisma.catalogue.playground.BoolKnobRow
import xyz.ksharma.prisma.catalogue.playground.EnumKnobRow
import xyz.ksharma.prisma.catalogue.playground.IconKnobRow
import xyz.ksharma.prisma.catalogue.playground.PlaygroundScaffold
import xyz.ksharma.prisma.catalogue.playground.StateCell
import xyz.ksharma.prisma.catalogue.playground.StringKnobRow
import xyz.ksharma.prisma.components.button.PrismaButton
import xyz.ksharma.prisma.components.button.PrismaButtonSize
import xyz.ksharma.prisma.components.button.PrismaButtonVariant
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaSemanticColors

/**
 * Button playground.
 *
 * - Knobs: text, variant, size, disabled, loading, leadingIcon, trailingIcon.
 * - "Tap to test" wires loading auto-resolve so users can feel the live cycle
 *   without leaving the loading toggle on permanently.
 * - States gallery pins the canonical states (default, disabled, loading,
 *   destructive, ghost, icon-only) so users can compare without knob work.
 */
@Composable
public fun ButtonShowcase() {
    var text by rememberSaveable { mutableStateOf("Save changes") }
    var variant by rememberSaveable { mutableStateOf(PrismaButtonVariant.Primary) }
    var size by rememberSaveable { mutableStateOf(PrismaButtonSize.Default) }
    var disabled by rememberSaveable { mutableStateOf(false) }
    var loading by rememberSaveable { mutableStateOf(false) }
    var leadingIcon by rememberSaveable { mutableStateOf<Int?>(null) }
    var trailingIcon by rememberSaveable { mutableStateOf<Int?>(null) }
    var tapCount by rememberSaveable { mutableStateOf(0) }

    // Auto-resolve loading after a simulated 2 second save so the toggle
    // demonstrates the full loading lifecycle rather than getting stuck.
    LaunchedEffect(loading) {
        if (loading) {
            delay(2000)
            loading = false
        }
    }

    PlaygroundScaffold(
        preview = {
            PrismaButton(
                text = if (loading) "Saving…" else text.ifBlank { " " },
                variant = variant,
                size = size,
                enabled = !disabled,
                loading = loading,
                onClick = {
                    if (!disabled && !loading) {
                        tapCount++
                        loading = true
                    }
                },
                leadingIcon = leadingIcon?.let { res ->
                    { Icon(painterResource(res), contentDescription = null) }
                },
                trailingIcon = trailingIcon?.let { res ->
                    { Icon(painterResource(res), contentDescription = null) }
                },
                contentDescription = if (variant == PrismaButtonVariant.Icon) text.ifBlank { "Action" } else null,
            )
        },
        knobs = {
            StringKnobRow(
                label = "Text",
                value = text,
                onChange = { text = it },
                placeholder = "Button label",
                helper = "Tap the live preview to fire a 2s loading cycle (taps so far: $tapCount).",
            )
            EnumKnobRow(
                label = "Variant",
                value = variant,
                values = PrismaButtonVariant.values().toList(),
                onChange = { variant = it },
                optionLabel = { it.name },
            )
            EnumKnobRow(
                label = "Size",
                value = size,
                values = PrismaButtonSize.values().toList(),
                onChange = { size = it },
                optionLabel = { it.name },
            )
            BoolKnobRow(label = "Disabled", value = disabled, onChange = { disabled = it })
            BoolKnobRow(
                label = "Loading",
                value = loading,
                onChange = { loading = it },
                helper = "Toggle on to see the spinner; auto-resolves in 2s.",
            )
            IconKnobRow(
                label = "Leading icon",
                value = leadingIcon,
                options = ICON_OPTIONS,
                onChange = { leadingIcon = it },
            )
            IconKnobRow(
                label = "Trailing icon",
                value = trailingIcon,
                options = ICON_OPTIONS,
                onChange = { trailingIcon = it },
            )
        },
        code = {
            buildString {
                append("PrismaButton(\n")
                append("    text = \"${text}\",\n")
                if (variant != PrismaButtonVariant.Primary) append("    variant = PrismaButtonVariant.${variant.name},\n")
                if (size != PrismaButtonSize.Default) append("    size = PrismaButtonSize.${size.name},\n")
                if (disabled) append("    enabled = false,\n")
                if (loading) append("    loading = true,\n")
                leadingIcon?.let { append("    leadingIcon = { Icon(painterResource(PrismaIcons.X), contentDescription = null) },\n") }
                trailingIcon?.let { append("    trailingIcon = { Icon(painterResource(PrismaIcons.X), contentDescription = null) },\n") }
                append("    onClick = { /* … */ },\n")
                append(")")
            }
        },
        states = {
            StateCell("Default") {
                PrismaButton(text = "Save", onClick = {})
            }
            StateCell("Secondary") {
                PrismaButton(text = "Cancel", variant = PrismaButtonVariant.Secondary, onClick = {})
            }
            StateCell("Outlined") {
                PrismaButton(text = "Discard", variant = PrismaButtonVariant.Outlined, onClick = {})
            }
            StateCell("Ghost") {
                PrismaButton(text = "Skip", variant = PrismaButtonVariant.Ghost, onClick = {})
            }
            StateCell("Destructive") {
                PrismaButton(text = "Delete", variant = PrismaButtonVariant.Destructive, onClick = {})
            }
            StateCell("Disabled") {
                PrismaButton(text = "Save", enabled = false, onClick = {})
            }
            StateCell("Loading") {
                PrismaButton(text = "Saving…", loading = true, onClick = {})
            }
            StateCell("Icon-only (heart)") { LikeHeartButton() }
        },
        a11y = {
            A11yPanel(
                role = "Button",
                minTouchTarget = "48 × 48 dp",
                bullets = listOf(
                    "The visible label IS the a11y label. Icon variant requires an explicit contentDescription.",
                    "Loading state announces \"Loading\"; the click is no-op while loading but the label is still read.",
                    "Disabled state conveys \"dimmed/unavailable\" via Role.Button + enabled=false.",
                    "Hit area expands to 48dp even when visual size (Sm) is smaller — never shrink the touch target.",
                    "Reduced motion: press scale / ripple suppressed by the system; background swap remains.",
                ),
            )
        },
    )
}

/**
 * Interactive heart toggle for the states gallery — taps fill/unfill the
 * heart so the button visibly responds, demonstrating that icon-only
 * variants are real, stateful affordances.
 */
@Composable
private fun LikeHeartButton() {
    var liked by rememberSaveable { mutableStateOf(false) }
    PrismaButton(
        text = "",
        variant = PrismaButtonVariant.Icon,
        onClick = { liked = !liked },
        contentDescription = if (liked) "Unlike" else "Like",
        leadingIcon = {
            Icon(
                painter = painterResource(PrismaIcons.Heart),
                contentDescription = null,
                tint = if (liked) PrismaSemanticColors.StatusDangerDefault.themed()
                       else androidx.compose.material3.LocalContentColor.current,
            )
        },
    )
}

private val ICON_OPTIONS: List<Pair<String, Int>> = listOf(
    "Plus" to PrismaIcons.Plus,
    "Search" to PrismaIcons.Search,
    "Heart" to PrismaIcons.Heart,
    "ArrowRight" to PrismaIcons.ArrowRight,
    "ArrowLeft" to PrismaIcons.ArrowLeft,
    "Check" to PrismaIcons.Check,
    "Close" to PrismaIcons.Close,
    "Settings" to PrismaIcons.Settings,
    "Edit" to PrismaIcons.Edit,
    "Trash" to PrismaIcons.Trash,
    "Share" to PrismaIcons.Share,
    "Upload" to PrismaIcons.Upload,
    "Download" to PrismaIcons.Download,
)
