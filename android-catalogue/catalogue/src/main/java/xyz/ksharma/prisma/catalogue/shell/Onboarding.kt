package xyz.ksharma.prisma.catalogue.shell

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import xyz.ksharma.prisma.components.button.PrismaButton
import xyz.ksharma.prisma.components.button.PrismaButtonVariant
import xyz.ksharma.prisma.components.icons.PrismaIcons
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaRadius
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing
import xyz.ksharma.prisma.tokens.PrismaTypography

private data class OnboardingPage(
    val eyebrow: String,
    val title: String,
    val titleAccent: String,
    val body: String,
    val iconRes: Int,
    val tags: List<String>,
)

private val pages = listOf(
    OnboardingPage(
        eyebrow = "01 — Welcome",
        title = "Catalogues, ",
        titleAccent = "built quietly.",
        body = "Prisma is a cross-platform design system for native catalogue apps on iOS and Android. Tap any component on the left to open its playground.",
        iconRes = PrismaIcons.Layers,
        tags = listOf("SwiftUI", "Jetpack Compose"),
    ),
    OnboardingPage(
        eyebrow = "02 — Tokens",
        title = "Two layers. ",
        titleAccent = "One source of truth.",
        body = "Eleven-stop primitive ramps, semantic aliases for components. Authored as W3C DTCG JSON, compiled to Tokens.kt and Tokens.swift through Style Dictionary — never copy-pasted.",
        iconRes = PrismaIcons.Grid,
        tags = listOf("W3C DTCG", "Style Dictionary", "Light + Dark"),
    ),
    OnboardingPage(
        eyebrow = "03 — Adaptive",
        title = "List on phone, ",
        titleAccent = "two-pane on tablet.",
        body = "Adaptive layout via Material3 list-detail (Android) and NavigationSplitView (iOS). State preserved across pane swaps; theme follows system or your override.",
        iconRes = PrismaIcons.List,
        tags = listOf("List-detail", "rememberSaveable"),
    ),
    OnboardingPage(
        eyebrow = "04 — Accessible",
        title = "Designed precisely. ",
        titleAccent = "Every state announced.",
        body = "WCAG AA contrast verified at build time. Live regions, headings, progress and group semantics on every interactive component. Keyboard, voice, and screen-reader compatible by default.",
        iconRes = PrismaIcons.Eye,
        tags = listOf("WCAG AA", "TalkBack", "VoiceOver"),
    ),
)

/**
 * First-launch onboarding — horizontal pager with editorial brand copy
 * lifted from the marketing site. Replaces the earlier dialog-style
 * welcome card per UX feedback that it felt like an afterthought.
 *
 * Pages: Welcome / Tokens / Adaptive / Accessible. Skip on every page,
 * "Get started" on the final page. Indicator dots reflect the current
 * page and accept taps for direct navigation.
 */
@Composable
public fun OnboardingOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PrismaSemanticColors.SurfaceBase.themed()),
        ) {
            OnboardingPagerScreen(onDismiss = onDismiss)
        }
    }
}

@Composable
private fun OnboardingPagerScreen(onDismiss: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PrismaSpacing.Sp5, vertical = PrismaSpacing.Sp4),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF9173FF),
                                    Color(0xFF7651F5),
                                    Color(0xFFE03088),
                                ),
                            ),
                        ),
                )
                Text(
                    text = "Prisma",
                    style = PrismaTypography.LabelLg.copy(letterSpacing = (-0.4).sp),
                    color = PrismaSemanticColors.TextPrimary.themed(),
                )
            }
            Text(
                text = "Skip",
                style = PrismaTypography.LabelMd,
                color = PrismaSemanticColors.TextSecondary.themed(),
                modifier = Modifier
                    .clip(RoundedCornerShape(PrismaRadius.Full))
                    .clickable(role = Role.Button, onClick = onDismiss)
                    .padding(horizontal = PrismaSpacing.Sp3, vertical = PrismaSpacing.Sp2),
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            OnboardingPageContent(page = pages[page])
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = PrismaSpacing.Sp5, vertical = PrismaSpacing.Sp4),
            verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp4),
        ) {
            PageIndicator(
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
                onSelect = { idx ->
                    scope.launch { pagerState.animateScrollToPage(idx) }
                },
            )
            PrismaButton(
                text = if (isLastPage) "Get started" else "Next",
                onClick = {
                    if (isLastPage) {
                        onDismiss()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = PrismaSpacing.Sp7, vertical = PrismaSpacing.Sp6),
        verticalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp5),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(PrismaRadius.Lg))
                .background(PrismaSemanticColors.AccentSubtle.themed()),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(page.iconRes),
                contentDescription = null,
                tint = PrismaSemanticColors.AccentDefault.themed(),
                modifier = Modifier.size(32.dp),
            )
        }
        Text(
            text = page.eyebrow,
            style = PrismaTypography.LabelSm.copy(fontFamily = FontFamily.Monospace),
            color = PrismaSemanticColors.TextTertiary.themed(),
        )
        Text(
            text = buildAnnotatedTitle(page.title, page.titleAccent),
            style = PrismaTypography.DisplaySm.copy(letterSpacing = (-0.5).sp),
            color = PrismaSemanticColors.TextPrimary.themed(),
        )
        Text(
            text = page.body,
            style = PrismaTypography.BodyLg,
            color = PrismaSemanticColors.TextSecondary.themed(),
        )
        if (page.tags.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(PrismaSpacing.Sp2),
                contentPadding = PaddingValues(end = PrismaSpacing.Sp7),
            ) {
                items(page.tags) { tag ->
                    TagPill(text = tag)
                }
            }
        }
    }
}

@Composable
private fun buildAnnotatedTitle(prefix: String, accent: String): androidx.compose.ui.text.AnnotatedString {
    val accentColor = PrismaSemanticColors.AccentDefault.themed()
    return androidx.compose.ui.text.buildAnnotatedString {
        append(prefix)
        pushStyle(androidx.compose.ui.text.SpanStyle(color = accentColor))
        append(accent)
        pop()
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.items(
    list: List<String>,
    item: @Composable (String) -> Unit,
) {
    items(list.size) { idx -> item(list[idx]) }
}

@Composable
private fun TagPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(PrismaRadius.Full))
            .background(PrismaSemanticColors.SurfaceRaised.themed())
            .border(
                1.dp,
                PrismaSemanticColors.BorderSubtle.themed(),
                RoundedCornerShape(PrismaRadius.Full),
            )
            .padding(horizontal = PrismaSpacing.Sp3, vertical = PrismaSpacing.Sp2),
    ) {
        Text(
            text = text,
            style = PrismaTypography.LabelSm.copy(fontFamily = FontFamily.Monospace),
            color = PrismaSemanticColors.TextSecondary.themed(),
        )
    }
}

@Composable
private fun PageIndicator(pageCount: Int, currentPage: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(pageCount) { idx ->
            val active = idx == currentPage
            val width = if (active) 24.dp else 8.dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(width = width, height = 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (active) PrismaSemanticColors.AccentDefault.themed()
                        else PrismaSemanticColors.BorderDefault.themed(),
                    )
                    .clickable(role = Role.Button, onClick = { onSelect(idx) }),
            )
        }
    }
}
