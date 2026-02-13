# Canonical Component Usage

Use these variants as the only approved patterns for new UI work.

## Buttons

Source: `ui/components/ProdyButton.kt`

- `ProdyPrimaryButton`: primary CTA.
- `ProdySecondaryButton`: secondary action.
- `ProdyOutlinedButton`: lower-emphasis, bordered action.
- `ProdyGhostButton`: tertiary text action.
- `ProdyIconButton`: icon-only action using tokenized touch targets.

Guidelines:
- Use `MaterialTheme.colorScheme` semantic roles.
- Use `ProdyDesignTokens.TouchTarget` for minimum sizing.
- Use `ProdyDesignTokens.Spacing/IconSize/Radius` for layout metrics.

## Cards

Source: `ui/components/ProdyCard.kt`

- `ProdyCard`: standard container.
- `ProdyElevatedCard`: tonal hierarchy (without ad-hoc shadow hacks).
- `ProdyOutlinedCard`: neutral separation via outline role.
- `ProdyFeaturedCard`: primary-container emphasis.
- `ProdyPremiumCard` and `ProdyPremiumGradientCard`: richer spotlight surfaces.
- `ProdyNotificationCard`: accent-edge content card.

Guidelines:
- Use semantic container/on-container colors.
- Use `ProdyDesignTokens.Radius/Elevation/Spacing` only.

## Chips

Source: `ui/components/ProdyChip.kt`

- `ProdyFilterChip`: multi-select filtering.
- `ProdySelectionChip`: single-select option.
- `ProdyInputChip`: removable/interactive entity.
- `ProdyAssistChip`: action suggestion.

Guidelines:
- Respect tokenized min height and icon sizes.
- Rely on `secondaryContainer`, `primary`, `surface`, and outline roles.

## List rows

Source: `ui/components/LeaderboardRow.kt` (and future shared row abstractions)

- Keep row paddings, avatar size, and icon scale tokenized.
- Use role-based surface/onSurface/onSurfaceVariant colors.
- Avoid row-level hardcoded hex colors; move decorative palettes into theme tokens first.

## Light / dark / dynamic mode validation

For every component update:
1. Check light mode (`ThemeMode.LIGHT`)
2. Check dark mode (`ThemeMode.DARK`)
3. Check dynamic color path on Android 12+ (`dynamicColor = true`)
4. Confirm contrast and hierarchy using semantic roles (not fixed hex values)
