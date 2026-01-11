package com.prody.prashant.domain.vocabulary

import com.prody.prashant.data.local.dao.VocabularyLearningDao
import com.prody.prashant.data.local.dao.WordUsageDao
import com.prody.prashant.data.local.entity.VocabularyEntity
import com.prody.prashant.data.local.entity.WordUsageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for celebrating vocabulary word usage in journal entries.
 *
 * Features:
 * - Detects when learned words are used in context
 * - Creates subtle, encouraging celebrations
 * - Awards bonus discipline points for vocabulary application
 * - Tracks word usage statistics
 */
@Singleton
class VocabularyCelebrationService @Inject constructor(
    private val wordUsageDao: WordUsageDao,
    private val vocabularyLearningDao: VocabularyLearningDao
) {

    companion object {
        // Points awarded for using a vocabulary word in context
        const val BONUS_POINTS_PER_USAGE = 3

        // Maximum bonus points per word per day to prevent farming
        const val MAX_POINTS_PER_WORD_PER_DAY = 6

        // Celebration messages
        private val CELEBRATION_TEMPLATES = listOf(
            "You used '%s' in context! +%d Discipline",
            "Great use of '%s'! +%d Discipline",
            "Nice! You applied '%s' +%d Discipline",
            "'%s' in action! +%d Discipline",
            "Excellent! '%s' used naturally +%d Discipline"
        )
    }

    /**
     * Process word usages for a journal entry and create celebrations.
     *
     * @param journalEntryId The ID of the journal entry
     * @param wordUsages List of detected word usages
     * @param userId The user ID
     * @return List of celebration messages to display
     */
    suspend fun processWordUsages(
        journalEntryId: Long,
        wordUsages: List<WordUsage>,
        userId: String = "local"
    ): List<VocabularyCelebration> {
        if (wordUsages.isEmpty()) {
            return emptyList()
        }

        val celebrations = mutableListOf<VocabularyCelebration>()
        val todayStart = getTodayStartTimestamp()

        for (usage in wordUsages) {
            // Check if this word was already used today (to prevent point farming)
            val usagesTodayCount = wordUsageDao.getUsagesByWordSync(usage.word.id, userId)
                .count { it.detectedAt >= todayStart }

            // Calculate bonus points (capped per word per day)
            val pointsForThisUsage = if (usagesTodayCount == 0) {
                BONUS_POINTS_PER_USAGE
            } else if (usagesTodayCount * BONUS_POINTS_PER_USAGE < MAX_POINTS_PER_WORD_PER_DAY) {
                BONUS_POINTS_PER_USAGE
            } else {
                0 // No more points for this word today
            }

            // Create word usage entity
            val wordUsageEntity = WordUsageEntity(
                userId = userId,
                wordId = usage.word.id,
                journalEntryId = journalEntryId,
                usedInSentence = usage.usedIn,
                matchedForm = usage.matchedForm,
                positionStart = usage.position.first,
                positionEnd = usage.position.last,
                detectedAt = usage.detectedAt.toEpochMilli(),
                celebrated = false,
                bonusPointsAwarded = pointsForThisUsage,
                pointsClaimed = false
            )

            // Save to database
            val usageId = wordUsageDao.insertWordUsage(wordUsageEntity)

            // Update vocabulary learning entity
            val currentLearning = vocabularyLearningDao.getLearningProgress(usage.word.id, userId)
            if (currentLearning != null) {
                val updated = currentLearning.copy(
                    usedInContext = true,
                    lastUsedAt = usage.detectedAt.toEpochMilli(),
                    timesUsed = currentLearning.timesUsed + 1
                )
                vocabularyLearningDao.updateLearningProgress(updated)
            }

            // Create celebration message
            if (pointsForThisUsage > 0) {
                val message = CELEBRATION_TEMPLATES.random().format(
                    usage.word.word,
                    pointsForThisUsage
                )

                celebrations.add(
                    VocabularyCelebration(
                        usageId = usageId,
                        word = usage.word,
                        message = message,
                        bonusPoints = pointsForThisUsage,
                        usageContext = usage.usedIn
                    )
                )
            }
        }

        return celebrations
    }

    /**
     * Get uncelebrated word usages for display.
     */
    suspend fun getUncelebratedUsages(userId: String = "local", limit: Int = 10): List<WordUsageEntity> {
        return wordUsageDao.getUncelebratedUsages(userId, limit)
    }

    /**
     * Mark a celebration as shown.
     */
    suspend fun markCelebrationShown(usageId: Long) {
        wordUsageDao.markAsCelebrated(usageId)
    }

    /**
     * Mark multiple celebrations as shown.
     */
    suspend fun markCelebrationsShown(usageIds: List<Long>) {
        wordUsageDao.markMultipleAsCelebrated(usageIds)
    }

    /**
     * Claim bonus points for a word usage.
     * This should be called after the celebration is shown and points are awarded.
     */
    suspend fun claimBonusPoints(usageId: Long, points: Int) {
        wordUsageDao.markPointsClaimed(usageId, points)
    }

    /**
     * Get celebration summary for display in session results.
     *
     * @return Summary of vocabulary achievements in this session
     */
    fun getCelebrationSummary(celebrations: List<VocabularyCelebration>): VocabularyCelebrationSummary {
        val totalWords = celebrations.distinctBy { it.word.id }.size
        val totalPoints = celebrations.sumOf { it.bonusPoints }

        return VocabularyCelebrationSummary(
            wordsUsed = totalWords,
            totalBonusPoints = totalPoints,
            celebrations = celebrations
        )
    }

    /**
     * Get words that have been learned but never used in context.
     */
    suspend fun getLearnedButUnusedWords(userId: String = "local", limit: Int = 20): List<Long> {
        return wordUsageDao.getLearnedButUnusedWordIds(userId, limit)
    }

    /**
     * Get statistics about word usage.
     */
    suspend fun getUsageStats(wordId: Long, userId: String = "local"): WordUsageStats? {
        return wordUsageDao.getWordUsageStats(wordId, userId)
    }

    /**
     * Check if a word has been used recently (for suggestions).
     */
    suspend fun hasBeenUsedRecently(
        wordId: Long,
        userId: String = "local",
        withinDays: Int = 7
    ): Boolean {
        val cutoffTime = System.currentTimeMillis() - (withinDays * 24 * 60 * 60 * 1000L)
        val usages = wordUsageDao.getUsagesByWordSync(wordId, userId)
        return usages.any { it.detectedAt >= cutoffTime }
    }

    private fun getTodayStartTimestamp(): Long {
        val now = System.currentTimeMillis()
        val millisInDay = 24 * 60 * 60 * 1000L
        return (now / millisInDay) * millisInDay
    }
}

/**
 * Represents a celebration for using a vocabulary word in context.
 */
data class VocabularyCelebration(
    val usageId: Long,
    val word: VocabularyEntity,
    val message: String,
    val bonusPoints: Int,
    val usageContext: String
)

/**
 * Summary of vocabulary celebrations in a session.
 */
data class VocabularyCelebrationSummary(
    val wordsUsed: Int,
    val totalBonusPoints: Int,
    val celebrations: List<VocabularyCelebration>
) {
    val hasAchievements: Boolean
        get() = wordsUsed > 0
}

/**
 * Reuses the WordUsageStats from WordUsageDao
 */
typealias WordUsageStats = com.prody.prashant.data.local.dao.WordUsageStats
