package com.prody.prashant.domain.social

import com.prody.prashant.data.local.dao.SocialDao
import com.prody.prashant.data.local.entity.CirclePrivacySettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Privacy Manager for Social Accountability Circles.
 *
 * Core Principle: NEVER share actual journal content. Only share what the user
 * explicitly allows - statistics and progress metrics.
 *
 * Privacy Tiers:
 * - Fully Private: No stats shared, only presence in circle
 * - Selective: User chooses exactly what to share
 * - Open: All stats visible (still NO journal content)
 */
@Singleton
class SocialPrivacyManager @Inject constructor(
    private val socialDao: SocialDao
) {

    companion object {
        private const val GLOBAL_CIRCLE_ID = "global"
    }

    /**
     * Get privacy settings for a specific circle.
     * Falls back to global settings if circle-specific settings don't exist.
     */
    suspend fun getPrivacySettings(
        userId: String,
        circleId: String
    ): PrivacySettings {
        // Try circle-specific settings first
        val circleSettings = socialDao.getPrivacySettings(userId, circleId)
        if (circleSettings != null) {
            return circleSettings.toDomain()
        }

        // Fall back to global settings
        val globalSettings = socialDao.getGlobalPrivacySettings(userId)
        return globalSettings?.toDomain() ?: getDefaultPrivacySettings()
    }

    /**
     * Observe global privacy settings.
     */
    fun observeGlobalPrivacySettings(userId: String): Flow<PrivacySettings> {
        return socialDao.observeGlobalPrivacySettings(userId)
            .map { it?.toDomain() ?: getDefaultPrivacySettings() }
    }

    /**
     * Get default privacy settings (balanced approach).
     */
    fun getDefaultPrivacySettings(): PrivacySettings {
        return PrivacySettings(
            shareStreakCount = true,
            shareEntryCount = true,
            shareMeditationStats = false,
            shareChallengeParticipation = true,
            allowNudgesFromMembers = true,
            showOnlineStatus = false
        )
    }

    /**
     * Update global privacy settings.
     */
    suspend fun updateGlobalPrivacySettings(
        userId: String,
        settings: PrivacySettings
    ) {
        val entity = CirclePrivacySettingsEntity(
            userId = userId,
            circleId = GLOBAL_CIRCLE_ID,
            shareStreakCount = settings.shareStreakCount,
            shareEntryCount = settings.shareEntryCount,
            shareMeditationStats = settings.shareMeditationStats,
            shareChallengeParticipation = settings.shareChallengeParticipation,
            allowNudgesFromMembers = settings.allowNudgesFromMembers,
            showOnlineStatus = settings.showOnlineStatus,
            updatedAt = System.currentTimeMillis()
        )
        socialDao.insertPrivacySettings(entity)
    }

    /**
     * Update privacy settings for a specific circle.
     */
    suspend fun updateCirclePrivacySettings(
        userId: String,
        circleId: String,
        settings: PrivacySettings
    ) {
        val entity = CirclePrivacySettingsEntity(
            userId = userId,
            circleId = circleId,
            shareStreakCount = settings.shareStreakCount,
            shareEntryCount = settings.shareEntryCount,
            shareMeditationStats = settings.shareMeditationStats,
            shareChallengeParticipation = settings.shareChallengeParticipation,
            allowNudgesFromMembers = settings.allowNudgesFromMembers,
            showOnlineStatus = settings.showOnlineStatus,
            updatedAt = System.currentTimeMillis()
        )
        socialDao.insertPrivacySettings(entity)
    }

    /**
     * Check if a specific stat type can be shared.
     */
    suspend fun canShareStat(
        userId: String,
        circleId: String,
        statType: StatType
    ): Boolean {
        val settings = getPrivacySettings(userId, circleId)
        return when (statType) {
            StatType.STREAK -> settings.shareStreakCount
            StatType.ENTRY_COUNT -> settings.shareEntryCount
            StatType.MEDITATION -> settings.shareMeditationStats
            StatType.CHALLENGE -> settings.shareChallengeParticipation
            StatType.ONLINE_STATUS -> settings.showOnlineStatus
        }
    }

    /**
     * Filter member stats based on their privacy settings.
     */
    suspend fun filterMemberStats(
        memberStats: MemberStats,
        circleId: String
    ): MemberStats {
        val settings = getPrivacySettings(memberStats.userId, circleId)
        return memberStats.getVisibleStats(settings)
    }

    /**
     * Create a privacy-safe update message.
     * This ensures no sensitive journal content is included.
     */
    fun createSafeUpdateMessage(
        updateType: UpdateType,
        value: Int? = null,
        displayName: String
    ): String {
        return when (updateType) {
            UpdateType.STREAK_MILESTONE -> {
                val milestone = value?.let { SocialStreakMilestone.fromStreak(it) }
                "$displayName reached a ${milestone?.days ?: value}-day streak! ${milestone?.emoji ?: "🔥"}"
            }
            UpdateType.ENTRY_COUNT -> {
                "$displayName wrote their ${value}${getOrdinalSuffix(value ?: 0)} entry!"
            }
            UpdateType.CHECK_IN -> {
                "$displayName completed their daily ritual ✅"
            }
            UpdateType.ENCOURAGEMENT -> {
                "$displayName shared encouragement 💪"
            }
            UpdateType.CHALLENGE_PROGRESS -> {
                "$displayName made progress on a challenge 🎯"
            }
            UpdateType.MILESTONE_100 -> {
                "$displayName reached 100 entries! 💯"
            }
            UpdateType.MILESTONE_365 -> {
                "$displayName completed a full year! 🎉"
            }
            UpdateType.BACK_FROM_BREAK -> {
                "$displayName is back! 👋"
            }
        }
    }

    /**
     * Validate that content is privacy-safe (no journal excerpts).
     */
    fun isContentSafe(content: String): Boolean {
        // Check length - updates should be short
        if (content.length > 200) return false

        // Check for common journal indicators
        val journalPatterns = listOf(
            "dear diary",
            "today i felt",
            "i'm feeling",
            "my thoughts",
            "i think",
            "i feel"
        )

        val lowerContent = content.lowercase()
        if (journalPatterns.any { lowerContent.contains(it) }) {
            return false
        }

        return true
    }

    /**
     * Get recommended privacy settings based on circle type.
     */
    fun getRecommendedSettings(circleType: CircleType): PrivacySettings {
        return when (circleType) {
            CircleType.CLOSE_FRIENDS -> PrivacySettings(
                shareStreakCount = true,
                shareEntryCount = true,
                shareMeditationStats = true,
                shareChallengeParticipation = true,
                allowNudgesFromMembers = true,
                showOnlineStatus = true
            )
            CircleType.ACCOUNTABILITY_GROUP -> PrivacySettings(
                shareStreakCount = true,
                shareEntryCount = true,
                shareMeditationStats = false,
                shareChallengeParticipation = true,
                allowNudgesFromMembers = true,
                showOnlineStatus = false
            )
            CircleType.CASUAL -> PrivacySettings(
                shareStreakCount = true,
                shareEntryCount = false,
                shareMeditationStats = false,
                shareChallengeParticipation = true,
                allowNudgesFromMembers = true,
                showOnlineStatus = false
            )
        }
    }

    private fun getOrdinalSuffix(number: Int): String {
        return when {
            number % 100 in 11..13 -> "th"
            number % 10 == 1 -> "st"
            number % 10 == 2 -> "nd"
            number % 10 == 3 -> "rd"
            else -> "th"
        }
    }
}

enum class StatType {
    STREAK,
    ENTRY_COUNT,
    MEDITATION,
    CHALLENGE,
    ONLINE_STATUS
}

enum class CircleType {
    CLOSE_FRIENDS,
    ACCOUNTABILITY_GROUP,
    CASUAL
}

private fun CirclePrivacySettingsEntity.toDomain(): PrivacySettings {
    return PrivacySettings(
        shareStreakCount = shareStreakCount,
        shareEntryCount = shareEntryCount,
        shareMeditationStats = shareMeditationStats,
        shareChallengeParticipation = shareChallengeParticipation,
        allowNudgesFromMembers = allowNudgesFromMembers,
        showOnlineStatus = showOnlineStatus
    )
}
