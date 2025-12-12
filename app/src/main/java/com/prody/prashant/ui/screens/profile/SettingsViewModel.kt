package com.prody.prashant.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.ai.GeminiModel
import com.prody.prashant.data.ai.GeminiResult
import com.prody.prashant.data.ai.GeminiService
import com.prody.prashant.data.local.preferences.PreferencesManager
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
    // Gemini AI Settings
    val geminiApiKey: String = "",
    val geminiModel: String = GeminiModel.GEMINI_1_5_FLASH.modelId,
    val buddhaAiEnabled: Boolean = true,
    val isTestingConnection: Boolean = false,
    val connectionTestResult: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val geminiService: GeminiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                preferencesManager.themeMode,
                preferencesManager.dynamicColors,
                preferencesManager.notificationsEnabled,
                preferencesManager.wisdomNotificationEnabled,
                preferencesManager.journalReminderEnabled,
                preferencesManager.hapticFeedbackEnabled,
                preferencesManager.compactCardView,
                preferencesManager.geminiApiKey,
                preferencesManager.geminiModel,
                preferencesManager.buddhaAiEnabled
            ) { values ->
                SettingsUiState(
                    themeMode = values[0] as String,
                    dynamicColors = values[1] as Boolean,
                    notificationsEnabled = values[2] as Boolean,
                    wisdomNotificationsEnabled = values[3] as Boolean,
                    journalRemindersEnabled = values[4] as Boolean,
                    hapticFeedbackEnabled = values[5] as Boolean,
                    compactView = values[6] as Boolean,
                    geminiApiKey = values[7] as String,
                    geminiModel = values[8] as String,
                    buddhaAiEnabled = values[9] as Boolean
                )
            }.collect { state ->
                _uiState.value = state
                // Initialize Gemini with stored settings
                if (state.geminiApiKey.isNotBlank()) {
                    val model = GeminiModel.entries.find { it.modelId == state.geminiModel }
                        ?: GeminiModel.GEMINI_1_5_FLASH
                    geminiService.initialize(state.geminiApiKey, model)
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

    // Gemini AI Settings
    fun setGeminiApiKey(apiKey: String) {
        viewModelScope.launch {
            preferencesManager.setGeminiApiKey(apiKey)
            if (apiKey.isNotBlank()) {
                val model = GeminiModel.entries.find { it.modelId == _uiState.value.geminiModel }
                    ?: GeminiModel.GEMINI_1_5_FLASH
                geminiService.initialize(apiKey, model)
            }
        }
    }

    fun setGeminiModel(modelId: String) {
        viewModelScope.launch {
            preferencesManager.setGeminiModel(modelId)
            val apiKey = _uiState.value.geminiApiKey
            if (apiKey.isNotBlank()) {
                val model = GeminiModel.entries.find { it.modelId == modelId }
                    ?: GeminiModel.GEMINI_1_5_FLASH
                geminiService.initialize(apiKey, model)
            }
        }
    }

    fun setBuddhaAiEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setBuddhaAiEnabled(enabled)
        }
    }

    fun testGeminiConnection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTestingConnection = true, connectionTestResult = null) }

            val result = geminiService.testConnection()

            _uiState.update { state ->
                state.copy(
                    isTestingConnection = false,
                    connectionTestResult = when (result) {
                        is GeminiResult.Success -> result.data
                        is GeminiResult.Error -> result.message
                        is GeminiResult.ApiKeyNotSet -> "Please enter your API key first"
                        is GeminiResult.Loading -> null
                    }
                )
            }
        }
    }

    fun clearConnectionTestResult() {
        _uiState.update { it.copy(connectionTestResult = null) }
    }
}
