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
 * Prody Design System - Theme Configuration (Redesigned)
 *
 * A premium, minimalist theme following strict flat design principles:
 * - NO shadows, gradients, or skeuomorphism
 * - Deep dark teal backgrounds (#0D2826) for dark mode
 * - Clean off-white backgrounds (#F0F4F3) for light mode
 * - Vibrant neon green accent (#36F97F) for interactive elements
 *
 * Design Philosophy:
 * - Extreme minimalism and cleanliness
 * - Strong visual hierarchy through color and typography
 * - Consistent 8dp spacing grid
 * - WCAG AA accessibility compliance
 */

// =============================================================================
// LIGHT COLOR SCHEME - Clean, Bright Aesthetic
// =============================================================================

private val LightColorScheme = lightColorScheme(
    // Primary - Vibrant Neon Green
    primary = ProdyPrimary,
    onPrimary = ProdyOnPrimary,
    primaryContainer = ProdyPrimaryContainer,
    onPrimaryContainer = ProdyTextPrimaryLight,

    // Secondary - Dark for contrast
    secondary = ProdySecondary,
    onSecondary = ProdyOnSecondary,
    secondaryContainer = ProdySurfaceContainerLight,
    onSecondaryContainer = ProdyTextPrimaryLight,

    // Tertiary - Medium gray
    tertiary = ProdyTertiary,
    onTertiary = ProdyOnTertiary,
    tertiaryContainer = ProdyTertiaryContainer,
    onTertiaryContainer = ProdyTextPrimaryLight,

    // Background & Surface - Clean whites
    background = ProdyBackground,
    onBackground = ProdyOnBackground,
    surface = ProdySurface,
    onSurface = ProdyOnSurface,
    surfaceVariant = ProdySurfaceVariant,
    onSurfaceVariant = ProdyOnSurfaceVariant,

    // Surface tones (flat - no elevation tinting)
    surfaceTint = Color.Transparent, // NO tinting for flat design
    inverseSurface = ProdyBackgroundDark,
    inverseOnSurface = ProdyTextPrimaryDark,
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
// DARK COLOR SCHEME - Deep Teal/Green Aesthetic
// =============================================================================

private val DarkColorScheme = darkColorScheme(
    // Primary - Vibrant Neon Green (same in both themes)
    primary = ProdyPrimaryDark,
    onPrimary = ProdyOnPrimaryDark,
    primaryContainer = ProdyPrimaryContainerDark,
    onPrimaryContainer = ProdyTextPrimaryDark,

    // Secondary - White for contrast
    secondary = ProdySecondaryDark,
    onSecondary = ProdyOnSecondaryDark,
    secondaryContainer = ProdySecondaryContainerDark,
    onSecondaryContainer = ProdyTextPrimaryDark,

    // Tertiary - Light gray
    tertiary = ProdyTertiaryDark,
    onTertiary = ProdyOnTertiaryDark,
    tertiaryContainer = ProdyTertiaryContainerDark,
    onTertiaryContainer = ProdyTextPrimaryDark,

    // Background & Surface - Deep teal/green
    background = ProdyBackgroundDark,
    onBackground = ProdyOnBackgroundDark,
    surface = ProdySurfaceDark,
    onSurface = ProdyOnSurfaceDark,
    surfaceVariant = ProdySurfaceVariantDark,
    onSurfaceVariant = ProdyOnSurfaceVariantDark,

    // Surface tones (flat - no elevation tinting)
    surfaceTint = Color.Transparent, // NO tinting for flat design
    inverseSurface = ProdyBackground,
    inverseOnSurface = ProdyTextPrimaryLight,
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
// EXTENDED SPACING SYSTEM - 8dp Grid
// =============================================================================

/**
 * Spacing values for consistent layout throughout the app.
 * Based on an 8dp grid system with 4dp fine-tuning.
 */
data class ProdySpacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,    // 4dp - fine adjustments
    val small: Dp = 8.dp,         // 8dp - base unit
    val medium: Dp = 12.dp,       // 12dp
    val default: Dp = 16.dp,      // 16dp - 2x base
    val large: Dp = 20.dp,        // 20dp
    val extraLarge: Dp = 24.dp,   // 24dp - 3x base
    val xxl: Dp = 32.dp,          // 32dp - 4x base
    val xxxl: Dp = 40.dp,         // 40dp - 5x base
    val huge: Dp = 48.dp,         // 48dp - 6x base
    val massive: Dp = 64.dp       // 64dp - 8x base
)

/**
 * Elevation values - FLAT DESIGN means minimal to no elevation.
 * Only use for semantic layering, not visual shadows.
 */
data class ProdyElevation(
    val none: Dp = 0.dp,          // No elevation - default
    val extraSmall: Dp = 0.dp,    // Flat
    val small: Dp = 0.dp,         // Flat
    val medium: Dp = 0.dp,        // Flat
    val large: Dp = 0.dp,         // Flat
    val extraLarge: Dp = 0.dp,    // Flat
    val overlay: Dp = 0.dp        // Flat - use scrim instead
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
 *                     Note: Disabled by default to maintain brand consistency
 * @param content The app content to be themed
 */
@Composable
fun ProdyTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false, // Disabled by default for brand consistency
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

    // Configure system UI appearance safely
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity
            if (activity != null) {
                try {
                    val window = activity.window
                    if (window != null) {
                        // Enable edge-to-edge display
                        WindowCompat.setDecorFitsSystemWindows(window, false)

                        // Set system bar colors to match theme background
                        val statusBarColor = if (darkTheme) {
                            ProdyBackgroundDark.toArgb()
                        } else {
                            ProdyBackground.toArgb()
                        }

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                            @Suppress("DEPRECATION")
                            window.statusBarColor = statusBarColor
                            @Suppress("DEPRECATION")
                            window.navigationBarColor = statusBarColor
                        }

                        // Configure system bar icon colors based on theme
                        WindowCompat.getInsetsController(window, view).apply {
                            isAppearanceLightStatusBars = !darkTheme
                            isAppearanceLightNavigationBars = !darkTheme
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.w("ProdyTheme", "Failed to configure system UI: ${e.message}")
                }
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
 * Access extended theme values from anywhere in the composition.
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

// =============================================================================
// THEME UTILITY EXTENSIONS
// =============================================================================

/**
 * Check if the current theme is dark mode.
 * Usage: val isDark = MaterialTheme.colorScheme.isDarkTheme()
 */
@Composable
fun isDarkTheme(): Boolean {
    return MaterialTheme.colorScheme.background == ProdyBackgroundDark
}

/**
 * Get the appropriate text color based on background.
 */
@Composable
fun getTextPrimary(): Color {
    return if (isDarkTheme()) ProdyTextPrimaryDark else ProdyTextPrimaryLight
}

@Composable
fun getTextSecondary(): Color {
    return if (isDarkTheme()) ProdyTextSecondaryDark else ProdyTextSecondaryLight
}

@Composable
fun getTextTertiary(): Color {
    return if (isDarkTheme()) ProdyTextTertiaryDark else ProdyTextTertiaryLight
}

/**
 * Get the accent color - consistent across themes
 */
fun getAccentColor(): Color = ProdyAccentGreen

/**
 * Get the appropriate surface color
 */
@Composable
fun getSurfaceColor(): Color {
    return if (isDarkTheme()) ProdySurfaceDark else ProdySurfaceLight
}

/**
 * Get the appropriate surface variant color
 */
@Composable
fun getSurfaceVariantColor(): Color {
    return if (isDarkTheme()) ProdySurfaceVariantDark else ProdySurfaceVariantLight
}

/**
 * Get the appropriate outline color
 */
@Composable
fun getOutlineColor(): Color {
    return if (isDarkTheme()) ProdyOutlineDark else ProdyOutlineLight
}

/**
 * Get the appropriate divider color
 */
@Composable
fun getDividerColor(): Color {
    return if (isDarkTheme()) ProdyDividerDark else ProdyDividerLight
}
