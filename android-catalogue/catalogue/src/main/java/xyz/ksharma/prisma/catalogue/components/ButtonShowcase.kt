package xyz.ksharma.prisma.catalogue.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import xyz.ksharma.prisma.catalogue.playground.A11yReport
import xyz.ksharma.prisma.catalogue.playground.A11ySheetContent
import xyz.ksharma.prisma.catalogue.playground.BoolKnobRow
import xyz.ksharma.prisma.catalogue.playground.CodeBlock
import xyz.ksharma.prisma.catalogue.playground.EnumKnobRow
import xyz.ksharma.prisma.catalogue.playground.IconKnobRow
import xyz.ksharma.prisma.catalogue.playground.PlaygroundState
import xyz.ksharma.prisma.catalogue.playground.PreviewSurface
import xyz.ksharma.prisma.catalogue.playground.StatesPager
import xyz.ksharma.prisma.catalogue.playground.StringKnobRow
import xyz.ksharma.prisma.components.button.PrismaButton
import xyz.ksharma.prisma.components.button.PrismaButtonSize
import xyz.ksharma.prisma.components.button.PrismaButtonVariant
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Pilot redesign of the playground UX. Replaces the long scrollable
 * "knobs above states above code" stack with a focused screen:
 *
 *   1. Live preview surface (large, breathing)
 *   2. Action pill row — Edit · A11y
 *   3. States horizontal pager (fixed height, page dots)
 *   4. Inline code snippet
 *
 * Knobs and the accessibility report each open in their own bottom sheet
 * so the live preview never gets pushed off-screen by a long edit panel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun ButtonShowcase() {
    var text by rememberSaveable { mutableStateOf("Save changes") }
    var variant by rememberSaveable { mutableStateOf(PrismaButtonVariant.Primary) }
    var size by rememberSaveable { mutableStateOf(PrismaButtonSize.Default) }
    // Single "Enabled" toggle replaces the separate Disabled bool — clearer
    // mental model: ON means the component is enabled.
    var enabled by rememberSaveable { mutableStateOf(true) }
    var loading by rememberSaveable { mutableStateOf(false) }
    var leadingIcon by rememberSaveable { mutableStateOf<Int?>(null) }
    var trailingIcon by rememberSaveable { mutableStateOf<Int?>(null) }
    var tapCount by rememberSaveable { mutableStateOf(0) }
    // Loading is purely driven by the knob toggle now. Previously a tap also
    // forced loading=true for 2s, which made the button feel "always
    // loading" — the loading state should follow the user's explicit choice.

    var knobsOpen by rememberSaveable { mutableStateOf(false) }
    var a11yOpen by rememberSaveable { mutableStateOf(false) }

    // No verticalScroll here — DetailPane already wraps the showcase in a
    // verticalScroll, and nesting two scrollables (each with infinite max
    // height) crashes Compose at measure-time.
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp5),
    ) {
        // 1. Live preview — taller and centered for breathing room.
        PreviewSurface(modifier = Modifier.heightIn(min = 240.dp)) {
            PrismaButton(
                text = if (loading) "Saving…" else text.ifBlank { " " },
                variant = variant,
                size = size,
                enabled = enabled,
                loading = loading,
                onClick = {
                    if (enabled && !loading) tapCount++
                },
                leadingIcon = leadingIcon?.let { res ->
                    { Icon(painterResource(res), contentDescription = null) }
                },
                trailingIcon = trailingIcon?.let { res ->
                    { Icon(painterResource(res), contentDescription = null) }
                },
                contentDescription = if (variant == PrismaButtonVariant.Icon) text.ifBlank { "Action" } else null,
            )
        }

        // 2. Action pills — Edit + A11y stay always-visible above the fold so
        //    the user never has to hunt for controls.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
        ) {
            ActionPill(
                iconRes = PrismaIcons.Edit,
                label = "Edit",
                modifier = Modifier.weight(1f),
                onClick = { knobsOpen = true },
            )
            ActionPill(
                iconRes = PrismaIcons.Eye,
                label = "A11y",
                modifier = Modifier.weight(1f),
                onClick = { a11yOpen = true },
            )
        }

        // 3. States horizontal pager — fixed height per state cell so swiping
        //    between states feels stable, not jittery.
        SectionLabel("States — swipe to compare")
        StatesPager(states = buttonStates())

        // 4. Inline code snippet for the current preview configuration.
        SectionLabel("Usage")
        CodeBlock(
            code = buildSnippet(text, variant, size, enabled, loading, leadingIcon, trailingIcon),
        )

        // Tap counter footer — small affordance to confirm interactivity.
        Text(
            text = "Live preview taps: $tapCount",
            style = PrismaTypography.BodySm,
            color = PrismaSemanticColors.TextTertiary.themed(),
            modifier = Modifier.padding(top = PrismaSpacing.Sp2),
        )
    }

    // Knobs sheet
    if (knobsOpen) {
        ModalBottomSheet(
            onDismissRequest = { knobsOpen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = PrismaSemanticColors.SurfaceBase.themed(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PrismaSpacing.Sp5, vertical = PrismaSpacing.Sp4),
                verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
            ) {
                Text(
                    text = "Edit",
                    style = PrismaTypography.HeadlineSm,
                    color = PrismaSemanticColors.TextPrimary.themed(),
                )
                StringKnobRow("Text", text, { text = it }, placeholder = "Button label")
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
                BoolKnobRow(
                    label = "Enabled",
                    value = enabled,
                    onChange = { enabled = it },
                    helper = "Off renders the component in its disabled state.",
                )
                BoolKnobRow(
                    label = "Loading",
                    value = loading,
                    onChange = { loading = it },
                    helper = "Toggle on to render the spinner state.",
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
                PrismaButton(
                    text = "Done",
                    onClick = { knobsOpen = false },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    // A11y sheet
    if (a11yOpen) {
        ModalBottomSheet(
            onDismissRequest = { a11yOpen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = PrismaSemanticColors.SurfaceBase.themed(),
        ) {
            A11ySheetContent(report = ButtonA11yReport)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = PrismaTypography.LabelSm,
        color = PrismaSemanticColors.TextTertiary.themed(),
    )
}

@Composable
private fun ActionPill(
    iconRes: Int,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(PrismaSemanticColors.SurfaceRaised.themed())
            .border(
                1.dp,
                PrismaSemanticColors.BorderSubtle.themed(),
                RoundedCornerShape(PrismaRadius.Md),
            )
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = PrismaSemanticColors.TextSecondary.themed(),
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = label,
            style = PrismaTypography.LabelMd,
            color = PrismaSemanticColors.TextPrimary.themed(),
        )
    }
}

@Composable
private fun buttonStates(): List<PlaygroundState> = listOf(
    PlaygroundState("Default") { PrismaButton(text = "Save changes", onClick = {}) },
    PlaygroundState("Secondary") { PrismaButton(text = "Cancel", variant = PrismaButtonVariant.Secondary, onClick = {}) },
    PlaygroundState("Outlined") { PrismaButton(text = "Discard", variant = PrismaButtonVariant.Outlined, onClick = {}) },
    PlaygroundState("Ghost") { PrismaButton(text = "Skip", variant = PrismaButtonVariant.Ghost, onClick = {}) },
    PlaygroundState("Destructive") { PrismaButton(text = "Delete", variant = PrismaButtonVariant.Destructive, onClick = {}) },
    PlaygroundState("Disabled") { PrismaButton(text = "Save", enabled = false, onClick = {}) },
    PlaygroundState("Loading") { PrismaButton(text = "Saving…", loading = true, onClick = {}) },
    PlaygroundState("Icon-only (heart)") { LikeHeartButton() },
)

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
                       else LocalContentColor.current,
            )
        },
    )
}

private fun buildSnippet(
    text: String,
    variant: PrismaButtonVariant,
    size: PrismaButtonSize,
    enabled: Boolean,
    loading: Boolean,
    leadingIcon: Int?,
    trailingIcon: Int?,
): String = buildString {
    append("PrismaButton(\n")
    append("    text = \"$text\",\n")
    if (variant != PrismaButtonVariant.Primary) append("    variant = PrismaButtonVariant.${variant.name},\n")
    if (size != PrismaButtonSize.Default) append("    size = PrismaButtonSize.${size.name},\n")
    if (!enabled) append("    enabled = false,\n")
    if (loading) append("    loading = true,\n")
    if (leadingIcon != null) append("    leadingIcon = { Icon(painterResource(PrismaIcons.X), contentDescription = null) },\n")
    if (trailingIcon != null) append("    trailingIcon = { Icon(painterResource(PrismaIcons.X), contentDescription = null) },\n")
    append("    onClick = { /* … */ },\n")
    append(")")
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

private val ButtonA11yReport = A11yReport(
    role = "Button",
    minTouchTarget = "48 × 48 dp / 44 × 44 pt",
    screenReader = "The visible label IS the accessible label. For icon-only variants, contentDescription / accessibilityLabel is required and enforced as a precondition. Loading state announces \"Loading\"; the click is no-op while loading but the label still reads.",
    voiceControl = "Voice Access (Android) / Voice Control (iOS) target the role and label, so \"Tap Save changes\" or \"Tap 5\" (with number overlay) both activate the right button. The icon variant relies on its accessibilityLabel for the spoken target.",
    keyboard = "Tab focuses; Space / Enter activates. Compose's default focus order matches the visual order. Disabled state is reachable but unactivated. No custom shortcuts — use the surrounding screen's shortcuts (e.g. Modal's confirm button as default).",
    contrast = "All button variants verified at WCAG AA in light + dark themes — body label ≥ 4.5:1 against the background, hover/pressed states stay above 4.5:1 too. Build-time contrast checker (scripts/check-contrast.mjs) gates this.",
    touchTarget = "Hit area expands to 48 × 48 dp on Android and 44 × 44 pt on iOS even when the visual size (Sm) is smaller. Never shrink the touch target to match the visual; the visual is purely cosmetic.",
    wcagQuote = "The size of the target for pointer inputs is at least 24 by 24 CSS pixels — Prisma exceeds this with 48 dp / 44 pt across every variant.",
    wcagRef = "2.5.8 Target Size (Minimum), Level AA",
)
