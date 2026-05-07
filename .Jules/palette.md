## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Semantic Feedback and Design System Alignment
**Learning:** Visual and tactile feedback significantly improves the journaling experience. Using semantic colors (error/warning/success) for word counts provides immediate guidance on entry depth. Aligning all interactive elements (IconButton -> ProdyIconButton, TextButton -> ProdyGhostButton) ensures consistent haptics and animations across the app.
**Action:** Always use established design system components (`ProdyIconButton`, `ProdyGhostButton`) for new UI and implement semantic color logic for high-importance metrics like entry length.
