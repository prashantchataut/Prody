package com.prody.prashant.domain.identity

/**
 * [ProdyFrames] - Avatar frame system for Prody
 *
 * Frames are cosmetic borders that appear around the user's avatar.
 * They display on multiple surfaces for consistent identity:
 * - Profile header
 * - Leaderboard rows
 * - Share cards
 * - Message cards (future messages)
 *
 * Visual Philosophy:
 * - Frames are subtle enhancements, not distractions
 * - Higher rarity = more refined treatment, not more noise
 * - Legendary frames use Quiet Gold sparingly
 * - No thick borders or loud glows
 */
object ProdyFrames {

    /**
     * Frame pattern types for visual distinction.
     */
    enum class PatternType {
        /** Simple clean border */
        SOLID,
        /** Subtle dashed pattern */
        DASHED,
        /** Thin double-line */
        DOUBLE,
        /** Geometric corner accents */
        CORNER_ACCENTS,
        /** Subtle ink brush effect */
        INK_EDGE,
        /** Minimal scanline effect (for special frames) */
        SCANLINE,
        /** Flowing/animated pattern (very subtle) */
        FLOWING
    }

    /**
     * Frame definition with visual and unlock properties.
     *
     * @property id Unique identifier for persistence
     * @property name User-facing frame name
     * @property description Philosophical or poetic description
     * @property unlockRequirement Human-readable unlock condition
     * @property rarity The cosmetic rarity of this frame
     * @property patternType Visual pattern type
     * @property borderWidth Border width in dp (thin is premium)
     * @property colorHex Primary color as hex Long
     * @property secondaryColorHex Secondary color for gradients/patterns
     * @property isDefault True if available to all users
     * @property isAnimated Whether this frame has subtle animation
     * @property requiredAchievementId Achievement ID required to unlock
     * @property requiredLevel Minimum level required to unlock
     * @property requiredDays Days on app required to unlock
     * @property isSpecial True for special frames (DEV, Founder, etc.)
     */
    data class Frame(
        val id: String,
        val name: String,
        val description: String,
        val unlockRequirement: String,
        val rarity: CosmeticRarity,
        val patternType: PatternType,
        val borderWidth: Float = 2f,
        val colorHex: Long,
        val secondaryColorHex: Long? = null,
        val isDefault: Boolean = false,
        val isAnimated: Boolean = false,
        val requiredAchievementId: String? = null,
        val requiredLevel: Int? = null,
        val requiredDays: Int? = null,
        val isSpecial: Boolean = false
    ) {
        /**
         * Checks if this frame is unlocked for the given user state.
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
            isSpecial && id == "dev_creator" -> isDevBadgeHolder
            isSpecial && id == "founder_light" -> isFounder
            isSpecial && id == "beta_pioneer" -> isBetaTester
            requiredLevel != null -> currentLevel >= requiredLevel
            requiredDays != null -> daysOnApp >= requiredDays
            requiredAchievementId != null -> unlockedAchievementIds.contains(requiredAchievementId)
            else -> false
        }
    }

    /**
     * Complete list of all available frames.
     */
    val allFrames: List<Frame> = listOf(
        // ===== DEFAULT FRAMES =====
        Frame(
            id = "default_clean",
            name = "Clean",
            description = "A simple, understated border",
            unlockRequirement = "Available to all",
            rarity = CosmeticRarity.COMMON,
            patternType = PatternType.SOLID,
            borderWidth = 2f,
            colorHex = 0xFF9E9E9E,
            isDefault = true
        ),
        Frame(
            id = "default_minimal",
            name = "Minimal",
            description = "Less is more",
            unlockRequirement = "Available to all",
            rarity = CosmeticRarity.COMMON,
            patternType = PatternType.SOLID,
            borderWidth = 1.5f,
            colorHex = 0xFFBDBDBD,
            isDefault = true
        ),

        // ===== LEVEL-BASED FRAMES =====
        Frame(
            id = "level_3_dashed",
            name = "Dashed Path",
            description = "The journey continues",
            unlockRequirement = "Reach Level 3",
            rarity = CosmeticRarity.COMMON,
            patternType = PatternType.DASHED,
            borderWidth = 2f,
            colorHex = 0xFF78909C,
            requiredLevel = 3
        ),
        Frame(
            id = "level_5_double",
            name = "Double Line",
            description = "A mark of dedication",
            unlockRequirement = "Reach Level 5",
            rarity = CosmeticRarity.RARE,
            patternType = PatternType.DOUBLE,
            borderWidth = 1.5f,
            colorHex = 0xFF5C6BC0,
            secondaryColorHex = 0xFF7986CB,
            requiredLevel = 5
        ),
        Frame(
            id = "level_7_corners",
            name = "Corner Accents",
            description = "Distinguished presence",
            unlockRequirement = "Reach Level 7",
            rarity = CosmeticRarity.RARE,
            patternType = PatternType.CORNER_ACCENTS,
            borderWidth = 2f,
            colorHex = 0xFF26A69A,
            secondaryColorHex = 0xFF4DB6AC,
            requiredLevel = 7
        ),
        Frame(
            id = "level_10_ink",
            name = "Ink Edge",
            description = "The brush of mastery",
            unlockRequirement = "Reach Level 10",
            rarity = CosmeticRarity.EPIC,
            patternType = PatternType.INK_EDGE,
            borderWidth = 2.5f,
            colorHex = 0xFF1A237E,
            secondaryColorHex = 0xFF303F9F,
            requiredLevel = 10
        ),

        // ===== ACHIEVEMENT-BASED FRAMES =====
        Frame(
            id = "streak_30_flame",
            name = "Flame Border",
            description = "Forged in consistency",
            unlockRequirement = "Maintain a 30-day streak",
            rarity = CosmeticRarity.RARE,
            patternType = PatternType.CORNER_ACCENTS,
            borderWidth = 2f,
            colorHex = 0xFFFF6B6B,
            secondaryColorHex = 0xFFFFAB76,
            requiredAchievementId = "streak_30"
        ),
        Frame(
            id = "streak_90_inferno",
            name = "Inferno Ring",
            description = "A season of unwavering fire",
            unlockRequirement = "Maintain a 90-day streak",
            rarity = CosmeticRarity.EPIC,
            patternType = PatternType.FLOWING,
            borderWidth = 2.5f,
            colorHex = 0xFFE65C2C,
            secondaryColorHex = 0xFFFF8C42,
            isAnimated = true,
            requiredAchievementId = "streak_90"
        ),
        Frame(
            id = "journal_100_reflection",
            name = "Mirror's Edge",
            description = "A hundred glimpses inward",
            unlockRequirement = "Write 100 journal entries",
            rarity = CosmeticRarity.EPIC,
            patternType = PatternType.DOUBLE,
            borderWidth = 2f,
            colorHex = 0xFF3AAFA9,
            secondaryColorHex = 0xFF6FD5CE,
            requiredAchievementId = "journal_100"
        ),
        Frame(
            id = "words_500_logophile",
            name = "Lexicon Border",
            description = "Words define you",
            unlockRequirement = "Learn 500 words",
            rarity = CosmeticRarity.EPIC,
            patternType = PatternType.INK_EDGE,
            borderWidth = 2f,
            colorHex = 0xFF6B5CE7,
            secondaryColorHex = 0xFF9B8AFF,
            requiredAchievementId = "word_collector_500"
        ),

        // ===== TIME-BASED FRAMES =====
        Frame(
            id = "days_30_month",
            name = "Moon Ring",
            description = "One lunar cycle of growth",
            unlockRequirement = "Be on Prody for 30 days",
            rarity = CosmeticRarity.RARE,
            patternType = PatternType.SOLID,
            borderWidth = 2f,
            colorHex = 0xFF4ECDC4,
            requiredDays = 30
        ),
        Frame(
            id = "days_90_season",
            name = "Season's Mark",
            description = "A full season on the path",
            unlockRequirement = "Be on Prody for 90 days",
            rarity = CosmeticRarity.RARE,
            patternType = PatternType.CORNER_ACCENTS,
            borderWidth = 2f,
            colorHex = 0xFFAC32E4,
            secondaryColorHex = 0xFF7918F2,
            requiredDays = 90
        ),
        Frame(
            id = "days_180_half_year",
            name = "Half Circle",
            description = "Six months of presence",
            unlockRequirement = "Be on Prody for 180 days",
            rarity = CosmeticRarity.EPIC,
            patternType = PatternType.DOUBLE,
            borderWidth = 2f,
            colorHex = 0xFF6B5CE7,
            secondaryColorHex = 0xFFD4AF37,
            requiredDays = 180
        ),

        // ===== LEGENDARY FRAMES =====
        Frame(
            id = "year_presence",
            name = "Full Circle",
            description = "One complete orbit of growth",
            unlockRequirement = "Be on Prody for 365 days",
            rarity = CosmeticRarity.LEGENDARY,
            patternType = PatternType.FLOWING,
            borderWidth = 2.5f,
            colorHex = 0xFFD4AF37,
            secondaryColorHex = 0xFFF4D03F,
            isAnimated = true,
            requiredDays = 365
        ),
        Frame(
            id = "streak_365_eternal",
            name = "Eternal Flame",
            description = "A year of unbroken dedication",
            unlockRequirement = "Maintain a 365-day streak",
            rarity = CosmeticRarity.LEGENDARY,
            patternType = PatternType.FLOWING,
            borderWidth = 3f,
            colorHex = 0xFFD4AF37,
            secondaryColorHex = 0xFFFFD700,
            isAnimated = true,
            requiredAchievementId = "streak_365"
        ),
        Frame(
            id = "words_1000_master",
            name = "Master's Seal",
            description = "A thousand doorways to meaning",
            unlockRequirement = "Learn 1000 words",
            rarity = CosmeticRarity.LEGENDARY,
            patternType = PatternType.INK_EDGE,
            borderWidth = 2.5f,
            colorHex = 0xFFD4AF37,
            secondaryColorHex = 0xFFF4D03F,
            isAnimated = true,
            requiredAchievementId = "word_collector_1000"
        ),

        // ===== SPECIAL FRAMES =====
        Frame(
            id = "dev_creator",
            name = "Creator's Ring",
            description = "For those who built the path",
            unlockRequirement = "Reserved for creators",
            rarity = CosmeticRarity.LEGENDARY,
            patternType = PatternType.SCANLINE,
            borderWidth = 2f,
            colorHex = 0xFFD4AF37,
            secondaryColorHex = 0xFF36F97F,
            isAnimated = true,
            isSpecial = true
        ),
        Frame(
            id = "founder_light",
            name = "Founding Light",
            description = "Present from the beginning",
            unlockRequirement = "Join during first month",
            rarity = CosmeticRarity.LEGENDARY,
            patternType = PatternType.CORNER_ACCENTS,
            borderWidth = 2.5f,
            colorHex = 0xFFD4AF37,
            secondaryColorHex = 0xFFFFE5B4,
            isAnimated = true,
            isSpecial = true
        ),
        Frame(
            id = "beta_pioneer",
            name = "Pioneer's Edge",
            description = "Among the first explorers",
            unlockRequirement = "Beta tester",
            rarity = CosmeticRarity.EPIC,
            patternType = PatternType.DASHED,
            borderWidth = 2f,
            colorHex = 0xFF36F97F,
            secondaryColorHex = 0xFF5DFA96,
            isSpecial = true
        )
    )

    /**
     * Gets all frames available to a user.
     */
    fun getAvailableFrames(
        currentLevel: Int,
        daysOnApp: Int,
        unlockedAchievementIds: Set<String>,
        isDevBadgeHolder: Boolean = false,
        isFounder: Boolean = false,
        isBetaTester: Boolean = false
    ): List<Frame> = allFrames.filter { frame ->
        frame.isUnlockedFor(
            currentLevel = currentLevel,
            daysOnApp = daysOnApp,
            unlockedAchievementIds = unlockedAchievementIds,
            isDevBadgeHolder = isDevBadgeHolder,
            isFounder = isFounder,
            isBetaTester = isBetaTester
        )
    }

    /**
     * Gets the default frame for new users.
     */
    fun getDefaultFrame(): Frame = allFrames.first { it.isDefault }

    /**
     * Finds a frame by ID.
     */
    fun findById(id: String): Frame? = allFrames.find { it.id == id }

    /**
     * Gets frames filtered by rarity.
     */
    fun getByRarity(rarity: CosmeticRarity): List<Frame> =
        allFrames.filter { it.rarity == rarity }

    /**
     * Gets all animated frames.
     */
    val animatedFrames: List<Frame>
        get() = allFrames.filter { it.isAnimated }

    /**
     * Gets all special frames.
     */
    val specialFrames: List<Frame>
        get() = allFrames.filter { it.isSpecial }

    /**
     * Total number of frames.
     */
    val totalCount: Int get() = allFrames.size
}
