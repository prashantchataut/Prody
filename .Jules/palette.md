## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-31 - Optimized Micro-Animations with Tooltips
**Learning:** In Jetpack Compose, micro-animations like scale should be applied via `Modifier.graphicsLayer` with direct state access inside the lambda to bypass recomposition. Combining these optimized interactions with Material 3 `TooltipBox` and `PlainTooltip` (with `isPersistent = false` for mobile) significantly elevates the "premium" feel and accessibility of icon-only components.
**Action:** When enhancing interactive components, prefer `graphicsLayer` for transformations and always provide optional tooltip support for icon-only buttons to improve clarity and accessibility.
