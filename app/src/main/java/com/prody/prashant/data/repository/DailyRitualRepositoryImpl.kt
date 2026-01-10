package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.DailyRitualDao
import com.prody.prashant.data.local.entity.DailyRitualEntity
import com.prody.prashant.domain.common.ErrorType
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.common.runSuspendCatching
import com.prody.prashant.domain.repository.DailyRitualRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of DailyRitualRepository using Room database.
 */
@Singleton
class DailyRitualRepositoryImpl @Inject constructor(
    private val dailyRitualDao: DailyRitualDao
) : DailyRitualRepository {

    // ==================== RETRIEVAL ====================

    override suspend fun getTodayRitual(userId: String): Result<DailyRitualEntity?> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to get today's ritual") {
            val todayStart = getTodayStartTimestamp()
            dailyRitualDao.getRitualForDate(userId, todayStart)
        }
    }

    override fun observeTodayRitual(userId: String): Flow<DailyRitualEntity?> {
        val todayStart = getTodayStartTimestamp()
        return dailyRitualDao.observeRitualForDate(userId, todayStart)
    }

    override suspend fun getRitualForDate(userId: String, date: Long): Result<DailyRitualEntity?> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to get ritual for date") {
            dailyRitualDao.getRitualForDate(userId, date)
        }
    }

    override fun getAllRituals(userId: String): Flow<List<DailyRitualEntity>> {
        return dailyRitualDao.getAllRituals(userId)
    }

    override fun getRitualsForDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<DailyRitualEntity>> {
        return dailyRitualDao.getRitualsForDateRange(userId, startDate, endDate)
    }

    override fun getRecentRituals(userId: String, limit: Int): Flow<List<DailyRitualEntity>> {
        return dailyRitualDao.getRecentRituals(userId, limit)
    }

    // ==================== MORNING RITUAL ====================

    override suspend fun getOrCreateTodayRitual(userId: String): Result<DailyRitualEntity> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to get or create today's ritual") {
            val todayStart = getTodayStartTimestamp()
            val existing = dailyRitualDao.getRitualForDate(userId, todayStart)
            if (existing != null) {
                existing
            } else {
                val newRitual = DailyRitualEntity(
                    userId = userId,
                    date = todayStart,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                val id = dailyRitualDao.insertRitual(newRitual)
                newRitual.copy(id = id)
            }
        }
    }

    override suspend fun completeMorningRitual(
        userId: String,
        intention: String?,
        mood: String?,
        wisdomId: Long?
    ): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to complete morning ritual") {
            val todayStart = getTodayStartTimestamp()
            val existing = dailyRitualDao.getRitualForDate(userId, todayStart)

            if (existing != null) {
                dailyRitualDao.completeMorningRitual(
                    id = existing.id,
                    completedAt = System.currentTimeMillis(),
                    intention = intention,
                    mood = mood,
                    wisdomId = wisdomId
                )
            } else {
                val newRitual = DailyRitualEntity(
                    userId = userId,
                    date = todayStart,
                    morningCompleted = true,
                    morningCompletedAt = System.currentTimeMillis(),
                    morningIntention = intention,
                    morningMood = mood,
                    morningWisdomId = wisdomId,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                dailyRitualDao.insertRitual(newRitual)
            }
        }
    }

    override suspend fun updateMorningIntention(userId: String, intention: String): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to update morning intention") {
            val todayStart = getTodayStartTimestamp()
            val existing = dailyRitualDao.getRitualForDate(userId, todayStart)
            if (existing != null) {
                dailyRitualDao.updateRitual(existing.copy(
                    morningIntention = intention,
                    updatedAt = System.currentTimeMillis()
                ))
            }
        }
    }

    override suspend fun isMorningRitualCompletedToday(userId: String): Boolean {
        val todayStart = getTodayStartTimestamp()
        return dailyRitualDao.isMorningRitualCompleted(userId, todayStart)
    }

    // ==================== EVENING RITUAL ====================

    override suspend fun completeEveningRitual(
        userId: String,
        dayRating: String,
        reflection: String?,
        mood: String?
    ): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to complete evening ritual") {
            val todayStart = getTodayStartTimestamp()
            val existing = dailyRitualDao.getRitualForDate(userId, todayStart)

            if (existing != null) {
                dailyRitualDao.completeEveningRitual(
                    id = existing.id,
                    completedAt = System.currentTimeMillis(),
                    dayRating = dayRating,
                    reflection = reflection,
                    mood = mood
                )
            } else {
                val newRitual = DailyRitualEntity(
                    userId = userId,
                    date = todayStart,
                    eveningCompleted = true,
                    eveningCompletedAt = System.currentTimeMillis(),
                    eveningDayRating = dayRating,
                    eveningReflection = reflection,
                    eveningMood = mood,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                dailyRitualDao.insertRitual(newRitual)
            }
        }
    }

    override suspend fun updateEveningReflection(userId: String, reflection: String): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to update evening reflection") {
            val todayStart = getTodayStartTimestamp()
            val existing = dailyRitualDao.getRitualForDate(userId, todayStart)
            if (existing != null) {
                dailyRitualDao.updateRitual(existing.copy(
                    eveningReflection = reflection,
                    updatedAt = System.currentTimeMillis()
                ))
            }
        }
    }

    override suspend fun isEveningRitualCompletedToday(userId: String): Boolean {
        val todayStart = getTodayStartTimestamp()
        return dailyRitualDao.isEveningRitualCompleted(userId, todayStart)
    }

    // ==================== EXPANSION ====================

    override suspend fun markExpandedToJournal(userId: String, journalId: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to mark expanded to journal") {
            val todayStart = getTodayStartTimestamp()
            val existing = dailyRitualDao.getRitualForDate(userId, todayStart)
            if (existing != null) {
                dailyRitualDao.setExpandedToJournal(existing.id, journalId)
            }
        }
    }

    override suspend fun markExpandedToMicroEntry(userId: String, microEntryId: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to mark expanded to micro entry") {
            val todayStart = getTodayStartTimestamp()
            val existing = dailyRitualDao.getRitualForDate(userId, todayStart)
            if (existing != null) {
                dailyRitualDao.setExpandedToMicroEntry(existing.id, microEntryId)
            }
        }
    }

    // ==================== STATISTICS ====================

    override suspend fun getCompletedRitualsCount(userId: String): Int {
        return dailyRitualDao.getCompletedRitualsCount(userId)
    }

    override suspend fun getMorningRitualCount(userId: String): Int {
        return dailyRitualDao.getMorningCompletedCount(userId)
    }

    override suspend fun getEveningRitualCount(userId: String): Int {
        return dailyRitualDao.getEveningCompletedCount(userId)
    }

    override suspend fun getFullRitualDaysCount(userId: String): Int {
        return dailyRitualDao.getFullRitualDaysCount(userId)
    }

    override suspend fun getCurrentRitualStreak(userId: String): Int {
        return dailyRitualDao.getCurrentStreak(userId)
    }

    override suspend fun getDayRatingDistribution(userId: String): Map<String, Int> {
        val ratingCounts = dailyRitualDao.getDayRatingDistribution(userId)
        return ratingCounts.associate { (it.rating ?: "unrated") to it.count }
    }

    override suspend fun getThisWeekRitualDays(userId: String): Int {
        val startOfWeek = LocalDate.now()
            .minusDays(LocalDate.now().dayOfWeek.value.toLong() - 1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return dailyRitualDao.getWeekRitualDays(userId, startOfWeek)
    }

    // ==================== CLEANUP ====================

    override suspend fun purgeSoftDeleted(): Int {
        return dailyRitualDao.purgeSoftDeleted()
    }

    // ==================== HELPERS ====================

    private fun getTodayStartTimestamp(): Long {
        return LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}
