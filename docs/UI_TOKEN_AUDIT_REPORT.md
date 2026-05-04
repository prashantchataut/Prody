# UI Token Audit Report

Scope: `ui/components/*` and `ui/screens/*`.

## Audit method

- Scanned Kotlin composables under:
  - `app/src/main/java/com/prody/prashant/ui/components`
  - `app/src/main/java/com/prody/prashant/ui/screens`
- Flagged occurrences of:
  - hardcoded size literals (`*.dp`, `*.sp`)
  - hardcoded color constructors (`Color(0x...)`)

## Summary

- Baseline scan (before this change): **100 files / 6505 literal matches**.
- Current scan (after canonical component cleanup): **97 files / 5696 literal matches**.
- Highest concentration areas remain profile, settings, challenges, and stats screens.

## Top offenders (by match count)

1. `ui/screens/profile/SettingsScreen.kt` (257)
2. `ui/screens/profile/ProfileScreen.kt` (201)
3. `ui/screens/challenges/ChallengesScreen.kt` (180)
4. `ui/screens/stats/StatsScreen.kt` (177)
5. `ui/screens/profile/EditProfileScreen.kt` (149)

## Initial remediation in this change

- Canonical button, card, and chip components updated to prioritize `ProdyDesignTokens` usage.
- Added static verification task (`enforceDesignTokens`) to block new direct color/size hardcoding in production composables.
- Added canonical component docs for button/card/chip/list-row variants to guide migrations.
