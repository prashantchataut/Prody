# Prody Privacy & Security Report

**Date:** December 2024
**Version:** 1.0.0-RC
**Status:** PRODUCTION READY

---

## Privacy Architecture

### Design Principles

1. **Offline-First** - All data stored locally by default
2. **Encryption at Rest** - Journal entries encrypted
3. **Minimal Collection** - Only essential data collected
4. **User Control** - Export and delete capabilities
5. **Transparency** - Clear data policy accessible

---

## 1. Data Encryption

### Implementation

**File:** `app/src/main/java/com/prody/prashant/data/security/EncryptionManager.kt`

### Encryption Standard

| Parameter | Value |
|-----------|-------|
| Algorithm | AES-256-GCM |
| Key Size | 256 bits |
| IV Size | 12 bytes (96 bits) |
| Auth Tag | 128 bits |

### Key Management

```kotlin
private val masterKey: MasterKey by lazy {
    MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
}
```

- Keys stored in Android Keystore
- Uses `EncryptedSharedPreferences`
- Per-app encryption key auto-generated
- Keys never leave the device

### Encrypted Data Format

```
ENC:base64(IV + ciphertext)
```

### Verification

```kotlin
fun isEncryptionAvailable(): Boolean {
    return try {
        val testText = "test"
        val encrypted = encryptText(testText)
        val decrypted = decryptText(encrypted)
        decrypted == testText
    } catch (e: Exception) {
        false
    }
}
```

---

## 2. Data Policy UI

### Implementation

**File:** `app/src/main/java/com/prody/prashant/ui/screens/profile/SettingsScreen.kt`

### Privacy Section in Settings

- Located in Settings screen
- Contains "View Data Policy" button
- Displays Privacy Summary Card
- Shows encryption status

### Data Policy Dialog Contents

1. **What We Collect**
   - Journal entries (stored locally, encrypted)
   - Mood selections and tags
   - Gamification progress
   - App preferences
   - Anonymous performance metrics

2. **How We Use Your Data**
   - Personalized AI insights
   - Self-improvement tracking
   - Mood analytics generation
   - Performance improvement (anonymized)

3. **Data Storage**
   - All data stored locally
   - AES-256 encryption
   - Export/delete options
   - No server uploads without consent

4. **AI Features**
   - Content sent only when AI enabled
   - AI providers don't store data
   - Can disable at any time
   - Cached responses local only

5. **Your Rights**
   - Export all data
   - Delete all data
   - Disable features
   - Opt out of AI

---

## 3. Content Moderation

### Implementation

**File:** `app/src/main/java/com/prody/prashant/data/moderation/ContentModerationManager.kt`

### Self-Harm Detection

```kotlin
private val selfHarmKeywords = setOf(
    "hurt myself", "end it all", "kill myself", "suicide",
    "self harm", "cut myself", "don't want to live"
)
```

### Supportive Response

When concerning content detected:
```kotlin
"If you're struggling, please reach out to a mental health
professional or crisis helpline. You're not alone, and help
is available."
```

### Crisis Resources

| Resource | Contact |
|----------|---------|
| National Suicide Prevention Lifeline | 988 |
| Crisis Text Line | Text HOME to 741741 |
| International Association for Suicide Prevention | https://www.iasp.info/resources/Crisis_Centres/ |

### Report Feature

Users can report content via email-based system:
- Reports stored locally
- User controls when to send
- No automatic transmission

---

## 4. API Security

### Key Management

**File:** `app/build.gradle.kts`

```kotlin
buildConfigField("String", "AI_API_KEY",
    "\"${localProperties.getProperty("AI_API_KEY", "")}\"")
```

### Security Measures

| Measure | Implementation |
|---------|----------------|
| Keys in local.properties | YES |
| .gitignore protection | YES |
| No hardcoded keys | YES |
| HTTPS only | YES |
| Certificate validation | YES |
| Timeout limits | YES |

### Graceful Failures

```kotlin
// No API key configured
if (BuildConfig.AI_API_KEY.isEmpty()) {
    return Result.Error("AI features require API key")
}

// Invalid key
if (response.code == 401) {
    return Result.Error("Invalid API key")
}

// Network failure
catch (e: Exception) {
    return cache.getCachedResponse()
        ?: Result.Error("Network unavailable")
}
```

---

## 5. Performance Monitoring

### Privacy-First Design

**File:** `app/src/main/java/com/prody/prashant/data/monitoring/PerformanceMonitor.kt`

### What IS Collected (Anonymous)

- Operation timing (journal save, screen load)
- Success/failure rates
- Device model and OS version
- App version

### What is NOT Collected

- Journal content
- User identity
- Personal information
- Usage patterns identifiable to user

### Data Sanitization

```kotlin
private fun sanitizeErrorMessage(message: String): String {
    // Remove email addresses
    var sanitized = message.replace(
        Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"),
        "[EMAIL]"
    )
    // Remove phone numbers
    sanitized = sanitized.replace(
        Regex("\\+?\\d{10,15}"),
        "[PHONE]"
    )
    // Remove file paths
    sanitized = sanitized.replace(
        Regex("/Users/[^/]+/"),
        "/Users/[USER]/"
    )
    return sanitized
}
```

---

## 6. Data Export & Deletion

### Export Feature

**File:** `app/src/main/java/com/prody/prashant/data/backup/BackupManager.kt`

Exportable data:
- Journal entries
- Future messages
- Vocabulary progress
- Achievements and stats
- Settings

Format: JSON file saved to device storage

### Deletion Feature

```kotlin
fun deleteAllUserData() {
    // Clears all database tables
    // Clears preferences
    // Clears encryption keys
    // Resets gamification
}
```

---

## 7. Compliance Status

### GDPR Considerations

| Requirement | Status |
|-------------|--------|
| Data portability | COMPLETE (Export feature) |
| Right to erasure | COMPLETE (Delete all data) |
| Consent | COMPLETE (AI features opt-in) |
| Transparency | COMPLETE (Data policy accessible) |

### App Store Requirements

| Requirement | Status |
|-------------|--------|
| Data collection disclosed | COMPLETE |
| Privacy policy accessible | COMPLETE |
| Encryption for sensitive data | COMPLETE |
| No unauthorized sharing | COMPLETE |

### Security Best Practices

| Practice | Status |
|----------|--------|
| No hardcoded secrets | COMPLETE |
| Secure key storage | COMPLETE |
| Network security config | COMPLETE |
| Input validation | COMPLETE |

---

## 8. Security Checklist

### Data at Rest
- [x] Journal entries encrypted (AES-256-GCM)
- [x] Keys stored in Android Keystore
- [x] No plaintext sensitive data

### Data in Transit
- [x] HTTPS only for API calls
- [x] Certificate validation enabled
- [x] No plain HTTP connections

### User Privacy
- [x] Data policy accessible
- [x] AI features opt-in
- [x] Export capability
- [x] Delete capability

### Error Handling
- [x] No personal data in logs
- [x] Sanitized error messages
- [x] Graceful API failures

---

## Summary

| Privacy Feature | Status |
|-----------------|--------|
| Journal Encryption | COMPLETE |
| Data Policy UI | COMPLETE |
| Content Moderation | COMPLETE |
| Report Feature | COMPLETE |
| Data Export | COMPLETE |
| Data Deletion | COMPLETE |
| API Security | COMPLETE |
| Anonymous Metrics | COMPLETE |
| GDPR Compliance | COMPLETE |
| App Store Compliance | COMPLETE |

**Privacy & Security Status: PRODUCTION READY**
