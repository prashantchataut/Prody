package com.prody.prashant.domain.intelligence

import com.prody.prashant.domain.model.Mood
import com.prody.prashant.domain.model.MoodTrend
import com.prody.prashant.domain.model.TimeOfDay
import com.prody.prashant.domain.streak.DualStreakStatus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.time.Duration

/**
 * ================================================================================================
 * PRODY SOUL LAYER - INTELLIGENCE MODELS
 * ================================================================================================
 *
 * The Soul Layer is the invisible intelligence that makes Prody feel alive.
 * These models represent the unified understanding of each user, enabling
 * personalized, context-aware interactions that feel human, not algorithmic.
 *
 * Key Principles:
 * - Every interaction should feel like it comes from something that knows you
 * - Data synthesis over data dumping - intelligent interpretation, not raw aggregation
 * - Delight through unexpected relevance, not constant notifications
 * - Warm without being saccharine, wise without being preachy
 */

// ================================================================================================
// USER CONTEXT - The Core Understanding
// ================================================================================================

/**
 * The brain of Prody's understanding of a user.
 * This is synthesized context, not raw data. Every field represents
 * intelligent interpretation of signals, not just database queries.
 */
data class UserContext(
    // === IDENTITY ===
    val displayName: String,
    val daysWithPrody: Int,
    val userArchetype: UserArchetype,
    val totalEntries: Int,

    // === TEMPORAL ===
    val timeOfDay: TimeOfDay,
    val dayOfWeek: DayOfWeek,
    val isWeekend: Boolean,
    val season: Season,
    val specialDay: SpecialDay?,

    // === EMOTIONAL STATE (Inferred) ===
    val recentMoodTrend: MoodTrend,
    val dominantMood: Mood?,
    val emotionalEnergy: EnergyLevel,
    val stressSignals: List<StressSignal>,

    // === BEHAVIORAL PATTERNS ===
    val engagementLevel: EngagementLevel,
    val lastActiveDate: LocalDate?,
    val daysSinceLastEntry: Int,
    val preferredJournalTime: TimeOfDay?,
    val averageSessionDuration: Duration,
    val preferredFeatures: List<UnlockableFeature>,

    // === GROWTH PATTERNS ===
    val currentStreak: DualStreakStatus,
    val recentThemes: List<String>,
    val recurringChallenges: List<String>,
    val growthAreas: List<SoulGrowthArea>,
    val recentWins: List<Win>,

    // === RELATIONSHIP WITH APP ===
    val trustLevel: TrustLevel,
    val hasCompletedOnboarding: Boolean,
    val hasTalkedToHaven: Boolean,
    val hasSharedDeepContent: Boolean,
    val preferredTone: CommunicationTone,

    // === FIRST WEEK STATE ===
    val firstWeekStage: FirstWeekStage?,
    val isInFirstWeek: Boolean
) {
    companion object {
        fun empty(): UserContext = UserContext(
            displayName = "Friend",
            daysWithPrody = 0,
            userArchetype = UserArchetype.EXPLORER,
            totalEntries = 0,
            timeOfDay = TimeOfDay.MORNING,
            dayOfWeek = DayOfWeek.MONDAY,
            isWeekend = false,
            season = Season.current(),
            specialDay = null,
            recentMoodTrend = MoodTrend.STABLE,
            dominantMood = null,
            emotionalEnergy = EnergyLevel.MEDIUM,
            stressSignals = emptyList(),
            engagementLevel = EngagementLevel.NEW,
            lastActiveDate = null,
            daysSinceLastEntry = 0,
            preferredJournalTime = null,
            averageSessionDuration = Duration.ZERO,
            preferredFeatures = emptyList(),
            currentStreak = DualStreakStatus.empty(),
            recentThemes = emptyList(),
            recurringChallenges = emptyList(),
            growthAreas = emptyList(),
            recentWins = emptyList(),
            trustLevel = TrustLevel.NEW,
            hasCompletedOnboarding = false,
            hasTalkedToHaven = false,
            hasSharedDeepContent = false,
            preferredTone = CommunicationTone.WARM,
            firstWeekStage = FirstWeekStage.DAY_1_FIRST_OPEN,
            isInFirstWeek = true
        )
    }

    /**
     * Checks if user appears to be going through a difficult time.
     * Used to adjust tone and content across the app.
     */
    val isStruggling: Boolean
        get() = recentMoodTrend == MoodTrend.DECLINING ||
                emotionalEnergy == EnergyLevel.LOW ||
                stressSignals.isNotEmpty() ||
                userArchetype == UserArchetype.STRUGGLING

    /**
     * Checks if user is thriving - used for celebratory moments.
     */
    val isThriving: Boolean
        get() = recentMoodTrend == MoodTrend.IMPROVING &&
                emotionalEnergy == EnergyLevel.HIGH &&
                userArchetype == UserArchetype.THRIVING

    /**
     * Gets the first name for more personal greetings.
     */
    val firstName: String
        get() = displayName.split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: "Friend"
}

// ================================================================================================
// USER ARCHETYPES - Behavioral Classification
// ================================================================================================

/**
 * User archetype based on behavioral patterns.
 * Not a permanent label - changes as user behavior evolves.
 */
enum class UserArchetype(
    val description: String,
    val needsExtra: String
) {
    EXPLORER(
        description = "New user, discovering features",
        needsExtra = "Guided discovery, not overwhelm"
    ),
    CONSISTENT(
        description = "Regular user, established patterns",
        needsExtra = "Depth over novelty, recognition of commitment"
    ),
    STRUGGLING(
        description = "Showing signs of difficulty",
        needsExtra = "Gentleness, support, reduced pressure"
    ),
    THRIVING(
        description = "Positive trajectory, engaged",
        needsExtra = "Celebration, challenge, growth opportunities"
    ),
    RETURNING(
        description = "Was gone, came back",
        needsExtra = "Welcome back, no guilt, fresh start"
    ),
    SPORADIC(
        description = "Engages irregularly",
        needsExtra = "Low-pressure engagement, value in each visit"
    )
}

// ================================================================================================
// MOOD & EMOTIONAL PATTERNS
// ================================================================================================

/**
 * Inferred energy level from writing patterns and engagement.
 */
enum class EnergyLevel(val description: String) {
    LOW("Short entries, less engagement, heavier themes"),
    MEDIUM("Normal engagement patterns"),
    HIGH("Longer entries, frequent engagement, lighter themes")
}

/**
 * Detected signals of stress or struggle.
 * Identified through journal content analysis.
 */
data class StressSignal(
    val type: StressSignalType,
    val confidence: Float, // 0.0 to 1.0
    val detectedAt: LocalDateTime,
    val context: String? = null // Brief context for why this was detected
)

enum class StressSignalType(val description: String) {
    NEGATIVE_LANGUAGE("Consistently negative word choices"),
    SLEEP_ISSUES("Mentions of sleep problems"),
    OVERWHELM("Expressions of being overwhelmed"),
    ISOLATION("Signs of social isolation"),
    WORK_STRESS("Work-related stress mentions"),
    RELATIONSHIP_STRAIN("Relationship difficulties"),
    HEALTH_CONCERNS("Health-related worries"),
    ANXIETY_MARKERS("Anxious thought patterns"),
    LOW_SELF_WORTH("Self-critical language")
}

// ================================================================================================
// ENGAGEMENT PATTERNS
// ================================================================================================

/**
 * User's engagement level with the app.
 */
enum class EngagementLevel(val daysThreshold: IntRange, val description: String) {
    NEW(0..7, "First week of usage"),
    DAILY(0..1, "Active every day or almost every day"),
    REGULAR(2..3, "Consistent but not daily"),
    SPORADIC(4..7, "Irregular engagement"),
    CHURNING(8..14, "At risk of abandoning"),
    RETURNING(Int.MAX_VALUE..Int.MAX_VALUE, "Returning after extended absence")
}

/**
 * Available features in the app for tracking preferences and progressive unlocking.
 *
 * Renamed from Feature to avoid collision with
 * com.prody.prashant.domain.wellbeing.Feature.
 */
enum class UnlockableFeature(val displayName: String, val unlockDay: Int) {
    JOURNAL("Journal", 0),
    DAILY_WISDOM("Daily Wisdom", 0),
    FUTURE_MESSAGE("Future Messages", 2),
    HAVEN("Haven Therapist", 3),
    VOCABULARY("Vocabulary Building", 0),
    STATS("Statistics", 5),
    GAMIFICATION("Skills & Achievements", 5),
    LEARNING_PATHS("Learning Paths", 7),
    SOCIAL_CIRCLES("Accountability Circles", 7),
    DEEP_DIVE("Deep Dive Reflections", 7),
    WIDGETS("Home Screen Widgets", 0),
    YEARLY_WRAPPED("Yearly Wrapped", 365)
}

// ================================================================================================
// GROWTH TRACKING
// ================================================================================================

/**
 * Identified area where user is showing growth.
 *
 * Renamed from GrowthArea to avoid collision with
 * com.prody.prashant.domain.wrapped.GrowthArea.
 */
data class SoulGrowthArea(
    val theme: String,
    val progress: Float, // 0.0 to 1.0
    val evidence: List<String>, // Brief notes of what showed growth
    val firstMentioned: LocalDate,
    val lastMentioned: LocalDate
)

/**
 * A win or achievement to celebrate.
 */
data class Win(
    val type: WinType,
    val title: String,
    val description: String,
    val date: LocalDate,
    val shouldCelebrate: Boolean = true
)

enum class WinType {
    STREAK_MILESTONE,      // Hit a streak milestone
    FIRST_OF_KIND,         // First journal, first Haven session, etc.
    CONSISTENCY,           // Maintained habit
    GROWTH_INDICATOR,      // Showed growth in an area
    CHALLENGE_OVERCOME,    // Addressed a recurring challenge
    VULNERABILITY,         // Shared something deep
    PATTERN_BREAK          // Broke a negative pattern
}

// ================================================================================================
// TRUST & RELATIONSHIP
// ================================================================================================

/**
 * Level of trust/relationship depth with the app.
 * Affects how personal and direct content can be.
 */
enum class TrustLevel(val description: String) {
    NEW("Just started, still cautious"),
    BUILDING("Getting comfortable, some vulnerability"),
    ESTABLISHED("Regular user, shares openly"),
    DEEP("Long-term user, shares deeply personal content")
}

/**
 * Preferred communication tone for this user.
 * Inferred from their writing style and responses.
 */
enum class CommunicationTone(val description: String) {
    WARM("Gentle, supportive, encouraging"),
    DIRECT("Straightforward, practical, efficient"),
    PLAYFUL("Lighter, with occasional humor"),
    GENTLE("Extra soft touch, especially careful with words")
}

// ================================================================================================
// TEMPORAL AWARENESS
// ================================================================================================

/**
 * Current season for seasonal content.
 */
enum class Season(val months: IntRange) {
    WINTER(12..2),
    SPRING(3..5),
    SUMMER(6..8),
    FALL(9..11);

    companion object {
        fun current(): Season {
            val month = LocalDate.now().monthValue
            return entries.find { season ->
                if (season.months.first > season.months.last) {
                    // Handle WINTER which wraps around the year
                    month >= season.months.first || month <= season.months.last
                } else {
                    month in season.months
                }
            } ?: SPRING
        }

        fun fromMonth(month: Int): Season {
            return entries.find { season ->
                if (season.months.first > season.months.last) {
                    month >= season.months.first || month <= season.months.last
                } else {
                    month in season.months
                }
            } ?: SPRING
        }
    }
}

/**
 * Special days that warrant unique content.
 */
sealed class SpecialDay(val displayName: String, val shouldCelebrate: Boolean = true) {
    object NewYear : SpecialDay("New Year")
    object Birthday : SpecialDay("Your Birthday")
    data class ProdyAnniversary(val yearsWithPrody: Int) : SpecialDay(
        "Your Prody Anniversary",
        shouldCelebrate = true
    )
    data class FirstEntryAnniversary(val yearsAgo: Int) : SpecialDay(
        "First Entry Anniversary"
    )
    object MentalHealthDay : SpecialDay("World Mental Health Day")
    object GratitudeDay : SpecialDay("Gratitude Day")
    data class Custom(val name: String) : SpecialDay(name)
}

// ================================================================================================
// FIRST WEEK JOURNEY
// ================================================================================================

/**
 * State machine for first week experience.
 * Tracks progress through the curated first week journey.
 */
enum class FirstWeekStage(
    val dayNumber: Int,
    val stageName: String,
    val isComplete: Boolean = false
) {
    // Day 1
    DAY_1_FIRST_OPEN(1, "First Open"),
    DAY_1_FIRST_ENTRY(1, "First Entry"),
    DAY_1_FIRST_WISDOM(1, "First Wisdom"),

    // Day 2
    DAY_2_RETURNING(2, "Returning User"),
    DAY_2_SECOND_ENTRY(2, "Second Entry"),

    // Day 3
    DAY_3_EXPLORING(3, "Exploring Features"),
    DAY_3_MET_BUDDHA(3, "Met Buddha AI"),

    // Day 4
    DAY_4_DEEPENING(4, "Deepening Practice"),
    DAY_4_TRIED_FEATURE(4, "Tried New Feature"),

    // Day 5
    DAY_5_BUILDING_HABIT(5, "Building Habit"),

    // Day 6
    DAY_6_ALMOST_THERE(6, "Almost There"),

    // Day 7
    DAY_7_CELEBRATION(7, "Week One Complete"),

    // Graduated
    GRADUATED(8, "First Week Graduate", isComplete = true);

    fun next(): FirstWeekStage? {
        val allStages = entries.toList()
        val currentIndex = allStages.indexOf(this)
        return allStages.getOrNull(currentIndex + 1)
    }

    companion object {
        fun fromDayNumber(day: Int): FirstWeekStage {
            return entries.find { it.dayNumber == day && !it.isComplete } ?: GRADUATED
        }
    }
}

/**
 * Content for a first week stage.
 */
data class FirstWeekContent(
    val stage: FirstWeekStage,
    val greeting: String,
    val primaryAction: String,
    val secondaryAction: String? = null,
    val hint: String? = null,
    val celebration: Celebration? = null,
    val unlock: FeatureUnlock? = null
)

/**
 * A celebration moment to display.
 */
data class Celebration(
    val type: CelebrationType,
    val title: String,
    val message: String,
    val xpReward: Int = 0,
    val tokensReward: Int = 0
)

/**
 * Types of celebrations - combines event types and visual effects.
 * Event types: Used to categorize what is being celebrated
 * Visual effects: Used to determine how to render the celebration
 */
enum class CelebrationType {
    // Event types
    FIRST_ENTRY,
    STREAK_MILESTONE,
    WEEK_COMPLETE,
    FEATURE_DISCOVERED,
    GROWTH_MOMENT,
    VULNERABILITY_SHARED,
    PATTERN_BROKEN,

    // Visual effect types
    CONFETTI,
    FIREWORKS,
    SPARKLE,
    SPARKLES,
    GENTLE_GLOW,
    WARM_FADE,
    ACHIEVEMENT
}

/**
 * A feature unlock notification.
 */
data class FeatureUnlock(
    val feature: UnlockableFeature,
    val message: String
)

// ================================================================================================
// CONTEXT-SPECIFIC MODELS (for different features)
// ================================================================================================

/**
 * Context optimized for Buddha AI responses.
 */
data class BuddhaContext(
    val userContext: UserContext,
    val recentJournalThemes: List<String>,
    val recurringPatterns: List<String>,
    val lastBuddhaInteraction: LocalDateTime?,
    val preferredWisdomStyle: WisdomStyle,
    val avoidTopics: List<String> // Topics to approach carefully
) {
    enum class WisdomStyle {
        STOIC,       // Marcus Aurelius, Seneca
        EASTERN,     // Buddhist, Taoist
        PRACTICAL,   // Modern psychology-based
        POETIC,      // More metaphorical, artistic
        DIRECT       // Straightforward, no-nonsense
    }
}

/**
 * Context optimized for Haven therapy sessions.
 */
data class HavenContext(
    val userContext: UserContext,
    val recentJournalContent: List<String>, // Last 5 entries (previews)
    val previousSessionSummary: String?,
    val sessionCount: Int,
    val preferredTherapeuticApproach: TherapeuticApproach,
    val crisisHistory: Boolean,
    val sensitiveTriggers: List<String>
) {
    enum class TherapeuticApproach {
        CBT,         // Cognitive Behavioral Therapy techniques
        DBT,         // Dialectical Behavior Therapy
        MINDFULNESS, // Mindfulness-based approaches
        ACT,         // Acceptance and Commitment Therapy
        GENERAL      // General supportive counseling
    }
}

/**
 * Context optimized for notification decisions.
 */
data class NotificationContext(
    val userContext: UserContext,
    val lastNotificationSentAt: LocalDateTime?,
    val notificationsSentToday: Int,
    val notificationOpenRate: Float, // Historical open rate
    val preferredNotificationTimes: List<Int>, // Hours when user opens notifications
    val ignoresNotificationsAt: List<Int>, // Hours when user ignores notifications
    val lastAppOpenAt: LocalDateTime?
)

/**
 * Context optimized for home screen content.
 */
data class HomeContext(
    val userContext: UserContext,
    val hasMemoryToSurface: Boolean,
    val memoryPreview: MemoryPreview?,
    val nextSuggestedAction: SuggestedAction?,
    val featureDiscovery: FeatureDiscovery?,
    val dailyTheme: DailyTheme
)

/**
 * A memory that could be surfaced.
 */
data class MemoryPreview(
    val id: Long,
    val type: MemoryType,
    val preview: String,
    val date: LocalDate,
    val surfaceReason: String
)

/**
 * A suggested action for the user.
 */
data class SuggestedAction(
    val type: SuggestedActionType,
    val title: String,
    val subtitle: String,
    val priority: Int, // Lower is higher priority
    val route: String
)

enum class SuggestedActionType {
    WRITE_JOURNAL,
    COMPLETE_RITUAL,
    REVIEW_WORDS,
    SEND_FUTURE_MESSAGE,
    VISIT_HAVEN,
    CHECK_STATS,
    EXPLORE_FEATURE
}

/**
 * A feature discovery suggestion.
 */
data class FeatureDiscovery(
    val feature: UnlockableFeature,
    val hook: String,
    val benefit: String,
    val ctaText: String,
    val route: String
)

/**
 * Daily theme for visual and content adjustments.
 */
data class DailyTheme(
    val name: String,
    val accentColorHex: String,
    val promptStyle: PromptStyle,
    val wisdomCategory: WisdomCategory
)

enum class PromptStyle {
    INTENTIONAL,  // Monday - setting intentions
    REFLECTIVE,   // Friday - looking back
    GENTLE,       // Sunday - restoration
    ENERGETIC,    // Wednesday - midweek push
    GRATEFUL      // Thursday - gratitude focus
}

enum class WisdomCategory {
    ACTION,
    GRATITUDE,
    PERSPECTIVE,
    GROWTH,
    PEACE,
    COURAGE,
    INTROSPECTION
}

// ================================================================================================
// MEMORY SYSTEM
// ================================================================================================

/**
 * A memory that can be surfaced to create magic moments.
 */
data class Memory(
    val id: Long,
    val type: MemoryType,
    val date: LocalDate,
    val preview: String,
    val fullContent: String,
    val mood: Mood?,
    val significance: MemorySignificance,
    val surfaceReason: String // "1 year ago today", "You wrote about this theme again"
)

enum class MemoryType {
    JOURNAL_ENTRY,
    FUTURE_MESSAGE_SENT,
    FUTURE_MESSAGE_RECEIVED,
    MILESTONE_ACHIEVED,
    HAVEN_BREAKTHROUGH,
    FIRST_OF_KIND, // First entry, first Haven session, etc.
    STREAK_ACHIEVEMENT
}

enum class MemorySignificance(val surfaceWeight: Float) {
    MILESTONE(1.0f),     // First entry, 100th entry, etc.
    ANNIVERSARY(0.9f),   // Same date, previous year/month
    THEMATIC(0.6f),      // Related to what they're experiencing now
    CONTRAST(0.8f),      // Show growth - "look how far you've come"
    CALLBACK(0.5f)       // They mentioned something again
}

// ================================================================================================
// NOTIFICATION INTELLIGENCE
// ================================================================================================

/**
 * Decision about whether/how to send a notification.
 */
sealed class NotificationDecision {
    data class Send(val notification: PendingNotification) : NotificationDecision()
    data class Skip(val reason: String) : NotificationDecision()
    data class Delay(val until: LocalDateTime, val reason: String) : NotificationDecision()
    data class Modify(val newContent: IntelligentNotificationContent, val reason: String) : NotificationDecision()
    data class Reschedule(val to: LocalDateTime, val reason: String) : NotificationDecision()
}

/**
 * A notification waiting to be sent.
 */
data class PendingNotification(
    val type: NotificationType,
    val scheduledTime: LocalDateTime,
    val content: IntelligentNotificationContent
)

enum class NotificationType {
    MORNING_REMINDER,
    EVENING_REFLECTION,
    STREAK_REMINDER,
    COMEBACK,
    MEMORY_ANNIVERSARY,
    FUTURE_MESSAGE_DELIVERY,
    WEEKLY_SUMMARY,
    ACHIEVEMENT,
    HAVEN_CHECK_IN,
    GENTLE_NUDGE
}

/**
 * Notification content generated by intelligence layer.
 *
 * Renamed from NotificationContent to avoid collision with
 * com.prody.prashant.data.content.NotificationContent object.
 */
data class IntelligentNotificationContent(
    val title: String,
    val body: String,
    val deepLink: String? = null
)

/**
 * Optimal notification times learned from user behavior.
 */
data class NotificationTimes(
    val morning: Int, // Hour (0-23)
    val evening: Int,
    val bestOverall: Int
)

// ================================================================================================
// COPY GENERATION
// ================================================================================================

/**
 * Template for generating context-aware copy.
 */
data class CopyTemplate(
    val default: String,
    val variants: List<CopyVariant>
)

/**
 * A variant of copy for specific conditions.
 */
data class CopyVariant(
    val condition: (UserContext) -> Boolean,
    val text: String
)
