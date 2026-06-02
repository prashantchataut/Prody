package com.prody.prashant.data.sync

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for SyncManager reliability improvements.
 *
 * Tests cover:
 * - SyncOperation data class behavior
 * - SyncState status messages
 * - SyncResult sealed class hierarchy
 * - Retryable error classification
 * - ConflictResolution enum coverage
 *
 * Integration tests (requiring Android context) are separate and test
 * queue processing, persistence, and network observation.
 */
class SyncOperationTest {

    @Test
    fun `sync operation has unique id by default`() {
        val op1 = SyncOperation(type = SyncOperationType.JOURNAL_CREATE, entityId = 1L)
        val op2 = SyncOperation(type = SyncOperationType.JOURNAL_CREATE, entityId = 1L)
        assertNotEquals("Each operation should have a unique ID", op1.id, op2.id)
    }

    @Test
    fun `sync operation preserves entityId`() {
        val op = SyncOperation(type = SyncOperationType.JOURNAL_UPDATE, entityId = 42L, data = "test")
        assertEquals(42L, op.entityId)
        assertEquals("test", op.data)
    }

    @Test
    fun `sync operation default priority is zero`() {
        val op = SyncOperation(type = SyncOperationType.PROFILE_UPDATE)
        assertEquals(0, op.priority)
    }

    @Test
    fun `sync operation tracks retry count`() {
        val op = SyncOperation(type = SyncOperationType.JOURNAL_CREATE, retryCount = 2)
        assertEquals(2, op.retryCount)
    }

    @Test
    fun `sync operation copy increments retry count`() {
        val op = SyncOperation(type = SyncOperationType.JOURNAL_CREATE, retryCount = 1)
        val retried = op.copy(retryCount = op.retryCount + 1)
        assertEquals(2, retried.retryCount)
        assertEquals(op.id, retried.id)
    }

    @Test
    fun `journal operation has elevated priority`() {
        val op = SyncOperation(
            type = SyncOperationType.JOURNAL_CREATE,
            entityId = 1L,
            priority = 5
        )
        assertEquals(5, op.priority)
    }
}

class SyncStateTest {

    @Test
    fun `default state is offline`() {
        val state = SyncState()
        assertEquals(SyncStatus.OFFLINE, state.status)
    }

    @Test
    fun `hasPendingChanges is true when pending count is positive`() {
        val state = SyncState(status = SyncStatus.PENDING, pendingCount = 3)
        assertTrue(state.hasPendingChanges)
    }

    @Test
    fun `hasPendingChanges is false when pending count is zero`() {
        val state = SyncState(status = SyncStatus.SYNCED, pendingCount = 0)
        assertFalse(state.hasPendingChanges)
    }

    @Test
    fun `isOnline is true for SYNCED status`() {
        val state = SyncState(status = SyncStatus.SYNCED)
        assertTrue(state.isOnline)
    }

    @Test
    fun `isOnline is true for SYNCING status`() {
        val state = SyncState(status = SyncStatus.SYNCING)
        assertTrue(state.isOnline)
    }

    @Test
    fun `isOnline is true for PENDING status`() {
        val state = SyncState(status = SyncStatus.PENDING)
        assertTrue(state.isOnline)
    }

    @Test
    fun `isOnline is false for OFFLINE status`() {
        val state = SyncState(status = SyncStatus.OFFLINE)
        assertFalse(state.isOnline)
    }

    @Test
    fun `isOnline is false for DISABLED status`() {
        val state = SyncState(status = SyncStatus.DISABLED)
        assertFalse(state.isOnline)
    }

    @Test
    fun `statusMessage for SYNCED is correct`() {
        val state = SyncState(status = SyncStatus.SYNCED)
        assertEquals("All changes saved", state.statusMessage)
    }

    @Test
    fun `statusMessage for SYNCING is correct`() {
        val state = SyncState(status = SyncStatus.SYNCING)
        assertEquals("Syncing...", state.statusMessage)
    }

    @Test
    fun `statusMessage for PENDING includes count`() {
        val state = SyncState(status = SyncStatus.PENDING, pendingCount = 5)
        assertEquals("5 changes pending", state.statusMessage)
    }

    @Test
    fun `statusMessage for FAILED mentions retry`() {
        val state = SyncState(status = SyncStatus.FAILED)
        assertEquals("Sync failed. Will retry.", state.statusMessage)
    }

    @Test
    fun `statusMessage for OFFLINE mentions local save`() {
        val state = SyncState(status = SyncStatus.OFFLINE)
        assertEquals("Offline. Changes saved locally.", state.statusMessage)
    }
}

class SyncResultTest {

    @Test
    fun `Success result carries operation id`() {
        val result = SyncResult.Success("journal_create_42")
        assertEquals("journal_create_42", result.operationId)
    }

    @Test
    fun `Failed result carries error message and retry flag`() {
        val result = SyncResult.Failed("op_123", "Network timeout", willRetry = true)
        assertEquals("op_123", result.operationId)
        assertEquals("Network timeout", result.error)
        assertTrue(result.willRetry)
    }

    @Test
    fun `Failed result with no retry indicates permanent failure`() {
        val result = SyncResult.Failed("op_456", "Not found", willRetry = false)
        assertFalse(result.willRetry)
    }

    @Test
    fun `Skipped result carries reason`() {
        val result = SyncResult.Skipped("op_789", "No entity ID")
        assertEquals("op_789", result.operationId)
        assertEquals("No entity ID", result.reason)
    }
}

class SyncRetryableErrorTest {

    private val helper = SyncRetryableErrorHelper()

    @Test
    fun `network errors are retryable`() {
        assertTrue(helper.isRetryableError(Exception("network error")))
    }

    @Test
    fun `timeout errors are retryable`() {
        assertTrue(helper.isRetryableError(Exception("connection timeout")))
    }

    @Test
    fun `connection errors are retryable`() {
        assertTrue(helper.isRetryableError(Exception("connection refused")))
    }

    @Test
    fun `rate limit 429 is retryable`() {
        assertTrue(helper.isRetryableError(Exception("429 Too Many Requests")))
    }

    @Test
    fun `service unavailable 503 is retryable`() {
        assertTrue(helper.isRetryableError(Exception("503 Service Unavailable")))
    }

    @Test
    fun `server error 500 is retryable`() {
        assertTrue(helper.isRetryableError(Exception("500 Internal Server Error")))
    }

    @Test
    fun `IOException is retryable`() {
        assertTrue(helper.isRetryableError(java.io.IOException("reset")))
    }

    @Test
    fun `bad request 400 is not retryable`() {
        assertFalse(helper.isRetryableError(Exception("400 Bad Request")))
    }

    @Test
    fun `unauthorized 401 is not retryable`() {
        assertFalse(helper.isRetryableError(Exception("401 Unauthorized")))
    }

    @Test
    fun `not found 404 is not retryable`() {
        assertFalse(helper.isRetryableError(Exception("404 Not Found")))
    }

    @Test
    fun `null message defaults to not retryable`() {
        assertFalse(helper.isRetryableError(Exception(null as String?)))
    }
}

class ConflictResolutionTest {

    @Test
    fun `all conflict resolution strategies exist`() {
        assertEquals(4, ConflictResolution.values().size)
        assertNotNull(ConflictResolution.LOCAL_WINS)
        assertNotNull(ConflictResolution.SERVER_WINS)
        assertNotNull(ConflictResolution.LAST_WRITE_WINS)
        assertNotNull(ConflictResolution.KEEP_BOTH)
    }
}

class SyncOperationTypeTest {

    @Test
    fun `all operation types exist`() {
        assertEquals(10, SyncOperationType.values().size)
        assertNotNull(SyncOperationType.JOURNAL_CREATE)
        assertNotNull(SyncOperationType.JOURNAL_UPDATE)
        assertNotNull(SyncOperationType.JOURNAL_DELETE)
        assertNotNull(SyncOperationType.PROFILE_UPDATE)
        assertNotNull(SyncOperationType.ACHIEVEMENT_UNLOCK)
        assertNotNull(SyncOperationType.STREAK_UPDATE)
        assertNotNull(SyncOperationType.FUTURE_MESSAGE_CREATE)
        assertNotNull(SyncOperationType.FUTURE_MESSAGE_UPDATE)
        assertNotNull(SyncOperationType.VOCABULARY_PROGRESS)
        assertNotNull(SyncOperationType.SETTINGS_UPDATE)
    }
}

/**
 * Helper class exposing the same retryable error logic as SyncManager
 * for unit testing without requiring Android context.
 */
class SyncRetryableErrorHelper {
    fun isRetryableError(e: Exception): Boolean {
        val message = e.message?.lowercase() ?: ""
        return message.contains("network") ||
                message.contains("timeout") ||
                message.contains("connection") ||
                message.contains("429") ||
                message.contains("503") ||
                message.contains("500") ||
                e is java.io.IOException
    }
}