# Kairos

**Learn one useful thing. Notice it in your life. Remember it when it matters.**

Kairos is a local-first Android learning and reflection app built with Kotlin, Jetpack Compose, Room, Hilt, and Material 3. Its primary product surface is intentionally limited to **Today**, **Learn**, **Reflect**, and **Library**.

## Android identity

The project is fully namespaced as:

```text
com.kairos.app
```

Both `namespace` and `applicationId` use that value. Debug builds use `com.kairos.app.debug`.

> Changing from `com.prody.prashant` to `com.kairos.app` creates a new Android application identity. Android does not automatically transfer the old app sandbox, Room database, preferences, widgets, notification settings, or authentication state. Treat this as a new listing unless you implement an explicit export/import migration from the old app.

## Core experience

### Today
- One stable vocabulary recommendation and one thoughtful quote per local date.
- Clear recommendation reasons and direct feedback: too easy, too hard, more like this, and less like this.
- A calm vertical reading flow rather than a dashboard of unrelated modules.

### Learn
- Searchable vocabulary with new, learned, favorite, and review-ready states.
- Spaced-repetition foundations and contextual word-use tracking.
- Deterministic filtering and ranking policies.

### Reflect
- Fast journal creation and readable chronological history.
- Search and bookmarks without requiring generated guidance.
- Local-first storage and offline behavior.

### Library
- Quotes, proverbs, idioms, and phrases behind one repository boundary.
- Search and favorite controls across content types.
- Editorial reading surfaces designed for longer text.

## Product principles

- **Focused over crowded:** experimental features do not compete in primary navigation.
- **Trustworthy over theatrical:** no fabricated community activity or fake remote behavior.
- **Glass as hierarchy:** translucent material is reserved for navigation and compact controls; reading surfaces remain legible.
- **Useful without generated content:** the core loop works offline.
- **Consent at the moment of intent:** notification permission is requested only after the user enables reminders.
- **Accessible motion:** meaningful transitions are short and reduced-motion settings are respected.

## Architecture

The promoted flows follow this dependency direction:

```text
Compose UI
  -> feature ViewModel
    -> domain repository / use case
      <- data repository implementation
        -> Room DAO / preferences / network client
```

ViewModels in the promoted flows do not coordinate Room DAOs directly. Legacy secondary routes remain in the source tree until their persistence and compatibility requirements can be retired safely.

## AI boundary

Generated guidance is optional and must never be required for Today, Learn, Reflect, or Library.

The current Android Google AI client is retained only as legacy development infrastructure. Google now recommends Firebase AI Logic for Android and the old `generative-ai-android` client is no longer actively maintained. A production release should complete that migration, enable App Check, and remove direct provider-key handling before exposing generated guidance broadly.

Provider secrets default to empty values in release builds. Local debug keys may be supplied through `local.properties`:

```properties
AI_API_KEY=local_development_only
OPENROUTER_API_KEY=local_development_only
THERAPIST_API_KEY=local_development_only
TTS_API_KEY=local_development_only
```

Never commit those values.

## Build

Prerequisites:

- JDK 17
- Android SDK 35
- network access for the first Gradle/dependency resolution

Run fast repository checks:

```bash
bash ./scripts/verify_kairos.sh
```

Run the complete local verification pipeline:

```bash
bash ./scripts/verify_kairos.sh --gradle
```

Equivalent Gradle command:

```bash
./gradlew clean :app:testDebugUnitTest :app:lintDebug :app:assembleDebug \
  --no-daemon --no-configuration-cache --stacktrace
```

## CI

`.github/workflows/android.yml`:

1. runs package, branding, DI, XML, resource, and build-policy checks;
2. runs unit tests;
3. runs Android lint;
4. assembles a debug APK;
5. uploads the lint report and APK.

The workflow invokes the verifier through `bash`, so it does not depend on ZIP or Git preserving the executable bit.

## Applying the changed-files ZIP

Overlaying a ZIP cannot remove stale files. After extraction, apply every path in [`FILES_TO_DELETE.txt`](FILES_TO_DELETE.txt). The complete-project ZIP already omits them.

## Current scope decision

The codebase still contains legacy experiments such as social circles, collaborative messages, complex gamification, Haven, Deep Dive, and yearly summaries. They are intentionally absent from primary navigation. Do not promote them again until each feature has a real user need, honest backend behavior where applicable, analytics, tests, and a maintenance owner.
