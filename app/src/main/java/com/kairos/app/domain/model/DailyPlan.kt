package com.kairos.app.domain.model

import com.kairos.app.domain.recommendation.DailyContentType
import java.time.LocalDate

data class DailyWord(
    val id: Long,
    val word: String,
    val definition: String,
    val pronunciation: String,
    val partOfSpeech: String,
    val exampleSentence: String,
    val difficulty: Int,
    val category: String
)

data class DailyQuote(
    val id: Long,
    val content: String,
    val author: String,
    val source: String,
    val category: String,
    val reflectionPrompt: String
)

data class RecommendedDailyItem<T>(
    val item: T,
    val type: DailyContentType,
    val score: Double,
    val reason: String,
    val impressedAt: Long? = null,
    val completedAt: Long? = null
)

data class DailyPlan(
    val localDate: LocalDate,
    val word: RecommendedDailyItem<DailyWord>?,
    val quote: RecommendedDailyItem<DailyQuote>?,
    val algorithmVersion: Int
)
