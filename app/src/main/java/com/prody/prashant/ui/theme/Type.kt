package com.prody.prashant.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.prody.prashant.R

/**
 * Prody Design System - Typography
 *
 * A refined typography system using Poppins with carefully calibrated weights,
 * sizes, and letter spacing for optimal readability and visual hierarchy.
 *
 * Design Principles:
 * - Generous line heights for comfortable reading
 * - Subtle letter spacing for elegance
 * - Clear weight distinctions for visual hierarchy
 * - Consistent optical sizing across all text styles
 */

// Primary font family - Poppins (for UI elements)
val PoppinsFamily = FontFamily(
    Font(R.font.poppins_thin, FontWeight.Thin),
    Font(R.font.poppins_extralight, FontWeight.ExtraLight),
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_extrabold, FontWeight.ExtraBold),
    Font(R.font.poppins_black, FontWeight.Black)
)

// Secondary font family - Playfair Display (for wisdom, quotes, and stoic content)
// This elegant serif font contrasts with Poppins to separate "ancient wisdom" from "modern interface"
val PlayfairFamily = FontFamily(
    Font(R.font.playfairdisplay_regular, FontWeight.Normal),
    Font(R.font.playfairdisplay_medium, FontWeight.Medium),
    Font(R.font.playfairdisplay_semibold, FontWeight.SemiBold),
    Font(R.font.playfairdisplay_bold, FontWeight.Bold),
    Font(R.font.playfairdisplay_italic, FontWeight.Normal, FontStyle.Italic)
)

/**
 * Main Typography configuration following Material Design 3 guidelines
 * with custom refinements for the Prody design language.
 */
val ProdyTypography = Typography(
    // ==========================================================================
    // DISPLAY STYLES - For large, impactful text (splash screens, hero sections)
    // ==========================================================================
    displayLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 52.sp,
        lineHeight = 60.sp,
        letterSpacing = (-0.5).sp  // Tighter tracking for large display
    ),
    displayMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 42.sp,
        lineHeight = 50.sp,
        letterSpacing = (-0.25).sp
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
        lineHeight = 26.sp,  // More generous for readability
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
// =============================================================================

/**
 * Quote style - For displaying wisdom quotes and inspirational text
 */
val QuoteTextStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Light,
    fontSize = 18.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.2.sp
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
 */
val ButtonTextStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
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
    letterSpacing = 1.5.sp  // Wide tracking for small caps effect
)

// =============================================================================
// WISDOM TYPOGRAPHY - Playfair Display for philosophical content
// =============================================================================

/**
 * Hero wisdom quote - For Buddha's Thought and featured wisdom cards
 * Large, impactful display with elegant serif presence
 */
val WisdomHeroStyle = TextStyle(
    fontFamily = PlayfairFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 24.sp,
    lineHeight = 34.sp,
    letterSpacing = 0.2.sp,
    fontStyle = FontStyle.Normal
)

/**
 * Large wisdom quote - For quote of the day and prominent wisdom
 */
val WisdomLargeStyle = TextStyle(
    fontFamily = PlayfairFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp,
    lineHeight = 30.sp,
    letterSpacing = 0.15.sp,
    fontStyle = FontStyle.Italic
)

/**
 * Medium wisdom text - For proverbs, idioms, and journal prompts
 */
val WisdomMediumStyle = TextStyle(
    fontFamily = PlayfairFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.1.sp
)

/**
 * Small wisdom text - For secondary wisdom content and attributions
 */
val WisdomSmallStyle = TextStyle(
    fontFamily = PlayfairFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.1.sp
)

/**
 * Wisdom attribution - For author names and sources
 */
val WisdomAttributionStyle = TextStyle(
    fontFamily = PlayfairFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.5.sp
)

/**
 * Wisdom caption - For small wisdom-related metadata
 */
val WisdomCaptionStyle = TextStyle(
    fontFamily = PlayfairFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp,
    fontStyle = FontStyle.Italic
)

/**
 * Journal prompt style - For reflective questions and prompts
 */
val JournalPromptStyle = TextStyle(
    fontFamily = PlayfairFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.1.sp,
    fontStyle = FontStyle.Italic
)

/**
 * Stoic maxim style - For concise philosophical statements
 */
val StoicMaximStyle = TextStyle(
    fontFamily = PlayfairFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.3.sp
)

// =============================================================================
// GAMIFICATION TYPOGRAPHY - Stats, Counters, and Achievements
// =============================================================================

/**
 * Streak counter style - Large, bold numbers for streak display
 */
val StreakCounterStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 48.sp,
    lineHeight = 56.sp,
    letterSpacing = (-1).sp
)

/**
 * Streak counter small - For inline streak displays
 */
val StreakCounterSmallStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    letterSpacing = (-0.5).sp
)

/**
 * XP display style - For experience points
 */
val XpDisplayStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp
)

/**
 * XP display large - For hero XP displays
 */
val XpDisplayLargeStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 36.sp,
    letterSpacing = (-0.25).sp
)

/**
 * Level badge text - For level indicators
 */
val LevelBadgeStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.1.sp
)

/**
 * Level badge large - For profile level display
 */
val LevelBadgeLargeStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    letterSpacing = (-0.25).sp
)

/**
 * Achievement title style - For badge and achievement names
 */
val AchievementTitleStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.1.sp
)

/**
 * Achievement description style - For badge descriptions
 */
val AchievementDescriptionStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.2.sp
)

/**
 * Rarity label style - For Common, Rare, Epic, etc. labels
 */
val RarityLabelStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 10.sp,
    lineHeight = 14.sp,
    letterSpacing = 1.0.sp  // Wide tracking for emphasis
)

/**
 * Leaderboard rank style - For position numbers
 */
val LeaderboardRankStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    letterSpacing = (-0.5).sp
)

/**
 * Leaderboard rank small - For list rank numbers
 */
val LeaderboardRankSmallStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.sp
)

/**
 * Leaderboard name style - For usernames on leaderboard
 */
val LeaderboardNameStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 15.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp
)

/**
 * Leaderboard points style - For point displays
 */
val LeaderboardPointsStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.sp
)

// =============================================================================
// STAT DISPLAY TYPOGRAPHY - For statistics and metrics
// =============================================================================

/**
 * Stat number hero - For large stat displays on profile/stats screens
 */
val StatNumberHeroStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 56.sp,
    lineHeight = 64.sp,
    letterSpacing = (-1.5).sp
)

/**
 * Stat number large - For prominent stat displays
 */
val StatNumberLargeStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 36.sp,
    lineHeight = 44.sp,
    letterSpacing = (-0.5).sp
)

/**
 * Stat number medium - For stat cards
 */
val StatNumberMediumStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    letterSpacing = 0.sp
)

/**
 * Stat number small - For compact stat displays
 */
val StatNumberSmallStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.sp
)

/**
 * Stat label style - For stat labels like "Total Entries", "Words Written"
 */
val StatLabelStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
)

/**
 * Stat label large - For prominent stat labels
 */
val StatLabelLargeStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.3.sp
)

// =============================================================================
// NOTIFICATION TYPOGRAPHY - For in-app and push notifications
// =============================================================================

/**
 * Notification title style - For notification headers
 */
val NotificationTitleStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.1.sp
)

/**
 * Notification body style - For notification content
 */
val NotificationBodyStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.2.sp
)

/**
 * Notification action style - For notification buttons
 */
val NotificationActionStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.5.sp
)

// =============================================================================
// CHALLENGE TYPOGRAPHY - For community challenges
// =============================================================================

/**
 * Challenge title style - For challenge names
 */
val ChallengeTitleStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.sp
)

/**
 * Challenge description style - For challenge details
 */
val ChallengeDescriptionStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.2.sp
)

/**
 * Challenge progress style - For progress indicators
 */
val ChallengeProgressStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.1.sp
)

/**
 * Challenge countdown style - For time remaining
 */
val ChallengeCountdownStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
)

// =============================================================================
// FUTURE MESSAGE TYPOGRAPHY - Time capsule styling
// =============================================================================

/**
 * Future message content style - For message text
 */
val FutureMessageContentStyle = TextStyle(
    fontFamily = PlayfairFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.1.sp,
    fontStyle = FontStyle.Italic
)

/**
 * Future message date style - For delivery date displays
 */
val FutureMessageDateStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.2.sp
)

/**
 * Future message countdown style - For "Arriving in X days"
 */
val FutureMessageCountdownStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.sp
)

// =============================================================================
// MOOD TYPOGRAPHY - For mood tracking and emotion display
// =============================================================================

/**
 * Mood emoji label style - For mood selection UI
 */
val MoodLabelStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.3.sp
)

/**
 * Mood insight style - For Buddha's mood-based insights
 */
val MoodInsightStyle = TextStyle(
    fontFamily = PlayfairFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.1.sp,
    fontStyle = FontStyle.Italic
)

// =============================================================================
// WORD OF THE DAY TYPOGRAPHY
// =============================================================================

/**
 * Word display style - For the featured word
 */
val WordDisplayStyle = TextStyle(
    fontFamily = PlayfairFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 36.sp,
    letterSpacing = 0.sp
)

/**
 * Pronunciation style - For phonetic spellings
 */
val PronunciationStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Light,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.5.sp,
    fontStyle = FontStyle.Italic
)

/**
 * Definition style - For word definitions
 */
val DefinitionStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.2.sp
)

/**
 * Example sentence style - For usage examples
 */
val ExampleSentenceStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.1.sp,
    fontStyle = FontStyle.Italic
)

/**
 * Etymology style - For word origin text
 */
val EtymologyStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Light,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.2.sp
)
