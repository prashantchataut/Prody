## 2024-07-25 - MainActivity Startup Optimization

**Context:** MainActivity.kt, the app's entry point.

**Learning:** The splash screen was blocked by a synchronous `preferencesManager.onboardingCompleted.first()` call, causing a noticeable delay. This is a classic main-thread I/O violation that directly impacts startup time.

**Action:** Decoupled the preference loading from the Activity's lifecycle by moving the logic into a `MainViewModel`. The Activity now observes a `StateFlow<MainActivityUiState>` and the splash screen's `setKeepOnScreenCondition` is driven by the `Loading` state of the `StateFlow`. This ensures the UI thread is never blocked and the app remains responsive during startup.
