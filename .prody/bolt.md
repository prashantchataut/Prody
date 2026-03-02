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

## 2024-05-24 - Bulk Initialization Performance
**Context:** Initial data seeding in `OnboardingViewModel.kt`.
**Learning:** Sequential suspend calls to Room DAOs (inserting vocabulary, quotes, proverbs, etc.) during onboarding caused multiple disk synchronization events. This resulted in a noticeable "stutter" during the transition from the final onboarding screen to the Home dashboard.
**Action:** Wrap all initial setup operations (User Profile, Stats, Achievements, and Initial Content) within a single `database.withTransaction` block. This consolidates disk I/O into a single atomic operation, drastically improving "Time to Home" for new users. Ensure DataStore updates remain outside the transaction to prevent unnecessary blocking.

## 2024-05-24 - Recomposition in Common UI Components
**Context:** Breathing animations in `EmptyState.kt`, `MagicalEffects.kt`, and `EnhancedAnimations.kt`.
**Learning:** Using `Modifier.alpha()` or `Modifier.scale()` with high-frequency animation states (e.g., infinite transitions) causes the entire Composable function to recompose on every frame. In complex screens, this leads to significant frame drops.
**Action:** Always migrate high-frequency animations to `Modifier.graphicsLayer { ... }`. This defers state reads to the drawing phase, keeping the main thread free for critical UI tasks.
