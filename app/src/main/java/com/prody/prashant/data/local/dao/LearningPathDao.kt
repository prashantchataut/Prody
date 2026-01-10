package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Learning Path operations
 */
@Dao
interface LearningPathDao {

    // ==================== LEARNING PATHS ====================

    @Query("SELECT * FROM learning_paths WHERE userId = :userId ORDER BY lastAccessedAt DESC")
    fun getAllPaths(userId: String): Flow<List<LearningPathEntity>>

    @Query("SELECT * FROM learning_paths WHERE userId = :userId AND isActive = 1 ORDER BY lastAccessedAt DESC")
    fun getActivePaths(userId: String): Flow<List<LearningPathEntity>>

    @Query("SELECT * FROM learning_paths WHERE userId = :userId AND completedAt IS NOT NULL ORDER BY completedAt DESC")
    fun getCompletedPaths(userId: String): Flow<List<LearningPathEntity>>

    @Query("SELECT * FROM learning_paths WHERE id = :pathId")
    suspend fun getPathById(pathId: String): LearningPathEntity?

    @Query("SELECT * FROM learning_paths WHERE id = :pathId")
    fun observePathById(pathId: String): Flow<LearningPathEntity?>

    @Query("SELECT * FROM learning_paths WHERE userId = :userId AND pathType = :pathType LIMIT 1")
    suspend fun getPathByType(userId: String, pathType: String): LearningPathEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPath(path: LearningPathEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaths(paths: List<LearningPathEntity>)

    @Update
    suspend fun updatePath(path: LearningPathEntity)

    @Delete
    suspend fun deletePath(path: LearningPathEntity)

    @Query("UPDATE learning_paths SET lastAccessedAt = :timestamp WHERE id = :pathId")
    suspend fun updateLastAccessed(pathId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE learning_paths SET completedLessons = :count, progressPercentage = :percentage WHERE id = :pathId")
    suspend fun updateProgress(pathId: String, count: Int, percentage: Float)

    @Query("UPDATE learning_paths SET completedAt = :timestamp, isActive = 0 WHERE id = :pathId")
    suspend fun markPathCompleted(pathId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE learning_paths SET currentLessonId = :lessonId WHERE id = :pathId")
    suspend fun updateCurrentLesson(pathId: String, lessonId: String?)

    @Query("SELECT COUNT(*) FROM learning_paths WHERE userId = :userId AND completedAt IS NOT NULL")
    suspend fun getCompletedPathCount(userId: String): Int

    // ==================== LEARNING LESSONS ====================

    @Query("SELECT * FROM learning_lessons WHERE pathId = :pathId ORDER BY orderIndex ASC")
    fun getLessonsForPath(pathId: String): Flow<List<LearningLessonEntity>>

    @Query("SELECT * FROM learning_lessons WHERE pathId = :pathId ORDER BY orderIndex ASC")
    suspend fun getLessonsForPathSync(pathId: String): List<LearningLessonEntity>

    @Query("SELECT * FROM learning_lessons WHERE id = :lessonId")
    suspend fun getLessonById(lessonId: String): LearningLessonEntity?

    @Query("SELECT * FROM learning_lessons WHERE id = :lessonId")
    fun observeLessonById(lessonId: String): Flow<LearningLessonEntity?>

    @Query("SELECT * FROM learning_lessons WHERE pathId = :pathId AND isCompleted = 0 ORDER BY orderIndex ASC LIMIT 1")
    suspend fun getNextIncompleteLesson(pathId: String): LearningLessonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LearningLessonEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LearningLessonEntity>)

    @Update
    suspend fun updateLesson(lesson: LearningLessonEntity)

    @Delete
    suspend fun deleteLesson(lesson: LearningLessonEntity)

    @Query("UPDATE learning_lessons SET isCompleted = 1, completedAt = :timestamp WHERE id = :lessonId")
    suspend fun markLessonCompleted(lessonId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE learning_lessons SET quizScore = :score WHERE id = :lessonId")
    suspend fun updateQuizScore(lessonId: String, score: Int)

    @Query("UPDATE learning_lessons SET userNotesJson = :notesJson WHERE id = :lessonId")
    suspend fun updateLessonNotes(lessonId: String, notesJson: String)

    @Query("UPDATE learning_lessons SET isLocked = 0 WHERE id = :lessonId")
    suspend fun unlockLesson(lessonId: String)

    @Query("SELECT COUNT(*) FROM learning_lessons WHERE pathId = :pathId AND isCompleted = 1")
    suspend fun getCompletedLessonCount(pathId: String): Int

    @Query("SELECT COUNT(*) FROM learning_lessons WHERE pathId = :pathId")
    suspend fun getTotalLessonCount(pathId: String): Int

    // ==================== LEARNING REFLECTIONS ====================

    @Query("SELECT * FROM learning_reflections WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllReflections(userId: String): Flow<List<LearningReflectionEntity>>

    @Query("SELECT * FROM learning_reflections WHERE lessonId = :lessonId ORDER BY createdAt DESC")
    fun getReflectionsForLesson(lessonId: String): Flow<List<LearningReflectionEntity>>

    @Query("SELECT * FROM learning_reflections WHERE pathId = :pathId ORDER BY createdAt DESC")
    fun getReflectionsForPath(pathId: String): Flow<List<LearningReflectionEntity>>

    @Query("SELECT * FROM learning_reflections WHERE id = :reflectionId")
    suspend fun getReflectionById(reflectionId: Long): LearningReflectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReflection(reflection: LearningReflectionEntity): Long

    @Update
    suspend fun updateReflection(reflection: LearningReflectionEntity)

    @Delete
    suspend fun deleteReflection(reflection: LearningReflectionEntity)

    @Query("UPDATE learning_reflections SET aiInsight = :insight WHERE id = :reflectionId")
    suspend fun updateReflectionInsight(reflectionId: Long, insight: String)

    @Query("UPDATE learning_reflections SET isBookmarked = :isBookmarked WHERE id = :reflectionId")
    suspend fun updateReflectionBookmark(reflectionId: Long, isBookmarked: Boolean)

    @Query("SELECT COUNT(*) FROM learning_reflections WHERE userId = :userId")
    suspend fun getTotalReflectionCount(userId: String): Int

    // ==================== PATH RECOMMENDATIONS ====================

    @Query("SELECT * FROM path_recommendations WHERE userId = :userId AND isDismissed = 0 AND isAccepted = 0 ORDER BY confidenceScore DESC, createdAt DESC")
    fun getActiveRecommendations(userId: String): Flow<List<PathRecommendationEntity>>

    @Query("SELECT * FROM path_recommendations WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllRecommendations(userId: String): Flow<List<PathRecommendationEntity>>

    @Query("SELECT * FROM path_recommendations WHERE id = :recommendationId")
    suspend fun getRecommendationById(recommendationId: Long): PathRecommendationEntity?

    @Query("SELECT * FROM path_recommendations WHERE userId = :userId AND pathType = :pathType AND isDismissed = 0 AND isAccepted = 0 LIMIT 1")
    suspend fun getActiveRecommendationForPath(userId: String, pathType: String): PathRecommendationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendation(recommendation: PathRecommendationEntity): Long

    @Update
    suspend fun updateRecommendation(recommendation: PathRecommendationEntity)

    @Delete
    suspend fun deleteRecommendation(recommendation: PathRecommendationEntity)

    @Query("UPDATE path_recommendations SET isDismissed = 1, dismissedAt = :timestamp WHERE id = :recommendationId")
    suspend fun dismissRecommendation(recommendationId: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE path_recommendations SET isAccepted = 1, acceptedAt = :timestamp WHERE id = :recommendationId")
    suspend fun acceptRecommendation(recommendationId: Long, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM path_recommendations WHERE userId = :userId AND isDismissed = 1 AND dismissedAt < :olderThan")
    suspend fun cleanupOldDismissedRecommendations(userId: String, olderThan: Long)

    // ==================== PATH PROGRESS CHECKPOINTS ====================

    @Query("SELECT * FROM path_progress_checkpoints WHERE pathId = :pathId ORDER BY achievedAt DESC")
    fun getCheckpointsForPath(pathId: String): Flow<List<PathProgressCheckpointEntity>>

    @Query("SELECT * FROM path_progress_checkpoints WHERE userId = :userId ORDER BY achievedAt DESC LIMIT :limit")
    fun getRecentCheckpoints(userId: String, limit: Int = 10): Flow<List<PathProgressCheckpointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckpoint(checkpoint: PathProgressCheckpointEntity): Long

    @Query("UPDATE path_progress_checkpoints SET celebrationShown = 1 WHERE id = :checkpointId")
    suspend fun markCheckpointCelebrated(checkpointId: Long)

    @Query("SELECT SUM(xpEarned) FROM path_progress_checkpoints WHERE userId = :userId AND pathId = :pathId")
    suspend fun getTotalXpForPath(userId: String, pathId: String): Int?

    // ==================== LEARNING NOTES ====================

    @Query("SELECT * FROM learning_notes WHERE lessonId = :lessonId ORDER BY createdAt DESC")
    fun getNotesForLesson(lessonId: String): Flow<List<LearningNoteEntity>>

    @Query("SELECT * FROM learning_notes WHERE pathId = :pathId ORDER BY createdAt DESC")
    fun getNotesForPath(pathId: String): Flow<List<LearningNoteEntity>>

    @Query("SELECT * FROM learning_notes WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllNotes(userId: String): Flow<List<LearningNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: LearningNoteEntity): Long

    @Update
    suspend fun updateNote(note: LearningNoteEntity)

    @Delete
    suspend fun deleteNote(note: LearningNoteEntity)

    // ==================== PATH BADGES ====================

    @Query("SELECT * FROM path_badges WHERE userId = :userId ORDER BY earnedAt DESC")
    fun getAllBadges(userId: String): Flow<List<PathBadgeEntity>>

    @Query("SELECT * FROM path_badges WHERE pathId = :pathId ORDER BY earnedAt DESC")
    fun getBadgesForPath(pathId: String): Flow<List<PathBadgeEntity>>

    @Query("SELECT * FROM path_badges WHERE userId = :userId AND isDisplayed = 1 ORDER BY earnedAt DESC LIMIT :limit")
    fun getDisplayedBadges(userId: String, limit: Int = 3): Flow<List<PathBadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadge(badge: PathBadgeEntity): Long

    @Update
    suspend fun updateBadge(badge: PathBadgeEntity)

    @Query("UPDATE path_badges SET isDisplayed = :isDisplayed WHERE id = :badgeId")
    suspend fun updateBadgeDisplay(badgeId: Long, isDisplayed: Boolean)

    @Query("SELECT COUNT(*) FROM path_badges WHERE userId = :userId")
    suspend fun getTotalBadgeCount(userId: String): Int

    // ==================== ANALYTICS & STATS ====================

    @Query("""
        SELECT COUNT(*) FROM learning_lessons
        WHERE pathId IN (SELECT id FROM learning_paths WHERE userId = :userId)
        AND isCompleted = 1
    """)
    suspend fun getTotalLessonsCompleted(userId: String): Int

    @Query("""
        SELECT SUM(estimatedMinutes) FROM learning_lessons
        WHERE pathId IN (SELECT id FROM learning_paths WHERE userId = :userId)
        AND isCompleted = 1
    """)
    suspend fun getTotalMinutesLearned(userId: String): Int?

    @Query("""
        SELECT COUNT(*) FROM learning_lessons
        WHERE pathId = :pathId
        AND isCompleted = 1
        AND completedAt >= :since
    """)
    suspend fun getLessonsCompletedSince(pathId: String, since: Long): Int

    @Query("""
        SELECT pathType, COUNT(*) as count
        FROM learning_paths
        WHERE userId = :userId AND completedAt IS NOT NULL
        GROUP BY pathType
    """)
    suspend fun getCompletedPathsByType(userId: String): List<PathTypeCount>

    @Transaction
    suspend fun completeLesson(lessonId: String, pathId: String) {
        markLessonCompleted(lessonId)
        val completedCount = getCompletedLessonCount(pathId)
        val totalCount = getTotalLessonCount(pathId)
        val percentage = if (totalCount > 0) (completedCount.toFloat() / totalCount * 100) else 0f
        updateProgress(pathId, completedCount, percentage)

        if (completedCount >= totalCount) {
            markPathCompleted(pathId)
        }
    }

    @Transaction
    suspend fun startPath(path: LearningPathEntity, lessons: List<LearningLessonEntity>) {
        insertPath(path)
        insertLessons(lessons)
        // Unlock first lesson
        if (lessons.isNotEmpty()) {
            unlockLesson(lessons.first().id)
        }
    }
}

/**
 * Data class for path type statistics
 */
data class PathTypeCount(
    val pathType: String,
    val count: Int
)
