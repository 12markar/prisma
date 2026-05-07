package xyz.ksharma.prisma.catalogue.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.components.switchctl.PrismaSwitch
import xyz.ksharma.prisma.components.textfield.PrismaTextField
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

@Composable
internal fun KnobLabel(text: String, hint: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = text,
            style = PrismaTypography.LabelMd,
            color = PrismaSemanticColors.TextSecondary.themed(),
        )
        if (hint != null) {
            Text(
                text = hint,
                style = PrismaTypography.BodySm,
                color = PrismaSemanticColors.TextTertiary.themed(),
            )
        }
    }
}

/** A single editable text knob — bound to a [PrismaTextField]. */
@Composable
public fun StringKnobRow(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    helper: String? = null,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2)) {
        KnobLabel(label, helper)
        PrismaTextField(
            value = value,
            onValueChange = onChange,
            placeholder = placeholder.takeIf { it.isNotEmpty() },
        )
    }
}

/** Boolean knob — label on left, [PrismaSwitch] on right. */
@Composable
public fun BoolKnobRow(
    label: String,
    value: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    helper: String? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
    ) {
        Box(modifier = Modifier.weight(1f)) { KnobLabel(label, helper) }
        PrismaSwitch(checked = value, onCheckedChange = onChange)
    }
}

/** Numeric knob backed by a slider. */
@Composable
public fun IntKnobRow(
    label: String,
    value: Int,
    range: IntRange,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    step: Int = 1,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KnobLabel(label)
            Text(
                text = value.toString(),
                style = PrismaTypography.LabelMd,
                color = PrismaSemanticColors.TextPrimary.themed(),
            )
        }
        val steps = if (step > 0) ((range.last - range.first) / step - 1).coerceAtLeast(0) else 0
        Slider(
            value = value.toFloat(),
            onValueChange = { onChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = steps,
        )
    }
}

/**
 * Enum knob rendered as a segmented chip row. For long enum lists this still
 * scrolls horizontally via [FlowRow] wrapping to multi-line if needed.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun <T> EnumKnobRow(
    label: String,
    value: T,
    values: List<T>,
    onChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    optionLabel: (T) -> String = { it.toString() },
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2)) {
        KnobLabel(label)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
            verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
        ) {
            values.forEach { v ->
                EnumChip(
                    label = optionLabel(v),
                    selected = v == value,
                    onClick = { onChange(v) },
                )
            }
        }
    }
}

@Composable
private fun EnumChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) PrismaSemanticColors.AccentDefault.themed()
             else PrismaSemanticColors.SurfaceRaised.themed()
    val fg = if (selected) PrismaSemanticColors.TextOnAccent.themed()
             else PrismaSemanticColors.TextPrimary.themed()
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(PrismaRadius.Full))
            .background(bg)
            .let {
                if (!selected) it.border(
                    1.dp,
                    PrismaSemanticColors.BorderSubtle.themed(),
                    RoundedCornerShape(PrismaRadius.Full),
                ) else it
            }
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = PrismaSpacing.Sp3, vertical = PrismaSpacing.Sp2),
    ) {
        Text(text = label, style = PrismaTypography.LabelSm, color = fg)
    }
}

/**
 * Icon picker knob — renders selectable icon tiles. The first tile is "None"
 * (returns `null`) when [nullable] is true.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun IconKnobRow(
    label: String,
    value: Int?,
    options: List<Pair<String, Int>>,
    onChange: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    nullable: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2)) {
        KnobLabel(label)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
            verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
        ) {
            if (nullable) {
                IconTile(
                    selected = value == null,
                    onClick = { onChange(null) },
                    iconRes = null,
                    fallback = "—",
                )
            }
            options.forEach { (name, res) ->
                IconTile(
                    selected = value == res,
                    onClick = { onChange(res) },
                    iconRes = res,
                    fallback = name,
                )
            }
        }
    }
}

@Composable
private fun IconTile(
    selected: Boolean,
    onClick: () -> Unit,
    iconRes: Int?,
    fallback: String,
) {
    val bg = if (selected) PrismaSemanticColors.AccentSubtle.themed()
             else PrismaSemanticColors.SurfaceRaised.themed()
    val borderColor = if (selected) PrismaSemanticColors.AccentDefault.themed()
                     else PrismaSemanticColors.BorderSubtle.themed()
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(bg)
            .border(
                if (selected) 2.dp else 1.dp,
                borderColor,
                RoundedCornerShape(PrismaRadius.Md),
            )
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (iconRes != null) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = fallback,
                tint = PrismaSemanticColors.TextPrimary.themed(),
                modifier = Modifier.size(18.dp),
            )
        } else {
            Text(
                text = fallback,
                style = PrismaTypography.LabelMd,
                color = PrismaSemanticColors.TextTertiary.themed(),
            )
        }
    }
}
