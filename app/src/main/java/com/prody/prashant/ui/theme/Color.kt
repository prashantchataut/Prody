package com.prody.prashant.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Prody Design System - Color Palette (Revamp 2026)
 *
 * A polished, intuitive color system for the Prody mental wellness companion.
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
val ProdySurfaceContainerLight = Color(0xFFEEEEEE)    // Container

val ProdyTextPrimaryLight = Color(0xFF1A1A1A)         // Near black
val ProdyTextSecondaryLight = Color(0xFF6C757D)       // Medium gray
val ProdyTextTertiaryLight = Color(0xFFA0A8AD)        // Light gray
val ProdyTextOnPrimaryLight = Color(0xFFFFFFFF)       // White on green
val ProdyTextOnAccentLight = Color(0xFFFFFFFF)        // White on accent

val ProdyOutlineLight = Color(0xFFE0E0E0)
val ProdyDividerLight = Color(0xFFEEEEEE)

// =============================================================================
// DARK THEME COLORS
// =============================================================================

val ProdyBackgroundDark = Color(0xFF121212)           // Standard Dark Background
val ProdySurfaceDark = Color(0xFF1E1E1E)              // Surface
val ProdySurfaceVariantDark = Color(0xFF2C2C2C)       // Surface Variant
val ProdySurfaceContainerDark = Color(0xFF333333)     // Container

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
// LEGACY / COMPATIBILITY COLORS (Mapped to New System)
// =============================================================================

val ProdyAccentGreen = ProdyForestGreen
val ProdyAccentGreenLight = Color(0xFF60AD5E) 
val ProdyAccentGreenDark = Color(0xFF005005)

val ProdyAccent = ProdyPrimary
val ProdyGreen = ProdyPrimary
val ProdyAccentBlue = ProdyInfo

// Surfaces
val ProdySurface = ProdySurfaceLight
val ProdyBackground = ProdyBackgroundLight
val ProdyOnPrimary = ProdyTextOnPrimaryLight
val ProdyOnSecondary = Color(0xFF000000)
val ProdyPrimaryContainer = ProdySuccessContainer
val ProdyTertiary = ProdyTextSecondaryLight
val ProdyOnTertiary = Color(0xFFFFFFFF)
val ProdyTertiaryContainer = ProdySurfaceVariantLight
val ProdyTextSecondary = ProdyTextSecondaryLight

val ProdyPrimaryDark = ProdyForestGreen
val ProdyOnPrimaryDark = Color(0xFFFFFFFF)
val ProdyPrimaryContainerDark = ProdySuccessContainerDark
val ProdySecondaryDark = ProdyWarmAmber
val ProdyOnSecondaryDark = Color(0xFF000000)
val ProdySecondaryContainerDark = Color(0xFF3E2723)
val ProdyTertiaryDark = ProdyTextSecondaryDark
val ProdyOnTertiaryDark = Color(0xFF000000)
val ProdyTertiaryContainerDark = ProdySurfaceVariantDark

val ProdyPrimaryVariant = ProdyAccentGreenDark

// Moods
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

// Haven
val HavenBackgroundLight = ProdyBackgroundLight
val HavenBubbleLight = Color(0xFFE8F5E9)
val HavenUserBubbleLight = ProdySurfaceLight
val HavenTextLight = ProdyTextPrimaryLight
val HavenBackgroundDark = ProdyBackgroundDark
val HavenBubbleDark = Color(0xFF1B3320)
val HavenUserBubbleDark = ProdySurfaceDark
val HavenTextDark = ProdyTextPrimaryDark
val HavenAccentRose = Color(0xFFE91E63)
val HavenAccentGold = ProdyWarmAmber

// Scrim
val Scrim = Color(0x52000000)

// Leaderboard / Gamification
val LeaderboardGold = Color(0xFFFFD700)
val LeaderboardSilver = Color(0xFFC0C0C0)
val LeaderboardBronze = Color(0xFFCD7F32)
val LeaderboardGoldLight = Color(0xFFFFE57F)
val LeaderboardGoldDark = Color(0xFFC7A500)
val LeaderboardSilverLight = Color(0xFFE0E0E0)
val LeaderboardSilverDark = Color(0xFF9E9E9E)
val LeaderboardBronzeLight = Color(0xFFFFCCBC)
val LeaderboardBronzeDark = Color(0xFF8D6E63)

val GoldTier = LeaderboardGold
val SilverTier = LeaderboardSilver
val BronzeTier = LeaderboardBronze
val PlatinumTier = Color(0xFFE1F5FE)

// Streak
val StreakColor = Color(0xFFE65100)
val StreakFire = Color(0xFFE65100)
val StreakWarm = Color(0xFFFF9800)
val StreakHot = Color(0xFFFF5722)
val StreakWeek = Color(0xFFFFA726)
val StreakMonth = Color(0xFFFF7043)
val StreakQuarter = Color(0xFFE64A19)
val StreakGlow = Color(0xFFFFD180)
val StreakEmber = Color(0xFFFFAB40)
val StreakInferno = Color(0xFFBF360C)
val StreakBlazing = Color(0xFFFF3D00)
val StreakCold = Color(0xFF90CAF9)

// Milestones
val StreakWeekMilestone = StreakWeek
val StreakMonthMilestone = StreakMonth
val StreakMilestone7 = StreakWeekMilestone
val StreakMilestone30 = StreakMonthMilestone
val StreakMilestone100 = StreakQuarter
val StreakMilestone365 = Color(0xFFD84315)

// Support
val SupportBoost = ProdySuccess
val SupportRespect = ProdyInfo
val SupportEncourage = ProdyWarmAmber

// Notifications
val NotificationAchievement = Color(0xFF9C27B0)
val NotificationCelebration = Color(0xFFFFEB3B)
val NotificationMotivation = Color(0xFFFF9800)
val NotificationPrimary = ProdyPrimary
val NotificationReminder = ProdyInfo
val NotificationStreak = StreakFire
val NotificationSuccess = ProdySuccess

// Gradients
object ProdyGradients {
    val primaryGradient = listOf(ProdyAccentGreenLight, ProdyAccentGreen, ProdyAccentGreenDark)
    val goldGradient = listOf(LeaderboardGoldLight, LeaderboardGold, LeaderboardGoldDark)
    val streakNotificationGradient = listOf(StreakWarm, StreakHot)
    val celebrationGradient = listOf(NotificationCelebration, NotificationAchievement)
    val achievementGradient = listOf(NotificationAchievement, ProdyPrimary)
    val motivationGradient = listOf(NotificationMotivation, StreakWarm)
    val oceanGradient = listOf(ProdyInfo, MoodCalm)
    val growthGradient = listOf(ProdySuccess, ProdyAccentGreen)
    val serenityGradient = listOf(MoodCalm, MoodGrateful)
    val goldBanner = goldGradient
    val silverBanner = listOf(LeaderboardSilverLight, LeaderboardSilver, LeaderboardSilverDark)
    val bronzeBanner = listOf(LeaderboardBronzeLight, LeaderboardBronze, LeaderboardBronzeDark)
    
    // Gradient Aliases
    val streakGradient = streakNotificationGradient
}

// Activity Pulse
val ActivityPulseBackground = Color(0xFFE0F2F1)
val ActivityPulseBackgroundLight = Color(0xFFE0F2F1)

// Journal History
val JournalHistoryAccent = ProdyPrimary
val JournalHistoryCardLight = ProdySurfaceLight
val JournalHistoryCardDark = ProdySurfaceDark
val JournalHistoryTextPrimaryLight = ProdyTextPrimaryLight
val JournalHistoryTextPrimaryDark = ProdyTextPrimaryDark
val JournalHistoryTextSecondaryLight = ProdyTextSecondaryLight
val JournalHistoryTextSecondaryDark = ProdyTextSecondaryDark
val JournalHistoryDividerLight = ProdyDividerLight
val JournalHistoryDividerDark = ProdyDividerDark

// Rarity
val RarityCommon = Color(0xFF9E9E9E)
val RarityUncommon = Color(0xFF66BB6A)
val RarityRare = Color(0xFF42A5F5)
val RarityEpic = Color(0xFFAB47BC)
val RarityLegendary = Color(0xFFFFD700)
val RarityMythic = Color(0xFFFF1744)

// Achievements
val AchievementUnlocked = ProdySuccess

// Premium
val ProdyPremiumViolet = Color(0xFF673AB7)
val ProdyPremiumVioletContainer = Color(0xFFD1C4E9)
val ProdyPremiumVioletDark = Color(0xFF512DA8)
val ProdyPremiumVioletLight = Color(0xFF9575CD)

// Time Capsule
val TimeCapsuleBackgroundLight = ProdyBackgroundLight
val TimeCapsuleBackgroundDark = ProdyBackgroundDark
val TimeCapsuleTitleTextLight = ProdyTextPrimaryLight
val TimeCapsuleTitleTextDark = ProdyTextPrimaryDark
val TimeCapsuleDiscardTextLight = ProdyTextSecondaryLight
val TimeCapsuleDiscardTextDark = ProdyTextSecondaryDark
val TimeCapsulePlaceholderLight = ProdyTextSecondaryLight
val TimeCapsulePlaceholderDark = ProdyTextSecondaryDark
val TimeCapsuleActiveTextLight = ProdyTextPrimaryLight
val TimeCapsuleActiveTextDark = ProdyTextPrimaryDark
val TimeCapsuleMultimediaIconLight = ProdyTextSecondaryLight
val TimeCapsuleMultimediaIconDark = ProdyTextSecondaryDark
val TimeCapsuleAttachTextLight = ProdyTextSecondaryLight
val TimeCapsuleAttachTextDark = ProdyTextSecondaryDark
val TimeCapsuleDividerLight = ProdyDividerLight
val TimeCapsuleDividerDark = ProdyDividerDark
val TimeCapsuleSectionTitleLight = ProdyTextPrimaryLight
val TimeCapsuleSectionTitleDark = ProdyTextPrimaryDark
val TimeCapsuleInactiveTagBgLight = ProdySurfaceVariantLight
val TimeCapsuleInactiveTagBgDark = ProdySurfaceVariantDark
val TimeCapsuleInactiveTagTextLight = ProdyTextSecondaryLight
val TimeCapsuleInactiveTagTextDark = ProdyTextSecondaryDark
val TimeCapsuleButtonTextLight = Color.White
val TimeCapsuleButtonTextDark = Color.White
val TimeCapsuleAccent = ProdyPrimary
val TimeCapsuleIconLight = ProdyTextSecondaryLight
val TimeCapsuleIconDark = ProdyTextSecondaryDark
val TimeCapsuleTabContainerLight = ProdySurfaceVariantLight
val TimeCapsuleTabContainerDark = ProdySurfaceVariantDark
val TimeCapsuleActiveTabTextLight = ProdyTextPrimaryLight
val TimeCapsuleActiveTabTextDark = ProdyTextPrimaryDark
val TimeCapsuleEmptyCircleBgLight = ProdySurfaceVariantLight
val TimeCapsuleEmptyCircleBgDark = ProdySurfaceVariantDark
val TimeCapsuleDashedCircleLight = ProdyDividerLight
val TimeCapsuleDashedCircleDark = ProdyDividerDark

// Daily Wisdom
val WordOfDayColor = Color(0xFFFFC107)
val IdiomPurple = Color(0xFF9C27B0)
val ProverbTeal = Color(0xFF009688)
val SeedGold = Color(0xFFFFD740)
val WisdomPerspective = Color(0xFF7E57C2)

// Challenges
val ChallengeActive = ProdySuccess
val ChallengeCompleted = LeaderboardGold

// Skills
val ClaritySkillColor = Color(0xFF29B6F6)
val DisciplineSkillColor = Color(0xFFAB47BC)
val CourageSkillColor = Color(0xFFFF7043)

// Misc
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
val BloomReady = ProdyForestGreen
val BloomGrowing = Color(0xFF8BC34A)
val SeedDormant = Color(0xFFBDBDBD)
val InteractiveHoverLight = Color(0x0A000000)
val InteractivePressedLight = Color(0x1F000000)
val InteractiveHoverDark = Color(0x0AFFFFFF)
val InteractivePressedDark = Color(0x1FFFFFFF)
val InteractiveFocus = ProdyForestGreen.copy(alpha = 0.5f)
val WrappedPurple1 = Color(0xFF6B5CE7)
val WrappedPurple2 = Color(0xFF8B7EF0)
val WrappedPink1 = Color(0xFFE91E63)
val WrappedPink2 = Color(0xFFF06292)
val SuccessGreen = ProdySuccess
val ErrorRed = ProdyError
val WarningAmber = ProdyWarning
val InfoBlue = ProdyInfo
val ProfileAvatarRing = ProdyForestGreen
val FutureCategoryGoal = ProdyForestGreen
val FutureCategoryMotivation = ProdyWarmAmber
val FutureMessageArrived = Color(0xFFFFD700)
val JournalAccent = ProdyForestGreen
val JournalAccentGreen = ProdyForestGreen
val XpBarFill = ProdyAccentGreen
val XpBarGlow = ProdyAccentGreen.copy(alpha = 0.5f)
val LevelUpGlow = ProdyAccentGreen
val JournalSaveButtonBgLight = ProdySurfaceVariantLight
val JournalIconCircleBorderLight = ProdyOutlineLight
val JournalCardCornerDetailLight = ProdySurfaceContainerLight
val JournalBackgroundLight = ProdyBackgroundLight
val JournalSurfaceLight = ProdySurfaceLight
val JournalTextPrimaryLight = ProdyTextPrimaryLight
val JournalTextSecondaryLight = ProdyTextSecondaryLight
val JournalPlaceholderLight = ProdyTextSecondaryLight
val JournalSliderInactiveLight = ProdyOutlineLight
val JournalSaveButtonBgDark = ProdySurfaceVariantDark
val JournalIconCircleBorderDark = ProdyOutlineDark
val JournalCardCornerDetailDark = ProdySurfaceContainerDark
val JournalBackgroundDark = ProdyBackgroundDark
val JournalSurfaceDark = ProdySurfaceDark
val JournalTextPrimaryDark = ProdyTextPrimaryDark
val JournalTextSecondaryDark = ProdyTextSecondaryDark
val JournalPlaceholderDark = ProdyTextSecondaryDark
val JournalSliderInactiveDark = ProdyOutlineDark

val TimeCapsuleTextPrimaryDark = Color(0xFFE2E2E6)
val TimeCapsuleTextPrimaryLight = Color(0xFF191C1E)
val TimeCapsuleTextSecondaryDark = Color(0xFFC4C6D0)
val TimeCapsuleTextSecondaryLight = Color(0xFF44474E)
