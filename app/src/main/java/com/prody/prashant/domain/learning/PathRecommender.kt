package com.prody.prashant.domain.learning

import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.data.local.entity.PathRecommendationEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PathRecommender
 *
 * Analyzes user's journal entries to recommend personalized learning paths.
 * Uses pattern matching, keyword analysis, and sentiment detection to identify
 * areas where a user might benefit from structured learning.
 */
@Singleton
class PathRecommender @Inject constructor() {

    /**
     * Analyze journal entries and generate path recommendations
     */
    fun analyzeAndRecommend(
        entries: List<JournalEntryEntity>,
        userId: String = "local"
    ): List<PathRecommendationEntity> {
        if (entries.isEmpty()) return emptyList()

        val recommendations = mutableListOf<PathRecommendationEntity>()
        val recentEntries = entries.take(20) // Focus on recent entries

        // Analyze patterns in recent entries
        val emotionPatterns = analyzeEmotionalPatterns(recentEntries)
        val themePatterns = analyzeThemePatterns(recentEntries)
        val moodTrends = analyzeMoodTrends(recentEntries)

        // Generate recommendations based on patterns
        emotionPatterns.forEach { (pathType, signals) ->
            val confidence = calculateConfidence(signals, recentEntries.size)
            if (confidence > 0.3f) { // Threshold for recommendation
                recommendations.add(
                    PathRecommendationEntity(
                        userId = userId,
                        pathType = pathType.id,
                        reason = generateRecommendationReason(pathType, signals),
                        confidenceScore = confidence,
                        basedOnEntriesJson = signals.entryIds.toString(),
                        basedOnPatternsJson = signals.patterns.toString()
                    )
                )
            }
        }

        return recommendations.sortedByDescending { it.confidenceScore }
    }

    /**
     * Analyze emotional patterns in journal entries
     */
    private fun analyzeEmotionalPatterns(
        entries: List<JournalEntryEntity>
    ): Map<PathType, RecommendationSignals> {
        val patterns = mutableMapOf<PathType, RecommendationSignals>()

        // Emotional Intelligence signals
        val emotionalIntelligenceSignals = detectSignals(
            entries,
            keywords = listOf(
                "don't understand", "confused about feelings", "emotional",
                "don't know what I feel", "mixed emotions", "overwhelming",
                "can't express", "bottled up", "suppress", "numb"
            ),
            moods = listOf("confused", "overwhelmed", "frustrated"),
            themes = listOf("emotions", "feelings", "self-awareness")
        )
        if (emotionalIntelligenceSignals.strength > 0) {
            patterns[PathType.EMOTIONAL_INTELLIGENCE] = emotionalIntelligenceSignals
        }

        // Mindfulness signals
        val mindfulnessSignals = detectSignals(
            entries,
            keywords = listOf(
                "can't focus", "distracted", "racing thoughts", "can't be present",
                "autopilot", "mindless", "zoned out", "scattered",
                "can't concentrate", "mind wandering"
            ),
            moods = listOf("scattered", "distracted", "restless"),
            themes = listOf("focus", "presence", "awareness")
        )
        if (mindfulnessSignals.strength > 0) {
            patterns[PathType.MINDFULNESS] = mindfulnessSignals
        }

        // Confidence signals
        val confidenceSignals = detectSignals(
            entries,
            keywords = listOf(
                "not good enough", "self-doubt", "imposter", "inadequate",
                "can't do", "failure", "afraid to try", "comparing myself",
                "not confident", "insecure", "worthless"
            ),
            moods = listOf("insecure", "doubtful", "inadequate"),
            themes = listOf("confidence", "self-worth", "comparison")
        )
        if (confidenceSignals.strength > 0) {
            patterns[PathType.CONFIDENCE] = confidenceSignals
        }

        // Relationship signals
        val relationshipSignals = detectSignals(
            entries,
            keywords = listOf(
                "lonely", "conflict", "argument", "relationship problems",
                "communication issues", "misunderstood", "disconnected",
                "can't connect", "relationship struggle", "feeling alone"
            ),
            moods = listOf("lonely", "hurt", "disconnected"),
            themes = listOf("relationships", "connection", "communication")
        )
        if (relationshipSignals.strength > 0) {
            patterns[PathType.RELATIONSHIPS] = relationshipSignals
        }

        // Stress Management signals
        val stressSignals = detectSignals(
            entries,
            keywords = listOf(
                "stressed", "overwhelmed", "can't cope", "too much",
                "burnout", "exhausted", "pressure", "drowning",
                "can't handle", "breaking point", "stressed out"
            ),
            moods = listOf("stressed", "overwhelmed", "exhausted", "anxious"),
            themes = listOf("stress", "overwhelm", "burnout")
        )
        if (stressSignals.strength > 0) {
            patterns[PathType.STRESS_MANAGEMENT] = stressSignals
        }

        // Gratitude signals (inverse - lack of appreciation)
        val gratitudeSignals = detectSignals(
            entries,
            keywords = listOf(
                "nothing good", "always bad", "never works", "hate my life",
                "ungrateful", "entitled", "take for granted", "cynical",
                "pessimistic", "negative", "complaining"
            ),
            moods = listOf("bitter", "resentful", "dissatisfied"),
            themes = listOf("negativity", "dissatisfaction")
        )
        if (gratitudeSignals.strength > 0) {
            patterns[PathType.GRATITUDE] = gratitudeSignals
        }

        // Self-Compassion signals
        val selfCompassionSignals = detectSignals(
            entries,
            keywords = listOf(
                "hate myself", "so stupid", "idiot", "worthless",
                "harsh on myself", "can't forgive", "beat myself up",
                "critical", "should have", "my fault", "failure"
            ),
            moods = listOf("ashamed", "guilty", "self-critical"),
            themes = listOf("self-criticism", "shame", "guilt")
        )
        if (selfCompassionSignals.strength > 0) {
            patterns[PathType.SELF_COMPASSION] = selfCompassionSignals
        }

        // Productivity signals
        val productivitySignals = detectSignals(
            entries,
            keywords = listOf(
                "procrastinating", "can't focus", "wasting time", "unproductive",
                "behind schedule", "missed deadline", "distracted",
                "not getting things done", "overwhelmed by tasks"
            ),
            moods = listOf("frustrated", "guilty", "overwhelmed"),
            themes = listOf("productivity", "focus", "time management")
        )
        if (productivitySignals.strength > 0) {
            patterns[PathType.PRODUCTIVITY] = productivitySignals
        }

        // Anxiety signals
        val anxietySignals = detectSignals(
            entries,
            keywords = listOf(
                "anxious", "panic", "worried", "fear", "nervous",
                "on edge", "can't relax", "catastrophizing", "what if",
                "worst case", "racing heart", "can't breathe"
            ),
            moods = listOf("anxious", "fearful", "panicked", "worried"),
            themes = listOf("anxiety", "worry", "fear")
        )
        if (anxietySignals.strength > 0) {
            patterns[PathType.ANXIETY_TOOLKIT] = anxietySignals
        }

        // Sleep signals
        val sleepSignals = detectSignals(
            entries,
            keywords = listOf(
                "can't sleep", "insomnia", "tired", "exhausted",
                "sleep deprived", "tossing and turning", "racing thoughts at night",
                "can't fall asleep", "wake up", "bad sleep"
            ),
            moods = listOf("tired", "exhausted", "drained"),
            themes = listOf("sleep", "rest", "fatigue")
        )
        if (sleepSignals.strength > 0) {
            patterns[PathType.SLEEP_WELLNESS] = sleepSignals
        }

        return patterns
    }

    /**
     * Detect recommendation signals in entries
     */
    private fun detectSignals(
        entries: List<JournalEntryEntity>,
        keywords: List<String>,
        moods: List<String>,
        themes: List<String>
    ): RecommendationSignals {
        var strength = 0f
        val matchedEntryIds = mutableListOf<Long>()
        val matchedPatterns = mutableListOf<String>()

        entries.forEach { entry ->
            val content = entry.content.lowercase()
            val entryMood = entry.mood.lowercase()
            val entryThemes = entry.aiThemes?.lowercase() ?: ""

            var entryMatches = 0

            // Check keywords
            keywords.forEach { keyword ->
                if (content.contains(keyword.lowercase())) {
                    entryMatches++
                    if (!matchedPatterns.contains(keyword)) {
                        matchedPatterns.add(keyword)
                    }
                }
            }

            // Check moods
            if (moods.any { entryMood.contains(it.lowercase()) }) {
                entryMatches += 2 // Mood matches weighted higher
            }

            // Check themes
            themes.forEach { theme ->
                if (entryThemes.contains(theme.lowercase())) {
                    entryMatches++
                }
            }

            if (entryMatches > 0) {
                matchedEntryIds.add(entry.id)
                strength += entryMatches.toFloat()
            }
        }

        return RecommendationSignals(
            strength = strength,
            entryIds = matchedEntryIds,
            patterns = matchedPatterns
        )
    }

    /**
     * Analyze theme patterns across entries
     */
    private fun analyzeThemePatterns(entries: List<JournalEntryEntity>): Map<String, Int> {
        val themes = mutableMapOf<String, Int>()

        entries.forEach { entry ->
            entry.aiThemes?.split(",")?.forEach { theme ->
                val cleanTheme = theme.trim().lowercase()
                if (cleanTheme.isNotBlank()) {
                    themes[cleanTheme] = themes.getOrDefault(cleanTheme, 0) + 1
                }
            }
        }

        return themes.filter { it.value >= 3 } // Theme must appear at least 3 times
    }

    /**
     * Analyze mood trends over time
     */
    private fun analyzeMoodTrends(entries: List<JournalEntryEntity>): Map<String, Int> {
        return entries
            .groupingBy { it.mood.lowercase() }
            .eachCount()
            .filter { it.value >= 2 }
    }

    /**
     * Calculate confidence score for a recommendation
     */
    private fun calculateConfidence(signals: RecommendationSignals, totalEntries: Int): Float {
        if (totalEntries == 0) return 0f

        val frequencyScore = signals.entryIds.size.toFloat() / totalEntries
        val intensityScore = minOf(signals.strength / 10f, 1f) // Normalize to 0-1
        val diversityScore = minOf(signals.patterns.size.toFloat() / 5f, 1f) // Diversity of patterns

        return (frequencyScore * 0.4f + intensityScore * 0.4f + diversityScore * 0.2f)
            .coerceIn(0f, 1f)
    }

    /**
     * Generate human-readable recommendation reason
     */
    private fun generateRecommendationReason(
        pathType: PathType,
        signals: RecommendationSignals
    ): String {
        val entryCount = signals.entryIds.size
        val topPatterns = signals.patterns.take(3)

        return when (pathType) {
            PathType.EMOTIONAL_INTELLIGENCE -> buildString {
                append("Your recent journal entries suggest you're navigating complex emotions. ")
                append("In $entryCount entries, you've explored themes like ${topPatterns.joinToString(", ")}. ")
                append("The Emotional Intelligence path can help you understand and work with your emotions more effectively.")
            }

            PathType.MINDFULNESS -> buildString {
                append("I notice patterns of distraction and overwhelm in your recent writing. ")
                append("Across $entryCount entries, you've mentioned ${topPatterns.joinToString(", ")}. ")
                append("Mindfulness practice can help you find more presence and peace in daily life.")
            }

            PathType.CONFIDENCE -> buildString {
                append("You've been wrestling with self-doubt in your recent entries. ")
                append("$entryCount entries touched on ${topPatterns.joinToString(", ")}. ")
                append("The Confidence path offers practical tools to build genuine self-trust.")
            }

            PathType.RELATIONSHIPS -> buildString {
                append("Your journaling reveals relationship challenges you're working through. ")
                append("In $entryCount entries, you explored ${topPatterns.joinToString(", ")}. ")
                append("This path can help you build healthier, more fulfilling connections.")
            }

            PathType.STRESS_MANAGEMENT -> buildString {
                append("I see significant stress patterns in your recent entries. ")
                append("$entryCount entries mentioned ${topPatterns.joinToString(", ")}. ")
                append("Learning stress resilience techniques could bring you relief and balance.")
            }

            PathType.GRATITUDE -> buildString {
                append("Your recent writing shows a pattern of negative focus. ")
                append("Across $entryCount entries, I noticed ${topPatterns.joinToString(", ")}. ")
                append("A gratitude practice could help shift your perspective and mood.")
            }

            PathType.SELF_COMPASSION -> buildString {
                append("You're being quite hard on yourself in your recent reflections. ")
                append("$entryCount entries included ${topPatterns.joinToString(", ")}. ")
                append("The Self-Compassion path teaches you to be kinder to yourself.")
            }

            PathType.PRODUCTIVITY -> buildString {
                append("Your entries reveal struggles with focus and productivity. ")
                append("In $entryCount entries, you mentioned ${topPatterns.joinToString(", ")}. ")
                append("This path offers strategies for mindful, sustainable productivity.")
            }

            PathType.ANXIETY_TOOLKIT -> buildString {
                append("Anxiety has been a recurring theme in your recent writing. ")
                append("$entryCount entries touched on ${topPatterns.joinToString(", ")}. ")
                append("This toolkit provides practical techniques for managing anxious moments.")
            }

            PathType.SLEEP_WELLNESS -> buildString {
                append("Sleep challenges are showing up frequently in your entries. ")
                append("Across $entryCount entries, you've written about ${topPatterns.joinToString(", ")}. ")
                append("Better sleep practices could transform your wellbeing.")
            }
        }
    }

    /**
     * Data class to hold recommendation signals
     */
    private data class RecommendationSignals(
        val strength: Float,
        val entryIds: List<Long>,
        val patterns: List<String>
    )
}
