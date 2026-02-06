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

## 2024-05-22 - Infinite Animation Optimization
**Context:** Bottom navigation FAB pulse and  in  and .
**Learning:** Infinite animations that read state values directly in the  chain (e.g., ) cause the entire Composable and its siblings to recompose and re-layout every frame (60+ times per second). This is a major source of "mysterious" lag in complex apps.
**Action:** Always move animation state reads into a lambda-based modifier like  or . This ensures the animation only triggers the drawing phase, bypassing recomposition and layout entirely for significantly better performance.

## 2024-05-22 - Infinite Animation Optimization
**Context:** Bottom navigation FAB pulse and `NavigationBreathingGlow` in `MainActivity.kt` and `MagicalEffects.kt`.
**Learning:** Infinite animations that read state values directly in the `Modifier` chain (e.g., `.alpha(alpha)`) cause the entire Composable and its siblings to recompose and re-layout every frame (60+ times per second). This is a major source of "mysterious" lag in complex apps.
**Action:** Always move animation state reads into a lambda-based modifier like `Modifier.graphicsLayer { ... }` or `Modifier.drawBehind { ... }`. This ensures the animation only triggers the drawing phase, bypassing recomposition and layout entirely for significantly better performance.

## 2024-05-22 - Reactive State and Lifecycle Awareness
**Context:** `HomeScreen.kt` state observation.
**Learning:** Using `collectAsState()` in ViewModels that update frequently can lead to wasted CPU cycles when the app is in the background. Additionally, failing to bind all `UiState` fields to components leads to a perception of "broken" features.
**Action:** Standardize on `collectAsStateWithLifecycle()` for all Screen-level state observation. When revamping a screen, perform a "State-to-UI" audit to ensure every field in the `UiState` has a corresponding visual representation, fulfilling the functional promise of the Clean Architecture layer.
