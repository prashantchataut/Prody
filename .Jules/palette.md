## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Component Modifier Hoisting
**Learning:** When wrapping a base component (like a button) in a higher-order composable (like a Tooltip), the `modifier` parameter must be applied to the root-most component. Failing to do so prevents layout-specific modifiers (like `Modifier.weight()`) from working correctly and can lead to silent layout failures or compilation errors.
**Action:** Always identify the root component in a custom Composable and apply the `modifier` parameter there to ensure full compatibility with Jetpack Compose layout systems.
