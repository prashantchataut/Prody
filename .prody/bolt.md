# BOLT ⚡ - PRODY PERFORMANCE JOURNAL

This journal contains CRITICAL performance learnings specific to the Prody codebase.

## 2024-05-20 - Animation Recomposition in NavigationBar
**Context:** Bottom navigation animations in `MainActivity.kt` and `MagicalEffects.kt`.
**Learning:** Inlining frequent animations (like breathing pulses) directly inside complex parent layouts like `NavigationBar` causes the entire parent to recompose every frame. Even small components like `NavigationBreathingGlow` can trigger wide invalidation if they don't use `graphicsLayer` correctly.
**Action:** Isolate frequent UI animations into their own small, private Composables. Use `Modifier.graphicsLayer { ... }` to apply `alpha` and `scale` updates, as this modifies the drawing layer directly without triggering recomposition of the parent scope.

## 2024-05-20 - Placeholder Feature Gap
**Context:** `HomeScreen.kt` vs `HomeViewModel.kt`.
**Learning:** Prody had a significant "feature gap" where sophisticated logic was implemented in ViewModels but the UI was stuck with hardcoded placeholders. This gave the impression of broken functionality ("Only 1% works").
**Action:** Always verify that UI components are correctly bound to the corresponding state fields in `UiState`. Explicitly mapping previously ignored fields like `dualStreakStatus`, `nextAction`, and `dailySeed` drastically improves the functional surface area of the app.

## 2024-05-24 - Efficient Data Seeding in Onboarding
**Context:** `OnboardingViewModel.kt`
**Learning:** Initializing multiple database entities (Profile, Stats, Achievements, Vocabulary, Quotes, etc.) during onboarding without a transaction caused a massive disk I/O bottleneck, leading to a "frozen" UI state during the transition to the home screen. Room's `database.withTransaction` is essential for atomic, high-performance mass inserts.
**Action:** Always wrap multi-entity initialization or bulk data seeding within a single Room transaction to minimize disk synchronization overhead.

## 2024-05-24 - Canvas-Based UI Indicators
**Context:** `ProgressIndicators.kt` and `OnboardingScreen.kt`.
**Learning:** Using a `Row` of multiple `Box` composables for page indicators creates unnecessary layout nodes and triggers expensive layout passes during page swipes as dot widths animate. A single `Canvas` drawing all dots based on an animated float index is significantly more performant and smoother.
**Action:** Prefer `Canvas`-based drawing for multi-state UI indicators like page dots or segmented progress bars to maintain 60fps during complex interactions.

## 2024-05-25 - java.time for High-Performance Grouping
**Context:** `HomeViewModel.kt` calculateMoodTrend logic.
**Learning:** Using `Calendar.getInstance()` within loops or grouping operations for temporal analysis causes unnecessary object allocation and is significantly slower than modern `java.time` APIs. `LocalDate` with `ZoneId` provides a much cleaner and more performant way to group entries by day.
**Action:** Always prefer `java.time.LocalDate` and `Instant` for performance-critical date grouping or temporal calculations in ViewModels.

## 2024-05-25 - Deferring State Reads to Draw Phase
**Context:** `ProgressIndicators.kt` and `HomeScreen.kt` animations.
**Learning:** Reading an animated `Float` state directly in a Composable's body triggers recomposition of the entire scope every frame. Reading the state inside a `graphicsLayer` block or `Canvas` draw block defers the read to the drawing phase, completely bypassing recomposition.
**Action:** Use lambda-based state access (e.g., `progress = { state.value }` or `graphicsLayer { alpha = state.value }`) to keep animations off the composition thread and ensure smooth 60fps performance.
