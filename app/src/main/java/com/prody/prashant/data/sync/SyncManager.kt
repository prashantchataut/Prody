package com.prody.prashant.data.sync

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.data.network.NetworkConnectivityManager
import com.prody.prashant.data.network.NetworkStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

enum class SyncStatus {
    SYNCED,
    SYNCING,
    PENDING,
    FAILED,
    OFFLINE,
    DISABLED
}

enum class SyncOperationType {
    JOURNAL_CREATE,
    JOURNAL_UPDATE,
    JOURNAL_DELETE,
    PROFILE_UPDATE,
    ACHIEVEMENT_UNLOCK,
    STREAK_UPDATE,
    FUTURE_MESSAGE_CREATE,
    FUTURE_MESSAGE_UPDATE,
    VOCABULARY_PROGRESS,
    SETTINGS_UPDATE
}

@Serializable
data class SyncOperation(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: SyncOperationType,
    val entityId: Long? = null,
    val data: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val priority: Int = 0,
    val retryCount: Int = 0
)

enum class ConflictResolution {
    LOCAL_WINS,
    SERVER_WINS,
    LAST_WRITE_WINS,
    KEEP_BOTH
}

data class SyncState(
    val status: SyncStatus = SyncStatus.OFFLINE,
    val pendingCount: Int = 0,
    val lastSyncTime: Long? = null,
    val lastError: String? = null
) {
    val hasPendingChanges: Boolean get() = pendingCount > 0
    val isOnline: Boolean get() = status != SyncStatus.OFFLINE && status != SyncStatus.DISABLED
    val statusMessage: String
        get() = when (status) {
            SyncStatus.SYNCED -> "All changes saved"
            SyncStatus.SYNCING -> "Syncing..."
            SyncStatus.PENDING -> "$pendingCount changes pending"
            SyncStatus.FAILED -> "Sync failed. Will retry."
            SyncStatus.OFFLINE -> "Offline. Changes saved locally."
            SyncStatus.DISABLED -> "Sync disabled"
        }
}

sealed class SyncResult {
    data class Success(val operationId: String) : SyncResult()
    data class Failed(val operationId: String, val error: String, val willRetry: Boolean) : SyncResult()
    data class Skipped(val operationId: String, val reason: String) : SyncResult()
}

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkManager: NetworkConnectivityManager,
    private val preferencesManager: PreferencesManager,
    @Named("SyncDataStore") private val syncDataStore: DataStore<Preferences>,
    private val journalDao: JournalDao,
    private val vocabularyDao: VocabularyDao
) {
    companion object {
        private const val TAG = "SyncManager"
        private const val MAX_RETRY_COUNT = 3
        private const val INITIAL_RETRY_DELAY_MS = 1000L
        private const val MAX_RETRY_DELAY_MS = 8000L
        private const val RETRY_MULTIPLIER = 2.0
        private const val MAX_OPERATION_RETRIES = 3
        private val PENDING_OPERATIONS_KEY = stringPreferencesKey("pending_sync_operations")
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mutex = Mutex()
    private val operationQueue = mutableListOf<SyncOperation>()

    private val _syncState = MutableStateFlow(SyncState())
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _syncEnabled = MutableStateFlow(true)
    val syncEnabled: StateFlow<Boolean> = _syncEnabled.asStateFlow()

    init {
        observeNetworkChanges()
        scope.launch { loadPendingOperations() }
    }

    private fun observeNetworkChanges() {
        scope.launch {
            networkManager.networkState.collect { networkState ->
                when (networkState.status) {
                    NetworkStatus.AVAILABLE -> {
                        if (_syncEnabled.value) {
                            mutex.withLock {
                                if (operationQueue.isNotEmpty()) processSyncQueueLocked()
                            }
                        } else {
                            updateSyncStatus(
                                if (operationQueue.isEmpty()) SyncStatus.SYNCED else SyncStatus.PENDING
                            )
                        }
                    }
                    NetworkStatus.LOST, NetworkStatus.UNAVAILABLE -> updateSyncStatus(SyncStatus.OFFLINE)
                    else -> {}
                }
            }
        }
    }

    fun queueOperation(operation: SyncOperation) {
        scope.launch {
            mutex.withLock {
                operationQueue.add(operation)
                savePendingOperationsLocked()
                updatePendingCount()
            }
            if (networkManager.isOnline && _syncEnabled.value) {
                processSyncQueue()
            }
        }
    }

    fun queueJournalOperation(type: SyncOperationType, journalId: Long, data: String = "") {
        queueOperation(SyncOperation(type = type, entityId = journalId, data = data, priority = 5))
    }

    private suspend fun processSyncQueueLocked() {
        if (operationQueue.isEmpty()) {
            updateSyncStatus(SyncStatus.SYNCED)
            return
        }
        if (!networkManager.isOnline) {
            updateSyncStatus(SyncStatus.OFFLINE)
            return
        }

        updateSyncStatus(SyncStatus.SYNCING)

        val sortedOperations = operationQueue.sortedWith(
            compareByDescending<SyncOperation> { it.priority }.thenBy { it.createdAt }
        )

        val failedOperations = mutableListOf<SyncOperation>()
        val completedIds = mutableSetOf<String>()

        for (operation in sortedOperations) {
            try {
                val result = executeOperationWithRetry(operation)
                when (result) {
                    is SyncResult.Success -> completedIds.add(result.operationId)
                    is SyncResult.Failed -> {
                        if (result.willRetry) {
                            val retryOp = operation.copy(retryCount = operation.retryCount + 1)
                            failedOperations.add(retryOp)
                        } else {
                            completedIds.add(result.operationId)
                        }
                        Log.w(TAG, "Operation ${operation.type} failed: ${result.error}")
                    }
                    is SyncResult.Skipped -> {
                        completedIds.add(result.operationId)
                        Log.i(TAG, "Operation ${operation.type} skipped: ${result.reason}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error processing ${operation.type}", e)
                val retryOp = operation.copy(retryCount = operation.retryCount + 1)
                if (retryOp.retryCount <= MAX_OPERATION_RETRIES) {
                    failedOperations.add(retryOp)
                }
            }
        }

        operationQueue.removeAll { it.id in completedIds }
        operationQueue.addAll(failedOperations)
        savePendingOperationsLocked()
        updatePendingCount()

        _syncState.value = _syncState.value.copy(
            lastSyncTime = System.currentTimeMillis(),
            lastError = if (failedOperations.isNotEmpty()) "${failedOperations.size} operations failed" else null
        )
        updateSyncStatus(if (operationQueue.isEmpty()) SyncStatus.SYNCED else SyncStatus.PENDING)
    }

    private suspend fun executeOperationWithRetry(operation: SyncOperation): SyncResult {
        var currentDelay = INITIAL_RETRY_DELAY_MS

        repeat(MAX_RETRY_COUNT) { attempt ->
            try {
                return executeOperation(operation)
            } catch (e: Exception) {
                val isRetryable = isRetryableError(e)
                Log.w(TAG, "${operation.type} failed (attempt ${attempt + 1}/$MAX_RETRY_COUNT): ${e.message}")

                if (!isRetryable || attempt == MAX_RETRY_COUNT - 1) {
                    return SyncResult.Failed(operation.id, e.message ?: "Unknown error", willRetry = false)
                }

                delay(currentDelay)
                currentDelay = (currentDelay * RETRY_MULTIPLIER).toLong().coerceAtMost(MAX_RETRY_DELAY_MS)
            }
        }
        return SyncResult.Failed(operation.id, "Max retries exceeded", willRetry = false)
    }

    private fun isRetryableError(e: Exception): Boolean {
        val message = e.message?.lowercase() ?: ""
        return message.contains("network") ||
                message.contains("timeout") ||
                message.contains("connection") ||
                message.contains("429") ||
                message.contains("503") ||
                message.contains("500") ||
                e is java.io.IOException
    }

    private suspend fun executeOperation(operation: SyncOperation): SyncResult {
        return when (operation.type) {
            SyncOperationType.JOURNAL_CREATE -> {
                operation.entityId?.let { syncJournalEntry(it) }
                    ?: SyncResult.Skipped(operation.id, "No entity ID")
            }
            SyncOperationType.JOURNAL_UPDATE -> {
                operation.entityId?.let { updateJournalEntry(it) }
                    ?: SyncResult.Skipped(operation.id, "No entity ID")
            }
            SyncOperationType.JOURNAL_DELETE -> {
                operation.entityId?.let { deleteJournalEntry(it) }
                    ?: SyncResult.Skipped(operation.id, "No entity ID")
            }
            SyncOperationType.VOCABULARY_PROGRESS -> {
                operation.entityId?.let { syncVocabulary(it) }
                    ?: SyncResult.Skipped(operation.id, "No entity ID")
            }
            else -> SyncResult.Skipped(operation.id, "Operation type ${operation.type} not yet implemented")
        }
    }

    fun forceSync() {
        if (!networkManager.isOnline) return
        scope.launch { processSyncQueue() }
    }

    private suspend fun processSyncQueue() {
        mutex.withLock { processSyncQueueLocked() }
    }

    fun clearPendingOperations() {
        scope.launch {
            mutex.withLock {
                operationQueue.clear()
                savePendingOperationsLocked()
                updatePendingCount()
                updateSyncStatus(SyncStatus.SYNCED)
            }
        }
    }

    fun setSyncEnabled(enabled: Boolean) {
        _syncEnabled.value = enabled
        if (enabled && networkManager.isOnline) {
            scope.launch { processSyncQueue() }
        } else if (!enabled) {
            updateSyncStatus(SyncStatus.DISABLED)
        }
    }

    fun observeSyncStatus(): Flow<SyncState> = combine(
        syncState,
        networkManager.networkState
    ) { sync, network ->
        when {
            !_syncEnabled.value -> sync.copy(status = SyncStatus.DISABLED)
            !network.isConnected -> sync.copy(status = SyncStatus.OFFLINE)
            else -> sync
        }
    }

    private fun updateSyncStatus(status: SyncStatus) {
        _syncState.value = _syncState.value.copy(status = status)
    }

    private fun updatePendingCount() {
        _syncState.value = _syncState.value.copy(pendingCount = operationQueue.size)
    }

    private suspend fun savePendingOperationsLocked() {
        try {
            val operationsJson = Json.encodeToString(operationQueue.toList())
            syncDataStore.edit { preferences ->
                preferences[PENDING_OPERATIONS_KEY] = operationsJson
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save pending operations", e)
        }
    }

    private suspend fun loadPendingOperations() {
        mutex.withLock {
            try {
                val operationsJson = syncDataStore.data.map { preferences ->
                    preferences[PENDING_OPERATIONS_KEY] ?: "[]"
                }.first()
                val operations = Json.decodeFromString<List<SyncOperation>>(operationsJson)
                operationQueue.clear()
                operationQueue.addAll(operations)
                updatePendingCount()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load pending operations", e)
            }
        }
    }

    // TODO: Replace with remote API call when backend is available
    private suspend fun syncJournalEntry(entryId: Long): SyncResult {
        Log.i(TAG, "Syncing journal entry: $entryId")
        return try {
            val entry = journalDao.getEntryById(entryId)
            if (entry == null) {
                Log.w(TAG, "Journal entry $entryId not found, skipping sync")
                SyncResult.Skipped("journal_create_$entryId", "Entry not found")
            } else {
                journalDao.updateSyncStatus(entryId, "synced")
                Log.i(TAG, "Marked journal entry $entryId as synced")
                SyncResult.Success("journal_create_$entryId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to mark journal entry $entryId as synced", e)
            SyncResult.Failed("journal_create_$entryId", e.message ?: "Unknown error", willRetry = true)
        }
    }

    private suspend fun updateJournalEntry(entryId: Long): SyncResult {
        Log.i(TAG, "Updating journal entry: $entryId")
        return try {
            val entry = journalDao.getEntryById(entryId)
            if (entry == null) {
                Log.w(TAG, "Journal entry $entryId not found, skipping update sync")
                SyncResult.Skipped("journal_update_$entryId", "Entry not found")
            } else {
                journalDao.updateSyncStatus(entryId, "synced")
                Log.i(TAG, "Marked journal entry $entryId as synced (update)")
                SyncResult.Success("journal_update_$entryId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to mark journal entry $entryId as synced (update)", e)
            SyncResult.Failed("journal_update_$entryId", e.message ?: "Unknown error", willRetry = true)
        }
    }

    private suspend fun deleteJournalEntry(entryId: Long): SyncResult {
        Log.i(TAG, "Deleting journal entry: $entryId")
        return try {
            journalDao.softDeleteEntry(entryId)
            journalDao.updateSyncStatus(entryId, "synced")
            Log.i(TAG, "Soft-deleted and marked journal entry $entryId as synced")
            SyncResult.Success("journal_delete_$entryId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to soft-delete journal entry $entryId", e)
            SyncResult.Failed("journal_delete_$entryId", e.message ?: "Unknown error", willRetry = true)
        }
    }

    private suspend fun syncVocabulary(vocabId: Long): SyncResult {
        Log.i(TAG, "Syncing vocabulary: $vocabId")
        // TODO: Add syncStatus column to VocabularyEntity for full sync tracking
        return try {
            val word = vocabularyDao.getWordById(vocabId)
            if (word == null) {
                Log.w(TAG, "Vocabulary word $vocabId not found, skipping sync")
                SyncResult.Skipped("vocabulary_$vocabId", "Word not found")
            } else {
                Log.i(TAG, "Vocabulary word $vocabId verified (syncStatus not yet tracked in entity)")
                SyncResult.Success("vocabulary_$vocabId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync vocabulary $vocabId", e)
            SyncResult.Failed("vocabulary_$vocabId", e.message ?: "Unknown error", willRetry = true)
        }
    }
}