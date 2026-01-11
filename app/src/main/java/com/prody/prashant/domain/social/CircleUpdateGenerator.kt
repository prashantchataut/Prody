package com.prody.prashant.domain.social

import com.prody.prashant.data.local.dao.SocialDao
import com.prody.prashant.data.local.entity.CircleUpdateEntity
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Automatic Update Generator for Social Accountability Circles.
 *
 * Generates privacy-safe updates when users hit milestones.
 * These updates celebrate progress without revealing journal content.
 */
@Singleton
class CircleUpdateGenerator @Inject constructor(
    private val socialDao: SocialDao,
    private val privacyManager: SocialPrivacyManager
) {

    /**
     * Generate streak milestone update.
     */
    suspend fun generateStreakUpdate(
        userId: String,
        displayName: String,
        streakDays: Int
    ) {
        // Only generate for milestones
        if (!StreakMilestone.isStreakMilestone(streakDays)) return

        val circles = socialDao.getUserCircles(userId)
        circles.forEach { circle ->
            // Check privacy settings
            if (!privacyManager.canShareStat(userId, circle.id, StatType.STREAK)) {
                return@forEach
            }

            val content = privacyManager.createSafeUpdateMessage(
                UpdateType.STREAK_MILESTONE,
                streakDays,
                displayName
            )

            val metadata = JSONObject().apply {
                put("streakDays", streakDays)
                put("milestone", StreakMilestone.fromStreak(streakDays)?.days ?: streakDays)
            }

            val update = CircleUpdateEntity(
                circleId = circle.id,
                userId = userId,
                updateType = UpdateType.STREAK_MILESTONE.name,
                content = content,
                metadata = metadata.toString(),
                createdAt = System.currentTimeMillis()
            )

            socialDao.insertUpdate(update)
            socialDao.updateLastActivity(circle.id)
        }
    }

    /**
     * Generate entry count milestone update.
     */
    suspend fun generateEntryMilestoneUpdate(
        userId: String,
        displayName: String,
        entryCount: Int
    ) {
        // Generate for specific milestones
        val milestones = listOf(1, 5, 10, 25, 50, 100, 250, 500, 1000)
        if (entryCount !in milestones) return

        val circles = socialDao.getUserCircles(userId)
        circles.forEach { circle ->
            // Check privacy settings
            if (!privacyManager.canShareStat(userId, circle.id, StatType.ENTRY_COUNT)) {
                return@forEach
            }

            val updateType = when (entryCount) {
                100 -> UpdateType.MILESTONE_100
                365 -> UpdateType.MILESTONE_365
                else -> UpdateType.ENTRY_COUNT
            }

            val content = privacyManager.createSafeUpdateMessage(
                updateType,
                entryCount,
                displayName
            )

            val metadata = JSONObject().apply {
                put("entryCount", entryCount)
                put("milestone", true)
            }

            val update = CircleUpdateEntity(
                circleId = circle.id,
                userId = userId,
                updateType = updateType.name,
                content = content,
                metadata = metadata.toString(),
                createdAt = System.currentTimeMillis()
            )

            socialDao.insertUpdate(update)
            socialDao.updateLastActivity(circle.id)
        }
    }

    /**
     * Generate daily check-in update.
     */
    suspend fun generateCheckInUpdate(
        userId: String,
        displayName: String,
        checkInType: String = "ritual"
    ) {
        val circles = socialDao.getUserCircles(userId)
        circles.forEach { circle ->
            val content = when (checkInType) {
                "ritual" -> "$displayName completed their daily ritual ✅"
                "morning" -> "$displayName started their day with intention 🌅"
                "evening" -> "$displayName reflected on their day 🌙"
                else -> "$displayName checked in ✅"
            }

            val metadata = JSONObject().apply {
                put("checkInType", checkInType)
                put("time", System.currentTimeMillis())
            }

            val update = CircleUpdateEntity(
                circleId = circle.id,
                userId = userId,
                updateType = UpdateType.CHECK_IN.name,
                content = content,
                metadata = metadata.toString(),
                createdAt = System.currentTimeMillis()
            )

            socialDao.insertUpdate(update)
            socialDao.updateLastActivity(circle.id)
        }
    }

    /**
     * Generate "back from break" update.
     */
    suspend fun generateReturnUpdate(
        userId: String,
        displayName: String,
        daysSinceLastEntry: Int
    ) {
        // Only for breaks of 3+ days
        if (daysSinceLastEntry < 3) return

        val circles = socialDao.getUserCircles(userId)
        circles.forEach { circle ->
            val content = when {
                daysSinceLastEntry >= 30 -> "$displayName is back after a long break! Welcome back! 🎊"
                daysSinceLastEntry >= 7 -> "$displayName returned after a week! 👋"
                else -> "$displayName is back! 👋"
            }

            val metadata = JSONObject().apply {
                put("daysSinceLastEntry", daysSinceLastEntry)
            }

            val update = CircleUpdateEntity(
                circleId = circle.id,
                userId = userId,
                updateType = UpdateType.BACK_FROM_BREAK.name,
                content = content,
                metadata = metadata.toString(),
                createdAt = System.currentTimeMillis()
            )

            socialDao.insertUpdate(update)
            socialDao.updateLastActivity(circle.id)
        }
    }

    /**
     * Generate challenge progress update.
     */
    suspend fun generateChallengeProgressUpdate(
        userId: String,
        displayName: String,
        challengeId: String,
        progress: Int,
        target: Int
    ) {
        val challenge = socialDao.getChallengeById(challengeId) ?: return
        val circle = socialDao.getCircleById(challenge.circleId) ?: return

        // Check privacy settings
        if (!privacyManager.canShareStat(userId, circle.id, StatType.CHALLENGE)) {
            return
        }

        val progressPercent = (progress.toFloat() / target.toFloat() * 100).toInt()

        // Generate updates at milestones: 25%, 50%, 75%, 100%
        val shouldGenerate = when (progressPercent) {
            in 25..26, in 50..51, in 75..76, 100 -> true
            else -> false
        }

        if (!shouldGenerate) return

        val content = when {
            progressPercent >= 100 -> "$displayName completed the challenge '${challenge.title}'! 🎉"
            progressPercent >= 75 -> "$displayName is 75% through '${challenge.title}'! 🎯"
            progressPercent >= 50 -> "$displayName reached halfway on '${challenge.title}' 💪"
            progressPercent >= 25 -> "$displayName made progress on '${challenge.title}' 📈"
            else -> "$displayName started '${challenge.title}' 🚀"
        }

        val metadata = JSONObject().apply {
            put("challengeId", challengeId)
            put("challengeTitle", challenge.title)
            put("progress", progress)
            put("target", target)
            put("progressPercent", progressPercent)
        }

        val update = CircleUpdateEntity(
            circleId = circle.id,
            userId = userId,
            updateType = UpdateType.CHALLENGE_PROGRESS.name,
            content = content,
            metadata = metadata.toString(),
            createdAt = System.currentTimeMillis()
        )

        socialDao.insertUpdate(update)
        socialDao.updateLastActivity(circle.id)
    }

    /**
     * Generate encouragement update (manual).
     */
    suspend fun generateEncouragementUpdate(
        userId: String,
        displayName: String,
        circleId: String,
        message: String
    ) {
        // Validate message is safe
        if (!privacyManager.isContentSafe(message)) {
            throw IllegalArgumentException("Message contains sensitive content")
        }

        val content = "$displayName: $message 💪"

        val metadata = JSONObject().apply {
            put("isManual", true)
        }

        val update = CircleUpdateEntity(
            circleId = circleId,
            userId = userId,
            updateType = UpdateType.ENCOURAGEMENT.name,
            content = content,
            metadata = metadata.toString(),
            createdAt = System.currentTimeMillis()
        )

        socialDao.insertUpdate(update)
        socialDao.updateLastActivity(circleId)
    }

    /**
     * Batch generate updates for multiple events.
     */
    suspend fun generateBatchUpdates(
        userId: String,
        displayName: String,
        events: List<UpdateEvent>
    ) {
        events.forEach { event ->
            when (event) {
                is UpdateEvent.StreakMilestone -> {
                    generateStreakUpdate(userId, displayName, event.streakDays)
                }
                is UpdateEvent.EntryMilestone -> {
                    generateEntryMilestoneUpdate(userId, displayName, event.entryCount)
                }
                is UpdateEvent.CheckIn -> {
                    generateCheckInUpdate(userId, displayName, event.checkInType)
                }
                is UpdateEvent.Return -> {
                    generateReturnUpdate(userId, displayName, event.daysSinceLastEntry)
                }
                is UpdateEvent.ChallengeProgress -> {
                    generateChallengeProgressUpdate(
                        userId,
                        displayName,
                        event.challengeId,
                        event.progress,
                        event.target
                    )
                }
            }
        }
    }

    /**
     * Clean up old updates (older than 90 days).
     */
    suspend fun cleanupOldUpdates(circleId: String) {
        val ninetyDaysAgo = System.currentTimeMillis() - (90 * 24 * 60 * 60 * 1000L)
        socialDao.deleteOldUpdates(circleId, ninetyDaysAgo)
    }

    /**
     * Get update suggestions for a user (what they might want to share).
     */
    suspend fun getUpdateSuggestions(
        userId: String,
        displayName: String
    ): List<UpdateSuggestion> {
        val suggestions = mutableListOf<UpdateSuggestion>()

        // This would typically check recent activity and suggest updates
        // For now, return empty list - can be expanded based on user activity

        return suggestions
    }
}

/**
 * Events that can trigger automatic updates.
 */
sealed class UpdateEvent {
    data class StreakMilestone(val streakDays: Int) : UpdateEvent()
    data class EntryMilestone(val entryCount: Int) : UpdateEvent()
    data class CheckIn(val checkInType: String) : UpdateEvent()
    data class Return(val daysSinceLastEntry: Int) : UpdateEvent()
    data class ChallengeProgress(
        val challengeId: String,
        val progress: Int,
        val target: Int
    ) : UpdateEvent()
}

/**
 * Suggested updates for manual posting.
 */
data class UpdateSuggestion(
    val type: UpdateType,
    val title: String,
    val description: String,
    val icon: String
)
