package com.prody.prashant.domain.identity

/**
 * [BadgePinboard] - Hero badge display system
 *
 * The pinboard displays 3-5 carefully selected badges that represent
 * the user's identity and achievements. Unlike a grid of all badges,
 * this is a curated showcase of what matters most to the user.
 *
 * Design Philosophy:
 * - Less is more: 3-5 badges, not 20+
 * - Horizontal layout: badges flow naturally, not in a grid
 * - Meaningful selection: users choose their "hero" badges
 * - Consistent display: appears on profile, leaderboard, share cards
 *
 * The pinboard is separate from loadouts - it's the "master" badge selection
 * that can optionally be overridden per loadout.
 *
 * Example usage:
 * ```
 * val pinboard = BadgePinboard.create(
 *     pinnedBadgeIds = listOf("streak_30", "word_collector_100", "founder"),
 *     availableBadges = unlockedAchievements
 * )
 *
 * // Render pinboard
 * pinboard.displayBadges.forEach { badge ->
 *     BadgeChip(badge = badge, rarity = badge.rarity)
 * }
 * ```
 */
object BadgePinboard {

    /** Maximum badges that can be pinned */
    const val MAX_PINNED_BADGES = 5

    /** Minimum badges required for a complete pinboard look */
    const val MIN_PINNED_BADGES = 1

    /** Recommended number of pinned badges for best visual */
    const val RECOMMENDED_PINNED_BADGES = 3

    /**
     * Display context for where the pinboard appears.
     * Different contexts may show different numbers of badges.
     */
    enum class DisplayContext {
        /** Full pinboard on profile (shows all pinned) */
        PROFILE_FULL,
        /** Compact pinboard on leaderboard row (shows max 3) */
        LEADERBOARD_ROW,
        /** Pinboard on share cards (shows max 3) */
        SHARE_CARD,
        /** Mini pinboard showing only top 2 */
        COMPACT
    }

    /**
     * Represents a pinned badge with display metadata.
     *
     * @property badgeId The achievement/badge ID
     * @property displayOrder Order in the pinboard (0 = first/leftmost)
     * @property pinnedAt When this badge was pinned
     */
    data class PinnedBadge(
        val badgeId: String,
        val displayOrder: Int,
        val pinnedAt: Long = System.currentTimeMillis()
    )

    /**
     * Resolved badge ready for display with full metadata.
     *
     * @property badgeId Achievement ID
     * @property name Display name
     * @property iconId Icon resource identifier
     * @property rarity Cosmetic rarity for styling
     * @property displayOrder Position in pinboard
     * @property isSpecial True for special badges (DEV, Founder, Beta)
     */
    data class DisplayBadge(
        val badgeId: String,
        val name: String,
        val iconId: String,
        val rarity: CosmeticRarity,
        val displayOrder: Int,
        val isSpecial: Boolean = false,
        val tooltipText: String = ""
    )

    /**
     * The complete pinboard state.
     *
     * @property pinnedBadges Ordered list of pinned badges
     * @property maxSlots Maximum slots available (may vary by user status)
     */
    data class Pinboard(
        val pinnedBadges: List<PinnedBadge>,
        val maxSlots: Int = MAX_PINNED_BADGES
    ) {
        /** Number of currently pinned badges */
        val pinnedCount: Int get() = pinnedBadges.size

        /** Whether the pinboard has room for more badges */
        val hasAvailableSlots: Boolean get() = pinnedCount < maxSlots

        /** Number of available slots */
        val availableSlots: Int get() = maxSlots - pinnedCount

        /** Whether the pinboard is empty */
        val isEmpty: Boolean get() = pinnedBadges.isEmpty()

        /**
         * Checks if a badge is already pinned.
         */
        fun isPinned(badgeId: String): Boolean =
            pinnedBadges.any { it.badgeId == badgeId }

        /**
         * Gets the pinned badge at a specific position.
         */
        fun getBadgeAt(position: Int): PinnedBadge? =
            pinnedBadges.find { it.displayOrder == position }

        /**
         * Gets badge IDs in display order.
         */
        fun getBadgeIds(): List<String> =
            pinnedBadges.sortedBy { it.displayOrder }.map { it.badgeId }

        /**
         * Gets badges limited by display context.
         */
        fun getBadgesForContext(context: DisplayContext): List<PinnedBadge> {
            val maxForContext = when (context) {
                DisplayContext.PROFILE_FULL -> maxSlots
                DisplayContext.LEADERBOARD_ROW -> 3
                DisplayContext.SHARE_CARD -> 3
                DisplayContext.COMPACT -> 2
            }
            return pinnedBadges
                .sortedBy { it.displayOrder }
                .take(maxForContext)
        }

        /**
         * Creates a new pinboard with a badge added.
         * Returns null if no slots available or badge already pinned.
         */
        fun withBadgePinned(badgeId: String): Pinboard? {
            if (!hasAvailableSlots || isPinned(badgeId)) return null
            val newOrder = (pinnedBadges.maxOfOrNull { it.displayOrder } ?: -1) + 1
            val newBadge = PinnedBadge(badgeId = badgeId, displayOrder = newOrder)
            return copy(pinnedBadges = pinnedBadges + newBadge)
        }

        /**
         * Creates a new pinboard with a badge removed.
         */
        fun withBadgeUnpinned(badgeId: String): Pinboard {
            val filtered = pinnedBadges.filter { it.badgeId != badgeId }
            // Reorder remaining badges
            val reordered = filtered
                .sortedBy { it.displayOrder }
                .mapIndexed { index, badge -> badge.copy(displayOrder = index) }
            return copy(pinnedBadges = reordered)
        }

        /**
         * Creates a new pinboard with badges reordered.
         */
        fun withReorderedBadges(newOrder: List<String>): Pinboard {
            val reordered = newOrder.mapIndexedNotNull { index, badgeId ->
                pinnedBadges.find { it.badgeId == badgeId }?.copy(displayOrder = index)
            }
            return copy(pinnedBadges = reordered)
        }

        /**
         * Creates a new pinboard replacing a badge at a position.
         */
        fun withBadgeReplaced(position: Int, newBadgeId: String): Pinboard? {
            if (isPinned(newBadgeId)) return null
            val updated = pinnedBadges.map { badge ->
                if (badge.displayOrder == position) {
                    badge.copy(badgeId = newBadgeId, pinnedAt = System.currentTimeMillis())
                } else {
                    badge
                }
            }
            return copy(pinnedBadges = updated)
        }
    }

    /**
     * Creates an empty pinboard.
     */
    fun createEmpty(maxSlots: Int = MAX_PINNED_BADGES): Pinboard =
        Pinboard(pinnedBadges = emptyList(), maxSlots = maxSlots)

    /**
     * Creates a pinboard from a list of badge IDs.
     * Respects the order of the input list.
     */
    fun createFromIds(
        badgeIds: List<String>,
        maxSlots: Int = MAX_PINNED_BADGES
    ): Pinboard {
        val pinnedBadges = badgeIds
            .take(maxSlots)
            .mapIndexed { index, badgeId ->
                PinnedBadge(badgeId = badgeId, displayOrder = index)
            }
        return Pinboard(pinnedBadges = pinnedBadges, maxSlots = maxSlots)
    }

    /**
     * Creates a pinboard from comma-separated string (for database storage).
     */
    fun createFromString(
        commaSeparatedIds: String,
        maxSlots: Int = MAX_PINNED_BADGES
    ): Pinboard {
        if (commaSeparatedIds.isBlank()) return createEmpty(maxSlots)
        val ids = commaSeparatedIds.split(",").filter { it.isNotBlank() }
        return createFromIds(ids, maxSlots)
    }

    /**
     * Converts pinboard to comma-separated string for storage.
     */
    fun Pinboard.toStorageString(): String =
        getBadgeIds().joinToString(",")

    /**
     * Validates that all pinned badges are actually unlocked.
     * Returns a filtered pinboard with only valid badges.
     */
    fun Pinboard.filterToUnlocked(unlockedBadgeIds: Set<String>): Pinboard {
        val validBadges = pinnedBadges
            .filter { unlockedBadgeIds.contains(it.badgeId) }
            .mapIndexed { index, badge -> badge.copy(displayOrder = index) }
        return copy(pinnedBadges = validBadges)
    }

    /**
     * Special badge identifiers for premium treatment.
     */
    object SpecialBadges {
        const val DEV_BADGE = "dev_badge"
        const val FOUNDER_BADGE = "founder"
        const val BETA_TESTER_BADGE = "beta_tester"

        val ALL_SPECIAL = setOf(DEV_BADGE, FOUNDER_BADGE, BETA_TESTER_BADGE)

        /**
         * Checks if a badge ID is a special badge.
         */
        fun isSpecial(badgeId: String): Boolean = ALL_SPECIAL.contains(badgeId)

        /**
         * Gets the rarity for a special badge.
         */
        fun getRarity(badgeId: String): CosmeticRarity = when (badgeId) {
            DEV_BADGE -> CosmeticRarity.LEGENDARY
            FOUNDER_BADGE -> CosmeticRarity.LEGENDARY
            BETA_TESTER_BADGE -> CosmeticRarity.EPIC
            else -> CosmeticRarity.COMMON
        }
    }

    /**
     * Helper to determine badge rarity based on achievement data.
     * Maps achievement rarities to cosmetic rarity.
     */
    fun getDisplayRarity(achievementRarity: String, badgeId: String): CosmeticRarity {
        // Special badges always use their designated rarity
        if (SpecialBadges.isSpecial(badgeId)) {
            return SpecialBadges.getRarity(badgeId)
        }

        // Map achievement rarity string to CosmeticRarity
        return when (achievementRarity.lowercase()) {
            "legendary" -> CosmeticRarity.LEGENDARY
            "epic" -> CosmeticRarity.EPIC
            "rare" -> CosmeticRarity.RARE
            else -> CosmeticRarity.COMMON
        }
    }

    /**
     * Gets a suggested initial pinboard for new users.
     * Shows their first achievements to encourage progression.
     */
    fun getSuggestedInitialBadges(unlockedBadgeIds: Set<String>): List<String> {
        // Priority order for initial pinboard
        val priorityOrder = listOf(
            "founder",           // Founder gets top billing
            "beta_tester",       // Beta tester second
            "dev_badge",         // DEV badge if applicable
            "first_step",        // First step achievement
            "early_bird",        // Time-based
            "night_owl",         // Time-based
            "streak_7",          // First streak achievement
            "word_collector_10", // First word milestone
            "journal_1"          // First journal entry
        )

        return priorityOrder
            .filter { unlockedBadgeIds.contains(it) }
            .take(RECOMMENDED_PINNED_BADGES)
    }
}
