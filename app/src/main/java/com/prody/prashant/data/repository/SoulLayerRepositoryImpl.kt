package com.prody.prashant.data.repository

import com.prody.prashant.domain.intelligence.*
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.domain.repository.SoulLayerRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SoulLayerRepository.
 * Orchestrates all Soul Layer components into a unified API.
 */
@Singleton
class SoulLayerRepositoryImpl @Inject constructor(
    private val userContextEngine: UserContextEngine,
    private val memoryEngine: MemoryEngine,
    private val temporalContentEngine: TemporalContentEngine,
    private val firstWeekJourneyManager: FirstWeekJourneyManager,
    private val notificationIntelligence: NotificationIntelligence,
    private val prodyVoice: ProdyVoice
) : SoulLayerRepository {

    // =============================================================================================
    // USER CONTEXT
    // =============================================================================================

    override suspend fun getCurrentContext(): UserContext {
        return userContextEngine.getCurrentContext()
    }

    override suspend fun refreshContext() {
        userContextEngine.refreshContext()
    }

    override fun observeContext(): Flow<UserContext> {
        return userContextEngine.observeContext()
    }

    override suspend fun getContextForBuddha(): BuddhaContext {
        return userContextEngine.getContextForBuddha()
    }

    override suspend fun getContextForHaven(): HavenContext {
        return userContextEngine.getContextForHaven()
    }

    override suspend fun getContextForNotification(): NotificationContext {
        return userContextEngine.getContextForNotification()
    }

    override suspend fun getContextForHome(): HomeContext {
        return userContextEngine.getContextForHome()
    }

    // =============================================================================================
    // MEMORY
    // =============================================================================================

    override suspend fun getMemoryToSurface(context: MemorySurfaceContext): SurfacedMemory? {
        val userContext = userContextEngine.getCurrentContext()
        return memoryEngine.getMemoryToSurface(userContext, context)
    }

    override suspend fun getRelatedMemories(content: String, currentMood: Mood?, limit: Int): List<Memory> {
        return memoryEngine.getRelatedMemories(content, currentMood, limit)
    }

    override suspend fun getAnniversaryMemories(): List<AnniversaryMemory> {
        return memoryEngine.getAnniversaryMemories()
    }

    override suspend fun getGrowthContrastMemory(): GrowthContrastMemory? {
        val userContext = userContextEngine.getCurrentContext()
        return memoryEngine.getGrowthContrastMemory(userContext)
    }

    override suspend fun getMilestoneToSurface(): MilestoneMemory? {
        val userContext = userContextEngine.getCurrentContext()
        return memoryEngine.getMilestoneToSurface(userContext)
    }

    // =============================================================================================
    // TEMPORAL CONTENT
    // =============================================================================================

    override suspend fun getGreeting(): TemporalGreeting {
        val context = userContextEngine.getCurrentContext()
        return temporalContentEngine.getGreeting(context)
    }

    override suspend fun getJournalPrompt(): TemporalPrompt {
        val context = userContextEngine.getCurrentContext()
        return temporalContentEngine.getJournalPrompt(context)
    }

    override suspend fun getPromptOptions(): List<TemporalPrompt> {
        val context = userContextEngine.getCurrentContext()
        return temporalContentEngine.getPromptOptions(context)
    }

    override suspend fun getDailyTheme(): DailyTheme {
        val context = userContextEngine.getCurrentContext()
        return temporalContentEngine.getDailyTheme(context)
    }

    override fun getSeasonalContext(): SeasonalContext {
        return temporalContentEngine.getSeasonalContext()
    }

    override suspend fun getTemporalEvent(): TemporalEvent? {
        val context = userContextEngine.getCurrentContext()
        return temporalContentEngine.getTemporalEvent(context)
    }

    override suspend fun getEveningReflectionContent(): EveningReflectionContent {
        val context = userContextEngine.getCurrentContext()
        return temporalContentEngine.getEveningReflectionContent(context)
    }

    override suspend fun getMorningContent(): MorningContent {
        val context = userContextEngine.getCurrentContext()
        return temporalContentEngine.getMorningContent(context)
    }

    // =============================================================================================
    // FIRST WEEK JOURNEY
    // =============================================================================================

    override suspend fun isInFirstWeek(): Boolean {
        return firstWeekJourneyManager.isInFirstWeek()
    }

    override suspend fun getFirstWeekState(): FirstWeekJourneyState? {
        return firstWeekJourneyManager.getCurrentState()
    }

    override suspend fun getFirstWeekDayContent(): FirstWeekDayContent {
        return firstWeekJourneyManager.getDayContent()
    }

    override suspend fun getWelcomeContent(): WelcomeContent {
        val context = userContextEngine.getCurrentContext()
        return firstWeekJourneyManager.getWelcomeContent(context.displayName)
    }

    override suspend fun markFirstWeekMilestone(milestone: FirstWeekMilestone) {
        firstWeekJourneyManager.markMilestoneCompleted(milestone)
    }

    override fun getCelebrationContent(milestone: FirstWeekMilestone): CelebrationContent {
        return firstWeekJourneyManager.getCelebrationContent(milestone)
    }

    override suspend fun getFirstWeekProgress(): FirstWeekProgress {
        return firstWeekJourneyManager.getProgressSummary()
    }

    override fun observeFirstWeekProgress(): Flow<FirstWeekProgress> {
        return firstWeekJourneyManager.observeProgress()
    }

    // =============================================================================================
    // NOTIFICATION INTELLIGENCE
    // =============================================================================================

    override suspend fun makeNotificationDecision(notification: PendingNotification): NotificationDecision {
        return notificationIntelligence.makeNotificationDecision(notification)
    }

    override suspend fun getOptimalNotificationTime(type: NotificationType): LocalDateTime {
        return notificationIntelligence.getOptimalNotificationTime(type)
    }

    override suspend fun generateNotificationContent(type: NotificationType): NotificationContent {
        return notificationIntelligence.generateNotificationContent(type)
    }

    override suspend fun isGoodTimeToNotify(): Boolean {
        return notificationIntelligence.isGoodTimeToNotify()
    }

    override suspend fun getDailyNotificationSchedule(): DailyNotificationSchedule {
        return notificationIntelligence.getDailyNotificationSchedule()
    }

    // =============================================================================================
    // PRODY VOICE
    // =============================================================================================

    override suspend fun getCopy(location: CopyLocation): String {
        val context = userContextEngine.getCurrentContext()
        return prodyVoice.getCopy(location, context)
    }

    override suspend fun getButtonCopy(button: ButtonType): ButtonCopy {
        val context = userContextEngine.getCurrentContext()
        return prodyVoice.getButtonCopy(button, context)
    }

    override suspend fun getCelebrationCopy(celebration: CelebrationType): CelebrationCopy {
        val context = userContextEngine.getCurrentContext()
        return prodyVoice.getCelebrationCopy(celebration, context)
    }

    override suspend fun getEmptyStateCopy(location: EmptyStateLocation): EmptyStateCopy {
        val context = userContextEngine.getCurrentContext()
        return prodyVoice.getEmptyStateCopy(location, context)
    }

    override fun getErrorCopy(error: ErrorType): ErrorCopy {
        // Error copy doesn't need async context - use empty context for consistent behavior
        return prodyVoice.getErrorCopy(error, UserContext.empty())
    }
}
