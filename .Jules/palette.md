## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Standardized Component Rollout
**Learning:** Establishing a custom design system component (like `ProdyIconButton`) is only effective if it's consistently applied. Replacing standard Material components in core layouts like `ProdyHeader` ensures that all screens benefit from custom haptics, animations, and accessibility features (like tooltips) without individual screen refactors.
**Action:** When enhancing base components, identify high-visibility areas where standard components are still used and replace them to ensure a unified "Prody" experience across the app.
