# Prody Runtime Baseline Report

**Date:** December 16, 2025
**Agent:** Parallel Agent 3 (Reliability, Performance, Accessibility, QA)

---

## Runtime Analysis (Code-Based)

Since the containerized environment lacks Android emulator/device access, this baseline is derived from comprehensive code analysis.

---

## Startup Analysis

### Application Initialization Sequence

```
1. Application.attachBaseContext()
   └── CrashHandler.initialize() - Early crash handling setup

2. Application.onCreate()
   ├── Process check (crash process vs main process)
   ├── Hilt injection (via @HiltAndroidApp)
   ├── Notification channels creation
   └── GamificationService.initializeUserData() (async)

3. MainActivity.onCreate()
   ├── Splash screen setup
   ├── Notification permission request (Android 13+)
   ├── Content composition (setContent)
   └── Notification scheduling (after injection complete)
```

### Potential Startup Bottlenecks

| Component | Risk | Location |
|-----------|------|----------|
| Database initialization | Medium | Room database first access |
| Notification scheduling | Low | Async via coroutine |
| Gamification data load | Medium | Background coroutine |
| Font loading | **HIGH** | Type.kt - can throw exceptions |

---

## Scroll Jank Locations (Identified via Code Analysis)

### Critical Jank Hotspots

1. **HomeScreen.kt**
   - Lines 300-307: Blur effect (12dp) in scrolling list
   - Lines 381-387: Blur effect (10dp) in scrolling content
   - Lines 507-516: Blur effect (25dp) in scrolling card
   - Lines 763-830: Animation state in forEach loop

2. **ChallengesScreen.kt**
   - Line 389: Blur effect (12dp) in list header
   - Line 458: Blur effect (40dp) - VERY EXPENSIVE
   - Line 513: Blur effect (40dp) - VERY EXPENSIVE
   - Lines 352-373: Canvas operations with trigonometric calculations

3. **StatsScreen.kt**
   - Lines 371-387: Canvas with cos/sin calculations
   - Lines 450-471: Canvas drawing in LazyColumn
   - Lines 968-989: Canvas pie chart in list
   - Line 625: LazyRow without proper keys

4. **QuotesScreen.kt**
   - Multiple expandable cards without proper keys

---

## Blank Content Screen Risk Assessment

### Screens with Empty State Handling

| Screen | Empty State | Loading State | Error State |
|--------|-------------|---------------|-------------|
| HomeScreen | Partial | Yes | Partial |
| JournalScreen | Yes (EmptyState component) | Partial | No |
| VocabularyListScreen | Yes (EmptyState) | Partial | No |
| StatsScreen | No | Partial | No |
| ChallengesScreen | No | Partial | No |
| QuotesScreen | No | Partial | No |
| ProfileScreen | No | Partial | No |

### Missing State Handlers (Requires Fixes)

1. **StatsScreen:** No empty state when no journal entries/moods
2. **ChallengesScreen:** No empty state for empty challenge lists
3. **QuotesScreen:** No empty state handling for tabs
4. **ProfileScreen:** No error handling for achievement loading

---

## Crash Risk Assessment

### Font Loading Crash (Highest Risk)

**File:** `app/src/main/java/com/prody/prashant/ui/theme/Type.kt`

```kotlin
// Current implementation uses try-catch but can still cause issues
val PoppinsFamily: FontFamily = try {
    FontFamily(
        Font(R.font.poppins_thin, FontWeight.Thin),
        // ... more fonts
    )
} catch (e: Exception) {
    FontFamily.SansSerif
}
```

**Problem:**
- Try-catch at FontFamily level may not catch all font loading scenarios
- Font exceptions can occur during scroll/layout, not just initialization
- Each Font() reference is evaluated lazily

**Font Files Verified:**
- `res/font/poppins_*.ttf` - 9 files present
- `res/font/playfairdisplay_*.ttf` - 5 files present

### Other Crash Risks

| Risk | Severity | File | Description |
|------|----------|------|-------------|
| AlarmManager null | Low | NotificationScheduler.kt | Handled with null check |
| DataStore corruption | Low | AiCacheManager.kt | Has cleanup mechanism |
| Room migration | Medium | ProdyDatabase.kt | Needs migration strategy review |

---

## Memory Concerns

### Identified Memory Risks

1. **AiCacheManager:** DataStore-based caching without strict size limits
2. **ShareProfileUtil:** Bitmap generation for sharing
3. **Infinite transitions:** Multiple in scrollable content
4. **Canvas operations:** Per-frame allocations in list items

### Recommended Memory Profiling

When runtime access is available:
```bash
# Enable StrictMode in debug builds (already partially configured)
# Monitor via Android Profiler:
# - Track allocations during scroll
# - Watch for memory growth over time
# - Profile specific screens
```

---

## Performance Metrics to Collect (Runtime)

| Metric | Target | How to Measure |
|--------|--------|----------------|
| Cold start time | < 2s | adb logcat timing |
| Scroll FPS | 60 FPS | GPU Profiler |
| Memory usage | < 150MB | Android Profiler |
| Frame drops | < 5% | Choreographer tracking |

---

## Recommended Runtime Tests

### Manual Test Checklist

```
[ ] Cold start from launcher
[ ] App resume from background
[ ] Rapid scroll through Home feed
[ ] Rapid scroll through Journal list
[ ] Rapid scroll through Vocabulary list
[ ] Open/close multiple journal entries
[ ] Navigate all bottom tabs rapidly
[ ] Trigger notification scheduling
[ ] Background app, wait 1 hour, resume
[ ] Test on low RAM device (2GB)
```

---

## Next Steps

1. Deploy to emulator/device when available
2. Run manual test checklist
3. Collect actual performance metrics
4. Update this document with measured values
5. Profile memory under load
