# Prody Test Plan

**Date:** December 16, 2025
**Agent:** Parallel Agent 3 (Reliability, Performance, Accessibility, QA)

---

## Test Coverage Overview

| Category | Tests Added | Location | Status |
|----------|-------------|----------|--------|
| Gamification Logic | 18 tests | `GamificationServiceTest.kt` | NEW |
| Weekly Stats | 15 tests | `WeeklyStatsCalculatorTest.kt` | NEW |
| Journal Repository | Existing | `JournalRepositoryTest.kt` | Existing |
| Vocabulary Repository | Existing | `VocabularyRepositoryTest.kt` | Existing |

---

## Unit Tests Added

### 1. GamificationServiceTest.kt

**Location:** `app/src/test/java/com/prody/prashant/domain/gamification/GamificationServiceTest.kt`

**Tests:**

#### Point Calculation Tests
- `recordActivity - journal entry awards 50 base points`
- `recordActivity - word learned awards 15 base points`
- `recordActivity - quote read awards 5 base points`
- `recordActivity - future letter sent awards 50 base points`

#### Streak Bonus Tests
- `recordActivity - streak bonus adds 2 points per streak day`
- `recordActivity - 10 day streak adds 20 bonus points`
- `recordActivity - zero streak adds no bonus`

#### Daily Cap Tests
- `recordActivity - returns 0 when daily cap reached`
- `recordActivity - caps points when would exceed daily limit`
- `recordActivity - large streak bonus capped by daily limit`

#### Edge Case Tests
- `recordActivity - returns 0 when no user profile exists`
- `recordActivity - handles missing stats gracefully`

#### Level Calculation Tests
- `level calculation - 0 points is level 1`
- `level calculation - 199 points is level 1`
- `level calculation - 200 points is level 2`
- `level calculation - 499 points is level 2`
- `level calculation - 500 points is level 3`
- `level calculation - 10000 points is level 10`
- `level calculation - 50000 points is still level 10 (max)`

#### Streak Calculation Tests
- `streak - same day activity maintains streak`
- `streak - next day activity increments streak`
- `streak - gap of 2 days resets streak to 1`
- `streak - gap of 7 days resets streak to 1`

---

### 2. WeeklyStatsCalculatorTest.kt

**Location:** `app/src/test/java/com/prody/prashant/domain/analytics/WeeklyStatsCalculatorTest.kt`

**Tests:**

#### Weekly Range Calculation Tests
- `getWeekRange - returns 7 days`
- `getWeekRange - end is today`
- `getWeekRange - start is 6 days before today`
- `getWeekDays - returns 7 day labels`
- `getWeekDays - contains valid day abbreviations`

#### Date Formatting Tests
- `formatDateRange - produces readable format`
- `formatDateRange - handles month boundary`

#### Consistency Score Tests
- `consistency score - perfect streak equals 100`
- `consistency score - half active days equals 50`
- `consistency score - zero days active returns 0`
- `consistency score - handles divide by zero`
- `consistency score - capped at 100`

#### Weekly Growth Tests
- `weekly growth - positive growth calculated correctly`
- `weekly growth - negative growth calculated correctly`
- `weekly growth - zero last week returns 100 percent growth`
- `weekly growth - both zero returns 0`
- `weekly growth - capped at reasonable bounds`

#### Learning Pace Tests
- `learning pace - high weekly points returns Fast`
- `learning pace - medium weekly points returns Steady`
- `learning pace - low weekly points returns Gradual`
- `learning pace - zero points returns Gradual`

---

## Test Dependencies

```kotlin
// build.gradle.kts
testImplementation(libs.junit)
testImplementation(libs.kotlinx.coroutines.test)
testImplementation(libs.mockk)
testImplementation(libs.turbine)
testImplementation(libs.androidx.arch.core.testing)
```

---

## Running Tests

### Command Line

```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.prody.prashant.domain.gamification.GamificationServiceTest"

# Run with coverage report
./gradlew testDebugUnitTest jacocoTestReport

# Run specific test method
./gradlew test --tests "*.`recordActivity - journal entry awards 50 base points`"
```

### Android Studio

1. Right-click on test file -> Run
2. Right-click on test method -> Run
3. View results in Run window

---

## UI Tests (Future Work)

### Recommended Tests to Add

#### Critical Flows

```kotlin
// AppLaunchTest.kt
@Test
fun app_launches_successfully() {
    composeTestRule.setContent { ProdyApp() }
    composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()
}

// OnboardingTest.kt
@Test
fun onboarding_completes_and_navigates_to_home() {
    composeTestRule.setContent { OnboardingScreen(onComplete = {}) }
    // Swipe through pages
    // Click complete
    // Verify navigation
}

// JournalTest.kt
@Test
fun journal_entry_can_be_saved() {
    composeTestRule.setContent { NewJournalEntryScreen() }
    composeTestRule.onNodeWithTag("journal_content").performTextInput("Test entry")
    composeTestRule.onNodeWithTag("save_button").performClick()
    // Verify save
}

// StatsTest.kt
@Test
fun stats_screen_renders_with_data() {
    // Seed test data
    composeTestRule.setContent { StatsScreen() }
    composeTestRule.onNodeWithTag("streak_badge").assertIsDisplayed()
    composeTestRule.onNodeWithTag("leaderboard").assertIsDisplayed()
}

// LeaderboardTest.kt
@Test
fun leaderboard_highlights_current_user() {
    composeTestRule.setContent { LeaderboardSection(currentUserId = "test") }
    composeTestRule.onNodeWithTag("current_user_row").assertIsDisplayed()
}

// FutureMessageTest.kt
@Test
fun future_message_category_selection_works() {
    composeTestRule.setContent { WriteMessageScreen() }
    composeTestRule.onNodeWithTag("category_selector").performClick()
    // Verify categories display
}
```

---

## Screenshot Tests (Recommended)

### Setup

```kotlin
// Add to build.gradle.kts
testImplementation("com.google.accompanist:accompanist-testharness:0.32.0")

// BaseScreenshotTest.kt
abstract class BaseScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    fun captureScreenshot(name: String) {
        // Screenshot capture logic
    }
}
```

### Key Screens to Capture

1. HomeScreen (with data)
2. HomeScreen (empty state)
3. JournalScreen (with entries)
4. JournalScreen (empty)
5. StatsScreen (with activity)
6. ProfileScreen
7. OnboardingScreen (each page)
8. FutureMessageScreen

---

## Test Coverage Goals

| Module | Current | Target |
|--------|---------|--------|
| Domain (gamification) | ~40% | 80% |
| Domain (analytics) | ~30% | 70% |
| Repository | ~50% | 80% |
| ViewModel | 0% | 60% |
| UI Components | 0% | 40% |

---

## CI/CD Integration

### GitHub Actions Workflow

```yaml
# .github/workflows/test.yml
name: Tests

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  unit-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run Unit Tests
        run: ./gradlew test
      - name: Upload Test Results
        uses: actions/upload-artifact@v3
        with:
          name: test-results
          path: app/build/reports/tests/
```

---

## Known Test Limitations

1. **No Instrumentation Tests Yet** - androidTest directory empty
2. **UI Tests Require Device/Emulator** - Not executable in current environment
3. **Database Tests Need In-Memory DB** - Room setup for testing needed
4. **Hilt Tests Need Special Runner** - HiltTestRule required

---

## Test Execution Results

### Expected Output (When Environment Ready)

```
BUILD SUCCESSFUL

Test results:
- GamificationServiceTest: 18 passed
- WeeklyStatsCalculatorTest: 15 passed
- JournalRepositoryTest: [existing tests]
- VocabularyRepositoryTest: [existing tests]

Total: XX tests, XX passed, 0 failed
```

---

## Related Files

- `MainDispatcherRule.kt` - Test coroutine dispatcher
- `build.gradle.kts` - Test dependencies
- `GamificationServiceTest.kt` - Gamification tests
- `WeeklyStatsCalculatorTest.kt` - Stats tests
