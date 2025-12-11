package com.prody.prashant.ui.screens.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.domain.model.JournalTemplate
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.util.BuddhaWisdom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class NewJournalEntryUiState(
    val content: String = "",
    val selectedMood: Mood = Mood.CALM,
    val wordCount: Int = 0,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val selectedTemplate: JournalTemplate? = null,
    val showTemplateSelector: Boolean = false,
    val availableTemplates: List<JournalTemplate> = JournalTemplate.all()
)

@HiltViewModel
class NewJournalEntryViewModel @Inject constructor(
    private val journalDao: JournalDao,
    private val userDao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewJournalEntryUiState())
    val uiState: StateFlow<NewJournalEntryUiState> = _uiState.asStateFlow()

    fun updateContent(content: String) {
        val wordCount = if (content.isBlank()) 0 else content.trim().split("\\s+".toRegex()).size
        _uiState.update {
            it.copy(content = content, wordCount = wordCount)
        }
    }

    fun updateMood(mood: Mood) {
        _uiState.update { it.copy(selectedMood = mood) }
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
            _uiState.update { it.copy(isSaving = true) }

            try {
                val state = _uiState.value

                // Generate Buddha's response
                val buddhaResponse = BuddhaWisdom.generateResponse(
                    content = state.content,
                    mood = state.selectedMood,
                    wordCount = state.wordCount
                )

                val entry = JournalEntryEntity(
                    content = state.content,
                    mood = state.selectedMood.name,
                    moodIntensity = 5,
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
                    it.copy(isSaving = false, error = e.message)
                }
            }
        }
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
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        // Night owl achievement (after midnight, before 5am)
        if (currentHour in 0..4) {
            checkAndUnlockAchievement("night_owl")
        }

        // Early bird achievement (before 6am)
        if (currentHour in 5..5) {
            checkAndUnlockAchievement("early_bird")
        }

        // Check for all moods achievement
        val allMoods = journalDao.getAllMoods().first()
        if (allMoods.size >= Mood.entries.size) {
            userDao.updateAchievementProgress("all_moods", allMoods.size)
            checkAndUnlockAchievement("all_moods")
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
