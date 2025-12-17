# Prody UI/UX Design Decisions

> **Date:** December 2024
> **Decision Owner:** UI/UX Upgrade Sprint
> **Status:** Active

---

## Signature Element Choice: Quiet Gold

### Decision

We choose **Quiet Gold** as the single signature element that makes Prody feel premium.

### Rationale

1. **Already in the codebase:** `GoldTier = Color(0xFFE6B422)` exists and is well-integrated
2. **Subtlety wins:** By removing visual noise, the remaining gold accents become more impactful
3. **Brand alignment:** "Calm growth" and "wisdom" are better conveyed by understated elegance than flashy effects
4. **Performance benefit:** Removing blur/glow effects improves rendering performance
5. **Easier to implement:** No new pattern system needed, just refinement

### Implementation Guidelines

| Element | Before | After |
|---------|--------|-------|
| Achievement badges | Glow + scale animation | Static gold border, subtle shadow |
| Streak display | Fire glow + pulse | Clean gold accent bar |
| Rank badges | Orbiting particles | Solid gold circle with subtle elevation |
| Level progress | Gradient fill | Gold progress indicator |
| Podium | Shine sweep animation | Gold tier colors with clean edges |

---

## Visual Noise Removals

### Confirmed Removals

| Element | File | Line(s) | Status |
|---------|------|---------|--------|
| Orbiting particles in Stats header | `StatsScreen.kt` | 393-421 | TO REMOVE |
| Orbiting particles in Profile header | `ProfileScreen.kt` | 668-710 | TO REMOVE |
| Shine sweep on podium | `StatsScreen.kt` | 1667-1684 | TO REMOVE |
| Rotating icon in empty state | `StatsScreen.kt` | 1772-1780 | TO REMOVE |
| Heavy blur (>8dp) effects | Multiple | - | REDUCE |

### Animation Changes

| Animation Type | Before | After |
|----------------|--------|-------|
| Spring damping | `DampingRatioMediumBouncy` | `DampingRatioLowBouncy` or `DampingRatioNoBouncy` |
| Entry animations | 50-100ms staggered delays | 30-50ms staggered delays |
| Glow pulses | 1000-2000ms infinite | Remove or make 4000ms subtle |
| Rotation animations | 8000-40000ms continuous | Remove from production UI |

---

## Color Hierarchy

### Primary Actions
- Use `GoldTier` for primary CTAs and achievements
- Use `ProdyPrimary` for navigation and secondary emphasis

### Accents
- `GoldTier` sparingly for celebrations, milestones, and unlocks
- Avoid using gold for backgrounds (too loud)

### Surfaces
- Keep surfaces clean (`ProdySurface`, `ProdySurfaceVariant`)
- Minimal gradients - prefer solid colors with subtle elevation

---

## Typography Hierarchy

No changes to typography - current system is adequate.

---

## Motion Principles

### Entrance
- Fast fade-in (200-300ms)
- Subtle vertical slide (8-16dp)
- No bounce

### Feedback
- Scale press: 0.98f (subtle)
- Duration: 100-150ms

### Transitions
- Page transitions: 300ms
- Tab changes: 200ms
- No spring bounce on navigation

---

## Rejected Alternatives

### Ambient Edge Glow
- **Reason for rejection:** Adds more visual noise, not less
- **Risk:** GPU-intensive on low-end devices

### Pattern Banners
- **Reason for rejection:** Already has banner system; adding patterns would compete
- **Risk:** Feature creep, inconsistent look

---

## Implementation Order

1. Remove orbiting particles from Stats and Profile headers
2. Remove shine sweep from podium
3. Reduce blur effects from 12-20dp to 4-8dp
4. Change spring animations from MediumBouncy to NoBouncy
5. Add quiet gold accents where glow was removed
6. Update empty state to static design

---

*Document Version: 1.0*
