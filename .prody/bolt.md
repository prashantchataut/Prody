# BOLT'S JOURNAL - PRODY PERFORMANCE GUARDIAN

## 2025-05-15 - Animation Recomposition Optimization
**Context:** MainActivity Navigation Bar and MagicalEffects.kt
**Learning:** Using `Modifier.scale()` and `Modifier.alpha()` inside a composable with frequently updating state (like an `InfiniteTransition`) triggers recomposition of the scope where the state is read on every frame.
**Action:** Use `Modifier.graphicsLayer { ... }` instead. Reading state inside the `graphicsLayer` block defers state reading to the draw phase, avoiding recomposition entirely. Also, isolating frequent animations into their own small Composables (e.g., `HavenPulseIcon`) ensures that if recomposition *does* occur, it's limited to the smallest possible part of the UI tree.

## 2025-05-15 - Memoization of Static Lists
**Context:** MainActivity ProdyApp
**Learning:** Static lists (like navigation items) created directly inside a Composable are re-allocated on every recomposition.
**Action:** Wrap such allocations in `remember { ... }` to ensure they are only created once per composition lifecycle.
