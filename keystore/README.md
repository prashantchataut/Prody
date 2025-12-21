# Prody Keystore Security

This directory contains the release signing key for the Prody Android application. This key is required to create official release builds that can be uploaded to the Google Play Store.

## 🛡️ Security Warning

**DO NOT COMMIT THE ACTUAL `prody-release.jks` KEYSTORE FILE TO THIS REPOSITORY.**

This file should be stored in a secure location, such as a secure vault or your CI/CD environment's secret storage. Exposing this key would allow anyone to sign and distribute malicious versions of the app, appearing as if they came from the official developer.

## ✍️ Signing a Release Build Locally

To sign a release build on your local machine, you need to provide the keystore and its credentials to the Gradle build system. This project is configured to read these values from environment variables to avoid hardcoding them in the source code.

### 1. Obtain the Keystore File

- Get the `prody-release.jks` file from the secure storage location.
- Place it in this `keystore/` directory. The build script is configured to look for it here.

### 2. Set Environment Variables

You need to set the following environment variables. The recommended way to do this for local builds is to add them to your global Gradle properties file.

- **File Location:** `~/.gradle/gradle.properties` (create the file if it doesn't exist)
- **Content to Add:**

```properties
# Prody Release Keystore Credentials
KEYSTORE_PASSWORD=your_keystore_password_here
KEY_ALIAS=your_key_alias_here
KEY_PASSWORD=your_key_password_here
```

- **Replace the placeholder values** with the actual credentials for the keystore.

### 3. Build the Release APK

Once the keystore is in place and the environment variables are set, you can build the release APK using the following Gradle command from the root of the project:

```bash
./gradlew assembleRelease
```

The signed APK will be located at `app/build/outputs/apk/release/app-release-signed.apk`.

## 🤖 For CI/CD Environments (e.g., GitHub Actions)

In your CI/CD pipeline, you should:
1.  **Store the keystore file** as a secure, base64-encoded secret.
2.  **Store the `KEYSTORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD`** as separate secrets.
3.  **During the build job:**
    - Decode the keystore secret back into a file (`keystore/prody-release.jks`).
    - Expose the credential secrets as environment variables for the Gradle build step.

This ensures that your signing keys are never exposed in your build logs or repository history.
