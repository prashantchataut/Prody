# Prody Development Roadmap

> Comprehensive long-term development plan for transforming Prody into a world-class personal growth companion app.

**Last Updated**: December 12, 2024  
**Current Version**: 1.0.0  
**Target Version**: 2.0.0

---

## 📋 Table of Contents

1. [Phase 1: Foundation & Quality](#phase-1-foundation--quality-weeks-1-4)
2. [Phase 2: Enhanced Learning Engine](#phase-2-enhanced-learning-engine-weeks-5-8)
3. [Phase 3: Platform Features](#phase-3-platform-features-weeks-9-12)
4. [Phase 4: AI & Personalization](#phase-4-ai--personalization-weeks-13-16)
5. [Phase 5: Social & Community](#phase-5-social--community-weeks-17-20)
6. [Technical Debt & Infrastructure](#technical-debt--infrastructure)
7. [Implementation Details](#implementation-details)

---

## Phase 1: Foundation & Quality (Weeks 1-4)

### 1.1 Testing Infrastructure ⚡ CRITICAL

**Objective**: Establish a robust testing foundation

#### Unit Tests
```
📁 app/src/test/java/com/prody/prashant/
├── data/
│   ├── GeminiServiceTest.kt
│   ├── PreferencesManagerTest.kt
│   └── repository/
│       ├── VocabularyRepositoryTest.kt
│       ├── JournalRepositoryTest.kt
│       └── QuoteRepositoryTest.kt
├── ui/
│   └── viewmodel/
│       ├── HomeViewModelTest.kt
│       ├── JournalViewModelTest.kt
│       ├── VocabularyViewModelTest.kt
│       └── OnboardingViewModelTest.kt
└── domain/
    └── model/
        └── AchievementTest.kt
```

**Implementation Tasks**:
- [ ] Add testing dependencies to `build.gradle.kts`:
  ```kotlin
  testImplementation("junit:junit:4.13.2")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
  testImplementation("io.mockk:mockk:1.13.8")
  testImplementation("app.cash.turbine:turbine:1.0.0")
  testImplementation("androidx.arch.core:core-testing:2.2.0")
  ```
- [ ] Create test utilities (FakePreferencesManager, FakeDAOs)
- [ ] Achieve 60%+ code coverage on ViewModels
- [ ] Add ViewModel state testing with Turbine

#### UI Tests (Compose)
```kotlin
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
androidTestImplementation("androidx.test.ext:junit:1.1.5")
androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
```

**Deliverables**:
- [ ] CI pipeline runs tests on every PR
- [ ] Test coverage reports in artifacts
- [ ] Minimum 40% overall coverage

---

### 1.2 Repository Pattern Completion

**Objective**: Create proper abstraction layer between ViewModels and Data sources

#### New Repository Interfaces
```kotlin
// domain/repository/VocabularyRepository.kt
interface VocabularyRepository {
    fun getAllWords(): Flow<List<VocabularyWord>>
    fun getWordOfTheDay(): Flow<VocabularyWord?>
    fun getWordsToReview(): Flow<List<VocabularyWord>>
    suspend fun markWordAsLearned(wordId: Long)
    suspend fun updateWordProgress(wordId: Long, progress: WordProgress)
}

// domain/repository/JournalRepository.kt
interface JournalRepository {
    fun getAllEntries(): Flow<List<JournalEntry>>
    fun getEntriesByMood(mood: Mood): Flow<List<JournalEntry>>
    fun getMoodAnalytics(period: AnalyticsPeriod): Flow<MoodAnalytics>
    suspend fun saveEntry(entry: JournalEntry): Long
    suspend fun deleteEntry(entryId: Long)
}
```

**Implementation Structure**:
```
📁 data/repository/
├── VocabularyRepositoryImpl.kt
├── JournalRepositoryImpl.kt
├── QuoteRepositoryImpl.kt
├── ChallengeRepositoryImpl.kt
└── UserRepositoryImpl.kt
```

**Hilt Binding**:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindVocabularyRepository(
        impl: VocabularyRepositoryImpl
    ): VocabularyRepository
}
```

---

### 1.3 Error Handling Framework

**Objective**: Consistent error handling across the app

```kotlin
// domain/common/Result.kt
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(
        val exception: Throwable,
        val userMessage: String,
        val errorType: ErrorType
    ) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

enum class ErrorType {
    NETWORK, DATABASE, AI_SERVICE, VALIDATION, UNKNOWN
}

// ui/common/ErrorHandler.kt
@Composable
fun ErrorDialog(
    error: Result.Error,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null
)

@Composable
fun ErrorSnackbar(
    error: Result.Error,
    snackbarHostState: SnackbarHostState
)
```

---

### 1.4 Performance Optimization

**Objective**: Identify and fix performance bottlenecks

- [ ] **Compose Performance**:
  - Add `@Stable` and `@Immutable` annotations where appropriate
  - Use `remember` and `derivedStateOf` properly
  - Profile with Layout Inspector

- [ ] **Database Optimization**:
  - Add indices to frequently queried columns
  - Implement pagination for large lists
  - Cache expensive queries

- [ ] **Lazy Loading**:
  - Implement proper `LazyColumn` keys
  - Use `contentType` for heterogeneous lists

---

## Phase 2: Enhanced Learning Engine (Weeks 5-8)

### 2.1 Spaced Repetition System (SRS) 🧠

**Objective**: Implement SM-2 algorithm for vocabulary retention

#### Database Schema
```kotlin
@Entity(tableName = "vocabulary_learning")
data class VocabularyLearningEntity(
    @PrimaryKey val wordId: Long,
    val easeFactor: Float = 2.5f,      // Difficulty factor
    val interval: Int = 1,              // Days until next review
    val repetitions: Int = 0,           // Consecutive correct answers
    val nextReviewDate: Long,           // Timestamp
    val lastReviewDate: Long? = null,
    val totalReviews: Int = 0,
    val correctReviews: Int = 0
)
```

#### SM-2 Algorithm Implementation
```kotlin
// domain/learning/SpacedRepetitionEngine.kt
class SpacedRepetitionEngine {
    
    data class ReviewResult(
        val newInterval: Int,
        val newEaseFactor: Float,
        val newRepetitions: Int,
        val nextReviewDate: Long
    )
    
    fun calculateNextReview(
        quality: Int,  // 0-5 rating
        currentInterval: Int,
        currentEaseFactor: Float,
        currentRepetitions: Int
    ): ReviewResult {
        // SM-2 implementation
        val newEaseFactor = maxOf(
            1.3f,
            currentEaseFactor + (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
        )
        
        val newRepetitions = if (quality >= 3) currentRepetitions + 1 else 0
        
        val newInterval = when {
            quality < 3 -> 1
            newRepetitions == 1 -> 1
            newRepetitions == 2 -> 6
            else -> (currentInterval * newEaseFactor).toInt()
        }
        
        return ReviewResult(
            newInterval = newInterval,
            newEaseFactor = newEaseFactor,
            newRepetitions = newRepetitions,
            nextReviewDate = System.currentTimeMillis() + (newInterval * 24 * 60 * 60 * 1000L)
        )
    }
}
```

#### UI Components
- [ ] **Review Screen**: Flashcard-style interface with swipe gestures
- [ ] **Progress Dashboard**: Visual representation of mastery levels
- [ ] **Review Notifications**: Smart reminders based on optimal review times

---

### 2.2 Interactive Flashcard System

**Objective**: Tinder-style swipe interface for vocabulary review

```kotlin
@Composable
fun FlashcardStack(
    cards: List<VocabularyWord>,
    onSwipeLeft: (VocabularyWord) -> Unit,  // Don't know
    onSwipeRight: (VocabularyWord) -> Unit, // Know
    onSwipeUp: (VocabularyWord) -> Unit     // Skip
) {
    // Swipeable card stack with animations
}
```

**Features**:
- [ ] Card flip animations
- [ ] Progress indicator (X of Y cards)
- [ ] Speed review mode (no animations)
- [ ] Undo last swipe
- [ ] Session statistics

---

### 2.3 Mood Analytics Dashboard

**Objective**: Comprehensive visualization of journal mood data

#### Data Models
```kotlin
data class MoodAnalytics(
    val weeklyTrend: List<DailyMoodData>,
    val monthlyDistribution: Map<Mood, Int>,
    val averageMoodIntensity: Float,
    val mostCommonMood: Mood,
    val moodByTimeOfDay: Map<TimeOfDay, Mood>,
    val wordFrequency: Map<String, Int>,
    val streakWithPositiveMood: Int
)

data class DailyMoodData(
    val date: LocalDate,
    val mood: Mood,
    val intensity: Float,
    val entryCount: Int
)
```

#### Visualizations
- [ ] **Weekly Mood Chart**: Line chart with mood trend
- [ ] **Mood Calendar Heatmap**: Monthly grid view
- [ ] **Mood Distribution Pie Chart**: Breakdown by mood type
- [ ] **Time-of-Day Analysis**: When user feels best
- [ ] **Word Cloud**: Most used words in journals

---

### 2.4 Word Association Games

**Objective**: Gamified vocabulary reinforcement

#### Game Types
```kotlin
sealed class VocabularyGame {
    data class SynonymMatch(
        val word: VocabularyWord,
        val options: List<String>,
        val correctAnswer: String
    ) : VocabularyGame()
    
    data class FillInTheBlank(
        val sentence: String,
        val word: VocabularyWord,
        val distractors: List<String>
    ) : VocabularyGame()
    
    data class WordChain(
        val startWord: VocabularyWord,
        val targetWord: VocabularyWord,
        val steps: Int
    ) : VocabularyGame()
    
    data class TimedQuiz(
        val words: List<VocabularyWord>,
        val timePerQuestion: Int = 10
    ) : VocabularyGame()
}
```

**Features**:
- [ ] Local high scores with Room
- [ ] Daily challenge mode
- [ ] Difficulty progression
- [ ] Achievement unlocks for game milestones

---

## Phase 3: Platform Features (Weeks 9-12)

### 3.1 Home Screen Widgets

**Objective**: Quick access to daily content from home screen

#### Widget Types
```kotlin
// Daily Quote Widget (4x2)
class DailyQuoteWidget : GlanceAppWidget() {
    @Composable
    override fun Content() {
        // Glance Compose UI
    }
}

// Word of the Day Widget (2x2)
class WordOfDayWidget : GlanceAppWidget()

// Streak Counter Widget (1x1)
class StreakWidget : GlanceAppWidget()

// Quick Journal Widget (4x1)
class QuickJournalWidget : GlanceAppWidget()
```

**Dependencies**:
```kotlin
implementation("androidx.glance:glance:1.0.0")
implementation("androidx.glance:glance-appwidget:1.0.0")
```

**Implementation Tasks**:
- [ ] Create `GlanceAppWidgetReceiver` for each widget
- [ ] Implement widget configuration activities
- [ ] Set up `WorkManager` for periodic updates
- [ ] Handle widget click actions to open app

---

### 3.2 Text-to-Speech Integration

**Objective**: Audio pronunciation for vocabulary learning

```kotlin
// util/TextToSpeechManager.kt
@Singleton
class TextToSpeechManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    
    fun initialize(onReady: () -> Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                isInitialized = true
                onReady()
            }
        }
    }
    
    fun speak(text: String, speed: Float = 1.0f, pitch: Float = 1.0f) {
        tts?.setSpeechRate(speed)
        tts?.setPitch(pitch)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }
    
    fun shutdown() {
        tts?.shutdown()
        tts = null
    }
}
```

**Features**:
- [ ] Play button on vocabulary cards
- [ ] Configurable speed and pitch in settings
- [ ] Batch pronunciation mode
- [ ] Queue management

---

### 3.3 Voice Journal Entries

**Objective**: Speech-to-text for hands-free journaling

```kotlin
// util/SpeechRecognizerManager.kt
class SpeechRecognizerManager(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (Int) -> Unit
) {
    private var speechRecognizer: SpeechRecognizer? = null
    
    fun startListening() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle) {
                    val matches = results.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )
                    matches?.firstOrNull()?.let { onResult(it) }
                }
                // ... other callbacks
            })
        }
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, 
                     RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        
        speechRecognizer?.startListening(intent)
    }
}
```

**Features**:
- [ ] Voice recording button in journal entry
- [ ] Real-time transcription preview
- [ ] Punctuation commands
- [ ] Voice activation option

---

### 3.4 Shareable Quote Cards

**Objective**: Generate beautiful quote images for social sharing

```kotlin
// util/QuoteCardGenerator.kt
class QuoteCardGenerator {
    
    suspend fun generateQuoteImage(
        quote: String,
        author: String,
        template: CardTemplate,
        size: IntSize = IntSize(1080, 1080)
    ): Bitmap = withContext(Dispatchers.Default) {
        // Create bitmap with Canvas drawing
        val bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Draw background, text, watermark
        template.apply(canvas, quote, author)
        
        bitmap
    }
    
    enum class CardTemplate {
        MINIMAL,
        GRADIENT,
        NATURE,
        DARK_MODE,
        ELEGANT
    }
}
```

**Features**:
- [ ] Multiple card templates
- [ ] Custom background colors/gradients
- [ ] Optional watermark with app branding
- [ ] Direct share to Instagram, Twitter, etc.

---

## Phase 4: AI & Personalization (Weeks 13-16)

### 4.1 Advanced Buddha AI Modes

**Objective**: Multiple AI personality modes for varied guidance

```kotlin
enum class BuddhaMode(
    val displayName: String,
    val systemPrompt: String
) {
    STOIC(
        "Stoic Sage",
        """You are a Stoic philosopher in the tradition of Marcus Aurelius, Seneca, and Epictetus.
           Focus on: control over emotions, virtue as highest good, accepting fate, living according to nature.
           Style: Direct, wise, measured, with occasional quotes from Stoic texts."""
    ),
    ZEN(
        "Zen Master",
        """You are a Zen Buddhist master offering minimalist wisdom.
           Focus on: Present moment awareness, koans and paradoxes, nature metaphors, silence and space.
           Style: Brief, poetic, uses questions to provoke insight, comfortable with ambiguity."""
    ),
    MOTIVATIONAL(
        "Peak Performer",
        """You are an energetic life coach focused on peak performance.
           Focus on: Action-oriented advice, goal-setting, building momentum, celebrating wins.
           Style: Enthusiastic, direct, uses power words, creates urgency."""
    ),
    PHILOSOPHICAL(
        "Socratic Guide",
        """You are a Socratic philosopher who guides through questioning.
           Focus on: Self-discovery through dialogue, examining assumptions, logical thinking.
           Style: Asks probing questions, never gives direct answers, encourages deeper thinking."""
    ),
    COMPASSIONATE(
        "Loving Kindness",
        """You are a compassionate presence offering unconditional support.
           Focus on: Self-compassion, emotional validation, healing, gentle encouragement.
           Style: Warm, nurturing, never judging, focuses on self-acceptance."""
    )
}
```

**Implementation**:
- [ ] Mode selector in Settings
- [ ] Mode indicator in Buddha Chat
- [ ] Mode-specific visual themes
- [ ] Smooth transition between modes

---

### 4.2 Content Recommendation Engine

**Objective**: Personalized content surfacing based on user behavior

```kotlin
// domain/recommendation/RecommendationEngine.kt
class RecommendationEngine @Inject constructor(
    private val userStatsRepository: UserStatsRepository,
    private val contentRepository: ContentRepository
) {
    data class UserPreferences(
        val favoriteTopics: List<String>,
        val preferredContentLength: ContentLength,
        val engagementPatterns: Map<ContentType, Float>,
        val completionRate: Float,
        val activeHours: List<Int>
    )
    
    suspend fun getRecommendations(
        count: Int = 10
    ): List<ContentRecommendation> {
        val preferences = analyzeUserBehavior()
        return contentRepository.getContentByRelevance(preferences)
            .take(count)
    }
    
    private suspend fun analyzeUserBehavior(): UserPreferences {
        // Analyze user interaction patterns
    }
}
```

**Features**:
- [ ] "For You" section on home screen
- [ ] "Because you liked X" suggestions
- [ ] Difficulty progression recommendations
- [ ] Time-of-day based suggestions

---

### 4.3 Adaptive Learning Path

**Objective**: Personalized learning journey based on skill assessment

```kotlin
data class LearningPath(
    val currentLevel: SkillLevel,
    val targetLevel: SkillLevel,
    val modules: List<LearningModule>,
    val estimatedCompletionDays: Int,
    val progressPercentage: Float
)

data class LearningModule(
    val id: String,
    val title: String,
    val description: String,
    val skills: List<String>,
    val prerequisites: List<String>,
    val estimatedMinutes: Int,
    val xpReward: Int,
    val status: ModuleStatus
)

enum class SkillLevel { BEGINNER, INTERMEDIATE, ADVANCED, EXPERT }
enum class ModuleStatus { LOCKED, AVAILABLE, IN_PROGRESS, COMPLETED }
```

**Features**:
- [ ] Initial skill assessment quiz
- [ ] Dynamic difficulty adjustment
- [ ] Skip already-known content
- [ ] Focus on weakness areas
- [ ] Learning velocity tracking

---

### 4.4 Weekly AI-Generated Digest

**Objective**: Personalized weekly summary with insights

```kotlin
data class WeeklyDigest(
    val weekOf: LocalDate,
    val journalSummary: JournalWeekSummary,
    val learningProgress: LearningWeekSummary,
    val achievements: List<Achievement>,
    val buddhaInsight: String,
    val nextWeekGoals: List<SuggestedGoal>,
    val shareableImage: Bitmap?
)

suspend fun generateWeeklyDigest(): WeeklyDigest {
    val journals = journalRepository.getEntriesForWeek()
    val learning = learningRepository.getWeekProgress()
    
    val buddhaInsight = geminiService.generateWeeklySummary(
        journalCount = journals.size,
        wordsLearned = learning.wordsLearned,
        dominantMood = journals.dominantMood(),
        streakDays = streakRepository.getCurrentStreak()
    )
    
    return WeeklyDigest(
        weekOf = LocalDate.now().startOfWeek(),
        journalSummary = journals.toSummary(),
        learningProgress = learning.toSummary(),
        achievements = achievementRepository.getUnlockedThisWeek(),
        buddhaInsight = buddhaInsight,
        nextWeekGoals = generateSuggestedGoals(journals, learning)
    )
}
```

---

## Phase 5: Social & Community (Weeks 17-20)

### 5.1 Local Leaderboard Enhancement

**Objective**: More engaging social features without cloud backend

```kotlin
// Simulated peer system using generated profiles
data class SimulatedPeer(
    val id: String,
    val displayName: String,
    val avatarSeed: Int,
    val streakDays: Int,
    val totalXp: Int,
    val learningStyle: String,
    val motivationalMessage: String
)

class PeerSimulator {
    fun generatePeers(count: Int, userStats: UserStats): List<SimulatedPeer> {
        // Generate realistic-looking peers based on user's level
        // Some above, some below, to create motivation
    }
    
    fun updatePeerProgress(peers: List<SimulatedPeer>): List<SimulatedPeer> {
        // Simulate daily progress for each peer
    }
}
```

---

### 5.2 Challenge System Enhancement

**Objective**: More engaging daily and weekly challenges

```kotlin
sealed class Challenge {
    abstract val id: String
    abstract val title: String
    abstract val description: String
    abstract val xpReward: Int
    abstract val expiresAt: Long
    
    data class VocabularyChallenge(
        override val id: String,
        override val title: String,
        override val description: String,
        override val xpReward: Int,
        override val expiresAt: Long,
        val targetWords: Int,
        val difficulty: Difficulty
    ) : Challenge()
    
    data class JournalChallenge(
        override val id: String,
        override val title: String,
        override val description: String,
        override val xpReward: Int,
        override val expiresAt: Long,
        val promptType: JournalPromptType,
        val minWordCount: Int
    ) : Challenge()
    
    data class StreakChallenge(
        override val id: String,
        override val title: String,
        override val description: String,
        override val xpReward: Int,
        override val expiresAt: Long,
        val targetDays: Int
    ) : Challenge()
}
```

**Features**:
- [ ] Daily rotating challenges
- [ ] Weekly epic challenges
- [ ] Challenge chains with narrative
- [ ] Milestone celebrations with animations

---

### 5.3 Achievement System 2.0

**Objective**: More achievements with better progression

#### New Achievement Categories
- **Learning Mastery**: Word count milestones, quiz scores
- **Consistency**: Streak achievements, daily routines
- **Journaling**: Entry milestones, mood tracking
- **Exploration**: Discovering all features
- **Social**: Sharing, contributing
- **Hidden**: Secret achievements for special actions

#### Achievement Tiers
```kotlin
enum class AchievementTier(val multiplier: Float) {
    BRONZE(1.0f),
    SILVER(1.5f),
    GOLD(2.0f),
    PLATINUM(3.0f),
    DIAMOND(5.0f)
}
```

---

## Technical Debt & Infrastructure

### Immediate Priorities

#### 1. Lint Warnings Resolution
```bash
./gradlew lint
# Address all warnings in lint-results.html
```

#### 2. ProGuard/R8 Optimization
```kotlin
// proguard-rules.pro
-keep class com.prody.prashant.data.local.entity.** { *; }
-keep class com.prody.prashant.domain.model.** { *; }
```

#### 3. Dependency Updates
```kotlin
// Check for updates
./gradlew dependencyUpdates
```

### Build Performance
- [ ] Enable Gradle build cache
- [ ] Configure Gradle daemon properly
- [ ] Enable parallel execution
- [ ] Implement incremental annotation processing

### Code Quality
- [ ] Add Detekt for static analysis
- [ ] Add Ktlint for code formatting
- [ ] Create custom lint rules for project patterns

---

## Implementation Details

### Migration Strategy

For database schema changes:
```kotlin
@Database(
    entities = [...],
    version = 3,  // Increment for each migration
    autoMigrations = [
        AutoMigration(from = 2, to = 3, spec = Migration2To3::class)
    ]
)
abstract class ProdyDatabase : RoomDatabase()

@RenameColumn(tableName = "vocabulary", fromColumnName = "old_name", toColumnName = "new_name")
class Migration2To3 : AutoMigrationSpec
```

### Feature Flags

For gradual rollout:
```kotlin
object FeatureFlags {
    val SRS_ENABLED = BuildConfig.DEBUG || BuildConfig.FLAVOR == "beta"
    val VOICE_JOURNAL_ENABLED = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    val WIDGETS_ENABLED = true
    val AI_MODES_ENABLED = true
}
```

### Monitoring & Analytics

Using local-only analytics:
```kotlin
@Entity(tableName = "analytics_events")
data class AnalyticsEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventName: String,
    val properties: String,  // JSON
    val timestamp: Long,
    val sessionId: String
)
```

---

## Timeline Summary

| Phase | Duration | Key Deliverables |
|-------|----------|------------------|
| **Phase 1** | Weeks 1-4 | Testing, Repositories, Error Handling |
| **Phase 2** | Weeks 5-8 | SRS, Flashcards, Mood Analytics, Games |
| **Phase 3** | Weeks 9-12 | Widgets, TTS, Voice Journal, Share Cards |
| **Phase 4** | Weeks 13-16 | AI Modes, Recommendations, Learning Paths |
| **Phase 5** | Weeks 17-20 | Enhanced Social, Challenges, Achievements |

---

## Success Metrics

| Metric | Current | Target |
|--------|---------|--------|
| Test Coverage | 0% | 60%+ |
| Lint Warnings | Unknown | 0 |
| Build Time | ~3min | <2min |
| APK Size | 22MB | <20MB |
| User Retention (D7) | N/A | 40%+ |
| Daily Active Features | 3 | 5+ |

---

## Resources & References

- [Android Developer Guides](https://developer.android.com/guide)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [SM-2 Algorithm](https://www.supermemo.com/en/archives1990-2015/english/ol/sm2)
- [Glance Widgets](https://developer.android.com/jetpack/compose/glance)

---

*This roadmap is a living document and will be updated as priorities evolve.*
