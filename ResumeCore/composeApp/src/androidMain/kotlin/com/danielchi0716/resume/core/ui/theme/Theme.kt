package com.danielchi0716.resume.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFF3B6A60),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBDF0E1),
    onPrimaryContainer = Color(0xFF00201B),
    secondary = Color(0xFF4A635D),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCE8E0),
    onSecondaryContainer = Color(0xFF06201B),
    tertiary = Color(0xFF456179),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCCE5FF),
    onTertiaryContainer = Color(0xFF001E31),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF4FBF7),
    onBackground = Color(0xFF161D1B),
    surface = Color(0xFFF4FBF7),
    onSurface = Color(0xFF161D1B),
    surfaceVariant = Color(0xFFDBE5DF),
    onSurfaceVariant = Color(0xFF3F4945),
    outline = Color(0xFF6F7975),
    outlineVariant = Color(0xFFBFC9C4),
    inverseSurface = Color(0xFF2B3230),
    inverseOnSurface = Color(0xFFECF2EE),
    inversePrimary = Color(0xFFA1D4C5),
    surfaceTint = Color(0xFF3B6A60),
    scrim = Color.Black,
    surfaceBright = Color(0xFFF4FBF7),
    surfaceDim = Color(0xFFD5DCD8),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFEEF5F1),
    surfaceContainer = Color(0xFFE8EFEB),
    surfaceContainerHigh = Color(0xFFE2E9E6),
    surfaceContainerHighest = Color(0xFFDDE4E0),
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFFA1D4C5),
    onPrimary = Color(0xFF003830),
    primaryContainer = Color(0xFF1F5148),
    onPrimaryContainer = Color(0xFFBDF0E1),
    secondary = Color(0xFFB0CCC4),
    onSecondary = Color(0xFF1C3530),
    secondaryContainer = Color(0xFF324B46),
    onSecondaryContainer = Color(0xFFCCE8E0),
    tertiary = Color(0xFFACCAE5),
    onTertiary = Color(0xFF143349),
    tertiaryContainer = Color(0xFF2D4A60),
    onTertiaryContainer = Color(0xFFCCE5FF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0E1513),
    onBackground = Color(0xFFDDE4E0),
    surface = Color(0xFF0E1513),
    onSurface = Color(0xFFDDE4E0),
    surfaceVariant = Color(0xFF3F4945),
    onSurfaceVariant = Color(0xFFBFC9C4),
    outline = Color(0xFF899390),
    outlineVariant = Color(0xFF3F4945),
    inverseSurface = Color(0xFFDDE4E0),
    inverseOnSurface = Color(0xFF2B3230),
    inversePrimary = Color(0xFF3B6A60),
    surfaceTint = Color(0xFFA1D4C5),
    scrim = Color.Black,
    surfaceBright = Color(0xFF343B39),
    surfaceDim = Color(0xFF0E1513),
    surfaceContainerLowest = Color(0xFF090F0E),
    surfaceContainerLow = Color(0xFF161D1B),
    surfaceContainer = Color(0xFF1A211F),
    surfaceContainerHigh = Color(0xFF252B29),
    surfaceContainerHighest = Color(0xFF303634),
)

enum class ThemeMode { System, Light, Dark, Dynamic }

val LocalThemeMode = staticCompositionLocalOf { ThemeMode.System }

@Composable
fun ResumeTheme(
    themeMode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit,
) {
    val dark = when (themeMode) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.System, ThemeMode.Dynamic -> isSystemInDarkTheme()
    }
    val context = LocalContext.current
    val colors: ColorScheme = when {
        themeMode == ThemeMode.Dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dark -> DarkColors
        else -> LightColors
    }
    CompositionLocalProvider(LocalThemeMode provides themeMode) {
        MaterialTheme(
            colorScheme = colors,
            typography = MaterialTheme.typography,
            content = content,
        )
    }
}
