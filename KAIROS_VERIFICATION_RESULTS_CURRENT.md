# Kairos CI, UI, and architecture verification

## Build-blocking CI fix

The failing workflow invoked `./scripts/verify_kairos.sh` directly. Archive-based and some Windows/Git checkouts can leave that file without the executable bit, producing exit code 126.

The Android workflow now:

1. runs `chmod +x gradlew scripts/verify_kairos.sh`; and
2. invokes the verifier as `bash ./scripts/verify_kairos.sh`.

The verifier also checks its own shell syntax and confirms that CI uses the mode-independent invocation.

## Required deletion

Delete this obsolete overlapping workflow from the actual repository:

```text
.github/workflows/ci.yml
```

The path is also recorded in `FILES_TO_DELETE.txt`. The full-project ZIP already omits it.

## Verification completed in this environment

- Verifier shell syntax: passed
- CI invocation contract: passed
- GitHub Actions YAML parse: passed
- Kotlin PSI syntax parse: 486 Kotlin/KTS files, 0 syntax failures
- Internal `com.prody.prashant` import scan: 1,536 imports checked, 0 missing targets
- Duplicate non-private top-level type scan: 0 duplicates
- Android resource XML parse: passed
- Focused ViewModel DAO-boundary scan: passed
- Release provider-key boundary scan: passed
- Release signing fallback scan: passed
- Fabricated community activity scan: passed
- Unverifiable privacy-copy scan: passed
- Explainable recommendation and notification policy smoke tests: passed
- Vocabulary and journal browse-policy smoke tests: passed
- Room migration 23 to 24 SQL smoke test: passed, including composite-key enforcement
- Changed-file whitespace check: passed

## Full Gradle status

The command below was attempted:

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug --no-daemon --stacktrace
```

It could not start Gradle because this isolated environment has no cached Gradle 8.9 distribution and cannot resolve `services.gradle.org`. No Kotlin, lint, test, or APK task was reached locally. GitHub Actions remains the authoritative full build after applying the workflow fix and deleting the obsolete workflow.

## UI work in this delivery

- Dark editorial profile hero with a restrained liquid-glass identity panel
- Focused learning evidence rather than social/follower metrics
- Three-page dark onboarding with layered content cards and real preference setup
- Animated learning progress and staged vocabulary reveals
- Centralized motion primitives with reduced-motion handling
- Shared press feedback for glass controls and animated navigation selection
- Original abstract Kairos aperture mark across Compose, splash, adaptive, monochrome, and legacy launcher assets
- Subtle profile backdrop parallax and deliberate, non-bouncy motion
