## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Optimized Multi-line Clear Action
**Learning:** For multi-line text fields (like bios), positioning the 'Clear' action at the top-right () is more intuitive and accessible than centering it vertically. It maintains a consistent entry point for the action regardless of content length and prevents the button from drifting into the middle of the text block.
**Action:** When implementing 'Clear' functionality in multi-line s, use a  with  inside the .

## 2025-01-26 - Optimized Multi-line Clear Action
**Learning:** For multi-line text fields (like bios), positioning the 'Clear' action at the top-right (`Alignment.Top`) is more intuitive and accessible than centering it vertically. It maintains a consistent entry point for the action regardless of content length and prevents the button from drifting into the middle of the text block.
**Action:** When implementing 'Clear' functionality in multi-line `BasicTextField`s, use a `Row` with `verticalAlignment = Alignment.Top` inside the `decorationBox`.
