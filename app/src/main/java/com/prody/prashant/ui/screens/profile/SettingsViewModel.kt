package com.prody.prashant.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.util.GeminiManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val themeMode: String = "system",
    val dynamicColors: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val wisdomNotificationsEnabled: Boolean = true,
    val journalRemindersEnabled: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val compactView: Boolean = false,
    // Gemini AI settings
    val geminiApiKey: String = "",
    val geminiModel: String = "gemini-1.5-flash",
    val geminiEnabled: Boolean = true,
    val isTestingApiKey: Boolean = false,
    val apiKeyTestResult: ApiKeyTestResult? = null
)

sealed class ApiKeyTestResult {
    data object Success : ApiKeyTestResult()
    data object Failed : ApiKeyTestResult()
    data object Testing : ApiKeyTestResult()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val geminiManager: GeminiManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val availableGeminiModels = GeminiManager.AVAILABLE_MODELS

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // Combine all settings flows
            combine(
                preferencesManager.themeMode,
                preferencesManager.dynamicColors,
                preferencesManager.notificationsEnabled,
                preferencesManager.wisdomNotificationEnabled,
                preferencesManager.journalReminderEnabled
            ) { values ->
                arrayOf(values[0], values[1], values[2], values[3], values[4])
            }.combine(
                combine(
                    preferencesManager.hapticFeedbackEnabled,
                    preferencesManager.compactCardView,
                    preferencesManager.geminiApiKey,
                    preferencesManager.geminiModel,
                    preferencesManager.geminiEnabled
                ) { values ->
                    arrayOf(values[0], values[1], values[2], values[3], values[4])
                }
            ) { first, second ->
                SettingsUiState(
                    themeMode = first[0] as String,
                    dynamicColors = first[1] as Boolean,
                    notificationsEnabled = first[2] as Boolean,
                    wisdomNotificationsEnabled = first[3] as Boolean,
                    journalRemindersEnabled = first[4] as Boolean,
                    hapticFeedbackEnabled = second[0] as Boolean,
                    compactView = second[1] as Boolean,
                    geminiApiKey = second[2] as String,
                    geminiModel = second[3] as String,
                    geminiEnabled = second[4] as Boolean
                )
            }.collect { state ->
                _uiState.update { current ->
                    state.copy(
                        isTestingApiKey = current.isTestingApiKey,
                        apiKeyTestResult = current.apiKeyTestResult
                    )
                }
            }
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
        }
    }

    fun setDynamicColors(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDynamicColors(enabled)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
        }
    }

    fun setWisdomNotifications(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setWisdomNotificationEnabled(enabled)
        }
    }

    fun setJournalReminders(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setJournalReminderEnabled(enabled)
        }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setHapticFeedbackEnabled(enabled)
        }
    }

    fun setCompactView(compact: Boolean) {
        viewModelScope.launch {
            preferencesManager.setCompactCardView(compact)
        }
    }

    // Gemini AI settings
    fun setGeminiApiKey(key: String) {
        viewModelScope.launch {
            preferencesManager.setGeminiApiKey(key)
            // Reset test result when key changes
            _uiState.update { it.copy(apiKeyTestResult = null) }
        }
    }

    fun setGeminiModel(model: String) {
        viewModelScope.launch {
            preferencesManager.setGeminiModel(model)
        }
    }

    fun setGeminiEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setGeminiEnabled(enabled)
        }
    }

    fun testGeminiApiKey() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isTestingApiKey = true, apiKeyTestResult = ApiKeyTestResult.Testing)
            }

            val apiKey = _uiState.value.geminiApiKey
            val model = _uiState.value.geminiModel

            val success = geminiManager.testApiConnection(apiKey, model)

            _uiState.update {
                it.copy(
                    isTestingApiKey = false,
                    apiKeyTestResult = if (success) ApiKeyTestResult.Success else ApiKeyTestResult.Failed
                )
            }
        }
    }

    fun clearApiKeyTestResult() {
        _uiState.update { it.copy(apiKeyTestResult = null) }
    }
}
