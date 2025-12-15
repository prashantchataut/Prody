package com.prody.prashant.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Prody Design System - Color Palette
 *
 * A refined, modern color system inspired by nature and growth.
 * Colors are chosen for optimal accessibility, visual hierarchy, and emotional resonance.
 *
 * Design Philosophy:
 * - Primary: Deep Forest Green - represents growth, wisdom, and natural progression
 * - Secondary: Warm Sand - evokes warmth, comfort, and approachability
 * - Tertiary: Soft Teal - adds freshness and vitality
 * - Accents: Carefully chosen for emotional resonance and clear visual distinction
 */

// =============================================================================
// LIGHT THEME COLORS - Forest & Warmth Palette
// =============================================================================

// Primary - Deep Forest Green (represents growth and wisdom)
val ProdyPrimary = Color(0xFF2D5A3D)  // Deeper, more sophisticated green
val ProdyPrimaryVariant = Color(0xFF1E3D2B)  // Rich dark green
val ProdyOnPrimary = Color(0xFFFFFFFF)
val ProdyPrimaryContainer = Color(0xFFD4E8DC)  // Soft sage container

// Secondary - Warm Sand (evokes warmth and approachability)
val ProdySecondary = Color(0xFFD4C4A8)  // Warm sand tone
val ProdySecondaryVariant = Color(0xFFC4B494)  // Deeper warm tone
val ProdyOnSecondary = Color(0xFF2D2A24)

// Tertiary - Soft Teal (adds freshness and vitality)
val ProdyTertiary = Color(0xFF5B9A8B)  // Sophisticated teal
val ProdyOnTertiary = Color(0xFFFFFFFF)
val ProdyTertiaryContainer = Color(0xFFCCE5E0)

// Background & Surface - Clean, warm whites
val ProdyBackground = Color(0xFFFBFAF8)  // Warm off-white
val ProdyOnBackground = Color(0xFF1A1C1E)

val ProdySurface = Color(0xFFFFFFFF)
val ProdySurfaceVariant = Color(0xFFF3F0E8)  // Subtle warm tint
val ProdyOnSurface = Color(0xFF1A1C1E)
val ProdyOnSurfaceVariant = Color(0xFF5C5C5C)

// Elevated surfaces with subtle depth
val ProdySurfaceElevated = Color(0xFFFEFEFE)
val ProdySurfaceDim = Color(0xFFF0EDE5)

// Error states
val ProdyError = Color(0xFFBF3B3B)  // Refined red
val ProdyOnError = Color(0xFFFFFFFF)
val ProdyErrorContainer = Color(0xFFFFE5E5)

// Success state
val ProdySuccess = Color(0xFF2E7D4A)
val ProdyOnSuccess = Color(0xFFFFFFFF)

// Warning state
val ProdyWarning = Color(0xFFE6A23C)
val ProdyOnWarning = Color(0xFF1A1C1E)

// Outlines & Dividers
val ProdyOutline = Color(0xFFDDD8D0)
val ProdyOutlineVariant = Color(0xFFCBC6BE)

// =============================================================================
// DARK THEME COLORS - Night Forest Palette
// =============================================================================

// Primary Dark - Luminous Green (visible yet soft)
val ProdyPrimaryDark = Color(0xFF8FD4A6)  // Luminous mint green
val ProdyPrimaryVariantDark = Color(0xFF6BBF8A)
val ProdyOnPrimaryDark = Color(0xFF003921)
val ProdyPrimaryContainerDark = Color(0xFF1A4028)

// Secondary Dark - Warm Taupe
val ProdySecondaryDark = Color(0xFFD4C4A8)
val ProdyOnSecondaryDark = Color(0xFF3C3627)
val ProdySecondaryContainerDark = Color(0xFF524B39)

// Tertiary Dark - Cool Aqua
val ProdyTertiaryDark = Color(0xFF7FBFB3)
val ProdyOnTertiaryDark = Color(0xFF003731)
val ProdyTertiaryContainerDark = Color(0xFF1E4D46)

// Background & Surface Dark - Deep charcoal
val ProdyBackgroundDark = Color(0xFF0F1210)  // Deep charcoal with green tint
val ProdyOnBackgroundDark = Color(0xFFE4E3DF)

val ProdySurfaceDark = Color(0xFF181C19)  // Elevated surface
val ProdySurfaceVariantDark = Color(0xFF252A26)
val ProdyOnSurfaceDark = Color(0xFFE4E3DF)
val ProdyOnSurfaceVariantDark = Color(0xFF9CA39D)

// Elevated surfaces for cards in dark mode
val ProdySurfaceElevatedDark = Color(0xFF1F2421)
val ProdySurfaceDimDark = Color(0xFF131613)

// Error Dark
val ProdyErrorDark = Color(0xFFFFB3B3)
val ProdyOnErrorDark = Color(0xFF5C1A1A)
val ProdyErrorContainerDark = Color(0xFF8C2626)

// Outlines Dark
val ProdyOutlineDark = Color(0xFF3C4139)
val ProdyOutlineVariantDark = Color(0xFF4F554C)

// =============================================================================
// MOOD COLORS - Emotionally Resonant Palette
// =============================================================================

// Each mood color is carefully chosen for psychological resonance
val MoodHappy = Color(0xFFFFC93C)       // Sunshine gold - joy and optimism
val MoodCalm = Color(0xFF6CB4D4)        // Serene sky blue - peace and tranquility
val MoodAnxious = Color(0xFFE8A87C)     // Soft coral - gentle acknowledgment
val MoodSad = Color(0xFF8BA8B9)         // Muted slate blue - understanding
val MoodMotivated = Color(0xFFFFD166)   // Energetic amber - drive and ambition
val MoodGrateful = Color(0xFF7EC8A3)    // Soft sage - appreciation and growth
val MoodConfused = Color(0xFFB39DDB)    // Soft lavender - reflection and clarity-seeking
val MoodExcited = Color(0xFFFF7B5A)     // Vibrant coral - enthusiasm
val MoodEnergetic = Color(0xFFFF8F42)   // Dynamic orange - vitality
val MoodInspired = Color(0xFF9B6DD4)    // Creative purple - imagination

// =============================================================================
// GAMIFICATION COLORS - Achievement & Reward Palette
// =============================================================================

// Tier Colors - Distinct and celebratory
val GoldTier = Color(0xFFE6B422)         // Rich gold - top achievement
val SilverTier = Color(0xFFAAB0B5)       // Sophisticated silver
val BronzeTier = Color(0xFFC08D5E)       // Warm bronze
val PlatinumTier = Color(0xFF8CD3D9)     // Premium ice blue

// Achievement States
val AchievementUnlocked = Color(0xFF3CB371)  // Vibrant green - success
val AchievementLocked = Color(0xFF888888)    // Neutral gray - not yet
val AchievementProgress = Color(0xFFF5A623)  // Warm amber - in progress
val AchievementRare = Color(0xFF7B68EE)      // Royal purple - special

// =============================================================================
// STREAK & ENGAGEMENT COLORS
// =============================================================================

// Streak Colors - Warm and motivating
val StreakFire = Color(0xFFE65C2C)       // Dynamic fire orange
val StreakIce = Color(0xFF3B9DD4)        // Cool determination blue
val StreakGlow = Color(0xFFFFD93D)       // Glowing warmth
val StreakColor = Color(0xFFE65C2C)      // Default streak

// Streak Milestones
val StreakWeek = Color(0xFFFF8C42)       // One week
val StreakMonth = Color(0xFFE65C2C)      // One month
val StreakQuarter = Color(0xFFD43B3B)    // Three months

// =============================================================================
// FUNCTIONAL COLORS
// =============================================================================

// Interactive states
val InteractiveHover = Color(0xFF000000).copy(alpha = 0.04f)
val InteractivePressed = Color(0xFF000000).copy(alpha = 0.08f)
val InteractiveFocus = Color(0xFF2D5A3D).copy(alpha = 0.12f)

// Overlay colors
val OverlayLight = Color(0xFFFFFFFF).copy(alpha = 0.9f)
val OverlayDark = Color(0xFF000000).copy(alpha = 0.5f)
val Scrim = Color(0xFF000000).copy(alpha = 0.32f)

// =============================================================================
// GRADIENT DEFINITIONS (for composables to use)
// =============================================================================

// Primary gradient colors
val GradientPrimaryStart = Color(0xFF2D5A3D)
val GradientPrimaryEnd = Color(0xFF5B9A8B)

// Warm gradient colors
val GradientWarmStart = Color(0xFFD4C4A8)
val GradientWarmEnd = Color(0xFFF5EDE0)

// Accent gradient colors
val GradientAccentStart = Color(0xFFE65C2C)
val GradientAccentEnd = Color(0xFFFFD93D)

// =============================================================================
// PREMIUM VIOLET PALETTE - Wisdom & Introspection Theme
// =============================================================================

// Primary Violet - Deep wisdom, introspection
val ProdyPremiumViolet = Color(0xFF6B5CE7)
val ProdyPremiumVioletVariant = Color(0xFF5A4AD4)
val ProdyPremiumVioletLight = Color(0xFF8B7EF0)
val ProdyPremiumVioletContainer = Color(0xFFE8E5FC)

// =============================================================================
// JOURNAL MOOD COLORS - Extended Premium Palette
// =============================================================================

val MoodJoyful = Color(0xFFFFC857)       // Warm sunshine yellow
val MoodReflective = Color(0xFFB4A7D6)   // Soft lavender for contemplation
val MoodNeutral = Color(0xFFB0BEC5)      // Balanced gray

// =============================================================================
// ACHIEVEMENT RARITY COLORS - Premium Tier System
// =============================================================================

val RarityCommon = Color(0xFF78909C)     // Slate gray
val RarityUncommon = Color(0xFF66BB6A)   // Fresh green
val RarityRare = Color(0xFF42A5F5)       // Bright blue
val RarityEpic = Color(0xFFAB47BC)       // Rich purple
val RarityLegendary = Color(0xFFD4AF37)  // Prestigious gold
val RarityMythic = Color(0xFFFFD700)     // Brilliant gold with special treatment

// Rarity Glow Colors (for badges/achievements)
val RarityCommonGlow = Color(0x339E9E9E)
val RarityUncommonGlow = Color(0x3366BB6A)
val RarityRareGlow = Color(0x3342A5F5)
val RarityEpicGlow = Color(0x33AB47BC)
val RarityLegendaryGlow = Color(0x33D4AF37)
val RarityMythicGlow = Color(0x66FFD700)

// =============================================================================
// ENHANCED STREAK COLORS - Fire & Energy
// =============================================================================

val StreakEmber = Color(0xFFFFC371)      // Warm ember glow
val StreakCold = Color(0xFF90CAF9)       // Just started - cool blue
val StreakWarm = Color(0xFFFFB74D)       // Building up - warm orange
val StreakHot = Color(0xFFFF7043)        // On fire - deep orange
val StreakBlazing = Color(0xFFE53935)    // Incredible streak - red hot

// =============================================================================
// LEADERBOARD POSITION COLORS
// =============================================================================

val LeaderboardGold = Color(0xFFFFD700)    // 1st place
val LeaderboardSilver = Color(0xFFC0C0C0)  // 2nd place
val LeaderboardBronze = Color(0xFFCD7F32)  // 3rd place
val LeaderboardTop10 = Color(0xFF6B5CE7)   // Top 10 highlight

// =============================================================================
// XP & PROGRESS BAR COLORS
// =============================================================================

val XpBarBackground = Color(0xFFE0E0E0)
val XpBarFill = Color(0xFF66BB6A)
val XpBarGlow = Color(0x6666BB6A)
val XpBarFillAlt = Color(0xFF4CAF50)      // Alternative XP bar fill
val LevelUpGlow = Color(0xFFFFD700)       // Level up celebration glow

// =============================================================================
// STATUS COLORS - Clear Communication
// =============================================================================

val SuccessColor = Color(0xFF4CAF50)
val WarningColor = Color(0xFFFFC107)
val ErrorColor = Color(0xFFE53935)
val InfoColor = Color(0xFF2196F3)

// =============================================================================
// TEXT HIERARCHY COLORS - Enhanced Readability
// =============================================================================

val TextPrimaryLight = Color(0xFF1A1A2E)
val TextSecondaryLight = Color(0xFF5A5A7A)
val TextTertiaryLight = Color(0xFF8A8AA0)

val TextPrimaryDark = Color(0xFFFAFAFC)
val TextSecondaryDark = Color(0xFFB0B0C0)
val TextTertiaryDark = Color(0xFF707080)

// =============================================================================
// SURFACE COLORS - Premium Depth System
// =============================================================================

val SurfaceLight = Color(0xFFFAFAFC)
val SurfaceMedium = Color(0xFFF5F5F7)
val SurfaceDark = Color(0xFF1A1A2E)
val SurfaceDarkElevated = Color(0xFF252542)

// =============================================================================
// NOTIFICATION COLORS - Lively & Engaging
// =============================================================================

val NotificationPrimary = Color(0xFF6B5CE7)
val NotificationSuccess = Color(0xFF4CAF50)
val NotificationWarning = Color(0xFFFF9800)
val NotificationCelebration = Color(0xFFFFD700)
val NotificationStreak = Color(0xFFFF6B6B)
val NotificationAchievement = Color(0xFF9C27B0)
val NotificationReminder = Color(0xFF00BCD4)
val NotificationMotivation = Color(0xFFFF5722)

// =============================================================================
// GRADIENT DEFINITIONS - Premium Visual Effects
// =============================================================================

object ProdyGradients {
    val primaryGradient = listOf(ProdyPrimary, ProdyTertiary)
    val premiumGradient = listOf(ProdyPremiumViolet, ProdyPremiumVioletLight)
    val goldGradient = listOf(Color(0xFFD4AF37), Color(0xFFF4D03F))
    val streakGradient = listOf(StreakFire, StreakGlow, StreakEmber)
    val calmGradient = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
    val growthGradient = listOf(Color(0xFF11998E), Color(0xFF38EF7D))
    val sunriseGradient = listOf(Color(0xFFFF6B6B), Color(0xFFFFC371))
    val oceanGradient = listOf(Color(0xFF2193B0), Color(0xFF6DD5ED))
    val forestGradient = listOf(Color(0xFF134E5E), Color(0xFF71B280))
    val motivationGradient = listOf(Color(0xFFFF416C), Color(0xFFFF4B2B))
    val serenityGradient = listOf(Color(0xFFA18CD1), Color(0xFFFBC2EB))
    val wisdomGradient = listOf(Color(0xFF4568DC), Color(0xFFB06AB3))

    // Notification-specific gradients
    val celebrationGradient = listOf(Color(0xFFFFD700), Color(0xFFFF8C00), Color(0xFFFF6347))
    val achievementGradient = listOf(Color(0xFF9C27B0), Color(0xFFE040FB))
    val streakNotificationGradient = listOf(Color(0xFFFF6B6B), Color(0xFFFFAB76))

    // Rarity tier gradients for achievement badges
    val commonGradient = listOf(RarityCommon, Color(0xFF90A4AE))
    val uncommonGradient = listOf(RarityUncommon, Color(0xFF81C784))
    val rareGradient = listOf(RarityRare, Color(0xFF64B5F6))
    val epicGradient = listOf(RarityEpic, Color(0xFFCE93D8))
    val legendaryGradient = listOf(RarityLegendary, Color(0xFFF4D03F))
    val mythicGradient = listOf(Color(0xFFFFD700), Color(0xFFFFF59D), Color(0xFFFFD700))

    // Level up celebration gradients
    val levelUpGradient = listOf(Color(0xFF7C4DFF), Color(0xFF448AFF), Color(0xFF69F0AE))
    val xpBoostGradient = listOf(Color(0xFF66BB6A), Color(0xFFA5D6A7))
}

// =============================================================================
// LEADERBOARD PODIUM COLORS - Distinct Position Indicators
// =============================================================================

val LeaderboardFirst = Color(0xFFFFD700)       // Gold for 1st place
val LeaderboardSecond = Color(0xFFC0C0C0)      // Silver for 2nd place
val LeaderboardThird = Color(0xFFCD7F32)       // Bronze for 3rd place
val LeaderboardTop10 = Color(0xFF64B5F6)       // Blue highlight for top 10
val LeaderboardUser = Color(0xFF81C784)        // Green highlight for current user

// Podium glow effects (with alpha for ambient lighting)
val LeaderboardFirstGlow = Color(0x66FFD700)
val LeaderboardSecondGlow = Color(0x66C0C0C0)
val LeaderboardThirdGlow = Color(0x66CD7F32)

// =============================================================================
// RARITY GLOW COLORS - Achievement Badge Ambient Effects
// =============================================================================

val RarityCommonGlow = Color(0x3378909C)       // Subtle gray glow
val RarityUncommonGlow = Color(0x3366BB6A)     // Soft green glow
val RarityRareGlow = Color(0x3342A5F5)         // Blue glow
val RarityEpicGlow = Color(0x33AB47BC)         // Purple glow
val RarityLegendaryGlow = Color(0x66D4AF37)    // Strong gold glow
val RarityMythicGlow = Color(0x80FFD700)       // Intense golden glow

// =============================================================================
// STREAK INTENSITY COLORS - Visual Progression
// =============================================================================

// Streak colors based on count (gradient from cold to blazing)
val StreakCold = Color(0xFF90CAF9)             // 1-3 days - just starting
val StreakWarm = Color(0xFFFFB74D)             // 4-6 days - building momentum
val StreakHot = Color(0xFFFF7043)              // 7-13 days - on fire
val StreakBlazing = Color(0xFFE53935)          // 14-29 days - incredible
val StreakInferno = Color(0xFFD500F9)          // 30+ days - legendary

// Streak milestone colors
val StreakWeekMilestone = Color(0xFFFF8C42)    // 7-day milestone
val StreakMonthMilestone = Color(0xFFE65C2C)   // 30-day milestone
val StreakQuarterMilestone = Color(0xFFD43B3B) // 90-day milestone
val StreakYearMilestone = Color(0xFFFFD700)    // 365-day milestone

// =============================================================================
// XP & LEVEL COLORS - Progress Visualization
// =============================================================================

val XpBarBackground = Color(0xFFE0E0E0)
val XpBarFill = Color(0xFF66BB6A)
val XpBarGlow = Color(0x6666BB6A)
val XpBarOverflow = Color(0xFFFFD700)          // When XP exceeds level requirement

val LevelBadgeBackground = Color(0xFF2D5A3D)
val LevelBadgeText = Color(0xFFFFFFFF)
val LevelUpGlow = Color(0x667C4DFF)

// =============================================================================
// EMOTION SPECTRUM COLORS - Journal Mood Tracking
// =============================================================================

// Primary emotions with psychological color associations
val EmotionJoy = Color(0xFFFFC93C)             // Warm yellow - happiness, optimism
val EmotionPeace = Color(0xFF81D4FA)           // Light blue - calm, serenity
val EmotionGratitude = Color(0xFFFF8A65)       // Warm coral - appreciation, warmth
val EmotionLove = Color(0xFFFF6B6B)            // Soft red - affection, connection
val EmotionHope = Color(0xFF69F0AE)            // Mint green - optimism, possibility
val EmotionCuriosity = Color(0xFFFFD54F)       // Amber - exploration, interest

// Challenging emotions (with softer, supportive tones)
val EmotionAnxiety = Color(0xFFCE93D8)         // Soft purple - gentle acknowledgment
val EmotionSadness = Color(0xFF90A4AE)         // Muted blue-grey - understanding
val EmotionAnger = Color(0xFFEF5350)           // Red - intensity, passion
val EmotionFear = Color(0xFF7E57C2)            // Deep purple - depth, mystery
val EmotionFrustration = Color(0xFFFFAB91)     // Peach - warm acknowledgment
val EmotionConfusion = Color(0xFFFFCC80)       // Light orange - seeking clarity

// Neutral/transitional emotions
val EmotionNeutral = Color(0xFFB0BEC5)         // Balanced gray
val EmotionReflective = Color(0xFFB4A7D6)      // Soft lavender - contemplation
val EmotionFocused = Color(0xFF4FC3F7)         // Clear blue - concentration
val EmotionDetermined = Color(0xFFFF7043)      // Deep orange - resolve

// =============================================================================
// WISDOM CONTENT COLORS - Buddha's Wisdom Cards
// =============================================================================

val WisdomCardBackground = Color(0xFFFFFBF0)   // Warm parchment-like background
val WisdomCardBackgroundDark = Color(0xFF2A2520) // Dark theme parchment
val WisdomQuoteText = Color(0xFF3E2723)        // Deep brown for wisdom text
val WisdomQuoteTextDark = Color(0xFFEFE8DC)    // Light warm text for dark mode
val WisdomAuthor = Color(0xFF5D4037)           // Brown for attribution
val WisdomAuthorDark = Color(0xFFBCAAA4)       // Light brown for dark mode

// Wisdom category colors
val WisdomStoic = Color(0xFF5C6BC0)            // Indigo - stoic philosophy
val WisdomGrowth = Color(0xFF66BB6A)           // Green - personal growth
val WisdomMindfulness = Color(0xFF26A69A)      // Teal - presence, awareness
val WisdomResilience = Color(0xFFEF6C00)       // Orange - strength, endurance
val WisdomGratitude = Color(0xFFFFB300)        // Amber - appreciation
val WisdomPerspective = Color(0xFF7E57C2)      // Purple - expanded viewpoint

// =============================================================================
// CHALLENGE COLORS - Community Challenges
// =============================================================================

val ChallengeActive = Color(0xFF4CAF50)        // Green - in progress
val ChallengeCompleted = Color(0xFFFFD700)     // Gold - achieved
val ChallengePending = Color(0xFFFFB300)       // Amber - upcoming
val ChallengeExpired = Color(0xFF9E9E9E)       // Gray - missed

val ChallengeMilestoneReached = Color(0xFF66BB6A)
val ChallengeMilestonePending = Color(0xFFE0E0E0)

// =============================================================================
// FUTURE MESSAGE COLORS - Time Capsule UI
// =============================================================================

val FutureMessageScheduled = Color(0xFF5C6BC0)  // Indigo - waiting
val FutureMessageArrived = Color(0xFFFFD700)    // Gold - delivered
val FutureMessageRead = Color(0xFF66BB6A)       // Green - opened

// Category colors for future messages
val FutureGoal = Color(0xFF66BB6A)              // Green - goals
val FutureMotivation = Color(0xFFFF7043)        // Orange - motivation
val FuturePromise = Color(0xFF5C6BC0)           // Indigo - commitments
val FutureGeneral = Color(0xFF90A4AE)           // Gray - general thoughts
    // Rarity-specific gradients
    val epicGradient = listOf(RarityEpic, Color(0xFFE040FB))
    val legendaryGradient = listOf(RarityLegendary, Color(0xFFF4D03F))
    val mythicGradient = listOf(RarityMythic, Color(0xFFFFF59D), RarityMythic)

    // XP and level gradients
    val xpGradient = listOf(XpBarFill, Color(0xFF81C784))
    val levelUpGradient = listOf(Color(0xFFFFD700), Color(0xFFFFE57F), Color(0xFFFFD700))

    // Streak intensity gradients
    val streakIntensityGradient = listOf(StreakCold, StreakWarm, StreakHot, StreakBlazing)

    // Leaderboard gradients
    val leaderboardGoldGradient = listOf(Color(0xFFFFD700), Color(0xFFFFF176))
    val leaderboardSilverGradient = listOf(Color(0xFFC0C0C0), Color(0xFFE0E0E0))
    val leaderboardBronzeGradient = listOf(Color(0xFFCD7F32), Color(0xFFD4A574))
}

// =============================================================================
// BRAND COLORS - Prody Identity
// =============================================================================

// Primary brand identity colors (aliases for consistent naming)
val ProdyGreen = ProdyPrimary
val ProdyGreenLight = ProdyTertiary
val ProdyGreenDark = ProdyPrimaryVariant

// Achievement/reward gold
val ProdyGold = Color(0xFFFFB300)
val ProdyGoldLight = Color(0xFFFFE54C)
val ProdyGoldDark = Color(0xFFC68400)

// =============================================================================
// SEMANTIC SUCCESS/WARNING/ERROR CONTAINERS
// =============================================================================

val SuccessGreenContainer = Color(0xFFE8F5E9)
val WarningAmberContainer = Color(0xFFFFF8E1)
val ErrorRedContainer = Color(0xFFFFEBEE)
val InfoBlueContainer = Color(0xFFE3F2FD)
