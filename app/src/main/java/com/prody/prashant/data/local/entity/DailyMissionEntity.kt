package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Daily Mission Entity - Tracks the 3 daily missions.
 *
 * Each day, players get 3 missions:
 * - 1 Reflect mission (journaling)
 * - 1 Sharpen mission (vocabulary/flashcards)
 * - 1 Commit mission (future messages)
 *
 * Missions auto-complete based on user actions (no manual checkboxes).
 * Missions expire at end of day.
 */
@Entity(
    tableName = "daily_missions",
    indices = [
        Index(value = ["date"], unique = false),
        Index(value = ["userId", "date"])
    ]
)
data class DailyMissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val missionId: String, // Unique ID for this mission type
    val userId: String = "local",
    val date: Long, // Day timestamp (start of day)
    val missionType: String, // "reflect", "sharpen", "commit"
    val title: String,
    val description: String,
    val targetValue: Int, // e.g., 120 words, 10 cards
    val currentProgress: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val rewardXp: Int = 0,
    val rewardTokens: Int = 0,
    val rewardClaimed: Boolean = false,
    val difficulty: String = "normal", // "easy", "normal", "hard"
    val createdAt: Long = System.currentTimeMillis()
) {
    fun progressPercent(): Float {
        if (targetValue <= 0) return if (isCompleted) 100f else 0f
        return ((currentProgress.toFloat() / targetValue) * 100).coerceIn(0f, 100f)
    }
}

/**
 * Weekly Trial Entity - The "boss challenge" of the week.
 *
 * Bigger goals that span the entire week.
 * Completing a weekly trial grants rare rewards.
 */
@Entity(
    tableName = "weekly_trials",
    indices = [
        Index(value = ["weekStart"], unique = false),
        Index(value = ["userId", "weekStart"])
    ]
)
data class WeeklyTrialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trialId: String,
    val userId: String = "local",
    val weekStart: Long, // Monday timestamp
    val weekEnd: Long, // Sunday end timestamp
    val title: String,
    val description: String,
    val trialType: String, // "multi_mode", "streak", "bloom", "special"
    val targetValue: Int,
    val currentProgress: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val rewardXp: Int = 0,
    val rewardTokens: Int = 0,
    val rewardBannerId: String? = null,
    val rewardTitleId: String? = null,
    val rewardClaimed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun progressPercent(): Float {
        if (targetValue <= 0) return if (isCompleted) 100f else 0f
        return ((currentProgress.toFloat() / targetValue) * 100).coerceIn(0f, 100f)
    }

    fun isActive(): Boolean {
        val now = System.currentTimeMillis()
        return now in weekStart..weekEnd
    }
}
