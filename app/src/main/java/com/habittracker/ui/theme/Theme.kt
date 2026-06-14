package com.habittracker.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = TerracottaDark,
    secondary = SageGreenDark,
    tertiary = OchreAmberDark,
    background = PaperDarkBg,
    surface = PaperDarkSurface,
    surfaceVariant = PaperDarkSurfaceVariant,
    onPrimary = PaperDarkBg,
    onSecondary = PaperDarkBg,
    onBackground = CreamOnDark,
    onSurface = CreamOnDark,
    onSurfaceVariant = LightGreyOnDark,
    outline = PaperDarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = Terracotta,
    secondary = SageGreen,
    tertiary = OchreAmber,
    background = PaperLightBg,
    surface = PaperLightSurface,
    surfaceVariant = PaperLightSurfaceVariant,
    onPrimary = PaperLightBg,
    onSecondary = PaperLightBg,
    onBackground = InkCharcoal,
    onSurface = InkCharcoal,
    onSurfaceVariant = InkGrey,
    outline = PaperLightOutline
)

@Composable
fun HabitTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            // Set status & nav bars icon colors
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
