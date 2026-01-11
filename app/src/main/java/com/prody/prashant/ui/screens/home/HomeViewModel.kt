package com.prody.prashant.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.ai.BuddhaAiRepository
import com.prody.prashant.data.ai.BuddhaAiResult
import com.prody.prashant.data.local.dao.*
import com.prody.prashant.data.local.entity.SeedEntity
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.data.onboarding.AiHint
import com.prody.prashant.data.onboarding.AiHintType
import com.prody.prashant.data.onboarding.AiOnboardingManager
import com.prody.prashant.data.onboarding.BuddhaGuideCard
import com.prody.prashant.domain.intelligence.*
import com.prody.prashant.domain.progress.ActiveProgressService
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.domain.progress.SeedBloomService
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.domain.repository.SoulLayerRepository
import com.prody.prashant.domain.streak.DualStreakManager
import com.prody.prashant.domain.streak.DualStreakStatus
import com.prody.prashant.util.BuddhaWisdom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * AI Proof Mode debug info - shown on AI surfaces when enabled in Settings.
 * Provides visibility into AI generation status for debugging.
 */
data class AiProofModeInfo(
    val provider: String = "",           // Gemini, OpenRouter, Fallback
    val cacheStatus: String = "",        // HIT, MISS, or empty if no call
    val timestamp: Long = 0L,            // Last successful generation timestamp
    val lastError: String? = null,       // Short error code/message if failed
    val isEnabled: Boolean = false,      // Whether AI Proof Mode is enabled
    val isAiConfigured: Boolean = true   // Whether API key is configured
)

/**
 * AI configuration status for the home screen.
 */
enum class AiConfigurationStatus {
    CONFIGURED,
    MISSING,
    ERROR
}

data class HomeUiState(
    val userName: String = "Growth Seeker",
    val currentStreak: Int = 0,
    val totalPoints: Int = 0,
    val dailyQuote: String = "The obstacle is the way.",
    val dailyQuoteAuthor: String = "Marcus Aurelius",
    val wordOfTheDay: String = "Serendipity",
    val wordDefinition: String = "The occurrence of events by chance in a happy or beneficial way",
    val wordPronunciation: String = "ser-uhn-DIP-i-tee",
    val wordId: Long = 0,
    val dailyProverb: String = "",
    val proverbMeaning: String = "",
    val proverbOrigin: String = "",
    val dailyIdiom: String = "",
    val idiomMeaning: String = "",
    val idiomExample: String = "",
    val idiomId: Long = 0,
    val buddhaThought: String = BuddhaWisdom.getRandomEncouragement(),
    val buddhaThoughtExplanation: String = "",
    val isBuddhaThoughtAiGenerated: Boolean = false,
    val isBuddhaThoughtLoading: Boolean = false,
    val canRefreshBuddhaThought: Boolean = true,
    val journalEntriesThisWeek: Int = 0,
    val wordsLearnedThisWeek: Int = 0,
    val daysActiveThisWeek: Int = 0,
    val isLoading: Boolean = true,
    val hasLoadError: Boolean = false,
    val error: String? = null,
    // AI configuration status for showing warning banner
    val aiConfigurationStatus: AiConfigurationStatus = AiConfigurationStatus.CONFIGURED,
    // Onboarding state
    val showBuddhaGuide: Boolean = false,
    val buddhaGuideCards: List<BuddhaGuideCard> = emptyList(),
    val showDailyWisdomHint: Boolean = false,
    // Reactive state - Did user journal today?
    val journaledToday: Boolean = false,
    val todayEntryMood: String = "",
    val todayEntryPreview: String = "",
    // ============== ACTIVE PROGRESS LAYER ==============
    // Next Action - contextual suggestion based on user behavior
    val nextAction: NextAction? = null,
    // Today's Progress - summary of today's activity
    val todayProgress: TodayProgress = TodayProgress(),
    // Seed -> Bloom mechanic
    val dailySeed: SeedEntity? = null,
    val showProgressFeedback: Boolean = false,
    val progressFeedbackTitle: String = "",
    val progressFeedbackMessage: String = "",
    // ============== AI PROOF MODE ==============
    // Debug info for Buddha Wisdom (shown when AI Proof Mode is enabled)
    val buddhaWisdomProofInfo: AiProofModeInfo = AiProofModeInfo(),
    // ============== DUAL STREAK SYSTEM ==============
    // Two independent streaks: Wisdom and Reflection
    val dualStreakStatus: DualStreakStatus = DualStreakStatus.empty(),
    // ============== SOUL LAYER INTELLIGENCE ==============
    // Context-aware greeting from Soul Layer
    val intelligentGreeting: String = "",
    val greetingSubtext: String = "",
    // First week journey state (null if graduated)
    val isInFirstWeek: Boolean = false,
    val firstWeekDayNumber: Int = 0,
    val firstWeekProgress: FirstWeekProgress? = null,
    val firstWeekDayContent: FirstWeekDayContent? = null,
    val showFirstWeekCelebration: Boolean = false,
    val firstWeekCelebration: CelebrationContent? = null,
    // Surfaced memory (null if none to show)
    val surfacedMemory: SurfacedMemory? = null,
    val showMemoryCard: Boolean = false,
    // Anniversary memories for today
    val anniversaryMemories: List<AnniversaryMemory> = emptyList(),
    // User context for personalization
    val userArchetype: UserArchetype = UserArchetype.EXPLORER,
    val trustLevel: TrustLevel = TrustLevel.NEW,
    val isUserStruggling: Boolean = false,
    val isUserThriving: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userDao: UserDao,
    private val vocabularyDao: VocabularyDao,
    private val quoteDao: QuoteDao,
    private val proverbDao: ProverbDao,
    private val idiomDao: IdiomDao,
    private val journalDao: JournalDao,
    private val preferencesManager: PreferencesManager,
    private val buddhaAiRepository: BuddhaAiRepository,
    private val aiOnboardingManager: AiOnboardingManager,
    private val activeProgressService: ActiveProgressService,
    private val seedBloomService: SeedBloomService,
    private val dualStreakManager: DualStreakManager,
    private val soulLayerRepository: SoulLayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentWordId: Long = 0
    private var lastBuddhaRefreshTime: Long = 0

    companion object {
        private const val TAG = "HomeViewModel"
        private const val REFRESH_COOLDOWN_MS = 30_000L // 30 second cooldown between refreshes
    }

    init {
        loadHomeData()
        loadBuddhaWisdom()
        checkOnboarding()
        loadActiveProgress()
        loadDailySeed()
        observeAiProofMode()
        checkAiConfiguration()
        loadDualStreakStatus()
        loadSoulLayerContent()
    }

    /**
     * Check AI configuration and update UI state accordingly.
     */
    private fun checkAiConfiguration() {
        val isConfigured = buddhaAiRepository.isAiConfigured()
        val status = if (isConfigured) AiConfigurationStatus.CONFIGURED else AiConfigurationStatus.MISSING
        _uiState.update { it.copy(aiConfigurationStatus = status) }
    }

    /**
     * Get the current AI configuration status.
     * Called from UI to determine if warning banner should be shown.
     */
    fun getAiConfigurationStatus(): AiConfigurationStatus {
        return _uiState.value.aiConfigurationStatus
    }

    /**
     * Observe AI Proof Mode preference and update UI state accordingly.
     */
    private fun observeAiProofMode() {
        viewModelScope.launch {
            preferencesManager.debugAiProofMode.collect { isEnabled ->
                _uiState.update { state ->
                    state.copy(
                        buddhaWisdomProofInfo = state.buddhaWisdomProofInfo.copy(isEnabled = isEnabled)
                    )
                }
            }
        }
    }

    // ============== ACTIVE PROGRESS LAYER ==============

    /**
     * Load the Active Progress Layer data:
     * - Next Action suggestion
     * - Today's Progress summary
     */
    private fun loadActiveProgress() {
        viewModelScope.launch {
            try {
                // Load Next Action
                val nextAction = activeProgressService.getNextAction()
                _uiState.update { it.copy(nextAction = nextAction) }

                // Load Today's Progress
                val todayProgress = activeProgressService.getTodayProgress()
                _uiState.update { it.copy(todayProgress = todayProgress) }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading active progress", e)
            }
        }
    }

    /**
     * Refresh the Next Action suggestion (called after user actions)
     */
    fun refreshNextAction() {
        viewModelScope.launch {
            try {
                val nextAction = activeProgressService.getNextAction()
                val todayProgress = activeProgressService.getTodayProgress()
                _uiState.update { it.copy(
                    nextAction = nextAction,
                    todayProgress = todayProgress
                )}
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error refreshing next action", e)
            }
        }
    }

    /**
     * Load today's Seed for the Seed -> Bloom mechanic
     */
    private fun loadDailySeed() {
        viewModelScope.launch {
            try {
                val seed = seedBloomService.getTodaySeed()
                _uiState.update { it.copy(dailySeed = seed) }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading daily seed", e)
            }
        }
    }

    /**
     * Show a progress feedback toast/banner after an action
     */
    fun showProgressFeedback(title: String, message: String) {
        _uiState.update { it.copy(
            showProgressFeedback = true,
            progressFeedbackTitle = title,
            progressFeedbackMessage = message
        )}

        // Auto-dismiss after a few seconds
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            dismissProgressFeedback()
        }
    }

    fun dismissProgressFeedback() {
        _uiState.update { it.copy(showProgressFeedback = false) }
    }

    // ============== DUAL STREAK SYSTEM ==============

    /**
     * Load dual streak status (reactive).
     */
    private fun loadDualStreakStatus() {
        viewModelScope.launch {
            try {
                dualStreakManager.getDualStreakStatusFlow().collect { status ->
                    _uiState.update { it.copy(dualStreakStatus = status) }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading dual streak status", e)
            }
        }
    }

    /**
     * Trigger wisdom streak maintenance when user views wisdom content.
     * Call this when user views quote, word, proverb, or idiom.
     */
    fun onWisdomContentViewed() {
        viewModelScope.launch {
            try {
                val result = dualStreakManager.maintainWisdomStreak()
                // Result can be used to show feedback/celebration if needed
                android.util.Log.d(TAG, "Wisdom streak result: $result")
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error maintaining wisdom streak", e)
            }
        }
    }

    /**
     * Trigger reflection streak maintenance when user writes journal or completes reflection.
     * Call this when user saves a journal entry or completes evening reflection.
     */
    fun onReflectionCompleted() {
        viewModelScope.launch {
            try {
                val result = dualStreakManager.maintainReflectionStreak()
                // Result can be used to show feedback/celebration if needed
                android.util.Log.d(TAG, "Reflection streak result: $result")
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error maintaining reflection streak", e)
            }
        }
    }

    // ============== SOUL LAYER INTELLIGENCE ==============

    /**
     * Load all Soul Layer content for the home screen.
     * This includes:
     * - Context-aware greeting
     * - First week journey state (if applicable)
     * - Memory to surface (if any)
     * - Anniversary memories
     * - User context for personalization
     */
    private fun loadSoulLayerContent() {
        viewModelScope.launch {
            try {
                // Load user context first - it's the foundation
                val context = soulLayerRepository.getCurrentContext()

                // Update basic context info
                _uiState.update { state ->
                    state.copy(
                        userArchetype = context.userArchetype,
                        trustLevel = context.trustLevel,
                        isUserStruggling = context.isStruggling,
                        isUserThriving = context.isThriving,
                        isInFirstWeek = context.isInFirstWeek
                    )
                }

                // Load greeting (intelligent, time-of-day aware)
                loadIntelligentGreeting()

                // Load first week state if in first week
                if (context.isInFirstWeek) {
                    loadFirstWeekState()
                }

                // Load memory to surface (if conditions are right)
                loadSurfacedMemory()

                // Load anniversary memories
                loadAnniversaryMemories()

                android.util.Log.d(TAG, "Soul Layer content loaded - Archetype: ${context.userArchetype}, Trust: ${context.trustLevel}")

            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading Soul Layer content", e)
            }
        }
    }

    /**
     * Load the context-aware intelligent greeting.
     * This considers:
     * - Time of day
     * - User's name
     * - User's emotional state
     * - Special occasions
     * - First week stage
     */
    private suspend fun loadIntelligentGreeting() {
        try {
            val greeting = soulLayerRepository.getGreeting()
            _uiState.update { state ->
                state.copy(
                    intelligentGreeting = greeting.greeting,
                    greetingSubtext = greeting.subtext
                )
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error loading greeting", e)
            // Fallback to generic greeting
            _uiState.update { state ->
                state.copy(
                    intelligentGreeting = "Welcome back",
                    greetingSubtext = "What's on your mind today?"
                )
            }
        }
    }

    /**
     * Load first week journey state.
     * Shows special content for users in their first 7 days.
     */
    private suspend fun loadFirstWeekState() {
        try {
            val state = soulLayerRepository.getFirstWeekState()
            val dayContent = soulLayerRepository.getFirstWeekDayContent()
            val progress = soulLayerRepository.getFirstWeekProgress()

            _uiState.update { uiState ->
                uiState.copy(
                    firstWeekDayNumber = state?.currentDayNumber ?: 1,
                    firstWeekProgress = progress,
                    firstWeekDayContent = dayContent
                )
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error loading first week state", e)
        }
    }

    /**
     * Load a memory to surface if conditions are right.
     * Memories create "magic moments" that delight users.
     */
    private suspend fun loadSurfacedMemory() {
        try {
            val memory = soulLayerRepository.getMemoryToSurface(MemorySurfaceContext.APP_OPEN)
            _uiState.update { state ->
                state.copy(
                    surfacedMemory = memory,
                    showMemoryCard = memory != null
                )
            }

            if (memory != null) {
                android.util.Log.d(TAG, "Surfaced memory: ${memory.surfaceReason} - ${memory.preview.take(50)}...")
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error loading surfaced memory", e)
        }
    }

    /**
     * Load anniversary memories for today.
     * "On this day X years ago..."
     */
    private suspend fun loadAnniversaryMemories() {
        try {
            val anniversaries = soulLayerRepository.getAnniversaryMemories()
            _uiState.update { state ->
                state.copy(anniversaryMemories = anniversaries)
            }

            if (anniversaries.isNotEmpty()) {
                android.util.Log.d(TAG, "Found ${anniversaries.size} anniversary memories for today")
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error loading anniversary memories", e)
        }
    }

    /**
     * Called when user interacts with a surfaced memory.
     */
    fun onMemoryInteracted(interactionType: String) {
        viewModelScope.launch {
            try {
                val memory = _uiState.value.surfacedMemory ?: return@launch

                // Track the interaction in Soul Layer
                // This helps the system learn what memories resonate
                android.util.Log.d(TAG, "Memory interaction: $interactionType for entry ${memory.entryId}")

                // Mark memory as interacted
                // The repository will handle persistence
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error handling memory interaction", e)
            }
        }
    }

    /**
     * Dismiss the memory card.
     */
    fun dismissMemoryCard() {
        _uiState.update { it.copy(showMemoryCard = false) }
        onMemoryInteracted("dismissed")
    }

    /**
     * Expand the memory card for full view.
     */
    fun expandMemoryCard() {
        onMemoryInteracted("expanded")
        // Navigation handled by UI
    }

    /**
     * Called when user completes a first week milestone.
     */
    fun onFirstWeekMilestoneCompleted(milestone: FirstWeekMilestone) {
        viewModelScope.launch {
            try {
                // Mark milestone as completed
                soulLayerRepository.markFirstWeekMilestone(milestone)

                // Get celebration content
                val celebration = soulLayerRepository.getCelebrationContent(milestone)

                _uiState.update { state ->
                    state.copy(
                        showFirstWeekCelebration = true,
                        firstWeekCelebration = celebration
                    )
                }

                // Refresh first week state
                loadFirstWeekState()

                android.util.Log.d(TAG, "First week milestone completed: $milestone")

            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error handling first week milestone", e)
            }
        }
    }

    /**
     * Dismiss the first week celebration.
     */
    fun dismissFirstWeekCelebration() {
        _uiState.update { it.copy(showFirstWeekCelebration = false, firstWeekCelebration = null) }
    }

    /**
     * Refresh Soul Layer content.
     * Called when user triggers a refresh or after significant actions.
     */
    fun refreshSoulLayerContent() {
        viewModelScope.launch {
            try {
                // Force refresh the context
                soulLayerRepository.refreshContext()

                // Reload all Soul Layer content
                loadSoulLayerContent()
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error refreshing Soul Layer content", e)
            }
        }
    }

    private fun checkOnboarding() {
        viewModelScope.launch {
            // Combine both onboarding checks into a single flow subscription
            // to prevent multiple coroutine leaks on configuration changes
            combine(
                aiOnboardingManager.shouldShowBuddhaGuide(),
                aiOnboardingManager.shouldShowHint(AiHintType.DAILY_WISDOM_TIP)
            ) { shouldShowBuddhaGuide, shouldShowDailyWisdomHint ->
                Pair(shouldShowBuddhaGuide, shouldShowDailyWisdomHint)
            }.collect { (shouldShowBuddhaGuide, shouldShowDailyWisdomHint) ->
                _uiState.update { state ->
                    state.copy(
                        showBuddhaGuide = shouldShowBuddhaGuide,
                        buddhaGuideCards = if (shouldShowBuddhaGuide) aiOnboardingManager.getBuddhaGuideCards() else state.buddhaGuideCards,
                        showDailyWisdomHint = shouldShowDailyWisdomHint
                    )
                }
            }
        }
    }

    fun onBuddhaGuideComplete() {
        viewModelScope.launch {
            aiOnboardingManager.markBuddhaGuideShown()
            _uiState.update { it.copy(showBuddhaGuide = false) }
        }
    }

    fun onBuddhaGuideDontShowAgain() {
        viewModelScope.launch {
            aiOnboardingManager.dismissBuddhaGuideForever()
            _uiState.update { it.copy(showBuddhaGuide = false) }
        }
    }

    fun onDailyWisdomHintDismiss() {
        viewModelScope.launch {
            aiOnboardingManager.markHintShown(AiHintType.DAILY_WISDOM_TIP)
            _uiState.update { it.copy(showDailyWisdomHint = false) }
        }
    }

    fun getDailyWisdomHint(): AiHint {
        return aiOnboardingManager.getHintContent(AiHintType.DAILY_WISDOM_TIP)
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            // Combine all reactive flows into a single stream to update the UI atomically.
            // This prevents multiple recompositions and ensures data consistency.
            val weekStart = getWeekStartTimestamp()
            val todayStart = getTodayStartTimestamp()

            // One-time fetch for daily content that doesn't need to be reactive.
            val quote = try { quoteDao.getQuoteOfTheDay() } catch (e: Exception) { android.util.Log.e(TAG, "Failed to load quote", e); null }
            val word = try { vocabularyDao.getWordOfTheDay() } catch (e: Exception) { android.util.Log.e(TAG, "Failed to load word", e); null }
            val proverb = try { proverbDao.getProverbOfTheDay() } catch (e: Exception) { android.util.Log.e(TAG, "Failed to load proverb", e); null }
            val idiom = try { idiomDao.getIdiomOfTheDay() } catch (e: Exception) { android.util.Log.e(TAG, "Failed to load idiom", e); null }

            // Mark daily content as shown
            quote?.let { quoteDao.markAsShownDaily(it.id) }
            word?.let {
                currentWordId = it.id
                vocabularyDao.markAsShownDaily(it.id)
            }
            proverb?.let { proverbDao.markAsShownDaily(it.id) }
            idiom?.let { idiomDao.markAsShownDaily(it.id) }

            combine(
                userDao.getUserProfile(),
                journalDao.getEntriesByDateRange(weekStart, System.currentTimeMillis()),
                vocabularyDao.getLearnedCountSince(weekStart),
                userDao.getStreakHistory(),
                journalDao.getEntriesByDateRange(todayStart, System.currentTimeMillis())
            ) { profile, weeklyJournalEntries, weeklyLearnedWords, streakHistory, todayJournalEntries ->
                // This transform function is called whenever any of the source flows emit a new value.
                // It calculates the new UI state based on the latest data.
                val daysActiveThisWeek = streakHistory.count { it.date >= weekStart }

                val (journaledToday, todayMood, todayPreview) = if (todayJournalEntries.isNotEmpty()) {
                    val latestEntry = todayJournalEntries.maxByOrNull { it.createdAt }
                    Triple(true, latestEntry?.mood ?: "", latestEntry?.content?.take(100) ?: "")
                } else {
                    Triple(false, "", "")
                }

                _uiState.value.copy(
                    userName = profile?.displayName ?: "Growth Seeker",
                    currentStreak = profile?.currentStreak ?: 0,
                    totalPoints = profile?.totalPoints ?: 0,
                    dailyQuote = quote?.content ?: _uiState.value.dailyQuote,
                    dailyQuoteAuthor = quote?.author ?: _uiState.value.dailyQuoteAuthor,
                    wordOfTheDay = word?.word ?: _uiState.value.wordOfTheDay,
                    wordDefinition = word?.definition ?: _uiState.value.wordDefinition,
                    wordPronunciation = word?.pronunciation ?: _uiState.value.wordPronunciation,
                    wordId = word?.id ?: _uiState.value.wordId,
                    dailyProverb = proverb?.content ?: _uiState.value.dailyProverb,
                    proverbMeaning = proverb?.meaning ?: _uiState.value.proverbMeaning,
                    proverbOrigin = proverb?.origin ?: _uiState.value.proverbOrigin,
                    dailyIdiom = idiom?.phrase ?: _uiState.value.dailyIdiom,
                    idiomMeaning = idiom?.meaning ?: _uiState.value.idiomMeaning,
                    idiomExample = idiom?.exampleSentence ?: _uiState.value.idiomExample,
                    idiomId = idiom?.id ?: _uiState.value.idiomId,
                    journalEntriesThisWeek = weeklyJournalEntries.size,
                    wordsLearnedThisWeek = weeklyLearnedWords,
                    daysActiveThisWeek = daysActiveThisWeek,
                    journaledToday = journaledToday,
                    todayEntryMood = todayMood,
                    todayEntryPreview = todayPreview,
                    isLoading = false
                )
            }.catch { e ->
                android.util.Log.e(TAG, "Error in combined home data flow", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasLoadError = true,
                        error = "Failed to load home data. Please check your connection."
                    )
                }
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun markWordAsLearned() {
        viewModelScope.launch {
            try {
                if (currentWordId > 0) {
                    vocabularyDao.markAsLearned(currentWordId)
                    userDao.incrementWordsLearned()
                    userDao.addPoints(25) // Points for learning a word

                    // Update achievement progress
                    userDao.getUserProfileSync()?.let { profile ->
                        userDao.updateAchievementProgress("words_10", profile.wordsLearned)
                        userDao.updateAchievementProgress("words_50", profile.wordsLearned)
                        userDao.updateAchievementProgress("words_100", profile.wordsLearned)
                        userDao.updateAchievementProgress("words_500", profile.wordsLearned)

                        // Check for unlocks
                        checkAndUnlockAchievement("words_10", profile.wordsLearned, 10)
                        checkAndUnlockAchievement("words_50", profile.wordsLearned, 50)
                        checkAndUnlockAchievement("words_100", profile.wordsLearned, 100)
                        checkAndUnlockAchievement("words_500", profile.wordsLearned, 500)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error marking word as learned", e)
            }
        }
    }

    private suspend fun checkAndUnlockAchievement(achievementId: String, progress: Int, requirement: Int) {
        if (progress >= requirement) {
            val achievement = userDao.getAchievementById(achievementId)
            if (achievement != null && !achievement.isUnlocked) {
                userDao.unlockAchievement(achievementId)
                // Award points for achievement
                val points = achievement.rewardValue.toIntOrNull() ?: 100
                userDao.addPoints(points)
            }
        }
    }

    private fun getWeekStartTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getTodayStartTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * Load Buddha's daily wisdom using AI with fallback to curated content.
     * Uses caching to avoid excessive API calls.
     */
    private fun loadBuddhaWisdom(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isBuddhaThoughtLoading = true) }

                // Check if AI is configured (API key present)
                val isAiConfigured = buddhaAiRepository.isAiConfigured()

                // Get stats before the call to determine cache status
                val statsBefore = buddhaAiRepository.getStats()
                val cacheHitsBefore = statsBefore.cacheHits

                val result = buddhaAiRepository.getDailyWisdom(forceRefresh)

                // Get stats after the call
                val statsAfter = buddhaAiRepository.getStats()
                val wasCacheHit = statsAfter.cacheHits > cacheHitsBefore

                when (result) {
                    is BuddhaAiResult.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                buddhaThought = result.data.wisdom,
                                buddhaThoughtExplanation = result.data.explanation,
                                isBuddhaThoughtAiGenerated = result.data.isAiGenerated,
                                isBuddhaThoughtLoading = false,
                                buddhaWisdomProofInfo = state.buddhaWisdomProofInfo.copy(
                                    provider = if (result.data.isAiGenerated) statsAfter.lastProvider.ifEmpty { "Gemini" } else "Fallback",
                                    cacheStatus = if (wasCacheHit) "HIT" else "MISS",
                                    timestamp = statsAfter.lastCallTimestamp.takeIf { it > 0 } ?: System.currentTimeMillis(),
                                    lastError = null,
                                    isAiConfigured = isAiConfigured
                                )
                            )
                        }
                        android.util.Log.d(TAG, "Buddha wisdom loaded (AI: ${result.data.isAiGenerated})")
                    }
                    is BuddhaAiResult.Fallback -> {
                        _uiState.update { state ->
                            state.copy(
                                buddhaThought = result.data.wisdom,
                                buddhaThoughtExplanation = result.data.explanation,
                                isBuddhaThoughtAiGenerated = false,
                                isBuddhaThoughtLoading = false,
                                buddhaWisdomProofInfo = state.buddhaWisdomProofInfo.copy(
                                    provider = "Fallback",
                                    cacheStatus = if (wasCacheHit) "HIT" else "MISS",
                                    timestamp = System.currentTimeMillis(),
                                    lastError = if (!isAiConfigured) "API key not configured" else statsAfter.lastError,
                                    isAiConfigured = isAiConfigured
                                )
                            )
                        }
                        android.util.Log.d(TAG, "Buddha wisdom loaded (fallback, AI configured: $isAiConfigured)")
                    }
                    is BuddhaAiResult.Error -> {
                        // Fall back to local wisdom
                        _uiState.update { state ->
                            state.copy(
                                buddhaThought = BuddhaWisdom.getDailyReflectionPrompt(),
                                buddhaThoughtExplanation = "From the archives",
                                isBuddhaThoughtAiGenerated = false,
                                isBuddhaThoughtLoading = false,
                                buddhaWisdomProofInfo = state.buddhaWisdomProofInfo.copy(
                                    provider = "Error",
                                    cacheStatus = "MISS",
                                    timestamp = System.currentTimeMillis(),
                                    lastError = result.message,
                                    isAiConfigured = isAiConfigured
                                )
                            )
                        }
                        android.util.Log.e(TAG, "Buddha wisdom error: ${result.message}")
                    }
                }

                lastBuddhaRefreshTime = System.currentTimeMillis()

            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading Buddha wisdom", e)
                _uiState.update { state ->
                    state.copy(
                        buddhaThought = BuddhaWisdom.getDailyReflectionPrompt(),
                        buddhaThoughtExplanation = "From the archives",
                        isBuddhaThoughtAiGenerated = false,
                        isBuddhaThoughtLoading = false,
                        buddhaWisdomProofInfo = state.buddhaWisdomProofInfo.copy(
                            provider = "Error",
                            cacheStatus = "MISS",
                            timestamp = System.currentTimeMillis(),
                            lastError = e.message,
                            isAiConfigured = buddhaAiRepository.isAiConfigured()
                        )
                    )
                }
            }
        }
    }

    /**
     * Refresh Buddha's thought with a new AI-generated one.
     * Has a cooldown to prevent spam.
     */
    fun refreshBuddhaThought() {
        val timeSinceLastRefresh = System.currentTimeMillis() - lastBuddhaRefreshTime

        if (timeSinceLastRefresh < REFRESH_COOLDOWN_MS) {
            // Still in cooldown
            _uiState.update { it.copy(canRefreshBuddhaThought = false) }

            viewModelScope.launch {
                kotlinx.coroutines.delay(REFRESH_COOLDOWN_MS - timeSinceLastRefresh)
                _uiState.update { it.copy(canRefreshBuddhaThought = true) }
            }
            return
        }

        loadBuddhaWisdom(forceRefresh = true)
    }

    fun retry() {
        _uiState.update { it.copy(isLoading = true) }
        loadHomeData()
        loadBuddhaWisdom()
    }
}
