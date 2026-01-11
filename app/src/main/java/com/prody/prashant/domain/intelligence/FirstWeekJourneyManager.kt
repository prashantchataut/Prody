package com.prody.prashant.domain.intelligence

import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.streak.DualStreakManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ================================================================================================
 * FIRST WEEK JOURNEY MANAGER - The New User Experience
 * ================================================================================================
 *
 * The first week is CRITICAL for user retention. This manager creates a carefully curated
 * experience that guides new users through discovery without overwhelming them.
 *
 * Philosophy:
 * - Day 1: Welcome, validate choice, encourage first entry
 * - Day 2: Celebrate return, build momentum
 * - Day 3: Introduce daily wisdom, show value
 * - Day 4: Deepen practice, unlock a feature
 * - Day 5: Build habit, acknowledge consistency
 * - Day 6: Explore more features, show depth
 * - Day 7: Celebrate milestone, set up for long-term success
 *
 * Key principles:
 * - Never overwhelm - one focus per day
 * - Celebrate every return
 * - Make each day feel special
 * - Gradual feature reveal
 * - Build toward the habit, not just the features
 */
@Singleton
class FirstWeekJourneyManager @Inject constructor(
    private val journalDao: JournalDao,
    private val preferencesManager: PreferencesManager,
    private val dualStreakManager: DualStreakManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Journey state
    private val _currentJourneyState = MutableStateFlow<FirstWeekJourneyState?>(null)
    val currentJourneyState: StateFlow<FirstWeekJourneyState?> = _currentJourneyState.asStateFlow()

    // Journey completion tracking
    private val _journeyCompleted = MutableStateFlow(false)
    val journeyCompleted: StateFlow<Boolean> = _journeyCompleted.asStateFlow()

    init {
        scope.launch {
            refreshJourneyState()
        }
    }

    // =============================================================================================
    // PUBLIC API
    // =============================================================================================

    /**
     * Get the current first week journey state.
     */
    suspend fun getCurrentState(): FirstWeekJourneyState? {
        refreshJourneyState()
        return _currentJourneyState.value
    }

    /**
     * Get content for the current day of the journey.
     */
    suspend fun getDayContent(): FirstWeekDayContent {
        val state = getCurrentState() ?: return getGraduatedContent()
        return getDayContent(state)
    }

    /**
     * Check if user is in their first week.
     */
    suspend fun isInFirstWeek(): Boolean {
        val firstLaunchTime = preferencesManager.firstLaunchTime.first()
        if (firstLaunchTime <= 0) return true // No first launch recorded, assume first week

        val daysWithPrody = calculateDaysWithPrody(firstLaunchTime)
        return daysWithPrody <= 7
    }

    /**
     * Get the welcome content for day 1 first open.
     */
    fun getWelcomeContent(displayName: String): WelcomeContent {
        val firstName = displayName.split(" ").firstOrNull() ?: "Friend"
        return WelcomeContent(
            greeting = "Welcome to Prody, $firstName",
            subtitle = "Your space for reflection, growth, and self-discovery",
            primaryMessage = "This is your private journal. A place to capture thoughts, track moods, and discover patterns in your life.",
            encouragement = "There's no right or wrong way to use Prody. Just be yourself.",
            firstAction = FirstWeekAction(
                type = FirstWeekActionType.WRITE_FIRST_ENTRY,
                title = "Write your first entry",
                description = "Start with whatever's on your mind",
                route = "journal/new",
                isRequired = false
            ),
            tips = listOf(
                "Write as little or as much as you want",
                "Your entries are private and secure",
                "Prody learns your patterns to help you grow"
            )
        )
    }

    /**
     * Mark a first week milestone as completed.
     */
    suspend fun markMilestoneCompleted(milestone: FirstWeekMilestone) {
        val currentMilestones = preferencesManager.firstWeekMilestones.first().toMutableSet()
        currentMilestones.add(milestone.name)
        preferencesManager.updateFirstWeekMilestones(currentMilestones)
        refreshJourneyState()
    }

    /**
     * Check if a milestone has been completed.
     */
    suspend fun isMilestoneCompleted(milestone: FirstWeekMilestone): Boolean {
        val completedMilestones = preferencesManager.firstWeekMilestones.first()
        return milestone.name in completedMilestones
    }

    /**
     * Get celebration content for completing a milestone.
     */
    fun getCelebrationContent(milestone: FirstWeekMilestone): CelebrationContent {
        return when (milestone) {
            FirstWeekMilestone.FIRST_ENTRY -> CelebrationContent(
                title = "First Entry!",
                message = "You've taken the first step on your journey of self-discovery.",
                xpReward = 50,
                tokensReward = 5,
                animation = CelebrationAnimation.CONFETTI,
                nextHint = "Come back tomorrow to build your streak!"
            )
            FirstWeekMilestone.SECOND_DAY_RETURN -> CelebrationContent(
                title = "Day 2!",
                message = "You came back! Consistency is the key to growth.",
                xpReward = 30,
                tokensReward = 3,
                animation = CelebrationAnimation.SPARKLE,
                nextHint = "Your reflection streak has begun!"
            )
            FirstWeekMilestone.FIRST_WISDOM -> CelebrationContent(
                title = "First Wisdom!",
                message = "You've discovered daily wisdom. A new insight awaits you each day.",
                xpReward = 20,
                tokensReward = 2,
                animation = CelebrationAnimation.GLOW,
                nextHint = "Try viewing a quote or word each morning"
            )
            FirstWeekMilestone.THREE_DAY_STREAK -> CelebrationContent(
                title = "3-Day Streak!",
                message = "Three days of reflection. You're building a powerful habit.",
                xpReward = 75,
                tokensReward = 10,
                animation = CelebrationAnimation.CONFETTI,
                nextHint = "Keep going - day 4 unlocks something special"
            )
            FirstWeekMilestone.FIRST_BUDDHA_RESPONSE -> CelebrationContent(
                title = "Wisdom Received!",
                message = "Buddha AI shared wisdom with you. Prody is learning how to help you best.",
                xpReward = 40,
                tokensReward = 5,
                animation = CelebrationAnimation.SPARKLE,
                nextHint = "Each entry helps Prody understand you better"
            )
            FirstWeekMilestone.FIVE_ENTRIES -> CelebrationContent(
                title = "5 Entries!",
                message = "Five moments captured. You're creating a valuable record of your journey.",
                xpReward = 100,
                tokensReward = 15,
                animation = CelebrationAnimation.CONFETTI,
                nextHint = "Patterns start to emerge around entry 10"
            )
            FirstWeekMilestone.WEEK_COMPLETE -> CelebrationContent(
                title = "First Week Complete!",
                message = "You've graduated from your first week! The full Prody experience awaits.",
                xpReward = 200,
                tokensReward = 30,
                animation = CelebrationAnimation.FIREWORKS,
                nextHint = "Explore all features now unlocked for you"
            )
            FirstWeekMilestone.EXPLORED_FEATURE -> CelebrationContent(
                title = "Explorer!",
                message = "You're curious - that's wonderful! Each feature has something special.",
                xpReward = 25,
                tokensReward = 3,
                animation = CelebrationAnimation.SPARKLE,
                nextHint = "More features unlock as you continue"
            )
        }
    }

    /**
     * Get the next recommended action for the user.
     */
    suspend fun getNextAction(): FirstWeekAction? {
        val state = getCurrentState() ?: return null

        return when {
            !state.hasWrittenToday -> FirstWeekAction(
                type = FirstWeekActionType.WRITE_ENTRY,
                title = "Write today's entry",
                description = "Capture a thought before the day ends",
                route = "journal/new",
                isRequired = false
            )
            !state.hasViewedWisdomToday -> FirstWeekAction(
                type = FirstWeekActionType.VIEW_WISDOM,
                title = "View daily wisdom",
                description = "A moment of inspiration awaits",
                route = "wisdom",
                isRequired = false
            )
            state.dayNumber >= 4 && !isMilestoneCompleted(FirstWeekMilestone.EXPLORED_FEATURE) -> FirstWeekAction(
                type = FirstWeekActionType.EXPLORE_FEATURE,
                title = "Discover a new feature",
                description = "There's more to explore",
                route = "discover",
                isRequired = false
            )
            else -> null
        }
    }

    /**
     * Get progress summary for the first week.
     */
    suspend fun getProgressSummary(): FirstWeekProgress {
        val state = getCurrentState()
        val completedMilestones = preferencesManager.firstWeekMilestones.first()
        val allMilestones = FirstWeekMilestone.entries

        return FirstWeekProgress(
            currentDay = state?.dayNumber ?: 8,
            totalDays = 7,
            entriesWritten = state?.entriesWritten ?: 0,
            streakDays = state?.currentStreak ?: 0,
            milestonesCompleted = completedMilestones.size,
            totalMilestones = allMilestones.size,
            isGraduated = state == null || state.dayNumber > 7,
            completedMilestones = allMilestones.filter { it.name in completedMilestones }
        )
    }

    /**
     * Observe journey progress changes.
     */
    fun observeProgress(): Flow<FirstWeekProgress> {
        return combine(
            _currentJourneyState,
            preferencesManager.firstWeekMilestones
        ) { state, milestones ->
            FirstWeekProgress(
                currentDay = state?.dayNumber ?: 8,
                totalDays = 7,
                entriesWritten = state?.entriesWritten ?: 0,
                streakDays = state?.currentStreak ?: 0,
                milestonesCompleted = milestones.size,
                totalMilestones = FirstWeekMilestone.entries.size,
                isGraduated = state == null || state.dayNumber > 7,
                completedMilestones = FirstWeekMilestone.entries.filter { it.name in milestones }
            )
        }
    }

    // =============================================================================================
    // PRIVATE HELPERS
    // =============================================================================================

    private suspend fun refreshJourneyState() {
        val firstLaunchTime = preferencesManager.firstLaunchTime.first()
        if (firstLaunchTime <= 0) {
            // No first launch recorded - this is day 1
            _currentJourneyState.value = FirstWeekJourneyState(
                dayNumber = 1,
                stage = FirstWeekStage.DAY_1_FIRST_OPEN,
                entriesWritten = 0,
                currentStreak = 0,
                hasWrittenToday = false,
                hasViewedWisdomToday = false,
                unlockedFeatures = getUnlockedFeatures(1),
                todaysFocus = "Welcome to Prody"
            )
            return
        }

        val daysWithPrody = calculateDaysWithPrody(firstLaunchTime)

        if (daysWithPrody > 7) {
            _currentJourneyState.value = null
            _journeyCompleted.value = true
            return
        }

        val entries = journalDao.getRecentEntries(50).first()
        val entriesWritten = entries.size
        val streak = dualStreakManager.getCurrentStatus()

        val todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val hasWrittenToday = entries.any { it.createdAt >= todayStart }

        val wisdomLastShown = preferencesManager.dailyWisdomLastShown.first()
        val hasViewedWisdomToday = wisdomLastShown >= todayStart

        val stage = determineStage(daysWithPrody, entriesWritten, hasWrittenToday, hasViewedWisdomToday)

        _currentJourneyState.value = FirstWeekJourneyState(
            dayNumber = daysWithPrody,
            stage = stage,
            entriesWritten = entriesWritten,
            currentStreak = streak.reflectionStreak.current,
            hasWrittenToday = hasWrittenToday,
            hasViewedWisdomToday = hasViewedWisdomToday,
            unlockedFeatures = getUnlockedFeatures(daysWithPrody),
            todaysFocus = getDayFocus(daysWithPrody)
        )
    }

    private fun determineStage(
        dayNumber: Int,
        entriesWritten: Int,
        hasWrittenToday: Boolean,
        hasViewedWisdomToday: Boolean
    ): FirstWeekStage {
        return when (dayNumber) {
            1 -> when {
                entriesWritten == 0 -> FirstWeekStage.DAY_1_FIRST_OPEN
                entriesWritten >= 1 && !hasViewedWisdomToday -> FirstWeekStage.DAY_1_FIRST_ENTRY
                else -> FirstWeekStage.DAY_1_FIRST_WISDOM
            }
            2 -> when {
                entriesWritten <= 1 -> FirstWeekStage.DAY_2_RETURNING
                else -> FirstWeekStage.DAY_2_SECOND_ENTRY
            }
            3 -> when {
                hasWrittenToday -> FirstWeekStage.DAY_3_MET_BUDDHA
                else -> FirstWeekStage.DAY_3_EXPLORING
            }
            4 -> when {
                hasWrittenToday -> FirstWeekStage.DAY_4_TRIED_FEATURE
                else -> FirstWeekStage.DAY_4_DEEPENING
            }
            5 -> FirstWeekStage.DAY_5_BUILDING_HABIT
            6 -> FirstWeekStage.DAY_6_ALMOST_THERE
            7 -> FirstWeekStage.DAY_7_CELEBRATION
            else -> FirstWeekStage.GRADUATED
        }
    }

    private fun getDayContent(state: FirstWeekJourneyState): FirstWeekDayContent {
        return when (state.dayNumber) {
            1 -> FirstWeekDayContent(
                dayNumber = 1,
                title = "Welcome",
                subtitle = "Your journey begins",
                greeting = getDay1Greeting(state),
                primaryAction = FirstWeekAction(
                    type = FirstWeekActionType.WRITE_FIRST_ENTRY,
                    title = if (state.entriesWritten == 0) "Write your first entry" else "Write another entry",
                    description = "Start with whatever's on your mind",
                    route = "journal/new",
                    isRequired = false
                ),
                secondaryAction = if (!state.hasViewedWisdomToday && state.entriesWritten > 0) {
                    FirstWeekAction(
                        type = FirstWeekActionType.VIEW_WISDOM,
                        title = "Discover daily wisdom",
                        description = "A quote to start your practice",
                        route = "wisdom",
                        isRequired = false
                    )
                } else null,
                tips = listOf(
                    "There's no minimum length - write what feels right",
                    "Your entries are completely private",
                    "Select a mood to track how you're feeling"
                ),
                celebration = if (state.entriesWritten == 1) {
                    getCelebrationContent(FirstWeekMilestone.FIRST_ENTRY)
                } else null,
                progressMessage = when (state.entriesWritten) {
                    0 -> "Start your journey with your first entry"
                    1 -> "First entry complete! You're off to a great start"
                    else -> "${state.entriesWritten} entries on day 1 - impressive!"
                }
            )
            2 -> FirstWeekDayContent(
                dayNumber = 2,
                title = "Day 2",
                subtitle = "You came back!",
                greeting = getDay2Greeting(state),
                primaryAction = FirstWeekAction(
                    type = FirstWeekActionType.WRITE_ENTRY,
                    title = "Continue your journal",
                    description = "Build on yesterday's reflection",
                    route = "journal/new",
                    isRequired = false
                ),
                secondaryAction = FirstWeekAction(
                    type = FirstWeekActionType.VIEW_WISDOM,
                    title = "Today's wisdom",
                    description = "A daily dose of inspiration",
                    route = "wisdom",
                    isRequired = false
                ),
                tips = listOf(
                    "Consistency matters more than length",
                    "Coming back is the hardest part - you did it!",
                    "Your reflection streak has begun"
                ),
                celebration = getCelebrationContent(FirstWeekMilestone.SECOND_DAY_RETURN),
                progressMessage = "Day 2 of 7 - your streak is building!"
            )
            3 -> FirstWeekDayContent(
                dayNumber = 3,
                title = "Day 3",
                subtitle = "Finding your rhythm",
                greeting = "Day 3! You're building a real habit now.",
                primaryAction = FirstWeekAction(
                    type = FirstWeekActionType.WRITE_ENTRY,
                    title = "Today's reflection",
                    description = "What's on your mind?",
                    route = "journal/new",
                    isRequired = false
                ),
                secondaryAction = FirstWeekAction(
                    type = FirstWeekActionType.VIEW_BUDDHA_RESPONSE,
                    title = "Get Buddha's wisdom",
                    description = "After your entry, ask Buddha for insight",
                    route = "journal/new",
                    isRequired = false
                ),
                tips = listOf(
                    "3 days is when habits start to form",
                    "Try asking Buddha AI for wisdom after your entry",
                    "Patterns start to emerge with consistency"
                ),
                celebration = if (state.currentStreak >= 3) {
                    getCelebrationContent(FirstWeekMilestone.THREE_DAY_STREAK)
                } else null,
                progressMessage = "Day 3 of 7 - habits are forming!"
            )
            4 -> FirstWeekDayContent(
                dayNumber = 4,
                title = "Day 4",
                subtitle = "Going deeper",
                greeting = "Day 4 - you're committed. Something special unlocks today.",
                primaryAction = FirstWeekAction(
                    type = FirstWeekActionType.WRITE_ENTRY,
                    title = "Write today's entry",
                    description = "Reflect on your journey so far",
                    route = "journal/new",
                    isRequired = false
                ),
                secondaryAction = FirstWeekAction(
                    type = FirstWeekActionType.EXPLORE_FEATURE,
                    title = "Meet Haven",
                    description = "Your AI therapeutic companion",
                    route = "haven",
                    isRequired = false
                ),
                tips = listOf(
                    "Haven is unlocked - a space for deeper conversations",
                    "You can talk to Haven anytime you need support",
                    "Your consistency is remarkable"
                ),
                celebration = null,
                progressMessage = "Day 4 of 7 - Haven is now available!"
            )
            5 -> FirstWeekDayContent(
                dayNumber = 5,
                title = "Day 5",
                subtitle = "Building the habit",
                greeting = "Day 5! Your commitment is inspiring.",
                primaryAction = FirstWeekAction(
                    type = FirstWeekActionType.WRITE_ENTRY,
                    title = "Continue your practice",
                    description = "Your daily reflection awaits",
                    route = "journal/new",
                    isRequired = false
                ),
                secondaryAction = FirstWeekAction(
                    type = FirstWeekActionType.VIEW_STATS,
                    title = "See your stats",
                    description = "Patterns are emerging",
                    route = "stats",
                    isRequired = false
                ),
                tips = listOf(
                    "Stats are unlocking - see your patterns",
                    "5 entries is a real milestone",
                    "You're in the top 20% of new users"
                ),
                celebration = if (state.entriesWritten >= 5) {
                    getCelebrationContent(FirstWeekMilestone.FIVE_ENTRIES)
                } else null,
                progressMessage = "Day 5 of 7 - stats now available!"
            )
            6 -> FirstWeekDayContent(
                dayNumber = 6,
                title = "Day 6",
                subtitle = "Almost there",
                greeting = "Day 6 - one more day and you've made it a full week!",
                primaryAction = FirstWeekAction(
                    type = FirstWeekActionType.WRITE_ENTRY,
                    title = "Penultimate entry",
                    description = "Tomorrow marks your first week!",
                    route = "journal/new",
                    isRequired = false
                ),
                secondaryAction = FirstWeekAction(
                    type = FirstWeekActionType.EXPLORE_FEATURE,
                    title = "Explore more",
                    description = "There's so much to discover",
                    route = "discover",
                    isRequired = false
                ),
                tips = listOf(
                    "Tomorrow is a big day - week one complete!",
                    "Look back at your first entry",
                    "You've come so far already"
                ),
                celebration = null,
                progressMessage = "Day 6 of 7 - tomorrow is the big day!"
            )
            7 -> FirstWeekDayContent(
                dayNumber = 7,
                title = "Week One!",
                subtitle = "You did it!",
                greeting = "Congratulations! You've completed your first week with Prody.",
                primaryAction = FirstWeekAction(
                    type = FirstWeekActionType.WRITE_ENTRY,
                    title = "Week one reflection",
                    description = "Reflect on your first week",
                    route = "journal/new",
                    isRequired = false
                ),
                secondaryAction = FirstWeekAction(
                    type = FirstWeekActionType.VIEW_JOURNEY,
                    title = "See your journey",
                    description = "Look back at this amazing week",
                    route = "journey",
                    isRequired = false
                ),
                tips = listOf(
                    "You've built a real habit",
                    "All features are now unlocked",
                    "The best is yet to come"
                ),
                celebration = getCelebrationContent(FirstWeekMilestone.WEEK_COMPLETE),
                progressMessage = "Week 1 complete! You're officially a Prody journaler!"
            )
            else -> getGraduatedContent()
        }
    }

    private fun getGraduatedContent(): FirstWeekDayContent {
        return FirstWeekDayContent(
            dayNumber = 8,
            title = "Graduate",
            subtitle = "The full Prody experience",
            greeting = "Welcome back! You've graduated from the first week journey.",
            primaryAction = FirstWeekAction(
                type = FirstWeekActionType.WRITE_ENTRY,
                title = "Continue your practice",
                description = "Your journal awaits",
                route = "journal/new",
                isRequired = false
            ),
            secondaryAction = null,
            tips = listOf(
                "All features are now available",
                "Your patterns will continue to reveal insights",
                "Keep building that streak!"
            ),
            celebration = null,
            progressMessage = "First week graduate - the journey continues!"
        )
    }

    private fun getDay1Greeting(state: FirstWeekJourneyState): String {
        return when {
            state.entriesWritten == 0 -> "This is your space. Start wherever feels right."
            state.entriesWritten == 1 -> "Your first entry is written! Already building your journey."
            else -> "You're off to an incredible start!"
        }
    }

    private fun getDay2Greeting(state: FirstWeekJourneyState): String {
        return "You came back - that's what matters most. Day 2 is all about momentum."
    }

    private fun getDayFocus(dayNumber: Int): String {
        return when (dayNumber) {
            1 -> "Getting started"
            2 -> "Building momentum"
            3 -> "Discovering wisdom"
            4 -> "Going deeper"
            5 -> "Forming habits"
            6 -> "Exploring features"
            7 -> "Celebrating growth"
            else -> "Continuing the journey"
        }
    }

    private fun getUnlockedFeatures(dayNumber: Int): List<Feature> {
        return Feature.entries.filter { it.unlockDay <= dayNumber }
    }

    private fun calculateDaysWithPrody(firstLaunchTime: Long): Int {
        if (firstLaunchTime <= 0) return 1
        val firstLaunch = Instant.ofEpochMilli(firstLaunchTime).atZone(ZoneId.systemDefault()).toLocalDate()
        return ChronoUnit.DAYS.between(firstLaunch, LocalDate.now()).toInt() + 1
    }
}

// ================================================================================================
// FIRST WEEK JOURNEY MODELS
// ================================================================================================

/**
 * Current state of the first week journey.
 */
data class FirstWeekJourneyState(
    val dayNumber: Int,
    val stage: FirstWeekStage,
    val entriesWritten: Int,
    val currentStreak: Int,
    val hasWrittenToday: Boolean,
    val hasViewedWisdomToday: Boolean,
    val unlockedFeatures: List<Feature>,
    val todaysFocus: String
)

/**
 * Content for a specific day of the first week.
 */
data class FirstWeekDayContent(
    val dayNumber: Int,
    val title: String,
    val subtitle: String,
    val greeting: String,
    val primaryAction: FirstWeekAction,
    val secondaryAction: FirstWeekAction?,
    val tips: List<String>,
    val celebration: CelebrationContent?,
    val progressMessage: String
)

/**
 * Welcome content for day 1 first open.
 */
data class WelcomeContent(
    val greeting: String,
    val subtitle: String,
    val primaryMessage: String,
    val encouragement: String,
    val firstAction: FirstWeekAction,
    val tips: List<String>
)

/**
 * A first week action the user can take.
 */
data class FirstWeekAction(
    val type: FirstWeekActionType,
    val title: String,
    val description: String,
    val route: String,
    val isRequired: Boolean
)

enum class FirstWeekActionType {
    WRITE_FIRST_ENTRY,
    WRITE_ENTRY,
    VIEW_WISDOM,
    VIEW_BUDDHA_RESPONSE,
    EXPLORE_FEATURE,
    VIEW_STATS,
    VIEW_JOURNEY
}

/**
 * Celebration content for completing a milestone.
 */
data class CelebrationContent(
    val title: String,
    val message: String,
    val xpReward: Int,
    val tokensReward: Int,
    val animation: CelebrationAnimation,
    val nextHint: String
)

enum class CelebrationAnimation {
    CONFETTI,
    SPARKLE,
    GLOW,
    FIREWORKS
}

/**
 * First week milestones.
 */
enum class FirstWeekMilestone {
    FIRST_ENTRY,
    SECOND_DAY_RETURN,
    FIRST_WISDOM,
    THREE_DAY_STREAK,
    FIRST_BUDDHA_RESPONSE,
    FIVE_ENTRIES,
    WEEK_COMPLETE,
    EXPLORED_FEATURE
}

/**
 * Progress summary for the first week.
 */
data class FirstWeekProgress(
    val currentDay: Int,
    val totalDays: Int,
    val entriesWritten: Int,
    val streakDays: Int,
    val milestonesCompleted: Int,
    val totalMilestones: Int,
    val isGraduated: Boolean,
    val completedMilestones: List<FirstWeekMilestone>
) {
    val progressPercentage: Float
        get() = (currentDay.coerceAtMost(7).toFloat() / totalDays) * 100

    val milestoneProgressPercentage: Float
        get() = (milestonesCompleted.toFloat() / totalMilestones) * 100
}
