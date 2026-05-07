## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Contextual Help via Tooltips
**Learning:** Icon-only actions (like "Clear") can sometimes be ambiguous for sighted users. Providing optional tooltips via `TooltipBox` and `PlainTooltip` (using the app's `PoppinsFamily` font) enhances clarity without cluttering the UI.
**Action:** Use the enhanced `ProdyIconButton` with the `tooltip` parameter for ambiguous icon actions to provide immediate contextual feedback on long press or hover.

## 2025-01-26 - Clear Button Placement in Multi-line Inputs
**Learning:** For multi-line text inputs, a "Clear" button is most accessible and visually balanced when placed at the top-right of the field using `verticalAlignment = Alignment.Top` within a `Row` in the `decorationBox`.
**Action:** Follow this layout pattern for all multi-line inputs with clear functionality to ensure the button remains visible and accessible regardless of content length.
