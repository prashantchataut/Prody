package com.prody.prashant.domain.ritual

import com.prody.prashant.data.local.dao.DailyRitualDao
import com.prody.prashant.data.local.entity.DailyRitualEntity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Intention outcome tracking.
 */
enum class IntentionOutcome(val value: String) {
    MET("met"),
    PARTIALLY("partially"),
    MISSED("missed"),
    FORGOT("forgot");

    companion object {
        fun fromString(value: String?): IntentionOutcome? {
            return values().find { it.value == value }
        }
    }
}

/**
 * Engine for generating contextual evening reflection prompts.
 *
 * Links back to morning intentions, provides meaningful reflection prompts,
 * and offers brief closing wisdom after completion.
 */
@Singleton
class EveningReflectionEngine @Inject constructor(
    private val dailyRitualDao: DailyRitualDao
) {

    /**
     * Generate a contextual evening prompt.
     * If there was a morning intention, reference it.
     */
    suspend fun generateEveningPrompt(
        userId: String,
        morningIntention: String? = null,
        dayRating: String? = null
    ): String {
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek

        // If we have a morning intention, create a callback prompt
        if (!morningIntention.isNullOrBlank()) {
            return getMorningIntentionCallbackPrompt(morningIntention)
        }

        // Otherwise, use contextual evening prompts
        return when {
            dayRating == DailyRitualEntity.DAY_RATING_TOUGH -> getDifficultDayPrompt()
            dayRating == DailyRitualEntity.DAY_RATING_GOOD -> getGoodDayPrompt()
            dayOfWeek == DayOfWeek.FRIDAY -> getFridayEveningPrompt()
            dayOfWeek == DayOfWeek.SUNDAY -> getSundayEveningPrompt()
            else -> getDefaultEveningPrompt()
        }
    }

    /**
     * Generate a secondary reflection prompt for deeper capture.
     */
    suspend fun generateReflectionPrompt(
        dayRating: String?,
        morningIntention: String? = null
    ): String {
        return when (dayRating) {
            DailyRitualEntity.DAY_RATING_GOOD -> listOf(
                "What made it good?",
                "What do you want to remember?",
                "What are you grateful for right now?",
                "What felt right today?"
            ).random()

            DailyRitualEntity.DAY_RATING_TOUGH -> listOf(
                "What made it hard?",
                "What do you need to let go of?",
                "What's one thing that helped, even a little?",
                "What would tomorrow's version of you want to hear?"
            ).random()

            else -> listOf(
                "What happened today that mattered?",
                "What are you carrying into tomorrow?",
                "What surprised you today?",
                "What's sitting with you tonight?"
            ).random()
        }
    }

    /**
     * Get closing wisdom after completing evening ritual.
     */
    fun getEveningCompletionMessage(
        dayRating: String?,
        intentionOutcome: IntentionOutcome? = null,
        currentStreak: Int = 0
    ): String {
        // If they achieved their intention
        if (intentionOutcome == IntentionOutcome.MET) {
            return listOf(
                "You did what you said you would. That's everything.",
                "You showed up for yourself. That matters.",
                "You kept your word to yourself. Rest well.",
                "That's how trust with yourself is built."
            ).random()
        }

        // If they partially met it
        if (intentionOutcome == IntentionOutcome.PARTIALLY) {
            return listOf(
                "Progress over perfection. You moved forward.",
                "You tried. That counts more than you think.",
                "It doesn't have to be perfect. You showed up.",
                "Partial is better than nothing. You're building something."
            ).random()
        }

        // After a tough day
        if (dayRating == DailyRitualEntity.DAY_RATING_TOUGH) {
            return listOf(
                "Tough days happen. You made it through.",
                "You showed up even when it was hard. That's strength.",
                "Tomorrow is new. Rest now.",
                "You did your best with what you had. That's enough.",
                "Some days are just about getting through. You did."
            ).random()
        }

        // After a good day
        if (dayRating == DailyRitualEntity.DAY_RATING_GOOD) {
            return listOf(
                "Good days are worth savoring. Sleep well.",
                "Hold onto what made today good.",
                "This is what you're building toward.",
                "Let today remind you what's possible."
            ).random()
        }

        // Streak acknowledgment
        if (currentStreak > 0 && currentStreak % 7 == 0) {
            return listOf(
                "You've checked in $currentStreak days in a row. You're building something real.",
                "$currentStreak days of showing up. This is how change happens.",
                "The consistency you're building is remarkable. $currentStreak days strong."
            ).random()
        }

        // Default closing wisdom
        return listOf(
            "You showed up today. That's what matters.",
            "Another day lived with intention. Rest well.",
            "You made space to reflect. That's rare.",
            "You're paying attention to your life. Keep going.",
            "The fact that you're here matters.",
            "This is the practice. Tomorrow, again.",
            "Day complete. Rest well."
        ).random()
    }

    /**
     * Analyze if the user met their morning intention based on evening inputs.
     * This is a simple heuristic - can be enhanced with AI later.
     */
    fun inferIntentionOutcome(
        morningIntention: String?,
        eveningReflection: String?,
        dayRating: String?
    ): IntentionOutcome {
        // If no intention, return forgot
        if (morningIntention.isNullOrBlank()) {
            return IntentionOutcome.FORGOT
        }

        // If no reflection, we can't infer
        if (eveningReflection.isNullOrBlank()) {
            // Use day rating as fallback
            return when (dayRating) {
                DailyRitualEntity.DAY_RATING_GOOD -> IntentionOutcome.MET
                DailyRitualEntity.DAY_RATING_TOUGH -> IntentionOutcome.MISSED
                else -> IntentionOutcome.PARTIALLY
            }
        }

        // Simple keyword matching
        val reflection = eveningReflection.lowercase()
        val positiveKeywords = listOf(
            "did", "accomplished", "finished", "completed", "achieved",
            "done", "succeed", "managed", "got it", "made progress"
        )
        val negativeKeywords = listOf(
            "didn't", "couldn't", "failed", "forgot", "missed",
            "struggled", "hard", "difficult", "overwhelming"
        )

        val hasPositive = positiveKeywords.any { reflection.contains(it) }
        val hasNegative = negativeKeywords.any { reflection.contains(it) }

        return when {
            hasPositive && !hasNegative -> IntentionOutcome.MET
            hasNegative && !hasPositive -> IntentionOutcome.MISSED
            hasPositive && hasNegative -> IntentionOutcome.PARTIALLY
            dayRating == DailyRitualEntity.DAY_RATING_GOOD -> IntentionOutcome.PARTIALLY
            else -> IntentionOutcome.PARTIALLY
        }
    }

    // ==================== PROMPT GENERATORS ====================

    private fun getMorningIntentionCallbackPrompt(intention: String): String {
        // Take first 50 chars of intention for brevity
        val shortIntention = if (intention.length > 50) {
            intention.take(47) + "..."
        } else {
            intention
        }

        return listOf(
            "This morning you focused on: \"$shortIntention\". How did it go?",
            "You set out to: \"$shortIntention\". How was your day?",
            "Earlier today: \"$shortIntention\". Did you get there?",
            "Your intention was: \"$shortIntention\". How'd that play out?",
            "You wanted to: \"$shortIntention\". What happened?"
        ).random()
    }

    private fun getDifficultDayPrompt(): String {
        return listOf(
            "Tough day. What's one thing you're letting go of?",
            "What made today hard?",
            "What do you need to release before tomorrow?",
            "It was a hard one. What helped, even a little?",
            "Rough day. What's one thing you learned?"
        ).random()
    }

    private fun getGoodDayPrompt(): String {
        return listOf(
            "What made today good?",
            "Good day. What do you want to remember?",
            "What worked today that you want to do again?",
            "What's one thing from today you're grateful for?",
            "What felt right today?"
        ).random()
    }

    private fun getFridayEveningPrompt(): String {
        return listOf(
            "Week's done. How are you feeling?",
            "Friday night. What are you carrying into the weekend?",
            "How did this week treat you?",
            "What's one thing from this week you want to remember?"
        ).random()
    }

    private fun getSundayEveningPrompt(): String {
        return listOf(
            "Sunday winding down. How do you feel about the week ahead?",
            "Another week starts tomorrow. What do you need?",
            "How was your weekend? What are you taking forward?",
            "Sunday night. Are you ready, or do you need more rest?"
        ).random()
    }

    private fun getDefaultEveningPrompt(): String {
        return listOf(
            "How was your day?",
            "What happened today that mattered?",
            "What's sitting with you tonight?",
            "What do you want to remember from today?",
            "What's one thing from today worth noting?",
            "How did today feel?",
            "What surprised you today?"
        ).random()
    }

    // ==================== ANALYTICS ====================

    /**
     * Get intention outcome distribution for analytics.
     */
    suspend fun getIntentionOutcomeStats(
        userId: String,
        since: Long
    ): Map<IntentionOutcome, Int> {
        // This would query the database for outcomes
        // For now, return empty map - will be implemented after DAO updates
        return emptyMap()
    }

    /**
     * Get intention completion rate.
     */
    suspend fun getIntentionCompletionRate(
        userId: String,
        days: Int = 30
    ): Double {
        // This would calculate the percentage of met intentions
        // For now, return 0.0 - will be implemented after DAO updates
        return 0.0
    }
}
