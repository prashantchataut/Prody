# Prody Database Schema

## Overview

This document outlines the database schema design for Prody. The app uses Room persistence library for local storage with potential for future cloud synchronization.

## Database Configuration

```kotlin
@Database(
    entities = [
        UserEntity::class,
        JournalEntryEntity::class,
        UserAchievementEntity::class,
        AchievementDefinitionEntity::class,
        FutureMessageEntity::class,
        SavedWisdomEntity::class,
        DailyActivityEntity::class,
        VocabularyEntity::class,
        QuoteEntity::class,
        ProverbEntity::class,
        LeaderboardEntity::class,
        AiCacheEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ProdyDatabase : RoomDatabase()
```

---

## Entity Definitions

### User Entity

Primary user profile and stats storage.

```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,  // UUID or Firebase UID for future auth

    // Profile Information
    val displayName: String,
    val email: String?,  // Nullable until OAuth integration
    val avatarUrl: String?,
    val bio: String?,
    val createdAt: Long,  // Unix timestamp
    val lastActiveAt: Long,

    // Stats
    val totalXp: Int = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalEntries: Int = 0,
    val totalWords: Int = 0,
    val wordsLearned: Int = 0,

    // Customization
    val selectedBannerId: String? = null,
    val selectedBadgeIds: String = "[]",  // JSON array of badge IDs for showcase
    val rankId: String = "seeker",

    // Settings
    val aiEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val darkMode: Int? = null,  // null = system, 0 = light, 1 = dark
    val reminderTime: String? = null  // HH:mm format
)
```

### Journal Entry Entity

User journal entries with AI-generated insights.

```kotlin
@Entity(
    tableName = "journal_entries",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["createdAt"]),
        Index(value = ["mood"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class JournalEntryEntity(
    @PrimaryKey
    val id: String,  // UUID
    val userId: String,

    // Content
    val content: String,
    val wordCount: Int,
    val mood: String?,  // Mood enum name

    // Timestamps
    val createdAt: Long,
    val updatedAt: Long,

    // AI Insights (populated after analysis)
    val primaryEmotion: String? = null,
    val themes: String? = null,  // JSON array of theme strings
    val sentiment: Float? = null,  // -1.0 to 1.0
    val buddhaResponse: String? = null,

    // Metadata
    val promptUsed: String? = null,
    val templateId: String? = null,
    val isDeleted: Boolean = false
)
```

### User Achievement Entity

Tracks unlocked achievements per user.

```kotlin
@Entity(
    tableName = "user_achievements",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["achievementId"])
    ],
    primaryKeys = ["userId", "achievementId"]
)
data class UserAchievementEntity(
    val odId: String,  // Composite key with achievementId
    val odId: String,  // Composite key with odId
    val odId: Long,
    val odId: Boolean = false,  // For special/limited badges
    val odId: Boolean = false,  // If user is showcasing this badge
    val odId: Float? = null  // 0.0 to 1.0 for progress-based achievements
)
```

### Achievement Definition Entity

Static achievement definitions (can be seeded from code).

```kotlin
@Entity(tableName = "achievement_definitions")
data class AchievementDefinitionEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val iconRes: String,  // Resource name for icon
    val category: String,  // Category enum name
    val rarity: String,  // Rarity enum name (COMMON, UNCOMMON, RARE, EPIC, LEGENDARY)
    val requirement: String,  // JSON object describing unlock conditions
    val points: Int = 50,
    val isLimited: Boolean = false,
    val maxHolders: Int? = null,  // For limited achievements
    val startDate: Long? = null,  // For time-limited achievements
    val endDate: Long? = null
)
```

### Future Message Entity

Messages sent to future self.

```kotlin
@Entity(
    tableName = "future_messages",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["deliverAt"]),
        Index(value = ["isDelivered"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FutureMessageEntity(
    @PrimaryKey
    val id: String,
    val userId: String,

    // Content
    val content: String,
    val category: String,  // GOAL, MOTIVATION, PROMISE, GENERAL

    // Timestamps
    val createdAt: Long,
    val deliverAt: Long,

    // Status
    val isDelivered: Boolean = false,
    val deliveredAt: Long? = null,
    val readAt: Long? = null
)
```

### Saved Wisdom Entity

User's saved/favorited wisdom items.

```kotlin
@Entity(
    tableName = "saved_wisdom",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["wisdomType"])
    ]
)
data class SavedWisdomEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val wisdomType: String,  // QUOTE, PROVERB, IDIOM, PHRASE, WORD
    val wisdomId: String,  // Reference to the source item
    val wisdomText: String,  // Cached text for quick display
    val wisdomSource: String?,  // Author/origin
    val savedAt: Long
)
```

### Daily Activity Entity

Tracks daily engagement for streaks and analytics.

```kotlin
@Entity(
    tableName = "daily_activity",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["date"])
    ],
    primaryKeys = ["userId", "date"]
)
data class DailyActivityEntity(
    val userId: String,
    val date: String,  // yyyy-MM-dd format
    val timestamp: Long,  // First activity timestamp

    // Activity flags
    val journalWritten: Boolean = false,
    val wordLearned: Boolean = false,
    val wisdomRead: Boolean = false,
    val futureMessageSent: Boolean = false,

    // Counts
    val entriesCount: Int = 0,
    val wordsWritten: Int = 0,
    val xpEarned: Int = 0,

    // Mood tracking
    val primaryMood: String? = null
)
```

### Vocabulary Entity

Words learned and available for review.

```kotlin
@Entity(
    tableName = "vocabulary",
    indices = [
        Index(value = ["word"]),
        Index(value = ["isLearned"])
    ]
)
data class VocabularyEntity(
    @PrimaryKey
    val id: String,
    val word: String,
    val pronunciation: String?,
    val partOfSpeech: String,
    val definition: String,
    val example: String?,
    val etymology: String?,
    val synonyms: String?,  // JSON array
    val antonyms: String?,  // JSON array

    // User progress
    val isLearned: Boolean = false,
    val learnedAt: Long? = null,
    val reviewCount: Int = 0,
    val lastReviewedAt: Long? = null,

    // Metadata
    val difficulty: Int = 1,  // 1-5 scale
    val category: String? = null
)
```

### Quote Entity

Wisdom quotes for daily inspiration.

```kotlin
@Entity(
    tableName = "quotes",
    indices = [
        Index(value = ["theme"]),
        Index(value = ["author"])
    ]
)
data class QuoteEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val author: String?,
    val source: String?,
    val theme: String?,  // Theme category

    // Display tracking
    val lastShownAt: Long? = null,
    val timesShown: Int = 0,

    // User interaction
    val isFavorite: Boolean = false,
    val favoritedAt: Long? = null
)
```

### Proverb Entity

Proverbs with meanings and origins.

```kotlin
@Entity(tableName = "proverbs")
data class ProverbEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val meaning: String,
    val origin: String?,
    val usage: String?,

    val lastShownAt: Long? = null,
    val timesShown: Int = 0,
    val isFavorite: Boolean = false
)
```

### Leaderboard Entity

User rankings cache.

```kotlin
@Entity(
    tableName = "leaderboard",
    indices = [
        Index(value = ["period"]),
        Index(value = ["rank"])
    ]
)
data class LeaderboardEntity(
    @PrimaryKey
    val odId: String,  // odId format: {odId}_{odId}
    val odId: String,
    val odId: String,
    val odId: String,  // WEEKLY, MONTHLY, ALL_TIME
    val odId: Int,
    val odId: Int,
    val odId: Int,
    val odId: String?,
    val odId: Long  // When this entry was last updated
)
```

### AI Cache Entity

Caches AI responses to reduce API calls.

```kotlin
@Entity(
    tableName = "ai_cache",
    indices = [
        Index(value = ["type"]),
        Index(value = ["expiresAt"])
    ]
)
data class AiCacheEntity(
    @PrimaryKey
    val key: String,  // Hash of input parameters
    val type: String,  // WISDOM, SUMMARY, INSIGHT, PROMPT, BUDDHA_RESPONSE
    val input: String,  // Cached input for validation
    val response: String,  // JSON response
    val createdAt: Long,
    val expiresAt: Long?,  // null = permanent cache
    val hitCount: Int = 0
)
```

---

## Relationships Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                            USER                                      │
│  id, displayName, email, avatarUrl, stats, settings                 │
└─────────────────────────────────────────────────────────────────────┘
         │           │           │           │           │
         │           │           │           │           │
         ▼           ▼           ▼           ▼           ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│  JOURNAL    │ │ ACHIEVEMENT │ │   FUTURE    │ │   SAVED     │ │   DAILY     │
│  ENTRIES    │ │ (unlocked)  │ │  MESSAGES   │ │  WISDOM     │ │  ACTIVITY   │
└─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘
                      │
                      ▼
              ┌─────────────┐
              │ ACHIEVEMENT │
              │ DEFINITIONS │
              │  (static)   │
              └─────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                    CONTENT (Independent)                            │
├─────────────┬─────────────┬─────────────┬─────────────┬─────────────┤
│ VOCABULARY  │   QUOTES    │  PROVERBS   │ LEADERBOARD │  AI_CACHE   │
└─────────────┴─────────────┴─────────────┴─────────────┴─────────────┘
```

---

## Type Converters

```kotlin
class Converters {
    @TypeConverter
    fun fromStringList(value: String): List<String> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        return Json.encodeToString(list)
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
```

---

## Migration Strategy

### Version 1 (Initial Release)
- Base schema with all entities
- Seed data for achievements, vocabulary, quotes, proverbs

### Future Migrations

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add new columns or tables
        database.execSQL(
            "ALTER TABLE users ADD COLUMN newColumn TEXT DEFAULT NULL"
        )
    }
}

// In database builder
Room.databaseBuilder(context, ProdyDatabase::class.java, "prody.db")
    .addMigrations(MIGRATION_1_2)
    .build()
```

---

## DAOs Overview

### UserDao
- `getUserById(id: String): Flow<UserEntity?>`
- `insertUser(user: UserEntity)`
- `updateUser(user: UserEntity)`
- `updateStats(id: String, xp: Int, level: Int, streak: Int)`

### JournalDao
- `getEntriesByUser(userId: String): Flow<List<JournalEntryEntity>>`
- `getEntryById(id: String): Flow<JournalEntryEntity?>`
- `insertEntry(entry: JournalEntryEntity)`
- `updateEntry(entry: JournalEntryEntity)`
- `softDeleteEntry(id: String)`
- `getEntriesCount(userId: String): Flow<Int>`
- `getWordCount(userId: String): Flow<Int>`

### AchievementDao
- `getUnlockedAchievements(userId: String): Flow<List<UserAchievementEntity>>`
- `getAllDefinitions(): Flow<List<AchievementDefinitionEntity>>`
- `unlockAchievement(userId: String, achievementId: String)`
- `getShowcaseAchievements(userId: String): Flow<List<UserAchievementEntity>>`

### FutureMessageDao
- `getMessagesByUser(userId: String): Flow<List<FutureMessageEntity>>`
- `getPendingMessages(userId: String): Flow<List<FutureMessageEntity>>`
- `getDeliverableMessages(currentTime: Long): List<FutureMessageEntity>`
- `insertMessage(message: FutureMessageEntity)`
- `markAsDelivered(id: String, deliveredAt: Long)`
- `markAsRead(id: String, readAt: Long)`

### VocabularyDao
- `getAllWords(): Flow<List<VocabularyEntity>>`
- `getLearnedWords(): Flow<List<VocabularyEntity>>`
- `getWordOfDay(): VocabularyEntity?`
- `markAsLearned(id: String, learnedAt: Long)`

### DailyActivityDao
- `getActivityForDate(userId: String, date: String): DailyActivityEntity?`
- `insertOrUpdateActivity(activity: DailyActivityEntity)`
- `getCurrentStreak(userId: String): Int`
- `getActivityHistory(userId: String, days: Int): List<DailyActivityEntity>`

---

## Indexing Strategy

### Performance Indices
1. **Journal Entries**: `userId`, `createdAt`, `mood` - for filtering and sorting
2. **Achievements**: `userId`, `achievementId` - composite for lookups
3. **Future Messages**: `userId`, `deliverAt`, `isDelivered` - for delivery queries
4. **Daily Activity**: `userId`, `date` - composite for date-based lookups
5. **Leaderboard**: `period`, `rank` - for period-based rankings

### Full-Text Search (Future)
```kotlin
@Fts4(contentEntity = JournalEntryEntity::class)
@Entity(tableName = "journal_entries_fts")
data class JournalEntryFts(
    @ColumnInfo(name = "content")
    val content: String
)
```

---

## Data Seeding

Initial content is seeded from `InitialContentData.kt`:
- 100+ vocabulary words
- 50+ quotes
- 30+ proverbs
- All achievement definitions
- Default user profile template

---

## Backup & Export

### Export Format (JSON)
```json
{
  "version": 1,
  "exportedAt": "2024-01-01T00:00:00Z",
  "user": { ... },
  "journalEntries": [ ... ],
  "achievements": [ ... ],
  "futureMessages": [ ... ],
  "savedWisdom": [ ... ],
  "dailyActivity": [ ... ]
}
```

### Import Strategy
1. Validate JSON structure
2. Check version compatibility
3. Merge or replace based on user preference
4. Recalculate derived stats
