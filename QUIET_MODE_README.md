# Quiet Mode Feature - Complete Implementation

## What is Quiet Mode?

Quiet Mode is a wellbeing-first feature that respects users' mental state when they need simplicity. It transforms Prody into a calm, focused space by hiding gamification elements and using softer visual design.

**Philosophy**: "Make quiet mode feel like the app genuinely cares about the user's wellbeing. It should never feel like punishment for being stressed."

## Quick Start

### For Developers

1. **Read the Documentation**
   - Start with `QUIET_MODE_SUMMARY.md` for overview
   - Read `QUIET_MODE_IMPLEMENTATION.md` for technical details
   - Review `QUIET_MODE_FLOWS.md` for user experience
   - Use `QUIET_MODE_CHECKLIST.md` for integration

2. **Key Files Created**
   ```
   Domain Layer:
   ├── domain/wellbeing/QuietModeDetector.kt (stress pattern detection)
   ├── domain/wellbeing/QuietModeManager.kt (state management)
   ├── domain/wellbeing/QuietModeFeatures.kt (feature visibility rules)
   ├── domain/wellbeing/QuietModeExtensions.kt (helper utilities)
   └── domain/wellbeing/QuietModeViewModel.kt (example ViewModel)

   UI Theme:
   └── ui/theme/QuietModeTheme.kt (muted color palette)

   UI Components:
   ├── ui/components/quietmode/QuietModeToggle.kt (home screen toggle)
   ├── ui/components/quietmode/QuietModeSuggestionDialog.kt (auto-suggest)
   └── ui/components/quietmode/QuietModeIndicator.kt (active state indicator)

   Data Layer:
   └── PreferencesManager.kt (modified - added Quiet Mode preferences)

   Documentation:
   ├── QUIET_MODE_SUMMARY.md (high-level overview)
   ├── QUIET_MODE_IMPLEMENTATION.md (technical guide)
   ├── QUIET_MODE_FLOWS.md (user flows and UI states)
   ├── QUIET_MODE_CHECKLIST.md (integration checklist)
   └── QUIET_MODE_README.md (this file)
   ```

3. **Integration Steps** (Quick Version)
   ```kotlin
   // 1. Add to DI
   @Provides
   @Singleton
   fun provideQuietModeManager(...): QuietModeManager

   // 2. In ViewModel
   @Inject lateinit var quietModeManager: QuietModeManager
   val isQuietMode = quietModeManager.isQuietModeEnabled()
       .stateIn(viewModelScope, SharingStarted.Lazily, false)

   // 3. In Composable
   val isQuietMode by viewModel.isQuietMode.collectAsState()

   if (!isQuietMode) {
       GamificationSection() // Hidden in Quiet Mode
   }

   JournalSection() // Always visible
   ```

4. **Repository Requirement**
   Add this method to `JournalRepository`:
   ```kotlin
   fun getEntriesAfterTimestamp(timestamp: Long): Flow<List<JournalEntryEntity>>
   ```

## What Changes in Quiet Mode?

### Hidden (Temporarily)
- Streaks, XP, levels, skills
- Leaderboard, rankings, comparisons
- Achievements, badges, titles
- Missions, challenges
- Celebration animations
- Complex statistics
- Profile decorations

### Kept (Always Available)
- Journal (full functionality)
- Mood tracking
- Daily wisdom (simplified)
- Future messages
- AI support (Buddha/Haven)
- Settings and privacy features
- Basic profile info

### Visual Changes
- Muted color palette
- Softer contrasts
- Calmer accent colors
- Increased spacing
- Minimal animations
- Softer corners

## How It Works

### 1. Manual Activation
User taps the "Need Things Simple?" toggle on the home screen, sees a confirmation dialog, and enables Quiet Mode.

### 2. Auto-Suggestion
The app analyzes recent journal entries for:
- Stress keywords: "overwhelmed", "exhausted", "can't cope"
- Negative moods: anxious, sad, confused (3+ occurrences)
- Declining mood intensity trends

When stress patterns are detected (50+ stress score, 2+ signals), a gentle suggestion appears:
> "It looks like things have been heavy lately. Want to simplify the app for a bit?"

**Important**: 3-day cooldown between suggestions to avoid nagging.

### 3. Check-In System
After 7 days in Quiet Mode, a gentle check-in appears:
> "How are you feeling? You've been in Quiet Mode for 7 days."

Options:
- "Bring everything back" → Exits Quiet Mode
- "Keep it simple" → Stays in Quiet Mode, next check-in in 7 days

## User Experience Principles

1. **Never Forced**: Always a suggestion, never mandatory
2. **User Control**: Easy to enable, easy to disable
3. **Caring Tone**: Warm, non-judgmental messaging
4. **Data Safety**: Emphasize nothing is lost
5. **Reversible**: All changes are temporary
6. **Respectful**: 3-day cooldown prevents nagging

## Technical Architecture

### Domain Layer
- **QuietModeDetector**: Analyzes stress patterns
- **QuietModeManager**: Manages state and suggestions
- **QuietModeFeatures**: Defines visibility rules

### Data Layer
- **PreferencesManager**: Stores Quiet Mode state
- **JournalRepository**: Provides entries for analysis

### UI Layer
- **QuietModeTheme**: Muted color palette
- **QuietModeToggle**: Manual activation button
- **QuietModeSuggestionDialog**: Auto-suggestion UI
- **QuietModeIndicator**: Active state display

### State Flow
```
User writes stressed journals
    ↓
QuietModeDetector analyzes patterns
    ↓
QuietModeManager determines if suggest
    ↓
UI shows suggestion dialog
    ↓
User accepts/dismisses
    ↓
PreferencesManager stores state
    ↓
UI components react to state change
```

## Integration Guide

### Step 1: Add Repository Method
```kotlin
// JournalRepository.kt
interface JournalRepository {
    fun getEntriesAfterTimestamp(timestamp: Long): Flow<List<JournalEntryEntity>>
}

// JournalRepositoryImpl.kt
override fun getEntriesAfterTimestamp(timestamp: Long): Flow<List<JournalEntryEntity>> {
    return journalDao.getEntriesAfterTimestamp(timestamp)
}

// JournalDao.kt
@Query("SELECT * FROM journal_entries WHERE createdAt >= :timestamp ORDER BY createdAt DESC")
fun getEntriesAfterTimestamp(timestamp: Long): Flow<List<JournalEntryEntity>>
```

### Step 2: Add Dependency Injection
```kotlin
// AppModule.kt or WellbeingModule.kt
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

### Step 3: Update HomeViewModel
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val quietModeManager: QuietModeManager
) : ViewModel() {

    val isQuietMode = quietModeManager
        .isQuietModeEnabled()
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _showSuggestion = MutableStateFlow(false)
    val showSuggestion = _showSuggestion.asStateFlow()

    init {
        checkForSuggestion()
    }

    private fun checkForSuggestion() {
        viewModelScope.launch {
            val suggestion = quietModeManager.shouldSuggestQuietMode()
            if (suggestion.shouldSuggest) {
                _showSuggestion.value = true
            }
        }
    }

    fun toggleQuietMode() {
        viewModelScope.launch {
            quietModeManager.toggleQuietMode()
        }
    }

    fun acceptSuggestion() {
        viewModelScope.launch {
            quietModeManager.enableQuietMode()
            quietModeManager.recordSuggestionShown()
            _showSuggestion.value = false
        }
    }

    fun dismissSuggestion() {
        viewModelScope.launch {
            quietModeManager.recordSuggestionShown()
            _showSuggestion.value = false
        }
    }
}
```

### Step 4: Update HomeScreen
```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val isQuietMode by viewModel.isQuietMode.collectAsState()
    val showSuggestion by viewModel.showSuggestion.collectAsState()

    // Suggestion dialog
    if (showSuggestion) {
        QuietModeSuggestionDialog(
            onAccept = { viewModel.acceptSuggestion() },
            onDismiss = { viewModel.dismissSuggestion() }
        )
    }

    Column {
        // Active indicator
        if (isQuietMode) {
            QuietModeIndicator(
                onExit = { viewModel.toggleQuietMode() }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Toggle card
        QuietModeToggle(
            isQuietModeActive = isQuietMode,
            onToggle = { viewModel.toggleQuietMode() }
        )

        // Conditional gamification
        if (!isQuietMode) {
            StreakCard()
            XPProgressBar()
            LeaderboardSection()
        }

        // Always visible
        JournalQuickAction()
        DailyWisdomCard()
    }
}
```

### Step 5: Update Other Screens
Repeat similar patterns for ProfileScreen, StatsScreen, JournalScreen, etc.

## Testing

### Manual Test Scenarios

1. **Stress Detection**
   - Create 3+ journal entries with keywords: "overwhelmed", "exhausted", "can't cope"
   - Use moods: anxious, sad, confused
   - Verify suggestion appears
   - Test "Accept" and "Dismiss" flows

2. **Manual Toggle**
   - Enable from home screen
   - Verify confirmation dialog
   - Check features hide
   - Check theme changes
   - Disable and verify features return

3. **Check-In**
   - Enable Quiet Mode
   - Advance time by 7 days (or adjust timestamp in preferences)
   - Verify check-in dialog appears
   - Test both "Keep" and "Exit" options

4. **Cooldown**
   - Dismiss a suggestion
   - Try to trigger another within 3 days
   - Verify suggestion doesn't appear

## Common Issues

### Issue: Suggestion shows too often
**Solution**: Verify `recordSuggestionShown()` is called in both accept and dismiss flows.

### Issue: Features not hiding
**Solution**: Ensure all gamification components are wrapped in `if (!isQuietMode)` checks.

### Issue: Colors not changing
**Solution**: Apply QuietModeTheme colors when `isQuietMode` is true.

### Issue: Repository method missing
**Solution**: Add `getEntriesAfterTimestamp()` to JournalRepository interface and implementation.

## Performance

- Stress analysis only runs when explicitly checked (not on every journal save)
- Uses Flow-based reactive state for efficient updates
- No heavy computations on main thread
- Simple animations for smooth UX

## Accessibility

- WCAG AA compliant color contrasts
- Clear, descriptive labels
- Screen reader compatible
- Focus indicators visible
- Keyboard navigation support

## Future Enhancements

Potential improvements:
1. User customization of what to hide
2. Scheduled Quiet Mode (evening hours)
3. Integration with phone Do Not Disturb
4. Multiple intensity levels
5. Analytics on effectiveness
6. Personalized stress keyword learning

## Support

### Documentation Files
- **QUIET_MODE_SUMMARY.md**: High-level overview and file listing
- **QUIET_MODE_IMPLEMENTATION.md**: Detailed technical guide with examples
- **QUIET_MODE_FLOWS.md**: User flows and UI state diagrams
- **QUIET_MODE_CHECKLIST.md**: Complete integration checklist
- **QUIET_MODE_README.md**: This file - quick reference

### Code Documentation
All files include comprehensive inline documentation:
- Class and function documentation
- Usage examples
- Philosophy notes
- Integration patterns

### Example Code
See `QuietModeViewModel.kt` for complete integration examples.

## Philosophy Reminders

**User First**: Mental wellbeing comes before engagement metrics.

**Never Forced**: Always a suggestion, never mandatory.

**Warm & Caring**: Non-judgmental, supportive messaging.

**Reversible**: Easy to enable, easy to disable.

**Data Safety**: Emphasize nothing is lost.

**Respectful**: Cooldown periods prevent nagging.

---

## Quick Reference

### Check if Quiet Mode is active
```kotlin
val isActive = quietModeManager.isQuietModeEnabled().first()
```

### Enable/Disable
```kotlin
quietModeManager.enableQuietMode()
quietModeManager.disableQuietMode()
quietModeManager.toggleQuietMode()
```

### Check if should suggest
```kotlin
val suggestion = quietModeManager.shouldSuggestQuietMode()
if (suggestion.shouldSuggest) {
    showSuggestionDialog()
}
```

### Hide features conditionally
```kotlin
if (QuietModeFeatures.shouldShowFeature(Feature.ACHIEVEMENTS, isQuietMode)) {
    ShowAchievements()
}
```

### Apply quiet theme
```kotlin
val bgColor = if (isQuietMode) {
    QuietModeTheme.getBackground()
} else {
    MaterialTheme.colorScheme.background
}
```

---

**Remember**: Every interaction should make the user feel cared for, not judged. Wellbeing first, always.
