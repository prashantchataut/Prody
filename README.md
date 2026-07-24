# Kairos

**Learn one useful thing. Notice it in your life. Remember it when it matters.**

Kairos is a local-first Android learning and reflection app built with Kotlin, Jetpack Compose, Room, Hilt, and Material 3. The focused product surface is organized around four destinations: **Today**, **Learn**, **Reflect**, and **Library**.

> The canonical Android identity is now `com.kairos.app`. This is a new package identity relative to historical Prody builds, so old-package local data does not migrate automatically without an explicit export/import path.

## Product surface

### Today
- One stable vocabulary recommendation and one thoughtful quote per local date.
- Explainable selection and direct feedback: too easy, too hard, more like this, and less like this.
- A calm vertical reading flow instead of a dashboard of unrelated modules.

### Learn
- Searchable vocabulary catalog with new, learned, and favorite states.
- Spaced-repetition foundations and contextual word-use tracking.
- Deterministic browse policies with unit coverage.

### Reflect
- Fast journal creation and a readable chronological history.
- Search and bookmark filters without requiring AI.
- Encrypted local storage and useful offline behavior.

### Library
- Quotes, proverbs, idioms, and phrases behind one repository boundary.
- Search and favorite controls across all content types.
- Editorial reading surfaces designed for longer text.

## Experience principles

- **Focused over crowded:** secondary experiments do not compete in primary navigation.
- **Trustworthy over theatrical:** no fabricated community activity or fake remote behavior.
- **Glass as hierarchy:** translucent material is limited to navigation and compact controls; reading surfaces stay legible.
- **Useful without AI:** the core loop works when network and generated features are unavailable.
- **Consent at the moment of intent:** notification permission is requested only after the user enables reminders.
- **Migration-aware:** the current `com.kairos.app` identifiers are treated as stable; historical Prody package trees must be removed rather than overlaid.

## First-run experience

Users may continue with a persistent local profile or sign in with Google. Onboarding has three steps:

1. Understand the daily word-and-thought loop.
2. Choose a vocabulary pace and idea categories that feed recommendation ranking.
3. Review local-first storage and optional notification behavior.

Onboarding completion is written only after the local catalog and profile transaction succeeds.

## Visual system

- Mineral indigo, clay, verdigris, and warm editorial neutrals.
- Restrained glass navigation and control surfaces.
- Matte reading surfaces for definitions, quotations, and journal text.
- Native Android sans for interface density and the platform serif family for reflective content; no missing font resources or runtime downloads.
- Edge-to-edge layout, explicit system insets, 48dp minimum controls, compact bottom navigation, and an adaptive wide-screen rail.
- Short state transitions and pressed feedback; looping motion is limited to the active cosmetic preview or hero and respects reduced-motion settings.

## Architecture

The focused screens use a strangler migration around the legacy application:

```text
Compose UI
  -> feature ViewModel
    -> domain repository / use case
      <- data repository implementation
        -> Room DAO / preferences / network client
```

Promoted ViewModels do not coordinate Room DAOs directly. Legacy routes remain in the source tree until their data and compatibility requirements can be retired safely.

Repository boundaries and compatibility rules are enforced by `scripts/verify_kairos.sh` and documented in the phase implementation report included with release artifacts.

## Technical stack

| Area | Technology |
|---|---|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| State | ViewModel + StateFlow |
| Dependency injection | Hilt |
| Persistence | Room + SQLCipher, DataStore, encrypted preferences |
| Async | Kotlin Coroutines + Flow |
| Navigation | Navigation Compose |
| Background work | WorkManager |
| Build | Gradle 8.9, JDK 17, Android SDK 35 |


## Meaningful progression

Kairos retains cosmetics, achievements, skill progression, missions, and streaks, but rewards evidence rather than app opens:

- **Clarity:** completed reflections and revisiting earlier writing.
- **Discipline:** successful recall and natural vocabulary use.
- **Courage:** writing, opening, and replying to future-self letters.
- **Consistency:** balanced practice across days, without streak multipliers or fake social ranks.

Achievement IDs and thresholds come from one canonical catalogue. Cosmetic banners are earned identity surfaces with distinct geometry; animation is reserved for the active preview/header to protect battery, accessibility, and visual calm.

## AI boundary

The app does **not** embed provider credentials in release builds. Legacy direct-provider integrations are available only for local debug development:

```properties
# local.properties — never commit
AI_API_KEY=your_local_development_key
OPENROUTER_API_KEY=your_optional_local_development_key
THERAPIST_API_KEY=your_optional_local_development_key
TTS_API_KEY=your_optional_local_development_key
```

Production generated features require an authenticated server gateway with quotas, abuse controls, schema validation, and provider isolation. Today, Learn, Reflect, and Library do not depend on that gateway.

## Build

Prerequisites:

- JDK 17+
- Android SDK 35
- Android Studio with current Compose tooling

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
```

Run the repository boundary and trust checks without Gradle:

```bash
bash ./scripts/verify_kairos.sh
```

## CI and releases

`.github/workflows/android.yml` runs architecture checks, unit tests, Android lint, and a debug build for pull requests and pushes. Tagged release builds fail closed unless all production signing secrets are configured in the protected `production` environment:

- `KEYSTORE_BASE64`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

The workflow never generates or publishes a fallback signing key.

## Compatibility invariants

Do not rename these current Kairos identifiers without a dedicated migration and upgrade test:

- `applicationId` and namespace (`com.kairos.app`);
- Room database and table names;
- preference and encrypted-storage keys;
- widget and pending-intent action identifiers;
- externally consumed deep links.

Visible branding and UI implementation names may migrate independently.

## Current migration status

The four primary destinations, local entry flow, onboarding, notification consent, recommendation feedback, release AI boundary, launcher identity, and adaptive navigation use the Kairos system. Some secondary legacy screens still use the earlier visual language and remain intentionally outside primary navigation.

## Verification

A complete verification run should include:

```bash
bash ./scripts/verify_kairos.sh
./gradlew clean :app:testDebugUnitTest :app:lintDebug :app:assembleDebug \
  --no-daemon --no-configuration-cache --stacktrace
```

Then test on at least:

- one compact phone in light and dark mode;
- one large-font configuration;
- one tablet or foldable width;
- Android 13+ notification permission flows;
- an upgrade install over an existing Kairos database.
