# Quiet Mode Implementation Guide

## Overview

Quiet Mode is a wellbeing feature that respects users' mental state when they need simplicity. When activated, it transforms Prody into a calm, focused space for genuine reflection by hiding gamification elements and using softer visual design.

## Philosophy

> "Make quiet mode feel like the app genuinely cares about the user's wellbeing. It should never feel like punishment for being stressed."

- **User First**: Mental wellbeing comes before engagement metrics
- **Never Forced**: Always a suggestion, never mandatory
- **Reversible**: Easy to enable, easy to disable
- **Caring**: Warm, non-judgmental messaging

## Core Components

### 1. Domain Layer (`domain/wellbeing/`)

#### QuietModeDetector.kt
Analyzes journal entries for stress patterns using:
- **Stress Keywords**: Detects words like "overwhelmed", "exhausted", "can't cope"
- **Mood Patterns**: Tracks negative moods (anxious, sad, confused)
- **Mood Trends**: Identifies declining mood intensity
- **Threshold Logic**: Requires multiple signals to avoid false positives

```kotlin
// Example usage
val detector = QuietModeDetector()
val result = detector.analyzeStressPatterns(recentEntries)

if (result.shouldSuggestQuietMode) {
    showQuietModeSuggestion()
}
```

#### QuietModeManager.kt
Manages Quiet Mode state and auto-suggestion:
- `enableQuietMode()`: Activates Quiet Mode
- `disableQuietMode()`: Deactivates Quiet Mode
- `shouldSuggestQuietMode()`: Determines if suggestion should be shown
- `shouldShowExitCheckIn()`: Checks if 7-day check-in is due

```kotlin
// Example usage
val manager = QuietModeManager(preferencesManager, journalRepository, detector)

// Check if we should suggest
val suggestion = manager.shouldSuggestQuietMode()
if (suggestion.shouldSuggest) {
    showSuggestionDialog()
    manager.recordSuggestionShown() // Prevent nagging
}

// Enable/disable
manager.enableQuietMode()
manager.disableQuietMode()
```

#### QuietModeFeatures.kt
Defines what's shown/hidden in Quiet Mode:

**Hidden:**
- Streaks, XP, levels, skills
- Leaderboard, achievements, badges
- Celebration animations
- Complex stats and analytics
- Profile decorations

**Kept:**
- Journal (all features)
- Mood tracking
- Daily wisdom (simplified)
- Future messages
- AI support (Buddha/Haven)

```kotlin
// Example usage
if (QuietModeFeatures.shouldShowFeature(Feature.ACHIEVEMENTS, isQuietMode)) {
    AchievementsSection()
}
```

### 2. UI Theme (`ui/theme/`)

#### QuietModeTheme.kt
Provides muted color palette:
- **Softer Contrasts**: Easier on the eyes
- **Muted Colors**: Desaturated but not depressing
- **Maintains Accessibility**: WCAG AA compliant
- **Respects Current Theme**: Works with light/dark mode

```kotlin
// Example usage in Composables
@Composable
fun SomeComponent(isQuietMode: Boolean) {
    val bgColor = if (isQuietMode) {
        QuietModeTheme.getBackground()
    } else {
        MaterialTheme.colorScheme.background
    }

    Surface(color = bgColor) {
        // ... content
    }
}
```

### 3. UI Components (`ui/components/quietmode/`)

#### QuietModeToggle.kt
Quick toggle for home screen:
- Shows confirmation dialog before enabling
- Explains what will change
- Easy one-tap disable
- Warm, inviting design

```kotlin
// Usage in HomeScreen
QuietModeToggle(
    isQuietModeActive = isQuietMode,
    onToggle = { quietModeManager.toggleQuietMode() }
)
```

#### QuietModeSuggestionDialog.kt
Auto-suggestion dialog:
- Warm, non-judgmental message
- Clear explanation of changes
- Easy accept/dismiss options
- Shows what's hidden vs kept

```kotlin
// Usage when suggestion triggers
if (shouldSuggest) {
    QuietModeSuggestionDialog(
        onAccept = {
            quietModeManager.enableQuietMode()
            quietModeManager.recordSuggestionShown()
        },
        onDismiss = {
            quietModeManager.recordSuggestionShown()
        }
    )
}
```

#### QuietModeIndicator.kt
Shows active state:
- Appears at top of home screen
- Subtle pulse animation
- Quick exit option
- Reassures that data is safe

```kotlin
// Usage in HomeScreen
if (isQuietMode) {
    QuietModeIndicator(
        onExit = { quietModeManager.disableQuietMode() }
    )
}
```

### 4. Preferences (`data/local/preferences/`)

#### PreferencesManager.kt
Added keys:
- `QUIET_MODE_ENABLED`: Boolean state
- `QUIET_MODE_AUTO_SUGGEST_THRESHOLD`: Customizable threshold
- `QUIET_MODE_LAST_SUGGESTED_AT`: Prevents nagging
- `QUIET_MODE_ENABLED_AT`: For check-in timing
- `QUIET_MODE_LAST_CHECK_IN_AT`: Tracks check-ins

## Integration Guide

### Step 1: Add to Dependency Injection

```kotlin
// In your DI module
@Provides
@Singleton
fun provideQuietModeDetector(): QuietModeDetector {
    return QuietModeDetector()
}

@Provides
@Singleton
fun provideQuietModeManager(
    preferencesManager: PreferencesManager,
    journalRepository: JournalRepository,
    detector: QuietModeDetector
): QuietModeManager {
    return QuietModeManager(preferencesManager, journalRepository, detector)
}
```

### Step 2: Update HomeScreen

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    quietModeManager: QuietModeManager
) {
    val isQuietMode by quietModeManager.isQuietModeEnabled().collectAsState(false)

    Column {
        // Show indicator when active
        if (isQuietMode) {
            QuietModeIndicator(
                onExit = { viewModel.disableQuietMode() }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Quiet Mode toggle card
        QuietModeToggle(
            isQuietModeActive = isQuietMode,
            onToggle = { viewModel.toggleQuietMode() }
        )

        // Conditionally show gamification
        if (!isQuietMode) {
            StreakCard()
            XPProgressBar()
            LeaderboardPreview()
        }

        // Always show core features
        JournalQuickAction()
        DailyWisdomCard()
    }
}
```

### Step 3: Add Auto-Suggestion Logic

```kotlin
// In HomeViewModel or app startup
viewModelScope.launch {
    val suggestion = quietModeManager.shouldSuggestQuietMode()

    if (suggestion.shouldSuggest) {
        _showQuietModeSuggestion.value = true
    }
}

// In Composable
if (showQuietModeSuggestion) {
    QuietModeSuggestionDialog(
        onAccept = {
            viewModel.enableQuietMode()
            viewModel.recordSuggestionShown()
        },
        onDismiss = {
            viewModel.recordSuggestionShown()
        }
    )
}
```

### Step 4: Update Other Screens

```kotlin
// ProfileScreen.kt
@Composable
fun ProfileScreen(quietModeManager: QuietModeManager) {
    val isQuietMode by quietModeManager.isQuietModeEnabled().collectAsState(false)

    Column {
        // Basic profile info (always shown)
        ProfileHeader()

        // Hide decorations in Quiet Mode
        if (!isQuietMode) {
            ProfileBanner()
            TrophyShelf()
            BadgesSection()
        }

        // Settings section with Quiet Mode toggle
        SettingsSection {
            CompactQuietModeToggle(
                isQuietModeActive = isQuietMode,
                onToggle = { viewModel.toggleQuietMode() }
            )
        }
    }
}

// StatsScreen.kt
@Composable
fun StatsScreen(quietModeManager: QuietModeManager) {
    val isQuietMode by quietModeManager.isQuietModeEnabled().collectAsState(false)

    if (isQuietMode) {
        // Simplified stats
        SimpleStatsView()
    } else {
        // Full analytics
        DetailedStatsView()
    }
}

// JournalScreen.kt
@Composable
fun JournalScreen(quietModeManager: QuietModeManager) {
    val isQuietMode by quietModeManager.isQuietModeEnabled().collectAsState(false)

    // Hide wisdom suggestions in Quiet Mode
    if (!isQuietMode) {
        WisdomSuggestions()
    }

    // Journal editor (always shown)
    JournalEditor()
}
```

### Step 5: Add Exit Check-In

```kotlin
// In HomeViewModel, check periodically
viewModelScope.launch {
    if (quietModeManager.shouldShowExitCheckIn()) {
        val duration = quietModeManager.getQuietModeDuration()
        _showExitCheckIn.value = duration
    }
}

// In Composable
showExitCheckIn?.let { days ->
    QuietModeExitCheckInDialog(
        daysInQuietMode = days,
        onKeepQuietMode = {
            viewModel.recordCheckInShown()
        },
        onExitQuietMode = {
            viewModel.disableQuietMode()
        },
        onDismiss = {
            viewModel.recordCheckInShown()
        }
    )
}
```

## JournalRepository Interface

The QuietModeManager needs this method in JournalRepository:

```kotlin
interface JournalRepository {
    // ... existing methods ...

    /**
     * Gets journal entries created after a specific timestamp.
     * Used for stress pattern analysis.
     */
    fun getEntriesAfterTimestamp(timestamp: Long): Flow<List<JournalEntryEntity>>
}

// Implementation example
override fun getEntriesAfterTimestamp(timestamp: Long): Flow<List<JournalEntryEntity>> {
    return journalDao.getEntriesAfterTimestamp(timestamp)
}

// DAO method
@Query("SELECT * FROM journal_entries WHERE createdAt >= :timestamp ORDER BY createdAt DESC")
fun getEntriesAfterTimestamp(timestamp: Long): Flow<List<JournalEntryEntity>>
```

## Testing Quiet Mode

### Manual Testing Checklist

1. **Enable Quiet Mode**
   - [ ] Toggle works from home screen
   - [ ] Confirmation dialog shows
   - [ ] Features hide correctly
   - [ ] Theme changes to muted colors
   - [ ] Indicator appears

2. **Auto-Suggestion**
   - [ ] Create 3+ journal entries with stress keywords
   - [ ] Suggestion appears after threshold
   - [ ] Suggestion doesn't show too frequently (3 day cooldown)

3. **Disable Quiet Mode**
   - [ ] Exit from indicator works
   - [ ] Exit from settings works
   - [ ] Features reappear
   - [ ] Theme returns to normal

4. **Check-In**
   - [ ] After 7 days, check-in dialog appears
   - [ ] Can choose to keep or exit
   - [ ] Check-in respects cooldown

## Accessibility

- All colors maintain WCAG AA contrast ratios
- Icons have proper content descriptions
- Buttons have clear labels
- Text is readable in both themes
- Focus indicators are visible

## Future Enhancements

Potential improvements for future versions:

1. **Customization**: Let users choose what to hide
2. **Schedules**: Auto-enable during certain hours
3. **Triggers**: Link to specific journal moods
4. **Analytics**: Track effectiveness of Quiet Mode
5. **Integration**: Coordinate with phone's Do Not Disturb

## Common Issues

### Issue: Suggestion shows too often
**Solution**: Check `QUIET_MODE_LAST_SUGGESTED_AT` is being set correctly. Default cooldown is 3 days.

### Issue: Features not hiding
**Solution**: Ensure components check `QuietModeFeatures.shouldShowFeature()` or use the `isQuietModeActive()` extension.

### Issue: Colors not changing
**Solution**: Make sure components use `QuietModeTheme` colors when `isQuietMode` is true.

### Issue: Repository method missing
**Solution**: Add `getEntriesAfterTimestamp()` to JournalRepository interface and implementation.

## Support

For questions or issues with Quiet Mode:
1. Check this documentation
2. Review the inline code comments
3. Test with the manual checklist
4. Ensure all dependencies are injected correctly

---

**Remember**: Quiet Mode is about caring for users' wellbeing. Keep the experience warm, gentle, and judgment-free.
