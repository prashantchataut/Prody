## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-03-03 - Accessible Tooltips for Icon-only Actions
**Learning:** Icon-only buttons (like back navigation or settings) often lack clear semantic meaning for screen readers and visual hints for power users. Standardizing on a custom `ProdyIconButton` that wraps Material 3's `TooltipBox` ensures every icon-only action has a non-persistent tooltip and a corresponding ARIA label/content description.
**Action:** Always prefer `ProdyIconButton` over standard `IconButton`. Ensure `tooltip` or `contentDescription` is provided to maintain accessibility and discoverability.
