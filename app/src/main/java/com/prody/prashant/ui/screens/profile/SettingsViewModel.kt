package com.prody.prashant.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.BuildConfig
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.notification.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import com.prody.prashant.data.ai.AiConfigStatus
import com.prody.prashant.data.ai.GeminiService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val themeMode: String = "system",
    val dynamicColors: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val wisdomNotificationsEnabled: Boolean = true,
    val journalRemindersEnabled: Boolean = true,
    val morningHour: Int = 9,
    val morningMinute: Int = 0,
    val eveningHour: Int = 20,
    val eveningMinute: Int = 0,
    val hapticFeedbackEnabled: Boolean = true,
    val compactView: Boolean = false,
    val buddhaAiEnabled: Boolean = true,
    // Buddha AI per-feature toggles
    val buddhaDailyWisdomEnabled: Boolean = true,
    val buddhaQuoteExplanationEnabled: Boolean = true,
    val buddhaJournalInsightsEnabled: Boolean = true,
    val buddhaPatternTrackingEnabled: Boolean = true,
    val buddhaPlayfulMode: Boolean = false,
    val buddhaReduceAiUsage: Boolean = false,
    // Privacy Mode settings
    val privacyLockJournal: Boolean = false,
    val privacyLockFutureMessages: Boolean = false,
    val privacyLockOnBackground: Boolean = true,
    // Data management
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val exportSuccess: Boolean = false,
    val importSuccess: Boolean = false,
    val dataError: String? = null,
    val showClearDataDialog: Boolean = false,
    val isClearingData: Boolean = false,
    // Debug notification state
    val isDebugBuild: Boolean = BuildConfig.DEBUG,
    val debugNotificationSent: String? = null,
    // Debug: AI Proof Mode - Shows AI generation metadata in UI
    val debugAiProofMode: Boolean = false,
    // AI Configuration Status
    val aiConfigStatus: AiConfigStatus = AiConfigStatus.READY
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val notificationScheduler: NotificationScheduler,
    private val geminiService: GeminiService
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
                // Combine appearance settings
                val appearanceSettings = combine(
                    preferencesManager.themeMode,
                    preferencesManager.dynamicColors,
                    preferencesManager.notificationsEnabled,
                    preferencesManager.wisdomNotificationEnabled,
                    preferencesManager.journalReminderEnabled
                ) { themeMode, dynamicColors, notificationsEnabled, wisdomEnabled, journalEnabled ->
                    AppearanceSettings(
                        themeMode = themeMode,
                        dynamicColors = dynamicColors,
                        notificationsEnabled = notificationsEnabled,
                        wisdomNotificationsEnabled = wisdomEnabled,
                        journalRemindersEnabled = journalEnabled
                    )
                }

                // Combine time settings
                val timeSettings = combine(
                    preferencesManager.dailyReminderHour,
                    preferencesManager.dailyReminderMinute,
                    preferencesManager.eveningReminderHour,
                    preferencesManager.eveningReminderMinute
                ) { morningHour, morningMinute, eveningHour, eveningMinute ->
                    TimeSettings(
                        morningHour = morningHour,
                        morningMinute = morningMinute,
                        eveningHour = eveningHour,
                        eveningMinute = eveningMinute
                    )
                }

                // Combine appearance and time settings
                val appearanceAndNotifications = combine(
                    appearanceSettings,
                    timeSettings
                ) { appearance, time ->
                    AppearanceAndNotificationSettings(
                        themeMode = appearance.themeMode,
                        dynamicColors = appearance.dynamicColors,
                        notificationsEnabled = appearance.notificationsEnabled,
                        wisdomNotificationsEnabled = appearance.wisdomNotificationsEnabled,
                        journalRemindersEnabled = appearance.journalRemindersEnabled,
                        morningHour = time.morningHour,
                        morningMinute = time.morningMinute,
                        eveningHour = time.eveningHour,
                        eveningMinute = time.eveningMinute
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
                    preferencesManager.buddhaPlayfulMode,
                    preferencesManager.buddhaReduceAiUsage,
                    preferencesManager.debugAiProofMode
                ) { playful, reduce, aiProofMode ->
                    BuddhaFeatures2(playful, reduce, aiProofMode)
                }

                // Combine Privacy Mode settings
                val privacySettings = combine(
                    preferencesManager.privacyLockJournal,
                    preferencesManager.privacyLockFutureMessages,
                    preferencesManager.privacyLockOnBackground
                ) { lockJournal, lockFutureMessages, lockOnBackground ->
                    PrivacySettings(lockJournal, lockFutureMessages, lockOnBackground)
                }

                // Combine all groups into final state
                combine(
                    appearanceAndNotifications,
                    preferences,
                    buddhaFeatures1,
                    buddhaFeatures2,
                    privacySettings
                ) { appearance, prefs, buddha1, buddha2, privacy ->
                    SettingsUiState(
                        themeMode = appearance.themeMode,
                        dynamicColors = appearance.dynamicColors,
                        notificationsEnabled = appearance.notificationsEnabled,
                        wisdomNotificationsEnabled = appearance.wisdomNotificationsEnabled,
                        journalRemindersEnabled = appearance.journalRemindersEnabled,
                        morningHour = appearance.morningHour,
                        morningMinute = appearance.morningMinute,
                        eveningHour = appearance.eveningHour,
                        eveningMinute = appearance.eveningMinute,
                        hapticFeedbackEnabled = prefs.hapticFeedbackEnabled,
                        compactView = prefs.compactView,
                        buddhaAiEnabled = prefs.buddhaAiEnabled,
                        buddhaDailyWisdomEnabled = buddha1.daily,
                        buddhaQuoteExplanationEnabled = buddha1.quote,
                        buddhaJournalInsightsEnabled = buddha1.journal,
                        buddhaPatternTrackingEnabled = buddha1.pattern,
                        buddhaPlayfulMode = buddha2.playful,
                        buddhaReduceAiUsage = buddha2.reduce,
                        debugAiProofMode = buddha2.aiProofMode,
                        privacyLockJournal = privacy.lockJournal,
                        privacyLockFutureMessages = privacy.lockFutureMessages,
                        privacyLockOnBackground = privacy.lockOnBackground
                    )
                }.collect { state ->
                    _uiState.value = state.copy(
                        aiConfigStatus = geminiService.getConfigStatus()
                    )
                }
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error loading settings", e)
            }
        }
    }

    private data class AppearanceSettings(
        val themeMode: String,
        val dynamicColors: Boolean,
        val notificationsEnabled: Boolean,
        val wisdomNotificationsEnabled: Boolean,
        val journalRemindersEnabled: Boolean
    )

    private data class TimeSettings(
        val morningHour: Int,
        val morningMinute: Int,
        val eveningHour: Int,
        val eveningMinute: Int
    )

    private data class AppearanceAndNotificationSettings(
        val themeMode: String,
        val dynamicColors: Boolean,
        val notificationsEnabled: Boolean,
        val wisdomNotificationsEnabled: Boolean,
        val journalRemindersEnabled: Boolean,
        val morningHour: Int,
        val morningMinute: Int,
        val eveningHour: Int,
        val eveningMinute: Int
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
        val playful: Boolean,
        val reduce: Boolean,
        val aiProofMode: Boolean
    )

    private data class PrivacySettings(
        val lockJournal: Boolean,
        val lockFutureMessages: Boolean,
        val lockOnBackground: Boolean
    )

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            try {
                preferencesManager.setThemeMode(mode)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting theme mode", e)
            }
        }
    }

    fun setDynamicColors(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setDynamicColors(enabled)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting dynamic colors", e)
            }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setNotificationsEnabled(enabled)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting notifications enabled", e)
            }
        }
    }

    fun setWisdomNotifications(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setWisdomNotificationEnabled(enabled)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting wisdom notifications", e)
            }
        }
    }

    fun setJournalReminders(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setJournalReminderEnabled(enabled)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting journal reminders", e)
            }
        }
    }

    fun setMorningNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            try {
                preferencesManager.setDailyReminderTime(hour, minute)
                notificationScheduler.rescheduleAllNotifications()
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting morning notification time", e)
            }
        }
    }

    fun setEveningNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            try {
                preferencesManager.setEveningReminderTime(hour, minute)
                notificationScheduler.rescheduleAllNotifications()
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting evening notification time", e)
            }
        }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setHapticFeedbackEnabled(enabled)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting haptic feedback", e)
            }
        }
    }

    fun setCompactView(compact: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setCompactCardView(compact)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting compact view", e)
            }
        }
    }

    fun setBuddhaAiEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBuddhaAiEnabled(enabled)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting Buddha AI enabled", e)
            }
        }
    }

    // Buddha AI per-feature setters

    fun setBuddhaDailyWisdomEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBuddhaDailyWisdomEnabled(enabled)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting Buddha daily wisdom enabled", e)
            }
        }
    }

    fun setBuddhaQuoteExplanationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBuddhaQuoteExplanationEnabled(enabled)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting Buddha quote explanation enabled", e)
            }
        }
    }

    fun setBuddhaJournalInsightsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBuddhaJournalInsightsEnabled(enabled)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting Buddha journal insights enabled", e)
            }
        }
    }

    fun setBuddhaPatternTrackingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBuddhaPatternTrackingEnabled(enabled)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting Buddha pattern tracking enabled", e)
            }
        }
    }

    fun setBuddhaPlayfulMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBuddhaPlayfulMode(enabled)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting Buddha playful mode", e)
            }
        }
    }

    fun setBuddhaReduceAiUsage(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBuddhaReduceAiUsage(enabled)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting Buddha reduce AI usage", e)
            }
        }
    }

    // Privacy Mode setters

    fun setPrivacyLockJournal(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setPrivacyLockJournal(enabled)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting privacy lock journal", e)
            }
        }
    }

    fun setPrivacyLockFutureMessages(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setPrivacyLockFutureMessages(enabled)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting privacy lock future messages", e)
            }
        }
    }

    fun setPrivacyLockOnBackground(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setPrivacyLockOnBackground(enabled)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting privacy lock on background", e)
            }
        }
    }

    // Data Management functions

    fun setExporting(exporting: Boolean) {
        _uiState.update { it.copy(isExporting = exporting) }
    }

    fun setExportSuccess(success: Boolean) {
        _uiState.update { it.copy(exportSuccess = success, isExporting = false) }
    }

    fun setImporting(importing: Boolean) {
        _uiState.update { it.copy(isImporting = importing) }
    }

    fun setImportSuccess(success: Boolean) {
        _uiState.update { it.copy(importSuccess = success, isImporting = false) }
    }

    fun setDataError(error: String?) {
        _uiState.update { it.copy(dataError = error, isExporting = false, isImporting = false) }
    }

    fun clearDataError() {
        _uiState.update { it.copy(dataError = null) }
    }

    fun clearExportSuccess() {
        _uiState.update { it.copy(exportSuccess = false) }
    }

    fun clearImportSuccess() {
        _uiState.update { it.copy(importSuccess = false) }
    }

    fun showClearDataDialog() {
        _uiState.update { it.copy(showClearDataDialog = true) }
    }

    fun hideClearDataDialog() {
        _uiState.update { it.copy(showClearDataDialog = false) }
    }

    fun clearAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isClearingData = true, showClearDataDialog = false) }
                // Clear preferences
                preferencesManager.clearAllPreferences()
                _uiState.update { it.copy(isClearingData = false) }
                onComplete()
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error clearing data", e)
                _uiState.update { it.copy(isClearingData = false, dataError = "Failed to clear data") }
            }
        }
    }

    // Debug notification functions

    /**
     * Sends a test notification immediately (DEBUG builds only).
     * @param type One of: morning, evening, word, streak, journal, future
     */
    fun sendTestNotification(type: String) {
        if (!BuildConfig.DEBUG) return
        try {
            notificationScheduler.debugTriggerNotificationNow(type)
            _uiState.update { it.copy(debugNotificationSent = type) }
            // Clear the message after a delay
            viewModelScope.launch {
                kotlinx.coroutines.delay(3000)
                _uiState.update { it.copy(debugNotificationSent = null) }
            }
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Error sending test notification", e)
        }
    }

    /**
     * Clears the debug notification sent message.
     */
    fun clearDebugNotificationMessage() {
        _uiState.update { it.copy(debugNotificationSent = null) }
    }

    /**
     * Toggles AI Proof Mode (DEBUG builds only).
     * When enabled, shows AI generation metadata (provider, timestamp) in UI.
     */
    fun setDebugAiProofMode(enabled: Boolean) {
        if (!BuildConfig.DEBUG) return
        viewModelScope.launch {
            try {
                preferencesManager.setDebugAiProofMode(enabled)
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error setting AI Proof Mode", e)
            }
        }
    }
}
