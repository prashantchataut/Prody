package com.prody.prashant.domain.intelligence

import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.domain.model.isPositive
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ln

/**
 * ================================================================================================
 * PATTERN ANALYSIS ENGINE - Local Recurring Theme Detection
 * ================================================================================================
 *
 * Analyzes journal entries purely on-device to find recurring themes and patterns.
 * Uses TF-IDF-like scoring to surface the most meaningful words and themes from
 * the user's writing, then groups them into actionable pattern insights.
 *
 * No external API calls are made. All computation happens locally.
 *
 * Key capabilities:
 * - Extract recurring themes from aiThemes fields and raw content
 * - Score terms using TF-IDF to surface meaningful (not common) words
 * - Detect mood trends over a lookback window
 * - Provide contextual suggestions based on detected themes
 * - Supply one-liner pattern context for the active journal editor
 */
@Singleton
class PatternAnalysisEngine @Inject constructor(
    private val journalDao: JournalDao,
    private val preferencesManager: PreferencesManager
) {

    // =============================================================================================
    // PUBLIC DATA MODELS
    // =============================================================================================

    data class PersonalizedPattern(
        val theme: String,
        val occurrenceCount: Int,
        val timespan: String,
        val sampleSnippet: String,
        val suggestion: String
    )

    data class PatternAnalysisResult(
        val patterns: List<PersonalizedPattern>,
        val dominantTheme: String?,
        val weeklyMoodTrend: String,
        val analysisTimestamp: Long,
        val entryCount: Int
    )

    // =============================================================================================
    // PUBLIC API
    // =============================================================================================

    /**
     * Analyze journal entries from the last [lookbackDays] days for recurring patterns.
     *
     * Returns null when the feature is disabled via preferences or when there are
     * fewer than [MIN_ENTRIES_REQUIRED] entries in the lookback window.
     */
    suspend fun analyzePatterns(lookbackDays: Int = 7): PatternAnalysisResult? {
        if (!isFeatureEnabled()) return null

        val cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(lookbackDays.toLong())
        val allRecent = journalDao.getRecentEntriesSync(MAX_ENTRIES_TO_FETCH)
        val entries = allRecent.filter { it.createdAt >= cutoff }

        if (entries.size < MIN_ENTRIES_REQUIRED) return null

        val timespan = formatTimespan(lookbackDays)
        val themeOccurrences = buildThemeOccurrenceMap(entries)
        val topThemes = themeOccurrences.entries
            .sortedByDescending { it.value }
            .take(MAX_PATTERNS)

        val patterns = topThemes.map { (theme, count) ->
            val snippet = findSnippetForTheme(entries, theme)
            PersonalizedPattern(
                theme = theme,
                occurrenceCount = count,
                timespan = timespan,
                sampleSnippet = snippet,
                suggestion = getSuggestionForTheme(theme)
            )
        }

        val dominantTheme = patterns.firstOrNull()?.theme
        val moodTrend = calculateMoodTrend(entries)

        return PatternAnalysisResult(
            patterns = patterns,
            dominantTheme = dominantTheme,
            weeklyMoodTrend = moodTrend,
            analysisTimestamp = System.currentTimeMillis(),
            entryCount = entries.size
        )
    }

    /**
     * Return the single most relevant pattern for home screen display, or null
     * if no pattern is available.
     */
    suspend fun getTopPatternForHome(): PersonalizedPattern? {
        val result = analyzePatterns() ?: return null
        return result.patterns.firstOrNull()
    }

    /**
     * Given the text the user is currently writing, check whether it touches a
     * theme that has already appeared multiple times this week.
     *
     * Returns a human-readable one-liner such as
     * "You've been writing about 'work stress' 3 times this week"
     * or null when no matching pattern is found.
     */
    suspend fun getPatternContextForJournal(currentContent: String): String? {
        if (!isFeatureEnabled()) return null

        val weekStart = getWeekStartMillis()
        val weeklyThemeStrings = journalDao.getThemesThisWeek(weekStart)
        val weeklyThemeCounts = parseThemeCounts(weeklyThemeStrings)

        val contentWords = tokenize(currentContent)
        if (contentWords.isEmpty()) return null

        // Find the best matching theme that the user has written about more than once
        val match = weeklyThemeCounts.entries
            .filter { (theme, count) -> count >= 2 && contentWords.any { word -> theme.contains(word) || word.contains(theme) } }
            .maxByOrNull { it.value }
            ?: return null

        return "You've been writing about '${match.key}' ${match.value} times this week"
    }

    // =============================================================================================
    // THEME EXTRACTION
    // =============================================================================================

    /**
     * Build a map of theme -> occurrence count by combining structured aiThemes
     * with TF-IDF scored content words.
     */
    private fun buildThemeOccurrenceMap(entries: List<JournalEntryEntity>): Map<String, Int> {
        val combined = mutableMapOf<String, Int>()

        // 1. Structured themes from aiThemes field
        val aiThemeCounts = extractAiThemeCounts(entries)
        aiThemeCounts.forEach { (theme, count) ->
            combined[theme] = (combined[theme] ?: 0) + count
        }

        // 2. TF-IDF scored content words
        val tfidfTerms = computeTfIdfTopTerms(entries)
        tfidfTerms.forEach { (term, score) ->
            // Merge only if the term was not already captured by aiThemes
            if (term !in combined) {
                // Convert fractional score to an integer occurrence count:
                // we count in how many entries the term actually appeared
                val docCount = entries.count { term in tokenize(it.content) }
                if (docCount >= 2) {
                    combined[term] = docCount
                }
            }
        }

        return combined
    }

    /**
     * Parse all comma-separated aiThemes across entries and count occurrences.
     */
    private fun extractAiThemeCounts(entries: List<JournalEntryEntity>): Map<String, Int> {
        return entries
            .mapNotNull { it.aiThemes }
            .flatMap { it.split(",") }
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }
            .groupingBy { it }
            .eachCount()
    }

    // =============================================================================================
    // TF-IDF SCORING
    // =============================================================================================

    /**
     * Compute TF-IDF scores across all [entries] and return the top scoring terms.
     *
     * TF  = (frequency of term in document) / (total terms in document)
     * IDF = ln(total documents / documents containing term)
     *
     * High TF-IDF means a word is frequent in some entries but not ubiquitous
     * across all of them, making it a distinguishing theme.
     */
    private fun computeTfIdfTopTerms(
        entries: List<JournalEntryEntity>,
        topN: Int = MAX_TFIDF_TERMS
    ): List<Pair<String, Double>> {
        if (entries.isEmpty()) return emptyList()

        val totalDocs = entries.size.toDouble()

        // Tokenize every document
        val tokenizedDocs = entries.map { tokenize(it.content) }

        // Document frequency: how many documents contain each term
        val docFrequency = mutableMapOf<String, Int>()
        tokenizedDocs.forEach { tokens ->
            tokens.toSet().forEach { term ->
                docFrequency[term] = (docFrequency[term] ?: 0) + 1
            }
        }

        // Aggregate TF-IDF: sum the TF-IDF score for each term across all documents
        val aggregateScores = mutableMapOf<String, Double>()
        tokenizedDocs.forEach { tokens ->
            if (tokens.isEmpty()) return@forEach
            val termFrequency = tokens.groupingBy { it }.eachCount()
            val docLength = tokens.size.toDouble()
            termFrequency.forEach { (term, count) ->
                val tf = count / docLength
                val df = docFrequency[term] ?: 1
                val idf = ln(totalDocs / df)
                aggregateScores[term] = (aggregateScores[term] ?: 0.0) + (tf * idf)
            }
        }

        // Filter out terms that only appear in a single document
        return aggregateScores.entries
            .filter { (term, _) -> (docFrequency[term] ?: 0) >= 2 }
            .sortedByDescending { it.value }
            .take(topN)
            .map { it.key to it.value }
    }

    // =============================================================================================
    // MOOD TREND ANALYSIS
    // =============================================================================================

    /**
     * Determine mood trend from a chronologically ordered (newest-first) list of entries.
     * Splits the window into an older half and a newer half and compares the ratio of
     * positive moods in each.
     */
    private fun calculateMoodTrend(entries: List<JournalEntryEntity>): String {
        if (entries.size < 2) return TREND_STABLE

        // entries are newest-first from the DAO; reverse so index 0 is oldest
        val chronological = entries.sortedBy { it.createdAt }
        val midpoint = chronological.size / 2
        val olderHalf = chronological.subList(0, midpoint)
        val newerHalf = chronological.subList(midpoint, chronological.size)

        val olderPositiveRatio = positiveRatio(olderHalf)
        val newerPositiveRatio = positiveRatio(newerHalf)

        val delta = newerPositiveRatio - olderPositiveRatio
        return when {
            delta > TREND_THRESHOLD -> TREND_IMPROVING
            delta < -TREND_THRESHOLD -> TREND_DECLINING
            else -> TREND_STABLE
        }
    }

    private fun positiveRatio(entries: List<JournalEntryEntity>): Float {
        if (entries.isEmpty()) return 0.5f
        val positiveCount = entries.count { Mood.fromString(it.mood).isPositive }
        return positiveCount.toFloat() / entries.size
    }

    // =============================================================================================
    // SUGGESTION MAPPING
    // =============================================================================================

    /**
     * Return a hardcoded contextual suggestion for a given theme.
     */
    private fun getSuggestionForTheme(theme: String): String {
        val lower = theme.lowercase()
        return when {
            lower.containsAny("work", "career", "job", "office", "boss", "colleague") ->
                "Consider setting boundaries"
            lower.containsAny("stress", "anxiety", "anxious", "overwhelm", "panic", "nervous") ->
                "Try a breathing exercise"
            lower.containsAny("relationship", "relationships", "partner", "friend", "family", "social") ->
                "Reach out to someone"
            lower.containsAny("health", "fitness", "exercise", "weight", "sleep", "body") ->
                "Small steps lead to big changes"
            lower.containsAny("creativity", "creative", "art", "music", "writing", "craft") ->
                "Keep nurturing your creative side"
            lower.containsAny("growth", "learning", "learn", "improve", "progress", "goal") ->
                "You're on an upward trajectory"
            else -> "Keep exploring this theme"
        }
    }

    // =============================================================================================
    // TEXT PROCESSING
    // =============================================================================================

    /**
     * Tokenize content into lowercase words, stripping punctuation and filtering out
     * stop words and very short tokens.
     */
    private fun tokenize(content: String): List<String> {
        return content
            .lowercase()
            .replace(PUNCTUATION_REGEX, " ")
            .split(WHITESPACE_REGEX)
            .filter { it.length >= MIN_TOKEN_LENGTH && it !in STOP_WORDS }
    }

    // =============================================================================================
    // HELPERS
    // =============================================================================================

    private suspend fun isFeatureEnabled(): Boolean {
        return preferencesManager.buddhaPatternTrackingEnabled.first()
    }

    private fun getWeekStartMillis(): Long {
        val now = System.currentTimeMillis()
        val dayOfWeek = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK)
        // Calendar.SUNDAY == 1; shift to make Monday == 0
        val daysSinceMonday = (dayOfWeek + 5) % 7
        return now - TimeUnit.DAYS.toMillis(daysSinceMonday.toLong()) -
            (now % TimeUnit.DAYS.toMillis(1))
    }

    /**
     * Parse the raw aiThemes strings returned from the DAO (one string per entry,
     * each a comma-separated list) into a single theme -> count map.
     */
    private fun parseThemeCounts(themeStrings: List<String>): Map<String, Int> {
        return themeStrings
            .flatMap { it.split(",") }
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }
            .groupingBy { it }
            .eachCount()
    }

    /**
     * Find the first short snippet from an entry whose content or aiThemes reference
     * the given [theme].
     */
    private fun findSnippetForTheme(entries: List<JournalEntryEntity>, theme: String): String {
        val matchingEntry = entries.firstOrNull { entry ->
            val inThemes = entry.aiThemes?.lowercase()?.contains(theme) == true
            val inContent = entry.content.lowercase().contains(theme)
            inThemes || inContent
        } ?: entries.first()

        return matchingEntry.content.take(SNIPPET_MAX_LENGTH).let { snippet ->
            if (matchingEntry.content.length > SNIPPET_MAX_LENGTH) "$snippet..." else snippet
        }
    }

    private fun formatTimespan(days: Int): String {
        return when {
            days == 1 -> "today"
            days <= 7 -> "this week"
            days <= 14 -> "the last 2 weeks"
            days <= 30 -> "this month"
            else -> "the last $days days"
        }
    }

    private fun String.containsAny(vararg keywords: String): Boolean {
        return keywords.any { this.contains(it) }
    }

    // =============================================================================================
    // CONSTANTS
    // =============================================================================================

    companion object {
        private const val MIN_ENTRIES_REQUIRED = 3
        private const val MAX_ENTRIES_TO_FETCH = 200
        private const val MAX_PATTERNS = 5
        private const val MAX_TFIDF_TERMS = 10
        private const val MIN_TOKEN_LENGTH = 3
        private const val SNIPPET_MAX_LENGTH = 120
        private const val TREND_THRESHOLD = 0.15f

        private const val TREND_IMPROVING = "improving"
        private const val TREND_DECLINING = "declining"
        private const val TREND_STABLE = "stable"

        private val PUNCTUATION_REGEX = Regex("[^a-zA-Z0-9\\s]")
        private val WHITESPACE_REGEX = Regex("\\s+")

        /**
         * Common English stop words to exclude from TF-IDF scoring.
         */
        private val STOP_WORDS = setOf(
            // Articles & determiners
            "the", "a", "an", "this", "that", "these", "those",
            // Pronouns
            "i", "me", "my", "myself", "we", "our", "ours", "ourselves",
            "you", "your", "yours", "yourself", "yourselves",
            "he", "him", "his", "himself", "she", "her", "hers", "herself",
            "it", "its", "itself", "they", "them", "their", "theirs", "themselves",
            // Prepositions
            "in", "on", "at", "to", "for", "with", "from", "by", "about",
            "into", "through", "during", "before", "after", "above", "below",
            "between", "under", "over", "out", "up", "down", "off", "against",
            // Conjunctions
            "and", "but", "or", "nor", "so", "yet", "both", "either", "neither",
            // Auxiliary / modal verbs
            "is", "am", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "having", "do", "does", "did", "doing",
            "will", "would", "shall", "should", "can", "could", "may", "might", "must",
            // Common verbs
            "get", "got", "getting", "make", "made", "making", "go", "going", "gone",
            "come", "came", "take", "took", "give", "gave", "say", "said",
            "know", "knew", "think", "thought", "see", "saw", "want", "wanted",
            "look", "looked", "use", "used", "find", "found", "tell", "told",
            // Adverbs & adjectives
            "not", "no", "very", "really", "just", "also", "too", "more", "most",
            "much", "many", "some", "any", "all", "each", "every", "other",
            "such", "only", "own", "same", "than", "then", "now", "here",
            "there", "when", "where", "how", "what", "which", "who", "whom",
            "why", "if", "because", "as", "until", "while", "of", "even",
            // Common filler / low-signal words
            "like", "well", "back", "still", "way", "thing", "things",
            "day", "time", "lot", "bit", "able", "around", "always",
            "never", "something", "anything", "everything", "nothing",
            "today", "yesterday", "tomorrow", "been", "being", "enough",
            "keep", "let", "put", "try", "seem", "need", "feel", "felt",
            "become", "began", "begin", "already", "since", "ago", "though",
            "actually", "probably", "maybe", "almost", "quite", "rather"
        )
    }
}
