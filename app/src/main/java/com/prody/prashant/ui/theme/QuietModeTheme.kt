package com.prody.prashant.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * QuietModeTheme - Muted color palette for Quiet Mode
 *
 * This theme provides a calmer, softer visual experience when the user
 * needs mental simplicity. Colors are desaturated and contrast is gentler.
 *
 * Philosophy:
 * - Not dark mode, just calmer
 * - Muted but not depressing
 * - Softer contrasts, easier on the eyes
 * - Maintains accessibility (WCAG AA compliance)
 * - Respects the user's current theme choice (light/dark)
 */
object QuietModeTheme {

    // =============================================================================
    // QUIET MODE LIGHT COLORS - Muted, Soft, Calming
    // =============================================================================

    // Backgrounds - Softer, more neutral
    val QuietBackgroundLight = Color(0xFFF5F7F6)              // Even softer than default
    val QuietSurfaceLight = Color(0xFFFAFBFA)                 // Very soft white
    val QuietSurfaceVariantLight = Color(0xFFF0F3F2)          // Gentle variant
    val QuietSurfaceContainerLight = Color(0xFFEDF0EF)        // Muted container

    // Text - Softer contrasts
    val QuietTextPrimaryLight = Color(0xFF3A3A3A)             // Softer black
    val QuietTextSecondaryLight = Color(0xFF7A8287)           // Muted gray
    val QuietTextTertiaryLight = Color(0xFFADB5BA)            // Very light gray

    // Accent - Muted green (less vibrant)
    val QuietAccentLight = Color(0xFF4FB87F)                  // Softer green
    val QuietAccentMutedLight = Color(0xFF6BC491)             // Even gentler

    // Outlines and dividers - Barely there
    val QuietOutlineLight = Color(0xFFE8EDEC)                 // Very subtle
    val QuietDividerLight = Color(0xFFF0F3F2)                 // Almost invisible

    // =============================================================================
    // QUIET MODE DARK COLORS - Deeper, Calmer
    // =============================================================================

    // Backgrounds - Deeper, more restful
    val QuietBackgroundDark = Color(0xFF0A1E1C)               // Deeper teal
    val QuietSurfaceDark = Color(0xFF0F2624)                  // Slightly lighter
    val QuietSurfaceVariantDark = Color(0xFF152E2C)           // Variant
    val QuietSurfaceContainerDark = Color(0xFF1A3331)         // Container

    // Text - Softer whites
    val QuietTextPrimaryDark = Color(0xFFEBEDED)              // Softer white
    val QuietTextSecondaryDark = Color(0xFFC5CCCB)            // Muted light gray
    val QuietTextTertiaryDark = Color(0xFF8FA19E)             // Subtle gray

    // Accent - Muted green for dark mode
    val QuietAccentDark = Color(0xFF4FB87F)                   // Same as light
    val QuietAccentMutedDark = Color(0xFF6BC491)              // Gentler

    // Outlines and dividers
    val QuietOutlineDark = Color(0xFF2A4240)                  // Very subtle
    val QuietDividerDark = Color(0xFF233937)                  // Almost invisible

    // =============================================================================
    // MOOD COLORS - Muted versions
    // =============================================================================

    val QuietMoodHappy = Color(0xFFE8CF8C)                    // Softer gold
    val QuietMoodCalm = Color(0xFF89B8C8)                     // Muted blue
    val QuietMoodAnxious = Color(0xFFD4B89C)                  // Softer coral
    val QuietMoodSad = Color(0xFFA3B8C3)                      // Muted slate
    val QuietMoodMotivated = Color(0xFFE8C88C)                // Softer amber
    val QuietMoodGrateful = Color(0xFF96BFAC)                 // Muted sage
    val QuietMoodConfused = Color(0xFFC5B3D9)                 // Softer lavender
    val QuietMoodExcited = Color(0xFFE09A8A)                  // Muted coral
    val QuietMoodEnergetic = Color(0xFFE09A72)                // Softer orange
    val QuietMoodInspired = Color(0xFFAF8FC4)                 // Muted purple
    val QuietMoodNostalgic = Color(0xFFC8B894)                // Softer sepia

    // =============================================================================
    // HELPER FUNCTIONS
    // =============================================================================

    /**
     * Get the appropriate background color based on current theme and quiet mode.
     */
    @Composable
    fun getBackground(): Color {
        return if (isDarkTheme()) QuietBackgroundDark else QuietBackgroundLight
    }

    /**
     * Get the appropriate surface color.
     */
    @Composable
    fun getSurface(): Color {
        return if (isDarkTheme()) QuietSurfaceDark else QuietSurfaceLight
    }

    /**
     * Get the appropriate surface variant color.
     */
    @Composable
    fun getSurfaceVariant(): Color {
        return if (isDarkTheme()) QuietSurfaceVariantDark else QuietSurfaceVariantLight
    }

    /**
     * Get the appropriate primary text color.
     */
    @Composable
    fun getTextPrimary(): Color {
        return if (isDarkTheme()) QuietTextPrimaryDark else QuietTextPrimaryLight
    }

    /**
     * Get the appropriate secondary text color.
     */
    @Composable
    fun getTextSecondary(): Color {
        return if (isDarkTheme()) QuietTextSecondaryDark else QuietTextSecondaryLight
    }

    /**
     * Get the appropriate tertiary text color.
     */
    @Composable
    fun getTextTertiary(): Color {
        return if (isDarkTheme()) QuietTextTertiaryDark else QuietTextTertiaryLight
    }

    /**
     * Get the muted accent color.
     */
    @Composable
    fun getAccent(): Color {
        return if (isDarkTheme()) QuietAccentDark else QuietAccentLight
    }

    /**
     * Get the even more muted accent color (for subtle highlights).
     */
    @Composable
    fun getAccentMuted(): Color {
        return if (isDarkTheme()) QuietAccentMutedDark else QuietAccentMutedLight
    }

    /**
     * Get the appropriate outline color.
     */
    @Composable
    fun getOutline(): Color {
        return if (isDarkTheme()) QuietOutlineDark else QuietOutlineLight
    }

    /**
     * Get the appropriate divider color.
     */
    @Composable
    fun getDivider(): Color {
        return if (isDarkTheme()) QuietDividerDark else QuietDividerLight
    }

    /**
     * Get muted mood color based on mood name.
     */
    fun getMutedMoodColor(moodName: String): Color {
        return when (moodName.uppercase()) {
            "HAPPY" -> QuietMoodHappy
            "CALM" -> QuietMoodCalm
            "ANXIOUS" -> QuietMoodAnxious
            "SAD" -> QuietMoodSad
            "MOTIVATED" -> QuietMoodMotivated
            "GRATEFUL" -> QuietMoodGrateful
            "CONFUSED" -> QuietMoodConfused
            "EXCITED" -> QuietMoodExcited
            "ENERGETIC" -> QuietMoodEnergetic
            "INSPIRED" -> QuietMoodInspired
            "NOSTALGIC" -> QuietMoodNostalgic
            else -> QuietMoodCalm // Default to calm
        }
    }

    /**
     * Checks if a color should be replaced with its quiet mode equivalent.
     * Use this in components to dynamically adjust colors.
     */
    @Composable
    fun applyQuietMode(
        normalColor: Color,
        isDarkMode: Boolean = isDarkTheme()
    ): Color {
        // Map common colors to their quiet equivalents
        return when (normalColor) {
            ProdyAccentGreen -> if (isDarkMode) QuietAccentDark else QuietAccentLight
            ProdyBackgroundLight -> QuietBackgroundLight
            ProdyBackgroundDark -> QuietBackgroundDark
            ProdySurfaceLight -> QuietSurfaceLight
            ProdySurfaceDark -> QuietSurfaceDark
            ProdyTextPrimaryLight -> QuietTextPrimaryLight
            ProdyTextPrimaryDark -> QuietTextPrimaryDark
            else -> normalColor // Keep original if no quiet equivalent
        }
    }
}

/**
 * Extension function to get quiet mode color palette.
 * Use this in components that need to be quiet-mode aware.
 */
@Composable
fun getQuietModeColors(): QuietModeColorPalette {
    val isDark = isDarkTheme()
    return QuietModeColorPalette(
        background = if (isDark) QuietModeTheme.QuietBackgroundDark else QuietModeTheme.QuietBackgroundLight,
        surface = if (isDark) QuietModeTheme.QuietSurfaceDark else QuietModeTheme.QuietSurfaceLight,
        surfaceVariant = if (isDark) QuietModeTheme.QuietSurfaceVariantDark else QuietModeTheme.QuietSurfaceVariantLight,
        textPrimary = if (isDark) QuietModeTheme.QuietTextPrimaryDark else QuietModeTheme.QuietTextPrimaryLight,
        textSecondary = if (isDark) QuietModeTheme.QuietTextSecondaryDark else QuietModeTheme.QuietTextSecondaryLight,
        textTertiary = if (isDark) QuietModeTheme.QuietTextTertiaryDark else QuietModeTheme.QuietTextTertiaryLight,
        accent = if (isDark) QuietModeTheme.QuietAccentDark else QuietModeTheme.QuietAccentLight,
        accentMuted = if (isDark) QuietModeTheme.QuietAccentMutedDark else QuietModeTheme.QuietAccentMutedLight,
        outline = if (isDark) QuietModeTheme.QuietOutlineDark else QuietModeTheme.QuietOutlineLight,
        divider = if (isDark) QuietModeTheme.QuietDividerDark else QuietModeTheme.QuietDividerLight
    )
}

/**
 * Data class holding all quiet mode colors for easy access.
 */
data class QuietModeColorPalette(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accent: Color,
    val accentMuted: Color,
    val outline: Color,
    val divider: Color
)
