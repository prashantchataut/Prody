# Dual Streak System - Integration Guide

## Overview

The Dual Streak System provides two independent streaks with different engagement levels:

1. **Wisdom Streak** (Easy): Quick daily engagement (~30 seconds)
   - Viewing daily quote, word, proverb, or idiom
   - Lower tier rewards
   - Encourages daily check-ins

2. **Reflection Streak** (Meaningful): Deep engagement
   - Writing journal entries (any length)
   - Completing evening reflection
   - Higher tier rewards
   - Rewards deeper commitment

## Key Features

- **Two Independent Streaks**: Each streak has its own counter and grace period
- **Grace Periods**: One skip per 14 days for each streak
- **Encouraging Design**: The system understands that life happens
- **Clean UI**: Shows both streaks side-by-side on the home screen

## Architecture

### Entity Layer
- **DualStreakEntity.kt**: Room entity storing both streaks and grace periods
- Location: `app/src/main/java/com/prody/prashant/data/local/entity/`

### DAO Layer
- **DualStreakDao.kt**: Database operations for dual streaks
- Location: `app/src/main/java/com/prody/prashant/data/local/dao/`

### Domain Layer
- **DualStreakManager.kt**: Business logic for streak maintenance
- **DualStreakModels.kt**: Data models (StreakInfo, DualStreakStatus, etc.)
- Location: `app/src/main/java/com/prody/prashant/domain/streak/`

### UI Layer
- **DualStreakCard.kt**: Composable component showing both streaks
- Location: `app/src/main/java/com/prody/prashant/ui/components/`

## Integration Points

### 1. Wisdom Streak Triggers

Call `viewModel.onWisdomContentViewed()` when the user:
- Views the daily quote
- Views the word of the day
- Views the proverb of the day
- Views the idiom of the day

**Example in HomeScreen.kt:**
```kotlin
LaunchedEffect(uiState.dailyQuote) {
    // User has viewed the quote, maintain wisdom streak
    viewModel.onWisdomContentViewed()
}
```

**Example in VocabularyDetailScreen.kt:**
```kotlin
LaunchedEffect(wordId) {
    // User viewed word details, maintain wisdom streak
    viewModel.onWisdomContentViewed()
}
```

### 2. Reflection Streak Triggers

Call `viewModel.onReflectionCompleted()` when the user:
- Saves a journal entry (any length)
- Completes evening reflection
- Completes a micro-entry with meaningful content

**Example in JournalScreen.kt:**
```kotlin
// After successfully saving journal entry
fun onSaveJournalEntry() {
    viewModelScope.launch {
        journalDao.insert(entry)
        // Maintain reflection streak
        dualStreakManager.maintainReflectionStreak()
    }
}
```

**Example in EveningReflectionScreen.kt:**
```kotlin
// After completing evening reflection
fun onCompleteReflection() {
    viewModelScope.launch {
        saveReflection()
        // Maintain reflection streak
        dualStreakManager.maintainReflectionStreak()
    }
}
```

## Displaying the Dual Streak Card

The DualStreakCard is already integrated into the HomeScreen. To add it to other screens:

```kotlin
@Composable
fun YourScreen(viewModel: YourViewModel = hiltViewModel()) {
    val dualStreakStatus by viewModel.dualStreakStatus.collectAsState()

    DualStreakCard(
        dualStreakStatus = dualStreakStatus,
        onTapForDetails = {
            // Show detail dialog or navigate to streak details screen
        }
    )
}
```

## Grace Period Logic

Each streak has one grace day per 14-day period:

1. **Automatic Grace**: NOT automatic - user must manually apply grace if needed
2. **Grace Availability**: Shown in UI with visual indicator
3. **Grace Reset**: 14 days after last use
4. **Encouraging Design**: Grace day helps preserve streaks when life happens

To manually apply grace (if implementing grace UI):
```kotlin
// Apply wisdom grace
viewModel.applyWisdomGrace()

// Apply reflection grace
viewModel.applyReflectionGrace()
```

## Rewards and XP

### Wisdom Streak Rewards (Lower Tier)
- Base XP: 5 per day
- Milestone XP: 25 (at 7, 14, 30 days, etc.)

### Reflection Streak Rewards (Higher Tier)
- Base XP: 15 per day
- Milestone XP: 50 (at 7, 14, 30 days, etc.)

## Database Migration

The system includes automatic database migration from version 7 to 8:
- Creates `dual_streaks` table
- No data loss from existing streak systems

## Testing Checklist

- [ ] Wisdom streak increments when viewing daily wisdom
- [ ] Reflection streak increments when journaling
- [ ] Grace period shows correctly in UI
- [ ] Grace period resets after 14 days
- [ ] Streaks are independent (one doesn't affect the other)
- [ ] Both streaks display correctly on HomeScreen
- [ ] Longest streak is tracked correctly
- [ ] Streak maintenance is idempotent (calling twice doesn't break it)

## Future Enhancements

Potential additions to the dual streak system:

1. **Streak Celebrations**: Animated celebrations at milestones (7, 30, 100 days)
2. **Streak Notifications**: Remind users before streak expires
3. **Streak Challenges**: Community challenges based on streaks
4. **Streak Badges**: Special badges for long streaks
5. **Streak Recovery**: Option to "recover" broken streaks with tokens/currency
6. **Detail Dialog**: Expanded stats and history in a detail dialog

## Philosophy

The Dual Streak System is designed to be:
- **Encouraging, not punishing**: Grace periods help users maintain streaks
- **Rewarding different engagement types**: Quick wins AND deep work both matter
- **Clear and transparent**: Users always know their streak status
- **Fair and forgiving**: Life happens, and the system understands that

## Support

For questions or issues with the dual streak system:
1. Check the implementation in `DualStreakManager.kt`
2. Review the UI in `DualStreakCard.kt`
3. Verify database schema in `DualStreakEntity.kt`
4. Test with different scenarios in your ViewModel

## Example Usage Flow

```
Day 1: User opens app → Views quote → Wisdom streak = 1
Day 2: User journals → Reflection streak = 1, Wisdom remains 1
Day 3: User views word → Wisdom streak = 2
Day 4: User misses both streaks
Day 5: User uses wisdom grace → Wisdom streak preserved
Day 6: User journals → Reflection breaks, starts at 1 again
```

This flow demonstrates how the two streaks work independently and how grace periods help maintain consistency.
