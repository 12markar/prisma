package xyz.ksharma.prisma.catalogue.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import xyz.ksharma.prisma.catalogue.registry.CatalogueEntry
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

@Composable
public fun DetailPane(
    entry: CatalogueEntry?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrismaSemanticColors.SurfaceBase.themed())
            .padding(PrismaSpacing.Sp7),
    ) {
        if (entry == null) {
            EmptyDetail()
        } else {
            EntryDetail(entry = entry)
        }
    }
}

@Composable
private fun EmptyDetail() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Select a component to see its showcase.",
            style = PrismaTypography.BodyLg,
            color = PrismaSemanticColors.TextTertiary.themed(),
        )
    }
}

@Composable
private fun EntryDetail(entry: CatalogueEntry) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
    ) {
        Text(
            text = entry.section.title,
            style = PrismaTypography.LabelSm,
            color = PrismaSemanticColors.TextTertiary.themed(),
        )
        Text(
            text = entry.title,
            style = PrismaTypography.HeadlineLg,
            color = PrismaSemanticColors.TextPrimary.themed(),
        )
        // Phase 0 placeholder. Phase 1+ replaces this with real foundation showcases
        // and component detail layouts (live demo, variants, states, tokens used,
        // a11y notes, code snippet, interactive playground).
        Text(
            text = "Phase 0 placeholder — implementation lands per-phase per docs/TODO.md.",
            style = PrismaTypography.BodyMd,
            color = PrismaSemanticColors.TextSecondary.themed(),
        )
        entry.content()
    }
}
