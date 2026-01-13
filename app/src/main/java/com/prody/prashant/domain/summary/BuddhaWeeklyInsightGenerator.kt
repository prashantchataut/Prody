package com.prody.prashant.domain.summary

import com.prody.prashant.data.ai.GeminiResult
import com.prody.prashant.data.ai.GeminiService
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.domain.model.Mood
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generates personalized Buddha insights for weekly summaries.
 *
 * These insights are NOT generic AI platitudes. They:
 * - Reference actual content from the user's entries
 * - Notice specific patterns and themes
 * - Ask meaningful questions based on what was written
 * - Feel like a wise friend who actually read your journal
 */
@Singleton
class BuddhaWeeklyInsightGenerator @Inject constructor(
    private val geminiService: GeminiService
) {

    /**
     * Generate a personalized insight for the week.
     *
     * Falls back to pattern-based insights if AI is unavailable.
     */
    suspend fun generateInsight(
        entries: List<JournalEntryEntity>,
        moodTrend: MoodTrend,
        topThemes: List<String>,
        dominantMood: Mood?,
        highlightEntry: JournalEntryEntity?
    ): String {
        // Try AI-generated insight first
        if (geminiService.isConfigured() && entries.isNotEmpty()) {
            val aiInsight = generateAiInsight(entries, moodTrend, topThemes, dominantMood, highlightEntry)
            if (aiInsight != null) {
                return aiInsight
            }
        }

        // Fallback to pattern-based insights
        return generatePatternBasedInsight(entries, moodTrend, topThemes, dominantMood, highlightEntry)
    }

    /**
     * Generate AI-powered personalized insight.
     */
    private suspend fun generateAiInsight(
        entries: List<JournalEntryEntity>,
        moodTrend: MoodTrend,
        topThemes: List<String>,
        dominantMood: Mood?,
        highlightEntry: JournalEntryEntity?
    ): String? {
        val prompt = buildAiPrompt(entries, moodTrend, topThemes, dominantMood, highlightEntry)

        return when (val result = geminiService.generateCustomResponse(prompt, includeSystemPrompt = true)) {
            is GeminiResult.Success -> result.data
            else -> null
        }
    }

    /**
     * Build a detailed prompt for AI to generate personalized insight.
     */
    private fun buildAiPrompt(
        entries: List<JournalEntryEntity>,
        moodTrend: MoodTrend,
        topThemes: List<String>,
        dominantMood: Mood?,
        highlightEntry: JournalEntryEntity?
    ): String {
        val entrySummaries = entries.take(5).mapIndexed { index, entry ->
            val date = Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("EEEE"))
            val preview = entry.content.take(200)
            val mood = Mood.fromString(entry.mood)
            "Day ${index + 1} ($date) - Mood: ${mood.displayName} ${mood.emoji}\n\"${preview}${if (entry.content.length > 200) "..." else ""}\""
        }.joinToString("\n\n")

        return """
Generate a personalized weekly reflection for this user's journal entries. This is their WEEKLY DIGEST - a moment to help them see patterns and growth.

IMPORTANT: Be specific and personal. Reference actual things they wrote about. Don't be generic.

THEIR WEEK:
- Entries: ${entries.size}
- Mood trend: ${moodTrend.name.lowercase().replace("_", " ")}
- Dominant feeling: ${dominantMood?.displayName ?: "varied"}
- Main themes: ${topThemes.joinToString(", ")}

SAMPLE ENTRIES FROM THIS WEEK:
$entrySummaries

YOUR TASK:
Write a 2-4 sentence reflection that:
1. References something specific they actually wrote about (use their themes or a concept they mentioned)
2. Notices a pattern or shift (if applicable)
3. Asks a meaningful question OR offers wisdom that connects to their specific situation
4. Feels warm and wise, not robotic

EXAMPLES OF GOOD INSIGHTS:
- "This week, you wrote a lot about [theme]. There's a pattern forming - notice how often [specific thing] comes up when you feel [mood]."
- "Your mood shifted from [X] to [Y] after writing about [topic]. What changed for you in that moment?"
- "You asked yourself a powerful question on [day]: [question]. Have you found an answer, or is it still unfolding?"
- "I noticed you mentioned [specific thing] three times this week. It seems to be calling for your attention."

AVOID:
- Generic statements that could apply to anyone
- Just summarizing their stats
- Clichés or motivational poster phrases
- Being overly formal or clinical

Write ONLY the reflection, nothing else. Make it feel like you actually read their journal.
""".trimIndent()
    }

    /**
     * Generate pattern-based insight when AI is unavailable.
     *
     * These are still personalized based on actual data.
     */
    private fun generatePatternBasedInsight(
        entries: List<JournalEntryEntity>,
        moodTrend: MoodTrend,
        topThemes: List<String>,
        dominantMood: Mood?,
        highlightEntry: JournalEntryEntity?
    ): String {
        val insights = mutableListOf<String>()

        // Theme-based insights
        if (topThemes.isNotEmpty()) {
            val mainTheme = topThemes.first()
            when (mainTheme) {
                "work" -> insights.add("This week, you wrote a lot about work. There's a pattern forming.")
                "relationships" -> insights.add("Relationships have been on your mind this week. Notice what these connections are teaching you.")
                "growth" -> insights.add("Your focus on growth shows a commitment to becoming who you want to be.")
                "challenges" -> insights.add("You've been facing challenges head-on. That takes courage.")
                "gratitude" -> insights.add("Your practice of gratitude is shaping how you see the world.")
                "emotions" -> insights.add("You've been processing a lot emotionally this week. That's important work.")
                "health" -> insights.add("Taking care of your health is taking care of your whole life.")
                "creativity" -> insights.add("Your creative energy is flowing. Pay attention to what inspires you.")
            }
        }

        // Mood shift insights
        when (moodTrend) {
            MoodTrend.IMPROVING -> {
                val startMood = entries.minByOrNull { it.createdAt }?.let { Mood.fromString(it.mood) }
                val endMood = entries.maxByOrNull { it.createdAt }?.let { Mood.fromString(it.mood) }
                if (startMood != null && endMood != null && startMood != endMood) {
                    insights.add("Your mood shifted from ${startMood.displayName} to ${endMood.displayName} this week. What helped that shift?")
                } else {
                    insights.add("Your emotional state has been improving. Notice what practices are serving you.")
                }
            }
            MoodTrend.DECLINING -> {
                insights.add("You've been going through something this week. Remember: difficult seasons precede growth.")
            }
            MoodTrend.STABLE -> {
                dominantMood?.let {
                    when (it) {
                        Mood.CALM -> insights.add("You've found a steady peace this week. This is what consistency looks like.")
                        Mood.HAPPY -> insights.add("Joy has been your companion this week. Savor these moments.")
                        Mood.MOTIVATED -> insights.add("Your motivation is strong. Channel this energy while it's burning.")
                        Mood.GRATEFUL -> insights.add("Gratitude has colored your week. This practice changes everything.")
                        else -> insights.add("There's power in emotional consistency. You're building stability.")
                    }
                }
            }
            MoodTrend.VOLATILE, MoodTrend.VARIABLE, MoodTrend.FLUCTUATING -> {
                insights.add("Your emotions have been shifting this week. Notice what triggers these changes.")
            }
            MoodTrend.INSUFFICIENT_DATA -> {
                // Not enough data yet, skip mood trend insight
            }
        }

        // Writing frequency insights
        when (entries.size) {
            in 7..Int.MAX_VALUE -> insights.add("You journaled every day this week. That kind of consistency compounds.")
            in 5..6 -> insights.add("You maintained a strong practice this week. Almost daily reflection - that's dedication.")
            in 3..4 -> insights.add("You've been checking in with yourself regularly. Keep showing up.")
            in 1..2 -> insights.add("Even in busy weeks, you made time for reflection. That matters.")
        }

        // Highlight entry insight
        highlightEntry?.let { entry ->
            val day = Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .dayOfWeek
            val mood = Mood.fromString(entry.mood)

            if (entry.wordCount > 300) {
                insights.add("You wrote deeply on ${formatDayOfWeek(day)}. Those ${entry.wordCount} words hold something important.")
            }

            // Look for questions in highlight entry
            if (entry.content.contains("?")) {
                insights.add("You asked yourself powerful questions this week. Have you found answers, or are they still unfolding?")
            }
        }

        // Combine insights intelligently
        return when (insights.size) {
            0 -> "Every moment of reflection adds to your growth. Your journey continues."
            1 -> insights[0]
            else -> {
                // Combine first two insights with good flow
                val first = insights[0]
                val second = insights[1]
                "$first $second"
            }
        }
    }

    /**
     * Generate celebration message for achievements.
     */
    fun generateCelebration(
        entriesCount: Int,
        isNewStreakRecord: Boolean,
        hasAllDays: Boolean,
        moodTrend: MoodTrend
    ): String? {
        return when {
            hasAllDays -> "Seven days, seven entries. You showed up for yourself every single day this week. 🌟"
            isNewStreakRecord -> "This is your longest streak yet! Your consistency is building something lasting."
            entriesCount >= 5 && moodTrend == MoodTrend.IMPROVING -> "Five entries and an upward mood trend. Your practice is working."
            entriesCount >= 4 -> "You're building a real habit here. Four entries this week - that's meaningful progress."
            else -> null
        }
    }

    /**
     * Generate encouragement message when activity is low.
     */
    fun generateEncouragement(entriesCount: Int, moodTrend: MoodTrend): String? {
        return when {
            entriesCount == 0 -> "Life gets busy, and that's okay. Your journal is here whenever you're ready to return."
            entriesCount == 1 -> "One entry is better than zero. Small steps still move you forward."
            entriesCount == 2 -> "Two check-ins with yourself this week. Even brief moments of reflection matter."
            moodTrend == MoodTrend.DECLINING && entriesCount < 4 ->
                "Writing can be hard when things are difficult. But that's often when we need it most."
            else -> null
        }
    }

    private fun formatDayOfWeek(day: DayOfWeek): String {
        return when (day) {
            DayOfWeek.MONDAY -> "Monday"
            DayOfWeek.TUESDAY -> "Tuesday"
            DayOfWeek.WEDNESDAY -> "Wednesday"
            DayOfWeek.THURSDAY -> "Thursday"
            DayOfWeek.FRIDAY -> "Friday"
            DayOfWeek.SATURDAY -> "Saturday"
            DayOfWeek.SUNDAY -> "Sunday"
        }
    }
}
