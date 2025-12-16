# Gamification Visual Layer Documentation

## Executive Summary

The Prody gamification system provides a comprehensive reward and progression framework with premium visual presentation. This document details where and how gamification elements appear throughout the application.

---

## Gamification Components

### 1. Points System

**Configuration:** `/domain/gamification/GamificationService.kt`

| Activity | Points | Daily Cap |
|----------|--------|-----------|
| Journal Entry | 50 | - |
| Word Learned | 15 | - |
| Quote Read | 5 | - |
| Proverb Explored | 8 | - |
| Future Letter Sent | 50 | - |
| Future Letter Received | 30 | - |
| Daily Check-in | 5 | - |
| Streak Bonus | 2/day | - |
| Review Completed | 15 | - |
| Buddha Conversation | 15 | - |
| **Maximum Daily** | - | 500 |

### Visual Display Locations

**Profile Header:**
- Large animated counter
- Gold star icon
- Glow effect on high values

**Stats Header:**
- Animated progress ring
- Points with formatting (1.5K, 2.3M)

**Leaderboard:**
- Points column with star icon
- Formatted numbers

---

### 2. Level System

**Calculation:** Points-based thresholds

| Level | Points Required | Title |
|-------|-----------------|-------|
| 1 | 0 | Newcomer |
| 2 | 100 | Seeker |
| 3 | 300 | Explorer |
| 4 | 600 | Apprentice |
| 5 | 1,000 | Journeyer |
| 6 | 2,000 | Practitioner |
| 7 | 4,000 | Adept |
| 8 | 7,000 | Sage |
| 9 | 12,000 | Master |
| 10 | 20,000 | Legend |

### Visual Display Locations

**Profile Screen:**
- Level badge on avatar (bottom-right)
- Level progress bar with gradient
- "Lvl X" chip
- XP to next level text

**Profile Header:**
- Level number in gold badge

**Leaderboard:**
- Implicit via points display

---

### 3. Streak System

**Tracking:** Consecutive days of activity

### Visual Display Locations

**Profile Header:**
- Fire icon with animation
- Current streak number
- "Best: X" subtitle if applicable
- Gradient background (orange to gold)
- Pulsing glow effect when active

**Stats Header:**
- Same animated display
- Longest streak comparison

**Leaderboard Items:**
- Fire icon with "Xd" label
- Orange accent color

**Home Screen:**
- Streak indicator in quick stats

### Animation Details
```kotlin
// Fire scale animation
fireScale: 1f -> 1.15f (500ms, EaseInOutCubic, reverse repeat)

// Glow alpha animation
glowAlpha: 0.4f -> 0.8f (800ms, EaseInOutCubic, reverse repeat)
```

---

### 4. Achievement System

**Categories:**
- Journal (entry milestones)
- Words (learning milestones)
- Quotes (reading milestones)
- Future Letters (sending/receiving)
- Buddha Conversations
- Streaks (day milestones)
- Time-based (Early Bird, Night Owl)

### Rarity Tiers

| Rarity | Color | Glow Color |
|--------|-------|------------|
| Common | Gray | Gray (0.3 alpha) |
| Uncommon | Green | Green glow |
| Rare | Blue | Blue glow |
| Epic | Purple | Purple glow |
| Legendary | Orange | Orange glow |
| Mythic | Gold gradient | Gold glow |

### Visual Display Locations

**Profile Screen:**
- Achievement section with filter chips
- "Unlocked" horizontal scroll (with count)
- "Locked" horizontal scroll (with count)
- Category filter (All, Journal, Words, etc.)

**Achievement Cards:**
```kotlin
EnhancedAchievementCard(
    achievement: AchievementEntity,
    isUnlocked: Boolean
)
```

**Card Features:**
- Rarity-colored border
- Icon with glow effect
- Title and description
- Progress indicator (if locked)
- Unlock date (if unlocked)
- Animated entry from right

---

### 5. Banner System

**Configuration:** `/domain/identity/ProdyBanners.kt`

**Banner Types:**
- Default banners (starter)
- Achievement banners (unlocked via milestones)
- Special event banners
- Debug/Beta banners (dev only)

### Visual Display Locations

**Profile Header:**
- Full-width banner background
- Animated pattern overlay
- Gradient overlay for text readability

**Leaderboard Items:**
```kotlin
CompactBannerStrip(
    bannerId: String,
    modifier: Modifier
)
```
- Small banner icon next to username
- Subtle visual differentiation

### Banner Rendering
```kotlin
BannerRenderer(
    banner: ProdyBanner,
    modifier: Modifier,
    showAnimation: Boolean,
    cornerRadius: Dp
)
```

---

### 6. Title System

**Titles by Level:**
- Level 1: Newcomer
- Level 2: Seeker
- Level 3: Explorer
- Level 4: Apprentice
- Level 5: Journeyer
- Level 6: Practitioner
- Level 7: Adept
- Level 8: Sage
- Level 9: Master
- Level 10: Legend

### Visual Display Locations

**Profile Header:**
- Below display name
- Gold premium icon
- Gold text color
- Medium font weight

---

### 7. Leaderboard & Social

**Entity:** `LeaderboardEntryEntity`

**Data Displayed:**
- Rank (position number)
- Display name
- Banner (compact strip)
- Total points
- Current streak
- Boosts received
- Respects received

### Podium Visualization (Top 3)

**Gold (1st Place):**
- Center position
- Tallest podium (130dp)
- Gold gradient background
- Animated glow
- Shine sweep animation
- Trophy icon

**Silver (2nd Place):**
- Left position
- Medium podium (100dp)
- Silver gradient
- Subtle glow
- Medal icon

**Bronze (3rd Place):**
- Right position
- Shortest podium (80dp)
- Bronze gradient
- Subtle glow
- Military tech icon

### Support System

**Actions:**
- Boost (rocket icon) - "Power up their journey"
- Respect (thumbs up icon) - "Acknowledge dedication"

**Visual Elements:**
- Bottom sheet presentation
- Count badges on icons
- Rate limit indication
- Disabled state when limit reached

---

### 8. Progress Indicators

**Types Used:**
- Circular progress ring (avatar, XP)
- Linear progress bar (level)
- Donut chart (mood distribution)
- Bar chart (weekly activity)

### Animation Details
```kotlin
// Progress animation
animatedProgress: tween(1500, FastOutSlowInEasing)

// Counter animation
animatedValue: tween(1500, FastOutSlowInEasing)
```

---

## Animation Summary

### Glow Effects
- Avatar glow (gold, pulsing)
- Streak fire glow (orange, pulsing)
- Achievement glow (rarity-colored)
- Rank badge glow (tier-colored)

### Scale Effects
- Fire icon (1f -> 1.15f)
- Press feedback (1f -> 0.98f)
- Podium avatar (1f -> 1.05f for 1st place)

### Rotation Effects
- Header background particles (40s full rotation)
- Empty state icon (wobble)

### Entrance Effects
- Fade in + slide from direction
- Staggered delays (50-100ms)
- Spring damping for bounce feel

---

## Accessibility Considerations

### Touch Targets
All interactive gamification elements maintain 48dp minimum.

### Color Contrast
Rarity colors chosen for sufficient contrast on both light and dark themes.

### Motion Reduction
Animations use standard durations; consider adding `preferredReducedMotion` support.

### Screen Reader
Achievement progress and stats include proper content descriptions.

---

## Conclusion

The gamification visual layer is comprehensive and premium-feeling with:
- Consistent animation language
- Proper color-coding for rarity/tier
- Multiple display locations for key stats
- Non-intrusive social features
- Celebratory moments for milestones

---

*Document Version: 1.0*
*Last Updated: December 2024*
