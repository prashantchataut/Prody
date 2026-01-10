package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Player Skills Entity - Tracks the 3 core skill stats.
 *
 * Clarity (Reflect): Journaling depth/consistency
 * Discipline (Sharpen): Flashcards/vocabulary consistency
 * Courage (Commit): Future messages + confronting themes
 *
 * Each skill has its own XP pool that levels independently (max level 10).
 * Daily and weekly XP caps prevent exploit/spam grinding.
 *
 * Level progression uses these thresholds (cumulative):
 * Level 1: 0, Level 2: 50, Level 3: 120, Level 4: 220, Level 5: 360,
 * Level 6: 550, Level 7: 800, Level 8: 1150, Level 9: 1600, Level 10: 2200
 *
 * Daily XP Caps: Clarity 150, Discipline 150, Courage 100
 */
@Entity(
    tableName = "player_skills",
    indices = [Index(value = ["userId"], unique = true)]
)
data class PlayerSkillsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",

    // Lifetime skill XP
    val clarityXp: Int = 0,
    val disciplineXp: Int = 0,
    val courageXp: Int = 0,

    // Daily XP tracking (reset at midnight)
    val dailyClarityXp: Int = 0,
    val dailyDisciplineXp: Int = 0,
    val dailyCourageXp: Int = 0,
    val dailyResetDate: Long = 0, // Timestamp of last daily reset

    // Weekly XP tracking (reset on Monday at midnight)
    val weeklyClarityXp: Int = 0,
    val weeklyDisciplineXp: Int = 0,
    val weeklyCourageXp: Int = 0,
    val weeklyResetDate: Long = 0, // Timestamp of last weekly reset (Monday)

    // Token currency (cosmetic unlocks)
    val tokens: Int = 0,

    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Processed reward keys for idempotency.
 * Prevents double-awarding XP on retries, recomposition, etc.
 */
@Entity(
    tableName = "processed_rewards",
    indices = [Index(value = ["rewardKey"], unique = true)]
)
data class ProcessedRewardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rewardKey: String,
    val processedAt: Long = System.currentTimeMillis()
)
