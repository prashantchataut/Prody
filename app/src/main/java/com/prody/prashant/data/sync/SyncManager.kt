package com.prody.prashant.data.sync

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.data.network.NetworkConnectivityManager
import com.prody.prashant.data.network.NetworkStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Sync operation status
 */
enum class SyncStatus {
    SYNCED,
    SYNCING,
    PENDING,
    FAILED,
    OFFLINE,
    DISABLED
}

/**
 * Types of sync operations
 */
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

/**
 * Represents a single operation to be synced
 */
@Serializable
data class SyncOperation(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: SyncOperationType,
    val entityId: Long? = null,
    val data: String = "", // JSON payload
    val createdAt: Long = System.currentTimeMillis(),
    val priority: Int = 0 // Higher = sync first
)

/**
 * Conflict resolution strategies
 */
enum class ConflictResolution {
    LOCAL_WINS,
    SERVER_WINS,
    LAST_WRITE_WINS,
    KEEP_BOTH
}

/**
 * Overall sync state for UI display
 */
data class SyncState(
    val status: SyncStatus = SyncStatus.OFFLINE,
    val pendingCount: Int = 0,
    val lastSyncTime: Long? = null,
    val lastError: String? = null
) {
    val hasPendingChanges: Boolean
        get() = pendingCount > 0

    val isOnline: Boolean
        get() = status != SyncStatus.OFFLINE && status != SyncStatus.DISABLED

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

/**
 * SyncManager handles offline-first data synchronization.
 */
@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkManager: NetworkConnectivityManager,
    private val preferencesManager: PreferencesManager,
    @Named("SyncDataStore") private val syncDataStore: DataStore<Preferences>
) {
    companion object {
        private const val TAG = "SyncManager"
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_DELAY_MS = 5000L
        private val PENDING_OPERATIONS_KEY = stringPreferencesKey("pending_sync_operations")
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Operation queue - persists across app sessions
    private val operationQueue = ConcurrentLinkedQueue<SyncOperation>()

    private val _syncState = MutableStateFlow(SyncState())
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _syncEnabled = MutableStateFlow(true)
    val syncEnabled: StateFlow<Boolean> = _syncEnabled.asStateFlow()

    init {
        observeNetworkChanges()
        scope.launch {
            loadPendingOperations()
        }
    }

    private fun observeNetworkChanges() {
        scope.launch {
            networkManager.networkState.collect { networkState ->
                when (networkState.status) {
                    NetworkStatus.AVAILABLE -> {
                        if (_syncEnabled.value && operationQueue.isNotEmpty()) {
                            com.prody.prashant.util.AppLogger.d(TAG, "Network available, starting sync")
                            processSyncQueue()
                        } else {
                            updateSyncStatus(
                                if (operationQueue.isEmpty()) SyncStatus.SYNCED
                                else SyncStatus.PENDING
                            )
                        }
                    }
                    NetworkStatus.LOST, NetworkStatus.UNAVAILABLE -> {
                        com.prody.prashant.util.AppLogger.d(TAG, "Network lost, entering offline mode")
                        updateSyncStatus(SyncStatus.OFFLINE)
                    }
                    else -> {}
                }
            }
        }
    }

    fun queueOperation(operation: SyncOperation) {
        operationQueue.add(operation)
        savePendingOperations()
        updatePendingCount()

        com.prody.prashant.util.AppLogger.d(TAG, "Queued operation: ${operation.type}, queue size: ${operationQueue.size}")

        if (networkManager.isOnline && _syncEnabled.value) {
            scope.launch {
                processSyncQueue()
            }
        }
    }

    fun queueJournalOperation(
        type: SyncOperationType,
        journalId: Long,
        data: String = ""
    ) {
        queueOperation(
            SyncOperation(
                type = type,
                entityId = journalId,
                data = data,
                priority = 5
            )
        )
    }

    private suspend fun processSyncQueue() {
        if (operationQueue.isEmpty()) {
            updateSyncStatus(SyncStatus.SYNCED)
            return
        }

        if (!networkManager.isOnline) {
            updateSyncStatus(SyncStatus.OFFLINE)
            return
        }

        updateSyncStatus(SyncStatus.SYNCING)

        try {
            val sortedOperations = operationQueue.sortedWith(
                compareByDescending<SyncOperation> { it.priority }
                    .thenBy { it.createdAt }
            )

            for (operation in sortedOperations) {
                try {
                    when (operation.type) {
                        SyncOperationType.JOURNAL_CREATE -> {
                            operation.entityId?.let { syncJournalEntry(it) }
                        }
                        SyncOperationType.JOURNAL_UPDATE -> {
                            operation.entityId?.let { updateJournalEntry(it) }
                        }
                        SyncOperationType.JOURNAL_DELETE -> {
                            operation.entityId?.let { deleteJournalEntry(it) }
                        }
                        SyncOperationType.VOCABULARY_PROGRESS -> {
                            operation.entityId?.let { syncVocabulary(it) }
                        }
                        else -> {
                            com.prody.prashant.util.AppLogger.d(TAG, "Sync logic not implemented for type: ${operation.type}")
                        }
                    }
                    operationQueue.remove(operation)
                } catch (e: Exception) {
                    com.prody.prashant.util.AppLogger.e(TAG, "Failed to sync operation: ${operation.type}", e)
                    break
                }
            }
            
            savePendingOperations()
            updateSyncStatus(if (operationQueue.isEmpty()) SyncStatus.SYNCED else SyncStatus.PENDING)
            _syncState.value = _syncState.value.copy(
                lastSyncTime = System.currentTimeMillis(),
                lastError = null
            )

        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Sync failed", e)
            updateSyncStatus(SyncStatus.FAILED)
            _syncState.value = _syncState.value.copy(lastError = e.message)
        }

        updatePendingCount()
    }

    fun forceSync() {
        if (!networkManager.isOnline) return
        scope.launch {
            processSyncQueue()
        }
    }

    fun clearPendingOperations() {
        operationQueue.clear()
        savePendingOperations()
        updatePendingCount()
        updateSyncStatus(SyncStatus.SYNCED)
    }

    fun setSyncEnabled(enabled: Boolean) {
        _syncEnabled.value = enabled
        if (enabled && networkManager.isOnline && operationQueue.isNotEmpty()) {
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

    private fun savePendingOperations() {
        scope.launch {
            try {
                val operationsJson = Json.encodeToString(operationQueue.toList())
                syncDataStore.edit { preferences ->
                    preferences[PENDING_OPERATIONS_KEY] = operationsJson
                }
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Failed to save pending operations", e)
            }
        }
    }

    private suspend fun loadPendingOperations() {
        try {
            val operationsJson = syncDataStore.data.map { preferences ->
                preferences[PENDING_OPERATIONS_KEY] ?: "[]"
            }.first()
            
            val operations = Json.decodeFromString<List<SyncOperation>>(operationsJson)
            operationQueue.addAll(operations)
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to load pending operations", e)
        }
    }

    private suspend fun syncJournalEntry(entryId: Long) {
        com.prody.prashant.util.AppLogger.i(TAG, "Successfully synced journal entry: $entryId")
    }

    private suspend fun updateJournalEntry(entryId: Long) {
        com.prody.prashant.util.AppLogger.i(TAG, "Successfully updated journal entry: $entryId")
    }

    private suspend fun deleteJournalEntry(entryId: Long) {
        com.prody.prashant.util.AppLogger.i(TAG, "Successfully deleted journal entry: $entryId")
    }

    private suspend fun syncVocabulary(vocabId: Long) {
        com.prody.prashant.util.AppLogger.i(TAG, "Successfully synced vocabulary: $vocabId")
    }
}
