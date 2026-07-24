# Kairos Design System

## Direction
A restrained, editorial product interface with liquid-glass-inspired navigation and controls. Content remains crisp and matte. Glass is reserved for floating navigation, compact toolbars, disclosures, and action clusters where translucency communicates layering.

## Scene sentence
A person opens Kairos for two quiet minutes beside a window in the morning or under a warm lamp at night; the interface should feel luminous and composed without looking ornamental.

## Anchors
- Apple Liquid Glass principles: hierarchy, harmony, edge-to-edge content, restrained color in controls.
- High-quality editorial reading apps: confident typography and generous measure.
- Native Android ergonomics: predictable actions, accessible targets, and responsive navigation.

## Color strategy
Restrained. Warm mineral neutrals carry the interface. Mineral indigo marks primary action and selection. Clay is a rare warmth accent. Teal is reserved for success and learned states.

### Light
- Background: Porcelain Mist, `#F7F6F2`.
- Surface: Milk Glass, `#FCFBF8`.
- Ink: `#202126`.
- Secondary ink: `#666970`.
- Primary: Mineral Indigo, `#495CC7`.
- Primary soft: `#E3E7FF`.
- Warm accent: Clay, `#C86F4E`.
- Success: Verdigris, `#2E7D70`.

### Dark
- Background: Night Mineral, `#111318`.
- Surface: `#191C22`.
- Ink: `#F1F0EB`.
- Secondary ink: `#B2B4BC`.
- Primary: Periwinkle, `#AEB8FF`.
- Primary soft: `#303754`.
- Warm accent: Soft Clay, `#E39A7D`.
- Success: Sea Glass, `#76C3B3`.

## Typography
- Interface: native Android sans serif for clarity and speed.
- Reflective content: Lora, used only for quotes, examples, and journal excerpts.
- No gradient text, all-caps headings, or exaggerated display sizes.
- Body minimum 14sp; primary reading text 17–20sp with generous line height.

## Shape
- Reading surfaces: 28dp, soft but not pill-like.
- Controls: 16–20dp.
- Navigation capsule: full rounded form.
- Avoid using the same radius for every component.

## Glass material
- Translucent tonal fill, never fully transparent.
- One-pixel light/dark edge and a restrained specular highlight.
- Soft ambient shadow only on floating layers.
- Real blur is optional and must degrade gracefully below API 31.
- Never place long text on highly transparent material.

## Layout
- Edge-to-edge background.
- Content measure capped on tablets.
- Four top-level destinations.
- Bottom navigation on compact width; rail on expanded width.
- Today uses vertically snapping editorial panels for word and thought.
- Learn, Reflect, and Library use lists and sections, not repeated generic cards.

## Motion
- 100–150ms press feedback.
- 200–280ms state changes.
- 320ms maximum for navigation or panel transitions.
- Ease-out-quart for entrances and ease-in-quart for exits.
- Reduced-motion mode removes spatial movement while preserving fades and state feedback.

## Accessibility
- 48dp minimum interactive targets.
- Contrast at least WCAG AA.
- Semantics describe state and action, not decoration.
- Large font layouts reflow without clipping.
- Selected navigation state is conveyed by text, icon, and semantics, not color alone.
- No essential information is hidden behind hover, animation, or translucency.
