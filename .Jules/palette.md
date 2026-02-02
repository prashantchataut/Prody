## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Interactive Tile Polish
**Learning:** Custom interactive components like dashboard tiles often lack basic accessibility roles and tactile feedback compared to standard buttons. Implementing a scale animation (0.98f) combined with haptic feedback and explicit semantic roles (Role.Button) significantly elevates the "premium" feel of a dashboard.
**Action:** Always check custom clickable Boxes and Surfaces for missing Role.Button semantics and haptic feedback, and apply a standard bouncy scale animation to provide immediate visual confirmation of the interaction.
