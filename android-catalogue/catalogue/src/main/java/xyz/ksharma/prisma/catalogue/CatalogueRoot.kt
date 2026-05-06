package xyz.ksharma.prisma.catalogue

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import xyz.ksharma.prisma.catalogue.shell.CatalogueShell
import xyz.ksharma.prisma.coreui.PrismaTheme
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaSemanticColors

/**
 * Exposes the in-app theme toggle to descendants (e.g. Sidebar's chrome
 * affordance) without prop-drilling through every layer.
 */
public val LocalThemeController = compositionLocalOf<ThemeController> {
    error("ThemeController not provided. Wrap your tree in CatalogueRoot or call CompositionLocalProvider.")
}

/** Pair of (isDark, toggle action). Stable so it can be remembered. */
public class ThemeController(
    public val isDark: Boolean,
    public val toggle: () -> Unit,
)

/**
 * App root. Owns the dark-mode toggle (preserved across config changes via
 * rememberSaveable). The catalogue shell itself is themed by [PrismaTheme].
 */
@Composable
public fun CatalogueRoot() {
    val systemDark = isSystemInDarkTheme()
    var isDark by rememberSaveable { mutableStateOf(systemDark) }

    val controller = remember(isDark) {
        ThemeController(isDark = isDark, toggle = { isDark = !isDark })
    }

    PrismaTheme(isDark = isDark) {
        CompositionLocalProvider(LocalThemeController provides controller) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrismaSemanticColors.SurfaceBase.themed()),
            ) {
                CatalogueShell()
            }
        }
    }
}
