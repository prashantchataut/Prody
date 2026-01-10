package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.HavenExerciseEntity
import com.prody.prashant.data.local.entity.HavenSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Haven Personal Therapist feature
 *
 * Provides database access for therapeutic sessions and exercises.
 * All queries support Flow for reactive UI updates.
 */
@Dao
interface HavenDao {

    // ==================== SESSION QUERIES ====================

    /**
     * Get all Haven sessions, most recent first
     */
    @Query("SELECT * FROM haven_sessions WHERE isDeleted = 0 ORDER BY startedAt DESC")
    fun getAllSessions(): Flow<List<HavenSessionEntity>>

    /**
     * Get all sessions for a specific user
     */
    @Query("SELECT * FROM haven_sessions WHERE userId = :userId AND isDeleted = 0 ORDER BY startedAt DESC")
    fun getSessionsByUser(userId: String): Flow<List<HavenSessionEntity>>

    /**
     * Get a specific session by ID
     */
    @Query("SELECT * FROM haven_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): HavenSessionEntity?

    /**
     * Get session by ID as Flow for reactive updates
     */
    @Query("SELECT * FROM haven_sessions WHERE id = :sessionId")
    fun observeSessionById(sessionId: Long): Flow<HavenSessionEntity?>

    /**
     * Get sessions by type
     */
    @Query("SELECT * FROM haven_sessions WHERE userId = :userId AND sessionType = :type AND isDeleted = 0 ORDER BY startedAt DESC")
    fun getSessionsByType(userId: String, type: String): Flow<List<HavenSessionEntity>>

    /**
     * Get recent sessions (last N sessions)
     */
    @Query("SELECT * FROM haven_sessions WHERE userId = :userId AND isDeleted = 0 ORDER BY startedAt DESC LIMIT :limit")
    fun getRecentSessions(userId: String, limit: Int = 10): Flow<List<HavenSessionEntity>>

    /**
     * Get completed sessions only
     */
    @Query("SELECT * FROM haven_sessions WHERE userId = :userId AND isCompleted = 1 AND isDeleted = 0 ORDER BY startedAt DESC")
    fun getCompletedSessions(userId: String): Flow<List<HavenSessionEntity>>

    /**
     * Get active (incomplete) session
     */
    @Query("SELECT * FROM haven_sessions WHERE userId = :userId AND isCompleted = 0 AND isDeleted = 0 ORDER BY startedAt DESC LIMIT 1")
    suspend fun getActiveSession(userId: String): HavenSessionEntity?

    /**
     * Get sessions in date range
     */
    @Query("SELECT * FROM haven_sessions WHERE userId = :userId AND startedAt BETWEEN :startDate AND :endDate AND isDeleted = 0 ORDER BY startedAt DESC")
    fun getSessionsByDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<HavenSessionEntity>>

    /**
     * Get total session count for user
     */
    @Query("SELECT COUNT(*) FROM haven_sessions WHERE userId = :userId AND isDeleted = 0")
    fun getTotalSessionCount(userId: String): Flow<Int>

    /**
     * Get completed session count
     */
    @Query("SELECT COUNT(*) FROM haven_sessions WHERE userId = :userId AND isCompleted = 1 AND isDeleted = 0")
    suspend fun getCompletedSessionCount(userId: String): Int

    /**
     * Get sessions with crisis detection
     */
    @Query("SELECT * FROM haven_sessions WHERE userId = :userId AND containedCrisisDetection = 1 AND isDeleted = 0 ORDER BY startedAt DESC")
    fun getCrisisSessions(userId: String): Flow<List<HavenSessionEntity>>

    /**
     * Get sessions with mood improvement (moodAfter > moodBefore)
     */
    @Query("""
        SELECT * FROM haven_sessions
        WHERE userId = :userId
        AND moodBefore IS NOT NULL
        AND moodAfter IS NOT NULL
        AND moodAfter > moodBefore
        AND isDeleted = 0
        ORDER BY startedAt DESC
    """)
    fun getSessionsWithMoodImprovement(userId: String): Flow<List<HavenSessionEntity>>

    /**
     * Get average mood improvement
     */
    @Query("""
        SELECT AVG(moodAfter - moodBefore)
        FROM haven_sessions
        WHERE userId = :userId
        AND moodBefore IS NOT NULL
        AND moodAfter IS NOT NULL
        AND isDeleted = 0
    """)
    suspend fun getAverageMoodImprovement(userId: String): Float?

    /**
     * Get session type distribution
     */
    @Query("""
        SELECT sessionType, COUNT(*) as count
        FROM haven_sessions
        WHERE userId = :userId AND isDeleted = 0
        GROUP BY sessionType
        ORDER BY count DESC
    """)
    suspend fun getSessionTypeDistribution(userId: String): List<SessionTypeCount>

    /**
     * Insert a new session
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: HavenSessionEntity): Long

    /**
     * Update an existing session
     */
    @Update
    suspend fun updateSession(session: HavenSessionEntity)

    /**
     * Update session completion status
     */
    @Query("UPDATE haven_sessions SET isCompleted = :isCompleted, endedAt = :endedAt WHERE id = :sessionId")
    suspend fun updateSessionCompletion(sessionId: Long, isCompleted: Boolean, endedAt: Long = System.currentTimeMillis())

    /**
     * Update session mood after
     */
    @Query("UPDATE haven_sessions SET moodAfter = :moodAfter WHERE id = :sessionId")
    suspend fun updateSessionMoodAfter(sessionId: Long, moodAfter: Int)

    /**
     * Update session rating
     */
    @Query("UPDATE haven_sessions SET userRating = :rating WHERE id = :sessionId")
    suspend fun updateSessionRating(sessionId: Long, rating: Int)

    /**
     * Delete a session (soft delete)
     */
    @Query("UPDATE haven_sessions SET isDeleted = 1 WHERE id = :sessionId")
    suspend fun softDeleteSession(sessionId: Long)

    /**
     * Delete a session permanently
     */
    @Delete
    suspend fun deleteSession(session: HavenSessionEntity)

    /**
     * Delete all sessions (for data clearing)
     */
    @Query("DELETE FROM haven_sessions WHERE userId = :userId")
    suspend fun deleteAllSessions(userId: String)

    // ==================== EXERCISE QUERIES ====================

    /**
     * Get all exercises for a user
     */
    @Query("SELECT * FROM haven_exercises WHERE userId = :userId AND isDeleted = 0 ORDER BY completedAt DESC")
    fun getAllExercises(userId: String): Flow<List<HavenExerciseEntity>>

    /**
     * Get a specific exercise by ID
     */
    @Query("SELECT * FROM haven_exercises WHERE id = :exerciseId")
    suspend fun getExerciseById(exerciseId: Long): HavenExerciseEntity?

    /**
     * Get exercises by type
     */
    @Query("SELECT * FROM haven_exercises WHERE userId = :userId AND exerciseType = :type AND isDeleted = 0 ORDER BY completedAt DESC")
    fun getExercisesByType(userId: String, type: String): Flow<List<HavenExerciseEntity>>

    /**
     * Get exercises from a specific session
     */
    @Query("SELECT * FROM haven_exercises WHERE fromSessionId = :sessionId AND isDeleted = 0 ORDER BY completedAt ASC")
    fun getExercisesFromSession(sessionId: Long): Flow<List<HavenExerciseEntity>>

    /**
     * Get recent exercises
     */
    @Query("SELECT * FROM haven_exercises WHERE userId = :userId AND isDeleted = 0 ORDER BY completedAt DESC LIMIT :limit")
    fun getRecentExercises(userId: String, limit: Int = 10): Flow<List<HavenExerciseEntity>>

    /**
     * Get completed exercises (wasCompleted = true)
     */
    @Query("SELECT * FROM haven_exercises WHERE userId = :userId AND wasCompleted = 1 AND isDeleted = 0 ORDER BY completedAt DESC")
    fun getCompletedExercises(userId: String): Flow<List<HavenExerciseEntity>>

    /**
     * Get exercises in date range
     */
    @Query("SELECT * FROM haven_exercises WHERE userId = :userId AND completedAt BETWEEN :startDate AND :endDate AND isDeleted = 0 ORDER BY completedAt DESC")
    fun getExercisesByDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<HavenExerciseEntity>>

    /**
     * Get total exercise count
     */
    @Query("SELECT COUNT(*) FROM haven_exercises WHERE userId = :userId AND isDeleted = 0")
    fun getTotalExerciseCount(userId: String): Flow<Int>

    /**
     * Get completed exercise count
     */
    @Query("SELECT COUNT(*) FROM haven_exercises WHERE userId = :userId AND wasCompleted = 1 AND isDeleted = 0")
    suspend fun getCompletedExerciseCount(userId: String): Int

    /**
     * Get exercise type distribution
     */
    @Query("""
        SELECT exerciseType, COUNT(*) as count
        FROM haven_exercises
        WHERE userId = :userId AND isDeleted = 0
        GROUP BY exerciseType
        ORDER BY count DESC
    """)
    suspend fun getExerciseTypeDistribution(userId: String): List<ExerciseTypeCount>

    /**
     * Get total time spent in exercises (in seconds)
     */
    @Query("SELECT SUM(durationSeconds) FROM haven_exercises WHERE userId = :userId AND wasCompleted = 1 AND isDeleted = 0")
    suspend fun getTotalExerciseTime(userId: String): Int?

    /**
     * Get average exercise duration
     */
    @Query("SELECT AVG(durationSeconds) FROM haven_exercises WHERE userId = :userId AND wasCompleted = 1 AND isDeleted = 0")
    suspend fun getAverageExerciseDuration(userId: String): Float?

    /**
     * Insert a new exercise
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: HavenExerciseEntity): Long

    /**
     * Update an exercise
     */
    @Update
    suspend fun updateExercise(exercise: HavenExerciseEntity)

    /**
     * Update exercise helpfulness rating
     */
    @Query("UPDATE haven_exercises SET helpfulness = :rating WHERE id = :exerciseId")
    suspend fun updateExerciseHelpfulness(exerciseId: Long, rating: Int)

    /**
     * Delete an exercise (soft delete)
     */
    @Query("UPDATE haven_exercises SET isDeleted = 1 WHERE id = :exerciseId")
    suspend fun softDeleteExercise(exerciseId: Long)

    /**
     * Delete an exercise permanently
     */
    @Delete
    suspend fun deleteExercise(exercise: HavenExerciseEntity)

    /**
     * Delete all exercises
     */
    @Query("DELETE FROM haven_exercises WHERE userId = :userId")
    suspend fun deleteAllExercises(userId: String)

    // ==================== STATISTICS & INSIGHTS ====================

    /**
     * Get today's session count
     */
    @Query("SELECT COUNT(*) FROM haven_sessions WHERE userId = :userId AND startedAt >= :startOfDay AND isDeleted = 0")
    suspend fun getTodaySessionCount(userId: String, startOfDay: Long): Int

    /**
     * Get today's exercise count
     */
    @Query("SELECT COUNT(*) FROM haven_exercises WHERE userId = :userId AND completedAt >= :startOfDay AND isDeleted = 0")
    suspend fun getTodayExerciseCount(userId: String, startOfDay: Long): Int

    /**
     * Get this week's session count
     */
    @Query("SELECT COUNT(*) FROM haven_sessions WHERE userId = :userId AND startedAt >= :weekStart AND isDeleted = 0")
    suspend fun getWeekSessionCount(userId: String, weekStart: Long): Int

    /**
     * Get this week's exercise count
     */
    @Query("SELECT COUNT(*) FROM haven_exercises WHERE userId = :userId AND completedAt >= :weekStart AND isDeleted = 0")
    suspend fun getWeekExerciseCount(userId: String, weekStart: Long): Int

    /**
     * Get session count since timestamp
     */
    @Query("SELECT COUNT(*) FROM haven_sessions WHERE userId = :userId AND startedAt >= :since AND isDeleted = 0")
    suspend fun getSessionCountSince(userId: String, since: Long): Int

    /**
     * Check if user has had any crisis sessions
     */
    @Query("SELECT COUNT(*) > 0 FROM haven_sessions WHERE userId = :userId AND containedCrisisDetection = 1 AND isDeleted = 0")
    suspend fun hasHadCrisisSessions(userId: String): Boolean

    /**
     * Get most recent session type
     */
    @Query("SELECT sessionType FROM haven_sessions WHERE userId = :userId AND isDeleted = 0 ORDER BY startedAt DESC LIMIT 1")
    suspend fun getMostRecentSessionType(userId: String): String?

    /**
     * Get most common exercise type
     */
    @Query("""
        SELECT exerciseType
        FROM haven_exercises
        WHERE userId = :userId AND isDeleted = 0
        GROUP BY exerciseType
        ORDER BY COUNT(*) DESC
        LIMIT 1
    """)
    suspend fun getMostCommonExerciseType(userId: String): String?
}

/**
 * Data class for session type distribution results
 */
data class SessionTypeCount(
    val sessionType: String,
    val count: Int
)

/**
 * Data class for exercise type distribution results
 */
data class ExerciseTypeCount(
    val exerciseType: String,
    val count: Int
)
