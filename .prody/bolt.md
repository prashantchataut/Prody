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

## 2024-05-21 - Drawing Layer Animation vs Recomposition
**Context:** Haven FAB pulse animation in `MainActivity.kt`.
**Learning:** Frequent animations (pulse, breathing) in high-level components like `NavigationBar` can cause the entire navigation tree to recompose every frame if using `Modifier.scale()` or `Modifier.alpha()`. This contributes significantly to "laggy throughout" reports.
**Action:** Isolate frequent animations into private Composables. Use `Modifier.graphicsLayer { ... }` to apply updates directly to the drawing layer, bypassing the Recomposition and Layout phases for a smooth 60fps experience.

## 2024-05-21 - Compose Stability in Domain Layer
**Context:** `HomeUiState` and `DualStreakStatus`.
**Learning:** Large UI states containing domain models often trigger unnecessary recompositions because the Compose compiler cannot guarantee the stability of classes from other modules or layers.
**Action:** Annotate UI-bound data classes and domain models with `@Immutable`. This explicitly tells the Compose compiler that these objects won't change after creation, enabling aggressive skipping of recompositions.
