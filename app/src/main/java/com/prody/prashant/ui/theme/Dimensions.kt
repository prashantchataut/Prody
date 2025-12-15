package com.prody.prashant.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Prody Design System - Dimensions
 *
 * A comprehensive dimension system for consistent spacing, sizing, and layout
 * throughout the application. Built on a 4dp base unit grid system for visual
 * harmony and predictable layouts.
 *
 * Design Principles:
 * - 4dp base unit for all spacing calculations
 * - Consistent touch targets for accessibility (48dp minimum)
 * - Clear visual hierarchy through size differentiation
 * - Responsive-ready values for different screen sizes
 *
 * Usage:
 * ```kotlin
 * Modifier.padding(ProdyDimensions.Spacing.lg)
 * Modifier.size(ProdyDimensions.Icon.md)
 * Modifier.height(ProdyDimensions.Component.buttonHeight)
 * ```
 */
object ProdyDimensions {

    // =========================================================================
    // SPACING SYSTEM - Based on 4dp grid
    // =========================================================================

    /**
     * Spacing values for margins, paddings, and gaps.
     * Follows a consistent scale based on 4dp base unit.
     */
    object Spacing {
        /** 2dp - Micro spacing for dense elements */
        val xxs: Dp = 2.dp

        /** 4dp - Extra small for tight gaps */
        val xs: Dp = 4.dp

        /** 8dp - Small padding/margin */
        val sm: Dp = 8.dp

        /** 12dp - Medium comfortable spacing */
        val md: Dp = 12.dp

        /** 16dp - Large standard content padding */
        val lg: Dp = 16.dp

        /** 20dp - Extra large */
        val xl: Dp = 20.dp

        /** 24dp - Section spacing */
        val xxl: Dp = 24.dp

        /** 32dp - Major section breaks */
        val xxxl: Dp = 32.dp

        /** 40dp - Large section breaks */
        val huge: Dp = 40.dp

        /** 48dp - Hero section spacing */
        val massive: Dp = 48.dp

        /** 64dp - Full-screen hero elements */
        val giant: Dp = 64.dp

        // Semantic spacing aliases
        /** Standard screen horizontal padding */
        val screenHorizontal: Dp = lg

        /** Standard screen vertical padding */
        val screenVertical: Dp = lg

        /** Standard card internal padding */
        val cardPadding: Dp = lg

        /** Small card internal padding */
        val cardPaddingSmall: Dp = md

        /** Large card internal padding */
        val cardPaddingLarge: Dp = xl

        /** Space between list items */
        val listItemSpacing: Dp = sm

        /** Space between sections */
        val sectionSpacing: Dp = xxl

        /** Space between related content groups */
        val contentGroupSpacing: Dp = lg

        /** Space between icon and text */
        val iconTextGap: Dp = sm

        /** Space between chip content and edges */
        val chipPadding: Dp = sm

        /** Space for inline elements */
        val inlineSpacing: Dp = xs
    }

    // =========================================================================
    // ICON SIZES - Standard icon dimensions
    // =========================================================================

    /**
     * Standard icon sizes for visual hierarchy.
     */
    object Icon {
        /** 12dp - Extra tiny icons */
        val xxs: Dp = 12.dp

        /** 16dp - Tiny inline icons, indicators */
        val xs: Dp = 16.dp

        /** 20dp - Small icons */
        val sm: Dp = 20.dp

        /** 24dp - Default Material icon size */
        val md: Dp = 24.dp

        /** 28dp - Medium-large icons */
        val lg: Dp = 28.dp

        /** 32dp - Prominent icons */
        val xl: Dp = 32.dp

        /** 40dp - Large icons */
        val xxl: Dp = 40.dp

        /** 48dp - Hero icons, achievements */
        val hero: Dp = 48.dp

        /** 56dp - Featured icons */
        val featured: Dp = 56.dp

        /** 64dp - Extra large feature icons */
        val jumbo: Dp = 64.dp

        /** 80dp - Empty state illustrations */
        val illustration: Dp = 80.dp

        // Semantic icon sizes
        /** Navigation bar icon */
        val navigation: Dp = md

        /** Bottom bar icon */
        val bottomNav: Dp = md

        /** Tab icon */
        val tab: Dp = md

        /** Button inline icon */
        val button: Dp = md

        /** List item leading icon */
        val listItem: Dp = md

        /** Chip icon */
        val chip: Dp = 18.dp

        /** Badge icon */
        val badge: Dp = xs

        /** Achievement badge icon */
        val achievement: Dp = xl

        /** Mood selector icon */
        val mood: Dp = xxl

        /** Empty state icon */
        val emptyState: Dp = illustration
    }

    // =========================================================================
    // AVATAR SIZES - Profile and user images
    // =========================================================================

    /**
     * Avatar sizes for user profile images.
     */
    object Avatar {
        /** 24dp - Tiny avatar for dense lists */
        val tiny: Dp = 24.dp

        /** 32dp - Small avatar for compact layouts */
        val sm: Dp = 32.dp

        /** 40dp - Default avatar size */
        val md: Dp = 40.dp

        /** 48dp - Large avatar */
        val lg: Dp = 48.dp

        /** 56dp - Extra large avatar */
        val xl: Dp = 56.dp

        /** 64dp - Featured avatar */
        val xxl: Dp = 64.dp

        /** 80dp - Profile page avatar */
        val profile: Dp = 80.dp

        /** 100dp - Hero profile avatar */
        val hero: Dp = 100.dp

        /** 120dp - Full profile display */
        val full: Dp = 120.dp

        // Semantic avatar sizes
        /** Leaderboard row avatar */
        val leaderboard: Dp = lg

        /** Comment/chat avatar */
        val chat: Dp = md

        /** Notification avatar */
        val notification: Dp = md
    }

    // =========================================================================
    // COMPONENT HEIGHTS - Standard component dimensions
    // =========================================================================

    /**
     * Standard heights for UI components.
     */
    object Component {
        /** 36dp - Small button height */
        val buttonHeightSmall: Dp = 36.dp

        /** 44dp - Compact button height */
        val buttonHeightCompact: Dp = 44.dp

        /** 48dp - Default button height */
        val buttonHeight: Dp = 48.dp

        /** 56dp - Large button height */
        val buttonHeightLarge: Dp = 56.dp

        /** 64dp - Hero button height */
        val buttonHeightHero: Dp = 64.dp

        /** 32dp - Chip height */
        val chipHeight: Dp = 32.dp

        /** 40dp - Large chip height */
        val chipHeightLarge: Dp = 40.dp

        /** 48dp - Input field height */
        val inputHeight: Dp = 48.dp

        /** 56dp - Large input field height */
        val inputHeightLarge: Dp = 56.dp

        /** 48dp - Search bar height */
        val searchBarHeight: Dp = 48.dp

        /** 56dp - FAB size */
        val fabSize: Dp = 56.dp

        /** 40dp - Small FAB size */
        val fabSizeSmall: Dp = 40.dp

        /** 96dp - Extended FAB width (minimum) */
        val fabExtendedMinWidth: Dp = 96.dp

        /** 64dp - Bottom navigation bar height */
        val bottomNavHeight: Dp = 64.dp

        /** 56dp - Top app bar height */
        val topAppBarHeight: Dp = 56.dp

        /** 64dp - Large top app bar height */
        val topAppBarHeightLarge: Dp = 64.dp

        /** 48dp - Tab bar height */
        val tabBarHeight: Dp = 48.dp

        /** 56dp - List item minimum height */
        val listItemHeight: Dp = 56.dp

        /** 72dp - Two-line list item height */
        val listItemHeightTwoLine: Dp = 72.dp

        /** 88dp - Three-line list item height */
        val listItemHeightThreeLine: Dp = 88.dp

        /** 48dp - Divider touch target height */
        val dividerTouchTarget: Dp = 48.dp
    }

    // =========================================================================
    // CARD DIMENSIONS - Card and container sizes
    // =========================================================================

    /**
     * Card dimensions for various card types.
     */
    object Card {
        /** 80dp - Compact card minimum height */
        val minHeightCompact: Dp = 80.dp

        /** 100dp - Standard card minimum height */
        val minHeight: Dp = 100.dp

        /** 120dp - Featured card minimum height */
        val minHeightFeatured: Dp = 120.dp

        /** 160dp - Hero card height */
        val heroHeight: Dp = 160.dp

        /** 200dp - Large card height */
        val largeHeight: Dp = 200.dp

        /** 160dp - Small card width */
        val smallWidth: Dp = 160.dp

        /** 200dp - Medium card width */
        val mediumWidth: Dp = 200.dp

        /** 280dp - Large card width */
        val largeWidth: Dp = 280.dp

        /** 140dp - Achievement card size */
        val achievementSize: Dp = 140.dp

        /** 160dp - Quote card width */
        val quoteWidth: Dp = 160.dp

        /** 120dp - Stat card width */
        val statCardWidth: Dp = 120.dp

        /** 200dp - Wisdom card height */
        val wisdomHeight: Dp = 200.dp
    }

    // =========================================================================
    // BADGE DIMENSIONS - Achievement and notification badges
    // =========================================================================

    /**
     * Badge sizes for achievements, notifications, and indicators.
     */
    object Badge {
        /** 8dp - Tiny indicator dot */
        val indicatorDot: Dp = 8.dp

        /** 16dp - Small notification badge */
        val notification: Dp = 16.dp

        /** 20dp - Count badge */
        val count: Dp = 20.dp

        /** 24dp - Small badge */
        val sm: Dp = 24.dp

        /** 32dp - Default badge */
        val md: Dp = 32.dp

        /** 40dp - Large badge */
        val lg: Dp = 40.dp

        /** 48dp - Achievement badge small */
        val achievementSmall: Dp = 48.dp

        /** 56dp - Achievement badge medium */
        val achievementMedium: Dp = 56.dp

        /** 64dp - Achievement badge default */
        val achievement: Dp = 64.dp

        /** 80dp - Achievement badge large */
        val achievementLarge: Dp = 80.dp

        /** 100dp - Achievement showcase */
        val achievementShowcase: Dp = 100.dp

        /** 120dp - Achievement hero display */
        val achievementHero: Dp = 120.dp
    }

    // =========================================================================
    // PROGRESS INDICATORS - Progress bars and circles
    // =========================================================================

    /**
     * Progress indicator dimensions.
     */
    object Progress {
        /** 2dp - Thin progress bar */
        val linearThin: Dp = 2.dp

        /** 4dp - Small progress bar */
        val linearSmall: Dp = 4.dp

        /** 8dp - Default progress bar */
        val linearDefault: Dp = 8.dp

        /** 12dp - Large progress bar */
        val linearLarge: Dp = 12.dp

        /** 16dp - Hero progress bar */
        val linearHero: Dp = 16.dp

        /** 10dp - XP bar height */
        val xpBar: Dp = 10.dp

        /** 24dp - Small circular progress */
        val circularSmall: Dp = 24.dp

        /** 32dp - Compact circular progress */
        val circularCompact: Dp = 32.dp

        /** 48dp - Default circular progress */
        val circularDefault: Dp = 48.dp

        /** 64dp - Large circular progress */
        val circularLarge: Dp = 64.dp

        /** 120dp - Hero circular progress (profile level) */
        val circularHero: Dp = 120.dp

        /** 2dp - Progress bar stroke width */
        val strokeThin: Dp = 2.dp

        /** 4dp - Default stroke width */
        val strokeDefault: Dp = 4.dp

        /** 8dp - Large stroke width */
        val strokeLarge: Dp = 8.dp

        /** 12dp - Hero stroke width */
        val strokeHero: Dp = 12.dp
    }

    // =========================================================================
    // STREAK DISPLAY - Streak counter and flame dimensions
    // =========================================================================

    /**
     * Streak-specific dimensions.
     */
    object Streak {
        /** 48dp - Small streak badge */
        val badgeSmall: Dp = 48.dp

        /** 64dp - Default streak badge */
        val badgeMedium: Dp = 64.dp

        /** 80dp - Large streak badge */
        val badgeLarge: Dp = 80.dp

        /** 100dp - Hero streak display */
        val badgeHero: Dp = 100.dp

        /** 20dp - Small flame icon */
        val flameSmall: Dp = 20.dp

        /** 28dp - Default flame icon */
        val flameMedium: Dp = 28.dp

        /** 40dp - Large flame icon */
        val flameLarge: Dp = 40.dp

        /** 56dp - Hero flame icon */
        val flameHero: Dp = 56.dp

        /** 24dp - Glow radius effect */
        val glowRadius: Dp = 24.dp
    }

    // =========================================================================
    // PROFILE BANNER - Banner and profile header dimensions
    // =========================================================================

    /**
     * Profile banner and header dimensions.
     */
    object Banner {
        /** 180dp - Default banner height */
        val height: Dp = 180.dp

        /** 200dp - Large banner height */
        val heightLarge: Dp = 200.dp

        /** 140dp - Compact banner height */
        val heightCompact: Dp = 140.dp

        /** 100dp - Profile image size */
        val profileImageSize: Dp = 100.dp

        /** 50dp - Profile image overlap with banner */
        val profileImageOffset: Dp = 50.dp

        /** 4dp - Profile image border width */
        val profileImageBorder: Dp = 4.dp
    }

    // =========================================================================
    // TOUCH TARGETS - Accessibility-compliant touch areas
    // =========================================================================

    /**
     * Minimum touch target sizes for accessibility compliance.
     * Follows WCAG 2.1 Level AA guidelines.
     */
    object Touch {
        /** 44dp - Minimum recommended touch target (WCAG) */
        val minimum: Dp = 44.dp

        /** 48dp - Standard comfortable touch target */
        val standard: Dp = 48.dp

        /** 56dp - Large comfortable touch target */
        val large: Dp = 56.dp

        /** 64dp - Spacious touch target */
        val spacious: Dp = 64.dp

        // Specific touch targets
        /** Icon button touch target */
        val iconButton: Dp = standard

        /** Checkbox/Radio touch target */
        val selectionControl: Dp = standard

        /** Slider thumb touch target */
        val sliderThumb: Dp = standard

        /** Close button touch target */
        val closeButton: Dp = standard
    }

    // =========================================================================
    // BORDERS & DIVIDERS - Line widths and border sizes
    // =========================================================================

    /**
     * Border widths and divider heights.
     */
    object Border {
        /** 0.5dp - Hairline border */
        val hairline: Dp = 0.5.dp

        /** 1dp - Thin border */
        val thin: Dp = 1.dp

        /** 2dp - Default border */
        val medium: Dp = 2.dp

        /** 3dp - Thick border */
        val thick: Dp = 3.dp

        /** 4dp - Extra thick border */
        val extraThick: Dp = 4.dp

        /** 2dp - Focus ring width */
        val focusRing: Dp = 2.dp

        /** 2dp - Selection border */
        val selection: Dp = 2.dp

        /** 1dp - Divider height */
        val divider: Dp = 1.dp
    }

    // =========================================================================
    // ELEVATION VALUES - Shadow depth levels
    // =========================================================================

    /**
     * Elevation values for shadow depth.
     */
    object Elevation {
        /** 0dp - No elevation */
        val none: Dp = 0.dp

        /** 1dp - Subtle lift */
        val xs: Dp = 1.dp

        /** 2dp - Low elevation (standard cards) */
        val sm: Dp = 2.dp

        /** 4dp - Medium elevation (elevated cards, FAB) */
        val md: Dp = 4.dp

        /** 6dp - High elevation (modals) */
        val lg: Dp = 6.dp

        /** 8dp - Higher elevation (dialogs) */
        val xl: Dp = 8.dp

        /** 12dp - Maximum elevation (tooltips, dropdowns) */
        val xxl: Dp = 12.dp

        /** 16dp - Overlay elevation */
        val overlay: Dp = 16.dp

        // Semantic elevations
        /** Card default elevation */
        val card: Dp = sm

        /** Elevated card elevation */
        val cardElevated: Dp = md

        /** FAB elevation */
        val fab: Dp = md

        /** Dialog elevation */
        val dialog: Dp = xl

        /** Bottom sheet elevation */
        val bottomSheet: Dp = xxl

        /** Dropdown/Menu elevation */
        val dropdown: Dp = md

        /** Tooltip elevation */
        val tooltip: Dp = sm
    }

    // =========================================================================
    // BOTTOM SHEET DIMENSIONS
    // =========================================================================

    /**
     * Bottom sheet specific dimensions.
     */
    object BottomSheet {
        /** 4dp - Handle width */
        val handleWidth: Dp = 32.dp

        /** 4dp - Handle height */
        val handleHeight: Dp = 4.dp

        /** 8dp - Handle top margin */
        val handleTopMargin: Dp = 8.dp

        /** 200dp - Peek height */
        val peekHeight: Dp = 200.dp

        /** 400dp - Half expanded height */
        val halfExpandedHeight: Dp = 400.dp
    }

    // =========================================================================
    // DIALOG DIMENSIONS
    // =========================================================================

    /**
     * Dialog and modal dimensions.
     */
    object Dialog {
        /** 280dp - Minimum dialog width */
        val minWidth: Dp = 280.dp

        /** 340dp - Default dialog width */
        val defaultWidth: Dp = 340.dp

        /** 400dp - Maximum dialog width */
        val maxWidth: Dp = 400.dp

        /** 24dp - Dialog content padding */
        val contentPadding: Dp = 24.dp

        /** 16dp - Dialog title bottom padding */
        val titleBottomPadding: Dp = 16.dp

        /** 24dp - Dialog actions top padding */
        val actionsTopPadding: Dp = 24.dp
    }

    // =========================================================================
    // CONTENT CONSTRAINTS - Maximum widths for readability
    // =========================================================================

    /**
     * Maximum content widths for optimal readability.
     */
    object ContentWidth {
        /** 320dp - Narrow content (forms) */
        val narrow: Dp = 320.dp

        /** 480dp - Medium content */
        val medium: Dp = 480.dp

        /** 600dp - Wide content (optimal reading) */
        val wide: Dp = 600.dp

        /** 840dp - Extra wide content */
        val extraWide: Dp = 840.dp

        /** 600dp - Optimal reading width */
        val readable: Dp = 600.dp
    }
}
