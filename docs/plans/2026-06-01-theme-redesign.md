# Prody Theme System Redesign — Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Transform Prody's theme from a bloated, inconsistent collection of 5+ overlapping systems into a single cohesive, modern design language that cascades automatically to all 47 screens.

**Architecture:** Consolidate Color.kt, Theme.kt, Type.kt, Shape.kt, Tokens.kt, Dimensions.kt, and ProdyDesignTokens.kt into a unified theme system using Material3 dynamic theming with extended CompositionLocals. Delete all deprecated systems.

**Tech Stack:** Jetpack Compose Material3, CompositionLocalProvider, OKLCH color space (via manual conversion), Poppins + one serif accent font

---

## Audit Summary

### Critical Issues Found

1. **5 overlapping design token systems** — `ProdyTokens`, `ProddyDimens`, `ProdyDesignTokens`, `ProdySpacing`, `ProdyElevation` all define spacing/elevation/colors with different naming conventions
2. **Color.kt: 360 lines, 200+ top-level vals** — no organization, duplicate semantics (`ProdyWarning` vs `ProdyTokens.SemanticColors.warning`), screen-specific colors that should be theme-scoped
3. **Type.kt: 862 lines, 40+ named TextStyle vals** — all Poppins (zero personality), monotonous, no serif accent for wisdom/letter content
4. **Shape.kt: 447 lines, 50+ named shapes** — many duplicates (`CardShape` = `CardShapeDefault`), redundant with `ProdyTokens.Radius`
5. **No OKLCH** — all colors are raw hex with no perceptual uniformity
6. **Deprecated APIs** — `window.statusBarColor` is deprecated, `ProddyDimens`/`ProdyDesignTokens` still exist as dead weight

### Design Direction

**Register:** Product (app UI — design serves the product, not the other way around)

**Scene:** A person opening Prody at 11pm after a long day, in bed, dim room light. They want calm, focus, warmth — not stimulation, not gaming flash. The app should feel like a warm lamp on a rainy night.

**Color Strategy:** Committed — one saturated forest green carries 30-60% of key surfaces (primary actions, streaks, active states). Warm amber as accent for highlights. Everything else is tinted neutrals.

**Typography Strategy:** Poppins for UI, but add a serif accent for wisdom/letter/quote content to give those sections gravitas. The contrast between sans-serif UI and serif content creates visual hierarchy without relying on color alone.

**Shape Strategy:** Simplify from 50+ named shapes to 8 core shapes. Use `ProdyTokens.Radius` exclusively.

---

## Phase 1: Foundation — Consolidate & Clean (Safe, No Visual Changes)

### Task 1: Delete deprecated token systems

**Files:**
- Delete: `app/src/main/java/com/prody/prashant/ui/theme/Dimensions.kt` (ProddyDimens + ProdySpacingCombinations, both @Deprecated)
- Modify: `app/src/main/java/com/prody/prashant/ui/theme/ProdyDesignTokens.kt` — mark entire file @Deprecated, add migration note

**Step 1:** Verify no external references to ProddyDimens or ProdySpacingCombinations exist (we already checked — only self-references)

**Step 2:** Delete Dimensions.kt

**Step 3:** Search for any remaining imports of ProddyDimens, ProdySpacingCombinations, ProdyDesignTokens across all .kt files and replace with ProdyTokens equivalents

**Step 4:** Commit: `refactor(theme): remove deprecated Dimensions.kt and ProdyDesignTokens dead code`

### Task 2: Consolidate Theme.kt spacing/elevation into ProdyTokens

**Files:**
- Modify: `app/src/main/java/com/prody/prashant/ui/theme/Theme.kt` — remove ProdySpacing/ProdyElevation data classes and CompositionLocals, replace all usages with ProdyTokens.Spacing/ProdyTokens.Elevation
- Modify: `app/src/main/java/com/prody/prashant/ui/theme/Tokens.kt` — ensure Spacing and Elevation objects match ProdySpacing/ProdyElevation exactly

**Step 1:** Find all usages of `LocalProdySpacing` and `LocalProdyElevation` and `ProdyTheme.spacing` / `ProdyTheme.elevation`

**Step 2:** Replace each with direct ProdyTokens.Spacing/ProdyTokens.Elevation references

**Step 3:** Remove ProdySpacing, ProdyElevation, LocalProdySpacing, LocalProdyElevation from Theme.kt

**Step 4:** Commit: `refactor(theme): consolidate spacing/elevation into ProdyTokens, remove duplicate CompositionLocals`

### Task 3: Remove duplicate color aliases from Color.kt

**Files:**
- Modify: `app/src/main/java/com/prody/prashant/ui/theme/Color.kt` — remove legacy/compatibility section (lines 86-113), remove all `Journal*Light/Dark` aliases that just point to Prody* equivalents, remove `TimeCapsule*Light/Dark` aliases, remove `XpBar*` aliases, remove `SuccessGreen/ErrorRed/WarningAmber/InfoBlue` aliases

**Step 1:** Search for all usages of each alias being removed across the codebase

**Step 2:** Replace each alias usage with the canonical Prody* name

**Step 3:** Remove the alias definitions from Color.kt

**Step 4:** Commit: `refactor(theme): remove duplicate color aliases, canonical names only`

---

## Phase 2: Visual Redesign — Color System (Changes Visual Appearance)

### Task 4: Redesign Color.kt with OKLCH-based palette

**Files:**
- Rewrite: `app/src/main/java/com/prody/prashant/ui/theme/Color.kt`

**Design:** Convert all hex colors to OKLCH-equivalent values for perceptual uniformity. The palette should be:
- **Primary:** Deep forest green (OKLCH equivalent of #2E7D32) — carries 30-60% of action surfaces
- **Secondary/Accent:** Warm amber (#FFA000) — used sparingly for highlights, streaks, CTAs
- **Neutrals:** Warm-tinted grays (not pure gray) — green-tinted in light, warm-tinted in dark
- **Semantic:** Success (green), Warning (amber), Error (warm red), Info (cool blue) — all in OKLCH
- **Haven:** Warm cream/rose palette — preserved for chat UI
- **Streak:** Fire gradient (orange → red) — preserved for gamification

**Step 1:** Define the new OKLCH color palette in Color.kt with clear sections

**Step 2:** Update LightColorScheme and DarkColorScheme in Theme.kt to use new colors

**Step 3:** Verify screens still compile (no removed color names that are still referenced)

**Step 4:** Commit: `feat(theme): redesign color palette with OKLCH-based warm tinted neutrals`

### Task 5: Add serif accent font for wisdom/letter content

**Files:**
- Modify: `app/src/main/java/com/prody/prashant/ui/theme/Type.kt` — add a serif FontFamily (e.g., Lora, Merriweather, or Noto Serif) and update wisdom/letter text styles to use it
- Add: font resource files for the serif font (download .ttf files)

**Step 1:** Choose a serif font that complements Poppins (Lora is ideal — open source, readable, warm)

**Step 2:** Add Lora font files to `app/src/main/res/font/`

**Step 3:** Create `SerifFamily` FontFamily in Type.kt with safe loading (same pattern as PoppinsFamily)

**Step 4:** Update all wisdom text styles (WisdomHeroStyle, WisdomLargeStyle, LetterGreetingStyle, LetterBodyStyle, LetterQuoteStyle, QuoteTextStyle) to use SerifFamily

**Step 5:** Commit: `feat(theme): add Lora serif accent font for wisdom and letter content`

### Task 6: Simplify Shape.kt — reduce from 50+ to 8 core shapes

**Files:**
- Rewrite: `app/src/main/java/com/prody/prashant/ui/theme/Shape.kt`

**Design:** Keep only:
1. `ProdyShapes` (Material3 Shapes object — xs/sm/md/lg/xl)
2. `CardShape` (16dp)
3. `ButtonShape` (12dp)
4. `PillShape` (50%)
5. `BottomSheetShape` (top corners only)
6. `DialogShape` (24dp)
7. `AvatarShape` (CircleShape)
8. `TextFieldShape` (12dp)

All screen-specific shapes (QuickActionTileShape, WisdomCarouselCardShape, etc.) become inline usages of these core shapes or ProdyTokens.Radius references.

**Step 1:** Find all usages of shapes being removed across the codebase

**Step 2:** Replace each with the appropriate core shape (most are just `RoundedCornerShape(16.dp)` which is `CardShape`)

**Step 3:** Rewrite Shape.kt with only the 8 core shapes

**Step 4:** Commit: `refactor(theme): simplify shapes from 50+ to 8 core shapes`

---

## Phase 3: Theme Integration — CompositionLocals & Extensions

### Task 7: Create extended theme with CompositionLocals for feature-specific colors

**Files:**
- Create: `app/src/main/java/com/prody/prashant/ui/theme/ProdyExtendedTheme.kt`

**Design:** Create CompositionLocals for:
- `LocalHavenColors` — warm cream/rose palette for Haven chat
- `LocalStreakColors` — fire gradient palette for gamification
- `LocalMoodColors` — mood emoji colors
- `LocalProdyTypography` — extended typography (wisdom, letter, stat styles)

This replaces the scattered top-level color vals in Color.kt with theme-aware, dark-mode-aware colors.

**Step 1:** Define ProdyHavenColors, ProdyStreakColors, ProdyMoodColors data classes

**Step 2:** Create light/dark instances

**Step 3:** Create CompositionLocals and provide them in ProdyTheme

**Step 4:** Migrate Haven screens to use LocalHavenColors.current instead of HavenBackgroundLight/Dark

**Step 5:** Commit: `feat(theme): add CompositionLocal-based extended theme for Haven, Streak, Mood`

### Task 8: Update Theme.kt — remove deprecated status bar API, clean up

**Files:**
- Modify: `app/src/main/java/com/prody/prashant/ui/theme/Theme.kt`

**Step 1:** Replace deprecated `window.statusBarColor` / `window.navigationBarColor` with `enableEdgeToEdge()` from `androidx.activity`

**Step 2:** Remove `ProdyTheme` object and replace all `ProdyTheme.spacing`/`ProdyTheme.elevation` with `ProdyTokens.Spacing`/`ProdyTokens.Elevation` (already done in Task 2, but verify)

**Step 3:** Commit: `fix(theme): replace deprecated status bar API with enableEdgeToEdge()`

---

## Phase 4: Screen-by-Screen Polish (Post-Theme)

After the theme foundation is solid, individual screens can be polished. Priority order:

1. **HomeScreen** — first thing users see
2. **JournalScreen + NewJournalEntryScreen** — core feature
3. **HavenChatScreen** — AI companion, uses Haven palette
4. **ProfileScreen** — identity/gamification hub
5. **OnboardingScreen** — first impression
6. All remaining screens (lower priority)

Each screen polish would be a separate task with:
- Audit current layout for spacing inconsistencies (replace hardcoded dp with ProdyTokens)
- Verify dark mode rendering
- Check accessibility contrast ratios
- Add micro-interactions where appropriate

**NOTE:** Without a working build environment, screen-level changes must be syntactically correct but cannot be visually verified. The theme-level changes in Phases 1-3 cascade automatically.

---

## Verification Strategy

Since we cannot build the app:
1. Every file change must be syntactically valid Kotlin
2. All import references must resolve correctly
3. No removed symbols can have remaining references (grep verification)
4. Create a `THEME_MIGRATION.md` doc listing all renamed/removed symbols and their replacements