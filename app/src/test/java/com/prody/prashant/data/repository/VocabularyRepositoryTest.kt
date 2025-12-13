package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.data.local.entity.VocabularyEntity
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.repository.VocabularyRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class VocabularyRepositoryTest {

    private lateinit var repository: VocabularyRepository
    private val vocabularyDao: VocabularyDao = mockk(relaxed = true)

    @Before
    fun setup() {
        repository = VocabularyRepositoryImpl(vocabularyDao)
    }

    @Test
    fun `getAllWords returns flow of words`() = runTest {
        // Given
        val mockWords = listOf(
            VocabularyEntity(id = 1, word = "Serendipity", definition = "Happy accident", isLearned = false, createdAt = 123L),
            VocabularyEntity(id = 2, word = "Ephemeral", definition = "Lasting a short time", isLearned = true, createdAt = 124L)
        )
        coEvery { vocabularyDao.getAllWords() } returns flowOf(mockWords)

        // When
        val result = repository.getAllWords().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Serendipity", result[0].word)
    }

    @Test
    fun `markAsLearned updates word status`() = runTest {
        // Given
        val wordId = 1L
        coEvery { vocabularyDao.markAsLearned(wordId) } returns Unit

        // When
        val result = repository.markAsLearned(wordId)

        // Then
        assertTrue(result is Result.Success)
        coVerify { vocabularyDao.markAsLearned(wordId) }
    }

    @Test
    fun `getWordOfTheDay returns a random word`() = runTest {
        // Given
        val mockWord = VocabularyEntity(id = 1, word = "Stoic", definition = "Enduring pain without complaint", isLearned = false, createdAt = 123L)
        coEvery { vocabularyDao.getRandomUnshownWord() } returns mockWord
        coEvery { vocabularyDao.markAsShown(mockWord.id) } returns Unit

        // When
        val result = repository.getWordOfTheDay()

        // Then
        assertTrue(result is Result.Success)
        assertEquals("Stoic", (result as Result.Success).data.word)
        coVerify { vocabularyDao.markAsShown(mockWord.id) }
    }
}
