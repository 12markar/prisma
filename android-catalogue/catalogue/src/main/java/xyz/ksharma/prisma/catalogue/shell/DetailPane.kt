package xyz.ksharma.prisma.catalogue.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.catalogue.registry.CatalogueEntry
import xyz.ksharma.prisma.catalogue.registry.CatalogueRegistry
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

@Composable
public fun DetailPane(
    entry: CatalogueEntry?,
    scrollOffsets: androidx.compose.runtime.snapshots.SnapshotStateMap<String, Int>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrismaSemanticColors.SurfaceBase.themed()),
    ) {
        if (entry == null) EmptyDetail() else EntryDetail(entry = entry, scrollOffsets = scrollOffsets)
    }
}

@Composable
private fun EmptyDetail() {
    val total = CatalogueRegistry.entries.size
    val implemented = listOf(
        "foundation.typography", "foundation.colors", "foundation.spacing",
        "foundation.elevation",  "foundation.motion", "foundation.radius",
        "input.button",
    ).size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PrismaSpacing.Sp7),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Subtle decorative ring — gives the empty state a centerpoint and brand presence.
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(PrismaSemanticColors.AccentSubtle.themed()),
        )
        Text(
            text = "Prisma",
            style = PrismaTypography.DisplaySm,
            color = PrismaSemanticColors.TextPrimary.themed(),
            modifier = Modifier.padding(top = PrismaSpacing.Sp5),
        )
        Text(
            text = "Pick a component from the sidebar to see its showcase.",
            style = PrismaTypography.BodyLg,
            color = PrismaSemanticColors.TextSecondary.themed(),
            modifier = Modifier.padding(top = PrismaSpacing.Sp2),
        )

        // Stats strip — gives the empty state functional value.
        Row(
            modifier = Modifier.padding(top = PrismaSpacing.Sp7),
            horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp7),
        ) {
            Stat(label = "ENTRIES", value = total.toString())
            Stat(label = "IMPLEMENTED", value = implemented.toString())
            Stat(label = "PHASE", value = "1")
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = PrismaTypography.HeadlineMd.copy(fontFamily = FontFamily.Monospace),
            color = PrismaSemanticColors.AccentDefault.themed(),
        )
        Text(
            text = label,
            style = PrismaTypography.LabelSm,
            color = PrismaSemanticColors.TextTertiary.themed(),
            modifier = Modifier.padding(top = PrismaSpacing.Sp1),
        )
    }
}

@Composable
private fun EntryDetail(
    entry: CatalogueEntry,
    scrollOffsets: androidx.compose.runtime.snapshots.SnapshotStateMap<String, Int>,
) {
    // Scroll state per-entry, owned by CatalogueShell so it survives the
    // AnimatedPane destruction on back-nav. Reads the stored pixel offset
    // for this entry on first composition, syncs back on every change.
    val scrollState = androidx.compose.runtime.remember(entry.key) {
        androidx.compose.foundation.ScrollState(scrollOffsets[entry.key] ?: 0)
    }
    androidx.compose.runtime.LaunchedEffect(entry.key, scrollState.value) {
        scrollOffsets[entry.key] = scrollState.value
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = PrismaSpacing.Sp8, vertical = PrismaSpacing.Sp7),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
    ) {
        // Section breadcrumb pill.
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(PrismaRadius.Full))
                .background(PrismaSemanticColors.SurfaceSunken.themed())
                .padding(horizontal = PrismaSpacing.Sp3, vertical = PrismaSpacing.Sp1),
        ) {
            Text(
                text = entry.section.title,
                style = PrismaTypography.LabelSm,
                color = PrismaSemanticColors.TextSecondary.themed(),
            )
        }

        Text(
            text = entry.title,
            style = PrismaTypography.DisplaySm,
            color = PrismaSemanticColors.TextPrimary.themed(),
        )

        // Subtitle / status line.
        if (entry.content == null) {
            Text(
                text = "Spec ready · implementation pending in upcoming phase.",
                style = PrismaTypography.BodyMd,
                color = PrismaSemanticColors.TextTertiary.themed(),
            )
        }

        // Subtle separator before content.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(vertical = PrismaSpacing.Sp1)
                .background(PrismaSemanticColors.BorderSubtle.themed()),
        )

        val showcase = entry.content
        if (showcase != null) {
            Box(modifier = Modifier.padding(top = PrismaSpacing.Sp4)) {
                showcase()
            }
        } else {
            PlaceholderState(entry = entry)
        }
    }
}

@Composable
private fun PlaceholderState(entry: CatalogueEntry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PrismaRadius.Lg))
            .background(PrismaSemanticColors.SurfaceSunken.themed())
            .padding(PrismaSpacing.Sp7),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
    ) {
        Text(
            text = "Implementation queued",
            style = PrismaTypography.TitleMd,
            color = PrismaSemanticColors.TextPrimary.themed(),
        )
        Text(
            text = "The spec for ${entry.title.lowercase()} is complete. Component code lands in the upcoming phase per docs/TODO.md.",
            style = PrismaTypography.BodyMd,
            color = PrismaSemanticColors.TextSecondary.themed(),
        )
        if (entry.tags.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2)) {
                entry.tags.take(4).forEach { tag ->
                    TagChip(tag = tag)
                }
            }
        }
    }
}

@Composable
private fun TagChip(tag: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(PrismaRadius.Full))
            .background(PrismaSemanticColors.SurfaceRaised.themed())
            .padding(horizontal = PrismaSpacing.Sp3, vertical = PrismaSpacing.Sp1),
    ) {
        Text(
            text = tag,
            style = PrismaTypography.LabelSm,
            color = PrismaSemanticColors.TextSecondary.themed(),
        )
    }
}
