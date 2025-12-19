package com.prody.prashant.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Prody Design System - Shapes (Redesigned)
 *
 * A minimalist, flat shape system following strict flat design principles.
 * All shapes are clean, geometric, and consistent throughout the app.
 *
 * Design Philosophy:
 * - Flat design with NO shadows or elevation effects
 * - Consistent corner radii based on 4dp increments
 * - Clean geometric forms for modern aesthetic
 * - Unified shape language across all components
 *
 * Corner Radius Scale (based on 4dp increments):
 * - None: 0dp (sharp corners)
 * - XS: 4dp (subtle rounding)
 * - SM: 8dp (light rounding)
 * - MD: 12dp (standard rounding)
 * - LG: 16dp (generous rounding)
 * - XL: 20dp (prominent rounding)
 * - XXL: 24dp (feature rounding)
 * - Full: 50% (pill/circle)
 */

// =============================================================================
// CORNER RADIUS CONSTANTS - Based on 4dp grid
// =============================================================================

/** No rounding - sharp corners */
private val CornerNone = 0.dp

/** Extra small corner radius - 4dp */
private val CornerXs = 4.dp

/** Small corner radius - 8dp */
private val CornerSm = 8.dp

/** Medium corner radius - 12dp */
private val CornerMd = 12.dp

/** Large corner radius - 16dp */
private val CornerLg = 16.dp

/** Extra large corner radius - 20dp */
private val CornerXl = 20.dp

/** Double extra large corner radius - 24dp */
private val CornerXxl = 24.dp

/** Full rounding - pill shape */
private const val CornerFull = 50

// =============================================================================
// MATERIAL 3 SHAPE SCALE
// =============================================================================

val ProdyShapes = Shapes(
    // Extra Small - For chips, badges, and small indicators
    extraSmall = RoundedCornerShape(CornerXs),

    // Small - For compact elements and secondary buttons
    small = RoundedCornerShape(CornerSm),

    // Medium - For standard cards and primary buttons
    medium = RoundedCornerShape(CornerMd),

    // Large - For featured cards and modals
    large = RoundedCornerShape(CornerLg),

    // Extra Large - For bottom sheets and full-screen surfaces
    extraLarge = RoundedCornerShape(CornerXxl)
)

// =============================================================================
// CARD SHAPES - Flat, consistent card styling
// =============================================================================

/** Standard card shape - for most content cards */
val CardShape = RoundedCornerShape(CornerLg)

/** Elevated card shape - Note: Flat design uses same shape, no elevation */
val ElevatedCardShape = RoundedCornerShape(CornerLg)

/** Compact card shape - for smaller, denser card layouts */
val CompactCardShape = RoundedCornerShape(CornerMd)

/** Featured card shape - for hero cards and featured content */
val FeaturedCardShape = RoundedCornerShape(CornerXl)

// =============================================================================
// BUTTON SHAPES - Clean, flat interactive elements
// =============================================================================

/** Primary button shape - standard interactive element */
val ButtonShape = RoundedCornerShape(CornerMd)

/** Small button shape - for secondary actions */
val SmallButtonShape = RoundedCornerShape(CornerSm)

/** Pill button shape - for tags and filter chips */
val PillButtonShape = RoundedCornerShape(CornerFull)

/** FAB shape - floating action button (flat, no elevation) */
val FloatingActionButtonShape = RoundedCornerShape(CornerLg)

/** Extended FAB shape */
val ExtendedFabShape = RoundedCornerShape(CornerLg)

// =============================================================================
// CHIP & BADGE SHAPES - Flat, consistent styling
// =============================================================================

/** Standard chip shape - for filters and categories */
val ChipShape = RoundedCornerShape(CornerSm)

/** Pill chip shape - for mood selectors and tags */
val PillChipShape = RoundedCornerShape(CornerFull)

/** Badge shape - for counters and indicators */
val BadgeShape = RoundedCornerShape(CornerXs)

/** Status badge shape - for streak and achievement badges */
val StatusBadgeShape = RoundedCornerShape(CornerMd)

// =============================================================================
// MODAL & OVERLAY SHAPES - Flat surfaces
// =============================================================================

/** Bottom sheet shape - rounded top corners only */
val BottomSheetShape = RoundedCornerShape(
    topStart = CornerXxl,
    topEnd = CornerXxl,
    bottomStart = CornerNone,
    bottomEnd = CornerNone
)

/** Dialog shape - for modal dialogs */
val DialogShape = RoundedCornerShape(CornerXxl)

/** Snackbar shape */
val SnackbarShape = RoundedCornerShape(CornerMd)

/** Tooltip shape */
val TooltipShape = RoundedCornerShape(CornerSm)

// =============================================================================
// INPUT SHAPES - Clean form elements
// =============================================================================

/** Search bar shape - fully rounded pill */
val SearchBarShape = RoundedCornerShape(CornerFull)

/** Text field shape - consistent rounding */
val TextFieldShape = RoundedCornerShape(CornerMd)

/** Dropdown shape */
val DropdownShape = RoundedCornerShape(CornerMd)

// =============================================================================
// AVATAR & IMAGE SHAPES - Circular and clean
// =============================================================================

/** Avatar shape - circular */
val AvatarShape: Shape = CircleShape

/** Small avatar shape - for list items */
val SmallAvatarShape: Shape = CircleShape

/** Image thumbnail shape */
val ThumbnailShape = RoundedCornerShape(CornerMd)

/** Profile banner shape - flat top, rounded bottom */
val ProfileBannerShape = RoundedCornerShape(
    topStart = CornerNone,
    topEnd = CornerNone,
    bottomStart = CornerXxl,
    bottomEnd = CornerXxl
)

// =============================================================================
// PROGRESS & INDICATOR SHAPES - Pill-style for clean aesthetics
// =============================================================================

/** Progress bar shape - pill style */
val ProgressIndicatorShape = RoundedCornerShape(CornerFull)

/** Progress track shape - matches indicator */
val ProgressTrackShape = RoundedCornerShape(CornerFull)

/** Slider thumb shape - circular */
val SliderThumbShape: Shape = CircleShape

// =============================================================================
// SPECIAL SHAPES - Feature-specific styling
// =============================================================================

/** Achievement card shape - slightly more rounded */
val AchievementCardShape = RoundedCornerShape(CornerXl)

/** Onboarding illustration container - generous rounding */
val OnboardingShape = RoundedCornerShape(CornerXxl)

/** Quote card shape - prominent wisdom card */
val QuoteCardShape = RoundedCornerShape(CornerXl)

/** Streak badge shape - pill-like */
val StreakBadgeShape = RoundedCornerShape(CornerLg)

/** Mood selector item shape - tappable mood buttons */
val MoodSelectorShape = RoundedCornerShape(CornerLg)

// =============================================================================
// NAVIGATION SHAPES - Clean nav elements
// =============================================================================

/** Navigation bar indicator shape - pill */
val NavIndicatorShape = RoundedCornerShape(CornerFull)

/** Tab indicator shape - pill */
val TabIndicatorShape = RoundedCornerShape(CornerFull)

// =============================================================================
// EXTENDED CARD SHAPES - Consistent card hierarchy
// =============================================================================

/** Small card shape - for compact content cards */
val CardShapeSmall = RoundedCornerShape(CornerSm)

/** Default card shape - for most content cards */
val CardShapeDefault = RoundedCornerShape(CornerMd)

/** Large card shape - for featured content */
val CardShapeLarge = RoundedCornerShape(CornerLg)

/** Hero card shape - for prominent hero sections */
val CardShapeHero = RoundedCornerShape(CornerXl)

// =============================================================================
// BADGE & ACHIEVEMENT SHAPES - Gamification elements
// =============================================================================

/** Badge shape - for small status indicators */
val BadgeShapeSmall = RoundedCornerShape(CornerXs)

/** Badge pill shape - for tag-like badges */
val BadgeShapePill = RoundedCornerShape(CornerFull)

/** Achievement badge shape - for achievement icons */
val AchievementBadgeShape = RoundedCornerShape(CornerLg)

/** Rarity badge shape - circular for rarity indicators */
val RarityBadgeShape: Shape = CircleShape

// =============================================================================
// BUTTON SHAPES - Extended scale
// =============================================================================

/** Default button shape */
val ButtonShapeDefault = RoundedCornerShape(CornerMd)

/** Pill button shape - fully rounded */
val ButtonShapePill = RoundedCornerShape(CornerFull)

/** Small button shape - for compact buttons */
val ButtonShapeSmall = RoundedCornerShape(CornerSm)

/** Large button shape - for prominent CTAs */
val ButtonShapeLarge = RoundedCornerShape(CornerLg)

// =============================================================================
// PROGRESS & XP BAR SHAPES - Gamification progress
// =============================================================================

/** Progress bar shape - pill for smooth aesthetics */
val ProgressBarShape = RoundedCornerShape(CornerFull)

/** XP bar shape - pill for visual appeal */
val XpBarShape = RoundedCornerShape(CornerFull)

/** Level progress shape - consistent with system */
val LevelProgressShape = RoundedCornerShape(CornerFull)

// =============================================================================
// SPECIAL PURPOSE SHAPES - Distinctive elements
// =============================================================================

/** Quote card shape - distinctive styling with asymmetric corners */
val QuoteCardShapeAsymmetric = RoundedCornerShape(
    topStart = CornerXs,
    topEnd = CornerLg,
    bottomEnd = CornerLg,
    bottomStart = CornerLg
)

/** Future message / Time Capsule card shape - unique styling */
val FutureMessageCardShape = RoundedCornerShape(
    topStart = CornerXl,
    topEnd = CornerXl,
    bottomEnd = CornerXs,
    bottomStart = CornerXl
)

/** Notification card shape */
val NotificationCardShape = RoundedCornerShape(CornerLg)

/** Leaderboard item shape */
val LeaderboardItemShape = RoundedCornerShape(CornerMd)

/** Leaderboard top 3 shape - more prominent for podium */
val LeaderboardTopShape = RoundedCornerShape(CornerLg)

// =============================================================================
// INPUT & FORM SHAPES - Clean form elements
// =============================================================================

/** Journal entry text field shape */
val JournalTextFieldShape = RoundedCornerShape(CornerLg)

/** Comment/Reply input shape - more rounded */
val CommentInputShape = RoundedCornerShape(CornerXxl)

/** Date picker shape */
val DatePickerShape = RoundedCornerShape(CornerLg)

// =============================================================================
// MODAL & SHEET SHAPES - Extended flat surfaces
// =============================================================================

/** Small bottom sheet shape */
val BottomSheetShapeSmall = RoundedCornerShape(
    topStart = CornerXl,
    topEnd = CornerXl,
    bottomStart = CornerNone,
    bottomEnd = CornerNone
)

/** Full bottom sheet shape */
val BottomSheetShapeFull = RoundedCornerShape(
    topStart = CornerXxl,
    topEnd = CornerXxl,
    bottomStart = CornerNone,
    bottomEnd = CornerNone
)

/** Modal card shape */
val ModalCardShape = RoundedCornerShape(CornerXxl)

// =============================================================================
// GAMIFICATION SHAPES - Stats and achievements
// =============================================================================

/** Streak flame container shape */
val StreakContainerShape = RoundedCornerShape(CornerXl)

/** XP container shape */
val XpContainerShape = RoundedCornerShape(CornerLg)

/** Level badge shape */
val LevelBadgeShape = RoundedCornerShape(CornerMd)

/** Celebration card shape - for level ups and milestones */
val CelebrationCardShape = RoundedCornerShape(CornerXxl)

// =============================================================================
// LIST ITEM SHAPES - Consistent list styling
// =============================================================================

/** Standard list item shape */
val ListItemShape = RoundedCornerShape(CornerMd)

/** Compact list item shape */
val ListItemShapeCompact = RoundedCornerShape(CornerSm)

/** Selectable list item shape */
val SelectableListItemShape = RoundedCornerShape(CornerMd)

// =============================================================================
// TIME CAPSULE SHAPES - Future Message specific
// =============================================================================

/** Time Capsule tab container shape */
val TimeCapsuleTabContainerShape = RoundedCornerShape(CornerMd)

/** Time Capsule tag shape */
val TimeCapsuleTagShape = RoundedCornerShape(CornerSm)

/** Time Capsule empty state circle */
val TimeCapsuleEmptyCircleShape: Shape = CircleShape

// =============================================================================
// ONBOARDING SHAPES - Welcome flow specific
// =============================================================================

/** Onboarding page indicator */
val OnboardingIndicatorShape = RoundedCornerShape(CornerFull)

/** Onboarding feature card */
val OnboardingFeatureCardShape = RoundedCornerShape(CornerLg)

/** Onboarding XP arc container */
val OnboardingXpArcShape: Shape = CircleShape

// =============================================================================
// HOME SCREEN SHAPES - Dashboard specific
// =============================================================================

/** Quick action tile shape */
val QuickActionTileShape = RoundedCornerShape(CornerLg)

/** Wisdom carousel card shape */
val WisdomCarouselCardShape = RoundedCornerShape(CornerXl)

/** Greeting banner shape */
val GreetingBannerShape = RoundedCornerShape(CornerLg)

// =============================================================================
// STATS SCREEN SHAPES - Analytics specific
// =============================================================================

/** Activity pulse container shape */
val ActivityPulseShape: Shape = CircleShape

/** Summary card shape */
val SummaryCardShape = RoundedCornerShape(CornerLg)

/** Stats hero number container */
val StatsHeroContainerShape = RoundedCornerShape(CornerXl)

// =============================================================================
// PROFILE SCREEN SHAPES - User profile specific
// =============================================================================

/** Trophy shelf container shape */
val TrophyShelfShape = RoundedCornerShape(CornerLg)

/** Profile stat card shape */
val ProfileStatCardShape = RoundedCornerShape(CornerMd)

/** Settings row shape */
val SettingsRowShape = RoundedCornerShape(CornerMd)
