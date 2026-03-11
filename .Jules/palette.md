## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-27 - Accessible Icon Button Pattern
**Learning:** Icon-only buttons often lack clarity for screen readers and new users. By centralizing tooltip and accessibility logic in a base `ProdyIconButton`, we ensure that every action has a fallback label without cluttering the visual UI. Standardizing on `TooltipBox` provides both a visual hint (on long-press) and an accessibility announcement.
**Action:** Use `ProdyIconButton` for all standalone icon actions. Always provide the `tooltip` parameter, which automatically serves as the `contentDescription` for accessibility if no explicit description is provided, while keeping the internal icon decorative to avoid redundant announcements.
