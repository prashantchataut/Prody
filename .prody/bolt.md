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

## 2026-05-22 - Optimized Drawing and Scope Ambiguity
**Context:** Animation refactoring in `MagicalEffects.kt` and `MainActivity.kt`.
**Learning:** While refactoring animations to use `graphicsLayer` and `drawBehind` significantly reduces recomposition, it introduces scope ambiguity. Properties like `alpha` and `size` in these scopes often conflict with similarly named `Modifier` extension functions or parameters in the outer Composable scope. This leads to compilation errors where the compiler tries to call the extension function instead of assigning to the scope's property.
**Action:** Always use explicit `this.` receivers (e.g., `this.alpha`, `this.size`) or capture the scope's property into a local variable (e.g., `val canvasSize = this.size`) within `graphicsLayer` and `drawBehind` blocks to ensure correct resolution and avoid "Function invocation expected" or "Assignment type mismatch" errors.

## 2026-05-22 - Indicator Drawing Optimization
**Context:** `ProdyProgressIndicator` in `OnboardingScreen.kt`.
**Learning:** Complex repeated UI indicators that animate properties causing layout changes (like `width`) are extremely expensive when implemented as individual `Box` components. Refactoring to a single `Canvas` drawing call using `drawBehind` and interpolating values manually in the drawing phase eliminates layout and recomposition cycles entirely for the animation.
**Action:** For performance-critical indicators, use a single `Canvas` and `animateFloatAsState` to drive the drawing logic rather than a collection of individual Composables.
