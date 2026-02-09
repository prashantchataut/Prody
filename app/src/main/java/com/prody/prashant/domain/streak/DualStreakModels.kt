package com.prody.prashant.domain.streak

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import java.time.ZoneId

/**
 * Streak type enum for dual streak system.
 */
enum class StreakType {
    WISDOM,     // Quick daily wisdom viewing (30 seconds)
    REFLECTION  // Deep journaling or evening reflection
}

/**
 * Result of a streak maintenance operation.
 */
sealed class StreakResult {
    data class Success(
        val streakType: StreakType,
        val newStreakCount: Int,
        val isNewLongest: Boolean,
        val message: String
    ) : StreakResult()

    data class AlreadyMaintained(
        val streakType: StreakType,
        val currentStreak: Int
    ) : StreakResult()

    data class GraceApplied(
        val streakType: StreakType,
        val preservedStreak: Int,
        val daysUntilGraceReset: Int
    ) : StreakResult()

    data class Broken(
        val streakType: StreakType,
        val previousStreak: Int,
        val graceWasAvailable: Boolean
    ) : StreakResult()

    data class Error(
        val streakType: StreakType,
        val message: String
    ) : StreakResult()
}

/**
 * Complete dual streak status for display in UI.
 */
@Immutable
data class DualStreakStatus(
    val wisdomStreak: StreakInfo,
    val reflectionStreak: StreakInfo
) {
    companion object {
        fun empty(): DualStreakStatus {
            return DualStreakStatus(
                wisdomStreak = StreakInfo.empty(StreakType.WISDOM),
                reflectionStreak = StreakInfo.empty(StreakType.REFLECTION)
            )
        }
    }
}

/**
 * Information about a single streak (Wisdom or Reflection).
 */
@Immutable
data class StreakInfo(
    val type: StreakType,
    val current: Int,
    val longest: Int,
    val lastMaintainedDate: LocalDate?,
    val maintainedToday: Boolean,
    val gracePeriodAvailable: Boolean,
    val daysUntilGracePeriodReset: Int,
    val isAtRisk: Boolean // True if streak will break tomorrow without action
) {
    companion object {
        fun empty(type: StreakType): StreakInfo {
            return StreakInfo(
                type = type,
                current = 0,
                longest = 0,
                lastMaintainedDate = null,
                maintainedToday = false,
                gracePeriodAvailable = true,
                daysUntilGracePeriodReset = 0,
                isAtRisk = false
            )
        }
    }

    /**
     * Get encouragement message based on streak state.
     */
    fun getEncouragementMessage(): String {
        return when {
            current == 0 -> "Start your first ${type.name.lowercase()} streak!"
            maintainedToday -> "You're on fire! ${current} day${if (current > 1) "s" else ""} strong!"
            isAtRisk && gracePeriodAvailable -> "Keep it alive! Grace day available if needed."
            isAtRisk -> "Don't break the streak! ${current} days and counting."
            current >= 7 -> "Amazing! ${current} days of consistency!"
            else -> "Keep going! Day ${current} of your journey."
        }
    }

    /**
     * Get milestone reached (if any).
     */
    fun getMilestone(): String? {
        return when (current) {
            7 -> "Week Warrior"
            14 -> "Two Week Champion"
            30 -> "Monthly Master"
            60 -> "Two Month Legend"
            100 -> "Century Club"
            365 -> "Year of Dedication"
            else -> null
        }
    }
}

/**
 * Streak maintenance trigger events.
 */
sealed class StreakTrigger {
    // Wisdom streak triggers (quick engagement)
    object ViewedQuote : StreakTrigger()
    object ViewedWord : StreakTrigger()
    object ViewedProverb : StreakTrigger()
    object ViewedIdiom : StreakTrigger()

    // Reflection streak triggers (deep engagement)
    data class WroteJournal(val wordCount: Int) : StreakTrigger()
    object CompletedEveningReflection : StreakTrigger()
    object CompletedMicroEntry : StreakTrigger()
}
