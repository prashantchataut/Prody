## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-28 - Tooltips and Consistent Input Clearing
**Learning:** Icon-only buttons (like "Clear") require tooltips for both accessibility (WCAG compliance) and to eliminate ambiguity for new users. Furthermore, maintaining consistent interaction patterns—such as providing "Clear" functionality for all text inputs (Name, Bio, etc.)—significantly reduces cognitive load and improves the "flow" of the editing experience.
**Action:** Always wrap `ProdyIconButton` in a `TooltipBox` when the action isn't immediately obvious from context. Ensure that similar input fields within the same screen offer a symmetrical set of quick-actions (Clear, Paste, etc.) to maintain a predictable interface.
