## 2025-05-15 - Harden Network Security and Journal Privacy
**Vulnerability:** Placeholder certificate pins in `network_security_config.xml` and missing `FLAG_SECURE` on sensitive journal screens.
**Learning:** Sequential placeholders like `GThRnpaJ1x8I2c4e8p5h6k7l8m9n0o1p2q3r4s5t6u8=` were used for certificate pinning, which provides no real security against MITM attacks. Additionally, highly private user journal content was exposed to screenshots and screen recordings.
**Prevention:** Always use real SHA-256 pins from production certificates and include intermediates/backup pins for resilience. Use `FLAG_SECURE` on all screens displaying or capturing sensitive user data.

## 2026-01-31 - Credential and Privacy Hardening
**Vulnerability:** Leaking API keys in Logcat via OkHttp interceptors and debug logs, and missing UI protection for therapeutic chats.
**Learning:** Prody's `OpenRouterService` used `HttpLoggingInterceptor.Level.BODY` in debug mode without redacting the `Authorization` header, exposing API keys to anybody with ADB access. Additionally, Haven't therapeutic screens lacked `FLAG_SECURE`, risking user privacy.
**Prevention:** Always use `redactHeader("Authorization")` in network interceptors. Remove logs that print partial secrets. Enforce `FLAG_SECURE` on all therapeutic and reflection screens by default.

## 2026-04-17 - Secure Communication and Screen Protection
**Vulnerability:** Placeholder certificate pins in `SecureHttpClient.kt` and missing `FLAG_SECURE` on sensitive screens (Future Messages, Micro-Journal, Monthly Letters, Locker).
**Learning:** Hardcoded placeholders like `sha256/GThRnpaJ1x8I2c4e8p5h6k7l8m9n0o1p2q3r4s5t6u7v8w9x0y1z2a3b4c5d6e7f8` were used for certificate pinning, which provides no real security. Additionally, sensitive user content was exposed to screenshots because `FLAG_SECURE` was only applied to a subset of reflection screens.
**Prevention:** Always synchronize certificate pins between `network_security_config.xml` and `SecureHttpClient.kt`. Use a centralized `SecureScreen` utility to ensure consistent application of `FLAG_SECURE` across all private data screens.
