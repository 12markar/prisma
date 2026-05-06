package xyz.ksharma.prisma.coreui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import xyz.ksharma.prisma.tokens.PrismaSemanticColor

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
    CompositionLocalProvider(LocalPrismaIsDark provides isDark) {
        MaterialTheme(content = content)
    }
}
