#!/usr/bin/env bash
set -euo pipefail

echo "Running static secret scan for Gradle/build config files..."

PATTERN='buildConfigField\s*\(|(AI|API|SECRET|TOKEN|KEY)[A-Z0-9_]*\s*=\s*"[^"]{12,}"|"sk-[A-Za-z0-9]{16,}"|"AIza[0-9A-Za-z_-]{20,}"'

MATCHES=$(rg -n --color=never \
  --glob '*.gradle' \
  --glob '*.gradle.kts' \
  --glob '**/BuildConfig.*' \
  "$PATTERN" .github app build.gradle.kts settings.gradle.kts gradle.properties 2>/dev/null || true)

if [[ -n "$MATCHES" ]]; then
  echo "❌ Potential secret-like configuration found in Gradle/build config files:"
  echo "$MATCHES"
  exit 1
fi

echo "✅ Secret scan passed: no secret-like Gradle/build config patterns found."
