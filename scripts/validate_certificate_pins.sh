#!/usr/bin/env bash
set -euo pipefail

CONFIG_PATH="${1:-app/src/main/res/xml/network_security_config.xml}"

if [[ ! -f "$CONFIG_PATH" ]]; then
  echo "Config not found: $CONFIG_PATH" >&2
  exit 1
fi

python - "$CONFIG_PATH" <<'PY'
import base64
import re
import subprocess
import sys
import tempfile
import xml.etree.ElementTree as ET
from pathlib import Path

config_path = Path(sys.argv[1])
root = ET.parse(config_path).getroot()

pin_re = re.compile(r'^[A-Za-z0-9+/]{43}=$')
failures = []

def chain_pins(host: str):
    cmd = f"echo | openssl s_client -servername {host} -connect {host}:443 -showcerts"
    proc = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    if proc.returncode != 0:
        raise RuntimeError(proc.stderr.strip() or proc.stdout.strip())

    blocks = []
    current = []
    for line in proc.stdout.splitlines():
        if '-----BEGIN CERTIFICATE-----' in line:
            current = [line]
        elif current:
            current.append(line)
            if '-----END CERTIFICATE-----' in line:
                blocks.append('\n'.join(current) + '\n')
                current = []

    pins = set()
    with tempfile.TemporaryDirectory() as d:
        for i, pem in enumerate(blocks):
            cert = Path(d) / f"cert-{i}.pem"
            cert.write_text(pem)
            pub = subprocess.run(
                ["openssl", "x509", "-in", str(cert), "-pubkey", "-noout"],
                capture_output=True,
                text=True,
                check=True,
            ).stdout
            p1 = subprocess.run(
                ["openssl", "pkey", "-pubin", "-outform", "DER"],
                input=pub,
                capture_output=True,
                check=True,
            )
            p2 = subprocess.run(
                ["openssl", "dgst", "-sha256", "-binary"],
                input=p1.stdout,
                capture_output=True,
                check=True,
            )
            pins.add(base64.b64encode(p2.stdout).decode())
    return pins

for domain_config in root.findall('domain-config'):
    domain_el = domain_config.find('domain')
    pin_set = domain_config.find('pin-set')

    if domain_el is None or pin_set is None:
        continue

    host = (domain_el.text or '').strip()
    pins = [p.text.strip() for p in pin_set.findall('pin') if p.text]
    expiration = pin_set.attrib.get('expiration')

    if len(pins) < 3:
        failures.append(f"{host}: expected at least 3 pins, found {len(pins)}")

    if not expiration:
        failures.append(f"{host}: pin-set expiration is missing")

    for pin in pins:
        if not pin_re.match(pin):
            failures.append(f"{host}: invalid SHA-256 pin format: {pin}")

    try:
        live_pins = chain_pins(host)
        if not any(pin in live_pins for pin in pins):
            failures.append(f"{host}: no configured pin matched live certificate chain")
    except Exception as exc:
        failures.append(f"{host}: live validation failed ({exc})")

if failures:
    print("Certificate pin validation failed:")
    for item in failures:
        print(f" - {item}")
    sys.exit(1)

print("Certificate pin validation passed.")
PY
