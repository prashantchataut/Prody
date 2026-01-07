package com.prody.prashant.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prody_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) {
    private val dataStore = context.dataStore

    private object PreferencesKeys {
        // Legacy key for one-time migration to SharedPreferences
        val ONBOARDING_COMPLETED_OLD = booleanPreferencesKey("onboarding_completed")

        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DAILY_REMINDER_HOUR = intPreferencesKey("daily_reminder_hour")
        val DAILY_REMINDER_MINUTE = intPreferencesKey("daily_reminder_minute")
        val WISDOM_NOTIFICATION_ENABLED = booleanPreferencesKey("wisdom_notification_enabled")
        val JOURNAL_REMINDER_ENABLED = booleanPreferencesKey("journal_reminder_enabled")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val LAST_ACTIVE_DATE = longPreferencesKey("last_active_date")
        val DAILY_WISDOM_LAST_SHOWN = longPreferencesKey("daily_wisdom_last_shown")
        val SELECTED_WISDOM_CATEGORIES = stringPreferencesKey("selected_wisdom_categories")
        val VOCABULARY_DIFFICULTY_PREFERENCE = intPreferencesKey("vocabulary_difficulty")
        val AUTO_PLAY_PRONUNCIATION = booleanPreferencesKey("auto_play_pronunciation")
        val COMPACT_CARD_VIEW = booleanPreferencesKey("compact_card_view")
        val HAPTIC_FEEDBACK_ENABLED = booleanPreferencesKey("haptic_feedback")
        val FIRST_LAUNCH_TIME = longPreferencesKey("first_launch_time")
        val USER_ID = stringPreferencesKey("user_id")
        val GAMIFICATION_INITIALIZED = booleanPreferencesKey("gamification_initialized")
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val GEMINI_MODEL = stringPreferencesKey("gemini_model")
        val BUDDHA_AI_ENABLED = booleanPreferencesKey("buddha_ai_enabled")
        val BUDDHA_DAILY_WISDOM_ENABLED = booleanPreferencesKey("buddha_daily_wisdom_enabled")
        val BUDDHA_QUOTE_EXPLANATION_ENABLED = booleanPreferencesKey("buddha_quote_explanation_enabled")
        val BUDDHA_JOURNAL_INSIGHTS_ENABLED = booleanPreferencesKey("buddha_journal_insights_enabled")
        val BUDDHA_PATTERN_TRACKING_ENABLED = booleanPreferencesKey("buddha_pattern_tracking_enabled")
        val BUDDHA_PLAYFUL_MODE = booleanPreferencesKey("buddha_playful_mode")
        val BUDDHA_REDUCE_AI_USAGE = booleanPreferencesKey("buddha_reduce_ai_usage")
        val BUDDHA_PERSONALITY_MODE = stringPreferencesKey("buddha_personality_mode")
        val DEBUG_PREVIEW_DEV_BADGE = booleanPreferencesKey("debug_preview_dev_badge")
        val DEBUG_PREVIEW_BETA_BADGE = booleanPreferencesKey("debug_preview_beta_badge")
        val DEBUG_AI_PROOF_MODE = booleanPreferencesKey("debug_ai_proof_mode")
        val PRIVACY_LOCK_JOURNAL = booleanPreferencesKey("privacy_lock_journal")
        val PRIVACY_LOCK_FUTURE_MESSAGES = booleanPreferencesKey("privacy_lock_future_messages")
        val PRIVACY_LOCK_ON_BACKGROUND = booleanPreferencesKey("privacy_lock_on_background")
        val PRIVACY_LAST_UNLOCKED_AT = longPreferencesKey("privacy_last_unlocked_at")
    }

    val onboardingCompleted: Flow<Boolean> = flow {
        if (sharedPreferences.contains(KEY_ONBOARDING_COMPLETED)) {
            emit(sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false))
        } else {
            val valueFromDataStore = dataStore.data
                .map { it[PreferencesKeys.ONBOARDING_COMPLETED_OLD] ?: false }
                .first()
            sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, valueFromDataStore).apply()
            dataStore.edit { it.remove(PreferencesKeys.ONBOARDING_COMPLETED_OLD) }
            emit(valueFromDataStore)
        }
    }.catch {
        emit(false)
    }.flowOn(Dispatchers.IO)

    suspend fun setOnboardingCompleted(completed: Boolean) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
        }
    }

    val themeMode: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.THEME_MODE] ?: "system"
        }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }

    val dynamicColors: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLORS] ?: false
        }

    suspend fun setDynamicColors(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLORS] = enabled
        }
    }

    val notificationsEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
        }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    val dailyReminderHour: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER_HOUR] ?: 9
        }

    val dailyReminderMinute: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER_MINUTE] ?: 0
        }

    suspend fun setDailyReminderTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER_HOUR] = hour
            preferences[PreferencesKeys.DAILY_REMINDER_MINUTE] = minute
        }
    }

    val wisdomNotificationEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.WISDOM_NOTIFICATION_ENABLED] ?: true
        }

    suspend fun setWisdomNotificationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.WISDOM_NOTIFICATION_ENABLED] = enabled
        }
    }

    val journalReminderEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.JOURNAL_REMINDER_ENABLED] ?: true
        }

    suspend fun setJournalReminderEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.JOURNAL_REMINDER_ENABLED] = enabled
        }
    }

    val currentStreak: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.CURRENT_STREAK] ?: 0
        }

    suspend fun setCurrentStreak(streak: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_STREAK] = streak
        }
    }

    val lastActiveDate: Flow<Long> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.LAST_ACTIVE_DATE] ?: 0L
        }

    suspend fun setLastActiveDate(date: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_ACTIVE_DATE] = date
        }
    }

    val dailyWisdomLastShown: Flow<Long> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.DAILY_WISDOM_LAST_SHOWN] ?: 0L
        }

    suspend fun setDailyWisdomLastShown(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_WISDOM_LAST_SHOWN] = timestamp
        }
    }

    val selectedWisdomCategories: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_WISDOM_CATEGORIES]
                ?.split(",")
                ?.filter { it.isNotBlank() }
                ?.toSet()
                ?: setOf("wisdom", "motivation", "stoic", "life")
        }

    suspend fun setSelectedWisdomCategories(categories: Set<String>) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_WISDOM_CATEGORIES] = categories.joinToString(",")
        }
    }

    val vocabularyDifficulty: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.VOCABULARY_DIFFICULTY_PREFERENCE] ?: 3
        }

    suspend fun setVocabularyDifficulty(difficulty: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.VOCABULARY_DIFFICULTY_PREFERENCE] = difficulty
        }
    }

    val autoPlayPronunciation: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.AUTO_PLAY_PRONUNCIATION] ?: false
        }

    suspend fun setAutoPlayPronunciation(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_PLAY_PRONUNCIATION] = enabled
        }
    }

    val compactCardView: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.COMPACT_CARD_VIEW] ?: false
        }

    suspend fun setCompactCardView(compact: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.COMPACT_CARD_VIEW] = compact
        }
    }

    val hapticFeedbackEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.HAPTIC_FEEDBACK_ENABLED] ?: true
        }

    suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTIC_FEEDBACK_ENABLED] = enabled
        }
    }

    val firstLaunchTime: Flow<Long> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.FIRST_LAUNCH_TIME] ?: 0L
        }

    suspend fun setFirstLaunchTime(timestamp: Long) {
        dataStore.edit { preferences ->
            if (preferences[PreferencesKeys.FIRST_LAUNCH_TIME] == null) {
                preferences[PreferencesKeys.FIRST_LAUNCH_TIME] = timestamp
            }
        }
    }

    val userId: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_ID] ?: ""
        }

    suspend fun setUserId(id: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = id
        }
    }

    val gamificationInitialized: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.GAMIFICATION_INITIALIZED] ?: false
        }

    suspend fun setGamificationInitialized(initialized: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GAMIFICATION_INITIALIZED] = initialized
        }
    }

    val geminiApiKey: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.GEMINI_API_KEY] ?: ""
        }

    suspend fun setGeminiApiKey(apiKey: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GEMINI_API_KEY] = apiKey
        }
    }

    val geminiModel: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.GEMINI_MODEL] ?: "gemini-1.5-flash"
        }

    suspend fun setGeminiModel(model: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GEMINI_MODEL] = model
        }
    }

    val buddhaAiEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.BUDDHA_AI_ENABLED] ?: true
        }

    suspend fun setBuddhaAiEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BUDDHA_AI_ENABLED] = enabled
        }
    }

    val buddhaDailyWisdomEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.BUDDHA_DAILY_WISDOM_ENABLED] ?: true
        }

    suspend fun setBuddhaDailyWisdomEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BUDDHA_DAILY_WISDOM_ENABLED] = enabled
        }
    }

    val buddhaQuoteExplanationEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.BUDDHA_QUOTE_EXPLANATION_ENABLED] ?: true
        }

    suspend fun setBuddhaQuoteExplanationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BUDDHA_QUOTE_EXPLANATION_ENABLED] = enabled
        }
    }

    val buddhaJournalInsightsEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.BUDDHA_JOURNAL_INSIGHTS_ENABLED] ?: true
        }

    suspend fun setBuddhaJournalInsightsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BUDDHA_JOURNAL_INSIGHTS_ENABLED] = enabled
        }
    }

    val buddhaPatternTrackingEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.BUDDHA_PATTERN_TRACKING_ENABLED] ?: true
        }

    suspend fun setBuddhaPatternTrackingEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BUDDHA_PATTERN_TRACKING_ENABLED] = enabled
        }
    }

    val buddhaPlayfulMode: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.BUDDHA_PLAYFUL_MODE] ?: false
        }

    suspend fun setBuddhaPlayfulMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BUDDHA_PLAYFUL_MODE] = enabled
        }
    }

    val buddhaReduceAiUsage: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.BUDDHA_REDUCE_AI_USAGE] ?: false
        }

    suspend fun setBuddhaReduceAiUsage(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BUDDHA_REDUCE_AI_USAGE] = enabled
        }
    }

    val buddhaPersonalityMode: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.BUDDHA_PERSONALITY_MODE] ?: "STOIC"
        }

    suspend fun setBuddhaPersonalityMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BUDDHA_PERSONALITY_MODE] = mode
        }
    }

    val debugPreviewDevBadge: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.DEBUG_PREVIEW_DEV_BADGE] ?: false
        }

    suspend fun setDebugPreviewDevBadge(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEBUG_PREVIEW_DEV_BADGE] = enabled
        }
    }

    val debugPreviewBetaBadge: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.DEBUG_PREVIEW_BETA_BADGE] ?: false
        }

    suspend fun setDebugPreviewBetaBadge(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEBUG_PREVIEW_BETA_BADGE] = enabled
        }
    }

    val debugAiProofMode: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.DEBUG_AI_PROOF_MODE] ?: false
        }

    suspend fun setDebugAiProofMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEBUG_AI_PROOF_MODE] = enabled
        }
    }

    val privacyLockJournal: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.PRIVACY_LOCK_JOURNAL] ?: false
        }

    suspend fun setPrivacyLockJournal(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PRIVACY_LOCK_JOURNAL] = enabled
        }
    }

    val privacyLockFutureMessages: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.PRIVACY_LOCK_FUTURE_MESSAGES] ?: false
        }

    suspend fun setPrivacyLockFutureMessages(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PRIVACY_LOCK_FUTURE_MESSAGES] = enabled
        }
    }

    val privacyLockOnBackground: Flow<Boolean> = dataStore..data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.PRIVACY_LOCK_ON_BACKGROUND] ?: true
        }

    suspend fun setPrivacyLockOnBackground(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PRIVACY_LOCK_ON_BACKGROUND] = enabled
        }
    }

    val privacyLastUnlockedAt: Flow<Long> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.PRIVACY_LAST_UNLOCKED_AT] ?: 0L
        }

    suspend fun setPrivacyLastUnlockedAt(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PRIVACY_LAST_UNLOCKED_AT] = timestamp
        }
    }

    suspend fun clearAllPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun resetToDefaults() {
        sharedPreferences.edit().remove(KEY_ONBOARDING_COMPLETED).apply()
        dataStore.edit { preferences ->
            preferences.clear()
            preferences[PreferencesKeys.THEME_MODE] = "system"
            preferences[PreferencesKeys.DYNAMIC_COLORS] = false
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = true
            preferences[PreferencesKeys.DAILY_REMINDER_HOUR] = 9
            preferences[PreferencesKeys.DAILY_REMINDER_MINUTE] = 0
            preferences[PreferencesKeys.WISDOM_NOTIFICATION_ENABLED] = true
            preferences[PreferencesKeys.JOURNAL_REMINDER_ENABLED] = true
            preferences[PreferencesKeys.CURRENT_STREAK] = 0
            preferences[PreferencesKeys.VOCABULARY_DIFFICULTY_PREFERENCE] = 3
            preferences[PreferencesKeys.AUTO_PLAY_PRONUNCIATION] = false
            preferences[PreferencesKeys.COMPACT_CARD_VIEW] = false
            preferences[PreferencesKeys.HAPTIC_FEEDBACK_ENABLED] = true
            preferences[PreferencesKeys.BUDDHA_AI_ENABLED] = true
            preferences[PreferencesKeys.BUDDHA_DAILY_WISDOM_ENABLED] = true
            preferences[PreferencesKeys.BUDDHA_QUOTE_EXPLANATION_ENABLED] = true
            preferences[PreferencesKeys.BUDDHA_JOURNAL_INSIGHTS_ENABLED] = true
            preferences[PreferencesKeys.BUDDHA_PATTERN_TRACKING_ENABLED] = true
            preferences[PreferencesKeys.BUDDHA_PLAYFUL_MODE] = false
            preferences[PreferencesKeys.BUDDHA_REDUCE_AI_USAGE] = false
            preferences[PreferencesKeys.PRIVACY_LOCK_JOURNAL] = false
            preferences[PreferencesKeys.PRIVACY_LOCK_FUTURE_MESSAGES] = false
            preferences[PreferencesKeys.PRIVACY_LOCK_ON_BACKGROUND] = true
        }
    }

    companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
