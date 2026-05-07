package xyz.ksharma.prisma.components.bottomsheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaSemanticColors
import xyz.ksharma.prisma.tokens.PrismaSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun PrismaBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    content: @Composable () -> Unit,
) {
    val isDark = LocalPrismaIsDark.current
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = PrismaSemanticColors.SurfaceRaised.resolve(isDark),
        contentColor = PrismaSemanticColors.TextPrimary.resolve(isDark),
        scrimColor = PrismaSemanticColors.SurfaceOverlay.resolve(isDark),
    ) {
        Box(modifier = Modifier.padding(PrismaSpacing.Sp7)) { content() }
    }
}
