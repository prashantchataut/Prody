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

## 2026-02-01 - Canvas vs Row for Progress Indicators
**Context:** `ProdyProgressIndicator` in `OnboardingScreen.kt`.
**Learning:** Using a `Row` of individual `Box`es with `animateDpAsState` for width animations triggers frequent layout passes because changing width affects the position of all items.
**Action:** Refactor repeated indicators to a single `Canvas`. Animate the selection as a `Float` and calculate widths/colors in `DrawScope`. This keeps the component's total size constant and eliminates layout overhead.

## 2026-02-01 - Deferring State Reads to Draw Phase
**Context:** Bottom navigation animations in `MainActivity.kt` and `MagicalEffects.kt`.
**Learning:** Reading state values directly in the Composable body (e.g., `Modifier.scale(scale)`) triggers recomposition.
**Action:** Always use `Modifier.graphicsLayer { ... }` and read the state value *inside* the block (e.g., `this.scaleX = scale`). This defers the state read to the drawing phase, preventing the Composable from recomposing during every animation frame.
