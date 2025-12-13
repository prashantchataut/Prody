package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1, // Single user profile
    val displayName: String = "Seeker",
    val bio: String = "",
    val avatarId: String = "default",
    val bannerId: String = "default_dawn",
    val titleId: String = "seeker", // Earned title
    val totalPoints: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: Long = System.currentTimeMillis(),
    val joinedAt: Long = System.currentTimeMillis(),
    val wordsLearned: Int = 0,
    val journalEntriesCount: Int = 0,
    val futureMessagesCount: Int = 0,
    val futureLettersSent: Int = 0,
    val futureLettersReceived: Int = 0,
    val buddhaConversations: Int = 0,
    val quotesReflected: Int = 0,
    val totalReflectionTime: Long = 0, // in seconds
    val preferredWisdomCategories: String = "", // Comma-separated
    val dailyGoalMinutes: Int = 10,
    val preferences: String = "{}" // JSON string for additional preferences
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
    val rarity: String = "common", // common, uncommon, rare, epic, legendary
    val celebrationMessage: String = "" // Message shown when achievement is unlocked
) {
    companion object {
        fun fromDomain(
            id: String,
            name: String,
            description: String,
            iconId: String,
            category: String,
            requirement: Int,
            rarity: String,
            celebrationMessage: String,
            rewardType: String = "points",
            rewardValue: String = "100"
        ): AchievementEntity {
            return AchievementEntity(
                id = id,
                name = name,
                description = description,
                iconId = iconId,
                category = category,
                requirement = requirement,
                currentProgress = 0,
                isUnlocked = false,
                unlockedAt = null,
                rewardType = rewardType,
                rewardValue = rewardValue,
                rarity = rarity,
                celebrationMessage = celebrationMessage
            )
        }
    }
}

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
