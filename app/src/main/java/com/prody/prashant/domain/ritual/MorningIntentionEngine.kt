package com.prody.prashant.domain.ritual

import com.prody.prashant.data.local.dao.DailyRitualDao
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.entity.DailyRitualEntity
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Engine for generating contextual morning intention prompts.
 *
 * Analyzes user patterns, streaks, day of week, and recent journal themes
 * to provide warm, personal prompts that feel human - never robotic.
 */
@Singleton
class MorningIntentionEngine @Inject constructor(
    private val dailyRitualDao: DailyRitualDao,
    private val journalDao: JournalDao
) {

    /**
     * Generate a contextual morning prompt based on user patterns and context.
     */
    suspend fun generateMorningPrompt(
        userId: String,
        currentStreak: Int,
        wasYesterdayDifficult: Boolean = false
    ): String {
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek

        // Get recent data for context
        val recentIntentions = dailyRitualDao.getRecentIntentions(userId, limit = 7)
        val weekStart = getStartOfWeek()
        val recentThemes = journalDao.getThemesThisWeek(weekStart)

        // Determine prompt based on context
        return when {
            // After a difficult day - be gentle and encouraging
            wasYesterdayDifficult -> getPostDifficultDayPrompt()

            // Streak milestone - acknowledge consistency
            currentStreak > 0 && currentStreak % 7 == 0 -> getStreakMilestonePrompt(currentStreak)

            // Regular streak day - keep momentum
            currentStreak >= 3 -> getStreakMaintenancePrompt()

            // Monday - week planning
            dayOfWeek == DayOfWeek.MONDAY -> getMondayPrompt()

            // Friday - week wrap up mindset
            dayOfWeek == DayOfWeek.FRIDAY -> getFridayPrompt()

            // Weekend - different energy
            dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY -> getWeekendPrompt(dayOfWeek)

            // Fresh start - no recent streak
            currentStreak == 0 -> getFreshStartPrompt()

            // Default contextual prompts
            else -> getDefaultPrompt()
        }
    }

    /**
     * Generate intention suggestions based on recent journal themes.
     * These are NOT generic like "Be productive" - they're based on actual user patterns.
     */
    suspend fun generateIntentionSuggestions(
        userId: String,
        limit: Int = 3
    ): List<String> {
        val weekStart = getStartOfWeek()
        val recentThemes = journalDao.getThemesThisWeek(weekStart)
        val recentIntentions = dailyRitualDao.getRecentIntentions(userId, limit = 10)
        val dominantMood = journalDao.getDominantMoodThisWeek(weekStart)

        val suggestions = mutableListOf<String>()

        // Parse themes from comma-separated strings
        val allThemes = recentThemes
            .mapNotNull { it }
            .flatMap { it.split(",").map { theme -> theme.trim() } }
            .filter { it.isNotBlank() }
            .take(5)

        // Generate suggestions based on patterns
        when {
            // If user often mentions relationships
            allThemes.any { it.contains("relationship", ignoreCase = true) ||
                           it.contains("family", ignoreCase = true) ||
                           it.contains("friend", ignoreCase = true) } -> {
                suggestions.add("Reach out to someone I care about")
            }

            // If user mentions work/career often
            allThemes.any { it.contains("work", ignoreCase = true) ||
                           it.contains("career", ignoreCase = true) ||
                           it.contains("project", ignoreCase = true) } -> {
                suggestions.add("Make progress on what matters most at work")
            }

            // If user mentions self-care, health
            allThemes.any { it.contains("health", ignoreCase = true) ||
                           it.contains("exercise", ignoreCase = true) ||
                           it.contains("rest", ignoreCase = true) } -> {
                suggestions.add("Take care of my body and mind")
            }

            // If user mentions creativity
            allThemes.any { it.contains("creative", ignoreCase = true) ||
                           it.contains("art", ignoreCase = true) ||
                           it.contains("writing", ignoreCase = true) } -> {
                suggestions.add("Create something, even if small")
            }
        }

        // Add universal meaningful suggestions
        if (suggestions.size < limit) {
            val universalSuggestions = listOf(
                "Be present in conversations today",
                "Notice something beautiful",
                "Do one thing I've been avoiding",
                "Show up as the person I want to be",
                "Make space for what brings me alive",
                "Choose curiosity over judgment",
                "Act from clarity, not urgency"
            )

            // Add suggestions that aren't already in recent intentions
            universalSuggestions
                .filter { suggestion ->
                    recentIntentions.none { it.contains(suggestion.take(15), ignoreCase = true) }
                }
                .take(limit - suggestions.size)
                .forEach { suggestions.add(it) }
        }

        return suggestions.take(limit)
    }

    // ==================== PROMPT GENERATORS ====================

    private fun getPostDifficultDayPrompt(): String {
        return listOf(
            "What's one thing you're looking forward to today?",
            "Yesterday was tough. What would feel good today?",
            "What would make today a little lighter?",
            "What's one kind thing you can do for yourself today?",
            "After a hard day, what matters most right now?"
        ).random()
    }

    private fun getStreakMilestonePrompt(streak: Int): String {
        return listOf(
            "You've shown up $streak days in a row. What's today's focus?",
            "$streak days of showing up. What matters today?",
            "You've built real momentum. What's calling you today?",
            "$streak days strong. What feels important right now?"
        ).random()
    }

    private fun getStreakMaintenancePrompt(): String {
        return listOf(
            "You've been consistent. What's today's focus?",
            "You keep showing up. What matters today?",
            "What's the one thing that would make today feel complete?",
            "You're building something. What's next?"
        ).random()
    }

    private fun getMondayPrompt(): String {
        return listOf(
            "What would make this week meaningful?",
            "A new week. What matters most right now?",
            "What do you want to carry into this week?",
            "If this week goes well, what will you have done?",
            "What's one thing that would make this week feel good?"
        ).random()
    }

    private fun getFridayPrompt(): String {
        return listOf(
            "As the week winds down, what still matters?",
            "What would help you finish the week strong?",
            "Before the weekend, what needs your attention?",
            "What would make today a good end to the week?"
        ).random()
    }

    private fun getWeekendPrompt(day: DayOfWeek): String {
        return if (day == DayOfWeek.SATURDAY) {
            listOf(
                "How do you want to feel by Sunday night?",
                "What would make this weekend feel good?",
                "What do you need this weekend?",
                "What would make today feel like a real Saturday?"
            ).random()
        } else {
            listOf(
                "Sunday. What would feel nourishing today?",
                "How do you want to step into next week?",
                "What would help you feel ready for Monday?",
                "What does your soul need today?"
            ).random()
        }
    }

    private fun getFreshStartPrompt(): String {
        return listOf(
            "A fresh start. What matters to you today?",
            "What's one thing you want to focus on?",
            "What would make today feel good?",
            "What's calling your attention right now?",
            "If today goes well, what will you have done?"
        ).random()
    }

    private fun getDefaultPrompt(): String {
        return listOf(
            "What's one thing you want to focus on today?",
            "What matters most to you right now?",
            "What would make today feel complete?",
            "What do you want to carry with you today?",
            "If today goes well, what will you have focused on?",
            "What's asking for your attention?",
            "What wants to happen through you today?"
        ).random()
    }

    // ==================== HELPER METHODS ====================

    /**
     * Check if yesterday was marked as a difficult day.
     */
    suspend fun wasYesterdayDifficult(userId: String): Boolean {
        val yesterday = getYesterdayDate()
        val yesterdayRitual = dailyRitualDao.getRitualForDate(userId, yesterday)
        return yesterdayRitual?.eveningDayRating == DailyRitualEntity.DAY_RATING_TOUGH
    }

    /**
     * Get affirmation message after completing morning ritual.
     */
    fun getMorningCompletionMessage(hasIntention: Boolean): String {
        return if (hasIntention) {
            listOf(
                "That's set. Go make it happen.",
                "You've got this. One step at a time.",
                "Good. Now let today unfold.",
                "That's your anchor. Come back to it when you need to.",
                "Simple and clear. That's all you need."
            ).random()
        } else {
            listOf(
                "You showed up. That's what matters.",
                "Sometimes showing up is enough.",
                "You're here. That counts.",
                "The day is yours."
            ).random()
        }
    }

    // ==================== DATE HELPERS ====================

    private fun getStartOfWeek(): Long {
        val today = LocalDate.now()
        val monday = today.with(DayOfWeek.MONDAY)
        return monday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun getYesterdayDate(): Long {
        val yesterday = LocalDate.now().minusDays(1)
        return yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
