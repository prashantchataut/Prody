package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1, // Single user profile
    val displayName: String = "Growth Seeker",
    val bio: String = "",
    val avatarId: String = "default",
    val bannerId: String = "default",
    val titleId: String = "newcomer", // Earned title
    val totalPoints: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: Long = System.currentTimeMillis(),
    val joinedAt: Long = System.currentTimeMillis(),
    val wordsLearned: Int = 0,
    val journalEntriesCount: Int = 0,
    val futureMessagesCount: Int = 0,
    val quotesReflected: Int = 0,
    val totalReflectionTime: Long = 0, // in seconds
    val preferredWisdomCategories: String = "", // Comma-separated
    val dailyGoalMinutes: Int = 10
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val iconId: String,
    val category: String, // streak, learning, social, special
    val requirement: Int, // Number needed to unlock
    val currentProgress: Int = 0,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val rewardType: String = "points", // points, avatar, banner, title
    val rewardValue: String = "100", // Points amount or reward ID
    val rarity: String = "common" // common, uncommon, rare, epic, legendary
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey
    val id: Int = 1,
    val dailyPointsEarned: Int = 0,
    val weeklyPointsEarned: Int = 0,
    val monthlyPointsEarned: Int = 0,
    val dailyWordsLearned: Int = 0,
    val weeklyWordsLearned: Int = 0,
    val monthlyWordsLearned: Int = 0,
    val dailyJournalEntries: Int = 0,
    val weeklyJournalEntries: Int = 0,
    val monthlyJournalEntries: Int = 0,
    val lastResetDate: Long = System.currentTimeMillis(),
    val weekStartDate: Long = System.currentTimeMillis(),
    val monthStartDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "streak_history")
data class StreakHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val activitiesCompleted: String = "", // Comma-separated activity types
    val pointsEarned: Int = 0,
    val streakDay: Int = 1
)
