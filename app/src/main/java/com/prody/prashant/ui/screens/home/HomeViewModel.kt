package com.prody.prashant.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.*
import com.prody.prashant.data.local.preferences.PreferencesManager
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
    val journalEntriesThisWeek: Int = 0,
    val wordsLearnedThisWeek: Int = 0,
    val daysActiveThisWeek: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userDao: UserDao,
    private val vocabularyDao: VocabularyDao,
    private val quoteDao: QuoteDao,
    private val proverbDao: ProverbDao,
    private val idiomDao: IdiomDao,
    private val journalDao: JournalDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentWordId: Long = 0

    companion object {
        private const val TAG = "HomeViewModel"
    }

    init {
        loadHomeData()
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
                // Get daily reflection prompt from Buddha
                _uiState.update { state ->
                    state.copy(
                        buddhaThought = BuddhaWisdom.getDailyReflectionPrompt(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading Buddha thought", e)
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
}
