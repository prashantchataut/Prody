# ADR-003: Production AI boundary

- Status: Accepted
- Date: 2026-07-23

## Context

Provider API keys compiled into an Android APK can be extracted. Encrypting a key on-device does not solve this when the application must decrypt it to call a third-party provider. Direct mobile integrations also make quotas, abuse controls, provider failover, and structured validation difficult to enforce.

## Decision

Release builds contain empty provider-key BuildConfig values and fail closed for legacy AI features. Local debug builds may read provider keys from `local.properties` for development only.

The target production architecture is a server-side Kairos AI gateway with authenticated requests, per-user quotas, abuse protection, schema validation, observability, and provider abstraction. The focused Today, Learn, Reflect, and Library journeys must remain useful when AI is unavailable.

Static certificate pins for third-party AI infrastructure are removed because Kairos does not control those providers' certificate rotation. Platform TLS remains in force.

## Consequences

- Release APKs no longer embed shared provider secrets.
- Legacy AI features are unavailable in production until the gateway exists.
- Core learning and reflection behavior remains fully functional without AI.
- Backend gateway implementation becomes an explicit release prerequisite for re-enabling AI-assisted features.
