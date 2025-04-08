package com.example.upkeep.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryVariant,
    tertiary = Pink80,
    background = Background,
    onBackground = OnBackground,
    surface = Color(0xFFE0E0E0), // Change surface color
    onSurface = Color(0xFF000000), // Change onSurface color
    error = Error,
    onError = OnError,
    outline = Color(0xFF808080)
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
//    onPrimaryContainer = Primary,
//    inversePrimary = Primary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryVariant,
//    onSecondaryContainer = Primary,
    tertiary = Pink40,
//    onTertiary = Pink40,
//    tertiaryContainer = Primary,
//    onTertiaryContainer = Primary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
//    surfaceVariant = Primary,
//    onSurfaceVariant = Primary,
//    surfaceTint = Primary,
//    inverseSurface = Primary,
//    inverseOnSurface = Primary,
//    error = Primary,
//    onError = Primary,
//    errorContainer = Primary,
//    onErrorContainer = Primary,
//    outline = Primary,
//    outlineVariant = Primary,
//    scrim = Primary,
//    surfaceBright = Primary,
    surfaceContainer = LightBlue,
    surfaceContainerHigh = LightBlue,
    surfaceContainerHighest = LightBlue,
    surfaceContainerLow = LightBlue,
    surfaceContainerLowest = LightBlue,
    surfaceDim = LightBlue,
)

@Composable
fun UpKeepTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}