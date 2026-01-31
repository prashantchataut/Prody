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
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    onSecondaryContainer = ProdyOnWarning, // Warning text usually black on amber

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

/**
 * Prody Design System - Spacing System
 */
data class ProdySpacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val default: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val huge: Dp = 48.dp
)

/**
 * Prody Design System - Elevation System
 */
data class ProdyElevation(
    val none: Dp = 0.dp,
    val extraLow: Dp = 1.dp,
    val low: Dp = 2.dp,
    val medium: Dp = 4.dp,
    val high: Dp = 8.dp,
    val extraHigh: Dp = 12.dp
)

/**
 * Helper to determine if the theme is dark.
 */
@Composable
fun isDarkTheme(): Boolean = isSystemInDarkTheme()

// Re-export composition locals
val LocalProdySpacing = staticCompositionLocalOf { ProdySpacing() }
val LocalProdyElevation = staticCompositionLocalOf { ProdyElevation() }

@Composable
fun ProdyTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false, // We stick to our brand colors
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
                val window = it.window
                WindowCompat.setDecorFitsSystemWindows(window, false)
                
                val statusBarColor = if (darkTheme) ProdyBackgroundDark.toArgb() else ProdyBackgroundLight.toArgb()
                
                // We rely on WindowCompat for handling status bar appearance
                // but setting color is still good for fallback
                @Suppress("DEPRECATION")
                window.statusBarColor = statusBarColor
                @Suppress("DEPRECATION")
                window.navigationBarColor = statusBarColor

                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalProdySpacing provides ProdySpacing(),
        LocalProdyElevation provides ProdyElevation()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ProdyTypography,
            shapes = ProdyShapes,
            content = content
        )
    }
}

object ProdyTheme {
    val spacing: ProdySpacing
        @Composable
        get() = LocalProdySpacing.current

    val elevation: ProdyElevation
        @Composable
        get() = LocalProdyElevation.current
}