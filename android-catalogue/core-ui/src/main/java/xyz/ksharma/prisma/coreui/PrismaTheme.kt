package xyz.ksharma.prisma.coreui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import xyz.ksharma.prisma.tokens.PrismaSemanticColor
import xyz.ksharma.prisma.tokens.PrismaSemanticColors

/**
 * Tracks the active dark/light mode for the Prisma theme. Defaults to the system
 * preference; the [PrismaTheme] composable allows explicit override (e.g., user
 * toggle in the catalogue chrome).
 */
public val LocalPrismaIsDark = staticCompositionLocalOf { false }

/**
 * Resolves a [PrismaSemanticColor] against the active Prisma theme override.
 * Prefer this over [PrismaSemanticColor.resolve] (which always reads
 * `isSystemInDarkTheme`) so the catalogue's theme toggle works.
 */
@Composable
public fun PrismaSemanticColor.themed(): Color = resolve(LocalPrismaIsDark.current)

/**
 * The Prisma theme. Provides `LocalPrismaIsDark` and a thin Material3 substrate
 * (used only for ripple, focus indication, etc — component code reads Prisma
 * tokens directly, never `MaterialTheme.colorScheme`).
 */
@Composable
public fun PrismaTheme(
    isDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Build a Material3 ColorScheme from Prisma semantic tokens so M3 internals
    // (ripples, OutlinedTextField underlays, native CircularProgressIndicator,
    // SnackbarHost, etc.) match the in-app theme override — not the system.
    val m3Scheme = remember(isDark) {
        val accent = PrismaSemanticColors.AccentDefault.resolve(isDark)
        val onAccent = PrismaSemanticColors.TextOnAccent.resolve(isDark)
        val surface = PrismaSemanticColors.SurfaceBase.resolve(isDark)
        val onSurface = PrismaSemanticColors.TextPrimary.resolve(isDark)
        val raised = PrismaSemanticColors.SurfaceRaised.resolve(isDark)
        val sunken = PrismaSemanticColors.SurfaceSunken.resolve(isDark)
        val danger = PrismaSemanticColors.StatusDangerDefault.resolve(isDark)
        val onDanger = PrismaSemanticColors.StatusDangerOnStatus.resolve(isDark)
        val border = PrismaSemanticColors.BorderDefault.resolve(isDark)
        val borderSubtle = PrismaSemanticColors.BorderSubtle.resolve(isDark)
        val secondaryText = PrismaSemanticColors.TextSecondary.resolve(isDark)
        val base = if (isDark) darkColorScheme() else lightColorScheme()
        base.copy(
            primary = accent,
            onPrimary = onAccent,
            secondary = accent,
            onSecondary = onAccent,
            background = surface,
            onBackground = onSurface,
            surface = surface,
            onSurface = onSurface,
            surfaceVariant = sunken,
            onSurfaceVariant = secondaryText,
            surfaceContainer = raised,
            surfaceContainerHigh = raised,
            surfaceContainerHighest = raised,
            surfaceContainerLow = sunken,
            surfaceContainerLowest = surface,
            error = danger,
            onError = onDanger,
            outline = border,
            outlineVariant = borderSubtle,
        )
    }
    CompositionLocalProvider(LocalPrismaIsDark provides isDark) {
        MaterialTheme(colorScheme = m3Scheme, content = content)
    }
}
