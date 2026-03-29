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

## 2024-05-25 - Deferred Animation State Reads
**Context:** `MainActivity.kt` (Haven FAB) and `MagicalEffects.kt` (NavigationBreathingGlow).
**Learning:** When using `animateFloatAsState` or `infiniteRepeatable` animations with `graphicsLayer`, using property delegation (`by`) causes the composable to recompose on every frame.
**Action:** Access the animation state directly via `.value` inside the `graphicsLayer` lambda. This ensures the state read is deferred to the drawing phase, completely bypassing the recomposition and layout phases for the animated properties.
