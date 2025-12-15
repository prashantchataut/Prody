package com.prody.prashant.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Prody Design System - Dimensions
 *
 * A comprehensive dimension system for consistent spacing, sizing, and layout across the app.
 * Based on an 8dp grid system with 4dp increments for fine-tuning.
 *
 * Design Philosophy:
 * - Consistent spacing creates visual rhythm and harmony
 * - Touch targets meet accessibility guidelines (minimum 48dp)
 * - Component sizes are proportional and scalable
 * - All dimensions are multiples of 4dp for pixel-perfect alignment
 */
object ProddyDimens {

    // =============================================================================
    // BASE SPACING UNIT (8dp grid with 4dp fine-tuning)
    // =============================================================================

    /** Extra extra small spacing - 2dp */
    val spacingXxs: Dp = 2.dp

    /** Extra small spacing - 4dp */
    val spacingXs: Dp = 4.dp

    /** Small spacing - 8dp */
    val spacingSm: Dp = 8.dp

    /** Medium spacing - 12dp */
    val spacingMd: Dp = 12.dp

    /** Default/Large spacing - 16dp */
    val spacingLg: Dp = 16.dp

    /** Extra large spacing - 24dp */
    val spacingXl: Dp = 24.dp

    /** Extra extra large spacing - 32dp */
    val spacingXxl: Dp = 32.dp

    /** Triple extra large spacing - 48dp */
    val spacingXxxl: Dp = 48.dp

    /** Massive spacing - 64dp */
    val spacingMassive: Dp = 64.dp

    // =============================================================================
    // CONTENT PADDING
    // =============================================================================

    /** Screen horizontal padding */
    val screenPadding: Dp = 16.dp

    /** Screen vertical padding */
    val screenPaddingVertical: Dp = 16.dp

    /** Card content padding */
    val cardPadding: Dp = 16.dp

    /** Small card content padding */
    val cardPaddingSmall: Dp = 12.dp

    /** Large card content padding */
    val cardPaddingLarge: Dp = 20.dp

    /** Modal/Dialog padding */
    val modalPadding: Dp = 24.dp

    /** Section padding (between major sections) */
    val sectionPadding: Dp = 24.dp

    // =============================================================================
    // COMPONENT SIZES - BUTTONS
    // =============================================================================

    /** Standard button height */
    val buttonHeight: Dp = 48.dp

    /** Small button height */
    val buttonHeightSmall: Dp = 36.dp

    /** Large button height */
    val buttonHeightLarge: Dp = 56.dp

    /** Icon button size */
    val iconButtonSize: Dp = 48.dp

    /** Small icon button size */
    val iconButtonSizeSmall: Dp = 40.dp

    /** FAB size */
    val fabSize: Dp = 56.dp

    /** Mini FAB size */
    val fabSizeMini: Dp = 40.dp

    // =============================================================================
    // COMPONENT SIZES - ICONS
    // =============================================================================

    /** Extra small icon - 16dp */
    val iconSizeSmall: Dp = 16.dp

    /** Default icon size - 24dp */
    val iconSizeDefault: Dp = 24.dp

    /** Large icon size - 32dp */
    val iconSizeLarge: Dp = 32.dp

    /** Extra large icon - 48dp */
    val iconSizeXl: Dp = 48.dp

    /** Huge icon - for hero sections */
    val iconSizeHuge: Dp = 64.dp

    // =============================================================================
    // COMPONENT SIZES - AVATARS
    // =============================================================================

    /** Small avatar - 32dp */
    val avatarSizeSmall: Dp = 32.dp

    /** Default avatar - 48dp */
    val avatarSizeDefault: Dp = 48.dp

    /** Large avatar - 64dp */
    val avatarSizeLarge: Dp = 64.dp

    /** Extra large avatar - 96dp */
    val avatarSizeXl: Dp = 96.dp

    /** Hero avatar - 120dp */
    val avatarSizeHero: Dp = 120.dp

    /** Profile page avatar */
    val profileAvatarSize: Dp = 100.dp

    // =============================================================================
    // COMPONENT SIZES - BADGES
    // =============================================================================

    /** Small badge size - 24dp */
    val badgeSizeSmall: Dp = 24.dp

    /** Default badge size - 48dp */
    val badgeSizeDefault: Dp = 48.dp

    /** Large badge size - 64dp */
    val badgeSizeLarge: Dp = 64.dp

    /** Showcase badge size - 80dp */
    val badgeSizeShowcase: Dp = 80.dp

    /** Achievement icon size on cards */
    val achievementIconSize: Dp = 48.dp

    // =============================================================================
    // TOUCH TARGETS (Accessibility)
    // =============================================================================

    /** Minimum touch target - accessibility requirement */
    val minTouchTarget: Dp = 48.dp

    /** Comfortable touch target */
    val comfortableTouchTarget: Dp = 56.dp

    /** Small touch target - use sparingly */
    val smallTouchTarget: Dp = 40.dp

    // =============================================================================
    // ELEVATION SYSTEM
    // =============================================================================

    /** No elevation */
    val elevationNone: Dp = 0.dp

    /** Low elevation - subtle shadow */
    val elevationLow: Dp = 2.dp

    /** Medium elevation - standard cards */
    val elevationMedium: Dp = 4.dp

    /** High elevation - FABs, elevated surfaces */
    val elevationHigh: Dp = 8.dp

    /** Very high elevation - modals, dialogs */
    val elevationVeryHigh: Dp = 16.dp

    /** Card default elevation */
    val cardElevation: Dp = 2.dp

    /** FAB elevation */
    val fabElevation: Dp = 6.dp

    /** Dialog elevation */
    val dialogElevation: Dp = 24.dp

    // =============================================================================
    // BORDER WIDTHS
    // =============================================================================

    /** Thin border - 1dp */
    val borderThin: Dp = 1.dp

    /** Default border - 2dp */
    val borderDefault: Dp = 2.dp

    /** Thick border - 3dp */
    val borderThick: Dp = 3.dp

    /** Focus ring width */
    val focusRingWidth: Dp = 2.dp

    /** Selection indicator width */
    val selectionIndicatorWidth: Dp = 3.dp

    // =============================================================================
    // DIVIDERS
    // =============================================================================

    /** Divider height */
    val dividerHeight: Dp = 1.dp

    /** Thick divider height */
    val dividerHeightThick: Dp = 2.dp

    // =============================================================================
    // PROGRESS BARS
    // =============================================================================

    /** Default progress bar height */
    val progressBarHeight: Dp = 8.dp

    /** Large progress bar height */
    val progressBarHeightLarge: Dp = 12.dp

    /** Small progress bar height */
    val progressBarHeightSmall: Dp = 4.dp

    /** XP bar height */
    val xpBarHeight: Dp = 10.dp

    /** Level progress bar height */
    val levelProgressHeight: Dp = 12.dp

    /** Circular progress size */
    val circularProgressSize: Dp = 48.dp

    /** Small circular progress size */
    val circularProgressSizeSmall: Dp = 24.dp

    // =============================================================================
    // NAVIGATION
    // =============================================================================

    /** Bottom navigation bar height */
    val bottomNavHeight: Dp = 64.dp

    /** Top app bar height */
    val topAppBarHeight: Dp = 64.dp

    /** Navigation rail width */
    val navRailWidth: Dp = 80.dp

    // =============================================================================
    // CARDS & CONTAINERS
    // =============================================================================

    /** Minimum card height */
    val cardMinHeight: Dp = 80.dp

    /** Standard card height */
    val cardStandardHeight: Dp = 120.dp

    /** Featured card height */
    val cardFeaturedHeight: Dp = 200.dp

    /** Quote card min height */
    val quoteCardMinHeight: Dp = 140.dp

    /** Journal entry card height */
    val journalCardHeight: Dp = 160.dp

    // =============================================================================
    // PROFILE & BANNERS
    // =============================================================================

    /** Profile banner height */
    val bannerHeight: Dp = 180.dp

    /** Profile avatar offset (overlap with banner) */
    val profileAvatarOffset: Dp = 50.dp

    /** Profile stats card width */
    val statsCardMinWidth: Dp = 100.dp

    /** Rank badge size */
    val rankBadgeSize: Dp = 28.dp

    // =============================================================================
    // STREAK & GAMIFICATION
    // =============================================================================

    /** Streak flame icon size */
    val streakFlameSize: Dp = 32.dp

    /** Streak badge container size */
    val streakBadgeSize: Dp = 56.dp

    /** Leaderboard rank number width */
    val leaderboardRankWidth: Dp = 40.dp

    /** Leaderboard avatar size */
    val leaderboardAvatarSize: Dp = 44.dp

    // =============================================================================
    // INPUT FIELDS
    // =============================================================================

    /** Standard text field height */
    val textFieldHeight: Dp = 56.dp

    /** Search bar height */
    val searchBarHeight: Dp = 48.dp

    /** Multiline text field min height */
    val textAreaMinHeight: Dp = 120.dp

    /** Journal entry text field min height */
    val journalTextFieldMinHeight: Dp = 200.dp

    // =============================================================================
    // CHIPS & TAGS
    // =============================================================================

    /** Chip height */
    val chipHeight: Dp = 32.dp

    /** Small chip height */
    val chipHeightSmall: Dp = 24.dp

    /** Filter chip height */
    val filterChipHeight: Dp = 36.dp

    // =============================================================================
    // MODALS & SHEETS
    // =============================================================================

    /** Bottom sheet peek height */
    val bottomSheetPeekHeight: Dp = 56.dp

    /** Modal max width */
    val modalMaxWidth: Dp = 400.dp

    /** Dialog min width */
    val dialogMinWidth: Dp = 280.dp

    /** Dialog max width */
    val dialogMaxWidth: Dp = 560.dp

    // =============================================================================
    // LISTS
    // =============================================================================

    /** List item height - single line */
    val listItemHeightSingle: Dp = 48.dp

    /** List item height - two lines */
    val listItemHeightDouble: Dp = 64.dp

    /** List item height - three lines */
    val listItemHeightTriple: Dp = 88.dp

    /** List item leading icon padding */
    val listItemIconPadding: Dp = 16.dp

    // =============================================================================
    // IMAGE SIZES
    // =============================================================================

    /** Thumbnail size - small */
    val thumbnailSmall: Dp = 40.dp

    /** Thumbnail size - medium */
    val thumbnailMedium: Dp = 56.dp

    /** Thumbnail size - large */
    val thumbnailLarge: Dp = 80.dp

    /** Image preview size */
    val imagePreviewSize: Dp = 120.dp
}

/**
 * Extension object for common spacing combinations
 */
object ProdySpacingCombinations {

    /** Card internal spacing */
    val cardInternal = ProddyDimens.spacingLg

    /** Section gap */
    val sectionGap = ProddyDimens.spacingXl

    /** Icon to text gap */
    val iconTextGap = ProddyDimens.spacingSm

    /** Button horizontal padding */
    val buttonHorizontalPadding = ProddyDimens.spacingLg

    /** Chip horizontal padding */
    val chipHorizontalPadding = ProddyDimens.spacingMd

    /** List item horizontal padding */
    val listItemHorizontalPadding = ProddyDimens.spacingLg

    /** Dense list item gap */
    val denseItemGap = ProddyDimens.spacingXs
}
