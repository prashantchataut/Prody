# Release Checklist

## Database schema and migration safety (required)

- [ ] Every Room schema version bump includes exported schema JSON under `app/schemas/com.prody.prashant.data.local.database.ProdyDatabase/`.
- [ ] Migration tests pass for all supported upgrade paths (at minimum `4 -> latest` and the previous major schema -> latest).
- [ ] Unsupported legacy versions are explicitly gated (no destructive fallback behavior).
- [ ] Recovery UX + backup restore path is verified manually on a device/emulator before release.
- [ ] CI/build gate blocks release unless migration verification tests pass.
