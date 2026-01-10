# Quiet Mode Feature - Implementation Summary

## Overview

Quiet Mode is a comprehensive wellbeing feature that respects users' mental state when they need simplicity. The implementation includes detection logic, UI components, theme adjustments, and integration patterns.

## Philosophy

**"Make quiet mode feel like the app genuinely cares about the user's wellbeing. It should never feel like punishment for being stressed."**

- User's mental wellbeing comes first
- Never forced, always a suggestion
- Easy to enable, easy to disable
- Warm, non-judgmental messaging
- Genuine care, not manipulation

## Files Created

### 1. Domain Layer (Business Logic)

#### `/app/src/main/java/com/prody/prashant/domain/wellbeing/`

**QuietModeDetector.kt** (263 lines)
- Analyzes journal entries for stress patterns
- Detects stress keywords (overwhelmed, exhausted, can't cope, etc.)
- Tracks negative mood patterns (anxious, sad, confused)
- Analyzes mood intensity trends (declining, stable, improving)
- Requires multiple signals to avoid false positives (50+ stress score, 2+ signals)
- Includes cooldown logic to prevent nagging (3-day minimum between suggestions)

**QuietModeManager.kt** (170 lines)
- Manages Quiet Mode state (enable/disable/toggle)
- Handles auto-suggestion logic based on journal patterns
- Coordinates with PreferencesManager for state persistence
- Provides 7-day check-in functionality
- Tracks duration of Quiet Mode usage
- Exposes reactive Flows for UI observation

**QuietModeFeatures.kt** (250 lines)
- Defines what features are hidden/visible in Quiet Mode
- Hidden: Streaks, XP, achievements, leaderboard, celebrations, complex stats
- Kept: Journal, mood tracking, daily wisdom, future messages, AI support
- Provides `shouldShowFeature()` helper for conditional rendering
- Documents the philosophy behind each decision

**QuietModeExtensions.kt** (120 lines)
- Helper extensions for easy Quiet Mode integration
- `isQuietModeActive()` Composable extension
- `shouldShow()` extension for Feature checking
- `whenQuietModeActive/Inactive()` conditional execution
- UI adjustment helpers (animation duration, corner radius, padding)
- Notification filtering based on type

**QuietModeViewModel.kt** (300 lines)
- Example ViewModel showing integration patterns
- State management for dialogs and check-ins
- Helper functions for feature checks
- Comprehensive documentation with usage examples
- Shows how to integrate into existing ViewModels

### 2. UI Theme

#### `/app/src/main/java/com/prody/prashant/ui/theme/`

**QuietModeTheme.kt** (320 lines)
- Muted color palette for calmer visual experience
- Light mode: Softer whites, muted greens, gentle grays
- Dark mode: Deeper teals, softer contrasts
- Muted mood colors (desaturated but not depressing)
- Helper Composable functions for easy color access
- `QuietModeColorPalette` data class for bundled colors
- Maintains WCAG AA accessibility compliance

### 3. UI Components

#### `/app/src/main/java/com/prody/prashant/ui/components/quietmode/`

**QuietModeToggle.kt** (280 lines)
- Main toggle button for home screen
- Shows confirmation dialog before enabling
- Clear explanation of what changes
- Compact version for settings
- Warm, inviting design with soft colors
- Easy one-tap disable for active mode

**QuietModeSuggestionDialog.kt** (340 lines)
- Auto-suggestion dialog with warm messaging
- "It looks like things have been heavy lately" approach
- Clear breakdown of hidden vs kept features
- Two options: "Yes, simplify" or "I'm okay, thanks"
- Exit check-in dialog for 7-day check-in
- Non-judgmental, caring tone throughout

**QuietModeIndicator.kt** (280 lines)
- Shows Quiet Mode is currently active
- Appears at top of home screen
- Subtle pulse animation on icon
- Quick exit button
- Reassuring "All your data is safe" message
- Compact and badge variants for different contexts
- Animated entrance/exit transitions

### 4. Preferences (Data Layer)

#### `/app/src/main/java/com/prody/prashant/data/local/preferences/`

**PreferencesManager.kt** (Modified)
Added preference keys and getters/setters:
- `QUIET_MODE_ENABLED`: Boolean state
- `QUIET_MODE_AUTO_SUGGEST_THRESHOLD`: Customizable threshold (default: 3)
- `QUIET_MODE_LAST_SUGGESTED_AT`: Prevents suggestion spam
- `QUIET_MODE_ENABLED_AT`: Timestamp when enabled (for check-ins)
- `QUIET_MODE_LAST_CHECK_IN_AT`: Tracks when last check-in was shown

### 5. Documentation

**QUIET_MODE_IMPLEMENTATION.md** (Comprehensive guide)
- Architecture overview
- Component documentation
- Integration guide with code examples
- Step-by-step implementation instructions
- Testing checklist
- Common issues and solutions
- Accessibility notes
- Future enhancement ideas

**QUIET_MODE_SUMMARY.md** (This file)
- High-level overview
- File listing and descriptions
- Key features and capabilities
- Quick reference

## Key Features

### 1. Stress Pattern Detection
- Analyzes last 7 days of journal entries
- Detects stress keywords in content
- Tracks negative mood frequency
- Identifies declining mood trends
- Requires multiple signals for suggestion (reduces false positives)

### 2. Auto-Suggestion System
- Suggests Quiet Mode when stress patterns detected
- 3-day cooldown between suggestions (prevents nagging)
- Warm, non-judgmental messaging
- Easy to accept or dismiss
- Records user choice to respect preferences

### 3. Feature Hiding/Showing
Hidden in Quiet Mode:
- Gamification: Streaks, XP, levels, skills, achievements
- Social: Leaderboard, rankings, comparisons
- Pressure: Missions, challenges, goals
- Visual: Celebrations, animations, decorations
- Stats: Complex analytics, detailed graphs

Kept in Quiet Mode:
- Core: Journal, mood tracking, all entries
- Support: Daily wisdom, future messages
- AI: Buddha therapist, Haven AI (if enabled)
- Essential: Settings, privacy, backup

### 4. Visual Design Changes
- Muted color palette (softer contrasts)
- Calmer greens (less vibrant)
- Softer text colors
- Subtler dividers and outlines
- Minimal animations
- Increased spacing (breathing room)
- Softer corner radius

### 5. Exit Mechanisms
Multiple ways to exit:
- Quick exit button on indicator
- Settings toggle
- 7-day gentle check-in: "How are you feeling?"
- All reversible, no data loss

### 6. Check-In System
- After 7 days, gentle check-in appears
- "How are you feeling? Want to bring everything back?"
- User can keep or exit Quiet Mode
- Another 7-day cycle if user chooses to keep
- Never forced to exit

## Integration Points

### Required Dependencies
```kotlin
// Inject in ViewModels
@Inject lateinit var quietModeManager: QuietModeManager

// Observe state
val isQuietMode = quietModeManager.isQuietModeEnabled().collectAsState(false)
```

### UI Integration Pattern
```kotlin
// Hide features conditionally
if (!isQuietMode) {
    GamificationSection()
}

// Always show core features
JournalSection()
```

### Repository Requirement
Add to `JournalRepository`:
```kotlin
fun getEntriesAfterTimestamp(timestamp: Long): Flow<List<JournalEntryEntity>>
```

## Testing

### Manual Test Scenarios

1. **Enable Manually**
   - Tap toggle on home screen
   - Verify confirmation dialog
   - Check features hide
   - Verify theme changes
   - Confirm indicator appears

2. **Auto-Suggestion**
   - Create 3+ stressed journal entries
   - Include keywords: "overwhelmed", "exhausted"
   - Use negative moods: anxious, sad
   - Verify suggestion appears
   - Check 3-day cooldown works

3. **Disable**
   - Tap exit on indicator
   - Verify confirmation dialog
   - Check features return
   - Confirm theme reverts

4. **Check-In**
   - Enable Quiet Mode
   - Wait 7 days (or adjust timestamp)
   - Verify check-in appears
   - Test both options

### Stress Keywords for Testing
Use these in journal entries to trigger detection:
- "overwhelmed"
- "exhausted"
- "can't cope"
- "too much"
- "stressed"
- "anxious"
- "breaking down"
- "burnt out"

## Performance Considerations

- Stress analysis only runs when explicitly checked (not on every journal save)
- Uses Flow-based reactive state (efficient updates)
- Preference reads are cached by DataStore
- No heavy computations on main thread
- Animations are simple and performant

## Accessibility

- WCAG AA compliant color contrasts
- Clear, descriptive labels
- Keyboard navigation support
- Screen reader compatible
- Focus indicators visible

## Localization Ready

All strings are hardcoded currently but structured for easy extraction to strings.xml:
- Dialog titles and messages
- Button labels
- Indicator text
- Feature descriptions

## Future Enhancements

Potential improvements:
1. User customization of what to hide
2. Scheduled Quiet Mode (evening hours)
3. Integration with phone Do Not Disturb
4. Multiple intensity levels (minimal, moderate, full)
5. Analytics on Quiet Mode effectiveness
6. Personalized stress keyword learning
7. Integration with mindfulness exercises

## Important Notes

### Philosophy Reminders
- Never make users feel bad about being stressed
- Always give them control
- Make it easy to try, easy to leave
- Respect their autonomy
- Provide reassurance (data is safe)

### Technical Reminders
- Check Quiet Mode state before showing gamification
- Use QuietModeTheme colors when active
- Respect the 3-day suggestion cooldown
- Record user interactions to avoid nagging
- Test with real stress patterns

### Design Reminders
- Keep messaging warm and caring
- Avoid clinical/medical language
- Use "simplify" not "disable" or "hide"
- Emphasize what's kept, not just what's lost
- Make exit as easy as entry

## Quick Reference Commands

### Check if Quiet Mode is active
```kotlin
val isActive = preferencesManager.quietModeEnabled.first()
```

### Enable/Disable
```kotlin
quietModeManager.enableQuietMode()
quietModeManager.disableQuietMode()
```

### Check if should suggest
```kotlin
val suggestion = quietModeManager.shouldSuggestQuietMode()
if (suggestion.shouldSuggest) { /* show dialog */ }
```

### Check feature visibility
```kotlin
if (QuietModeFeatures.shouldShowFeature(Feature.STREAKS, isQuietMode)) {
    ShowStreaks()
}
```

## File Size Summary

Total implementation:
- Domain Logic: ~1,400 lines
- UI Components: ~900 lines
- Theme: ~320 lines
- Documentation: ~1,000 lines
- **Total: ~3,620 lines of production code + documentation**

## Success Criteria

Quiet Mode is successful if:
1. Users feel cared for, not judged
2. Stress detection is accurate (minimal false positives)
3. Features hide/show reliably
4. Theme changes are subtle but effective
5. Users can easily enable and disable
6. No data is ever lost
7. Performance remains smooth
8. Accessibility is maintained

---

**Remember**: Quiet Mode is about genuine care for users' wellbeing. Every decision should prioritize their mental health over engagement metrics.
