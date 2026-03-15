## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Semantic Tooltips for Icon Buttons
**Learning:** Tooltips for icon-only buttons should serve as the primary accessibility label if an explicit one isn't provided. This ensures that visual users and screen reader users receive identical context. Wrapping core components like `ProdyIconButton` with `TooltipBox` ensures this pattern is enforced globally.
**Action:** Integrate `TooltipBox` into base icon button components. Map the `tooltip` property to `contentDescription` in the `semantics` block of the interactive surface to maintain a single source of truth for the button's intent.
