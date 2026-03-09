## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-27 - Accessible Tooltips for Icon Buttons
**Learning:** Icon-only buttons often lack immediate clarity and accessibility. Integrating a conditional `TooltipBox` into the base `ProdyIconButton` component ensures that all such buttons can easily provide visual hints and default accessibility descriptions without code duplication.
**Action:** When creating or updating base UI components for icons, always include an optional `tooltip` parameter. Ensure it is used to automatically populate accessibility semantics (like `contentDescription`) when an explicit one is not provided.
