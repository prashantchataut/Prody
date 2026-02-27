## 2025-05-15 - Harden Network Security and Journal Privacy
**Vulnerability:** Placeholder certificate pins in `network_security_config.xml` and missing `FLAG_SECURE` on sensitive journal screens.
**Learning:** Sequential placeholders like `GThRnpaJ1x8I2c4e8p5h6k7l8m9n0o1p2q3r4s5t6u8=` were used for certificate pinning, which provides no real security against MITM attacks. Additionally, highly private user journal content was exposed to screenshots and screen recordings.
**Prevention:** Always use real SHA-256 pins from production certificates and include intermediates/backup pins for resilience. Use `FLAG_SECURE` on all screens displaying or capturing sensitive user data.

## 2026-01-31 - Credential and Privacy Hardening
**Vulnerability:** Leaking API keys in Logcat via OkHttp interceptors and debug logs, and missing UI protection for therapeutic chats.
**Learning:** Prody's `OpenRouterService` used `HttpLoggingInterceptor.Level.BODY` in debug mode without redacting the `Authorization` header, exposing API keys to anybody with ADB access. Additionally, Haven't therapeutic screens lacked `FLAG_SECURE`, risking user privacy.
**Prevention:** Always use `redactHeader("Authorization")` in network interceptors. Remove logs that print partial secrets. Enforce `FLAG_SECURE` on all therapeutic and reflection screens by default.

## 2026-02-15 - Fail-Secure Database and Expanded Privacy Protection
**Vulnerability:** Deterministic fallback passphrases and unencrypted Room database fallback in `DatabaseFactory`, plus missing `FLAG_SECURE` on several sensitive screens (Monthly Letters, Time Capsules, Weekly Digests, Locker, Stats, Profile).
**Learning:** Initializing an encrypted database had an unencrypted fallback, violating the "Fail Secure" principle. Deterministic keys based on `ANDROID_ID` were also used as a fallback, which is insecure. Furthermore, private user insights and reflections were captureable on several key screens.
**Prevention:** Remove all unencrypted database fallbacks and deterministic key generation. Ensure encryption failures throw a `SecurityException`. Apply `FLAG_SECURE` consistently to all screens displaying sensitive user data or AI-generated reflections.
