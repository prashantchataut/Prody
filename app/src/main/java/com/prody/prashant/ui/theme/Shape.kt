package com.prody.prashant.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Prody Design System - Shapes
 *
 * 8 core shapes covering the full spectrum of UI elements.
 * All named shapes below are backward-compatible aliases.
 *
 * Core Shape Scale:
 * - None (0dp)   — Sharp corners
 * - Xs  (4dp)    — Badges, small indicators
 * - Sm  (8dp)    — Chips, compact elements
 * - Md  (12dp)   — Standard cards, buttons, inputs
 * - Lg  (16dp)   — Featured cards, prominent elements
 * - Xl  (20dp)   — Hero cards, large surfaces
 * - Xxl (24dp)   — Bottom sheets, dialogs
 * - Full (50%)   — Pills, circles, avatars
 */

// =============================================================================
// CORNER RADIUS CONSTANTS - 4dp grid
// =============================================================================

private val CornerNone = 0.dp
private val CornerXs = 4.dp
private val CornerSm = 8.dp
private val CornerMd = 12.dp
private val CornerLg = 16.dp
private val CornerXl = 20.dp
private val CornerXxl = 24.dp
private const val CornerFull = 50

// =============================================================================
// 8 CORE SHAPES
// =============================================================================

val ProdyShapeNone = RoundedCornerShape(CornerNone)
val ProdyShapeXs = RoundedCornerShape(CornerXs)
val ProdyShapeSm = RoundedCornerShape(CornerSm)
val ProdyShapeMd = RoundedCornerShape(CornerMd)
val ProdyShapeLg = RoundedCornerShape(CornerLg)
val ProdyShapeXl = RoundedCornerShape(CornerXl)
val ProdyShapeXxl = RoundedCornerShape(CornerXxl)
val ProdyShapeFull = RoundedCornerShape(CornerFull)

// =============================================================================
// ASYMMETRIC SHAPES - Cannot be simplified to core shapes
// =============================================================================

val BottomSheetShape = RoundedCornerShape(
    topStart = CornerXxl,
    topEnd = CornerXxl,
    bottomStart = CornerNone,
    bottomEnd = CornerNone
)

val BottomSheetShapeSmall = RoundedCornerShape(
    topStart = CornerXl,
    topEnd = CornerXl,
    bottomStart = CornerNone,
    bottomEnd = CornerNone
)

val BottomSheetShapeFull = RoundedCornerShape(
    topStart = CornerXxl,
    topEnd = CornerXxl,
    bottomStart = CornerNone,
    bottomEnd = CornerNone
)

val ProfileBannerShape = RoundedCornerShape(
    topStart = CornerNone,
    topEnd = CornerNone,
    bottomStart = CornerXxl,
    bottomEnd = CornerXxl
)

val QuoteCardShapeAsymmetric = RoundedCornerShape(
    topStart = CornerXs,
    topEnd = CornerLg,
    bottomEnd = CornerLg,
    bottomStart = CornerLg
)

val FutureMessageCardShape = RoundedCornerShape(
    topStart = CornerXl,
    topEnd = CornerXl,
    bottomEnd = CornerXs,
    bottomStart = CornerXl
)

// =============================================================================
// MATERIAL 3 SHAPE SCALE
// =============================================================================

val ProdyShapes = Shapes(
    extraSmall = ProdyShapeXs,
    small = ProdyShapeSm,
    medium = ProdyShapeMd,
    large = ProdyShapeLg,
    extraLarge = ProdyShapeXxl
)

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Cards
// =============================================================================

val CardShape = ProdyShapeLg
val ElevatedCardShape = ProdyShapeLg
val CompactCardShape = ProdyShapeMd
val FeaturedCardShape = ProdyShapeXl
val CardShapeSmall = ProdyShapeSm
val CardShapeDefault = ProdyShapeMd
val CardShapeLarge = ProdyShapeLg
val CardShapeHero = ProdyShapeXl

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Buttons
// =============================================================================

val ButtonShape = ProdyShapeMd
val SmallButtonShape = ProdyShapeSm
val PillButtonShape = ProdyShapeFull
val FloatingActionButtonShape = ProdyShapeLg
val ExtendedFabShape = ProdyShapeLg
val ButtonShapeDefault = ProdyShapeMd
val ButtonShapePill = ProdyShapeFull
val ButtonShapeSmall = ProdyShapeSm
val ButtonShapeLarge = ProdyShapeLg

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Chips & Badges
// =============================================================================

val ChipShape = ProdyShapeSm
val PillChipShape = ProdyShapeFull
val BadgeShape = ProdyShapeXs
val StatusBadgeShape = ProdyShapeMd
val BadgeShapeSmall = ProdyShapeXs
val BadgeShapePill = ProdyShapeFull
val AchievementBadgeShape = ProdyShapeLg
val RarityBadgeShape: Shape = CircleShape

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Modals & Overlays
// =============================================================================

val DialogShape = ProdyShapeXxl
val SnackbarShape = ProdyShapeMd
val TooltipShape = ProdyShapeSm
val ModalCardShape = ProdyShapeXxl
val CelebrationCardShape = ProdyShapeXxl

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Inputs
// =============================================================================

val SearchBarShape = ProdyShapeFull
val TextFieldShape = ProdyShapeMd
val DropdownShape = ProdyShapeMd

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Avatars & Images
// =============================================================================

val AvatarShape: Shape = CircleShape
val SmallAvatarShape: Shape = CircleShape
val ThumbnailShape = ProdyShapeMd

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Progress & Indicators
// =============================================================================

val ProgressIndicatorShape = ProdyShapeFull
val ProgressTrackShape = ProdyShapeFull
val SliderThumbShape: Shape = CircleShape
val ProgressBarShape = ProdyShapeFull
val XpBarShape = ProdyShapeFull
val LevelProgressShape = ProdyShapeFull

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Navigation
// =============================================================================

val NavIndicatorShape = ProdyShapeFull
val TabIndicatorShape = ProdyShapeFull

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Feature Shapes
// =============================================================================

val AchievementCardShape = ProdyShapeXl
val OnboardingShape = ProdyShapeXxl
val QuoteCardShape = ProdyShapeXl
val StreakBadgeShape = ProdyShapeLg
val MoodSelectorShape = ProdyShapeLg
val NotificationCardShape = ProdyShapeLg

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Leaderboard
// =============================================================================

val LeaderboardItemShape = ProdyShapeMd
val LeaderboardTopShape = ProdyShapeLg

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Input & Forms
// =============================================================================

val JournalTextFieldShape = ProdyShapeLg
val CommentInputShape = ProdyShapeXxl
val DatePickerShape = ProdyShapeLg

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Gamification
// =============================================================================

val StreakContainerShape = ProdyShapeXl
val XpContainerShape = ProdyShapeLg
val LevelBadgeShape = ProdyShapeMd

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - List Items
// =============================================================================

val ListItemShape = ProdyShapeMd
val ListItemShapeCompact = ProdyShapeSm
val SelectableListItemShape = ProdyShapeMd

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Time Capsule
// =============================================================================

val TimeCapsuleTabContainerShape = ProdyShapeMd
val TimeCapsuleTagShape = ProdyShapeSm
val TimeCapsuleEmptyCircleShape: Shape = CircleShape

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Onboarding
// =============================================================================

val OnboardingIndicatorShape = ProdyShapeFull
val OnboardingFeatureCardShape = ProdyShapeLg
val OnboardingXpArcShape: Shape = CircleShape

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Home Screen
// =============================================================================

val QuickActionTileShape = ProdyShapeLg
val WisdomCarouselCardShape = ProdyShapeXl
val GreetingBannerShape = ProdyShapeLg

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Stats Screen
// =============================================================================

val ActivityPulseShape: Shape = CircleShape
val SummaryCardShape = ProdyShapeLg
val StatsHeroContainerShape = ProdyShapeXl

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Profile Screen
// =============================================================================

val TrophyShelfShape = ProdyShapeLg
val ProfileStatCardShape = ProdyShapeMd
val SettingsRowShape = ProdyShapeMd