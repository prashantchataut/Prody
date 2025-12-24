package com.prody.prashant.domain.identity

/**
 * [ShareCards] - Instagram-worthy shareable card system
 *
 * Share cards allow users to share their achievements, profile, and progress
 * on social media. Each card type is designed to be visually appealing and
 * showcase the user's cosmetics (banner, frame, badges, etc.).
 *
 * Design Philosophy:
 * - Premium feel: Cards look professional, not gamey
 * - Identity showcase: User's cosmetics are prominently displayed
 * - Minimal branding: Subtle Prody attribution, user is the hero
 * - Aspect ratios optimized for Instagram stories and posts
 *
 * Card Types:
 * - Profile Card: Full identity showcase
 * - Achievement Card: Single achievement unlock celebration
 * - Banner Unlock Card: New banner earned
 * - Streak Card: Streak milestone celebration
 * - Level Up Card: Level progression announcement
 * - Rank Card: Leaderboard position showcase
 * - Journey Card: Progress summary (weekly/monthly)
 *
 * Example usage:
 * ```
 * val card = ShareCards.createProfileCard(
 *     displayName = "Wise Seeker",
 *     title = ProdyTitles.findById("philosopher"),
 *     banner = ProdyBanners.findById("sages_horizon"),
 *     frame = ProdyFrames.findById("contemplative_ring"),
 *     pinnedBadges = listOf(...),
 *     stats = ShareCards.ProfileStats(...)
 * )
 *
 * // Render and share
 * ShareCardRenderer.render(card, ShareCards.AspectRatio.INSTAGRAM_STORY)
 * ```
 */
object ShareCards {

    /**
     * Supported aspect ratios for share cards.
     */
    enum class AspectRatio(
        val widthRatio: Float,
        val heightRatio: Float,
        val displayName: String
    ) {
        /** 9:16 for Instagram/TikTok stories */
        INSTAGRAM_STORY(9f, 16f, "Story"),
        /** 1:1 for Instagram posts */
        INSTAGRAM_POST(1f, 1f, "Square"),
        /** 4:5 for Instagram portrait posts */
        INSTAGRAM_PORTRAIT(4f, 5f, "Portrait"),
        /** 16:9 for Twitter/general landscape */
        LANDSCAPE(16f, 9f, "Landscape");

        val ratio: Float get() = widthRatio / heightRatio
    }

    /**
     * Base card content that all cards share.
     */
    interface ShareCard {
        val cardType: CardType
        val aspectRatio: AspectRatio
        val bannerId: String?
        val accentColorId: String
        val timestamp: Long
    }

    /**
     * Types of share cards.
     */
    enum class CardType(
        val id: String,
        val displayName: String,
        val defaultAspectRatio: AspectRatio
    ) {
        PROFILE("profile", "Profile", AspectRatio.INSTAGRAM_PORTRAIT),
        ACHIEVEMENT("achievement", "Achievement", AspectRatio.INSTAGRAM_POST),
        BANNER_UNLOCK("banner_unlock", "New Banner", AspectRatio.INSTAGRAM_POST),
        STREAK("streak", "Streak", AspectRatio.INSTAGRAM_STORY),
        LEVEL_UP("level_up", "Level Up", AspectRatio.INSTAGRAM_POST),
        RANK("rank", "Rank", AspectRatio.INSTAGRAM_POST),
        JOURNEY("journey", "Journey", AspectRatio.INSTAGRAM_STORY)
    }

    /**
     * Profile statistics for display on cards.
     */
    data class ProfileStats(
        val currentLevel: Int,
        val totalPoints: Int,
        val currentStreak: Int,
        val daysOnApp: Int,
        val wordsLearned: Int = 0,
        val journalEntries: Int = 0,
        val achievementsUnlocked: Int = 0
    )

    /**
     * Profile share card - Full identity showcase.
     *
     * Shows: Avatar, Name, Title, Banner, Frame, Pinned Badges, Stats
     */
    data class ProfileCard(
        val displayName: String,
        val titleId: String,
        val titleName: String,
        override val bannerId: String?,
        val frameId: String,
        override val accentColorId: String,
        val pinnedBadgeIds: List<String>,
        val stats: ProfileStats,
        val avatarId: String = "default",
        override val aspectRatio: AspectRatio = CardType.PROFILE.defaultAspectRatio,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ShareCard {
        override val cardType: CardType = CardType.PROFILE
    }

    /**
     * Achievement unlock share card.
     *
     * Celebrates a single achievement unlock with dramatic presentation.
     */
    data class AchievementCard(
        val achievementId: String,
        val achievementName: String,
        val achievementDescription: String,
        val achievementIconId: String,
        val achievementRarity: CosmeticRarity,
        val celebrationMessage: String,
        val displayName: String,
        override val bannerId: String?,
        override val accentColorId: String,
        val frameId: String,
        override val aspectRatio: AspectRatio = CardType.ACHIEVEMENT.defaultAspectRatio,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ShareCard {
        override val cardType: CardType = CardType.ACHIEVEMENT
    }

    /**
     * Banner unlock share card.
     *
     * Shows the newly unlocked banner in full glory.
     */
    data class BannerUnlockCard(
        val unlockedBannerId: String,
        val bannerName: String,
        val bannerDescription: String,
        val bannerRarity: CosmeticRarity,
        val unlockRequirement: String,
        val displayName: String,
        override val accentColorId: String,
        val frameId: String,
        override val aspectRatio: AspectRatio = CardType.BANNER_UNLOCK.defaultAspectRatio,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ShareCard {
        override val cardType: CardType = CardType.BANNER_UNLOCK
        override val bannerId: String = unlockedBannerId
    }

    /**
     * Streak milestone share card.
     *
     * Celebrates streak milestones (7, 30, 100, 365 days).
     */
    data class StreakCard(
        val currentStreak: Int,
        val milestoneReached: Int, // The milestone being celebrated
        val displayName: String,
        val titleName: String,
        override val bannerId: String?,
        override val accentColorId: String,
        val frameId: String,
        override val aspectRatio: AspectRatio = CardType.STREAK.defaultAspectRatio,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ShareCard {
        override val cardType: CardType = CardType.STREAK

        val streakMessage: String
            get() = when {
                milestoneReached >= 365 -> "One year of dedication"
                milestoneReached >= 100 -> "100 days strong"
                milestoneReached >= 30 -> "30 days of growth"
                milestoneReached >= 7 -> "First week complete"
                else -> "$currentStreak day streak"
            }
    }

    /**
     * Level up share card.
     *
     * Announces reaching a new level.
     */
    data class LevelUpCard(
        val previousLevel: Int,
        val newLevel: Int,
        val newLevelName: String,
        val displayName: String,
        override val bannerId: String?,
        override val accentColorId: String,
        val frameId: String,
        val totalPoints: Int,
        override val aspectRatio: AspectRatio = CardType.LEVEL_UP.defaultAspectRatio,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ShareCard {
        override val cardType: CardType = CardType.LEVEL_UP
    }

    /**
     * Rank share card.
     *
     * Shows leaderboard position.
     */
    data class RankCard(
        val rank: Int,
        val previousRank: Int,
        val totalParticipants: Int,
        val weeklyPoints: Int,
        val displayName: String,
        val titleName: String,
        override val bannerId: String?,
        override val accentColorId: String,
        val frameId: String,
        val pinnedBadgeIds: List<String>,
        override val aspectRatio: AspectRatio = CardType.RANK.defaultAspectRatio,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ShareCard {
        override val cardType: CardType = CardType.RANK

        val rankChange: Int get() = previousRank - rank
        val isRankUp: Boolean get() = rankChange > 0
        val isRankDown: Boolean get() = rankChange < 0
        val percentile: Float get() = ((totalParticipants - rank + 1).toFloat() / totalParticipants) * 100
    }

    /**
     * Journey summary share card.
     *
     * Shows weekly or monthly progress summary.
     */
    data class JourneyCard(
        val periodType: PeriodType,
        val periodLabel: String, // e.g., "This Week", "December 2024"
        val pointsEarned: Int,
        val wordsLearned: Int,
        val journalEntries: Int,
        val streakDays: Int,
        val achievementsUnlocked: Int,
        val displayName: String,
        val titleName: String,
        override val bannerId: String?,
        override val accentColorId: String,
        val frameId: String,
        override val aspectRatio: AspectRatio = CardType.JOURNEY.defaultAspectRatio,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ShareCard {
        override val cardType: CardType = CardType.JOURNEY
    }

    enum class PeriodType(val displayName: String) {
        WEEKLY("Weekly"),
        MONTHLY("Monthly")
    }

    // =========================================================================
    // Factory Methods
    // =========================================================================

    /**
     * Creates a profile share card.
     */
    fun createProfileCard(
        displayName: String,
        titleId: String,
        titleName: String,
        bannerId: String?,
        frameId: String,
        accentColorId: String,
        pinnedBadgeIds: List<String>,
        stats: ProfileStats,
        avatarId: String = "default",
        aspectRatio: AspectRatio = CardType.PROFILE.defaultAspectRatio
    ): ProfileCard = ProfileCard(
        displayName = displayName,
        titleId = titleId,
        titleName = titleName,
        bannerId = bannerId,
        frameId = frameId,
        accentColorId = accentColorId,
        pinnedBadgeIds = pinnedBadgeIds,
        stats = stats,
        avatarId = avatarId,
        aspectRatio = aspectRatio
    )

    /**
     * Creates an achievement share card.
     */
    fun createAchievementCard(
        achievementId: String,
        achievementName: String,
        achievementDescription: String,
        achievementIconId: String,
        achievementRarity: CosmeticRarity,
        celebrationMessage: String,
        displayName: String,
        bannerId: String?,
        accentColorId: String,
        frameId: String,
        aspectRatio: AspectRatio = CardType.ACHIEVEMENT.defaultAspectRatio
    ): AchievementCard = AchievementCard(
        achievementId = achievementId,
        achievementName = achievementName,
        achievementDescription = achievementDescription,
        achievementIconId = achievementIconId,
        achievementRarity = achievementRarity,
        celebrationMessage = celebrationMessage,
        displayName = displayName,
        bannerId = bannerId,
        accentColorId = accentColorId,
        frameId = frameId,
        aspectRatio = aspectRatio
    )

    /**
     * Creates a banner unlock share card.
     */
    fun createBannerUnlockCard(
        banner: ProdyBanners.Banner,
        displayName: String,
        accentColorId: String,
        frameId: String,
        aspectRatio: AspectRatio = CardType.BANNER_UNLOCK.defaultAspectRatio
    ): BannerUnlockCard = BannerUnlockCard(
        unlockedBannerId = banner.id,
        bannerName = banner.name,
        bannerDescription = banner.description,
        bannerRarity = banner.rarity,
        unlockRequirement = banner.unlockRequirement,
        displayName = displayName,
        accentColorId = accentColorId,
        frameId = frameId,
        aspectRatio = aspectRatio
    )

    /**
     * Creates a streak milestone share card.
     */
    fun createStreakCard(
        currentStreak: Int,
        displayName: String,
        titleName: String,
        bannerId: String?,
        accentColorId: String,
        frameId: String,
        aspectRatio: AspectRatio = CardType.STREAK.defaultAspectRatio
    ): StreakCard {
        // Determine which milestone this streak represents
        val milestone = when {
            currentStreak >= 365 -> 365
            currentStreak >= 100 -> 100
            currentStreak >= 30 -> 30
            currentStreak >= 7 -> 7
            else -> currentStreak
        }
        return StreakCard(
            currentStreak = currentStreak,
            milestoneReached = milestone,
            displayName = displayName,
            titleName = titleName,
            bannerId = bannerId,
            accentColorId = accentColorId,
            frameId = frameId,
            aspectRatio = aspectRatio
        )
    }

    /**
     * Creates a level up share card.
     */
    fun createLevelUpCard(
        previousLevel: Int,
        newLevel: Int,
        newLevelName: String,
        displayName: String,
        bannerId: String?,
        accentColorId: String,
        frameId: String,
        totalPoints: Int,
        aspectRatio: AspectRatio = CardType.LEVEL_UP.defaultAspectRatio
    ): LevelUpCard = LevelUpCard(
        previousLevel = previousLevel,
        newLevel = newLevel,
        newLevelName = newLevelName,
        displayName = displayName,
        bannerId = bannerId,
        accentColorId = accentColorId,
        frameId = frameId,
        totalPoints = totalPoints,
        aspectRatio = aspectRatio
    )

    /**
     * Creates a rank share card.
     */
    fun createRankCard(
        rank: Int,
        previousRank: Int,
        totalParticipants: Int,
        weeklyPoints: Int,
        displayName: String,
        titleName: String,
        bannerId: String?,
        accentColorId: String,
        frameId: String,
        pinnedBadgeIds: List<String>,
        aspectRatio: AspectRatio = CardType.RANK.defaultAspectRatio
    ): RankCard = RankCard(
        rank = rank,
        previousRank = previousRank,
        totalParticipants = totalParticipants,
        weeklyPoints = weeklyPoints,
        displayName = displayName,
        titleName = titleName,
        bannerId = bannerId,
        accentColorId = accentColorId,
        frameId = frameId,
        pinnedBadgeIds = pinnedBadgeIds,
        aspectRatio = aspectRatio
    )

    /**
     * Creates a journey summary share card.
     */
    fun createJourneyCard(
        periodType: PeriodType,
        periodLabel: String,
        pointsEarned: Int,
        wordsLearned: Int,
        journalEntries: Int,
        streakDays: Int,
        achievementsUnlocked: Int,
        displayName: String,
        titleName: String,
        bannerId: String?,
        accentColorId: String,
        frameId: String,
        aspectRatio: AspectRatio = CardType.JOURNEY.defaultAspectRatio
    ): JourneyCard = JourneyCard(
        periodType = periodType,
        periodLabel = periodLabel,
        pointsEarned = pointsEarned,
        wordsLearned = wordsLearned,
        journalEntries = journalEntries,
        streakDays = streakDays,
        achievementsUnlocked = achievementsUnlocked,
        displayName = displayName,
        titleName = titleName,
        bannerId = bannerId,
        accentColorId = accentColorId,
        frameId = frameId,
        aspectRatio = aspectRatio
    )

    // =========================================================================
    // Utility Functions
    // =========================================================================

    /**
     * Gets recommended aspect ratio for a card type.
     */
    fun getRecommendedAspectRatio(cardType: CardType): AspectRatio =
        cardType.defaultAspectRatio

    /**
     * Gets all supported aspect ratios for a card type.
     */
    fun getSupportedAspectRatios(cardType: CardType): List<AspectRatio> =
        AspectRatio.entries.toList()

    /**
     * Gets the share text for a card.
     */
    fun getShareText(card: ShareCard): String = when (card) {
        is ProfileCard -> "Check out my journey on Prody! #Prody #PersonalGrowth"
        is AchievementCard -> "I just unlocked \"${card.achievementName}\" on Prody! ${card.celebrationMessage} #Prody"
        is BannerUnlockCard -> "New banner unlocked: ${card.bannerName}! #Prody"
        is StreakCard -> "${card.streakMessage}! #Prody #${card.currentStreak}DayStreak"
        is LevelUpCard -> "Level ${card.newLevel} reached! I'm now a ${card.newLevelName}. #Prody"
        is RankCard -> "Ranked #${card.rank} this week! #Prody #Leaderboard"
        is JourneyCard -> "My ${card.periodType.displayName.lowercase()} journey on Prody: ${card.pointsEarned} points earned! #Prody"
        else -> "Sharing from Prody #PersonalGrowth"
    }

    /**
     * Gets watermark text for the card.
     * Subtle branding that doesn't overpower the user's achievement.
     */
    fun getWatermarkText(): String = "prody"

    /**
     * Determines if a card should use animated elements.
     * Based on the cosmetic animation budget.
     */
    fun shouldAnimate(card: ShareCard): Boolean = when (card) {
        is AchievementCard -> card.achievementRarity >= CosmeticRarity.EPIC
        is BannerUnlockCard -> card.bannerRarity >= CosmeticRarity.EPIC
        is LevelUpCard -> card.newLevel >= 5
        else -> false
    }
}
