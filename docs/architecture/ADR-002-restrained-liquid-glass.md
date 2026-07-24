# ADR-002: Restrained Liquid Glass visual system

- Status: Accepted
- Date: 2026-07-23

## Context

A translucent visual language can provide depth and hierarchy, but applying blur, gradients, and floating cards to every surface reduces legibility, increases rendering cost, and quickly becomes decorative noise. Kairos is primarily a reading and recall product, so vocabulary definitions, quotes, and journal text must remain calm and highly legible.

## Decision

Glass is a **hierarchy material**, not the default container.

Use translucent glass surfaces only for:

- top-level navigation;
- compact headers and icon controls;
- segmented controls;
- small action clusters.

Use opaque or near-opaque editorial reading surfaces for:

- vocabulary definitions and examples;
- quotes and long-form wisdom;
- journal entries;
- empty, loading, and error content.

The system uses mineral indigo for recognition, clay for warmth, verdigris for completion, warm neutral backgrounds, native sans typography for interface text, and a serif face for reflective reading moments. Motion is limited to state change, pressed feedback, navigation selection, and bounded loading states. Every primary target is at least 48dp, text remains selectable where useful, and content is laid out edge-to-edge with explicit system-inset handling.

## Consequences

- The app feels dimensional without making every element compete for attention.
- Reading surfaces preserve contrast in light and dark modes.
- Rendering behavior is more predictable on lower-end devices.
- Future components have clear criteria for whether glass is appropriate.
