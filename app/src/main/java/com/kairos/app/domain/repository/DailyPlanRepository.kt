package com.kairos.app.domain.repository

import com.kairos.app.domain.model.DailyPlan
import com.kairos.app.domain.recommendation.ContentInteractionType
import com.kairos.app.domain.recommendation.DailyContentType
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
}
