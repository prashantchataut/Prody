package com.prody.prashant.domain.repository

import com.prody.prashant.data.local.dao.LearningPathDao
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.entity.*
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.common.ErrorType
import com.prody.prashant.domain.learning.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Learning Path Repository
 *
 * Central repository for all learning path operations.
 * Handles path creation, progress tracking, lesson completion,
 * reflections, recommendations, and achievements.
 */
@Singleton
class LearningPathRepository @Inject constructor(
    private val learningPathDao: LearningPathDao,
    private val journalDao: JournalDao,
    private val pathRecommender: PathRecommender,
    private val pathContentProvider: PathContentProvider
) {

    private val gson = Gson()
    private val userId = "local"

    // ==================== PATH OPERATIONS ====================

    /**
     * Get all paths for user
     */
    fun observeAllPaths(): Flow<List<LearningPath>> {
        return learningPathDao.getAllPaths(userId).map { entities ->
            entities.map { pathEntity ->
                mapToLearningPath(pathEntity)
            }
        }
    }

    /**
     * Get active paths
     */
    fun observeActivePaths(): Flow<List<LearningPath>> {
        return learningPathDao.getActivePaths(userId).map { entities ->
            entities.map { mapToLearningPath(it) }
        }
    }

    /**
     * Get completed paths
     */
    fun observeCompletedPaths(): Flow<List<LearningPath>> {
        return learningPathDao.getCompletedPaths(userId).map { entities ->
            entities.map { mapToLearningPath(it) }
        }
    }

    /**
     * Get specific path by ID
     */
    fun observePath(pathId: String): Flow<LearningPath?> {
        return learningPathDao.observePathById(pathId).map { entity ->
            entity?.let { mapToLearningPath(it) }
        }
    }

    /**
     * Start a new learning path
     */
    suspend fun startPath(pathType: PathType): Result<LearningPath> {
        return try {
            // Check if path already exists
            val existing = learningPathDao.getPathByType(userId, pathType.id)
            if (existing != null) {
                return Result.error(
                    Exception("You've already started this path"),
                    "You've already started this path",
                    ErrorType.VALIDATION
                )
            }

            // Create path with lessons
            val (pathEntity, lessons) = pathContentProvider.createPathWithLessons(pathType, userId)

            // Insert into database
            learningPathDao.startPath(pathEntity, lessons)

            Result.Success(mapToLearningPath(pathEntity))
        } catch (e: Exception) {
            Result.error(e, "Failed to start path: ${e.message}", ErrorType.DATABASE)
        }
    }

    /**
     * Resume or continue a path
     */
    suspend fun resumePath(pathId: String): Result<Unit> {
        return try {
            learningPathDao.updateLastAccessed(pathId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to resume path: ${e.message}", ErrorType.DATABASE)
        }
    }

    // ==================== LESSON OPERATIONS ====================

    /**
     * Get all lessons for a path
     */
    fun observeLessons(pathId: String): Flow<List<Lesson>> {
        return learningPathDao.getLessonsForPath(pathId).map { entities ->
            entities.map { mapToLesson(it) }
        }
    }

    /**
     * Get specific lesson
     */
    suspend fun getLesson(lessonId: String): Result<Lesson> {
        return try {
            val lesson = learningPathDao.getLessonById(lessonId)
            if (lesson != null) {
                Result.Success(mapToLesson(lesson))
            } else {
                Result.error(
                    Exception("Lesson not found"),
                    "Lesson not found",
                    ErrorType.NOT_FOUND
                )
            }
        } catch (e: Exception) {
            Result.error(e, "Failed to get lesson: ${e.message}", ErrorType.DATABASE)
        }
    }

    /**
     * Complete a lesson
     */
    suspend fun completeLesson(lessonId: String, pathId: String, quizScore: Int? = null): Result<Unit> {
        return try {
            // Mark lesson as completed
            learningPathDao.completeLesson(lessonId, pathId)

            // Update quiz score if applicable
            if (quizScore != null) {
                learningPathDao.updateQuizScore(lessonId, quizScore)
            }

            // Get lessons to unlock next one
            val lessons = learningPathDao.getLessonsForPathSync(pathId)
            val completedLesson = lessons.find { it.id == lessonId }
            val nextLesson = completedLesson?.let { current ->
                lessons.find { it.orderIndex == current.orderIndex + 1 }
            }

            // Unlock next lesson
            nextLesson?.let {
                learningPathDao.unlockLesson(it.id)
                learningPathDao.updateCurrentLesson(pathId, it.id)
            }

            // Create checkpoint
            val checkpoint = PathProgressCheckpointEntity(
                pathId = pathId,
                userId = userId,
                checkpointType = "lesson_complete",
                lessonId = lessonId,
                description = "Completed lesson",
                xpEarned = 25,
                tokensEarned = 10
            )
            learningPathDao.insertCheckpoint(checkpoint)

            // Check if path is complete
            val completed = learningPathDao.getCompletedLessonCount(pathId)
            val total = learningPathDao.getTotalLessonCount(pathId)
            if (completed >= total) {
                val completionCheckpoint = PathProgressCheckpointEntity(
                    pathId = pathId,
                    userId = userId,
                    checkpointType = "path_completed",
                    description = "Completed entire learning path!",
                    xpEarned = 100,
                    tokensEarned = 50
                )
                learningPathDao.insertCheckpoint(completionCheckpoint)

                // Award badge
                val path = learningPathDao.getPathById(pathId)
                path?.let { p ->
                    val badge = PathBadgeEntity(
                        pathId = pathId,
                        userId = userId,
                        badgeType = "path_completed",
                        badgeName = "${p.title} Master",
                        badgeDescription = "Completed the entire ${p.title} learning path",
                        badgeIcon = p.iconEmoji,
                        rarity = "rare"
                    )
                    learningPathDao.insertBadge(badge)
                }
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to complete lesson: ${e.message}", ErrorType.DATABASE)
        }
    }

    /**
     * Save user notes for a lesson
     */
    suspend fun saveNotes(lessonId: String, pathId: String, noteContent: String): Result<Unit> {
        return try {
            val note = LearningNoteEntity(
                lessonId = lessonId,
                pathId = pathId,
                userId = userId,
                noteContent = noteContent
            )
            learningPathDao.insertNote(note)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to save notes: ${e.message}", ErrorType.DATABASE)
        }
    }

    // ==================== REFLECTION OPERATIONS ====================

    /**
     * Save a reflection
     */
    suspend fun saveReflection(
        lessonId: String,
        pathId: String,
        promptText: String,
        userResponse: String,
        mood: String? = null
    ): Result<Long> {
        return try {
            val wordCount = userResponse.split("\\s+".toRegex()).size
            val reflection = LearningReflectionEntity(
                lessonId = lessonId,
                pathId = pathId,
                userId = userId,
                promptText = promptText,
                userResponse = userResponse,
                wordCount = wordCount,
                mood = mood
            )
            val id = learningPathDao.insertReflection(reflection)
            Result.Success(id)
        } catch (e: Exception) {
            Result.error(e, "Failed to save reflection: ${e.message}", ErrorType.DATABASE)
        }
    }

    /**
     * Get reflections for a lesson
     */
    fun observeReflections(lessonId: String): Flow<List<LearningReflection>> {
        return learningPathDao.getReflectionsForLesson(lessonId).map { entities ->
            entities.map { mapToReflection(it) }
        }
    }

    /**
     * Get all reflections for user
     */
    fun observeAllReflections(): Flow<List<LearningReflection>> {
        return learningPathDao.getAllReflections(userId).map { entities ->
            entities.map { mapToReflection(it) }
        }
    }

    /**
     * Bookmark a reflection
     */
    suspend fun bookmarkReflection(reflectionId: Long, bookmarked: Boolean): Result<Unit> {
        return try {
            learningPathDao.updateReflectionBookmark(reflectionId, bookmarked)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to bookmark reflection: ${e.message}", ErrorType.DATABASE)
        }
    }

    // ==================== RECOMMENDATION OPERATIONS ====================

    /**
     * Generate path recommendations based on journal entries
     */
    suspend fun generateRecommendations(): Result<List<PathRecommendation>> {
        return try {
            // Get recent journal entries
            val entries = journalDao.getRecentEntries(20).first()

            // Generate recommendations
            val recommendationEntities = pathRecommender.analyzeAndRecommend(entries, userId)

            // Save to database
            recommendationEntities.forEach { recommendation ->
                // Check if similar recommendation exists
                val existing = learningPathDao.getActiveRecommendationForPath(
                    userId,
                    recommendation.pathType
                )
                if (existing == null) {
                    learningPathDao.insertRecommendation(recommendation)
                }
            }

            // Map to domain models
            val recommendations = recommendationEntities.mapNotNull { entity ->
                val pathType = PathType.fromId(entity.pathType)
                pathType?.let {
                    PathRecommendation(
                        id = entity.id,
                        pathType = it,
                        reason = entity.reason,
                        basedOn = emptyList(), // Could parse JSON if needed
                        confidence = entity.confidenceScore,
                        createdAt = entity.createdAt
                    )
                }
            }

            Result.Success(recommendations)
        } catch (e: Exception) {
            Result.error(e, "Failed to generate recommendations: ${e.message}", ErrorType.AI_SERVICE)
        }
    }

    /**
     * Get active recommendations
     */
    fun observeActiveRecommendations(): Flow<List<PathRecommendation>> {
        return learningPathDao.getActiveRecommendations(userId).map { entities ->
            entities.mapNotNull { entity ->
                val pathType = PathType.fromId(entity.pathType)
                pathType?.let {
                    PathRecommendation(
                        id = entity.id,
                        pathType = it,
                        reason = entity.reason,
                        basedOn = emptyList(),
                        confidence = entity.confidenceScore,
                        createdAt = entity.createdAt
                    )
                }
            }
        }
    }

    /**
     * Accept a recommendation
     */
    suspend fun acceptRecommendation(recommendationId: Long): Result<LearningPath> {
        return try {
            val recommendation = learningPathDao.getRecommendationById(recommendationId)
            if (recommendation != null) {
                learningPathDao.acceptRecommendation(recommendationId)
                val pathType = PathType.fromId(recommendation.pathType)
                if (pathType != null) {
                    startPath(pathType)
                } else {
                    Result.error(
                        Exception("Invalid path type"),
                        "Invalid path type",
                        ErrorType.VALIDATION
                    )
                }
            } else {
                Result.error(
                    Exception("Recommendation not found"),
                    "Recommendation not found",
                    ErrorType.NOT_FOUND
                )
            }
        } catch (e: Exception) {
            Result.error(e, "Failed to accept recommendation: ${e.message}", ErrorType.DATABASE)
        }
    }

    /**
     * Dismiss a recommendation
     */
    suspend fun dismissRecommendation(recommendationId: Long): Result<Unit> {
        return try {
            learningPathDao.dismissRecommendation(recommendationId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to dismiss recommendation: ${e.message}", ErrorType.DATABASE)
        }
    }

    // ==================== BADGE & ACHIEVEMENT OPERATIONS ====================

    /**
     * Get all badges for user
     */
    fun observeBadges(): Flow<List<PathBadge>> {
        return learningPathDao.getAllBadges(userId).map { entities ->
            entities.map { mapToBadge(it) }
        }
    }

    /**
     * Get displayed badges
     */
    fun observeDisplayedBadges(limit: Int = 3): Flow<List<PathBadge>> {
        return learningPathDao.getDisplayedBadges(userId, limit).map { entities ->
            entities.map { mapToBadge(it) }
        }
    }

    // ==================== STATISTICS ====================

    /**
     * Get learning statistics
     */
    suspend fun getLearningStats(): Result<LearningStats> {
        return try {
            val stats = LearningStats(
                totalPathsStarted = learningPathDao.getAllPaths(userId).map { it.size }.first(),
                totalPathsCompleted = learningPathDao.getCompletedPathCount(userId),
                totalLessonsCompleted = learningPathDao.getTotalLessonsCompleted(userId),
                totalMinutesLearned = learningPathDao.getTotalMinutesLearned(userId) ?: 0,
                totalReflections = learningPathDao.getTotalReflectionCount(userId),
                totalBadges = learningPathDao.getTotalBadgeCount(userId),
                currentStreak = 0, // Would calculate based on daily activity
                longestStreak = 0  // Would calculate based on history
            )
            Result.Success(stats)
        } catch (e: Exception) {
            Result.error(e, "Failed to get stats: ${e.message}", ErrorType.DATABASE)
        }
    }

    // ==================== MAPPING FUNCTIONS ====================

    private fun mapToLearningPath(entity: LearningPathEntity): LearningPath {
        val pathType = PathType.fromId(entity.pathType) ?: PathType.MINDFULNESS

        return LearningPath(
            id = entity.id,
            type = pathType,
            lessons = emptyList(), // Loaded separately via observeLessons
            progress = PathProgress(
                completedLessons = entity.completedLessons,
                totalLessons = entity.totalLessons,
                percentage = entity.progressPercentage,
                currentLessonId = entity.currentLessonId,
                startedAt = entity.startedAt,
                lastAccessedAt = entity.lastAccessedAt,
                completedAt = entity.completedAt,
                estimatedMinutesRemaining = entity.estimatedMinutesTotal -
                    (entity.completedLessons * (entity.estimatedMinutesTotal / entity.totalLessons))
            ),
            isActive = entity.isActive,
            recommendation = null
        )
    }

    private fun mapToLesson(entity: LearningLessonEntity): Lesson {
        val lessonType = LessonType.valueOf(entity.lessonType.uppercase())
        val content = when (lessonType) {
            LessonType.READING -> gson.fromJson(entity.contentJson, LessonContent.Reading::class.java)
            LessonType.REFLECTION -> gson.fromJson(entity.contentJson, LessonContent.Reflection::class.java)
            LessonType.EXERCISE -> gson.fromJson(entity.contentJson, LessonContent.Exercise::class.java)
            LessonType.JOURNAL_PROMPT -> gson.fromJson(entity.contentJson, LessonContent.JournalPrompt::class.java)
            LessonType.MEDITATION -> gson.fromJson(entity.contentJson, LessonContent.Meditation::class.java)
            LessonType.QUIZ -> gson.fromJson(entity.contentJson, LessonContent.Quiz::class.java)
        }

        return Lesson(
            id = entity.id,
            pathId = entity.pathId,
            orderIndex = entity.orderIndex,
            title = entity.title,
            type = lessonType,
            content = content,
            estimatedMinutes = entity.estimatedMinutes,
            isCompleted = entity.isCompleted,
            isLocked = entity.isLocked,
            completedAt = entity.completedAt,
            quizScore = entity.quizScore
        )
    }

    private fun mapToReflection(entity: LearningReflectionEntity): LearningReflection {
        return LearningReflection(
            id = entity.id,
            lessonId = entity.lessonId,
            pathId = entity.pathId,
            promptText = entity.promptText,
            userResponse = entity.userResponse,
            aiInsight = entity.aiInsight,
            createdAt = entity.createdAt,
            wordCount = entity.wordCount,
            mood = entity.mood,
            isBookmarked = entity.isBookmarked
        )
    }

    private fun mapToBadge(entity: PathBadgeEntity): PathBadge {
        return PathBadge(
            id = entity.id,
            pathId = entity.pathId,
            badgeType = BadgeType.valueOf(entity.badgeType.uppercase()),
            badgeName = entity.badgeName,
            badgeDescription = entity.badgeDescription,
            badgeIcon = entity.badgeIcon,
            earnedAt = entity.earnedAt,
            rarity = BadgeRarity.valueOf(entity.rarity.uppercase())
        )
    }
}
