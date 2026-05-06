package xyz.ksharma.prisma.catalogue

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import xyz.ksharma.prisma.catalogue.shell.CatalogueShell
import xyz.ksharma.prisma.coreui.PrismaTheme
import xyz.ksharma.prisma.coreui.themed
import xyz.ksharma.prisma.tokens.PrismaSemanticColors

/**
 * Exposes the in-app theme toggle to descendants (e.g. Sidebar's chrome
 * affordance) without prop-drilling through every layer.
 */
public val LocalThemeController = compositionLocalOf<ThemeController> {
    error("ThemeController not provided. Wrap your tree in CatalogueRoot.")
}

public class ThemeController(
    public val isDark: Boolean,
    public val toggle: () -> Unit,
)

/**
 * App root.
 *
 * - Owns the dark-mode override (rememberSaveable; defaults to system on first launch).
 * - Drives status-bar / navigation-bar icon appearance via WindowInsetsControllerCompat
 *   so icons stay legible against whatever Prisma surface they sit over.
 * - Paints the entire window with `surface.base.themed()` — including the
 *   safe-area regions — so content edges do not bleed to the cold-start window
 *   colour during animations or insets recompute.
 */
@Composable
public fun CatalogueRoot() {
    val systemDark = isSystemInDarkTheme()
    var isDark by rememberSaveable { mutableStateOf(systemDark) }

    val controller = remember(isDark) {
        ThemeController(isDark = isDark, toggle = { isDark = !isDark })
    }

    // System-bar appearance follows the active Prisma theme (light bg → dark icons,
    // dark bg → light icons). SideEffect keeps the system in sync as `isDark` flips.
    val view = LocalView.current
    if (!view.isInEditMode) {
        val activity = view.context as? Activity
        SideEffect {
            val window = activity?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !isDark
            insetsController.isAppearanceLightNavigationBars = !isDark
        }
    }

    PrismaTheme(isDark = isDark) {
        CompositionLocalProvider(LocalThemeController provides controller) {
            // Outer Box: paints the full window (incl. safe-area regions) with
            // surface.base. Inner Box applies system-bar padding so the actual
            // shell content avoids the status / navigation bar — but the bar
            // regions still render the themed background instead of the legacy
            // window colour.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrismaSemanticColors.SurfaceBase.themed()),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars),
                ) {
                    CatalogueShell()
                }
            }
        }
    }
}
