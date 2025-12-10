package com.prody.prashant.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val compactView: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
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
                preferencesManager.compactCardView
            ) { values ->
                SettingsUiState(
                    themeMode = values[0] as String,
                    dynamicColors = values[1] as Boolean,
                    notificationsEnabled = values[2] as Boolean,
                    wisdomNotificationsEnabled = values[3] as Boolean,
                    journalRemindersEnabled = values[4] as Boolean,
                    hapticFeedbackEnabled = values[5] as Boolean,
                    compactView = values[6] as Boolean
                )
            }.collect { state ->
                _uiState.value = state
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
}
