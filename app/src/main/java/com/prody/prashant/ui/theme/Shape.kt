package com.prody.prashant.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Prody Design System - Shapes
 *
 * A comprehensive shape system that creates visual consistency and hierarchy.
 * Uses a combination of rounded corners and curves to create a friendly,
 * approachable aesthetic while maintaining modern elegance.
 *
 * Design Principles:
 * - Generous corner radii for a soft, welcoming feel
 * - Consistent shape language across components
 * - Clear distinction between interactive and static elements
 * - Smooth curves that complement the growth-focused brand
 */

// =============================================================================
// MATERIAL 3 SHAPE SCALE
// =============================================================================

val ProdyShapes = Shapes(
    // Extra Small - For small chips, indicators, and subtle elements
    extraSmall = RoundedCornerShape(6.dp),

    // Small - For compact cards, buttons, and interactive elements
    small = RoundedCornerShape(10.dp),

    // Medium - For standard cards and containers
    medium = RoundedCornerShape(14.dp),

    // Large - For prominent cards and modal surfaces
    large = RoundedCornerShape(20.dp),

    // Extra Large - For bottom sheets and full-screen modals
    extraLarge = RoundedCornerShape(28.dp)
)

// =============================================================================
// CARD SHAPES - For various card types throughout the app
// =============================================================================

/** Standard card shape - for most content cards */
val CardShape = RoundedCornerShape(18.dp)

/** Elevated card shape - for cards with more prominence */
val ElevatedCardShape = RoundedCornerShape(20.dp)

/** Compact card shape - for smaller, denser card layouts */
val CompactCardShape = RoundedCornerShape(14.dp)

/** Featured card shape - for hero cards and featured content */
val FeaturedCardShape = RoundedCornerShape(24.dp)

// =============================================================================
// INTERACTIVE ELEMENT SHAPES
// =============================================================================

/** Primary button shape - rounded for friendliness */
val ButtonShape = RoundedCornerShape(14.dp)

/** Small button shape - for secondary actions */
val SmallButtonShape = RoundedCornerShape(10.dp)

/** Pill button shape - for tags and filter chips */
val PillButtonShape = RoundedCornerShape(50)

/** FAB shape - floating action button */
val FloatingActionButtonShape = RoundedCornerShape(18.dp)

/** Extended FAB shape */
val ExtendedFabShape = RoundedCornerShape(20.dp)

// =============================================================================
// CHIP & BADGE SHAPES
// =============================================================================

/** Standard chip shape - for filters and categories */
val ChipShape = RoundedCornerShape(10.dp)

/** Pill chip shape - for mood selectors and tags */
val PillChipShape = RoundedCornerShape(50)

/** Badge shape - for counters and indicators */
val BadgeShape = RoundedCornerShape(8.dp)

/** Status badge shape - for streak and achievement badges */
val StatusBadgeShape = RoundedCornerShape(12.dp)

// =============================================================================
// MODAL & OVERLAY SHAPES
// =============================================================================

/** Bottom sheet shape - rounded top corners only */
val BottomSheetShape = RoundedCornerShape(
    topStart = 28.dp,
    topEnd = 28.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

/** Dialog shape - for modal dialogs */
val DialogShape = RoundedCornerShape(28.dp)

/** Snackbar shape */
val SnackbarShape = RoundedCornerShape(14.dp)

/** Tooltip shape */
val TooltipShape = RoundedCornerShape(10.dp)

// =============================================================================
// INPUT SHAPES
// =============================================================================

/** Search bar shape - fully rounded */
val SearchBarShape = RoundedCornerShape(50)

/** Text field shape - subtle rounding */
val TextFieldShape = RoundedCornerShape(14.dp)

/** Dropdown shape */
val DropdownShape = RoundedCornerShape(14.dp)

// =============================================================================
// AVATAR & IMAGE SHAPES
// =============================================================================

/** Avatar shape - circular */
val AvatarShape: Shape = CircleShape

/** Small avatar shape - for list items */
val SmallAvatarShape: Shape = CircleShape

/** Image thumbnail shape */
val ThumbnailShape = RoundedCornerShape(12.dp)

/** Profile banner shape - rounded bottom */
val ProfileBannerShape = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = 0.dp,
    bottomStart = 24.dp,
    bottomEnd = 24.dp
)

// =============================================================================
// PROGRESS & INDICATOR SHAPES
// =============================================================================

/** Progress bar shape */
val ProgressIndicatorShape = RoundedCornerShape(6.dp)

/** Progress track shape */
val ProgressTrackShape = RoundedCornerShape(6.dp)

/** Slider thumb shape */
val SliderThumbShape: Shape = CircleShape

// =============================================================================
// SPECIAL SHAPES
// =============================================================================

/** Achievement card shape - extra rounded for celebration feel */
val AchievementCardShape = RoundedCornerShape(22.dp)

/** Onboarding illustration container */
val OnboardingShape = RoundedCornerShape(32.dp)

/** Quote card shape - distinctive styling */
val QuoteCardShape = RoundedCornerShape(20.dp)

/** Streak badge shape - pill-like */
val StreakBadgeShape = RoundedCornerShape(16.dp)

/** Mood selector item shape */
val MoodSelectorShape = RoundedCornerShape(16.dp)

// =============================================================================
// NAVIGATION SHAPES
// =============================================================================

/** Navigation bar indicator shape */
val NavIndicatorShape = RoundedCornerShape(50)

/** Tab indicator shape */
val TabIndicatorShape = RoundedCornerShape(50)
