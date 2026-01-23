## 2025-05-15 - Improving Accessibility Semantics in Custom Components
**Learning:** Many custom interactive components (Surfaces, Rows) in this app were missing the `role = Role.Button` semantic, making them less discoverable for screen reader users.
**Action:** Always check `clickable` modifiers on non-button components and ensure the `role` parameter is set correctly.
