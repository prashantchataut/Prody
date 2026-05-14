## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2026-03-27 - Balancing Standardization and Delight in Empty States
**Learning:** Standardizing UI components like empty states across the app is crucial for consistency, but it shouldn't come at the cost of existing "delight" elements. The original manual empty state had an icon in its action button which was lost during the initial refactor to a shared component.
**Action:** When refactoring feature-specific UI to shared design system components, always perform a "delight audit" to ensure features like button icons or unique animations are preserved. Enhance the shared components to support these features if they are missing.
