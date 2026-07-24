package com.kairos.app.domain.repository

import com.kairos.app.domain.common.Result
import kotlinx.coroutines.flow.Flow

data class TodayProgressSnapshot(
    val displayName: String? = null,
    val currentStreak: Int = 0,
    val journalEntriesThisWeek: Int = 0,
    val wordsLearnedThisWeek: Int = 0
)

data class TodayWordCompletion(
    val newlyIntroduced: Boolean
)

/**
 * Focused boundary for the small amount of progress data shown on Today.
 * DAO coordination and legacy compatibility writes stay in the data layer.
 */
interface TodayProgressRepository {
    fun observeProgress(
        userId: String,
        weekStartMillis: Long,
        nowMillis: Long
    ): Flow<TodayProgressSnapshot>

    suspend fun completeWord(
        userId: String,
        wordId: Long,
        completedAtMillis: Long
    ): Result<TodayWordCompletion>
}
