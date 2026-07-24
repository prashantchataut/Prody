# ADR-004: Local-first entry and permission consent

- Status: Accepted
- Date: 2026-07-23

## Context

Vocabulary, reflection, and the local library do not require a remote account. Requiring Google authentication before users can evaluate those features creates unnecessary friction and implies cloud behavior that is not yet implemented. Requesting notification permission during first launch also asks for trust before users understand the value or frequency of reminders.

## Decision

Kairos offers two entry paths:

- **Continue locally** creates a persistent local session and keeps the core experience on the device.
- **Continue with Google** remains optional for future account-backed capabilities.

The local session uses the stable `local` user identity for new installations. Existing stored identifiers remain readable for upgrade compatibility.

Onboarding collects only inputs that affect product behavior: vocabulary pace and preferred idea categories. Its database initialization is owned by `OnboardingRepository`, and completion is recorded only after the local content/profile transaction succeeds.

Android notification permission is not requested at launch. On Android 13 and later, it is requested only when the user explicitly enables notifications in Settings. A denied permission leaves the in-app preference disabled.

## Consequences

- Users can reach the useful core without an account or network.
- Authentication no longer overpromises synchronization.
- Recommendation setup is concise and behaviorally meaningful.
- Notification consent is contextual and reversible.
- Account migration and real cross-device synchronization remain separate future projects.
