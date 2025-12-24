package com.prody.prashant.domain.identity

/**
 * [AccentColors] - Subtle highlight color system for Prody
 *
 * Accent colors are subtle highlight colors that appear in multiple surfaces:
 * - Profile header accent lines
 * - Leaderboard row highlight
 * - Share card accent
 * - Card borders (subtle)
 *
 * These are NOT full themes - they're subtle touches that personalize
 * without overwhelming the design system.
 *
 * Visual Philosophy:
 * - Accents should be subtle enhancements
 * - Never compete with content
 * - Work well in both light and dark mode
 * - Compliment the main green accent (#36F97F)
 */
object AccentColors {

    /**
     * Accent color definition.
     *
     * @property id Unique identifier for persistence
     * @property name User-facing name
     * @property description Brief description
     * @property unlockRequirement Human-readable unlock condition
     * @property rarity Cosmetic rarity
     * @property colorHex Primary accent color
     * @property colorDarkHex Darker variant for dark mode adjustments
     * @property colorLightHex Lighter variant for backgrounds
     * @property isDefault True if available to all users
     * @property requiredAchievementId Achievement ID to unlock
     * @property requiredLevel Level to unlock
     * @property requiredDays Days to unlock
     * @property isSpecial Special accent (DEV, etc.)
     */
    data class AccentColor(
        val id: String,
        val name: String,
        val description: String,
        val unlockRequirement: String,
        val rarity: CosmeticRarity,
        val colorHex: Long,
        val colorDarkHex: Long,
        val colorLightHex: Long,
        val isDefault: Boolean = false,
        val requiredAchievementId: String? = null,
        val requiredLevel: Int? = null,
        val requiredDays: Int? = null,
        val isSpecial: Boolean = false
    ) {
        /**
         * Checks if this accent is unlocked.
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
            isSpecial && id == "dev_gold" -> isDevBadgeHolder
            isSpecial && id == "founder_gold" -> isFounder
            isSpecial && id == "beta_green" -> isBetaTester
            requiredLevel != null -> currentLevel >= requiredLevel
            requiredDays != null -> daysOnApp >= requiredDays
            requiredAchievementId != null -> unlockedAchievementIds.contains(requiredAchievementId)
            else -> false
        }
    }

    /**
     * All available accent colors.
     */
    val allAccents: List<AccentColor> = listOf(
        // ===== DEFAULT ACCENTS =====
        AccentColor(
            id = "default_green",
            name = "Prody Green",
            description = "The signature Prody accent",
            unlockRequirement = "Available to all",
            rarity = CosmeticRarity.COMMON,
            colorHex = 0xFF36F97F,
            colorDarkHex = 0xFF2ED56B,
            colorLightHex = 0xFF5DFA96,
            isDefault = true
        ),
        AccentColor(
            id = "default_teal",
            name = "Calm Teal",
            description = "Serene and balanced",
            unlockRequirement = "Available to all",
            rarity = CosmeticRarity.COMMON,
            colorHex = 0xFF26A69A,
            colorDarkHex = 0xFF00897B,
            colorLightHex = 0xFF4DB6AC,
            isDefault = true
        ),
        AccentColor(
            id = "default_slate",
            name = "Subtle Slate",
            description = "Minimal and refined",
            unlockRequirement = "Available to all",
            rarity = CosmeticRarity.COMMON,
            colorHex = 0xFF78909C,
            colorDarkHex = 0xFF546E7A,
            colorLightHex = 0xFF90A4AE,
            isDefault = true
        ),

        // ===== LEVEL-BASED ACCENTS =====
        AccentColor(
            id = "level_3_sky",
            name = "Clear Sky",
            description = "The horizon opens",
            unlockRequirement = "Reach Level 3",
            rarity = CosmeticRarity.COMMON,
            colorHex = 0xFF42A5F5,
            colorDarkHex = 0xFF1E88E5,
            colorLightHex = 0xFF64B5F6,
            requiredLevel = 3
        ),
        AccentColor(
            id = "level_5_indigo",
            name = "Deep Indigo",
            description = "Depth of understanding",
            unlockRequirement = "Reach Level 5",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFF5C6BC0,
            colorDarkHex = 0xFF3F51B5,
            colorLightHex = 0xFF7986CB,
            requiredLevel = 5
        ),
        AccentColor(
            id = "level_7_violet",
            name = "Sage Violet",
            description = "Wisdom's color",
            unlockRequirement = "Reach Level 7",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFF9C27B0,
            colorDarkHex = 0xFF7B1FA2,
            colorLightHex = 0xFFBA68C8,
            requiredLevel = 7
        ),
        AccentColor(
            id = "level_10_gold",
            name = "Quiet Gold",
            description = "The color of mastery",
            unlockRequirement = "Reach Level 10",
            rarity = CosmeticRarity.LEGENDARY,
            colorHex = 0xFFD4AF37,
            colorDarkHex = 0xFFB8962D,
            colorLightHex = 0xFFF4D03F,
            requiredLevel = 10
        ),

        // ===== ACHIEVEMENT-BASED ACCENTS =====
        AccentColor(
            id = "streak_fire",
            name = "Flame",
            description = "The color of consistency",
            unlockRequirement = "Achieve 30-day streak",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFFFF6B6B,
            colorDarkHex = 0xFFE53935,
            colorLightHex = 0xFFFFAB76,
            requiredAchievementId = "streak_30"
        ),
        AccentColor(
            id = "streak_inferno",
            name = "Inferno",
            description = "Blazing dedication",
            unlockRequirement = "Achieve 90-day streak",
            rarity = CosmeticRarity.EPIC,
            colorHex = 0xFFE65C2C,
            colorDarkHex = 0xFFD84315,
            colorLightHex = 0xFFFF8C42,
            requiredAchievementId = "streak_90"
        ),
        AccentColor(
            id = "journal_turquoise",
            name = "Inner Sea",
            description = "Depths of reflection",
            unlockRequirement = "Write 50 journal entries",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFF3AAFA9,
            colorDarkHex = 0xFF2D9A94,
            colorLightHex = 0xFF6FD5CE,
            requiredAchievementId = "journal_50"
        ),
        AccentColor(
            id = "words_lavender",
            name = "Word Lavender",
            description = "The hue of language",
            unlockRequirement = "Learn 100 words",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFF6B5CE7,
            colorDarkHex = 0xFF5A4AD4,
            colorLightHex = 0xFF9B8AFF,
            requiredAchievementId = "word_collector_100"
        ),
        AccentColor(
            id = "temporal_purple",
            name = "Time's Color",
            description = "Across past and future",
            unlockRequirement = "Write 5 future-self letters",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFF667EEA,
            colorDarkHex = 0xFF5566D4,
            colorLightHex = 0xFF8899F0,
            requiredAchievementId = "letter_5"
        ),

        // ===== TIME-BASED ACCENTS =====
        AccentColor(
            id = "days_30_moon",
            name = "Moonlight",
            description = "One lunar cycle",
            unlockRequirement = "Be on Prody for 30 days",
            rarity = CosmeticRarity.RARE,
            colorHex = 0xFF4ECDC4,
            colorDarkHex = 0xFF3DBDB4,
            colorLightHex = 0xFF6FD5CE,
            requiredDays = 30
        ),
        AccentColor(
            id = "days_90_aurora",
            name = "Aurora",
            description = "A season of light",
            unlockRequirement = "Be on Prody for 90 days",
            rarity = CosmeticRarity.EPIC,
            colorHex = 0xFFAC32E4,
            colorDarkHex = 0xFF9020D4,
            colorLightHex = 0xFFCC66F0,
            requiredDays = 90
        ),
        AccentColor(
            id = "days_180_twilight",
            name = "Twilight",
            description = "Half a year of presence",
            unlockRequirement = "Be on Prody for 180 days",
            rarity = CosmeticRarity.EPIC,
            colorHex = 0xFF7918F2,
            colorDarkHex = 0xFF6010E0,
            colorLightHex = 0xFFA050F5,
            requiredDays = 180
        ),
        AccentColor(
            id = "days_365_solar",
            name = "Solar Gold",
            description = "One complete orbit",
            unlockRequirement = "Be on Prody for 365 days",
            rarity = CosmeticRarity.LEGENDARY,
            colorHex = 0xFFFFD700,
            colorDarkHex = 0xFFE6C200,
            colorLightHex = 0xFFFFE54C,
            requiredDays = 365
        ),

        // ===== SPECIAL ACCENTS =====
        AccentColor(
            id = "dev_gold",
            name = "Creator's Gold",
            description = "For those who built the path",
            unlockRequirement = "Reserved for creators",
            rarity = CosmeticRarity.LEGENDARY,
            colorHex = 0xFFD4AF37,
            colorDarkHex = 0xFFB8962D,
            colorLightHex = 0xFFF4D03F,
            isSpecial = true
        ),
        AccentColor(
            id = "founder_gold",
            name = "Founding Light",
            description = "Present from the beginning",
            unlockRequirement = "Join during first month",
            rarity = CosmeticRarity.LEGENDARY,
            colorHex = 0xFFF4D03F,
            colorDarkHex = 0xFFE6C230,
            colorLightHex = 0xFFFFE5B4,
            isSpecial = true
        ),
        AccentColor(
            id = "beta_green",
            name = "Pioneer Green",
            description = "Among the first explorers",
            unlockRequirement = "Beta tester",
            rarity = CosmeticRarity.EPIC,
            colorHex = 0xFF36F97F,
            colorDarkHex = 0xFF2ED56B,
            colorLightHex = 0xFF5DFA96,
            isSpecial = true
        )
    )

    /**
     * Gets all accents available to a user.
     */
    fun getAvailableAccents(
        currentLevel: Int,
        daysOnApp: Int,
        unlockedAchievementIds: Set<String>,
        isDevBadgeHolder: Boolean = false,
        isFounder: Boolean = false,
        isBetaTester: Boolean = false
    ): List<AccentColor> = allAccents.filter { accent ->
        accent.isUnlockedFor(
            currentLevel = currentLevel,
            daysOnApp = daysOnApp,
            unlockedAchievementIds = unlockedAchievementIds,
            isDevBadgeHolder = isDevBadgeHolder,
            isFounder = isFounder,
            isBetaTester = isBetaTester
        )
    }

    /**
     * Gets the default accent for new users.
     */
    fun getDefaultAccent(): AccentColor = allAccents.first { it.isDefault }

    /**
     * Finds an accent by ID.
     */
    fun findById(id: String): AccentColor? = allAccents.find { it.id == id }

    /**
     * Gets accents filtered by rarity.
     */
    fun getByRarity(rarity: CosmeticRarity): List<AccentColor> =
        allAccents.filter { it.rarity == rarity }

    /**
     * Gets all special accents.
     */
    val specialAccents: List<AccentColor>
        get() = allAccents.filter { it.isSpecial }

    /**
     * Total number of accents.
     */
    val totalCount: Int get() = allAccents.size
}
