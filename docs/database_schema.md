# Prody Database Schema Documentation

## Overview

Prody uses **Room** as its local database solution, providing a robust SQLite abstraction layer with compile-time query verification. The database is designed to support offline-first functionality while maintaining flexibility for future backend sync capabilities.

**Database Name:** `prody_database`
**Current Version:** 1
**ORM:** Android Room with Kotlin

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              ProdyDatabase                                   │
│                          (Room Database v1)                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────────┐  │
│  │  User Domain    │  │ Content Domain  │  │   Gamification Domain       │  │
│  ├─────────────────┤  ├─────────────────┤  ├─────────────────────────────┤  │
│  │ user_profile    │  │ vocabulary      │  │ achievements                │  │
│  │ user_stats      │  │ vocab_learning  │  │ leaderboard                 │  │
│  │ streak_history  │  │ quotes          │  │ peer_interactions           │  │
│  │ journal_entries │  │ proverbs        │  │ challenges                  │  │
│  │ future_messages │  │ idioms          │  │ challenge_milestones        │  │
│  │                 │  │ phrases         │  │ challenge_participation     │  │
│  │                 │  │ motiv_messages  │  │ challenge_leaderboard       │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────────┘  │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Entity Reference

### User Domain

#### 1. `user_profile`

Central user profile entity containing identity, preferences, and aggregate statistics.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | INTEGER | NO | 1 | Primary key (singleton) |
| `displayName` | TEXT | NO | "Growth Seeker" | User's display name |
| `bio` | TEXT | NO | "" | User biography |
| `avatarId` | TEXT | NO | "default" | Reference to avatar asset |
| `bannerId` | TEXT | NO | "default_dawn" | Reference to banner asset |
| `titleId` | TEXT | NO | "seeker" | Earned title identifier |
| `totalPoints` | INTEGER | NO | 0 | Cumulative XP points |
| `currentStreak` | INTEGER | NO | 0 | Active daily streak |
| `longestStreak` | INTEGER | NO | 0 | Historical best streak |
| `lastActiveDate` | INTEGER | NO | now() | Timestamp of last activity |
| `joinedAt` | INTEGER | NO | now() | Account creation timestamp |
| `wordsLearned` | INTEGER | NO | 0 | Total vocabulary mastered |
| `journalEntriesCount` | INTEGER | NO | 0 | Total journal entries |
| `futureMessagesCount` | INTEGER | NO | 0 | Total future letters |
| `futureLettersSent` | INTEGER | NO | 0 | Letters written |
| `futureLettersReceived` | INTEGER | NO | 0 | Letters delivered |
| `buddhaConversations` | INTEGER | NO | 0 | AI conversations count |
| `quotesReflected` | INTEGER | NO | 0 | Quotes interacted with |
| `totalReflectionTime` | INTEGER | NO | 0 | Total reflection seconds |
| `preferredWisdomCategories` | TEXT | NO | "" | Comma-separated preferences |
| `dailyGoalMinutes` | INTEGER | NO | 10 | Daily engagement goal |
| `preferences` | TEXT | NO | "{}" | JSON blob for settings |

**Indices:**
- PRIMARY KEY on `id`

**Notes:**
- Single-row singleton pattern (id always = 1)
- `preferences` JSON schema can evolve without migrations
- Timestamps stored as Unix epoch milliseconds

---

#### 2. `user_stats`

Daily, weekly, and monthly activity metrics for analytics and gamification.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | INTEGER | NO | 1 | Primary key (singleton) |
| `dailyPointsEarned` | INTEGER | NO | 0 | Points earned today |
| `weeklyPointsEarned` | INTEGER | NO | 0 | Points earned this week |
| `monthlyPointsEarned` | INTEGER | NO | 0 | Points earned this month |
| `dailyWordsLearned` | INTEGER | NO | 0 | Words learned today |
| `weeklyWordsLearned` | INTEGER | NO | 0 | Words learned this week |
| `monthlyWordsLearned` | INTEGER | NO | 0 | Words learned this month |
| `dailyJournalEntries` | INTEGER | NO | 0 | Entries written today |
| `weeklyJournalEntries` | INTEGER | NO | 0 | Entries written this week |
| `monthlyJournalEntries` | INTEGER | NO | 0 | Entries written this month |
| `lastResetDate` | INTEGER | NO | now() | Daily reset timestamp |
| `weekStartDate` | INTEGER | NO | now() | Week start timestamp |
| `monthStartDate` | INTEGER | NO | now() | Month start timestamp |

**Indices:**
- PRIMARY KEY on `id`

**Reset Logic:**
- Daily stats reset at midnight local time
- Weekly stats reset on Monday 00:00
- Monthly stats reset on 1st of month 00:00

---

#### 3. `streak_history`

Historical record of daily activity for streak tracking and heatmap visualization.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | INTEGER | NO | auto | Primary key (auto-increment) |
| `date` | INTEGER | NO | - | Date timestamp |
| `activitiesCompleted` | TEXT | NO | "" | Comma-separated activity types |
| `pointsEarned` | INTEGER | NO | 0 | XP earned that day |
| `streakDay` | INTEGER | NO | 1 | Streak day number |

**Indices:**
- PRIMARY KEY on `id`
- INDEX on `date` (for range queries)

**Activity Types:**
- `journal` - Journal entry written
- `vocabulary` - Word learned
- `reflection` - Quote reflected
- `future_message` - Future letter written
- `challenge` - Challenge progress made

---

#### 4. `journal_entries`

User journal entries with mood tracking and AI response integration.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | INTEGER | NO | auto | Primary key (auto-increment) |
| `content` | TEXT | NO | - | Journal entry text |
| `mood` | TEXT | NO | - | Mood identifier |
| `moodIntensity` | INTEGER | NO | 5 | Intensity 1-10 scale |
| `buddhaResponse` | TEXT | YES | null | AI-generated wisdom response |
| `tags` | TEXT | NO | "" | Comma-separated tags |
| `isBookmarked` | BOOLEAN | NO | false | User bookmark flag |
| `wordCount` | INTEGER | NO | 0 | Entry word count |
| `createdAt` | INTEGER | NO | now() | Creation timestamp |
| `updatedAt` | INTEGER | NO | now() | Last modification timestamp |

**Indices:**
- PRIMARY KEY on `id`
- INDEX on `createdAt` DESC (for recent entries)
- INDEX on `mood` (for mood analytics)
- INDEX on `isBookmarked` (for favorites filter)

**Mood Values:**
- `joyful`, `happy`, `content`, `calm`, `neutral`
- `thoughtful`, `melancholy`, `anxious`, `frustrated`, `sad`

**Notes:**
- `buddhaResponse` is populated async after entry creation
- `tags` supports user-defined categorization
- `wordCount` calculated on insert/update

---

#### 5. `future_messages`

Time capsule messages from user to their future self.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | INTEGER | NO | auto | Primary key (auto-increment) |
| `title` | TEXT | NO | - | Message title |
| `content` | TEXT | NO | - | Message body |
| `deliveryDate` | INTEGER | NO | - | Scheduled delivery timestamp |
| `isDelivered` | BOOLEAN | NO | false | Delivery status |
| `isRead` | BOOLEAN | NO | false | Read status |
| `category` | TEXT | NO | "general" | Message category |
| `attachedGoal` | TEXT | YES | null | Related goal/intention |
| `createdAt` | INTEGER | NO | now() | Creation timestamp |
| `deliveredAt` | INTEGER | YES | null | Actual delivery timestamp |

**Indices:**
- PRIMARY KEY on `id`
- INDEX on `deliveryDate` (for scheduling)
- INDEX on `isDelivered` (for pending messages)

**Categories:**
- `general` - General future message
- `goal` - Goal-related reminder
- `promise` - Promise to self
- `reminder` - Simple reminder
- `motivation` - Motivational message

**Delivery Logic:**
- WorkManager checks pending messages daily
- Notification triggered when `deliveryDate <= now() && !isDelivered`
- `deliveredAt` set on notification delivery

---

### Content Domain

#### 6. `vocabulary`

Word of the day content with spaced repetition learning support.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | INTEGER | NO | auto | Primary key (auto-increment) |
| `word` | TEXT | NO | - | The vocabulary word |
| `definition` | TEXT | NO | - | Word definition |
| `pronunciation` | TEXT | NO | "" | IPA pronunciation guide |
| `partOfSpeech` | TEXT | NO | "" | Grammar category |
| `exampleSentence` | TEXT | NO | "" | Usage example |
| `synonyms` | TEXT | NO | "" | Comma-separated synonyms |
| `antonyms` | TEXT | NO | "" | Comma-separated antonyms |
| `origin` | TEXT | NO | "" | Etymology information |
| `difficulty` | INTEGER | NO | 1 | Difficulty 1-5 scale |
| `category` | TEXT | NO | "general" | Content category |
| `isLearned` | BOOLEAN | NO | false | Mastery status |
| `learnedAt` | INTEGER | YES | null | Mastery timestamp |
| `reviewCount` | INTEGER | NO | 0 | Times reviewed |
| `lastReviewedAt` | INTEGER | YES | null | Last review timestamp |
| `nextReviewAt` | INTEGER | YES | null | Next review (SR) |
| `masteryLevel` | INTEGER | NO | 0 | Mastery 0-100% |
| `isFavorite` | BOOLEAN | NO | false | User favorite flag |
| `shownAsDaily` | BOOLEAN | NO | false | Shown as word of day |
| `shownAt` | INTEGER | YES | null | When shown as daily |

**Indices:**
- PRIMARY KEY on `id`
- UNIQUE on `word`
- INDEX on `category`
- INDEX on `difficulty`
- INDEX on `isLearned`
- INDEX on `shownAsDaily`

**Categories:**
- `general`, `academic`, `business`, `literary`, `scientific`, `philosophical`

**Spaced Repetition Algorithm:**
```
Interval[0] = 1 day
Interval[n] = Interval[n-1] * 2.5
If forgotten: reset to Interval[0]
masteryLevel increases by 10-20 per correct review
```

---

#### 7. `vocabulary_learning`

Detailed learning progress for spaced repetition system.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | INTEGER | NO | auto | Primary key |
| `vocabularyId` | INTEGER | NO | - | FK to vocabulary |
| `easeFactor` | REAL | NO | 2.5 | SM-2 ease factor |
| `interval` | INTEGER | NO | 0 | Current interval days |
| `repetitions` | INTEGER | NO | 0 | Successful repetitions |
| `nextReviewDate` | INTEGER | YES | null | Next review timestamp |
| `lastReviewDate` | INTEGER | YES | null | Last review timestamp |
| `learningStep` | INTEGER | NO | 0 | Learning phase step |
| `isGraduated` | BOOLEAN | NO | false | Graduated from learning |

**Indices:**
- PRIMARY KEY on `id`
- FOREIGN KEY `vocabularyId` REFERENCES `vocabulary(id)`
- INDEX on `nextReviewDate`

---

#### 8. `quotes`

Inspirational quotes for daily wisdom.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | INTEGER | NO | auto | Primary key |
| `text` | TEXT | NO | - | Quote text |
| `author` | TEXT | NO | - | Quote attribution |
| `category` | TEXT | NO | "general" | Thematic category |
| `isFavorite` | BOOLEAN | NO | false | User favorite |
| `shownAsDaily` | BOOLEAN | NO | false | Shown as daily |
| `shownAt` | INTEGER | YES | null | When shown |
| `reflectionCount` | INTEGER | NO | 0 | Times reflected |

**Categories:**
- `stoic`, `philosophical`, `motivational`, `mindfulness`, `growth`, `resilience`

---

#### 9. `proverbs`

Cultural wisdom and proverbs.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | INTEGER | NO | auto | Primary key |
| `text` | TEXT | NO | - | Proverb text |
| `meaning` | TEXT | NO | - | Explanation |
| `origin` | TEXT | NO | "" | Cultural origin |
| `category` | TEXT | NO | "general" | Category |
| `isFavorite` | BOOLEAN | NO | false | User favorite |
| `shownAsDaily` | BOOLEAN | NO | false | Shown as daily |
| `shownAt` | INTEGER | YES | null | When shown |

---

#### 10. `idioms`

Idiomatic expressions with explanations.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | INTEGER | NO | auto | Primary key |
| `expression` | TEXT | NO | - | Idiom text |
| `meaning` | TEXT | NO | - | Explanation |
| `exampleUsage` | TEXT | NO | "" | Usage example |
| `origin` | TEXT | NO | "" | Etymology |
| `category` | TEXT | NO | "general" | Category |
| `isFavorite` | BOOLEAN | NO | false | User favorite |
| `shownAsDaily` | BOOLEAN | NO | false | Shown as daily |
| `shownAt` | INTEGER | YES | null | When shown |

---

#### 11. `phrases`

Useful phrases for communication.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | INTEGER | NO | auto | Primary key |
| `phrase` | TEXT | NO | - | Phrase text |
| `meaning` | TEXT | NO | - | Explanation |
| `context` | TEXT | NO | "" | Usage context |
| `category` | TEXT | NO | "general" | Category |
| `isFavorite` | BOOLEAN | NO | false | User favorite |
| `shownAsDaily` | BOOLEAN | NO | false | Shown as daily |
| `shownAt` | INTEGER | YES | null | When shown |

---

#### 12. `motivational_messages`

AI-generated motivational messages cache.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | INTEGER | NO | auto | Primary key |
| `message` | TEXT | NO | - | Message content |
| `context` | TEXT | NO | - | Context/trigger |
| `mood` | TEXT | YES | null | Associated mood |
| `usedAt` | INTEGER | YES | null | When displayed |
| `createdAt` | INTEGER | NO | now() | Creation timestamp |

---

### Gamification Domain

#### 13. `achievements`

Badge and achievement definitions with progress tracking.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | TEXT | NO | - | Unique achievement ID |
| `name` | TEXT | NO | - | Display name |
| `description` | TEXT | NO | - | Achievement description |
| `iconId` | TEXT | NO | - | Icon asset reference |
| `category` | TEXT | NO | - | Achievement category |
| `requirement` | INTEGER | NO | - | Unlock threshold |
| `currentProgress` | INTEGER | NO | 0 | User's progress |
| `isUnlocked` | BOOLEAN | NO | false | Unlock status |
| `unlockedAt` | INTEGER | YES | null | Unlock timestamp |
| `rewardType` | TEXT | NO | "points" | Reward type |
| `rewardValue` | TEXT | NO | "100" | Reward amount/ID |
| `rarity` | TEXT | NO | "common" | Rarity tier |
| `celebrationMessage` | TEXT | NO | "" | Unlock message |

**Indices:**
- PRIMARY KEY on `id`
- INDEX on `category`
- INDEX on `isUnlocked`
- INDEX on `rarity`

**Categories:**
- `wisdom` - Vocabulary and quote-related
- `reflection` - Journal-related
- `consistency` - Streak-related
- `presence` - Daily engagement
- `temporal` - Future message-related
- `mastery` - Advanced achievements

**Rarity Tiers:**
| Rarity | Points | Drop Rate |
|--------|--------|-----------|
| `common` | 50-100 | 40% |
| `uncommon` | 150-250 | 30% |
| `rare` | 300-500 | 20% |
| `epic` | 750-1000 | 8% |
| `legendary` | 1500+ | 2% |

---

#### 14. `leaderboard`

Global leaderboard entries for social features.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `odId` | TEXT | NO | - | Unique peer ID |
| `displayName` | TEXT | NO | - | Display name |
| `avatarId` | TEXT | NO | "default" | Avatar reference |
| `titleId` | TEXT | NO | "newcomer" | Title reference |
| `totalPoints` | INTEGER | NO | 0 | All-time points |
| `weeklyPoints` | INTEGER | NO | 0 | Weekly points |
| `currentStreak` | INTEGER | NO | 0 | Active streak |
| `rank` | INTEGER | NO | 0 | Current rank |
| `previousRank` | INTEGER | NO | 0 | Previous rank |
| `isCurrentUser` | BOOLEAN | NO | false | Self indicator |
| `lastActiveAt` | INTEGER | NO | now() | Last activity |
| `boostsReceived` | INTEGER | NO | 0 | Boosts received |
| `congratsReceived` | INTEGER | NO | 0 | Congrats received |

**Indices:**
- PRIMARY KEY on `odId`
- INDEX on `totalPoints` DESC
- INDEX on `weeklyPoints` DESC
- INDEX on `rank`

---

#### 15. `peer_interactions`

Social interaction tracking (boosts, congratulations).

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | INTEGER | NO | auto | Primary key |
| `peerId` | TEXT | NO | - | Target peer ID |
| `interactionType` | TEXT | NO | - | Type of interaction |
| `achievementId` | TEXT | YES | null | Related achievement |
| `message` | TEXT | YES | null | Optional message |
| `createdAt` | INTEGER | NO | now() | Interaction timestamp |

**Interaction Types:**
- `boost` - Encouragement boost
- `congrats` - Congratulations
- `milestone` - Milestone celebration

---

#### 16. `challenges`

Community challenge definitions.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | TEXT | NO | - | Unique challenge ID |
| `title` | TEXT | NO | - | Challenge name |
| `description` | TEXT | NO | - | Challenge description |
| `type` | TEXT | NO | - | Challenge type |
| `iconName` | TEXT | NO | "challenge_default" | Icon reference |
| `startDate` | INTEGER | NO | - | Start timestamp |
| `endDate` | INTEGER | NO | - | End timestamp |
| `targetCount` | INTEGER | NO | - | Individual target |
| `currentUserProgress` | INTEGER | NO | 0 | User's progress |
| `isJoined` | BOOLEAN | NO | false | Join status |
| `joinedAt` | INTEGER | YES | null | Join timestamp |
| `totalParticipants` | INTEGER | NO | 0 | Participant count |
| `communityProgress` | INTEGER | NO | 0 | Aggregate progress |
| `communityTarget` | INTEGER | NO | 0 | Community goal |
| `rewardPoints` | INTEGER | NO | 0 | Point reward |
| `rewardBadgeId` | TEXT | YES | null | Badge reward |
| `rewardTitle` | TEXT | YES | null | Title reward |
| `difficulty` | TEXT | NO | "medium" | Difficulty level |
| `isCompleted` | BOOLEAN | NO | false | Completion status |
| `completedAt` | INTEGER | YES | null | Completion time |
| `isFeatured` | BOOLEAN | NO | false | Featured flag |
| `createdAt` | INTEGER | NO | now() | Creation timestamp |

**Indices:**
- PRIMARY KEY on `id`
- INDEX on `startDate`, `endDate`
- INDEX on `isJoined`
- INDEX on `isFeatured`

**Challenge Types:**
- `journaling` - Journal entry challenges
- `vocabulary` - Word learning challenges
- `streak` - Consistency challenges
- `meditation` - Mindfulness challenges
- `mixed` - Multi-activity challenges

---

#### 17. `challenge_milestones`

Challenge milestone definitions.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | TEXT | NO | - | Milestone ID |
| `challengeId` | TEXT | NO | - | Parent challenge |
| `title` | TEXT | NO | - | Milestone name |
| `description` | TEXT | NO | - | Description |
| `targetProgress` | INTEGER | NO | - | Progress threshold |
| `rewardPoints` | INTEGER | NO | 0 | Point reward |
| `iconName` | TEXT | NO | - | Icon reference |
| `isAchieved` | BOOLEAN | NO | false | Achievement status |
| `achievedAt` | INTEGER | YES | null | Achievement time |

---

#### 18. `challenge_participation`

Challenge participation records.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | TEXT | NO | - | Participation ID |
| `odId` | TEXT | NO | - | Participant ID |
| `challengeId` | TEXT | NO | - | Challenge ID |
| `progress` | INTEGER | NO | 0 | Current progress |
| `isCompleted` | BOOLEAN | NO | false | Completion status |
| `completedAt` | INTEGER | YES | null | Completion time |
| `joinedAt` | INTEGER | NO | now() | Join timestamp |

---

#### 19. `challenge_leaderboard`

Challenge-specific leaderboards.

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | TEXT | NO | - | Entry ID |
| `challengeId` | TEXT | NO | - | Challenge ID |
| `odId` | TEXT | NO | - | Participant ID |
| `displayName` | TEXT | NO | - | Display name |
| `avatarId` | TEXT | NO | - | Avatar reference |
| `progress` | INTEGER | NO | 0 | Progress amount |
| `rank` | INTEGER | NO | 0 | Current rank |

---

## Data Access Objects (DAOs)

### Available DAOs

| DAO | Entity | Primary Operations |
|-----|--------|-------------------|
| `JournalDao` | journal_entries | CRUD, search, mood filter, bookmarks |
| `FutureMessageDao` | future_messages | CRUD, delivery queries, scheduling |
| `VocabularyDao` | vocabulary | CRUD, spaced repetition, favorites |
| `VocabularyLearningDao` | vocabulary_learning | SR progress tracking |
| `QuoteDao` | quotes | Random selection, favorites, categories |
| `ProverbDao` | proverbs | Random selection, favorites |
| `IdiomDao` | idioms | Random selection, favorites |
| `PhraseDao` | phrases | Random selection, favorites |
| `UserDao` | user_profile, achievements, stats | Profile CRUD, achievements, stats |
| `ChallengeDao` | challenges, milestones, participation | Challenge management |

---

## Entity Relationships

```
┌──────────────────┐          ┌──────────────────┐
│   user_profile   │──1:N────▶│  journal_entries │
│                  │          └──────────────────┘
│                  │          ┌──────────────────┐
│                  │──1:N────▶│  future_messages │
│                  │          └──────────────────┘
│                  │          ┌──────────────────┐
│                  │──1:N────▶│   achievements   │
│                  │          └──────────────────┘
│                  │          ┌──────────────────┐
│                  │──1:1────▶│    user_stats    │
└──────────────────┘          └──────────────────┘
         │                    ┌──────────────────┐
         └──────────1:N──────▶│  streak_history  │
                              └──────────────────┘

┌──────────────────┐          ┌──────────────────┐
│    vocabulary    │──1:1────▶│ vocab_learning   │
└──────────────────┘          └──────────────────┘

┌──────────────────┐          ┌──────────────────────┐
│    challenges    │──1:N────▶│ challenge_milestones │
│                  │          └──────────────────────┘
│                  │          ┌─────────────────────────┐
│                  │──1:N────▶│ challenge_participation │
│                  │          └─────────────────────────┘
│                  │          ┌───────────────────────┐
│                  │──1:N────▶│ challenge_leaderboard │
└──────────────────┘          └───────────────────────┘

┌──────────────────┐          ┌──────────────────────┐
│   leaderboard    │──1:N────▶│  peer_interactions   │
└──────────────────┘          └──────────────────────┘
```

---

## Migration Strategy

### Development Phase
Currently using `fallbackToDestructiveMigration()` for rapid iteration.

### Production Migration Plan

#### Version 1 → 2 (Planned)
```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add new columns for AI cache
        database.execSQL("""
            ALTER TABLE journal_entries
            ADD COLUMN aiCacheExpiry INTEGER DEFAULT NULL
        """)

        // Create AI response cache table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS ai_response_cache (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                entryId INTEGER NOT NULL,
                response TEXT NOT NULL,
                model TEXT NOT NULL,
                createdAt INTEGER NOT NULL,
                expiresAt INTEGER NOT NULL,
                FOREIGN KEY (entryId) REFERENCES journal_entries(id)
                ON DELETE CASCADE
            )
        """)
    }
}
```

#### Version 2 → 3 (Future)
```kotlin
// Cloud sync preparation
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add sync metadata columns
        database.execSQL("""
            ALTER TABLE user_profile
            ADD COLUMN cloudSyncEnabled INTEGER DEFAULT 0
        """)
        database.execSQL("""
            ALTER TABLE user_profile
            ADD COLUMN lastSyncedAt INTEGER DEFAULT NULL
        """)
        database.execSQL("""
            ALTER TABLE user_profile
            ADD COLUMN syncVersion INTEGER DEFAULT 0
        """)
    }
}
```

---

## Planned Schema Enhancements

### Phase 2: AI Response Caching

```kotlin
@Entity(tableName = "ai_response_cache")
data class AiResponseCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entryId: Long,            // FK to journal_entries
    val promptHash: String,        // Hash of input for deduplication
    val response: String,          // Cached AI response
    val model: String,             // AI model used
    val tokens: Int,               // Token count for cost tracking
    val createdAt: Long,
    val expiresAt: Long            // TTL for cache invalidation
)
```

### Phase 3: Saved Wisdom Collections

```kotlin
@Entity(tableName = "saved_wisdom")
data class SavedWisdomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contentType: String,       // quote, proverb, idiom, vocabulary
    val contentId: Long,           // Reference to content
    val collectionId: Long? = null,// Optional folder/collection
    val note: String = "",         // User's note
    val savedAt: Long
)

@Entity(tableName = "wisdom_collections")
data class WisdomCollectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val iconId: String = "folder",
    val createdAt: Long,
    val itemCount: Int = 0
)
```

### Phase 4: Daily Activity Tracking

```kotlin
@Entity(tableName = "daily_activity")
data class DailyActivityEntity(
    @PrimaryKey
    val date: Long,                // Date (day start timestamp)
    val journalMinutes: Int = 0,
    val vocabularyMinutes: Int = 0,
    val reflectionMinutes: Int = 0,
    val totalMinutes: Int = 0,
    val activitiesJson: String = "[]",  // Detailed activity log
    val moodAverage: Float? = null,
    val entryCount: Int = 0,
    val wordsWritten: Int = 0,
    val wordsLearned: Int = 0
)
```

---

## Performance Considerations

### Indexing Strategy
- All foreign keys are indexed
- Frequently queried columns have indices
- Composite indices for common query patterns

### Query Optimization
- Use `@Transaction` for multi-table operations
- Paginate large result sets with `PagingSource`
- Use `Flow` for reactive updates

### Storage Optimization
- Timestamps as INTEGER (epoch millis)
- Booleans as INTEGER (0/1)
- JSON strings for flexible schema evolution
- BLOB avoided - external files preferred

---

## Best Practices

### 1. Entity Design
```kotlin
// Always include timestamps
val createdAt: Long = System.currentTimeMillis()
val updatedAt: Long = System.currentTimeMillis()

// Use meaningful defaults
val status: String = "active"

// Prefer TEXT for flexible data
val metadata: String = "{}"  // JSON blob
```

### 2. DAO Patterns
```kotlin
// Return Flow for reactive updates
@Query("SELECT * FROM table ORDER BY createdAt DESC")
fun getAll(): Flow<List<Entity>>

// Use suspend for one-shot operations
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun upsert(entity: Entity)

// Batch operations for performance
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun upsertAll(entities: List<Entity>)
```

### 3. Transaction Safety
```kotlin
@Transaction
suspend fun updateUserWithStats(
    profile: UserProfileEntity,
    stats: UserStatsEntity
) {
    updateProfile(profile)
    updateStats(stats)
}
```

---

## Testing

### Unit Testing DAOs
```kotlin
@RunWith(AndroidJUnit4::class)
class JournalDaoTest {
    private lateinit var database: ProdyDatabase
    private lateinit var dao: JournalDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ProdyDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.journalDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndRetrieve() = runTest {
        val entry = JournalEntryEntity(
            content = "Test entry",
            mood = "happy"
        )
        val id = dao.insert(entry)
        val retrieved = dao.getById(id).first()

        assertEquals(entry.content, retrieved?.content)
    }
}
```

---

## Schema Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           PRODY DATABASE SCHEMA                          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   USER DOMAIN                                                            │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ user_profile (PK: id=1)                                         │   │
│   │   ├── displayName, bio, avatarId, bannerId, titleId             │   │
│   │   ├── totalPoints, currentStreak, longestStreak                 │   │
│   │   ├── wordsLearned, journalEntriesCount, futureMessagesCount    │   │
│   │   └── preferences (JSON)                                        │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ journal_entries (PK: id, auto)                                  │   │
│   │   ├── content, mood, moodIntensity, tags                        │   │
│   │   ├── buddhaResponse, isBookmarked, wordCount                   │   │
│   │   └── createdAt, updatedAt                                      │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ future_messages (PK: id, auto)                                  │   │
│   │   ├── title, content, category, attachedGoal                    │   │
│   │   ├── deliveryDate, isDelivered, isRead                         │   │
│   │   └── createdAt, deliveredAt                                    │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│   CONTENT DOMAIN                                                         │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ vocabulary (PK: id, auto, UNIQUE: word)                         │   │
│   │   ├── word, definition, pronunciation, partOfSpeech             │   │
│   │   ├── exampleSentence, synonyms, antonyms, origin               │   │
│   │   ├── difficulty, category, masteryLevel                        │   │
│   │   └── isLearned, isFavorite, shownAsDaily                       │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│   ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐   │
│   │   quotes     │ │  proverbs    │ │   idioms     │ │   phrases    │   │
│   │ PK: id, auto │ │ PK: id, auto │ │ PK: id, auto │ │ PK: id, auto │   │
│   └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘   │
│                                                                          │
│   GAMIFICATION DOMAIN                                                    │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ achievements (PK: id)                                           │   │
│   │   ├── name, description, iconId, category, rarity               │   │
│   │   ├── requirement, currentProgress, isUnlocked, unlockedAt      │   │
│   │   └── rewardType, rewardValue, celebrationMessage               │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ challenges (PK: id)                                             │   │
│   │   ├── title, description, type, difficulty                      │   │
│   │   ├── startDate, endDate, targetCount                           │   │
│   │   ├── communityProgress, communityTarget, totalParticipants     │   │
│   │   └── rewardPoints, rewardBadgeId, rewardTitle                  │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ leaderboard (PK: odId)                                          │   │
│   │   ├── displayName, avatarId, titleId                            │   │
│   │   ├── totalPoints, weeklyPoints, currentStreak                  │   │
│   │   ├── rank, previousRank, isCurrentUser                         │   │
│   │   └── boostsReceived, congratsReceived                          │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1 | 2024-XX-XX | Initial schema with all core entities |

---

*Last Updated: December 2024*
*Schema Version: 1*
*Room Version: 2.6.x*
