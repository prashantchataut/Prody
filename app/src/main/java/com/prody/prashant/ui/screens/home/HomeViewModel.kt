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
import com.prody.prashant.domain.progress.ActiveProgressService
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.domain.progress.SeedBloomService
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.util.BuddhaWisdom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

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
    val buddhaThought: String = BuddhaWisdom.getRandomEncouragement(),
    val buddhaThoughtExplanation: String = "",
    val isBuddhaThoughtAiGenerated: Boolean = false,
    val isBuddhaThoughtLoading: Boolean = false,
    val canRefreshBuddhaThought: Boolean = true,
    val journalEntriesThisWeek: Int = 0,
    val wordsLearnedThisWeek: Int = 0,
    val daysActiveThisWeek: Int = 0,
    val isLoading: Boolean = true,
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
    val progressFeedbackMessage: String = ""
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
    private val seedBloomService: SeedBloomService
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

    private fun checkOnboarding() {
        viewModelScope.launch {
            // Check if we should show the Buddha Guide intro
            aiOnboardingManager.shouldShowBuddhaGuide().collect { shouldShow ->
                if (shouldShow) {
                    _uiState.update { state ->
                        state.copy(
                            showBuddhaGuide = true,
                            buddhaGuideCards = aiOnboardingManager.getBuddhaGuideCards()
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            // Check if we should show daily wisdom hint
            aiOnboardingManager.shouldShowHint(AiHintType.DAILY_WISDOM_TIP).collect { shouldShow ->
                _uiState.update { state ->
                    state.copy(showDailyWisdomHint = shouldShow)
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
            try {
                // Load user profile
                userDao.getUserProfile().collect { profile ->
                    profile?.let {
                        _uiState.update { state ->
                            state.copy(
                                userName = it.displayName,
                                currentStreak = it.currentStreak,
                                totalPoints = it.totalPoints
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading user profile", e)
            }
        }

        viewModelScope.launch {
            try {
                // Load quote of the day
                val quote = quoteDao.getQuoteOfTheDay()
                quote?.let {
                    quoteDao.markAsShownDaily(it.id)
                    _uiState.update { state ->
                        state.copy(
                            dailyQuote = it.content,
                            dailyQuoteAuthor = it.author
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading quote of the day", e)
            }
        }

        viewModelScope.launch {
            try {
                // Load word of the day
                val word = vocabularyDao.getWordOfTheDay()
                word?.let {
                    currentWordId = it.id
                    vocabularyDao.markAsShownDaily(it.id)
                    _uiState.update { state ->
                        state.copy(
                            wordOfTheDay = it.word,
                            wordDefinition = it.definition,
                            wordPronunciation = it.pronunciation,
                            wordId = it.id
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading word of the day", e)
            }
        }

        viewModelScope.launch {
            try {
                // Load proverb of the day
                val proverb = proverbDao.getProverbOfTheDay()
                proverb?.let {
                    proverbDao.markAsShownDaily(it.id)
                    _uiState.update { state ->
                        state.copy(
                            dailyProverb = it.content,
                            proverbMeaning = it.meaning,
                            proverbOrigin = it.origin
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading proverb of the day", e)
            }
        }

        viewModelScope.launch {
            try {
                // Load idiom of the day
                val idiom = idiomDao.getIdiomOfTheDay()
                idiom?.let {
                    idiomDao.markAsShownDaily(it.id)
                    _uiState.update { state ->
                        state.copy(
                            dailyIdiom = it.phrase,
                            idiomMeaning = it.meaning,
                            idiomExample = it.exampleSentence
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading idiom of the day", e)
            }
        }

        viewModelScope.launch {
            try {
                // Load weekly stats
                val weekStart = getWeekStartTimestamp()

                journalDao.getEntriesByDateRange(weekStart, System.currentTimeMillis())
                    .collect { entries ->
                        _uiState.update { state ->
                            state.copy(journalEntriesThisWeek = entries.size)
                        }
                    }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading journal entries this week", e)
            }
        }

        viewModelScope.launch {
            try {
                val weekStart = getWeekStartTimestamp()
                vocabularyDao.getLearnedCountSince(weekStart).collect { count ->
                    _uiState.update { state ->
                        state.copy(wordsLearnedThisWeek = count)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading learned words count this week", e)
            }
        }

        viewModelScope.launch {
            try {
                // Set loading to false after initial data load
                _uiState.update { state ->
                    state.copy(isLoading = false)
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error completing home data load", e)
                _uiState.update { state ->
                    state.copy(isLoading = false)
                }
            }
        }

        // Calculate days active this week
        viewModelScope.launch {
            try {
                userDao.getStreakHistory().collect { history ->
                    val weekStart = getWeekStartTimestamp()
                    val daysActive = history.count { it.date >= weekStart }
                    _uiState.update { state ->
                        state.copy(daysActiveThisWeek = daysActive)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading streak history", e)
            }
        }

        // Check if user journaled today (reactive home hero)
        viewModelScope.launch {
            try {
                val todayStart = getTodayStartTimestamp()
                journalDao.getEntriesByDateRange(todayStart, System.currentTimeMillis())
                    .collect { todayEntries ->
                        if (todayEntries.isNotEmpty()) {
                            val latestEntry = todayEntries.maxByOrNull { it.createdAt }
                            _uiState.update { state ->
                                state.copy(
                                    journaledToday = true,
                                    todayEntryMood = latestEntry?.mood ?: "",
                                    todayEntryPreview = latestEntry?.content?.take(100) ?: ""
                                )
                            }
                        } else {
                            _uiState.update { state ->
                                state.copy(
                                    journaledToday = false,
                                    todayEntryMood = "",
                                    todayEntryPreview = ""
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error checking today's journal entries", e)
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

                val result = buddhaAiRepository.getDailyWisdom(forceRefresh)

                when (result) {
                    is BuddhaAiResult.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                buddhaThought = result.data.wisdom,
                                buddhaThoughtExplanation = result.data.explanation,
                                isBuddhaThoughtAiGenerated = result.data.isAiGenerated,
                                isBuddhaThoughtLoading = false
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
                                isBuddhaThoughtLoading = false
                            )
                        }
                        android.util.Log.d(TAG, "Buddha wisdom loaded (fallback)")
                    }
                    is BuddhaAiResult.Error -> {
                        // Fall back to local wisdom
                        _uiState.update { state ->
                            state.copy(
                                buddhaThought = BuddhaWisdom.getDailyReflectionPrompt(),
                                buddhaThoughtExplanation = "From the archives",
                                isBuddhaThoughtAiGenerated = false,
                                isBuddhaThoughtLoading = false
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
                        isBuddhaThoughtLoading = false
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

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun retry() {
        _uiState.update { it.copy(isLoading = true, error = null, hasLoadError = false) }
        loadHomeData()
        loadBuddhaWisdom()
    }
}
