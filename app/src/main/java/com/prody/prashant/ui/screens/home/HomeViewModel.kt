package com.prody.prashant.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.ai.BuddhaAiRepository
import com.prody.prashant.data.ai.BuddhaAiResult
import com.prody.prashant.data.local.dao.*
import com.prody.prashant.data.local.entity.IdiomEntity
import com.prody.prashant.data.local.entity.ProverbEntity
import com.prody.prashant.data.local.entity.QuoteEntity
import com.prody.prashant.data.local.entity.SeedEntity
import com.prody.prashant.data.local.entity.VocabularyEntity
import com.prody.prashant.data.local.entity.UserProfileEntity
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.data.local.entity.StreakHistoryEntity
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.data.onboarding.AiHint
import com.prody.prashant.data.onboarding.AiHintType
import com.prody.prashant.data.onboarding.AiOnboardingManager
import com.prody.prashant.data.onboarding.BuddhaGuideCard
import com.prody.prashant.domain.intelligence.*
import com.prody.prashant.domain.progress.ActiveProgressService
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.SeedBloomService
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.domain.repository.SoulLayerRepository
import com.prody.prashant.domain.streak.DualStreakManager
import com.prody.prashant.domain.streak.DualStreakStatus
import com.prody.prashant.util.BuddhaWisdom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * AI Proof Mode debug info - shown on AI surfaces when enabled in Settings.
 */
data class AiProofModeInfo(
    val provider: String = "",
    val cacheStatus: String = "",
    val timestamp: Long = 0L,
    val lastError: String? = null,
    val isEnabled: Boolean = false,
    val isAiConfigured: Boolean = true
)

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
    val aiConfigurationStatus: AiConfigurationStatus = AiConfigurationStatus.CONFIGURED,
    val showBuddhaGuide: Boolean = false,
    val buddhaGuideCards: List<BuddhaGuideCard> = emptyList(),
    val showDailyWisdomHint: Boolean = false,
    val journaledToday: Boolean = false,
    val todayEntryMood: String = "",
    val todayEntryPreview: String = "",
    val nextAction: NextAction? = null,
    val todayProgress: TodayProgress = TodayProgress(),
    val dailySeed: SeedEntity? = null,
    val showProgressFeedback: Boolean = false,
    val progressFeedbackTitle: String = "",
    val progressFeedbackMessage: String = "",
    val buddhaWisdomProofInfo: AiProofModeInfo = AiProofModeInfo(),
    val dualStreakStatus: DualStreakStatus = DualStreakStatus.empty(),
    val intelligentGreeting: String = "",
    val greetingSubtext: String = "",
    val isInFirstWeek: Boolean = false,
    val firstWeekDayNumber: Int = 0,
    val firstWeekProgress: FirstWeekProgress? = null,
    val firstWeekDayContent: FirstWeekDayContent? = null,
    val showFirstWeekCelebration: Boolean = false,
    val firstWeekCelebration: CelebrationContent? = null,
    val surfacedMemory: SurfacedMemory? = null,
    val showMemoryCard: Boolean = false,
    val anniversaryMemories: List<AnniversaryMemory> = emptyList(),
    val userArchetype: UserArchetype = UserArchetype.EXPLORER,
    val trustLevel: TrustLevel = TrustLevel.NEW,
    val isUserStruggling: Boolean = false,
    val isUserThriving: Boolean = false,
    val personalizedPatternText: String = "",
    val personalizedPatternSuggestion: String = "",
    val intelligenceInsights: List<IntelligenceInsight> = emptyList(),
    val isPremiumIntelligenceEnabled: Boolean = false,
    val weeklyMoodTrend: List<Float> = emptyList()
)

private data class DailyContent(
    val quote: QuoteEntity?,
    val word: VocabularyEntity?,
    val proverb: ProverbEntity?,
    val idiom: IdiomEntity?
)

private data class HomeDataArgs(
    val profile: UserProfileEntity?,
    val weeklyJournalEntries: List<JournalEntryEntity>,
    val weeklyLearnedWords: Int,
    val streakHistory: List<StreakHistoryEntity>,
    val todayJournalEntries: List<JournalEntryEntity>,
    val dualStreak: DualStreakStatus,
    val aiProofMode: Boolean,
    val userContext: UserContext,
    val firstWeekProgress: FirstWeekProgress?,
    val todaySeed: SeedEntity?,
    val premiumIntelligenceEnabled: Boolean
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
    private val soulLayerRepository: SoulLayerRepository,
    private val patternAnalysisEngine: PatternAnalysisEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentWordId: Long = 0
    private var lastBuddhaRefreshTime: Long = 0

    companion object {
        private const val TAG = "HomeViewModel"
    }

    init {
        loadInitialData()
        loadPersonalizedPattern()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val weekStart = getWeekStartTimestamp()
            val todayStart = getTodayStartTimestamp()

            val dailyContent = fetchDailyContentConcurrently()

            viewModelScope.launch {
                dailyContent.quote?.let { quoteDao.markAsShownDaily(it.id) }
                dailyContent.word?.let {
                    currentWordId = it.id
                    vocabularyDao.markAsShownDaily(it.id)
                }
                dailyContent.proverb?.let { proverbDao.markAsShownDaily(it.id) }
                dailyContent.idiom?.let { idiomDao.markAsShownDaily(it.id) }
            }

            combine(
                userDao.getUserProfile().distinctUntilChanged(),
                journalDao.getEntriesByDateRange(weekStart, System.currentTimeMillis()).distinctUntilChanged(),
                vocabularyDao.getLearnedCountSince(weekStart).distinctUntilChanged(),
                userDao.getStreakHistory().distinctUntilChanged(),
                journalDao.getEntriesByDateRange(todayStart, System.currentTimeMillis()).distinctUntilChanged(),
                dualStreakManager.getDualStreakStatusFlow().distinctUntilChanged(),
                preferencesManager.debugAiProofMode.distinctUntilChanged(),
                soulLayerRepository.observeContext().distinctUntilChanged(),
                soulLayerRepository.observeFirstWeekProgress().distinctUntilChanged(),
                seedBloomService.observeTodaySeed().distinctUntilChanged(),
                preferencesManager.premiumIntelligenceEnabled.distinctUntilChanged()
            ) { args ->
                HomeDataArgs(
                    profile = args[0] as? UserProfileEntity,
                    weeklyJournalEntries = (args[1] as? List<*>)?.filterIsInstance<JournalEntryEntity>() ?: emptyList(),
                    weeklyLearnedWords = args[2] as? Int ?: 0,
                    streakHistory = (args[3] as? List<*>)?.filterIsInstance<StreakHistoryEntity>() ?: emptyList(),
                    todayJournalEntries = (args[4] as? List<*>)?.filterIsInstance<JournalEntryEntity>() ?: emptyList(),
                    dualStreak = args[5] as? DualStreakStatus ?: DualStreakStatus.empty(),
                    aiProofMode = args[6] as? Boolean ?: false,
                    userContext = args[7] as? UserContext ?: UserContext.empty(),
                    firstWeekProgress = args[8] as? FirstWeekProgress,
                    todaySeed = args[9] as? SeedEntity,
                    premiumIntelligenceEnabled = args[10] as? Boolean ?: false
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
            }.collect { args ->
                val daysActiveThisWeek = args.streakHistory.count { it.date >= weekStart }

                val (journaledToday, todayMood, todayPreview) = if (args.todayJournalEntries.isNotEmpty()) {
                    val latestEntry = args.todayJournalEntries.maxByOrNull { it.createdAt }
                    Triple(true, latestEntry?.mood ?: "", latestEntry?.content?.take(100) ?: "")
                } else {
                    Triple(false, "", "")
                }

                _uiState.update { state ->
                    state.copy(
                        userName = args.profile?.displayName ?: "Growth Seeker",
                        currentStreak = args.profile?.currentStreak ?: 0,
                        totalPoints = args.profile?.totalPoints ?: 0,
                        dailyQuote = dailyContent.quote?.content ?: state.dailyQuote,
                        dailyQuoteAuthor = dailyContent.quote?.author ?: state.dailyQuoteAuthor,
                        wordOfTheDay = dailyContent.word?.word ?: state.wordOfTheDay,
                        wordDefinition = dailyContent.word?.definition ?: state.wordDefinition,
                        wordPronunciation = dailyContent.word?.pronunciation ?: state.wordPronunciation,
                        wordId = dailyContent.word?.id ?: state.wordId,
                        dailyProverb = dailyContent.proverb?.content ?: state.dailyProverb,
                        proverbMeaning = dailyContent.proverb?.meaning ?: state.proverbMeaning,
                        proverbOrigin = dailyContent.proverb?.origin ?: state.proverbOrigin,
                        dailyIdiom = dailyContent.idiom?.phrase ?: state.dailyIdiom,
                        idiomMeaning = dailyContent.idiom?.meaning ?: state.idiomMeaning,
                        idiomExample = dailyContent.idiom?.exampleSentence ?: state.idiomExample,
                        idiomId = dailyContent.idiom?.id ?: state.idiomId,
                        journalEntriesThisWeek = args.weeklyJournalEntries.size,
                        wordsLearnedThisWeek = args.weeklyLearnedWords,
                        daysActiveThisWeek = daysActiveThisWeek,
                        journaledToday = journaledToday,
                        todayEntryMood = todayMood,
                        todayEntryPreview = todayPreview,
                        dualStreakStatus = args.dualStreak,
                        buddhaWisdomProofInfo = state.buddhaWisdomProofInfo.copy(isEnabled = args.aiProofMode),
                        userArchetype = args.userContext.userArchetype,
                        trustLevel = args.userContext.trustLevel,
                        isUserStruggling = args.userContext.isStruggling,
                        isUserThriving = args.userContext.isThriving,
                        isInFirstWeek = args.userContext.isInFirstWeek,
                        firstWeekProgress = args.firstWeekProgress,
                        dailySeed = args.todaySeed,
                        isPremiumIntelligenceEnabled = args.premiumIntelligenceEnabled,
                        weeklyMoodTrend = calculateMoodTrend(args.weeklyJournalEntries),
                        isLoading = false
                    )
                }
            }

            launch { loadBuddhaWisdom() }
            launch { checkOnboarding() }
            launch { loadActiveProgress() }
            launch { checkAiConfiguration() }
            launch { loadIntelligentGreeting() }
            launch { loadSurfacedMemory() }
            launch { loadAnniversaryMemories() }

            // Fixed: Reactive intelligence insights
            preferencesManager.premiumIntelligenceEnabled.collect { enabled ->
                if (enabled) loadIntelligenceInsights()
            }
        }
    }

    private fun calculateMoodTrend(entries: List<JournalEntryEntity>): List<Float> {
        return entries.sortedBy { it.createdAt }.mapNotNull { entry ->
            val mood = try { com.prody.prashant.domain.model.Mood.valueOf(entry.mood.uppercase()) } catch (_: Exception) { null }
            mood?.ordinal?.plus(1)?.toFloat()
        }
    }

    private suspend fun fetchDailyContentConcurrently(): DailyContent = coroutineScope {
        val quoteDeferred = async { try { quoteDao.getQuoteOfTheDay() } catch (e: Exception) { null } }
        val wordDeferred = async { try { vocabularyDao.getWordOfTheDay() } catch (e: Exception) { null } }
        val proverbDeferred = async { try { proverbDao.getProverbOfTheDay() } catch (e: Exception) { null } }
        val idiomDeferred = async { try { idiomDao.getIdiomOfTheDay() } catch (e: Exception) { null } }

        DailyContent(quoteDeferred.await(), wordDeferred.await(), proverbDeferred.await(), idiomDeferred.await())
    }

    private fun checkAiConfiguration() {
        val status = if (buddhaAiRepository.isAiConfigured()) AiConfigurationStatus.CONFIGURED else AiConfigurationStatus.MISSING
        _uiState.update { it.copy(aiConfigurationStatus = status) }
    }

    fun getAiConfigurationStatus() = _uiState.value.aiConfigurationStatus

    private fun loadActiveProgress() {
        viewModelScope.launch {
            try {
                val nextAction = activeProgressService.getNextAction()
                val todayProgress = activeProgressService.getTodayProgress()
                _uiState.update { it.copy(nextAction = nextAction, todayProgress = todayProgress) }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading active progress", e)
            }
        }
    }

    fun refreshNextAction() = loadActiveProgress()

    fun showProgressFeedback(title: String, message: String) {
        _uiState.update { it.copy(showProgressFeedback = true, progressFeedbackTitle = title, progressFeedbackMessage = message) }
        viewModelScope.launch { kotlinx.coroutines.delay(3000); dismissProgressFeedback() }
    }

    fun dismissProgressFeedback() = _uiState.update { it.copy(showProgressFeedback = false) }

    fun onWisdomContentViewed() = viewModelScope.launch { dualStreakManager.maintainWisdomStreak() }

    fun onReflectionCompleted() = viewModelScope.launch { dualStreakManager.maintainReflectionStreak() }

    private suspend fun loadIntelligentGreeting() {
        try {
            val greeting = soulLayerRepository.getGreeting()
            _uiState.update { it.copy(intelligentGreeting = greeting.greeting, greetingSubtext = greeting.subtitle) }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error loading greeting", e)
        }
    }

    private suspend fun loadSurfacedMemory() {
        try {
            val memory = soulLayerRepository.getMemoryToSurface(MemorySurfaceContext.APP_OPEN)
            _uiState.update { it.copy(surfacedMemory = memory, showMemoryCard = memory != null) }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error loading surfaced memory", e)
        }
    }

    private suspend fun loadAnniversaryMemories() {
        try {
            val anniversaries = soulLayerRepository.getAnniversaryMemories()
            _uiState.update { it.copy(anniversaryMemories = anniversaries) }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error loading anniversary memories", e)
        }
    }

    private suspend fun loadIntelligenceInsights() {
        try {
            val insights = patternAnalysisEngine.analyzePatterns(lookbackDays = 7)?.patterns?.map {
                IntelligenceInsight(title = it.theme, description = it.sampleSnippet, actionable = it.suggestion)
            } ?: emptyList()
            _uiState.update { it.copy(intelligenceInsights = insights) }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error loading intelligence insights", e)
        }
    }

    fun onMemoryInteracted(interactionType: String) {}

    fun dismissMemoryCard() = _uiState.update { it.copy(showMemoryCard = false) }

    fun expandMemoryCard() {}

    fun onFirstWeekMilestoneCompleted(milestone: FirstWeekMilestone) {
        viewModelScope.launch {
            try {
                soulLayerRepository.markFirstWeekMilestone(milestone)
                val celebration = soulLayerRepository.getCelebrationContent(milestone)
                _uiState.update { it.copy(showFirstWeekCelebration = true, firstWeekCelebration = celebration) }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error handling first week milestone", e)
            }
        }
    }

    fun dismissFirstWeekCelebration() = _uiState.update { it.copy(showFirstWeekCelebration = false, firstWeekCelebration = null) }

    fun refreshSoulLayerContent() {
        viewModelScope.launch {
            try {
                soulLayerRepository.refreshContext()
                loadIntelligentGreeting()
                loadSurfacedMemory()
                loadAnniversaryMemories()
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error refreshing Soul Layer content", e)
            }
        }
    }

    private fun checkOnboarding() {
        viewModelScope.launch {
            combine(aiOnboardingManager.shouldShowBuddhaGuide(), aiOnboardingManager.shouldShowHint(AiHintType.DAILY_WISDOM_TIP)) { s1, s2 -> s1 to s2 }
                .collect { (b, h) ->
                    _uiState.update { it.copy(showBuddhaGuide = b, buddhaGuideCards = if (b) aiOnboardingManager.getBuddhaGuideCards() else it.buddhaGuideCards, showDailyWisdomHint = h) }
                }
        }
    }

    fun onBuddhaGuideComplete() = viewModelScope.launch { aiOnboardingManager.markBuddhaGuideShown(); _uiState.update { it.copy(showBuddhaGuide = false) } }

    fun onBuddhaGuideDontShowAgain() = viewModelScope.launch { aiOnboardingManager.dismissBuddhaGuideForever(); _uiState.update { it.copy(showBuddhaGuide = false) } }

    fun onDailyWisdomHintDismiss() = viewModelScope.launch { aiOnboardingManager.markHintShown(AiHintType.DAILY_WISDOM_TIP); _uiState.update { it.copy(showDailyWisdomHint = false) } }

    fun getDailyWisdomHint() = aiOnboardingManager.getHintContent(AiHintType.DAILY_WISDOM_TIP)

    fun markWordAsLearned() {
        viewModelScope.launch {
            if (currentWordId > 0) {
                vocabularyDao.markAsLearned(currentWordId)
                userDao.incrementWordsLearned()
                userDao.addPoints(25)
                userDao.getUserProfileSync()?.let { profile ->
                    userDao.updateAchievementProgress("words_10", profile.wordsLearned)
                    checkAndUnlockAchievement("words_10", profile.wordsLearned, 10)
                }
            }
        }
    }

    private suspend fun checkAndUnlockAchievement(id: String, p: Int, r: Int) {
        if (p >= r) {
            userDao.getAchievementById(id)?.let {
                if (!it.isUnlocked) {
                    userDao.unlockAchievement(id)
                    userDao.addPoints(it.rewardValue.toIntOrNull() ?: 100)
                }
            }
        }
    }

    private fun getWeekStartTimestamp(): Long = Calendar.getInstance().apply { set(Calendar.DAY_OF_WEEK, firstDayOfWeek); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis

    private fun getTodayStartTimestamp(): Long = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis

    private fun loadBuddhaWisdom(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isBuddhaThoughtLoading = true) }
                val isAiConfigured = buddhaAiRepository.isAiConfigured()
                val statsBefore = buddhaAiRepository.getStats()
                val result = buddhaAiRepository.getDailyWisdom(forceRefresh)
                val statsAfter = buddhaAiRepository.getStats()
                val wasCacheHit = statsAfter.cacheHits > statsBefore.cacheHits

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
                    }
                    is BuddhaAiResult.Error -> {
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
                    }
                }
                lastBuddhaRefreshTime = System.currentTimeMillis()
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading Buddha wisdom", e)
            }
        }
    }

    fun refreshBuddhaThought() = loadBuddhaWisdom(forceRefresh = true)

    fun retry() { loadInitialData(); loadPersonalizedPattern() }

    private fun loadPersonalizedPattern() {
        viewModelScope.launch {
            try {
                patternAnalysisEngine.getTopPatternForHome()?.let { topPattern ->
                    _uiState.update { it.copy(personalizedPatternText = "You've been writing about '${topPattern.theme}' ${topPattern.occurrenceCount} times ${topPattern.timespan}", personalizedPatternSuggestion = topPattern.suggestion) }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading personalized pattern", e)
            }
        }
    }
}
