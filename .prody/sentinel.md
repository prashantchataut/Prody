## 2025-05-15 - Harden Network Security and Journal Privacy
**Vulnerability:** Placeholder certificate pins in `network_security_config.xml` and missing `FLAG_SECURE` on sensitive journal screens.
**Learning:** Sequential placeholders like `GThRnpaJ1x8I2c4e8p5h6k7l8m9n0o1p2q3r4s5t6u8=` were used for certificate pinning, which provides no real security against MITM attacks. Additionally, highly private user journal content was exposed to screenshots and screen recordings.
**Prevention:** Always use real SHA-256 pins from production certificates and include intermediates/backup pins for resilience. Use `FLAG_SECURE` on all screens displaying or capturing sensitive user data.

## 2026-01-31 - Credential and Privacy Hardening
**Vulnerability:** Leaking API keys in Logcat via OkHttp interceptors and debug logs, and missing UI protection for therapeutic chats.
**Learning:** Prody's `OpenRouterService` used `HttpLoggingInterceptor.Level.BODY` in debug mode without redacting the `Authorization` header, exposing API keys to anybody with ADB access. Additionally, Haven't therapeutic screens lacked `FLAG_SECURE`, risking user privacy.
**Prevention:** Always use `redactHeader("Authorization")` in network interceptors. Remove logs that print partial secrets. Enforce `FLAG_SECURE` on all therapeutic and reflection screens by default.

## 2026-05-20 - Fail-Secure Database Policy and Information Leakage Prevention
**Vulnerability:** Insecure fallback to unencrypted database and weak passphrases in DatabaseFactory and SecureDatabaseManager, and technical crash details exposed in production.
**Learning:** The database initialization had a 'Fail Open' design where any encryption failure resulted in falling back to plain-text storage or weak ANDROID_ID derived keys. This completely bypassed the intended security for sensitive journal data. Additionally, CrashActivity leaked stack traces and internal messages in production builds.
**Prevention:** Enforce a 'Fail Secure' policy by throwing SecurityException if encryption initialization fails. Always wrap technical debug info and 'Copy Report' actions in BuildConfig.DEBUG checks. Sanitize logs to avoid printing metadata like key lengths.
