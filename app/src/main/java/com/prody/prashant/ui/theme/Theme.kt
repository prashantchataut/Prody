package com.prody.prashant.ui.theme

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

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Prody Design System - Theme Configuration (Revamp 2026)
 *
 * Implements the "Polished, Intuitive Mental Wellness Companion" aesthetic.
 *
 * Core changes:
 * - Primary: Forest Green (#2E7D32)
 * - Backgrounds: Clean White / Soft Gray
 * - Typography: Poppins (handled in Type.kt)
 * - Shapes: Rounded (12dp/16dp) (handled in Shape.kt)
 * - Spacing/Elevation: ProdyTokens.Spacing / ProdyTokens.Elevation
 */

private val LightColorScheme = lightColorScheme(
    primary = ProdyPrimary,
    onPrimary = ProdyTextOnPrimaryLight,
    primaryContainer = ProdyPrimaryContainer,
    onPrimaryContainer = ProdyTextPrimaryLight,

    secondary = ProdySecondary,
    onSecondary = ProdyOnSecondary,
    secondaryContainer = ProdyWarningContainer,
    onSecondaryContainer = ProdyOnWarning,

    tertiary = ProdyTextSecondaryLight,
    onTertiary = ProdyOnSuccess,
    tertiaryContainer = ProdySurfaceVariantLight,
    onTertiaryContainer = ProdyTextPrimaryLight,

    background = ProdyBackgroundLight,
    onBackground = ProdyTextPrimaryLight,
    surface = ProdySurfaceLight,
    onSurface = ProdyTextPrimaryLight,
    surfaceVariant = ProdySurfaceVariantLight,
    onSurfaceVariant = ProdyTextSecondaryLight,

    error = ProdyError,
    onError = ProdyOnError,
    errorContainer = ProdyErrorContainer,
    onErrorContainer = ProdyError,

    outline = ProdyOutlineLight,
    outlineVariant = ProdyDividerLight,

    scrim = Scrim
)

private val DarkColorScheme = darkColorScheme(
    primary = ProdyPrimaryDark,
    onPrimary = ProdyTextOnPrimaryDark,
    primaryContainer = ProdyPrimaryContainerDark,
    onPrimaryContainer = ProdyTextPrimaryDark,

    secondary = ProdySecondaryDark,
    onSecondary = ProdyOnSecondaryDark,
    secondaryContainer = ProdyWarningContainerDark,
    onSecondaryContainer = ProdyOnWarning,

    tertiary = ProdyTextSecondaryDark,
    onTertiary = ProdyTextOnPrimaryDark,
    tertiaryContainer = ProdySurfaceVariantDark,
    onTertiaryContainer = ProdyTextPrimaryDark,

    background = ProdyBackgroundDark,
    onBackground = ProdyTextPrimaryDark,
    surface = ProdySurfaceDark,
    onSurface = ProdyTextPrimaryDark,
    surfaceVariant = ProdySurfaceVariantDark,
    onSurfaceVariant = ProdyTextSecondaryDark,

    error = ProdyError,
    onError = ProdyOnError,
    errorContainer = ProdyErrorContainerDark,
    onErrorContainer = ProdyError,

    outline = ProdyOutlineDark,
    outlineVariant = ProdyDividerDark,

    scrim = Scrim
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

@Composable
fun isDarkTheme(): Boolean = isSystemInDarkTheme()

@Composable
fun ProdyTheme(
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

    CompositionLocalProvider(
        LocalHavenColors provides havenColors,
        LocalStreakColors provides LightStreakColors,
        LocalMoodColors provides LightMoodColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ProdyTypography,
            shapes = ProdyShapes,
            content = content
        )
    }
}

@Composable
fun getTextPrimary(): Color {
    return if (isDarkTheme()) ProdyTextPrimaryDark else ProdyTextPrimaryLight
}