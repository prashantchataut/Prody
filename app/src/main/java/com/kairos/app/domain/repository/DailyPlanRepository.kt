package com.kairos.app.domain.repository

import com.kairos.app.domain.model.DailyPlan
import com.kairos.app.domain.recommendation.ContentInteractionType
import com.kairos.app.domain.recommendation.DailyContentType
import com.kairos.app.domain.recommendation.InteractionSignal
import java.time.LocalDate

interface DailyPlanRepository {
    suspend fun getOrCreateDailyPlan(
        userId: String,
        localDate: LocalDate = LocalDate.now(),
        now: Long = System.currentTimeMillis()
    ): DailyPlan

    suspend fun recordImpression(
        userId: String,
        localDate: LocalDate,
        type: DailyContentType,
        contentId: Long,
        now: Long = System.currentTimeMillis()
    )

    suspend fun recordInteraction(
        userId: String,
        localDate: LocalDate,
        type: DailyContentType,
        contentId: Long,
        interaction: ContentInteractionType,
        category: String? = null,
        sourceKey: String? = null,
        difficulty: Int? = null,
        now: Long = System.currentTimeMillis()
    )

    /**
     * Read the user's recorded interaction signals since [sinceMillis], mapped to
     * the pure domain signal type used by the personalization profile. Used by the
     * Learn-tab practice queue so sessions reflect real taste, not just SRS state.
     */
    suspend fun recentInteractionSignals(
        userId: String,
        sinceMillis: Long
    ): List<InteractionSignal>
}
