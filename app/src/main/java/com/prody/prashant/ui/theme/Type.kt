package com.prody.prashant.ui.theme

import android.util.Log
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontLoadingStrategy
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.prody.prashant.R

/**
 * Prody Design System - Typography (Redesigned)
 *
 * A clean, modern typography system using EXCLUSIVELY Poppins font family
 * for all text elements throughout the entire application.
 *
 * Design Principles:
 * - Single font family (Poppins) for visual consistency
 * - Clear weight distinctions for visual hierarchy
 * - Generous line heights for comfortable reading
 * - Optimized letter spacing for mobile readability
 * - WCAG AA contrast compliance for all text sizes
 *
 * Typography Hierarchy:
 * - Bold: Titles and important numbers
 * - Medium/SemiBold: Section headers
 * - Regular: Body text
 * - Light: Subtle secondary content
 */

private const val TAG = "ProdyTypography"

/**
 * Creates a safe Font instance with proper error handling.
 * Uses Async loading strategy to prevent crash if font resource is unavailable.
 */
private fun safeFont(
    resId: Int,
    weight: FontWeight,
    style: FontStyle = FontStyle.Normal
): Font? {
    return try {
        Font(
            resId = resId,
            weight = weight,
            style = style,
            loadingStrategy = FontLoadingStrategy.Async
        )
    } catch (e: Exception) {
        com.prody.prashant.util.AppLogger.w(TAG, "Failed to load font resource $resId with weight $weight", e)
        null
    }
}

/**
 * Primary font family - Poppins (EXCLUSIVE font for all UI elements)
 *
 * Uses async loading strategy to ensure fonts are loaded gracefully,
 * falling back to system fonts if unavailable.
 */
val PoppinsFamily: FontFamily = try {
    val fonts = listOfNotNull(
        safeFont(R.font.poppins_thin, FontWeight.Thin),
        safeFont(R.font.poppins_extralight, FontWeight.ExtraLight),
        safeFont(R.font.poppins_light, FontWeight.Light),
        safeFont(R.font.poppins_regular, FontWeight.Normal),
        safeFont(R.font.poppins_medium, FontWeight.Medium),
        safeFont(R.font.poppins_semibold, FontWeight.SemiBold),
        safeFont(R.font.poppins_bold, FontWeight.Bold),
        safeFont(R.font.poppins_extrabold, FontWeight.ExtraBold),
        safeFont(R.font.poppins_black, FontWeight.Black)
    )

    if (fonts.isEmpty()) {
        com.prody.prashant.util.AppLogger.e(TAG, "All Poppins fonts failed to load, falling back to system sans-serif")
        FontFamily.SansSerif
    } else {
        if (fonts.size < 9) {
            com.prody.prashant.util.AppLogger.w(TAG, "Only ${fonts.size}/9 Poppins fonts loaded successfully")
        }
        FontFamily(fonts)
    }
} catch (e: Exception) {
    com.prody.prashant.util.AppLogger.e(TAG, "Failed to initialize Poppins font family", e)
    FontFamily.SansSerif
}

/**
 * Main Typography configuration following Material Design 3 guidelines
 * with Poppins as the exclusive font family.
 */
val ProdyTypography = Typography(
    // ==========================================================================
    // DISPLAY STYLES - For large, impactful text (splash screens, hero sections)
    // ==========================================================================
    displayLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 56.sp,
        lineHeight = 64.sp,
        letterSpacing = (-1.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 42.sp,
        lineHeight = 50.sp,
        letterSpacing = (-1.0).sp
    ),
    displaySmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 42.sp,
        letterSpacing = 0.sp
    ),

    // ==========================================================================
    // HEADLINE STYLES - For section headers and prominent text
    // ==========================================================================
    headlineLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),

    // ==========================================================================
    // TITLE STYLES - For card titles, list items, and UI element headers
    // ==========================================================================
    titleLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // ==========================================================================
    // BODY STYLES - For main content and readable text blocks
    // ==========================================================================
    bodyLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.3.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.3.sp
    ),

    // ==========================================================================
    // LABEL STYLES - For buttons, chips, tabs, and small UI elements
    // ==========================================================================
    labelLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )
)

// =============================================================================
// EXTENDED TYPOGRAPHY - Custom text styles for special use cases
// All using Poppins exclusively
// =============================================================================

/**
 * Quote style - For displaying wisdom quotes and inspirational text
 */
val QuoteTextStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Light,
    fontSize = 18.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.2.sp,
    fontStyle = FontStyle.Italic
)

/**
 * Number display - For stats, counters, and numerical highlights
 */
val NumberDisplayStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    letterSpacing = (-0.5).sp
)

/**
 * Caption style - For timestamps, metadata, and secondary information
 */
val CaptionTextStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp
)

/**
 * Button text - Emphasized for interactive elements
 * Note: Uses Regular weight as per design spec (not bold)
 */
val ButtonTextStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,  // Regular weight per design spec
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.5.sp
)

/**
 * Overline style - For category labels and small headers
 */
val OverlineTextStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 10.sp,
    lineHeight = 14.sp,
    letterSpacing = 1.5.sp
)

// =============================================================================
// WISDOM TYPOGRAPHY - Using Poppins (replacing Playfair)
// =============================================================================

/**
 * Hero wisdom quote - For Buddha's Thought and featured wisdom cards
 */
val WisdomHeroStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Light,
    fontSize = 24.sp,
    lineHeight = 34.sp,
    letterSpacing = 0.2.sp,
    fontStyle = FontStyle.Italic
)

/**
 * Large wisdom quote - For quote of the day and prominent wisdom
 */
val WisdomLargeStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Light,
    fontSize = 20.sp,
    lineHeight = 30.sp,
    letterSpacing = 0.15.sp,
    fontStyle = FontStyle.Italic
)

/**
 * Medium wisdom text - For proverbs, idioms, and journal prompts
 */
val WisdomMediumStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.1.sp
)

/**
 * Small wisdom text - For secondary wisdom content and attributions
 */
val WisdomSmallStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.1.sp
)

/**
 * Wisdom attribution - For author names and sources
 */
val WisdomAttributionStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.5.sp
)

/**
 * Wisdom caption - For small wisdom-related metadata
 */
val WisdomCaptionStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp,
    fontStyle = FontStyle.Italic
)

/**
 * Journal prompt style - For reflective questions and prompts
 */
val JournalPromptStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Light,
    fontSize = 18.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.1.sp,
    fontStyle = FontStyle.Italic
)

/**
 * Stoic maxim style - For concise philosophical statements
 */
val StoicMaximStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.3.sp
)

// =============================================================================
// GAMIFICATION TYPOGRAPHY - Stats, Badges, Streaks, XP
// =============================================================================

/**
 * Stat number style - For large stat displays (total entries, words written)
 */
val StatNumberStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    letterSpacing = (-0.5).sp
)

/**
 * Large stat number - For hero stats and major milestones
 */
val StatNumberLargeStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 48.sp,
    lineHeight = 56.sp,
    letterSpacing = (-0.5).sp
)

/**
 * Badge title style - For achievement and badge names
 */
val BadgeTitleStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.5.sp
)

/**
 * Badge description style - For achievement descriptions
 */
val BadgeDescriptionStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.25.sp
)

/**
 * Streak counter style - For displaying streak numbers prominently
 */
val StreakCounterStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 36.sp,
    lineHeight = 44.sp,
    letterSpacing = (-0.5).sp
)

/**
 * Streak label style - For "days" or "streak" labels
 */
val StreakLabelStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
)

/**
 * XP text style - For experience points display
 */
val XpTextStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp
)

/**
 * Level text style - For level display
 */
val LevelTextStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.sp
)

/**
 * Leaderboard rank style - For position numbers on leaderboard
 */
val LeaderboardRankStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 24.sp,
    lineHeight = 28.sp,
    letterSpacing = (-0.5).sp
)

/**
 * Leaderboard name style - For user names on leaderboard
 */
val LeaderboardNameStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp
)

/**
 * Leaderboard points style - For point displays
 */
val LeaderboardPointsStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp
)

/**
 * Progress percentage style - For showing progress percentages
 */
val ProgressPercentageStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.25.sp
)

// =============================================================================
// NOTIFICATION TYPOGRAPHY - For push notifications and in-app messages
// =============================================================================

/**
 * Notification title style - Bold and attention-grabbing
 */
val NotificationTitleStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.1.sp
)

/**
 * Notification body style - Clear and readable
 */
val NotificationBodyStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.2.sp
)

// =============================================================================
// CARD TYPOGRAPHY - For various card types
// =============================================================================

/**
 * Card title style - For card headers
 */
val CardTitleStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.sp
)

/**
 * Card subtitle style - For card secondary headers
 */
val CardSubtitleStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp
)

/**
 * Card body style - For card content
 */
val CardBodyStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.2.sp
)

/**
 * Card caption style - For timestamps and metadata on cards
 */
val CardCaptionStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp
)

// =============================================================================
// ONBOARDING SPECIFIC TYPOGRAPHY
// =============================================================================

/**
 * Onboarding title - Large, bold, attention-grabbing
 */
val OnboardingTitleStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 36.sp,
    letterSpacing = (-0.25).sp
)

/**
 * Onboarding subtitle - Secondary explanation text
 */
val OnboardingSubtitleStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.1.sp
)

/**
 * Onboarding CTA button text
 */
val OnboardingButtonStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,  // Regular weight per design spec
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
)

// =============================================================================
// TIME CAPSULE SPECIFIC TYPOGRAPHY
// =============================================================================

/**
 * Time capsule section header
 */
val TimeCapsuleSectionStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 1.0.sp
)

/**
 * Time capsule tag text
 */
val TimeCapsuleTagStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp
)

// =============================================================================
// PROFILE SPECIFIC TYPOGRAPHY
// =============================================================================

/**
 * Profile display name
 */
val ProfileNameStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    letterSpacing = 0.sp
)

/**
 * Profile title/badge text
 */
val ProfileTitleStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.25.sp
)

// =============================================================================
// HOME SCREEN SPECIFIC TYPOGRAPHY
// =============================================================================

/**
 * Home greeting text
 */
val HomeGreetingStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    letterSpacing = 0.sp
)

/**
 * Quick action tile label
 */
val QuickActionLabelStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.25.sp
)

// =============================================================================
// STATS SCREEN SPECIFIC TYPOGRAPHY
// =============================================================================

/**
 * Stats hero number (very large)
 */
val StatsHeroNumberStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 56.sp,
    lineHeight = 64.sp,
    letterSpacing = (-1.0).sp
)

/**
 * Stats label
 */
val StatsLabelStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
)

// =============================================================================
// MONTHLY LETTER TYPOGRAPHY - Beautiful, letter-like reading experience
// =============================================================================

/**
 * Letter greeting - warm, personal opening
 */
val LetterGreetingStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Light,
    fontSize = 20.sp,
    lineHeight = 32.sp,
    letterSpacing = 0.1.sp,
    fontStyle = FontStyle.Italic
)

/**
 * Letter body text - comfortable reading for letter content
 */
val LetterBodyStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.2.sp
)

/**
 * Letter section header - distinguishes different parts of the letter
 */
val LetterSectionHeaderStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 1.0.sp
)

/**
 * Letter highlight quote - for featured quotes from user's entries
 */
val LetterQuoteStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Light,
    fontSize = 18.sp,
    lineHeight = 30.sp,
    letterSpacing = 0.15.sp,
    fontStyle = FontStyle.Italic
)

/**
 * Letter closing - warm, personal sign-off
 */
val LetterClosingStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.2.sp
)

/**
 * Letter metadata - dates, stats presented elegantly
 */
val LetterMetadataStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.4.sp
)

// =============================================================================
// HAVEN TYPOGRAPHY - "Anti-AI" Handwritten Feel
// =============================================================================

/**
 * Haven Message Style - Serif font for organic, human feel
 * Uses default Serif as a fallback for a handwritten aesthetic
 */
val HavenMessageStyle = TextStyle(
    fontFamily = PoppinsFamily, // Switched to Poppins for a cleaner, premium look
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.2.sp
)

/**
 * Haven User Input Style - Clean Sans-Serif for clarity
 */
val HavenInputStyle = TextStyle(
    fontFamily = PoppinsFamily,    // Keep user input clean and modern
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.1.sp
)

/**
 * Letter title - month and year display
 */
val LetterTitleStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    letterSpacing = 0.sp
)

/**
 * Letter stat number - for presenting numbers in letter
 */
val LetterStatNumberStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 36.sp,
    letterSpacing = (-0.5).sp
)

/**
 * Letter stat label - for stat descriptions in letter
 */
val LetterStatLabelStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.3.sp
)

