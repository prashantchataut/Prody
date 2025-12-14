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
    val compactView: Boolean = false,
    val buddhaAiEnabled: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "SettingsViewModel"
    }

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                // Combine first 5 appearance/notification settings
                val appearanceAndNotifications = combine(
                    preferencesManager.themeMode,
                    preferencesManager.dynamicColors,
                    preferencesManager.notificationsEnabled,
                    preferencesManager.wisdomNotificationEnabled,
                    preferencesManager.journalReminderEnabled
                ) { themeMode, dynamicColors, notificationsEnabled, wisdomEnabled, journalEnabled ->
                    AppearanceAndNotificationSettings(
                        themeMode = themeMode,
                        dynamicColors = dynamicColors,
                        notificationsEnabled = notificationsEnabled,
                        wisdomNotificationsEnabled = wisdomEnabled,
                        journalRemindersEnabled = journalEnabled
                    )
                }

                // Combine remaining preference settings
                val preferences = combine(
                    preferencesManager.hapticFeedbackEnabled,
                    preferencesManager.compactCardView,
                    preferencesManager.buddhaAiEnabled
                ) { hapticEnabled, compactView, buddhaEnabled ->
                    PreferenceSettings(
                        hapticFeedbackEnabled = hapticEnabled,
                        compactView = compactView,
                        buddhaAiEnabled = buddhaEnabled
                    )
                }

                // Combine both groups into final state
                combine(appearanceAndNotifications, preferences) { appearance, prefs ->
                    SettingsUiState(
                        themeMode = appearance.themeMode,
                        dynamicColors = appearance.dynamicColors,
                        notificationsEnabled = appearance.notificationsEnabled,
                        wisdomNotificationsEnabled = appearance.wisdomNotificationsEnabled,
                        journalRemindersEnabled = appearance.journalRemindersEnabled,
                        hapticFeedbackEnabled = prefs.hapticFeedbackEnabled,
                        compactView = prefs.compactView,
                        buddhaAiEnabled = prefs.buddhaAiEnabled
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading settings", e)
            }
        }
    }

    private data class AppearanceAndNotificationSettings(
        val themeMode: String,
        val dynamicColors: Boolean,
        val notificationsEnabled: Boolean,
        val wisdomNotificationsEnabled: Boolean,
        val journalRemindersEnabled: Boolean
    )

    private data class PreferenceSettings(
        val hapticFeedbackEnabled: Boolean,
        val compactView: Boolean,
        val buddhaAiEnabled: Boolean
    )

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            try {
                preferencesManager.setThemeMode(mode)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error setting theme mode", e)
            }
        }
    }

    fun setDynamicColors(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setDynamicColors(enabled)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error setting dynamic colors", e)
            }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setNotificationsEnabled(enabled)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error setting notifications enabled", e)
            }
        }
    }

    fun setWisdomNotifications(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setWisdomNotificationEnabled(enabled)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error setting wisdom notifications", e)
            }
        }
    }

    fun setJournalReminders(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setJournalReminderEnabled(enabled)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error setting journal reminders", e)
            }
        }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setHapticFeedbackEnabled(enabled)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error setting haptic feedback", e)
            }
        }
    }

    fun setCompactView(compact: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setCompactCardView(compact)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error setting compact view", e)
            }
        }
    }

    fun setBuddhaAiEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBuddhaAiEnabled(enabled)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error setting Buddha AI enabled", e)
            }
        }
    }
}
