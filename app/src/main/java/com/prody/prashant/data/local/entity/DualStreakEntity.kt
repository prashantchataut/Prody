package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Dual Streak Entity - Two independent streaks for different engagement types.
 *
 * Philosophy: Different types of engagement deserve different recognition.
 * Wisdom Streak (Easy): Quick daily wisdom engagement (30 seconds)
 * Reflection Streak (Meaningful): Deep journaling or evening reflection
 *
 * Design Principles:
 * - Two independent streaks with different purposes
 * - Each streak has its own grace period (one skip per 14 days)
 * - Wisdom Streak: Lower barrier, encourages daily check-ins
 * - Reflection Streak: Higher value, rewards deep engagement
 * - Grace periods feel encouraging, not punishing
 * - The system understands that life happens
 *
 * Wisdom Streak Triggers:
 * - Viewing daily quote, word, proverb, or idiom
 * - Takes ~30 seconds maximum
 * - Lower tier rewards (small XP, minor badges)
 *
 * Reflection Streak Triggers:
 * - Writing a journal entry (any length)
 * - Completing evening reflection
 * - Higher tier rewards (major XP, significant badges)
 *
 * Grace Period Logic:
 * - Each streak gets ONE grace day per 14-day period
 * - Grace day is visible in UI with countdown
 * - Missing one day with grace available = streak preserved
 * - Grace day resets 14 days after last use
 * - Encourages consistency without fear of failure
 */
@Entity(
    tableName = "dual_streaks",
    indices = [Index(value = ["userId"], unique = true)]
)
data class DualStreakEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",

    // ============== WISDOM STREAK ==============
    // Quick daily engagement - viewing wisdom content
    val wisdomStreakCurrent: Int = 0,
    val wisdomStreakLongest: Int = 0,
    val wisdomLastMaintainedDate: Long = 0, // Timestamp of last day wisdom was viewed

    // Grace period for wisdom streak (one skip per 14 days)
    val wisdomGracePeriodUsed: Boolean = false,
    val wisdomGracePeriodUsedDate: Long = 0, // When the grace was used
    val wisdomGracePeriodResetDate: Long = 0, // When grace becomes available again (14 days after use)

    // ============== REFLECTION STREAK ==============
    // Meaningful engagement - journaling or reflection
    val reflectionStreakCurrent: Int = 0,
    val reflectionStreakLongest: Int = 0,
    val reflectionLastMaintainedDate: Long = 0, // Timestamp of last day reflection was done

    // Grace period for reflection streak (one skip per 14 days)
    val reflectionGracePeriodUsed: Boolean = false,
    val reflectionGracePeriodUsedDate: Long = 0, // When the grace was used
    val reflectionGracePeriodResetDate: Long = 0, // When grace becomes available again (14 days after use)

    // ============== METADATA ==============
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val GRACE_PERIOD_DAYS = 14 // One grace day per 14-day period
        const val MILLIS_PER_DAY = 86400000L // 24 hours in milliseconds

        /**
         * Check if wisdom grace period is available.
         */
        fun isWisdomGraceAvailable(entity: DualStreakEntity): Boolean {
            val now = System.currentTimeMillis()
            return !entity.wisdomGracePeriodUsed || now >= entity.wisdomGracePeriodResetDate
        }

        /**
         * Check if reflection grace period is available.
         */
        fun isReflectionGraceAvailable(entity: DualStreakEntity): Boolean {
            val now = System.currentTimeMillis()
            return !entity.reflectionGracePeriodUsed || now >= entity.reflectionGracePeriodResetDate
        }

        /**
         * Get days until wisdom grace period resets.
         */
        fun daysUntilWisdomGraceReset(entity: DualStreakEntity): Int {
            if (!entity.wisdomGracePeriodUsed) return 0
            val now = System.currentTimeMillis()
            if (now >= entity.wisdomGracePeriodResetDate) return 0
            return ((entity.wisdomGracePeriodResetDate - now) / MILLIS_PER_DAY).toInt() + 1
        }

        /**
         * Get days until reflection grace period resets.
         */
        fun daysUntilReflectionGraceReset(entity: DualStreakEntity): Int {
            if (!entity.reflectionGracePeriodUsed) return 0
            val now = System.currentTimeMillis()
            if (now >= entity.reflectionGracePeriodResetDate) return 0
            return ((entity.reflectionGracePeriodResetDate - now) / MILLIS_PER_DAY).toInt() + 1
        }
    }

    /**
     * Check if wisdom was maintained today.
     */
    fun isWisdomMaintainedToday(): Boolean {
        val now = System.currentTimeMillis()
        val todayStart = getTodayStartTimestamp()
        return wisdomLastMaintainedDate >= todayStart
    }

    /**
     * Check if reflection was maintained today.
     */
    fun isReflectionMaintainedToday(): Boolean {
        val now = System.currentTimeMillis()
        val todayStart = getTodayStartTimestamp()
        return reflectionLastMaintainedDate >= todayStart
    }

    /**
     * Check if wisdom streak was maintained yesterday.
     */
    fun wasWisdomMaintainedYesterday(): Boolean {
        val yesterdayStart = getYesterdayStartTimestamp()
        val yesterdayEnd = getTodayStartTimestamp()
        return wisdomLastMaintainedDate in yesterdayStart..<yesterdayEnd
    }

    /**
     * Check if reflection streak was maintained yesterday.
     */
    fun wasReflectionMaintainedYesterday(): Boolean {
        val yesterdayStart = getYesterdayStartTimestamp()
        val yesterdayEnd = getTodayStartTimestamp()
        return reflectionLastMaintainedDate in yesterdayStart..<yesterdayEnd
    }

    private fun getTodayStartTimestamp(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getYesterdayStartTimestamp(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
