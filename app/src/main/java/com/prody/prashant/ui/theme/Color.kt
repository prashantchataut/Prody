package com.prody.prashant.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Prody Design System - Premium Color Palette
 *
 * A radical redesign for Prody's UI/UX overhaul - Phase 2
 *
 * Design Philosophy:
 * - Extreme minimalism, cleanliness, and modernity
 * - NO shadows, gradients, or hi-fi elements
 * - Flat, modern, compact, extremely clean aesthetic
 *
 * Color Palette (Strict Adherence):
 * - Dark Mode Background: Deep rich dark teal/green (#0D2826)
 * - Light Mode Background: Clean bright off-white (#F0F4F3)
 * - Accent: Vibrant neon green (#36F97F) for interactive elements
 * - Pure black (#000000) for focal screens (Time Capsule, Stats Leaderboard)
 * - Pure white (#FFFFFF) for focal screens in light mode
 *
 * WCAG AA Compliance: All color combinations meet contrast requirements
 */

// =============================================================================
// PREMIUM ACCENT COLOR - Vibrant Neon Green
// =============================================================================

/** Primary interactive accent - Vibrant neon green for buttons, links, active states */
val ProdyAccent = Color(0xFF36F97F)
val ProdyAccentVariant = Color(0xFF2EE06F)
val ProdyOnAccent = Color(0xFF000000)  // Black text on neon green

// =============================================================================
// LIGHT THEME COLORS - Clean Minimalist Palette
// =============================================================================

// Primary - Vibrant Neon Green accent for interactivity
val ProdyPrimary = Color(0xFF36F97F)  // Vibrant neon green accent
val ProdyPrimaryVariant = Color(0xFF2DD96D)  // Slightly darker variant
val ProdyOnPrimary = Color(0xFF000000)  // Black text on green
val ProdyPrimaryContainer = Color(0xFFD9FDE6)  // Very light green container

// Secondary - Subtle gray tones for secondary elements
val ProdySecondary = Color(0xFF6C757D)  // Medium gray
val ProdySecondaryVariant = Color(0xFF495057)  // Darker gray
val ProdyOnSecondary = Color(0xFFFFFFFF)

// Tertiary - Soft teal for accents
val ProdyTertiary = Color(0xFF20B2AA)  // Light sea green
val ProdyOnTertiary = Color(0xFFFFFFFF)
val ProdyTertiaryContainer = Color(0xFFE0F7F7)

// Background & Surface - Clean off-white
val ProdyBackground = Color(0xFFF0F4F3)  // Clean bright off-white/pale gray
val ProdyOnBackground = Color(0xFF1A1A1A)  // Near-black for primary text

val ProdySurface = Color(0xFFFFFFFF)  // Pure white for cards/surfaces
val ProdySurfaceVariant = Color(0xFFF5F7F6)  // Slightly darker surface variant
val ProdyOnSurface = Color(0xFF1A1A1A)  // Near-black for primary text
val ProdyOnSurfaceVariant = Color(0xFF6C757D)  // Medium gray for secondary text

// Elevated surfaces - minimal differentiation (flat design)
val ProdySurfaceElevated = Color(0xFFFFFFFF)  // Pure white
val ProdySurfaceDim = Color(0xFFE8ECEB)  // Slightly dimmed surface

// Error states
val ProdyError = Color(0xFFDC3545)  // Clean red
val ProdyOnError = Color(0xFFFFFFFF)
val ProdyErrorContainer = Color(0xFFFCE4E4)

// Success state
val ProdySuccess = Color(0xFF36F97F)  // Match accent green
val ProdyOnSuccess = Color(0xFF000000)

// Warning state
val ProdyWarning = Color(0xFFFFC107)  // Amber warning
val ProdyOnWarning = Color(0xFF1A1A1A)

// Outlines & Dividers - Subtle grays
val ProdyOutline = Color(0xFFDEE2E6)  // Light gray outline
val ProdyOutlineVariant = Color(0xFFCED4DA)  // Slightly darker outline

// =============================================================================
// DARK THEME COLORS - Deep Teal/Green Premium Palette
// =============================================================================

// Primary Dark - Vibrant Neon Green (same accent)
val ProdyPrimaryDark = Color(0xFF36F97F)  // Vibrant neon green accent
val ProdyPrimaryVariantDark = Color(0xFF4AFA8E)  // Lighter variant for dark mode
val ProdyOnPrimaryDark = Color(0xFF000000)  // Black text on green
val ProdyPrimaryContainerDark = Color(0xFF1A4033)  // Dark green container

// Secondary Dark - Light gray tones
val ProdySecondaryDark = Color(0xFFD3D8D7)  // Subtle light gray
val ProdyOnSecondaryDark = Color(0xFF1A1A1A)
val ProdySecondaryContainerDark = Color(0xFF2A4240)  // Dark muted green container

// Tertiary Dark - Soft teal
val ProdyTertiaryDark = Color(0xFF5FCFCF)  // Bright teal
val ProdyOnTertiaryDark = Color(0xFF003737)
val ProdyTertiaryContainerDark = Color(0xFF1A3331)

// Background & Surface Dark - Deep rich teal/green
val ProdyBackgroundDark = Color(0xFF0D2826)  // Deep rich dark teal/green
val ProdyOnBackgroundDark = Color(0xFFFFFFFF)  // Pure white for primary text

val ProdySurfaceDark = Color(0xFF1A3331)  // Slightly elevated surface
val ProdySurfaceVariantDark = Color(0xFF2A4240)  // Card/container background
val ProdyOnSurfaceDark = Color(0xFFFFFFFF)  // Pure white for primary text
val ProdyOnSurfaceVariantDark = Color(0xFFD3D8D7)  // Subtle gray for secondary text

// Elevated surfaces for cards in dark mode
val ProdySurfaceElevatedDark = Color(0xFF2A4240)  // Elevated dark surface
val ProdySurfaceDimDark = Color(0xFF0A1F1D)  // Deeper than background

// Error Dark
val ProdyErrorDark = Color(0xFFFF6B6B)  // Bright red for visibility
val ProdyOnErrorDark = Color(0xFF1A1A1A)
val ProdyErrorContainerDark = Color(0xFF4A1F1F)

// Outlines Dark
val ProdyOutlineDark = Color(0xFF3A5250)  // Subtle dark outline
val ProdyOutlineVariantDark = Color(0xFF4A625F)  // Slightly lighter outline

// =============================================================================
// PURE BLACK/WHITE - For Focal Screens
// =============================================================================

/** Pure black for immersive focal screens (Time Capsule, Stats Leaderboard) */
val ProdyPureBlack = Color(0xFF000000)
/** Pure white for focal screens in light mode */
val ProdyPureWhite = Color(0xFFFFFFFF)

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

// Interactive states - using vibrant neon green accent
val InteractiveHover = Color(0xFF36F97F).copy(alpha = 0.08f)
val InteractivePressed = Color(0xFF36F97F).copy(alpha = 0.16f)
val InteractiveFocus = Color(0xFF36F97F).copy(alpha = 0.24f)

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
// XP & PROGRESS BAR COLORS - Neon Green Accent
// =============================================================================

val XpBarBackground = Color(0xFFE8ECEB)       // Light mode bar background
val XpBarBackgroundDark = Color(0xFF2A4240)   // Dark mode bar background
val XpBarFill = Color(0xFF36F97F)             // Vibrant neon green fill
val XpBarGlow = Color(0x6636F97F)             // Glow effect
val XpBarFillAlt = Color(0xFF2EE06F)          // Alternative fill
val LevelUpGlow = Color(0xFF36F97F)           // Level up celebration glow

// =============================================================================
// STATUS COLORS - Clear Communication
// =============================================================================

val SuccessColor = Color(0xFF36F97F)          // Match accent green
val WarningColor = Color(0xFFFFC107)          // Amber warning
val ErrorColor = Color(0xFFDC3545)            // Clean red
val InfoColor = Color(0xFF17A2B8)             // Teal info

// =============================================================================
// TEXT HIERARCHY COLORS - Enhanced Readability (WCAG AA Compliant)
// =============================================================================

// Light mode text colors (on #F0F4F3 background)
val TextPrimaryLight = Color(0xFF1A1A1A)      // Near-black for primary text
val TextSecondaryLight = Color(0xFF6C757D)    // Medium gray for secondary text
val TextTertiaryLight = Color(0xFF9CA3AF)     // Light gray for tertiary/hints

// Dark mode text colors (on #0D2826 background)
val TextPrimaryDark = Color(0xFFFFFFFF)       // Pure white for primary text
val TextSecondaryDark = Color(0xFFD3D8D7)     // Subtle light gray for secondary
val TextTertiaryDark = Color(0xFF9CA3AF)      // Muted gray for tertiary/hints

// =============================================================================
// SURFACE COLORS - Premium Flat Design System (No Shadows)
// =============================================================================

// Light mode surfaces
val SurfaceLight = Color(0xFFF0F4F3)           // Main background
val SurfaceMedium = Color(0xFFF5F7F6)          // Cards and containers
val SurfaceWhite = Color(0xFFFFFFFF)           // Pure white for focal elements

// Dark mode surfaces (deep teal/green palette)
val SurfaceDark = Color(0xFF0D2826)            // Main background
val SurfaceDarkElevated = Color(0xFF1A3331)    // Cards and containers
val SurfaceDarkMuted = Color(0xFF2A4240)       // Elevated containers

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
val LeaderboardTop10Highlight = Color(0xFF64B5F6) // Renamed to avoid conflict with LeaderboardTop10
val LeaderboardUser = Color(0xFF81C784)        // Green highlight for current user

// Podium glow effects (with alpha for ambient lighting)
val LeaderboardFirstGlow = Color(0x66FFD700)
val LeaderboardSecondGlow = Color(0x66C0C0C0)
val LeaderboardThirdGlow = Color(0x66CD7F32)

// =============================================================================
// STREAK INTENSITY COLORS - Visual Progression
// =============================================================================

// Streak colors based on count (gradient from cold to blazing)
// Note: StreakCold, StreakWarm, StreakHot, StreakBlazing are defined earlier in the file

val StreakInferno = Color(0xFFD500F9)          // 30+ days - legendary

// Streak milestone colors
val StreakWeekMilestone = Color(0xFFFF8C42)    // 7-day milestone
val StreakMonthMilestone = Color(0xFFE65C2C)   // 30-day milestone
val StreakQuarterMilestone = Color(0xFFD43B3B) // 90-day milestone
val StreakYearMilestone = Color(0xFFFFD700)    // 365-day milestone

// =============================================================================
// XP & LEVEL COLORS - Progress Visualization
// =============================================================================

// Note: XpBarBackground, XpBarFill, XpBarGlow, XpBarFillAlt are defined earlier.
// Wait, XpBarFillAlt is defined earlier (line 250). XpBarFill (248).
// So I should validly REMOVE these duplicates from this bottom block I am pasting!
// XpBarOverflow was NOT defined earlier (snippet 229 showed XpBarOverflow in bottom only?)
// Let's check line 248 in step 310.
// 248: val XpBarFill = Color(0xFF66BB6A)
// 249: val XpBarGlow = Color(0x6666BB6A)
// 250: val XpBarFillAlt = Color(0xFF4CAF50)
// 251: val LevelUpGlow = Color(0xFFFFD700)

// So I should REMOVE XpBarFill, XpBarGlow, XpBarFillAlt, LevelUpGlow from my New Block.
// I should KEEP XpBarOverflow.

val XpBarOverflow = Color(0xFFFFD700)          // When XP exceeds level requirement

val LevelBadgeBackground = Color(0xFF2D5A3D)
val LevelBadgeText = Color(0xFFFFFFFF)
// LevelUpGlow is defined at 251.

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
// EmotionNeutral defined at 203? "val MoodNeutral". Not EmotionNeutral.
// So keeping EmotionNeutral.
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

val ChallengeActive = Color(0xFF36F97F)        // Neon green - in progress
val ChallengeCompleted = Color(0xFFFFD700)     // Gold - achieved
val ChallengePending = Color(0xFFFFB300)       // Amber - upcoming
val ChallengeExpired = Color(0xFF9E9E9E)       // Gray - missed

val ChallengeMilestoneReached = Color(0xFF36F97F)   // Neon green
val ChallengeMilestonePending = Color(0xFFE8ECEB)   // Light gray

// =============================================================================
// FUTURE MESSAGE COLORS - Time Capsule UI
// =============================================================================

val FutureMessageScheduled = Color(0xFF5C6BC0)  // Indigo - waiting
val FutureMessageArrived = Color(0xFFFFD700)    // Gold - delivered
val FutureMessageRead = Color(0xFF36F97F)       // Neon green - opened

// Category colors for future messages
val FutureGoal = Color(0xFF36F97F)              // Neon green - goals
val FutureMotivation = Color(0xFFFF7043)        // Orange - motivation
val FuturePromise = Color(0xFF5C6BC0)           // Indigo - commitments
val FutureGeneral = Color(0xFF90A4AE)           // Gray - general thoughts

// =============================================================================
// TIME CAPSULE WRITE SCREEN - Clean Minimalist Design System
// =============================================================================

// Vibrant Neon Green Accent (shared between light and dark modes)
val TimeCapsuleAccent = Color(0xFF36F97F)       // Vibrant neon green accent

// Light Mode Colors
val TimeCapsuleBackgroundLight = Color(0xFFFFFFFF)      // Pure white background
val TimeCapsuleTitleTextLight = Color(0xFF212529)       // Very dark gray for header title
val TimeCapsuleDiscardTextLight = Color(0xFF212529)     // Very dark gray for discard
val TimeCapsulePlaceholderLight = Color(0xFFADB5BD)     // Subtle light gray for placeholders
val TimeCapsuleActiveTextLight = Color(0xFF212529)      // Very dark gray for active input text
val TimeCapsuleMultimediaIconLight = Color(0xFF6C757D)  // Medium-dark gray for camera/mic icons
val TimeCapsuleAttachTextLight = Color(0xFF6C757D)      // Medium-dark gray for "Attach memories"
val TimeCapsuleDividerLight = Color(0xFFE0E7E6)         // Very light gray for dividers
val TimeCapsuleSectionTitleLight = Color(0xFF212529)    // Very dark gray for section titles
val TimeCapsuleInactiveTagBgLight = Color(0xFFF0F3F2)   // Very light gray inactive tag bg
val TimeCapsuleInactiveTagTextLight = Color(0xFF212529) // Very dark gray inactive tag text
val TimeCapsuleButtonTextLight = Color(0xFF000000)      // Pure black for button text

// Time Capsule List Screen Colors - Light Mode
val TimeCapsuleTextPrimaryLight = Color(0xFF212529)     // Primary text color (dark gray)
val TimeCapsuleTextSecondaryLight = Color(0xFF6C757D)   // Secondary text color (medium gray)
val TimeCapsuleIconLight = Color(0xFF212529)            // Icon color (dark gray)
val TimeCapsuleTabContainerLight = Color(0xFFF0F3F2)    // Tab container background
val TimeCapsuleActiveTabTextLight = Color(0xFF000000)   // Active tab text (black on accent)
val TimeCapsuleEmptyCircleBgLight = Color(0xFFF0F3F2)   // Empty state inner circle background
val TimeCapsuleDashedCircleLight = Color(0xFFE0E7E6)    // Dashed circle color

// Dark Mode Colors
val TimeCapsuleBackgroundDark = Color(0xFF0D2826)       // Deep dark teal/green background
val TimeCapsuleTitleTextDark = Color(0xFFFFFFFF)        // Pure white for header title
val TimeCapsuleDiscardTextDark = Color(0xFFD3D8D7)      // Light subtle gray for discard
val TimeCapsulePlaceholderDark = Color(0xFF8A9493)      // Subtle light gray for placeholders
val TimeCapsuleActiveTextDark = Color(0xFFFFFFFF)       // Pure white for active input text
val TimeCapsuleMultimediaIconDark = Color(0xFFFFFFFF)   // Pure white for camera/mic icons
val TimeCapsuleAttachTextDark = Color(0xFFD3D8D7)       // Light subtle gray for "Attach memories"
val TimeCapsuleDividerDark = Color(0xFF404040)          // Very dark gray for dividers
val TimeCapsuleSectionTitleDark = Color(0xFFFFFFFF)     // Pure white for section titles
val TimeCapsuleInactiveTagBgDark = Color(0xFF2A4240)    // Dark muted green inactive tag bg
val TimeCapsuleInactiveTagTextDark = Color(0xFFD3D8D7)  // Light subtle gray inactive tag text
val TimeCapsuleButtonTextDark = Color(0xFF000000)       // Pure black for button text

// Time Capsule List Screen Colors - Dark Mode
val TimeCapsuleTextPrimaryDark = Color(0xFFFFFFFF)      // Primary text color (white)
val TimeCapsuleTextSecondaryDark = Color(0xFFD3D8D7)    // Secondary text color (light gray)
val TimeCapsuleIconDark = Color(0xFFFFFFFF)             // Icon color (white)
val TimeCapsuleTabContainerDark = Color(0xFF1A3331)     // Tab container background
val TimeCapsuleActiveTabTextDark = Color(0xFF000000)    // Active tab text (black on accent)
val TimeCapsuleEmptyCircleBgDark = Color(0xFF1A3331)    // Empty state inner circle background
val TimeCapsuleDashedCircleDark = Color(0xFF3A5250)     // Dashed circle color

//Brand/Containers were redundant I believe?
//Step 310 lines 262 and 284 showed Text/Status/Notification.
//What about Brand ProdyGreen etc?
//Line 23ish (in snippet 1 of 306) showed ProdyPrimary etc.
//Line 480 (in snippet of 306) showed ProdyGreen aliases?
//Let's check if ProdyGreen is in 1-299.
//Scan 1-299 content in step 310.
//ProdyPrimary is there. ProdyGreen is NOT in 1-299 (I don't see aliases).
//So I KEEP Brand Colors.

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

// =============================================================================
// ONBOARDING DESIGN SYSTEM COLORS - Unified with New Design System
// =============================================================================

// Primary Accent - Vibrant Neon Green (buttons, progress indicators, highlights)
val OnboardingAccent = Color(0xFF36F97F)         // Match primary accent
val OnboardingAccentDark = Color(0xFF2EE06F)     // Slightly darker variant
val OnboardingAccentLight = Color(0xFF5AFFA0)    // Lighter variant

// Light Mode Colors - Clean off-white background
val OnboardingBackgroundLight = Color(0xFFF0F4F3)         // Match new light background
val OnboardingSurfaceLight = Color(0xFFFFFFFF)            // Pure white surfaces
val OnboardingSurfaceVariantLight = Color(0xFFF5F7F6)     // Slightly darker variant
val OnboardingCardLight = Color(0xFFFFFFFF)               // White cards
val OnboardingTextPrimaryLight = Color(0xFF1A1A1A)        // Near-black primary text
val OnboardingTextSecondaryLight = Color(0xFF6C757D)      // Medium gray secondary text
val OnboardingTextTertiaryLight = Color(0xFF9CA3AF)       // Light gray tertiary
val OnboardingIconContainerLight = Color(0xFFE6FFF0)      // Light green tint for icons
val OnboardingDividerLight = Color(0xFFDEE2E6)            // Subtle dividers

// Dark Mode Colors - Deep Teal/Green Theme (Match new dark background)
val OnboardingBackgroundDark = Color(0xFF0D2826)          // Match new dark background
val OnboardingSurfaceDark = Color(0xFF1A3331)             // Elevated surface
val OnboardingSurfaceVariantDark = Color(0xFF2A4240)      // Card/container background
val OnboardingCardDark = Color(0xFF2A4240)                // Cards
val OnboardingTextPrimaryDark = Color(0xFFFFFFFF)         // Pure white primary text
val OnboardingTextSecondaryDark = Color(0xFFD3D8D7)       // Subtle gray secondary text
val OnboardingTextTertiaryDark = Color(0xFF9CA3AF)        // Muted gray tertiary
val OnboardingIconContainerDark = Color(0xFF1A4033)       // Dark green container for icons
val OnboardingDividerDark = Color(0xFF3A5250)             // Dark dividers

// Button Colors - Neon green accent
val OnboardingButtonPrimary = Color(0xFF36F97F)           // Neon green
val OnboardingButtonPrimaryDark = Color(0xFF000000)       // Black text on button
val OnboardingButtonTextLight = Color(0xFF000000)         // Black text on green
val OnboardingButtonTextDark = Color(0xFF36F97F)          // Green text for secondary buttons

// Quote Card Colors
val OnboardingQuoteCardLight = Color(0xFFFFFFFF)
val OnboardingQuoteCardDark = Color(0xFF2A4240)
val OnboardingQuoteHighlight = Color(0xFF36F97F)          // Neon green highlight

// Progress Indicator Colors
val OnboardingProgressActive = Color(0xFF36F97F)          // Neon green active
val OnboardingProgressInactiveLight = Color(0xFFDEE2E6)   // Light gray inactive
val OnboardingProgressInactiveDark = Color(0xFF3A5250)    // Dark gray inactive

// XP Arc Colors
val OnboardingXpArcBackground = Color(0xFFE8ECEB)         // Light mode arc track
val OnboardingXpArcBackgroundDark = Color(0xFF2A4240)     // Dark mode arc track
val OnboardingXpArcFill = Color(0xFF36F97F)               // Neon green fill
val OnboardingXpArcGlow = Color(0x4D36F97F)               // Subtle green glow

// Stat Card Colors
val OnboardingStatCardLight = Color(0xFFF5F7F6)
val OnboardingStatCardDark = Color(0xFF2A4240)
val OnboardingStatCardLockedLight = Color(0xFFE8ECEB)
val OnboardingStatCardLockedDark = Color(0xFF1A3331)

// Feature List Colors
val OnboardingFeatureIconBgLight = Color(0xFFE6FFF0)      // Light green tint
val OnboardingFeatureIconBgDark = Color(0xFF1A4033)       // Dark green

// Leaderboard Colors
val OnboardingLeaderboardRowLight = Color(0xFFFFFFFF)
val OnboardingLeaderboardRowDark = Color(0xFF2A4240)
val OnboardingLeaderboardRowActiveLight = Color(0xFFE6FFF0)   // Light green tint for active
val OnboardingLeaderboardRowActiveDark = Color(0xFF1A4033)    // Dark green for active

// =============================================================================
// JOURNAL NEW ENTRY DESIGN SYSTEM - Clean Minimalist Theme (Unified)
// =============================================================================

// Light Mode Colors - Clean off-white background
val JournalBackgroundLight = Color(0xFFF0F4F3)          // Match new light background
val JournalSurfaceLight = Color(0xFFFFFFFF)             // Pure white for cards/containers
val JournalPrimaryTextLight = Color(0xFF1A1A1A)         // Near-black for primary text
val JournalSecondaryTextLight = Color(0xFF6C757D)       // Medium gray for secondary text
val JournalPlaceholderTextLight = Color(0xFF9CA3AF)     // Subtle gray for placeholders
val JournalSliderInactiveLight = Color(0xFFDEE2E6)      // Light gray for inactive slider track
val JournalSaveButtonBgLight = Color(0xFFE6FFF0)        // Very light green for save button background
val JournalIconCircleBorderLight = Color(0xFFDEE2E6)    // Subtle light gray border for icon circles
val JournalCardCornerDetailLight = Color(0xFFF5F7F6)    // Very light gray for corner decorations

// Dark Mode Colors - Deep teal/green background
val JournalBackgroundDark = Color(0xFF0D2826)           // Match new dark background
val JournalSurfaceDark = Color(0xFF2A4240)              // Slightly lighter muted green for cards
val JournalPrimaryTextDark = Color(0xFFFFFFFF)          // Pure white for primary text
val JournalSecondaryTextDark = Color(0xFFD3D8D7)        // Light subtle gray for descriptions
val JournalPlaceholderTextDark = Color(0xFF9CA3AF)      // Subtle gray for placeholders
val JournalSliderInactiveDark = Color(0xFF3A5250)       // Dark gray for inactive slider track
val JournalSaveButtonBgDark = Color(0xFF1A3331)         // Very dark muted green for save button background
val JournalIconCircleBorderDark = Color(0xFF3A5250)     // Subtle darker border for icon circles
val JournalCardCornerDetailDark = Color(0xFF2A4240)     // Darker for corner decorations

// Accent Green - Vibrant Neon Green (same for both modes)
val JournalAccentGreen = Color(0xFF36F97F)              // Vibrant neon green accent

// =============================================================================
// JOURNEY ONBOARDING DESIGN SYSTEM - Premium Stoic Theme
// =============================================================================

// Background Colors - Minimalist & Premium
val JourneyBackgroundDark = Color(0xFF121212)    // Deep black for dark mode
val JourneyBackgroundLight = Color(0xFFF5F2E9)   // Warm cream for light mode

// Primary Brand Color - Deep Forest Green (Stoic, grounded)
val JourneyPrimary = Color(0xFF1A3C34)
val JourneyPrimaryVariant = Color(0xFF0E2420)
val JourneyOnPrimary = Color(0xFFFFFFFF)

// Accent Color - Muted Gold (Premium, wisdom)
val JourneyAccent = Color(0xFFC5A059)
val JourneyAccentDark = Color(0xFFB08A3A)
val JourneyAccentLight = Color(0xFFD4B978)

// Surface Colors
val JourneySurfaceLight = Color(0xFFFFFBF5)      // Warm white
val JourneySurfaceDark = Color(0xFF1A1A1A)       // Slightly elevated from background
val JourneyCardLight = Color(0xFFFFF8EE)         // Cream/paper-like for contract
val JourneyCardDark = Color(0xFF2A2A2A)          // Dark grey for contract

// Text Colors
val JourneyTextPrimaryLight = Color(0xFF1A1A1A)
val JourneyTextPrimaryDark = Color(0xFFF5F2E9)
val JourneyTextSecondaryLight = Color(0xFF5A5A5A)
val JourneyTextSecondaryDark = Color(0xFFB0A89C)
val JourneyTextTertiaryLight = Color(0xFF8A8A8A)
val JourneyTextTertiaryDark = Color(0xFF6A6A6A)

// Button Colors
val JourneyButtonPrimary = Color(0xFF1A3C34)     // Deep green button
val JourneyButtonTextOnPrimary = Color(0xFFF5F2E9) // Cream text on green
val JourneyButtonSecondary = Color(0xFFC5A059)   // Gold secondary button

// Arc & Progress Colors
val JourneyArcBackground = Color(0xFFE8E5DD)     // Light mode arc track
val JourneyArcBackgroundDark = Color(0xFF2A2A2A) // Dark mode arc track
val JourneyArcFill = Color(0xFFC5A059)           // Gold fill for arc
val JourneyArcGlow = Color(0x4DC5A059)           // Subtle gold glow

// Stat Card Colors
val JourneyStatCardLight = Color(0xFFF5F2E9)
val JourneyStatCardDark = Color(0xFF1E1E1E)
val JourneyStatCardBorderLight = Color(0xFFE0DCD0)
val JourneyStatCardBorderDark = Color(0xFF3A3A3A)
val JourneyStatCardLockedLight = Color(0xFFF0EDE5)
val JourneyStatCardLockedDark = Color(0xFF1A1A1A)

// Icon Colors
val JourneyIconFire = Color(0xFFE67E22)          // Streak fire orange
val JourneyIconScroll = Color(0xFFC5A059)        // Wisdom scroll gold
val JourneyIconLocked = Color(0xFF6A6A6A)        // Locked padlock grey
val JourneyIconTrophy = Color(0xFFC5A059)        // Trophy gold

// Contract/Letter Card Colors
val JourneyContractPaperLight = Color(0xFFFFF8EE) // Cream paper
val JourneyContractPaperDark = Color(0xFF2A2520)  // Dark parchment
val JourneyContractTextLight = Color(0xFF2A2520)  // Brown ink
val JourneyContractTextDark = Color(0xFFE8E0D4)   // Light ink
val JourneyContractLineLight = Color(0xFFD4C4A8)  // Signature line
val JourneyContractLineDark = Color(0xFF5A5046)

// Seal Animation Colors
val JourneySealRed = Color(0xFF8B2323)            // Wax seal red
val JourneySealGold = Color(0xFFC5A059)           // Gold seal alternative
val JourneySealComplete = Color(0xFF2D5A3D)       // Green checkmark

// Page Indicator Colors
val JourneyIndicatorActive = Color(0xFFC5A059)    // Gold active dot
val JourneyIndicatorInactiveLight = Color(0xFFD4C4A8)
val JourneyIndicatorInactiveDark = Color(0xFF3A3A3A)

// =============================================================================
// JOURNAL HISTORY DESIGN SYSTEM - Premium Minimalist Theme
// =============================================================================

// Accent Color - Vibrant Neon Green (key interactive elements, highlights)
val JournalHistoryAccent = Color(0xFF36F97F)

// Dark Mode Colors - Chronological, Organized Minimalism
val JournalHistoryBackgroundDark = Color(0xFF000000)      // Pure black
val JournalHistoryCardDark = Color(0xFF212121)            // Dark gray with hint of green
val JournalHistoryTextPrimaryDark = Color(0xFFFFFFFF)     // Pure white
val JournalHistoryTextSecondaryDark = Color(0xFFD3D8D7)   // Subtle gray
val JournalHistoryIntensityBgDark = Color(0xFF404040)     // Intensity tag background
val JournalHistoryDividerDark = Color(0xFFFFFFFF)         // Section divider
val JournalHistoryDateBlockBgDark = Color(0xFFFFFFFF)     // White date block
val JournalHistoryDateBlockTextDark = Color(0xFF000000)   // Black text on date block
val JournalHistoryButtonBorderDark = Color(0xFFFFFFFF)    // Load more button border

// Light Mode Colors - Luminous, Accessible Variant
val JournalHistoryBackgroundLight = Color(0xFFFFFFFF)     // Pure white
val JournalHistoryCardLight = Color(0xFFF0F0F0)           // Very light gray
val JournalHistoryTextPrimaryLight = Color(0xFF1A1A1A)    // Near-black
val JournalHistoryTextSecondaryLight = Color(0xFF6C757D)  // Subtle dark gray
val JournalHistoryIntensityBgLight = Color(0xFFB0B0B0)    // Medium gray
val JournalHistoryDividerLight = Color(0xFF1A1A1A)        // Dark divider
val JournalHistoryDateBlockBgLight = Color(0xFFFFFFFF)    // White date block
val JournalHistoryDateBlockTextLight = Color(0xFF1A1A1A)  // Dark text on date block
val JournalHistoryButtonBorderLight = Color(0xFF1A1A1A)   // Dark button border

// Mood Colors for Journal History (matching the design spec)
val JournalHistoryMoodEcstatic = Color(0xFF36F97F)        // Vibrant neon green
val JournalHistoryMoodCalm = Color(0xFF7ED321)            // Muted light green
val JournalHistoryMoodAnxious = Color(0xFFF5A623)         // Orange
val JournalHistoryMoodMelancholy = Color(0xFF4A90E2)      // Light blue
