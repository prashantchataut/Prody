package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * ================================================================================================
 * SOUL LAYER DATA ACCESS OBJECT
 * ================================================================================================
 *
 * Provides database operations for the Soul Layer intelligence system.
 * All queries are designed to support the various Soul Layer engines:
 * - UserContextEngine: User context caching and pattern detection
 * - MemoryEngine: Surfaced memory tracking
 * - NotificationIntelligence: Notification history and learning
 * - FirstWeekJourneyManager: First week progress tracking
 * - ContextAwareBuddhaService: Buddha interaction tracking
 * - ContextAwareHavenService: Haven insight tracking
 */
@Dao
interface SoulLayerDao {

    // =============================================================================================
    // SURFACED MEMORIES
    // =============================================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurfacedMemory(memory: SurfacedMemoryEntity): Long

    @Update
    suspend fun updateSurfacedMemory(memory: SurfacedMemoryEntity)

    @Query("SELECT * FROM surfaced_memories WHERE userId = :userId ORDER BY surfacedAt DESC")
    fun observeSurfacedMemories(userId: String = "local"): Flow<List<SurfacedMemoryEntity>>

    @Query("SELECT * FROM surfaced_memories WHERE userId = :userId ORDER BY surfacedAt DESC LIMIT :limit")
    suspend fun getRecentSurfacedMemories(userId: String = "local", limit: Int = 10): List<SurfacedMemoryEntity>

    @Query("SELECT * FROM surfaced_memories WHERE userId = :userId AND journalEntryId = :entryId ORDER BY surfacedAt DESC LIMIT 1")
    suspend fun getLastSurfacedForEntry(userId: String = "local", entryId: Long): SurfacedMemoryEntity?

    @Query("SELECT COUNT(*) FROM surfaced_memories WHERE userId = :userId AND surfacedAt > :since")
    suspend fun getSurfacedMemoryCountSince(userId: String = "local", since: Long): Int

    @Query("SELECT * FROM surfaced_memories WHERE userId = :userId AND surfaceReason = :reason ORDER BY surfacedAt DESC LIMIT :limit")
    suspend fun getSurfacedMemoriesByReason(userId: String = "local", reason: String, limit: Int = 10): List<SurfacedMemoryEntity>

    @Query("SELECT * FROM surfaced_memories WHERE userId = :userId AND wasInteractedWith = 1 ORDER BY interactedAt DESC LIMIT :limit")
    suspend fun getInteractedMemories(userId: String = "local", limit: Int = 10): List<SurfacedMemoryEntity>

    @Query("""
        UPDATE surfaced_memories
        SET wasInteractedWith = 1, interactionType = :interactionType, interactedAt = :interactedAt
        WHERE id = :memoryId
    """)
    suspend fun markMemoryInteracted(memoryId: Long, interactionType: String, interactedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM surfaced_memories WHERE userId = :userId AND surfacedAt < :before")
    suspend fun deleteOldSurfacedMemories(userId: String = "local", before: Long)

    // =============================================================================================
    // USER CONTEXT CACHE
    // =============================================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateContextCache(cache: UserContextCacheEntity): Long

    @Query("SELECT * FROM user_context_cache WHERE userId = :userId LIMIT 1")
    suspend fun getContextCache(userId: String = "local"): UserContextCacheEntity?

    @Query("SELECT * FROM user_context_cache WHERE userId = :userId LIMIT 1")
    fun observeContextCache(userId: String = "local"): Flow<UserContextCacheEntity?>

    @Query("SELECT validUntil > :now FROM user_context_cache WHERE userId = :userId")
    suspend fun isContextCacheValid(userId: String = "local", now: Long = System.currentTimeMillis()): Boolean?

    @Query("DELETE FROM user_context_cache WHERE userId = :userId")
    suspend fun clearContextCache(userId: String = "local")

    @Query("UPDATE user_context_cache SET validUntil = :validUntil WHERE userId = :userId")
    suspend fun extendContextCacheValidity(userId: String = "local", validUntil: Long)

    // =============================================================================================
    // NOTIFICATION HISTORY
    // =============================================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificationHistory(notification: NotificationHistoryEntity): Long

    @Update
    suspend fun updateNotificationHistory(notification: NotificationHistoryEntity)

    @Query("SELECT * FROM notification_history WHERE userId = :userId ORDER BY scheduledAt DESC")
    fun observeNotificationHistory(userId: String = "local"): Flow<List<NotificationHistoryEntity>>

    @Query("SELECT * FROM notification_history WHERE userId = :userId ORDER BY scheduledAt DESC LIMIT :limit")
    suspend fun getRecentNotifications(userId: String = "local", limit: Int = 50): List<NotificationHistoryEntity>

    @Query("SELECT * FROM notification_history WHERE userId = :userId AND notificationType = :type ORDER BY scheduledAt DESC LIMIT :limit")
    suspend fun getNotificationsByType(userId: String = "local", type: String, limit: Int = 20): List<NotificationHistoryEntity>

    @Query("SELECT * FROM notification_history WHERE userId = :userId AND sentAt IS NOT NULL AND wasOpened = 1 ORDER BY openedAt DESC LIMIT :limit")
    suspend fun getOpenedNotifications(userId: String = "local", limit: Int = 20): List<NotificationHistoryEntity>

    @Query("SELECT * FROM notification_history WHERE userId = :userId AND sentAt IS NOT NULL AND resultedInAction = 1 ORDER BY actionAt DESC LIMIT :limit")
    suspend fun getActionedNotifications(userId: String = "local", limit: Int = 20): List<NotificationHistoryEntity>

    @Query("""
        SELECT AVG(CASE WHEN wasOpened = 1 THEN 1.0 ELSE 0.0 END)
        FROM notification_history
        WHERE userId = :userId AND sentAt IS NOT NULL AND sentAt > :since
    """)
    suspend fun getNotificationOpenRate(userId: String = "local", since: Long): Float?

    @Query("""
        SELECT AVG(CASE WHEN resultedInAction = 1 THEN 1.0 ELSE 0.0 END)
        FROM notification_history
        WHERE userId = :userId AND sentAt IS NOT NULL AND wasOpened = 1 AND sentAt > :since
    """)
    suspend fun getNotificationActionRate(userId: String = "local", since: Long): Float?

    @Query("""
        SELECT hourOfDay, COUNT(*) as count, AVG(CASE WHEN wasOpened = 1 THEN 1.0 ELSE 0.0 END) as openRate
        FROM notification_history
        WHERE userId = :userId AND sentAt IS NOT NULL
        GROUP BY hourOfDay
        ORDER BY openRate DESC
    """)
    suspend fun getNotificationPerformanceByHour(userId: String = "local"): List<HourlyNotificationStats>

    @Query("""
        UPDATE notification_history
        SET wasOpened = 1, openedAt = :openedAt
        WHERE id = :notificationId
    """)
    suspend fun markNotificationOpened(notificationId: Long, openedAt: Long = System.currentTimeMillis())

    @Query("""
        UPDATE notification_history
        SET resultedInAction = 1, actionType = :actionType, actionAt = :actionAt
        WHERE id = :notificationId
    """)
    suspend fun markNotificationActioned(notificationId: Long, actionType: String, actionAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM notification_history WHERE userId = :userId AND scheduledAt < :before")
    suspend fun deleteOldNotificationHistory(userId: String = "local", before: Long)

    // =============================================================================================
    // DETECTED PATTERNS
    // =============================================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetectedPattern(pattern: DetectedPatternEntity): Long

    @Update
    suspend fun updateDetectedPattern(pattern: DetectedPatternEntity)

    @Query("SELECT * FROM detected_patterns WHERE userId = :userId AND isActive = 1 ORDER BY confidence DESC")
    fun observeActivePatterns(userId: String = "local"): Flow<List<DetectedPatternEntity>>

    @Query("SELECT * FROM detected_patterns WHERE userId = :userId AND isActive = 1 ORDER BY confidence DESC")
    suspend fun getActivePatterns(userId: String = "local"): List<DetectedPatternEntity>

    @Query("SELECT * FROM detected_patterns WHERE userId = :userId AND patternType = :type AND isActive = 1 ORDER BY confidence DESC LIMIT 1")
    suspend fun getPatternByType(userId: String = "local", type: String): DetectedPatternEntity?

    @Query("SELECT * FROM detected_patterns WHERE userId = :userId AND isActive = 1 AND wasShownToUser = 0 AND confidence >= :minConfidence ORDER BY confidence DESC LIMIT :limit")
    suspend fun getPatternsToShow(userId: String = "local", minConfidence: Float = 0.7f, limit: Int = 3): List<DetectedPatternEntity>

    @Query("""
        UPDATE detected_patterns
        SET wasShownToUser = 1, shownAt = :shownAt
        WHERE id = :patternId
    """)
    suspend fun markPatternShown(patternId: Long, shownAt: Long = System.currentTimeMillis())

    @Query("""
        UPDATE detected_patterns
        SET userFeedback = :feedback, feedbackAt = :feedbackAt
        WHERE id = :patternId
    """)
    suspend fun setPatternFeedback(patternId: Long, feedback: String, feedbackAt: Long = System.currentTimeMillis())

    @Query("""
        UPDATE detected_patterns
        SET lastConfirmedAt = :confirmedAt, occurrenceCount = occurrenceCount + 1
        WHERE id = :patternId
    """)
    suspend fun confirmPattern(patternId: Long, confirmedAt: Long = System.currentTimeMillis())

    @Query("UPDATE detected_patterns SET isActive = 0 WHERE id = :patternId")
    suspend fun deactivatePattern(patternId: Long)

    @Query("DELETE FROM detected_patterns WHERE userId = :userId AND isActive = 0 AND detectedAt < :before")
    suspend fun deleteOldInactivePatterns(userId: String = "local", before: Long)

    // =============================================================================================
    // BUDDHA INTERACTIONS
    // =============================================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuddhaInteraction(interaction: BuddhaInteractionEntity): Long

    @Update
    suspend fun updateBuddhaInteraction(interaction: BuddhaInteractionEntity)

    @Query("SELECT * FROM buddha_interactions WHERE userId = :userId ORDER BY interactedAt DESC")
    fun observeBuddhaInteractions(userId: String = "local"): Flow<List<BuddhaInteractionEntity>>

    @Query("SELECT * FROM buddha_interactions WHERE userId = :userId ORDER BY interactedAt DESC LIMIT :limit")
    suspend fun getRecentBuddhaInteractions(userId: String = "local", limit: Int = 50): List<BuddhaInteractionEntity>

    @Query("SELECT * FROM buddha_interactions WHERE userId = :userId AND interactionType = :type ORDER BY interactedAt DESC LIMIT :limit")
    suspend fun getBuddhaInteractionsByType(userId: String = "local", type: String, limit: Int = 20): List<BuddhaInteractionEntity>

    @Query("SELECT * FROM buddha_interactions WHERE userId = :userId AND wasHelpful = 1 ORDER BY interactedAt DESC LIMIT :limit")
    suspend fun getHelpfulBuddhaInteractions(userId: String = "local", limit: Int = 20): List<BuddhaInteractionEntity>

    @Query("""
        SELECT responseWisdomStyle, COUNT(*) as count,
               AVG(CASE WHEN wasHelpful = 1 THEN 1.0 WHEN wasHelpful = 0 THEN 0.0 ELSE NULL END) as helpfulRate
        FROM buddha_interactions
        WHERE userId = :userId AND responseWisdomStyle IS NOT NULL
        GROUP BY responseWisdomStyle
        ORDER BY helpfulRate DESC
    """)
    suspend fun getWisdomStylePreferences(userId: String = "local"): List<WisdomStyleStats>

    @Query("""
        SELECT AVG(CASE WHEN wasHelpful = 1 THEN 1.0 ELSE 0.0 END)
        FROM buddha_interactions
        WHERE userId = :userId AND wasHelpful IS NOT NULL AND interactedAt > :since
    """)
    suspend fun getBuddhaHelpfulnessRate(userId: String = "local", since: Long): Float?

    @Query("""
        UPDATE buddha_interactions
        SET wasHelpful = :wasHelpful, helpfulnessRating = :rating
        WHERE id = :interactionId
    """)
    suspend fun setBuddhaInteractionFeedback(interactionId: Long, wasHelpful: Boolean, rating: Int? = null)

    @Query("DELETE FROM buddha_interactions WHERE userId = :userId AND interactedAt < :before")
    suspend fun deleteOldBuddhaInteractions(userId: String = "local", before: Long)

    // =============================================================================================
    // HAVEN INSIGHTS
    // =============================================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHavenInsight(insight: HavenInsightEntity): Long

    @Update
    suspend fun updateHavenInsight(insight: HavenInsightEntity)

    @Query("SELECT * FROM haven_insights WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeHavenInsights(userId: String = "local"): Flow<List<HavenInsightEntity>>

    @Query("SELECT * FROM haven_insights WHERE userId = :userId ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentHavenInsights(userId: String = "local", limit: Int = 50): List<HavenInsightEntity>

    @Query("SELECT * FROM haven_insights WHERE userId = :userId AND sessionId = :sessionId ORDER BY createdAt DESC")
    suspend fun getInsightsForSession(userId: String = "local", sessionId: Long): List<HavenInsightEntity>

    @Query("SELECT * FROM haven_insights WHERE userId = :userId AND insightType = :type ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getInsightsByType(userId: String = "local", type: String, limit: Int = 20): List<HavenInsightEntity>

    @Query("SELECT * FROM haven_insights WHERE userId = :userId AND wasEffective = 1 ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getEffectiveInsights(userId: String = "local", limit: Int = 20): List<HavenInsightEntity>

    @Query("""
        SELECT therapeuticApproachUsed, COUNT(*) as count,
               AVG(CASE WHEN wasEffective = 1 THEN 1.0 WHEN wasEffective = 0 THEN 0.0 ELSE NULL END) as effectiveRate,
               AVG(moodImprovement) as avgMoodImprovement
        FROM haven_insights
        WHERE userId = :userId AND therapeuticApproachUsed IS NOT NULL
        GROUP BY therapeuticApproachUsed
        ORDER BY effectiveRate DESC
    """)
    suspend fun getTherapeuticApproachStats(userId: String = "local"): List<TherapeuticApproachStats>

    @Query("""
        SELECT AVG(moodImprovement)
        FROM haven_insights
        WHERE userId = :userId AND moodImprovement IS NOT NULL AND createdAt > :since
    """)
    suspend fun getAverageMoodImprovement(userId: String = "local", since: Long): Float?

    @Query("UPDATE haven_insights SET wasUsedInFutureSession = 1 WHERE id = :insightId")
    suspend fun markInsightUsed(insightId: Long)

    @Query("DELETE FROM haven_insights WHERE userId = :userId AND createdAt < :before")
    suspend fun deleteOldHavenInsights(userId: String = "local", before: Long)

    // =============================================================================================
    // TEMPORAL CONTENT HISTORY
    // =============================================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemporalContentHistory(content: TemporalContentHistoryEntity): Long

    @Update
    suspend fun updateTemporalContentHistory(content: TemporalContentHistoryEntity)

    @Query("SELECT * FROM temporal_content_history WHERE userId = :userId ORDER BY shownAt DESC")
    fun observeTemporalContentHistory(userId: String = "local"): Flow<List<TemporalContentHistoryEntity>>

    @Query("SELECT * FROM temporal_content_history WHERE userId = :userId ORDER BY shownAt DESC LIMIT :limit")
    suspend fun getRecentTemporalContent(userId: String = "local", limit: Int = 50): List<TemporalContentHistoryEntity>

    @Query("SELECT * FROM temporal_content_history WHERE userId = :userId AND contentType = :type ORDER BY shownAt DESC LIMIT :limit")
    suspend fun getTemporalContentByType(userId: String = "local", type: String, limit: Int = 20): List<TemporalContentHistoryEntity>

    @Query("SELECT * FROM temporal_content_history WHERE userId = :userId AND contentType = :type AND shownAt > :since ORDER BY shownAt DESC")
    suspend fun getRecentContentOfType(userId: String = "local", type: String, since: Long): List<TemporalContentHistoryEntity>

    @Query("SELECT contentId FROM temporal_content_history WHERE userId = :userId AND contentType = :type AND shownAt > :since")
    suspend fun getRecentlyShownContentIds(userId: String = "local", type: String, since: Long): List<String?>

    @Query("""
        UPDATE temporal_content_history
        SET wasEngaged = 1, engagementType = :engagementType, engagedAt = :engagedAt
        WHERE id = :contentId
    """)
    suspend fun markTemporalContentEngaged(contentId: Long, engagementType: String, engagedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM temporal_content_history WHERE userId = :userId AND shownAt < :before")
    suspend fun deleteOldTemporalContentHistory(userId: String = "local", before: Long)

    // =============================================================================================
    // FIRST WEEK PROGRESS
    // =============================================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFirstWeekProgress(progress: FirstWeekProgressEntity): Long

    @Query("SELECT * FROM first_week_progress WHERE userId = :userId LIMIT 1")
    suspend fun getFirstWeekProgress(userId: String = "local"): FirstWeekProgressEntity?

    @Query("SELECT * FROM first_week_progress WHERE userId = :userId LIMIT 1")
    fun observeFirstWeekProgress(userId: String = "local"): Flow<FirstWeekProgressEntity?>

    @Query("SELECT isGraduated FROM first_week_progress WHERE userId = :userId")
    suspend fun hasGraduatedFirstWeek(userId: String = "local"): Boolean?

    @Query("""
        UPDATE first_week_progress
        SET isGraduated = 1, graduatedAt = :graduatedAt
        WHERE userId = :userId
    """)
    suspend fun markFirstWeekGraduated(userId: String = "local", graduatedAt: Long = System.currentTimeMillis())

    @Query("""
        UPDATE first_week_progress
        SET completedMilestonesJson = :milestonesJson, updatedAt = :updatedAt
        WHERE userId = :userId
    """)
    suspend fun updateFirstWeekMilestones(userId: String = "local", milestonesJson: String, updatedAt: Long = System.currentTimeMillis())

    @Query("""
        UPDATE first_week_progress
        SET totalEntriesInFirstWeek = totalEntriesInFirstWeek + 1,
            totalWordsInFirstWeek = totalWordsInFirstWeek + :words,
            updatedAt = :updatedAt
        WHERE userId = :userId
    """)
    suspend fun incrementFirstWeekStats(userId: String = "local", words: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("""
        UPDATE first_week_progress
        SET totalXpEarned = totalXpEarned + :xp,
            totalTokensEarned = totalTokensEarned + :tokens,
            updatedAt = :updatedAt
        WHERE userId = :userId
    """)
    suspend fun addFirstWeekRewards(userId: String = "local", xp: Int, tokens: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM first_week_progress WHERE userId = :userId")
    suspend fun clearFirstWeekProgress(userId: String = "local")

    // =============================================================================================
    // CLEANUP OPERATIONS
    // =============================================================================================

    /**
     * Cleans up old data across all Soul Layer tables.
     * Call this periodically to prevent database bloat.
     */
    @Transaction
    suspend fun cleanupOldData(userId: String = "local", retentionDays: Int = 90) {
        val cutoff = System.currentTimeMillis() - (retentionDays * 24 * 60 * 60 * 1000L)
        deleteOldSurfacedMemories(userId, cutoff)
        deleteOldNotificationHistory(userId, cutoff)
        deleteOldInactivePatterns(userId, cutoff)
        deleteOldBuddhaInteractions(userId, cutoff)
        deleteOldHavenInsights(userId, cutoff)
        deleteOldTemporalContentHistory(userId, cutoff)
    }

    /**
     * Clears all Soul Layer data for a user.
     * Use with caution - this is destructive.
     */
    @Transaction
    suspend fun clearAllUserData(userId: String = "local") {
        clearContextCache(userId)
        clearFirstWeekProgress(userId)
        deleteOldSurfacedMemories(userId, System.currentTimeMillis() + 1)
        deleteOldNotificationHistory(userId, System.currentTimeMillis() + 1)
        deleteOldBuddhaInteractions(userId, System.currentTimeMillis() + 1)
        deleteOldHavenInsights(userId, System.currentTimeMillis() + 1)
        deleteOldTemporalContentHistory(userId, System.currentTimeMillis() + 1)
    }
}

// ================================================================================================
// QUERY RESULT MODELS
// ================================================================================================

/**
 * Stats for notification performance by hour of day.
 */
data class HourlyNotificationStats(
    val hourOfDay: Int,
    val count: Int,
    val openRate: Float
)

/**
 * Stats for wisdom style preferences.
 */
data class WisdomStyleStats(
    val responseWisdomStyle: String?,
    val count: Int,
    val helpfulRate: Float?
)

/**
 * Stats for therapeutic approach effectiveness.
 */
data class TherapeuticApproachStats(
    val therapeuticApproachUsed: String?,
    val count: Int,
    val effectiveRate: Float?,
    val avgMoodImprovement: Float?
)
