# ADR-001: Focused product shell and strangler migration

- Status: Accepted
- Date: 2026-07-23

## Context

The application had accumulated many top-level experiences: vocabulary, quotes, journals, AI guidance, challenges, social circles, achievements, learning paths, analytics, and future messages. Promoting all of them at once made the main journey difficult to understand and forced the home screen to initialize unrelated subsystems.

A complete rewrite would put Room migrations, widgets, notifications, and existing user data at unnecessary risk.

## Decision

Kairos promotes four durable destinations:

1. **Today** — one daily learning and reflection moment.
2. **Learn** — vocabulary discovery and review.
3. **Reflect** — journal creation and history.
4. **Library** — quotes, proverbs, idioms, and phrases.

The new navigation shell routes to focused screens while legacy routes remain available for compatibility. The entry flow offers a persistent local profile so vocabulary, journaling, and the library are not blocked by Google authentication; account sign-in remains optional. This is a strangler migration: each promoted feature is rebuilt behind a smaller state and repository boundary, then legacy implementations can be removed after behavior and migration coverage are proven.

Newly migrated ViewModels must depend on repositories or use cases, not Room DAOs. UI filters remain pure functions and receive unit tests. Persistent identifiers such as the Android application ID, database name, preference names, widget actions, and Room table names remain unchanged until an explicit migration is approved.

## Consequences

- The primary experience becomes learnable without deleting valuable legacy code immediately.
- Existing installations keep their local data and update path.
- Secondary features no longer compete for top-level navigation.
- Some old screens temporarily retain the older visual language.
- Removal of legacy code becomes a measured follow-up rather than a risky prerequisite.
