## 2024-05-24 - Haptic Feedback and Loading Accessibility
**Learning:** In Jetpack Compose, calling `@Composable` functions like `stringResource` inside non-composable contexts like the `semantics` lambda causes compilation errors. Also, tactile feedback via `LocalHapticFeedback` significantly improves the perceived quality of button interactions.
**Action:** Always resolve resources to local variables before using them in semantics or other lambdas. Use `HapticFeedbackType.LongPress` as a reliable way to trigger platform-standard tactile response on buttons.
