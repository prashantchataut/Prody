## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Accessible Tooltips for Icon Buttons
**Learning:** Icon-only buttons can be ambiguous without visual labels. Integrating `TooltipBox` into the base `ProdyIconButton` component ensures that all icon actions are self-descriptive on long-press while maintaining a clean UI. Crucially, the `modifier` must be applied to the `TooltipBox` to avoid breaking layout constraints (like `Modifier.weight`).
**Action:** Always provide a `tooltip` parameter when using `ProdyIconButton` for non-obvious actions. Ensure the tooltip uses `PoppinsFamily` to stay consistent with the Prody brand voice.
