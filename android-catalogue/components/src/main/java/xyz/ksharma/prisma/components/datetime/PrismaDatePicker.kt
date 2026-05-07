package xyz.ksharma.prisma.components.datetime

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import xyz.ksharma.prisma.coreui.LocalPrismaIsDark
import xyz.ksharma.prisma.tokens.PrismaSemanticColors

/** Wraps M3 DatePicker with Prisma-themed colours. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun PrismaDatePicker(
    state: DatePickerState = rememberDatePickerState(),
    modifier: Modifier = Modifier,
) {
    val isDark = LocalPrismaIsDark.current
    DatePicker(
        state = state,
        modifier = modifier,
        colors = DatePickerDefaults.colors(
            containerColor = PrismaSemanticColors.SurfaceRaised.resolve(isDark),
            titleContentColor = PrismaSemanticColors.TextSecondary.resolve(isDark),
            headlineContentColor = PrismaSemanticColors.TextPrimary.resolve(isDark),
            weekdayContentColor = PrismaSemanticColors.TextTertiary.resolve(isDark),
            dayContentColor = PrismaSemanticColors.TextPrimary.resolve(isDark),
            selectedDayContainerColor = PrismaSemanticColors.AccentDefault.resolve(isDark),
            selectedDayContentColor = PrismaSemanticColors.TextOnAccent.resolve(isDark),
            todayContentColor = PrismaSemanticColors.AccentDefault.resolve(isDark),
            todayDateBorderColor = PrismaSemanticColors.AccentDefault.resolve(isDark),
        ),
    )
}

/** Wraps M3 TimePicker with Prisma-themed colours. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun PrismaTimePicker(
    state: TimePickerState = rememberTimePickerState(),
    modifier: Modifier = Modifier,
) {
    val isDark = LocalPrismaIsDark.current
    TimePicker(
        state = state,
        modifier = modifier,
        colors = prismaTimePickerColors(isDark),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun prismaTimePickerColors(isDark: Boolean): TimePickerColors = TimePickerDefaults.colors(
    clockDialColor = PrismaSemanticColors.SurfaceSunken.resolve(isDark),
    clockDialSelectedContentColor = PrismaSemanticColors.TextOnAccent.resolve(isDark),
    clockDialUnselectedContentColor = PrismaSemanticColors.TextSecondary.resolve(isDark),
    selectorColor = PrismaSemanticColors.AccentDefault.resolve(isDark),
    timeSelectorSelectedContainerColor = PrismaSemanticColors.AccentSubtle.resolve(isDark),
    timeSelectorSelectedContentColor = PrismaSemanticColors.AccentDefault.resolve(isDark),
    timeSelectorUnselectedContainerColor = PrismaSemanticColors.SurfaceSunken.resolve(isDark),
    timeSelectorUnselectedContentColor = PrismaSemanticColors.TextPrimary.resolve(isDark),
)
