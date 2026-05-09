## 2025-05-15 - Harden Network Security and Journal Privacy
**Vulnerability:** Placeholder certificate pins in `network_security_config.xml` and missing `FLAG_SECURE` on sensitive journal screens.
**Learning:** Sequential placeholders like `GThRnpaJ1x8I2c4e8p5h6k7l8m9n0o1p2q3r4s5t6u8=` were used for certificate pinning, which provides no real security against MITM attacks. Additionally, highly private user journal content was exposed to screenshots and screen recordings.
**Prevention:** Always use real SHA-256 pins from production certificates and include intermediates/backup pins for resilience. Use `FLAG_SECURE` on all screens displaying or capturing sensitive user data.

## 2026-01-31 - Credential and Privacy Hardening
**Vulnerability:** Leaking API keys in Logcat via OkHttp interceptors and debug logs, and missing UI protection for therapeutic chats.
**Learning:** Prody's `OpenRouterService` used `HttpLoggingInterceptor.Level.BODY` in debug mode without redacting the `Authorization` header, exposing API keys to anybody with ADB access. Additionally, Haven't therapeutic screens lacked `FLAG_SECURE`, risking user privacy.
**Prevention:** Always use `redactHeader("Authorization")` in network interceptors. Remove logs that print partial secrets. Enforce `FLAG_SECURE` on all therapeutic and reflection screens by default.

## 2026-05-09 - Comprehensive Privacy Hardening with SecureScreen
**Vulnerability:** Fragmented and missing screenshot protection (`FLAG_SECURE`) across sensitive journaling, reflection, and future message screens.
**Learning:** In a single-activity Compose architecture, applying and clearing `FLAG_SECURE` in `DisposableEffect` without coordination leads to "protection leaks" during navigation. If screen A (secure) navigates to screen B (secure), screen B's entry adds the flag, but screen A's disposal (on a transition) might clear it prematurely, or vice versa when going back.
**Prevention:** Use a centralized reference-counted utility like `SecureScreen` to manage activity-wide window flags. This ensures the protection remains active as long as at least one secure component is visible, preventing race conditions during navigation transitions.
