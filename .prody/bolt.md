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

## 2024-05-20 - Canvas vs Component Recomposition
**Context:** `ProdyProgressIndicator` in `OnboardingScreen.kt`.
**Learning:** Animating the width of multiple `Box` components in a `Row` triggers recomposition and layout for every dot on every frame. For smooth, high-frequency animations like progress dots, a single `Canvas` is significantly more efficient.
**Action:** Use `Canvas` for multi-item indicators that animate frequently. Animate the target state as a `Float` and calculate item properties (width, color) within the `DrawScope` to keep the animation purely in the drawing phase.

## 2024-05-20 - Transaction Boundaries & Hybrid Storage
**Context:** `OnboardingViewModel.kt` refactor.
**Learning:** Room's `withTransaction` only ensures atomicity for SQLite operations. Including DataStore or SharedPreferences updates inside this block can lead to perceived success in one system while the other fails, and unnecessarily holds the DB transaction open during slower I/O.
**Action:** Keep Room transactions strictly for DAO operations. Perform DataStore or other I/O updates outside the transaction block, typically after successful completion of the database work.
