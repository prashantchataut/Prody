# Kairos stability and rebrand report

## Root causes addressed

1. Hilt `2.59.2` was incompatible with AGP `8.7.2`. The project is pinned to Hilt `2.52` while AGP remains `8.7.2`.
2. Explicit Hilt providers duplicated types that already had `@Inject` constructors. Those duplicate bindings were removed.
3. `AppModule` contained conflicting and stale imports. Imports and bindings were normalized.
4. A `JavaCompile.doFirst` deletion workaround captured Gradle script state and was incompatible with configuration-cache serialization. It was removed, and configuration cache remains disabled for this toolchain.
5. `AuthRepository` used `kotlinx.coroutines.tasks.await` without the Play Services coroutines artifact. The missing dependency was added.
6. The project referenced absent bundled font resources. Theme families now use platform Sans/Serif families so the project builds without distributing font files.
7. A FileProvider authority was hard-coded and failed for `.debug` application IDs. It now derives from `context.packageName`.
8. A notification intent used reflection to load `MainActivity`. It now uses a direct class reference.
9. Source files contained widespread mojibake from an encoding round-trip. User-visible text, content seeds, comments, and diagnostics were repaired.
10. CI depended on executable permissions for a shell script. It now invokes the verifier with `bash` and runs Gradle with configuration cache explicitly disabled.
11. GitHub runner memory settings were excessive. Gradle/Kotlin heaps and worker count were bounded for standard CI runners.

## Identity

- Namespace: `com.kairos.app`
- Application ID: `com.kairos.app`
- Debug application ID: `com.kairos.app.debug`
- Root project: `Kairos`
- Visible app name: `Kairos`

## AI changes

- Legacy Gemini model identifiers were moved from obsolete Gemini 1.x models to stable Gemini 2.5 Flash, Flash-Lite, and Pro identifiers.
- The system prompt now presents an optional Kairos Guide instead of impersonating Buddha.
- The prompt forbids fabricated memory, diagnosis, therapy claims, and dependency-forming language.
- Immediate-danger content is routed toward real-world support rather than normal coaching.
- User-facing labels were changed from “Buddha personality” and “AI generated” toward “guidance tone” and “optional guidance.”

## UI/UX changes in this stability pass

This pass did not attempt another risky screen-by-screen visual rewrite. It hardened the shared system already used by the promoted screens:

- corrected corrupted typography and symbols across the app;
- preserved the four-destination information architecture;
- made skeleton loading honor reduced-motion settings;
- retained glass for navigation and compact controls while keeping reading surfaces matte;
- neutralized misleading AI-centric copy;
- documented which legacy features should remain hidden.

## Remaining production work

- Complete the Android migration from the deprecated Google AI client to Firebase AI Logic with App Check.
- Add screenshot tests and instrumented navigation tests on a real Android toolchain.
- Decide whether Kairos is a new store listing or implement an explicit Prody export/Kairos import migration.
- Remove legacy feature modules only after a separate deletion PR and database-retention decision.
- Run usability tests on the four primary flows before adding more animation or glass.
