package com.prody.prashant.data.backup

import com.prody.prashant.data.local.entity.*
import kotlinx.serialization.Serializable

/**
 * Data class representing a complete backup of user data.
 * Contains all user-generated content that should be preserved.
 */
@Serializable
data class ProdyBackup(
    val version: Int = BACKUP_VERSION,
    val createdAt: Long = System.currentTimeMillis(),
    val appVersion: String = "",
    val journalEntries: List<JournalBackup> = emptyList(),
    val futureMessages: List<FutureMessageBackup> = emptyList(),
    val vocabularyProgress: List<VocabularyProgressBackup> = emptyList(),
    val userProfile: UserProfileBackup? = null,
    val preferences: PreferencesBackup? = null,
    val streakHistory: List<StreakHistoryBackup> = emptyList()
) {
    companion object {
        const val BACKUP_VERSION = 1
    }
}

@Serializable
data class JournalBackup(
    val id: Long,
    val content: String,
    val mood: String,
    val moodIntensity: Int,
    val buddhaResponse: String?,
    val tags: String,
    val isBookmarked: Boolean,
    val wordCount: Int,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class FutureMessageBackup(
    val id: Long,
    val title: String,
    val content: String,
    val deliveryDate: Long,
    val isDelivered: Boolean,
    val isRead: Boolean,
    val category: String,
    val attachedGoal: String?,
    val createdAt: Long,
    val deliveredAt: Long?
)

@Serializable
data class VocabularyProgressBackup(
    val wordId: Long,
    val word: String,
    val isLearned: Boolean,
    val learnedAt: Long?,
    val reviewCount: Int,
    val lastReviewedAt: Long?,
    val nextReviewAt: Long?,
    val masteryLevel: Int,
    val isFavorite: Boolean
)

@Serializable
data class UserProfileBackup(
    val displayName: String,
    val bio: String,
    val avatarId: String,
    val bannerId: String,
    val titleId: String,
    val totalPoints: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActiveDate: Long,
    val joinedAt: Long,
    val wordsLearned: Int,
    val journalEntriesCount: Int,
    val futureMessagesCount: Int,
    val quotesReflected: Int,
    val totalReflectionTime: Long,
    val preferredWisdomCategories: String,
    val dailyGoalMinutes: Int
)

@Serializable
data class PreferencesBackup(
    val themeMode: String,
    val dynamicColors: Boolean,
    val notificationsEnabled: Boolean,
    val dailyReminderHour: Int,
    val dailyReminderMinute: Int,
    val wisdomNotificationEnabled: Boolean,
    val journalReminderEnabled: Boolean,
    val vocabularyDifficulty: Int,
    val autoPlayPronunciation: Boolean,
    val compactCardView: Boolean,
    val hapticFeedbackEnabled: Boolean
)

@Serializable
data class StreakHistoryBackup(
    val id: Long,
    val date: Long,
    val activitiesCompleted: String,
    val pointsEarned: Int,
    val streakDay: Int
)

// Extension functions to convert between entities and backup models
fun JournalEntryEntity.toBackup() = JournalBackup(
    id = id,
    content = content,
    mood = mood,
    moodIntensity = moodIntensity,
    buddhaResponse = buddhaResponse,
    tags = tags,
    isBookmarked = isBookmarked,
    wordCount = wordCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun JournalBackup.toEntity() = JournalEntryEntity(
    id = id,
    content = content,
    mood = mood,
    moodIntensity = moodIntensity,
    buddhaResponse = buddhaResponse,
    tags = tags,
    isBookmarked = isBookmarked,
    wordCount = wordCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun FutureMessageEntity.toBackup() = FutureMessageBackup(
    id = id,
    title = title,
    content = content,
    deliveryDate = deliveryDate,
    isDelivered = isDelivered,
    isRead = isRead,
    category = category,
    attachedGoal = attachedGoal,
    createdAt = createdAt,
    deliveredAt = deliveredAt
)

fun FutureMessageBackup.toEntity() = FutureMessageEntity(
    id = id,
    title = title,
    content = content,
    deliveryDate = deliveryDate,
    isDelivered = isDelivered,
    isRead = isRead,
    category = category,
    attachedGoal = attachedGoal,
    createdAt = createdAt,
    deliveredAt = deliveredAt
)

fun VocabularyEntity.toProgressBackup() = VocabularyProgressBackup(
    wordId = id,
    word = word,
    isLearned = isLearned,
    learnedAt = learnedAt,
    reviewCount = reviewCount,
    lastReviewedAt = lastReviewedAt,
    nextReviewAt = nextReviewAt,
    masteryLevel = masteryLevel,
    isFavorite = isFavorite
)

fun UserProfileEntity.toBackup() = UserProfileBackup(
    displayName = displayName,
    bio = bio,
    avatarId = avatarId,
    bannerId = bannerId,
    titleId = titleId,
    totalPoints = totalPoints,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    lastActiveDate = lastActiveDate,
    joinedAt = joinedAt,
    wordsLearned = wordsLearned,
    journalEntriesCount = journalEntriesCount,
    futureMessagesCount = futureMessagesCount,
    quotesReflected = quotesReflected,
    totalReflectionTime = totalReflectionTime,
    preferredWisdomCategories = preferredWisdomCategories,
    dailyGoalMinutes = dailyGoalMinutes
)

fun UserProfileBackup.toEntity() = UserProfileEntity(
    displayName = displayName,
    bio = bio,
    avatarId = avatarId,
    bannerId = bannerId,
    titleId = titleId,
    totalPoints = totalPoints,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    lastActiveDate = lastActiveDate,
    joinedAt = joinedAt,
    wordsLearned = wordsLearned,
    journalEntriesCount = journalEntriesCount,
    futureMessagesCount = futureMessagesCount,
    quotesReflected = quotesReflected,
    totalReflectionTime = totalReflectionTime,
    preferredWisdomCategories = preferredWisdomCategories,
    dailyGoalMinutes = dailyGoalMinutes
)

fun StreakHistoryEntity.toBackup() = StreakHistoryBackup(
    id = id,
    date = date,
    activitiesCompleted = activitiesCompleted,
    pointsEarned = pointsEarned,
    streakDay = streakDay
)

fun StreakHistoryBackup.toEntity() = StreakHistoryEntity(
    id = id,
    date = date,
    activitiesCompleted = activitiesCompleted,
    pointsEarned = pointsEarned,
    streakDay = streakDay
)
