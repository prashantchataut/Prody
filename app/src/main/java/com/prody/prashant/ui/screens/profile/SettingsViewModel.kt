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
    val buddhaAiEnabled: Boolean = true,
    // Buddha AI per-feature toggles
    val buddhaDailyWisdomEnabled: Boolean = true,
    val buddhaQuoteExplanationEnabled: Boolean = true,
    val buddhaJournalInsightsEnabled: Boolean = true,
    val buddhaPatternTrackingEnabled: Boolean = true,
    val buddhaVocabularyContextEnabled: Boolean = true,
    val buddhaMessageHelperEnabled: Boolean = true,
    val buddhaPlayfulMode: Boolean = false,
    val buddhaReduceAiUsage: Boolean = false
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

                // Combine general preference settings
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

                // Combine Buddha AI feature toggles (first group)
                val buddhaFeatures1 = combine(
                    preferencesManager.buddhaDailyWisdomEnabled,
                    preferencesManager.buddhaQuoteExplanationEnabled,
                    preferencesManager.buddhaJournalInsightsEnabled,
                    preferencesManager.buddhaPatternTrackingEnabled
                ) { daily, quote, journal, pattern ->
                    BuddhaFeatures1(daily, quote, journal, pattern)
                }

                // Combine Buddha AI feature toggles (second group)
                val buddhaFeatures2 = combine(
                    preferencesManager.buddhaVocabularyContextEnabled,
                    preferencesManager.buddhaMessageHelperEnabled,
                    preferencesManager.buddhaPlayfulMode,
                    preferencesManager.buddhaReduceAiUsage
                ) { vocab, message, playful, reduce ->
                    BuddhaFeatures2(vocab, message, playful, reduce)
                }

                // Combine all groups into final state
                combine(
                    appearanceAndNotifications,
                    preferences,
                    buddhaFeatures1,
                    buddhaFeatures2
                ) { appearance, prefs, buddha1, buddha2 ->
                    SettingsUiState(
                        themeMode = appearance.themeMode,
                        dynamicColors = appearance.dynamicColors,
                        notificationsEnabled = appearance.notificationsEnabled,
                        wisdomNotificationsEnabled = appearance.wisdomNotificationsEnabled,
                        journalRemindersEnabled = appearance.journalRemindersEnabled,
                        hapticFeedbackEnabled = prefs.hapticFeedbackEnabled,
                        compactView = prefs.compactView,
                        buddhaAiEnabled = prefs.buddhaAiEnabled,
                        buddhaDailyWisdomEnabled = buddha1.daily,
                        buddhaQuoteExplanationEnabled = buddha1.quote,
                        buddhaJournalInsightsEnabled = buddha1.journal,
                        buddhaPatternTrackingEnabled = buddha1.pattern,
                        buddhaVocabularyContextEnabled = buddha2.vocab,
                        buddhaMessageHelperEnabled = buddha2.message,
                        buddhaPlayfulMode = buddha2.playful,
                        buddhaReduceAiUsage = buddha2.reduce
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

    private data class BuddhaFeatures1(
        val daily: Boolean,
        val quote: Boolean,
        val journal: Boolean,
        val pattern: Boolean
    )

    private data class BuddhaFeatures2(
        val vocab: Boolean,
        val message: Boolean,
        val playful: Boolean,
        val reduce: Boolean
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

    // Buddha AI per-feature setters

    fun setBuddhaDailyWisdomEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBuddhaDailyWisdomEnabled(enabled)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error setting Buddha daily wisdom enabled", e)
            }
        }
    }

    fun setBuddhaQuoteExplanationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBuddhaQuoteExplanationEnabled(enabled)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error setting Buddha quote explanation enabled", e)
            }
        }
    }

    fun setBuddhaJournalInsightsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBuddhaJournalInsightsEnabled(enabled)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error setting Buddha journal insights enabled", e)
            }
        }
    }

    fun setBuddhaPatternTrackingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBuddhaPatternTrackingEnabled(enabled)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error setting Buddha pattern tracking enabled", e)
            }
        }
    }

    fun setBuddhaVocabularyContextEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBuddhaVocabularyContextEnabled(enabled)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error setting Buddha vocabulary context enabled", e)
            }
        }
    }

    fun setBuddhaMessageHelperEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBuddhaMessageHelperEnabled(enabled)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error setting Buddha message helper enabled", e)
            }
        }
    }

    fun setBuddhaPlayfulMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBuddhaPlayfulMode(enabled)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error setting Buddha playful mode", e)
            }
        }
    }

    fun setBuddhaReduceAiUsage(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBuddhaReduceAiUsage(enabled)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error setting Buddha reduce AI usage", e)
            }
        }
    }
}
