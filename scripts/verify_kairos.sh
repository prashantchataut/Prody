#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

python3 scripts/verify_kairos.py

if [[ "${1:-}" == "--gradle" ]]; then
  ./gradlew clean :app:testDebugUnitTest :app:lintDebug :app:assembleDebug \
    --no-daemon --no-configuration-cache --stacktrace
fi
