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

## 2026-02-16 - GraphicsLayer and Canvas Optimizations
**Context:** Navigation animations, onboarding progress indicator, and home screen dashboard.
**Learning:** Using `.graphicsLayer { ... }` for `scale` and `alpha` animations prevents invalidating the layout/composition phases, keeping work on the render thread. For repeated UI elements like progress dots, refactoring from multiple Composables (`Row` + `Box`es) to a single `Canvas` significantly reduces overhead by eliminating layout passes during animations.
**Action:** Prefer `graphicsLayer` for frequently changing properties and `Canvas` for group animations to maintain 60fps.

## 2026-02-16 - Bridging the Feature Gap
**Context:** `HomeScreen.kt` and `HomeViewModel.kt`.
**Learning:** sophistication in the ViewModel layer (e.g., Soul Layer intelligence, mood mapping, dual streaks) remains hidden if the UI relies on hardcoded placeholders. Explicitly binding every dashboard metric to the `UiState` is the most effective way to improve perceived application completeness.
**Action:** Always verify that every field in `UiState` is being utilized by the corresponding Composable.
