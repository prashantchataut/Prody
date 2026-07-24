package com.kairos.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Kairos Material theme.
 *
 * Dynamic color is opt-in so the editorial identity remains stable by default.
 * Legacy theme aliases are retained while persisted identifiers and older screens
 * migrate without breaking existing installations.
 */

private val LightColorScheme = lightColorScheme(
    primary = KairosPrimary,
    onPrimary = KairosTextOnPrimaryLight,
    primaryContainer = KairosIndigoContainerLight,
    onPrimaryContainer = KairosOnIndigoContainerLight,

    secondary = KairosClay,
    onSecondary = Color.White,
    secondaryContainer = KairosClayContainerLight,
    onSecondaryContainer = KairosOnClayContainerLight,

    tertiary = KairosVerdigris,
    onTertiary = Color.White,
    tertiaryContainer = KairosVerdigrisContainerLight,
    onTertiaryContainer = KairosOnVerdigrisContainerLight,

    background = KairosBackgroundLight,
    onBackground = KairosTextPrimaryLight,
    surface = KairosSurfaceLight,
    onSurface = KairosTextPrimaryLight,
    surfaceVariant = KairosSurfaceVariantLight,
    onSurfaceVariant = KairosTextSecondaryLight,

    error = KairosError,
    onError = KairosOnError,
    errorContainer = KairosErrorContainer,
    onErrorContainer = KairosError,

    outline = KairosOutlineLight,
    outlineVariant = KairosDividerLight,

    scrim = Scrim
)

private val DarkColorScheme = darkColorScheme(
    primary = KairosPrimaryDark,
    onPrimary = KairosTextOnPrimaryDark,
    primaryContainer = KairosIndigoContainerDark,
    onPrimaryContainer = KairosOnIndigoContainerDark,

    secondary = KairosSoftClay,
    onSecondary = Color(0xFF3C160A),
    secondaryContainer = KairosClayContainerDark,
    onSecondaryContainer = KairosOnClayContainerDark,

    tertiary = KairosSeaGlass,
    onTertiary = Color(0xFF072E28),
    tertiaryContainer = KairosVerdigrisContainerDark,
    onTertiaryContainer = KairosOnVerdigrisContainerDark,

    background = KairosBackgroundDark,
    onBackground = KairosTextPrimaryDark,
    surface = KairosSurfaceDark,
    onSurface = KairosTextPrimaryDark,
    surfaceVariant = KairosSurfaceVariantDark,
    onSurfaceVariant = KairosTextSecondaryDark,

    error = KairosError,
    onError = KairosOnError,
    errorContainer = KairosErrorContainerDark,
    onErrorContainer = KairosError,

    outline = KairosOutlineDark,
    outlineVariant = KairosDividerDark,

    scrim = Scrim
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

@Composable
fun isDarkTheme(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f

@Composable
fun KairosTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

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
            val activity = view.context as? Activity
            activity?.let {
                val insetsController = WindowCompat.getInsetsController(it.window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    val havenColors = if (darkTheme) DarkHavenColors else LightHavenColors
    val kairosGlassColors = if (darkTheme) DarkKairosGlassColors else LightKairosGlassColors

    CompositionLocalProvider(
        LocalKairosGlassColors provides kairosGlassColors,
        LocalHavenColors provides havenColors,
        LocalStreakColors provides LightStreakColors,
        LocalMoodColors provides LightMoodColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = KairosTypography,
            shapes = KairosShapes,
            content = content
        )
    }
}

@Composable
fun getTextPrimary(): Color {
    return if (isDarkTheme()) KairosTextPrimaryDark else KairosTextPrimaryLight
}