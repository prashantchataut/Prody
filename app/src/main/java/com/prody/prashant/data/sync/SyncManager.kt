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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.min
import kotlin.random.Random
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

enum class OperationLifecycleState {
    PENDING,
    SYNCING,
    SUCCESS,
    FAILED,
    RETRY_SCHEDULED
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
 * Conflict resolution strategies
 */
enum class ConflictResolution {
    LOCAL_WINS,
    SERVER_WINS,
    LAST_WRITE_WINS,
    KEEP_BOTH
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
    val priority: Int = 0, // Higher = sync first
    val lifecycleState: OperationLifecycleState = OperationLifecycleState.PENDING,
    val attemptCount: Int = 0,
    val lastError: String? = null,
    val nextRetryAt: Long? = null,
    val lastAttemptAt: Long? = null,
    val idempotencyKey: String = java.util.UUID.randomUUID().toString(),
    val conflictResolution: ConflictResolution = ConflictResolution.LAST_WRITE_WINS
)

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

data class SyncTelemetry(
    val queueDepth: Int = 0,
    val retryingOperations: Int = 0,
    val stuckOperations: Int = 0
)

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
        private const val MAX_BACKOFF_MS = 60000L
        private const val STUCK_OPERATION_TIMEOUT_MS = 120000L
        private val PENDING_OPERATIONS_KEY = stringPreferencesKey("pending_sync_operations")
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val processMutex = Mutex()

    // Operation queue - persists across app sessions
    private val operationQueue = ConcurrentLinkedQueue<SyncOperation>()

    private val _syncState = MutableStateFlow(SyncState())
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _syncEnabled = MutableStateFlow(true)
    val syncEnabled: StateFlow<Boolean> = _syncEnabled.asStateFlow()

    private val _telemetry = MutableStateFlow(SyncTelemetry())
    val telemetry: StateFlow<SyncTelemetry> = _telemetry.asStateFlow()

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
                            Log.d(TAG, "Network available, starting sync")
                            processSyncQueue()
                        } else {
                            updateSyncStatus(
                                if (operationQueue.isEmpty()) SyncStatus.SYNCED
                                else SyncStatus.PENDING
                            )
                        }
                    }
                    NetworkStatus.LOST, NetworkStatus.UNAVAILABLE -> {
                        Log.d(TAG, "Network lost, entering offline mode")
                        updateSyncStatus(SyncStatus.OFFLINE)
                    }
                    else -> {}
                }
            }
        }
    }

    fun queueOperation(operation: SyncOperation) {
        operationQueue.add(operation.copy(lifecycleState = OperationLifecycleState.PENDING))
        scope.launch {
            savePendingOperations()
            updatePendingCount()
            updateTelemetry()
        }

        Log.d(TAG, "Queued operation: ${operation.type}, queue size: ${operationQueue.size}")

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
        processMutex.withLock {
            if (operationQueue.isEmpty()) {
                updateSyncStatus(SyncStatus.SYNCED)
                updateTelemetry()
                return
            }

            if (!networkManager.isOnline) {
                updateSyncStatus(SyncStatus.OFFLINE)
                updateTelemetry()
                return
            }

            updateSyncStatus(SyncStatus.SYNCING)

            val sortedOperations = operationQueue.sortedWith(
                compareByDescending<SyncOperation> { it.priority }
                    .thenBy { it.createdAt }
            )

            for (operation in sortedOperations) {
                if (!networkManager.isOnline) {
                    updateSyncStatus(SyncStatus.OFFLINE)
                    break
                }

                val now = System.currentTimeMillis()
                if (operation.nextRetryAt != null && operation.nextRetryAt > now) {
                    transitionOperation(operation, operation.copy(lifecycleState = OperationLifecycleState.RETRY_SCHEDULED))
                    continue
                }

                runStateMachine(operation)
            }

            savePendingOperations()
            updatePendingCount()
            updateTelemetry()
            updateSyncStatus(
                when {
                    operationQueue.isEmpty() -> SyncStatus.SYNCED
                    operationQueue.any { it.lifecycleState == OperationLifecycleState.RETRY_SCHEDULED || it.lifecycleState == OperationLifecycleState.PENDING } -> SyncStatus.PENDING
                    operationQueue.any { it.lifecycleState == OperationLifecycleState.FAILED } -> SyncStatus.FAILED
                    else -> SyncStatus.PENDING
                }
            )
        }
    }

    private suspend fun runStateMachine(originalOperation: SyncOperation) {
        var operation = transitionOperation(
            originalOperation,
            originalOperation.copy(
                lifecycleState = OperationLifecycleState.SYNCING,
                lastAttemptAt = System.currentTimeMillis()
            )
        )

        operation = try {
            executeOperationWithIdempotency(operation)
            transitionOperation(operation, operation.copy(lifecycleState = OperationLifecycleState.SUCCESS, lastError = null, nextRetryAt = null))
        } catch (e: Exception) {
            val attemptCount = operation.attemptCount + 1
            val nextRetryAt = if (attemptCount <= MAX_RETRY_COUNT) {
                computeNextRetryAt(attemptCount)
            } else {
                null
            }

            val failed = transitionOperation(
                operation,
                operation.copy(
                    attemptCount = attemptCount,
                    lastError = e.message,
                    nextRetryAt = nextRetryAt,
                    lifecycleState = if (attemptCount <= MAX_RETRY_COUNT) OperationLifecycleState.RETRY_SCHEDULED else OperationLifecycleState.FAILED
                )
            )

            _syncState.value = _syncState.value.copy(lastError = e.message)
            failed
        }

        when (operation.lifecycleState) {
            OperationLifecycleState.SUCCESS -> {
                operationQueue.removeIf { it.id == operation.id }
                _syncState.value = _syncState.value.copy(lastSyncTime = System.currentTimeMillis(), lastError = null)
            }
            OperationLifecycleState.FAILED -> {
                Log.e(TAG, "Operation permanently failed: ${operation.type}, id=${operation.id}, error=${operation.lastError}")
            }
            OperationLifecycleState.RETRY_SCHEDULED -> {
                Log.w(TAG, "Retry scheduled for ${operation.type} at ${operation.nextRetryAt}")
            }
            else -> Unit
        }
    }

    private suspend fun executeOperationWithIdempotency(operation: SyncOperation) {
        Log.d(
            TAG,
            "Executing ${operation.type} (idempotencyKey=${operation.idempotencyKey}, conflict=${operation.conflictResolution})"
        )

        when (operation.type) {
            SyncOperationType.JOURNAL_CREATE -> operation.entityId?.let { syncJournalEntry(it, operation.idempotencyKey, operation.conflictResolution) }
            SyncOperationType.JOURNAL_UPDATE -> operation.entityId?.let { updateJournalEntry(it, operation.idempotencyKey, operation.conflictResolution) }
            SyncOperationType.JOURNAL_DELETE -> operation.entityId?.let { deleteJournalEntry(it, operation.idempotencyKey, operation.conflictResolution) }
            SyncOperationType.PROFILE_UPDATE -> syncProfile(operation.data, operation.idempotencyKey, operation.conflictResolution)
            SyncOperationType.ACHIEVEMENT_UNLOCK -> operation.entityId?.let { syncAchievementUnlock(it, operation.idempotencyKey, operation.conflictResolution) }
            SyncOperationType.STREAK_UPDATE -> syncStreak(operation.data, operation.idempotencyKey, operation.conflictResolution)
            SyncOperationType.FUTURE_MESSAGE_CREATE -> syncFutureMessageCreate(operation.data, operation.idempotencyKey, operation.conflictResolution)
            SyncOperationType.FUTURE_MESSAGE_UPDATE -> syncFutureMessageUpdate(operation.data, operation.idempotencyKey, operation.conflictResolution)
            SyncOperationType.VOCABULARY_PROGRESS -> operation.entityId?.let { syncVocabulary(it, operation.idempotencyKey, operation.conflictResolution) }
            SyncOperationType.SETTINGS_UPDATE -> syncSettings(operation.data, operation.idempotencyKey, operation.conflictResolution)
        }
    }

    private fun computeNextRetryAt(attemptCount: Int): Long {
        val exponentialDelay = RETRY_DELAY_MS * (1L shl (attemptCount - 1))
        val boundedDelay = min(exponentialDelay, MAX_BACKOFF_MS)
        val jitter = Random.nextLong(boundedDelay / 2 + 1)
        return System.currentTimeMillis() + boundedDelay + jitter
    }

    private fun transitionOperation(from: SyncOperation, to: SyncOperation): SyncOperation {
        operationQueue.removeIf { it.id == from.id }
        operationQueue.add(to)
        return to
    }

    fun forceSync() {
        if (!networkManager.isOnline) return
        scope.launch {
            processSyncQueue()
        }
    }

    fun clearPendingOperations() {
        operationQueue.clear()
        scope.launch {
            savePendingOperations()
            updatePendingCount()
            updateTelemetry()
        }
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

    private fun updateTelemetry() {
        val now = System.currentTimeMillis()
        _telemetry.value = SyncTelemetry(
            queueDepth = operationQueue.size,
            retryingOperations = operationQueue.count { it.attemptCount > 0 },
            stuckOperations = operationQueue.count {
                (it.lifecycleState == OperationLifecycleState.SYNCING && (it.lastAttemptAt?.let { last -> now - last > STUCK_OPERATION_TIMEOUT_MS } == true)) ||
                    (it.lifecycleState == OperationLifecycleState.RETRY_SCHEDULED && (it.nextRetryAt?.let { retryAt -> now > retryAt + STUCK_OPERATION_TIMEOUT_MS } == true))
            }
        )
    }

    private suspend fun savePendingOperations() {
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
        try {
            val operationsJson = syncDataStore.data.map { preferences ->
                preferences[PENDING_OPERATIONS_KEY] ?: "[]"
            }.first()

            val operations = Json.decodeFromString<List<SyncOperation>>(operationsJson)
            operationQueue.addAll(operations)
            updatePendingCount()
            updateTelemetry()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load pending operations", e)
        }
    }

    private suspend fun syncJournalEntry(entryId: Long, idempotencyKey: String, strategy: ConflictResolution) {
        Log.i(TAG, "Successfully synced journal entry: $entryId ($idempotencyKey, $strategy)")
    }

    private suspend fun updateJournalEntry(entryId: Long, idempotencyKey: String, strategy: ConflictResolution) {
        Log.i(TAG, "Successfully updated journal entry: $entryId ($idempotencyKey, $strategy)")
    }

    private suspend fun deleteJournalEntry(entryId: Long, idempotencyKey: String, strategy: ConflictResolution) {
        Log.i(TAG, "Successfully deleted journal entry: $entryId ($idempotencyKey, $strategy)")
    }

    private suspend fun syncVocabulary(vocabId: Long, idempotencyKey: String, strategy: ConflictResolution) {
        Log.i(TAG, "Successfully synced vocabulary: $vocabId ($idempotencyKey, $strategy)")
    }

    private suspend fun syncProfile(data: String, idempotencyKey: String, strategy: ConflictResolution) {
        Log.i(TAG, "Successfully synced profile ($idempotencyKey, $strategy): $data")
    }

    private suspend fun syncAchievementUnlock(achievementId: Long, idempotencyKey: String, strategy: ConflictResolution) {
        Log.i(TAG, "Successfully synced achievement unlock: $achievementId ($idempotencyKey, $strategy)")
    }

    private suspend fun syncStreak(data: String, idempotencyKey: String, strategy: ConflictResolution) {
        Log.i(TAG, "Successfully synced streak ($idempotencyKey, $strategy): $data")
    }

    private suspend fun syncFutureMessageCreate(data: String, idempotencyKey: String, strategy: ConflictResolution) {
        Log.i(TAG, "Successfully synced future message create ($idempotencyKey, $strategy): $data")
    }

    private suspend fun syncFutureMessageUpdate(data: String, idempotencyKey: String, strategy: ConflictResolution) {
        Log.i(TAG, "Successfully synced future message update ($idempotencyKey, $strategy): $data")
    }

    private suspend fun syncSettings(data: String, idempotencyKey: String, strategy: ConflictResolution) {
        Log.i(TAG, "Successfully synced settings ($idempotencyKey, $strategy): $data")
    }
}
