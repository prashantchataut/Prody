package com.prody.prashant.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prody_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    // Keys
    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DAILY_REMINDER_HOUR = intPreferencesKey("daily_reminder_hour")
        val DAILY_REMINDER_MINUTE = intPreferencesKey("daily_reminder_minute")
        val EVENING_REMINDER_HOUR = intPreferencesKey("evening_reminder_hour")
        val EVENING_REMINDER_MINUTE = intPreferencesKey("evening_reminder_minute")
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

        // Gamification
        val GAMIFICATION_INITIALIZED = booleanPreferencesKey("gamification_initialized")

        // Gemini AI Settings
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val GEMINI_MODEL = stringPreferencesKey("gemini_model")
        val BUDDHA_AI_ENABLED = booleanPreferencesKey("buddha_ai_enabled")

        // Buddha AI Feature Toggles
        val BUDDHA_DAILY_WISDOM_ENABLED = booleanPreferencesKey("buddha_daily_wisdom_enabled")
        val BUDDHA_QUOTE_EXPLANATION_ENABLED = booleanPreferencesKey("buddha_quote_explanation_enabled")
        val BUDDHA_JOURNAL_INSIGHTS_ENABLED = booleanPreferencesKey("buddha_journal_insights_enabled")
        val BUDDHA_PATTERN_TRACKING_ENABLED = booleanPreferencesKey("buddha_pattern_tracking_enabled")
        val BUDDHA_PLAYFUL_MODE = booleanPreferencesKey("buddha_playful_mode")
        val BUDDHA_REDUCE_AI_USAGE = booleanPreferencesKey("buddha_reduce_ai_usage")
        val BUDDHA_PERSONALITY_MODE = stringPreferencesKey("buddha_personality_mode")

        // Debug: Special Badge Preview Toggles
        val DEBUG_PREVIEW_DEV_BADGE = booleanPreferencesKey("debug_preview_dev_badge")
        val DEBUG_PREVIEW_BETA_BADGE = booleanPreferencesKey("debug_preview_beta_badge")

        // Debug: AI Proof Mode - Shows AI generation metadata in UI
        val DEBUG_AI_PROOF_MODE = booleanPreferencesKey("debug_ai_proof_mode")

        // Privacy Mode settings
        val PRIVACY_LOCK_JOURNAL = booleanPreferencesKey("privacy_lock_journal")
        val PRIVACY_LOCK_FUTURE_MESSAGES = booleanPreferencesKey("privacy_lock_future_messages")
        val PRIVACY_LOCK_ON_BACKGROUND = booleanPreferencesKey("privacy_lock_on_background")
        val PRIVACY_LAST_UNLOCKED_AT = longPreferencesKey("privacy_last_unlocked_at")

        // Weekly Summary settings
        val WEEKLY_SUMMARY_ENABLED = booleanPreferencesKey("weekly_summary_enabled")
        val WEEKLY_SUMMARY_DAY = intPreferencesKey("weekly_summary_day") // 0-6 (Sunday-Saturday)
        val LAST_WEEKLY_SUMMARY_SHOWN = longPreferencesKey("last_weekly_summary_shown")
        val WEEKLY_SUMMARY_NOTIFICATIONS = booleanPreferencesKey("weekly_summary_notifications")

        // Daily Ritual settings
        val MORNING_RITUAL_ENABLED = booleanPreferencesKey("morning_ritual_enabled")
        val EVENING_RITUAL_ENABLED = booleanPreferencesKey("evening_ritual_enabled")
        val MORNING_RITUAL_START_HOUR = intPreferencesKey("morning_ritual_start_hour")
        val MORNING_RITUAL_END_HOUR = intPreferencesKey("morning_ritual_end_hour")
        val EVENING_RITUAL_START_HOUR = intPreferencesKey("evening_ritual_start_hour")
        val RITUAL_REMINDER_ENABLED = booleanPreferencesKey("ritual_reminder_enabled")

        // Quiet Mode settings
        val QUIET_MODE_ENABLED = booleanPreferencesKey("quiet_mode_enabled")
        val QUIET_MODE_AUTO_SUGGEST_THRESHOLD = intPreferencesKey("quiet_mode_auto_suggest_threshold")
        val QUIET_MODE_LAST_SUGGESTED_AT = longPreferencesKey("quiet_mode_last_suggested_at")
        val QUIET_MODE_ENABLED_AT = longPreferencesKey("quiet_mode_enabled_at")
        val QUIET_MODE_LAST_CHECK_IN_AT = longPreferencesKey("quiet_mode_last_check_in_at")

        // Haven Personal Therapist settings
        val HAVEN_ENABLED = booleanPreferencesKey("haven_enabled")
        val HAVEN_NOTIFICATIONS_ENABLED = booleanPreferencesKey("haven_notifications_enabled")
        val HAVEN_DAILY_CHECK_IN_TIME = intPreferencesKey("haven_daily_check_in_time")
        val HAVEN_LAST_SESSION_AT = longPreferencesKey("haven_last_session_at")
        val THERAPIST_API_KEY = stringPreferencesKey("therapist_api_key")
    }

    // Onboarding
    val onboardingCompleted: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    // Theme
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

    // Notifications
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

    val eveningReminderHour: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.EVENING_REMINDER_HOUR] ?: 20
        }

    val eveningReminderMinute: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.EVENING_REMINDER_MINUTE] ?: 0
        }

    suspend fun setEveningReminderTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.EVENING_REMINDER_HOUR] = hour
            preferences[PreferencesKeys.EVENING_REMINDER_MINUTE] = minute
        }
    }

    // Streak
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

    // Wisdom
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

    // Vocabulary
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

    // UI Preferences
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

    // User Info
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

    // Gamification Initialized Flag
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

    // Gemini AI Settings
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

    // Buddha AI Feature Toggles
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

    // ===== DEBUG: SPECIAL BADGE PREVIEW =====
    // These are for previewing Dev/Beta badges without OAuth
    // Only shown in debug builds, not in production

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

    // Debug: AI Proof Mode - Shows AI generation metadata in UI (DEBUG builds only)
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

    // ===== PRIVACY MODE SETTINGS =====

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

    val privacyLockOnBackground: Flow<Boolean> = dataStore.data
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

    // ===== WEEKLY SUMMARY SETTINGS =====

    val weeklySummaryEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.WEEKLY_SUMMARY_ENABLED] ?: true
        }

    suspend fun setWeeklySummaryEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.WEEKLY_SUMMARY_ENABLED] = enabled
        }
    }

    val weeklySummaryDay: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.WEEKLY_SUMMARY_DAY] ?: 0 // Default Sunday (0)
        }

    suspend fun setWeeklySummaryDay(dayOfWeek: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.WEEKLY_SUMMARY_DAY] = dayOfWeek.coerceIn(0, 6)
        }
    }

    val lastWeeklySummaryShown: Flow<Long> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.LAST_WEEKLY_SUMMARY_SHOWN] ?: 0L
        }

    suspend fun setLastWeeklySummaryShown(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_WEEKLY_SUMMARY_SHOWN] = timestamp
        }
    }

    val weeklySummaryNotifications: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.WEEKLY_SUMMARY_NOTIFICATIONS] ?: true
        }

    suspend fun setWeeklySummaryNotifications(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.WEEKLY_SUMMARY_NOTIFICATIONS] = enabled
        }
    }

    // ===== DAILY RITUAL SETTINGS =====

    val morningRitualEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.MORNING_RITUAL_ENABLED] ?: true
        }

    suspend fun setMorningRitualEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MORNING_RITUAL_ENABLED] = enabled
        }
    }

    val eveningRitualEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.EVENING_RITUAL_ENABLED] ?: true
        }

    suspend fun setEveningRitualEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.EVENING_RITUAL_ENABLED] = enabled
        }
    }

    val morningRitualStartHour: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.MORNING_RITUAL_START_HOUR] ?: 5
        }

    suspend fun setMorningRitualStartHour(hour: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MORNING_RITUAL_START_HOUR] = hour
        }
    }

    val morningRitualEndHour: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.MORNING_RITUAL_END_HOUR] ?: 12
        }

    suspend fun setMorningRitualEndHour(hour: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MORNING_RITUAL_END_HOUR] = hour
        }
    }

    val eveningRitualStartHour: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.EVENING_RITUAL_START_HOUR] ?: 18
        }

    suspend fun setEveningRitualStartHour(hour: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.EVENING_RITUAL_START_HOUR] = hour
        }
    }

    val ritualReminderEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.RITUAL_REMINDER_ENABLED] ?: true
        }

    suspend fun setRitualReminderEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.RITUAL_REMINDER_ENABLED] = enabled
        }
    }

    // ===== QUIET MODE SETTINGS =====

    val quietModeEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.QUIET_MODE_ENABLED] ?: false
        }

    suspend fun setQuietModeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.QUIET_MODE_ENABLED] = enabled
            if (enabled) {
                preferences[PreferencesKeys.QUIET_MODE_ENABLED_AT] = System.currentTimeMillis()
            }
        }
    }

    val quietModeAutoSuggestThreshold: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.QUIET_MODE_AUTO_SUGGEST_THRESHOLD] ?: 3 // Default: 3 negative entries
        }

    suspend fun setQuietModeAutoSuggestThreshold(threshold: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.QUIET_MODE_AUTO_SUGGEST_THRESHOLD] = threshold
        }
    }

    val quietModeLastSuggestedAt: Flow<Long> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.QUIET_MODE_LAST_SUGGESTED_AT] ?: 0L
        }

    suspend fun setQuietModeLastSuggestedAt(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.QUIET_MODE_LAST_SUGGESTED_AT] = timestamp
        }
    }

    val quietModeEnabledAt: Flow<Long> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.QUIET_MODE_ENABLED_AT] ?: 0L
        }

    val quietModeLastCheckInAt: Flow<Long> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.QUIET_MODE_LAST_CHECK_IN_AT] ?: 0L
        }

    suspend fun setQuietModeLastCheckInAt(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.QUIET_MODE_LAST_CHECK_IN_AT] = timestamp
        }
    }

    // ===== HAVEN PERSONAL THERAPIST SETTINGS =====

    val havenEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.HAVEN_ENABLED] ?: true
        }

    suspend fun setHavenEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAVEN_ENABLED] = enabled
        }
    }

    val havenNotificationsEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.HAVEN_NOTIFICATIONS_ENABLED] ?: true
        }

    suspend fun setHavenNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAVEN_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    val havenDailyCheckInTime: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.HAVEN_DAILY_CHECK_IN_TIME] ?: 9
        }

    suspend fun setHavenDailyCheckInTime(hour: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAVEN_DAILY_CHECK_IN_TIME] = hour
        }
    }

    val havenLastSessionAt: Flow<Long> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.HAVEN_LAST_SESSION_AT] ?: 0L
        }

    suspend fun setHavenLastSessionAt(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAVEN_LAST_SESSION_AT] = timestamp
        }
    }

    val therapistApiKey: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.THERAPIST_API_KEY] ?: ""
        }

    suspend fun setTherapistApiKey(apiKey: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THERAPIST_API_KEY] = apiKey
        }
    }

    // Clear all preferences
    suspend fun clearAllPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Resets all preferences to their default values.
     */
    suspend fun resetToDefaults() {
        dataStore.edit { preferences ->
            preferences.clear()
            // Set default values
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = false
            preferences[PreferencesKeys.THEME_MODE] = "system"
            preferences[PreferencesKeys.DYNAMIC_COLORS] = false
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = true
            preferences[PreferencesKeys.DAILY_REMINDER_HOUR] = 9
            preferences[PreferencesKeys.DAILY_REMINDER_MINUTE] = 0
            preferences[PreferencesKeys.EVENING_REMINDER_HOUR] = 20
            preferences[PreferencesKeys.EVENING_REMINDER_MINUTE] = 0
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
            preferences[PreferencesKeys.WEEKLY_SUMMARY_ENABLED] = true
            preferences[PreferencesKeys.WEEKLY_SUMMARY_DAY] = 0 // Sunday
            preferences[PreferencesKeys.WEEKLY_SUMMARY_NOTIFICATIONS] = true
            preferences[PreferencesKeys.MORNING_RITUAL_ENABLED] = true
            preferences[PreferencesKeys.EVENING_RITUAL_ENABLED] = true
            preferences[PreferencesKeys.MORNING_RITUAL_START_HOUR] = 5
            preferences[PreferencesKeys.MORNING_RITUAL_END_HOUR] = 12
            preferences[PreferencesKeys.EVENING_RITUAL_START_HOUR] = 18
            preferences[PreferencesKeys.RITUAL_REMINDER_ENABLED] = true
        }
    }
}
