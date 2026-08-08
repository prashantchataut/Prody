package com.kairos.app.ui.browse

import com.kairos.app.data.local.entity.JournalEntryEntity
import com.kairos.app.data.local.entity.VocabularyEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BrowsePoliciesTest {

    @Test
    fun vocabulary_dueItemsComeBeforeAlphabeticalItems() {
        val now = 1_000L
        val words = listOf(
            VocabularyEntity(id = 1, word = "Able", definition = "capable"),
            VocabularyEntity(id = 2, word = "Zest", definition = "energy", nextReviewAt = 900L)
        )

        val result = filterVocabulary(words, false, "all", "", now)

        assertEquals(listOf(2L, 1L), result.map { it.id })
    }

    @Test
    fun vocabulary_combinesSavedAndQueryFilters() {
        val words = listOf(
            VocabularyEntity(id = 1, word = "Lucid", definition = "clear", isFavorite = true),
            VocabularyEntity(id = 2, word = "Liminal", definition = "at a boundary", isFavorite = true),
            VocabularyEntity(id = 3, word = "Clear", definition = "easy to understand")
        )

        val result = filterVocabulary(words, true, "saved", "clear", 0L)

        assertEquals(listOf(1L), result.map { it.id })
    }

    @Test
    fun vocabulary_newMeansNeverStudiedAndUnsaved() {
        val words = listOf(
            VocabularyEntity(id = 1, word = "Fresh", definition = "new", reviewCount = 0, masteryLevel = 0),
            VocabularyEntity(id = 2, word = "Seen", definition = "studied once", reviewCount = 1, masteryLevel = 2),
            VocabularyEntity(id = 3, word = "Liked", definition = "saved", isFavorite = true, reviewCount = 0, masteryLevel = 0)
        )

        val result = filterVocabulary(words, false, "new", "", 0L)

        assertEquals(listOf(1L), result.map { it.id })
    }

    @Test
    fun journal_excludesDeletedEntriesAndSortsNewestFirst() {
        val entries = listOf(
            JournalEntryEntity(id = 1, content = "older", mood = "calm", createdAt = 10L),
            JournalEntryEntity(id = 2, content = "deleted", mood = "calm", createdAt = 30L, isDeleted = true),
            JournalEntryEntity(id = 3, content = "newer", mood = "calm", createdAt = 20L)
        )

        val result = filterJournalEntries(entries, false, "")

        assertEquals(listOf(3L, 1L), result.map { it.id })
    }

    @Test
    fun journal_searchesTitleContentAndTagsCaseInsensitively() {
        val entries = listOf(
            JournalEntryEntity(id = 1, title = "Morning", content = "quiet", mood = "calm"),
            JournalEntryEntity(id = 2, content = "Project review", mood = "focused", tags = "WORK")
        )

        val result = filterJournalEntries(entries, false, "work")

        assertTrue(result.single().id == 2L)
    }
}
