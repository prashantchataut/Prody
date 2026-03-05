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

## 2024-05-24 - Thread-Safe Concurrent DAO Access
**Context:** `HomeViewModel.kt`'s `fetchDailyContentConcurrently`.
**Learning:** While `async` launches coroutines concurrently, executing multiple Room DAO calls within a `coroutineScope` on the default `ViewModel` dispatcher (Main) still risks blocking the main thread if the database connection pool is saturated or if the operations are not explicitly offloaded. Explicitly wrapping these parallel calls in `withContext(Dispatchers.IO)` is essential for ensuring they remain off the main thread.
**Action:** Always wrap parallel database operations (using `async`) in `withContext(Dispatchers.IO)` to guarantee main-thread safety and optimize disk I/O performance.

## 2024-05-24 - State Invalidation in Complex Lists
**Context:** `HomeScreen.kt` dashboard.
**Learning:** Large `LazyColumn` layouts with many heterogeneous items (Header, Overview, Charts, Summary) can suffer from unnecessary item recompositions during state updates if items don't have stable identities. Assigning explicit `key`s to `item` and `items` blocks significantly improves scroll performance and reduces rendering overhead by helping Compose identify which components truly need updating.
**Action:** Always provide stable, unique `key`s for items in `LazyColumn` and `LazyRow`, especially when those items are bound to complex, frequently updating reactive state.
