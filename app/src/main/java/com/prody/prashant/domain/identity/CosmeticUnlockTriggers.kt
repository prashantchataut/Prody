package com.prody.prashant.domain.identity

/**
 * [CosmeticUnlockTriggers] - Meaningful unlock triggers for cosmetics
 *
 * Maps user actions and milestones to cosmetic unlocks.
 * Every unlock should feel earned through genuine engagement,
 * not arbitrary thresholds.
 *
 * Design Philosophy:
 * - Unlocks celebrate real achievements, not grinding
 * - Each unlock tells a story about the user's journey
 * - Quality over quantity: fewer, more meaningful cosmetics
 * - No pay-to-unlock: everything is earned
 *
 * Trigger Categories:
 * 1. Level Milestones - Natural progression rewards
 * 2. Time Dedication - Honoring long-term commitment
 * 3. Achievement Completion - Specific accomplishments
 * 4. Behavior Patterns - Recognizing healthy habits
 * 5. Special Status - DEV, Founder, Beta exclusive
 *
 * Example usage:
 * ```
 * val newlyUnlocked = CosmeticUnlockTriggers.checkForNewUnlocks(
 *     previousState = previousUserState,
 *     currentState = currentUserState
 * )
 *
 * newlyUnlocked.forEach { unlock ->
 *     showUnlockCelebration(unlock)
 * }
 * ```
 */
object CosmeticUnlockTriggers {

    /**
     * Categories of unlock triggers.
     */
    enum class TriggerCategory(
        val id: String,
        val displayName: String,
        val description: String
    ) {
        LEVEL(
            id = "level",
            displayName = "Level Progress",
            description = "Unlocked by reaching new levels"
        ),
        TIME(
            id = "time",
            displayName = "Time Dedication",
            description = "Unlocked through continued presence"
        ),
        ACHIEVEMENT(
            id = "achievement",
            displayName = "Achievement",
            description = "Unlocked by completing specific goals"
        ),
        BEHAVIOR(
            id = "behavior",
            displayName = "Habits",
            description = "Unlocked through consistent behavior"
        ),
        SPECIAL(
            id = "special",
            displayName = "Special",
            description = "Exclusive cosmetics for special users"
        )
    }

    /**
     * Types of cosmetics that can be unlocked.
     */
    enum class CosmeticType(val id: String) {
        BANNER("banner"),
        FRAME("frame"),
        TITLE("title"),
        ACCENT("accent"),
        BADGE("badge")
    }

    /**
     * Represents a cosmetic that was just unlocked.
     */
    data class UnlockedCosmetic(
        val cosmeticId: String,
        val cosmeticType: CosmeticType,
        val name: String,
        val description: String,
        val rarity: CosmeticRarity,
        val triggerCategory: TriggerCategory,
        val unlockReason: String,
        val celebrationMessage: String
    )

    /**
     * User state snapshot for unlock checking.
     */
    data class UserState(
        val level: Int,
        val daysOnApp: Int,
        val currentStreak: Int,
        val longestStreak: Int,
        val wordsLearned: Int,
        val journalEntries: Int,
        val futureLettersSent: Int,
        val futureLettersReceived: Int,
        val buddhaConversations: Int,
        val quotesReflected: Int,
        val totalReflectionMinutes: Int,
        val unlockedAchievementIds: Set<String>,
        val isDevBadgeHolder: Boolean = false,
        val isFounder: Boolean = false,
        val isBetaTester: Boolean = false,
        val earlyMorningSessionDays: Int = 0,  // Sessions before 7 AM
        val lateNightSessionDays: Int = 0,      // Sessions after 10 PM
        val weekendSessionDays: Int = 0         // Weekend sessions
    )

    // =========================================================================
    // Level-Based Triggers
    // =========================================================================

    /**
     * Level milestones that unlock cosmetics.
     */
    private val levelTriggers: Map<Int, List<UnlockedCosmetic>> = mapOf(
        2 to listOf(
            UnlockedCosmetic(
                cosmeticId = "level_2_horizon",
                cosmeticType = CosmeticType.BANNER,
                name = "New Horizon",
                description = "The first view from the ascending path",
                rarity = CosmeticRarity.COMMON,
                triggerCategory = TriggerCategory.LEVEL,
                unlockReason = "Reached Level 2",
                celebrationMessage = "Your journey begins to unfold"
            ),
            UnlockedCosmetic(
                cosmeticId = "level_2_simple",
                cosmeticType = CosmeticType.FRAME,
                name = "Simple Circle",
                description = "Clean and focused",
                rarity = CosmeticRarity.COMMON,
                triggerCategory = TriggerCategory.LEVEL,
                unlockReason = "Reached Level 2",
                celebrationMessage = "A new frame for your journey"
            )
        ),
        3 to listOf(
            UnlockedCosmetic(
                cosmeticId = "student_path",
                cosmeticType = CosmeticType.BANNER,
                name = "Student's Path",
                description = "For those who have begun their journey in earnest",
                rarity = CosmeticRarity.COMMON,
                triggerCategory = TriggerCategory.LEVEL,
                unlockReason = "Reached Level 3",
                celebrationMessage = "The student's path opens before you"
            ),
            UnlockedCosmetic(
                cosmeticId = "student",
                cosmeticType = CosmeticType.TITLE,
                name = "Student",
                description = "One who has committed to learning",
                rarity = CosmeticRarity.COMMON,
                triggerCategory = TriggerCategory.LEVEL,
                unlockReason = "Reached Level 3",
                celebrationMessage = "You've earned the Student title"
            )
        ),
        5 to listOf(
            UnlockedCosmetic(
                cosmeticId = "contemplative_garden",
                cosmeticType = CosmeticType.BANNER,
                name = "Contemplative Garden",
                description = "A place of reflection and growth",
                rarity = CosmeticRarity.RARE,
                triggerCategory = TriggerCategory.LEVEL,
                unlockReason = "Reached Level 5",
                celebrationMessage = "Your garden of wisdom grows"
            ),
            UnlockedCosmetic(
                cosmeticId = "practitioner",
                cosmeticType = CosmeticType.TITLE,
                name = "Practitioner",
                description = "One who puts wisdom into action",
                rarity = CosmeticRarity.RARE,
                triggerCategory = TriggerCategory.LEVEL,
                unlockReason = "Reached Level 5",
                celebrationMessage = "Wisdom in action"
            )
        ),
        7 to listOf(
            UnlockedCosmetic(
                cosmeticId = "sages_vista",
                cosmeticType = CosmeticType.BANNER,
                name = "Sage's Vista",
                description = "The view from heights of understanding",
                rarity = CosmeticRarity.EPIC,
                triggerCategory = TriggerCategory.LEVEL,
                unlockReason = "Reached Level 7",
                celebrationMessage = "The sage's view reveals itself"
            )
        ),
        10 to listOf(
            UnlockedCosmetic(
                cosmeticId = "awakened_sky",
                cosmeticType = CosmeticType.BANNER,
                name = "Awakened Sky",
                description = "The infinite expanse of realized potential",
                rarity = CosmeticRarity.LEGENDARY,
                triggerCategory = TriggerCategory.LEVEL,
                unlockReason = "Reached Level 10",
                celebrationMessage = "You have reached the highest level"
            ),
            UnlockedCosmetic(
                cosmeticId = "awakened",
                cosmeticType = CosmeticType.TITLE,
                name = "Awakened",
                description = "One who has achieved full awareness",
                rarity = CosmeticRarity.LEGENDARY,
                triggerCategory = TriggerCategory.LEVEL,
                unlockReason = "Reached Level 10",
                celebrationMessage = "The title of the awakened is yours"
            ),
            UnlockedCosmetic(
                cosmeticId = "radiant_circle",
                cosmeticType = CosmeticType.FRAME,
                name = "Radiant Circle",
                description = "A frame of pure light",
                rarity = CosmeticRarity.LEGENDARY,
                triggerCategory = TriggerCategory.LEVEL,
                unlockReason = "Reached Level 10",
                celebrationMessage = "Your radiance shines through"
            )
        )
    )

    // =========================================================================
    // Time-Based Triggers
    // =========================================================================

    /**
     * Time milestones that unlock cosmetics.
     */
    private val timeTriggers: Map<Int, List<UnlockedCosmetic>> = mapOf(
        7 to listOf(
            UnlockedCosmetic(
                cosmeticId = "week_one",
                cosmeticType = CosmeticType.BANNER,
                name = "First Week",
                description = "Seven days of possibility",
                rarity = CosmeticRarity.COMMON,
                triggerCategory = TriggerCategory.TIME,
                unlockReason = "7 days on Prody",
                celebrationMessage = "Your first week is complete"
            )
        ),
        30 to listOf(
            UnlockedCosmetic(
                cosmeticId = "month_one",
                cosmeticType = CosmeticType.BANNER,
                name = "First Moon",
                description = "One lunar cycle of growth",
                rarity = CosmeticRarity.RARE,
                triggerCategory = TriggerCategory.TIME,
                unlockReason = "30 days on Prody",
                celebrationMessage = "One moon cycle complete"
            ),
            UnlockedCosmetic(
                cosmeticId = "dedicated",
                cosmeticType = CosmeticType.TITLE,
                name = "Dedicated",
                description = "A month of commitment",
                rarity = CosmeticRarity.RARE,
                triggerCategory = TriggerCategory.TIME,
                unlockReason = "30 days on Prody",
                celebrationMessage = "Your dedication shines"
            )
        ),
        90 to listOf(
            UnlockedCosmetic(
                cosmeticId = "quarter_year",
                cosmeticType = CosmeticType.BANNER,
                name = "Season's Turn",
                description = "A full season on the path",
                rarity = CosmeticRarity.EPIC,
                triggerCategory = TriggerCategory.TIME,
                unlockReason = "90 days on Prody",
                celebrationMessage = "A full season of growth"
            )
        ),
        365 to listOf(
            UnlockedCosmetic(
                cosmeticId = "year_one",
                cosmeticType = CosmeticType.BANNER,
                name = "Full Circle",
                description = "One complete orbit of growth and reflection",
                rarity = CosmeticRarity.LEGENDARY,
                triggerCategory = TriggerCategory.TIME,
                unlockReason = "365 days on Prody",
                celebrationMessage = "A full year complete - you are truly dedicated"
            ),
            UnlockedCosmetic(
                cosmeticId = "perennial",
                cosmeticType = CosmeticType.TITLE,
                name = "Perennial",
                description = "One who returns season after season",
                rarity = CosmeticRarity.LEGENDARY,
                triggerCategory = TriggerCategory.TIME,
                unlockReason = "365 days on Prody",
                celebrationMessage = "Your dedication spans a full year"
            )
        )
    )

    // =========================================================================
    // Streak-Based Triggers
    // =========================================================================

    private val streakTriggers: Map<Int, List<UnlockedCosmetic>> = mapOf(
        7 to listOf(
            UnlockedCosmetic(
                cosmeticId = "week_streak_frame",
                cosmeticType = CosmeticType.FRAME,
                name = "Week Warrior",
                description = "First week streak complete",
                rarity = CosmeticRarity.COMMON,
                triggerCategory = TriggerCategory.BEHAVIOR,
                unlockReason = "7-day streak",
                celebrationMessage = "Your first week streak!"
            )
        ),
        30 to listOf(
            UnlockedCosmetic(
                cosmeticId = "flame_keeper",
                cosmeticType = CosmeticType.BANNER,
                name = "Flame Keeper",
                description = "For those who maintain the sacred fire of consistency",
                rarity = CosmeticRarity.RARE,
                triggerCategory = TriggerCategory.BEHAVIOR,
                unlockReason = "30-day streak",
                celebrationMessage = "The flame burns bright"
            ),
            UnlockedCosmetic(
                cosmeticId = "consistent",
                cosmeticType = CosmeticType.TITLE,
                name = "Consistent",
                description = "30 days of unbroken dedication",
                rarity = CosmeticRarity.RARE,
                triggerCategory = TriggerCategory.BEHAVIOR,
                unlockReason = "30-day streak",
                celebrationMessage = "Consistency is key"
            )
        ),
        100 to listOf(
            UnlockedCosmetic(
                cosmeticId = "century_frame",
                cosmeticType = CosmeticType.FRAME,
                name = "Century Ring",
                description = "100 days of continuous dedication",
                rarity = CosmeticRarity.EPIC,
                triggerCategory = TriggerCategory.BEHAVIOR,
                unlockReason = "100-day streak",
                celebrationMessage = "A century of dedication"
            )
        ),
        365 to listOf(
            UnlockedCosmetic(
                cosmeticId = "eternal_flame",
                cosmeticType = CosmeticType.BANNER,
                name = "Eternal Flame",
                description = "A year of unbroken dedication",
                rarity = CosmeticRarity.LEGENDARY,
                triggerCategory = TriggerCategory.BEHAVIOR,
                unlockReason = "365-day streak",
                celebrationMessage = "The eternal flame burns within you"
            ),
            UnlockedCosmetic(
                cosmeticId = "undying",
                cosmeticType = CosmeticType.TITLE,
                name = "Undying",
                description = "Your commitment knows no end",
                rarity = CosmeticRarity.LEGENDARY,
                triggerCategory = TriggerCategory.BEHAVIOR,
                unlockReason = "365-day streak",
                celebrationMessage = "Your dedication is legendary"
            )
        )
    )

    // =========================================================================
    // Unlock Checking
    // =========================================================================

    /**
     * Checks for newly unlocked cosmetics based on state change.
     *
     * @param previousState User state before the change
     * @param currentState User state after the change
     * @return List of newly unlocked cosmetics
     */
    fun checkForNewUnlocks(
        previousState: UserState,
        currentState: UserState
    ): List<UnlockedCosmetic> {
        val newUnlocks = mutableListOf<UnlockedCosmetic>()

        // Check level triggers
        levelTriggers.forEach { (level, cosmetics) ->
            if (previousState.level < level && currentState.level >= level) {
                newUnlocks.addAll(cosmetics)
            }
        }

        // Check time triggers
        timeTriggers.forEach { (days, cosmetics) ->
            if (previousState.daysOnApp < days && currentState.daysOnApp >= days) {
                newUnlocks.addAll(cosmetics)
            }
        }

        // Check streak triggers
        streakTriggers.forEach { (streak, cosmetics) ->
            if (previousState.currentStreak < streak && currentState.currentStreak >= streak) {
                newUnlocks.addAll(cosmetics)
            }
        }

        // Check achievement-based unlocks
        val newAchievements = currentState.unlockedAchievementIds - previousState.unlockedAchievementIds
        newUnlocks.addAll(checkAchievementUnlocks(newAchievements))

        // Check special status unlocks
        newUnlocks.addAll(checkSpecialStatusUnlocks(previousState, currentState))

        return newUnlocks
    }

    /**
     * Checks for cosmetics unlocked by achievements.
     */
    private fun checkAchievementUnlocks(newAchievementIds: Set<String>): List<UnlockedCosmetic> {
        val unlocks = mutableListOf<UnlockedCosmetic>()

        // Map achievement IDs to cosmetic unlocks
        val achievementCosmeticMap = mapOf(
            "word_collector_100" to UnlockedCosmetic(
                cosmeticId = "word_garden",
                cosmeticType = CosmeticType.BANNER,
                name = "Word Garden",
                description = "A place where language blooms",
                rarity = CosmeticRarity.RARE,
                triggerCategory = TriggerCategory.ACHIEVEMENT,
                unlockReason = "Learned 100 words",
                celebrationMessage = "Your vocabulary garden grows"
            ),
            "journal_100" to UnlockedCosmetic(
                cosmeticId = "inner_sanctum",
                cosmeticType = CosmeticType.BANNER,
                name = "Inner Sanctum",
                description = "The quiet space where you meet yourself",
                rarity = CosmeticRarity.RARE,
                triggerCategory = TriggerCategory.ACHIEVEMENT,
                unlockReason = "100 journal entries",
                celebrationMessage = "Your inner world deepens"
            ),
            "buddha_100" to UnlockedCosmetic(
                cosmeticId = "buddha_companion",
                cosmeticType = CosmeticType.BANNER,
                name = "Wisdom Companion",
                description = "For those who walk with the sage",
                rarity = CosmeticRarity.EPIC,
                triggerCategory = TriggerCategory.ACHIEVEMENT,
                unlockReason = "100 Buddha conversations",
                celebrationMessage = "Wisdom is your companion"
            ),
            "early_bird" to UnlockedCosmetic(
                cosmeticId = "dawn_seeker",
                cosmeticType = CosmeticType.BANNER,
                name = "Dawn Seeker",
                description = "Colors of the early morning light",
                rarity = CosmeticRarity.RARE,
                triggerCategory = TriggerCategory.BEHAVIOR,
                unlockReason = "Early morning sessions",
                celebrationMessage = "You greet each dawn with purpose"
            ),
            "night_owl" to UnlockedCosmetic(
                cosmeticId = "night_contemplative",
                cosmeticType = CosmeticType.BANNER,
                name = "Night Contemplative",
                description = "The deep colors of evening reflection",
                rarity = CosmeticRarity.RARE,
                triggerCategory = TriggerCategory.BEHAVIOR,
                unlockReason = "Late night sessions",
                celebrationMessage = "The quiet hours are yours"
            )
        )

        newAchievementIds.forEach { achievementId ->
            achievementCosmeticMap[achievementId]?.let { unlocks.add(it) }
        }

        return unlocks
    }

    /**
     * Checks for special status cosmetic unlocks.
     */
    private fun checkSpecialStatusUnlocks(
        previousState: UserState,
        currentState: UserState
    ): List<UnlockedCosmetic> {
        val unlocks = mutableListOf<UnlockedCosmetic>()

        // DEV status
        if (!previousState.isDevBadgeHolder && currentState.isDevBadgeHolder) {
            unlocks.addAll(
                listOf(
                    UnlockedCosmetic(
                        cosmeticId = "dev_code_flow",
                        cosmeticType = CosmeticType.BANNER,
                        name = "Code Flow",
                        description = "For those who built the path",
                        rarity = CosmeticRarity.LEGENDARY,
                        triggerCategory = TriggerCategory.SPECIAL,
                        unlockReason = "DEV badge holder",
                        celebrationMessage = "Welcome, creator"
                    ),
                    UnlockedCosmetic(
                        cosmeticId = "dev_creator",
                        cosmeticType = CosmeticType.FRAME,
                        name = "Creator's Mark",
                        description = "The mark of those who build",
                        rarity = CosmeticRarity.LEGENDARY,
                        triggerCategory = TriggerCategory.SPECIAL,
                        unlockReason = "DEV badge holder",
                        celebrationMessage = "Your creator mark awaits"
                    ),
                    UnlockedCosmetic(
                        cosmeticId = "creator",
                        cosmeticType = CosmeticType.TITLE,
                        name = "Creator",
                        description = "One who builds worlds",
                        rarity = CosmeticRarity.LEGENDARY,
                        triggerCategory = TriggerCategory.SPECIAL,
                        unlockReason = "DEV badge holder",
                        celebrationMessage = "The Creator title is yours"
                    )
                )
            )
        }

        // Founder status
        if (!previousState.isFounder && currentState.isFounder) {
            unlocks.add(
                UnlockedCosmetic(
                    cosmeticId = "founder",
                    cosmeticType = CosmeticType.BANNER,
                    name = "Founding Light",
                    description = "For those who believed from the beginning",
                    rarity = CosmeticRarity.LEGENDARY,
                    triggerCategory = TriggerCategory.SPECIAL,
                    unlockReason = "Founding member",
                    celebrationMessage = "Thank you for believing"
                )
            )
        }

        // Beta tester status
        if (!previousState.isBetaTester && currentState.isBetaTester) {
            unlocks.add(
                UnlockedCosmetic(
                    cosmeticId = "beta_pioneer",
                    cosmeticType = CosmeticType.BANNER,
                    name = "Pioneer's Path",
                    description = "Among the first explorers",
                    rarity = CosmeticRarity.EPIC,
                    triggerCategory = TriggerCategory.SPECIAL,
                    unlockReason = "Beta tester",
                    celebrationMessage = "Thank you for pioneering"
                )
            )
        }

        return unlocks
    }

    // =========================================================================
    // Utility Functions
    // =========================================================================

    /**
     * Gets all cosmetics available at a given user state.
     */
    fun getAllAvailableCosmetics(state: UserState): List<String> {
        val available = mutableListOf<String>()

        // Level-based
        levelTriggers.filter { it.key <= state.level }
            .flatMap { it.value }
            .forEach { available.add(it.cosmeticId) }

        // Time-based
        timeTriggers.filter { it.key <= state.daysOnApp }
            .flatMap { it.value }
            .forEach { available.add(it.cosmeticId) }

        // Streak-based
        val maxStreak = maxOf(state.currentStreak, state.longestStreak)
        streakTriggers.filter { it.key <= maxStreak }
            .flatMap { it.value }
            .forEach { available.add(it.cosmeticId) }

        return available.distinct()
    }

    /**
     * Gets the next unlock milestone for a user.
     */
    fun getNextMilestone(state: UserState): NextMilestone? {
        // Check closest level milestone
        val nextLevel = levelTriggers.keys.filter { it > state.level }.minOrNull()
        val levelProgress = nextLevel?.let {
            val currentPoints = state.level
            NextMilestone(
                type = "Level $it",
                current = currentPoints,
                target = it,
                cosmetics = levelTriggers[it]?.map { c -> c.name } ?: emptyList()
            )
        }

        // Check closest time milestone
        val nextTime = timeTriggers.keys.filter { it > state.daysOnApp }.minOrNull()
        val timeProgress = nextTime?.let {
            NextMilestone(
                type = "$it days",
                current = state.daysOnApp,
                target = it,
                cosmetics = timeTriggers[it]?.map { c -> c.name } ?: emptyList()
            )
        }

        // Check closest streak milestone
        val nextStreak = streakTriggers.keys.filter { it > state.currentStreak }.minOrNull()
        val streakProgress = nextStreak?.let {
            NextMilestone(
                type = "$it-day streak",
                current = state.currentStreak,
                target = it,
                cosmetics = streakTriggers[it]?.map { c -> c.name } ?: emptyList()
            )
        }

        // Return the closest milestone
        return listOfNotNull(levelProgress, timeProgress, streakProgress)
            .minByOrNull { it.target - it.current }
    }

    /**
     * Represents the next unlock milestone.
     */
    data class NextMilestone(
        val type: String,
        val current: Int,
        val target: Int,
        val cosmetics: List<String>
    ) {
        val progress: Float get() = current.toFloat() / target
        val remaining: Int get() = target - current
    }
}
