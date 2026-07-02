package com.example.pharmax.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PharmaXLightColorScheme = lightColorScheme(
    primary = PharmaGreen,
    onPrimary = PharmaSurface,
    primaryContainer = PharmaGreenContainer,
    secondary = PharmaBlue,
    background = PharmaBackground,
    surface = PharmaSurface,
    onSurface = PharmaDarkText,
    onSurfaceVariant = PharmaGrayText,
    outline = PharmaOutline,
    outlineVariant = PharmaDivider,
    tertiaryContainer = PharmaGreenTint,
    onTertiaryContainer = PharmaGreenContainer,
    secondaryContainer = PharmaBlueTint,
    onSecondaryContainer = PharmaBlue,
    error = PharmaError,
    errorContainer = PharmaErrorTint,
    onErrorContainer = PharmaError
)

private val PharmaXDarkColorScheme = darkColorScheme(
    primary = PharmaGreenDark,
    onPrimary = PharmaBackgroundDark,
    primaryContainer = PharmaGreenContainerDark,
    secondary = PharmaBlueDark,
    background = PharmaBackgroundDark,
    surface = PharmaSurfaceDark,
    onSurface = PharmaDarkTextDark,
    onSurfaceVariant = PharmaGrayTextDark,
    outline = PharmaOutlineDark,
    outlineVariant = PharmaDividerDark,
    tertiaryContainer = PharmaGreenTintDark,
    onTertiaryContainer = PharmaGreenDark,
    secondaryContainer = PharmaBlueTintDark,
    onSecondaryContainer = PharmaBlueDark,
    error = PharmaErrorDark,
    errorContainer = PharmaErrorTintDark,
    onErrorContainer = PharmaErrorDark
)

@Composable
fun PharmaXTheme(
    content: @Composable () -> Unit
) {
    val darkTheme = AppThemeState.isDarkMode.value
    val colorScheme = if (darkTheme) PharmaXDarkColorScheme else PharmaXLightColorScheme

    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = colorScheme.background.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
