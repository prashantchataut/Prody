package com.kairos.app.domain.recommendation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExplainableRecommendationRankerTest {
    private val ranker = ExplainableRecommendationRanker()
    private val now = 1_800_000_000_000L

    @Test
    fun `overdue introduced word outranks equivalent new word`() {
        val newWord = candidate(id = 1, introduced = false, dueAt = null)
        val overdueWord = candidate(id = 2, introduced = true, dueAt = now - 10 * DAY_MS)

        val ranked = ranker.rank(listOf(newWord, overdueWord), context())

        assertEquals(2L, ranked.first().candidate.id)
        assertTrue(ranked.first().reason.contains("review", ignoreCase = true))
    }

    @Test
    fun `recently selected item receives a meaningful repetition penalty`() {
        val recent = candidate(id = 1)
        val fresh = candidate(id = 2)
        val context = context().copy(
            recentSelections = listOf(
                CandidateHistory(
                    selectedAt = now - DAY_MS,
                    contentId = 1,
                    type = DailyContentType.VOCABULARY,
                    category = "growth",
                    sourceKey = "noun"
                )
            )
        )

        val ranked = ranker.rank(listOf(recent, fresh), context)

        assertEquals(2L, ranked.first().candidate.id)
        assertTrue(ranked.last().breakdown.repetitionPenalty >= 0.8)
    }

    @Test
    fun `positive feedback changes quote ordering`() {
        val growth = RecommendationCandidate(1, DailyContentType.QUOTE, "growth", "Author A", quality = 0.8)
        val calm = RecommendationCandidate(2, DailyContentType.QUOTE, "calm", "Author B", quality = 0.8)
        val context = context().copy(categoryFeedback = mapOf("calm" to 5.0))

        val ranked = ranker.rank(listOf(growth, calm), context)

        assertEquals(2L, ranked.first().candidate.id)
    }

    @Test
    fun `same user date and candidates always produce same order`() {
        val candidates = (1L..20L).map { candidate(it) }

        val first = ranker.rank(candidates, context()).map { it.candidate.id }
        val second = ranker.rank(candidates.reversed(), context()).map { it.candidate.id }

        assertEquals(first, second)
    }

    private fun candidate(
        id: Long,
        introduced: Boolean = false,
        dueAt: Long? = null
    ) = RecommendationCandidate(
        id = id,
        type = DailyContentType.VOCABULARY,
        category = "growth",
        sourceKey = "noun",
        difficulty = 3,
        quality = 0.8,
        reviewDueAt = dueAt,
        isIntroduced = introduced
    )

    private fun context() = RecommendationContext(
        userId = "test-user",
        epochDay = 21_000,
        now = now,
        targetDifficulty = 3
    )

    private companion object {
        const val DAY_MS = 24L * 60L * 60L * 1_000L
    }
}
