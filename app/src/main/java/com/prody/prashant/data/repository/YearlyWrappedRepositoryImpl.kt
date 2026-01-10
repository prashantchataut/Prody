package com.prody.prashant.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.YearlyWrappedDao
import com.prody.prashant.data.local.entity.YearlyWrappedEntity
import com.prody.prashant.domain.common.ErrorType
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.common.runSuspendCatching
import com.prody.prashant.domain.repository.YearlyWrappedRepository
import com.prody.prashant.domain.wrapped.*
import kotlinx.coroutines.flow.Flow
import java.time.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of YearlyWrappedRepository.
 *
 * Handles all yearly wrapped operations including generation, retrieval,
 * and conversion between entity and domain models.
 */
@Singleton
class YearlyWrappedRepositoryImpl @Inject constructor(
    private val yearlyWrappedDao: YearlyWrappedDao,
    private val journalDao: JournalDao,
    private val wrappedGenerator: YearlyWrappedGenerator,
    private val gson: Gson
) : YearlyWrappedRepository {

    // ==================== RETRIEVAL ====================

    override fun getAllWrapped(userId: String): Flow<List<YearlyWrappedEntity>> {
        return yearlyWrappedDao.getAllWrapped(userId)
    }

    override suspend fun getWrappedByYear(userId: String, year: Int): Result<YearlyWrappedEntity?> =
        runSuspendCatching(
            errorType = ErrorType.DATABASE,
            errorMessage = "Failed to retrieve wrapped for year $year"
        ) {
            yearlyWrappedDao.getWrappedByYear(userId, year)
        }

    override fun observeWrappedByYear(userId: String, year: Int): Flow<YearlyWrappedEntity?> {
        return yearlyWrappedDao.observeWrappedByYear(userId, year)
    }

    override suspend fun getLatestWrapped(userId: String): Result<YearlyWrappedEntity?> =
        runSuspendCatching(
            errorType = ErrorType.DATABASE,
            errorMessage = "Failed to retrieve latest wrapped"
        ) {
            yearlyWrappedDao.getLatestWrapped(userId)
        }

    override fun observeLatestWrapped(userId: String): Flow<YearlyWrappedEntity?> {
        return yearlyWrappedDao.observeLatestWrapped(userId)
    }

    override suspend fun getWrappedById(id: Long): Result<YearlyWrappedEntity> =
        runSuspendCatching(
            errorType = ErrorType.DATABASE,
            errorMessage = "Failed to retrieve wrapped"
        ) {
            yearlyWrappedDao.getWrappedById(id)
                ?: throw NoSuchElementException("Wrapped with ID $id not found")
        }

    override fun observeWrappedById(id: Long): Flow<YearlyWrappedEntity?> {
        return yearlyWrappedDao.observeWrappedById(id)
    }

    override fun getUnviewedWrapped(userId: String): Flow<List<YearlyWrappedEntity>> {
        return yearlyWrappedDao.getUnviewedWrapped(userId)
    }

    override fun getFavoriteWrapped(userId: String): Flow<List<YearlyWrappedEntity>> {
        return yearlyWrappedDao.getFavoriteWrapped(userId)
    }

    override suspend fun getAvailableYears(userId: String): List<Int> {
        return yearlyWrappedDao.getAvailableYears(userId)
    }

    override fun observeAvailableYears(userId: String): Flow<List<Int>> {
        return yearlyWrappedDao.observeAvailableYears(userId)
    }

    // ==================== GENERATION ====================

    override suspend fun generateWrapped(
        userId: String,
        year: Int,
        config: WrappedGenerationConfig
    ): Result<YearlyWrappedEntity> = runSuspendCatching(
        errorType = ErrorType.DATABASE,
        errorMessage = "Failed to generate wrapped for year $year"
    ) {
        // Check if already exists
        val existing = yearlyWrappedDao.getWrappedByYear(userId, year)
        if (existing != null) {
            throw IllegalStateException("Wrapped already exists for year $year")
        }

        // Generate wrapped
        val generationResult = wrappedGenerator.generate(userId, year, config)
        if (generationResult.isFailure) {
            throw generationResult.exceptionOrNull()
                ?: Exception("Failed to generate wrapped")
        }

        val wrappedEntity = generationResult.getOrThrow()

        // Save to database
        val id = yearlyWrappedDao.insertWrapped(wrappedEntity)

        // Return the saved entity
        yearlyWrappedDao.getWrappedById(id)
            ?: throw Exception("Failed to retrieve saved wrapped")
    }

    override suspend fun hasWrappedForYear(userId: String, year: Int): Boolean {
        return yearlyWrappedDao.wrappedExistsForYear(userId, year)
    }

    override suspend fun canGenerateWrappedForYear(
        userId: String,
        year: Int
    ): Result<Boolean> = runSuspendCatching(
        errorType = ErrorType.DATABASE,
        errorMessage = "Failed to check if wrapped can be generated"
    ) {
        // Check if wrapped already exists
        if (hasWrappedForYear(userId, year)) {
            return@runSuspendCatching false
        }

        // Check if year is in the past or current year is ending
        val currentYear = LocalDate.now().year
        if (year > currentYear) {
            return@runSuspendCatching false
        }

        // Check if there are enough entries for that year
        val yearStart = LocalDate.of(year, 1, 1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val yearEnd = LocalDate.of(year, 12, 31)
            .atTime(23, 59, 59)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val entries = journalDao.getAllEntriesSync().filter {
            it.createdAt in yearStart..yearEnd && !it.isDeleted
        }

        entries.size >= 5 // Minimum 5 entries required
    }

    // ==================== UPDATE ====================

    override suspend fun markAsViewed(
        id: Long,
        completionPercent: Int,
        slidesViewed: String
    ): Result<Unit> = runSuspendCatching(
        errorType = ErrorType.DATABASE,
        errorMessage = "Failed to mark wrapped as viewed"
    ) {
        yearlyWrappedDao.markAsViewed(
            id = id,
            completionPercent = completionPercent,
            slidesViewed = slidesViewed
        )
    }

    override suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean): Result<Unit> =
        runSuspendCatching(
            errorType = ErrorType.DATABASE,
            errorMessage = "Failed to update favorite status"
        ) {
            yearlyWrappedDao.updateFavoriteStatus(id, isFavorite)
        }

    override suspend fun markAsShared(id: Long): Result<Unit> =
        runSuspendCatching(
            errorType = ErrorType.DATABASE,
            errorMessage = "Failed to mark wrapped as shared"
        ) {
            yearlyWrappedDao.markAsShared(id)
        }

    override suspend fun updateViewProgress(
        id: Long,
        percent: Int,
        slidesViewed: String
    ): Result<Unit> = runSuspendCatching(
        errorType = ErrorType.DATABASE,
        errorMessage = "Failed to update view progress"
    ) {
        yearlyWrappedDao.updateViewProgress(id, percent, slidesViewed)
    }

    override suspend fun saveWrapped(wrapped: YearlyWrappedEntity): Result<Long> =
        runSuspendCatching(
            errorType = ErrorType.DATABASE,
            errorMessage = "Failed to save wrapped"
        ) {
            yearlyWrappedDao.insertWrapped(wrapped)
        }

    // ==================== DELETE ====================

    override suspend fun deleteWrapped(id: Long): Result<Unit> =
        runSuspendCatching(
            errorType = ErrorType.DATABASE,
            errorMessage = "Failed to delete wrapped"
        ) {
            yearlyWrappedDao.deleteWrappedById(id)
        }

    override suspend fun softDeleteWrapped(id: Long): Result<Unit> =
        runSuspendCatching(
            errorType = ErrorType.DATABASE,
            errorMessage = "Failed to soft delete wrapped"
        ) {
            yearlyWrappedDao.softDeleteWrapped(id)
        }

    // ==================== STATISTICS ====================

    override fun getWrappedCount(userId: String): Flow<Int> {
        return yearlyWrappedDao.getWrappedCount(userId)
    }

    override suspend fun getUnviewedCount(userId: String): Int {
        return yearlyWrappedDao.getUnviewedCount(userId)
    }

    // ==================== UTILITY ====================

    override fun entityToDomain(entity: YearlyWrappedEntity): YearlyWrapped {
        // Parse JSON fields
        val themes: List<ThemeHighlight> = try {
            val type = object : TypeToken<List<ThemeHighlight>>() {}.type
            gson.fromJson(entity.topThemesJson, type)
        } catch (e: Exception) {
            emptyList()
        }

        val growthAreas: List<GrowthArea> = try {
            val type = object : TypeToken<List<GrowthArea>>() {}.type
            gson.fromJson(entity.growthAreasJson, type)
        } catch (e: Exception) {
            emptyList()
        }

        val challenges: List<ChallengeOvercome> = try {
            val type = object : TypeToken<List<ChallengeOvercome>>() {}.type
            gson.fromJson(entity.challengesOvercomeJson, type)
        } catch (e: Exception) {
            emptyList()
        }

        val keyMoments: List<KeyMoment> = try {
            val type = object : TypeToken<List<KeyMoment>>() {}.type
            gson.fromJson(entity.keyMomentsJson, type)
        } catch (e: Exception) {
            emptyList()
        }

        val patterns: List<Pattern> = try {
            val type = object : TypeToken<List<Pattern>>() {}.type
            gson.fromJson(entity.patternsJson, type)
        } catch (e: Exception) {
            emptyList()
        }

        val shareableCards: List<ShareableCard> = try {
            val type = object : TypeToken<List<ShareableCard>>() {}.type
            gson.fromJson(entity.shareableCardsJson, type)
        } catch (e: Exception) {
            emptyList()
        }

        val monthlyMoodAverages: List<Float> = try {
            val type = object : TypeToken<List<Float>>() {}.type
            gson.fromJson(entity.moodEvolution, type)
        } catch (e: Exception) {
            List(12) { 0f }
        }

        return YearlyWrapped(
            id = entity.id,
            year = entity.year,
            generatedAt = entity.generatedAt,
            stats = YearStats(
                totalEntries = entity.totalJournalEntries,
                totalMicroEntries = entity.totalMicroEntries,
                wordsWritten = entity.totalWordsWritten,
                averageWordsPerEntry = entity.averageWordsPerEntry,
                longestEntry = entity.longestEntry,
                longestEntryId = entity.longestEntryId,
                activeDays = entity.activeDaysCount,
                longestStreak = entity.longestStreak,
                meditationMinutes = entity.totalMeditationMinutes,
                bloomsCompleted = entity.bloomsCompleted,
                wordsLearned = entity.vocabularyWordsLearned,
                wordsUsed = entity.vocabularyWordsUsed,
                idiomsExplored = entity.idiomsExplored,
                proverbsDiscovered = entity.proverbsDiscovered,
                messagesWritten = entity.futureMessagesWritten,
                messagesReceived = entity.futureMessagesReceived,
                mostDistantMessage = entity.mostDistantMessage,
                mostActiveMonth = entity.mostActiveMonth?.let { Month.of(it) },
                mostActiveDay = entity.mostActiveDay?.let { DayOfWeek.valueOf(it.uppercase()) },
                mostActiveTime = entity.mostActiveTimeOfDay?.let {
                    TimeOfDay.valueOf(it.uppercase())
                },
                firstEntryDate = entity.firstEntryDate,
                lastEntryDate = entity.lastEntryDate
            ),
            moodJourney = MoodJourney(
                averageMood = entity.averageMood,
                trend = MoodTrend.valueOf(entity.moodTrend.uppercase()),
                mostCommonMood = entity.mostCommonMood,
                moodVariety = entity.moodVariety,
                brightestMonth = entity.brightestMonth?.let { Month.of(it) },
                mostReflectiveMonth = entity.mostReflectiveMonth?.let { Month.of(it) },
                monthlyAverages = monthlyMoodAverages
            ),
            themes = themes,
            growthAreas = growthAreas,
            challenges = challenges,
            keyMoments = keyMoments,
            patterns = patterns,
            narratives = YearNarratives(
                opening = entity.openingNarrative,
                yearSummary = entity.yearSummaryNarrative,
                growthStory = entity.growthStoryNarrative,
                moodJourney = entity.moodJourneyNarrative,
                lookingAhead = entity.lookingAheadNarrative,
                milestone = entity.milestoneNarrative
            ),
            shareableCards = shareableCards,
            isViewed = entity.isViewed,
            isFavorite = entity.isFavorite,
            viewedAt = entity.viewedAt
        )
    }
}
