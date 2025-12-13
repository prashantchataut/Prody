package com.prody.prashant.domain.identity

/**
 * [ProdyBanners] - Profile banner system for Prody
 *
 * Banners are earned through achievements, milestones, and time spent with the app.
 * They serve as visual representations of the user's journey and accomplishments.
 *
 * Banners provide profile customization while celebrating genuine growth:
 * - Default banners are available to all users
 * - Level-based banners unlock as users progress
 * - Achievement-based banners celebrate specific accomplishments
 * - Time-based banners honor dedication and longevity
 *
 * Visual patterns range from simple gradients to complex geometric designs,
 * each carrying meaning related to its unlock requirement.
 *
 * Example usage:
 * ```
 * val availableBanners = ProdyBanners.getAvailableBanners(
 *     currentLevel = 5,
 *     daysOnApp = 30,
 *     unlockedAchievementIds = setOf("streak_30", "journal_30")
 * )
 * ```
 */
object ProdyBanners {

    /**
     * Pattern types for banner visual design.
     * Each type represents a distinct visual style that can be rendered on the banner.
     */
    enum class PatternType {
        /** Simple gradient with no overlay pattern */
        SOLID,
        /** Subtle geometric shapes forming repeating patterns */
        GEOMETRIC,
        /** Flowing wave lines creating movement */
        WAVES,
        /** Star and dot patterns resembling night sky */
        CONSTELLATION,
        /** Circular meditative patterns inspired by mandalas */
        MANDALA,
        /** Flowing aurora-like patterns with smooth color transitions */
        AURORA
    }

    /**
     * Banner definition with visual and unlock properties.
     *
     * @property id Unique identifier for persistence and selection
     * @property name User-facing banner name
     * @property description Philosophical or poetic description
     * @property unlockRequirement Human-readable unlock condition
     * @property gradientColors List of hex color values for the banner gradient
     * @property patternType Visual pattern overlay type
     * @property isDefault True if this banner is available to all users
     * @property requiredAchievementId Achievement ID required to unlock (if applicable)
     * @property requiredLevel Minimum level required to unlock (if applicable)
     * @property requiredDays Days on app required to unlock (if applicable)
     */
    data class Banner(
        val id: String,
        val name: String,
        val description: String,
        val unlockRequirement: String,
        val gradientColors: List<Long>,
        val patternType: PatternType,
        val isDefault: Boolean = false,
        val requiredAchievementId: String? = null,
        val requiredLevel: Int? = null,
        val requiredDays: Int? = null
    ) {
        /**
         * Checks if this banner is unlocked for the given user state.
         *
         * @param currentLevel The user's current level
         * @param daysOnApp Days since the user joined
         * @param unlockedAchievementIds Set of achievement IDs the user has unlocked
         * @return True if the banner is available to the user
         */
        fun isUnlockedFor(
            currentLevel: Int,
            daysOnApp: Int,
            unlockedAchievementIds: Set<String>
        ): Boolean = when {
            isDefault -> true
            requiredLevel != null -> currentLevel >= requiredLevel
            requiredDays != null -> daysOnApp >= requiredDays
            requiredAchievementId != null -> unlockedAchievementIds.contains(requiredAchievementId)
            else -> false
        }
    }

    /**
     * Complete list of all available banners in Prody.
     */
    val allBanners: List<Banner> = listOf(
        // ===== DEFAULT BANNERS =====
        Banner(
            id = "default_dawn",
            name = "Morning Light",
            description = "The default banner for all seekers",
            unlockRequirement = "Available to all",
            gradientColors = listOf(0xFF667EEA, 0xFF764BA2),
            patternType = PatternType.SOLID,
            isDefault = true
        ),
        Banner(
            id = "default_twilight",
            name = "Evening Calm",
            description = "Soft colors of day's end",
            unlockRequirement = "Available to all",
            gradientColors = listOf(0xFF2C3E50, 0xFF4CA1AF),
            patternType = PatternType.SOLID,
            isDefault = true
        ),
        Banner(
            id = "default_forest",
            name = "Forest Path",
            description = "The colors of growth and renewal",
            unlockRequirement = "Available to all",
            gradientColors = listOf(0xFF11998E, 0xFF38EF7D),
            patternType = PatternType.SOLID,
            isDefault = true
        ),

        // ===== LEVEL-BASED BANNERS =====
        Banner(
            id = "level_2_horizon",
            name = "New Horizon",
            description = "The first view from the ascending path",
            unlockRequirement = "Reach Level 2",
            gradientColors = listOf(0xFF56CCF2, 0xFF2F80ED),
            patternType = PatternType.WAVES,
            requiredLevel = 2
        ),
        Banner(
            id = "student_path",
            name = "Student's Path",
            description = "For those who have begun their journey in earnest",
            unlockRequirement = "Reach Level 3",
            gradientColors = listOf(0xFF11998E, 0xFF38EF7D),
            patternType = PatternType.GEOMETRIC,
            requiredLevel = 3
        ),
        Banner(
            id = "practitioner_mark",
            name = "Practitioner's Mark",
            description = "The sign of one who acts upon wisdom",
            unlockRequirement = "Reach Level 4",
            gradientColors = listOf(0xFFFF6B6B, 0xFFFFAB76),
            patternType = PatternType.WAVES,
            requiredLevel = 4
        ),
        Banner(
            id = "contemplative_garden",
            name = "Contemplative Garden",
            description = "A place of reflection and growth",
            unlockRequirement = "Reach Level 5",
            gradientColors = listOf(0xFF4ECDC4, 0xFF44B09E),
            patternType = PatternType.MANDALA,
            requiredLevel = 5
        ),
        Banner(
            id = "philosophers_garden",
            name = "Philosopher's Garden",
            description = "Where wisdom takes root and blooms",
            unlockRequirement = "Reach Level 6",
            gradientColors = listOf(0xFF6B5CE7, 0xFF9B8AFF),
            patternType = PatternType.MANDALA,
            requiredLevel = 6
        ),
        Banner(
            id = "sages_vista",
            name = "Sage's Vista",
            description = "The view from heights of understanding",
            unlockRequirement = "Reach Level 7",
            gradientColors = listOf(0xFFAC32E4, 0xFF7918F2),
            patternType = PatternType.AURORA,
            requiredLevel = 7
        ),
        Banner(
            id = "sages_horizon",
            name = "Sage's Horizon",
            description = "The view from heights of wisdom",
            unlockRequirement = "Reach Level 8",
            gradientColors = listOf(0xFFD4AF37, 0xFFF4D03F),
            patternType = PatternType.AURORA,
            requiredLevel = 8
        ),
        Banner(
            id = "luminary_light",
            name = "Luminary's Light",
            description = "Radiance earned through dedication",
            unlockRequirement = "Reach Level 9",
            gradientColors = listOf(0xFFF4D03F, 0xFFFFE5B4, 0xFFD4AF37),
            patternType = PatternType.CONSTELLATION,
            requiredLevel = 9
        ),
        Banner(
            id = "awakened_sky",
            name = "Awakened Sky",
            description = "The infinite expanse of realized potential",
            unlockRequirement = "Reach Level 10",
            gradientColors = listOf(0xFFFFFFFF, 0xFFE8E8E8, 0xFFD4AF37),
            patternType = PatternType.CONSTELLATION,
            requiredLevel = 10
        ),

        // ===== ACHIEVEMENT-BASED BANNERS =====
        Banner(
            id = "flame_keeper",
            name = "Flame Keeper",
            description = "For those who maintain the sacred fire of consistency",
            unlockRequirement = "Achieve 30-day streak",
            gradientColors = listOf(0xFFFF6B6B, 0xFFFFAB76, 0xFFFFC371),
            patternType = PatternType.WAVES,
            requiredAchievementId = "streak_30"
        ),
        Banner(
            id = "eternal_flame",
            name = "Eternal Flame",
            description = "A year of unbroken dedication",
            unlockRequirement = "Achieve 365-day streak",
            gradientColors = listOf(0xFFFF4500, 0xFFFF8C00, 0xFFFFD700),
            patternType = PatternType.AURORA,
            requiredAchievementId = "streak_365"
        ),
        Banner(
            id = "word_garden",
            name = "Word Garden",
            description = "A place where language blooms",
            unlockRequirement = "Learn 100 words",
            gradientColors = listOf(0xFF6B5CE7, 0xFF9B8AFF),
            patternType = PatternType.GEOMETRIC,
            requiredAchievementId = "word_collector_100"
        ),
        Banner(
            id = "logophile_library",
            name = "Logophile's Library",
            description = "Walls lined with the words you've gathered",
            unlockRequirement = "Learn 500 words",
            gradientColors = listOf(0xFF2C3E50, 0xFF4CA1AF),
            patternType = PatternType.GEOMETRIC,
            requiredAchievementId = "word_collector_500"
        ),
        Banner(
            id = "master_library",
            name = "Master's Library",
            description = "A thousand words, a thousand doorways to meaning",
            unlockRequirement = "Learn 1000 words",
            gradientColors = listOf(0xFF1A1A2E, 0xFF16213E, 0xFF0F3460),
            patternType = PatternType.CONSTELLATION,
            requiredAchievementId = "word_collector_1000"
        ),
        Banner(
            id = "inner_sanctum",
            name = "Inner Sanctum",
            description = "The quiet space where you meet yourself",
            unlockRequirement = "Write 100 journal entries",
            gradientColors = listOf(0xFF3AAFA9, 0xFF6FD5CE),
            patternType = PatternType.MANDALA,
            requiredAchievementId = "journal_100"
        ),
        Banner(
            id = "year_of_pages",
            name = "Year of Pages",
            description = "Three hundred sixty-five chapters of your inner life",
            unlockRequirement = "Write 365 journal entries",
            gradientColors = listOf(0xFF2C3E50, 0xFF3AAFA9, 0xFF6FD5CE),
            patternType = PatternType.WAVES,
            requiredAchievementId = "journal_365"
        ),
        Banner(
            id = "timeless",
            name = "Timeless",
            description = "One who speaks across the boundaries of time",
            unlockRequirement = "Receive a future-self letter",
            gradientColors = listOf(0xFF667EEA, 0xFF764BA2, 0xFFAA076B),
            patternType = PatternType.CONSTELLATION,
            requiredAchievementId = "letter_received"
        ),
        Banner(
            id = "time_architect",
            name = "Time Architect",
            description = "Building bridges across the river of time",
            unlockRequirement = "Write 10 future-self letters",
            gradientColors = listOf(0xFF4801FF, 0xFF7918F2, 0xFFAC32E4),
            patternType = PatternType.GEOMETRIC,
            requiredAchievementId = "letter_10"
        ),
        Banner(
            id = "dawn_seeker",
            name = "Dawn Seeker",
            description = "Colors of the early morning light",
            unlockRequirement = "Use Prody before 7 AM for 7 days",
            gradientColors = listOf(0xFFFF9966, 0xFFFF5E62),
            patternType = PatternType.AURORA,
            requiredAchievementId = "early_bird"
        ),
        Banner(
            id = "night_contemplative",
            name = "Night Contemplative",
            description = "The deep colors of evening reflection",
            unlockRequirement = "Use Prody after 10 PM for 7 days",
            gradientColors = listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364),
            patternType = PatternType.CONSTELLATION,
            requiredAchievementId = "night_owl"
        ),
        Banner(
            id = "buddha_companion",
            name = "Wisdom Companion",
            description = "For those who walk with the sage",
            unlockRequirement = "Have 100 conversations with Buddha",
            gradientColors = listOf(0xFF4ECDC4, 0xFF44B09E, 0xFF2D5A4A),
            patternType = PatternType.MANDALA,
            requiredAchievementId = "buddha_100"
        ),

        // ===== TIME-BASED BANNERS =====
        Banner(
            id = "week_one",
            name = "First Week",
            description = "Seven days of possibility",
            unlockRequirement = "Be on Prody for 7 days",
            gradientColors = listOf(0xFF56CCF2, 0xFF2F80ED),
            patternType = PatternType.WAVES,
            requiredDays = 7
        ),
        Banner(
            id = "fortnight",
            name = "Fortnight",
            description = "Two weeks of presence",
            unlockRequirement = "Be on Prody for 14 days",
            gradientColors = listOf(0xFF667EEA, 0xFF764BA2),
            patternType = PatternType.WAVES,
            requiredDays = 14
        ),
        Banner(
            id = "month_one",
            name = "First Moon",
            description = "One lunar cycle of growth",
            unlockRequirement = "Be on Prody for 30 days",
            gradientColors = listOf(0xFF4ECDC4, 0xFF44B09E),
            patternType = PatternType.AURORA,
            requiredDays = 30
        ),
        Banner(
            id = "two_moons",
            name = "Two Moons",
            description = "Two months of dedication",
            unlockRequirement = "Be on Prody for 60 days",
            gradientColors = listOf(0xFF2C3E50, 0xFF4CA1AF),
            patternType = PatternType.AURORA,
            requiredDays = 60
        ),
        Banner(
            id = "quarter_year",
            name = "Season's Turn",
            description = "A full season on the path",
            unlockRequirement = "Be on Prody for 90 days",
            gradientColors = listOf(0xFFAC32E4, 0xFF7918F2, 0xFF4801FF),
            patternType = PatternType.CONSTELLATION,
            requiredDays = 90
        ),
        Banner(
            id = "half_year",
            name = "Half Circle",
            description = "Six months of growth and reflection",
            unlockRequirement = "Be on Prody for 180 days",
            gradientColors = listOf(0xFF6B5CE7, 0xFF9B8AFF, 0xFFD4AF37),
            patternType = PatternType.MANDALA,
            requiredDays = 180
        ),
        Banner(
            id = "year_one",
            name = "Full Circle",
            description = "One complete orbit of growth and reflection",
            unlockRequirement = "Be on Prody for 365 days",
            gradientColors = listOf(0xFFD4AF37, 0xFFF4D03F, 0xFFFFE5B4),
            patternType = PatternType.AURORA,
            requiredDays = 365
        ),

        // ===== SPECIAL BANNERS =====
        Banner(
            id = "founder",
            name = "Founding Light",
            description = "For those who believed from the beginning",
            unlockRequirement = "Join during the first month",
            gradientColors = listOf(0xFFD4AF37, 0xFFF4D03F, 0xFFFFFFFF),
            patternType = PatternType.CONSTELLATION,
            requiredAchievementId = "founder"
        )
    )

    /**
     * Gets all banners available to a user based on their current state.
     *
     * @param currentLevel The user's current level
     * @param daysOnApp Days since the user joined the app
     * @param unlockedAchievementIds Set of achievement IDs the user has unlocked
     * @return List of banners the user has unlocked
     */
    fun getAvailableBanners(
        currentLevel: Int,
        daysOnApp: Int,
        unlockedAchievementIds: Set<String>
    ): List<Banner> {
        return allBanners.filter { banner ->
            banner.isUnlockedFor(currentLevel, daysOnApp, unlockedAchievementIds)
        }
    }

    /**
     * Gets the default banner for new users.
     */
    fun getDefaultBanner(): Banner {
        return allBanners.first { it.isDefault }
    }

    /**
     * Finds a banner by its unique identifier.
     *
     * @param id The banner's unique identifier
     * @return The matching banner, or null if not found
     */
    fun findById(id: String): Banner? = allBanners.find { it.id == id }

    /**
     * Gets all default banners available to all users.
     */
    val defaultBanners: List<Banner>
        get() = allBanners.filter { it.isDefault }

    /**
     * Gets all level-based banners.
     */
    val levelBanners: List<Banner>
        get() = allBanners.filter { it.requiredLevel != null }

    /**
     * Gets all achievement-based banners.
     */
    val achievementBanners: List<Banner>
        get() = allBanners.filter { it.requiredAchievementId != null }

    /**
     * Gets all time-based banners.
     */
    val timeBanners: List<Banner>
        get() = allBanners.filter { it.requiredDays != null }

    /**
     * Gets the total number of banners.
     */
    val totalCount: Int get() = allBanners.size

    /**
     * Checks if a specific banner should be newly unlocked based on state change.
     *
     * @param bannerId The banner to check
     * @param previousState Previous unlock state
     * @param newLevel New level after change
     * @param newDaysOnApp New days count
     * @param newAchievementIds New set of achievement IDs
     * @return True if the banner was just unlocked
     */
    fun checkNewlyUnlocked(
        bannerId: String,
        previousState: Boolean,
        newLevel: Int,
        newDaysOnApp: Int,
        newAchievementIds: Set<String>
    ): Boolean {
        if (previousState) return false
        val banner = findById(bannerId) ?: return false
        return banner.isUnlockedFor(newLevel, newDaysOnApp, newAchievementIds)
    }
}
