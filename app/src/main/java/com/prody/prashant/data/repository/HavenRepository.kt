package com.prody.prashant.data.repository

import com.prody.prashant.data.ai.HavenAiService
import com.prody.prashant.data.local.dao.HavenDao
import com.prody.prashant.data.local.entity.HavenExerciseEntity
import com.prody.prashant.data.local.entity.HavenSessionEntity
import com.prody.prashant.data.security.EncryptionManager
import com.prody.prashant.domain.haven.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Haven Repository - Handles all Haven data operations
 *
 * Responsibilities:
 * - Coordinate between Haven DAO and AI Service
 * - Encrypt/decrypt sensitive conversation data
 * - Map between entities and domain models
 * - Provide clean API for ViewModels
 */
@Singleton
class HavenRepository @Inject constructor(
    private val havenDao: HavenDao,
    private val havenAiService: HavenAiService,
    private val encryptionManager: EncryptionManager
) {
    private val json = Json { ignoreUnknownKeys = true }

    // ==================== SESSION OPERATIONS ====================

    /**
     * Start a new Haven session
     */
    suspend fun startSession(
        sessionType: SessionType,
        userId: String = "local",
        userName: String? = null,
        moodBefore: Int? = null
    ): Result<Pair<Long, HavenAiResponse>> = withContext(Dispatchers.IO) {
        try {
            // Get user context
            val sessionCount = havenDao.getCompletedSessionCount(userId)
            val context = HavenConversationContext(
                sessionType = sessionType,
                previousMessages = emptyList(),
                moodBefore = moodBefore,
                userName = userName,
                hasUsedTechniquesBefore = emptyList(),
                preferredExercises = emptyList(),
                sessionNumber = sessionCount + 1
            )

            // Get AI greeting
            val aiResult = havenAiService.startSession(sessionType, userName, context)
            if (aiResult.isFailure) {
                return@withContext Result.failure(aiResult.exceptionOrNull()!!)
            }

            val aiResponse = aiResult.getOrThrow()

            // Create initial session entity
            val sessionEntity = HavenSessionEntity(
                userId = userId,
                sessionType = sessionType.name,
                startedAt = System.currentTimeMillis(),
                moodBefore = moodBefore,
                messagesJson = encryptionManager.encryptText(
                    json.encodeToString(
                        listOf(
                            HavenMessage(
                                content = aiResponse.message,
                                isUser = false,
                                isCrisisResponse = aiResponse.isCrisisDetected,
                                recalledMessage = aiResponse.recalledContent
                            )
                        )
                    )
                ),
                containedCrisisDetection = aiResponse.isCrisisDetected
            )

            val sessionId = havenDao.insertSession(sessionEntity)

            Result.success(Pair(sessionId, aiResponse))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Continue a Haven conversation
     */
    suspend fun continueConversation(
        sessionId: Long,
        userMessage: String,
        userId: String = "local"
    ): Result<HavenAiResponse> = withContext(Dispatchers.IO) {
        try {
            val session = havenDao.getSessionById(sessionId)
                ?: return@withContext Result.failure(Exception("Session not found"))

            // Decrypt and parse existing messages
            val existingMessages = decryptMessages(session.messagesJson)

            // Build context
            val context = HavenConversationContext(
                sessionType = SessionType.fromString(session.sessionType),
                previousMessages = existingMessages,
                moodBefore = session.moodBefore,
                userName = null, // Could fetch from user profile
                hasUsedTechniquesBefore = emptyList(),
                preferredExercises = emptyList(),
                sessionNumber = havenDao.getCompletedSessionCount(userId)
            )

            // Get AI response
            val aiResult = havenAiService.continueConversation(
                sessionType = SessionType.fromString(session.sessionType),
                context = context,
                userMessage = userMessage
            )

            if (aiResult.isFailure) {
                return@withContext Result.failure(aiResult.exceptionOrNull()!!)
            }

            val aiResponse = aiResult.getOrThrow()

            // Add both messages to conversation
            val userMsg = HavenMessage(content = userMessage, isUser = true)
            val aiMsg = HavenMessage(
                content = aiResponse.message,
                isUser = false,
                techniqueUsed = aiResponse.techniqueApplied,
                exerciseSuggested = aiResponse.suggestedExercise,
                isCrisisResponse = aiResponse.isCrisisDetected,
                recalledMessage = aiResponse.recalledContent
            )

            val updatedMessages = existingMessages + listOf(userMsg, aiMsg)

            // Update session
            val updatedSession = session.copy(
                messagesJson = encryptionManager.encryptText(json.encodeToString(updatedMessages)),
                containedCrisisDetection = session.containedCrisisDetection || aiResponse.isCrisisDetected
            )

            havenDao.updateSession(updatedSession)

            Result.success(aiResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Complete a Haven session
     */
    suspend fun completeSession(
        sessionId: Long,
        moodAfter: Int? = null
    ): Result<SessionSummary> = withContext(Dispatchers.IO) {
        try {
            val session = havenDao.getSessionById(sessionId)
                ?: return@withContext Result.failure(Exception("Session not found"))

            val messages = decryptMessages(session.messagesJson)

            // Extract insights
            val insightsResult = havenAiService.extractInsights(
                messages = messages,
                sessionType = SessionType.fromString(session.sessionType)
            )

            val insights = insightsResult.getOrNull() ?: emptyList()

            // Update session
            havenDao.updateSessionCompletion(sessionId, true, System.currentTimeMillis())
            if (moodAfter != null) {
                havenDao.updateSessionMoodAfter(sessionId, moodAfter)
            }

            val updatedSession = havenDao.getSessionById(sessionId)
                ?: return@withContext Result.failure(IllegalStateException("Session not found"))

            // Build summary
            val summary = SessionSummary(
                sessionType = SessionType.fromString(updatedSession.sessionType),
                duration = (updatedSession.endedAt ?: System.currentTimeMillis()) - updatedSession.startedAt,
                messageCount = messages.size,
                techniquesUsed = messages.mapNotNull { it.techniqueUsed }.distinct(),
                exercisesCompleted = emptyList(), // Would need to query exercise completions
                moodBefore = updatedSession.moodBefore,
                moodAfter = updatedSession.moodAfter,
                moodChange = run {
                    val before = updatedSession.moodBefore
                    val after = updatedSession.moodAfter
                    if (before != null && after != null) after - before else null
                },
                keyInsights = insights,
                suggestedFollowUp = null,
                containedCrisis = updatedSession.containedCrisisDetection
            )

            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get all sessions for a user
     */
    fun getSessions(userId: String = "local"): Flow<List<HavenSessionEntity>> {
        return havenDao.getSessionsByUser(userId)
    }

    /**
     * Get a session with decrypted messages
     */
    suspend fun getSessionWithMessages(sessionId: Long): Result<Pair<HavenSessionEntity, List<HavenMessage>>> {
        return withContext(Dispatchers.IO) {
            try {
                val session = havenDao.getSessionById(sessionId)
                    ?: return@withContext Result.failure(Exception("Session not found"))

                val messages = decryptMessages(session.messagesJson)

                Result.success(Pair(session, messages))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Delete a session
     */
    suspend fun deleteSession(sessionId: Long) = withContext(Dispatchers.IO) {
        havenDao.softDeleteSession(sessionId)
    }

    // ==================== EXERCISE OPERATIONS ====================

    /**
     * Start a guided exercise
     */
    suspend fun startExercise(
        exerciseType: ExerciseType,
        userId: String = "local",
        fromSessionId: Long? = null
    ): Result<GuidedExercise> = withContext(Dispatchers.IO) {
        try {
            val exercise = GuidedExercises.getExercise(exerciseType)
            Result.success(exercise)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Complete an exercise
     */
    suspend fun completeExercise(
        exerciseType: ExerciseType,
        durationSeconds: Int,
        notes: String? = null,
        userId: String = "local",
        fromSessionId: Long? = null,
        wasCompleted: Boolean = true,
        completionRate: Float = 1.0f
    ): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val entity = HavenExerciseEntity(
                userId = userId,
                exerciseType = exerciseType.name,
                completedAt = System.currentTimeMillis(),
                durationSeconds = durationSeconds,
                notes = notes?.let { encryptionManager.encryptText(it) },
                fromSessionId = fromSessionId,
                wasCompleted = wasCompleted,
                completionRate = completionRate
            )

            val exerciseId = havenDao.insertExercise(entity)
            Result.success(exerciseId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get all exercises for a user
     */
    fun getExercises(userId: String = "local"): Flow<List<HavenExerciseEntity>> {
        return havenDao.getAllExercises(userId)
    }

    /**
     * Get exercises from a specific session
     */
    fun getExercisesFromSession(sessionId: Long): Flow<List<HavenExerciseEntity>> {
        return havenDao.getExercisesFromSession(sessionId)
    }

    // ==================== STATISTICS ====================

    /**
     * Get comprehensive Haven statistics
     */
    suspend fun getStats(userId: String = "local"): Result<HavenStats> = withContext(Dispatchers.IO) {
        try {
            val totalSessions = havenDao.getTotalSessionCount(userId).first()
            val completedSessions = havenDao.getCompletedSessionCount(userId)
            val totalExercises = havenDao.getTotalExerciseCount(userId).first()
            val completedExercises = havenDao.getCompletedExerciseCount(userId)
            val totalTimeSeconds = havenDao.getTotalExerciseTime(userId) ?: 0
            val avgMoodImprovement = havenDao.getAverageMoodImprovement(userId)
            val mostUsedSessionType = havenDao.getMostRecentSessionType(userId)
            val mostUsedExercise = havenDao.getMostCommonExerciseType(userId)
            val hasCrisis = havenDao.hasHadCrisisSessions(userId)

            val stats = HavenStats(
                totalSessions = totalSessions,
                completedSessions = completedSessions,
                totalExercises = totalExercises,
                completedExercises = completedExercises,
                totalTimeMinutes = totalTimeSeconds / 60,
                averageMoodImprovement = avgMoodImprovement,
                mostUsedSessionType = mostUsedSessionType?.let { SessionType.fromString(it) },
                mostUsedExercise = mostUsedExercise?.let { ExerciseType.fromString(it) },
                streakDays = 0, // Would need separate streak tracking
                sessionTypeBreakdown = emptyMap(),
                exerciseTypeBreakdown = emptyMap(),
                hasCrisisHistory = hasCrisis
            )

            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Decrypt messages from JSON string
     */
    private fun decryptMessages(encryptedJson: String): List<HavenMessage> {
        return try {
            val decrypted = encryptionManager.decryptText(encryptedJson)
            json.decodeFromString<List<HavenMessage>>(decrypted)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Check if Haven is configured
     */
    fun isConfigured(): Boolean = havenAiService.isConfigured()

    /**
     * Get details about configuration status for debugging
     */
    fun getConfigurationStatus(): String = havenAiService.getConfigurationStatus()

    /**
     * Check if the service is in offline mode
     */
    fun isOffline(): Boolean = havenAiService.isOffline()
}
