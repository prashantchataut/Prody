# Package migration: `com.prody.prashant` -> `com.kairos.app`

## What changed

- Gradle namespace: `com.kairos.app`
- Android application ID: `com.kairos.app`
- Kotlin package declarations and source paths: `com/kairos/app`
- FileProvider authority: `${applicationId}.fileprovider`
- Firebase clients: `com.kairos.app` and `com.kairos.app.debug`

## Important consequence

Android treats the new application ID as a separate app. Installing Kairos does not update an installed Prody build, and it cannot directly read Prody's private Room database or preferences.

## Supported launch paths

### New app/listing

Use the project as delivered. This is the safest option while the product is being substantially rebuilt.

### Existing-user migration

Before shipping the new package, add an export flow to the final Prody build and an import flow to Kairos. Export only user-created content and learning progress; do not copy encryption keys, Firebase tokens, notification permission state, or generated caches.

Recommended transferable data:

- journal entries and bookmarks;
- future messages;
- vocabulary learning state;
- saved quotes and library items;
- explicit user preferences;
- recommendation interaction history when schema-compatible.

The migration requires dedicated schema validation, integrity checks, duplicate handling, and upgrade tests. It is not implemented by a package rename alone.
