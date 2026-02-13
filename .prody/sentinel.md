## 2025-05-15 - Harden Network Security and Journal Privacy
**Vulnerability:** Placeholder certificate pins in `network_security_config.xml` and missing `FLAG_SECURE` on sensitive journal screens.
**Learning:** Sequential placeholders like `GThRnpaJ1x8I2c4e8p5h6k7l8m9n0o1p2q3r4s5t6u8=` were used for certificate pinning, which provides no real security against MITM attacks. Additionally, highly private user journal content was exposed to screenshots and screen recordings.
**Prevention:** Always use real SHA-256 pins from production certificates and include intermediates/backup pins for resilience. Use `FLAG_SECURE` on all screens displaying or capturing sensitive user data.

## 2026-01-31 - Credential and Privacy Hardening
**Vulnerability:** Leaking API keys in Logcat via OkHttp interceptors and debug logs, and missing UI protection for therapeutic chats.
**Learning:** Prody's `OpenRouterService` used `HttpLoggingInterceptor.Level.BODY` in debug mode without redacting the `Authorization` header, exposing API keys to anybody with ADB access. Additionally, Haven't therapeutic screens lacked `FLAG_SECURE`, risking user privacy.
**Prevention:** Always use `redactHeader("Authorization")` in network interceptors. Remove logs that print partial secrets. Enforce `FLAG_SECURE` on all therapeutic and reflection screens by default.

## 2025-05-16 - Centralized UI Privacy and Crash Report Hardening
**Vulnerability:** Information leakage via technical crash details in production and inconsistent screenshot protection across sensitive screens.
**Learning:** `CrashActivity` was exposing full stack traces and exception messages to users in production builds, which could leak internal logic or data. Additionally, while some screens manually applied `FLAG_SECURE`, others like weekly summaries and monthly letters were unprotected.
**Prevention:** Implement a centralized `SecureScreen` utility to ensure consistent application of `FLAG_SECURE`. Always wrap technical debug info in `if (BuildConfig.DEBUG)` checks on public-facing error screens.
