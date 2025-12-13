package com.prody.prashant.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prody.prashant.domain.identity.ProdyAchievements
import com.prody.prashant.domain.identity.ProdyBanners

/**
 * [ProdyDesignTokens] - Comprehensive design token system for Prody
 *
 * Provides consistent visual language across the entire application.
 * All visual values should be referenced from this object to ensure
 * design consistency and ease of future updates.
 *
 * Design Philosophy:
 * - Primary actions: Deep, confident colors (not neon)
 * - Success states: Natural greens, not gaming greens
 * - Achievement glow: Subtle, warm, earned feeling
 * - Text: High contrast, easy on eyes
 * - Backgrounds: Subtle gradients, never flat
 *
 * Example usage:
 * ```
 * Box(
 *     modifier = Modifier
 *         .padding(ProdyDesignTokens.Spacing.medium)
 *         .clip(RoundedCornerShape(ProdyDesignTokens.Radius.medium))
 *         .shadow(ProdyDesignTokens.Elevation.low)
 * )
 * ```
 */
object ProdyDesignTokens {

    // ===== ELEVATION =====

    /**
     * Elevation values for shadows and depth.
     * Uses subtle shadows for a refined, non-gamified appearance.
     */
    object Elevation {
        val none: Dp = 0.dp
        val low: Dp = 2.dp
        val medium: Dp = 4.dp
        val high: Dp = 8.dp
        val highest: Dp = 12.dp

        /** Default card elevation */
        val card: Dp = low

        /** Floating action button elevation */
        val fab: Dp = medium

        /** Dialog/Modal elevation */
        val dialog: Dp = high

        /** Popup/Dropdown elevation */
        val popup: Dp = medium
    }

    // ===== CORNER RADIUS =====

    /**
     * Corner radius values for rounded elements.
     */
    object Radius {
        val none: Dp = 0.dp
        val extraSmall: Dp = 4.dp
        val small: Dp = 8.dp
        val medium: Dp = 12.dp
        val large: Dp = 16.dp
        val extraLarge: Dp = 24.dp
        val full: Dp = 100.dp // For pills and circles

        /** Default card corner radius */
        val card: Dp = medium

        /** Button corner radius */
        val button: Dp = small

        /** Input field corner radius */
        val input: Dp = small

        /** Chip corner radius */
        val chip: Dp = full

        /** Bottom sheet corner radius (top corners only) */
        val bottomSheet: Dp = extraLarge

        /** Banner bottom corner radius */
        val banner: Dp = extraLarge
    }

    // ===== SPACING =====

    /**
     * Spacing values based on 4dp grid system.
     */
    object Spacing {
        val none: Dp = 0.dp
        val extraSmall: Dp = 4.dp
        val small: Dp = 8.dp
        val medium: Dp = 16.dp
        val large: Dp = 24.dp
        val extraLarge: Dp = 32.dp
        val huge: Dp = 48.dp

        /** Content padding inside cards */
        val cardPadding: Dp = medium

        /** Screen edge horizontal padding */
        val screenHorizontal: Dp = medium

        /** Screen edge vertical padding */
        val screenVertical: Dp = medium

        /** Space between cards in lists */
        val cardGap: Dp = small

        /** Space between sections */
        val sectionGap: Dp = large

        /** Icon to text spacing */
        val iconTextGap: Dp = small

        /** Item spacing in dense lists */
        val denseItemGap: Dp = extraSmall
    }

    // ===== TOUCH TARGETS =====

    /**
     * Minimum sizes for touch-accessible elements.
     * Ensures accessibility compliance.
     */
    object TouchTarget {
        /** Minimum touch target size (accessibility requirement) */
        val minimum: Dp = 48.dp

        /** Comfortable touch target for primary actions */
        val comfortable: Dp = 56.dp

        /** Small touch targets for secondary actions */
        val small: Dp = 40.dp

        /** Icon button size */
        val iconButton: Dp = 48.dp
    }

    // ===== ANIMATION DURATIONS =====

    /**
     * Animation duration values in milliseconds.
     */
    object Animation {
        /** Very fast animations (micro-interactions) */
        const val instant: Int = 100

        /** Fast animations (button feedback, small state changes) */
        const val fast: Int = 150

        /** Normal animations (most state transitions) */
        const val normal: Int = 300

        /** Slow animations (screen transitions, large changes) */
        const val slow: Int = 450

        /** Very slow animations (celebrations, attention-grabbing) */
        const val emphasis: Int = 600

        /** Extra long for special celebrations */
        const val celebration: Int = 1000

        // Specific use cases
        /** Entrance animation duration */
        const val entrance: Int = normal

        /** Exit animation duration */
        const val exit: Int = fast

        /** Loading shimmer cycle duration */
        const val shimmer: Int = 1500

        /** Achievement glow pulse duration */
        const val glowPulse: Int = 2000
    }

    // ===== OPACITY =====

    /**
     * Opacity values for various states.
     */
    object Opacity {
        /** Disabled state opacity */
        const val disabled: Float = 0.38f

        /** Hint/placeholder text opacity */
        const val hint: Float = 0.60f

        /** Secondary content opacity */
        const val secondary: Float = 0.70f

        /** Primary content opacity */
        const val primary: Float = 0.87f

        /** Full opacity */
        const val full: Float = 1f

        /** Overlay/scrim opacity */
        const val scrim: Float = 0.32f

        /** Modal backdrop opacity */
        const val backdrop: Float = 0.50f

        /** Subtle pattern overlay */
        const val patternOverlay: Float = 0.15f
    }

    // ===== ICON SIZES =====

    /**
     * Standard icon sizes.
     */
    object IconSize {
        val extraSmall: Dp = 16.dp
        val small: Dp = 20.dp
        val medium: Dp = 24.dp
        val large: Dp = 32.dp
        val extraLarge: Dp = 40.dp
        val huge: Dp = 56.dp

        /** Navigation bar icon size */
        val navigation: Dp = medium

        /** Achievement badge icon size */
        val achievement: Dp = large

        /** Profile avatar icon size */
        val avatar: Dp = huge
    }

    // ===== PROFILE BANNER DIMENSIONS =====

    /**
     * Profile banner specific dimensions.
     */
    object Banner {
        val height: Dp = 180.dp
        val bottomCornerRadius: Dp = Radius.extraLarge
        val profileImageSize: Dp = 100.dp
        val profileImageOffset: Dp = 50.dp // Half of profile image overlapping banner
    }

    // ===== ACHIEVEMENT COLORS =====

    /**
     * Colors for achievement rarity levels.
     * Provides Compose Color instances from domain hex values.
     */
    object AchievementColors {

        /**
         * Gets the primary color for an achievement rarity.
         */
        fun primaryColorFor(rarity: ProdyAchievements.Rarity): Color {
            return Color(rarity.primaryColorHex)
        }

        /**
         * Gets the secondary color for an achievement rarity.
         */
        fun secondaryColorFor(rarity: ProdyAchievements.Rarity): Color {
            return Color(rarity.secondaryColorHex)
        }

        /**
         * Gets gradient colors for an achievement rarity.
         */
        fun gradientFor(rarity: ProdyAchievements.Rarity): List<Color> {
            return listOf(
                Color(rarity.primaryColorHex),
                Color(rarity.secondaryColorHex)
            )
        }

        /**
         * Gets the primary color for an achievement category.
         */
        fun primaryColorFor(category: ProdyAchievements.Category): Color {
            return Color(category.primaryColorHex)
        }

        /**
         * Gets the secondary color for an achievement category.
         */
        fun secondaryColorFor(category: ProdyAchievements.Category): Color {
            return Color(category.secondaryColorHex)
        }

        /**
         * Gets gradient colors for an achievement category.
         */
        fun gradientFor(category: ProdyAchievements.Category): List<Color> {
            return listOf(
                Color(category.primaryColorHex),
                Color(category.secondaryColorHex)
            )
        }

        // Pre-defined rarity colors for direct access
        val commonPrimary = Color(0xFF78909C)
        val commonSecondary = Color(0xFF90A4AE)

        val uncommonPrimary = Color(0xFF4CAF50)
        val uncommonSecondary = Color(0xFF81C784)

        val rarePrimary = Color(0xFF2196F3)
        val rareSecondary = Color(0xFF64B5F6)

        val epicPrimary = Color(0xFF9C27B0)
        val epicSecondary = Color(0xFFBA68C8)

        val legendaryPrimary = Color(0xFFD4AF37)
        val legendarySecondary = Color(0xFFF4D03F)
    }

    // ===== BANNER COLORS =====

    /**
     * Utilities for banner gradient colors.
     */
    object BannerColors {

        /**
         * Converts banner gradient colors to Compose Color list.
         */
        fun gradientFor(banner: ProdyBanners.Banner): List<Color> {
            return banner.gradientColors.map { Color(it) }
        }
    }

    // ===== SEMANTIC COLORS =====

    /**
     * Semantic colors for consistent meaning across the app.
     */
    object SemanticColors {
        // Success states - natural greens
        val success = Color(0xFF4CAF50)
        val successLight = Color(0xFF81C784)
        val successDark = Color(0xFF388E3C)

        // Warning states - warm amber
        val warning = Color(0xFFFFA000)
        val warningLight = Color(0xFFFFCA28)
        val warningDark = Color(0xFFFF8F00)

        // Error states - muted red
        val error = Color(0xFFE53935)
        val errorLight = Color(0xFFEF5350)
        val errorDark = Color(0xFFC62828)

        // Info states - calm blue
        val info = Color(0xFF2196F3)
        val infoLight = Color(0xFF64B5F6)
        val infoDark = Color(0xFF1976D2)

        // Streak flame colors
        val streakPrimary = Color(0xFFFF6B6B)
        val streakSecondary = Color(0xFFFFAB76)
        val streakTertiary = Color(0xFFFFC371)

        // Rank/Gold colors
        val goldPrimary = Color(0xFFD4AF37)
        val goldSecondary = Color(0xFFF4D03F)
        val goldLight = Color(0xFFFFE5B4)
    }

    // ===== CARD DIMENSIONS =====

    /**
     * Standard card dimensions and properties.
     */
    object Card {
        val minHeight: Dp = 80.dp
        val padding: Dp = Spacing.cardPadding
        val elevation: Dp = Elevation.card
        val cornerRadius: Dp = Radius.card

        /** Achievement card specific dimensions */
        val achievementIconSize: Dp = 48.dp
        val achievementProgressHeight: Dp = 8.dp

        /** Quote card specific dimensions */
        val quoteMarkSize: Dp = 32.dp
    }

    // ===== PROGRESS INDICATORS =====

    /**
     * Progress indicator dimensions.
     */
    object Progress {
        val linearHeight: Dp = 8.dp
        val linearHeightSmall: Dp = 4.dp
        val circularSize: Dp = 48.dp
        val circularSizeSmall: Dp = 24.dp
        val circularStrokeWidth: Dp = 4.dp

        /** Level progress bar dimensions */
        val levelProgressHeight: Dp = 12.dp
        val levelProgressCornerRadius: Dp = 6.dp
    }

    // ===== SPECIFIC COMPONENT TOKENS =====

    /**
     * Streak display specific tokens.
     */
    object Streak {
        val flameIconSize: Dp = 32.dp
        val counterTextSize: Dp = 24.dp // In sp, but using dp for consistency
        val badgeSize: Dp = 56.dp
    }

    /**
     * Profile specific tokens.
     */
    object Profile {
        val avatarSize: Dp = 100.dp
        val avatarBorderWidth: Dp = 4.dp
        val rankBadgeSize: Dp = 28.dp
        val statCardMinWidth: Dp = 100.dp
    }

    /**
     * Bottom navigation tokens.
     */
    object BottomNav {
        val height: Dp = 64.dp
        val iconSize: Dp = IconSize.navigation
        val labelGap: Dp = 4.dp
    }
}
