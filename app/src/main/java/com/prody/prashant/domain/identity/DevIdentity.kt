package com.prody.prashant.domain.identity

/**
 * [DevIdentity] - Complete DEV identity cosmetics system
 *
 * This file provides a unified API for all DEV-exclusive cosmetics.
 * The DEV identity is reserved for creators/developers and represents
 * the highest tier of identity customization in Prody.
 *
 * DEV Cosmetics Philosophy:
 * - Premium but not flashy: Quiet confidence, not "look at me"
 * - Consistent theming: All DEV cosmetics share visual DNA
 * - Subtle recognition: Those who know, know
 * - Terminal/code aesthetic: Clean, technical, elegant
 *
 * Color Palette:
 * - Primary: Deep teal (#0D2826, #1A3633)
 * - Accent: Terminal green (#36F97F)
 * - Highlight: Soft gold (#D4AF37) for legendary elements
 *
 * DEV Cosmetic Set:
 * - Banner: "Code Flow" - Flowing code texture
 * - Frame: "Creator's Mark" - Terminal-style frame
 * - Title: "Creator" - Simple, earned
 * - Accent: "Dev Gold" - Subtle gold highlight
 * - Badge: "DEV" - The original badge
 *
 * Example usage:
 * ```
 * if (user.isDevBadgeHolder) {
 *     val devCosmetics = DevIdentity.getDevCosmetics()
 *     // Apply DEV loadout
 *     profileLoadout.applyDevDefaults(devCosmetics)
 * }
 * ```
 */
object DevIdentity {

    // =========================================================================
    // DEV Cosmetic IDs
    // =========================================================================

    /** DEV badge ID */
    const val DEV_BADGE_ID = "dev_badge"

    /** DEV banner ID */
    const val DEV_BANNER_ID = "dev_code_flow"

    /** DEV frame ID */
    const val DEV_FRAME_ID = "dev_creator"

    /** DEV title ID */
    const val DEV_TITLE_ID = "creator"

    /** DEV accent color ID */
    const val DEV_ACCENT_ID = "dev_gold"

    // =========================================================================
    // DEV Color Palette
    // =========================================================================

    /**
     * DEV color palette for consistent theming.
     */
    object ColorPalette {
        /** Deep teal - primary background */
        const val PRIMARY_DARK = 0xFF0D2826L

        /** Medium teal - secondary background */
        const val PRIMARY_MEDIUM = 0xFF1A3633L

        /** Terminal green - accent/highlight */
        const val ACCENT_GREEN = 0xFF36F97FL

        /** Soft gold - legendary highlight */
        const val HIGHLIGHT_GOLD = 0xFFD4AF37L

        /** Light gold - secondary highlight */
        const val HIGHLIGHT_GOLD_LIGHT = 0xFFF5E6B3L

        /** Text on dark background */
        const val TEXT_ON_DARK = 0xFFE0E0E0L

        /** Gradient colors for DEV banner */
        val BANNER_GRADIENT = listOf(PRIMARY_DARK, PRIMARY_MEDIUM, ACCENT_GREEN)

        /** Gradient colors for DEV frame */
        val FRAME_GRADIENT = listOf(PRIMARY_DARK, ACCENT_GREEN)
    }

    // =========================================================================
    // DEV Cosmetics Data
    // =========================================================================

    /**
     * Complete DEV cosmetics set.
     */
    data class DevCosmetics(
        val bannerId: String = DEV_BANNER_ID,
        val frameId: String = DEV_FRAME_ID,
        val titleId: String = DEV_TITLE_ID,
        val accentColorId: String = DEV_ACCENT_ID,
        val badgeId: String = DEV_BADGE_ID
    ) {
        /**
         * Returns the recommended pinned badges for DEV identity.
         * DEV badge should always be first.
         */
        val recommendedPinnedBadges: List<String>
            get() = listOf(badgeId)

        /**
         * Creates a profile loadout with all DEV cosmetics.
         */
        fun toLoadout(
            loadoutId: String,
            loadoutName: String = "Creator"
        ): ProfileLoadout.Loadout {
            return ProfileLoadout.Loadout(
                id = loadoutId,
                name = loadoutName,
                bannerId = bannerId,
                frameId = frameId,
                titleId = titleId,
                accentColorId = accentColorId,
                pinnedBadgeIds = recommendedPinnedBadges,
                isActive = false
            )
        }
    }

    /**
     * Gets the complete DEV cosmetics set.
     */
    fun getDevCosmetics(): DevCosmetics = DevCosmetics()

    /**
     * Checks if a cosmetic ID belongs to the DEV set.
     */
    fun isDevCosmetic(cosmeticId: String): Boolean = cosmeticId in listOf(
        DEV_BADGE_ID,
        DEV_BANNER_ID,
        DEV_FRAME_ID,
        DEV_TITLE_ID,
        DEV_ACCENT_ID
    )

    // =========================================================================
    // DEV Cosmetic Verification
    // =========================================================================

    /**
     * Verifies DEV cosmetics are available in the system.
     * Used for debugging/validation.
     */
    fun verifyDevCosmetics(): DevCosmeticsVerification {
        val banner = ProdyBanners.findById(DEV_BANNER_ID)
        val frame = ProdyFrames.findById(DEV_FRAME_ID)
        val title = ProdyTitles.findById(DEV_TITLE_ID)
        val accent = AccentColors.findById(DEV_ACCENT_ID)

        return DevCosmeticsVerification(
            bannerExists = banner != null,
            bannerIsLegendary = banner?.rarity == CosmeticRarity.LEGENDARY,
            bannerIsAnimated = banner?.isAnimated == true,
            frameExists = frame != null,
            frameIsLegendary = frame?.rarity == CosmeticRarity.LEGENDARY,
            titleExists = title != null,
            titleIsSpecial = title?.isSpecial == true,
            accentExists = accent != null,
            accentIsSpecial = accent?.isSpecial == true
        )
    }

    /**
     * Result of DEV cosmetics verification.
     */
    data class DevCosmeticsVerification(
        val bannerExists: Boolean,
        val bannerIsLegendary: Boolean,
        val bannerIsAnimated: Boolean,
        val frameExists: Boolean,
        val frameIsLegendary: Boolean,
        val titleExists: Boolean,
        val titleIsSpecial: Boolean,
        val accentExists: Boolean,
        val accentIsSpecial: Boolean
    ) {
        val isComplete: Boolean
            get() = bannerExists && frameExists && titleExists && accentExists

        val isProperlyConfigured: Boolean
            get() = isComplete &&
                    bannerIsLegendary &&
                    bannerIsAnimated &&
                    frameIsLegendary &&
                    titleIsSpecial &&
                    accentIsSpecial
    }

    // =========================================================================
    // DEV Identity Display
    // =========================================================================

    /**
     * Display configuration for DEV identity elements.
     */
    object DisplayConfig {
        /** DEV badge displays on these surfaces */
        val BADGE_SURFACES = setOf(
            ProdyBanners.DisplaySurface.PROFILE_HEADER,
            ProdyBanners.DisplaySurface.LEADERBOARD_ROW,
            ProdyBanners.DisplaySurface.SHARE_CARD,
            ProdyBanners.DisplaySurface.MESSAGE_CARD
        )

        /** DEV cosmetics always get animation priority */
        const val ANIMATION_PRIORITY = true

        /** Subtle glow effect intensity for DEV elements */
        const val GLOW_INTENSITY = 0.15f

        /** Animation duration for DEV elements (very slow) */
        const val ANIMATION_DURATION_MS = 6000L
    }

    // =========================================================================
    // Founder & Beta Tester Identity
    // =========================================================================

    /**
     * Founder identity cosmetics.
     * Similar structure to DEV but with golden theme.
     */
    object FounderIdentity {
        const val BADGE_ID = "founder"
        const val BANNER_ID = "founder"
        const val FRAME_ID = "founder_light"
        const val TITLE_ID = "founder"
        const val ACCENT_ID = "founder_gold"

        fun getCosmetics(): SpecialCosmetics = SpecialCosmetics(
            badgeId = BADGE_ID,
            bannerId = BANNER_ID,
            frameId = FRAME_ID,
            titleId = TITLE_ID,
            accentColorId = ACCENT_ID
        )
    }

    /**
     * Beta tester identity cosmetics.
     * Green-themed to match Prody brand.
     */
    object BetaTesterIdentity {
        const val BADGE_ID = "beta_tester"
        const val BANNER_ID = "beta_pioneer"
        const val FRAME_ID = "beta_pioneer"
        const val TITLE_ID = "pioneer"
        const val ACCENT_ID = "beta_green"

        fun getCosmetics(): SpecialCosmetics = SpecialCosmetics(
            badgeId = BADGE_ID,
            bannerId = BANNER_ID,
            frameId = FRAME_ID,
            titleId = TITLE_ID,
            accentColorId = ACCENT_ID
        )
    }

    /**
     * Generic special cosmetics holder.
     */
    data class SpecialCosmetics(
        val badgeId: String,
        val bannerId: String,
        val frameId: String,
        val titleId: String,
        val accentColorId: String
    )

    // =========================================================================
    // Identity Priority
    // =========================================================================

    /**
     * Determines which special identity should take precedence
     * when a user has multiple special statuses.
     *
     * Priority (highest to lowest):
     * 1. DEV - The creators
     * 2. Founder - Early believers
     * 3. Beta Tester - Early adopters
     */
    enum class IdentityPriority(val weight: Int) {
        DEV(100),
        FOUNDER(80),
        BETA_TESTER(60),
        NONE(0)
    }

    /**
     * Gets the highest priority special identity for a user.
     */
    fun getHighestPriorityIdentity(
        isDevBadgeHolder: Boolean,
        isFounder: Boolean,
        isBetaTester: Boolean
    ): IdentityPriority = when {
        isDevBadgeHolder -> IdentityPriority.DEV
        isFounder -> IdentityPriority.FOUNDER
        isBetaTester -> IdentityPriority.BETA_TESTER
        else -> IdentityPriority.NONE
    }

    /**
     * Gets the recommended cosmetics for a user's highest priority identity.
     */
    fun getRecommendedCosmetics(
        isDevBadgeHolder: Boolean,
        isFounder: Boolean,
        isBetaTester: Boolean
    ): SpecialCosmetics? = when (getHighestPriorityIdentity(isDevBadgeHolder, isFounder, isBetaTester)) {
        IdentityPriority.DEV -> SpecialCosmetics(
            badgeId = DEV_BADGE_ID,
            bannerId = DEV_BANNER_ID,
            frameId = DEV_FRAME_ID,
            titleId = DEV_TITLE_ID,
            accentColorId = DEV_ACCENT_ID
        )
        IdentityPriority.FOUNDER -> FounderIdentity.getCosmetics()
        IdentityPriority.BETA_TESTER -> BetaTesterIdentity.getCosmetics()
        IdentityPriority.NONE -> null
    }
}
