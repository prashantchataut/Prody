# Haven UI/UX Revamp Specification

## Overview

This document outlines the comprehensive design system and UI/UX specifications for the Haven therapeutic companion feature within the Prody application. Haven provides a safe, calming space for users to engage with therapeutic AI support, guided exercises, and mindfulness practices.

---

## Design Philosophy

### Core Principles

1. **Therapeutic Environment** - The UI should feel like a calm, private sanctuary
2. **Anti-Anxiety Design** - Avoid jarring animations, bright colors, or sudden changes
3. **Human-Centered** - Technology should feel warm, not robotic
4. **Accessible First** - All users, regardless of ability, should feel supported
5. **Privacy Forward** - Visual cues should reinforce that this is a safe, private space

### Emotional Goals

- **Safety**: Users should feel protected and secure
- **Warmth**: The interface should feel like a gentle presence, not a cold tool
- **Trust**: Build confidence through consistent, predictable interactions
- **Hope**: Even in difficult moments, the design should inspire gentle optimism

---

## Color Strategy

### Haven Palette

```kotlin
// Light Mode
val HavenBackgroundLight = Color(0xFFFAF8F5)  // Warm off-white, like parchment
val HavenBubbleLight = Color(0xFFE8D5C4)      // Soft rose/blush for Haven's messages
val HavenUserBubbleLight = Color(0xFFF0EAE2) // Neutral warm for user messages
val HavenTextLight = Color(0xFF3D3330)        // Warm dark brown for readability
val HavenAccentRose = Color(0xFFD4A5A5)       // Muted rose for accents
val HavenAccentGold = Color(0xFFD4AF37)       // Soft gold for memory/recall highlights

// Dark Mode
val HavenBackgroundDark = Color(0xFF1A1614)   // Deep warm charcoal
val HavenBubbleDark = Color(0xFF3D2F2F)       // Deep rose for Haven's messages
val HavenUserBubbleDark = Color(0xFF2A2422)   // Warm dark for user messages
val HavenTextDark = Color(0xFFF0EAE2)         // Warm light for readability
```

### Color Usage Guidelines

| Element | Light Mode | Dark Mode | Purpose |
|---------|------------|-----------|---------|
| Background | `HavenBackgroundLight` | `HavenBackgroundDark` | Creates warmth |
| Haven Messages | `HavenBubbleLight` | `HavenBubbleDark` | Distinguishes AI |
| User Messages | `HavenUserBubbleLight` | `HavenUserBubbleDark` | User identification |
| Primary Text | `HavenTextLight` | `HavenTextDark` | Maximum readability |
| Accent (Memory) | `HavenAccentGold` | `HavenAccentGold` | Recall/memory features |
| Crisis Banner | Soft pink, not red | Soft pink, not red | Non-alarming alerts |

### Color Psychology

- **Rose/Blush tones**: Convey warmth, compassion, and emotional safety
- **Gold accents**: Signify value, memory, and importance
- **Warm neutrals**: Reduce visual stress, feel organic and human
- **Avoid pure white/black**: Too stark, can increase anxiety

---

## Typography System

### Font Family

```kotlin
val PoppinsFamily = FontFamily(/* Poppins font weights */)

// Haven-Specific Styles
val HavenMessageStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 24.sp,
    letterSpacing = 0.15.sp
)

val HavenInputStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 22.sp
)
```

### Typography Scale

| Usage | Size | Weight | Line Height |
|-------|------|--------|-------------|
| Haven Messages | 16sp | Regular | 24sp |
| User Input | 16sp | Regular | 22sp |
| Section Headers | 20sp | SemiBold | 28sp |
| Screen Title | 24sp | Bold | 32sp |
| Timestamps | 12sp | Regular | 16sp |
| Labels/Badges | 12sp | Medium | 16sp |

### Reading Comfort

- Minimum line height of 1.5x font size for body text
- Maximum line length of 65 characters for optimal readability
- Left-aligned text (not justified) for easier reading
- Adequate contrast ratios (WCAG AA minimum)

---

## Spacing System

### 16dp Baseline Grid

All spacing should be multiples of 4dp, with 16dp as the primary unit:

```kotlin
// Spacing Tokens
val SpacingXS = 4.dp   // Tight spacing (inline elements)
val SpacingS = 8.dp    // Small spacing (related items)
val SpacingM = 16.dp   // Medium spacing (section padding)
val SpacingL = 24.dp   // Large spacing (between sections)
val SpacingXL = 32.dp  // Extra large (screen padding)
val SpacingXXL = 48.dp // Maximum (major section breaks)
```

### Screen Padding

- **Horizontal padding**: 16dp (standard), 24dp (tablets)
- **Top padding**: Safe area + 16dp
- **Bottom padding**: 16dp + navigation safe area
- **Content padding in cards**: 16dp (compact), 20dp (comfortable)

### Message Spacing

- Between messages: 16dp (allows breathing room)
- Within message bubble: 16dp padding
- Timestamp offset: 8dp below message

---

## Touch Target Guidelines

### Minimum Sizes

- **Interactive elements**: 48dp x 48dp minimum
- **Buttons**: 48dp height minimum
- **Icon buttons**: 48dp x 48dp touch area (icon can be smaller)
- **List items**: 56dp minimum height

### Accessibility

- No touch targets closer than 8dp apart
- Clear focus indicators for keyboard navigation
- Adequate color contrast (4.5:1 for normal text, 3:1 for large text)

---

## Component Specifications

### Message Bubbles

```kotlin
// Haven's Messages (AI)
Card(
    shape = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = 4.dp,  // Small corner indicates speaker
        bottomEnd = 20.dp
    ),
    colors = CardDefaults.cardColors(containerColor = HavenBubbleLight),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat design
)

// User's Messages
Card(
    shape = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = 20.dp,
        bottomEnd = 4.dp  // Small corner indicates speaker
    ),
    colors = CardDefaults.cardColors(containerColor = HavenUserBubbleLight),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
)
```

### Input Bar

- Rounded corners: 24dp
- Minimum height: 48dp
- Padding: 8dp internal
- Background: Warm neutral matching user bubbles
- Send button: Icon only, 48dp touch target, color accent when active

### Typing Indicator

**Anti-AI Loading Pattern**: Instead of bouncing dots (which feel robotic), use a gentle "breathing" animation:

```kotlin
// Slow, calming color swell
val infiniteTransition = rememberInfiniteTransition()
val alpha by infiniteTransition.animateFloat(
    initialValue = 0.1f,
    targetValue = 0.4f,
    animationSpec = infiniteRepeatable(
        animation = tween(3000, easing = LinearOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
    )
)
```

Visual: A soft, glowing orb that slowly pulses with text "Haven is listening..."

### Session Type Cards

- Size: Fill available width, two columns
- Height: Dynamic based on content
- Corner radius: 12dp
- Icon container: 48dp circle with 15% opacity background
- Touch feedback: Subtle ripple effect

### Exercise Chips

- Height: Dynamic (min 48dp)
- Padding: 12dp
- Corner radius: 12dp
- Icon: 24dp with accent color
- Secondary text: Duration in minutes

### Crisis Resources Banner

**Important**: Use warm, supportive colors instead of alarming red:

```kotlin
val containerColor = Color(0xFFFCE4EC) // Very light pink
val iconColor = Color(0xFFC2185B)      // Rose red (not fire engine red)
```

- Icon: Heart, not warning triangle
- Language: "You are not alone" not "CRISIS ALERT"
- Expandable to show resources

---

## Animation Guidelines

### Principles

1. **Slow and Soothing**: Animations should be 300-500ms minimum
2. **Ease Out**: Use `LinearOutSlowInEasing` for entering elements
3. **No Sudden Movements**: Everything should feel gradual
4. **Purpose-Driven**: Only animate when it serves user understanding

### Specific Animations

| Animation | Duration | Easing | Purpose |
|-----------|----------|--------|---------|
| Screen transitions | 400ms | EaseOut | Smooth navigation |
| Message appear | 300ms | EaseOut | New content |
| Typing indicator | 3000ms cycle | Ease | Calming presence |
| Crisis banner expand | 300ms | EaseOut | Information reveal |
| Button feedback | 150ms | Linear | Instant response |

### Avoid

- Bouncing animations
- Quick, jerky movements
- Flashing or pulsing effects (except slow breathing)
- Parallax scrolling
- Auto-playing video or distracting motion

---

## Accessibility Features

### Screen Reader Support

- All interactive elements have content descriptions
- Message sender (Haven/User) is announced
- Timestamps are readable
- Crisis resources are marked as important

### Visual Accessibility

- Support for system font scaling
- High contrast mode compatibility
- Reduced motion setting respected
- Color not used as sole information carrier

### Cognitive Accessibility

- Clear, simple language
- Consistent navigation patterns
- Predictable behavior
- Undo options where possible

---

## Offline Mode Design

### Visual Indicators

When Haven is offline, the UI should:

1. **Display a Banner** at the top of the screen
   - Background: Warm tertiary container color
   - Icon: Cloud/WiFi off icon
   - Message: "AI Chat Offline - Exercises & Journal available"
   - Action: "Retry" button

2. **Disable Chat Input** gracefully
   - Show placeholder text explaining offline status
   - Exercises and journal remain functional

3. **Provide Diagnostics** (expandable)
   - Configuration status
   - Troubleshooting steps
   - Link to settings if needed

### Error States

- Use warm colors, not alarming red
- Provide clear next steps
- Never blame the user
- Offer alternatives (exercises, journal)

---

## Responsiveness Guidelines

### Breakpoints

```kotlin
// Compact (phones portrait)
val CompactWidth = 0.dp..599.dp

// Medium (phones landscape, small tablets)
val MediumWidth = 600.dp..839.dp

// Expanded (tablets, desktop)
val ExpandedWidth = 840.dp..Float.MAX_VALUE.dp
```

### Adaptive Layouts

| Screen Size | Session Grid | Message Width | Sidebar |
|-------------|--------------|---------------|---------|
| Compact | 2 columns | 80% max | None |
| Medium | 3 columns | 70% max | None |
| Expanded | 4 columns | 60% max | Stats sidebar |

### Orientation

- Support both portrait and landscape
- Chat remains functional in landscape
- Exercise screens adapt to available space

---

## Security Visual Cues

### Screenshot Prevention

When in Haven chat:
```kotlin
DisposableEffect(Unit) {
    val window = (context as? Activity)?.window
    window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    onDispose {
        window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}
```

### Privacy Indicators

- Lock icon in header (optional)
- "Your conversations are encrypted" footer
- No external sharing options during sensitive sessions

---

## Implementation Checklist

### Haven Home Screen
- [ ] Session type grid (2 columns)
- [ ] Quick exercises horizontal scroll
- [ ] Recent sessions list
- [ ] Offline mode banner with retry
- [ ] Crisis resources (expandable)
- [ ] Statistics card (if sessions exist)

### Haven Chat Screen
- [ ] Message list with proper spacing
- [ ] Typing indicator (breathing animation)
- [ ] Input bar with voice support
- [ ] Exercise suggestion cards
- [ ] Crisis resources (when triggered)
- [ ] Session completion flow
- [ ] Mood rating dialog

### Haven Exercise Screen
- [ ] Exercise step display
- [ ] Timer/progress indicator
- [ ] Breathing visualization (for breathing exercises)
- [ ] Completion celebration
- [ ] Notes input option

---

## Quality Assurance Criteria

1. **Visual Consistency**: All Haven screens use Haven color palette
2. **Touch Targets**: All interactive elements meet 48dp minimum
3. **Spacing**: All spacing follows 4dp grid
4. **Accessibility**: Screen reader announces all content correctly
5. **Offline Graceful**: App functions (limited) without API connection
6. **No Jarring Elements**: Animations are slow and soothing
7. **Crisis Safety**: Resources always accessible, non-alarming design
8. **Privacy**: Screenshot protection active in chat
9. **Responsiveness**: Works on phones, tablets, both orientations
10. **Error Handling**: All errors have clear, helpful messages

---

## Appendix: Component Library Reference

### Colors
- `HavenBackgroundLight/Dark`
- `HavenBubbleLight/Dark`
- `HavenUserBubbleLight/Dark`
- `HavenTextLight/Dark`
- `HavenAccentRose`
- `HavenAccentGold`

### Text Styles
- `HavenMessageStyle`
- `HavenInputStyle`

### Components
- `OfflineModeBanner`
- `MessageBubble`
- `TypingIndicator`
- `ChatInputBar`
- `SessionTypeCard`
- `ExerciseChip`
- `CrisisResourcesBanner`
- `SessionSummaryCard`
- `MoodSelectionDialog`

---

*This specification is a living document and should be updated as Haven evolves.*
