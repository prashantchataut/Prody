# Kairos Design System — Paper & Ink

## Direction
A warm editorial commonplace book. Daily vocabulary and reflection are a quiet
ritual, so the interface reads like paper: flat, warm, hairline-structured, with
one confident serif voice. Nothing floats, nothing glows, nothing is glass.

## Scene sentence
A person opens Kairos for two quiet minutes beside a window in the morning or
under a warm lamp at night; the interface should feel like a well-made notebook —
calm, legible, and composed — never ornamental.

## Dials
- Variance 5 · Motion 3 · Density 3
- One accent (vermilion), one gray family (warm paper), serif display, hairline structure.

## Anchors
- Editorial commonplace books: confident serif display, generous measure, quiet rules.
- Material 3: navigation, components, and interaction stay on-spec; brand expresses
  through color roles, the type scale, and shape.
- Native Android ergonomics: 48dp targets, system Back, edge-to-edge insets.

## Color strategy
Warm paper neutrals carry the interface. Vermilion marks primary action and
selection — the only accent. Deep forest is reserved for success states, oxblood
for errors. No purple, no indigo, no gradients, no pastels, no neon greens.

### Light — paper
- Ground: `#F4F0E8` · Surface: `#FBF8F1` · Container: `#E4DECD`
- Ink: `#221E17` · Soft ink: `#6E6759` · Faint ink: `#98907F`
- Hairline: `#D8D2C4`
- Accent (vermilion): `#B3401F` · Wash: `#F6E3DA` · On accent: `#FFF8F0`
- Success: deep forest `#33543F` · Error: oxblood `#B3261E`

### Dark — night paper
- Ground: `#171410` · Surface: `#1D1913` · Container: `#2E281D`
- Ink: `#EDE7DA` · Soft: `#A79E8C` · Faint: `#7C7465`
- Accent: `#E5855F` · Wash: `#4A2415` · Hairline: `#3A342A`
- Success: pale forest `#8FA98F`

## Typography
- Display and headline roles: bundled Lora (semi-bold), tight tracking (−0.2 to
  −0.8sp), reduced line height. Lora is the voice of the word, the quote, and the
  reflection.
- Body, titles, and labels: native Android sans, sentence case everywhere.
- No uppercase eyebrows or kickers above headings. The heading carries its own weight.
- Body minimum 14sp; reading text 17–20sp with generous line height.

## Shape & surfaces
- Radii capped at 12–16dp: controls 12, containers 16. No pills except tiny chips.
- Flat paper panels: solid surface/container fill + 1px hairline (`outlineVariant`).
  No shadows, no blur, no gradient borders, no specular overlays.
- The `KairosGlassSurface` name is retained for compatibility; the material is paper.

## Navigation
- Solid paper bar/rail with a hairline rule. No floating glass capsule, no pill indicator.
- Selected destination: vermilion text and a short 2dp vermilion underline.

## Layout
- Edge-to-edge flat ground (`colorScheme.background`).
- Content measure capped on tablets.
- Four top-level destinations: Today, Learn, Reflect, Library.
- Today is a two-page book spread — word folio and thought folio — each opening
  with a hairline rule (no labels, no section numbers).

## Motion
- 100–150ms press feedback.
- 200–280ms state changes.
- 320ms maximum for navigation or panel transitions.
- Reduced-motion mode removes spatial movement while preserving fades and state feedback.

## Accessibility
- 48dp minimum interactive targets.
- Contrast at least WCAG AA.
- Semantics describe state and action, not decoration.
- Large font layouts reflow without clipping.
- Selected navigation state is conveyed by text, icon, and semantics, not color alone.
