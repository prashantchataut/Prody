package com.prody.prashant.data.sync

import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sync operation status
 */
enum class SyncStatus {
    /** No sync in progress, data is up to date */
    SYNCED,
    /** Sync operation in progress */
    SYNCING,
    /** Pending changes waiting to sync */
    PENDING,
    /** Sync failed, will retry */
    FAILED,
    /** Offline, changes will sync when online */
    OFFLINE,
    /** User disabled sync */
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
    /** Local changes take precedence */
    LOCAL_WINS,
    /** Server changes take precedence */
    SERVER_WINS,
    /** Most recent change wins */
    LAST_WRITE_WINS,
    /** Keep both versions, let user decide */
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
 *
 * Features:
 * - Queue-based operation handling
 * - Automatic retry on network restore
 * - Conflict resolution strategies
 * - Priority-based sync ordering
 * - Real-time sync status updates
 *
 * Sync Priority Order:
 * 1. Profile updates (ensure identity is synced first)
 * 2. Achievement/streak updates (gamification consistency)
 * 3. Journal entries (core content)
 * 4. Future messages
 * 5. Settings/vocabulary progress
 *
 * This is a local-first implementation. When server sync is added:
 * - Implement processSyncQueue() to send operations to server
 * - Add conflict detection and resolution
 * - Implement pullChanges() to receive server updates
 */
@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkManager: NetworkConnectivityManager,
    private val preferencesManager: PreferencesManager
) {
    companion object {
        private const val TAG = "SyncManager"
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_DELAY_MS = 5000L
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
        loadPendingOperations()
    }

    /**
     * Observe network changes and trigger sync when online
     */
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
                    NetworkStatus.LOSING -> {
                        // Don't change status yet, wait for actual loss
                    }
                }
            }
        }
    }

    /**
     * Add an operation to the sync queue
     */
    fun queueOperation(operation: SyncOperation) {
        operationQueue.add(operation)
        savePendingOperations()
        updatePendingCount()

        Log.d(TAG, "Queued operation: ${operation.type}, queue size: ${operationQueue.size}")

        // If online and sync enabled, process immediately
        if (networkManager.isOnline && _syncEnabled.value) {
            scope.launch {
                processSyncQueue()
            }
        }
    }

    /**
     * Queue a journal operation
     */
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
                priority = 5 // Medium priority
            )
        )
    }

    /**
     * Queue a profile update operation
     */
    fun queueProfileUpdate(data: String = "") {
        queueOperation(
            SyncOperation(
                type = SyncOperationType.PROFILE_UPDATE,
                data = data,
                priority = 10 // High priority
            )
        )
    }

    /**
     * Queue an achievement unlock
     */
    fun queueAchievementUnlock(achievementId: String) {
        queueOperation(
            SyncOperation(
                type = SyncOperationType.ACHIEVEMENT_UNLOCK,
                data = achievementId,
                priority = 8 // High priority
            )
        )
    }

    /**
     * Process the sync queue
     * Currently logs operations - implement server sync when ready
     */
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
            // Sort by priority (higher first) then by creation time
            val sortedOperations = operationQueue.sortedWith(
                compareByDescending<SyncOperation> { it.priority }
                    .thenBy { it.createdAt }
            )

            for (operation in sortedOperations) {
                // TODO: Implement actual server sync when backend is ready
                // For now, just log and mark as synced
                Log.d(TAG, "Processing sync operation: ${operation.type}, id: ${operation.entityId}")

                // Simulate successful sync
                operationQueue.remove(operation)
            }

            savePendingOperations()
            updateSyncStatus(SyncStatus.SYNCED)
            _syncState.value = _syncState.value.copy(
                lastSyncTime = System.currentTimeMillis(),
                lastError = null
            )

        } catch (e: Exception) {
            Log.e(TAG, "Sync failed", e)
            updateSyncStatus(SyncStatus.FAILED)
            _syncState.value = _syncState.value.copy(lastError = e.message)
        }

        updatePendingCount()
    }

    /**
     * Force a sync attempt
     */
    fun forceSync() {
        if (!networkManager.isOnline) {
            Log.d(TAG, "Cannot force sync while offline")
            return
        }

        scope.launch {
            processSyncQueue()
        }
    }

    /**
     * Clear all pending operations (use with caution)
     */
    fun clearPendingOperations() {
        operationQueue.clear()
        savePendingOperations()
        updatePendingCount()
        updateSyncStatus(SyncStatus.SYNCED)
    }

    /**
     * Enable or disable sync
     */
    fun setSyncEnabled(enabled: Boolean) {
        _syncEnabled.value = enabled
        if (enabled && networkManager.isOnline && operationQueue.isNotEmpty()) {
            scope.launch {
                processSyncQueue()
            }
        } else if (!enabled) {
            updateSyncStatus(SyncStatus.DISABLED)
        }
    }

    /**
     * Get combined state flow for UI
     */
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

    /**
     * Save pending operations to persistent storage
     * In production, use Room or DataStore for durability
     */
    private fun savePendingOperations() {
        // TODO: Implement persistent storage for sync queue
        // For now, operations are kept in memory
        Log.d(TAG, "Pending operations count: ${operationQueue.size}")
    }

    /**
     * Load pending operations from persistent storage
     */
    private fun loadPendingOperations() {
        // TODO: Load from persistent storage
        updatePendingCount()
    }

    /**
     * Resolve conflict between local and server data
     */
    fun resolveConflict(
        strategy: ConflictResolution,
        localTimestamp: Long,
        serverTimestamp: Long
    ): Boolean {
        return when (strategy) {
            ConflictResolution.LOCAL_WINS -> true
            ConflictResolution.SERVER_WINS -> false
            ConflictResolution.LAST_WRITE_WINS -> localTimestamp > serverTimestamp
            ConflictResolution.KEEP_BOTH -> true // Caller handles this case
        }
    }
}
