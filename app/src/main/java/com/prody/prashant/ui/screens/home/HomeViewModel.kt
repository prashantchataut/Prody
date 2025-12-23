package com.prody.prashant.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.ai.BuddhaAiRepository
import com.prody.prashant.data.ai.BuddhaAiResult
import com.prody.prashant.data.local.dao.*
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.data.onboarding.AiHint
import com.prody.prashant.data.onboarding.AiHintType
import com.prody.prashant.data.onboarding.AiOnboardingManager
import com.prody.prashant.data.onboarding.BuddhaGuideCard
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
    val showDailyWisdomHint: Boolean = false
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
    private val aiOnboardingManager: AiOnboardingManager
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
            _uiState.update { it.copy(isLoading = true) }

            // Fetch daily data that doesn't need to be observed continuously
            val quote = try { quoteDao.getQuoteOfTheDay() } catch (e: Exception) { null }
            val word = try { vocabularyDao.getWordOfTheDay() } catch (e: Exception) { null }
            val proverb = try { proverbDao.getProverbOfTheDay() } catch (e: Exception) { null }
            val idiom = try { idiomDao.getIdiomOfTheDay() } catch (e: Exception) { null }

            // Mark them as shown
            quote?.let { quoteDao.markAsShownDaily(it.id) }
            word?.let {
                currentWordId = it.id
                vocabularyDao.markAsShownDaily(it.id)
            }
            proverb?.let { proverbDao.markAsShownDaily(it.id) }
            idiom?.let { idiomDao.markAsShownDaily(it.id) }

            val weekStart = getWeekStartTimestamp()

            combine(
                userDao.getUserProfile(),
                journalDao.getEntriesByDateRange(weekStart, System.currentTimeMillis()),
                vocabularyDao.getLearnedCountSince(weekStart),
                userDao.getStreakHistory()
            ) { profile, journalEntries, learnedWordsCount, streakHistory ->
                // This lambda creates a new state object with all the data.
                val daysActive = streakHistory.count { it.date >= weekStart }
                _uiState.value.copy(
                    userName = profile?.displayName ?: _uiState.value.userName,
                    currentStreak = profile?.currentStreak ?: _uiState.value.currentStreak,
                    totalPoints = profile?.totalPoints ?: _uiState.value.totalPoints,
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
                    journalEntriesThisWeek = journalEntries.size,
                    wordsLearnedThisWeek = learnedWordsCount,
                    daysActiveThisWeek = daysActive,
                    isLoading = false
                )
            }.catch { e ->
                android.util.Log.e(TAG, "Error in combined home data flow", e)
                // In case of error, just stop loading and keep the existing data
                _uiState.update { it.copy(isLoading = false) }
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
}
