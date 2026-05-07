package xyz.ksharma.prisma.components.loading

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaSemanticColors

public enum class PrismaLoadingSize(public val diameter: Dp, public val stroke: Dp) {
    Sm(16.dp, 2.dp),
    Md(24.dp, 2.5.dp),
    Lg(40.dp, 3.dp),
}

@Composable
public fun PrismaCircularLoading(
    modifier: Modifier = Modifier,
    size: PrismaLoadingSize = PrismaLoadingSize.Md,
    label: String = "Loading",
) {
    val isDark = LocalPrismaIsDark.current
    CircularProgressIndicator(
        modifier = modifier
            .size(size.diameter)
            .progressSemantics()
            .semantics { contentDescription = label },
        color = PrismaSemanticColors.AccentDefault.resolve(isDark),
        strokeWidth = size.stroke,
        trackColor = PrismaSemanticColors.SurfaceSunken.resolve(isDark),
    )
}

@Composable
public fun PrismaLinearLoading(
    modifier: Modifier = Modifier,
    progress: Float? = null, // null → indeterminate
    label: String = "Loading",
) {
    val isDark = LocalPrismaIsDark.current
    val a11yMod = if (progress == null) {
        modifier.progressSemantics().semantics { contentDescription = label }
    } else {
        modifier.progressSemantics(progress.coerceIn(0f, 1f)).semantics { contentDescription = label }
    }
    if (progress == null) {
        LinearProgressIndicator(
            modifier = a11yMod,
            color = PrismaSemanticColors.AccentDefault.resolve(isDark),
            trackColor = PrismaSemanticColors.SurfaceSunken.resolve(isDark),
        )
    } else {
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = a11yMod,
            color = PrismaSemanticColors.AccentDefault.resolve(isDark),
            trackColor = PrismaSemanticColors.SurfaceSunken.resolve(isDark),
        )
    }
}
