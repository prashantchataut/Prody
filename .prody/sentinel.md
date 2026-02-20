## 2025-05-15 - Harden Network Security and Journal Privacy
**Vulnerability:** Placeholder certificate pins in `network_security_config.xml` and missing `FLAG_SECURE` on sensitive journal screens.
**Learning:** Sequential placeholders like `GThRnpaJ1x8I2c4e8p5h6k7l8m9n0o1p2q3r4s5t6u8=` were used for certificate pinning, which provides no real security against MITM attacks. Additionally, highly private user journal content was exposed to screenshots and screen recordings.
**Prevention:** Always use real SHA-256 pins from production certificates and include intermediates/backup pins for resilience. Use `FLAG_SECURE` on all screens displaying or capturing sensitive user data.

## 2026-01-31 - Credential and Privacy Hardening
**Vulnerability:** Leaking API keys in Logcat via OkHttp interceptors and debug logs, and missing UI protection for therapeutic chats.
**Learning:** Prody's `OpenRouterService` used `HttpLoggingInterceptor.Level.BODY` in debug mode without redacting the `Authorization` header, exposing API keys to anybody with ADB access. Additionally, Haven't therapeutic screens lacked `FLAG_SECURE`, risking user privacy.
**Prevention:** Always use `redactHeader("Authorization")` in network interceptors. Remove logs that print partial secrets. Enforce `FLAG_SECURE` on all therapeutic and reflection screens by default.

## 2026-02-20 - Enforce 'Fail Secure' for Database Encryption
**Vulnerability:** The application had a 'Fail Open' policy for database encryption, where failure to initialize SQLCipher would cause a silent fallback to an unencrypted Room database. It also used a deterministic passphrase based on ANDROID_ID as a fallback.
**Learning:** Legacy code often includes 'fallbacks' to ensure app functionality at the cost of security, which is dangerous for apps handling sensitive personal data like journals.
**Prevention:** Always implement 'Fail Secure' policies for encryption. Remove deterministic fallbacks and ensure that any failure to establish a secure channel or storage results in a hard failure (e.g., SecurityException) rather than data exposure.
