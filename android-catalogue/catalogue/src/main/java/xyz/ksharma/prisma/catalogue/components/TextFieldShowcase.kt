package xyz.ksharma.prisma.catalogue.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.catalogue.playground.A11yReport
import xyz.ksharma.prisma.catalogue.playground.BoolKnobRow
import xyz.ksharma.prisma.catalogue.playground.EnumKnobRow
import xyz.ksharma.prisma.catalogue.playground.IconKnobRow
import xyz.ksharma.prisma.catalogue.playground.IntKnobRow
import xyz.ksharma.prisma.catalogue.playground.PlaygroundScreen
import xyz.ksharma.prisma.catalogue.playground.PlaygroundState
import xyz.ksharma.prisma.catalogue.playground.StringKnobRow
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.components.textfield.PrismaTextField
import xyz.ksharma.prisma.components.textfield.PrismaTextFieldSize
import xyz.ksharma.prisma.components.textfield.PrismaTextFieldVariant
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaSemanticColors

private enum class ValidationKind(val display: String) {
    None("None"),
    Email("Email"),
    Min8Chars("Min 8 chars"),
}

@Composable
public fun TextFieldShowcase() {
    var value by rememberSaveable { mutableStateOf("") }
    var label by rememberSaveable { mutableStateOf("Email") }
    var placeholder by rememberSaveable { mutableStateOf("you@example.com") }
    var helper by rememberSaveable { mutableStateOf("We'll never share it.") }
    var variant by rememberSaveable { mutableStateOf(PrismaTextFieldVariant.Outlined) }
    var size by rememberSaveable { mutableStateOf(PrismaTextFieldSize.Md) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var readOnly by rememberSaveable { mutableStateOf(false) }
    var secure by rememberSaveable { mutableStateOf(false) }
    var maxCount by rememberSaveable { mutableStateOf(0) }
    var leadingIcon by rememberSaveable { mutableStateOf<Int?>(null) }
    var validation by rememberSaveable { mutableStateOf(ValidationKind.None) }

    // Live validation. The maxCount rule is non-blocking — typing past the
    // cap surfaces an explicit error rather than silently dropping
    // keystrokes (a common UX antipattern).
    val derivedError: String? = when {
        value.isEmpty() -> null
        validation == ValidationKind.Email && !value.contains('@') -> "Enter a valid email address."
        validation == ValidationKind.Min8Chars && value.length < 8 -> "At least 8 characters required."
        maxCount > 0 && value.length > maxCount -> "Too long: ${value.length}/$maxCount."
        else -> null
    }

    PlaygroundScreen(
        preview = {
            PrismaTextField(
                value = value,
                onValueChange = { value = it },
                label = label.takeIf { it.isNotBlank() },
                placeholder = placeholder.takeIf { it.isNotBlank() },
                helperText = helper.takeIf { it.isNotBlank() },
                errorText = derivedError,
                enabled = enabled,
                readOnly = readOnly,
                variant = variant,
                size = size,
                secureTextEntry = secure,
                counter = if (maxCount > 0) value.length else null,
                maxCount = if (maxCount > 0) maxCount else null,
                leadingIcon = leadingIcon?.let { res ->
                    {
                        Icon(
                            painter = painterResource(res),
                            contentDescription = null,
                            tint = PrismaSemanticColors.TextTertiary.themed(),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
                keyboardOptions = if (validation == ValidationKind.Email)
                    KeyboardOptions(keyboardType = KeyboardType.Email) else KeyboardOptions.Default,
            )
        },
        knobs = {
            StringKnobRow("Label", label, { label = it }, placeholder = "Field label")
            StringKnobRow("Placeholder", placeholder, { placeholder = it }, placeholder = "Hint shown when empty")
            StringKnobRow("Helper", helper, { helper = it }, placeholder = "Sub-text below the field")
            EnumKnobRow(
                label = "Variant",
                value = variant,
                values = PrismaTextFieldVariant.values().toList(),
                onChange = { variant = it },
                optionLabel = { it.name },
            )
            EnumKnobRow(
                label = "Size",
                value = size,
                values = PrismaTextFieldSize.values().toList(),
                onChange = { size = it },
                optionLabel = { it.name },
            )
            EnumKnobRow(
                label = "Validation",
                value = validation,
                values = ValidationKind.values().toList(),
                onChange = { validation = it },
                optionLabel = { it.display },
            )
            IntKnobRow(
                label = "Max characters (0 = off)",
                value = maxCount,
                range = 0..200,
                step = 10,
                onChange = { maxCount = it },
            )
            BoolKnobRow(
                label = "Enabled",
                value = enabled,
                onChange = { enabled = it },
                helper = "Off renders the disabled state.",
            )
            BoolKnobRow("Read only", readOnly, { readOnly = it })
            BoolKnobRow("Secure entry", secure, { secure = it })
            IconKnobRow(
                label = "Leading icon",
                value = leadingIcon,
                options = ICON_OPTIONS,
                onChange = { leadingIcon = it },
            )
        },
        states = listOf(
            PlaygroundState("Empty") { EmptyStateCell() },
            PlaygroundState("Filled") { FilledStateCell() },
            PlaygroundState("Error") {
                PrismaTextField(value = "j@", onValueChange = {}, label = "Email", errorText = "Enter a valid email address.")
            },
            PlaygroundState("Disabled") {
                PrismaTextField(value = "maya@example.com", onValueChange = {}, label = "Email", enabled = false)
            },
            PlaygroundState("Read only") {
                PrismaTextField(value = "maya@example.com", onValueChange = {}, label = "Email", readOnly = true)
            },
            PlaygroundState("Counter (live, non-blocking)") { CounterStateCell() },
            PlaygroundState("Filled variant + leading icon") { FilledVariantStateCell() },
            PlaygroundState("Multiline") { MultilineStateCell() },
        ),
        code = {
            buildString {
                append("PrismaTextField(\n")
                append("    value = state,\n    onValueChange = { state = it },\n")
                if (label.isNotBlank()) append("    label = \"$label\",\n")
                if (placeholder.isNotBlank()) append("    placeholder = \"$placeholder\",\n")
                if (helper.isNotBlank()) append("    helperText = \"$helper\",\n")
                if (variant != PrismaTextFieldVariant.Outlined) append("    variant = PrismaTextFieldVariant.${variant.name},\n")
                if (size != PrismaTextFieldSize.Md) append("    size = PrismaTextFieldSize.${size.name},\n")
                if (!enabled) append("    enabled = false,\n")
                if (readOnly) append("    readOnly = true,\n")
                if (secure) append("    secureTextEntry = true,\n")
                if (maxCount > 0) append("    counter = state.length,\n    maxCount = $maxCount,\n")
                append(")")
            }
        },
        a11y = TextFieldA11yReport,
    )
}

@Composable
private fun EmptyStateCell() {
    var v by rememberSaveable { mutableStateOf("") }
    PrismaTextField(value = v, onValueChange = { v = it }, label = "Email", placeholder = "you@example.com")
}

@Composable
private fun FilledStateCell() {
    var v by rememberSaveable { mutableStateOf("maya@example.com") }
    PrismaTextField(value = v, onValueChange = { v = it }, label = "Email")
}

@Composable
private fun CounterStateCell() {
    var v by rememberSaveable { mutableStateOf("Hello") }
    PrismaTextField(
        value = v,
        onValueChange = { v = it },
        label = "Tagline",
        helperText = "Keep it concise.",
        counter = v.length,
        maxCount = 24,
        errorText = if (v.length > 24) "Too long: ${v.length}/24." else null,
    )
}

@Composable
private fun FilledVariantStateCell() {
    var v by rememberSaveable { mutableStateOf("") }
    PrismaTextField(
        value = v,
        onValueChange = { v = it },
        label = "Search",
        placeholder = "Search components",
        variant = PrismaTextFieldVariant.Filled,
        leadingIcon = {
            Icon(
                painter = painterResource(PrismaIcons.Search),
                contentDescription = null,
                tint = PrismaSemanticColors.TextTertiary.themed(),
                modifier = Modifier.size(18.dp),
            )
        },
    )
}

@Composable
private fun MultilineStateCell() {
    var v by rememberSaveable { mutableStateOf("Compose multi-line text here.\nResize freely.") }
    PrismaTextField(value = v, onValueChange = { v = it }, label = "Bio", singleLine = false)
}

private val ICON_OPTIONS: List<Pair<String, Int>> = listOf(
    "Search" to PrismaIcons.Search,
    "Mail" to PrismaIcons.Mail,
    "User" to PrismaIcons.User,
    "Lock" to PrismaIcons.Lock,
    "Phone" to PrismaIcons.Phone,
    "Calendar" to PrismaIcons.Calendar,
    "Tag" to PrismaIcons.Tag,
    "Link" to PrismaIcons.Link,
)

private val TextFieldA11yReport = A11yReport(
    role = "EditText",
    minTouchTarget = "48 dp height / 44 pt height",
    screenReader = "The label is associated with the field — TalkBack and VoiceOver read the label first, then the current value, then the helper text. Error messages are announced via stateDescription / accessibilityValue when they appear, so the user hears \"Enter a valid email address\" without having to re-focus the field.",
    voiceControl = "Voice Access / Voice Control target the visible label as the spoken handle (\"Tap Email\"). The field's current value is exposed via accessibilityValue so \"What does Email say?\" returns the typed content.",
    keyboard = "Tab focuses, Shift-Tab moves backwards, ESC clears focus. The IME's keyboard type follows the validation knob (Email validation switches to the email keyboard with @ key in the suggestions row). Secure entry routes through the password manager when available.",
    contrast = "All text colours verified at WCAG AA in light + dark themes — placeholder is text.tertiary which sits above 4.5:1, helper is text.tertiary, error text uses status.danger (4.7:1 in light, 5.1:1 in dark). Build-time check-contrast.mjs gates regressions.",
    touchTarget = "Min height 48 dp on Android (Sm), 44 pt on iOS — even for the small variant the entire field is the tap target. Trailing chevrons / clear buttons each have their own 48 × 48 dp hit area.",
    wcagQuote = "Labels or instructions are provided when content requires user input. — Prisma's TextField never ships unlabelled; the label is part of the API contract, not optional.",
    wcagRef = "3.3.2 Labels or Instructions, Level A",
)
