package xyz.ksharma.prisma.components.drawer

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaSemanticColors

/** Wraps M3 ModalNavigationDrawer with Prisma-themed colours. */
@Composable
public fun PrismaDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val isDark = LocalPrismaIsDark.current
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = PrismaSemanticColors.SurfaceRaised.resolve(isDark),
                drawerContentColor = PrismaSemanticColors.TextPrimary.resolve(isDark),
            ) { drawerContent() }
        },
        drawerState = drawerState,
        modifier = modifier,
        gesturesEnabled = gesturesEnabled,
        content = content,
    )
}
