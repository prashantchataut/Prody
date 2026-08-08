package com.kairos.app.domain.vocabulary

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WordFamilyExpanderTest {

    @Test
    fun `root with no family expands to a single row`() {
        val spec = WordFamilySpec(
            word = "Equanimity",
            partOfSpeech = "noun",
            definition = "Mental calmness, especially in difficulty.",
            difficulty = 4,
            category = "mindfulness"
        )
        val rows = WordFamilyExpander.expand(listOf(spec))
        assertEquals(1, rows.size)
        assertEquals("Equanimity", rows.single().word)
        assertEquals("mindfulness", rows.single().category)
        assertEquals(4, rows.single().difficulty)
    }

    @Test
    fun `root with family expands root plus each member`() {
        val spec = WordFamilySpec(
            word = "Discipline",
            partOfSpeech = "noun",
            definition = "Training oneself to act in accordance with a chosen code.",
            difficulty = 2,
            category = "self-improvement",
            derived = listOf(
                DerivedWord("Disciplined", "adjective", "Showing controlled behavior."),
                DerivedWord("Self-Discipline", "noun", "Control of one's own behavior.", difficultyOffset = 1)
            )
        )
        val rows = WordFamilyExpander.expand(listOf(spec))
        assertEquals(3, rows.size)
        assertEquals(listOf("Discipline", "Disciplined", "Self-Discipline"), rows.map { it.word })
    }

    @Test
    fun `derived rows are marked with family origin and root word is recoverable`() {
        val spec = WordFamilySpec(
            word = "Consistency",
            partOfSpeech = "noun",
            definition = "Behaving the same way over time.",
            derived = listOf(DerivedWord("Consistent", "adjective", "Acting the same way over time."))
        )
        val rows = WordFamilyExpander.expand(listOf(spec))

        assertFalse(WordFamilyExpander.isDerived(rows[0]))
        assertTrue(WordFamilyExpander.isDerived(rows[1]))
        assertEquals("Consistency", WordFamilyExpander.rootWordOf(rows[1]))
    }

    @Test
    fun `difficulty offset is clamped to the one to five scale`() {
        val spec = WordFamilySpec(
            word = "Absorb",
            partOfSpeech = "verb",
            definition = "To take in or soak up.",
            difficulty = 5,
            derived = listOf(DerivedWord("Absorbed", "adjective", "Engrossed.", difficultyOffset = 3))
        )
        val rows = WordFamilyExpander.expand(listOf(spec))
        assertEquals(5, rows[0].difficulty)
        assertEquals(5, rows[1].difficulty)
    }

    @Test
    fun `expansion is deterministic for the same spec`() {
        val spec = WordFamilySpec(
            word = "Persist",
            partOfSpeech = "verb",
            definition = "To continue firmly despite difficulty.",
            derived = listOf(
                DerivedWord("Persistence", "noun", "The fact of continuing firmly."),
                DerivedWord("Persistent", "adjective", "Continuing firmly.")
            )
        )
        val first = WordFamilyExpander.expand(listOf(spec)).map { it.word to it.origin }
        val second = WordFamilyExpander.expand(listOf(spec)).map { it.word to it.origin }
        assertEquals(first, second)
    }

    @Test
    fun `expanded catalog contains no duplicate words`() {
        val rows = WordFamilyExpander.expand(
            listOf(
                WordFamilySpec("Resilient", "adjective", "Able to recover quickly."),
                WordFamilySpec("Resilience", "noun", "Capacity to recover quickly.")
            )
        )
        val words = rows.map { it.word.lowercase() }
        assertEquals(words.size, words.distinct().size)
    }
}
