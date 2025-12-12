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
 * Prody Design System - Theme Configuration
 *
 * A comprehensive theme setup that provides:
 * - Light and Dark color schemes with carefully designed contrast ratios
 * - Support for Material You dynamic colors (Android 12+)
 * - Extended spacing system for consistent layouts
 * - Proper system UI handling for edge-to-edge experience
 *
 * Accessibility Compliance:
 * - All color combinations meet WCAG 2.1 AA contrast requirements (4.5:1 for text)
 * - Interactive elements have sufficient contrast (3:1 minimum)
 * - Focus states and selection indicators are clearly visible
 */

// =============================================================================
// LIGHT COLOR SCHEME - Forest & Warmth Palette
// =============================================================================

private val LightColorScheme = lightColorScheme(
    // Primary colors
    primary = ProdyPrimary,
    onPrimary = ProdyOnPrimary,
    primaryContainer = ProdyPrimaryContainer,
    onPrimaryContainer = ProdyPrimaryVariant,

    // Secondary colors
    secondary = ProdySecondary,
    onSecondary = ProdyOnSecondary,
    secondaryContainer = ProdySecondary.copy(alpha = 0.24f),
    onSecondaryContainer = ProdyOnSecondary,

    // Tertiary colors
    tertiary = ProdyTertiary,
    onTertiary = ProdyOnTertiary,
    tertiaryContainer = ProdyTertiaryContainer,
    onTertiaryContainer = ProdyPrimaryVariant,

    // Background & surface
    background = ProdyBackground,
    onBackground = ProdyOnBackground,
    surface = ProdySurface,
    onSurface = ProdyOnSurface,
    surfaceVariant = ProdySurfaceVariant,
    onSurfaceVariant = ProdyOnSurfaceVariant,

    // Surface tones for elevation
    surfaceTint = ProdyPrimary,
    inverseSurface = ProdyOnSurface,
    inverseOnSurface = ProdySurface,
    inversePrimary = ProdyPrimaryDark,

    // Error colors
    error = ProdyError,
    onError = ProdyOnError,
    errorContainer = ProdyErrorContainer,
    onErrorContainer = ProdyError,

    // Outline colors
    outline = ProdyOutline,
    outlineVariant = ProdyOutlineVariant,

    // Scrim for overlays
    scrim = Scrim
)

// =============================================================================
// DARK COLOR SCHEME - Night Forest Palette
// =============================================================================

private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = ProdyPrimaryDark,
    onPrimary = ProdyOnPrimaryDark,
    primaryContainer = ProdyPrimaryContainerDark,
    onPrimaryContainer = ProdyPrimaryDark,

    // Secondary colors
    secondary = ProdySecondaryDark,
    onSecondary = ProdyOnSecondaryDark,
    secondaryContainer = ProdySecondaryContainerDark,
    onSecondaryContainer = ProdySecondaryDark,

    // Tertiary colors
    tertiary = ProdyTertiaryDark,
    onTertiary = ProdyOnTertiaryDark,
    tertiaryContainer = ProdyTertiaryContainerDark,
    onTertiaryContainer = ProdyTertiaryDark,

    // Background & surface
    background = ProdyBackgroundDark,
    onBackground = ProdyOnBackgroundDark,
    surface = ProdySurfaceDark,
    onSurface = ProdyOnSurfaceDark,
    surfaceVariant = ProdySurfaceVariantDark,
    onSurfaceVariant = ProdyOnSurfaceVariantDark,

    // Surface tones for elevation
    surfaceTint = ProdyPrimaryDark,
    inverseSurface = ProdyOnSurfaceDark,
    inverseOnSurface = ProdySurfaceDark,
    inversePrimary = ProdyPrimary,

    // Error colors
    error = ProdyErrorDark,
    onError = ProdyOnErrorDark,
    errorContainer = ProdyErrorContainerDark,
    onErrorContainer = ProdyErrorDark,

    // Outline colors
    outline = ProdyOutlineDark,
    outlineVariant = ProdyOutlineVariantDark,

    // Scrim for overlays
    scrim = Scrim
)

// =============================================================================
// THEME MODE ENUM
// =============================================================================

enum class ThemeMode {
    LIGHT,  // Always use light theme
    DARK,   // Always use dark theme
    SYSTEM  // Follow system preference
}

// =============================================================================
// EXTENDED SPACING SYSTEM
// =============================================================================

/**
 * Spacing values for consistent layout throughout the app.
 * Based on an 4dp base unit with harmonious scaling.
 */
data class ProdySpacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val default: Dp = 16.dp,
    val large: Dp = 20.dp,
    val extraLarge: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 40.dp,
    val huge: Dp = 48.dp,
    val massive: Dp = 64.dp
)

/**
 * Elevation values for consistent depth throughout the app.
 */
data class ProdyElevation(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 1.dp,
    val small: Dp = 2.dp,
    val medium: Dp = 4.dp,
    val large: Dp = 6.dp,
    val extraLarge: Dp = 8.dp,
    val overlay: Dp = 12.dp
)

// Composition locals for extended theme values
val LocalProdySpacing = staticCompositionLocalOf { ProdySpacing() }
val LocalProdyElevation = staticCompositionLocalOf { ProdyElevation() }

// =============================================================================
// MAIN THEME COMPOSABLE
// =============================================================================

/**
 * Main Prody theme composable that wraps the entire application.
 *
 * @param themeMode The preferred theme mode (LIGHT, DARK, or SYSTEM)
 * @param dynamicColor Whether to use Material You dynamic colors (Android 12+)
 * @param content The app content to be themed
 */
@Composable
fun ProdyTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Determine if dark theme should be used
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    // Select the appropriate color scheme
    val colorScheme = when {
        // Use dynamic colors if available and enabled
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Use custom dark scheme
        darkTheme -> DarkColorScheme
        // Use custom light scheme
        else -> LightColorScheme
    }

    // Configure system UI appearance
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Enable edge-to-edge display
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // Set transparent system bars
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                @Suppress("DEPRECATION")
                window.statusBarColor = Color.Transparent.toArgb()
                @Suppress("DEPRECATION")
                window.navigationBarColor = Color.Transparent.toArgb()
            }

            // Configure system bar icon colors based on theme
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    // Provide extended theme values and apply Material Theme
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

// =============================================================================
// THEME EXTENSION ACCESSORS
// =============================================================================

/**
 * Access spacing values from anywhere in the composition.
 * Usage: ProdyTheme.spacing.medium
 */
object ProdyTheme {
    val spacing: ProdySpacing
        @Composable
        get() = LocalProdySpacing.current

    val elevation: ProdyElevation
        @Composable
        get() = LocalProdyElevation.current
}
