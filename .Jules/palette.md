## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Accessible Icon Buttons with Tooltips
**Learning:** Icon-only buttons (like "Clear", "Delete", or "Settings") benefit from standard tooltips to provide visual context for sighted users while matching the screen reader experience. Material 3's `TooltipBox` should be wrapped around the interactive `Surface` to ensure the tooltip appears on long-press/hover without breaking the touch target.
**Action:** When adding icon-only buttons, use `ProdyIconButton` with the `tooltip` parameter. Ensure the `modifier` is applied to the `TooltipBox` (the root) to correctly handle layout properties like `weight` or `padding`.
