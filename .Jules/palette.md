## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-30 - Semantic Accessibility & Haptic Consistency
**Learning:** UX delight comes from the intersection of tactile feedback and semantic clarity. Migrating to `ProdyIconButton` ensures haptic consistency, while using `pluralStringResource` and specific accessibility labels (e.g., "Pause" vs "Cancel") ensures the interface is technically and grammatically accessible.
**Action:** Use `ProdyIconButton` for all icon-only interactions. Always implement `<plurals>` for user-facing counters and ensure ARIA/content descriptions precisely match the current action state.
