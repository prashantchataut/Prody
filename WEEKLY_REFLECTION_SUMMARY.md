# Weekly Reflection Summary System

## Overview

The Weekly Reflection Summary system makes journaling feel valuable by providing users with personalized, meaningful insights about their writing patterns, emotional journey, and personal growth.

**Core Philosophy**: Users should feel like their journaling practice matters. The weekly summary is NOT just statistics - it's a wise friend who actually read their journal and noticed patterns.

## Architecture

### Domain Layer

#### 1. WeeklySummaryEngine (`domain/summary/WeeklySummaryEngine.kt`)

The core interface for generating comprehensive weekly summaries.

```kotlin
interface WeeklySummaryEngine {
    suspend fun generate(userId: String, weekOf: LocalDate): WeeklySummary
    suspend fun canGenerate(userId: String, weekOf: LocalDate): Boolean
}
```

**Implementation**: `WeeklySummaryEngineImpl` performs deep analysis:
- Activity metrics (entries, words, active days)
- Mood trend analysis (improving/stable/declining)
- Content theme extraction
- Writing pattern detection
- Highlight entry selection
- Week-over-week comparison

#### 2. WeeklySummary Data Model

Comprehensive weekly summary with:
- **Time period**: `weekStart`, `weekEnd`
- **Activity**: `entriesCount`, `totalWords`, `activeDays`
- **Mood analysis**: `moodTrend`, `dominantMood`, `moodDistribution`
- **Content**: `topThemes`, `patterns`
- **Gamification**: `streakStatus`
- **Personalization**: `buddhaInsight`, `celebrationMessage`
- **Highlights**: `highlightEntry`, `highlightReason`
- **Comparison**: `previousWeekComparison`

#### 3. BuddhaWeeklyInsightGenerator (`domain/summary/BuddhaWeeklyInsightGenerator.kt`)

Generates personalized insights that feel human, not robotic.

**AI-Powered Insights** (when GeminiService is configured):
- References actual content from user's entries
- Notices specific patterns and themes
- Asks meaningful questions based on what was written
- Feels like a wise friend who read your journal

**Pattern-Based Fallback** (when AI unavailable):
- Still personalized based on actual data
- Theme-based insights
- Mood shift observations
- Writing frequency acknowledgments

**Example Good Insights**:
- "This week, you wrote a lot about [theme]. There's a pattern forming."
- "Your mood shifted from [X] to [Y] after writing about [topic]. What changed?"
- "You asked yourself a powerful question on [day]: [question]. Have you found an answer?"

**Example BAD Insights** (what we avoid):
- "Keep up the good work!" ❌
- "Consistency is key." ❌
- "You're making progress!" ❌

#### 4. WeeklySummaryScheduler (`domain/summary/WeeklySummaryScheduler.kt`)

Manages automated generation and display timing.

**Features**:
- Schedule generation on user-configured day (default: Sunday)
- Check if summary should be shown on app open
- Track last shown date to avoid duplicates
- WorkManager integration for background generation

### Data Layer

#### Repository Integration

`WeeklyDigestRepositoryImpl` now uses `WeeklySummaryEngine`:

```kotlin
override suspend fun generateWeeklyDigest(userId: String): Result<WeeklyDigestEntity> {
    val summary = weeklySummaryEngine.generate(userId, previousWeekDate)
    // Convert WeeklySummary to WeeklyDigestEntity
    // Save to database
}
```

### Preferences

**New Settings** (`PreferencesManager`):
- `WEEKLY_SUMMARY_ENABLED`: Enable/disable weekly summaries (default: true)
- `WEEKLY_SUMMARY_DAY`: Day of week to show summary (0-6, default: 0 for Sunday)
- `LAST_WEEKLY_SUMMARY_SHOWN`: Timestamp of last shown summary
- `WEEKLY_SUMMARY_NOTIFICATIONS`: Enable notifications (default: true)

### UI Layer

#### WeeklyDigestScreen Enhancements

**New Sections**:
1. **Week at a Glance**: Entries, words, active days
2. **Emotional Landscape**: Mood distribution visualization
3. **Mood Trend**: Improving/stable/declining with icon
4. **What You Wrote About**: Theme chips
5. **Patterns Noticed**: Writing patterns detected
6. **Buddha's Reflection**: Personalized insight (NOT generic)
7. **Highlight of the Week**: Best entry with reason
8. **Compared to Last Week**: Week-over-week metrics
9. **Celebration/Encouragement**: Motivational message
10. **Share Button**: Create shareable summary card

**Celebration Messages**:
- 7 active days: "Seven days, seven entries. You showed up for yourself every single day! 🌟"
- 7+ entries: "You journaled X times this week. That kind of consistency compounds."
- 5-6 entries: "You maintained a strong practice this week. X entries - that's dedication."
- 3-4 entries: "You've been checking in with yourself regularly. Keep showing up."
- 1-2 entries: "Even in busy weeks, you made time for reflection. That matters."
- 0 entries: "Life gets busy, and that's okay. Your journal is here whenever you're ready."

## Analysis Features

### Mood Trend Analysis

Compares first half vs second half of week:
- **IMPROVING**: Positive mood percentage increased by >20%
- **DECLINING**: Positive mood percentage decreased by >20%
- **STABLE**: Change within ±20%

Positive moods: HAPPY, CALM, MOTIVATED, GRATEFUL, EXCITED

### Theme Extraction

Analyzes journal content for 8 theme categories:
- **Work**: job, career, office, meeting, project
- **Relationships**: friend, family, partner, love
- **Health**: exercise, sleep, fitness, wellness
- **Growth**: learn, improve, progress, goals
- **Emotions**: feelings, anxiety, stress, mood
- **Creativity**: create, inspiration, art, music
- **Gratitude**: grateful, thankful, appreciate
- **Challenges**: problems, struggle, obstacles

Returns top 3 themes by frequency.

### Writing Pattern Detection

**Patterns Detected**:
- **MORNING_WRITER**: >60% entries written 5-11 AM
- **EVENING_REFLECTOR**: >60% entries written 6-11 PM
- **DEEP_THINKER**: Average word count >300
- **CONCISE_REFLECTOR**: Average word count <100
- **CONSISTENT_JOURNALER**: 5+ entries in week
- **EMOTIONAL_PROCESSOR**: >50% entries contain emotional processing words
- **GRATITUDE_PRACTICER**: >30% entries contain gratitude keywords
- **GOAL_ORIENTED**: >30% entries mention goals/plans

### Highlight Entry Selection

**Scoring System**:
- +10 points per 100 words (max 50)
- +100 points if bookmarked
- +30 points if has AI insights
- +20 points for positive moods
- +10 points if contains questions

Highest scoring entry becomes the highlight.

### Week-Over-Week Comparison

Compares with previous week:
- Entry count change
- Word count change
- Mood improvement detection
- New themes discovered

## Dependency Injection

**Module**: `RepositoryModule.kt`

```kotlin
@Binds
@Singleton
abstract fun bindWeeklySummaryEngine(
    impl: WeeklySummaryEngineImpl
): WeeklySummaryEngine
```

**Dependencies**:
- `JournalDao`: Fetch journal entries
- `MicroEntryDao`: Fetch micro entries
- `PreferencesManager`: User settings & streak tracking
- `BuddhaWeeklyInsightGenerator`: AI-powered insights

## Usage

### Manual Generation

```kotlin
// In ViewModel or Repository
val userId = "local"
val digest = weeklyDigestRepository.generateWeeklyDigest(userId)
```

### Automated Scheduling

```kotlin
// Initialize scheduler (e.g., in Application onCreate)
weeklySummaryScheduler.schedule()

// Check if should show on app open
if (weeklySummaryScheduler.shouldShowSummaryNow()) {
    // Show summary modal
    weeklySummaryScheduler.markSummaryShown()
}
```

### Customizing Settings

```kotlin
// Change summary day to Friday (5)
preferencesManager.setWeeklySummaryDay(5)

// Disable weekly summaries
preferencesManager.setWeeklySummaryEnabled(false)
```

## Future Enhancements

1. **Share Functionality**: Generate beautiful shareable cards
2. **Notification System**: Remind users when summary is ready
3. **Historical Trends**: View summaries from past weeks
4. **Intention Tracking**: Integrate with daily ritual completion rates
5. **Streak Milestones**: Celebrate journaling streaks in summaries
6. **Custom Themes**: User-defined theme categories
7. **Export Options**: PDF/Image export of summaries

## Design Principles

1. **Personalization over Generic**: Every insight should reference actual user data
2. **Wisdom over Statistics**: Focus on meaning, not just numbers
3. **Encouragement over Judgment**: Celebrate effort, not perfection
4. **Patterns over Platitudes**: Notice real trends, don't generate clichés
5. **Human over AI**: Make Buddha feel like a wise friend, not a robot

## Testing

### Unit Tests

Test `WeeklySummaryEngineImpl`:
- Mood trend calculation edge cases
- Theme extraction accuracy
- Pattern detection thresholds
- Highlight selection logic

Test `BuddhaWeeklyInsightGenerator`:
- Pattern-based fallback quality
- Celebration message variety
- Encouragement appropriateness

### Integration Tests

Test `WeeklyDigestRepositoryImpl`:
- Summary generation with real data
- Database entity conversion
- Error handling

### UI Tests

Test `WeeklyDigestScreen`:
- All sections display correctly
- Celebration messages appear
- Share button functionality
- Navigation to highlight entry

## Performance Considerations

1. **Background Generation**: Use WorkManager for off-main-thread processing
2. **Caching**: Store generated summaries in database
3. **Lazy Loading**: Only generate when needed
4. **AI Throttling**: Use pattern-based fallback when AI quota exceeded

## Files Created

### Domain Layer
- `app/src/main/java/com/prody/prashant/domain/summary/WeeklySummaryEngine.kt`
- `app/src/main/java/com/prody/prashant/domain/summary/WeeklySummaryEngineImpl.kt`
- `app/src/main/java/com/prody/prashant/domain/summary/BuddhaWeeklyInsightGenerator.kt`
- `app/src/main/java/com/prody/prashant/domain/summary/WeeklySummaryScheduler.kt`

### Data Layer (Modified)
- `app/src/main/java/com/prody/prashant/data/repository/WeeklyDigestRepositoryImpl.kt` (enhanced)
- `app/src/main/java/com/prody/prashant/data/local/preferences/PreferencesManager.kt` (added preferences)

### DI Layer (Modified)
- `app/src/main/java/com/prody/prashant/di/RepositoryModule.kt` (added bindings)

### UI Layer (Modified)
- `app/src/main/java/com/prody/prashant/ui/screens/digest/WeeklyDigestScreen.kt` (added sections)

### Existing Entities Used
- `app/src/main/java/com/prody/prashant/data/local/entity/WeeklyDigestEntity.kt`
- `app/src/main/java/com/prody/prashant/data/local/dao/WeeklyDigestDao.kt`
- `app/src/main/java/com/prody/prashant/ui/screens/digest/WeeklyDigestViewModel.kt`

## Summary

The Weekly Reflection Summary system transforms journaling from a solo activity into a guided growth practice. By providing personalized, meaningful insights that reference actual user content, it makes users feel heard, understood, and motivated to continue their practice.

The system is designed to scale - starting with pattern-based insights but leveraging AI when available for truly personalized reflections. Most importantly, it never feels generic or robotic. Buddha is a wise friend who actually read your journal.
