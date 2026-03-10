# Prody v1.0.0 - January 2026

## Release Summary

Prody v1.0.0 is a premium journaling and self-reflection app featuring AI-powered insights, vocabulary building, and gamification systems.

## Key Fixes

### Build & Compilation Fixes
- Fixed duplicate `BloomStats` class declaration causing compilation errors
- Resolved non-exhaustive `when` expressions in BannerDisplay pattern rendering
- Fixed unresolved icon references in ReceiptCard component (Visibility, Close, ChevronRight)
- Resolved AutoMirrored icon reference issues in ProdyIcons

### Code Quality Improvements
- Renamed `BloomStats` in ContextBloomService to `ContextBloomStats` to avoid collision with gamification BloomStats
- Added else branches to pattern type switch statements for future-proofing
- Standardized icon references to use ProdyIcons directly instead of nested objects

## Features

### Core Features
- Journal entry with AI-powered Buddha insights
- Voice transcription for hands-free journaling
- Gamification system with Bloom mechanics for vocabulary learning
- Context Bloom - detects when learned words are used naturally in journal entries

### Premium Features
- Wisdom collection (Word of Day, Quotes, Proverbs)
- Soul Layer intelligence for personalized insights
- Anniversary memory surfacing
- Growth contrast tracking

## Technical Details

- **Version**: 1.0.0
- **Version Code**: 1
- **Min SDK**: 24
- **Target SDK**: 35
- **Compile SDK**: 35

## Download

Download link: https://knowprashant.vercel.app/prody-1.0.0

---

*This release has been verified to pass all build checks (debug, release, lint).*

# Prody Maintenance - May 2024

## Maintenance Summary
Performed a comprehensive codebase cleanup, error resolution, and security hardening.

## Key Changes

### Bug Fixes & Improvements
- Resolved deprecated Material Icon usage across the codebase, particularly with `AutoMirrored` variants.
- Connected missing profile navigation on the Home screen.
- Added missing database indexes to `HavenMemoryEntity` to optimize "Vault" feature queries.
- Cleaned up unused imports and variables in `MainActivity`, `HomeScreen`, and `HavenViewModel`.

### Security & Privacy
- Hardened GitHub Actions CI/CD workflows by pinning all actions to specific commit SHAs.
- Verified `FLAG_SECURE` implementation on sensitive screens (Journal, Haven Chat) to prevent unauthorized screenshots.
- Redacted sensitive headers in network logs (OpenRouter).

### Documentation
- Updated `README.md` to reflect new Haven therapeutic features and Soul Layer intelligence.
- Updated tech stack documentation with specific dependency versions.
