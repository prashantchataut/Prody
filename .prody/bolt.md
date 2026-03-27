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

## 2024-05-24 - Efficient Data Seeding in Onboarding
**Context:** `OnboardingViewModel.kt`
**Learning:** Initializing multiple database entities (Profile, Stats, Achievements, Vocabulary, Quotes, etc.) during onboarding without a transaction caused a massive disk I/O bottleneck, leading to a "frozen" UI state during the transition to the home screen. Room's `database.withTransaction` is essential for atomic, high-performance mass inserts.
**Action:** Always wrap multi-entity initialization or bulk data seeding within a single Room transaction to minimize disk synchronization overhead.

## 2024-05-24 - Canvas-Based UI Indicators
**Context:** `ProgressIndicators.kt` and `OnboardingScreen.kt`.
**Learning:** Using a `Row` of multiple `Box` composables for page indicators creates unnecessary layout nodes and triggers expensive layout passes during page swipes as dot widths animate. A single `Canvas` drawing all dots based on an animated float index is significantly more performant and smoother.
**Action:** Prefer `Canvas`-based drawing for multi-state UI indicators like page dots or segmented progress bars to maintain 60fps during complex interactions.

## 2026-03-27 - Compose Animation State Read Deferral
**Context:** Bottom navigation animations in `MainActivity.kt`.
**Learning:** Using property delegation (`by`) for animation states inside a Composable causes the scope where the delegation is defined to recompose whenever the state changes. For high-frequency animations (like breathing pulses), this can cause severe lag.
**Action:** Access the animation state directly via `.value` inside a `graphicsLayer` lambda (e.g., `graphicsLayer { alpha = alphaState.value }`). This guarantees the state read is deferred to the drawing phase, bypassing recomposition entirely for that component's parents.

## 2026-03-27 - Defensive Layout Calculations
**Context:** `MoodChart` in `HomeScreen.kt`.
**Learning:** Layout calculations that depend on list sizes (like `stepX = width / (data.size - 1)`) are prone to division-by-zero crashes if the data is empty or contains a single item.
**Action:** Always implement defensive checks for list-dependent layout math. For charts, use `if (data.size > 1) ... else 0f` to ensure stability even with minimal user data.

## 2026-03-27 - Reactive UI Binding Gap
**Context:** `HomeScreen.kt` and `HomeViewModel.kt`.
**Learning:** Sophisticated features like "Active Progress Layer" or "Dual Streak System" appear broken if the UI isn't explicitly bound to the corresponding state fields.
**Action:** Rigorously map ViewModel `UiState` fields to production-grade components (like `NextActionCard` and `TodayProgressCard`) instead of using hardcoded placeholders, ensuring the UI reflects the full intelligence of the domain layer.
