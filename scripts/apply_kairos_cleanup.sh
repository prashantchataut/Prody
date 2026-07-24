#!/usr/bin/env bash
set -euo pipefail

# ZIP overlays cannot remove stale package trees. Run this once from the repository root
# after copying Kairos changes into an older Prody checkout.
paths=(
  ".github/workflows/ci.yml"
  "app/src/main/java/com/prody"
  "app/src/debug/java/com/prody"
  "app/src/release/java/com/prody"
  "app/src/test/java/com/prody"
  "app/src/androidTest/java/com/prody"
  "app/src/main/java/com/kairos/app/domain/gamification/NewGameSessionManager.kt"
)

for path in "${paths[@]}"; do
  if [[ -e "$path" ]]; then
    rm -rf -- "$path"
    printf 'Deleted %s\n' "$path"
  fi
done
