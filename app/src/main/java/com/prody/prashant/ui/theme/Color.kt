package com.prody.prashant.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Prody Design System - Color Palette (Revamp 2026)
 *
 * A polished, intuitive color system for the Prody mental wellness companion.
 *
 * Design Principles:
 * - Primary: Deep forest green (#2E7D32) for trust and growth
 * - Secondary: Warm amber (#FFA000) for encouragement and energy
 * - Background: Clean whites (#FFFFFF) and soft grays (#FAFAFA)
 * - Error states: Subtle red (#D32F2F)
 * - Success states: Muted green (#388E3C)
 */

// =============================================================================
// BRAND COLORS - Core Identity
// =============================================================================

val ProdyForestGreen = Color(0xFF2E7D32)
val ProdyWarmAmber = Color(0xFFFFA000)

val ProdyPrimary = ProdyForestGreen
val ProdySecondary = ProdyWarmAmber

// =============================================================================
// LIGHT THEME COLORS
// =============================================================================

val ProdyBackgroundLight = Color(0xFFFAFAFA)          // Soft gray
val ProdySurfaceLight = Color(0xFFFFFFFF)             // Clean white
val ProdySurfaceVariantLight = Color(0xFFF5F5F5)      // Slightly darker gray

val ProdyTextPrimaryLight = Color(0xFF1A1A1A)         // Near black
val ProdyTextSecondaryLight = Color(0xFF6C757D)       // Medium gray
val ProdyTextTertiaryLight = Color(0xFFA0A8AD)        // Light gray
val ProdyTextOnPrimaryLight = Color(0xFFFFFFFF)       // White on green

val ProdyOutlineLight = Color(0xFFE0E0E0)
val ProdyDividerLight = Color(0xFFEEEEEE)

// =============================================================================
// DARK THEME COLORS
// =============================================================================

// For Dark mode, we derive deep variants to maintain the "Forest" feel
val ProdyBackgroundDark = Color(0xFF121212)           // Standard Dark Background
val ProdySurfaceDark = Color(0xFF1E1E1E)              // Surface
val ProdySurfaceVariantDark = Color(0xFF2C2C2C)       // Surface Variant

val ProdyTextPrimaryDark = Color(0xFFFFFFFF)
val ProdyTextSecondaryDark = Color(0xFFB0B0B0)
val ProdyTextTertiaryDark = Color(0xFF808080)
val ProdyTextOnPrimaryDark = Color(0xFFFFFFFF)

val ProdyOutlineDark = Color(0xFF424242)
val ProdyDividerDark = Color(0xFF303030)

// =============================================================================
// SEMANTIC COLORS
// =============================================================================

val ProdyError = Color(0xFFD32F2F)                    // Subtle red
val ProdySuccess = Color(0xFF388E3C)                  // Muted green
val ProdyWarning = Color(0xFFFFA000)                  // Warm amber
val ProdyInfo = Color(0xFF1976D2)                     // Info Blue

val ProdyOnError = Color(0xFFFFFFFF)
val ProdyOnSuccess = Color(0xFFFFFFFF)
val ProdyOnWarning = Color(0xFF000000)
val ProdyOnInfo = Color(0xFFFFFFFF)

// Containers (Light)
val ProdyErrorContainer = Color(0xFFFFEBEE)
val ProdySuccessContainer = Color(0xFFE8F5E9)
val ProdyWarningContainer = Color(0xFFFFF8E1)
val ProdyInfoContainer = Color(0xFFE3F2FD)

// Containers (Dark)
val ProdyErrorContainerDark = Color(0xFF4A2525)
val ProdySuccessContainerDark = Color(0xFF1B3320)
val ProdyWarningContainerDark = Color(0xFF3E2723)
val ProdyInfoContainerDark = Color(0xFF0D47A1)

// =============================================================================
// COMPATIBILITY ALIASES (Mapping old names to new palette)
// =============================================================================

val ProdyAccentGreen = ProdyForestGreen
val ProdyAccentGreenLight = Color(0xFF60AD5E) // Light variant of forest green
val ProdyAccentGreenDark = Color(0xFF005005)  // Dark variant of forest green

val ProdyAccent = ProdyPrimary
val ProdyGreen = ProdyPrimary

// Keeping these for build compatibility, mapped to new palette or similar
val ProdySurface = ProdySurfaceLight
val ProdyBackground = ProdyBackgroundLight
val ProdyOnPrimary = ProdyTextOnPrimaryLight
val ProdyOnSecondary = Color(0xFF000000)
val ProdyPrimaryContainer = ProdySuccessContainer // Using success container as a proxy for primary container
val ProdyTertiary = ProdyTextSecondaryLight
val ProdyOnTertiary = Color(0xFFFFFFFF)
val ProdyTertiaryContainer = ProdySurfaceVariantLight

val ProdyPrimaryDark = ProdyForestGreen // Keep primary green in dark mode too? Or maybe a lighter variant?
// Material Design usually recommends lighter primary in dark mode.
// Let's use a slightly lighter Green for Dark Mode Primary if needed, or stick to brand.
// For now, sticking to brand.
val ProdyOnPrimaryDark = Color(0xFFFFFFFF)
val ProdyPrimaryContainerDark = ProdySuccessContainerDark
val ProdySecondaryDark = ProdyWarmAmber
val ProdyOnSecondaryDark = Color(0xFF000000)
val ProdySecondaryContainerDark = Color(0xFF3E2723)
val ProdyTertiaryDark = ProdyTextSecondaryDark
val ProdyOnTertiaryDark = Color(0xFF000000)
val ProdyTertiaryContainerDark = ProdySurfaceVariantDark


// Mood Colors (Keeping existing values or tweaking slightly to match "Clean" aesthetic)
val MoodHappy = Color(0xFFFFC107)
val MoodCalm = Color(0xFF4FC3F7)
val MoodAnxious = Color(0xFFFF8A65)
val MoodSad = Color(0xFF90A4AE)
val MoodMotivated = Color(0xFFFFD54F)
val MoodGrateful = Color(0xFFAED581)
val MoodConfused = Color(0xFF9575CD)
val MoodExcited = Color(0xFFFF7043)
val MoodEnergetic = Color(0xFFFFB74D)
val MoodInspired = Color(0xFF7986CB)
val MoodNostalgic = Color(0xFFA1887F)

// Haven specific
val HavenBackgroundLight = ProdyBackgroundLight
val HavenBubbleLight = Color(0xFFE8F5E9) // Very light green
val HavenUserBubbleLight = ProdySurfaceLight
val HavenTextLight = ProdyTextPrimaryLight

val HavenBackgroundDark = ProdyBackgroundDark
val HavenBubbleDark = Color(0xFF1B3320)
val HavenUserBubbleDark = ProdySurfaceDark
val HavenTextDark = ProdyTextPrimaryDark

// Scrim
val Scrim = Color(0x52000000)

// Leaderboard / Gamification
val LeaderboardGold = Color(0xFFFFD700)
val LeaderboardSilver = Color(0xFFC0C0C0)
val LeaderboardBronze = Color(0xFFCD7F32)

// Other compatibility
val StreakFire = Color(0xFFE65100)
val StreakWarm = Color(0xFFFF9800)
val StreakHot = Color(0xFFFF5722)

val ProdyOutline = ProdyOutlineLight
val ProdyOutlineVariant = ProdyDividerLight

val ProdyOutlineVariantDark = ProdyDividerDark

val ProdySurfaceElevated = ProdySurfaceLight
val ProdySurfaceDim = ProdySurfaceVariantLight
val ProdySurfaceElevatedDark = ProdySurfaceDark
val ProdySurfaceDimDark = ProdySurfaceVariantDark

val ProdyInverseSurface = Color(0xFF303030)
val ProdyInverseOnSurface = Color(0xFFF5F5F5)
val ProdyInversePrimary = Color(0xFF81C784)

// Bloom System
val BloomReady = ProdyForestGreen
val BloomGrowing = Color(0xFF8BC34A)
val SeedDormant = Color(0xFFBDBDBD)

// Interactive
val InteractiveHoverLight = Color(0x0A000000)
val InteractivePressedLight = Color(0x1F000000)
val InteractiveHoverDark = Color(0x0AFFFFFF)
val InteractivePressedDark = Color(0x1FFFFFFF)

val InteractiveFocus = ProdyForestGreen.copy(alpha = 0.5f)

// Wrapped Gradients (keeping mostly same but referencing new colors where possible)
// ... (Keeping complex gradient objects as is is usually safer unless requested to change)
// But I'll define the base colors used in them if they were missing.
val WrappedPurple1 = Color(0xFF6B5CE7)
val WrappedPurple2 = Color(0xFF8B7EF0)
val WrappedPink1 = Color(0xFFE91E63)
val WrappedPink2 = Color(0xFFF06292)

// Deprecated Aliases (Map to new system)
val SuccessGreen = ProdySuccess
val ErrorRed = ProdyError
val WarningAmber = ProdyWarning
val InfoBlue = ProdyInfo

// Profile
val ProfileAvatarRing = ProdyForestGreen

// Future Message
val FutureCategoryGoal = ProdyForestGreen
val FutureCategoryMotivation = ProdyWarmAmber

// Journal
val JournalAccent = ProdyForestGreen

// Missing symbols needed for build
val ProdySurfaceContainerDark = ProdySurfaceVariantDark
val ProdySurfaceContainerLight = ProdySurfaceVariantLight
val ProdyPremiumViolet = Color(0xFF673AB7)
val ProdyPremiumVioletContainer = Color(0xFFD1C4E9)

val RarityCommon = Color(0xFF9E9E9E)
val RarityUncommon = Color(0xFF4CAF50)
val RarityRare = Color(0xFF2196F3)
val RarityEpic = Color(0xFF9C27B0)
val RarityLegendary = Color(0xFFFF9800)
val RarityMythic = Color(0xFFFF5722)

val LeaderboardGoldLight = Color(0xFFFFD700)
val LeaderboardGoldDark = Color(0xFFCFB53B)
val LeaderboardSilverLight = Color(0xFFC0C0C0)
val LeaderboardSilverDark = Color(0xFFA9A9A9)
val LeaderboardBronzeLight = Color(0xFFCD7F32)
val LeaderboardBronzeDark = Color(0xFF8B4513)

val ActivityPulseBackground = ProdyPrimary.copy(alpha = 0.1f)
val ActivityPulseBackgroundLight = ProdyPrimary.copy(alpha = 0.05f)

val ProdyTextOnAccentLight = Color(0xFFFFFFFF)
val AchievementUnlocked = ProdySuccess
val GoldTier = LeaderboardGold
val StreakColor = ProdyWarmAmber