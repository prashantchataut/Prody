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
 * Each skill has its own XP pool that levels independently.
 * Daily XP caps prevent exploit/spam grinding.
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
    val dailyResetDate: Long = 0, // Timestamp of last reset

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
