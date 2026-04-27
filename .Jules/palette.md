## 2025-01-26 - Unified Haptic Feedback for Buttons
**Learning:** High-quality mobile apps benefit significantly from consistent tactile feedback on primary interactions. Prody has a custom `rememberProdyHaptic()` utility that respects user settings and should be used in all design system interactive components.
**Action:** Always integrate `rememberProdyHaptic()` and `haptic.click()` or `haptic.selection()` into base interactive components (Buttons, Chips, ClickableCards) to ensure a "premium" feel across the entire app without duplicating logic.

## 2025-01-26 - Dynamic Form Validation Feedback
**Learning:** Providing real-time, visual feedback as users approach form constraints (like character limits) significantly reduces friction and errors. Semantic color shifts (warning at 80%, error at 100%) are more intuitive than simple counters.
**Action:** Implement dynamic character counters in all high-priority input forms. Use `ProdyDesignTokens.SemanticColors.warning` for the cautionary state to provide a consistent cross-app language for constraints.

## 2025-01-26 - Semantic Accessibility & Proper Pluralization
**Learning:** Micro-UX polish comes from details like correct pluralization (e.g., "1 word" vs "1 words") and providing semantic context for icon-only actions. Screen readers rely on these details to make the app inclusive and professional.
**Action:** Use `pluralStringResource` for all user-facing counts. Ensure every `ProdyIconButton` has a descriptive `contentDescription` resource, avoiding `null` unless the icon is purely decorative and accompanied by adjacent text.
