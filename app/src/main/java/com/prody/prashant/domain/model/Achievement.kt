package com.prody.prashant.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.prody.prashant.ui.theme.*

enum class AchievementRarity(val color: Color, val displayName: String) {
    COMMON(Color(0xFF9E9E9E), "Common"),
    UNCOMMON(Color(0xFF4CAF50), "Uncommon"),
    RARE(Color(0xFF2196F3), "Rare"),
    EPIC(Color(0xFF9C27B0), "Epic"),
    LEGENDARY(Color(0xFFFF9800), "Legendary")
}

enum class AchievementCategory {
    STREAK, LEARNING, JOURNAL, SOCIAL, SPECIAL
}

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val category: AchievementCategory,
    val rarity: AchievementRarity,
    val requirement: Int,
    val currentProgress: Int = 0,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val rewardPoints: Int = 100
) {
    val progressPercentage: Float
        get() = if (requirement > 0) (currentProgress.toFloat() / requirement).coerceIn(0f, 1f) else 0f
}

object Achievements {
    val allAchievements = listOf(
        // Streak Achievements
        Achievement(
            id = "streak_3",
            name = "Getting Started",
            description = "Maintain a 3-day streak",
            icon = Icons.Filled.LocalFireDepartment,
            category = AchievementCategory.STREAK,
            rarity = AchievementRarity.COMMON,
            requirement = 3,
            rewardPoints = 50
        ),
        Achievement(
            id = "streak_7",
            name = "Week Warrior",
            description = "Maintain a 7-day streak",
            icon = Icons.Filled.Whatshot,
            category = AchievementCategory.STREAK,
            rarity = AchievementRarity.UNCOMMON,
            requirement = 7,
            rewardPoints = 150
        ),
        Achievement(
            id = "streak_30",
            name = "Monthly Master",
            description = "Maintain a 30-day streak",
            icon = Icons.Filled.EmojiEvents,
            category = AchievementCategory.STREAK,
            rarity = AchievementRarity.RARE,
            requirement = 30,
            rewardPoints = 500
        ),
        Achievement(
            id = "streak_100",
            name = "Century Champion",
            description = "Maintain a 100-day streak",
            icon = Icons.Filled.MilitaryTech,
            category = AchievementCategory.STREAK,
            rarity = AchievementRarity.EPIC,
            requirement = 100,
            rewardPoints = 1500
        ),
        Achievement(
            id = "streak_365",
            name = "Year of Growth",
            description = "Maintain a 365-day streak",
            icon = Icons.Filled.AutoAwesome,
            category = AchievementCategory.STREAK,
            rarity = AchievementRarity.LEGENDARY,
            requirement = 365,
            rewardPoints = 5000
        ),

        // Learning Achievements
        Achievement(
            id = "words_10",
            name = "Word Explorer",
            description = "Learn 10 new words",
            icon = Icons.Filled.MenuBook,
            category = AchievementCategory.LEARNING,
            rarity = AchievementRarity.COMMON,
            requirement = 10,
            rewardPoints = 50
        ),
        Achievement(
            id = "words_50",
            name = "Vocabulary Builder",
            description = "Learn 50 new words",
            icon = Icons.Filled.School,
            category = AchievementCategory.LEARNING,
            rarity = AchievementRarity.UNCOMMON,
            requirement = 50,
            rewardPoints = 200
        ),
        Achievement(
            id = "words_100",
            name = "Word Master",
            description = "Learn 100 new words",
            icon = Icons.Filled.Psychology,
            category = AchievementCategory.LEARNING,
            rarity = AchievementRarity.RARE,
            requirement = 100,
            rewardPoints = 500
        ),
        Achievement(
            id = "words_500",
            name = "Lexicon Legend",
            description = "Learn 500 new words",
            icon = Icons.Filled.WorkspacePremium,
            category = AchievementCategory.LEARNING,
            rarity = AchievementRarity.LEGENDARY,
            requirement = 500,
            rewardPoints = 2500
        ),

        // Journal Achievements
        Achievement(
            id = "journal_1",
            name = "First Thoughts",
            description = "Write your first journal entry",
            icon = Icons.Filled.Create,
            category = AchievementCategory.JOURNAL,
            rarity = AchievementRarity.COMMON,
            requirement = 1,
            rewardPoints = 25
        ),
        Achievement(
            id = "journal_10",
            name = "Thoughtful Mind",
            description = "Write 10 journal entries",
            icon = Icons.Filled.EditNote,
            category = AchievementCategory.JOURNAL,
            rarity = AchievementRarity.UNCOMMON,
            requirement = 10,
            rewardPoints = 150
        ),
        Achievement(
            id = "journal_50",
            name = "Reflective Soul",
            description = "Write 50 journal entries",
            icon = Icons.Filled.AutoStories,
            category = AchievementCategory.JOURNAL,
            rarity = AchievementRarity.RARE,
            requirement = 50,
            rewardPoints = 500
        ),
        Achievement(
            id = "journal_100",
            name = "Chronicle Keeper",
            description = "Write 100 journal entries",
            icon = Icons.Filled.HistoryEdu,
            category = AchievementCategory.JOURNAL,
            rarity = AchievementRarity.EPIC,
            requirement = 100,
            rewardPoints = 1000
        ),

        // Future Message Achievements
        Achievement(
            id = "future_1",
            name = "Time Traveler",
            description = "Send your first message to future self",
            icon = Icons.Filled.Schedule,
            category = AchievementCategory.SPECIAL,
            rarity = AchievementRarity.COMMON,
            requirement = 1,
            rewardPoints = 50
        ),
        Achievement(
            id = "future_10",
            name = "Future Connector",
            description = "Send 10 messages to future self",
            icon = Icons.Filled.Mail,
            category = AchievementCategory.SPECIAL,
            rarity = AchievementRarity.UNCOMMON,
            requirement = 10,
            rewardPoints = 200
        ),
        Achievement(
            id = "future_received",
            name = "Message Received",
            description = "Receive your first message from past self",
            icon = Icons.Filled.MarkEmailRead,
            category = AchievementCategory.SPECIAL,
            rarity = AchievementRarity.RARE,
            requirement = 1,
            rewardPoints = 300
        ),

        // Social Achievements
        Achievement(
            id = "boost_10",
            name = "Encourager",
            description = "Boost 10 peers",
            icon = Icons.Filled.ThumbUp,
            category = AchievementCategory.SOCIAL,
            rarity = AchievementRarity.UNCOMMON,
            requirement = 10,
            rewardPoints = 150
        ),
        Achievement(
            id = "top_weekly",
            name = "Weekly Champion",
            description = "Reach #1 on weekly leaderboard",
            icon = Icons.Filled.Leaderboard,
            category = AchievementCategory.SOCIAL,
            rarity = AchievementRarity.EPIC,
            requirement = 1,
            rewardPoints = 1000
        ),

        // Special Achievements
        Achievement(
            id = "night_owl",
            name = "Night Owl",
            description = "Journal after midnight",
            icon = Icons.Filled.NightsStay,
            category = AchievementCategory.SPECIAL,
            rarity = AchievementRarity.UNCOMMON,
            requirement = 1,
            rewardPoints = 100
        ),
        Achievement(
            id = "early_bird",
            name = "Early Bird",
            description = "Journal before 6 AM",
            icon = Icons.Filled.WbSunny,
            category = AchievementCategory.SPECIAL,
            rarity = AchievementRarity.UNCOMMON,
            requirement = 1,
            rewardPoints = 100
        ),
        Achievement(
            id = "all_moods",
            name = "Emotional Range",
            description = "Journal with all different moods",
            icon = Icons.Filled.Mood,
            category = AchievementCategory.SPECIAL,
            rarity = AchievementRarity.RARE,
            requirement = 8, // Number of moods
            rewardPoints = 400
        )
    )

    fun getAchievementById(id: String): Achievement? {
        return allAchievements.find { it.id == id }
    }

    fun getAchievementsByCategory(category: AchievementCategory): List<Achievement> {
        return allAchievements.filter { it.category == category }
    }
}
