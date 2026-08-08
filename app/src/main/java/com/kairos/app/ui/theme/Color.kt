package com.kairos.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Kairos color system.
 *
 * Paper & Ink: a warm editorial commonplace book. One accent — vermilion —
 * on one warm gray family. Existing Kairos-named aliases remain for binary and
 * source compatibility while screens migrate to semantic Kairos tokens.
 */

// =============================================================================
// BRAND COLORS - Core Identity
// =============================================================================

val KairosMineralIndigo = Color(0xFFB3401F)   // Vermilion — the single accent
val KairosClay = Color(0xFFA0522D)            // Muted sienna, same accent family
val KairosVerdigris = Color(0xFF33543F)       // Deep forest — success only
val KairosPeriwinkle = Color(0xFFE5855F)      // Vermilion light (dark-mode accent)
val KairosSoftClay = Color(0xFFD98E6E)        // Pale clay for dark secondary
val KairosSeaGlass = Color(0xFF8FA98F)        // Pale forest — dark success

val KairosIndigoContainerLight = Color(0xFFF6E3DA)   // Accent wash
val KairosOnIndigoContainerLight = Color(0xFF5C1F0A) // Deep vermilion on wash
val KairosClayContainerLight = Color(0xFFF0E1D2)     // Warm clay wash
val KairosOnClayContainerLight = Color(0xFF5A2E1A)
val KairosVerdigrisContainerLight = Color(0xFFDDE6DB) // Pale forest wash
val KairosOnVerdigrisContainerLight = Color(0xFF1C3526)

val KairosIndigoContainerDark = Color(0xFF4A2415)     // Night accent wash
val KairosOnIndigoContainerDark = Color(0xFFF6E3DA)
val KairosClayContainerDark = Color(0xFF5A3020)
val KairosOnClayContainerDark = Color(0xFFF3D9C9)
val KairosVerdigrisContainerDark = Color(0xFF24402F)
val KairosOnVerdigrisContainerDark = Color(0xFFC8D9C6)

val KairosForestGreen = KairosMineralIndigo
val KairosWarmAmber = KairosClay

val KairosPrimary = KairosForestGreen
val KairosSecondary = KairosWarmAmber

// =============================================================================
// LIGHT THEME COLORS — Paper & ink neutrals
// =============================================================================

val KairosBackgroundLight = Color(0xFFF4F0E8)          // Paper ground
val KairosSurfaceLight = Color(0xFFFBF8F1)             // Paper surface
val KairosSurfaceVariantLight = Color(0xFFEFEADD)      // Soft paper
val KairosSurfaceContainerLight = Color(0xFFE4DECD)    // Paper container

val KairosTextPrimaryLight = Color(0xFF221E17)         // Ink
val KairosTextSecondaryLight = Color(0xFF6E6759)       // Soft ink
val KairosTextTertiaryLight = Color(0xFF98907F)        // Faint ink
val KairosTextOnPrimaryLight = Color(0xFFFFF8F0)       // Cream on vermilion
val KairosTextOnAccentLight = Color(0xFFFFF8F0)        // Cream on accent

val KairosOutlineLight = Color(0xFFD8D2C4)             // Hairline
val KairosDividerLight = Color(0xFFE1DAC9)             // Softer hairline

// =============================================================================
// DARK THEME COLORS — Night paper
// =============================================================================

val KairosBackgroundDark = Color(0xFF171410)             // Night paper ground
val KairosSurfaceDark = Color(0xFF1D1913)               // Night paper surface
val KairosSurfaceVariantDark = Color(0xFF262118)        // Night soft paper
val KairosSurfaceContainerDark = Color(0xFF2E281D)      // Night paper container

val KairosTextPrimaryDark = Color(0xFFEDE7DA)            // Night ink
val KairosTextSecondaryDark = Color(0xFFA79E8C)           // Night soft ink
val KairosTextTertiaryDark = Color(0xFF7C7465)           // Night faint ink
val KairosTextOnPrimaryDark = Color(0xFF2E1505)

val KairosOutlineDark = Color(0xFF3A342A)                // Night hairline
val KairosDividerDark = Color(0xFF352F26)                // Night soft hairline

// =============================================================================
// SEMANTIC COLORS — Warm-tinted for cohesion with the paper world
// =============================================================================

val KairosError = Color(0xFFB3261E)                    // Oxblood
val KairosSuccess = KairosVerdigris                    // Deep forest
val KairosWarning = Color(0xFFFFA000)                  // Warm amber
val KairosInfo = Color(0xFF1565C0)                     // Deep blue (informational)

val KairosOnError = Color(0xFFFFFFFF)
val KairosOnSuccess = Color(0xFFFFFFFF)
val KairosOnWarning = Color(0xFF000000)
val KairosOnInfo = Color(0xFFFFFFFF)

// Containers (Light) — warm-tinted
val KairosErrorContainer = Color(0xFFF4DBD5)              // Pale oxblood wash
val KairosSuccessContainer = Color(0xFFDDE6DB)            // Pale forest wash
val KairosWarningContainer = Color(0xFFFFF3D6)            // Amber wash
val KairosInfoContainer = Color(0xFFE3F2FD)               // Blue wash

// Containers (Dark) — warm-tinted darks
val KairosErrorContainerDark = Color(0xFF59241F)
val KairosSuccessContainerDark = Color(0xFF24402F)
val KairosWarningContainerDark = Color(0xFF3E2A16)
val KairosInfoContainerDark = Color(0xFF0D3B6E)

// =============================================================================
// LEGACY / COMPATIBILITY COLORS (Mapped to New System)
// =============================================================================

val KairosAccentGreen = KairosForestGreen
val KairosAccentGreenLight = Color(0xFFC96A45)
val KairosAccentGreenDark = Color(0xFF8C3012)

val KairosAccent = KairosPrimary
val KairosGreen = KairosPrimary
val KairosAccentBlue = KairosInfo

// Surfaces
val KairosSurface = KairosSurfaceLight
val KairosBackground = KairosBackgroundLight
val KairosOnPrimary = KairosTextOnPrimaryLight
val KairosOnSecondary = Color(0xFF2E1505)
val KairosPrimaryContainer = KairosIndigoContainerLight
val KairosTertiary = KairosTextSecondaryLight
val KairosOnTertiary = Color(0xFFFFFFFF)
val KairosTertiaryContainer = KairosSurfaceVariantLight
val KairosTextSecondary = KairosTextSecondaryLight

val KairosPrimaryDark = KairosPeriwinkle
val KairosOnPrimaryDark = Color(0xFF2E1505)
val KairosPrimaryContainerDark = KairosIndigoContainerDark
val KairosSecondaryDark = KairosSoftClay
val KairosOnSecondaryDark = Color(0xFF3A1606)
val KairosSecondaryContainerDark = KairosClayContainerDark
val KairosTertiaryDark = KairosTextSecondaryDark
val KairosOnTertiaryDark = Color(0xFF13291D)
val KairosTertiaryContainerDark = KairosSurfaceVariantDark



// Moods
val MoodHappy = Color(0xFFE3A93C)
val MoodCalm = Color(0xFF5B8FA8)
val MoodAnxious = Color(0xFFC86A4A)
val MoodSad = Color(0xFF8A8A80)
val MoodMotivated = Color(0xFFD9A02E)
val MoodGrateful = Color(0xFF8A9B70)
val MoodConfused = Color(0xFFB3937B)
val MoodExcited = Color(0xFFC8552E)
val MoodEnergetic = Color(0xFFC98A2E)
val MoodInspired = Color(0xFFB0855A)
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
