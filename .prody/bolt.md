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

## 2024-05-20 - Performance-Critical Deferred State Reading
**Context:** Frequent UI animations (breathing, pulsing) in `MainActivity` and `MagicalEffects`.
**Learning:** Using `by animateFloatAsState` and reading the value in the composition scope (e.g., passing it to `Modifier.scale(scale)`) triggers recomposition on every frame.
**Action:** Use `Modifier.graphicsLayer { ... }` and read the `.value` of the state-backed animation directly inside the lambda. This defers the state read to the drawing phase, bypassing recomposition and layout entirely for significantly better 60fps performance.

## 2024-05-20 - Canvas-based Progress Indicators
**Context:** `ProdyProgressIndicator` in `OnboardingScreen.kt`.
**Learning:** A `Row` of individual `Box` elements with animated widths causes expensive layout recalculations and multiple recompositions across the list on every frame of the transition.
**Action:** Refactor complex repeated UI indicators to use a single `Canvas` drawing call. Animating element properties (width, color) purely in the drawing phase eliminates layout overhead and ensures buttery-smooth transitions even on lower-end devices.
