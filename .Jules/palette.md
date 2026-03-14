## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Standardized Accessible IconButtons
**Learning:** Icon-only buttons often lack discoverability for new users and can be confusing in complex interfaces. Standardizing on a component that integrates Material 3 `TooltipBox` and `PlainTooltip` alongside consistent accessibility semantics ensures that secondary actions are both accessible to screen readers and discoverable for sighted users via long-press or hover.
**Action:** Always use `ProdyIconButton` instead of the standard Material `IconButton`. Ensure every instance provides a descriptive `tooltip` parameter, which automatically doubles as the `contentDescription` for accessibility, maintaining a single source of truth for action descriptions.
