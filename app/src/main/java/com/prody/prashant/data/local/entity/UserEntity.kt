package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_profile",
    indices = [
        androidx.room.Index(value = ["odUserId"], unique = true)
    ]
)
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1, // Single user profile (legacy, kept for migration)
    // Firebase/Google Auth fields - prepared for multi-user support
    val odUserId: String = "local", // Firebase UID or "local" for offline
    val email: String? = null, // Google account email
    val photoUrl: String? = null, // Google profile photo URL
    val isAnonymous: Boolean = true, // True until Google sign-in
    val authProvider: String = "local", // local, google, anonymous
    val lastAuthenticatedAt: Long? = null,
    val displayName: String = "Growth Seeker",
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
    val preferences: String = "{}", // JSON string for additional preferences
    // Gamification 2.0 - Special badges (future OAuth assignment)
    val isDevBadgeHolder: Boolean = false, // Single DEV badge holder
    val isBetaTester: Boolean = false, // Beta tester badge
    val isFounder: Boolean = false, // Founding user badge
    val profileFrameRarity: String = "common", // common, uncommon, rare, epic, legendary
    val boostsReceived: Int = 0, // Total boosts received
    val boostsGiven: Int = 0, // Total boosts given
    val dailyBoostsRemaining: Int = 5, // Daily boost limit
    val lastBoostResetDate: Long = System.currentTimeMillis(), // For daily reset
    // Sync metadata
    val syncStatus: String = "pending",
    val lastSyncedAt: Long? = null,
    val serverVersion: Long = 0
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val iconId: String,
    val category: String, // wisdom, reflection, consistency, presence, temporal, mastery
    val requirement: Int, // Number needed to unlock
    val currentProgress: Int = 0,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val rewardType: String = "points", // points, avatar, banner, title
    val rewardValue: String = "100", // Points amount or reward ID
    val rarity: String = "common", // common, uncommon, rare, epic, legendary
    val celebrationMessage: String = "" // Elegant message shown when unlocked
) {
    companion object {
        /**
         * Creates an AchievementEntity from domain values.
         */
        fun fromDomain(
            id: String,
            name: String,
            description: String,
            category: String,
            rarity: String,
            iconName: String,
            requirement: Int,
            celebrationMessage: String,
            rewardPoints: Int = 100
        ): AchievementEntity {
            return AchievementEntity(
                id = id,
                name = name,
                description = description,
                category = category,
                rarity = rarity,
                iconId = iconName,
                requirement = requirement,
                currentProgress = 0,
                isUnlocked = false,
                unlockedAt = null,
                rewardType = "points",
                rewardValue = rewardPoints.toString(),
                celebrationMessage = celebrationMessage
            )
        }
    }
}

@Entity(
    tableName = "user_stats",
    indices = [androidx.room.Index(value = ["userId"])]
)
data class UserStatsEntity(
    @PrimaryKey
    val id: Int = 1,
    val userId: String = "local", // Multi-user support
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

@Entity(
    tableName = "streak_history",
    indices = [
        androidx.room.Index(value = ["userId"]),
        androidx.room.Index(value = ["date"]),
        androidx.room.Index(value = ["userId", "date"])
    ]
)
data class StreakHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local", // Multi-user support
    val date: Long,
    val activitiesCompleted: String = "", // Comma-separated activity types
    val pointsEarned: Int = 0,
    val streakDay: Int = 1
)
