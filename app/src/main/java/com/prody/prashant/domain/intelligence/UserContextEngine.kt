package com.prody.prashant.domain.intelligence

import com.prody.prashant.data.local.dao.HavenDao
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.MicroEntryDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.analytics.MoodAnalyticsEngine
import com.prody.prashant.domain.model.AnalyticsPeriod
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.domain.model.MoodShift
import com.prody.prashant.domain.model.MoodTrend
import com.prody.prashant.domain.model.TimeOfDay
import com.prody.prashant.domain.model.isNegative
import com.prody.prashant.domain.model.isPositive
import com.prody.prashant.domain.streak.DualStreakManager
import com.prody.prashant.domain.streak.DualStreakStatus
import com.prody.prashant.domain.streak.StreakType
import com.prody.prashant.domain.wellbeing.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * ================================================================================================
 * USER CONTEXT ENGINE - The Brain of Prody
 * ================================================================================================
 *
 * This engine synthesizes all user signals into actionable context that informs every interaction.
 * It's NOT a data dump - it's intelligent interpretation of patterns, behaviors, and emotional states.
 *
 * Key responsibilities:
 * - Gather data from multiple sources (journal, mood, streak, Haven, analytics)
 * - Synthesize into unified understanding
 * - Detect user archetype (EXPLORER, CONSISTENT, STRUGGLING, THRIVING, RETURNING)
 * - Infer emotional state from patterns, not just mood selections
 * - Track trust level and preferred communication tone
 * - Provide context-specific views for different features
 *
 * Usage:
 * - Call getCurrentContext() for general context
 * - Call getContextForBuddha(), getContextForHaven(), etc. for feature-specific contexts
 * - Context is cached and refreshed on significant user actions
 */
@Singleton
class UserContextEngine @Inject constructor(
    private val journalDao: JournalDao,
    private val userDao: UserDao,
    private val microEntryDao: MicroEntryDao,
    private val havenDao: HavenDao,
    private val preferencesManager: PreferencesManager,
    private val dualStreakManager: DualStreakManager,
    private val moodAnalyticsEngine: MoodAnalyticsEngine
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Cached context with flow for reactive updates
    private val _currentContext = MutableStateFlow<UserContext>(UserContext.empty())
    val currentContext: StateFlow<UserContext> = _currentContext.asStateFlow()

    // Last refresh timestamp
    private var lastContextRefresh: LocalDateTime = LocalDateTime.MIN

    // Context is considered stale after 5 minutes
    private val contextStalenessThreshold = 5.minutes

    init {
        // Initial context load
        scope.launch {
            refreshContext()
        }
    }

    // =============================================================================================
    // PUBLIC API
    // =============================================================================================

    /**
     * Get the current user context. Refreshes if stale.
     */
    suspend fun getCurrentContext(): UserContext {
        if (isContextStale()) {
            refreshContext()
        }
        return _currentContext.value
    }

    /**
     * Force refresh the context. Call after significant user actions.
     */
    suspend fun refreshContext() {
        val newContext = synthesizeContext()
        _currentContext.value = newContext
        lastContextRefresh = LocalDateTime.now()
    }

    /**
     * Observe context changes reactively.
     */
    fun observeContext(): Flow<UserContext> = _currentContext.asStateFlow()

    /**
     * Get context optimized for Buddha AI interactions.
     */
    suspend fun getContextForBuddha(): BuddhaContext {
        val context = getCurrentContext()
        val recentEntries = journalDao.getRecentEntries(10).first()
        val themes = extractThemes(recentEntries)
        val patterns = detectRecurringPatterns(recentEntries)

        return BuddhaContext(
            userContext = context,
            recentJournalThemes = themes,
            recurringPatterns = patterns,
            lastBuddhaInteraction = getLastBuddhaInteraction(),
            preferredWisdomStyle = inferWisdomStyle(context, recentEntries),
            avoidTopics = detectSensitiveTopics(recentEntries)
        )
    }

    /**
     * Get context optimized for Haven therapy sessions.
     */
    suspend fun getContextForHaven(): HavenContext {
        val context = getCurrentContext()
        val recentEntries = journalDao.getRecentEntries(5).first()
        val userProfile = userDao.getUserProfileSync()
        val userId = userProfile?.odUserId ?: "local"
        val sessions = havenDao.getRecentSessions(userId = userId, limit = 10).first()
        val lastSession = sessions.firstOrNull()

        return HavenContext(
            userContext = context,
            recentJournalContent = recentEntries.map { it.content.take(200) },
            previousSessionSummary = lastSession?.keyInsightsJson,
            sessionCount = sessions.size,
            preferredTherapeuticApproach = inferTherapeuticApproach(context, sessions),
            crisisHistory = sessions.any { it.containedCrisisDetection },
            sensitiveTriggers = detectSensitiveTopics(recentEntries)
        )
    }

    /**
     * Get context optimized for notification decisions.
     */
    suspend fun getContextForNotification(): NotificationContext {
        val context = getCurrentContext()
        val lastNotification = getLastNotificationTime()
        val notificationsToday = getNotificationCountToday()

        return NotificationContext(
            userContext = context,
            lastNotificationSentAt = lastNotification,
            notificationsSentToday = notificationsToday,
            notificationOpenRate = calculateNotificationOpenRate(),
            preferredNotificationTimes = inferPreferredNotificationTimes(),
            ignoresNotificationsAt = inferIgnoredNotificationTimes(),
            lastAppOpenAt = getLastAppOpenTime()
        )
    }

    /**
     * Get context optimized for home screen content.
     */
    suspend fun getContextForHome(): HomeContext {
        val context = getCurrentContext()

        return HomeContext(
            userContext = context,
            hasMemoryToSurface = shouldSurfaceMemory(context),
            memoryPreview = getMemoryPreview(),
            nextSuggestedAction = getSuggestedAction(context),
            featureDiscovery = getFeatureDiscovery(context),
            dailyTheme = getDailyTheme(context)
        )
    }

    // =============================================================================================
    // CONTEXT SYNTHESIS - The Core Logic
    // =============================================================================================

    private suspend fun synthesizeContext(): UserContext = coroutineScope {
        // Gather all signals in parallel for efficiency
        val userProfileDeferred = async { userDao.getUserProfileSync() }
        val journalsDeferred = async { journalDao.getRecentEntries(30).first() }
        val streakDeferred = async { dualStreakManager.getCurrentStatus() }
        val preferencesDeferred = async { loadPreferences() }
        val firstLaunchDeferred = async { preferencesManager.firstLaunchTime.first() }

        // Await all data
        val userProfile = userProfileDeferred.await()
        val userId = userProfile?.odUserId ?: "local"
        val journals = journalsDeferred.await()
        val streak = streakDeferred.await()
        val preferences = preferencesDeferred.await()
        val firstLaunch = firstLaunchDeferred.await()

        // Get user-specific data after we have userId
        val sessionsDeferred = async { havenDao.getRecentSessions(userId = userId, limit = 10).first() }
        val microEntriesDeferred = async { microEntryDao.getRecentEntries(limit = 20).first() }

        val sessions = sessionsDeferred.await()
        val microEntries = microEntriesDeferred.await()

        // Calculate days with Prody
        val daysWithPrody = calculateDaysWithPrody(firstLaunch)

        // Detect user archetype
        val archetype = detectArchetype(journals, streak, daysWithPrody)

        // Infer emotional state
        val emotionalState = inferEmotionalState(journals)

        // Detect stress signals
        val stressSignals = detectStressSignals(journals)

        // Calculate trust level
        val trustLevel = calculateTrustLevel(journals, sessions)

        // Determine preferred tone
        val preferredTone = inferPreferredTone(journals)

        // Determine engagement level
        val engagementLevel = calculateEngagementLevel(journals, daysWithPrody)

        // Extract themes and patterns
        val themes = extractThemes(journals)
        val challenges = detectRecurringChallenges(journals)
        val wins = detectRecentWins(journals, streak)
        val growthAreas = detectGrowthAreas(journals)

        // Calculate first week stage
        val firstWeekStage = calculateFirstWeekStage(daysWithPrody, journals, preferences)

        // Get temporal context
        val now = LocalDateTime.now()
        val timeOfDay = TimeOfDay.fromHour(now.hour)

        UserContext(
            displayName = userProfile?.displayName ?: "Friend",
            daysWithPrody = daysWithPrody,
            userArchetype = archetype,
            totalEntries = journals.size + microEntries.size,
            timeOfDay = timeOfDay,
            dayOfWeek = now.dayOfWeek,
            isWeekend = now.dayOfWeek in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
            season = Season.current(),
            specialDay = detectSpecialDay(firstLaunch),
            recentMoodTrend = emotionalState.trend,
            dominantMood = emotionalState.dominantMood,
            emotionalEnergy = emotionalState.energy,
            stressSignals = stressSignals,
            engagementLevel = engagementLevel,
            lastActiveDate = journals.firstOrNull()?.createdAt?.toLocalDate(),
            daysSinceLastEntry = calculateDaysSinceLastEntry(journals),
            preferredJournalTime = inferPreferredJournalTime(journals),
            averageSessionDuration = calculateAverageSessionDuration(journals),
            preferredFeatures = inferPreferredUnlockableFeatures(journals, sessions, microEntries),
            currentStreak = streak,
            recentThemes = themes,
            recurringChallenges = challenges,
            growthAreas = growthAreas,
            recentWins = wins,
            trustLevel = trustLevel,
            hasCompletedOnboarding = preferences.onboardingCompleted,
            hasTalkedToHaven = sessions.isNotEmpty(),
            hasSharedDeepContent = hasSharedDeepContent(journals, sessions),
            preferredTone = preferredTone,
            firstWeekStage = firstWeekStage,
            isInFirstWeek = daysWithPrody <= 7
        )
    }

    // =============================================================================================
    // ARCHETYPE DETECTION
    // =============================================================================================

    private fun detectArchetype(
        journals: List<JournalEntryEntity>,
        streak: DualStreakStatus,
        daysWithPrody: Int
    ): UserArchetype {
        // New user - first week
        if (daysWithPrody <= 7) {
            return UserArchetype.EXPLORER
        }

        val daysSinceLastEntry = calculateDaysSinceLastEntry(journals)

        // Returning user - was gone more than 14 days
        if (daysSinceLastEntry > 14) {
            return UserArchetype.RETURNING
        }

        // Analyze mood patterns
        val recentMoods = journals.take(10).mapNotNull { Mood.fromString(it.mood) }
        val negativeMoodRatio = recentMoods.count { it.isNegative }.toFloat() / recentMoods.size.coerceAtLeast(1)
        val positiveMoodRatio = recentMoods.count { it.isPositive }.toFloat() / recentMoods.size.coerceAtLeast(1)

        // Struggling - high negative mood ratio or declining patterns
        if (negativeMoodRatio > 0.6f || detectDecliningPattern(journals)) {
            return UserArchetype.STRUGGLING
        }

        // Thriving - high positive mood ratio and consistent engagement
        if (positiveMoodRatio > 0.7f && streak.reflectionStreak.current >= 3) {
            return UserArchetype.THRIVING
        }

        // Consistent - regular engagement
        if (streak.reflectionStreak.current >= 5 || daysSinceLastEntry <= 2) {
            return UserArchetype.CONSISTENT
        }

        // Sporadic - irregular engagement
        return UserArchetype.SPORADIC
    }

    private fun detectDecliningPattern(journals: List<JournalEntryEntity>): Boolean {
        if (journals.size < 5) return false

        val recentHalf = journals.take(journals.size / 2)
        val olderHalf = journals.drop(journals.size / 2)

        val recentPositiveRatio = recentHalf.count { Mood.fromString(it.mood).isPositive }.toFloat() / recentHalf.size
        val olderPositiveRatio = olderHalf.count { Mood.fromString(it.mood).isPositive }.toFloat() / olderHalf.size

        // Declining if recent entries are notably more negative
        return olderPositiveRatio - recentPositiveRatio > 0.2f
    }

    // =============================================================================================
    // EMOTIONAL STATE INFERENCE
    // =============================================================================================

    private data class EmotionalState(
        val trend: MoodTrend,
        val dominantMood: Mood?,
        val energy: EnergyLevel
    )

    private fun inferEmotionalState(journals: List<JournalEntryEntity>): EmotionalState {
        if (journals.isEmpty()) {
            return EmotionalState(MoodTrend.STABLE, null, EnergyLevel.MEDIUM)
        }

        // Use MoodAnalyticsEngine for sophisticated analysis
        val analytics = moodAnalyticsEngine.generateAnalytics(journals, AnalyticsPeriod.TWO_WEEKS)

        // Determine trend
        val trend = when {
            journals.size < 3 -> MoodTrend.STABLE
            analytics.streakWithPositiveMood >= 3 -> MoodTrend.IMPROVING
            detectDecliningPattern(journals) -> MoodTrend.DECLINING
            detectVolatilePattern(journals) -> MoodTrend.VOLATILE
            else -> MoodTrend.STABLE
        }

        // Determine energy level from writing patterns
        val avgWordCount = journals.take(5).map { it.wordCount }.average()
        val energy = when {
            avgWordCount < 50 -> EnergyLevel.LOW
            avgWordCount > 200 -> EnergyLevel.HIGH
            else -> EnergyLevel.MEDIUM
        }

        return EmotionalState(
            trend = trend,
            dominantMood = analytics.mostCommonMood,
            energy = energy
        )
    }

    private fun detectVolatilePattern(journals: List<JournalEntryEntity>): Boolean {
        if (journals.size < 4) return false

        val moodChanges = journals.zipWithNext { a, b ->
            val moodA = Mood.fromString(a.mood)
            val moodB = Mood.fromString(b.mood)
            (moodA.isPositive && moodB.isNegative) || (moodA.isNegative && moodB.isPositive)
        }.count { it }

        // Volatile if more than half the entries show mood swings
        return moodChanges.toFloat() / (journals.size - 1) > 0.5f
    }

    // =============================================================================================
    // STRESS SIGNAL DETECTION
    // =============================================================================================

    private fun detectStressSignals(journals: List<JournalEntryEntity>): List<StressSignal> {
        val signals = mutableListOf<StressSignal>()
        val recentContent = journals.take(10).joinToString(" ") { it.content.lowercase() }

        // Check for various stress indicators
        STRESS_PATTERNS.forEach { (type, patterns) ->
            val matchCount = patterns.count { recentContent.contains(it) }
            if (matchCount >= 2) {
                signals.add(
                    StressSignal(
                        type = type,
                        confidence = (matchCount.toFloat() / patterns.size).coerceIn(0.3f, 1.0f),
                        detectedAt = LocalDateTime.now(),
                        context = null
                    )
                )
            }
        }

        return signals.take(3) // Limit to top 3 signals
    }

    // =============================================================================================
    // TRUST LEVEL CALCULATION
    // =============================================================================================

    private fun calculateTrustLevel(
        journals: List<JournalEntryEntity>,
        sessions: List<com.prody.prashant.data.local.entity.HavenSessionEntity>
    ): TrustLevel {
        val journalCount = journals.size
        val avgWordCount = journals.map { it.wordCount }.average().takeIf { !it.isNaN() } ?: 0.0
        val hasUsedHaven = sessions.isNotEmpty()
        val havenSessionCount = sessions.size

        return when {
            // Deep trust - long history, high vulnerability
            journalCount >= 50 && avgWordCount > 150 && havenSessionCount >= 3 -> TrustLevel.DEEP
            // Established - regular use, some vulnerability
            journalCount >= 20 && (avgWordCount > 100 || hasUsedHaven) -> TrustLevel.ESTABLISHED
            // Building - some history
            journalCount >= 5 -> TrustLevel.BUILDING
            // New
            else -> TrustLevel.NEW
        }
    }

    // =============================================================================================
    // TONE INFERENCE
    // =============================================================================================

    private fun inferPreferredTone(journals: List<JournalEntryEntity>): CommunicationTone {
        if (journals.isEmpty()) return CommunicationTone.WARM

        val avgWordCount = journals.take(10).map { it.wordCount }.average()
        val recentContent = journals.take(5).joinToString(" ").lowercase()

        // Check for humor/playfulness indicators
        val playfulIndicators = listOf("haha", "lol", ":)", "funny", "laugh", "joke")
        val hasPlayfulness = playfulIndicators.any { recentContent.contains(it) }

        // Check for directness indicators
        val directIndicators = listOf("need to", "should", "must", "going to", "will")
        val isDirectStyle = directIndicators.count { recentContent.contains(it) } >= 3

        // Check for gentleness needed (struggling)
        val struggles = detectStressSignals(journals)

        return when {
            struggles.isNotEmpty() -> CommunicationTone.GENTLE
            hasPlayfulness -> CommunicationTone.PLAYFUL
            isDirectStyle && avgWordCount < 100 -> CommunicationTone.DIRECT
            else -> CommunicationTone.WARM
        }
    }

    // =============================================================================================
    // ENGAGEMENT & PATTERNS
    // =============================================================================================

    private fun calculateEngagementLevel(journals: List<JournalEntryEntity>, daysWithPrody: Int): EngagementLevel {
        if (daysWithPrody <= 7) return EngagementLevel.NEW

        val daysSinceLastEntry = calculateDaysSinceLastEntry(journals)

        // Returning user
        if (daysSinceLastEntry > 14) return EngagementLevel.RETURNING

        // Calculate entries in last 14 days
        val twoWeeksAgo = System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000L)
        val recentEntryCount = journals.count { it.createdAt >= twoWeeksAgo }
        val avgEntriesPerDay = recentEntryCount.toFloat() / 14

        return when {
            avgEntriesPerDay >= 0.8f -> EngagementLevel.DAILY
            avgEntriesPerDay >= 0.4f -> EngagementLevel.REGULAR
            avgEntriesPerDay >= 0.15f -> EngagementLevel.SPORADIC
            else -> EngagementLevel.CHURNING
        }
    }

    private fun calculateDaysSinceLastEntry(journals: List<JournalEntryEntity>): Int {
        val lastEntry = journals.firstOrNull() ?: return Int.MAX_VALUE
        val lastEntryDate = lastEntry.createdAt.toLocalDate()
        return ChronoUnit.DAYS.between(lastEntryDate, LocalDate.now()).toInt()
    }

    private fun inferPreferredJournalTime(journals: List<JournalEntryEntity>): TimeOfDay? {
        if (journals.size < 5) return null

        val timeDistribution = journals.groupBy { entry ->
            val hour = Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .hour
            TimeOfDay.fromHour(hour)
        }

        return timeDistribution.maxByOrNull { it.value.size }?.key
    }

    private fun calculateAverageSessionDuration(journals: List<JournalEntryEntity>): Duration {
        if (journals.isEmpty()) return Duration.ZERO

        // Estimate based on word count (rough estimate: 20 words per minute)
        val avgWordCount = journals.take(10).map { it.wordCount }.average()
        val estimatedMinutes = (avgWordCount / 20).coerceAtLeast(1.0)

        return estimatedMinutes.minutes
    }

    private fun inferPreferredUnlockableFeatures(
        journals: List<JournalEntryEntity>,
        sessions: List<com.prody.prashant.data.local.entity.HavenSessionEntity>,
        microEntries: List<com.prody.prashant.data.local.entity.MicroEntryEntity>
    ): List<UnlockableFeature> {
        val features = mutableListOf<UnlockableFeature>()

        if (journals.isNotEmpty()) features.add(UnlockableFeature.JOURNAL)
        if (sessions.isNotEmpty()) features.add(UnlockableFeature.HAVEN)
        if (microEntries.isNotEmpty()) features.add(UnlockableFeature.DAILY_WISDOM)

        return features
    }

    // =============================================================================================
    // THEME & PATTERN EXTRACTION
    // =============================================================================================

    private fun extractThemes(journals: List<JournalEntryEntity>): List<String> {
        // Extract from AI themes if available, otherwise analyze content
        val aiThemes = journals.take(10)
            .mapNotNull { it.aiThemes }
            .flatMap { it.split(",") }
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key }

        if (aiThemes.isNotEmpty()) return aiThemes

        // Fallback: Extract common meaningful words
        val content = journals.take(10).joinToString(" ") { it.content.lowercase() }
        return THEME_KEYWORDS.filter { content.contains(it) }.take(5)
    }

    private fun detectRecurringChallenges(journals: List<JournalEntryEntity>): List<String> {
        val content = journals.take(20).joinToString(" ") { it.content.lowercase() }
        return CHALLENGE_PATTERNS.filter { pattern ->
            content.contains(pattern)
        }.take(3)
    }

    private fun detectRecurringPatterns(journals: List<JournalEntryEntity>): List<String> {
        val patterns = mutableListOf<String>()

        // Time patterns
        val timeDistribution = journals.groupBy { entry ->
            TimeOfDay.fromHour(
                Instant.ofEpochMilli(entry.createdAt)
                    .atZone(ZoneId.systemDefault())
                    .hour
            )
        }
        val dominantTime = timeDistribution.maxByOrNull { it.value.size }?.key
        if (dominantTime != null && timeDistribution[dominantTime]!!.size >= journals.size * 0.6) {
            patterns.add("Prefers journaling in the ${dominantTime.displayName.lowercase()}")
        }

        // Mood patterns
        val moodDistribution = journals.groupBy { Mood.fromString(it.mood) }
        val dominantMood = moodDistribution.maxByOrNull { it.value.size }?.key
        if (dominantMood != null && moodDistribution[dominantMood]!!.size >= journals.size * 0.5) {
            patterns.add("Often journals when feeling ${dominantMood.displayName.lowercase()}")
        }

        return patterns.take(3)
    }

    private fun detectGrowthAreas(journals: List<JournalEntryEntity>): List<SoulGrowthArea> {
        // Simplified growth detection - look for themes that appear with improving moods
        if (journals.size < 10) return emptyList()

        val themes = extractThemes(journals)
        val recentMoods = journals.take(5).map { Mood.fromString(it.mood) }
        val positiveRecently = recentMoods.count { it.isPositive } > recentMoods.size / 2

        if (!positiveRecently) return emptyList()

        return themes.take(2).map { theme ->
            SoulGrowthArea(
                theme = theme,
                progress = 0.5f, // Placeholder - would need more sophisticated tracking
                evidence = listOf("Recent positive entries mention this theme"),
                firstMentioned = journals.last().createdAt.toLocalDate(),
                lastMentioned = journals.first().createdAt.toLocalDate()
            )
        }
    }

    private fun detectRecentWins(journals: List<JournalEntryEntity>, streak: DualStreakStatus): List<Win> {
        val wins = mutableListOf<Win>()

        // Streak milestones
        val milestones = listOf(7, 14, 30, 60, 100)
        val wisdomMilestone = milestones.lastOrNull { it <= streak.wisdomStreak.current }
        val reflectionMilestone = milestones.lastOrNull { it <= streak.reflectionStreak.current }

        if (wisdomMilestone != null && wisdomMilestone >= 7) {
            wins.add(
                Win(
                    type = WinType.STREAK_MILESTONE,
                    title = "$wisdomMilestone Day Wisdom Streak",
                    description = "You've engaged with daily wisdom for $wisdomMilestone days",
                    date = LocalDate.now()
                )
            )
        }

        if (reflectionMilestone != null && reflectionMilestone >= 7) {
            wins.add(
                Win(
                    type = WinType.STREAK_MILESTONE,
                    title = "$reflectionMilestone Day Reflection Streak",
                    description = "You've been journaling consistently for $reflectionMilestone days",
                    date = LocalDate.now()
                )
            )
        }

        // First entry win
        if (journals.size == 1) {
            wins.add(
                Win(
                    type = WinType.FIRST_OF_KIND,
                    title = "First Entry",
                    description = "You wrote your first journal entry",
                    date = journals.first().createdAt.toLocalDate()
                )
            )
        }

        return wins.take(3)
    }

    // =============================================================================================
    // FIRST WEEK JOURNEY
    // =============================================================================================

    private suspend fun calculateFirstWeekStage(
        daysWithPrody: Int,
        journals: List<JournalEntryEntity>,
        preferences: UserPreferencesSnapshot
    ): FirstWeekStage? {
        if (daysWithPrody > 7) return FirstWeekStage.GRADUATED

        val journalCount = journals.size
        val hasViewedWisdom = preferences.dailyWisdomViewed

        return when {
            daysWithPrody == 1 && journalCount == 0 -> FirstWeekStage.DAY_1_FIRST_OPEN
            daysWithPrody == 1 && journalCount >= 1 && !hasViewedWisdom -> FirstWeekStage.DAY_1_FIRST_ENTRY
            daysWithPrody == 1 -> FirstWeekStage.DAY_1_FIRST_WISDOM
            daysWithPrody == 2 && journalCount <= 1 -> FirstWeekStage.DAY_2_RETURNING
            daysWithPrody == 2 -> FirstWeekStage.DAY_2_SECOND_ENTRY
            daysWithPrody == 3 -> FirstWeekStage.DAY_3_EXPLORING
            daysWithPrody == 4 -> FirstWeekStage.DAY_4_DEEPENING
            daysWithPrody == 5 -> FirstWeekStage.DAY_5_BUILDING_HABIT
            daysWithPrody == 6 -> FirstWeekStage.DAY_6_ALMOST_THERE
            daysWithPrody == 7 -> FirstWeekStage.DAY_7_CELEBRATION
            else -> FirstWeekStage.GRADUATED
        }
    }

    // =============================================================================================
    // SPECIAL DAYS & TEMPORAL
    // =============================================================================================

    private fun detectSpecialDay(firstLaunchTime: Long): SpecialDay? {
        val today = LocalDate.now()
        val firstLaunch = if (firstLaunchTime > 0) {
            Instant.ofEpochMilli(firstLaunchTime).atZone(ZoneId.systemDefault()).toLocalDate()
        } else null

        // Check for Prody anniversary
        if (firstLaunch != null &&
            firstLaunch.monthValue == today.monthValue &&
            firstLaunch.dayOfMonth == today.dayOfMonth &&
            firstLaunch.year != today.year
        ) {
            val yearsWithPrody = today.year - firstLaunch.year
            return SpecialDay.ProdyAnniversary(yearsWithPrody)
        }

        // Check for New Year
        if (today.monthValue == 1 && today.dayOfMonth <= 7) {
            return SpecialDay.NewYear
        }

        // Check for World Mental Health Day (October 10)
        if (today.monthValue == 10 && today.dayOfMonth == 10) {
            return SpecialDay.MentalHealthDay
        }

        return null
    }

    // =============================================================================================
    // HOME CONTEXT HELPERS
    // =============================================================================================

    private suspend fun shouldSurfaceMemory(context: UserContext): Boolean {
        // Don't overwhelm struggling users
        if (context.isStruggling) return false

        // Higher chance on special days
        if (context.specialDay != null) return kotlin.random.Random.nextFloat() < 0.7f

        // Base 15% chance
        return kotlin.random.Random.nextFloat() < 0.15f
    }

    private suspend fun getMemoryPreview(): MemoryPreview? {
        // Check for anniversary memories
        val today = LocalDate.now()
        val oneYearAgo = today.minusYears(1)
        val oneYearAgoStart = oneYearAgo.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val oneYearAgoEnd = oneYearAgo.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        // This would need a proper DAO query - simplified here
        val entries = journalDao.getRecentEntries(365).first()
        val anniversaryEntry = entries.find {
            it.createdAt >= oneYearAgoStart && it.createdAt < oneYearAgoEnd
        }

        return anniversaryEntry?.let {
            MemoryPreview(
                id = it.id,
                type = MemoryType.JOURNAL_ENTRY,
                preview = it.content.take(100) + "...",
                date = it.createdAt.toLocalDate(),
                surfaceReason = "1 year ago today"
            )
        }
    }

    private fun getSuggestedAction(context: UserContext): SuggestedAction {
        return when {
            context.daysSinceLastEntry >= 1 -> SuggestedAction(
                type = SuggestedActionType.WRITE_JOURNAL,
                title = "Write Today's Entry",
                subtitle = "Capture a thought before the day ends",
                priority = 1,
                route = "journal/new"
            )
            context.isStruggling && !context.hasTalkedToHaven -> SuggestedAction(
                type = SuggestedActionType.VISIT_HAVEN,
                title = "Talk to Haven",
                subtitle = "A space to work through things",
                priority = 2,
                route = "haven"
            )
            else -> SuggestedAction(
                type = SuggestedActionType.WRITE_JOURNAL,
                title = "What's on your mind?",
                subtitle = "Start a new entry",
                priority = 3,
                route = "journal/new"
            )
        }
    }

    private suspend fun getFeatureDiscovery(context: UserContext): FeatureDiscovery? {
        // Only suggest one feature per session
        if (context.isInFirstWeek) return null

        // Feature discovery based on usage patterns
        return when {
            UnlockableFeature.FUTURE_MESSAGE !in context.preferredFeatures &&
            context.daysWithPrody >= 3 -> FeatureDiscovery(
                feature = UnlockableFeature.FUTURE_MESSAGE,
                hook = "You've been reflecting. Want to send a message to future you?",
                benefit = "It's like leaving a note for someone you'll become.",
                ctaText = "Write to Future You",
                route = "future-messages/new"
            )
            UnlockableFeature.HAVEN !in context.preferredFeatures &&
            context.isStruggling -> FeatureDiscovery(
                feature = UnlockableFeature.HAVEN,
                hook = "Rough patch? There's someone here who can help.",
                benefit = "Haven is your private space to work through things.",
                ctaText = "Meet Haven",
                route = "haven"
            )
            else -> null
        }
    }

    private fun getDailyTheme(context: UserContext): DailyTheme {
        return when (context.dayOfWeek) {
            DayOfWeek.MONDAY -> DailyTheme(
                name = "Fresh Start",
                accentColorHex = "#4CAF50",
                promptStyle = PromptStyle.INTENTIONAL,
                wisdomCategory = WisdomCategory.ACTION
            )
            DayOfWeek.TUESDAY -> DailyTheme(
                name = "Building Momentum",
                accentColorHex = "#2196F3",
                promptStyle = PromptStyle.ENERGETIC,
                wisdomCategory = WisdomCategory.GROWTH
            )
            DayOfWeek.WEDNESDAY -> DailyTheme(
                name = "Midweek Clarity",
                accentColorHex = "#9C27B0",
                promptStyle = PromptStyle.ENERGETIC,
                wisdomCategory = WisdomCategory.PERSPECTIVE
            )
            DayOfWeek.THURSDAY -> DailyTheme(
                name = "Gratitude Day",
                accentColorHex = "#FF9800",
                promptStyle = PromptStyle.GRATEFUL,
                wisdomCategory = WisdomCategory.GRATITUDE
            )
            DayOfWeek.FRIDAY -> DailyTheme(
                name = "Reflect & Release",
                accentColorHex = "#673AB7",
                promptStyle = PromptStyle.REFLECTIVE,
                wisdomCategory = WisdomCategory.PEACE
            )
            DayOfWeek.SATURDAY -> DailyTheme(
                name = "Weekend Exploration",
                accentColorHex = "#00BCD4",
                promptStyle = PromptStyle.GENTLE,
                wisdomCategory = WisdomCategory.INTROSPECTION
            )
            DayOfWeek.SUNDAY -> DailyTheme(
                name = "Prepare & Restore",
                accentColorHex = "#FFB300",
                promptStyle = PromptStyle.GENTLE,
                wisdomCategory = WisdomCategory.COURAGE
            )
        }
    }

    // =============================================================================================
    // BUDDHA CONTEXT HELPERS
    // =============================================================================================

    private suspend fun getLastBuddhaInteraction(): LocalDateTime? {
        val lastEntry = journalDao.getRecentEntries(1).first().firstOrNull()
        return lastEntry?.updatedAt?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
    }

    private fun inferWisdomStyle(
        context: UserContext,
        journals: List<JournalEntryEntity>
    ): BuddhaContext.WisdomStyle {
        // Default to stoic, adjust based on user preferences
        val content = journals.take(10).joinToString(" ").lowercase()

        return when {
            content.contains("meditation") || content.contains("mindful") -> BuddhaContext.WisdomStyle.EASTERN
            content.contains("practical") || content.contains("action") -> BuddhaContext.WisdomStyle.PRACTICAL
            context.preferredTone == CommunicationTone.PLAYFUL -> BuddhaContext.WisdomStyle.POETIC
            context.preferredTone == CommunicationTone.DIRECT -> BuddhaContext.WisdomStyle.DIRECT
            else -> BuddhaContext.WisdomStyle.STOIC
        }
    }

    private fun detectSensitiveTopics(journals: List<JournalEntryEntity>): List<String> {
        val content = journals.take(10).joinToString(" ").lowercase()
        return SENSITIVE_TOPICS.filter { content.contains(it) }
    }

    // =============================================================================================
    // HAVEN CONTEXT HELPERS
    // =============================================================================================

    private fun inferTherapeuticApproach(
        context: UserContext,
        sessions: List<com.prody.prashant.data.local.entity.HavenSessionEntity>
    ): TherapeuticApproach {
        // Check what techniques worked in past sessions
        val techniques = sessions.flatMap { session ->
            session.techniquesUsedJson.split(",").map { it.trim().lowercase() }
        }

        return when {
            "mindfulness" in techniques || "breathing" in techniques -> TherapeuticApproach.MINDFULNESS
            "cbt" in techniques || "cognitive" in techniques -> TherapeuticApproach.CBT
            "dbt" in techniques || "dialectical" in techniques -> TherapeuticApproach.DBT
            "acceptance" in techniques -> TherapeuticApproach.ACT
            else -> TherapeuticApproach.GENERAL
        }
    }

    // =============================================================================================
    // NOTIFICATION CONTEXT HELPERS
    // =============================================================================================

    private suspend fun getLastNotificationTime(): LocalDateTime? {
        // Would need a notification history table - placeholder
        return null
    }

    private suspend fun getNotificationCountToday(): Int {
        // Would need a notification history table - placeholder
        return 0
    }

    private suspend fun calculateNotificationOpenRate(): Float {
        // Would need a notification history table - placeholder
        return 0.5f
    }

    private suspend fun inferPreferredNotificationTimes(): List<Int> {
        // Based on journal times and app opens
        val journals = journalDao.getRecentEntries(30).first()
        val hours = journals.map { entry ->
            Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .hour
        }.groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(3)
            .map { it.key }

        return hours.ifEmpty { listOf(9, 20) } // Default morning and evening
    }

    private suspend fun inferIgnoredNotificationTimes(): List<Int> {
        // Would need notification interaction history - placeholder
        return listOf(0, 1, 2, 3, 4, 5) // Late night hours
    }

    private suspend fun getLastAppOpenTime(): LocalDateTime? {
        val lastEntry = journalDao.getRecentEntries(1).first().firstOrNull()
        return lastEntry?.createdAt?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
    }

    // =============================================================================================
    // MISC HELPERS
    // =============================================================================================

    private fun isContextStale(): Boolean {
        return ChronoUnit.MINUTES.between(lastContextRefresh, LocalDateTime.now()) > contextStalenessThreshold.inWholeMinutes
    }

    private fun calculateDaysWithPrody(firstLaunchTime: Long): Int {
        if (firstLaunchTime <= 0) return 1
        val firstLaunch = Instant.ofEpochMilli(firstLaunchTime).atZone(ZoneId.systemDefault()).toLocalDate()
        return ChronoUnit.DAYS.between(firstLaunch, LocalDate.now()).toInt() + 1
    }

    private fun hasSharedDeepContent(
        journals: List<JournalEntryEntity>,
        sessions: List<com.prody.prashant.data.local.entity.HavenSessionEntity>
    ): Boolean {
        // Check for long entries or Haven sessions
        val hasLongEntries = journals.any { it.wordCount > 300 }
        val hasHavenSessions = sessions.isNotEmpty()
        return hasLongEntries || hasHavenSessions
    }

    private fun Long.toLocalDate(): LocalDate {
        return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    private data class UserPreferencesSnapshot(
        val onboardingCompleted: Boolean,
        val dailyWisdomViewed: Boolean
    )

    private suspend fun loadPreferences(): UserPreferencesSnapshot {
        return UserPreferencesSnapshot(
            onboardingCompleted = preferencesManager.onboardingCompleted.first(),
            dailyWisdomViewed = preferencesManager.dailyWisdomLastShown.first() > 0
        )
    }

    // =============================================================================================
    // PATTERN CONSTANTS
    // =============================================================================================

    companion object {
        private val STRESS_PATTERNS = mapOf(
            StressSignalType.NEGATIVE_LANGUAGE to listOf(
                "hopeless", "worthless", "pointless", "can't", "impossible", "hate", "terrible"
            ),
            StressSignalType.SLEEP_ISSUES to listOf(
                "can't sleep", "insomnia", "tired", "exhausted", "no sleep", "sleepless"
            ),
            StressSignalType.OVERWHELM to listOf(
                "overwhelmed", "too much", "drowning", "buried", "can't keep up"
            ),
            StressSignalType.WORK_STRESS to listOf(
                "work stress", "deadline", "boss", "fired", "overworked", "burnout"
            ),
            StressSignalType.ANXIETY_MARKERS to listOf(
                "anxious", "panic", "worried", "nervous", "scared", "fear"
            ),
            StressSignalType.LOW_SELF_WORTH to listOf(
                "not good enough", "failure", "stupid", "loser", "worthless", "hate myself"
            )
        )

        private val THEME_KEYWORDS = listOf(
            "work", "family", "health", "relationships", "growth", "goals",
            "anxiety", "gratitude", "stress", "happiness", "love", "career",
            "self-care", "mindfulness", "motivation", "purpose", "change"
        )

        private val CHALLENGE_PATTERNS = listOf(
            "struggling with", "keep failing", "can't stop", "always worried",
            "hard to", "difficult to", "frustrated", "stuck", "same problem"
        )

        private val SENSITIVE_TOPICS = listOf(
            "death", "suicide", "abuse", "trauma", "addiction", "self-harm"
        )
    }
}
