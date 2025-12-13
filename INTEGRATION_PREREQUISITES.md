# Prody Integration Prerequisites Guide

> Complete documentation for setting up all required APIs, services, and dependencies for the Prody Android application.

**Last Updated**: December 13, 2024
**App Version**: 1.0.0
**Target SDK**: 35 (Android 15)
**Minimum SDK**: 24 (Android 7.0)

---

## Table of Contents

1. [Overview](#overview)
2. [Development Environment Setup](#development-environment-setup)
3. [Google Gemini AI Integration](#google-gemini-ai-integration)
4. [Build Configuration](#build-configuration)
5. [Required Permissions](#required-permissions)
6. [Third-Party Dependencies](#third-party-dependencies)
7. [Local Storage Configuration](#local-storage-configuration)
8. [Release Configuration](#release-configuration)
9. [Testing Setup](#testing-setup)
10. [Troubleshooting](#troubleshooting)
11. [Best Practices](#best-practices)

---

## Overview

Prody is a personal growth companion app featuring AI-powered wisdom guidance, vocabulary learning with spaced repetition, mood tracking, and gamification. The app operates primarily offline with optional AI features requiring Google Gemini API integration.

### Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                         Prody App                           │
├─────────────────────────────────────────────────────────────┤
│  UI Layer (Jetpack Compose)                                 │
│  ├── Screens (Home, Journal, Vocabulary, etc.)              │
│  └── ViewModels (Hilt-injected)                             │
├─────────────────────────────────────────────────────────────┤
│  Domain Layer                                               │
│  ├── Models & Entities                                      │
│  └── Business Logic (SpacedRepetition, MoodAnalytics)       │
├─────────────────────────────────────────────────────────────┤
│  Data Layer                                                 │
│  ├── Room Database (Local)                                  │
│  ├── DataStore Preferences                                  │
│  └── GeminiService (External API)                           │
└─────────────────────────────────────────────────────────────┘
```

### External Service Dependencies

| Service | Provider | Required | Purpose |
|---------|----------|----------|---------|
| Gemini AI | Google | Optional | Buddha AI mentor, wisdom generation |

---

## Development Environment Setup

### System Requirements

```yaml
Operating System: Windows 10+, macOS 11+, or Linux (Ubuntu 20.04+)
RAM: 16GB recommended (8GB minimum)
Storage: 20GB free space for SDK and build artifacts
Java: JDK 17 (OpenJDK or Oracle JDK)
```

### Required Software

1. **Android Studio Hedgehog (2023.1.1)** or newer
   - Download: https://developer.android.com/studio
   - Required plugins: Kotlin, Android SDK 35

2. **JDK 17**
   ```bash
   # Verify installation
   java -version
   # Should show: openjdk version "17.x.x"
   ```

3. **Android SDK Components**
   - Android SDK Platform 35
   - Android SDK Build-Tools 35.0.0
   - Android SDK Platform-Tools
   - Android Emulator (for testing)

### Project Setup

```bash
# Clone the repository
git clone <repository-url>
cd prody

# Sync Gradle dependencies
./gradlew --refresh-dependencies

# Build debug variant
./gradlew assembleDebug
```

### IDE Configuration

In Android Studio, configure the following:

1. **File > Settings > Build, Execution, Deployment > Build Tools > Gradle**
   - Gradle JDK: 17
   - Gradle Build: Gradle wrapper

2. **File > Settings > Editor > Code Style > Kotlin**
   - Import scheme from `.editorconfig` if available

---

## Google Gemini AI Integration

### Overview

The Buddha AI mentor feature uses Google's Gemini API for generating personalized wisdom responses, daily thoughts, and weekly reflections.

### Step 1: Obtain API Key

1. Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Sign in with your Google account
3. Click "Create API Key"
4. Select or create a Google Cloud project
5. Copy the generated API key

**Important Security Notes:**
- Never commit API keys to version control
- API keys are stored encrypted on user devices via DataStore
- Each user provides their own API key in app settings

### Step 2: API Key Configuration

Users configure their API key within the app:

```kotlin
// App Settings Screen
// Navigate: Profile > Settings > AI Settings > Enter API Key
```

The key is stored in Android DataStore:

```kotlin
// Storage location: /data/data/com.prody.prashant/files/datastore/prody_preferences
// Key: "gemini_api_key"
// Encryption: Android Encrypted DataStore
```

### Step 3: Available Models

The app supports multiple Gemini models:

| Model | ID | Best For | Rate Limit (Free) |
|-------|------|----------|-------------------|
| Gemini 1.5 Flash | `gemini-1.5-flash` | Fast, general use (default) | 15 RPM |
| Gemini 1.5 Flash 8B | `gemini-1.5-flash-8b` | Fastest response time | 15 RPM |
| Gemini 1.5 Pro | `gemini-1.5-pro` | Complex reasoning | 2 RPM |
| Gemini 1.0 Pro | `gemini-1.0-pro` | Stable, legacy | 15 RPM |

### Step 4: Usage in Code

```kotlin
// GeminiService initialization (handled automatically by AppModule)
@Singleton
class GeminiService @Inject constructor() {

    fun initialize(apiKey: String, model: GeminiModel = GeminiModel.GEMINI_1_5_FLASH) {
        // Model configuration
    }

    suspend fun generateJournalResponse(
        content: String,
        mood: Mood,
        moodIntensity: Int,
        wordCount: Int
    ): GeminiResult<String>
}
```

### API Configuration Parameters

```kotlin
// Generation configuration
temperature = 0.9f    // Creativity level (0.0-1.0)
topK = 40             // Token selection diversity
topP = 0.95f          // Nucleus sampling threshold
maxOutputTokens = 1024 // Maximum response length

// Safety settings (all set to MEDIUM_AND_ABOVE)
- HarmCategory.HARASSMENT
- HarmCategory.HATE_SPEECH
- HarmCategory.SEXUALLY_EXPLICIT
- HarmCategory.DANGEROUS_CONTENT
```

### Error Handling

The app handles common API errors:

| Error Type | User Message |
|------------|--------------|
| Invalid API Key | "Invalid API key. Please check your Gemini API key in settings." |
| Quota Exceeded | "API quota exceeded. Please try again later." |
| Network Error | "Network error. Please check your internet connection." |
| Safety Block | "Response was blocked by safety filters." |
| Timeout | "Request timed out. Please try again." |

---

## Build Configuration

### Gradle Properties

**File: `gradle.properties`**

```properties
# Performance optimization
org.gradle.jvmargs=-Xmx4096m
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configuration-cache=true

# Android configuration
android.useAndroidX=true
android.nonTransitiveRClass=true
android.defaults.buildfeatures.buildconfig=true

# Kotlin configuration
kotlin.code.style=official
```

### Build Variants

| Variant | ApplicationId Suffix | Features |
|---------|---------------------|----------|
| debug | `.debug` | No minification, debug tools enabled |
| release | (none) | Full minification, resource shrinking |

### Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing configuration)
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Generate APK with all variants
./gradlew assemble
```

---

## Required Permissions

**File: `AndroidManifest.xml`**

### Network Permissions (Required for AI features)

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### Notification Permissions

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

### Permission Request Flow

```kotlin
// POST_NOTIFICATIONS (Android 13+)
// Requested at app launch or when enabling notifications
// Handled via Accompanist Permissions library

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    requestPermission(Manifest.permission.POST_NOTIFICATIONS)
}
```

---

## Third-Party Dependencies

### Core Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| Kotlin | 2.0.21 | Language |
| Jetpack Compose BOM | 2024.11.00 | UI framework |
| Material 3 | BOM-managed | Design system |
| Room | 2.6.1 | Local database |
| DataStore | 1.1.1 | Preferences storage |
| Hilt | 2.52 | Dependency injection |
| Navigation Compose | 2.8.4 | Screen navigation |

### AI Integration

| Library | Version | Purpose |
|---------|---------|---------|
| Google Generative AI | 0.9.0 | Gemini API client |

### Async & Concurrency

| Library | Version | Purpose |
|---------|---------|---------|
| Coroutines | 1.9.0 | Asynchronous programming |
| Kotlinx Serialization | 1.7.3 | JSON serialization |

### Background Processing

| Library | Version | Purpose |
|---------|---------|---------|
| WorkManager | 2.10.0 | Background tasks |
| Glance | 1.1.0 | App widgets |

### UI Utilities

| Library | Version | Purpose |
|---------|---------|---------|
| Coil | 2.7.0 | Image loading |
| Accompanist | 0.34.0 | System UI, Permissions |
| Splash Screen | 1.0.1 | Launch screen |

### Testing

| Library | Version | Purpose |
|---------|---------|---------|
| JUnit | 4.13.2 | Unit testing |
| MockK | 1.13.13 | Mocking |
| Turbine | 1.2.0 | Flow testing |
| Espresso | 3.6.1 | UI testing |

---

## Local Storage Configuration

### Room Database

**Database Name:** `prody_database`
**Current Version:** 3
**Schema Export:** Enabled

**Tables:**
- `journal_entries` - User journal entries
- `future_messages` - Scheduled future messages
- `vocabulary` - Vocabulary words
- `vocabulary_learning` - Spaced repetition learning data
- `quotes`, `proverbs`, `idioms`, `phrases` - Wisdom content
- `user_profile` - User profile data
- `achievements` - Achievement tracking
- `user_stats` - User statistics
- `streak_history` - Streak tracking
- `leaderboard_entries` - Leaderboard data
- `peer_interactions` - Social features
- `motivational_messages` - Scheduled motivations
- `challenges`, `challenge_milestones`, `challenge_participation`, `challenge_leaderboard` - Challenge system

**Migration Strategy:**
```kotlin
@Database(
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 2, to = 3)
    ]
)
```

### DataStore Preferences

**File:** `prody_preferences`

**User Settings:**
```kotlin
// Theme & Display
theme_mode: String          // "system", "light", "dark"
dynamic_colors: Boolean     // Material You support
compact_card_view: Boolean  // Card display mode

// Notifications
notifications_enabled: Boolean
daily_reminder_hour: Int    // 0-23
daily_reminder_minute: Int  // 0-59
wisdom_notification_enabled: Boolean
journal_reminder_enabled: Boolean

// Learning
vocabulary_difficulty: Int  // 1-10
auto_play_pronunciation: Boolean

// AI Configuration
gemini_api_key: String      // User's API key (encrypted)
gemini_model: String        // Selected model ID
buddha_ai_enabled: Boolean  // Toggle AI features

// App State
onboarding_completed: Boolean
haptic_feedback_enabled: Boolean
```

---

## Release Configuration

### Signing Configuration

**Development:**
- Uses debug keystore (automatic)

**Production:**
```kotlin
signingConfigs {
    create("release") {
        storeFile = file("prody-release.jks")
        storePassword = System.getenv("KEYSTORE_PASSWORD")
        keyAlias = System.getenv("KEY_ALIAS")
        keyPassword = System.getenv("KEY_PASSWORD")
    }
}
```

### Environment Variables

| Variable | Description | Default (Dev Only) |
|----------|-------------|-------------------|
| `KEYSTORE_PASSWORD` | Keystore password | `prody2024` |
| `KEY_ALIAS` | Key alias name | `prody` |
| `KEY_PASSWORD` | Key password | `prody2024` |

### Creating Release Keystore

```bash
keytool -genkey -v -keystore prody-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias prody \
  -storepass <your-password> \
  -keypass <your-password> \
  -dname "CN=Prody, OU=Development, O=Prody, L=City, ST=State, C=US"
```

### ProGuard Configuration

**File:** `proguard-rules.pro`

Key rules:
- Keep Room entities and DAOs
- Keep Kotlin serialization classes
- Keep Hilt-generated code
- Keep data model classes
- Keep Gemini API models

---

## Testing Setup

### Unit Testing

**Location:** `app/src/test/`

```bash
# Run all unit tests
./gradlew test

# Run with coverage
./gradlew testDebugUnitTestCoverage
```

**Key Test Areas:**
- ViewModel state management
- Repository operations
- Business logic (SpacedRepetitionEngine, MoodAnalyticsEngine)
- Data transformations

### Instrumented Testing

**Location:** `app/src/androidTest/`

```bash
# Run instrumented tests
./gradlew connectedAndroidTest
```

**Key Test Areas:**
- Database migrations
- UI interactions
- Navigation flows
- Permission handling

### Mocking Dependencies

```kotlin
// MockK for unit tests
testImplementation("io.mockk:mockk:1.13.13")

// MockK for instrumented tests
androidTestImplementation("io.mockk:mockk-android:1.13.13")
```

---

## Troubleshooting

### Common Build Issues

**Issue: Gradle sync fails**
```bash
# Clean and refresh dependencies
./gradlew clean
./gradlew --refresh-dependencies
```

**Issue: KSP/Room compilation errors**
```bash
# Clear KSP cache
rm -rf app/build/generated/ksp
./gradlew clean assembleDebug
```

**Issue: Hilt injection errors**
1. Ensure `@HiltAndroidApp` on Application class
2. Ensure `@AndroidEntryPoint` on Activities
3. Ensure all dependencies have `@Inject` constructors or `@Provides` methods

### Gemini API Issues

**Issue: API key not working**
1. Verify key at [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Check if billing is enabled (for higher quotas)
3. Ensure no IP restrictions on the key

**Issue: Rate limiting**
- Free tier: 15 requests per minute
- Implement exponential backoff
- Consider upgrading to paid tier

**Issue: Safety filters blocking responses**
- Rephrase content to be less sensitive
- Cannot disable safety filters for user-facing content

### Database Issues

**Issue: Migration failures**
```kotlin
// Use fallbackToDestructiveMigration for development
Room.databaseBuilder(...)
    .fallbackToDestructiveMigration()
    .build()
```

**Issue: Schema mismatch**
1. Increment database version
2. Add AutoMigration or manual migration
3. Export schema for verification

---

## Best Practices

### Security

1. **API Key Management**
   - Never hardcode API keys
   - Use encrypted DataStore for storage
   - Rotate keys periodically

2. **Data Storage**
   - All sensitive data stored locally
   - Use encrypted SharedPreferences for sensitive settings
   - Implement backup exclusion rules

3. **Network Security**
   - All API calls use HTTPS
   - Implement certificate pinning for production
   - Validate API responses

### Performance

1. **Database Operations**
   - Use suspend functions for all DAO operations
   - Implement pagination for large lists
   - Use indices for frequently queried columns

2. **UI Performance**
   - Use `remember` and `derivedStateOf` appropriately
   - Implement lazy loading for lists
   - Profile with Android Studio Profiler

3. **Memory Management**
   - Avoid holding references in long-lived objects
   - Use `viewModelScope` for coroutines
   - Implement proper cleanup in `onCleared()`

### Code Quality

1. **Error Handling**
   - Wrap all external calls in try-catch
   - Provide meaningful error messages to users
   - Log errors for debugging (remove in production)

2. **Testing**
   - Write unit tests for ViewModels
   - Write integration tests for repositories
   - Test database migrations thoroughly

3. **Documentation**
   - Document public APIs with KDoc
   - Keep README and docs updated
   - Comment complex business logic

---

## Quick Start Checklist

- [ ] Install Android Studio Hedgehog or newer
- [ ] Install JDK 17
- [ ] Clone repository
- [ ] Sync Gradle dependencies
- [ ] Build debug variant successfully
- [ ] (Optional) Obtain Gemini API key
- [ ] (Optional) Configure API key in app settings
- [ ] Run unit tests
- [ ] Test on emulator or device

---

## Support & Resources

- **Android Developer Documentation**: https://developer.android.com
- **Jetpack Compose**: https://developer.android.com/jetpack/compose
- **Google AI Studio**: https://makersuite.google.com
- **Gemini API Documentation**: https://ai.google.dev/docs
- **Hilt Documentation**: https://dagger.dev/hilt/
- **Room Documentation**: https://developer.android.com/training/data-storage/room

---

*This document is part of the Prody project and should be updated whenever significant changes are made to the integration requirements.*
