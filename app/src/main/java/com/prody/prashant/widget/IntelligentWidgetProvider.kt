package com.prody.prashant.widget

import android.content.Context
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.preferences.PreferencesManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ================================================================================================
 * INTELLIGENT WIDGET PROVIDER
 * ================================================================================================
 *
 * Provides Soul Layer intelligence for app widgets.
 * Widgets can use this to display context-aware content including:
 * - Time-of-day appropriate greetings and prompts
 * - User state awareness (struggling, thriving, returning)
 * - Memory surfacing hints
 * - Personalized encouragement
 *
 * This is a lightweight version of the full Soul Layer that can run
 * in widget contexts without heavy dependencies.
 */
@Singleton
class IntelligentWidgetProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager,
    private val journalDao: JournalDao,
    private val userDao: UserDao
) {

    companion object {
        private const val TAG = "IntelligentWidgetProvider"
    }

    // =============================================================================================
    // INTELLIGENT PROMPTS
    // =============================================================================================

    /**
     * Get a context-aware journal prompt for the Quick Journal widget.
     * Considers time of day and user's recent activity.
     */
    suspend fun getIntelligentJournalPrompt(): WidgetJournalPrompt = withContext(Dispatchers.IO) {
        val timeOfDay = getCurrentTimeOfDay()
        val daysSinceLastEntry = getDaysSinceLastEntry()
        val userName = getUserName()

        // Determine prompt based on context
        val prompt = when {
            // Returning user after absence
            daysSinceLastEntry > 3 -> getReturningUserPrompt(daysSinceLastEntry)
            // Time-based prompts
            else -> getTimeBasedPrompt(timeOfDay)
        }

        WidgetJournalPrompt(
            prompt = prompt,
            subtext = getPromptSubtext(timeOfDay, userName),
            icon = getPromptIcon(timeOfDay),
            isPersonalized = daysSinceLastEntry > 3 || userName != null
        )
    }

    /**
     * Get a context-aware greeting for widgets.
     */
    suspend fun getIntelligentGreeting(): WidgetGreeting = withContext(Dispatchers.IO) {
        val timeOfDay = getCurrentTimeOfDay()
        val userName = getUserName()
        val daysSinceLastEntry = getDaysSinceLastEntry()

        val greeting = when (timeOfDay) {
            TimeOfDay.EARLY_MORNING -> if (userName != null) "Early bird, $userName" else "Early riser"
            TimeOfDay.MORNING -> if (userName != null) "Good morning, $userName" else "Good morning"
            TimeOfDay.LATE_MORNING -> if (userName != null) "Hello, $userName" else "Hello"
            TimeOfDay.AFTERNOON -> if (userName != null) "Good afternoon, $userName" else "Good afternoon"
            TimeOfDay.EVENING -> if (userName != null) "Good evening, $userName" else "Good evening"
            TimeOfDay.NIGHT -> if (userName != null) "Night owl, $userName" else "Night owl"
            TimeOfDay.LATE_NIGHT -> if (userName != null) "Still up, $userName?" else "Still up?"
        }

        val subtext = when {
            daysSinceLastEntry > 7 -> "We've missed you"
            daysSinceLastEntry > 3 -> "Welcome back"
            else -> getTimeBasedSubtext(timeOfDay)
        }

        WidgetGreeting(
            greeting = greeting,
            subtext = subtext,
            emoji = getTimeEmoji(timeOfDay)
        )
    }

    /**
     * Get an intelligent quote introduction based on context.
     */
    suspend fun getQuoteIntroduction(): String = withContext(Dispatchers.IO) {
        val timeOfDay = getCurrentTimeOfDay()
        when (timeOfDay) {
            TimeOfDay.EARLY_MORNING -> "Start your day with wisdom"
            TimeOfDay.MORNING -> "Morning wisdom"
            TimeOfDay.LATE_MORNING -> "Midday reflection"
            TimeOfDay.AFTERNOON -> "Afternoon inspiration"
            TimeOfDay.EVENING -> "Evening reflection"
            TimeOfDay.NIGHT -> "Night thoughts"
            TimeOfDay.LATE_NIGHT -> "Late night wisdom"
        }
    }

    /**
     * Get streak encouragement message.
     */
    suspend fun getStreakMessage(currentStreak: Int): WidgetStreakMessage = withContext(Dispatchers.IO) {
        val userName = getUserName()

        val (title, message, emoji) = when {
            currentStreak == 0 -> Triple(
                "Start fresh",
                "Today's a great day to begin",
                "\uD83C\uDF31" // seedling
            )
            currentStreak == 1 -> Triple(
                "Day 1",
                "Every journey starts here",
                "\u2728" // sparkles
            )
            currentStreak in 2..6 -> Triple(
                "$currentStreak days",
                "You're building momentum",
                "\uD83D\uDD25" // fire
            )
            currentStreak == 7 -> Triple(
                "1 week!",
                "A whole week of growth",
                "\uD83C\uDF89" // party popper
            )
            currentStreak in 8..13 -> Triple(
                "$currentStreak days",
                "Impressive dedication",
                "\uD83D\uDCAA" // flexed bicep
            )
            currentStreak == 14 -> Triple(
                "2 weeks!",
                "Two weeks strong",
                "\uD83C\uDFC6" // trophy
            )
            currentStreak in 15..29 -> Triple(
                "$currentStreak days",
                "You're on fire",
                "\uD83D\uDD25" // fire
            )
            currentStreak == 30 -> Triple(
                "1 month!",
                "A full month of journaling",
                "\uD83C\uDF1F" // star
            )
            currentStreak in 31..99 -> Triple(
                "$currentStreak days",
                "Legendary consistency",
                "\u2B50" // star
            )
            currentStreak >= 100 -> Triple(
                "$currentStreak days",
                "You're unstoppable",
                "\uD83D\uDC51" // crown
            )
            else -> Triple(
                "$currentStreak days",
                "Keep going",
                "\uD83D\uDD25"
            )
        }

        WidgetStreakMessage(
            title = title,
            message = if (userName != null) "$message, $userName" else message,
            emoji = emoji,
            streakCount = currentStreak
        )
    }

    /**
     * Check if user might benefit from a gentle nudge.
     */
    suspend fun shouldShowNudge(): Boolean = withContext(Dispatchers.IO) {
        val daysSinceLastEntry = getDaysSinceLastEntry()
        val hour = LocalDateTime.now().hour

        // Show nudge if:
        // 1. They haven't journaled today AND
        // 2. It's a good time (morning or evening) AND
        // 3. They're not a brand new user (have at least 1 entry)
        val hasAnyEntries = journalDao.getEntryCount() > 0
        val isGoodTime = hour in 7..10 || hour in 18..21

        hasAnyEntries && daysSinceLastEntry >= 1 && isGoodTime
    }

    /**
     * Get a memory teaser for widgets (if there's an anniversary memory).
     */
    suspend fun getMemoryTeaser(): WidgetMemoryTeaser? = withContext(Dispatchers.IO) {
        try {
            val today = LocalDate.now()
            val todayStart = today.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val todayEnd = today.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            // Look for entries from past years on this day
            val allEntries = journalDao.getAllEntriesSync()
            val anniversaryEntry = allEntries.filter { entry ->
                val entryDate = java.time.Instant.ofEpochMilli(entry.createdAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                entryDate.dayOfMonth == today.dayOfMonth &&
                        entryDate.month == today.month &&
                        entryDate.year != today.year
            }.maxByOrNull { it.createdAt }

            anniversaryEntry?.let { entry ->
                val entryDate = java.time.Instant.ofEpochMilli(entry.createdAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val yearsAgo = today.year - entryDate.year

                WidgetMemoryTeaser(
                    teaser = "On this day ${if (yearsAgo == 1) "1 year" else "$yearsAgo years"} ago...",
                    preview = entry.content.take(50) + if (entry.content.length > 50) "..." else "",
                    yearsAgo = yearsAgo
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    // =============================================================================================
    // PRIVATE HELPERS
    // =============================================================================================

    private fun getCurrentTimeOfDay(): TimeOfDay {
        val hour = LocalDateTime.now().hour
        return when (hour) {
            in 5..6 -> TimeOfDay.EARLY_MORNING
            in 7..9 -> TimeOfDay.MORNING
            in 10..11 -> TimeOfDay.LATE_MORNING
            in 12..16 -> TimeOfDay.AFTERNOON
            in 17..20 -> TimeOfDay.EVENING
            in 21..23 -> TimeOfDay.NIGHT
            else -> TimeOfDay.LATE_NIGHT
        }
    }

    private suspend fun getDaysSinceLastEntry(): Int {
        return try {
            val lastEntry = journalDao.getLatestEntry()
            if (lastEntry != null) {
                val lastDate = java.time.Instant.ofEpochMilli(lastEntry.createdAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val today = LocalDate.now()
                java.time.temporal.ChronoUnit.DAYS.between(lastDate, today).toInt()
            } else {
                Int.MAX_VALUE // No entries yet
            }
        } catch (e: Exception) {
            Int.MAX_VALUE
        }
    }

    private suspend fun getUserName(): String? {
        return try {
            val profile = userDao.getUserProfileSync()
            profile?.displayName?.takeIf { it.isNotBlank() && it != "Growth Seeker" }
        } catch (e: Exception) {
            null
        }
    }

    private fun getReturningUserPrompt(daysSinceLastEntry: Int): String {
        return when {
            daysSinceLastEntry > 14 -> "A lot can change in ${daysSinceLastEntry} days. What's new?"
            daysSinceLastEntry > 7 -> "It's been a while. How have you been?"
            else -> "Welcome back! What's on your mind?"
        }
    }

    private fun getTimeBasedPrompt(timeOfDay: TimeOfDay): String {
        return when (timeOfDay) {
            TimeOfDay.EARLY_MORNING -> listOf(
                "What woke you up early today?",
                "How does this quiet moment feel?",
                "What are you hoping for today?"
            ).random()
            TimeOfDay.MORNING -> listOf(
                "What's on your mind this morning?",
                "How are you feeling today?",
                "What would make today great?"
            ).random()
            TimeOfDay.LATE_MORNING -> listOf(
                "How's your day going so far?",
                "What's been on your mind?",
                "Take a moment to reflect..."
            ).random()
            TimeOfDay.AFTERNOON -> listOf(
                "How's the day treating you?",
                "What's happened so far today?",
                "Midday check-in: how are you?"
            ).random()
            TimeOfDay.EVENING -> listOf(
                "How was your day?",
                "What's the highlight of today?",
                "Anything weighing on your mind?"
            ).random()
            TimeOfDay.NIGHT -> listOf(
                "How are you feeling tonight?",
                "What's on your mind before bed?",
                "Any thoughts to capture?"
            ).random()
            TimeOfDay.LATE_NIGHT -> listOf(
                "What's keeping you up?",
                "Late night thoughts?",
                "Sometimes the best insights come at night..."
            ).random()
        }
    }

    private fun getPromptSubtext(timeOfDay: TimeOfDay, userName: String?): String {
        val name = userName?.let { ", $it" } ?: ""
        return when (timeOfDay) {
            TimeOfDay.EARLY_MORNING -> "The early morning is for thinkers$name"
            TimeOfDay.MORNING -> "Start your day with reflection$name"
            TimeOfDay.LATE_MORNING -> "A moment for yourself$name"
            TimeOfDay.AFTERNOON -> "Pause and reflect$name"
            TimeOfDay.EVENING -> "Unwind with journaling$name"
            TimeOfDay.NIGHT -> "End the day mindfully$name"
            TimeOfDay.LATE_NIGHT -> "Quiet moments for clarity$name"
        }
    }

    private fun getPromptIcon(timeOfDay: TimeOfDay): String {
        return when (timeOfDay) {
            TimeOfDay.EARLY_MORNING -> "\uD83C\uDF05" // sunrise
            TimeOfDay.MORNING -> "\u2600\uFE0F" // sun
            TimeOfDay.LATE_MORNING -> "\u2615" // coffee
            TimeOfDay.AFTERNOON -> "\uD83C\uDF24\uFE0F" // sun behind cloud
            TimeOfDay.EVENING -> "\uD83C\uDF06" // sunset
            TimeOfDay.NIGHT -> "\uD83C\uDF19" // crescent moon
            TimeOfDay.LATE_NIGHT -> "\u2728" // sparkles
        }
    }

    private fun getTimeEmoji(timeOfDay: TimeOfDay): String {
        return when (timeOfDay) {
            TimeOfDay.EARLY_MORNING -> "\uD83C\uDF05"
            TimeOfDay.MORNING -> "\uD83C\uDF1E"
            TimeOfDay.LATE_MORNING -> "\u2600\uFE0F"
            TimeOfDay.AFTERNOON -> "\uD83C\uDF24\uFE0F"
            TimeOfDay.EVENING -> "\uD83C\uDF06"
            TimeOfDay.NIGHT -> "\uD83C\uDF19"
            TimeOfDay.LATE_NIGHT -> "\uD83C\uDF1A"
        }
    }

    private fun getTimeBasedSubtext(timeOfDay: TimeOfDay): String {
        return when (timeOfDay) {
            TimeOfDay.EARLY_MORNING -> "Early birds catch insights"
            TimeOfDay.MORNING -> "A fresh start awaits"
            TimeOfDay.LATE_MORNING -> "Time for a quick reflection"
            TimeOfDay.AFTERNOON -> "Midday mindfulness"
            TimeOfDay.EVENING -> "Wind down with words"
            TimeOfDay.NIGHT -> "Peaceful night ahead"
            TimeOfDay.LATE_NIGHT -> "Night owls think deep"
        }
    }
}

// ================================================================================================
// WIDGET DATA MODELS
// ================================================================================================

/**
 * Intelligent journal prompt data for widgets.
 */
data class WidgetJournalPrompt(
    val prompt: String,
    val subtext: String,
    val icon: String,
    val isPersonalized: Boolean
)

/**
 * Intelligent greeting data for widgets.
 */
data class WidgetGreeting(
    val greeting: String,
    val subtext: String,
    val emoji: String
)

/**
 * Streak message data for widgets.
 */
data class WidgetStreakMessage(
    val title: String,
    val message: String,
    val emoji: String,
    val streakCount: Int
)

/**
 * Memory teaser data for widgets.
 */
data class WidgetMemoryTeaser(
    val teaser: String,
    val preview: String,
    val yearsAgo: Int
)

/**
 * Time of day enum for widget-specific granularity.
 */
private enum class TimeOfDay {
    EARLY_MORNING,  // 5-6am
    MORNING,        // 7-9am
    LATE_MORNING,   // 10-11am
    AFTERNOON,      // 12-4pm
    EVENING,        // 5-8pm
    NIGHT,          // 9-11pm
    LATE_NIGHT      // 12-4am
}
