## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Centralized Tooltip Support for Icon Buttons
**Learning:** Enhancing base design system components (like `ProdyIconButton`) with optional UX features (like tooltips) significantly improves app-wide accessibility and clarity with minimal overhead. It also ensures that these small touches of "delight" are applied consistently wherever the component is used.
**Action:** When adding common UX patterns (tooltips, specific haptics, loading states), prioritize implementing them in the base component rather than at the call-site to maintain a single source of truth for the design system's behavior.
