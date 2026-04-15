## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Tactile Feedback for Custom Interactive Surfaces
**Learning:** Custom interactive surfaces in Compose (like  or  with ) often miss the default tactile feedback provided by Material components. Standardizing these with  and a subtle scale animation (0.98f) using  ensures a consistent "premium" feel and better affordance for users.
**Action:** When implementing custom interactive components that don't use design system buttons, always manually integrate haptic feedback and a press-triggered scale animation to maintain interaction parity across the app.

## 2025-01-26 - Tactile Feedback for Custom Interactive Surfaces
**Learning:** Custom interactive surfaces in Compose (like `Box` or `Surface` with `.clickable`) often miss the default tactile feedback provided by Material components. Standardizing these with `rememberProdyHaptic().click()` and a subtle scale animation (0.98f) using `graphicsLayer` ensures a consistent "premium" feel and better affordance for users.
**Action:** When implementing custom interactive components that don't use design system buttons, always manually integrate haptic feedback and a press-triggered scale animation to maintain interaction parity across the app.
