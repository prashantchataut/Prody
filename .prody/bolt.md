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

## 2026-02-13 - Canvas-based Progress Indicators
**Context:** `OnboardingScreen.kt` and `ProdyProgressIndicator`.
**Learning:** Using a `Row` of `Box`es with individual `animateDpAsState` for a multi-dot progress indicator causes numerous recompositions and layout passes during page transitions.
**Action:** Refactor multi-element indicators to a single `Canvas`. Use a single `animateFloatAsState` for the current page index and calculate individual element properties (width, color) using distance-based logic and `lerp` within the `DrawScope`. This keeps the entire animation in the drawing phase, drastically reducing CPU/GPU overhead.
