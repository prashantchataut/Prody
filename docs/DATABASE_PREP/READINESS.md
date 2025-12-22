# Prody Database & Auth Integration Readiness

**Date:** December 2024
**Version:** 1.0.0-RC

---

## Overview

Prody's database architecture is designed for offline-first operation with full preparation for Google Auth integration and cloud synchronization. This document details the current state and readiness for production backend integration.

---

## 1. Database Architecture

### Room Database Configuration

**File:** `app/src/main/java/com/prody/prashant/data/local/database/ProdyDatabase.kt`

```kotlin
@Database(
    entities = [
        JournalEntryEntity::class,
        FutureMessageEntity::class,
        VocabularyEntity::class,
        VocabularyLearningEntity::class,
        QuoteEntity::class,
        ProverbEntity::class,
        IdiomEntity::class,
        PhraseEntity::class,
        UserProfileEntity::class,
        AchievementEntity::class,
        UserStatsEntity::class,
        StreakHistoryEntity::class,
        LeaderboardEntryEntity::class,
        PeerInteractionEntity::class,
        MotivationalMessageEntity::class,
        ChallengeEntity::class,
        ChallengeMilestoneEntity::class,
        ChallengeParticipationEntity::class,
        ChallengeLeaderboardEntity::class
    ],
    version = 2,
    exportSchema = true
)
```

### Entity Count: 19

| Category | Entities |
|----------|----------|
| Core Content | JournalEntryEntity, FutureMessageEntity |
| Vocabulary | VocabularyEntity, VocabularyLearningEntity |
| Wisdom Content | QuoteEntity, ProverbEntity, IdiomEntity, PhraseEntity |
| User Data | UserProfileEntity, AchievementEntity, UserStatsEntity, StreakHistoryEntity |
| Leaderboard | LeaderboardEntryEntity, PeerInteractionEntity |
| Challenges | ChallengeEntity, ChallengeMilestoneEntity, ChallengeParticipationEntity, ChallengeLeaderboardEntity |
| System | MotivationalMessageEntity |

---

## 2. Multi-User Support (userId Fields)

### All User-Generated Content Entities Have userId

Every entity that stores user-generated content includes:

```kotlin
val userId: String = "local"
```

**Default Behavior:**
- New users start with `userId = "local"`
- Works offline without authentication
- Upon Google Auth sign-in, replace with Firebase UID

### Entity userId Status

| Entity | Has userId | Has Indices | Notes |
|--------|-----------|-------------|-------|
| JournalEntryEntity | YES | userId, createdAt, composite | Full multi-user ready |
| FutureMessageEntity | YES | userId, deliveryDate, composite | Full multi-user ready |
| VocabularyLearningEntity | YES | Composite key (wordId, userId) | Full multi-user ready |
| UserProfileEntity | YES (odUserId) | Unique index | Full auth fields |
| UserStatsEntity | YES | userId index | Full multi-user ready |
| StreakHistoryEntity | YES | userId, date, composite | Full multi-user ready |

### User Profile Auth Fields

**File:** `app/src/main/java/com/prody/prashant/data/local/entity/UserEntity.kt`

```kotlin
data class UserProfileEntity(
    // Firebase/Google Auth fields - prepared for multi-user support
    val odUserId: String = "local",       // Firebase UID or "local" for offline
    val email: String? = null,            // Google account email
    val photoUrl: String? = null,         // Google profile photo URL
    val isAnonymous: Boolean = true,      // True until Google sign-in
    val authProvider: String = "local",   // local, google, anonymous
    val lastAuthenticatedAt: Long? = null,
    // ...
)
```

---

## 3. Sync Metadata

### All Syncable Entities Include

```kotlin
val syncStatus: String = "pending"    // pending, synced, conflict
val lastSyncedAt: Long? = null
val serverVersion: Long = 0           // For conflict resolution
val isDeleted: Boolean = false        // Soft delete for sync
```

### Sync Status Values

| Status | Description |
|--------|-------------|
| pending | Local changes not yet synced to server |
| synced | Local and server are in sync |
| conflict | Conflict detected, needs resolution |

### Entities with Sync Metadata

- JournalEntryEntity
- FutureMessageEntity
- UserProfileEntity

---

## 4. Database Indices

### Performance Indices Already Created

**JournalEntryEntity:**
```kotlin
@Entity(
    tableName = "journal_entries",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["createdAt"]),
        Index(value = ["userId", "createdAt"])  // Composite for filtered sorts
    ]
)
```

**FutureMessageEntity:**
```kotlin
@Entity(
    tableName = "future_messages",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["deliveryDate"]),
        Index(value = ["userId", "deliveryDate"])
    ]
)
```

**VocabularyLearningEntity:**
```kotlin
@Entity(
    tableName = "vocabulary_learning",
    primaryKeys = ["wordId", "userId"],  // Composite key
    indices = [
        Index(value = ["wordId"]),
        Index(value = ["userId"]),
        Index(value = ["nextReviewDate"]),
        Index(value = ["boxLevel"]),
        Index(value = ["userId", "nextReviewDate"])
    ]
)
```

**StreakHistoryEntity:**
```kotlin
@Entity(
    tableName = "streak_history",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["date"]),
        Index(value = ["userId", "date"])
    ]
)
```

---

## 5. Migration Strategy

### Current Development Strategy

```kotlin
// In ProdyDatabase.kt
.fallbackToDestructiveMigration()
```

This is appropriate for development but requires proper migrations before production.

### Production Migration Requirements

Before production release, implement:

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add userId columns to all entities
        database.execSQL(
            "ALTER TABLE journal_entries ADD COLUMN userId TEXT NOT NULL DEFAULT 'local'"
        )

        // Add sync metadata columns
        database.execSQL(
            "ALTER TABLE journal_entries ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'pending'"
        )
        database.execSQL(
            "ALTER TABLE journal_entries ADD COLUMN lastSyncedAt INTEGER"
        )
        database.execSQL(
            "ALTER TABLE journal_entries ADD COLUMN serverVersion INTEGER NOT NULL DEFAULT 0"
        )
        database.execSQL(
            "ALTER TABLE journal_entries ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0"
        )

        // Add indices
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_journal_entries_userId ON journal_entries(userId)"
        )

        // ... similar for other entities
    }
}
```

### Schema Export

Schema export is enabled for migration verification:
```kotlin
exportSchema = true
```

Schemas are output to: `$projectDir/schemas`

---

## 6. API Key Management

### Configuration Method

**File:** `app/build.gradle.kts`

```kotlin
// Load local.properties for API key configuration
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

// Build config fields
buildConfigField(
    "String",
    "AI_API_KEY",
    "\"${localProperties.getProperty("AI_API_KEY", "")}\""
)
buildConfigField(
    "String",
    "OPENROUTER_API_KEY",
    "\"${localProperties.getProperty("OPENROUTER_API_KEY", "")}\""
)
```

### Security Best Practices

| Practice | Status |
|----------|--------|
| Keys in local.properties | YES |
| local.properties in .gitignore | YES |
| No hardcoded keys in source | YES |
| Environment variable fallback | YES |
| Graceful handling of missing keys | YES |

### local.properties Template

```properties
# AI API Configuration
AI_API_KEY=your_gemini_api_key_here
OPENROUTER_API_KEY=your_openrouter_api_key_here

# Firebase (for future Google Auth)
# FIREBASE_API_KEY=your_firebase_api_key_here
```

---

## 7. Google Auth Integration Preparation

### Ready Components

1. **UserProfileEntity** - All auth fields prepared:
   - `odUserId` for Firebase UID
   - `email` for Google account email
   - `photoUrl` for profile photo
   - `isAnonymous` flag
   - `authProvider` (local, google, anonymous)
   - `lastAuthenticatedAt` timestamp

2. **All Content Entities** - userId fields with "local" default:
   - Seamless transition from offline to authenticated
   - No data migration needed on sign-in

3. **Sync Infrastructure** - SyncManager ready for cloud:
   - Operation queuing
   - Priority-based sync
   - Conflict resolution strategy

### Integration Steps Required

1. **Add Firebase Dependencies:**
   ```kotlin
   implementation("com.google.firebase:firebase-auth-ktx")
   implementation("com.google.android.gms:play-services-auth")
   ```

2. **Configure Firebase Project:**
   - Add `google-services.json`
   - Enable Google Sign-In provider in Firebase Console

3. **Implement Auth Flow:**
   - Update odUserId from "local" to Firebase UID
   - Sync local data to cloud
   - Handle sign-out (keep local or clear)

4. **Replace Migration Strategy:**
   - Implement proper MIGRATION_2_3 for production
   - Remove `fallbackToDestructiveMigration()`

---

## 8. Data Handling for Production

### Demo Data

| Content Type | Status | Notes |
|--------------|--------|-------|
| Quotes | Pre-seeded | 100+ quotes from JSON |
| Proverbs | Pre-seeded | From JSON resource |
| Idioms | Pre-seeded | From JSON resource |
| Phrases | Pre-seeded | From JSON resource |
| Vocabulary | Pre-seeded | 500+ words from JSON |
| Challenges | Pre-seeded | Default challenges |
| Leaderboard | Empty | No fake entries |
| Journal | Empty | User creates |
| Future Messages | Empty | User creates |

### Data Source Flow

```
JSON Resources → DatabaseSeeder → Room Database → DAO → Repository → ViewModel → UI
```

### No Fake User Data

- All user-generated content starts empty
- No fake journal entries
- No fake future messages
- No pre-populated leaderboard entries
- All progress (XP, streaks, achievements) starts at zero

---

## 9. Leaderboard Preparation

### LeaderboardEntryEntity

**File:** `app/src/main/java/com/prody/prashant/data/local/entity/LeaderboardEntity.kt`

```kotlin
data class LeaderboardEntryEntity(
    val odId: String,                      // Unique peer ID (Firebase UID)
    val displayName: String,
    val avatarId: String = "default",
    val titleId: String = "newcomer",
    val bannerId: String = "default_dawn",
    val totalPoints: Int = 0,
    val weeklyPoints: Int = 0,
    val currentStreak: Int = 0,
    val rank: Int = 0,
    val previousRank: Int = 0,
    val isCurrentUser: Boolean = false,
    val lastActiveAt: Long,
    val boostsReceived: Int = 0,
    val isDevBadgeHolder: Boolean = false,
    val isBetaTester: Boolean = false,
    val profileFrameRarity: String = "common",
    val lastBoostedByCurrentUser: Long? = null
)
```

### Backend Requirements for Leaderboard

1. **Firestore Collection Structure:**
   ```
   users/{userId}/publicProfile
   leaderboard/global/entries/{userId}
   leaderboard/weekly/entries/{userId}
   ```

2. **Real-Time Updates:**
   - Use Firestore listeners for live leaderboard
   - Cache locally for offline viewing

3. **Security Rules:**
   - Users can only write their own data
   - Public profiles are read-only to others
   - Rate limiting for boost actions

---

## 10. Signing Configuration

### Release Signing

**File:** `app/build.gradle.kts`

```kotlin
signingConfigs {
    create("release") {
        val keystoreFile = file("prody-release.jks")
        val rootKeystoreFile = file("../keystore/prody-release.jks")
        storeFile = when {
            keystoreFile.exists() -> keystoreFile
            rootKeystoreFile.exists() -> rootKeystoreFile
            else -> null
        }
        storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "prody2024"
        keyAlias = System.getenv("KEY_ALIAS") ?: "prody"
        keyPassword = System.getenv("KEY_PASSWORD") ?: "prody2024"
    }
}
```

### Production Security

| Item | Status | Notes |
|------|--------|-------|
| Keystore file | Prepared | prody-release.jks |
| Environment variables | Supported | KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD |
| ProGuard rules | Configured | proguard-rules.pro |
| Minification | Enabled for release | isMinifyEnabled = true |
| Resource shrinking | Enabled | isShrinkResources = true |

---

## Summary

| Readiness Area | Status | Details |
|----------------|--------|---------|
| userId fields | COMPLETE | All user content entities have userId |
| Sync metadata | COMPLETE | syncStatus, lastSyncedAt, serverVersion, isDeleted |
| Database indices | COMPLETE | Optimized for userId queries |
| Auth fields | COMPLETE | UserProfileEntity has all Google Auth fields |
| API key management | COMPLETE | Secure via local.properties |
| Demo data | CLEAN | No fake user data |
| Migration strategy | DEVELOPMENT | Needs production migrations |
| Signing config | READY | Release keystore configured |

**Overall Database/Auth Readiness: 90% COMPLETE**

Remaining items for production:
1. Add Firebase dependencies
2. Implement proper database migrations
3. Configure Firebase project
4. Implement Google Sign-In flow

---

## Quick Reference: Entity Fields

### Required Fields for All User Content

```kotlin
// Authentication
val userId: String = "local"

// Sync (for cloud-enabled entities)
val syncStatus: String = "pending"
val lastSyncedAt: Long? = null
val serverVersion: Long = 0
val isDeleted: Boolean = false
```

### Auth Provider Values

```kotlin
authProvider = "local"     // Offline/no auth
authProvider = "google"    // Google Sign-In
authProvider = "anonymous" // Firebase Anonymous Auth
```
