# Applying this Kairos update

## Full-project ZIP

Replace your working copy with the contents of the full-project ZIP, while keeping private files such as `local.properties` and your production keystore outside source control.

## Changed-files ZIP

1. Extract the archive into the repository root and allow matching files to be overwritten.
2. Open `FILES_TO_DELETE.txt`.
3. Delete every repository-relative path listed there from both your local checkout and GitHub.
4. Commit the changes and let the Android workflow run.

This delivery requires deleting `.github/workflows/ci.yml`. It is an older overlapping Android workflow and is intentionally absent from the full-project ZIP.

## CI permission fix

The Android workflow now runs:

```bash
bash ./scripts/verify_kairos.sh
```

It also grants executable permission before Gradle starts. This makes verification work even when an archive, Windows checkout, or Git file mode leaves the script non-executable.

Optionally preserve executable mode in Git:

```bash
git update-index --chmod=+x scripts/verify_kairos.sh
git commit -m "Mark Kairos verifier executable"
```

The workflow does not depend on that optional step.
