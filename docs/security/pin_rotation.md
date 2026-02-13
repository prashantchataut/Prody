# Certificate Pinning Rotation & Expiry Response

## Current Pin Set Review
Pin sets are defined in `app/src/main/res/xml/network_security_config.xml` for:
- `generativelanguage.googleapis.com`
- `openrouter.ai`

Validation status:
- Structural validation (pin format/count/expiration present): **passed** locally.
- Live endpoint chain validation: **performed in CI** via `scripts/validate_certificate_pins.sh` (requires outbound network).

## Rotation Process (Overlapping Pin Windows)
1. **T-30 days (or earlier)**: collect new chain SPKI pins from the provider.
2. Add new pins **without removing old pins**.
3. Keep at least 3 pins per domain:
   - active leaf/intermediate,
   - next chain pin,
   - backup CA pin.
4. Ship at least one release with both old/new pins overlapping.
5. Verify no TLS pin mismatch spike post-release.
6. Remove deprecated pins only after overlap window completes and monitoring remains clean.

## Remote Kill-Switch / Fallback Policy
`PinningPolicyManager` fetches remote policy from `BuildConfig.PINNING_POLICY_URL`.

Supported remote keys:
- `pinning_enabled` (boolean)
- `fallback_transport_mode` (`NONE` or `SYSTEM_CA_ONLY`)
- `allow_automatic_fallback_on_pin_failure` (boolean)
- `reason` (string)

When pin mismatch/expiry events happen and policy allows fallback:
- app switches OpenRouter transport to system-CA HTTPS (pinning disabled temporarily),
- fallback auto-expires after 24h,
- security event is recorded through `PerformanceMonitor`.

## Monitoring Alerting for Pin Mismatch Spikes
`PerformanceMonitor.recordTlsPinMismatch(...)` raises an alert log when pin failures reach threshold:
- threshold: 5 failures
- window: 5 minutes

## CI Pre-release Certificate Validation
The CI workflow runs:

```bash
./scripts/validate_certificate_pins.sh app/src/main/res/xml/network_security_config.xml
```

This checks:
- required pin count,
- pin format,
- live certificate chain SPKI hash overlap with configured pins.
