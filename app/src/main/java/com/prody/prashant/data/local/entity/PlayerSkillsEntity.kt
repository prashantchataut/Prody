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
 * Each skill has its own XP pool that levels independently (max level 20).
 * Daily and weekly XP caps prevent exploit/spam grinding.
 *
 * Level progression uses these thresholds (cumulative) - 20 levels total:
 * Level 1: 0, Level 2: 50, Level 3: 120, Level 4: 220, Level 5: 360,
 * Level 6: 550, Level 7: 800, Level 8: 1150, Level 9: 1600, Level 10: 2200,
 * Level 11: 2900, Level 12: 3700, Level 13: 4600, Level 14: 5600, Level 15: 6700,
 * Level 16: 8000, Level 17: 9500, Level 18: 11200, Level 19: 13100, Level 20: 15300
 *
 * Approximate time to reach milestones:
 * - Level 5: ~1 week of consistent use
 * - Level 10: ~1 month of consistent use
 * - Level 15: ~3 months of consistent use
 * - Level 20 (True Mastery): ~6 months of dedicated use
 *
 * Daily XP Caps: Clarity 150, Discipline 150, Courage 100
 * Weekly XP Caps: Clarity 800, Discipline 800, Courage 500
 *
 * Perks are unlocked at specific levels and stored in unlockedPerkIds (JSON array).
 * Freeze tokens from perks are accumulated and tracked separately from monthly resets.
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

    // Perk tracking - JSON array of unlocked perk IDs (e.g., ["clarity_l5_templates", "discipline_l5_streak_shield"])
    val unlockedPerkIds: String = "[]",

    // Freeze tokens earned from perks (separate from monthly mindful breaks)
    val perkFreezeTokens: Int = 0,
    val perkFreezeTokensUsed: Int = 0, // Tracks how many perk-granted tokens have been used

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
