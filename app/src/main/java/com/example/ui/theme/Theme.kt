package com.example.ui.theme

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
    primary = ZenAccent,
    onPrimary = ZenOnyx,
    primaryContainer = ZenSurfaceElevated,
    onPrimaryContainer = ZenTextPrimary,
    secondary = ZenAccentBlue,
    onSecondary = ZenOnyx,
    secondaryContainer = ZenSurface,
    onSecondaryContainer = ZenTextPrimary,
    tertiary = ZenAccentPurple,
    background = ZenCanvas,
    onBackground = ZenTextPrimary,
    surface = ZenSurface,
    onSurface = ZenTextPrimary,
    surfaceVariant = ZenSurfaceElevated,
    onSurfaceVariant = ZenTextSecondary,
    outline = ZenGlassBorder,
    outlineVariant = ZenGlassBorderStrong
)

private val LightColorScheme = lightColorScheme(
    primary = ZenOnyx,
    onPrimary = ZenTextPrimary,
    secondary = ZenAccentBlue,
    background = ZenCanvas,
    surface = ZenSurface,
    onSurface = ZenTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // EmuZen is designed primarily in Dark Mode
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = ZenCanvas.toArgb()
                window.navigationBarColor = ZenCanvas.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
