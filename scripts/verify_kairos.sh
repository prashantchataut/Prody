#!/usr/bin/env bash
set -euo pipefail

root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
issues=0
fail() { printf 'FAILED: %s\n' "$*" >&2; issues=$((issues + 1)); }

[[ -f gradlew ]] || fail "gradlew is missing"
[[ -f gradle/wrapper/gradle-wrapper.jar ]] || fail "Gradle wrapper JAR is missing"
[[ -f gradle/wrapper/gradle-wrapper.properties ]] || fail "Gradle wrapper properties are missing"

for stale in \
  .github/workflows/ci.yml \
  app/src/main/java/com/prody \
  app/src/debug/java/com/prody \
  app/src/release/java/com/prody \
  app/src/test/java/com/prody \
  app/src/androidTest/java/com/prody \
  app/src/main/java/com/kairos/app/domain/gamification/NewGameSessionManager.kt; do
  [[ ! -e "$stale" ]] || fail "stale path exists: $stale (run bash scripts/apply_kairos_cleanup.sh)"
done

python3 - <<'PY'
from pathlib import Path
import json, re, sys, xml.etree.ElementTree as ET
issues=[]
root=Path('.')

build=(root/'app/build.gradle.kts').read_text(encoding='utf-8-sig')
for expected in ('namespace = "com.kairos.app"','applicationId = "com.kairos.app"'):
    if expected not in build: issues.append(f'missing build identity: {expected}')

kotlin_files = list((root/'app/src').rglob('*.kt'))
package_sources = {}
declared_fqns = set()

for p in kotlin_files:
    text=p.read_text(encoding='utf-8-sig', errors='replace')
    package_match=re.search(r'^package\s+([\w.]+)', text, re.M)
    if not package_match: continue
    package=package_match.group(1)
    package_sources.setdefault(package, []).append(text)
    declaration_pattern = re.compile(
        r'\b(?:class|interface|object|typealias)\s+([A-Za-z_]\w*)'
        r'|\bfun\s+(?:<[^>{}]+>\s*)?(?:[A-Za-z_][\w<>,?. ]*\.)?([A-Za-z_]\w*)\s*\('
        r'|\b(?:val|var)\s+(?:[A-Za-z_][\w<>,?. ]*\.)?([A-Za-z_]\w*)\b'
    )
    for match in declaration_pattern.finditer(text):
        name=next((group for group in match.groups() if group), None)
        if name: declared_fqns.add(f'{package}.{name}')

for p in kotlin_files:
    text=p.read_text(encoding='utf-8-sig', errors='replace')
    m=re.search(r'^package\s+([\w.]+)', text, re.M)
    if not m: continue
    package=m.group(1)
    if not package.startswith('com.kairos.app'):
        issues.append(f'{p} has package {package}')
    if 'com.prody.prashant' in text:
        issues.append(f'{p} imports or references com.prody.prashant')

    parts=p.parts
    if 'java' in parts:
        java_index=parts.index('java')
        expected=Path(*package.split('.'))
        actual=Path(*parts[java_index + 1:-1])
        if actual != expected:
            issues.append(f'{p} package/path mismatch: {package}')

    imports=[]
    for line in text.splitlines():
        match=re.match(r'\s*import\s+([^\s]+)(?:\s+as\s+([^\s]+))?', line)
        if match: imports.append((match.group(1), match.group(2)))
    simple_names={}
    for imported, alias in imports:
        simple=alias or imported.rsplit('.', 1)[-1]
        if simple == '*': continue
        simple_names.setdefault(simple, set()).add(imported)
    for simple, imported_names in simple_names.items():
        if len(imported_names) > 1:
            issues.append(f'{p} has ambiguous import {simple}: {sorted(imported_names)}')

    for imported, _alias in imports:
        if not imported.startswith('com.kairos.app') or imported.endswith('.*'):
            continue
        if imported in ('com.kairos.app.R', 'com.kairos.app.BuildConfig'):
            continue
        parts=imported.split('.')
        if not any('.'.join(parts[:end]) in declared_fqns for end in range(len(parts), 3, -1)):
            issues.append(f'{p} imports missing internal symbol {imported}')

resource_names = {}
res_root=root/'app/src/main/res'
for p in res_root.rglob('*'):
    if not p.is_file(): continue
    folder=p.parent.name.split('-', 1)[0]
    if folder == 'values' and p.suffix == '.xml':
        try:
            values_root=ET.parse(p).getroot()
            for element in values_root:
                name=element.attrib.get('name')
                if name:
                    resource_type = element.tag
                    if resource_type in ('string-array', 'integer-array'):
                        resource_type='array'
                    resource_names.setdefault(resource_type, set()).add(name)
        except Exception as error:
            issues.append(f'invalid XML {p}: {error}')
    else:
        resource_names.setdefault(folder, set()).add(p.stem)

for p in res_root.rglob('*.xml'):
    try: ET.parse(p)
    except Exception as e: issues.append(f'invalid XML {p}: {e}')

for p in kotlin_files:
    text=p.read_text(encoding='utf-8-sig', errors='replace')
    for resource_type, resource_name in re.findall(r'(?<!android\.)\bR\.([A-Za-z_]+)\.([A-Za-z0-9_]+)', text):
        if resource_name not in resource_names.get(resource_type, set()):
            issues.append(f'{p} references missing R.{resource_type}.{resource_name}')

manifest_path=root/'app/src/main/AndroidManifest.xml'
manifest=manifest_path.read_text(encoding='utf-8-sig')
if 'com.prody' in manifest: issues.append('AndroidManifest.xml contains com.prody')
try:
    manifest_root=ET.parse(manifest_path).getroot()
    android_name='{http://schemas.android.com/apk/res/android}name'
    for tag in ('application', 'activity', 'activity-alias', 'service', 'receiver', 'provider'):
        for element in manifest_root.iter(tag):
            component=element.attrib.get(android_name)
            if not component or component.startswith(('android.', 'com.google.', 'androidx.')):
                continue
            resolved = f'com.kairos.app{component}' if component.startswith('.') else component
            if resolved.startswith('com.kairos.app') and resolved not in declared_fqns:
                issues.append(f'AndroidManifest.xml references missing component {resolved}')
except Exception as error:
    issues.append(f'could not validate AndroidManifest components: {error}')

services=root/'app/google-services.json'
if services.exists():
    data=json.loads(services.read_text(encoding='utf-8-sig'))
    packages={c.get('client_info',{}).get('android_client_info',{}).get('package_name') for c in data.get('client',[])}
    for package in ('com.kairos.app','com.kairos.app.debug'):
        if package not in packages: issues.append(f'google-services.json has no client for {package}')

if 'org.gradle.configuration-cache=true' in (root/'gradle.properties').read_text(encoding='utf-8-sig'):
    issues.append('configuration cache is enabled before Android/KSP cache safety is established')


# Core user-facing ViewModels use repositories rather than coordinating Room.
for feature in ('profile', 'journal', 'futuremessage'):
    for p in (root/f'app/src/main/java/com/kairos/app/ui/screens/{feature}').glob('*ViewModel.kt'):
        text=p.read_text(encoding='utf-8-sig', errors='replace')
        if 'import com.kairos.app.data.local.dao.' in text:
            issues.append(f'{p} imports a Room DAO directly')

# Milestone IDs must resolve to the canonical achievement catalogue.
policy=(root/'app/src/main/java/com/kairos/app/domain/gamification/AchievementMilestonePolicy.kt').read_text(encoding='utf-8-sig')
catalog=(root/'app/src/main/java/com/kairos/app/domain/identity/KairosAchievements.kt').read_text(encoding='utf-8-sig')
policy_ids=set(re.findall(r'"([a-z0-9_]+)"\s+to\s+\d+', policy))
catalog_ids=set(re.findall(r'id\s*=\s*"([a-z0-9_]+)"', catalog))
for missing in sorted(policy_ids - catalog_ids):
    issues.append(f'achievement milestone has no catalogue entry: {missing}')

# Provider + @Inject constructor duplicate bindings in app modules.
inject_types=set()
for p in (root/'app/src/main/java').rglob('*.kt'):
    text=p.read_text(encoding='utf-8-sig', errors='replace')
    inject_types.update(re.findall(r'class\s+(\w+)[\s\S]{0,180}?@Inject\s+constructor\s*\(', text))
for p in (root/'app/src/main/java/com/kairos/app/di').glob('*.kt'):
    text=p.read_text(encoding='utf-8-sig', errors='replace')
    for fn, typ in re.findall(r'fun\s+(provide\w+)\s*\([\s\S]*?\)\s*:\s*([\w.]+)', text):
        simple=typ.rsplit('.',1)[-1]
        if simple in inject_types:
            issues.append(f'{p}::{fn} duplicates @Inject binding for {simple}')

for issue in issues: print(f' - {issue}')
if issues: sys.exit(1)
print(f'Kairos verification passed: {sum(1 for _ in (root/"app/src").rglob("*.kt"))} Kotlin files checked')
PY

if (( issues > 0 )); then
  exit 1
fi
