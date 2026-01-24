package com.prody.prashant.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Prody Design System - Color Palette (Redesigned)
 *
 * A premium, minimalist color system for the Prody self-improvement companion app.
 * Following the design principles of extreme minimalism, cleanliness, and modernity.
 *
 * Design Philosophy:
 * - NO shadows, gradients, or skeuomorphism - strictly flat and 2D
 * - Deep dark teal/green backgrounds for dark mode
 * - Clean bright off-white for light mode
 * - Vibrant neon green accent for interactive elements
 * - Clear visual hierarchy with purposeful color usage
 *
 * HAVEN SPECIFIC PALETTE ("Blush & Bone"):
 * - Light: Bone background (#FAF9F6), Blush bubbles (#E2A9A9), Dark text (#2D2424)
 * - Dark: Deep Plum background (#1A1616), Deep Rose bubbles (#5E3E3E), Soft Cream text (#E8E4E4)
 */

// =============================================================================
// HAVEN PALETTE - "Blush & Bone" / "Night Sanctuary"
// =============================================================================

// Light Mode ("Blush & Bone")
val HavenBackgroundLight = Color(0xFFFAF9F6)          // Soft Bone White
val HavenBubbleLight = Color(0xFFE2A9A9)              // Blush Pink (Haven's Voice)
val HavenUserBubbleLight = Color(0xFFFFFFFF)          // White (User's Voice)
val HavenTextLight = Color(0xFF2D2424)                // Dark Brown/Black (Soft)
val HavenAccentGold = Color(0xFFD4AF37)               // Soft Gold (Accent)
val HavenAccentRose = Color(0xFFB76E79)               // Rose Gold (Accent)

// Dark Mode ("Night Sanctuary")
val HavenBackgroundDark = Color(0xFF1A1616)           // Deep Plum/Black
val HavenBubbleDark = Color(0xFF5E3E3E)               // Deepened Rose (Haven's Voice)
val HavenUserBubbleDark = Color(0xFF2A2424)           // Darker Plum (User's Voice)
val HavenTextDark = Color(0xFFE8E4E4)                 // Soft Cream


// =============================================================================
// PRIMARY BRAND COLORS - Core Identity
// =============================================================================

/** Vibrant neon green accent - THE signature color for interactive elements */
val ProdyAccentGreen = Color(0xFF36F97F)
val ProdyAccentGreenDark = Color(0xFF2ED56B)
val ProdyAccentGreenLight = Color(0xFF5DFA96)

// =============================================================================
// LIGHT THEME COLORS - Clean, Bright Aesthetic
// =============================================================================

// Background & Surface - Clean whites
val ProdyBackgroundLight = Color(0xFFF0F4F3)          // Primary light background
val ProdySurfaceLight = Color(0xFFFFFFFF)             // Pure white surface
val ProdySurfaceVariantLight = Color(0xFFF5F7F6)      // Subtle surface variant
val ProdySurfaceContainerLight = Color(0xFFE8EDEC)    // Container background
val ProdySurfaceElevatedLight = Color(0xFFFFFFFF)     // Elevated surface

// Text Colors - Dark for readability
val ProdyTextPrimaryLight = Color(0xFF1A1A1A)         // Primary text - near black
val ProdyTextSecondaryLight = Color(0xFF6C757D)       // Secondary text - medium gray
val ProdyTextTertiaryLight = Color(0xFFA0A8AD)        // Tertiary text - light gray
val ProdyTextOnAccentLight = Color(0xFF000000)        // Text on accent - black

// Interactive Elements
val ProdyOutlineLight = Color(0xFFE0E7E6)             // Borders and dividers
val ProdyOutlineVariantLight = Color(0xFFD0D8D7)      // Variant outline
val ProdyDividerLight = Color(0xFFE0E7E6)             // Divider color

// =============================================================================
// DARK THEME COLORS - Deep Teal/Green Aesthetic
// =============================================================================

// Background & Surface - Deep teal/green
val ProdyBackgroundDark = Color(0xFF0D2826)           // Primary dark background - deep teal
val ProdySurfaceDark = Color(0xFF142E2B)              // Surface - slightly lighter
val ProdySurfaceVariantDark = Color(0xFF1A3633)       // Surface variant
val ProdySurfaceContainerDark = Color(0xFF2A4240)     // Container background
val ProdySurfaceElevatedDark = Color(0xFF1F3B38)      // Elevated surface

// Text Colors - Light for contrast
val ProdyTextPrimaryDark = Color(0xFFFFFFFF)          // Primary text - pure white
val ProdyTextSecondaryDark = Color(0xFFD3D8D7)        // Secondary text - light gray
val ProdyTextTertiaryDark = Color(0xFF8A9493)         // Tertiary text - muted gray
val ProdyTextOnAccentDark = Color(0xFF000000)         // Text on accent - black

// Interactive Elements
val ProdyOutlineDark = Color(0xFF3A5250)              // Borders and dividers
val ProdyOutlineVariantDark = Color(0xFF4A6260)       // Variant outline
val ProdyDividerDark = Color(0xFF3A5250)              // Divider color

// =============================================================================
// SEMANTIC COLORS - Flat, No Gradients
// =============================================================================

// Success States
val ProdySuccess = Color(0xFF36F97F)                  // Success green (same as accent)
val ProdySuccessContainer = Color(0xFFE6FFF0)         // Light mode success container
val ProdySuccessContainerDark = Color(0xFF1A3331)     // Dark mode success container
val ProdyOnSuccess = Color(0xFF000000)

// Error States
val ProdyError = Color(0xFFE53935)                    // Error red - flat
val ProdyErrorContainer = Color(0xFFFFEBEE)           // Light mode error container
val ProdyErrorContainerDark = Color(0xFF4A2525)       // Dark mode error container
val ProdyOnError = Color(0xFFFFFFFF)

// Warning States
val ProdyWarning = Color(0xFFFFC107)                  // Warning amber - flat
val ProdyWarningContainer = Color(0xFFFFF8E1)         // Light mode warning container
val ProdyWarningContainerDark = Color(0xFF4A4020)     // Dark mode warning container
val ProdyOnWarning = Color(0xFF1A1A1A)

// Info States
val ProdyInfo = Color(0xFF42A5F5)                     // Info blue - flat
val ProdyInfoContainer = Color(0xFFE3F2FD)            // Light mode info container
val ProdyInfoContainerDark = Color(0xFF1A3040)        // Dark mode info container
val ProdyOnInfo = Color(0xFFFFFFFF)

// Accent Blue
val ProdyAccentBlue = Color(0xFF42A5F5)               // Blue accent for scheduled items

// Text Secondary (alias for compatibility)
val ProdyTextSecondary = ProdyTextSecondaryLight      // Secondary text color

// =============================================================================
// MOOD COLORS - Flat, Psychologically Resonant
// =============================================================================

val MoodHappy = Color(0xFFFFC93C)                     // Sunshine gold - joy
val MoodCalm = Color(0xFF6CB4D4)                      // Serene sky blue - peace
val MoodAnxious = Color(0xFFE8A87C)                   // Soft coral - gentle
val MoodSad = Color(0xFF8BA8B9)                       // Muted slate blue - understanding
val MoodMotivated = Color(0xFFFFD166)                 // Energetic amber - drive
val MoodGrateful = Color(0xFF7EC8A3)                  // Soft sage - appreciation
val MoodConfused = Color(0xFFB39DDB)                  // Soft lavender - reflection
val MoodExcited = Color(0xFFFF7B5A)                   // Vibrant coral - enthusiasm
val MoodEnergetic = Color(0xFFFF8F42)                 // Dynamic orange - vitality
val MoodInspired = Color(0xFF9B6DD4)                  // Creative purple - imagination
val MoodNostalgic = Color(0xFFD4A574)                 // Warm sepia - remembrance

// =============================================================================
// GAMIFICATION COLORS - Leaderboard & Achievements (Flat)
// =============================================================================

// Leaderboard Podium - Muted but distinct
val LeaderboardGold = Color(0xFFD4AF37)               // Gold for 1st - muted
val LeaderboardSilver = Color(0xFFB8C0C4)             // Silver for 2nd - muted
val LeaderboardBronze = Color(0xFFCD9575)             // Bronze for 3rd - muted

// Gold Palette for 1st Place Animated Banner
val LeaderboardGoldLight = Color(0xFFE6C866)
val LeaderboardGoldDark = Color(0xFFB8962D)
val LeaderboardGoldShimmer = Color(0xFFF5E6B3)

// Silver Palette for 2nd Place Animated Banner
val LeaderboardSilverLight = Color(0xFFD4DCDF)
val LeaderboardSilverDark = Color(0xFF9CA4A8)
val LeaderboardSilverShimmer = Color(0xFFEDF1F2)

// Bronze Palette for 3rd Place Animated Banner
val LeaderboardBronzeLight = Color(0xFFDEAF8F)
val LeaderboardBronzeDark = Color(0xFFB07D5B)
val LeaderboardBronzeShimmer = Color(0xFFF0D4C4)

// User Highlight
val LeaderboardUserHighlight = Color(0xFF36F97F)      // Accent green for YOU section

// Achievement Rarity Colors (Flat)
val RarityCommon = Color(0xFF78909C)                  // Slate gray
val RarityUncommon = Color(0xFF66BB6A)                // Fresh green
val RarityRare = Color(0xFF42A5F5)                    // Bright blue
val RarityEpic = Color(0xFFAB47BC)                    // Rich purple
val RarityLegendary = Color(0xFFD4AF37)               // Gold
val RarityMythic = Color(0xFFFFD700)                  // Brilliant gold

// =============================================================================
// STREAK COLORS - Fire & Energy (Flat)
// =============================================================================

val StreakColor = Color(0xFFE65C2C)                   // Fire orange
val StreakFire = Color(0xFFE65C2C)                    // Dynamic fire
val StreakWarm = Color(0xFFFFB74D)                    // Warm building up
val StreakHot = Color(0xFFFF7043)                     // On fire
val StreakBlazing = Color(0xFFE53935)                 // Incredible streak

// Streak Milestone Colors
val StreakWeekMilestone = Color(0xFFFF8C42)           // 7-day
val StreakMonthMilestone = Color(0xFFE65C2C)          // 30-day
val StreakQuarterMilestone = Color(0xFFD43B3B)        // 90-day
val StreakYearMilestone = Color(0xFFFFD700)           // 365-day

// Streak Milestone Colors (numbered aliases for BoostingSystem)
val StreakMilestone7 = StreakWeekMilestone            // 7-day milestone
val StreakMilestone30 = StreakMonthMilestone          // 30-day milestone
val StreakMilestone100 = Color(0xFFD43B3B)            // 100-day milestone
val StreakMilestone365 = StreakYearMilestone          // 365-day milestone

// Support/Boost Colors
val SupportBoost = Color(0xFF36F97F)                  // Green for boost
val SupportRespect = Color(0xFF42A5F5)                // Blue for respect
val SupportEncourage = Color(0xFFFFB300)              // Amber for encourage

// =============================================================================
// XP & PROGRESS COLORS (Flat)
// =============================================================================

val XpBarBackground = Color(0xFFE0E7E6)               // Light mode track
val XpBarBackgroundDark = Color(0xFF2A4240)           // Dark mode track
val XpBarFill = Color(0xFF36F97F)                     // Accent green fill
val XpBarOverflow = Color(0xFFFFD700)                 // Gold when exceeds requirement
val LevelUpGlow = Color(0xFF36F97F)                   // Level up accent

val LevelBadgeBackground = Color(0xFF36F97F)
val LevelBadgeText = Color(0xFF000000)

// =============================================================================
// FUTURE MESSAGE / TIME CAPSULE COLORS
// =============================================================================

// Light Mode
val TimeCapsuleBackgroundLight = Color(0xFFF0F4F3)
val TimeCapsuleSurfaceLight = Color(0xFFFFFFFF)
val TimeCapsuleTextPrimaryLight = Color(0xFF1A1A1A)
val TimeCapsuleTextSecondaryLight = Color(0xFF6C757D)
val TimeCapsulePlaceholderLight = Color(0xFFADB5BD)
val TimeCapsuleIconLight = Color(0xFF6C757D)
val TimeCapsuleDividerLight = Color(0xFFE0E7E6)
val TimeCapsuleTagBgInactiveLight = Color(0xFFF0F3F2)
val TimeCapsuleTagTextInactiveLight = Color(0xFF1A1A1A)
val TimeCapsuleTabContainerLight = Color(0xFFF0F3F2)

// Dark Mode
val TimeCapsuleBackgroundDark = Color(0xFF0D2826)
val TimeCapsuleSurfaceDark = Color(0xFF1A3633)
val TimeCapsuleTextPrimaryDark = Color(0xFFFFFFFF)
val TimeCapsuleTextSecondaryDark = Color(0xFFD3D8D7)
val TimeCapsulePlaceholderDark = Color(0xFF8A9493)
val TimeCapsuleIconDark = Color(0xFFFFFFFF)
val TimeCapsuleDividerDark = Color(0xFF3A5250)
val TimeCapsuleTagBgInactiveDark = Color(0xFF2A4240)
val TimeCapsuleTagTextInactiveDark = Color(0xFFD3D8D7)
val TimeCapsuleTabContainerDark = Color(0xFF1A3331)

// Accent for Time Capsule (same vibrant green)
val TimeCapsuleAccent = Color(0xFF36F97F)

// Category Colors for Future Messages
val FutureCategoryGoal = Color(0xFF36F97F)            // Green - goals
val FutureCategoryMotivation = Color(0xFFFF7043)      // Orange - motivation
val FutureCategoryPromise = Color(0xFF5C6BC0)         // Indigo - commitments
val FutureCategoryGeneral = Color(0xFF90A4AE)         // Gray - general

// Status Colors
val FutureMessageScheduled = Color(0xFF5C6BC0)        // Indigo - waiting
val FutureMessageArrived = Color(0xFFFFD700)          // Gold - delivered
val FutureMessageRead = Color(0xFF36F97F)             // Green - opened

// =============================================================================
// JOURNAL COLORS
// =============================================================================

// Light Mode
val JournalBackgroundLight = Color(0xFFF0F4F3)
val JournalSurfaceLight = Color(0xFFFFFFFF)
val JournalTextPrimaryLight = Color(0xFF1A1A1A)
val JournalTextSecondaryLight = Color(0xFF6C757D)
val JournalPlaceholderLight = Color(0xFFADB5BD)
val JournalSliderInactiveLight = Color(0xFFDEE2E6)
val JournalIconBorderLight = Color(0xFFE9ECEF)
val JournalAccent = Color(0xFF36F97F)

// Dark Mode
val JournalBackgroundDark = Color(0xFF0D2826)
val JournalSurfaceDark = Color(0xFF2A4240)
val JournalTextPrimaryDark = Color(0xFFFFFFFF)
val JournalTextSecondaryDark = Color(0xFFD3D8D7)
val JournalPlaceholderDark = Color(0xFF8A9493)
val JournalSliderInactiveDark = Color(0xFF404B4A)
val JournalIconBorderDark = Color(0xFF3A5250)

// Journal History Accent
val JournalHistoryAccent = Color(0xFF36F97F)

// Journal History Light
val JournalHistoryBackgroundLight = Color(0xFFF0F4F3)
val JournalHistoryCardLight = Color(0xFFFFFFFF)
val JournalHistoryTextPrimaryLight = Color(0xFF1A1A1A)
val JournalHistoryTextSecondaryLight = Color(0xFF6C757D)
val JournalHistoryDividerLight = Color(0xFFE0E7E6)

// Journal History Dark
val JournalHistoryBackgroundDark = Color(0xFF0D2826)
val JournalHistoryCardDark = Color(0xFF1A3633)
val JournalHistoryTextPrimaryDark = Color(0xFFFFFFFF)
val JournalHistoryTextSecondaryDark = Color(0xFFD3D8D7)
val JournalHistoryDividerDark = Color(0xFF3A5250)
val JournalHistoryIntensityBgDark = Color(0xFF2A4240)
val JournalHistoryIntensityBgLight = Color(0xFFE8EDEC)
val JournalHistoryDateBlockBgDark = Color(0xFF36F97F)
val JournalHistoryDateBlockBgLight = Color(0xFF36F97F)
val JournalHistoryDateBlockTextDark = Color(0xFF000000)
val JournalHistoryDateBlockTextLight = Color(0xFF000000)
val JournalHistoryButtonBorderDark = Color(0xFFFFFFFF)
val JournalHistoryButtonBorderLight = Color(0xFF1A1A1A)

// =============================================================================
// ONBOARDING COLORS
// =============================================================================

// Light Mode
val OnboardingBackgroundLight = Color(0xFFF0F4F3)
val OnboardingSurfaceLight = Color(0xFFFFFFFF)
val OnboardingSurfaceVariantLight = Color(0xFFF5F7F6)
val OnboardingCardLight = Color(0xFFFFFFFF)
val OnboardingTextPrimaryLight = Color(0xFF1A1A1A)
val OnboardingTextSecondaryLight = Color(0xFF6C757D)
val OnboardingTextTertiaryLight = Color(0xFFA0A8AD)
val OnboardingIconContainerLight = Color(0xFFE6FFF0)
val OnboardingDividerLight = Color(0xFFE0E7E6)

// Dark Mode
val OnboardingBackgroundDark = Color(0xFF0D2826)
val OnboardingSurfaceDark = Color(0xFF142E2B)
val OnboardingSurfaceVariantDark = Color(0xFF1A3633)
val OnboardingCardDark = Color(0xFF1A3633)
val OnboardingTextPrimaryDark = Color(0xFFFFFFFF)
val OnboardingTextSecondaryDark = Color(0xFFD3D8D7)
val OnboardingTextTertiaryDark = Color(0xFF8A9493)
val OnboardingIconContainerDark = Color(0xFF1A3331)
val OnboardingDividerDark = Color(0xFF3A5250)

// Onboarding Accent (same vibrant green)
val OnboardingAccent = Color(0xFF36F97F)
val OnboardingButtonPrimary = Color(0xFF36F97F)
val OnboardingButtonTextLight = Color(0xFF000000)
val OnboardingButtonTextDark = Color(0xFF000000)

// Progress Indicators
val OnboardingProgressActive = Color(0xFF36F97F)
val OnboardingProgressInactiveLight = Color(0xFFE0E7E6)
val OnboardingProgressInactiveDark = Color(0xFF3A5250)

// XP Arc
val OnboardingXpArcFill = Color(0xFF36F97F)
val OnboardingXpArcBackgroundLight = Color(0xFFE0E7E6)
val OnboardingXpArcBackgroundDark = Color(0xFF2A4240)

// =============================================================================
// PROFILE SCREEN COLORS
// =============================================================================

// Banner placeholder
val ProfileBannerPlaceholderLight = Color(0xFFE8EDEC)
val ProfileBannerPlaceholderDark = Color(0xFF1A3633)

// Avatar ring progress
val ProfileAvatarRing = Color(0xFF36F97F)
val ProfileAvatarRingBackground = Color(0xFF3A5250)

// Badge colors
val ProfileBadgeBackground = Color(0xFF36F97F)
val ProfileBadgeText = Color(0xFF000000)

// Trophy shelf
val TrophyShelfBackgroundLight = Color(0xFFF5F7F6)
val TrophyShelfBackgroundDark = Color(0xFF1A3633)
val TrophyLockedOverlay = Color(0x80000000)

// =============================================================================
// STATS SCREEN COLORS
// =============================================================================

// Activity Pulse visualization
val ActivityPulseGreen = Color(0xFF36F97F)
val ActivityPulseGreenLight = Color(0xFF5DFA96)
val ActivityPulseGreenDark = Color(0xFF2ED56B)
val ActivityPulseBackground = Color(0xFF2A4240)
val ActivityPulseBackgroundLight = Color(0xFFE8EDEC)

// Summary Cards
val SummaryCardBackgroundLight = Color(0xFFFFFFFF)
val SummaryCardBackgroundDark = Color(0xFF1A3633)
val SummaryCardAccent = Color(0xFF36F97F)

// =============================================================================
// HOME SCREEN COLORS
// =============================================================================

// Quick Action Tiles
val QuickActionTileLight = Color(0xFFFFFFFF)
val QuickActionTileDark = Color(0xFF1A3633)
val QuickActionIconBg = Color(0xFFE6FFF0)
val QuickActionIconBgDark = Color(0xFF1A3331)

// Daily Wisdom Carousel
val WisdomCardLight = Color(0xFFFFFFFF)
val WisdomCardDark = Color(0xFF1A3633)
val WisdomCardAccent = Color(0xFF36F97F)

// Daily Wisdom Card Category Colors
val WordOfDayColor = Color(0xFFD4AF37)           // Gold - vocabulary
val IdiomPurple = Color(0xFFB39DDB)              // Soft lavender - idioms
val ProverbTeal = Color(0xFF26A69A)              // Teal - proverbs
val SeedGold = Color(0xFFE8B42F)                 // Golden yellow - seeds

// =============================================================================
// WISDOM & QUOTES COLORS (Flat)
// =============================================================================

val WisdomCardBackground = Color(0xFFFFFFFF)
val WisdomCardBackgroundDark = Color(0xFF1A3633)
val WisdomQuoteText = Color(0xFF1A1A1A)
val WisdomQuoteTextDark = Color(0xFFFFFFFF)
val WisdomAuthor = Color(0xFF6C757D)
val WisdomAuthorDark = Color(0xFFD3D8D7)

// Wisdom category colors
val WisdomStoic = Color(0xFF5C6BC0)                   // Indigo
val WisdomGrowth = Color(0xFF36F97F)                  // Accent green
val WisdomMindfulness = Color(0xFF26A69A)             // Teal
val WisdomResilience = Color(0xFFEF6C00)              // Orange
val WisdomGratitude = Color(0xFFFFB300)               // Amber
val WisdomPerspective = Color(0xFF7E57C2)             // Purple

// =============================================================================
// CHALLENGE COLORS (Flat)
// =============================================================================

val ChallengeActive = Color(0xFF36F97F)               // Green - in progress
val ChallengeCompleted = Color(0xFFFFD700)            // Gold - achieved
val ChallengePending = Color(0xFFFFB300)              // Amber - upcoming
val ChallengeExpired = Color(0xFF9E9E9E)              // Gray - missed

val ChallengeMilestoneReached = Color(0xFF36F97F)
val ChallengeMilestonePending = Color(0xFFE0E7E6)

// =============================================================================
// INTERACTIVE STATES (Flat)
// =============================================================================

val InteractiveHoverLight = Color(0x08000000)         // Very subtle hover
val InteractivePressedLight = Color(0x10000000)       // Subtle pressed
val InteractiveHoverDark = Color(0x08FFFFFF)
val InteractivePressedDark = Color(0x10FFFFFF)

// Focus states
val InteractiveFocus = Color(0x1F36F97F)              // Green focus ring

// =============================================================================
// OVERLAY COLORS
// =============================================================================

val OverlayLight = Color(0xE6FFFFFF)                  // White overlay
val OverlayDark = Color(0x80000000)                   // Dark overlay
val Scrim = Color(0x52000000)                         // Scrim for modals

// =============================================================================
// NOTIFICATION COLORS (Flat)
// =============================================================================

val NotificationPrimary = Color(0xFF36F97F)
val NotificationSuccess = Color(0xFF36F97F)
val NotificationWarning = Color(0xFFFF9800)
val NotificationCelebration = Color(0xFFFFD700)
val NotificationStreak = Color(0xFFE65C2C)
val NotificationAchievement = Color(0xFFAB47BC)
val NotificationReminder = Color(0xFF00BCD4)
val NotificationMotivation = Color(0xFFFF5722)

// =============================================================================
// TEXT HIERARCHY COLORS
// =============================================================================

val TextPrimaryLight = Color(0xFF1A1A1A)
val TextSecondaryLight = Color(0xFF6C757D)
val TextTertiaryLight = Color(0xFFA0A8AD)

val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFFD3D8D7)
val TextTertiaryDark = Color(0xFF8A9493)

// =============================================================================
// MATERIAL 3 COLOR SCHEME COLORS - For Theme.kt compatibility
// =============================================================================

// Light Theme Material 3 Colors
val ProdyPrimary = Color(0xFF36F97F)
val ProdyPrimaryVariant = Color(0xFF2ED56B)
val ProdyOnPrimary = Color(0xFF000000)
val ProdyPrimaryContainer = Color(0xFFE6FFF0)

val ProdySecondary = Color(0xFF1A1A1A)
val ProdySecondaryVariant = Color(0xFF2A2A2A)
val ProdyOnSecondary = Color(0xFFFFFFFF)

val ProdyTertiary = Color(0xFF6C757D)
val ProdyOnTertiary = Color(0xFFFFFFFF)
val ProdyTertiaryContainer = Color(0xFFF0F4F3)

val ProdyBackground = Color(0xFFF0F4F3)
val ProdyOnBackground = Color(0xFF1A1A1A)

val ProdySurface = Color(0xFFFFFFFF)
val ProdySurfaceVariant = Color(0xFFF5F7F6)
val ProdyOnSurface = Color(0xFF1A1A1A)
val ProdyOnSurfaceVariant = Color(0xFF6C757D)
val ProdySurfaceElevated = Color(0xFFFFFFFF)
val ProdySurfaceDim = Color(0xFFE8EDEC)

// Note: ProdyError, ProdyOnError, ProdyErrorContainer, ProdySuccess, ProdyOnSuccess,
// ProdyWarning, ProdyOnWarning are defined in the SEMANTIC COLORS section above

val ProdyOutline = Color(0xFFE0E7E6)
val ProdyOutlineVariant = Color(0xFFD0D8D7)

// Dark Theme Material 3 Colors
val ProdyPrimaryDark = Color(0xFF36F97F)
val ProdyPrimaryVariantDark = Color(0xFF5DFA96)
val ProdyOnPrimaryDark = Color(0xFF000000)
val ProdyPrimaryContainerDark = Color(0xFF1A3331)

val ProdySecondaryDark = Color(0xFFFFFFFF)
val ProdyOnSecondaryDark = Color(0xFF1A1A1A)
val ProdySecondaryContainerDark = Color(0xFF2A4240)

val ProdyTertiaryDark = Color(0xFFD3D8D7)
val ProdyOnTertiaryDark = Color(0xFF1A1A1A)
val ProdyTertiaryContainerDark = Color(0xFF1A3633)

// Note: ProdyBackgroundDark is defined in DARK THEME COLORS section above
val ProdyOnBackgroundDark = Color(0xFFFFFFFF)

// Note: ProdySurfaceDark, ProdySurfaceVariantDark, ProdySurfaceElevatedDark are defined above
val ProdyOnSurfaceDark = Color(0xFFFFFFFF)
val ProdyOnSurfaceVariantDark = Color(0xFFD3D8D7)
val ProdySurfaceDimDark = Color(0xFF0A1F1D)

val ProdyErrorDark = Color(0xFFFF8A80)
val ProdyOnErrorDark = Color(0xFF1A1A1A)
// Note: ProdyErrorContainerDark is defined in SEMANTIC COLORS section above

// Note: ProdyOutlineDark, ProdyOutlineVariantDark are defined in DARK THEME COLORS section above

// =============================================================================
// BRAND ALIASES
// =============================================================================

val ProdyGreen = ProdyAccentGreen
val ProdyGreenLight = ProdyAccentGreenLight
val ProdyGreenDark = ProdyAccentGreenDark

// Main accent color alias
val ProdyAccent = ProdyAccentGreen

// Premium violet dark variant (missing)
val ProdyPremiumVioletDark = Color(0xFF4A3AB8)

// =============================================================================
// GRADIENT DEFINITIONS (Minimal - only for special effects like animated banners)
// =============================================================================

// Top-level gradient definitions for easy access
val primaryGradient = listOf(ProdyAccentGreenDark, ProdyAccentGreen, ProdyAccentGreenLight)
val goldGradient = listOf(LeaderboardGoldDark, LeaderboardGold, LeaderboardGoldLight)

object ProdyGradients {
    // Primary gradient (green accent)
    val primaryGradient = listOf(ProdyAccentGreenDark, ProdyAccentGreen, ProdyAccentGreenLight)

    // Gold gradient for achievements/rewards
    val goldGradient = listOf(LeaderboardGoldDark, LeaderboardGold, LeaderboardGoldLight)

    // Gold banner gradient for 1st place
    val goldBanner = listOf(LeaderboardGoldDark, LeaderboardGold, LeaderboardGoldLight, LeaderboardGold)

    // Silver banner gradient for 2nd place
    val silverBanner = listOf(LeaderboardSilverDark, LeaderboardSilver, LeaderboardSilverLight, LeaderboardSilver)

    // Bronze banner gradient for 3rd place
    val bronzeBanner = listOf(LeaderboardBronzeDark, LeaderboardBronze, LeaderboardBronzeLight, LeaderboardBronze)

    // Streak fire gradient (subtle)
    val streakGradient = listOf(StreakWarm, StreakFire, StreakHot)

    // Level up celebration
    val levelUpGradient = listOf(ProdyAccentGreenDark, ProdyAccentGreen, ProdyAccentGreenLight)

    // Notification gradients
    val streakNotificationGradient = listOf(StreakWarm, StreakFire, StreakHot)
    val celebrationGradient = listOf(LeaderboardGoldDark, LeaderboardGold, LeaderboardGoldLight)
    val achievementGradient = listOf(RarityEpic, RarityLegendary, LeaderboardGold)
    val motivationGradient = listOf(MoodMotivated, MoodExcited, MoodEnergetic)
    val oceanGradient = listOf(MoodCalm, ProdyInfo, MoodCalm)
    val growthGradient = listOf(ProdyAccentGreenDark, ProdyAccentGreen, ProdyAccentGreenLight)
    val serenityGradient = listOf(MoodCalm, MoodGrateful, MoodCalm)
}

// =============================================================================
// DEPRECATED COLORS - Keeping for backward compatibility
// =============================================================================

// Tier colors for gamification - actively used throughout the app
val GoldTier = Color(0xFFD4AF37)
val SilverTier = Color(0xFFB8C0C4)
val BronzeTier = Color(0xFFCD9575)
val PlatinumTier = Color(0xFF8CD3D9)

val AchievementUnlocked = Color(0xFF36F97F)
val AchievementLocked = Color(0xFF888888)
val AchievementProgress = Color(0xFFF5A623)
val AchievementRare = Color(0xFF7B68EE)

val StreakIce = Color(0xFF3B9DD4)
val StreakGlow = Color(0xFFFFD93D)
val StreakCold = Color(0xFF90CAF9)
val StreakEmber = Color(0xFFFFC371)
val StreakInferno = Color(0xFFD500F9)
val StreakWeek = Color(0xFFFF8C42)
val StreakMonth = Color(0xFFE65C2C)
val StreakQuarter = Color(0xFFD43B3B)

val LeaderboardFirst = LeaderboardGold
val LeaderboardSecond = LeaderboardSilver
val LeaderboardThird = LeaderboardBronze
val LeaderboardTop10 = Color(0xFF36F97F)
val LeaderboardTop10Highlight = Color(0xFF36F97F)
val LeaderboardUser = Color(0xFF36F97F)
val LeaderboardFirstGlow = Color(0x66D4AF37)
val LeaderboardSecondGlow = Color(0x66B8C0C4)
val LeaderboardThirdGlow = Color(0x66CD9575)

// Rarity Glow Colors (deprecated - flat design has no glows)
val RarityCommonGlow = Color(0x339E9E9E)
val RarityUncommonGlow = Color(0x3366BB6A)
val RarityRareGlow = Color(0x3342A5F5)
val RarityEpicGlow = Color(0x33AB47BC)
val RarityLegendaryGlow = Color(0x33D4AF37)
val RarityMythicGlow = Color(0x66FFD700)

val XpBarGlow = Color(0x6636F97F)
val XpBarFillAlt = Color(0xFF36F97F)

val SuccessColor = ProdySuccess
val SuccessGreen = ProdySuccess
val WarningColor = ProdyWarning
val ErrorColor = ProdyError
val InfoColor = ProdyInfo

val SuccessGreenContainer = ProdySuccessContainer
val WarningAmberContainer = ProdyWarningContainer
val ErrorRedContainer = ProdyErrorContainer
val InfoBlueContainer = ProdyInfoContainer

val SurfaceLight = ProdySurfaceLight
val SurfaceMedium = Color(0xFFF5F7F6)
val SurfaceDark = ProdySurfaceDark
val SurfaceDarkElevated = ProdySurfaceElevatedDark

val GradientPrimaryStart = Color(0xFF36F97F)
val GradientPrimaryEnd = Color(0xFF5DFA96)
val GradientWarmStart = Color(0xFFE8EDEC)
val GradientWarmEnd = Color(0xFFF5F7F6)
val GradientAccentStart = Color(0xFF2ED56B)
val GradientAccentEnd = Color(0xFF36F97F)

val ProdyPremiumViolet = Color(0xFF6B5CE7)
val ProdyPremiumVioletVariant = Color(0xFF5A4AD4)
val ProdyPremiumVioletLight = Color(0xFF8B7EF0)
val ProdyPremiumVioletContainer = Color(0xFFE8E5FC)

val MoodJoyful = MoodHappy
val MoodReflective = MoodConfused
val MoodNeutral = Color(0xFFB0BEC5)

val ProdyGold = Color(0xFFFFB300)
val ProdyGoldLight = Color(0xFFFFE54C)
val ProdyGoldDark = Color(0xFFC68400)

val OnboardingAccentDark = Color(0xFF2ED56B)
val OnboardingAccentLight = Color(0xFF5DFA96)
val OnboardingButtonPrimaryDark = Color(0xFF0D2826)
val OnboardingQuoteCardLight = Color(0xFFFFFFFF)
val OnboardingQuoteCardDark = Color(0xFF1A3633)
val OnboardingQuoteHighlight = Color(0xFF36F97F)
val OnboardingXpArcGlow = Color(0x4D36F97F)
val OnboardingStatCardLight = Color(0xFFFFFFFF)
val OnboardingStatCardDark = Color(0xFF1A3633)
val OnboardingStatCardLockedLight = Color(0xFFF0F2F1)
val OnboardingStatCardLockedDark = Color(0xFF142E2B)
val OnboardingFeatureIconBgLight = Color(0xFFE6FFF0)
val OnboardingFeatureIconBgDark = Color(0xFF1A3331)
val OnboardingLeaderboardRowLight = Color(0xFFFFFFFF)
val OnboardingLeaderboardRowDark = Color(0xFF1A3633)
val OnboardingLeaderboardRowActiveLight = Color(0xFFE6FFF0)
val OnboardingLeaderboardRowActiveDark = Color(0xFF1A3331)

val JournalSaveButtonBgLight = Color(0xFFE6FFF0)
val JournalSaveButtonBgDark = Color(0xFF1A3331)
val JournalIconCircleBorderLight = Color(0xFFE9ECEF)
val JournalIconCircleBorderDark = Color(0xFF3A5250)
val JournalCardCornerDetailLight = Color(0xFFF0F2F4)
val JournalCardCornerDetailDark = Color(0xFF354845)
val JournalAccentGreen = Color(0xFF36F97F)

val JournalHistoryMoodEcstatic = Color(0xFF36F97F)
val JournalHistoryMoodCalm = Color(0xFF7ED321)
val JournalHistoryMoodAnxious = Color(0xFFF5A623)
val JournalHistoryMoodMelancholy = Color(0xFF4A90E2)

val JourneyBackgroundDark = Color(0xFF0D2826)
val JourneyBackgroundLight = Color(0xFFF0F4F3)
val JourneyPrimary = Color(0xFF36F97F)
val JourneyPrimaryVariant = Color(0xFF2ED56B)
val JourneyOnPrimary = Color(0xFF000000)
val JourneyAccent = Color(0xFF36F97F)
val JourneyAccentDark = Color(0xFF2ED56B)
val JourneyAccentLight = Color(0xFF5DFA96)
val JourneySurfaceLight = Color(0xFFFFFFFF)
val JourneySurfaceDark = Color(0xFF142E2B)
val JourneyCardLight = Color(0xFFFFFFFF)
val JourneyCardDark = Color(0xFF1A3633)
val JourneyTextPrimaryLight = Color(0xFF1A1A1A)
val JourneyTextPrimaryDark = Color(0xFFFFFFFF)
val JourneyTextSecondaryLight = Color(0xFF6C757D)
val JourneyTextSecondaryDark = Color(0xFFD3D8D7)
val JourneyTextTertiaryLight = Color(0xFFA0A8AD)
val JourneyTextTertiaryDark = Color(0xFF8A9493)
val JourneyButtonPrimary = Color(0xFF36F97F)
val JourneyButtonTextOnPrimary = Color(0xFF000000)
val JourneyButtonSecondary = Color(0xFF1A1A1A)
val JourneyArcBackground = Color(0xFFE0E7E6)
val JourneyArcBackgroundDark = Color(0xFF2A4240)
val JourneyArcFill = Color(0xFF36F97F)
val JourneyArcGlow = Color(0x4D36F97F)
val JourneyStatCardLight = Color(0xFFFFFFFF)
val JourneyStatCardDark = Color(0xFF1A3633)
val JourneyStatCardBorderLight = Color(0xFFE0E7E6)
val JourneyStatCardBorderDark = Color(0xFF3A5250)
val JourneyStatCardLockedLight = Color(0xFFF0F2F1)
val JourneyStatCardLockedDark = Color(0xFF142E2B)
val JourneyIconFire = Color(0xFFE65C2C)
val JourneyIconScroll = Color(0xFF36F97F)
val JourneyIconLocked = Color(0xFF8A9493)
val JourneyIconTrophy = Color(0xFFD4AF37)
val JourneyContractPaperLight = Color(0xFFFFFFFF)
val JourneyContractPaperDark = Color(0xFF1A3633)
val JourneyContractTextLight = Color(0xFF1A1A1A)
val JourneyContractTextDark = Color(0xFFFFFFFF)
val JourneyContractLineLight = Color(0xFFE0E7E6)
val JourneyContractLineDark = Color(0xFF3A5250)
val JourneySealRed = Color(0xFF8B2323)
val JourneySealGold = Color(0xFFD4AF37)
val JourneySealComplete = Color(0xFF36F97F)
val JourneyIndicatorActive = Color(0xFF36F97F)
val JourneyIndicatorInactiveLight = Color(0xFFE0E7E6)
val JourneyIndicatorInactiveDark = Color(0xFF3A5250)

val TimeCapsuleAccentLight = Color(0xFF36F97F)
val TimeCapsuleAccentDark = Color(0xFF36F97F)
val TimeCapsuleTitleTextLight = Color(0xFF1A1A1A)
val TimeCapsuleTitleTextDark = Color(0xFFFFFFFF)
val TimeCapsuleDiscardTextLight = Color(0xFF6C757D)
val TimeCapsuleDiscardTextDark = Color(0xFFD3D8D7)
val TimeCapsuleActiveTextLight = Color(0xFF1A1A1A)
val TimeCapsuleActiveTextDark = Color(0xFFFFFFFF)
val TimeCapsuleMultimediaIconLight = Color(0xFF6C757D)
val TimeCapsuleMultimediaIconDark = Color(0xFFFFFFFF)
val TimeCapsuleAttachTextLight = Color(0xFF6C757D)
val TimeCapsuleAttachTextDark = Color(0xFFD3D8D7)
val TimeCapsuleSectionTitleLight = Color(0xFF1A1A1A)
val TimeCapsuleSectionTitleDark = Color(0xFFFFFFFF)
val TimeCapsuleInactiveTagBgLight = Color(0xFFF0F3F2)
val TimeCapsuleInactiveTagBgDark = Color(0xFF2A4240)
val TimeCapsuleInactiveTagTextLight = Color(0xFF1A1A1A)
val TimeCapsuleInactiveTagTextDark = Color(0xFFD3D8D7)
val TimeCapsuleButtonTextLight = Color(0xFF000000)
val TimeCapsuleButtonTextDark = Color(0xFF000000)
val TimeCapsuleActiveTabTextLight = Color(0xFF000000)
val TimeCapsuleActiveTabTextDark = Color(0xFF000000)
val TimeCapsuleEmptyCircleBgLight = Color(0xFFF0F3F2)
val TimeCapsuleEmptyCircleBgDark = Color(0xFF1A3331)
val TimeCapsuleDashedCircleLight = Color(0xFFE0E7E6)
val TimeCapsuleDashedCircleDark = Color(0xFF3A5250)

val FutureGoal = FutureCategoryGoal
val FutureMotivation = FutureCategoryMotivation
val FuturePromise = FutureCategoryPromise
val FutureGeneral = FutureCategoryGeneral

val EmotionJoy = MoodHappy
val EmotionPeace = MoodCalm
val EmotionGratitude = MoodGrateful
val EmotionLove = Color(0xFFFF6B6B)
val EmotionHope = Color(0xFF69F0AE)
val EmotionCuriosity = Color(0xFFFFD54F)
val EmotionAnxiety = MoodAnxious
val EmotionSadness = MoodSad
val EmotionAnger = Color(0xFFEF5350)
val EmotionFear = Color(0xFF7E57C2)
val EmotionFrustration = Color(0xFFFFAB91)
val EmotionConfusion = MoodConfused
val EmotionNeutral = MoodNeutral
val EmotionReflective = MoodConfused
val EmotionFocused = Color(0xFF4FC3F7)
val EmotionDetermined = Color(0xFFFF7043)

// =============================================================================
// YEARLY WRAPPED COLORS - Vibrant, Celebratory Gradients
// =============================================================================

// Wrapped gradient colors
val WrappedPurple1 = Color(0xFF6B5CE7)
val WrappedPurple2 = Color(0xFF8B7EF0)
val WrappedPurple3 = Color(0xFFB39DDB)

val WrappedPink1 = Color(0xFFE91E63)
val WrappedPink2 = Color(0xFFF06292)
val WrappedPink3 = Color(0xFFFF80AB)

val WrappedOrange1 = Color(0xFFFF6F00)
val WrappedOrange2 = Color(0xFFFF8F00)
val WrappedOrange3 = Color(0xFFFFB74D)

val WrappedBlue1 = Color(0xFF1565C0)
val WrappedBlue2 = Color(0xFF42A5F5)
val WrappedBlue3 = Color(0xFF90CAF9)

val WrappedTeal1 = Color(0xFF00695C)
val WrappedTeal2 = Color(0xFF26A69A)
val WrappedTeal3 = Color(0xFF80CBC4)

val WrappedGreen1 = Color(0xFF2ED56B)
val WrappedGreen2 = Color(0xFF36F97F)
val WrappedGreen3 = Color(0xFF5DFA96)

val WrappedYellow1 = Color(0xFFF57F17)
val WrappedYellow2 = Color(0xFFFFEB3B)
val WrappedYellow3 = Color(0xFFFFF176)

val WrappedIndigo1 = Color(0xFF283593)
val WrappedIndigo2 = Color(0xFF5C6BC0)
val WrappedIndigo3 = Color(0xFF9FA8DA)

// Wrapped card background colors
val WrappedCardBackgroundLight = Color(0xFFFFFFFF)
val WrappedCardBackgroundDark = Color(0xFF1A3633)

// Wrapped text colors
val WrappedTextOnGradient = Color(0xFFFFFFFF)
val WrappedTextSecondaryOnGradient = Color(0xE6FFFFFF)

// Wrapped celebration colors
val WrappedConfettiRed = Color(0xFFE53935)
val WrappedConfettiOrange = Color(0xFFFF6F00)
val WrappedConfettiYellow = Color(0xFFFFEB3B)
val WrappedConfettiGreen = Color(0xFF36F97F)
val WrappedConfettiBlue = Color(0xFF42A5F5)
val WrappedConfettiPurple = Color(0xFF8B7EF0)
val WrappedConfettiPink = Color(0xFFE91E63)

object WrappedGradients {
    // Opening slide - Vibrant purple to pink
    val opening = listOf(WrappedPurple1, WrappedPurple2, WrappedPink1)

    // Stats slides - Bold and energetic
    val stats = listOf(WrappedGreen1, WrappedGreen2, WrappedGreen3)
    val writingStats = listOf(WrappedOrange1, WrappedOrange2, WrappedOrange3)
    val engagement = listOf(WrappedBlue1, WrappedBlue2, WrappedBlue3)
    val learning = listOf(WrappedPurple1, WrappedPurple2, WrappedPurple3)

    // Mood journey - Calming and reflective
    val moodJourney = listOf(WrappedTeal1, WrappedTeal2, WrappedTeal3)
    val moodHighlights = listOf(WrappedBlue1, WrappedBlue2, WrappedBlue3)

    // Themes and growth - Inspiring
    val themes = listOf(WrappedIndigo1, WrappedIndigo2, WrappedIndigo3)
    val growth = listOf(WrappedGreen1, WrappedGreen2, WrappedGreen3)
    val challenges = listOf(WrappedOrange1, WrappedOrange2, WrappedOrange3)

    // Key moments - Warm and personal
    val keyMoments = listOf(WrappedYellow1, WrappedYellow2, WrappedYellow3)
    val patterns = listOf(WrappedPink1, WrappedPink2, WrappedPink3)

    // Narratives - Deep and meaningful
    val narratives = listOf(WrappedPurple1, WrappedIndigo2, WrappedBlue2)

    // Looking ahead - Hopeful and bright
    val lookingAhead = listOf(WrappedGreen1, WrappedBlue2, WrappedPurple3)

    // Celebration - Full spectrum
    val celebration = listOf(
        WrappedPurple1, WrappedPink1, WrappedOrange1,
        WrappedYellow1, WrappedGreen1, WrappedBlue1, WrappedIndigo1
    )

    // Shareable cards - Each has unique gradient
    val shareCardTotalWords = listOf(WrappedGreen1, WrappedGreen2, WrappedGreen3)
    val shareCardStreak = listOf(WrappedOrange1, WrappedOrange2, WrappedOrange3)
    val shareCardMood = listOf(WrappedBlue1, WrappedBlue2, WrappedBlue3)
    val shareCardTheme = listOf(WrappedPurple1, WrappedPurple2, WrappedPurple3)
    val shareCardGrowth = listOf(WrappedTeal1, WrappedTeal2, WrappedTeal3)
    val shareCardActiveDays = listOf(WrappedYellow1, WrappedYellow2, WrappedYellow3)
}

// =============================================================================
// SKILL COLORS - Three Core Skills
// =============================================================================

/** Clarity skill - Associated with journaling and self-reflection */
val ClaritySkillColor = Color(0xFF6CB4D4)                   // Serene sky blue

/** Discipline skill - Associated with learning and vocabulary */
val DisciplineSkillColor = Color(0xFF9B6DD4)                // Creative purple

/** Courage skill - Associated with future self messages */
val CourageSkillColor = Color(0xFFFF7B5A)                   // Vibrant coral

// Skill color variants for different contexts
val ClaritySkillLight = Color(0xFF8CC8E0)
val ClaritySkillDark = Color(0xFF4A9CC0)
val DisciplineSkillLight = Color(0xFFB38AE0)
val DisciplineSkillDark = Color(0xFF7B50C0)
val CourageSkillLight = Color(0xFFFF9A7D)
val CourageSkillDark = Color(0xFFE65C37)

// =============================================================================
// HAVEN COLORS - Blush & Bone Aesthetic
// =============================================================================

// Primary Mode (Light) - Blush & Bone
val HavenBackgroundLight = Color(0xFFFAF9F6)          // Off-white/Bone
val HavenBubbleLight = Color(0xFFE2A9A9)              // Muted Blush
val HavenTextLight = Color(0xFF2D2424)                // Deep Espresso
val HavenUserBubbleLight = Color(0xFFE8EDEC)          // Soft Grey/White for user

// Dark Mode - The "Night Sanctuary"
val HavenBackgroundDark = Color(0xFF1A1616)           // Deep Plum/Charcoal base (not pure black)
val HavenBubbleDark = Color(0xFF5E3E3E)               // Deepened Rose
val HavenTextDark = Color(0xFFE8E4E4)                 // Soft Cream
val HavenUserBubbleDark = Color(0xFF2A2424)           // Darker charcoal for user

// Accents
val HavenAccentGold = Color(0xFFD4AF37)               // Soft Gold
val HavenAccentRoseGold = Color(0xFFB76E79)           // Rose Gold

// Breathing/Pulsing Colors
val HavenPulseStart = Color(0xFFE2A9A9)
val HavenPulseEnd = Color(0xFFF4C2C2)

// =============================================================================
// BLOOM SYSTEM COLORS - Vocabulary Growth
// =============================================================================

/** Seed state - Word learned but not yet ready to bloom */
val SeedDormant = Color(0xFF8D6E63)                         // Earth brown

/** Growing state - Word in transition */
val BloomGrowing = Color(0xFF81C784)                        // Fresh green

/** Ready state - Word ready to be used */
val BloomReady = Color(0xFF66BB6A)                          // Vibrant green

/** Flourishing state - Word fully mastered */
val BloomFlourishing = Color(0xFF4CAF50)                    // Deep green

// Bloom gradient colors
val BloomSeedLight = Color(0xFFA1887F)
val BloomGrowingLight = Color(0xFFA5D6A7)
val BloomReadyLight = Color(0xFF81C784)

// Note: WisdomPerspective color is defined above in "Wisdom category colors" section
