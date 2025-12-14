package com.prody.prashant.ui.screens.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.ai.GeminiResult
import com.prody.prashant.data.ai.GeminiService
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.ui.theme.JournalTemplate
import com.prody.prashant.util.BuddhaWisdom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class NewJournalEntryUiState(
    val content: String = "",
    val selectedMood: Mood = Mood.CALM,
    val moodIntensity: Int = 5,
    val wordCount: Int = 0,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isGeneratingAiResponse: Boolean = false,
    val error: String? = null,
    val selectedTemplate: JournalTemplate? = null,
    val showTemplateSelector: Boolean = false,
    val availableTemplates: List<JournalTemplate> = JournalTemplate.all(),
    val buddhaAiEnabled: Boolean = true,
    val geminiConfigured: Boolean = false
)

@HiltViewModel
class NewJournalEntryViewModel @Inject constructor(
    private val journalDao: JournalDao,
    private val userDao: UserDao,
    private val geminiService: GeminiService,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewJournalEntryUiState())
    val uiState: StateFlow<NewJournalEntryUiState> = _uiState.asStateFlow()

    init {
        loadAiSettings()
    }

    private fun loadAiSettings() {
        viewModelScope.launch {
            combine(
                preferencesManager.buddhaAiEnabled,
                preferencesManager.geminiApiKey
            ) { enabled, apiKey ->
                Pair(enabled, apiKey.isNotBlank())
            }.collect { (enabled, configured) ->
                _uiState.update {
                    it.copy(
                        buddhaAiEnabled = enabled,
                        geminiConfigured = configured
                    )
                }
            }
        }
    }

    fun updateContent(content: String) {
        val wordCount = if (content.isBlank()) 0 else content.trim().split("\\s+".toRegex()).size
        _uiState.update {
            it.copy(content = content, wordCount = wordCount)
        }
    }

    fun updateMood(mood: Mood) {
        _uiState.update { it.copy(selectedMood = mood) }
    }

    fun updateMoodIntensity(intensity: Int) {
        _uiState.update { it.copy(moodIntensity = intensity.coerceIn(1, 10)) }
    }

    fun toggleTemplateSelector() {
        _uiState.update { it.copy(showTemplateSelector = !it.showTemplateSelector) }
    }

    fun selectTemplate(template: JournalTemplate) {
        val templateContent = template.prompts.joinToString("\n")
        _uiState.update {
            it.copy(
                selectedTemplate = template,
                content = templateContent,
                wordCount = templateContent.trim().split("\\s+".toRegex()).size,
                showTemplateSelector = false
            )
        }
    }

    fun clearTemplate() {
        _uiState.update {
            it.copy(
                selectedTemplate = null,
                content = "",
                wordCount = 0
            )
        }
    }

    fun saveEntry() {
        viewModelScope.launch {
            val state = _uiState.value

            // Don't save empty entries
            if (state.content.isBlank()) {
                _uiState.update { it.copy(error = "Please write something before saving") }
                return@launch
            }

            _uiState.update { it.copy(isSaving = true, isGeneratingAiResponse = true, error = null) }

            try {
                // Generate Buddha's response - use Gemini if available, fallback to local
                val buddhaResponse = generateBuddhaResponse(state)

                _uiState.update { it.copy(isGeneratingAiResponse = false) }

                val entry = JournalEntryEntity(
                    content = state.content,
                    mood = state.selectedMood.name,
                    moodIntensity = state.moodIntensity,
                    buddhaResponse = buddhaResponse,
                    wordCount = state.wordCount,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                journalDao.insertEntry(entry)

                // Update user stats
                userDao.incrementJournalEntries()
                userDao.addPoints(50) // Points for journaling

                // Update achievements
                updateJournalAchievements()

                // Check for special achievements
                checkSpecialAchievements()

                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isGeneratingAiResponse = false,
                        error = "Failed to save: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun generateBuddhaResponse(state: NewJournalEntryUiState): String {
        // Only use Gemini AI if enabled and configured
        if (state.buddhaAiEnabled && state.geminiConfigured && geminiService.isConfigured()) {
            val result = geminiService.generateJournalResponse(
                content = state.content,
                mood = state.selectedMood,
                moodIntensity = state.moodIntensity,
                wordCount = state.wordCount
            )

            return when (result) {
                is GeminiResult.Success -> result.data
                is GeminiResult.Error -> {
                    // Log the error but fall back to local generation
                    BuddhaWisdom.generateResponse(
                        content = state.content,
                        mood = state.selectedMood,
                        wordCount = state.wordCount
                    )
                }
                is GeminiResult.ApiKeyNotSet,
                is GeminiResult.Loading -> {
                    BuddhaWisdom.generateResponse(
                        content = state.content,
                        mood = state.selectedMood,
                        wordCount = state.wordCount
                    )
                }
            }
        }

        // Use local Buddha wisdom
        return BuddhaWisdom.generateResponse(
            content = state.content,
            mood = state.selectedMood,
            wordCount = state.wordCount
        )
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private suspend fun updateJournalAchievements() {
        val profile = userDao.getUserProfileSync() ?: return
        val entriesCount = profile.journalEntriesCount

        // Update progress
        userDao.updateAchievementProgress("journal_1", entriesCount)
        userDao.updateAchievementProgress("journal_10", entriesCount)
        userDao.updateAchievementProgress("journal_50", entriesCount)
        userDao.updateAchievementProgress("journal_100", entriesCount)

        // Check unlocks
        if (entriesCount >= 1) checkAndUnlockAchievement("journal_1")
        if (entriesCount >= 10) checkAndUnlockAchievement("journal_10")
        if (entriesCount >= 50) checkAndUnlockAchievement("journal_50")
        if (entriesCount >= 100) checkAndUnlockAchievement("journal_100")
    }

    private suspend fun checkSpecialAchievements() {
        try {
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

            // Night owl achievement (after midnight, before 5am)
            if (currentHour in 0..4) {
                checkAndUnlockAchievement("night_owl")
            }

            // Early bird achievement (5am to 6am - users who wake early)
            if (currentHour == 5) {
                checkAndUnlockAchievement("early_bird")
            }

            // Check for all moods achievement
            // Use firstOrNull to safely handle potential empty Flow scenarios
            val allMoods = journalDao.getAllMoods().firstOrNull() ?: emptyList()
            if (allMoods.size >= Mood.entries.size) {
                userDao.updateAchievementProgress("all_moods", allMoods.size)
                checkAndUnlockAchievement("all_moods")
            }
        } catch (e: Exception) {
            android.util.Log.e("NewJournalEntryVM", "Failed to check special achievements", e)
        }
    }

    private suspend fun checkAndUnlockAchievement(achievementId: String) {
        val achievement = userDao.getAchievementById(achievementId)
        if (achievement != null && !achievement.isUnlocked) {
            userDao.unlockAchievement(achievementId)
            val points = achievement.rewardValue.toIntOrNull() ?: 100
            userDao.addPoints(points)
        }
    }
}
