## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Standardized Accessible Tooltips
**Learning:** Icon-only buttons (like back buttons in headers) require clear textual hints for both sighted users (tooltips) and screen reader users (ARIA/Content Descriptions). Integrating these into a single property fallback (`contentDescription ?: tooltip`) ensures no interactive element is left unlabeled while reducing developer friction.
**Action:** Use the enhanced `ProdyIconButton` with the `tooltip` parameter for all icon-only actions. Ensure the `modifier` is passed to the `TooltipBox` (if present) to maintain correct layout anchoring in headers and lists.
