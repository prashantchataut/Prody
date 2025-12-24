package com.prody.prashant.domain.identity

/**
 * [CosmeticRarity] - Premium rarity language for all cosmetic items
 *
 * Rarity determines the visual treatment of cosmetics across the app.
 * Designed to be premium and understated, not childish or game-like.
 *
 * Visual Philosophy:
 * - Common: clean, matte - no effects
 * - Rare: subtle border treatment + slightly richer texture
 * - Epic: tasteful accent line + slow motion detail (not glow spam)
 * - Legendary: Quiet Gold + subtle animated sheen (very slow, barely there)
 *
 * This is a "made by pros" signal - avoiding loud gradients and sparkles.
 */
enum class CosmeticRarity(
    val id: String,
    val displayName: String,
    val sortOrder: Int,
    /** Border width multiplier (1.0 = standard, 0 = no border) */
    val borderMultiplier: Float,
    /** Texture richness (0.0 = matte, 1.0 = rich) */
    val textureRichness: Float,
    /** Animation duration in ms (0 = no animation, higher = slower/subtler) */
    val animationDurationMs: Int,
    /** Animation alpha (0.0 = invisible, 1.0 = full) - keep low for premium feel */
    val animationAlpha: Float,
    /** Primary accent color (as Long for hex storage) */
    val primaryColorHex: Long,
    /** Secondary accent color for gradients */
    val secondaryColorHex: Long,
    /** Whether this rarity uses the Quiet Gold accent */
    val usesQuietGold: Boolean = false
) {
    COMMON(
        id = "common",
        displayName = "Common",
        sortOrder = 0,
        borderMultiplier = 0f,
        textureRichness = 0f,
        animationDurationMs = 0,
        animationAlpha = 0f,
        primaryColorHex = 0xFF9E9E9E,  // Clean gray
        secondaryColorHex = 0xFFBDBDBD
    ),

    RARE(
        id = "rare",
        displayName = "Rare",
        sortOrder = 1,
        borderMultiplier = 1.0f,
        textureRichness = 0.3f,
        animationDurationMs = 0,
        animationAlpha = 0f,
        primaryColorHex = 0xFF42A5F5,  // Clean blue
        secondaryColorHex = 0xFF64B5F6
    ),

    EPIC(
        id = "epic",
        displayName = "Epic",
        sortOrder = 2,
        borderMultiplier = 1.2f,
        textureRichness = 0.5f,
        animationDurationMs = 4000,    // Very slow
        animationAlpha = 0.15f,        // Very subtle
        primaryColorHex = 0xFF9C27B0,  // Rich purple
        secondaryColorHex = 0xFFBA68C8
    ),

    LEGENDARY(
        id = "legendary",
        displayName = "Legendary",
        sortOrder = 3,
        borderMultiplier = 1.5f,
        textureRichness = 0.7f,
        animationDurationMs = 6000,    // Extremely slow for subtle sheen
        animationAlpha = 0.2f,         // Barely there
        primaryColorHex = 0xFFD4AF37,  // Quiet Gold
        secondaryColorHex = 0xFFF5E6B3,
        usesQuietGold = true
    );

    companion object {
        fun fromId(id: String): CosmeticRarity? = entries.find { it.id == id }

        fun fromSortOrder(order: Int): CosmeticRarity? = entries.find { it.sortOrder == order }

        /** Get all rarities sorted by prestige (lowest to highest) */
        val sortedByPrestige: List<CosmeticRarity>
            get() = entries.sortedBy { it.sortOrder }
    }
}
