package com.prody.prashant.domain.wrapped

import com.prody.prashant.domain.model.MoodTrend
import java.time.DayOfWeek
import java.time.Month

/**
 * Domain models for Yearly Wrapped feature.
 *
 * These models represent the business logic layer, providing a clean
 * separation from database entities and UI state.
 */

/**
 * Complete Yearly Wrapped summary
 */
data class YearlyWrapped(
    val id: Long = 0,
    val year: Int,
    val generatedAt: Long,
    val stats: YearStats,
    val moodJourney: MoodJourney,
    val themes: List<ThemeHighlight>,
    val growthAreas: List<GrowthArea>,
    val challenges: List<ChallengeOvercome>,
    val keyMoments: List<KeyMoment>,
    val patterns: List<Pattern>,
    val narratives: YearNarratives,
    val shareableCards: List<ShareableCard>,
    val isViewed: Boolean = false,
    val isFavorite: Boolean = false,
    val viewedAt: Long? = null
)

/**
 * Comprehensive statistics for the year
 */
data class YearStats(
    // Writing stats
    val totalEntries: Int,
    val totalMicroEntries: Int,
    val wordsWritten: Long,
    val averageWordsPerEntry: Int,
    val longestEntry: Int,
    val longestEntryId: Long? = null,

    // Engagement stats
    val activeDays: Int,
    val longestStreak: Int,
    val meditationMinutes: Int,
    val bloomsCompleted: Int,

    // Learning stats
    val wordsLearned: Int,
    val wordsUsed: Int,
    val idiomsExplored: Int,
    val proverbsDiscovered: Int,

    // Time capsule stats
    val messagesWritten: Int,
    val messagesReceived: Int,
    val mostDistantMessage: Long, // Days into future

    // Activity patterns
    val mostActiveMonth: Month?,
    val mostActiveDay: DayOfWeek?,
    val mostActiveTime: TimeOfDay?,
    val firstEntryDate: Long?,
    val lastEntryDate: Long?
)

/**
 * Mood journey throughout the year
 */
data class MoodJourney(
    val averageMood: Float, // 1-10
    val trend: MoodTrend,
    val mostCommonMood: String?,
    val moodVariety: Int, // Number of different moods
    val brightestMonth: Month?,
    val mostReflectiveMonth: Month?,
    val monthlyAverages: List<Float> // 12 values, one per month
)

/**
 * Time of day categorization
 */
enum class TimeOfDay {
    MORNING,    // 5am-12pm
    AFTERNOON,  // 12pm-5pm
    EVENING,    // 5pm-9pm
    NIGHT       // 9pm-5am
}

/**
 * Theme discovered in journal entries
 */
data class ThemeHighlight(
    val theme: String,
    val count: Int,
    val trend: ThemeTrend,
    val exampleEntry: String? = null, // Snippet from entry
    val exampleEntryId: Long? = null
)

/**
 * Theme trend throughout the year
 */
enum class ThemeTrend {
    GROWING,     // Increasingly mentioned
    STABLE,      // Consistently mentioned
    DECLINING,   // Less mentioned over time
    EMERGING,    // Recently started appearing
    RESOLVED     // Was mentioned early, then stopped
}

/**
 * Growth area identified
 */
data class GrowthArea(
    val area: String,
    val description: String,
    val entriesCount: Int,
    val periodStart: Long?,
    val periodEnd: Long?,
    val evolution: GrowthEvolution
)

/**
 * How growth evolved
 */
enum class GrowthEvolution {
    BREAKTHROUGH,  // Major progress made
    STEADY,        // Consistent growth
    EMERGING,      // Just starting to grow
    CHALLENGED     // Still working through it
}

/**
 * Challenge overcome during the year
 */
data class ChallengeOvercome(
    val challenge: String,
    val period: String, // "Early Year", "Mid Year", "Recent"
    val insight: String? = null
)

/**
 * Key moment from the year
 */
data class KeyMoment(
    val entryId: Long,
    val date: Long,
    val snippet: String,
    val why: String, // Why this was highlighted
    val mood: String?,
    val type: MomentType
)

/**
 * Type of key moment
 */
enum class MomentType {
    BREAKTHROUGH,   // Major realization
    MILESTONE,      // Achievement unlocked
    TURNING_POINT,  // Change in direction
    PEAK_HAPPINESS, // Happiest moment
    DEEP_REFLECTION,// Most insightful
    VULNERABILITY,  // Most open/honest
    GROWTH,         // Clear growth shown
    GRATITUDE       // Gratitude moment
}

/**
 * Pattern identified in journaling behavior
 */
data class Pattern(
    val type: PatternType,
    val description: String,
    val occurrences: Int
)

/**
 * Type of pattern
 */
enum class PatternType {
    MORNING_WRITER,
    EVENING_REFLECTOR,
    WEEKEND_WARRIOR,
    CONSISTENT_JOURNALER,
    BURST_WRITER,
    DEEP_THINKER,
    CONCISE_REFLECTOR,
    MOOD_TRACKER,
    GOAL_SETTER,
    GRATITUDE_PRACTITIONER
}

/**
 * AI-generated narratives
 */
data class YearNarratives(
    val opening: String?,
    val yearSummary: String?,
    val growthStory: String?,
    val moodJourney: String?,
    val lookingAhead: String?,
    val milestone: String?
)

/**
 * Shareable card for social media
 */
data class ShareableCard(
    val id: String,
    val type: CardType,
    val title: String,
    val content: String,
    val stat: String?,
    val backgroundGradient: List<String>, // Hex colors
    val textColor: String = "#FFFFFF",
    val accentColor: String = "#36F97F"
)

/**
 * Type of shareable card
 */
enum class CardType {
    TOTAL_WORDS,
    LONGEST_STREAK,
    MOOD_JOURNEY,
    TOP_THEME,
    GROWTH_STORY,
    WORDS_LEARNED,
    ACTIVE_DAYS,
    KEY_INSIGHT,
    YEAR_SUMMARY,
    MOST_COMMON_MOOD
}

/**
 * Slide configuration for wrapped experience
 */
data class WrappedSlide(
    val id: String,
    val type: SlideType,
    val order: Int,
    val autoAdvanceDelay: Long = 0L // 0 = manual advance
)

/**
 * Type of slide in wrapped experience
 */
enum class SlideType {
    OPENING,              // Welcome to your year
    STATS_OVERVIEW,       // High-level stats
    WRITING_STATS,        // Entries, words, etc.
    ENGAGEMENT_STATS,     // Streaks, active days
    LEARNING_STATS,       // Vocabulary, wisdom
    MOOD_JOURNEY,         // Mood evolution
    MOOD_HIGHLIGHTS,      // Brightest/reflective months
    TOP_THEMES,           // Most written about themes
    GROWTH_AREAS,         // Areas of growth
    CHALLENGES,           // Obstacles overcome
    KEY_MOMENTS,          // Highlighted entries
    PATTERNS,             // Journaling patterns
    NARRATIVES,           // AI-generated stories
    LOOKING_AHEAD,        // Encouragement for next year
    SHAREABLE_CARDS,      // Share your year
    CELEBRATION           // Final celebration
}

/**
 * Slide navigation state
 */
data class SlideNavigation(
    val currentSlide: Int = 0,
    val totalSlides: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isAutoAdvancing: Boolean = false
)

/**
 * Wrapped generation config
 */
data class WrappedGenerationConfig(
    val year: Int,
    val includeNarratives: Boolean = true,
    val generateShareableCards: Boolean = true,
    val minEntriesRequired: Int = 5, // Minimum entries to generate wrapped
    val topThemesCount: Int = 5,
    val keyMomentsCount: Int = 5,
    val growthAreasCount: Int = 3,
    val challengesCount: Int = 3
)

/**
 * Wrapped generation status
 */
sealed class WrappedGenerationStatus {
    object NotStarted : WrappedGenerationStatus()
    data class InProgress(val stage: GenerationStage, val progress: Int) : WrappedGenerationStatus()
    data class Completed(val wrapped: YearlyWrapped) : WrappedGenerationStatus()
    data class Failed(val reason: String) : WrappedGenerationStatus()
}

/**
 * Stage of wrapped generation
 */
enum class GenerationStage {
    COLLECTING_DATA,
    ANALYZING_STATS,
    ANALYZING_MOOD,
    EXTRACTING_THEMES,
    IDENTIFYING_GROWTH,
    DETECTING_CHALLENGES,
    SELECTING_KEY_MOMENTS,
    GENERATING_NARRATIVES,
    CREATING_SHAREABLE_CARDS,
    FINALIZING
}
