## 2025-05-15 - Harden Network Security and Journal Privacy
**Vulnerability:** Placeholder certificate pins in `network_security_config.xml` and missing `FLAG_SECURE` on sensitive journal screens.
**Learning:** Sequential placeholders like `GThRnpaJ1x8I2c4e8p5h6k7l8m9n0o1p2q3r4s5t6u8=` were used for certificate pinning, which provides no real security against MITM attacks. Additionally, highly private user journal content was exposed to screenshots and screen recordings.
**Prevention:** Always use real SHA-256 pins from production certificates and include intermediates/backup pins for resilience. Use `FLAG_SECURE` on all screens displaying or capturing sensitive user data.

## 2026-01-31 - Credential and Privacy Hardening
**Vulnerability:** Leaking API keys in Logcat via OkHttp interceptors and debug logs, and missing UI protection for therapeutic chats.
**Learning:** Prody's `OpenRouterService` used `HttpLoggingInterceptor.Level.BODY` in debug mode without redacting the `Authorization` header, exposing API keys to anybody with ADB access. Additionally, Haven't therapeutic screens lacked `FLAG_SECURE`, risking user privacy.
**Prevention:** Always use `redactHeader("Authorization")` in network interceptors. Remove logs that print partial secrets. Enforce `FLAG_SECURE` on all therapeutic and reflection screens by default.

## 2026-02-01 - Harden Database Encryption and Enforce UI Privacy
**Vulnerability:** Fail-open database encryption fallback to unencrypted Room, weak deterministic fallback passphrase using ANDROID_ID, and missing screenshot protection on multiple sensitive screens.
**Learning:** Prody's database initialization logic silently reverted to unencrypted storage if SQLCipher failed, and used a predictable passphrase based on device ID if Keystore access failed. This "Fail Open" pattern directly contradicted the app's privacy promise. Additionally, UI privacy was applied inconsistently via manual flag management.
**Prevention:** Always follow the "Fail Secure" principle for encryption—throw exceptions rather than allowing insecure fallbacks. Use centralized UI utilities like `SecureScreen` to consistently enforce `FLAG_SECURE` across all sensitive application layers.
