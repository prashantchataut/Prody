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

## 2024-05-24 - Canvas-based Progress Indicators
**Context:** `ProdyProgressIndicator` in `OnboardingScreen.kt`.
**Learning:** Using multiple `Box` composables for indicators that animate their width simultaneously causes multiple layout passes and recompositions. This is especially noticeable on lower-end devices during page transitions.
**Action:** Refactor repeated indicators to use a single `Canvas`. Use `animateFloatAsState` for the page index and calculate widths/colors via lerping in the draw phase. This keeps the animation logic purely in the drawing phase, bypassing the expensive layout and recomposition cycles.

## 2024-05-24 - UI-State Feature Binding
**Context:** `HomeScreen.kt` and `HomeViewModel.kt`.
**Learning:** Significant functionality was effectively "hidden" from the user because the UI was using hardcoded placeholders while the ViewModel already managed complex states like mood trends, mindful minutes, and intelligent greetings.
**Action:** Conduct a thorough audit of `UiState` vs UI implementation. Explicitly binding metrics like `moodTrend` (mapped from mood strings) and `mindfulMinutes` (derived from `totalReflectionTime`) immediately restores the "production" feel of the app and validates the underlying architecture.
