package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.repository.JournalRepository
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

class JournalRepositoryTest {

    private lateinit var repository: JournalRepository
    private val journalDao: JournalDao = mockk(relaxed = true)

    @Before
    fun setup() {
        repository = JournalRepositoryImpl(journalDao)
    }

    @Test
    fun `getAllEntries returns flow of entries`() = runTest {
        // Given
        val mockEntries = listOf(
            JournalEntryEntity(id = 1, content = "Today was good", mood = "Happy", createdAt = 100L),
            JournalEntryEntity(id = 2, content = "Felt anxious", mood = "Anxious", createdAt = 200L)
        )
        coEvery { journalDao.getAllEntries() } returns flowOf(mockEntries)

        // When
        val result = repository.getAllEntries().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Today was good", result[0].content)
    }

    @Test
    fun `saveEntry insert entry and returns id`() = runTest {
        // Given
        val entry = JournalEntryEntity(content = "New Entry", mood = "Neutral", createdAt = 300L)
        val expectedId = 5L
        coEvery { journalDao.insertEntry(entry) } returns expectedId

        // When
        val result = repository.saveEntry(entry)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedId, (result as Result.Success).data)
        coVerify { journalDao.insertEntry(entry) }
    }

    @Test
    fun `deleteEntryById deletes entry`() = runTest {
        // Given
        val entryId = 1L
        coEvery { journalDao.deleteEntryById(entryId) } returns Unit

        // When
        val result = repository.deleteEntryById(entryId)

        // Then
        assertTrue(result is Result.Success)
        coVerify { journalDao.deleteEntryById(entryId) }
    }
}
