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
val MoodCalm = Color(0xFF7EC8E3)         // Serene sky blue
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

// =============================================================================
// ENHANCED STREAK COLORS - Fire & Energy
// =============================================================================

val StreakEmber = Color(0xFFFFC371)      // Warm ember glow

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
}
