package com.prody.prashant.ui.screens.futuremessage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.FutureMessageDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.FutureMessageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class WriteMessageUiState(
    val title: String = "",
    val content: String = "",
    val deliveryDate: Long = getDefaultDeliveryDate(),
    val selectedPreset: DatePreset = DatePreset.ONE_MONTH,
    val selectedCategory: MessageCategory = MessageCategory.GENERAL,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
) {
    val canSave: Boolean
        get() = title.isNotBlank() && content.isNotBlank()
}

private fun getDefaultDeliveryDate(): Long {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, 1)
    return calendar.timeInMillis
}

@HiltViewModel
class WriteMessageViewModel @Inject constructor(
    private val futureMessageDao: FutureMessageDao,
    private val userDao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteMessageUiState())
    val uiState: StateFlow<WriteMessageUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    fun updateCategory(category: MessageCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectDatePreset(preset: DatePreset) {
        val calendar = Calendar.getInstance()
        when (preset) {
            DatePreset.ONE_WEEK -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            DatePreset.ONE_MONTH -> calendar.add(Calendar.MONTH, 1)
            DatePreset.SIX_MONTHS -> calendar.add(Calendar.MONTH, 6)
            DatePreset.ONE_YEAR -> calendar.add(Calendar.YEAR, 1)
            DatePreset.CUSTOM -> return // Handled by date picker
        }

        _uiState.update {
            it.copy(
                selectedPreset = preset,
                deliveryDate = calendar.timeInMillis
            )
        }
    }

    fun selectCustomDate(date: Long) {
        _uiState.update {
            it.copy(
                selectedPreset = DatePreset.CUSTOM,
                deliveryDate = date
            )
        }
    }

    fun saveMessage() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            try {
                val state = _uiState.value

                val message = FutureMessageEntity(
                    title = state.title,
                    content = state.content,
                    deliveryDate = state.deliveryDate,
                    category = state.selectedCategory.name.lowercase(),
                    createdAt = System.currentTimeMillis()
                )

                futureMessageDao.insertMessage(message)

                // Update user stats
                userDao.incrementFutureMessages()
                userDao.addPoints(75) // Points for sending future message

                // Update achievements
                updateFutureMessageAchievements()

                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, error = e.message)
                }
            }
        }
    }

    private suspend fun updateFutureMessageAchievements() {
        val profile = userDao.getUserProfileSync() ?: return
        val messagesCount = profile.futureMessagesCount

        // Update progress
        userDao.updateAchievementProgress("future_1", messagesCount)
        userDao.updateAchievementProgress("future_10", messagesCount)

        // Check unlocks
        if (messagesCount >= 1) checkAndUnlockAchievement("future_1")
        if (messagesCount >= 10) checkAndUnlockAchievement("future_10")
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
