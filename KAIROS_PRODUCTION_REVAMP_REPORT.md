# Kairos production revamp report

Date: 2026-07-23

## Delivery scope

This delivery combines the earlier product-foundation work with the new visual, navigation, onboarding, architecture, security, and release-engineering phase. It is measured against the original uploaded Prody source tree.

- 83 source-tree changes
- 41 added files
- 41 modified files
- 1 deleted workflow
- 14,329 reported insertions and 9,849 reported deletions

The line totals are inflated by normalization in several older CRLF-formatted files; the file count is the more useful review measure.

## Product reset

Kairos now has four primary destinations:

1. **Today** — one stable word, one thought, and one useful action.
2. **Learn** — vocabulary discovery, search, favorites, and learned state.
3. **Reflect** — journal creation, search, bookmarks, and history.
4. **Library** — quotes, proverbs, idioms, and phrases.

Legacy experiments remain available through existing routes where compatibility requires them, but they no longer compete in the primary navigation shell.

## Visual system

The new design uses a restrained Liquid Glass-inspired material system rather than applying blur and translucency everywhere.

### Glass surfaces

Used for:

- compact bottom navigation;
- wide-screen navigation rail;
- top controls and icon buttons;
- segmented controls;
- compact action clusters.

### Editorial surfaces

Vocabulary definitions, quotes, journal entries, empty states, and error states use matte or near-opaque reading surfaces. This protects contrast, reduces visual noise, and avoids unnecessary rendering cost.

### Identity

- Mineral indigo for recognition and primary action.
- Clay for warmth and reflective emphasis.
- Verdigris for completion and positive state.
- Warm neutral backgrounds with low-chroma ambient washes.
- Native Android sans for interface text.
- Lora for quotations and reflective reading.
- 48dp minimum primary controls.
- Short state transitions and pressed feedback rather than decorative looping animation.

## Core screen changes

### Today

- Replaced the crowded dashboard with vertically scrollable, snapping Word and Thought moments.
- Added pronunciation, part of speech, definition, example, author, and recommendation reasoning.
- Added completion, practice, reflection, and library actions.
- Added progressive-disclosure tuning controls for difficulty and preference feedback.
- Added loading, empty, and error states.
- Connected the visible content to the same stable Daily Plan used by notifications.

### Learn

- Added a focused searchable vocabulary browser.
- Added All, New, and Learned segments.
- Preserved favorite and learned actions.
- Added clear empty, loading, and failure states.
- Moved browse rules into pure deterministic policies.

### Reflect

- Added a quiet chronological reflection surface.
- Added search, bookmarked filtering, history access, and a prominent new-entry action.
- Removed promotional AI labels from the primary journal history.
- Memoized derived title, excerpt, date, and word-count presentation work.

### Library

- Unified quotes, proverbs, idioms, and phrases behind one interface.
- Added search and favorite filtering.
- Added editorial reading rows and centered tablet-width content.
- Memoized collection filtering.

## Adaptive navigation

- Compact widths use a floating glass bottom bar.
- Widths at or above 720dp use a navigation rail.
- Content is edge-to-edge with explicit system-bar and scaffold inset handling.
- Secondary screens hide the primary navigation automatically.

## First-run experience

The old eight-page carousel advertised Haven, XP, leaderboards, community, and other non-core systems. It has been replaced with three honest steps:

1. Explain the daily word-and-thought loop.
2. Configure vocabulary pace and preferred idea categories.
3. Explain local-first storage, optional notifications, and the four destinations.

`OnboardingViewModel` now depends on `OnboardingRepository`, not Room DAOs. Setup is marked complete only after local profile and catalog initialization succeeds.

## Local-first access

- Added a persistent **Continue locally** entry path.
- Google sign-in remains optional.
- The core vocabulary, reflection, and library experience is no longer blocked by authentication.
- New local installations use the stable `local` identity.
- Existing package, storage, and database identifiers remain unchanged for upgrade safety.

## Recommendation foundation

The combined delivery includes:

- per-user, per-date daily selections;
- stable selection across restarts;
- impression and interaction history;
- explainable score breakdowns;
- review urgency;
- category affinity;
- novelty and repetition penalties;
- difficulty fit;
- temporal relevance;
- quality and diversity signals;
- Too easy, Too hard, More like this, and Less like this feedback.

The ranker is deterministic and auditable. It does not pretend to be a trained model before sufficient behavioral data exists.

## Notification behavior

The notification surface is reduced to:

- Daily Moment;
- optional Evening Reflection;
- Weekly Recap;
- exact Future Message delivery.

The delivery policy suppresses prompts when:

- notifications or that category are disabled;
- quiet hours are active;
- the relevant action is already complete;
- the daily cap is reached;
- another notification was sent within six hours;
- the app was opened recently.

Android 13+ notification permission is requested only when the user enables notifications in Settings. It is not requested during launch or onboarding.

## Architecture work

The promoted feature direction is:

```text
Compose UI
  -> ViewModel
    -> domain repository / use case
      <- data implementation
        -> Room DAO / preferences / network
```

New repository boundaries:

- `DailyPlanRepository`
- `TodayProgressRepository`
- `VocabularyRepository` usage in Learn
- `JournalRepository` usage in Reflect
- `WisdomLibraryRepository`
- `OnboardingRepository`

The focused ViewModels contain no Room DAO imports. Compatibility writes remain only where older screens still depend on legacy entity fields and are documented for later removal.

## Trust and security corrections

- Removed fabricated challenge participants, usernames, community totals, and leaderboard behavior.
- Reworded privacy and AI copy to avoid unverifiable on-device or provider-retention claims.
- Release BuildConfig provider credentials are empty.
- Direct provider keys are debug-only local-development inputs.
- Removed static certificate pins for third-party AI infrastructure.
- The core app remains usable when generated features are unavailable.

## CI and signing

- Consolidated overlapping Android workflows.
- Deleted `.github/workflows/ci.yml`.
- CI now runs architecture checks, tests, lint, and a debug build through one workflow.
- Tagged releases depend on successful verification.
- Production releases require all signing secrets in the protected `production` environment.
- The workflow never generates or publishes a fallback signing key.
- `gradlew` is executable with mode 755.

## Branding

- Visible application name is Kairos.
- Navigation labels, notification copy, share copy, widgets, launcher artwork, and onboarding use the Kairos identity.
- The launcher uses an abstract moment mark, mineral indigo field, and clay accent.
- Internal package, database, preference, and action identifiers retain Prody naming where renaming would risk upgrades.

## Documentation added

- ADR-001: focused product shell and strangler migration
- ADR-002: restrained Liquid Glass visual system
- ADR-003: production AI boundary
- ADR-004: local-first entry and permission consent
- ADR-005: fail-closed release signing and consolidated CI
- Core dependency and compatibility boundaries
- Repository verification script
- Rewritten truthful README

## Verification completed

- Kotlin parser validation for 39 changed Kotlin/KTS files.
- Android XML parsing for the complete resource tree.
- GitHub workflow YAML parsing.
- Focused ViewModel DAO-boundary scan.
- Release provider-key boundary scan.
- Release signing fallback scan.
- Fabricated community behavior scan.
- Unverifiable privacy-copy scan.
- Pure Kotlin recommendation and notification rule execution.
- Pure Kotlin browse-policy execution.
- Combined patch application from the original source tree.
- Byte-content comparison proving the applied patch recreates the final source tree.
- ZIP and tar integrity checks are recorded in the accompanying verification file.

## Verification limitation

A full Gradle Android build, Android lint run, emulator render, Compose screenshot test, and real migration install could not be completed in this environment. Gradle 8.9 was not cached and the wrapper could not resolve `services.gradle.org` because outbound DNS/network access was unavailable.

This limitation is explicit: the source passed static, parser, pure-domain, patch, and archive checks, but it still requires the repository CI and Android-device QA before release.

## Required release QA

1. Run `./scripts/verify_kairos.sh --gradle` in CI or a networked development environment.
2. Install over an existing Prody database and verify Room migration to version 24.
3. Test compact phone, large text, dark mode, and tablet/foldable widths.
4. Test local entry, Google entry, process death during onboarding, and sign-out behavior.
5. Test Android 13+ notification grant and denial flows.
6. Capture Compose screenshot baselines for the four primary screens and onboarding.
7. Verify generated features remain disabled in a production release without the future gateway.

## Deliberate remaining scope

- Secondary legacy screens have not all been visually migrated.
- Real cloud synchronization is not implemented or advertised as complete.
- Production AI gateway work remains a separate backend project.
- The Android package name remains unchanged.
- Kairos remains a working public name until trademark and store clearance is completed.
- No reference image was attached in this turn, so the design follows the requested Liquid Glass/minimalist direction rather than a pixel match to that image.
