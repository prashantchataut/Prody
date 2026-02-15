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

## 2024-05-20 - Canvas for Repeated Indicators
**Context:** `ProdyProgressIndicator` in onboarding.
**Learning:** Using multiple `Box` composables with `animateDpAsState` for a simple progress indicator creates unnecessary layout overhead and multiple recompositions. If there are 8+ dots, it triggers 8+ layout passes every frame of animation.
**Action:** Use a single `Canvas` for repeated indicators. Animate a single state (like the current page index) and interpolate dot properties (width, color) directly in the `DrawScope`. This keeps the entire animation in the drawing phase with zero layout cost.

## 2024-05-20 - GraphicsLayer vs Modifier ambiguity
**Context:** `MainActivity.kt` and `MagicalEffects.kt`.
**Learning:** When both `androidx.compose.ui.draw.alpha` (extension function) and `graphicsLayer` are used, assigning to `alpha` inside the `graphicsLayer` block can be ambiguous or fail if not handled correctly.
**Action:** Use `this@graphicsLayer.alpha` or ensure `this` is correctly scoped to `GraphicsLayerScope` when setting properties that share names with `Modifier.alpha`. This ensures the property is set on the layer rather than creating a new `Modifier`.
