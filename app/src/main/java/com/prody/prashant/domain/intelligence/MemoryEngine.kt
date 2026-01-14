package com.prody.prashant.domain.intelligence

import com.prody.prashant.data.local.dao.HavenDao
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.entity.HavenSessionEntity
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.domain.streak.DualStreakManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * ================================================================================================
 * MEMORY ENGINE - The Heart of Prody
 * ================================================================================================
 *
 * This engine creates "magic moments" by intelligently surfacing memories at the right time.
 * It's not about showing random old entries - it's about creating emotional connection through
 * timely, meaningful reminders of the user's journey.
 *
 * Memory surfacing philosophy:
 * - Anniversary memories: "1 year ago today, you wrote..."
 * - Growth contrast: "Look how far you've come since..."
 * - Thematic echoes: When journaling about work, surface a related insight from the past
 * - Milestone callbacks: First entry anniversary, 100th entry, etc.
 * - Positive reinforcement: Surface wins during hard times
 *
 * Key principles:
 * - Never overwhelm - max 1 memory per day, max 3 per week
 * - Be sensitive - don't surface sad memories during hard times (unless growth-related)
 * - Create delight - the goal is a "warm fuzzy" feeling, not information overload
 * - Respect context - consider what user is doing when deciding to surface
 */
@Singleton
class MemoryEngine @Inject constructor(
    private val journalDao: JournalDao,
    private val havenDao: HavenDao,
    private val preferencesManager: PreferencesManager,
    private val dualStreakManager: DualStreakManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Track memory surfacing to avoid overwhelming user
    private val _lastSurfacedMemory = MutableStateFlow<SurfacedMemory?>(null)
    val lastSurfacedMemory: StateFlow<SurfacedMemory?> = _lastSurfacedMemory.asStateFlow()

    // Memory surfacing state
    private var lastMemorySurfaceDate: LocalDate? = null
    private var memoriesSurfacedThisWeek: Int = 0

    // =============================================================================================
    // PUBLIC API
    // =============================================================================================

    /**
     * Get a memory to surface, if appropriate right now.
     * Returns null if no memory should be shown (too recent, user struggling, etc.)
     *
     * Call this:
     * - On app open
     * - After completing a journal entry (with context)
     * - When viewing the home screen
     */
    suspend fun getMemoryToSurface(
        userContext: UserContext,
        surfaceContext: MemorySurfaceContext = MemorySurfaceContext.APP_OPEN
    ): SurfacedMemory? {
        // Check if we should surface a memory at all
        if (!shouldSurfaceMemory(userContext, surfaceContext)) {
            return null
        }

        // Try to find a memory in priority order
        val memory = findBestMemory(userContext, surfaceContext)

        if (memory != null) {
            // Track that we surfaced a memory
            markMemorySurfaced(memory)
            _lastSurfacedMemory.value = memory
        }

        return memory
    }

    /**
     * Get memories related to what the user is currently writing about.
     * Used for "related memories" suggestions during journaling.
     */
    suspend fun getRelatedMemories(
        currentContent: String,
        currentMood: Mood?,
        limit: Int = 3
    ): List<Memory> {
        if (currentContent.length < 20) return emptyList()

        val allEntries = journalDao.getRecentEntries(100).first()
        if (allEntries.size < 5) return emptyList()

        // Extract themes from current content
        val currentThemes = extractThemesFromContent(currentContent)
        if (currentThemes.isEmpty()) return emptyList()

        // Find entries with similar themes
        return allEntries
            .filter { entry ->
                val entryDate = entry.createdAt.toLocalDate()
                // Not too recent (at least 7 days ago)
                ChronoUnit.DAYS.between(entryDate, LocalDate.now()) >= 7
            }
            .mapNotNull { entry ->
                val entryThemes = extractThemesFromContent(entry.content)
                val themeOverlap = currentThemes.intersect(entryThemes.toSet())

                if (themeOverlap.isNotEmpty()) {
                    val significance = calculateSignificance(entry, themeOverlap.size)
                    createMemoryFromJournalEntry(
                        entry = entry,
                        surfaceReason = "You wrote about ${themeOverlap.first()} before",
                        significance = significance
                    )
                } else null
            }
            .sortedByDescending { it.significance.surfaceWeight }
            .take(limit)
    }

    /**
     * Get anniversary memories - entries from exactly X years/months ago.
     * Great for "On This Day" features.
     */
    suspend fun getAnniversaryMemories(): List<AnniversaryMemory> {
        val today = LocalDate.now()
        val memories = mutableListOf<AnniversaryMemory>()
        val allEntries = journalDao.getRecentEntries(365).first()

        // Check for 1 year anniversaries
        val oneYearAgo = today.minusYears(1)
        val oneYearEntries = allEntries.filter { entry ->
            val entryDate = entry.createdAt.toLocalDate()
            entryDate.monthValue == oneYearAgo.monthValue &&
                entryDate.dayOfMonth == oneYearAgo.dayOfMonth &&
                entryDate.year == oneYearAgo.year
        }

        oneYearEntries.forEach { entry ->
            memories.add(
                AnniversaryMemory(
                    memory = createMemoryFromJournalEntry(
                        entry = entry,
                        surfaceReason = "1 year ago today",
                        significance = MemorySignificance.ANNIVERSARY
                    ),
                    yearsAgo = 1,
                    monthsAgo = null,
                    exactDate = entry.createdAt.toLocalDate()
                )
            )
        }

        // Check for 6 month anniversaries
        val sixMonthsAgo = today.minusMonths(6)
        val sixMonthEntries = allEntries.filter { entry ->
            val entryDate = entry.createdAt.toLocalDate()
            entryDate.monthValue == sixMonthsAgo.monthValue &&
                entryDate.dayOfMonth == sixMonthsAgo.dayOfMonth &&
                entryDate.year == sixMonthsAgo.year
        }

        sixMonthEntries.forEach { entry ->
            memories.add(
                AnniversaryMemory(
                    memory = createMemoryFromJournalEntry(
                        entry = entry,
                        surfaceReason = "6 months ago today",
                        significance = MemorySignificance.ANNIVERSARY
                    ),
                    yearsAgo = null,
                    monthsAgo = 6,
                    exactDate = entry.createdAt.toLocalDate()
                )
            )
        }

        // Check for 1 month anniversaries (for newer users)
        val oneMonthAgo = today.minusMonths(1)
        val oneMonthEntries = allEntries.filter { entry ->
            val entryDate = entry.createdAt.toLocalDate()
            entryDate.monthValue == oneMonthAgo.monthValue &&
                entryDate.dayOfMonth == oneMonthAgo.dayOfMonth &&
                entryDate.year == oneMonthAgo.year
        }

        oneMonthEntries.take(1).forEach { entry ->
            memories.add(
                AnniversaryMemory(
                    memory = createMemoryFromJournalEntry(
                        entry = entry,
                        surfaceReason = "1 month ago today",
                        significance = MemorySignificance.ANNIVERSARY
                    ),
                    yearsAgo = null,
                    monthsAgo = 1,
                    exactDate = entry.createdAt.toLocalDate()
                )
            )
        }

        return memories.sortedByDescending { it.yearsAgo ?: 0 }
    }

    /**
     * Get growth contrast memories - show how far user has come.
     * "6 months ago you wrote about struggling with X. Look how far you've come!"
     */
    suspend fun getGrowthContrastMemory(userContext: UserContext): GrowthContrastMemory? {
        if (userContext.totalEntries < 20) return null
        if (!userContext.isThriving) return null // Only show growth contrast when user is doing well

        val allEntries = journalDao.getRecentEntries(100).first()
        if (allEntries.size < 20) return null

        // Look for an old entry where they were struggling
        val oldStruggleEntry = allEntries
            .filter { entry ->
                val entryDate = entry.createdAt.toLocalDate()
                val daysAgo = ChronoUnit.DAYS.between(entryDate, LocalDate.now())
                daysAgo >= 60 && daysAgo <= 180 // 2-6 months ago
            }
            .find { entry ->
                val mood = Mood.fromString(entry.mood)
                mood == Mood.ANXIOUS || mood == Mood.SAD || mood == Mood.CONFUSED
            } ?: return null

        // Find a recent positive entry on similar theme
        val recentEntries = allEntries.take(10)
        val recentPositiveEntry = recentEntries.find { entry ->
            val mood = Mood.fromString(entry.mood)
            mood == Mood.HAPPY || mood == Mood.CALM || mood == Mood.MOTIVATED || mood == Mood.GRATEFUL
        } ?: return null

        val oldThemes = extractThemesFromContent(oldStruggleEntry.content)
        val recentThemes = extractThemesFromContent(recentPositiveEntry.content)
        val sharedTheme = oldThemes.intersect(recentThemes.toSet()).firstOrNull()

        return GrowthContrastMemory(
            oldMemory = createMemoryFromJournalEntry(
                entry = oldStruggleEntry,
                surfaceReason = "Where you were",
                significance = MemorySignificance.CONTRAST
            ),
            currentState = "You're in a much better place now",
            sharedTheme = sharedTheme,
            growthMessage = generateGrowthMessage(oldStruggleEntry, recentPositiveEntry),
            daysBetween = ChronoUnit.DAYS.between(
                oldStruggleEntry.createdAt.toLocalDate(),
                LocalDate.now()
            ).toInt()
        )
    }

    /**
     * Get first entry memory - special milestone.
     */
    suspend fun getFirstEntryMemory(): Memory? {
        val allEntries = journalDao.getAllEntries().first()
        val firstEntry = allEntries.lastOrNull() ?: return null

        return createMemoryFromJournalEntry(
            entry = firstEntry,
            surfaceReason = "Your very first entry",
            significance = MemorySignificance.MILESTONE
        )
    }

    /**
     * Check if there's a milestone to celebrate.
     */
    suspend fun getMilestoneToSurface(userContext: UserContext): MilestoneMemory? {
        val totalEntries = userContext.totalEntries
        val daysWithPrody = userContext.daysWithPrody

        // Entry milestones
        val entryMilestones = listOf(1, 5, 10, 25, 50, 100, 200, 365, 500, 1000)
        val entryMilestone = entryMilestones.find { it == totalEntries }

        if (entryMilestone != null) {
            return MilestoneMemory(
                type = MemoryMilestoneType.ENTRY_COUNT,
                number = entryMilestone,
                title = getMilestoneTitle(MemoryMilestoneType.ENTRY_COUNT, entryMilestone),
                message = getMilestoneMessage(MemoryMilestoneType.ENTRY_COUNT, entryMilestone),
                celebrationType = getCelebrationType(entryMilestone)
            )
        }

        // Days with Prody milestones
        val dayMilestones = listOf(7, 30, 60, 90, 180, 365, 500, 730, 1000)
        val dayMilestone = dayMilestones.find { it == daysWithPrody }

        if (dayMilestone != null) {
            return MilestoneMemory(
                type = MemoryMilestoneType.DAYS_WITH_PRODY,
                number = dayMilestone,
                title = getMilestoneTitle(MemoryMilestoneType.DAYS_WITH_PRODY, dayMilestone),
                message = getMilestoneMessage(MemoryMilestoneType.DAYS_WITH_PRODY, dayMilestone),
                celebrationType = getCelebrationType(dayMilestone)
            )
        }

        // Streak milestones (already handled in streak manager, but we can enhance here)
        val streak = dualStreakManager.getDualStreakStatus()
        val streakMilestones = listOf(7, 14, 30, 60, 100, 365)

        val wisdomMilestone = streakMilestones.find { it == streak.wisdomStreak.current }
        if (wisdomMilestone != null) {
            return MilestoneMemory(
                type = MemoryMilestoneType.WISDOM_STREAK,
                number = wisdomMilestone,
                title = getMilestoneTitle(MemoryMilestoneType.WISDOM_STREAK, wisdomMilestone),
                message = getMilestoneMessage(MemoryMilestoneType.WISDOM_STREAK, wisdomMilestone),
                celebrationType = getCelebrationType(wisdomMilestone)
            )
        }

        val reflectionMilestone = streakMilestones.find { it == streak.reflectionStreak.current }
        if (reflectionMilestone != null) {
            return MilestoneMemory(
                type = MemoryMilestoneType.REFLECTION_STREAK,
                number = reflectionMilestone,
                title = getMilestoneTitle(MemoryMilestoneType.REFLECTION_STREAK, reflectionMilestone),
                message = getMilestoneMessage(MemoryMilestoneType.REFLECTION_STREAK, reflectionMilestone),
                celebrationType = getCelebrationType(reflectionMilestone)
            )
        }

        return null
    }

    /**
     * Surface a haven breakthrough moment - when a haven session had a significant impact.
     */
    suspend fun getHavenBreakthroughMemory(): Memory? {
        val sessions = havenDao.getRecentSessions(userId = "local", limit = 20).first()

        // Find a session where mood improved significantly
        val breakthroughSession = sessions.find { session: HavenSessionEntity ->
            val moodBefore: Int = session.moodBefore ?: return@find false
            val moodAfter: Int = session.moodAfter ?: return@find false
            (moodAfter - moodBefore) >= 3 // At least 3-point improvement
        } ?: return null

        return createMemoryFromHavenSession(
            session = breakthroughSession,
            surfaceReason = "A breakthrough moment with Haven",
            significance = MemorySignificance.MILESTONE
        )
    }

    // =============================================================================================
    // PRIVATE HELPERS - MEMORY FINDING
    // =============================================================================================

    private suspend fun shouldSurfaceMemory(
        userContext: UserContext,
        context: MemorySurfaceContext
    ): Boolean {
        // Don't surface if user is struggling (unless it's a supportive memory)
        if (userContext.isStruggling && context != MemorySurfaceContext.SUPPORTIVE) {
            return false
        }

        // Don't surface too frequently
        if (lastMemorySurfaceDate == LocalDate.now()) {
            return false
        }

        // Don't surface more than 3 times per week
        if (memoriesSurfacedThisWeek >= 3) {
            // Reset weekly counter if it's a new week
            val today = LocalDate.now()
            val weekStart = today.minusDays(today.dayOfWeek.value.toLong() - 1)
            if (lastMemorySurfaceDate?.isBefore(weekStart) == true) {
                memoriesSurfacedThisWeek = 0
            } else {
                return false
            }
        }

        // In first week, don't surface memories (they have none)
        if (userContext.isInFirstWeek) {
            return false
        }

        // Need at least 10 entries for meaningful memories
        if (userContext.totalEntries < 10) {
            return false
        }

        // Probability-based surfacing for natural feeling
        val surfaceProbability = when (context) {
            MemorySurfaceContext.APP_OPEN -> 0.15f
            MemorySurfaceContext.POST_JOURNAL -> 0.25f
            MemorySurfaceContext.HOME_SCREEN -> 0.10f
            MemorySurfaceContext.SUPPORTIVE -> 0.40f
            MemorySurfaceContext.ANNIVERSARY_CHECK -> 1.0f
        }

        return Random.nextFloat() < surfaceProbability
    }

    private suspend fun findBestMemory(
        userContext: UserContext,
        context: MemorySurfaceContext
    ): SurfacedMemory? {
        // Priority 1: Anniversary memories (always show if available)
        val anniversaryMemories = getAnniversaryMemories()
        if (anniversaryMemories.isNotEmpty()) {
            val best = anniversaryMemories.first()
            return SurfacedMemory(
                memory = best.memory,
                surfaceReason = best.memory.surfaceReason,
                context = context,
                shouldAnimate = true,
                actionText = "Revisit this moment",
                dismissText = "Maybe later"
            )
        }

        // Priority 2: Milestone to celebrate
        val milestone = getMilestoneToSurface(userContext)
        if (milestone != null) {
            return SurfacedMemory(
                memory = Memory(
                    id = 0,
                    type = MemoryType.MILESTONE_ACHIEVED,
                    date = LocalDate.now(),
                    preview = milestone.title,
                    fullContent = milestone.message,
                    mood = null,
                    significance = MemorySignificance.MILESTONE,
                    surfaceReason = "Milestone achieved!"
                ),
                surfaceReason = "Milestone achieved!",
                context = context,
                shouldAnimate = true,
                actionText = "Celebrate",
                dismissText = "Thanks!"
            )
        }

        // Priority 3: Growth contrast (when user is thriving)
        if (userContext.isThriving) {
            val growthMemory = getGrowthContrastMemory(userContext)
            if (growthMemory != null) {
                return SurfacedMemory(
                    memory = growthMemory.oldMemory,
                    surfaceReason = growthMemory.growthMessage,
                    context = context,
                    shouldAnimate = true,
                    actionText = "See my journey",
                    dismissText = "Maybe later"
                )
            }
        }

        // Priority 4: Random positive memory
        val positiveMemory = getRandomPositiveMemory()
        if (positiveMemory != null) {
            return SurfacedMemory(
                memory = positiveMemory,
                surfaceReason = positiveMemory.surfaceReason,
                context = context,
                shouldAnimate = false,
                actionText = "Read more",
                dismissText = "Not now"
            )
        }

        return null
    }

    private suspend fun getRandomPositiveMemory(): Memory? {
        val allEntries = journalDao.getRecentEntries(100).first()

        val positiveEntries = allEntries.filter { entry ->
            val mood = Mood.fromString(entry.mood)
            val daysAgo = ChronoUnit.DAYS.between(entry.createdAt.toLocalDate(), LocalDate.now())
            (mood == Mood.HAPPY || mood == Mood.GRATEFUL || mood == Mood.MOTIVATED || mood == Mood.EXCITED) &&
                daysAgo >= 14 // At least 2 weeks old
        }

        if (positiveEntries.isEmpty()) return null

        val randomEntry = positiveEntries.random()
        val daysAgo = ChronoUnit.DAYS.between(randomEntry.createdAt.toLocalDate(), LocalDate.now())

        return createMemoryFromJournalEntry(
            entry = randomEntry,
            surfaceReason = when {
                daysAgo >= 365 -> "A happy memory from ${daysAgo / 365} year${if (daysAgo / 365 > 1) "s" else ""} ago"
                daysAgo >= 30 -> "A happy memory from ${daysAgo / 30} month${if (daysAgo / 30 > 1) "s" else ""} ago"
                else -> "A happy memory from $daysAgo days ago"
            },
            significance = MemorySignificance.THEMATIC
        )
    }

    // =============================================================================================
    // PRIVATE HELPERS - MEMORY CREATION
    // =============================================================================================

    private fun createMemoryFromJournalEntry(
        entry: JournalEntryEntity,
        surfaceReason: String,
        significance: MemorySignificance
    ): Memory {
        return Memory(
            id = entry.id,
            type = MemoryType.JOURNAL_ENTRY,
            date = entry.createdAt.toLocalDate(),
            preview = createPreview(entry.content),
            fullContent = entry.content,
            mood = Mood.fromString(entry.mood),
            significance = significance,
            surfaceReason = surfaceReason
        )
    }

    private fun createMemoryFromHavenSession(
        session: HavenSessionEntity,
        surfaceReason: String,
        significance: MemorySignificance
    ): Memory {
        val preview = session.keyInsightsJson?.take(150) ?: "A meaningful conversation with Haven"

        return Memory(
            id = session.id,
            type = MemoryType.HAVEN_BREAKTHROUGH,
            date = session.startedAt.toLocalDate(),
            preview = preview,
            fullContent = session.keyInsightsJson ?: "",
            mood = null,
            significance = significance,
            surfaceReason = surfaceReason
        )
    }

    private fun createPreview(content: String): String {
        // Get first meaningful sentence or first 100 chars
        val firstSentence = content.split(Regex("[.!?]")).firstOrNull()?.trim()

        return when {
            firstSentence != null && firstSentence.length >= 20 && firstSentence.length <= 150 -> firstSentence
            content.length <= 100 -> content
            else -> content.take(100).substringBeforeLast(" ") + "..."
        }
    }

    // =============================================================================================
    // PRIVATE HELPERS - THEME EXTRACTION
    // =============================================================================================

    private fun extractThemesFromContent(content: String): List<String> {
        val lowercaseContent = content.lowercase()
        return THEME_KEYWORDS.filter { lowercaseContent.contains(it) }
    }

    private fun calculateSignificance(entry: JournalEntryEntity, themeOverlapCount: Int): MemorySignificance {
        // Bookmarked entries are more significant
        if (entry.isBookmarked) return MemorySignificance.MILESTONE

        // Longer entries tend to be more significant
        if (entry.wordCount > 300) return MemorySignificance.MILESTONE

        // Multiple theme overlaps increase significance
        if (themeOverlapCount >= 3) return MemorySignificance.THEMATIC

        return MemorySignificance.CALLBACK
    }

    // =============================================================================================
    // PRIVATE HELPERS - MESSAGE GENERATION
    // =============================================================================================

    private fun generateGrowthMessage(oldEntry: JournalEntryEntity, recentEntry: JournalEntryEntity): String {
        val daysAgo = ChronoUnit.DAYS.between(oldEntry.createdAt.toLocalDate(), LocalDate.now())
        val monthsAgo = daysAgo / 30

        val timePhrase = when {
            monthsAgo >= 6 -> "$monthsAgo months ago"
            monthsAgo >= 1 -> "$monthsAgo month${if (monthsAgo > 1) "s" else ""} ago"
            else -> "$daysAgo days ago"
        }

        return "Look how far you've come since $timePhrase"
    }

    private fun getMilestoneTitle(type: MemoryMilestoneType, number: Int): String {
        return when (type) {
            MemoryMilestoneType.ENTRY_COUNT -> when (number) {
                1 -> "First Entry!"
                5 -> "Getting Started"
                10 -> "Finding Your Rhythm"
                25 -> "Consistent Writer"
                50 -> "Dedicated Journaler"
                100 -> "Century of Thoughts"
                200 -> "Prolific Writer"
                365 -> "A Year of Entries"
                500 -> "Unstoppable"
                1000 -> "Legendary Writer"
                else -> "$number Entries"
            }
            MemoryMilestoneType.DAYS_WITH_PRODY -> when (number) {
                7 -> "One Week Together"
                30 -> "Month-Long Journey"
                60 -> "Two Months Strong"
                90 -> "Quarter Year"
                180 -> "Half Year Milestone"
                365 -> "One Year Anniversary"
                500 -> "500 Days of Growth"
                730 -> "Two Year Journey"
                1000 -> "1000 Days Together"
                else -> "$number Days"
            }
            MemoryMilestoneType.WISDOM_STREAK -> when (number) {
                7 -> "Week of Wisdom"
                14 -> "Wisdom Seeker"
                30 -> "Monthly Wisdom"
                60 -> "Wisdom Devotee"
                100 -> "Century of Wisdom"
                365 -> "Year of Daily Wisdom"
                else -> "$number Day Wisdom Streak"
            }
            MemoryMilestoneType.REFLECTION_STREAK -> when (number) {
                7 -> "Week of Reflection"
                14 -> "Deep Thinker"
                30 -> "Monthly Reflector"
                60 -> "Reflection Master"
                100 -> "Century of Reflection"
                365 -> "Year of Daily Reflection"
                else -> "$number Day Reflection Streak"
            }
        }
    }

    private fun getMilestoneMessage(type: MemoryMilestoneType, number: Int): String {
        return when (type) {
            MemoryMilestoneType.ENTRY_COUNT -> when (number) {
                1 -> "You've taken the first step on your journey of self-discovery."
                5 -> "Five entries down! You're building a beautiful habit."
                10 -> "Ten entries! Your journal is becoming a treasure trove of insights."
                25 -> "Twenty-five entries. Look at all you've reflected on!"
                50 -> "Half a hundred thoughts captured. That's impressive dedication."
                100 -> "One hundred entries! You've created a meaningful record of your journey."
                200 -> "Two hundred entries. Your commitment to growth is inspiring."
                365 -> "A year's worth of entries. What an incredible achievement!"
                500 -> "Five hundred entries. You're truly dedicated to your practice."
                1000 -> "One thousand entries! You've built something truly remarkable."
                else -> "You've written $number entries. Keep going!"
            }
            MemoryMilestoneType.DAYS_WITH_PRODY -> when (number) {
                7 -> "A full week together! Here's to many more."
                30 -> "A whole month of journaling. You're building something special."
                60 -> "Two months of growth. Your consistency is remarkable."
                90 -> "Three months! A quarter of a year well spent."
                180 -> "Half a year of reflection. Look how far you've come."
                365 -> "One year together! What an incredible journey it's been."
                500 -> "500 days of growth and discovery. Simply amazing."
                730 -> "Two years! Your dedication to self-improvement is inspiring."
                1000 -> "1000 days. You've built something truly meaningful."
                else -> "$number days with Prody. Thank you for the journey."
            }
            MemoryMilestoneType.WISDOM_STREAK, MemoryMilestoneType.REFLECTION_STREAK -> when (number) {
                7 -> "A week of daily practice! Consistency is key to growth."
                14 -> "Two weeks strong! Your dedication is building real habits."
                30 -> "A whole month! This is becoming part of who you are."
                60 -> "Two months of daily practice. Remarkable discipline."
                100 -> "100 days! Very few people achieve this level of consistency."
                365 -> "An entire year of daily practice. You're truly extraordinary."
                else -> "A $number day streak! Keep the momentum going."
            }
        }
    }

    private fun getCelebrationType(number: Int): CelebrationType {
        return when {
            number >= 365 -> CelebrationType.WEEK_COMPLETE // Use biggest celebration
            number >= 100 -> CelebrationType.STREAK_MILESTONE
            number >= 50 -> CelebrationType.GROWTH_MOMENT
            number >= 10 -> CelebrationType.FEATURE_DISCOVERED
            number >= 5 -> CelebrationType.FIRST_ENTRY
            else -> CelebrationType.FIRST_ENTRY
        }
    }

    private fun markMemorySurfaced(memory: SurfacedMemory) {
        lastMemorySurfaceDate = LocalDate.now()
        memoriesSurfacedThisWeek++

        // Persist to preferences
        scope.launch {
            // Could add preferences tracking here if needed
        }
    }

    private fun Long.toLocalDate(): LocalDate {
        return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    // =============================================================================================
    // CONSTANTS
    // =============================================================================================

    companion object {
        private val THEME_KEYWORDS = listOf(
            "work", "job", "career", "boss", "colleague", "meeting", "deadline", "project",
            "family", "mom", "dad", "parent", "sibling", "brother", "sister", "child", "kid",
            "friend", "friendship", "relationship", "partner", "love", "dating", "marriage",
            "health", "exercise", "gym", "workout", "sleep", "tired", "energy", "sick",
            "anxiety", "anxious", "worried", "stress", "stressed", "overwhelmed", "pressure",
            "happy", "joy", "grateful", "thankful", "blessed", "excited", "hopeful",
            "sad", "depressed", "down", "lonely", "alone", "hurt", "pain",
            "growth", "learning", "improve", "progress", "goal", "achievement", "success",
            "fear", "scared", "afraid", "nervous", "doubt", "uncertain",
            "anger", "angry", "frustrated", "annoyed", "irritated",
            "peace", "calm", "relaxed", "mindful", "meditation", "breathing",
            "money", "finance", "budget", "saving", "spending", "debt",
            "home", "house", "apartment", "moving", "neighbors",
            "travel", "vacation", "trip", "adventure", "explore",
            "hobby", "reading", "writing", "music", "art", "cooking", "garden"
        )
    }
}

// ================================================================================================
// MEMORY ENGINE MODELS
// ================================================================================================

/**
 * Context in which a memory might be surfaced.
 */
enum class MemorySurfaceContext {
    APP_OPEN,           // User just opened the app
    POST_JOURNAL,       // User just finished journaling
    HOME_SCREEN,        // User is viewing home screen
    SUPPORTIVE,         // User needs emotional support
    ANNIVERSARY_CHECK   // Explicit check for anniversary memories
}

/**
 * A memory that has been selected for surfacing.
 */
data class SurfacedMemory(
    val memory: Memory,
    val surfaceReason: String,
    val context: MemorySurfaceContext,
    val shouldAnimate: Boolean,
    val actionText: String,
    val dismissText: String
)

/**
 * An anniversary memory with temporal context.
 */
data class AnniversaryMemory(
    val memory: Memory,
    val yearsAgo: Int?,
    val monthsAgo: Int?,
    val exactDate: LocalDate
)

/**
 * A growth contrast memory showing progress.
 */
data class GrowthContrastMemory(
    val oldMemory: Memory,
    val currentState: String,
    val sharedTheme: String?,
    val growthMessage: String,
    val daysBetween: Int
)

/**
 * A milestone memory for celebrations.
 */
data class MilestoneMemory(
    val type: MemoryMilestoneType,
    val number: Int,
    val title: String,
    val message: String,
    val celebrationType: CelebrationType
)

/**
 * Types of milestones tracked in the memory engine.
 *
 * Renamed from MilestoneType to avoid collision with
 * com.prody.prashant.domain.gamification.MemoryMilestoneType.
 */
enum class MemoryMilestoneType {
    ENTRY_COUNT,
    DAYS_WITH_PRODY,
    WISDOM_STREAK,
    REFLECTION_STREAK
}
