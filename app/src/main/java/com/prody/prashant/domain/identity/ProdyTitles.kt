package com.prody.prashant.domain.identity

/**
 * [ProdyTitles] - User display title system for Prody
 *
 * Titles appear below or beside the user's name on:
 * - Profile header
 * - Leaderboard rows
 * - Share cards
 *
 * Titles represent achievements and milestones in the user's journey.
 * They are meant to feel earned and meaningful, not trivial.
 */
object ProdyTitles {

    /**
     * Title definition.
     *
     * @property id Unique identifier for persistence
     * @property name Display title (what shows under the name)
     * @property description What this title represents
     * @property unlockRequirement Human-readable unlock condition
     * @property rarity The cosmetic rarity of this title
     * @property colorHex Primary display color
     * @property isDefault True if available to all users
     * @property requiredAchievementId Achievement ID required to unlock
     * @property requiredLevel Minimum level required to unlock
     * @property requiredDays Days on app required to unlock
     * @property isSpecial True for special titles (DEV, Founder, etc.)
     * @property sortOrder Display order (lower = appears first in selection)
     */
    data class Title(
        val id: String,
        val name: String,
        val description: String,
        val unlockRequirement: String,
        val rarity: CosmeticRarity,
        val colorHex: Long,
        val isDefault: Boolean = false,
        val requiredAchievementId: String? = null,
        val requiredLevel: Int? = null,
        val requiredDays: Int? = null,
        val isSpecial: Boolean = false,
        val sortOrder: Int = 100
    ) {
        /**
         * Checks if this title is unlocked for the given user state.
         */
        fun isUnlockedFor(
            currentLevel: Int,
            daysOnApp: Int,
            unlockedAchievementIds: Set<String>,
            isDevBadgeHolder: Boolean = false,
            isFounder: Boolean = false,
            isBetaTester: Boolean = false
        ): Boolean = when {
            isDefault -> true
            isSpecial && id == "creator" -> isDevBadgeHolder
            isSpecial && id == "founder" -> isFounder
            isSpecial && id == "pioneer" -> isBetaTester
            requiredLevel != null -> currentLevel >= requiredLevel
            requiredDays != null -> daysOnApp >= requiredDays
            requiredAchievementId != null -> unlockedAchievementIds.contains(requiredAchievementId)
            else -> false
        }
    }

    /**
     * Complete list of all available titles.
     */
    val allTitles: List<Title> = listOf(
        // ===== DEFAULT TITLES =====
        Title(
            id = "seeker",
            name = "Seeker",
            description = "One who seeks growth",
            unlockRequirement = "Available to all",
            rarity = CosmeticRarity.COMMON,
            colorHex = 0xFF9E9E9E,
            isDefault = true,
            sortOrder = 0
        ),
        Title(
            id = "learner",
            name = "Learner",
            description = "Always learning",
            unlockRequirement = "Available to all",
            rarity = CosmeticRarity.COMMON,
            colorHex = 0xFF78909C,
            isDefault = true,
            sortOrder = 1
        ),

        // ===== LEVEL-BASED TITLES (from ProdyRanks) =====
        Title(
            id = "initiate",
            name = "Initiate",
            description = "Committed to the practice",
            unlockRequirement = "Reach Level 2 (200 points)",
            rarity = CosmeticRarity.COMMON,
            colorHex = 0xFF607D8B,
            requiredLevel = 2,
            sortOrder = 10
        ),
        Title(
            id = "student_of_life",
            name = "Student of Life",
            description = "Learning from experience",
            unlockRequirement = "Reach Level 3 (500 points)",
            rarity = CosmeticRarity.COMMON,
            colorHex = 0xFF5C6BC0,
            requiredLevel = 3,
            sortOrder = 11
        ),
        Title(
            id = "practitioner",
            name = "Practitioner",
            description = "Wisdom in daily action",
            unlockRequirement = "Reach Level 4 (1000 points)",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFF26A69A,
            requiredLevel = 4,
            sortOrder = 12
        ),
        Title(
            id = "contemplative",
            name = "Contemplative",
            description = "Deep understanding",
            unlockRequirement = "Reach Level 5 (1500 points)",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFF4ECDC4,
            requiredLevel = 5,
            sortOrder = 13
        ),
        Title(
            id = "philosopher",
            name = "Philosopher",
            description = "Lover of wisdom",
            unlockRequirement = "Reach Level 6 (2500 points)",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFF6B5CE7,
            requiredLevel = 6,
            sortOrder = 14
        ),
        Title(
            id = "sage",
            name = "Sage",
            description = "Tempered by time",
            unlockRequirement = "Reach Level 7 (4000 points)",
            rarity = CosmeticRarity.EPIC,
            colorHex = 0xFFAC32E4,
            requiredLevel = 7,
            sortOrder = 15
        ),
        Title(
            id = "luminary",
            name = "Luminary",
            description = "Light for others",
            unlockRequirement = "Reach Level 8 (6000 points)",
            rarity = CosmeticRarity.EPIC,
            colorHex = 0xFFD4AF37,
            requiredLevel = 8,
            sortOrder = 16
        ),
        Title(
            id = "wayfinder",
            name = "Wayfinder",
            description = "Navigate by inner stars",
            unlockRequirement = "Reach Level 9 (8500 points)",
            rarity = CosmeticRarity.EPIC,
            colorHex = 0xFFF4D03F,
            requiredLevel = 9,
            sortOrder = 17
        ),
        Title(
            id = "awakened",
            name = "Awakened",
            description = "Clear vision, full living",
            unlockRequirement = "Reach Level 10 (12000 points)",
            rarity = CosmeticRarity.LEGENDARY,
            colorHex = 0xFFD4AF37,
            requiredLevel = 10,
            sortOrder = 18
        ),

        // ===== ACHIEVEMENT-BASED TITLES =====
        Title(
            id = "wordsmith",
            name = "Wordsmith",
            description = "Craftsman of language",
            unlockRequirement = "Learn 50 words",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFF6B5CE7,
            requiredAchievementId = "word_collector_50",
            sortOrder = 30
        ),
        Title(
            id = "logophile",
            name = "Logophile",
            description = "True lover of words",
            unlockRequirement = "Learn 500 words",
            rarity = CosmeticRarity.EPIC,
            colorHex = 0xFF9B8AFF,
            requiredAchievementId = "word_collector_500",
            sortOrder = 31
        ),
        Title(
            id = "master_of_words",
            name = "Master of Words",
            description = "A thousand words form a symphony",
            unlockRequirement = "Learn 1000 words",
            rarity = CosmeticRarity.LEGENDARY,
            colorHex = 0xFFD4AF37,
            requiredAchievementId = "word_collector_1000",
            sortOrder = 32
        ),
        Title(
            id = "chronicler",
            name = "Chronicler",
            description = "Keeper of your own story",
            unlockRequirement = "Write 30 journal entries",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFF3AAFA9,
            requiredAchievementId = "journal_30",
            sortOrder = 40
        ),
        Title(
            id = "reflective_soul",
            name = "Reflective Soul",
            description = "Deep inner examination",
            unlockRequirement = "Write 50 journal entries",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFF6FD5CE,
            requiredAchievementId = "journal_50",
            sortOrder = 41
        ),
        Title(
            id = "memoirist",
            name = "Memoirist",
            description = "A hundred chapters of inner life",
            unlockRequirement = "Write 100 journal entries",
            rarity = CosmeticRarity.EPIC,
            colorHex = 0xFF4ECDC4,
            requiredAchievementId = "journal_100",
            sortOrder = 42
        ),
        Title(
            id = "flame_keeper",
            name = "Flame Keeper",
            description = "Maintains the sacred fire",
            unlockRequirement = "Achieve 30-day streak",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFFFF6B6B,
            requiredAchievementId = "streak_30",
            sortOrder = 50
        ),
        Title(
            id = "ever_burning",
            name = "Ever Burning",
            description = "A season of fire",
            unlockRequirement = "Achieve 90-day streak",
            rarity = CosmeticRarity.EPIC,
            colorHex = 0xFFE65C2C,
            requiredAchievementId = "streak_90",
            sortOrder = 51
        ),
        Title(
            id = "eternal_flame",
            name = "Eternal Flame",
            description = "A year of unbroken fire",
            unlockRequirement = "Achieve 365-day streak",
            rarity = CosmeticRarity.LEGENDARY,
            colorHex = 0xFFD4AF37,
            requiredAchievementId = "streak_365",
            sortOrder = 52
        ),
        Title(
            id = "time_weaver",
            name = "Time Weaver",
            description = "Connecting across time",
            unlockRequirement = "Write 5 future-self letters",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFF667EEA,
            requiredAchievementId = "letter_5",
            sortOrder = 60
        ),
        Title(
            id = "temporal_architect",
            name = "Temporal Architect",
            description = "Builder of futures",
            unlockRequirement = "Write 10 future-self letters",
            rarity = CosmeticRarity.EPIC,
            colorHex = 0xFF764BA2,
            requiredAchievementId = "letter_10",
            sortOrder = 61
        ),
        Title(
            id = "buddha_companion",
            name = "Wisdom Companion",
            description = "Walks with the sage",
            unlockRequirement = "Have 100 Buddha conversations",
            rarity = CosmeticRarity.EPIC,
            colorHex = 0xFF44B09E,
            requiredAchievementId = "buddha_100",
            sortOrder = 70
        ),
        Title(
            id = "dawn_seeker",
            name = "Dawn Seeker",
            description = "Greets the morning light",
            unlockRequirement = "Use Prody before 7 AM for 7 days",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFFFF9966,
            requiredAchievementId = "early_bird",
            sortOrder = 80
        ),
        Title(
            id = "night_owl",
            name = "Night Contemplative",
            description = "Finds wisdom in darkness",
            unlockRequirement = "Use Prody after 10 PM for 7 days",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFF2C5364,
            requiredAchievementId = "night_owl",
            sortOrder = 81
        ),

        // ===== TIME-BASED TITLES =====
        Title(
            id = "dedicated_30",
            name = "Dedicated",
            description = "One month of presence",
            unlockRequirement = "Be on Prody for 30 days",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFF56CCF2,
            requiredDays = 30,
            sortOrder = 90
        ),
        Title(
            id = "steadfast_90",
            name = "Steadfast",
            description = "A season of commitment",
            unlockRequirement = "Be on Prody for 90 days",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFF4801FF,
            requiredDays = 90,
            sortOrder = 91
        ),
        Title(
            id = "veteran_180",
            name = "Veteran",
            description = "Half a year of growth",
            unlockRequirement = "Be on Prody for 180 days",
            rarity = CosmeticRarity.EPIC,
            colorHex = 0xFF7918F2,
            requiredDays = 180,
            sortOrder = 92
        ),
        Title(
            id = "elder_365",
            name = "Elder",
            description = "One full year of wisdom",
            unlockRequirement = "Be on Prody for 365 days",
            rarity = CosmeticRarity.LEGENDARY,
            colorHex = 0xFFD4AF37,
            requiredDays = 365,
            sortOrder = 93
        ),

        // ===== SPECIAL TITLES =====
        Title(
            id = "creator",
            name = "Creator",
            description = "Builder of this path",
            unlockRequirement = "Reserved for creators",
            rarity = CosmeticRarity.LEGENDARY,
            colorHex = 0xFFD4AF37,
            isSpecial = true,
            sortOrder = 200
        ),
        Title(
            id = "founder",
            name = "Founding Light",
            description = "Present from the beginning",
            unlockRequirement = "Join during first month",
            rarity = CosmeticRarity.LEGENDARY,
            colorHex = 0xFFF4D03F,
            isSpecial = true,
            sortOrder = 201
        ),
        Title(
            id = "pioneer",
            name = "Pioneer",
            description = "Among the first explorers",
            unlockRequirement = "Beta tester",
            rarity = CosmeticRarity.EPIC,
            colorHex = 0xFF36F97F,
            isSpecial = true,
            sortOrder = 202
        )
    )

    /**
     * Gets all titles available to a user.
     */
    fun getAvailableTitles(
        currentLevel: Int,
        daysOnApp: Int,
        unlockedAchievementIds: Set<String>,
        isDevBadgeHolder: Boolean = false,
        isFounder: Boolean = false,
        isBetaTester: Boolean = false
    ): List<Title> = allTitles.filter { title ->
        title.isUnlockedFor(
            currentLevel = currentLevel,
            daysOnApp = daysOnApp,
            unlockedAchievementIds = unlockedAchievementIds,
            isDevBadgeHolder = isDevBadgeHolder,
            isFounder = isFounder,
            isBetaTester = isBetaTester
        )
    }.sortedBy { it.sortOrder }

    /**
     * Gets the default title for new users.
     */
    fun getDefaultTitle(): Title = allTitles.first { it.isDefault }

    /**
     * Finds a title by ID.
     */
    fun findById(id: String): Title? = allTitles.find { it.id == id }

    /**
     * Gets titles filtered by rarity.
     */
    fun getByRarity(rarity: CosmeticRarity): List<Title> =
        allTitles.filter { it.rarity == rarity }

    /**
     * Gets all special titles.
     */
    val specialTitles: List<Title>
        get() = allTitles.filter { it.isSpecial }

    /**
     * Total number of titles.
     */
    val totalCount: Int get() = allTitles.size
}
