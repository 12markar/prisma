package xyz.ksharma.prisma.catalogue.sdui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import xyz.ksharma.prisma.components.button.PrismaButton
import xyz.ksharma.prisma.components.button.PrismaButtonSize
import xyz.ksharma.prisma.components.button.PrismaButtonVariant
import xyz.ksharma.prisma.components.card.PrismaCard
import xyz.ksharma.prisma.components.card.PrismaCardVariant
import xyz.ksharma.prisma.components.listitem.PrismaListItem
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColor
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Recursive renderer that walks an [SduiNode] tree and emits Compose. The
 * `when` is exhaustive on the sealed hierarchy so adding a new node type to
 * [SduiNode] forces a compile-time update here.
 */
@Composable
public fun SduiRender(node: SduiNode, modifier: Modifier = Modifier) {
    when (node) {
        is SduiNode.Column   -> RenderColumn(node, modifier)
        is SduiNode.Row      -> RenderRow(node, modifier)
        is SduiNode.Text     -> RenderText(node, modifier)
        is SduiNode.Image    -> RenderImage(node, modifier)
        is SduiNode.Button   -> RenderButton(node, modifier)
        is SduiNode.Card     -> RenderCard(node, modifier)
        is SduiNode.Tabs     -> RenderTabs(node, modifier)
        is SduiNode.Divider  -> RenderDivider(modifier)
        is SduiNode.Spacer   -> RenderSpacer(node)
        is SduiNode.Badge    -> RenderBadge(node, modifier)
        is SduiNode.ListItem -> RenderListItem(node, modifier)
    }
}

// region — containers

@Composable
private fun RenderColumn(node: SduiNode.Column, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding((node.padding ?: 0f).dp),
        verticalArrangement = Arrangement.spacedBy((node.spacing ?: 0f).dp),
        horizontalAlignment = horizontalAlignFor(node.alignment),
    ) {
        node.children.forEach { SduiRender(it) }
    }
}

@Composable
private fun RenderRow(node: SduiNode.Row, modifier: Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangementFor(node.alignment, node.spacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        node.children.forEach { SduiRender(it) }
    }
}

@Composable
private fun RenderCard(node: SduiNode.Card, modifier: Modifier) {
    PrismaCard(
        modifier = modifier.fillMaxWidth(),
        variant = PrismaCardVariant.Elevated,
        contentPadding = (node.padding ?: 0f).dp,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy((node.spacing ?: 0f).dp),
        ) {
            node.children.forEach { SduiRender(it) }
        }
    }
}

// endregion

// region — leaves

@Composable
private fun RenderText(node: SduiNode.Text, modifier: Modifier) {
    Text(
        text = node.value,
        style = typographyFor(node.variant),
        color = textColorFor(node.color).themed(),
        textAlign = textAlignFor(node.align),
        modifier = if (node.align != null) modifier.fillMaxWidth() else modifier,
    )
}

@Composable
private fun RenderImage(node: SduiNode.Image, modifier: Modifier) {
    val shape = RoundedCornerShape((node.cornerRadius ?: 0f).dp)
    var imageMod: Modifier = modifier.clip(shape)
    if (node.width != null) imageMod = imageMod.width(node.width.dp) else imageMod = imageMod.fillMaxWidth()
    if (node.height != null) imageMod = imageMod.height(node.height.dp)
    AsyncImage(
        model = node.url,
        contentDescription = node.alt,
        contentScale = when (node.contentMode) {
            "fit" -> ContentScale.Fit
            "fill" -> ContentScale.FillBounds
            else -> ContentScale.Crop
        },
        modifier = imageMod,
    )
}

@Composable
private fun RenderButton(node: SduiNode.Button, modifier: Modifier) {
    val context = LocalContext.current
    val onClick: () -> Unit = remember(node.action) {
        {
            when (val a = node.action) {
                is SduiAction.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(a.url))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    runCatching { context.startActivity(intent) }
                }
                is SduiAction.Log     -> println("[sdui] action.log id=${a.id}")
                SduiAction.Noop, null -> Unit
            }
        }
    }
    val widthMod = if (node.fullWidth == true) modifier.fillMaxWidth() else modifier
    PrismaButton(
        text = node.label,
        onClick = onClick,
        modifier = widthMod,
        variant = buttonVariantFor(node.variant),
        size = buttonSizeFor(node.size),
        enabled = node.disabled != true,
    )
}

@Composable
private fun RenderDivider(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(PrismaSemanticColors.BorderSubtle.themed()),
    )
}

@Composable
private fun RenderSpacer(node: SduiNode.Spacer) {
    Spacer(modifier = Modifier.height((node.size ?: 8f).dp))
}

@Composable
private fun RenderBadge(node: SduiNode.Badge, modifier: Modifier) {
    val (bg, fg) = badgeTones(node.tone)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(PrismaRadius.Full))
            .background(bg.themed())
            .padding(horizontal = PrismaSpacing.Sp3, vertical = PrismaSpacing.Sp1),
    ) {
        Text(
            text = node.label,
            style = PrismaTypography.LabelSm,
            color = fg.themed(),
        )
    }
}

@Composable
private fun RenderListItem(node: SduiNode.ListItem, modifier: Modifier) {
    val context = LocalContext.current
    val onClick: (() -> Unit)? = node.action?.let {
        {
            when (val a = it) {
                is SduiAction.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(a.url))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    runCatching { context.startActivity(intent) }
                }
                is SduiAction.Log -> println("[sdui] listItem.log id=${a.id}")
                SduiAction.Noop -> Unit
            }
        }
    }
    PrismaListItem(
        primary = node.title,
        secondary = node.subtitle,
        onClick = onClick,
        modifier = modifier,
        leading = node.leadingIconUrl?.let { url ->
            {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(PrismaRadius.Sm)),
                )
            }
        },
        trailing = node.trailingText?.let { text ->
            {
                Text(
                    text = text,
                    style = PrismaTypography.LabelMd,
                    color = PrismaSemanticColors.TextSecondary.themed(),
                )
            }
        },
    )
}

// endregion

// region — tabs (only node with internal state)

@Composable
private fun RenderTabs(node: SduiNode.Tabs, modifier: Modifier) {
    // Reset selection only when the tab labels change — preserves the user's
    // selection through hot-reloads that only edit content within a tab.
    val labelsKey = remember(node) { node.items.joinToString("|") { it.label } }
    var selected by remember(labelsKey) {
        mutableIntStateOf(node.selectedIndex.coerceIn(0, node.items.lastIndex))
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(PrismaRadius.Md))
                .background(PrismaSemanticColors.SurfaceSunken.themed())
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            node.items.forEachIndexed { index, item ->
                val isSelected = index == selected
                val bg = if (isSelected) PrismaSemanticColors.SurfaceRaised else PrismaSemanticColors.SurfaceSunken
                val fg = if (isSelected) PrismaSemanticColors.TextPrimary else PrismaSemanticColors.TextSecondary
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(PrismaRadius.Sm))
                        .background(bg.themed())
                        .clickable { selected = index }
                        .padding(vertical = PrismaSpacing.Sp2, horizontal = PrismaSpacing.Sp2),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item.label,
                        style = PrismaTypography.LabelMd,
                        color = fg.themed(),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(PrismaSpacing.Sp4))
        node.items.getOrNull(selected)?.let { SduiRender(it.content) }
    }
}

// endregion

// region — variant lookups

@Composable
private fun typographyFor(name: String?): TextStyle = when (name) {
    "displayLg"  -> PrismaTypography.DisplayLg
    "displayMd"  -> PrismaTypography.DisplayMd
    "displaySm"  -> PrismaTypography.DisplaySm
    "headlineLg" -> PrismaTypography.HeadlineLg
    "headlineMd" -> PrismaTypography.HeadlineMd
    "headlineSm" -> PrismaTypography.HeadlineSm
    "titleLg"    -> PrismaTypography.TitleLg
    "titleMd"    -> PrismaTypography.TitleMd
    "titleSm"    -> PrismaTypography.TitleSm
    "bodyLg"     -> PrismaTypography.BodyLg
    "bodyMd"     -> PrismaTypography.BodyMd
    "bodySm"     -> PrismaTypography.BodySm
    "labelLg"    -> PrismaTypography.LabelLg
    "labelMd"    -> PrismaTypography.LabelMd
    "labelSm"    -> PrismaTypography.LabelSm
    else         -> PrismaTypography.BodyMd
}

private fun textColorFor(name: String?): PrismaSemanticColor = when (name) {
    "primary"   -> PrismaSemanticColors.TextPrimary
    "secondary" -> PrismaSemanticColors.TextSecondary
    "tertiary"  -> PrismaSemanticColors.TextTertiary
    "accent"    -> PrismaSemanticColors.AccentDefault
    "danger"    -> PrismaSemanticColors.StatusDangerDefault
    else        -> PrismaSemanticColors.TextPrimary
}

private fun textAlignFor(name: String?): TextAlign? = when (name) {
    "center" -> TextAlign.Center
    "end"    -> TextAlign.End
    "start"  -> TextAlign.Start
    else     -> null
}

private fun horizontalAlignFor(name: String?): Alignment.Horizontal = when (name) {
    "center" -> Alignment.CenterHorizontally
    "end"    -> Alignment.End
    else     -> Alignment.Start
}

private fun horizontalArrangementFor(name: String?, spacing: Float?): Arrangement.Horizontal {
    val gap = (spacing ?: 0f).dp
    return when (name) {
        "center" -> Arrangement.spacedBy(gap, Alignment.CenterHorizontally)
        "end"    -> Arrangement.spacedBy(gap, Alignment.End)
        else     -> Arrangement.spacedBy(gap, Alignment.Start)
    }
}

private fun buttonVariantFor(name: String?): PrismaButtonVariant = when (name) {
    "primary"     -> PrismaButtonVariant.Primary
    "secondary"   -> PrismaButtonVariant.Secondary
    "outlined"    -> PrismaButtonVariant.Outlined
    "ghost"       -> PrismaButtonVariant.Ghost
    "destructive" -> PrismaButtonVariant.Destructive
    else          -> PrismaButtonVariant.Primary
}

private fun buttonSizeFor(name: String?): PrismaButtonSize = when (name) {
    "sm" -> PrismaButtonSize.Sm
    "lg" -> PrismaButtonSize.Lg
    else -> PrismaButtonSize.Default
}

private fun badgeTones(tone: String?): Pair<PrismaSemanticColor, PrismaSemanticColor> = when (tone) {
    "accent"  -> PrismaSemanticColors.AccentDefault to PrismaSemanticColors.TextOnAccent
    "success" -> PrismaSemanticColors.StatusSuccessDefault to PrismaSemanticColors.StatusSuccessOnStatus
    "warning" -> PrismaSemanticColors.StatusWarningDefault to PrismaSemanticColors.StatusWarningOnStatus
    "danger"  -> PrismaSemanticColors.StatusDangerDefault to PrismaSemanticColors.StatusDangerOnStatus
    else      -> PrismaSemanticColors.SurfaceSunken to PrismaSemanticColors.TextSecondary
}

// endregion
