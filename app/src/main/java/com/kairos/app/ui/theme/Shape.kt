package com.kairos.app.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Kairos Design System - Shapes
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

val KairosShapeNone = RoundedCornerShape(CornerNone)
val KairosShapeXs = RoundedCornerShape(CornerXs)
val KairosShapeSm = RoundedCornerShape(CornerSm)
val KairosShapeMd = RoundedCornerShape(CornerMd)
val KairosShapeLg = RoundedCornerShape(CornerLg)
val KairosShapeXl = RoundedCornerShape(CornerXl)
val KairosShapeXxl = RoundedCornerShape(CornerXxl)
val KairosShapeFull = RoundedCornerShape(CornerFull)

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

val KairosShapes = Shapes(
    extraSmall = KairosShapeXs,
    small = KairosShapeSm,
    medium = KairosShapeMd,
    large = KairosShapeLg,
    extraLarge = KairosShapeXxl
)

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Cards
// =============================================================================

val CardShape = KairosShapeLg
val ElevatedCardShape = KairosShapeLg
val CompactCardShape = KairosShapeMd
val FeaturedCardShape = KairosShapeXl
val CardShapeSmall = KairosShapeSm
val CardShapeDefault = KairosShapeMd
val CardShapeLarge = KairosShapeLg
val CardShapeHero = KairosShapeXl

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Buttons
// =============================================================================

val ButtonShape = KairosShapeMd
val SmallButtonShape = KairosShapeSm
val PillButtonShape = KairosShapeFull
val FloatingActionButtonShape = KairosShapeLg
val ExtendedFabShape = KairosShapeLg
val ButtonShapeDefault = KairosShapeMd
val ButtonShapePill = KairosShapeFull
val ButtonShapeSmall = KairosShapeSm
val ButtonShapeLarge = KairosShapeLg

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Chips & Badges
// =============================================================================

val ChipShape = KairosShapeSm
val PillChipShape = KairosShapeFull
val BadgeShape = KairosShapeXs
val StatusBadgeShape = KairosShapeMd
val BadgeShapeSmall = KairosShapeXs
val BadgeShapePill = KairosShapeFull
val AchievementBadgeShape = KairosShapeLg
val RarityBadgeShape: Shape = CircleShape

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Modals & Overlays
// =============================================================================

val DialogShape = KairosShapeXxl
val SnackbarShape = KairosShapeMd
val TooltipShape = KairosShapeSm
val ModalCardShape = KairosShapeXxl
val CelebrationCardShape = KairosShapeXxl

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Inputs
// =============================================================================

val SearchBarShape = KairosShapeFull
val TextFieldShape = KairosShapeMd
val DropdownShape = KairosShapeMd

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Avatars & Images
// =============================================================================

val AvatarShape: Shape = CircleShape
val SmallAvatarShape: Shape = CircleShape
val ThumbnailShape = KairosShapeMd

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Progress & Indicators
// =============================================================================

val ProgressIndicatorShape = KairosShapeFull
val ProgressTrackShape = KairosShapeFull
val SliderThumbShape: Shape = CircleShape
val ProgressBarShape = KairosShapeFull
val XpBarShape = KairosShapeFull
val LevelProgressShape = KairosShapeFull

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Navigation
// =============================================================================

val NavIndicatorShape = KairosShapeFull
val TabIndicatorShape = KairosShapeFull

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Feature Shapes
// =============================================================================

val AchievementCardShape = KairosShapeXl
val OnboardingShape = KairosShapeXxl
val QuoteCardShape = KairosShapeXl
val StreakBadgeShape = KairosShapeLg
val MoodSelectorShape = KairosShapeLg
val NotificationCardShape = KairosShapeLg

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Leaderboard
// =============================================================================

val LeaderboardItemShape = KairosShapeMd
val LeaderboardTopShape = KairosShapeLg

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Input & Forms
// =============================================================================

val JournalTextFieldShape = KairosShapeLg
val CommentInputShape = KairosShapeXxl
val DatePickerShape = KairosShapeLg

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Gamification
// =============================================================================

val StreakContainerShape = KairosShapeXl
val XpContainerShape = KairosShapeLg
val LevelBadgeShape = KairosShapeMd

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - List Items
// =============================================================================

val ListItemShape = KairosShapeMd
val ListItemShapeCompact = KairosShapeSm
val SelectableListItemShape = KairosShapeMd

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Time Capsule
// =============================================================================

val TimeCapsuleTabContainerShape = KairosShapeMd
val TimeCapsuleTagShape = KairosShapeSm
val TimeCapsuleEmptyCircleShape: Shape = CircleShape

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Onboarding
// =============================================================================

val OnboardingIndicatorShape = KairosShapeFull
val OnboardingFeatureCardShape = KairosShapeLg
val OnboardingXpArcShape: Shape = CircleShape

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Home Screen
// =============================================================================

val QuickActionTileShape = KairosShapeLg
val WisdomCarouselCardShape = KairosShapeXl
val GreetingBannerShape = KairosShapeLg

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Stats Screen
// =============================================================================

val ActivityPulseShape: Shape = CircleShape
val SummaryCardShape = KairosShapeLg
val StatsHeroContainerShape = KairosShapeXl

// =============================================================================
// BACKWARD-COMPATIBLE ALIASES - Profile Screen
// =============================================================================

val TrophyShelfShape = KairosShapeLg
val ProfileStatCardShape = KairosShapeMd
val SettingsRowShape = KairosShapeMd