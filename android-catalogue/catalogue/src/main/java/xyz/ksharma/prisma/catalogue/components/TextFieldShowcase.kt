package xyz.ksharma.prisma.catalogue.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.catalogue.playground.A11yPanel
import xyz.ksharma.prisma.catalogue.playground.BoolKnobRow
import xyz.ksharma.prisma.catalogue.playground.EnumKnobRow
import xyz.ksharma.prisma.catalogue.playground.IconKnobRow
import xyz.ksharma.prisma.catalogue.playground.IntKnobRow
import xyz.ksharma.prisma.catalogue.playground.PlaygroundScaffold
import xyz.ksharma.prisma.catalogue.playground.StateCell
import xyz.ksharma.prisma.catalogue.playground.StringKnobRow
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.components.textfield.PrismaTextField
import xyz.ksharma.prisma.components.textfield.PrismaTextFieldSize
import xyz.ksharma.prisma.components.textfield.PrismaTextFieldVariant
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

private enum class ValidationKind(public val display: String) {
    None("None"),
    Email("Email"),
    Min8Chars("Min 8 chars"),
}

/**
 * TextField playground.
 *
 * - Knobs cover label, placeholder, helper, variant, size, disabled, readOnly,
 *   secure, leadingIcon, plus a *live* validation pipeline (None / Email /
 *   Min8) that paints `errorText` based on what the user actually types.
 * - Critical UX fix vs. the old showcase: `maxCount` no longer gates input.
 *   Users can keep typing past max — the counter goes red and an error
 *   message appears. (Hard-blocking input was confusing.)
 */
@Composable
public fun TextFieldShowcase() {
    var value by rememberSaveable { mutableStateOf("") }
    var label by rememberSaveable { mutableStateOf("Email") }
    var placeholder by rememberSaveable { mutableStateOf("you@example.com") }
    var helper by rememberSaveable { mutableStateOf("We'll never share it.") }
    var variant by rememberSaveable { mutableStateOf(PrismaTextFieldVariant.Outlined) }
    var size by rememberSaveable { mutableStateOf(PrismaTextFieldSize.Md) }
    var disabled by rememberSaveable { mutableStateOf(false) }
    var readOnly by rememberSaveable { mutableStateOf(false) }
    var secure by rememberSaveable { mutableStateOf(false) }
    var maxCount by rememberSaveable { mutableStateOf(0) } // 0 means no counter
    var leadingIcon by rememberSaveable { mutableStateOf<Int?>(null) }
    var validation by rememberSaveable { mutableStateOf(ValidationKind.None) }

    val derivedError: String? = when {
        value.isEmpty() -> null
        validation == ValidationKind.Email && !value.contains('@') -> "Enter a valid email address."
        validation == ValidationKind.Min8Chars && value.length < 8 -> "At least 8 characters required."
        maxCount > 0 && value.length > maxCount -> "Too long: ${value.length}/$maxCount."
        else -> null
    }

    PlaygroundScaffold(
        preview = {
            PrismaTextField(
                value = value,
                onValueChange = { value = it },
                label = label.takeIf { it.isNotBlank() },
                placeholder = placeholder.takeIf { it.isNotBlank() },
                helperText = helper.takeIf { it.isNotBlank() },
                errorText = derivedError,
                enabled = !disabled,
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
                singleLine = !secure || true,
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
            BoolKnobRow("Disabled", disabled, { disabled = it })
            BoolKnobRow("Read only", readOnly, { readOnly = it })
            BoolKnobRow("Secure entry", secure, { secure = it })
            IconKnobRow(
                label = "Leading icon",
                value = leadingIcon,
                options = ICON_OPTIONS,
                onChange = { leadingIcon = it },
            )
        },
        code = {
            buildString {
                append("PrismaTextField(\n")
                append("    value = state,\n    onValueChange = { state = it },\n")
                if (label.isNotBlank()) append("    label = \"${label}\",\n")
                if (placeholder.isNotBlank()) append("    placeholder = \"${placeholder}\",\n")
                if (helper.isNotBlank()) append("    helperText = \"${helper}\",\n")
                if (variant != PrismaTextFieldVariant.Outlined) append("    variant = PrismaTextFieldVariant.${variant.name},\n")
                if (size != PrismaTextFieldSize.Md) append("    size = PrismaTextFieldSize.${size.name},\n")
                if (disabled) append("    enabled = false,\n")
                if (readOnly) append("    readOnly = true,\n")
                if (secure) append("    secureTextEntry = true,\n")
                if (maxCount > 0) append("    counter = state.length,\n    maxCount = $maxCount,\n")
                append(")")
            }
        },
        states = {
            StateCell("Empty") {
                PrismaTextField(value = "", onValueChange = {}, label = "Email", placeholder = "you@example.com")
            }
            StateCell("Filled") {
                PrismaTextField(value = "maya@example.com", onValueChange = {}, label = "Email")
            }
            StateCell("Error") {
                PrismaTextField(value = "j@", onValueChange = {}, label = "Email", errorText = "Enter a valid email address.")
            }
            StateCell("Disabled") {
                PrismaTextField(value = "maya@example.com", onValueChange = {}, label = "Email", enabled = false)
            }
            StateCell("Read only") {
                PrismaTextField(value = "maya@example.com", onValueChange = {}, label = "Email", readOnly = true)
            }
            StateCell("Filled variant") {
                PrismaTextField(
                    value = "",
                    onValueChange = {},
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
            StateCell("Multiline") {
                MultilineExample()
            }
            StateCell("Counter") {
                CounterExample()
            }
        },
        a11y = {
            A11yPanel(
                role = "EditText",
                minTouchTarget = "48 dp height",
                bullets = listOf(
                    "Label is associated with the field — TalkBack reads the label, then the value, then helper.",
                    "Error: errorText is announced via stateDescription; border + helper colour shift to danger.",
                    "Counter is a hint, not a constraint — typing past maxCount shows the error but never blocks input.",
                    "Secure entry: characters announced as \"dot\"; auto-fill / password manager respected.",
                    "ReadOnly conveys \"read-only\" while keeping focus and copyable text.",
                ),
            )
        },
    )
}

@Composable
private fun MultilineExample() {
    var v by rememberSaveable { mutableStateOf("Compose multi-line text here.\nResize freely.") }
    PrismaTextField(value = v, onValueChange = { v = it }, label = "Bio", singleLine = false)
}

@Composable
private fun CounterExample() {
    // Note: input is NOT gated by maxCount — over-typing shows the counter
    // turn red and surfaces an explicit error, matching the playground rule.
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
