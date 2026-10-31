package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FlowDarkColorScheme = darkColorScheme(
    primary = FlowCyan,
    onPrimary = Color.Black,
    secondary = FlowBlue,
    onSecondary = Color.White,
    tertiary = FlowGreen,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkBorder,
    onSurfaceVariant = Color(0xFF8B949E)
)

@Composable
fun AsadFlowTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FlowDarkColorScheme,
        typography = Typography,
        content = content
    )
}
