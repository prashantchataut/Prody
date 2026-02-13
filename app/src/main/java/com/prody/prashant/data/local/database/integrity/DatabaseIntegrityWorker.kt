package com.prody.prashant.data.local.database.integrity

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.prody.prashant.data.local.database.ProdyDatabase
import com.prody.prashant.data.security.SecureDatabaseManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DatabaseIntegrityWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val secureDatabaseManager: SecureDatabaseManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val appContext = applicationContext
        DatabaseIntegrityMonitor.markVerifying(appContext)

        return try {
            val isValid = secureDatabaseManager.verifyDatabaseIntegrity(
                appContext.getDatabasePath(ProdyDatabase.DATABASE_NAME)
            )
            if (isValid) {
                DatabaseIntegrityMonitor.markHealthy(appContext)
                Log.d(TAG, "Database integrity verified")
                Result.success()
            } else {
                DatabaseIntegrityMonitor.markDegraded(appContext)
                Log.e(TAG, "Database integrity failed, entering degraded mode")
                Result.success()
            }
        } catch (e: Exception) {
            DatabaseIntegrityMonitor.markDegraded(
                appContext,
                userMessage = "We hit a local storage issue. Prody is in safe mode until integrity checks pass."
            )
            Log.e(TAG, "Integrity verification crashed, degraded mode enabled", e)
            Result.success()
        }
    }

    companion object {
        private const val TAG = "DatabaseIntegrityWorker"
        private const val UNIQUE_WORK_NAME = "database_integrity_verification"

        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<DatabaseIntegrityWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag(UNIQUE_WORK_NAME)
                .build()

            WorkManager.getInstance(context.applicationContext)
                .enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.KEEP, request)
        }
    }
}

object DatabaseIntegrityStartupTask {
    fun schedule(context: Context, reason: String) {
        Log.d("DatabaseIntegrityStartup", "Scheduling database integrity verification: $reason")
        DatabaseIntegrityWorker.enqueue(context.applicationContext)
    }
}
