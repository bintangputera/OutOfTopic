package com.poetralabs.outoftopic.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WarmColorScheme = lightColorScheme(
    primary = TerracottaBrand,
    onPrimary = Ivory,
    primaryContainer = CoralAccent,
    onPrimaryContainer = Ivory,
    secondary = DarkSurface,
    onSecondary = Ivory,
    secondaryContainer = WarmSand,
    onSecondaryContainer = CharcoalWarm,
    tertiary = OliveGray,
    onTertiary = Ivory,
    tertiaryContainer = BorderCream,
    onTertiaryContainer = DarkWarm,
    error = ErrorCrimson,
    onError = Ivory,
    errorContainer = Color(0xFFFFEDED),
    onErrorContainer = ErrorCrimson,
    background = Parchment,
    onBackground = AnthropicNearBlack,
    surface = Ivory,
    onSurface = AnthropicNearBlack,
    surfaceVariant = WarmSand,
    onSurfaceVariant = OliveGray,
    outline = BorderWarm,
    outlineVariant = BorderCream,
    inverseSurface = DarkSurface,
    inverseOnSurface = WarmSilver,
)

@Composable
fun OutOfTopicTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WarmColorScheme,
        typography = Typography,
        content = content
    )
}
