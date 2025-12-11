# Prody Keystore

This directory contains documentation for the signing keystore used in release builds.

## Open Source Keystore

For open source contributions and testing, the GitHub Actions workflow automatically generates a keystore with the following credentials when no secrets are configured:

```bash
keytool -genkeypair -v \
  -keystore prody-release.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias prody \
  -storepass prody2024 \
  -keypass prody2024 \
  -dname "CN=Prody App, OU=Prody, O=Prody, L=Unknown, ST=Unknown, C=IN"
```

## Keystore Credentials

For open source builds (auto-generated):
- **Keystore Password**: `prody2024`
- **Key Alias**: `prody`
- **Key Password**: `prody2024`

## Production Builds

For production/Play Store releases, use your own private keystore and set these GitHub Secrets:
- `KEYSTORE_BASE64`: Base64-encoded keystore file
- `KEYSTORE_PASSWORD`: Your keystore password
- `KEY_ALIAS`: Your key alias
- `KEY_PASSWORD`: Your key password

### Encoding Keystore to Base64

```bash
base64 -i your-keystore.jks -o keystore-base64.txt
```

## Security Note

The open source keystore credentials are intentionally public to allow anyone to build and test the app.
For production releases, always use your own private keystore that is not shared publicly.
