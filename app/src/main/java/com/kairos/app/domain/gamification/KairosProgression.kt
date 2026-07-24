package com.kairos.app.domain.gamification

import kotlin.math.roundToInt

/**
 * The small, canonical progression policy for Kairos.
 *
 * Progress is awarded for evidence of learning and reflection, never for merely
 * opening the application. Repeated low-effort actions taper during a day so
 * points remain a record of meaningful practice rather than an engagement trap.
 */
enum class GrowthDimension {
    LEARNING,
    REFLECTION,
    MEMORY,
    CONSISTENCY
}

enum class GrowthAction(
    val dimension: GrowthDimension,
    val basePoints: Int,
    val dailyFullRewardLimit: Int,
    val dailyHardLimit: Int
) {
    VOCABULARY_RECALLED(GrowthDimension.LEARNING, 12, 8, 16),
    VOCABULARY_USED(GrowthDimension.LEARNING, 22, 5, 10),
    REFLECTION_COMPLETED(GrowthDimension.REFLECTION, 28, 2, 3),
    REFLECTION_REVISITED(GrowthDimension.REFLECTION, 8, 3, 6),
    FUTURE_MESSAGE_WRITTEN(GrowthDimension.MEMORY, 26, 1, 2),
    FUTURE_MESSAGE_OPENED(GrowthDimension.MEMORY, 16, 2, 4),
    FUTURE_MESSAGE_REPLIED(GrowthDimension.MEMORY, 30, 2, 3),
    DAILY_MOMENT_COMPLETED(GrowthDimension.CONSISTENCY, 14, 1, 1)
}

data class GrowthReward(
    val action: GrowthAction,
    val points: Int,
    val dimension: GrowthDimension,
    val reason: String,
    val reachedDailyLimit: Boolean
)

data class GrowthDay(
    val actionCounts: Map<GrowthAction, Int> = emptyMap(),
    val activeDimensions: Set<GrowthDimension> = emptySet(),
    val totalPoints: Int = 0
)

data class GrowthResult(
    val reward: GrowthReward,
    val updatedDay: GrowthDay
)

data class GrowthProfile(
    val learning: Int = 0,
    val reflection: Int = 0,
    val memory: Int = 0,
    val consistency: Int = 0
) {
    val total: Int get() = learning + reflection + memory + consistency

    fun add(dimension: GrowthDimension, points: Int): GrowthProfile = when (dimension) {
        GrowthDimension.LEARNING -> copy(learning = learning + points)
        GrowthDimension.REFLECTION -> copy(reflection = reflection + points)
        GrowthDimension.MEMORY -> copy(memory = memory + points)
        GrowthDimension.CONSISTENCY -> copy(consistency = consistency + points)
    }
}

object KairosProgressionPolicy {
    /**
     * @param quality Evidence quality from 0.5 to 1.25. Callers should derive it
     * from real outcomes such as recall grade or reflection completeness.
     */
    fun award(
        action: GrowthAction,
        day: GrowthDay,
        quality: Double = 1.0
    ): GrowthResult {
        val completed = day.actionCounts[action] ?: 0
        val multiplier = when {
            completed >= action.dailyHardLimit -> 0.0
            completed >= action.dailyFullRewardLimit -> 0.35
            else -> 1.0
        }
        val safeQuality = quality.coerceIn(0.5, 1.25)
        val points = (action.basePoints * multiplier * safeQuality).roundToInt()
        val nextCount = completed + 1
        val reason = when {
            points == 0 -> "Daily practice limit reached"
            multiplier < 1.0 -> "Practice counted with reduced points"
            safeQuality > 1.0 -> "Strong evidence of learning"
            else -> "Meaningful practice completed"
        }
        val reward = GrowthReward(
            action = action,
            points = points,
            dimension = action.dimension,
            reason = reason,
            reachedDailyLimit = nextCount >= action.dailyHardLimit
        )
        return GrowthResult(
            reward = reward,
            updatedDay = day.copy(
                actionCounts = day.actionCounts + (action to nextCount),
                activeDimensions = day.activeDimensions + action.dimension,
                totalPoints = day.totalPoints + points
            )
        )
    }

    /** A balanced day requires practice in at least two distinct dimensions. */
    fun balancedDayBonus(day: GrowthDay): Int = when (day.activeDimensions.size) {
        0, 1 -> 0
        2 -> 8
        3 -> 14
        else -> 20
    }

    fun levelFor(totalPoints: Int): Int {
        if (totalPoints <= 0) return 1
        var level = 1
        var remaining = totalPoints
        while (remaining >= pointsToNextLevel(level)) {
            remaining -= pointsToNextLevel(level)
            level += 1
        }
        return level
    }

    fun progressWithinLevel(totalPoints: Int): Float {
        if (totalPoints <= 0) return 0f
        var level = 1
        var remaining = totalPoints
        var threshold = pointsToNextLevel(level)
        while (remaining >= threshold) {
            remaining -= threshold
            level += 1
            threshold = pointsToNextLevel(level)
        }
        return (remaining.toFloat() / threshold).coerceIn(0f, 1f)
    }

    private fun pointsToNextLevel(level: Int): Int = 120 + ((level - 1) * 35)
}
