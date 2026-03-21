## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Accessible Icon Buttons with Tooltips
**Learning:** Icon-only buttons (like 'Clear', 'Attach', or 'Record') are often ambiguous for screen reader users and sighted users alike. Wrapping them in a Material 3 `TooltipBox` and providing both a `tooltip` and a `contentDescription` significantly improves accessibility and discoverability without cluttering the UI.
**Action:** Use the enhanced `ProdyIconButton` for all icon-only interactions. Always provide a semantic string from `strings.xml` for both `tooltip` and `contentDescription` to ensure a consistent experience across different interaction modes.
