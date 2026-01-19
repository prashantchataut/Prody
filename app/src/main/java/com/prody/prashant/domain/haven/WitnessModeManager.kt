package com.prody.prashant.domain.haven

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.prody.prashant.MainActivity
import com.prody.prashant.ProdyApplication
import com.prody.prashant.R
import com.prody.prashant.data.local.dao.HavenMemoryDao
import com.prody.prashant.data.local.entity.HavenMemoryCategory
import com.prody.prashant.data.local.entity.HavenMemoryEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Witness Mode Manager - THE VAULT Callback System
 *
 * Haven's "Witness Mode" feature - remembers facts/truths from conversations
 * and proactively follows up with the user.
 *
 * "You said 'Exam on Friday'. It's Monday. Did you survive?"
 *
 * On app start, this manager:
 * 1. Checks for pending memories that need follow-up
 * 2. Sends notifications for memories where factDate was >3 days ago
 * 3. Updates memory status to prevent duplicate notifications
 *
 * This creates a powerful emotional experience: Haven remembers.
 */
@Singleton
class WitnessModeManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val havenMemoryDao: HavenMemoryDao
) {
    companion object {
        private const val TAG = "WitnessModeManager"
        private const val NOTIFICATION_ID_WITNESS_BASE = 9000
        private const val MAX_NOTIFICATIONS_PER_CHECK = 3 // Don't spam the user
    }

    /**
     * Check for pending memories and trigger follow-up notifications.
     * Should be called on app start (in ProdyApplication.initializeApp).
     */
    suspend fun checkForPendingFollowUps(userId: String = "local") = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Checking for pending memory follow-ups...")

            val pendingMemories = havenMemoryDao.getPendingMemoriesForFollowUp(userId)

            if (pendingMemories.isEmpty()) {
                Log.d(TAG, "No pending memories to follow up on")
                return@withContext
            }

            Log.d(TAG, "Found ${pendingMemories.size} memories ready for follow-up")

            // Process top N memories to avoid notification spam
            val memoriesToProcess = pendingMemories.take(MAX_NOTIFICATIONS_PER_CHECK)

            memoriesToProcess.forEachIndexed { index, memory ->
                try {
                    sendWitnessNotification(memory, NOTIFICATION_ID_WITNESS_BASE + index)

                    // Mark as followed up to prevent re-sending
                    havenMemoryDao.markAsFollowedUp(memory.id)

                    Log.d(TAG, "Sent follow-up notification for memory: ${memory.fact.take(50)}...")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to send notification for memory ${memory.id}", e)
                }
            }

            // Expire old memories that are too stale
            havenMemoryDao.expireOldMemories()

        } catch (e: Exception) {
            Log.e(TAG, "Error checking for pending follow-ups", e)
        }
    }

    /**
     * Send a witness mode notification for a memory.
     */
    private fun sendWitnessNotification(memory: HavenMemoryEntity, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        if (notificationManager == null) {
            Log.w(TAG, "NotificationManager not available")
            return
        }

        // Generate the notification message based on the memory
        val (title, body) = generateWitnessMessage(memory)

        // Create intent to open Haven when tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("destination", "haven")
            putExtra("witness_memory_id", memory.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ProdyApplication.CHANNEL_HAVEN)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    /**
     * Generate a witness message based on the memory.
     * Returns Pair<title, body>
     */
    private fun generateWitnessMessage(memory: HavenMemoryEntity): Pair<String, String> {
        val daysAgo = if (memory.factDate != null) {
            TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - memory.factDate).toInt()
        } else {
            TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - memory.createdAt).toInt()
        }

        val dayName = getCurrentDayName()
        val factPreview = memory.fact.take(100)

        // Category-specific messages
        val title = when (memory.category) {
            HavenMemoryCategory.EXAM -> "Haven remembers..."
            HavenMemoryCategory.DEADLINE -> "Haven noticed something..."
            HavenMemoryCategory.COMMITMENT -> "Haven is checking in..."
            HavenMemoryCategory.EVENT -> "Haven remembers..."
            HavenMemoryCategory.GOAL -> "Haven is curious..."
            HavenMemoryCategory.HEALTH -> "Haven is thinking of you..."
            HavenMemoryCategory.WORK -> "Haven remembered..."
            HavenMemoryCategory.RELATIONSHIP -> "Haven wanted to ask..."
            else -> "Haven remembers..."
        }

        val body = when (memory.category) {
            HavenMemoryCategory.EXAM -> {
                "You mentioned \"$factPreview\". It's $dayName. Did you survive? \uD83D\uDCAA"
            }
            HavenMemoryCategory.DEADLINE -> {
                "You had a deadline: \"$factPreview\". How did it go?"
            }
            HavenMemoryCategory.COMMITMENT -> {
                "You said \"$factPreview\". Did you follow through? I believe in you."
            }
            HavenMemoryCategory.EVENT -> {
                "That event you mentioned - \"$factPreview\". How was it?"
            }
            HavenMemoryCategory.GOAL -> {
                "You set a goal: \"$factPreview\". Any progress? I'm rooting for you."
            }
            HavenMemoryCategory.HEALTH -> {
                "You mentioned \"$factPreview\". How are you feeling now?"
            }
            HavenMemoryCategory.WORK -> {
                "That work thing - \"$factPreview\". Did it work out?"
            }
            HavenMemoryCategory.RELATIONSHIP -> {
                "You shared about \"$factPreview\". How did that go?"
            }
            else -> {
                "You mentioned \"$factPreview\" ${daysAgo} days ago. How did it go?"
            }
        }

        return Pair(title, body)
    }

    /**
     * Get the current day name (Monday, Tuesday, etc.)
     */
    private fun getCurrentDayName(): String {
        return SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
    }

    /**
     * Record user response to a follow-up.
     * Called when user interacts with Haven after receiving a witness notification.
     */
    suspend fun recordFollowUpResponse(
        memoryId: Long,
        outcome: String,
        response: String? = null
    ) = withContext(Dispatchers.IO) {
        try {
            havenMemoryDao.recordOutcome(memoryId, outcome, response)
            Log.d(TAG, "Recorded outcome for memory $memoryId: $outcome")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to record outcome for memory $memoryId", e)
        }
    }

    /**
     * Dismiss a memory (user doesn't want follow-up).
     */
    suspend fun dismissMemory(memoryId: Long) = withContext(Dispatchers.IO) {
        try {
            havenMemoryDao.dismissMemory(memoryId)
            Log.d(TAG, "Dismissed memory $memoryId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to dismiss memory $memoryId", e)
        }
    }

    /**
     * Get statistics about witness mode effectiveness.
     */
    suspend fun getWitnessStats(userId: String = "local"): WitnessModeStats = withContext(Dispatchers.IO) {
        try {
            val pendingCount = havenMemoryDao.getPendingMemoryCount(userId)
            val resolvedCount = havenMemoryDao.getResolvedMemoryCount(userId)
            val successRate = havenMemoryDao.getSuccessRate(userId) ?: 0f

            WitnessModeStats(
                pendingMemories = pendingCount,
                resolvedMemories = resolvedCount,
                successRate = successRate
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get witness stats", e)
            WitnessModeStats()
        }
    }
}

/**
 * Statistics for witness mode effectiveness.
 */
data class WitnessModeStats(
    val pendingMemories: Int = 0,
    val resolvedMemories: Int = 0,
    val successRate: Float = 0f
)
