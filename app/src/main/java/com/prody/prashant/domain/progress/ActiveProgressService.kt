package com.prody.prashant.domain.progress

import android.util.Log
import androidx.compose.runtime.Immutable
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.data.local.dao.FutureMessageDao
import com.prody.prashant.data.local.dao.QuoteDao
import com.prody.prashant.data.ai.BuddhaAiRepository
import com.prody.prashant.data.ai.BuddhaAiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Active Progress Service - The brain behind "Active Progress Layer"
 *
 * This service produces:
 * 1. Next Action suggestions based on real user behavior
 * 2. Progress Feedback summaries (Today's Progress, This Week)
 * 3. Data for Seed -> Bloom mechanic integration
 *
 * Rules:
 * - No generic suggestions - must be driven by current local user data
 * - Next Action must be contextual and simple
 * - Progress must never feel passive
 */
@Singleton
class ActiveProgressService @Inject constructor(
    private val journalDao: JournalDao,
    private val userDao: UserDao,
    private val vocabularyDao: VocabularyDao,
    private val futureMessageDao: FutureMessageDao,
    private val quoteDao: QuoteDao,
    private val buddhaAiRepository: BuddhaAiRepository
) {
    companion object {
        private const val TAG = "ActiveProgressService"
    }

    /**
     * Get the contextual "Next Action" suggestion based on user behavior.
     * This is the core of making progress feel active, not passive.
     */
    suspend fun getNextAction(): NextAction {
        return try {
            val todayStart = getTodayStartTimestamp()
            val weekStart = getWeekStartTimestamp()

            // Gather user activity data
            val journaledToday = journalDao.getEntryCountSince(todayStart) > 0
            val journalsThisWeek = journalDao.getEntryCountSince(weekStart)
            val wordsLearned = vocabularyDao.getLearnedCountSinceSync(weekStart)
            val pendingReviews = vocabularyDao.getPendingReviewCount()
            val futureMessagesSent = futureMessageDao.getMessageCountSince(todayStart)
            val totalFutureMessages = futureMessageDao.getTotalMessageCount()
            val savedQuotes = quoteDao.getSavedQuoteCount()
            val profile = userDao.getUserProfileSync()
            val streak = profile?.currentStreak ?: 0

            // Decision logic - prioritize based on what makes sense
            when {
                // If user journaled today, suggest a follow-up
                journaledToday && journalsThisWeek >= 2 -> {
                    NextAction(
                        type = NextActionType.FOLLOW_UP_JOURNAL,
                        title = "Write a short follow-up",
                        subtitle = "Capture how your day evolved",
                        actionRoute = "journal/new",
                        priority = 2
                    )
                }

                // If vocabulary is lagging (pending reviews)
                pendingReviews > 0 -> {
                    NextAction(
                        type = NextActionType.REVIEW_WORDS,
                        title = "Review ${pendingReviews.coerceAtMost(10)} words",
                        subtitle = "Strengthen your vocabulary",
                        actionRoute = "vocabulary",
                        priority = 3
                    )
                }

                // If never used future messages
                totalFutureMessages == 0 -> {
                    NextAction(
                        type = NextActionType.WRITE_FUTURE_MESSAGE,
                        title = "Write to future you",
                        subtitle = "Send encouragement to tomorrow",
                        actionRoute = "future_message/write",
                        priority = 4
                    )
                }

                // If saved quotes but never reflected
                savedQuotes > 0 && profile?.quotesReflected == 0 -> {
                    NextAction(
                        type = NextActionType.REFLECT_ON_QUOTE,
                        title = "Reflect on saved wisdom",
                        subtitle = "Apply what you've collected",
                        actionRoute = "quotes",
                        priority = 5
                    )
                }

                // If haven't journaled today
                !journaledToday -> {
                    val timeOfDay = getTimeOfDayLabel()
                    NextAction(
                        type = NextActionType.START_JOURNAL,
                        title = "Start your $timeOfDay reflection",
                        subtitle = getJournalSubtitle(streak),
                        actionRoute = "journal/new",
                        priority = 1
                    )
                }

                // Default: Learn a new word
                else -> {
                    NextAction(
                        type = NextActionType.LEARN_WORD,
                        title = "Learn today's word",
                        subtitle = "Expand your vocabulary",
                        actionRoute = "vocabulary",
                        priority = 6
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting next action", e)
            // Fallback to safe default
            NextAction(
                type = NextActionType.START_JOURNAL,
                title = "Start your reflection",
                subtitle = "Take a moment for yourself",
                actionRoute = "journal/new",
                priority = 1
            )
        }
    }

    /**
     * Get Today's Progress summary - what the user accomplished today.
     * Must never be blank - show designed empty state if needed.
     */
    suspend fun getTodayProgress(): TodayProgress {
        return try {
            val todayStart = getTodayStartTimestamp()

            val journalEntries = journalDao.getEntryCountSince(todayStart)
            val wordsLearned = vocabularyDao.getLearnedCountTodaySync(todayStart)
            val wordCount = journalDao.getTotalWordCountSince(todayStart)
            val profile = userDao.getUserProfileSync()
            val pointsToday = profile?.let {
                // Calculate approximate points earned today
                (journalEntries * 50) + (wordsLearned * 25)
            } ?: 0

            TodayProgress(
                journalEntries = journalEntries,
                wordsWritten = wordCount,
                wordsLearned = wordsLearned,
                pointsEarned = pointsToday,
                currentStreak = profile?.currentStreak ?: 0,
                isEmpty = journalEntries == 0 && wordsLearned == 0
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting today's progress", e)
            TodayProgress(isEmpty = true)
        }
    }

    /**
     * Get This Week's Progress summary.
     */
    suspend fun getWeeklyProgress(): WeeklyProgress {
        return try {
            val weekStart = getWeekStartTimestamp()

            val journalEntries = journalDao.getEntryCountSince(weekStart)
            val wordsLearned = vocabularyDao.getLearnedCountSinceSync(weekStart)
            val totalWordCount = journalDao.getTotalWordCountSince(weekStart)
            val daysActive = journalDao.getActiveDaysCountSince(weekStart)
            val profile = userDao.getUserProfileSync()

            // Get weekly patterns if available
            val weeklyInsight = try {
                val result = buddhaAiRepository.getWeeklyPatterns(
                    journalCount = journalEntries,
                    dominantMood = null, // Could be enhanced to pass actual mood
                    themes = emptyList(),
                    moodTrend = "stable",
                    activeTimeOfDay = getTimeOfDayLabel(),
                    streakDays = profile?.currentStreak ?: 0
                )
                when (result) {
                    is BuddhaAiResult.Success -> result.data.suggestion
                    is BuddhaAiResult.Fallback -> result.data.suggestion
                    is BuddhaAiResult.Error -> null
                }
            } catch (e: Exception) {
                null
            }

            WeeklyProgress(
                journalEntries = journalEntries,
                wordsWritten = totalWordCount,
                wordsLearned = wordsLearned,
                daysActive = daysActive,
                currentStreak = profile?.currentStreak ?: 0,
                longestStreak = profile?.longestStreak ?: 0,
                weeklyInsight = weeklyInsight
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting weekly progress", e)
            WeeklyProgress()
        }
    }

    /**
     * Generate progress feedback message after an action.
     * This is the "compact feedback moment" shown after meaningful actions.
     */
    fun getProgressFeedback(actionType: ActionType, value: Int = 0): ProgressFeedback {
        return when (actionType) {
            ActionType.JOURNAL_SAVED -> {
                ProgressFeedback(
                    title = "Entry saved",
                    message = "+50 points",
                    detail = if (value > 100) "You wrote $value words today" else null,
                    type = FeedbackType.SUCCESS
                )
            }
            ActionType.WORD_LEARNED -> {
                ProgressFeedback(
                    title = "Word learned",
                    message = "+25 points",
                    detail = "You've learned $value words this week",
                    type = FeedbackType.SUCCESS
                )
            }
            ActionType.FUTURE_MESSAGE_SCHEDULED -> {
                ProgressFeedback(
                    title = "Message scheduled",
                    message = "+50 points",
                    detail = "Your future self will thank you",
                    type = FeedbackType.SUCCESS
                )
            }
            ActionType.FLASHCARD_SESSION -> {
                ProgressFeedback(
                    title = "Session complete",
                    message = "+$value points",
                    detail = null,
                    type = FeedbackType.SUCCESS
                )
            }
            ActionType.STREAK_EXTENDED -> {
                ProgressFeedback(
                    title = "Streak extended",
                    message = "$value day${if (value > 1) "s" else ""} strong",
                    detail = null,
                    type = FeedbackType.CELEBRATION
                )
            }
            ActionType.SEED_BLOOMED -> {
                ProgressFeedback(
                    title = "Seed bloomed",
                    message = "Wisdom applied",
                    detail = "+25 bonus points",
                    type = FeedbackType.CELEBRATION
                )
            }
        }
    }

    // ================= HELPERS =================

    private fun getTodayStartTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getWeekStartTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getTimeOfDayLabel(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "morning"
            hour < 17 -> "afternoon"
            else -> "evening"
        }
    }

    private fun getJournalSubtitle(streak: Int): String {
        return when {
            streak >= 7 -> "Keep your $streak-day streak alive"
            streak >= 3 -> "You're on a roll - day ${streak + 1} awaits"
            streak > 0 -> "Continue your ${streak}-day momentum"
            else -> "A few minutes of clarity"
        }
    }
}

// ================= DATA CLASSES =================

@Immutable
data class NextAction(
    val type: NextActionType,
    val title: String,
    val subtitle: String,
    val actionRoute: String,
    val priority: Int = 0
)

enum class NextActionType {
    START_JOURNAL,
    FOLLOW_UP_JOURNAL,
    REVIEW_WORDS,
    LEARN_WORD,
    WRITE_FUTURE_MESSAGE,
    REFLECT_ON_QUOTE,
    COMPLETE_CHALLENGE
}

@Immutable
data class TodayProgress(
    val journalEntries: Int = 0,
    val wordsWritten: Int = 0,
    val wordsLearned: Int = 0,
    val pointsEarned: Int = 0,
    val currentStreak: Int = 0,
    val isEmpty: Boolean = false
)

@Immutable
data class WeeklyProgress(
    val journalEntries: Int = 0,
    val wordsWritten: Int = 0,
    val wordsLearned: Int = 0,
    val daysActive: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val weeklyInsight: String? = null
)

data class ProgressFeedback(
    val title: String,
    val message: String,
    val detail: String? = null,
    val type: FeedbackType = FeedbackType.SUCCESS
)

enum class FeedbackType {
    SUCCESS,
    CELEBRATION,
    INFO
}

enum class ActionType {
    JOURNAL_SAVED,
    WORD_LEARNED,
    FUTURE_MESSAGE_SCHEDULED,
    FLASHCARD_SESSION,
    STREAK_EXTENDED,
    SEED_BLOOMED
}
