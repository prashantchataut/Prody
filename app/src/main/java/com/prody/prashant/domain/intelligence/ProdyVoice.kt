package com.prody.prashant.domain.intelligence

import com.prody.prashant.domain.model.Mood
import com.prody.prashant.domain.model.TimeOfDay
import java.time.DayOfWeek
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ================================================================================================
 * PRODY VOICE - The Unified Copywriting System
 * ================================================================================================
 *
 * Every piece of text in Prody should feel like it comes from the same caring, wise,
 * warm friend. This system ensures consistent tone and personalization across:
 *
 * - Greetings and goodbyes
 * - Button labels and CTAs
 * - Empty states
 * - Error messages
 * - Celebrations
 * - Encouragement
 * - Feature explanations
 *
 * Voice principles:
 * - Warm, not saccharine (supportive without being over-the-top)
 * - Wise, not preachy (insights without lecturing)
 * - Personal, not intrusive (remembers without being creepy)
 * - Encouraging, not demanding (invites without pressuring)
 * - Honest, not harsh (truthful but kind)
 *
 * This is how Prody speaks. Every string should pass through this lens.
 */
@Singleton
class ProdyVoice @Inject constructor() {

    // =============================================================================================
    // PUBLIC API - CONTEXTUAL COPY
    // =============================================================================================

    /**
     * Get context-aware copy for a given location and context.
     */
    fun getCopy(location: CopyLocation, context: UserContext): String {
        return when (location) {
            // Home screen
            CopyLocation.HOME_GREETING -> getHomeGreeting(context)
            CopyLocation.HOME_SUBTITLE -> getHomeSubtitle(context)
            CopyLocation.HOME_EMPTY_STATE -> getHomeEmptyState(context)
            CopyLocation.HOME_CTA_PRIMARY -> getHomePrimaryCta(context)
            CopyLocation.HOME_CTA_SECONDARY -> getHomeSecondaryCta(context)

            // Journal
            CopyLocation.JOURNAL_NEW_ENTRY_TITLE -> getJournalNewEntryTitle(context)
            CopyLocation.JOURNAL_PLACEHOLDER -> getJournalPlaceholder(context)
            CopyLocation.JOURNAL_EMPTY_STATE -> getJournalEmptyState(context)
            CopyLocation.JOURNAL_SAVED_CONFIRMATION -> getJournalSavedConfirmation(context)
            CopyLocation.JOURNAL_BUDDHA_LOADING -> getJournalBuddhaLoading(context)

            // Wisdom
            CopyLocation.WISDOM_GREETING -> getWisdomGreeting(context)
            CopyLocation.WISDOM_EMPTY_STATE -> getWisdomEmptyState(context)

            // Haven
            CopyLocation.HAVEN_GREETING -> getHavenGreeting(context)
            CopyLocation.HAVEN_EMPTY_STATE -> getHavenEmptyState(context)

            // Stats
            CopyLocation.STATS_GREETING -> getStatsGreeting(context)
            CopyLocation.STATS_EMPTY_STATE -> getStatsEmptyState(context)

            // Streak
            CopyLocation.STREAK_MAINTAINED -> getStreakMaintained(context)
            CopyLocation.STREAK_BROKEN -> getStreakBroken(context)
            CopyLocation.STREAK_MILESTONE -> getStreakMilestone(context)

            // Errors
            CopyLocation.ERROR_GENERIC -> getErrorGeneric(context)
            CopyLocation.ERROR_NETWORK -> getErrorNetwork(context)
            CopyLocation.ERROR_AI_UNAVAILABLE -> getErrorAiUnavailable(context)

            // Loading
            CopyLocation.LOADING_GENERIC -> getLoadingGeneric(context)
            CopyLocation.LOADING_AI -> getLoadingAi(context)
        }
    }

    /**
     * Get copy for a specific button/action.
     */
    fun getButtonCopy(button: ButtonType, context: UserContext): ButtonCopy {
        return when (button) {
            ButtonType.WRITE_ENTRY -> ButtonCopy(
                label = when {
                    context.daysSinceLastEntry == 0 -> "Write another entry"
                    context.totalEntries == 0 -> "Write your first entry"
                    else -> "Write today's entry"
                },
                hint = when {
                    context.isStruggling -> "No pressure - write what feels right"
                    context.currentStreak.reflectionStreak.isAtRisk -> "Keep your streak alive!"
                    else -> "Capture a thought"
                }
            )

            ButtonType.VIEW_WISDOM -> ButtonCopy(
                label = "Today's wisdom",
                hint = when (context.timeOfDay) {
                    TimeOfDay.MORNING -> "Start your day with insight"
                    TimeOfDay.EVENING, TimeOfDay.NIGHT -> "A thought before bed"
                    else -> "A moment of reflection"
                }
            )

            ButtonType.TALK_TO_HAVEN -> ButtonCopy(
                label = when {
                    !context.hasTalkedToHaven -> "Meet Haven"
                    context.isStruggling -> "Talk to Haven"
                    else -> "Visit Haven"
                },
                hint = when {
                    context.isStruggling -> "A supportive conversation awaits"
                    !context.hasTalkedToHaven -> "Your AI therapeutic companion"
                    else -> "Continue the conversation"
                }
            )

            ButtonType.ASK_BUDDHA -> ButtonCopy(
                label = "Ask Buddha",
                hint = "Get wisdom on your entry"
            )

            ButtonType.VIEW_STATS -> ButtonCopy(
                label = "Your stats",
                hint = when {
                    context.totalEntries < 10 -> "Patterns emerge with more entries"
                    else -> "See your journey in data"
                }
            )

            ButtonType.SEND_FUTURE_MESSAGE -> ButtonCopy(
                label = "Message to future you",
                hint = "Write to who you'll become"
            )
        }
    }

    /**
     * Get celebration copy.
     */
    fun getCelebrationCopy(celebration: CelebrationType, context: UserContext): CelebrationCopy {
        return when (celebration) {
            CelebrationType.FIRST_ENTRY -> CelebrationCopy(
                title = "Your first entry!",
                message = when {
                    context.preferredTone == CommunicationTone.PLAYFUL -> "You did it! Your journey has officially begun. Here's to many more moments of reflection!"
                    context.preferredTone == CommunicationTone.DIRECT -> "First entry complete. You've started building a valuable habit."
                    else -> "You've taken the first step on your journey of self-discovery. Every great journey begins with a single step."
                },
                encouragement = "Come back tomorrow to build your streak!"
            )

            CelebrationType.STREAK_MILESTONE -> CelebrationCopy(
                title = getStreakMilestoneTitle(context.currentStreak.reflectionStreak.current),
                message = getStreakMilestoneMessage(context.currentStreak.reflectionStreak.current, context),
                encouragement = "Keep going - consistency is the key to growth!"
            )

            CelebrationType.WEEK_COMPLETE -> CelebrationCopy(
                title = "First week complete!",
                message = "You've built a real habit. The first week is the hardest, and you did it! The full Prody experience now awaits.",
                encouragement = "You're officially a Prody journaler now."
            )

            CelebrationType.FEATURE_DISCOVERED -> CelebrationCopy(
                title = "Feature unlocked!",
                message = "Your curiosity is rewarded. This feature is now available to you.",
                encouragement = "There's more to discover!"
            )

            CelebrationType.GROWTH_MOMENT -> CelebrationCopy(
                title = "Growth moment!",
                message = "We noticed something special - you're showing real progress.",
                encouragement = "Keep nurturing your growth!"
            )

            CelebrationType.VULNERABILITY_SHARED -> CelebrationCopy(
                title = "Thank you for sharing",
                message = "Opening up takes courage. Your honesty with yourself is a sign of strength.",
                encouragement = "This space is always safe for your thoughts."
            )

            CelebrationType.PATTERN_BROKEN -> CelebrationCopy(
                title = "Pattern broken!",
                message = "You've disrupted an old pattern. That takes awareness and effort.",
                encouragement = "New patterns start forming with consistency."
            )

            // Visual effect types - provide default celebration copy
            CelebrationType.CONFETTI,
            CelebrationType.FIREWORKS,
            CelebrationType.SPARKLE,
            CelebrationType.SPARKLES,
            CelebrationType.GENTLE_GLOW,
            CelebrationType.WARM_FADE,
            CelebrationType.ACHIEVEMENT -> CelebrationCopy(
                title = "Celebration!",
                message = "Something wonderful happened!",
                encouragement = "Keep up the great work!"
            )
        }
    }

    /**
     * Get empty state copy.
     */
    fun getEmptyStateCopy(location: EmptyStateLocation, context: UserContext): EmptyStateCopy {
        return when (location) {
            EmptyStateLocation.JOURNAL_LIST -> EmptyStateCopy(
                title = when {
                    context.totalEntries == 0 && context.isInFirstWeek -> "Your journal awaits"
                    context.totalEntries == 0 -> "No entries yet"
                    else -> "No entries found"
                },
                message = when {
                    context.totalEntries == 0 && context.isInFirstWeek -> "This will become a beautiful record of your journey. Start with whatever's on your mind."
                    context.totalEntries == 0 -> "Every journey begins with a single step. Your first entry is waiting to be written."
                    else -> "Try a different search or filter."
                },
                ctaLabel = if (context.totalEntries == 0) "Write your first entry" else null,
                ctaRoute = if (context.totalEntries == 0) "journal/new" else null
            )

            EmptyStateLocation.BOOKMARKS -> EmptyStateCopy(
                title = "No bookmarks yet",
                message = "Entries you want to revisit can be bookmarked for easy access.",
                ctaLabel = null,
                ctaRoute = null
            )

            EmptyStateLocation.STATS -> EmptyStateCopy(
                title = when {
                    context.totalEntries < 5 -> "Keep writing!"
                    else -> "Stats are loading..."
                },
                message = when {
                    context.totalEntries < 5 -> "We need at least 5 entries to show meaningful patterns. You have ${context.totalEntries} so far - keep going!"
                    else -> "Your insights are on the way."
                },
                ctaLabel = if (context.totalEntries < 5) "Write an entry" else null,
                ctaRoute = if (context.totalEntries < 5) "journal/new" else null
            )

            EmptyStateLocation.FUTURE_MESSAGES -> EmptyStateCopy(
                title = "No messages to future you",
                message = "Send a message to your future self. It could be encouragement, a goal, or a promise.",
                ctaLabel = "Write to future you",
                ctaRoute = "future-messages/new"
            )

            EmptyStateLocation.HAVEN_HISTORY -> EmptyStateCopy(
                title = when {
                    !context.hasTalkedToHaven -> "Haven is waiting"
                    else -> "No previous sessions"
                },
                message = when {
                    !context.hasTalkedToHaven -> "Haven is your AI therapeutic companion. A safe space for deeper conversations."
                    else -> "Start a new conversation with Haven."
                },
                ctaLabel = if (!context.hasTalkedToHaven) "Meet Haven" else "Start a session",
                ctaRoute = "haven/new"
            )

            EmptyStateLocation.MEMORIES -> EmptyStateCopy(
                title = "Memories unlock over time",
                message = "As you continue journaling, Prody will surface meaningful memories at the right moments.",
                ctaLabel = null,
                ctaRoute = null
            )

            EmptyStateLocation.SEARCH_RESULTS -> EmptyStateCopy(
                title = "No results found",
                message = "Try different keywords or filters.",
                ctaLabel = null,
                ctaRoute = null
            )
        }
    }

    /**
     * Get error copy.
     */
    fun getErrorCopy(error: VoiceErrorType, context: UserContext): ErrorCopy {
        return when (error) {
            VoiceErrorType.NETWORK -> ErrorCopy(
                title = "Connection lost",
                message = when {
                    context.preferredTone == CommunicationTone.PLAYFUL -> "The internet seems to have wandered off. Don't worry, your data is safe locally!"
                    else -> "We couldn't reach the server. Your data is saved locally and will sync when connected."
                },
                actionLabel = "Try again",
                isRetryable = true
            )

            VoiceErrorType.AI_UNAVAILABLE -> ErrorCopy(
                title = "Buddha is resting",
                message = "Our AI wisdom engine is temporarily unavailable. Your entry is saved - you can get Buddha's response later.",
                actionLabel = "Save anyway",
                isRetryable = false
            )

            VoiceErrorType.SAVE_FAILED -> ErrorCopy(
                title = "Couldn't save",
                message = "Something went wrong. Your entry is in your drafts - we won't lose it.",
                actionLabel = "Try again",
                isRetryable = true
            )

            VoiceErrorType.LOAD_FAILED -> ErrorCopy(
                title = "Couldn't load",
                message = "We're having trouble loading this. Let's try again.",
                actionLabel = "Retry",
                isRetryable = true
            )

            VoiceErrorType.GENERIC -> ErrorCopy(
                title = "Something went wrong",
                message = when {
                    context.preferredTone == CommunicationTone.PLAYFUL -> "Oops! Technology had a hiccup. Let's try that again."
                    else -> "An unexpected error occurred. Please try again."
                },
                actionLabel = "Try again",
                isRetryable = true
            )
        }
    }

    // =============================================================================================
    // PRIVATE HELPERS - HOME SCREEN
    // =============================================================================================

    private fun getHomeGreeting(context: UserContext): String {
        val firstName = context.firstName

        return when {
            context.specialDay != null -> getSpecialDayGreeting(context.specialDay!!, firstName)
            context.userArchetype == UserArchetype.RETURNING -> listOf(
                "Welcome back, $firstName",
                "Good to see you again",
                "It's been a while, $firstName"
            ).random()
            context.daysSinceLastEntry > 7 -> listOf(
                "Hey there, $firstName",
                "Welcome back",
                "Nice to see you"
            ).random()
            else -> getTimeBasedGreeting(context.timeOfDay, firstName)
        }
    }

    private fun getTimeBasedGreeting(timeOfDay: TimeOfDay, firstName: String): String {
        return when (timeOfDay) {
            TimeOfDay.MORNING -> listOf(
                "Good morning, $firstName",
                "Rise and shine",
                "A fresh day awaits"
            ).random()
            TimeOfDay.AFTERNOON -> listOf(
                "Good afternoon, $firstName",
                "Hey there",
                "How's your day going?"
            ).random()
            TimeOfDay.EVENING -> listOf(
                "Good evening, $firstName",
                "Winding down?",
                "Evening reflection time"
            ).random()
            TimeOfDay.NIGHT, TimeOfDay.LATE_NIGHT -> listOf(
                "Still up, $firstName?",
                "Late night thoughts?",
                "The quiet hours"
            ).random()
        }
    }

    private fun getSpecialDayGreeting(specialDay: SpecialDay, firstName: String): String {
        return when (specialDay) {
            is SpecialDay.NewYear -> "Happy New Year, $firstName!"
            is SpecialDay.Birthday -> "Happy Birthday, $firstName!"
            is SpecialDay.ProdyAnniversary -> "Happy ${specialDay.yearsWithPrody} year anniversary, $firstName!"
            is SpecialDay.MentalHealthDay -> "World Mental Health Day"
            is SpecialDay.GratitudeDay -> "Happy Gratitude Day!"
            else -> "Hello, $firstName"
        }
    }

    private fun getHomeSubtitle(context: UserContext): String {
        return when {
            context.specialDay is SpecialDay.ProdyAnniversary -> "Look how far you've come!"
            context.isStruggling -> "No pressure. Just possibilities."
            context.isThriving -> "You're on fire!"
            context.daysSinceLastEntry == 0 -> "Back for more?"
            context.daysSinceLastEntry == 1 -> "Ready to continue?"
            context.daysSinceLastEntry > 7 -> "Your journal missed you"
            context.currentStreak.reflectionStreak.current >= 7 -> "${context.currentStreak.reflectionStreak.current} day streak!"
            else -> "What's on your mind?"
        }
    }

    private fun getHomeEmptyState(context: UserContext): String {
        return when {
            context.isInFirstWeek -> "This is your space. Start wherever feels right."
            context.totalEntries == 0 -> "Your journal awaits its first entry."
            else -> "Ready to reflect?"
        }
    }

    private fun getHomePrimaryCta(context: UserContext): String {
        return when {
            context.totalEntries == 0 -> "Write your first entry"
            context.daysSinceLastEntry == 0 -> "Write another entry"
            else -> "Write today's entry"
        }
    }

    private fun getHomeSecondaryCta(context: UserContext): String {
        return when {
            !context.hasTalkedToHaven && context.daysWithPrody >= 4 -> "Meet Haven"
            context.isStruggling -> "Talk to Haven"
            context.timeOfDay == TimeOfDay.MORNING -> "Morning wisdom"
            else -> "View your journey"
        }
    }

    // =============================================================================================
    // PRIVATE HELPERS - JOURNAL
    // =============================================================================================

    private fun getJournalNewEntryTitle(context: UserContext): String {
        return when {
            context.totalEntries == 0 -> "Your first entry"
            context.daysSinceLastEntry == 0 -> "Another thought to capture"
            else -> "Today's entry"
        }
    }

    private fun getJournalPlaceholder(context: UserContext): String {
        return when {
            context.isStruggling -> "Write whatever feels right. There's no wrong way to do this..."
            context.isInFirstWeek -> "Start writing... There's no minimum length."
            context.totalEntries == 0 -> "What's on your mind? This is your private space..."
            context.daysSinceLastEntry > 3 -> "It's been a few days. How have things been?..."
            else -> "What's on your mind?..."
        }
    }

    private fun getJournalEmptyState(context: UserContext): String {
        return when {
            context.totalEntries == 0 -> "Your journey begins with a single thought"
            else -> "No entries match your search"
        }
    }

    private fun getJournalSavedConfirmation(context: UserContext): String {
        return when {
            context.totalEntries == 1 -> "First entry saved! You've started something special."
            context.currentStreak.reflectionStreak.current == 1 -> "Entry saved. Streak started!"
            context.daysSinceLastEntry == 0 -> "Entry saved. Two entries in one day!"
            else -> "Entry saved."
        }
    }

    private fun getJournalBuddhaLoading(context: UserContext): String {
        return listOf(
            "Buddha is reflecting on your words...",
            "Wisdom is on its way...",
            "Finding the right insight...",
            "Buddha is contemplating...",
            "Preparing wisdom for you..."
        ).random()
    }

    // =============================================================================================
    // PRIVATE HELPERS - WISDOM
    // =============================================================================================

    private fun getWisdomGreeting(context: UserContext): String {
        return when (context.timeOfDay) {
            TimeOfDay.MORNING -> "Morning wisdom"
            TimeOfDay.EVENING, TimeOfDay.NIGHT -> "Evening reflection"
            else -> "Today's wisdom"
        }
    }

    private fun getWisdomEmptyState(context: UserContext): String {
        return "New wisdom awaits you each day."
    }

    // =============================================================================================
    // PRIVATE HELPERS - HAVEN
    // =============================================================================================

    private fun getHavenGreeting(context: UserContext): String {
        return when {
            !context.hasTalkedToHaven -> "Welcome to Haven"
            context.isStruggling -> "I'm here for you"
            else -> "Welcome back"
        }
    }

    private fun getHavenEmptyState(context: UserContext): String {
        return when {
            !context.hasTalkedToHaven -> "Haven is your safe space for deeper conversations. Ready when you are."
            else -> "Start a new conversation whenever you need support."
        }
    }

    // =============================================================================================
    // PRIVATE HELPERS - STATS
    // =============================================================================================

    private fun getStatsGreeting(context: UserContext): String {
        return when {
            context.totalEntries < 10 -> "Your journey so far"
            context.isThriving -> "Look at you go!"
            else -> "Your insights"
        }
    }

    private fun getStatsEmptyState(context: UserContext): String {
        return when {
            context.totalEntries < 5 -> "Patterns reveal themselves after a few more entries."
            else -> "Your stats are loading..."
        }
    }

    // =============================================================================================
    // PRIVATE HELPERS - STREAK
    // =============================================================================================

    private fun getStreakMaintained(context: UserContext): String {
        val streak = context.currentStreak.reflectionStreak.current
        return when {
            streak >= 30 -> "Incredible! $streak days of consistent reflection."
            streak >= 7 -> "$streak day streak! You're building a powerful habit."
            streak >= 3 -> "$streak days in a row! Keep going!"
            else -> "Streak maintained! Day $streak."
        }
    }

    private fun getStreakBroken(context: UserContext): String {
        return when {
            context.preferredTone == CommunicationTone.GENTLE -> "Streaks can be rebuilt. What matters is you're here now."
            context.preferredTone == CommunicationTone.DIRECT -> "Streak reset. Start fresh today."
            else -> "Streak reset - but every journey has pauses. Start fresh today."
        }
    }

    private fun getStreakMilestone(context: UserContext): String {
        return getStreakMilestoneTitle(context.currentStreak.reflectionStreak.current)
    }

    private fun getStreakMilestoneTitle(days: Int): String {
        return when (days) {
            7 -> "Week Warrior!"
            14 -> "Two Week Champion!"
            30 -> "Monthly Master!"
            60 -> "Two Month Legend!"
            100 -> "Century Club!"
            365 -> "Year of Dedication!"
            else -> "$days Day Streak!"
        }
    }

    private fun getStreakMilestoneMessage(days: Int, context: UserContext): String {
        return when (days) {
            7 -> "A full week of reflection. You're building something real."
            14 -> "Two weeks of consistency! Your habit is taking root."
            30 -> "A whole month! This is now part of who you are."
            60 -> "Two months. Remarkable dedication to your growth."
            100 -> "100 days! Very few people achieve this level of consistency."
            365 -> "An entire year of daily reflection. You're extraordinary."
            else -> "Your consistency is inspiring. Keep going!"
        }
    }

    // =============================================================================================
    // PRIVATE HELPERS - ERRORS
    // =============================================================================================

    private fun getErrorGeneric(context: UserContext): String {
        return when {
            context.preferredTone == CommunicationTone.PLAYFUL -> "Oops! Something went sideways. Let's try again."
            else -> "Something unexpected happened. Please try again."
        }
    }

    private fun getErrorNetwork(context: UserContext): String {
        return "Connection lost. Your data is safe locally."
    }

    private fun getErrorAiUnavailable(context: UserContext): String {
        return "Buddha is temporarily unavailable. Your entry is saved - you can get wisdom later."
    }

    // =============================================================================================
    // PRIVATE HELPERS - LOADING
    // =============================================================================================

    private fun getLoadingGeneric(context: UserContext): String {
        return listOf(
            "Loading...",
            "Just a moment...",
            "Getting things ready..."
        ).random()
    }

    private fun getLoadingAi(context: UserContext): String {
        return listOf(
            "Thinking...",
            "Contemplating...",
            "Finding wisdom...",
            "Reflecting..."
        ).random()
    }
}

// ================================================================================================
// PRODY VOICE MODELS
// ================================================================================================

/**
 * Locations in the app where copy is needed.
 */
enum class CopyLocation {
    // Home
    HOME_GREETING,
    HOME_SUBTITLE,
    HOME_EMPTY_STATE,
    HOME_CTA_PRIMARY,
    HOME_CTA_SECONDARY,

    // Journal
    JOURNAL_NEW_ENTRY_TITLE,
    JOURNAL_PLACEHOLDER,
    JOURNAL_EMPTY_STATE,
    JOURNAL_SAVED_CONFIRMATION,
    JOURNAL_BUDDHA_LOADING,

    // Wisdom
    WISDOM_GREETING,
    WISDOM_EMPTY_STATE,

    // Haven
    HAVEN_GREETING,
    HAVEN_EMPTY_STATE,

    // Stats
    STATS_GREETING,
    STATS_EMPTY_STATE,

    // Streak
    STREAK_MAINTAINED,
    STREAK_BROKEN,
    STREAK_MILESTONE,

    // Errors
    ERROR_GENERIC,
    ERROR_NETWORK,
    ERROR_AI_UNAVAILABLE,

    // Loading
    LOADING_GENERIC,
    LOADING_AI
}

/**
 * Button types that need copy.
 */
enum class ButtonType {
    WRITE_ENTRY,
    VIEW_WISDOM,
    TALK_TO_HAVEN,
    ASK_BUDDHA,
    VIEW_STATS,
    SEND_FUTURE_MESSAGE
}

/**
 * Button copy with label and hint.
 */
data class ButtonCopy(
    val label: String,
    val hint: String
)

/**
 * Celebration copy.
 */
data class CelebrationCopy(
    val title: String,
    val message: String,
    val encouragement: String
)

/**
 * Empty state locations.
 */
enum class EmptyStateLocation {
    JOURNAL_LIST,
    BOOKMARKS,
    STATS,
    FUTURE_MESSAGES,
    HAVEN_HISTORY,
    MEMORIES,
    SEARCH_RESULTS
}

/**
 * Empty state copy.
 */
data class EmptyStateCopy(
    val title: String,
    val message: String,
    val ctaLabel: String?,
    val ctaRoute: String?
)

/**
 * Error types for Prody voice responses.
 *
 * Renamed from ErrorType to avoid collision with
 * com.prody.prashant.domain.common.ErrorType.
 */
enum class VoiceErrorType {
    NETWORK,
    AI_UNAVAILABLE,
    SAVE_FAILED,
    LOAD_FAILED,
    GENERIC
}

/**
 * Error copy.
 */
data class ErrorCopy(
    val title: String,
    val message: String,
    val actionLabel: String,
    val isRetryable: Boolean
)
