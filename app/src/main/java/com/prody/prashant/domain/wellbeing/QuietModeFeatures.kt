package com.prody.prashant.domain.wellbeing

/**
 * QuietModeFeatures - Defines what features are shown or hidden in Quiet Mode
 *
 * This object serves as the source of truth for what changes when Quiet Mode is active.
 * It's designed to be easily referenced by UI components throughout the app.
 *
 * Philosophy:
 * - Hide everything that creates pressure or comparison
 * - Keep everything that supports genuine reflection
 * - The app becomes a safe, simple space for self-care
 */
object QuietModeFeatures {

    /**
     * Features that should be HIDDEN in Quiet Mode.
     * These create pressure, competition, or visual complexity.
     */
    object Hidden {
        // Gamification elements
        const val STREAKS = true
        const val XP_POINTS = true
        const val LEVEL_DISPLAY = true
        const val SKILLS = true
        const val ACHIEVEMENTS = true
        const val ACHIEVEMENT_NOTIFICATIONS = true
        const val LEADERBOARD = true
        const val MISSIONS = true
        const val CHALLENGES = true
        const val RANK_DISPLAY = true
        const val BADGES = true
        const val TITLES = true
        const val COSMETICS = true

        // Notifications and popups
        const val ACHIEVEMENT_POPUPS = true
        const val LEVEL_UP_CELEBRATIONS = true
        const val STREAK_CELEBRATIONS = true
        const val WEEKLY_SUMMARY_POPUP = true
        const val MILESTONE_POPUPS = true

        // Visual complexity
        const val COMPLEX_ANIMATIONS = true
        const val CELEBRATION_EFFECTS = true
        const val PARTICLE_EFFECTS = true
        const val GRADIENT_BACKGROUNDS = true

        // Suggestions and AI features (optional - can be too stimulating)
        const val WISDOM_SUGGESTIONS_IN_JOURNAL = true
        const val AI_JOURNAL_INSIGHTS = true // Still generated, just not shown prominently
        const val BUDDHA_PROACTIVE_MESSAGES = true

        // Profile decorations
        const val PROFILE_BANNER = true
        const val PROFILE_FRAMES = true
        const val AVATAR_DECORATIONS = true
        const val TROPHY_SHELF = true

        // Stats that create pressure
        const val DETAILED_STATS = true // Hide complex analytics
        const val COMPARISON_STATS = true // Hide comparison with others
        const val GOAL_PRESSURE_INDICATORS = true
    }

    /**
     * Features that should be KEPT (visible) in Quiet Mode.
     * These support genuine wellbeing and reflection.
     */
    object Visible {
        // Core journaling
        const val JOURNAL_ENTRY = true
        const val JOURNAL_HISTORY = true
        const val MOOD_TRACKING = true
        const val JOURNAL_SEARCH = true
        const val JOURNAL_TAGS = true
        const val JOURNAL_BOOKMARKS = true

        // Reflection tools
        const val DAILY_WISDOM = true // Single quote, simplified
        const val FUTURE_MESSAGES = true
        const val MEDITATION_TIMER = true
        const val GRATITUDE_PROMPTS = true

        // Optional AI (if user has it enabled)
        const val BUDDHA_THERAPIST = true // 1-on-1 support
        const val HAVEN_AI = true // Safe space for venting

        // Navigation
        const val BASIC_NAVIGATION = true
        const val SETTINGS = true
        const val PROFILE_BASICS = true // Name, avatar only

        // Simple stats
        const val ENTRY_COUNT = true // Just number of entries
        const val DAYS_JOURNALING = true // Simple count

        // Essential features
        const val NOTIFICATIONS_SETTING = true
        const val REMINDERS = true
        const val PRIVACY_LOCK = true
        const val BACKUP_EXPORT = true
    }

    /**
     * UI Treatment in Quiet Mode.
     * How the visual design changes.
     */
    object UITreatment {
        const val USE_MUTED_COLORS = true
        const val SOFTER_CORNERS = true
        const val INCREASED_SPACING = true
        const val SIMPLIFIED_TYPOGRAPHY = true
        const val MINIMAL_ANIMATIONS = true
        const val NO_SHADOWS = true // Already true in flat design
        const val CALMER_CONTRAST = true
        const val REDUCED_VISUAL_WEIGHT = true
    }

    /**
     * Checks if a specific feature should be shown based on Quiet Mode state.
     *
     * @param feature The feature to check
     * @param isQuietModeActive Whether Quiet Mode is currently active
     * @return true if the feature should be visible
     */
    fun shouldShowFeature(feature: Feature, isQuietModeActive: Boolean): Boolean {
        return if (isQuietModeActive) {
            // In Quiet Mode, only show features marked as Visible
            when (feature) {
                Feature.JOURNAL_ENTRY -> Visible.JOURNAL_ENTRY
                Feature.JOURNAL_HISTORY -> Visible.JOURNAL_HISTORY
                Feature.MOOD_TRACKING -> Visible.MOOD_TRACKING
                Feature.DAILY_WISDOM -> Visible.DAILY_WISDOM
                Feature.FUTURE_MESSAGES -> Visible.FUTURE_MESSAGES
                Feature.BUDDHA_THERAPIST -> Visible.BUDDHA_THERAPIST
                Feature.HAVEN_AI -> Visible.HAVEN_AI

                Feature.STREAKS -> !Hidden.STREAKS
                Feature.XP_POINTS -> !Hidden.XP_POINTS
                Feature.LEVEL_DISPLAY -> !Hidden.LEVEL_DISPLAY
                Feature.SKILLS -> !Hidden.SKILLS
                Feature.ACHIEVEMENTS -> !Hidden.ACHIEVEMENTS
                Feature.LEADERBOARD -> !Hidden.LEADERBOARD
                Feature.MISSIONS -> !Hidden.MISSIONS
                Feature.CHALLENGES -> !Hidden.CHALLENGES
                Feature.BADGES -> !Hidden.BADGES
                Feature.DETAILED_STATS -> !Hidden.DETAILED_STATS
                Feature.PROFILE_DECORATIONS -> !Hidden.PROFILE_BANNER
                Feature.ACHIEVEMENT_NOTIFICATIONS -> !Hidden.ACHIEVEMENT_NOTIFICATIONS
                Feature.WISDOM_SUGGESTIONS -> !Hidden.WISDOM_SUGGESTIONS_IN_JOURNAL
                Feature.AI_INSIGHTS -> !Hidden.AI_JOURNAL_INSIGHTS
            }
        } else {
            // Not in Quiet Mode, show everything
            true
        }
    }

    /**
     * Gets a user-friendly explanation of what Quiet Mode does.
     */
    fun getExplanation(): String {
        return """
            Quiet Mode simplifies Prody to help you focus on what matters most: your wellbeing.

            Hidden:
            • Streaks, XP, and achievements
            • Leaderboard and comparisons
            • Complex stats and graphs
            • Celebration animations
            • Profile decorations

            Kept:
            • Your journal and all entries
            • Mood tracking
            • Daily wisdom
            • Future messages
            • AI support (if enabled)

            The app becomes a calm, simple space just for you.
        """.trimIndent()
    }
}

/**
 * Enum representing different app features that can be toggled.
 */
enum class Feature {
    // Core features (always visible in Quiet Mode)
    JOURNAL_ENTRY,
    JOURNAL_HISTORY,
    MOOD_TRACKING,
    DAILY_WISDOM,
    FUTURE_MESSAGES,
    BUDDHA_THERAPIST,
    HAVEN_AI,

    // Gamification features (hidden in Quiet Mode)
    STREAKS,
    XP_POINTS,
    LEVEL_DISPLAY,
    SKILLS,
    ACHIEVEMENTS,
    LEADERBOARD,
    MISSIONS,
    CHALLENGES,
    BADGES,

    // Stats and analytics
    DETAILED_STATS,

    // Profile elements
    PROFILE_DECORATIONS,

    // Notifications and suggestions
    ACHIEVEMENT_NOTIFICATIONS,
    WISDOM_SUGGESTIONS,
    AI_INSIGHTS
}
