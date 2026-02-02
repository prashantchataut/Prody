# BOLT ⚡ PERFORMANCE JOURNAL

## 2025-05-14 - Navigation Animation Performance
**Context:** Bottom navigation bar and Haven FAB in `MainActivity.kt` and `MagicalEffects.kt`.
**Learning:** Infinite animations (`rememberInfiniteTransition`) that are read directly in the composition phase of a navigation bar cause the entire bar to recompose every frame (60+ times per second). This is especially problematic when multiple nav items have their own "glow" or "pulse" effects.
**Action:** Isolate animated components into their own small, private composables and use `graphicsLayer` (scaleX, scaleY, alpha) to apply animation values. This moves the animation updates from the Recomposition phase to the Draw phase, drastically reducing CPU usage. Also, ensure animations only run when the component is active/visible.

## 2025-05-14 - Thread Blocking in ViewModel Initialization
**Context:** `VocabularyReviewViewModel.kt` and `PreferencesManager.kt`.
**Learning:** Using `runBlocking` to fetch synchronous values from `DataStore` or `SharedPreferences` inside a ViewModel's `init` block (or as a property initializer) blocks the Main thread, increasing app startup time and causing jank during navigation.
**Action:** Remove `runBlocking` from shared managers. Fetch required values asynchronously within a `viewModelScope.launch` block or observe them as a Flow/State.

## 2025-05-14 - Mockup Content in Production Code
**Context:** `HomeScreen.kt` was showing static data despite a fully functional `HomeViewModel`.
**Learning:** Some parts of the UI might be left as "Revamped" mockups that don't actually bind to the domain data, leading to a perceived lack of functionality.
**Action:** Always verify that UI components are correctly collecting and observing state from their respective ViewModels. Use `collectAsStateWithLifecycle()` to ensure efficient and lifecycle-aware state updates.
