package com.prody.prashant.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
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

    companion object {
        // Key for SharedPreferences. Using a different name to avoid potential conflicts
        // with DataStore keys, although not strictly necessary.
        const val KEY_ONBOARDING_COMPLETED_SP = "onboarding_completed_sync"
    }

    init {
        // CRITICAL: One-time migration for startup performance.
        // This moves the 'onboardingCompleted' flag from the asynchronous DataStore
        // to the synchronous SharedPreferences. This allows the MainViewModel to
        // determine the start destination WITHOUT waiting for a Flow to emit,
        // dramatically reducing the time the splash screen is shown.
        if (!sharedPreferences.contains(KEY_ONBOARDING_COMPLETED_SP)) {
            Log.d("PreferencesManager", "Onboarding flag not in SharedPreferences, attempting migration from DataStore.")
            // runBlocking is acceptable here as this is a critical, one-time operation
            // that MUST complete before the UI can be drawn correctly.
            runBlocking {
                try {
                    // Read directly from the DataStore flow to get the current value
                    val onboardingCompletedFromDataStore = dataStore.data
                        .map { preferences ->
                            preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
                        }.first()

                    Log.d("PreferencesManager", "Migrating onboarding_completed flag ($onboardingCompletedFromDataStore) to SharedPreferences.")
                    setOnboardingCompletedSync(onboardingCompletedFromDataStore)
                } catch (e: Exception) {
                    // If migration fails (e.g., I/O error on DataStore), default to 'false'.
                    // This is a safe fallback, ensuring the app starts into the onboarding flow.
                    Log.e("PreferencesManager", "Failed to migrate onboarding_completed flag from DataStore", e)
                    setOnboardingCompletedSync(false)
                }
            }
        }
    }

    // Keys
    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
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
    }

    // Onboarding
    /**
     * Synchronously checks if the onboarding process has been completed.
     * This is critical for making an immediate decision on the start destination
     * of the app, avoiding a long splash screen wait time.
     * It reads from SharedPreferences, which is populated by a one-time
     * migration from DataStore in the init block.
     */
    fun getOnboardingCompletedSync(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED_SP, false)
    }

    /**
     * The reactive Flow for onboarding completion status.
     * Continues to be used by parts of the app that need to react to this change.
     */
    val onboardingCompleted: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }

    /**
     * Asynchronously sets the onboarding completion status.
     * This writes to BOTH the fast, synchronous SharedPreferences and the
     * persistent, asynchronous DataStore to ensure data consistency.
     */
    suspend fun setOnboardingCompleted(completed: Boolean) {
        setOnboardingCompletedSync(completed)
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    /**
     * Synchronously writes the onboarding status to SharedPreferences.
     * This is used by the migration logic and the public async setter.
     */
    private fun setOnboardingCompletedSync(completed: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED_SP, completed).apply()
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
}
