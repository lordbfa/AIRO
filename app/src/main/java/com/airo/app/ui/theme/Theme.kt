package com.airo.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AiroGreen = Color(0xFF2E7D52)
private val AiroGreenDark = Color(0xFF1B5E37)
private val AiroAmber = Color(0xFFB8860B)

private val LightColors = lightColorScheme(
    primary = AiroGreen,
    secondary = AiroAmber,
    tertiary = AiroGreenDark,
)

private val DarkColors = darkColorScheme(
    primary = AiroGreen,
    secondary = AiroAmber,
    tertiary = AiroGreenDark,
)

@Composable
fun AiroTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (useDarkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
