## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Non-Intrusive Guidance with Tooltips
**Learning:** Icon-only buttons in dense UIs (like the Journal Editor) benefit from tooltips to provide clarity without cluttering the screen. Wrapping the interactive `Surface` in a `TooltipBox` ensures the tooltip triggers correctly on hover or long-press without interfering with primary touch mechanics.
**Action:** Use the enhanced `ProdyIconButton` for all secondary icon-only actions. Always pair with `contentDescription` for screen readers and `tooltip` for visual guidance, using `MaterialTheme.typography.labelSmall` for consistent sizing.
