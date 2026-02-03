## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-30 - Enhanced Input Feedback Patterns
**Learning:** High-priority form fields benefit from immediate visual feedback as they approach limits. Using semantic colors for character counters (warning at 80%, error at 100%) and providing a one-tap 'Clear' button with haptic feedback significantly improves the perceived responsiveness and ease of use for profile management.
**Action:** Implement dynamic character counter coloring and 'Clear' buttons for all primary user-editable fields using the centralized `ProdyDesignTokens.SemanticColors`.
