package com.prody.prashant.data.onboarding

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.aiOnboardingDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "ai_onboarding_prefs"
)

/**
 * AI feature hint types
 */
enum class AiHintType {
    /** Introduction to Buddha AI */
    BUDDHA_INTRO,
    /** First journal entry - explain AI analysis */
    FIRST_JOURNAL_INSIGHT,
    /** First quote view - explain explanation feature */
    FIRST_QUOTE_EXPLANATION,
    /** AI settings customization tip */
    AI_CUSTOMIZATION_TIP,
    /** Daily wisdom generation */
    DAILY_WISDOM_TIP,
    /** Pattern tracking explanation */
    PATTERN_TRACKING_TIP,
    /** Message helper tip */
    MESSAGE_HELPER_TIP
}

/**
 * Contextual AI hint data
 */
data class AiHint(
    val type: AiHintType,
    val title: String,
    val message: String,
    val actionLabel: String? = null
)

/**
 * AiOnboardingManager tracks and controls AI feature onboarding hints.
 *
 * Features:
 * - One-time hints for each AI feature
 * - "Don't show again" support
 * - Contextual hint delivery
 * - Minimal, non-intrusive guidance
 *
 * Design Principles:
 * - Max 3 cards for Buddha Guide intro
 * - Hints disappear after first interaction
 * - Users can dismiss permanently
 */
@Singleton
class AiOnboardingManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        // Preference keys for tracking hint display
        private val KEY_BUDDHA_GUIDE_SHOWN = booleanPreferencesKey("buddha_guide_shown")
        private val KEY_BUDDHA_GUIDE_DISMISSED = booleanPreferencesKey("buddha_guide_dismissed")
        private val KEY_BUDDHA_INTRO_SHOWN = booleanPreferencesKey("buddha_intro_shown")
        private val KEY_FIRST_JOURNAL_HINT_SHOWN = booleanPreferencesKey("first_journal_hint_shown")
        private val KEY_FIRST_QUOTE_HINT_SHOWN = booleanPreferencesKey("first_quote_hint_shown")
        private val KEY_AI_CUSTOMIZATION_HINT_SHOWN = booleanPreferencesKey("ai_customization_hint_shown")
        private val KEY_DAILY_WISDOM_HINT_SHOWN = booleanPreferencesKey("daily_wisdom_hint_shown")
        private val KEY_PATTERN_TRACKING_HINT_SHOWN = booleanPreferencesKey("pattern_tracking_hint_shown")
        private val KEY_MESSAGE_HELPER_HINT_SHOWN = booleanPreferencesKey("message_helper_hint_shown")
        private val KEY_ALL_HINTS_DISABLED = booleanPreferencesKey("all_hints_disabled")
    }

    private val dataStore = context.aiOnboardingDataStore

    /**
     * Check if Buddha Guide intro should be shown (first-time users)
     */
    fun shouldShowBuddhaGuide(): Flow<Boolean> {
        return dataStore.data.map { prefs ->
            val shown = prefs[KEY_BUDDHA_GUIDE_SHOWN] == true
            val dismissed = prefs[KEY_BUDDHA_GUIDE_DISMISSED] == true
            !shown && !dismissed
        }
    }

    /**
     * Mark Buddha Guide as shown (user completed it)
     */
    suspend fun markBuddhaGuideShown() {
        dataStore.edit { prefs ->
            prefs[KEY_BUDDHA_GUIDE_SHOWN] = true
        }
    }

    /**
     * Dismiss Buddha Guide forever (user chose "Don't show again")
     */
    suspend fun dismissBuddhaGuideForever() {
        dataStore.edit { prefs ->
            prefs[KEY_BUDDHA_GUIDE_DISMISSED] = true
        }
    }

    /**
     * Check if a specific hint should be shown
     */
    fun shouldShowHint(hintType: AiHintType): Flow<Boolean> {
        return dataStore.data.map { prefs ->
            if (prefs[KEY_ALL_HINTS_DISABLED] == true) {
                false
            } else {
                val key = getKeyForHint(hintType)
                prefs[key] != true
            }
        }
    }

    /**
     * Mark a hint as shown (won't show again)
     */
    suspend fun markHintShown(hintType: AiHintType) {
        dataStore.edit { prefs ->
            prefs[getKeyForHint(hintType)] = true
        }
    }

    /**
     * Disable all AI hints (user preference)
     */
    suspend fun disableAllHints() {
        dataStore.edit { prefs ->
            prefs[KEY_ALL_HINTS_DISABLED] = true
        }
    }

    /**
     * Re-enable all AI hints
     */
    suspend fun enableAllHints() {
        dataStore.edit { prefs ->
            prefs[KEY_ALL_HINTS_DISABLED] = false
        }
    }

    /**
     * Reset all hints (show them again)
     */
    suspend fun resetAllHints() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    /**
     * Check if all hints are disabled
     */
    fun areHintsDisabled(): Flow<Boolean> {
        return dataStore.data.map { prefs ->
            prefs[KEY_ALL_HINTS_DISABLED] == true
        }
    }

    /**
     * Get the hint content for a specific type
     */
    fun getHintContent(hintType: AiHintType): AiHint {
        return when (hintType) {
            AiHintType.BUDDHA_INTRO -> AiHint(
                type = hintType,
                title = "Meet Buddha",
                message = "Buddha is your AI companion for mindful reflection. He'll help you gain insights from your journal entries and provide personalized wisdom.",
                actionLabel = "Got it"
            )
            AiHintType.FIRST_JOURNAL_INSIGHT -> AiHint(
                type = hintType,
                title = "AI-Powered Insights",
                message = "Buddha will analyze your journal for insights. Your data stays private and is only processed locally on your device.",
                actionLabel = "Understood"
            )
            AiHintType.FIRST_QUOTE_EXPLANATION -> AiHint(
                type = hintType,
                title = "Explore Deeper",
                message = "Tap 'Explain' to see Buddha's interpretation of this wisdom. Each explanation is personalized to help you reflect.",
                actionLabel = "Try it"
            )
            AiHintType.AI_CUSTOMIZATION_TIP -> AiHint(
                type = hintType,
                title = "Customize Your Guide",
                message = "Buddha can be more or less direct based on your preference. Adjust in Settings → Buddha AI.",
                actionLabel = "Open Settings"
            )
            AiHintType.DAILY_WISDOM_TIP -> AiHint(
                type = hintType,
                title = "Daily Wisdom",
                message = "Buddha generates personalized wisdom based on your recent reflections. Check back daily for new insights.",
                actionLabel = "Great"
            )
            AiHintType.PATTERN_TRACKING_TIP -> AiHint(
                type = hintType,
                title = "Pattern Recognition",
                message = "Buddha tracks patterns in your moods and themes over time. View your weekly insights in the Stats section.",
                actionLabel = "View Stats"
            )
            AiHintType.MESSAGE_HELPER_TIP -> AiHint(
                type = hintType,
                title = "Writing Helper",
                message = "Need help starting your message to your future self? Buddha can suggest prompts based on your current mood.",
                actionLabel = "Try it"
            )
        }
    }

    /**
     * Get all Buddha Guide intro cards (max 3)
     */
    fun getBuddhaGuideCards(): List<BuddhaGuideCard> {
        return listOf(
            BuddhaGuideCard(
                title = "AI-Powered Journaling",
                description = "Buddha analyzes your entries to surface emotional patterns and provide meaningful insights.",
                iconName = "auto_awesome"
            ),
            BuddhaGuideCard(
                title = "Privacy First",
                description = "Your journal data never leaves your device. All AI processing is done locally and securely.",
                iconName = "lock"
            ),
            BuddhaGuideCard(
                title = "Personalized Wisdom",
                description = "Get quotes, proverbs, and insights tailored to your current mindset and emotional state.",
                iconName = "psychology"
            )
        )
    }

    private fun getKeyForHint(hintType: AiHintType): Preferences.Key<Boolean> {
        return when (hintType) {
            AiHintType.BUDDHA_INTRO -> KEY_BUDDHA_INTRO_SHOWN
            AiHintType.FIRST_JOURNAL_INSIGHT -> KEY_FIRST_JOURNAL_HINT_SHOWN
            AiHintType.FIRST_QUOTE_EXPLANATION -> KEY_FIRST_QUOTE_HINT_SHOWN
            AiHintType.AI_CUSTOMIZATION_TIP -> KEY_AI_CUSTOMIZATION_HINT_SHOWN
            AiHintType.DAILY_WISDOM_TIP -> KEY_DAILY_WISDOM_HINT_SHOWN
            AiHintType.PATTERN_TRACKING_TIP -> KEY_PATTERN_TRACKING_HINT_SHOWN
            AiHintType.MESSAGE_HELPER_TIP -> KEY_MESSAGE_HELPER_HINT_SHOWN
        }
    }
}

/**
 * Data class for Buddha Guide intro cards
 */
data class BuddhaGuideCard(
    val title: String,
    val description: String,
    val iconName: String
)
