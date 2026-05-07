package xyz.ksharma.prisma.catalogue.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

/**
 * Per-component a11y dossier. Sections cover screen reader, voice control,
 * keyboard, contrast, touch target, with an optional WCAG 2.2 quote anchor.
 * Reusable across components — each showcase passes its own [A11yReport].
 */
public data class A11yReport(
    val role: String,
    val minTouchTarget: String,
    val screenReader: String,
    val voiceControl: String,
    val keyboard: String,
    val contrast: String,
    val touchTarget: String,
    val wcagQuote: String,
    val wcagRef: String,
)

@Composable
public fun A11ySheetContent(report: A11yReport) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PrismaSpacing.Sp5, vertical = PrismaSpacing.Sp5),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp6),
    ) {
        // Header
        Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp1)) {
            Text(
                text = "Accessibility",
                style = PrismaTypography.HeadlineSm,
                color = PrismaSemanticColors.TextPrimary.themed(),
            )
            Text(
                text = "How this component reads, talks, and responds across assistive tech.",
                style = PrismaTypography.BodyMd,
                color = PrismaSemanticColors.TextSecondary.themed(),
            )
        }

        // Quick facts
        Row(horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3)) {
            QuickFact(label = "Role", value = report.role)
            QuickFact(label = "Min target", value = report.minTouchTarget)
        }

        Section(
            iconRes = PrismaIcons.Message,
            title = "Screen reader",
            subtitle = "TalkBack · VoiceOver",
            body = report.screenReader,
        )
        Section(
            iconRes = PrismaIcons.Phone,
            title = "Voice control",
            subtitle = "Voice Access · Voice Control",
            body = report.voiceControl,
        )
        Section(
            iconRes = PrismaIcons.Grid,
            title = "Keyboard",
            subtitle = "External keyboard · Tab navigation",
            body = report.keyboard,
        )
        Section(
            iconRes = PrismaIcons.Eye,
            title = "Color contrast",
            subtitle = "WCAG AA · 4.5:1 body, 3:1 large text",
            body = report.contrast,
        )
        Section(
            iconRes = PrismaIcons.Scan,
            title = "Touch target",
            subtitle = "48 × 48 dp Android · 44 × 44 pt iOS",
            body = report.touchTarget,
        )

        // Platform note — same on every component so it lives at the
        // sheet level rather than the per-component A11yReport.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(PrismaRadius.Md))
                .background(PrismaSemanticColors.SurfaceSunken.themed())
                .padding(PrismaSpacing.Sp4),
            horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(PrismaIcons.Info),
                contentDescription = null,
                tint = PrismaSemanticColors.TextTertiary.themed(),
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "Text scales with the system font-size setting — Android \"Display size & text\" / iOS \"Larger Text\". Layouts hold up to 200%; no in-app override.",
                style = PrismaTypography.BodySm,
                color = PrismaSemanticColors.TextSecondary.themed(),
            )
        }

        // WCAG anchor quote
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(PrismaRadius.Md))
                .background(PrismaSemanticColors.AccentSubtle.themed())
                .padding(PrismaSpacing.Sp4),
            verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
            ) {
                Icon(
                    painter = painterResource(PrismaIcons.Doc),
                    contentDescription = null,
                    tint = PrismaSemanticColors.AccentDefault.themed(),
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = "WCAG 2.2 — ${report.wcagRef}",
                    style = PrismaTypography.LabelSm.copy(fontFamily = FontFamily.Monospace),
                    color = PrismaSemanticColors.AccentDefault.themed(),
                )
            }
            Text(
                text = "“${report.wcagQuote}”",
                style = PrismaTypography.BodyMd,
                color = PrismaSemanticColors.TextPrimary.themed(),
            )
        }
    }
}

@Composable
private fun QuickFact(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(PrismaRadius.Md))
            .background(PrismaSemanticColors.SurfaceSunken.themed())
            .padding(horizontal = PrismaSpacing.Sp4, vertical = PrismaSpacing.Sp3),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label.uppercase(),
            style = PrismaTypography.LabelSm,
            color = PrismaSemanticColors.TextTertiary.themed(),
        )
        Text(
            text = value,
            style = PrismaTypography.BodyMd,
            color = PrismaSemanticColors.TextPrimary.themed(),
        )
    }
}

@Composable
private fun Section(
    iconRes: Int,
    title: String,
    subtitle: String,
    body: String,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4)) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(PrismaRadius.Md))
                .background(PrismaSemanticColors.AccentSubtle.themed())
                .border(
                    1.dp,
                    PrismaSemanticColors.BorderSubtle.themed(),
                    RoundedCornerShape(PrismaRadius.Md),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = PrismaSemanticColors.AccentDefault.themed(),
                modifier = Modifier.size(18.dp),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp1)) {
            Text(
                text = title,
                style = PrismaTypography.TitleSm,
                color = PrismaSemanticColors.TextPrimary.themed(),
            )
            Text(
                text = subtitle,
                style = PrismaTypography.LabelSm,
                color = PrismaSemanticColors.TextTertiary.themed(),
            )
            Text(
                text = body,
                style = PrismaTypography.BodyMd,
                color = PrismaSemanticColors.TextSecondary.themed(),
                modifier = Modifier.padding(top = PrismaSpacing.Sp1),
            )
        }
    }
}
