# ADR-005: Fail-closed release signing and consolidated Android CI

- Status: Accepted
- Date: 2026-07-23

## Context

The repository contained overlapping Android workflows. One workflow generated a public fallback keystore and could publish that artifact as a tagged release. Such an APK is not a trustworthy continuation of a production signing identity and makes release provenance ambiguous.

## Decision

The Android pipeline is consolidated in `.github/workflows/android.yml`.

For pull requests and branch pushes it runs:

- Kairos architecture and trust-copy checks;
- debug unit tests;
- Android lint;
- a debug APK build.

Tagged releases run only after verification succeeds. The release job uses the protected `production` environment and requires all signing secrets. It fails immediately when any secret is absent and never creates a fallback key.

Release AI provider credentials remain empty in the APK. Signing credentials and provider credentials are separate concerns.

## Consequences

- CI has one authoritative Android path.
- Debug artifacts cannot be mistaken for production releases.
- A tag alone is insufficient to publish an APK without the real signing identity.
- Repository administrators must configure and protect the production environment before releasing.
