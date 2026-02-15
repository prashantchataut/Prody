## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Visual Progress Feedback for Qualitative Inputs
**Learning:** For inputs that require a minimum quality or length (like journal entries or long-form reflections), a simple character count is often insufficient. Providing a visual progress bar towards the recommended "goal" encourages more descriptive input and makes the user feel a sense of progression towards a more valuable output (like AI insights).
**Action:** When an input has a recommended minimum length for optional features, use a `LinearProgressIndicator` to visualize progress towards that goal. Combine this with semantic color shifts on the count text to provide clear status feedback.
