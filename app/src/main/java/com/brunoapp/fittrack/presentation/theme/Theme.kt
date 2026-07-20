package com.brunoapp.fittrack.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = LimePrimary,
    onPrimary = CharcoalBackground,
    primaryContainer = LimeContainer,
    onPrimaryContainer = LimePrimary,
    secondary = LimeDark,
    onSecondary = CharcoalBackground,
    tertiary = GoldRecord,
    onTertiary = CharcoalBackground,
    background = CharcoalBackground,
    onBackground = TextPrimary,
    surface = CharcoalSurface,
    onSurface = TextPrimary,
    surfaceVariant = CharcoalSurfaceHigh,
    onSurfaceVariant = TextSecondary,
    outline = CharcoalOutline,
    error = RedError,
    onError = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = LimeOnLight,
    onPrimary = LightSurface,
    primaryContainer = Color_LimeContainerLight,
    onPrimaryContainer = LightTextPrimary,
    secondary = LimeDark,
    onSecondary = LightSurface,
    tertiary = GoldRecord,
    onTertiary = LightTextPrimary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceHigh,
    onSurfaceVariant = LightTextSecondary,
    outline = LightTextSecondary,
    error = RedError,
    onError = LightSurface
)

@Composable
fun FitTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FitTrackTypography,
        shapes = FitTrackShapes,
        content = content
    )
}
