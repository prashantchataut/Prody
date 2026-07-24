#!/usr/bin/env python3
"""Fast repository checks that run before the Android toolchain.

These checks intentionally cover failures that are easy to miss in review:
package-path drift, stale branding, malformed resources, conflicting imports,
incompatible Hilt/AGP versions, duplicate Hilt bindings, and unsafe release setup.
"""
from __future__ import annotations

import collections
import json
import re
import sys
import tomllib
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
APP = ROOT / "app"
SOURCE_ROOTS = [
    APP / "src/main/java",
    APP / "src/debug/java",
    APP / "src/release/java",
    APP / "src/test/java",
    APP / "src/androidTest/java",
]
EXPECTED_PACKAGE = "com.kairos.app"
EXPECTED_PATH = Path(*EXPECTED_PACKAGE.split("."))
errors: list[str] = []
notes: list[str] = []


def fail(message: str) -> None:
    errors.append(message)


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8-sig")


def version_tuple(value: str) -> tuple[int, ...]:
    nums = re.findall(r"\d+", value)
    return tuple(int(n) for n in nums[:3])


# 1. Build identity and version compatibility.
versions_file = ROOT / "gradle/libs.versions.toml"
with versions_file.open("rb") as handle:
    catalog = tomllib.load(handle)
versions = catalog.get("versions", {})
agp = str(versions.get("agp", "0"))
hilt = str(versions.get("hilt", "0"))
if version_tuple(hilt) >= (2, 59) and version_tuple(agp) < (9, 0):
    fail(f"Hilt {hilt} requires AGP 9+, but the project uses AGP {agp}.")

app_gradle = read(APP / "build.gradle.kts")
for key in ("namespace", "applicationId"):
    if not re.search(rf'\b{key}\s*=\s*"{re.escape(EXPECTED_PACKAGE)}"', app_gradle):
        fail(f"app/build.gradle.kts must set {key} to {EXPECTED_PACKAGE}.")
if "compileDebugJavaWithJavac" in app_gradle and "doFirst" in app_gradle:
    fail("Remove task doFirst workarounds from JavaCompile tasks; they break configuration-cache serialization.")
if not re.search(r'buildConfigField\(\s*"String",\s*"AI_API_KEY",\s*"\\"\\""\s*\)', app_gradle):
    fail("Release AI_API_KEY must default to an empty value.")

gradle_properties = read(ROOT / "gradle.properties")
if "org.gradle.configuration-cache=true" in gradle_properties:
    fail("Configuration cache must remain disabled until all Android/KSP tasks are verified compatible.")

# 2. Package declarations must agree with their paths.
kt_files: list[Path] = []
for source_root in SOURCE_ROOTS:
    if not source_root.exists():
        continue
    for path in source_root.rglob("*.kt"):
        kt_files.append(path)
        text = read(path)
        package_match = re.search(r"^package\s+([\w.]+)", text, re.MULTILINE)
        if package_match is None:
            fail(f"{path.relative_to(ROOT)} has no package declaration.")
            continue
        declared = package_match.group(1)
        try:
            relative_parent = path.parent.relative_to(source_root)
        except ValueError:
            continue
        expected = Path(*declared.split("."))
        if relative_parent != expected:
            fail(
                f"{path.relative_to(ROOT)} declares {declared}, but its path maps to "
                f"{'.'.join(relative_parent.parts)}."
            )
        if not (declared == EXPECTED_PACKAGE or declared.startswith(EXPECTED_PACKAGE + ".")):
            fail(f"{path.relative_to(ROOT)} is outside the {EXPECTED_PACKAGE} namespace: {declared}.")

# 3. Stale brand/package references and text corruption.
text_extensions = {".kt", ".kts", ".xml", ".md", ".properties", ".json", ".toml", ".yml", ".yaml"}
mojibake_fragments = ("â€", "â€”", "â€¢", "ðŸ", "Ëˆ", "ï»¿")
for path in ROOT.rglob("*"):
    if not path.is_file() or path.suffix.lower() not in text_extensions:
        continue
    if any(part in {".git", ".gradle", "build"} for part in path.parts):
        continue
    text = read(path)
    lowered = text.lower()
    migration_docs = {
        Path("README.md"),
        Path("FILES_TO_DELETE.txt"),
        Path("KAIROS_STABILITY_REPORT.md"),
        Path("docs/PACKAGE_MIGRATION.md"),
    }
    relative_path = path.relative_to(ROOT)
    if relative_path not in migration_docs and (
        "com.prody.prashant" in lowered or re.search(r"\bprody\b", lowered)
    ):
        fail(f"{relative_path} still contains the old Prody identity.")
    if any(fragment in text for fragment in mojibake_fragments) or any(
        0x80 <= ord(char) <= 0x9F for char in text
    ):
        fail(f"{path.relative_to(ROOT)} contains likely mojibake/encoding corruption.")

# 4. Duplicate and conflicting Kotlin imports.
for path in kt_files:
    imports: list[tuple[int, str]] = []
    for line_number, line in enumerate(read(path).splitlines(), start=1):
        stripped = line.strip()
        if stripped.startswith("import "):
            imports.append((line_number, stripped.removeprefix("import ").strip()))
    seen: dict[str, int] = {}
    simple_names: dict[str, list[tuple[int, str]]] = collections.defaultdict(list)
    for line_number, import_spec in imports:
        if import_spec in seen:
            fail(f"{path.relative_to(ROOT)}:{line_number} duplicates import {import_spec}.")
        seen[import_spec] = line_number
        if not import_spec.endswith(".*") and " as " not in import_spec:
            simple_names[import_spec.rsplit(".", 1)[-1]].append((line_number, import_spec))
    for simple_name, values in simple_names.items():
        unique = {spec for _, spec in values}
        if len(unique) > 1:
            fail(f"{path.relative_to(ROOT)} has ambiguous imports for {simple_name}: {sorted(unique)}")

# 5. Catch explicit @Provides bindings for types that already use @Inject constructors.
inject_constructible: dict[str, Path] = {}
for path in kt_files:
    text = read(path)
    for match in re.finditer(r"\b(?:class|open\s+class)\s+(\w+)\s+@Inject\s+constructor", text):
        inject_constructible[match.group(1)] = path
for path in kt_files:
    text = read(path)
    if "@Module" not in text or "@Provides" not in text:
        continue
    for match in re.finditer(r"@Provides[\s\S]{0,250}?fun\s+(\w+)[\s\S]{0,500}?\)\s*:\s*([\w.]+)", text):
        function_name, return_type = match.group(1), match.group(2).rsplit(".", 1)[-1]
        constructor_file = inject_constructible.get(return_type)
        if constructor_file is not None:
            fail(
                f"{path.relative_to(ROOT)}::{function_name} explicitly provides {return_type}, "
                f"which already has an @Inject constructor in {constructor_file.relative_to(ROOT)}."
            )

# 6. Android XML and resources.
res = APP / "src/main/res"
resources: dict[str, set[str]] = collections.defaultdict(set)
file_resource_types = {"drawable", "mipmap", "layout", "xml", "anim", "animator", "font", "raw", "menu", "navigation", "color"}
if res.exists():
    for directory in res.iterdir():
        if not directory.is_dir():
            continue
        resource_type = directory.name.split("-", 1)[0]
        if resource_type in file_resource_types:
            for file in directory.iterdir():
                if file.is_file():
                    resources[resource_type].add(file.stem)
    for xml_path in res.rglob("*.xml"):
        try:
            tree = ET.parse(xml_path)
        except ET.ParseError as exc:
            fail(f"{xml_path.relative_to(ROOT)} is malformed XML: {exc}")
            continue
        if xml_path.parent.name.startswith("values"):
            for node in tree.getroot():
                name = node.attrib.get("name")
                if not name:
                    continue
                resource_type = node.tag
                if resource_type == "item":
                    resource_type = node.attrib.get("type", "item")
                resources[resource_type].add(name)

resource_ref = re.compile(r"(?<!android\.)\bR\.(\w+)\.(\w+)")
for path in kt_files:
    text = read(path)
    for match in resource_ref.finditer(text):
        resource_type, name = match.groups()
        if resource_type in {"id", "styleable"}:
            continue
        if name not in resources.get(resource_type, set()):
            line = text.count("\n", 0, match.start()) + 1
            fail(f"{path.relative_to(ROOT)}:{line} references missing R.{resource_type}.{name}.")

xml_ref = re.compile(r"@(?!(?:android|tools):)(\+?)([\w]+)/([\w.]+)")
for path in res.rglob("*.xml") if res.exists() else []:
    text = read(path)
    for match in xml_ref.finditer(text):
        plus, resource_type, name = match.groups()
        if plus or resource_type == "id":
            continue
        if name not in resources.get(resource_type, set()):
            line = text.count("\n", 0, match.start()) + 1
            fail(f"{path.relative_to(ROOT)}:{line} references missing @{resource_type}/{name}.")

# 7. Manifest and Firebase registration.
manifest_path = APP / "src/main/AndroidManifest.xml"
try:
    manifest = ET.parse(manifest_path).getroot()
    android_ns = "{http://schemas.android.com/apk/res/android}"
    for provider in manifest.iter("provider"):
        authority = provider.attrib.get(android_ns + "authorities", "")
        if authority.startswith("com."):
            fail(
                f"{manifest_path.relative_to(ROOT)} hard-codes provider authority {authority}; "
                "use ${applicationId} so debug builds work."
            )
except ET.ParseError as exc:
    fail(f"{manifest_path.relative_to(ROOT)} is malformed XML: {exc}")

google_services = APP / "google-services.json"
if google_services.exists():
    try:
        payload = json.loads(read(google_services))
        packages = {
            client.get("client_info", {}).get("android_client_info", {}).get("package_name")
            for client in payload.get("client", [])
        }
        if EXPECTED_PACKAGE not in packages:
            fail(f"google-services.json has no client for {EXPECTED_PACKAGE}.")
        if EXPECTED_PACKAGE + ".debug" not in packages:
            notes.append("google-services.json has no explicit debug client; Google sign-in may fail for debug signing.")
    except json.JSONDecodeError as exc:
        fail(f"google-services.json is invalid JSON: {exc}")
else:
    notes.append("google-services.json is absent; Firebase-backed sign-in will be unavailable.")

print(f"Kairos verification: {len(kt_files)} Kotlin files checked")
print(f"Versions: AGP {agp}, Hilt {hilt}")
for note in notes:
    print(f"NOTE: {note}")
if errors:
    print(f"FAILED: {len(errors)} issue(s)")
    for issue in errors:
        print(f" - {issue}")
    sys.exit(1)
print("PASSED: package, branding, DI, resources, XML, encoding, and build-policy checks")
