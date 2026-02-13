package com.prody.prashant.data.backup

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.prody.prashant.BuildConfig
import com.prody.prashant.data.local.dao.*
import com.prody.prashant.data.local.preferences.PreferencesManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for creating and restoring backups of user data.
 * Supports JSON export/import functionality for all user content.
 */
@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val journalDao: JournalDao,
    private val futureMessageDao: FutureMessageDao,
    private val vocabularyDao: VocabularyDao,
    private val userDao: UserDao,
    private val preferencesManager: PreferencesManager
) {
    companion object {
        private const val TAG = "BackupManager"
    }

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /**
     * Creates a complete backup of all user data.
     * @return ProdyBackup object containing all user data
     */
    suspend fun createBackup(): ProdyBackup = withContext(Dispatchers.IO) {
        val journalEntries = journalDao.getAllEntriesSync().map { it.toBackup() }
        val futureMessages = futureMessageDao.getAllMessagesSync().map { it.toBackup() }
        val vocabularyProgress = vocabularyDao.getVocabularyProgressSync().map { it.toProgressBackup() }
        val userProfile = userDao.getUserProfileSync()?.toBackup()
        val streakHistory = userDao.getRecentStreakHistory(365).first().map { it.toBackup() }

        // Get current preferences
        val preferences = PreferencesBackup(
            themeMode = preferencesManager.themeMode.first(),
            dynamicColors = preferencesManager.dynamicColors.first(),
            notificationsEnabled = preferencesManager.notificationsEnabled.first(),
            dailyReminderHour = preferencesManager.dailyReminderHour.first(),
            dailyReminderMinute = preferencesManager.dailyReminderMinute.first(),
            wisdomNotificationEnabled = preferencesManager.wisdomNotificationEnabled.first(),
            journalReminderEnabled = preferencesManager.journalReminderEnabled.first(),
            vocabularyDifficulty = preferencesManager.vocabularyDifficulty.first(),
            autoPlayPronunciation = preferencesManager.autoPlayPronunciation.first(),
            compactCardView = preferencesManager.compactCardView.first(),
            hapticFeedbackEnabled = preferencesManager.hapticFeedbackEnabled.first()
        )

        ProdyBackup(
            version = ProdyBackup.BACKUP_VERSION,
            createdAt = System.currentTimeMillis(),
            appVersion = BuildConfig.VERSION_NAME,
            journalEntries = journalEntries,
            futureMessages = futureMessages,
            vocabularyProgress = vocabularyProgress,
            userProfile = userProfile,
            preferences = preferences,
            streakHistory = streakHistory
        )
    }

    /**
     * Exports backup to a JSON file.
     * @return Uri of the exported file, or null if export failed
     */
    suspend fun exportBackup(): Uri? = withContext(Dispatchers.IO) {
        try {
            val backup = createBackup()
            val jsonString = json.encodeToString(backup)

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "prody_backup_$timestamp.json"

            val backupDir = File(context.cacheDir, "backups")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            val backupFile = File(backupDir, fileName)
            backupFile.writeText(jsonString)

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                backupFile
            )
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to export backup", e)
            null
        }
    }

    /**
     * Creates an Intent to share the backup file.
     */
    suspend fun createShareIntent(): Intent? = withContext(Dispatchers.IO) {
        val uri = exportBackup() ?: return@withContext null

        Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    /**
     * Restores data from a backup.
     * @param inputStream InputStream containing the backup JSON
     * @param restoreOptions Options specifying what to restore
     * @return RestoreResult indicating success or failure
     */
    suspend fun restoreBackup(
        inputStream: InputStream,
        restoreOptions: RestoreOptions = RestoreOptions()
    ): RestoreResult = withContext(Dispatchers.IO) {
        try {
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val backup = json.decodeFromString<ProdyBackup>(jsonString)

            // Version check
            if (backup.version > ProdyBackup.BACKUP_VERSION) {
                return@withContext RestoreResult.Error("Backup version is newer than app version. Please update the app.")
            }

            var entriesRestored = 0
            var messagesRestored = 0
            var vocabularyRestored = 0

            // Restore journal entries
            if (restoreOptions.restoreJournalEntries && backup.journalEntries.isNotEmpty()) {
                if (restoreOptions.clearExistingData) {
                    journalDao.deleteAllEntries()
                }
                val entries = backup.journalEntries.map { it.toEntity() }
                journalDao.insertEntries(entries)
                entriesRestored = entries.size
            }

            // Restore future messages
            if (restoreOptions.restoreFutureMessages && backup.futureMessages.isNotEmpty()) {
                if (restoreOptions.clearExistingData) {
                    futureMessageDao.deleteAllMessages()
                }
                val messages = backup.futureMessages.map { it.toEntity() }
                futureMessageDao.insertMessages(messages)
                messagesRestored = messages.size
            }

            // Restore vocabulary progress (update existing words with progress)
            if (restoreOptions.restoreVocabularyProgress && backup.vocabularyProgress.isNotEmpty()) {
                for (progress in backup.vocabularyProgress) {
                    // Find existing word by name and update progress
                    val existingWord = vocabularyDao.getWordByName(progress.word)
                    if (existingWord != null) {
                        vocabularyDao.updateWord(
                            existingWord.copy(
                                isLearned = progress.isLearned,
                                learnedAt = progress.learnedAt,
                                reviewCount = progress.reviewCount,
                                lastReviewedAt = progress.lastReviewedAt,
                                nextReviewAt = progress.nextReviewAt,
                                masteryLevel = progress.masteryLevel,
                                isFavorite = progress.isFavorite
                            )
                        )
                        vocabularyRestored++
                    }
                }
            }

            // Restore user profile
            if (restoreOptions.restoreUserProfile && backup.userProfile != null) {
                userDao.insertUserProfile(backup.userProfile.toEntity())
            }

            // Restore preferences
            if (restoreOptions.restorePreferences && backup.preferences != null) {
                with(backup.preferences) {
                    preferencesManager.setThemeMode(themeMode)
                    preferencesManager.setDynamicColors(dynamicColors)
                    preferencesManager.setNotificationsEnabled(notificationsEnabled)
                    preferencesManager.setDailyReminderTime(dailyReminderHour, dailyReminderMinute)
                    preferencesManager.setWisdomNotificationEnabled(wisdomNotificationEnabled)
                    preferencesManager.setJournalReminderEnabled(journalReminderEnabled)
                    preferencesManager.setVocabularyDifficulty(vocabularyDifficulty)
                    preferencesManager.setAutoPlayPronunciation(autoPlayPronunciation)
                    preferencesManager.setCompactCardView(compactCardView)
                    preferencesManager.setHapticFeedbackEnabled(hapticFeedbackEnabled)
                }
            }

            // Restore streak history
            if (restoreOptions.restoreStreakHistory && backup.streakHistory.isNotEmpty()) {
                for (streak in backup.streakHistory) {
                    userDao.insertStreakHistory(streak.toEntity())
                }
            }

            RestoreResult.Success(
                entriesRestored = entriesRestored,
                messagesRestored = messagesRestored,
                vocabularyProgressRestored = vocabularyRestored,
                backupDate = backup.createdAt
            )
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to restore backup", e)
            RestoreResult.Error("Failed to restore backup: ${e.message}")
        }
    }

    /**
     * Validates a backup file without restoring it.
     * @return BackupInfo if valid, null otherwise
     */
    suspend fun validateBackup(inputStream: InputStream): BackupInfo? = withContext(Dispatchers.IO) {
        try {
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val backup = json.decodeFromString<ProdyBackup>(jsonString)

            BackupInfo(
                version = backup.version,
                createdAt = backup.createdAt,
                appVersion = backup.appVersion,
                journalEntriesCount = backup.journalEntries.size,
                futureMessagesCount = backup.futureMessages.size,
                vocabularyProgressCount = backup.vocabularyProgress.size,
                hasUserProfile = backup.userProfile != null,
                hasPreferences = backup.preferences != null,
                streakHistoryCount = backup.streakHistory.size
            )
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to validate backup", e)
            null
        }
    }

    /**
     * Gets the size of data that would be backed up.
     */
    suspend fun getBackupStats(): BackupStats = withContext(Dispatchers.IO) {
        BackupStats(
            journalEntriesCount = journalDao.getAllEntriesSync().size,
            futureMessagesCount = futureMessageDao.getAllMessagesSync().size,
            vocabularyProgressCount = vocabularyDao.getVocabularyProgressSync().size
        )
    }

    /**
     * Cleans up old backup files from cache.
     */
    suspend fun cleanupOldBackups(keepCount: Int = 5) = withContext(Dispatchers.IO) {
        val backupDir = File(context.cacheDir, "backups")
        if (backupDir.exists()) {
            val files = backupDir.listFiles { _, name -> name.startsWith("prody_backup_") && name.endsWith(".json") }
                ?.sortedByDescending { it.lastModified() }
                ?: return@withContext

            if (files.size > keepCount) {
                files.drop(keepCount).forEach { it.delete() }
            }
        }
    }
}

/**
 * Options for customizing what data to restore.
 */
data class RestoreOptions(
    val restoreJournalEntries: Boolean = true,
    val restoreFutureMessages: Boolean = true,
    val restoreVocabularyProgress: Boolean = true,
    val restoreUserProfile: Boolean = true,
    val restorePreferences: Boolean = true,
    val restoreStreakHistory: Boolean = true,
    val clearExistingData: Boolean = false
)

/**
 * Result of a restore operation.
 */
sealed class RestoreResult {
    data class Success(
        val entriesRestored: Int,
        val messagesRestored: Int,
        val vocabularyProgressRestored: Int,
        val backupDate: Long
    ) : RestoreResult()

    data class Error(val message: String) : RestoreResult()
}

/**
 * Information about a backup file.
 */
data class BackupInfo(
    val version: Int,
    val createdAt: Long,
    val appVersion: String,
    val journalEntriesCount: Int,
    val futureMessagesCount: Int,
    val vocabularyProgressCount: Int,
    val hasUserProfile: Boolean,
    val hasPreferences: Boolean,
    val streakHistoryCount: Int
)

/**
 * Statistics about current data that would be backed up.
 */
data class BackupStats(
    val journalEntriesCount: Int,
    val futureMessagesCount: Int,
    val vocabularyProgressCount: Int
)
