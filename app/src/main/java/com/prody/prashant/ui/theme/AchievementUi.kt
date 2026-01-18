package com.prody.prashant.ui.theme
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Foundation
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Moving
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.PsychologyAlt
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.prody.prashant.domain.identity.ProdyAchievements

/**
 * [AchievementUi] - UI layer achievement models with Compose types
 *
 * This file provides the bridge between the domain-layer achievement system
 * (ProdyAchievements) and the UI layer, adding Compose-specific types like
 * Color and ImageVector that cannot exist in the domain layer.
 *
 * The UI layer models wrap domain models and add visual properties needed
 * for rendering achievements in Jetpack Compose.
 */

/**
 * UI model for achievement rarity with visual properties.
 * Maps to ProdyAchievements.Rarity with added Compose Color support.
 *
 * Renamed from AchievementRarity to avoid collision with
 * com.prody.prashant.domain.gamification.AchievementRarity.
 *
 * @property color Primary display color for this rarity
 * @property secondaryColor Secondary/gradient color for this rarity
 * @property displayName User-facing rarity name
 * @property glowIntensity Visual glow effect intensity (0.0 to 1.0)
 */
enum class UiAchievementRarity(
    val color: Color,
    val secondaryColor: Color,
    val displayName: String,
    val glowIntensity: Float
) {
    COMMON(
        color = Color(0xFF78909C),
        secondaryColor = Color(0xFF90A4AE),
        displayName = "Common",
        glowIntensity = 0f
    ),
    UNCOMMON(
        color = Color(0xFF4CAF50),
        secondaryColor = Color(0xFF81C784),
        displayName = "Uncommon",
        glowIntensity = 0.2f
    ),
    RARE(
        color = Color(0xFF2196F3),
        secondaryColor = Color(0xFF64B5F6),
        displayName = "Rare",
        glowIntensity = 0.4f
    ),
    EPIC(
        color = Color(0xFF9C27B0),
        secondaryColor = Color(0xFFBA68C8),
        displayName = "Epic",
        glowIntensity = 0.6f
    ),
    LEGENDARY(
        color = Color(0xFFD4AF37),
        secondaryColor = Color(0xFFF4D03F),
        displayName = "Legendary",
        glowIntensity = 0.85f
    );

    /**
     * Gets gradient colors for this rarity.
     */
    val gradientColors: List<Color>
        get() = listOf(color, secondaryColor)

    companion object {
        /**
         * Converts domain rarity to UI rarity.
         */
        fun fromDomain(domainRarity: ProdyAchievements.Rarity): UiAchievementRarity {
            return when (domainRarity) {
                ProdyAchievements.Rarity.COMMON -> COMMON
                ProdyAchievements.Rarity.UNCOMMON -> UNCOMMON
                ProdyAchievements.Rarity.RARE -> RARE
                ProdyAchievements.Rarity.EPIC -> EPIC
                ProdyAchievements.Rarity.LEGENDARY -> LEGENDARY
            }
        }
    }
}

/**
 * Categories of achievements with visual properties.
 * Maps to ProdyAchievements.Category with added Compose Color and Icon support.
 *
 * Renamed from AchievementCategory to avoid collision with
 * com.prody.prashant.domain.gamification.AchievementCategory.
 *
 * @property displayName User-facing category name
 * @property description Category description
 * @property color Primary color for this category
 * @property secondaryColor Secondary/gradient color
 * @property icon Default icon for this category
 */
enum class UiAchievementCategory(
    val displayName: String,
    val description: String,
    val color: Color,
    val secondaryColor: Color,
    val icon: ImageVector
) {
    WISDOM(
        displayName = "Wisdom",
        description = "Achievements from learning words, quotes, and proverbs",
        color = Color(0xFF6B5CE7),
        secondaryColor = Color(0xFF9B8AFF),
        icon = ProdyIcons.AutoStories
    ),
    REFLECTION(
        displayName = "Reflection",
        description = "Achievements from journaling and self-examination",
        color = Color(0xFF3AAFA9),
        secondaryColor = Color(0xFF6FD5CE),
        icon = ProdyIcons.Psychology
    ),
    CONSISTENCY(
        displayName = "Consistency",
        description = "Achievements from maintaining streaks and habits",
        color = Color(0xFFFF6B6B),
        secondaryColor = Color(0xFFFFAB76),
        icon = ProdyIcons.LocalFireDepartment
    ),
    PRESENCE(
        displayName = "Presence",
        description = "Achievements from engaging with Buddha and mindfulness",
        color = Color(0xFF4ECDC4),
        secondaryColor = Color(0xFF44B09E),
        icon = ProdyIcons.SelfImprovement
    ),
    TEMPORAL(
        displayName = "Temporal",
        description = "Achievements from future-self letters and time awareness",
        color = Color(0xFF667EEA),
        secondaryColor = Color(0xFF764BA2),
        icon = ProdyIcons.Schedule
    ),
    MASTERY(
        displayName = "Mastery",
        description = "Special achievements for exceptional dedication",
        color = Color(0xFFD4AF37),
        secondaryColor = Color(0xFFF4D03F),
        icon = ProdyIcons.WorkspacePremium
    ),

    // Legacy categories for backwards compatibility
    STREAK(
        displayName = "Streak",
        description = "Consistency achievements",
        color = Color(0xFFFF6B6B),
        secondaryColor = Color(0xFFFFAB76),
        icon = ProdyIcons.LocalFireDepartment
    ),
    LEARNING(
        displayName = "Learning",
        description = "Vocabulary achievements",
        color = Color(0xFF6B5CE7),
        secondaryColor = Color(0xFF9B8AFF),
        icon = ProdyIcons.MenuBook
    ),
    JOURNAL(
        displayName = "Journal",
        description = "Journaling achievements",
        color = Color(0xFF3AAFA9),
        secondaryColor = Color(0xFF6FD5CE),
        icon = ProdyIcons.EditNote
    ),
    SOCIAL(
        displayName = "Social",
        description = "Social engagement achievements",
        color = Color(0xFF4ECDC4),
        secondaryColor = Color(0xFF44B09E),
        icon = ProdyIcons.ThumbUp
    ),
    SPECIAL(
        displayName = "Special",
        description = "Special achievements",
        color = Color(0xFF667EEA),
        secondaryColor = Color(0xFF764BA2),
        icon = ProdyIcons.AutoAwesome
    ),
    EXPLORER(
        displayName = "Explorer",
        description = "Exploration achievements",
        color = Color(0xFF00BCD4),
        secondaryColor = Color(0xFF4DD0E1),
        icon = ProdyIcons.Explore
    );

    /**
     * Gets gradient colors for this category.
     */
    val gradientColors: List<Color>
        get() = listOf(color, secondaryColor)

    companion object {
        /**
         * Converts domain category to UI category.
         */
        fun fromDomain(domainCategory: ProdyAchievements.Category): UiAchievementCategory {
            return when (domainCategory) {
                ProdyAchievements.Category.WISDOM -> WISDOM
                ProdyAchievements.Category.REFLECTION -> REFLECTION
                ProdyAchievements.Category.CONSISTENCY -> CONSISTENCY
                ProdyAchievements.Category.PRESENCE -> PRESENCE
                ProdyAchievements.Category.TEMPORAL -> TEMPORAL
                ProdyAchievements.Category.MASTERY -> MASTERY
                ProdyAchievements.Category.SOCIAL -> SOCIAL
                ProdyAchievements.Category.EXPLORER -> EXPLORER
            }
        }
    }
}

/**
 * UI model for achievements with visual properties.
 *
 * This is placed in the ui.theme package because it contains Compose types
 * (Color, ImageVector), keeping Compose dependencies out of the domain layer.
 *
 * Renamed from Achievement to avoid collision with
 * com.prody.prashant.domain.gamification.Achievement.
 *
 * @property id Unique identifier for persistence and lookup
 * @property name Achievement name displayed to users
 * @property description Philosophical or poetic description
 * @property icon Material icon for visual representation
 * @property category The category this achievement belongs to
 * @property rarity The rarity level determining visual treatment
 * @property requirement Numeric threshold to unlock this achievement
 * @property requirementDescription Human-readable requirement explanation
 * @property celebrationMessage Elegant message shown when unlocked
 * @property currentProgress Current user progress toward this achievement
 * @property isUnlocked Whether the user has unlocked this achievement
 * @property unlockedAt Timestamp when achievement was unlocked
 * @property rewardPoints Points awarded for unlocking this achievement
 */
data class UiAchievement(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val category: UiAchievementCategory,
    val rarity: UiAchievementRarity,
    val requirement: Int,
    val requirementDescription: String = "",
    val celebrationMessage: String = "",
    val currentProgress: Int = 0,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val rewardPoints: Int = 100
) {
    /**
     * Progress percentage toward unlocking this achievement.
     */
    val progressPercentage: Float
        get() = if (requirement > 0) (currentProgress.toFloat() / requirement).coerceIn(0f, 1f) else 0f

    /**
     * Whether this achievement is close to being unlocked (>75% progress).
     */
    val isNearCompletion: Boolean
        get() = !isUnlocked && progressPercentage >= 0.75f

    companion object {
        /**
         * Creates a UI Achievement from a domain Achievement.
         *
         * @param domainAchievement The domain-layer achievement
         * @param currentProgress Current user progress
         * @param isUnlocked Whether unlocked
         * @param unlockedAt When unlocked
         * @return UI Achievement with proper icon
         */
        fun fromDomain(
            domainAchievement: ProdyAchievements.Achievement,
            currentProgress: Int = 0,
            isUnlocked: Boolean = false,
            unlockedAt: Long? = null
        ): UiAchievement {
            return UiAchievement(
                id = domainAchievement.id,
                name = domainAchievement.name,
                description = domainAchievement.description,
                icon = AchievementIcons.getIcon(domainAchievement.iconName),
                category = UiAchievementCategory.fromDomain(domainAchievement.category),
                rarity = UiAchievementRarity.fromDomain(domainAchievement.rarity),
                requirement = domainAchievement.requirement,
                requirementDescription = domainAchievement.requirementDescription,
                celebrationMessage = domainAchievement.celebrationMessage,
                currentProgress = currentProgress,
                isUnlocked = isUnlocked,
                unlockedAt = unlockedAt,
                rewardPoints = domainAchievement.rewardPoints
            )
        }
    }
}

/**
 * Maps icon name strings to Material Icons.
 * Used to convert domain-layer icon names to Compose ImageVectors.
 */
object AchievementIcons {
    private val iconMap: Map<String, ImageVector> = mapOf(
        // General
        "lightbulb" to ProdyIcons.Lightbulb,
        "eco" to ProdyIcons.Eco,
        "construction" to ProdyIcons.Construction,
        "handyman" to ProdyIcons.Handyman,
        "menu_book" to ProdyIcons.MenuBook,
        "school" to ProdyIcons.School,
        "favorite" to ProdyIcons.Favorite,
        "stars" to ProdyIcons.Stars,
        "format_quote" to ProdyIcons.AutoStories,
        "auto_stories" to ProdyIcons.AutoStories,
        "collections_bookmark" to ProdyIcons.CollectionsBookmark,
        "explore" to ProdyIcons.Explore,
        "history_edu" to ProdyIcons.HistoryEdu,

        // Reflection
        "edit_note" to ProdyIcons.EditNote,
        "record_voice_over" to ProdyIcons.RecordVoiceOver,
        "chat" to ProdyIcons.Chat,
        "history" to ProdyIcons.History,
        "psychology" to ProdyIcons.Psychology,
        "book" to ProdyIcons.Book,
        "auto_awesome" to ProdyIcons.AutoAwesome,
        "mood" to ProdyIcons.Mood,
        "map" to ProdyIcons.Map,
        "palette" to ProdyIcons.Palette,

        // Consistency
        "whatshot" to ProdyIcons.Whatshot,
        "local_fire_department" to ProdyIcons.LocalFireDepartment,
        "bolt" to ProdyIcons.Bolt,
        "nightlight" to ProdyIcons.Nightlight,
        "dark_mode" to ProdyIcons.DarkMode,
        "park" to ProdyIcons.Park,
        "landscape" to ProdyIcons.Landscape,
        "psychology_alt" to ProdyIcons.PsychologyAlt,

        // Presence
        "forum" to ProdyIcons.Forum,
        "help_outline" to ProdyIcons.HelpOutline,
        "diversity_3" to ProdyIcons.Diversity3,
        "self_improvement" to ProdyIcons.SelfImprovement,
        "handshake" to ProdyIcons.Handshake,

        // Temporal
        "mail" to ProdyIcons.Mail,
        "send" to ProdyIcons.Send,
        "hourglass_empty" to ProdyIcons.HourglassEmpty,
        "architecture" to ProdyIcons.Architecture,
        "mark_email_read" to ProdyIcons.MarkEmailRead,
        "hearing" to ProdyIcons.Hearing,
        "schedule" to ProdyIcons.Schedule,

        // Mastery
        "wb_twilight" to ProdyIcons.WbTwilight,
        "nights_stay" to ProdyIcons.NightsStay,
        "check_circle" to ProdyIcons.CheckCircle,
        "verified" to ProdyIcons.Verified,
        "moving" to ProdyIcons.Moving,
        "trending_up" to ProdyIcons.TrendingUp,
        "terrain" to ProdyIcons.Terrain,
        "refresh" to ProdyIcons.Refresh,
        "memory" to ProdyIcons.Memory,
        "foundation" to ProdyIcons.Foundation,
        "workspace_premium" to ProdyIcons.WorkspacePremium,

        // Legacy
        "emoji_events" to ProdyIcons.EmojiEvents,
        "military_tech" to ProdyIcons.MilitaryTech,
        "create" to ProdyIcons.Create,
        "wb_sunny" to ProdyIcons.WbSunny,
        "thumb_up" to ProdyIcons.ThumbUp,
        "leaderboard" to ProdyIcons.Leaderboard
    )

    /**
     * Gets the ImageVector for a given icon name.
     * Falls back to AutoAwesome for unknown icons.
     */
    fun getIcon(iconName: String): ImageVector {
        return iconMap[iconName] ?: ProdyIcons.AutoAwesome
    }
}

/**
 * All available achievements with full visual properties.
 * Provides access to both legacy UI achievements and new domain-based achievements.
 *
 * Renamed from Achievements to avoid collision with
 * com.prody.prashant.domain.gamification.Achievements.
 */
object UiAchievements {
    /**
     * All achievements converted from domain layer with proper icons.
     */
    val allAchievements: List<UiAchievement> by lazy {
        ProdyAchievements.allAchievements.map { domainAchievement ->
            UiAchievement.fromDomain(domainAchievement)
        }
    }

    /**
     * Legacy achievements for backwards compatibility.
     * These will be gradually replaced by the domain-based achievements.
     */
    val legacyAchievements = listOf(
        // Streak Achievements
        UiAchievement(
            id = "streak_3",
            name = "Kindling",
            description = "A spark becomes a flame with patient tending",
            icon = ProdyIcons.Whatshot,
            category = UiAchievementCategory.CONSISTENCY,
            rarity = UiAchievementRarity.COMMON,
            requirement = 3,
            requirementDescription = "Maintain a 3-day streak",
            celebrationMessage = "Three days - the kindling has caught. Keep tending the flame.",
            rewardPoints = 50
        ),
        UiAchievement(
            id = "streak_7",
            name = "Steady Flame",
            description = "A week of presence - the habit takes root",
            icon = ProdyIcons.LocalFireDepartment,
            category = UiAchievementCategory.CONSISTENCY,
            rarity = UiAchievementRarity.UNCOMMON,
            requirement = 7,
            requirementDescription = "Maintain a 7-day streak",
            celebrationMessage = "Seven days without breaking - you are building something real.",
            rewardPoints = 150
        ),
        UiAchievement(
            id = "streak_30",
            name = "Moon Cycle",
            description = "A complete lunar cycle of daily practice",
            icon = ProdyIcons.Nightlight,
            category = UiAchievementCategory.CONSISTENCY,
            rarity = UiAchievementRarity.RARE,
            requirement = 30,
            requirementDescription = "Maintain a 30-day streak",
            celebrationMessage = "One moon's passage of unbroken dedication. The habit is forged.",
            rewardPoints = 500
        ),
        UiAchievement(
            id = "streak_90",
            name = "Season of Growth",
            description = "Three months - a season of transformation",
            icon = ProdyIcons.Park,
            category = UiAchievementCategory.CONSISTENCY,
            rarity = UiAchievementRarity.EPIC,
            requirement = 90,
            requirementDescription = "Maintain a 90-day streak",
            celebrationMessage = "A full season of growth. You have transformed.",
            rewardPoints = 1500
        ),
        UiAchievement(
            id = "streak_365",
            name = "Year of Presence",
            description = "365 days of showing up for yourself",
            icon = ProdyIcons.Stars,
            category = UiAchievementCategory.CONSISTENCY,
            rarity = UiAchievementRarity.LEGENDARY,
            requirement = 365,
            requirementDescription = "Maintain a 365-day streak",
            celebrationMessage = "One complete orbit around the sun, present each day. This is mastery.",
            rewardPoints = 5000
        ),

        // Learning Achievements
        UiAchievement(
            id = "words_10",
            name = "Gathering Words",
            description = "A vocabulary is a garden - you are planting seeds",
            icon = ProdyIcons.Eco,
            category = UiAchievementCategory.WISDOM,
            rarity = UiAchievementRarity.COMMON,
            requirement = 10,
            requirementDescription = "Learn 10 words",
            celebrationMessage = "Ten words now live within you, ready to bloom in thought and speech.",
            rewardPoints = 50
        ),
        UiAchievement(
            id = "words_50",
            name = "Wordsmith",
            description = "Words are tools, and you are learning your craft",
            icon = ProdyIcons.Handyman,
            category = UiAchievementCategory.WISDOM,
            rarity = UiAchievementRarity.UNCOMMON,
            requirement = 50,
            requirementDescription = "Learn 50 words",
            celebrationMessage = "Fifty words - a craftsman's toolkit begins to take shape.",
            rewardPoints = 200
        ),
        UiAchievement(
            id = "words_100",
            name = "Lexicon Keeper",
            description = "A hundred words is a language unto itself",
            icon = ProdyIcons.MenuBook,
            category = UiAchievementCategory.WISDOM,
            rarity = UiAchievementRarity.RARE,
            requirement = 100,
            requirementDescription = "Learn 100 words",
            celebrationMessage = "One hundred words now color your world with new meaning.",
            rewardPoints = 500
        ),
        UiAchievement(
            id = "words_500",
            name = "Logophile",
            description = "A true lover of words, their histories, their music",
            icon = ProdyIcons.Favorite,
            category = UiAchievementCategory.WISDOM,
            rarity = UiAchievementRarity.LEGENDARY,
            requirement = 500,
            requirementDescription = "Learn 500 words",
            celebrationMessage = "Five hundred words - you have become a keeper of language.",
            rewardPoints = 2500
        ),

        // Journal Achievements
        UiAchievement(
            id = "journal_1",
            name = "First Reflection",
            description = "The examined life begins with a single honest word",
            icon = ProdyIcons.EditNote,
            category = UiAchievementCategory.REFLECTION,
            rarity = UiAchievementRarity.COMMON,
            requirement = 1,
            requirementDescription = "Write your first journal entry",
            celebrationMessage = "You have begun the sacred practice of self-examination.",
            rewardPoints = 25
        ),
        UiAchievement(
            id = "journal_10",
            name = "Inner Dialogue",
            description = "A conversation with yourself, growing richer each day",
            icon = ProdyIcons.Chat,
            category = UiAchievementCategory.REFLECTION,
            rarity = UiAchievementRarity.UNCOMMON,
            requirement = 10,
            requirementDescription = "Write 10 journal entries",
            celebrationMessage = "Ten conversations with your deeper self - the dialogue deepens.",
            rewardPoints = 150
        ),
        UiAchievement(
            id = "journal_30",
            name = "Chronicle Keeper",
            description = "Your story is being written, one entry at a time",
            icon = ProdyIcons.AutoStories,
            category = UiAchievementCategory.REFLECTION,
            rarity = UiAchievementRarity.RARE,
            requirement = 30,
            requirementDescription = "Write 30 journal entries",
            celebrationMessage = "Thirty chapters of your inner life now preserved in words.",
            rewardPoints = 500
        ),
        UiAchievement(
            id = "journal_100",
            name = "Memoir of the Soul",
            description = "A hundred glimpses into the depths of your being",
            icon = ProdyIcons.Book,
            category = UiAchievementCategory.REFLECTION,
            rarity = UiAchievementRarity.EPIC,
            requirement = 100,
            requirementDescription = "Write 100 journal entries",
            celebrationMessage = "One hundred reflections - a true memoir of the soul.",
            rewardPoints = 1000
        ),

        // Future Message Achievements
        UiAchievement(
            id = "future_1",
            name = "Message in a Bottle",
            description = "A letter cast into the river of time",
            icon = ProdyIcons.Mail,
            category = UiAchievementCategory.TEMPORAL,
            rarity = UiAchievementRarity.UNCOMMON,
            requirement = 1,
            requirementDescription = "Write your first future-self letter",
            celebrationMessage = "Your words now travel toward a future you. May they arrive with meaning.",
            rewardPoints = 50
        ),
        UiAchievement(
            id = "future_5",
            name = "Time Weaver",
            description = "Connecting present intention to future realization",
            icon = ProdyIcons.HourglassEmpty,
            category = UiAchievementCategory.TEMPORAL,
            rarity = UiAchievementRarity.RARE,
            requirement = 5,
            requirementDescription = "Write 5 future-self letters",
            celebrationMessage = "Five letters to your future self - you are weaving threads across time.",
            rewardPoints = 200
        ),
        UiAchievement(
            id = "future_received",
            name = "Echo from the Past",
            description = "Your past self has something to tell you",
            icon = ProdyIcons.MarkEmailRead,
            category = UiAchievementCategory.TEMPORAL,
            rarity = UiAchievementRarity.RARE,
            requirement = 1,
            requirementDescription = "Receive your first future-self letter",
            celebrationMessage = "A message from who you were. Listen carefully.",
            rewardPoints = 300
        ),

        // Buddha/Presence Achievements
        UiAchievement(
            id = "buddha_first",
            name = "First Counsel",
            description = "You sought wisdom, and wisdom answered",
            icon = ProdyIcons.Forum,
            category = UiAchievementCategory.PRESENCE,
            rarity = UiAchievementRarity.COMMON,
            requirement = 1,
            requirementDescription = "Have your first conversation with Buddha",
            celebrationMessage = "The dialogue has begun. Buddha awaits your questions.",
            rewardPoints = 25
        ),
        UiAchievement(
            id = "buddha_10",
            name = "Seeking Mind",
            description = "One who asks is one who grows",
            icon = ProdyIcons.PsychologyAlt,
            category = UiAchievementCategory.PRESENCE,
            rarity = UiAchievementRarity.UNCOMMON,
            requirement = 10,
            requirementDescription = "Have 10 conversations with Buddha",
            celebrationMessage = "Ten conversations - you are learning to ask the right questions.",
            rewardPoints = 150
        ),

        // Social Achievements
        UiAchievement(
            id = "boost_10",
            name = "Encourager",
            description = "Supporting others on their journey",
            icon = ProdyIcons.ThumbUp,
            category = UiAchievementCategory.SOCIAL,
            rarity = UiAchievementRarity.UNCOMMON,
            requirement = 10,
            requirementDescription = "Boost 10 peers",
            celebrationMessage = "Ten souls encouraged. Your support ripples outward.",
            rewardPoints = 150
        ),
        UiAchievement(
            id = "top_weekly",
            name = "Weekly Champion",
            description = "Rising to the summit of dedication",
            icon = ProdyIcons.Leaderboard,
            category = UiAchievementCategory.SOCIAL,
            rarity = UiAchievementRarity.EPIC,
            requirement = 1,
            requirementDescription = "Reach first place on weekly leaderboard",
            celebrationMessage = "You stand at the pinnacle this week. Well earned.",
            rewardPoints = 1000
        ),

        // Special Achievements
        UiAchievement(
            id = "night_owl",
            name = "Evening Contemplative",
            description = "The quiet hours of night invite reflection",
            icon = ProdyIcons.NightsStay,
            category = UiAchievementCategory.MASTERY,
            rarity = UiAchievementRarity.UNCOMMON,
            requirement = 7,
            requirementDescription = "Use Prody after 10 PM for 7 days",
            celebrationMessage = "Seven nights of quiet contemplation. The darkness holds its own wisdom.",
            rewardPoints = 200
        ),
        UiAchievement(
            id = "early_bird",
            name = "Dawn Seeker",
            description = "Those who rise with the sun often find themselves ahead",
            icon = ProdyIcons.WbTwilight,
            category = UiAchievementCategory.MASTERY,
            rarity = UiAchievementRarity.UNCOMMON,
            requirement = 7,
            requirementDescription = "Use Prody before 7 AM for 7 days",
            celebrationMessage = "Seven dawns greeted with intention. The early hours hold power.",
            rewardPoints = 200
        ),
        UiAchievement(
            id = "all_moods",
            name = "Emotional Cartographer",
            description = "Mapping the terrain of your inner landscape",
            icon = ProdyIcons.Map,
            category = UiAchievementCategory.REFLECTION,
            rarity = UiAchievementRarity.RARE,
            requirement = 8,
            requirementDescription = "Experience all mood types",
            celebrationMessage = "The full spectrum of emotion is yours to understand.",
            rewardPoints = 400
        )
    )

    /**
     * Gets an achievement by ID, checking both new and legacy achievements.
     */
    fun getAchievementById(id: String): UiAchievement? {
        return allAchievements.find { it.id == id }
            ?: legacyAchievements.find { it.id == id }
    }

    /**
     * Gets achievements by category.
     */
    fun getAchievementsByCategory(category: UiAchievementCategory): List<UiAchievement> {
        return allAchievements.filter { it.category == category }
    }

    /**
     * Gets achievements by rarity.
     */
    fun getAchievementsByRarity(rarity: UiAchievementRarity): List<UiAchievement> {
        return allAchievements.filter { it.rarity == rarity }
    }

    /**
     * Gets achievements that are nearly complete for a user.
     */
    fun getNearCompletionAchievements(achievements: List<UiAchievement>): List<UiAchievement> {
        return achievements.filter { it.isNearCompletion }
    }

    /**
     * Total number of achievements available.
     */
    val totalCount: Int get() = allAchievements.size
}
