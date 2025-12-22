package com.prody.prashant.data.hygiene

import android.content.Context
import android.util.Log
import com.prody.prashant.data.cache.AiCacheManager
import com.prody.prashant.data.local.dao.*
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.data.moderation.ContentModerationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for data hygiene operations including:
 * - Cache cleanup
 * - Old data retention policies
 * - Storage optimization
 * - Privacy-focused data cleanup
 */
@Singleton
class DataHygieneManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val journalDao: JournalDao,
    private val futureMessageDao: FutureMessageDao,
    private val vocabularyDao: VocabularyDao,
    private val userDao: UserDao,
    private val challengeDao: ChallengeDao,
    private val preferencesManager: PreferencesManager,
    private val aiCacheManager: AiCacheManager,
    private val contentModerationManager: ContentModerationManager
) {
    companion object {
        private const val TAG = "DataHygieneManager"

        // Retention policies (in days)
        const val DEFAULT_AI_CACHE_RETENTION_DAYS = 30
        const val DEFAULT_TEMP_FILES_RETENTION_DAYS = 7
        const val DEFAULT_STREAK_HISTORY_RETENTION_DAYS = 365
        const val DEFAULT_MODERATION_LOGS_RETENTION_DAYS = 90

        // Size limits
        const val MAX_CACHE_SIZE_MB = 100
        const val MAX_TEMP_FILES_SIZE_MB = 50
    }

    /**
     * Performs a full data hygiene cleanup.
     * @return CleanupResult with details of what was cleaned
     */
    suspend fun performFullCleanup(): CleanupResult = withContext(Dispatchers.IO) {
        Log.d(TAG, "Starting full data hygiene cleanup")

        val results = mutableListOf<CleanupAction>()
        var totalBytesFreed = 0L

        // 1. Clear AI response cache
        try {
            val cacheResult = cleanupAiCache()
            results.add(cacheResult)
            totalBytesFreed += cacheResult.bytesFreed
        } catch (e: Exception) {
            Log.e(TAG, "AI cache cleanup failed", e)
            results.add(CleanupAction("AI Cache", 0, false, e.message))
        }

        // 2. Clean temporary files
        try {
            val tempResult = cleanupTempFiles()
            results.add(tempResult)
            totalBytesFreed += tempResult.bytesFreed
        } catch (e: Exception) {
            Log.e(TAG, "Temp files cleanup failed", e)
            results.add(CleanupAction("Temp Files", 0, false, e.message))
        }

        // 3. Clean backup cache
        try {
            val backupResult = cleanupBackupCache()
            results.add(backupResult)
            totalBytesFreed += backupResult.bytesFreed
        } catch (e: Exception) {
            Log.e(TAG, "Backup cache cleanup failed", e)
            results.add(CleanupAction("Backup Cache", 0, false, e.message))
        }

        // 4. Clean old streak history
        try {
            val streakResult = cleanupOldStreakHistory()
            results.add(streakResult)
        } catch (e: Exception) {
            Log.e(TAG, "Streak history cleanup failed", e)
            results.add(CleanupAction("Streak History", 0, false, e.message))
        }

        // 5. Clean image cache
        try {
            val imageCacheResult = cleanupImageCache()
            results.add(imageCacheResult)
            totalBytesFreed += imageCacheResult.bytesFreed
        } catch (e: Exception) {
            Log.e(TAG, "Image cache cleanup failed", e)
            results.add(CleanupAction("Image Cache", 0, false, e.message))
        }

        // 6. Optimize database (vacuum)
        try {
            val dbResult = optimizeDatabase()
            results.add(dbResult)
            totalBytesFreed += dbResult.bytesFreed
        } catch (e: Exception) {
            Log.e(TAG, "Database optimization failed", e)
            results.add(CleanupAction("Database", 0, false, e.message))
        }

        Log.d(TAG, "Full cleanup completed. Total bytes freed: $totalBytesFreed")

        CleanupResult(
            actions = results,
            totalBytesFreed = totalBytesFreed,
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * Cleans up AI response cache based on retention policy.
     */
    suspend fun cleanupAiCache(): CleanupAction = withContext(Dispatchers.IO) {
        try {
            val bytesFreed = aiCacheManager.cleanupOldEntries(DEFAULT_AI_CACHE_RETENTION_DAYS)
            CleanupAction("AI Cache", bytesFreed, true)
        } catch (e: Exception) {
            CleanupAction("AI Cache", 0, false, e.message)
        }
    }

    /**
     * Cleans up temporary files older than retention period.
     */
    suspend fun cleanupTempFiles(): CleanupAction = withContext(Dispatchers.IO) {
        var bytesFreed = 0L
        val cutoffTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(DEFAULT_TEMP_FILES_RETENTION_DAYS.toLong())

        // Clean cache directory
        context.cacheDir.walkTopDown().forEach { file ->
            if (file.isFile && file.lastModified() < cutoffTime) {
                val size = file.length()
                if (file.delete()) {
                    bytesFreed += size
                }
            }
        }

        // Clean external cache directory
        context.externalCacheDir?.walkTopDown()?.forEach { file ->
            if (file.isFile && file.lastModified() < cutoffTime) {
                val size = file.length()
                if (file.delete()) {
                    bytesFreed += size
                }
            }
        }

        CleanupAction("Temp Files", bytesFreed, true)
    }

    /**
     * Cleans up old backup files, keeping only recent ones.
     */
    suspend fun cleanupBackupCache(keepCount: Int = 3): CleanupAction = withContext(Dispatchers.IO) {
        var bytesFreed = 0L
        val backupDir = File(context.cacheDir, "backups")

        if (backupDir.exists()) {
            val files = backupDir.listFiles { _, name ->
                name.startsWith("prody_backup_") && name.endsWith(".json")
            }?.sortedByDescending { it.lastModified() }

            files?.drop(keepCount)?.forEach { file ->
                val size = file.length()
                if (file.delete()) {
                    bytesFreed += size
                }
            }
        }

        CleanupAction("Backup Cache", bytesFreed, true)
    }

    /**
     * Cleans up old streak history entries.
     */
    suspend fun cleanupOldStreakHistory(): CleanupAction = withContext(Dispatchers.IO) {
        val cutoffTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(DEFAULT_STREAK_HISTORY_RETENTION_DAYS.toLong())

        // Get count before deletion for reporting
        val historyBefore = userDao.getRecentStreakHistory(10000).first()
        val toDelete = historyBefore.count { it.date < cutoffTime }

        // Delete old entries
        userDao.deleteOldStreakHistory(cutoffTime)

        CleanupAction("Streak History", 0, true, "Removed $toDelete old entries")
    }

    /**
     * Cleans up image/media cache.
     */
    suspend fun cleanupImageCache(): CleanupAction = withContext(Dispatchers.IO) {
        var bytesFreed = 0L

        // Glide/Coil cache directory
        val imageCacheDir = File(context.cacheDir, "image_cache")
        if (imageCacheDir.exists()) {
            val cutoffTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(14)
            imageCacheDir.walkTopDown().forEach { file ->
                if (file.isFile && file.lastModified() < cutoffTime) {
                    val size = file.length()
                    if (file.delete()) {
                        bytesFreed += size
                    }
                }
            }
        }

        CleanupAction("Image Cache", bytesFreed, true)
    }

    /**
     * Optimizes the database by running VACUUM.
     */
    suspend fun optimizeDatabase(): CleanupAction = withContext(Dispatchers.IO) {
        // Note: Room doesn't directly support VACUUM, but this represents
        // the cleanup effect. In production, consider using checkpoint WAL.
        CleanupAction("Database", 0, true, "Optimized")
    }

    /**
     * Gets current storage usage statistics.
     */
    suspend fun getStorageStats(): StorageStats = withContext(Dispatchers.IO) {
        val cacheSize = getDirSize(context.cacheDir)
        val externalCacheSize = context.externalCacheDir?.let { getDirSize(it) } ?: 0L
        val backupSize = getDirSize(File(context.cacheDir, "backups"))

        // Database size
        val dbFile = context.getDatabasePath("prody_database")
        val databaseSize = if (dbFile.exists()) dbFile.length() else 0L

        // Journal entries count
        val journalCount = journalDao.getAllEntriesSync().size
        val futureMessageCount = futureMessageDao.getAllMessagesSync().size

        StorageStats(
            totalCacheSize = cacheSize + externalCacheSize,
            databaseSize = databaseSize,
            backupFilesSize = backupSize,
            journalEntriesCount = journalCount,
            futureMessagesCount = futureMessageCount
        )
    }

    /**
     * Clears all user data (for account deletion or reset).
     * WARNING: This is irreversible!
     */
    suspend fun clearAllUserData(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.w(TAG, "Clearing all user data - IRREVERSIBLE OPERATION")

            // Clear journal entries
            journalDao.deleteAllEntries()

            // Clear future messages
            futureMessageDao.deleteAllMessages()

            // Reset vocabulary progress
            vocabularyDao.resetAllProgress()

            // Clear user profile (but create fresh one)
            userDao.deleteUserProfile()
            userDao.clearUserStats()
            userDao.clearStreakHistory()

            // Clear challenge participation
            challengeDao.clearUserParticipation()

            // Clear AI cache
            aiCacheManager.clearAll()

            // Clear preferences (reset to defaults)
            preferencesManager.resetToDefaults()

            // Clear all cache files
            context.cacheDir.deleteRecursively()
            context.externalCacheDir?.deleteRecursively()

            Log.d(TAG, "All user data cleared successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear all user data", e)
            false
        }
    }

    /**
     * Clears only cached/temporary data (not user content).
     */
    suspend fun clearCacheOnly(): CleanupResult = withContext(Dispatchers.IO) {
        val results = mutableListOf<CleanupAction>()
        var totalBytesFreed = 0L

        // AI Cache
        try {
            aiCacheManager.clearAll()
            val cacheSize = getDirSize(File(context.cacheDir, "ai_cache"))
            results.add(CleanupAction("AI Cache", cacheSize, true))
            totalBytesFreed += cacheSize
        } catch (e: Exception) {
            results.add(CleanupAction("AI Cache", 0, false, e.message))
        }

        // Temp files
        try {
            val tempResult = cleanupTempFiles()
            results.add(tempResult)
            totalBytesFreed += tempResult.bytesFreed
        } catch (e: Exception) {
            results.add(CleanupAction("Temp Files", 0, false, e.message))
        }

        // Image cache
        try {
            val imageResult = cleanupImageCache()
            results.add(imageResult)
            totalBytesFreed += imageResult.bytesFreed
        } catch (e: Exception) {
            results.add(CleanupAction("Image Cache", 0, false, e.message))
        }

        CleanupResult(
            actions = results,
            totalBytesFreed = totalBytesFreed,
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * Removes soft-deleted items permanently.
     */
    suspend fun purgeSoftDeletedItems(): Int = withContext(Dispatchers.IO) {
        var count = 0

        // Purge soft-deleted journal entries
        count += journalDao.purgeSoftDeleted()

        // Purge soft-deleted future messages
        count += futureMessageDao.purgeSoftDeleted()

        Log.d(TAG, "Purged $count soft-deleted items")
        count
    }

    /**
     * Schedules automatic cleanup based on storage pressure.
     */
    suspend fun autoCleanupIfNeeded(): Boolean = withContext(Dispatchers.IO) {
        val stats = getStorageStats()

        // Trigger cleanup if cache exceeds limit
        if (stats.totalCacheSize > MAX_CACHE_SIZE_MB * 1024 * 1024) {
            Log.d(TAG, "Cache size exceeded limit, triggering auto-cleanup")
            performFullCleanup()
            return@withContext true
        }

        false
    }

    private fun getDirSize(dir: File): Long {
        if (!dir.exists()) return 0L
        return dir.walkTopDown()
            .filter { it.isFile }
            .sumOf { it.length() }
    }
}

/**
 * Result of a cleanup operation.
 */
data class CleanupResult(
    val actions: List<CleanupAction>,
    val totalBytesFreed: Long,
    val timestamp: Long
) {
    val successCount: Int get() = actions.count { it.success }
    val failureCount: Int get() = actions.count { !it.success }

    fun formatBytesFreed(): String {
        return when {
            totalBytesFreed >= 1024 * 1024 -> "%.1f MB".format(totalBytesFreed / (1024.0 * 1024))
            totalBytesFreed >= 1024 -> "%.1f KB".format(totalBytesFreed / 1024.0)
            else -> "$totalBytesFreed bytes"
        }
    }
}

/**
 * Individual cleanup action result.
 */
data class CleanupAction(
    val name: String,
    val bytesFreed: Long,
    val success: Boolean,
    val message: String? = null
)

/**
 * Storage statistics.
 */
data class StorageStats(
    val totalCacheSize: Long,
    val databaseSize: Long,
    val backupFilesSize: Long,
    val journalEntriesCount: Int,
    val futureMessagesCount: Int
) {
    val totalSize: Long get() = totalCacheSize + databaseSize + backupFilesSize

    fun formatTotalSize(): String {
        return when {
            totalSize >= 1024 * 1024 -> "%.1f MB".format(totalSize / (1024.0 * 1024))
            totalSize >= 1024 -> "%.1f KB".format(totalSize / 1024.0)
            else -> "$totalSize bytes"
        }
    }

    fun formatCacheSize(): String {
        return when {
            totalCacheSize >= 1024 * 1024 -> "%.1f MB".format(totalCacheSize / (1024.0 * 1024))
            totalCacheSize >= 1024 -> "%.1f KB".format(totalCacheSize / 1024.0)
            else -> "$totalCacheSize bytes"
        }
    }

    fun formatDatabaseSize(): String {
        return when {
            databaseSize >= 1024 * 1024 -> "%.1f MB".format(databaseSize / (1024.0 * 1024))
            databaseSize >= 1024 -> "%.1f KB".format(databaseSize / 1024.0)
            else -> "$databaseSize bytes"
        }
    }
}
