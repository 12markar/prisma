package xyz.ksharma.prisma.catalogue

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
 * App root. Owns the dark-mode toggle (preserved across config changes via
 * rememberSaveable). The catalogue shell itself is themed by [PrismaTheme].
 */
@Composable
public fun CatalogueRoot() {
    val systemDark = isSystemInDarkTheme()
    var isDark by rememberSaveable { mutableStateOf(systemDark) }

    PrismaTheme(isDark = isDark) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PrismaSemanticColors.SurfaceBase.themed()),
        ) {
            CatalogueShell()
            // TODO Phase 1: floating theme toggle pill in chrome (top-right) with
            //   smooth crossfade animation across motion.duration.default.
        }
    }
}
