# Public test signing identity for Kairos CI releases.

This keystore is intentionally public and must not be used for Play Store
or any production distribution channel.

- File: `Kairos-release.jks`
- Alias: `kairos`
- Store password: `kairos-public-test`
- Key password: `kairos-public-test`

CI sets `KEYSTORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD` from these values.
