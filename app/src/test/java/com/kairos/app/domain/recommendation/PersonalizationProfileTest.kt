package com.kairos.app.domain.recommendation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonalizationProfileTest {

    private val now = 1_800_000_000_000L
    private val day = 24L * 60 * 60 * 1000

    @Test
    fun `empty history yields zero confidence and no shifts`() {
        val profile = PersonalizationProfile.compute(emptyMap(), emptyList(), now)
        assertEquals(0.0, profile.confidence, 0.0001)
        assertEquals(0, profile.difficultyDelta)
        assertTrue(profile.categoryAffinity.isEmpty())
    }

    @Test
    fun `more like this strengthens category affinity`() {
        val signals = listOf(
            InteractionSignal(ContentInteractionType.MORE_LIKE_THIS, category = "Mindfulness", createdAt = now)
        )
        val profile = PersonalizationProfile.compute(emptyMap(), signals, now)
        val affinity = profile.categoryAffinity["mindfulness"] ?: 0.0
        assertTrue("expected positive affinity, was $affinity", affinity > 0.3)
    }

    @Test
    fun `less like this weakens category affinity`() {
        val signals = listOf(
            InteractionSignal(ContentInteractionType.LESS_LIKE_THIS, category = "stoic", createdAt = now)
        )
        val profile = PersonalizationProfile.compute(emptyMap(), signals, now)
        val affinity = profile.categoryAffinity["stoic"] ?: 0.0
        assertTrue("expected negative affinity, was $affinity", affinity < -0.3)
    }

    @Test
    fun `preferred categories seed the baseline`() {
        val profile = PersonalizationProfile.compute(
            preferredCategories = mapOf("growth" to 1.0, "reflection" to 0.5),
            interactions = emptyList(),
            now = now
        )
        assertEquals(1.0, profile.categoryAffinity["growth"] ?: 0.0, 0.0001)
        assertEquals(0.5, profile.categoryAffinity["reflection"] ?: 0.0, 0.0001)
    }

    @Test
    fun `old signals decay so taste can change`() {
        val recent = listOf(
            InteractionSignal(ContentInteractionType.MORE_LIKE_THIS, category = "growth", createdAt = now)
        )
        val ancient = listOf(
            InteractionSignal(ContentInteractionType.MORE_LIKE_THIS, category = "growth", createdAt = now - 90 * day)
        )
        val recentProfile = PersonalizationProfile.compute(emptyMap(), recent, now)
        val ancientProfile = PersonalizationProfile.compute(emptyMap(), ancient, now)
        assertTrue(
            "recent signal should dominate, got ${recentProfile.categoryAffinity["growth"]} vs ${ancientProfile.categoryAffinity["growth"]}",
            (recentProfile.categoryAffinity["growth"] ?: 0.0) > (ancientProfile.categoryAffinity["growth"] ?: 0.0)
        )
    }

    @Test
    fun `too easy and too hard shift target difficulty`() {
        val repeated = { type: ContentInteractionType ->
            listOf(
                InteractionSignal(type, createdAt = now),
                InteractionSignal(type, createdAt = now),
                InteractionSignal(type, createdAt = now)
            )
        }
        val easy = PersonalizationProfile.compute(
            emptyMap(),
            repeated(ContentInteractionType.TOO_EASY),
            now
        )
        val hard = PersonalizationProfile.compute(
            emptyMap(),
            repeated(ContentInteractionType.TOO_HARD),
            now
        )
        // Three signals move the delta by ±1.5, which rounds to a level change.
        assertTrue("expected easy.difficultyDelta < 0, got ${easy.difficultyDelta}", easy.difficultyDelta < 0)
        assertTrue("expected hard.difficultyDelta > 0, got ${hard.difficultyDelta}", hard.difficultyDelta > 0)
    }
}
