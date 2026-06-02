# Prody Full Wiring Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Wire Prody's remaining stubs — Firebase Auth, real leaderboard data, functional sync — and polish every screen for production readiness.

**Architecture:** Offline-first with Firebase Auth for identity, Room for all local data, and SyncManager for eventual cloud sync. Auth provides user identity; all data remains local-first with Room. Leaderboard shifts from demo data to real user stats with local-only ranking (no backend leaderboard yet — that's a future Phase).

**Tech Stack:** Kotlin, Jetpack Compose, Hilt DI, Room, Firebase Auth (Google Sign-In), Coroutines/Flow

---

## Phase 1: Firebase Auth (Identity Foundation)

Everything depends on user identity. Currently `userId = "local"` everywhere. This phase creates real auth.

### Task 1.1: Add Firebase Dependencies

**Files:**
- Modify: `app/build.gradle.kts`
- Modify: `build.gradle.kts` (root)
- Create: `app/google-services.json` (user-provided)

**Step 1:** Add Firebase BOM and Auth dependency to `app/build.gradle.kts`:

```kotlin
// In dependencies block:
implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
implementation("com.google.firebase:firebase-auth")
implementation("com.google.android.gms:play-services-auth:21.3.0")
```

**Step 2:** Add Google Services plugin to root `build.gradle.kts`:

```kotlin
// In plugins block:
id("com.google.gms.google-services") version "4.4.2" apply false
```

**Step 3:** Add Google Services plugin to `app/build.gradle.kts`:

```kotlin
// In plugins block:
id("com.google.gms.google-services")
```

**Step 4:** User places `google-services.json` in `app/` directory.

**Step 5:** Build and verify no compile errors.

---

### Task 1.2: Create Auth Infrastructure

**Files:**
- Create: `app/src/main/java/com/prody/prashant/data/auth/AuthRepository.kt`
- Create: `app/src/main/java/com/prody/prashant/data/auth/AuthState.kt`
- Modify: `app/src/main/java/com/prody/prashant/di/AppModule.kt`

**AuthState.kt:**

```kotlin
package com.prody.prashant.data.auth

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Authenticated(val userId: String, val email: String?, val displayName: String?, val photoUrl: String?) : AuthState()
    data object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}
```

**AuthRepository.kt:**

```kotlin
package com.prody.prashant.data.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    companion object {
        private const val TAG = "AuthRepository"
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentUser: FirebaseUser? get() = firebaseAuth.currentUser
    val currentUserId: String? get() = currentUser?.uid

    init {
        firebaseAuth.addAuthStateListener { auth ->
            val user = auth.currentUser
            _authState.value = if (user != null) {
                AuthState.Authenticated(
                    userId = user.uid,
                    email = user.email,
                    displayName = user.displayName,
                    photoUrl = user.photoUrl?.toString()
                )
            } else {
                AuthState.Unauthenticated
            }
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("No user ID"))
            Log.i(TAG, "Google sign-in successful: $userId")
            Result.success(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Google sign-in failed", e)
            _authState.value = AuthState.Error(e.message ?: "Sign-in failed")
            Result.failure(e)
        }
    }

    suspend fun signInAnonymously(): Result<String> {
        return try {
            val result = firebaseAuth.signInAnonymously().await()
            val userId = result.user?.uid ?: return Result.failure(Exception("No user ID"))
            Log.i(TAG, "Anonymous sign-in successful: $userId")
            Result.success(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Anonymous sign-in failed", e)
            _authState.value = AuthState.Error(e.message ?: "Sign-in failed")
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        try {
            firebaseAuth.signOut()
            Log.i(TAG, "Sign-out successful")
        } catch (e: Exception) {
            Log.e(TAG, "Sign-out failed", e)
        }
    }

    fun isSignedIn(): Boolean = firebaseAuth.currentUser != null
}
```

**AppModule.kt additions:**

```kotlin
// In provides section:
@Provides
@Singleton
fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

@Provides
@Singleton
fun provideAuthRepository(firebaseAuth: FirebaseAuth): AuthRepository = AuthRepository(firebaseAuth)
```

---

### Task 1.3: Create AuthViewModel and Login/Signup Screen

**Files:**
- Create: `app/src/main/java/com/prody/prashant/ui/screens/auth/AuthViewModel.kt`
- Create: `app/src/main/java/com/prody/prashant/ui/screens/auth/AuthScreen.kt`
- Modify: `app/src/main/java/com/prody/prashant/ui/navigation/ProdyNavigation.kt` (add auth route)
- Modify: `app/src/main/java/com/prody/prashant/ui/navigation/Screen.kt` (add auth screen route)
- Modify: `app/src/main/java/com/prody/prashant/MainActivity.kt` (observe auth state)

**AuthViewModel.kt:** Hilt-injected ViewModel that observes AuthRepository.authState, provides signInWithGoogle(), signInAnonymously(), signOut() methods.

**AuthScreen.kt:** Compose screen with:
- Prody-branded Google Sign-In button (green accent, not default Google blue)
- "Continue without account" button for anonymous sign-in
- Loading state with shimmer
- Error state with retry
- Uses ProdyTheme, LocalHavenColors, LocalMoodColors

**Navigation changes:** Add `Screen.Auth` route. MainActivity observes authState and shows AuthScreen when Unauthenticated, ProdyApp when Authenticated.

---

### Task 1.4: Migrate userId from "local" to Firebase UID

**Files:**
- Modify: All DAOs that default `userId = "local"` — add migration to update existing data
- Modify: `ProdyDatabase.kt` — add MIGRATION_23_24 that updates `userId` from "local" to the Firebase UID
- Modify: `OnboardingViewModel.kt` — use Firebase user ID instead of random UUID
- Modify: `HomeViewModel.kt` — use current user ID from AuthRepository

This is the most delicate task. The migration needs to:
1. Create a DataStore key for `currentUserId`
2. On first auth, update all `userId = "local"` rows to the Firebase UID
3. All new data uses the Firebase UID

**Migration SQL:**
```sql
UPDATE journal_entries SET userId = :newUserId WHERE userId = 'local';
UPDATE future_messages SET userId = :newUserId WHERE userId = 'local';
-- ... for all entities with userId
```

---

## Phase 2: Leaderboard Real Data

### Task 2.1: Replace Seeded Demo Data with Real User Stats

**Files:**
- Modify: `app/src/main/java/com/prody/prashant/ui/screens/stats/StatsViewModel.kt`
- Create: `app/src/main/java/com/prody/prashant/data/local/dao/LeaderboardDao.kt`
- Modify: `app/src/main/java/com/prody/prashant/di/AppModule.kt` (add LeaderboardDao)
- Modify: `app/src/main/java/com/prody/prashant/data/local/database/ProdyDatabase.kt` (add LeaderboardDao)

**Changes:**
1. Remove `createSampleLeaderboard()` method from StatsViewModel
2. Create `LeaderboardDao` with queries that compute rankings from real user data (journal entries, streaks, XP)
3. The leaderboard shows the current user's position among local milestones (not fake users)
4. If no other users exist (single-user app), show personal records and milestones instead of a competitive leaderboard

---

## Phase 3: SyncManager Real Implementation

### Task 3.1: Wire SyncManager to Real Repositories

**Files:**
- Modify: `app/src/main/java/com/prody/prashant/data/sync/SyncManager.kt`
- Modify: `app/src/main/java/com/prody/prashant/di/AppModule.kt`

**Changes:**
1. Inject `JournalRepository`, `VocabularyRepository`, `UserProfileRepository` into SyncManager
2. Replace stub methods with actual repository calls:
   - `syncJournalEntry(id)` → `journalRepository.syncToRemote(entry)`
   - `updateJournalEntry(id)` → `journalRepository.updateRemote(entry)`
   - `deleteJournalEntry(id)` → `journalRepository.deleteRemote(id)`
   - `syncVocabulary(id)` → `vocabularyRepository.syncToRemote(vocab)`
3. Since there's no remote backend yet, these calls should:
   - Mark entries as `syncStatus = "synced"` locally
   - Log the sync attempt
   - Return `SyncResult.Success`
   - This makes the offline-first pattern work: data is always available locally, and when a backend is added later, only the repository implementations need to change

---

## Phase 4: Screen Polish (Per-Task)

### Task 4.1: Home Screen — Wire Real Streak Data

The HomeScreen is already fully wired. Verify:
- DualStreakManager provides real streak data
- Wisdom/quote of the day uses real DAO data
- Quick actions navigate to real screens
- Greeting uses real user name from profile

### Task 4.2: Stats Screen — Real Leaderboard

Replace demo leaderboard with:
- Personal stats (real XP, real streak, real journal count)
- Achievement progress (real unlocked vs total)
- Weekly growth chart (real data from journal entries)
- Personal records section (longest streak, most words, etc.)

### Task 4.3: Profile Screen — Auth Integration

Add to ProfileScreen:
- Sign-in status display (email, name, avatar from Firebase)
- "Sign in with Google" button if anonymous
- "Sign out" option if authenticated
- Account section in settings

### Task 4.4: Haven Screen — Verify Real Data Flow

Already fully wired. Verify:
- Haven conversations are encrypted and persisted
- AI service fallback works (Gemini → OpenRouter → local)
- Crisis detection triggers resources
- Exercise completion is tracked

---

## Checkpoint: After All Phases

- [ ] All tests pass: `./gradlew test`
- [ ] App builds: `./gradlew assembleDebug`
- [ ] Auth flow works: anonymous → Google Sign-In → sign out
- [ ] All screens display real data
- [ ] Leaderboard shows personal stats, not fake users
- [ ] SyncManager marks data as synced locally
- [ ] No crashes on fresh install or returning user

## Risks and Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| Firebase Auth setup requires google-services.json | Blocks auth | User provides this file; anonymous auth works without it |
| Migration from "local" userId to Firebase UID | Data loss if migration fails | Migration runs in transaction; fallback keeps "local" |
| No remote backend for sync | Sync is local-only for now | SyncManager marks data as synced; backend can be added later |
| Leaderboard has no other users | Competitive aspect is missing | Show personal records and milestones instead |

## Open Questions

1. What Firebase project should google-services.json point to? (User provides)
2. Should anonymous auth be allowed long-term, or force Google Sign-In? (Recommend: allow anonymous, prompt upgrade)
3. Is there a backend API planned for sync, or is this purely offline-first for now? (Recommend: offline-first for this phase)