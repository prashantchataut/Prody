package com.prody.prashant.domain.identity

/**
 * [ProfileLoadout] - Profile "look" configuration system for Prody
 *
 * Users can save 2-3 profile "looks" and switch instantly between them.
 * A loadout contains:
 * - Banner
 * - Frame
 * - Title
 * - Pinned badges set (3-5)
 * - Accent color
 *
 * Why this works:
 * - Creates immediate personalization
 * - Makes the profile feel intentional
 * - Makes sharing more fun
 *
 * Implementation notes:
 * - Minimal data model (single table)
 * - No new screens needed beyond a "Customize" modal
 */
object ProfileLoadout {

    /**
     * Maximum number of loadouts a user can save.
     */
    const val MAX_LOADOUTS = 3

    /**
     * Maximum number of pinned badges in a loadout.
     */
    const val MAX_PINNED_BADGES = 5

    /**
     * Minimum number of pinned badges in a loadout.
     */
    const val MIN_PINNED_BADGES = 0

    /**
     * Default number of pinned badges shown.
     */
    const val DEFAULT_PINNED_BADGES = 3

    /**
     * Loadout configuration data class.
     *
     * @property id Unique identifier for this loadout
     * @property name User-defined name for this loadout (e.g., "My Look", "Professional")
     * @property bannerId Selected banner ID from ProdyBanners
     * @property frameId Selected frame ID from ProdyFrames
     * @property titleId Selected title ID from ProdyTitles
     * @property accentColorId Selected accent color ID from AccentColors
     * @property pinnedBadgeIds List of pinned badge/achievement IDs (3-5 badges)
     * @property isActive Whether this is the currently active loadout
     * @property createdAt Timestamp when this loadout was created
     * @property updatedAt Timestamp when this loadout was last modified
     * @property sortOrder Display order in loadout list
     */
    data class Loadout(
        val id: String,
        val name: String,
        val bannerId: String,
        val frameId: String,
        val titleId: String,
        val accentColorId: String,
        val pinnedBadgeIds: List<String>,
        val isActive: Boolean = false,
        val createdAt: Long = System.currentTimeMillis(),
        val updatedAt: Long = System.currentTimeMillis(),
        val sortOrder: Int = 0
    ) {
        /**
         * Returns the number of pinned badges.
         */
        val pinnedBadgeCount: Int
            get() = pinnedBadgeIds.size

        /**
         * Validates that the loadout is within constraints.
         */
        fun isValid(): Boolean {
            return name.isNotBlank() &&
                    name.length <= 20 &&
                    bannerId.isNotBlank() &&
                    frameId.isNotBlank() &&
                    titleId.isNotBlank() &&
                    accentColorId.isNotBlank() &&
                    pinnedBadgeIds.size <= MAX_PINNED_BADGES
        }

        /**
         * Creates a copy with updated timestamp.
         */
        fun withUpdatedTimestamp(): Loadout = copy(updatedAt = System.currentTimeMillis())

        /**
         * Creates a copy with a new pinned badge added (if under limit).
         */
        fun withPinnedBadge(badgeId: String): Loadout {
            if (pinnedBadgeIds.size >= MAX_PINNED_BADGES || pinnedBadgeIds.contains(badgeId)) {
                return this
            }
            return copy(
                pinnedBadgeIds = pinnedBadgeIds + badgeId,
                updatedAt = System.currentTimeMillis()
            )
        }

        /**
         * Creates a copy with a pinned badge removed.
         */
        fun withoutPinnedBadge(badgeId: String): Loadout {
            return copy(
                pinnedBadgeIds = pinnedBadgeIds.filter { it != badgeId },
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    /**
     * Default loadout names for new users.
     */
    val defaultLoadoutNames = listOf("Primary", "Alternate", "Custom")

    /**
     * Creates a default loadout for new users.
     */
    fun createDefaultLoadout(
        id: String = generateLoadoutId(),
        name: String = defaultLoadoutNames[0],
        isActive: Boolean = true
    ): Loadout {
        return Loadout(
            id = id,
            name = name,
            bannerId = ProdyBanners.getDefaultBanner().id,
            frameId = ProdyFrames.getDefaultFrame().id,
            titleId = ProdyTitles.getDefaultTitle().id,
            accentColorId = AccentColors.getDefaultAccent().id,
            pinnedBadgeIds = emptyList(),
            isActive = isActive,
            sortOrder = 0
        )
    }

    /**
     * Generates a unique loadout ID.
     */
    fun generateLoadoutId(): String {
        return "loadout_${System.currentTimeMillis()}_${(0..999).random()}"
    }

    /**
     * Validates a list of loadouts to ensure only one is active.
     */
    fun validateLoadouts(loadouts: List<Loadout>): List<Loadout> {
        if (loadouts.isEmpty()) return loadouts

        val activeCount = loadouts.count { it.isActive }

        return when {
            activeCount == 0 -> {
                // Make the first one active
                loadouts.mapIndexed { index, loadout ->
                    if (index == 0) loadout.copy(isActive = true) else loadout
                }
            }
            activeCount > 1 -> {
                // Only keep the first active one
                var foundActive = false
                loadouts.map { loadout ->
                    if (loadout.isActive && !foundActive) {
                        foundActive = true
                        loadout
                    } else {
                        loadout.copy(isActive = false)
                    }
                }
            }
            else -> loadouts
        }
    }
}

/**
 * [LoadoutPreview] - Preview data for displaying a loadout in selection UI
 */
data class LoadoutPreview(
    val id: String,
    val name: String,
    val banner: ProdyBanners.Banner?,
    val frame: ProdyFrames.Frame?,
    val title: ProdyTitles.Title?,
    val accentColor: AccentColors.AccentColor?,
    val pinnedBadgeCount: Int,
    val isActive: Boolean
) {
    companion object {
        /**
         * Creates a preview from a loadout.
         */
        fun fromLoadout(loadout: ProfileLoadout.Loadout): LoadoutPreview {
            return LoadoutPreview(
                id = loadout.id,
                name = loadout.name,
                banner = ProdyBanners.findById(loadout.bannerId),
                frame = ProdyFrames.findById(loadout.frameId),
                title = ProdyTitles.findById(loadout.titleId),
                accentColor = AccentColors.findById(loadout.accentColorId),
                pinnedBadgeCount = loadout.pinnedBadgeCount,
                isActive = loadout.isActive
            )
        }
    }
}

/**
 * [ActiveCosmetics] - Currently equipped cosmetics for easy access
 *
 * This represents the currently active cosmetic configuration,
 * resolved from the active loadout or defaults.
 */
data class ActiveCosmetics(
    val banner: ProdyBanners.Banner,
    val frame: ProdyFrames.Frame,
    val title: ProdyTitles.Title,
    val accentColor: AccentColors.AccentColor,
    val pinnedBadgeIds: List<String>
) {
    companion object {
        /**
         * Creates active cosmetics from a loadout, falling back to defaults.
         */
        fun fromLoadout(loadout: ProfileLoadout.Loadout?): ActiveCosmetics {
            val banner = loadout?.bannerId?.let { ProdyBanners.findById(it) }
                ?: ProdyBanners.getDefaultBanner()
            val frame = loadout?.frameId?.let { ProdyFrames.findById(it) }
                ?: ProdyFrames.getDefaultFrame()
            val title = loadout?.titleId?.let { ProdyTitles.findById(it) }
                ?: ProdyTitles.getDefaultTitle()
            val accentColor = loadout?.accentColorId?.let { AccentColors.findById(it) }
                ?: AccentColors.getDefaultAccent()

            return ActiveCosmetics(
                banner = banner,
                frame = frame,
                title = title,
                accentColor = accentColor,
                pinnedBadgeIds = loadout?.pinnedBadgeIds ?: emptyList()
            )
        }

        /**
         * Creates default cosmetics.
         */
        fun defaults(): ActiveCosmetics {
            return ActiveCosmetics(
                banner = ProdyBanners.getDefaultBanner(),
                frame = ProdyFrames.getDefaultFrame(),
                title = ProdyTitles.getDefaultTitle(),
                accentColor = AccentColors.getDefaultAccent(),
                pinnedBadgeIds = emptyList()
            )
        }
    }
}
