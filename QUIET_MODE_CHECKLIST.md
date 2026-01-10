# Quiet Mode - Integration Checklist

Use this checklist to ensure Quiet Mode is fully integrated into your Prody app.

## Pre-Integration Setup

- [ ] Review QUIET_MODE_IMPLEMENTATION.md
- [ ] Review QUIET_MODE_SUMMARY.md
- [ ] Review QUIET_MODE_FLOWS.md
- [ ] Understand the philosophy and user flows

## 1. Repository Layer

### JournalRepository Interface
- [ ] Add `getEntriesAfterTimestamp(timestamp: Long): Flow<List<JournalEntryEntity>>` method
- [ ] Implement in JournalRepositoryImpl
- [ ] Add corresponding DAO query in JournalDao:
  ```kotlin
  @Query("SELECT * FROM journal_entries WHERE createdAt >= :timestamp ORDER BY createdAt DESC")
  fun getEntriesAfterTimestamp(timestamp: Long): Flow<List<JournalEntryEntity>>
  ```
- [ ] Test the query returns correct entries

## 2. Dependency Injection

### Hilt Module (e.g., AppModule.kt or WellbeingModule.kt)
- [ ] Add QuietModeDetector singleton provider
  ```kotlin
  @Provides
  @Singleton
  fun provideQuietModeDetector(): QuietModeDetector
  ```
- [ ] Add QuietModeManager singleton provider
  ```kotlin
  @Provides
  @Singleton
  fun provideQuietModeManager(
      preferencesManager: PreferencesManager,
      journalRepository: JournalRepository,
      detector: QuietModeDetector
  ): QuietModeManager
  ```
- [ ] Verify dependencies are available (PreferencesManager, JournalRepository)

## 3. ViewModel Integration

### HomeViewModel (or create QuietModeViewModel)
- [ ] Inject QuietModeManager
- [ ] Add `isQuietModeEnabled` StateFlow
- [ ] Add `showSuggestionDialog` state
- [ ] Add `showExitCheckIn` state
- [ ] Add `checkForSuggestion()` function
- [ ] Add `toggleQuietMode()` function
- [ ] Add `enableQuietMode()` function
- [ ] Add `disableQuietMode()` function
- [ ] Add `acceptSuggestion()` function
- [ ] Add `dismissSuggestion()` function
- [ ] Call `checkForSuggestion()` in init or onResume

## 4. HomeScreen UI

### Basic Integration
- [ ] Inject QuietModeManager or ViewModel
- [ ] Collect `isQuietModeEnabled` state
- [ ] Add QuietModeIndicator at top (when active)
- [ ] Add QuietModeToggle card
- [ ] Add QuietModeSuggestionDialog (when suggestion triggers)
- [ ] Add QuietModeExitCheckInDialog (for 7-day check-in)

### Conditional Rendering
- [ ] Wrap StreakCard in `if (!isQuietMode)`
- [ ] Wrap XPProgressBar in `if (!isQuietMode)`
- [ ] Wrap LeaderboardSection in `if (!isQuietMode)`
- [ ] Wrap AchievementsSection in `if (!isQuietMode)`
- [ ] Wrap MissionsSection in `if (!isQuietMode)`
- [ ] Keep JournalSection (always visible)
- [ ] Keep DailyWisdomCard (always visible, simplified)
- [ ] Keep FutureMessagesSection (always visible)

### Theme Application
- [ ] Apply QuietModeTheme colors when `isQuietMode` is true
- [ ] Test color changes in both light and dark mode

## 5. ProfileScreen

- [ ] Collect `isQuietModeEnabled` state
- [ ] Hide ProfileBanner when quiet mode active
- [ ] Hide BadgesSection when quiet mode active
- [ ] Hide TrophyShelf when quiet mode active
- [ ] Hide ProfileFrames when quiet mode active
- [ ] Keep basic profile info (name, avatar)
- [ ] Add CompactQuietModeToggle in settings section
- [ ] Test profile simplification

## 6. StatsScreen

- [ ] Collect `isQuietModeEnabled` state
- [ ] Show simplified stats view when quiet mode active
- [ ] Hide detailed analytics when quiet mode active
- [ ] Hide comparison stats when quiet mode active
- [ ] Keep basic entry count and days journaling
- [ ] Test stats screen in both modes

## 7. JournalScreen

- [ ] Collect `isQuietModeEnabled` state
- [ ] Hide wisdom suggestions in editor when quiet mode active
- [ ] Hide AI insights prompts when quiet mode active
- [ ] Keep full journal editor functionality
- [ ] Keep mood tracking
- [ ] Keep tags and bookmarks
- [ ] Apply QuietModeTheme colors when active
- [ ] Test journal entry creation in both modes

## 8. Notifications

### Notification Service
- [ ] Inject QuietModeManager
- [ ] Check quiet mode before showing achievement notifications
- [ ] Check quiet mode before showing level-up notifications
- [ ] Check quiet mode before showing streak notifications
- [ ] Check quiet mode before showing leaderboard updates
- [ ] Always show journal reminders (essential)
- [ ] Always show daily wisdom notifications
- [ ] Always show future message arrivals
- [ ] Use `QuietModeUI.shouldShowNotification()` helper

## 9. Gamification Logic

### GamificationService / ViewModel
- [ ] Inject QuietModeManager
- [ ] Check quiet mode before showing XP gain popups
- [ ] Check quiet mode before showing level-up celebrations
- [ ] Check quiet mode before showing achievement unlocks
- [ ] Still track XP/achievements in background (just don't show)
- [ ] Test gamification works normally when quiet mode disabled

## 10. Settings Screen

- [ ] Add "Quiet Mode" section in settings
- [ ] Add CompactQuietModeToggle
- [ ] Add description: "Simplify the app when you need focus"
- [ ] Optional: Add threshold customization
- [ ] Optional: Add "What is Quiet Mode?" info dialog
- [ ] Test toggle works from settings

## 11. Testing

### Manual Testing
- [ ] Test manual enable from home screen
- [ ] Test manual enable from settings
- [ ] Test confirmation dialog appears
- [ ] Verify features hide correctly
- [ ] Verify theme changes correctly
- [ ] Verify indicator appears
- [ ] Test manual disable from indicator
- [ ] Test manual disable from settings
- [ ] Verify features return
- [ ] Verify theme reverts

### Auto-Suggestion Testing
- [ ] Create 3+ journal entries with stress keywords
- [ ] Use keywords: "overwhelmed", "exhausted", "can't cope"
- [ ] Use negative moods: anxious, sad, confused
- [ ] Verify suggestion dialog appears
- [ ] Test "Accept" flow
- [ ] Test "Dismiss" flow
- [ ] Verify 3-day cooldown works (adjust timestamp or wait)
- [ ] Verify doesn't suggest when already in quiet mode

### Check-In Testing
- [ ] Enable quiet mode
- [ ] Adjust timestamp to 7 days later (or wait)
- [ ] Verify check-in dialog appears
- [ ] Test "Keep it simple" option
- [ ] Test "Bring everything back" option
- [ ] Verify next check-in timing

### Edge Cases
- [ ] Test with no journal entries
- [ ] Test with insufficient entries (<3)
- [ ] Test with only positive entries
- [ ] Test suggestion cooldown period
- [ ] Test rapid toggling on/off
- [ ] Test with offline/no network
- [ ] Test state persistence after app restart

### Visual Testing
- [ ] Test in light mode
- [ ] Test in dark mode
- [ ] Test color contrast (accessibility)
- [ ] Test on different screen sizes
- [ ] Test animations are smooth
- [ ] Test indicator visibility
- [ ] Test dialog layouts
- [ ] Test with dynamic text sizes

## 12. Performance

- [ ] Verify no lag when toggling
- [ ] Verify stress analysis doesn't block UI
- [ ] Verify smooth color transitions
- [ ] Verify efficient state updates (no unnecessary recompositions)
- [ ] Test with large number of journal entries (100+)
- [ ] Monitor memory usage

## 13. Accessibility

- [ ] Test with TalkBack enabled
- [ ] Verify all buttons have content descriptions
- [ ] Verify focus order is logical
- [ ] Verify color contrast meets WCAG AA
- [ ] Test with large text sizes
- [ ] Test with bold text enabled
- [ ] Test keyboard navigation

## 14. Localization (Future)

- [ ] Extract all hardcoded strings to strings.xml
- [ ] Add string keys for all dialogs
- [ ] Add string keys for all buttons
- [ ] Add string keys for all descriptions
- [ ] Test with different languages (if applicable)
- [ ] Test with RTL languages (if applicable)

## 15. Analytics (Optional)

If you have analytics:
- [ ] Track quiet mode enabled events
- [ ] Track quiet mode disabled events
- [ ] Track suggestion shown events
- [ ] Track suggestion accepted events
- [ ] Track suggestion dismissed events
- [ ] Track check-in shown events
- [ ] Track check-in responses
- [ ] Track duration in quiet mode
- [ ] Track stress score distribution
- [ ] Respect user privacy (anonymize data)

## 16. Documentation

- [ ] Update app README with Quiet Mode section
- [ ] Document for other developers
- [ ] Add inline code comments where needed
- [ ] Update changelog
- [ ] Prepare user-facing documentation (if needed)

## 17. User Education

### First Time User
- [ ] Consider onboarding slide about Quiet Mode
- [ ] Add tooltip or first-time hint on toggle
- [ ] Explain in app tour (if you have one)

### In-App Help
- [ ] Add "What is Quiet Mode?" info dialog
- [ ] Link to help article (if you have support docs)
- [ ] Add FAQ entry

## 18. Release Preparation

### Before Release
- [ ] Complete all integration tasks
- [ ] Pass all manual tests
- [ ] Verify performance is good
- [ ] Verify accessibility compliance
- [ ] Review code with team
- [ ] Test on multiple devices
- [ ] Test on different Android versions

### Release Notes
- [ ] Write user-friendly description
- [ ] Highlight wellbeing focus
- [ ] Explain what it does
- [ ] Emphasize user control
- [ ] Mention it's optional

### Post-Release
- [ ] Monitor crash reports
- [ ] Monitor user feedback
- [ ] Track adoption rate
- [ ] Track user satisfaction
- [ ] Iterate based on feedback

## Verification Checklist

### Code Quality
- [ ] No compiler warnings
- [ ] No lint errors
- [ ] Code follows project style guide
- [ ] All functions have documentation
- [ ] Complex logic has comments
- [ ] No hardcoded values (use constants)
- [ ] Proper error handling

### Functionality
- [ ] All features work as expected
- [ ] No crashes or errors
- [ ] State persists correctly
- [ ] Transitions are smooth
- [ ] Performance is acceptable
- [ ] Works offline
- [ ] Works with no journal entries

### User Experience
- [ ] Messaging is warm and caring
- [ ] Dialogs are clear and helpful
- [ ] Actions are reversible
- [ ] No nagging behavior
- [ ] Easy to understand
- [ ] Easy to use
- [ ] Feels caring, not clinical

## Common Integration Issues

### Issue: Repository method not found
- **Fix**: Ensure `getEntriesAfterTimestamp` is added to JournalRepository interface

### Issue: Dependency injection fails
- **Fix**: Check all dependencies are provided in Hilt module

### Issue: State not updating
- **Fix**: Ensure using `collectAsState()` in Composables, not `first()`

### Issue: Features not hiding
- **Fix**: Double-check conditional rendering wraps all gamification elements

### Issue: Colors not changing
- **Fix**: Ensure QuietModeTheme colors are applied when `isQuietMode` is true

### Issue: Suggestion shows too often
- **Fix**: Verify `recordSuggestionShown()` is called in both accept and dismiss

### Issue: Suggestion never shows
- **Fix**: Check threshold values, verify journal entries have stress keywords

## Final Sign-Off

Before marking complete:
- [ ] All integration tasks completed
- [ ] All tests passed
- [ ] Code reviewed and approved
- [ ] Documentation complete
- [ ] User education materials ready
- [ ] Release notes written
- [ ] Team alignment achieved

---

## Notes

Use this space for implementation notes, issues encountered, or decisions made:

```
[Your notes here]
```

---

**Remember**: The goal is to make users feel cared for, not to maximize engagement. Wellbeing first, metrics second.

**Review Date**: _____________
**Completed By**: _____________
**Reviewed By**: _____________
