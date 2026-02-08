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

## 2024-05-20 - GraphicsLayer Property Shadowing
**Context:** `Modifier.graphicsLayer { ... }` in `MainActivity.kt`.
**Learning:** Using local variable names like `alpha` or `scale` that shadow the properties of `GraphicsLayerScope` inside the lambda causes a compilation error ("'val' cannot be reassigned") because the compiler tries to assign to the local `val` instead of the scope's property.
**Action:** Always use unique names for animated state variables (e.g., `animatedAlpha`, `animatedScale`) when they are intended to be used within a `graphicsLayer { ... }` block.

## 2024-05-20 - Multi-indicator Layout Optimization
**Context:** `ProdyProgressIndicator` in `OnboardingScreen.kt`.
**Learning:** Animating widths of multiple individual items in a `Row` using `animateDpAsState` triggers expensive layout passes every frame as the items push each other.
**Action:** Use a single `Canvas` to draw the entire indicator row. By animating a single "active index" float and calculating all bar widths/colors during the drawing phase, you avoid recomposition and re-layout of the individual indicator bars.
