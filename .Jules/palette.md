## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Accessible Icon Buttons with Tooltips
**Learning:** Icon-only buttons (like Media, Voice, List) in complex editors can be ambiguous. Wrapping the interactive `Surface` in Material 3's `TooltipBox` provides essential visual hints on long-press/hover without cluttering the UI. Standardizing these into a `ProdyIconButton` component ensures consistent accessibility semantics and a "premium" feel.
**Action:** Use `ProdyIconButton` for all icon-only actions. Ensure `tooltip` and `contentDescription` are both provided, and use `MaterialTheme.typography.labelSmall` for tooltips to maintain visual harmony.
