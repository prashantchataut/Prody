package com.prody.prashant.domain.repository

import com.prody.prashant.data.local.entity.DeepDiveEntity
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.deepdive.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Deep Dive operations.
 *
 * Handles all business logic for the Deep Dive feature including
 * CRUD operations, scheduling, progress tracking, and analytics.
 */
interface DeepDiveRepository {

    // ==================== RETRIEVAL ====================

    /**
     * Get a specific deep dive by ID
     */
    suspend fun getDeepDiveById(id: Long, userId: String = "local"): Result<DeepDiveEntity?>

    /**
     * Observe a specific deep dive
     */
    fun observeDeepDive(id: Long): Flow<DeepDiveEntity?>

    /**
     * Get all scheduled (not completed) deep dives
     */
    fun getScheduledDeepDives(userId: String = "local"): Flow<List<DeepDiveEntity>>

    /**
     * Get all completed deep dives
     */
    fun getCompletedDeepDives(userId: String = "local"): Flow<List<DeepDiveEntity>>

    /**
     * Get next scheduled deep dive
     */
    suspend fun getNextScheduledDeepDive(userId: String = "local"): Result<DeepDiveEntity?>

    /**
     * Get deep dives for a specific theme
     */
    fun getDeepDivesByTheme(userId: String = "local", theme: DeepDiveTheme): Flow<List<DeepDiveEntity>>

    /**
     * Get completed deep dives by theme
     */
    fun getCompletedDeepDivesByTheme(userId: String = "local", theme: DeepDiveTheme): Flow<List<DeepDiveEntity>>

    /**
     * Get overdue deep dives
     */
    suspend fun getOverdueDeepDives(userId: String = "local"): Result<List<DeepDiveEntity>>

    /**
     * Search deep dives by content
     */
    fun searchDeepDives(userId: String = "local", query: String): Flow<List<DeepDiveEntity>>

    // ==================== SESSION MANAGEMENT ====================

    /**
     * Start a deep dive session
     */
    suspend fun startSession(deepDiveId: Long): Result<DeepDiveSession>

    /**
     * Get current session with prompts
     */
    suspend fun getSession(deepDiveId: Long, userId: String = "local"): Result<DeepDiveSession?>

    /**
     * Save opening reflection
     */
    suspend fun saveOpeningReflection(deepDiveId: Long, reflection: String): Result<Unit>

    /**
     * Save core response
     */
    suspend fun saveCoreResponse(deepDiveId: Long, response: String): Result<Unit>

    /**
     * Save key insight
     */
    suspend fun saveKeyInsight(deepDiveId: Long, insight: String): Result<Unit>

    /**
     * Save commitment statement
     */
    suspend fun saveCommitmentStatement(deepDiveId: Long, commitment: String): Result<Unit>

    /**
     * Save mood rating (before or after)
     */
    suspend fun saveMoodRating(deepDiveId: Long, moodBefore: Int?, moodAfter: Int?): Result<Unit>

    /**
     * Update current step
     */
    suspend fun updateCurrentStep(deepDiveId: Long, step: DeepDiveProgress): Result<Unit>

    /**
     * Complete a deep dive session
     */
    suspend fun completeSession(deepDiveId: Long, durationMinutes: Int): Result<DeepDiveEntity>

    /**
     * Auto-save session progress
     */
    suspend fun autoSaveProgress(deepDive: DeepDiveEntity): Result<Unit>

    // ==================== SCHEDULING ====================

    /**
     * Schedule next deep dive
     */
    suspend fun scheduleNextDeepDive(
        userId: String = "local",
        preferredDayOfWeek: Int = 6,
        preferredHour: Int = 19,
        preferredMinute: Int = 0
    ): Result<DeepDiveEntity>

    /**
     * Schedule multiple deep dives
     */
    suspend fun scheduleMultipleDeepDives(
        userId: String = "local",
        count: Int = 4
    ): Result<List<DeepDiveEntity>>

    /**
     * Reschedule a deep dive
     */
    suspend fun rescheduleDeepDive(deepDiveId: Long, newScheduledDate: Long): Result<Unit>

    /**
     * Suggest theme based on recent moods
     */
    suspend fun suggestTheme(userId: String = "local"): Result<DeepDiveTheme>

    /**
     * Ensure minimum scheduled deep dives exist
     */
    suspend fun ensureScheduledDeepDives(userId: String = "local", minimum: Int = 2): Result<Unit>

    // ==================== ANALYTICS ====================

    /**
     * Get deep dive analytics
     */
    suspend fun getAnalytics(userId: String = "local"): Result<DeepDiveAnalytics>

    /**
     * Get summaries of completed deep dives
     */
    suspend fun getCompletedSummaries(userId: String = "local", limit: Int = 10): Result<List<DeepDiveSummary>>

    /**
     * Get theme frequency
     */
    suspend fun getThemeFrequency(userId: String = "local"): Result<Map<DeepDiveTheme, Int>>

    /**
     * Get unexplored themes
     */
    suspend fun getUnexploredThemes(userId: String = "local"): Result<List<DeepDiveTheme>>

    // ==================== DELETION ====================

    /**
     * Delete a deep dive
     */
    suspend fun deleteDeepDive(deepDiveId: Long): Result<Unit>

    /**
     * Soft delete a deep dive
     */
    suspend fun softDeleteDeepDive(deepDiveId: Long): Result<Unit>
}
