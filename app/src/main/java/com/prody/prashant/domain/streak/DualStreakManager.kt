package com.prody.prashant.domain.streak

import com.prody.prashant.data.local.dao.DualStreakDao
import com.prody.prashant.data.local.entity.DualStreakEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Dual Streak Manager - Business logic for the dual streak system.
 *
 * Manages two independent streaks:
 * - Wisdom Streak (Easy): Viewing daily wisdom content (quote, word, proverb, idiom)
 * - Reflection Streak (Meaningful): Writing journal entries or completing reflections
 *
 * Philosophy:
 * - Different engagement types deserve different recognition
 * - Wisdom streak: Quick wins, lower barrier to entry
 * - Reflection streak: Deeper commitment, higher rewards
 * - Grace periods make the system forgiving and encouraging
 * - The app understands that life happens
 *
 * Grace Period Logic:
 * - Each streak gets ONE skip per 14-day period
 * - Grace day is visible in UI with countdown
 * - Encourages consistency without fear of harsh punishment
 */
@Singleton
class DualStreakManager @Inject constructor(
    private val dualStreakDao: DualStreakDao
) {

    companion object {
        private const val TAG = "DualStreakManager"

        // Wisdom Streak Rewards (Lower tier)
        const val WISDOM_STREAK_BASE_XP = 5
        const val WISDOM_STREAK_MILESTONE_XP = 25

        // Reflection Streak Rewards (Higher tier)
        const val REFLECTION_STREAK_BASE_XP = 15
        const val REFLECTION_STREAK_MILESTONE_XP = 50
    }

    // ============== WISDOM STREAK OPERATIONS ==============

    /**
     * Maintain wisdom streak for today.
     * Call this when user views any daily wisdom content.
     *
     * @param userId User identifier (default "local")
     * @return StreakResult indicating what happened
     */
    suspend fun maintainWisdomStreak(userId: String = "local"): StreakResult {
        return try {
            val streak = dualStreakDao.getDualStreak(userId) ?: DualStreakEntity(userId = userId)

            // Check if already maintained today
            if (streak.isWisdomMaintainedToday()) {
                return StreakResult.AlreadyMaintained(
                    streakType = StreakType.WISDOM,
                    currentStreak = streak.wisdomStreakCurrent
                )
            }

            // Maintain the streak
            val updated = dualStreakDao.maintainWisdomStreak(userId)
            if (!updated) {
                return StreakResult.AlreadyMaintained(
                    streakType = StreakType.WISDOM,
                    currentStreak = streak.wisdomStreakCurrent
                )
            }

            // Get updated streak data
            val newStreak = dualStreakDao.getDualStreak(userId) ?: return StreakResult.Error(
                streakType = StreakType.WISDOM,
                message = "Failed to retrieve updated streak"
            )

            val isNewLongest = newStreak.wisdomStreakCurrent == newStreak.wisdomStreakLongest &&
                    newStreak.wisdomStreakCurrent > streak.wisdomStreakLongest

            StreakResult.Success(
                streakType = StreakType.WISDOM,
                newStreakCount = newStreak.wisdomStreakCurrent,
                isNewLongest = isNewLongest,
                message = getWisdomStreakMessage(newStreak.wisdomStreakCurrent, isNewLongest)
            )
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error maintaining wisdom streak", e)
            StreakResult.Error(
                streakType = StreakType.WISDOM,
                message = e.message ?: "Unknown error"
            )
        }
    }

    /**
     * Apply grace period to wisdom streak if available.
     *
     * @param userId User identifier (default "local")
     * @return StreakResult indicating if grace was applied
     */
    suspend fun applyWisdomGrace(userId: String = "local"): StreakResult {
        return try {
            val streak = dualStreakDao.getDualStreak(userId) ?: return StreakResult.Error(
                streakType = StreakType.WISDOM,
                message = "No streak data found"
            )

            val graceApplied = dualStreakDao.applyWisdomGrace(userId)
            if (!graceApplied) {
                return StreakResult.Error(
                    streakType = StreakType.WISDOM,
                    message = "Grace period not available or not needed"
                )
            }

            val updatedStreak = dualStreakDao.getDualStreak(userId) ?: streak
            val daysUntilReset = DualStreakEntity.daysUntilWisdomGraceReset(updatedStreak)

            StreakResult.GraceApplied(
                streakType = StreakType.WISDOM,
                preservedStreak = updatedStreak.wisdomStreakCurrent,
                daysUntilGraceReset = daysUntilReset
            )
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error applying wisdom grace", e)
            StreakResult.Error(
                streakType = StreakType.WISDOM,
                message = e.message ?: "Unknown error"
            )
        }
    }

    // ============== REFLECTION STREAK OPERATIONS ==============

    /**
     * Maintain reflection streak for today.
     * Call this when user writes a journal entry or completes evening reflection.
     *
     * @param userId User identifier (default "local")
     * @return StreakResult indicating what happened
     */
    suspend fun maintainReflectionStreak(userId: String = "local"): StreakResult {
        return try {
            val streak = dualStreakDao.getDualStreak(userId) ?: DualStreakEntity(userId = userId)

            // Check if already maintained today
            if (streak.isReflectionMaintainedToday()) {
                return StreakResult.AlreadyMaintained(
                    streakType = StreakType.REFLECTION,
                    currentStreak = streak.reflectionStreakCurrent
                )
            }

            // Maintain the streak
            val updated = dualStreakDao.maintainReflectionStreak(userId)
            if (!updated) {
                return StreakResult.AlreadyMaintained(
                    streakType = StreakType.REFLECTION,
                    currentStreak = streak.reflectionStreakCurrent
                )
            }

            // Get updated streak data
            val newStreak = dualStreakDao.getDualStreak(userId) ?: return StreakResult.Error(
                streakType = StreakType.REFLECTION,
                message = "Failed to retrieve updated streak"
            )

            val isNewLongest = newStreak.reflectionStreakCurrent == newStreak.reflectionStreakLongest &&
                    newStreak.reflectionStreakCurrent > streak.reflectionStreakLongest

            StreakResult.Success(
                streakType = StreakType.REFLECTION,
                newStreakCount = newStreak.reflectionStreakCurrent,
                isNewLongest = isNewLongest,
                message = getReflectionStreakMessage(newStreak.reflectionStreakCurrent, isNewLongest)
            )
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error maintaining reflection streak", e)
            StreakResult.Error(
                streakType = StreakType.REFLECTION,
                message = e.message ?: "Unknown error"
            )
        }
    }

    /**
     * Apply grace period to reflection streak if available.
     *
     * @param userId User identifier (default "local")
     * @return StreakResult indicating if grace was applied
     */
    suspend fun applyReflectionGrace(userId: String = "local"): StreakResult {
        return try {
            val streak = dualStreakDao.getDualStreak(userId) ?: return StreakResult.Error(
                streakType = StreakType.REFLECTION,
                message = "No streak data found"
            )

            val graceApplied = dualStreakDao.applyReflectionGrace(userId)
            if (!graceApplied) {
                return StreakResult.Error(
                    streakType = StreakType.REFLECTION,
                    message = "Grace period not available or not needed"
                )
            }

            val updatedStreak = dualStreakDao.getDualStreak(userId) ?: streak
            val daysUntilReset = DualStreakEntity.daysUntilReflectionGraceReset(updatedStreak)

            StreakResult.GraceApplied(
                streakType = StreakType.REFLECTION,
                preservedStreak = updatedStreak.reflectionStreakCurrent,
                daysUntilGraceReset = daysUntilReset
            )
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error applying reflection grace", e)
            StreakResult.Error(
                streakType = StreakType.REFLECTION,
                message = e.message ?: "Unknown error"
            )
        }
    }

    // ============== STATUS QUERIES ==============

    /**
     * Get complete dual streak status (reactive).
     */
    fun getDualStreakStatusFlow(userId: String = "local"): Flow<DualStreakStatus> {
        return dualStreakDao.getDualStreakFlow(userId).map { entity ->
            entity?.toDualStreakStatus() ?: DualStreakStatus.empty()
        }
    }

    /**
     * Get complete dual streak status (one-time).
     */
    suspend fun getDualStreakStatus(userId: String = "local"): DualStreakStatus {
        val entity = dualStreakDao.getDualStreak(userId)
        return entity?.toDualStreakStatus() ?: DualStreakStatus.empty()
    }

    /**
     * Check if grace period is available for a specific streak type.
     */
    suspend fun checkGracePeriodAvailable(
        userId: String = "local",
        streakType: StreakType
    ): Boolean {
        val entity = dualStreakDao.getDualStreak(userId) ?: return true // Grace available by default
        return when (streakType) {
            StreakType.WISDOM -> DualStreakEntity.isWisdomGraceAvailable(entity)
            StreakType.REFLECTION -> DualStreakEntity.isReflectionGraceAvailable(entity)
        }
    }

    // ============== HELPER FUNCTIONS ==============

    /**
     * Convert entity to DualStreakStatus.
     */
    private fun DualStreakEntity.toDualStreakStatus(): DualStreakStatus {
        val wisdomLastDate = if (wisdomLastMaintainedDate > 0) {
            Instant.ofEpochMilli(wisdomLastMaintainedDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        } else null

        val reflectionLastDate = if (reflectionLastMaintainedDate > 0) {
            Instant.ofEpochMilli(reflectionLastMaintainedDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        } else null

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        // Check if streaks are at risk (not maintained today or yesterday)
        val wisdomAtRisk = wisdomLastDate?.isBefore(yesterday) ?: (wisdomStreakCurrent > 0)
        val reflectionAtRisk = reflectionLastDate?.isBefore(yesterday) ?: (reflectionStreakCurrent > 0)

        return DualStreakStatus(
            wisdomStreak = StreakInfo(
                type = StreakType.WISDOM,
                current = wisdomStreakCurrent,
                longest = wisdomStreakLongest,
                lastMaintainedDate = wisdomLastDate,
                maintainedToday = isWisdomMaintainedToday(),
                gracePeriodAvailable = DualStreakEntity.isWisdomGraceAvailable(this),
                daysUntilGracePeriodReset = DualStreakEntity.daysUntilWisdomGraceReset(this),
                isAtRisk = wisdomAtRisk && wisdomStreakCurrent > 0
            ),
            reflectionStreak = StreakInfo(
                type = StreakType.REFLECTION,
                current = reflectionStreakCurrent,
                longest = reflectionStreakLongest,
                lastMaintainedDate = reflectionLastDate,
                maintainedToday = isReflectionMaintainedToday(),
                gracePeriodAvailable = DualStreakEntity.isReflectionGraceAvailable(this),
                daysUntilGracePeriodReset = DualStreakEntity.daysUntilReflectionGraceReset(this),
                isAtRisk = reflectionAtRisk && reflectionStreakCurrent > 0
            )
        )
    }

    /**
     * Get encouraging message for wisdom streak.
     */
    private fun getWisdomStreakMessage(count: Int, isNewLongest: Boolean): String {
        return when {
            isNewLongest && count > 1 -> "New wisdom streak record! $count days! 🔥"
            count == 1 -> "Wisdom streak started! Keep it going! 📚"
            count == 7 -> "Week of wisdom complete! ✨"
            count == 30 -> "30 days of daily wisdom! Incredible! 🌟"
            count % 10 == 0 -> "$count days of wisdom! You're unstoppable! 🚀"
            else -> "Wisdom streak: $count days! Keep learning! 💡"
        }
    }

    /**
     * Get encouraging message for reflection streak.
     */
    private fun getReflectionStreakMessage(count: Int, isNewLongest: Boolean): String {
        return when {
            isNewLongest && count > 1 -> "New reflection record! $count days of deep work! ✍️"
            count == 1 -> "Reflection streak started! Keep reflecting! 🌱"
            count == 7 -> "Week of reflection! You're building something meaningful! 🌿"
            count == 30 -> "30 days of reflection! This is life-changing! 🏆"
            count % 10 == 0 -> "$count days of reflection! Your journey is inspiring! 🎯"
            else -> "Reflection streak: $count days! Keep growing! 🌸"
        }
    }
}
