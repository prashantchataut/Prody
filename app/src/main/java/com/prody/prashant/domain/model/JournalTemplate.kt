package com.prody.prashant.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.prody.prashant.ui.theme.*

/**
 * Represents a journal entry template with guided prompts.
 */
data class JournalTemplate(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val prompts: List<String>,
    val placeholderText: String
) {
    companion object {
        /**
         * Returns all available journal templates.
         */
        fun all(): List<JournalTemplate> = listOf(
            gratitude(),
            reflection(),
            goalSetting(),
            morningPages(),
            eveningReview(),
            problemSolving(),
            creativeBrainstorm(),
            selfCompassion(),
            habitTracker(),
            weeklyReview()
        )

        /**
         * Gratitude template - Focus on thankfulness.
         */
        fun gratitude() = JournalTemplate(
            id = "gratitude",
            name = "Gratitude Journal",
            description = "Cultivate positivity by reflecting on what you're thankful for",
            icon = Icons.Filled.Favorite,
            color = MoodHappy,
            prompts = listOf(
                "Three things I'm grateful for today:",
                "1. ",
                "2. ",
                "3. ",
                "",
                "Why these matter to me:",
                "",
                "One person I appreciate and why:",
                "",
                "A simple pleasure I enjoyed today:"
            ),
            placeholderText = "Start your gratitude practice..."
        )

        /**
         * Daily reflection template.
         */
        fun reflection() = JournalTemplate(
            id = "reflection",
            name = "Daily Reflection",
            description = "Review your day with structured prompts",
            icon = Icons.Filled.Psychology,
            color = ProdyPrimary,
            prompts = listOf(
                "What went well today?",
                "",
                "What could have gone better?",
                "",
                "What did I learn?",
                "",
                "How did I grow as a person?",
                "",
                "One thing I'll do differently tomorrow:"
            ),
            placeholderText = "Reflect on your day..."
        )

        /**
         * Goal setting template.
         */
        fun goalSetting() = JournalTemplate(
            id = "goals",
            name = "Goal Setting",
            description = "Define and track your personal goals",
            icon = Icons.Filled.Flag,
            color = AchievementUnlocked,
            prompts = listOf(
                "My main goal for this period:",
                "",
                "Why is this goal important to me?",
                "",
                "Three steps I can take toward this goal:",
                "1. ",
                "2. ",
                "3. ",
                "",
                "Potential obstacles and how I'll overcome them:",
                "",
                "How I'll celebrate when I achieve this:"
            ),
            placeholderText = "Define your goals..."
        )

        /**
         * Morning pages - stream of consciousness writing.
         */
        fun morningPages() = JournalTemplate(
            id = "morning",
            name = "Morning Pages",
            description = "Start your day with free-flowing thoughts",
            icon = Icons.Filled.WbSunny,
            color = MoodEnergetic,
            prompts = listOf(
                "Good morning! Today I'm feeling...",
                "",
                "My intention for today is...",
                "",
                "What's on my mind right now...",
                "",
                "Three priorities for today:",
                "1. ",
                "2. ",
                "3. ",
                "",
                "I'm looking forward to..."
            ),
            placeholderText = "Welcome the new day..."
        )

        /**
         * Evening review template.
         */
        fun eveningReview() = JournalTemplate(
            id = "evening",
            name = "Evening Wind-Down",
            description = "Process your day before rest",
            icon = Icons.Filled.NightsStay,
            color = MoodCalm,
            prompts = listOf(
                "How am I feeling at the end of this day?",
                "",
                "The best moment of today was...",
                "",
                "Something that challenged me:",
                "",
                "How I handled it:",
                "",
                "One thing I'm proud of today:",
                "",
                "Tomorrow I will...",
                "",
                "I'm grateful for..."
            ),
            placeholderText = "Wind down your day..."
        )

        /**
         * Problem solving template.
         */
        fun problemSolving() = JournalTemplate(
            id = "problem",
            name = "Problem Solving",
            description = "Work through challenges systematically",
            icon = Icons.Filled.Lightbulb,
            color = MoodAnxious,
            prompts = listOf(
                "The challenge I'm facing:",
                "",
                "Why is this bothering me?",
                "",
                "What have I tried so far?",
                "",
                "Possible solutions:",
                "1. ",
                "2. ",
                "3. ",
                "",
                "Pros and cons of each:",
                "",
                "My chosen approach:",
                "",
                "First step I'll take:"
            ),
            placeholderText = "Work through your challenge..."
        )

        /**
         * Creative brainstorming template.
         */
        fun creativeBrainstorm() = JournalTemplate(
            id = "creative",
            name = "Creative Brainstorm",
            description = "Explore ideas freely without judgment",
            icon = Icons.Filled.AutoAwesome,
            color = MoodInspired,
            prompts = listOf(
                "Topic/Project I'm exploring:",
                "",
                "Wild ideas (no judgment!):",
                "- ",
                "- ",
                "- ",
                "- ",
                "",
                "Connections I see between ideas:",
                "",
                "What excites me most:",
                "",
                "Next creative step:"
            ),
            placeholderText = "Let your creativity flow..."
        )

        /**
         * Self-compassion template.
         */
        fun selfCompassion() = JournalTemplate(
            id = "compassion",
            name = "Self-Compassion",
            description = "Practice kindness toward yourself",
            icon = Icons.Filled.SelfImprovement,
            color = MoodSad,
            prompts = listOf(
                "Something difficult I'm experiencing:",
                "",
                "How this makes me feel:",
                "",
                "What I would tell a friend in this situation:",
                "",
                "Acknowledging my humanity (everyone struggles):",
                "",
                "One kind thing I can do for myself today:",
                "",
                "A gentle reminder to myself:"
            ),
            placeholderText = "Be kind to yourself..."
        )

        /**
         * Habit tracker journal.
         */
        fun habitTracker() = JournalTemplate(
            id = "habits",
            name = "Habit Check-In",
            description = "Track and reflect on your daily habits",
            icon = Icons.Filled.CheckCircle,
            color = StreakColor,
            prompts = listOf(
                "Habits I completed today:",
                "[ ] ",
                "[ ] ",
                "[ ] ",
                "",
                "What helped me stay on track:",
                "",
                "What got in the way:",
                "",
                "How I feel about my progress:",
                "",
                "Tomorrow I will focus on:"
            ),
            placeholderText = "Track your habits..."
        )

        /**
         * Weekly review template.
         */
        fun weeklyReview() = JournalTemplate(
            id = "weekly",
            name = "Weekly Review",
            description = "Comprehensive review of your week",
            icon = Icons.Filled.CalendarMonth,
            color = ProdySecondary,
            prompts = listOf(
                "Highlight of this week:",
                "",
                "Challenges I faced:",
                "",
                "What I learned this week:",
                "",
                "Progress toward my goals:",
                "",
                "What I'm leaving behind:",
                "",
                "My focus for next week:",
                "",
                "Three wins, big or small:",
                "1. ",
                "2. ",
                "3. "
            ),
            placeholderText = "Review your week..."
        )

        /**
         * Get template by ID.
         */
        fun getById(id: String): JournalTemplate? = all().find { it.id == id }
    }
}
