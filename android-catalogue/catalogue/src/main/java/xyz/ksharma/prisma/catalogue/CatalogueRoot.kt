package xyz.ksharma.prisma.catalogue

import android.app.Activity
import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import xyz.ksharma.prisma.catalogue.shell.CatalogueShell
import xyz.ksharma.prisma.catalogue.shell.OnboardingOverlay
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
    /** True when the user explicitly overrode system; false → following system. */
    public val isUserOverride: Boolean,
    public val toggle: () -> Unit,
    public val followSystem: () -> Unit,
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
    // Read system on every composition so the app follows system theme dynamically
    // when there is no user override.
    val systemDark = isSystemInDarkTheme()
    // null = follow system; non-null = user-forced light (false) or dark (true).
    var userOverride by rememberSaveable { mutableStateOf<Boolean?>(null) }

    // Onboarding dismissal is persisted in SharedPreferences so it actually
    // survives process death — `rememberSaveable` only restores when the
    // OS hands us a saved Bundle, not on a fresh cold start. Without this
    // the welcome screen reappeared on every launch.
    val context = LocalContext.current
    val prefs = remember(context) { context.getSharedPreferences("prisma_prefs", Context.MODE_PRIVATE) }
    var showOnboarding by remember { mutableStateOf(!prefs.getBoolean("onboardingDone", false)) }

    // Inspector + a11y overlay implementations remain available for future
    // reintroduction (e.g. behind a long-press chrome action), but they're
    // not surfaced today — kept the daily chrome to a single theme toggle
    // per UX feedback that the multi-icon chrome was engineer-noise.
    val isDark = userOverride ?: systemDark
    val isUserOverride = userOverride != null

    val controller = remember(isDark, isUserOverride) {
        ThemeController(
            isDark = isDark,
            isUserOverride = isUserOverride,
            toggle = { userOverride = !isDark },
            followSystem = { userOverride = null },
        )
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
            // Smoothly cross-fade the root surface colour during theme swaps so
            // the change feels deliberate instead of an abrupt snap. Leaf-level
            // colours still resolve discretely; the eye reads the root fade as
            // the whole surface easing.
            val animatedSurface by animateColorAsState(
                targetValue = PrismaSemanticColors.SurfaceBase.themed(),
                animationSpec = tween(durationMillis = 300),
                label = "rootSurface",
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(animatedSurface),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars),
                ) {
                    CatalogueShell()
                }
                OnboardingOverlay(
                    visible = showOnboarding,
                    onDismiss = {
                        // Write to disk first, THEN flip local state. Crashing
                        // mid-edit() would otherwise leave the overlay closed
                        // for the rest of the session but reopening on next
                        // launch — surprising and worth avoiding.
                        prefs.edit().putBoolean("onboardingDone", true).apply()
                        showOnboarding = false
                    },
                )
            }
        }
    }
}
