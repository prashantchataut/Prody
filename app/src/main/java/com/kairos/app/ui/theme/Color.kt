package com.kairos.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Kairos color system.
 *
 * Mineral indigo provides recognition, clay adds editorial warmth, and verdigris
 * communicates completion. Existing Kairos-named aliases remain for binary and
 * source compatibility while screens migrate to semantic Kairos tokens.
 */

// =============================================================================
// BRAND COLORS - Core Identity
// =============================================================================

val KairosMineralIndigo = Color(0xFF495CC7)
val KairosClay = Color(0xFFC86F4E)
val KairosVerdigris = Color(0xFF2E7D70)
val KairosPeriwinkle = Color(0xFFAEB8FF)
val KairosSoftClay = Color(0xFFE39A7D)
val KairosSeaGlass = Color(0xFF76C3B3)

val KairosIndigoContainerLight = Color(0xFFE3E6FF)
val KairosOnIndigoContainerLight = Color(0xFF17215F)
val KairosClayContainerLight = Color(0xFFFFDBCF)
val KairosOnClayContainerLight = Color(0xFF4A1D10)
val KairosVerdigrisContainerLight = Color(0xFFBDEBDD)
val KairosOnVerdigrisContainerLight = Color(0xFF08372F)

val KairosIndigoContainerDark = Color(0xFF303C88)
val KairosOnIndigoContainerDark = Color(0xFFE3E6FF)
val KairosClayContainerDark = Color(0xFF633826)
val KairosOnClayContainerDark = Color(0xFFFFDBCF)
val KairosVerdigrisContainerDark = Color(0xFF174E45)
val KairosOnVerdigrisContainerDark = Color(0xFFBDEBDD)

val KairosForestGreen = KairosMineralIndigo
val KairosWarmAmber = KairosClay

val KairosPrimary = KairosForestGreen
val KairosSecondary = KairosWarmAmber

// =============================================================================
// LIGHT THEME COLORS — Editorial warm neutrals
// =============================================================================

val KairosBackgroundLight = Color(0xFFF7F6F2)          // Warm off-white (green tint)
val KairosSurfaceLight = Color(0xFFFCFBF8)             // Warm white
val KairosSurfaceVariantLight = Color(0xFFF0EFEA)      // Warm light gray
val KairosSurfaceContainerLight = Color(0xFFE8E8E4)     // Warm container gray

val KairosTextPrimaryLight = Color(0xFF202126)         // Warm near-black (olive undertone)
val KairosTextSecondaryLight = Color(0xFF666970)         // Warm medium gray (green-gray)
val KairosTextTertiaryLight = Color(0xFF92959D)         // Warm light gray
val KairosTextOnPrimaryLight = Color(0xFFFFFFFF)        // White on green
val KairosTextOnAccentLight = Color(0xFFFFFFFF)         // White on accent

val KairosOutlineLight = Color(0xFFD7D8DE)              // Warm outline
val KairosDividerLight = Color(0xFFE7E7E3)              // Warm divider

// =============================================================================
// DARK THEME COLORS — Quiet ink-toned darks
// =============================================================================

val KairosBackgroundDark = Color(0xFF111318)             // Warm deep dark
val KairosSurfaceDark = Color(0xFF191C22)               // Warm dark surface
val KairosSurfaceVariantDark = Color(0xFF242831)        // Warm dark variant
val KairosSurfaceContainerDark = Color(0xFF2B303A)      // Warm container dark

val KairosTextPrimaryDark = Color(0xFFF1F0EB)            // Warm white
val KairosTextSecondaryDark = Color(0xFFB2B4BC)           // Warm medium gray
val KairosTextTertiaryDark = Color(0xFF858993)           // Warm dark gray
val KairosTextOnPrimaryDark = Color(0xFF17215F)

val KairosOutlineDark = Color(0xFF3C414C)                // Warm outline dark
val KairosDividerDark = Color(0xFF2A2E36)                // Warm divider dark

// =============================================================================
// SEMANTIC COLORS — Warm-tinted for cohesion with brand
// =============================================================================

val KairosError = Color(0xFFD32F2F)                    // Clear red (unchanged — needs clarity)
val KairosSuccess = KairosVerdigris                  // Richer forest green (slightly warmer)
val KairosWarning = Color(0xFFFFA000)                  // Warm amber (unchanged)
val KairosInfo = Color(0xFF1565C0)                     // Deeper blue (more serious, less cold)

val KairosOnError = Color(0xFFFFFFFF)
val KairosOnSuccess = Color(0xFFFFFFFF)
val KairosOnWarning = Color(0xFF000000)
val KairosOnInfo = Color(0xFFFFFFFF)

// Containers (Light) — warm-tinted
val KairosErrorContainer = Color(0xFFFDECEA)              // Warm red tint
val KairosSuccessContainer = Color(0xFFDCEFEA)            // Green tint (unchanged)
val KairosWarningContainer = Color(0xFFFFF8E1)            // Amber tint
val KairosInfoContainer = Color(0xFFE3F2FD)               // Blue tint

// Containers (Dark) — warm-tinted darks
val KairosErrorContainerDark = Color(0xFF4A2525)
val KairosSuccessContainerDark = Color(0xFF193630)
val KairosWarningContainerDark = Color(0xFF3E2723)
val KairosInfoContainerDark = Color(0xFF0D3B6E)

// =============================================================================
// LEGACY / COMPATIBILITY COLORS (Mapped to New System)
// =============================================================================

val KairosAccentGreen = KairosForestGreen
val KairosAccentGreenLight = Color(0xFF7888E8)
val KairosAccentGreenDark = Color(0xFF33439E)

val KairosAccent = KairosPrimary
val KairosGreen = KairosPrimary
val KairosAccentBlue = KairosInfo

// Surfaces
val KairosSurface = KairosSurfaceLight
val KairosBackground = KairosBackgroundLight
val KairosOnPrimary = KairosTextOnPrimaryLight
val KairosOnSecondary = Color(0xFF000000)
val KairosPrimaryContainer = KairosSuccessContainer
val KairosTertiary = KairosTextSecondaryLight
val KairosOnTertiary = Color(0xFFFFFFFF)
val KairosTertiaryContainer = KairosSurfaceVariantLight
val KairosTextSecondary = KairosTextSecondaryLight

val KairosPrimaryDark = KairosPeriwinkle
val KairosOnPrimaryDark = Color(0xFF17215F)
val KairosPrimaryContainerDark = KairosSuccessContainerDark
val KairosSecondaryDark = KairosSoftClay
val KairosOnSecondaryDark = Color(0xFF000000)
val KairosSecondaryContainerDark = Color(0xFF3E2723)
val KairosTertiaryDark = KairosTextSecondaryDark
val KairosOnTertiaryDark = Color(0xFF000000)
val KairosTertiaryContainerDark = KairosSurfaceVariantDark



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

// Haven — Warm Reddish-Cream Palette (slightly warmer for coziness)
val HavenBackgroundLight = Color(0xFFFFF8F4)              // Warmer cream
val HavenBubbleLight = Color(0xFFF5DDD0)                 // Soft blush/rose
val HavenUserBubbleLight = Color(0xFFFFF0EB)              // Lighter cream user
val HavenTextLight = Color(0xFF2D2424)                   // Warm dark text
val HavenBackgroundDark = Color(0xFF1A1214)              // Deep warm dark
val HavenBubbleDark = Color(0xFF3D2A2A)                   // Dark rose bubble
val HavenUserBubbleDark = Color(0xFF2A1E1E)               // Dark user bubble
val HavenTextDark = Color(0xFFF0EAE2)                    // Warm light text
val HavenAccentRose = Color(0xFFD4736B)                   // Muted dusty rose
val HavenAccentGold = Color(0xFFD4A574)                   // Warm caramel gold

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
val SupportBoost = KairosSuccess
val SupportRespect = KairosInfo
val SupportEncourage = KairosWarmAmber

// Notifications
val NotificationAchievement = Color(0xFF9C27B0)
val NotificationCelebration = Color(0xFFFFEB3B)
val NotificationMotivation = Color(0xFFFF9800)
val NotificationPrimary = KairosPrimary
val NotificationReminder = KairosInfo
val NotificationStreak = StreakFire
val NotificationSuccess = KairosSuccess

// Gradients
object KairosGradients {
    val primaryGradient = listOf(KairosAccentGreenLight, KairosAccentGreen, KairosAccentGreenDark)
    val goldGradient = listOf(LeaderboardGoldLight, LeaderboardGold, LeaderboardGoldDark)
    val streakNotificationGradient = listOf(StreakWarm, StreakHot)
    val celebrationGradient = listOf(NotificationCelebration, NotificationAchievement)
    val achievementGradient = listOf(NotificationAchievement, KairosPrimary)
    val motivationGradient = listOf(NotificationMotivation, StreakWarm)
    val oceanGradient = listOf(KairosInfo, MoodCalm)
    val growthGradient = listOf(KairosSuccess, KairosAccentGreen)
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
val JournalHistoryAccent = KairosPrimary
val JournalHistoryCardLight = KairosSurfaceLight
val JournalHistoryCardDark = KairosSurfaceDark
val JournalHistoryTextPrimaryLight = KairosTextPrimaryLight
val JournalHistoryTextPrimaryDark = KairosTextPrimaryDark
val JournalHistoryTextSecondaryLight = KairosTextSecondaryLight
val JournalHistoryTextSecondaryDark = KairosTextSecondaryDark
val JournalHistoryDividerLight = KairosDividerLight
val JournalHistoryDividerDark = KairosDividerDark

// Rarity
val RarityCommon = Color(0xFF9E9E9E)
val RarityUncommon = Color(0xFF66BB6A)
val RarityRare = Color(0xFF42A5F5)
val RarityEpic = Color(0xFFAB47BC)
val RarityLegendary = Color(0xFFFFD700)
val RarityMythic = Color(0xFFFF1744)

// Achievements
val AchievementUnlocked = KairosSuccess

// Premium
val KairosPremiumViolet = Color(0xFF673AB7)
val KairosPremiumVioletContainer = Color(0xFFD1C4E9)
val KairosPremiumVioletDark = Color(0xFF512DA8)
val KairosPremiumVioletLight = Color(0xFF9575CD)

// Time Capsule
val TimeCapsuleBackgroundLight = KairosBackgroundLight
val TimeCapsuleBackgroundDark = KairosBackgroundDark
val TimeCapsuleTitleTextLight = KairosTextPrimaryLight
val TimeCapsuleTitleTextDark = KairosTextPrimaryDark
val TimeCapsuleDiscardTextLight = KairosTextSecondaryLight
val TimeCapsuleDiscardTextDark = KairosTextSecondaryDark
val TimeCapsulePlaceholderLight = KairosTextSecondaryLight
val TimeCapsulePlaceholderDark = KairosTextSecondaryDark
val TimeCapsuleActiveTextLight = KairosTextPrimaryLight
val TimeCapsuleActiveTextDark = KairosTextPrimaryDark
val TimeCapsuleMultimediaIconLight = KairosTextSecondaryLight
val TimeCapsuleMultimediaIconDark = KairosTextSecondaryDark
val TimeCapsuleAttachTextLight = KairosTextSecondaryLight
val TimeCapsuleAttachTextDark = KairosTextSecondaryDark
val TimeCapsuleDividerLight = KairosDividerLight
val TimeCapsuleDividerDark = KairosDividerDark
val TimeCapsuleSectionTitleLight = KairosTextPrimaryLight
val TimeCapsuleSectionTitleDark = KairosTextPrimaryDark
val TimeCapsuleInactiveTagBgLight = KairosSurfaceVariantLight
val TimeCapsuleInactiveTagBgDark = KairosSurfaceVariantDark
val TimeCapsuleInactiveTagTextLight = KairosTextSecondaryLight
val TimeCapsuleInactiveTagTextDark = KairosTextSecondaryDark
val TimeCapsuleButtonTextLight = Color.White
val TimeCapsuleButtonTextDark = Color.White
val TimeCapsuleAccent = KairosPrimary
val TimeCapsuleIconLight = KairosTextSecondaryLight
val TimeCapsuleIconDark = KairosTextSecondaryDark
val TimeCapsuleTabContainerLight = KairosSurfaceVariantLight
val TimeCapsuleTabContainerDark = KairosSurfaceVariantDark
val TimeCapsuleActiveTabTextLight = KairosTextPrimaryLight
val TimeCapsuleActiveTabTextDark = KairosTextPrimaryDark
val TimeCapsuleEmptyCircleBgLight = KairosSurfaceVariantLight
val TimeCapsuleEmptyCircleBgDark = KairosSurfaceVariantDark
val TimeCapsuleDashedCircleLight = KairosDividerLight
val TimeCapsuleDashedCircleDark = KairosDividerDark

// Daily Wisdom
val WordOfDayColor = Color(0xFFFFC107)
val IdiomPurple = Color(0xFF9C27B0)
val ProverbTeal = Color(0xFF009688)
val SeedGold = Color(0xFFFFD740)
val WisdomPerspective = Color(0xFF7E57C2)

// Challenges
val ChallengeActive = KairosSuccess
val ChallengeCompleted = LeaderboardGold

// Skills
val ClaritySkillColor = Color(0xFF29B6F6)
val DisciplineSkillColor = Color(0xFFAB47BC)
val CourageSkillColor = Color(0xFFFF7043)

// Misc
val KairosOutline = KairosOutlineLight
val KairosOutlineVariant = KairosDividerLight
val KairosOutlineVariantDark = KairosDividerDark
val KairosSurfaceElevated = KairosSurfaceLight
val KairosSurfaceDim = KairosSurfaceVariantLight
val KairosSurfaceElevatedDark = KairosSurfaceDark
val KairosSurfaceDimDark = KairosSurfaceVariantDark
val KairosInverseSurface = Color(0xFF303030)
val KairosInverseOnSurface = Color(0xFFF5F5F5)
val KairosInversePrimary = Color(0xFF81C784)
val BloomReady = KairosForestGreen
val BloomGrowing = Color(0xFF8BC34A)
val SeedDormant = Color(0xFFBDBDBD)
val InteractiveHoverLight = Color(0x0A000000)
val InteractivePressedLight = Color(0x1F000000)
val InteractiveHoverDark = Color(0x0AFFFFFF)
val InteractivePressedDark = Color(0x1FFFFFFF)
val InteractiveFocus = KairosForestGreen.copy(alpha = 0.5f)
val WrappedPurple1 = Color(0xFF6B5CE7)
val WrappedPurple2 = Color(0xFF8B7EF0)
val WrappedPink1 = Color(0xFFE91E63)
val WrappedPink2 = Color(0xFFF06292)
val SuccessGreen = KairosSuccess
val ErrorRed = KairosError
val WarningAmber = KairosWarning
val InfoBlue = KairosInfo
val ProfileAvatarRing = KairosForestGreen
val FutureCategoryGoal = KairosForestGreen
val FutureCategoryMotivation = KairosWarmAmber
val FutureMessageArrived = Color(0xFFFFD700)
val JournalAccentGreen = KairosForestGreen
val XpBarFill = KairosAccentGreen
val XpBarGlow = KairosAccentGreen.copy(alpha = 0.5f)
val LevelUpGlow = KairosAccentGreen

// Time Capsule Colors
val TimeCapsuleTextPrimaryLight = Color(0xFF1A1C1E)
val TimeCapsuleTextSecondaryLight = Color(0xFF42474E)
val TimeCapsuleTextPrimaryDark = Color(0xFFE2E2E6)
val TimeCapsuleTextSecondaryDark = Color(0xFFC4C7C5)
