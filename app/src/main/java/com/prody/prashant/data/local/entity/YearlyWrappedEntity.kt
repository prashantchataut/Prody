package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for Yearly Wrapped - End-of-year celebration.
 *
 * A comprehensive summary of the user's year in journaling, showing growth,
 * patterns, achievements, and highlights in an engaging Spotify Wrapped-style
 * experience.
 */
@Entity(
    tableName = "yearly_wrapped",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["year"]),
        Index(value = ["userId", "year"], unique = true)
    ]
)
data class YearlyWrappedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // User & Time
    val userId: String = "local",
    val year: Int,
    val generatedAt: Long = System.currentTimeMillis(),
    val isViewed: Boolean = false,
    val viewedAt: Long? = null,

    // ==================== CORE STATISTICS ====================

    // Writing Stats
    val totalJournalEntries: Int = 0,
    val totalMicroEntries: Int = 0,
    val totalWordsWritten: Long = 0,
    val averageWordsPerEntry: Int = 0,
    val longestEntry: Int = 0, // Word count
    val longestEntryId: Long? = null,

    // Engagement Stats
    val activeDaysCount: Int = 0,
    val longestStreak: Int = 0,
    val totalMeditationMinutes: Int = 0,
    val bloomsCompleted: Int = 0, // Seeds bloomed

    // Learning Stats
    val vocabularyWordsLearned: Int = 0,
    val vocabularyWordsUsed: Int = 0, // Used in journal entries
    val idiomsExplored: Int = 0,
    val proverbsDiscovered: Int = 0,

    // Time Capsule Stats
    val futureMessagesWritten: Int = 0,
    val futureMessagesReceived: Int = 0,
    val mostDistantMessage: Long = 0, // Days into future

    // Activity Patterns
    val mostActiveMonth: Int? = null, // 1-12
    val mostActiveDay: String? = null, // "Monday", "Tuesday", etc.
    val mostActiveTimeOfDay: String? = null, // "morning", "afternoon", "evening", "night"
    val firstEntryDate: Long? = null,
    val lastEntryDate: Long? = null,

    // ==================== MOOD JOURNEY ====================

    val averageMood: Float = 0f, // 1-10 scale
    val moodTrend: String = "stable", // improving, stable, declining, fluctuating
    val mostCommonMood: String? = null,
    val moodVariety: Int = 0, // Number of different moods experienced
    val brightestMonth: Int? = null, // Month with highest average mood (1-12)
    val mostReflectiveMonth: Int? = null, // Month with most entries
    val moodEvolution: String = "[]", // JSON array of monthly mood averages

    // ==================== THEMES & INSIGHTS ====================

    // Top themes extracted from journal entries
    val topThemesJson: String = "[]", // JSON array of {theme: String, count: Int, trend: String}

    // Growth areas detected through content analysis
    val growthAreasJson: String = "[]", // JSON array of {area: String, description: String, entries: Int}

    // Challenges overcome (detected from mood progression and content)
    val challengesOvercomeJson: String = "[]", // JSON array of {challenge: String, period: String}

    // Key moments - highlighted journal entries
    val keyMomentsJson: String = "[]", // JSON array of {entryId: Long, date: Long, snippet: String, why: String}

    // Recurring patterns
    val patternsJson: String = "[]", // JSON array of patterns like "morning_writer", "evening_reflector"

    // ==================== AI NARRATIVES ====================

    // Opening narrative - sets the tone
    val openingNarrative: String? = null,

    // Year summary - overall story
    val yearSummaryNarrative: String? = null,

    // Growth story - how user evolved
    val growthStoryNarrative: String? = null,

    // Mood journey narrative - emotional evolution
    val moodJourneyNarrative: String? = null,

    // Looking ahead - encouragement for next year
    val lookingAheadNarrative: String? = null,

    // Personal milestone narrative
    val milestoneNarrative: String? = null,

    // ==================== SHAREABLE CARDS ====================

    // Pre-generated shareable cards data
    val shareableCardsJson: String = "[]", // JSON array of card data

    // ==================== METADATA ====================

    val isShared: Boolean = false,
    val sharedAt: Long? = null,
    val isFavorite: Boolean = false,

    // Completion status
    val viewCompletionPercent: Int = 0, // How much of wrapped they viewed
    val slidesViewed: String = "[]", // JSON array of slide names viewed

    // Sync metadata
    val syncStatus: String = "pending", // pending, synced, conflict
    val lastSyncedAt: Long? = null,
    val isDeleted: Boolean = false
)
