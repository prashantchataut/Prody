#!/usr/bin/env bash
set -eu

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

FOCUSED_VIEW_MODELS=(
  app/src/main/java/com/prody/prashant/ui/screens/home/TodayViewModel.kt
  app/src/main/java/com/prody/prashant/ui/screens/vocabulary/VocabularyListViewModel.kt
  app/src/main/java/com/prody/prashant/ui/screens/journal/JournalViewModel.kt
  app/src/main/java/com/prody/prashant/ui/screens/quotes/QuotesViewModel.kt
  app/src/main/java/com/prody/prashant/ui/screens/onboarding/OnboardingViewModel.kt
)

if grep -n "data.local.dao" "${FOCUSED_VIEW_MODELS[@]}"; then
  echo "Focused ViewModels must not import Room DAOs." >&2
  exit 1
fi

echo "Focused ViewModel dependency boundary: OK"

DEFAULT_CONFIG="$(awk '/defaultConfig \{/,/sourceSets \{/' app/build.gradle.kts)"
if printf '%s\n' "$DEFAULT_CONFIG" | grep -q 'localProperties.getProperty'; then
  echo "Release/defaultConfig must not embed local provider keys." >&2
  exit 1
fi

echo "Release provider-key boundary: OK"

if grep -R -n -E \
  'prody2024|keytool -genkeypair' \
  .github/workflows >/tmp/kairos_release_fallback.txt; then
  cat /tmp/kairos_release_fallback.txt >&2
  echo "CI must not generate or publish a fallback release key." >&2
  exit 1
fi

echo "Release signing fallback scan: OK"

if grep -R -n -E \
  'Random\.nextInt\(500|simulated leaderboard|fake leaderboard' \
  app/src/main/java >/tmp/kairos_fake_activity.txt; then
  cat /tmp/kairos_fake_activity.txt >&2
  echo "Fabricated community activity must not ship." >&2
  exit 1
fi

echo "Fabricated community activity scan: OK"

if grep -R -n -E \
  'Nothing is stored on external servers|analyze on-device first|AI providers do not store your personal data' \
  app/src/main/java app/src/main/res/values >/tmp/kairos_unverifiable_copy.txt; then
  cat /tmp/kairos_unverifiable_copy.txt >&2
  echo "Unverifiable privacy copy must not ship." >&2
  exit 1
fi

echo "Trust-copy scan: OK"

python3 - <<'PY'
from pathlib import Path
import xml.etree.ElementTree as ET

for path in Path('app/src/main/res').rglob('*.xml'):
    ET.parse(path)
print('Android XML parse: OK')
PY

if [ "${1:-}" = "--gradle" ]; then
  ./gradlew :app:testDebugUnitTest :app:lintDebug --no-daemon
fi
