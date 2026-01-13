package com.prody.prashant.domain.repository

import com.prody.prashant.domain.intelligence.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Soul Layer intelligence operations.
 * Provides a unified API for accessing all Soul Layer functionality.
 */
interface SoulLayerRepository {

    // =============================================================================================
    // USER CONTEXT
    // =============================================================================================

    /**
     * Get the current user context.
     */
    suspend fun getCurrentContext(): UserContext

    /**
     * Force refresh the user context.
     */
    suspend fun refreshContext()

    /**
     * Observe user context changes.
     */
    fun observeContext(): Flow<UserContext>

    /**
     * Get context optimized for Buddha AI interactions.
     */
    suspend fun getContextForBuddha(): BuddhaContext

    /**
     * Get context optimized for Haven therapy sessions.
     */
    suspend fun getContextForHaven(): HavenContext

    /**
     * Get context optimized for notification decisions.
     */
    suspend fun getContextForNotification(): NotificationContext

    /**
     * Get context optimized for home screen content.
     */
    suspend fun getContextForHome(): HomeContext

    // =============================================================================================
    // MEMORY
    // =============================================================================================

    /**
     * Get a memory to surface if appropriate.
     */
    suspend fun getMemoryToSurface(context: MemorySurfaceContext = MemorySurfaceContext.APP_OPEN): SurfacedMemory?

    /**
     * Get memories related to current content.
     */
    suspend fun getRelatedMemories(content: String, currentMood: com.prody.prashant.domain.model.Mood?, limit: Int = 3): List<Memory>

    /**
     * Get anniversary memories for today.
     */
    suspend fun getAnniversaryMemories(): List<AnniversaryMemory>

    /**
     * Get a growth contrast memory showing progress.
     */
    suspend fun getGrowthContrastMemory(): GrowthContrastMemory?

    /**
     * Get milestone to celebrate.
     */
    suspend fun getMilestoneToSurface(): MilestoneMemory?

    // =============================================================================================
    // TEMPORAL CONTENT
    // =============================================================================================

    /**
     * Get context-aware greeting.
     */
    suspend fun getGreeting(): TemporalGreeting

    /**
     * Get a journal prompt for the current time and user state.
     */
    suspend fun getJournalPrompt(): TemporalPrompt

    /**
     * Get all available prompt options.
     */
    suspend fun getPromptOptions(): List<TemporalPrompt>

    /**
     * Get the daily theme.
     */
    suspend fun getDailyTheme(): DailyTheme

    /**
     * Get seasonal context.
     */
    fun getSeasonalContext(): SeasonalContext

    /**
     * Check for temporal events.
     */
    suspend fun getTemporalEvent(): TemporalEvent?

    /**
     * Get evening reflection content.
     */
    suspend fun getEveningReflectionContent(): EveningReflectionContent

    /**
     * Get morning content.
     */
    suspend fun getMorningContent(): MorningContent

    // =============================================================================================
    // FIRST WEEK JOURNEY
    // =============================================================================================

    /**
     * Check if user is in first week.
     */
    suspend fun isInFirstWeek(): Boolean

    /**
     * Get current first week journey state.
     */
    suspend fun getFirstWeekState(): FirstWeekJourneyState?

    /**
     * Get content for current day of first week.
     */
    suspend fun getFirstWeekDayContent(): FirstWeekDayContent

    /**
     * Get welcome content for day 1.
     */
    suspend fun getWelcomeContent(): WelcomeContent

    /**
     * Mark a first week milestone as completed.
     */
    suspend fun markFirstWeekMilestone(milestone: FirstWeekMilestone)

    /**
     * Get celebration content for a milestone.
     */
    fun getCelebrationContent(milestone: FirstWeekMilestone): CelebrationContent

    /**
     * Get first week progress summary.
     */
    suspend fun getFirstWeekProgress(): FirstWeekProgress

    /**
     * Observe first week progress.
     */
    fun observeFirstWeekProgress(): Flow<FirstWeekProgress>

    // =============================================================================================
    // NOTIFICATION INTELLIGENCE
    // =============================================================================================

    /**
     * Make a notification decision.
     */
    suspend fun makeNotificationDecision(notification: PendingNotification): NotificationDecision

    /**
     * Get optimal notification time.
     */
    suspend fun getOptimalNotificationTime(type: NotificationType): java.time.LocalDateTime

    /**
     * Generate notification content.
     */
    suspend fun generateNotificationContent(type: NotificationType): IntelligentNotificationContent

    /**
     * Check if now is a good time to notify.
     */
    suspend fun isGoodTimeToNotify(): Boolean

    /**
     * Get daily notification schedule.
     */
    suspend fun getDailyNotificationSchedule(): DailyNotificationSchedule

    // =============================================================================================
    // PRODY VOICE
    // =============================================================================================

    /**
     * Get context-aware copy for a location.
     */
    suspend fun getCopy(location: CopyLocation): String

    /**
     * Get button copy.
     */
    suspend fun getButtonCopy(button: ButtonType): ButtonCopy

    /**
     * Get celebration copy.
     */
    suspend fun getCelebrationCopy(celebration: CelebrationType): CelebrationCopy

    /**
     * Get empty state copy.
     */
    suspend fun getEmptyStateCopy(location: EmptyStateLocation): EmptyStateCopy

    /**
     * Get error copy.
     */
    fun getErrorCopy(error: VoiceErrorType): ErrorCopy
}
