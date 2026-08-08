package com.kairos.app.domain.recommendation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonalizedStudyQueueTest {

    private val now = 1_800_000_000_000L
    private val day = 24L * 60 * 60 * 1000

    @Test
    fun `empty candidates produce empty queue`() {
        val queue = PersonalizedStudyQueue.build(emptyList(), profile(), now)
        assertTrue(queue.isEmpty())
    }

    @Test
    fun `due reviews are ranked before new words`() {
        val due = candidate(id = 1, due = true, reviewDueAt = now - day)
        val fresh = candidate(id = 2, isNew = true)
        val queue = PersonalizedStudyQueue.build(listOf(fresh, due), profile(), now, maxItems = 5)

        assertEquals(listOf(1L, 2L), queue.map { it.contentId })
        assertTrue(queue.first().reason.contains("Due", ignoreCase = true))
    }

    @Test
    fun `new word ratio caps how many new words enter the queue`() {
        val due = (1L..6L).map { candidate(id = it, due = true, reviewDueAt = now - day) }
        val fresh = (100L..110L).map { candidate(id = it, isNew = true) }

        val queue = PersonalizedStudyQueue.build(due + fresh, profile(), now, maxItems = 10, newWordRatio = 0.4f)
        val newCount = queue.count { it.contentId >= 100 }

        assertEquals(10, queue.size)
        assertTrue("expected at most 4 new words, got $newCount", newCount <= 4)
    }

    @Test
    fun `profile category affinity lifts matching candidates`() {
        val mindfulness = candidate(id = 1, category = "mindfulness")
        val business = candidate(id = 2, category = "business")
        val p = profile(categoryAffinity = mapOf("mindfulness" to 2.0))

        val queue = PersonalizedStudyQueue.build(listOf(business, mindfulness), p, now, maxItems = 2)
        assertEquals(1L, queue.first().contentId)
    }

    @Test
    fun `recently seen items are pushed down by novelty`() {
        val seen = candidate(id = 1, lastSeenAt = now - day)
        val unseen = candidate(id = 2, lastSeenAt = null)
        val queue = PersonalizedStudyQueue.build(listOf(seen, unseen), profile(), now, maxItems = 2)
        assertEquals(2L, queue.first().contentId)
    }

    @Test
    fun `every assignment carries a human readable reason`() {
        val due = candidate(id = 1, due = true, reviewDueAt = now - 2 * day)
        val fresh = candidate(id = 2, isNew = true)
        val queue = PersonalizedStudyQueue.build(listOf(fresh, due), profile(), now, maxItems = 2)
        queue.forEach { assignment ->
            assertTrue(assignment.reason.isNotBlank())
            assertTrue(assignment.reason.first().isUpperCase())
        }
    }

    @Test
    fun `queue never exceeds max items`() {
        val items = (1L..50L).map { candidate(id = it, due = true, reviewDueAt = now - day) }
        val queue = PersonalizedStudyQueue.build(items, profile(), now, maxItems = 7)
        assertEquals(7, queue.size)
    }

    private fun candidate(
        id: Long,
        category: String = "growth",
        due: Boolean = false,
        isNew: Boolean = false,
        reviewDueAt: Long? = null,
        lastSeenAt: Long? = null
    ) = StudyCandidate(
        contentId = id,
        category = category,
        difficulty = 2,
        isNew = isNew,
        isDueForReview = due,
        lastSeenAt = lastSeenAt,
        reviewDueAt = reviewDueAt
    )

    private fun profile(
        categoryAffinity: Map<String, Double> = emptyMap()
    ) = PersonalizationProfile(
        categoryAffinity = categoryAffinity,
        sourceAffinity = emptyMap(),
        difficultyDelta = 0,
        confidence = 0.0
    )
}
