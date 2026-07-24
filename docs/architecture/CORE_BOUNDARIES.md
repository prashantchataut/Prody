# Kairos core boundaries

## Dependency direction

```text
Compose UI
  -> feature ViewModel
    -> domain repository / use case
      <- data repository implementation
        -> Room DAO / network client / preferences
```

New core feature code must not import Room DAOs into ViewModels. Domain contracts must not import Android UI types. Data implementations may translate persistence models into domain-facing models where needed.

## Primary feature ownership

| Feature | UI | State owner | Boundary |
|---|---|---|---|
| Today | `FocusedTodayScreen` | `TodayViewModel` | `DailyPlanRepository` + `TodayProgressRepository` |
| Learn | `FocusedLearnScreen` | `VocabularyListViewModel` | `VocabularyRepository` |
| Reflect | `FocusedReflectScreen` | `JournalViewModel` | `JournalRepository` |
| Library | `FocusedLibraryScreen` | `QuotesViewModel` | `WisdomLibraryRepository` |
| Onboarding | `OnboardingScreen` | `OnboardingViewModel` | `OnboardingRepository` |

## Compatibility invariants

Until a dedicated migration is released, do not rename:

- `applicationId` / namespace;
- Room database and table names;
- preference and encrypted-storage keys;
- widget and pending-intent action identifiers;
- existing deep-link routes consumed outside the app.

Visible strings, icons, navigation labels, and Kotlin implementation names may migrate independently.

## Definition of done for a migrated feature

- No DAO imports in its ViewModel.
- Stable loading, empty, error, and content states.
- Search/filter behavior is deterministic and unit-tested.
- 48dp minimum interactive targets.
- Light and dark previews exist for high-value reading surfaces.
- No fake remote, community, or AI behavior.
- Offline behavior is useful and clearly represented.
