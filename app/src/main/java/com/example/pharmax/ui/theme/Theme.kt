package com.example.pharmax.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PharmaXColorScheme = lightColorScheme(
    primary = PharmaGreen,
    onPrimary = PharmaSurface,
    primaryContainer = PharmaGreenContainer,
    secondary = PharmaBlue,
    background = PharmaBackground,
    surface = PharmaSurface,
    onSurface = PharmaDarkText,
    onSurfaceVariant = PharmaGrayText,
    outline = PharmaOutline
)

@Composable
fun PharmaXTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PharmaXColorScheme,
        typography = Typography,
        content = content
    )
}
