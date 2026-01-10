package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.DeepDiveDao
import com.prody.prashant.data.local.entity.DeepDiveEntity
import com.prody.prashant.domain.common.ErrorType
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.common.runSuspendCatching
import com.prody.prashant.domain.deepdive.*
import com.prody.prashant.domain.repository.DeepDiveRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of DeepDiveRepository using Room database.
 */
@Singleton
class DeepDiveRepositoryImpl @Inject constructor(
    private val deepDiveDao: DeepDiveDao,
    private val promptGenerator: DeepDivePromptGenerator,
    private val scheduler: DeepDiveScheduler
) : DeepDiveRepository {

    // ==================== RETRIEVAL ====================

    override suspend fun getDeepDiveById(id: Long, userId: String): Result<DeepDiveEntity?> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to get deep dive") {
            deepDiveDao.getDeepDiveById(id)
        }
    }

    override fun observeDeepDive(id: Long): Flow<DeepDiveEntity?> {
        return deepDiveDao.observeDeepDiveById(id)
    }

    override fun getScheduledDeepDives(userId: String): Flow<List<DeepDiveEntity>> {
        return deepDiveDao.getScheduledDeepDives(userId)
    }

    override fun getCompletedDeepDives(userId: String): Flow<List<DeepDiveEntity>> {
        return deepDiveDao.getCompletedDeepDives(userId)
    }

    override suspend fun getNextScheduledDeepDive(userId: String): Result<DeepDiveEntity?> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to get next scheduled deep dive") {
            deepDiveDao.getNextScheduledDeepDive(userId)
        }
    }

    override fun getDeepDivesByTheme(userId: String, theme: DeepDiveTheme): Flow<List<DeepDiveEntity>> {
        return deepDiveDao.getDeepDivesByTheme(userId, theme.id)
    }

    override fun getCompletedDeepDivesByTheme(userId: String, theme: DeepDiveTheme): Flow<List<DeepDiveEntity>> {
        return deepDiveDao.getCompletedDeepDivesByTheme(userId, theme.id)
    }

    override suspend fun getOverdueDeepDives(userId: String): Result<List<DeepDiveEntity>> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to get overdue deep dives") {
            deepDiveDao.getOverdueDeepDives(userId)
        }
    }

    override fun searchDeepDives(userId: String, query: String): Flow<List<DeepDiveEntity>> {
        return deepDiveDao.searchDeepDives(userId, query)
    }

    // ==================== SESSION MANAGEMENT ====================

    override suspend fun startSession(deepDiveId: Long): Result<DeepDiveSession> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to start deep dive session") {
            val entity = deepDiveDao.getDeepDiveById(deepDiveId)
                ?: throw IllegalStateException("Deep dive not found")

            val theme = DeepDiveTheme.fromId(entity.theme)
                ?: throw IllegalStateException("Invalid theme")

            // Generate personalized prompts
            val prompts = promptGenerator.generatePersonalizedPrompts(entity.userId, theme)

            // Update session metadata if not already started
            if (entity.sessionStartedAt == null) {
                deepDiveDao.updateSessionMetadata(
                    id = deepDiveId,
                    startedAt = System.currentTimeMillis(),
                    durationMinutes = 0
                )

                // Update step to opening if not started
                if (entity.currentStep == DeepDiveEntity.STEP_NOT_STARTED) {
                    deepDiveDao.updateCurrentStep(deepDiveId, DeepDiveEntity.STEP_OPENING)
                }
            }

            // Reload entity with updates
            val updatedEntity = deepDiveDao.getDeepDiveById(deepDiveId)
                ?: throw IllegalStateException("Failed to reload deep dive")

            val progress = DeepDiveProgress.fromStepName(updatedEntity.currentStep)

            DeepDiveSession(
                entity = updatedEntity,
                theme = theme,
                prompts = prompts,
                progress = progress
            )
        }
    }

    override suspend fun getSession(deepDiveId: Long, userId: String): Result<DeepDiveSession?> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to get session") {
            val entity = deepDiveDao.getDeepDiveById(deepDiveId) ?: return@runSuspendCatching null

            val theme = DeepDiveTheme.fromId(entity.theme) ?: return@runSuspendCatching null

            // Get previous completions for variation
            val completedCount = deepDiveDao.getCompletedCountByTheme(userId, theme.id)

            val prompts = promptGenerator.generatePrompts(theme, entity.promptVariation, completedCount)
            val progress = DeepDiveProgress.fromStepName(entity.currentStep)

            DeepDiveSession(
                entity = entity,
                theme = theme,
                prompts = prompts,
                progress = progress
            )
        }
    }

    override suspend fun saveOpeningReflection(deepDiveId: Long, reflection: String): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to save opening reflection") {
            val entity = deepDiveDao.getDeepDiveById(deepDiveId)
                ?: throw IllegalStateException("Deep dive not found")

            val updated = entity.copy(
                openingReflection = reflection,
                updatedAt = System.currentTimeMillis()
            )
            deepDiveDao.updateDeepDive(updated)
        }
    }

    override suspend fun saveCoreResponse(deepDiveId: Long, response: String): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to save core response") {
            val entity = deepDiveDao.getDeepDiveById(deepDiveId)
                ?: throw IllegalStateException("Deep dive not found")

            val updated = entity.copy(
                coreResponse = response,
                updatedAt = System.currentTimeMillis()
            )
            deepDiveDao.updateDeepDive(updated)
        }
    }

    override suspend fun saveKeyInsight(deepDiveId: Long, insight: String): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to save key insight") {
            val entity = deepDiveDao.getDeepDiveById(deepDiveId)
                ?: throw IllegalStateException("Deep dive not found")

            val updated = entity.copy(
                keyInsight = insight,
                updatedAt = System.currentTimeMillis()
            )
            deepDiveDao.updateDeepDive(updated)
        }
    }

    override suspend fun saveCommitmentStatement(deepDiveId: Long, commitment: String): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to save commitment statement") {
            val entity = deepDiveDao.getDeepDiveById(deepDiveId)
                ?: throw IllegalStateException("Deep dive not found")

            val updated = entity.copy(
                commitmentStatement = commitment,
                updatedAt = System.currentTimeMillis()
            )
            deepDiveDao.updateDeepDive(updated)
        }
    }

    override suspend fun saveMoodRating(deepDiveId: Long, moodBefore: Int?, moodAfter: Int?): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to save mood rating") {
            val entity = deepDiveDao.getDeepDiveById(deepDiveId)
                ?: throw IllegalStateException("Deep dive not found")

            val updated = entity.copy(
                moodBefore = moodBefore ?: entity.moodBefore,
                moodAfter = moodAfter ?: entity.moodAfter,
                updatedAt = System.currentTimeMillis()
            )
            deepDiveDao.updateDeepDive(updated)
        }
    }

    override suspend fun updateCurrentStep(deepDiveId: Long, step: DeepDiveProgress): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to update current step") {
            deepDiveDao.updateCurrentStep(deepDiveId, step.stepName)
        }
    }

    override suspend fun completeSession(deepDiveId: Long, durationMinutes: Int): Result<DeepDiveEntity> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to complete session") {
            val entity = deepDiveDao.getDeepDiveById(deepDiveId)
                ?: throw IllegalStateException("Deep dive not found")

            val now = System.currentTimeMillis()

            val updated = entity.copy(
                isCompleted = true,
                completedAt = now,
                durationMinutes = durationMinutes,
                currentStep = DeepDiveEntity.STEP_COMPLETED,
                updatedAt = now
            )

            deepDiveDao.updateDeepDive(updated)

            // Ensure future deep dives are scheduled
            scheduler.ensureScheduledDeepDives(entity.userId)

            updated
        }
    }

    override suspend fun autoSaveProgress(deepDive: DeepDiveEntity): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to auto-save progress") {
            deepDiveDao.updateDeepDive(deepDive.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    // ==================== SCHEDULING ====================

    override suspend fun scheduleNextDeepDive(
        userId: String,
        preferredDayOfWeek: Int,
        preferredHour: Int,
        preferredMinute: Int
    ): Result<DeepDiveEntity> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to schedule deep dive") {
            scheduler.scheduleNextDeepDive(userId, preferredDayOfWeek, preferredHour, preferredMinute)
        }
    }

    override suspend fun scheduleMultipleDeepDives(
        userId: String,
        count: Int
    ): Result<List<DeepDiveEntity>> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to schedule deep dives") {
            scheduler.scheduleMultipleDeepDives(userId, count)
        }
    }

    override suspend fun rescheduleDeepDive(deepDiveId: Long, newScheduledDate: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to reschedule deep dive") {
            val success = scheduler.rescheduleDeepDive(deepDiveId, newScheduledDate)
            if (!success) {
                throw IllegalStateException("Failed to reschedule deep dive")
            }
        }
    }

    override suspend fun suggestTheme(userId: String): Result<DeepDiveTheme> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to suggest theme") {
            scheduler.suggestThemeBasedOnMood(userId)
        }
    }

    override suspend fun ensureScheduledDeepDives(userId: String, minimum: Int): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to ensure scheduled deep dives") {
            scheduler.ensureScheduledDeepDives(userId, minimum)
        }
    }

    // ==================== ANALYTICS ====================

    override suspend fun getAnalytics(userId: String): Result<DeepDiveAnalytics> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to get analytics") {
            val totalCompleted = deepDiveDao.getTotalCompletedCount(userId)
            val totalScheduled = deepDiveDao.getTotalScheduledCount(userId)
            val avgDuration = deepDiveDao.getAverageDuration(userId) ?: 0.0
            val avgMoodImprovement = deepDiveDao.getAverageMoodImprovement(userId) ?: 0.0

            val themeFrequencyCounts = deepDiveDao.getThemeFrequency(userId)
            val themeFrequency = themeFrequencyCounts.mapNotNull { themeCount ->
                DeepDiveTheme.fromId(themeCount.theme)?.let { it to themeCount.count }
            }.toMap()

            val mostRecentDate = deepDiveDao.getMostRecentCompletionDate(userId)

            val unexploredThemeIds = deepDiveDao.getUnexploredThemes(userId)
            val unexploredThemes = unexploredThemeIds.mapNotNull { DeepDiveTheme.fromId(it) }

            DeepDiveAnalytics(
                totalCompleted = totalCompleted,
                totalScheduled = totalScheduled,
                averageDurationMinutes = avgDuration,
                averageMoodImprovement = avgMoodImprovement,
                themeFrequency = themeFrequency,
                mostRecentCompletionDate = mostRecentDate,
                unexploredThemes = unexploredThemes
            )
        }
    }

    override suspend fun getCompletedSummaries(userId: String, limit: Int): Result<List<DeepDiveSummary>> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to get completed summaries") {
            val completed = deepDiveDao.getRecentCompletedDeepDives(userId, limit)
            completed.mapNotNull { DeepDiveSummary.fromEntity(it) }
        }
    }

    override suspend fun getThemeFrequency(userId: String): Result<Map<DeepDiveTheme, Int>> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to get theme frequency") {
            val themeCounts = deepDiveDao.getThemeFrequency(userId)
            themeCounts.mapNotNull { themeCount ->
                DeepDiveTheme.fromId(themeCount.theme)?.let { it to themeCount.count }
            }.toMap()
        }
    }

    override suspend fun getUnexploredThemes(userId: String): Result<List<DeepDiveTheme>> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to get unexplored themes") {
            val unexploredIds = deepDiveDao.getUnexploredThemes(userId)
            unexploredIds.mapNotNull { DeepDiveTheme.fromId(it) }
        }
    }

    // ==================== DELETION ====================

    override suspend fun deleteDeepDive(deepDiveId: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to delete deep dive") {
            deepDiveDao.deleteDeepDiveById(deepDiveId)
            scheduler.cancelNotification(deepDiveId)
        }
    }

    override suspend fun softDeleteDeepDive(deepDiveId: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to delete deep dive") {
            deepDiveDao.softDeleteDeepDive(deepDiveId)
            scheduler.cancelNotification(deepDiveId)
        }
    }
}
