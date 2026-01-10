# Vocabulary in Context System

## Overview

The Vocabulary in Context system connects learned vocabulary words to actual usage in journal entries. It provides smart word detection, contextual suggestions, and celebrates when users apply their learned vocabulary.

## Architecture

### Core Components

#### 1. VocabularyDetector
**Location:** `domain/vocabulary/VocabularyDetector.kt`

Interface for detecting learned vocabulary words in text content.

**Features:**
- Case-insensitive matching
- Handles different word forms (run/running/ran)
- Word boundary detection (no partial matches)
- Sentence extraction for context

**Key Classes:**
```kotlin
interface VocabularyDetector {
    fun detectLearnedWords(content: String, learnedWords: List<VocabularyEntity>): List<WordUsage>
    fun extractSentence(content: String, position: IntRange): String
}

data class WordUsage(
    val word: VocabularyEntity,
    val usedIn: String,
    val position: IntRange,
    val detectedAt: Instant,
    val matchedForm: String
)
```

#### 2. VocabularyDetectorImpl
**Location:** `domain/vocabulary/VocabularyDetectorImpl.kt`

Implementation with smart word detection including:
- Regular and irregular verb forms
- Plural forms
- Comparative/superlative forms
- 50+ common irregular verbs

#### 3. VocabularyCelebrationService
**Location:** `domain/vocabulary/VocabularyCelebrationService.kt`

Celebrates vocabulary usage with:
- Subtle, encouraging messages
- Bonus discipline points (3 points per usage)
- Daily caps to prevent farming (max 6 points per word per day)
- Usage tracking and statistics

**Key Methods:**
```kotlin
suspend fun processWordUsages(
    journalEntryId: Long,
    wordUsages: List<WordUsage>,
    userId: String
): List<VocabularyCelebration>

suspend fun getLearnedButUnusedWords(userId: String, limit: Int): List<Long>
```

#### 4. VocabularySuggestionEngine
**Location:** `domain/vocabulary/VocabularySuggestionEngine.kt`

Provides contextual word suggestions based on:
- Topic detection (emotions, work, relationships, growth, etc.)
- Word relevance scoring
- Recent usage filtering (3-day cooldown)
- Maximum 3 suggestions at once

**Features:**
- 10 topic categories with keyword matching
- Relevance scoring (0.0-1.0)
- Part of speech boosting
- Non-intrusive suggestions

### Database Layer

#### 1. WordUsageEntity
**Location:** `data/local/entity/WordUsageEntity.kt`

Tracks vocabulary word usage in journal entries.

**Fields:**
- `wordId`: Vocabulary word that was used
- `journalEntryId`: Journal entry where it was used
- `usedInSentence`: The sentence containing the word
- `matchedForm`: Actual form matched (e.g., "running" for "run")
- `positionStart/End`: Position in content
- `celebrated`: Whether user saw celebration
- `bonusPointsAwarded`: Points earned
- `pointsClaimed`: Whether points were awarded

#### 2. WordUsageDao
**Location:** `data/local/dao/WordUsageDao.kt`

Provides queries for:
- Getting usages by word or journal entry
- Counting unique words used
- Finding learned but unused words
- Celebration management
- Usage statistics

**Key Queries:**
```kotlin
suspend fun getUsagesByWord(wordId: Long, userId: String): List<WordUsageEntity>
suspend fun countUniqueWordsUsed(userId: String): Int
suspend fun getLearnedButUnusedWordIds(userId: String, limit: Int): List<Long>
suspend fun getUncelebratedUsages(userId: String, limit: Int): List<WordUsageEntity>
```

#### 3. VocabularyLearningEntity Updates
**New fields added:**
- `usedInContext: Boolean` - Whether word has been used in journal
- `lastUsedAt: Long?` - Timestamp of last usage
- `timesUsed: Int` - Total number of times used

### UI Layer

#### 1. VocabularyReviewScreen
**Location:** `ui/screens/vocabulary/VocabularyReviewScreen.kt`

Shows vocabulary progress including:
- Words learned this week
- Words used in context (with celebration badges)
- Words not yet used (gentle nudge)
- Vocabulary growth chart (12-week view)
- Statistics cards (learned, used, bonus points)

**Features:**
- Clean, card-based design
- Animated transitions
- Interactive word chips
- Growth visualization

#### 2. VocabularyReviewViewModel
**Location:** `ui/screens/vocabulary/VocabularyReviewViewModel.kt`

Manages:
- Loading vocabulary statistics
- Calculating growth data
- Filtering words by usage status
- Providing words needing practice

#### 3. VocabularySuggestionChip
**Location:** `ui/components/VocabularySuggestionChip.kt`

Reusable components:
- `VocabularySuggestionSection`: Shows suggestion chips
- `VocabularySuggestionChip`: Individual suggestion
- `VocabularyCelebrationBadge`: Celebration notification
- `InlineVocabularyCelebrations`: Post-save celebrations

### Integration Points

#### NewJournalEntryScreen Integration

**To integrate vocabulary detection and suggestions:**

1. Add to ViewModel dependencies:
```kotlin
@HiltViewModel
class NewJournalEntryViewModel @Inject constructor(
    // ... existing dependencies
    private val vocabularyDetector: VocabularyDetector,
    private val celebrationService: VocabularyCelebrationService,
    private val suggestionEngine: VocabularySuggestionEngine,
    private val vocabularyDao: VocabularyDao,
    private val wordUsageDao: WordUsageDao
) : ViewModel()
```

2. Add to UI State:
```kotlin
data class NewJournalEntryUiState(
    // ... existing fields
    val vocabularySuggestions: List<VocabularySuggestion> = emptyList(),
    val vocabularyCelebrations: List<VocabularyCelebration> = emptyList(),
    val showVocabularySuggestions: Boolean = false
)
```

3. Add content analysis with debounce:
```kotlin
private val contentFlow = MutableStateFlow("")

init {
    contentFlow
        .debounce(500) // Wait 500ms after user stops typing
        .collectLatest { content ->
            if (content.length >= 100) {
                analyzContentForSuggestions(content)
            }
        }
}

private suspend fun analyzContentForSuggestions(content: String) {
    val suggestions = suggestionEngine.getSuggestions(content, userId)
    _uiState.update {
        it.copy(
            vocabularySuggestions = suggestions,
            showVocabularySuggestions = suggestions.isNotEmpty()
        )
    }
}
```

4. On save, detect word usages:
```kotlin
suspend fun saveEntry() {
    // ... existing save logic

    // Detect vocabulary usage
    val learnedWords = vocabularyDao.getLearnedWords().first()
    val wordUsages = vocabularyDetector.detectLearnedWords(
        content = uiState.value.content,
        learnedWords = learnedWords
    )

    // Process celebrations
    val celebrations = celebrationService.processWordUsages(
        journalEntryId = savedEntryId,
        wordUsages = wordUsages,
        userId = userId
    )

    // Update UI
    _uiState.update {
        it.copy(
            vocabularyCelebrations = celebrations,
            isSaved = true
        )
    }
}
```

5. Add to UI:
```kotlin
// In NewJournalEntryScreen composable

// Add after content input field
VocabularySuggestionSection(
    suggestions = uiState.vocabularySuggestions,
    onSuggestionClick = { word ->
        viewModel.insertWordAtCursor(word.word)
        viewModel.dismissSuggestions()
    },
    onDismiss = { viewModel.dismissSuggestions() }
)

// Add celebrations to session result
if (uiState.vocabularyCelebrations.isNotEmpty()) {
    InlineVocabularyCelebrations(
        celebrations = uiState.vocabularyCelebrations.map {
            it.word.word to it.bonusPoints
        }
    )
}
```

## Database Migration

Migration 8 -> 9 adds:
1. `word_usages` table with foreign keys to vocabulary and journal_entries
2. Indices for efficient querying
3. New fields to `vocabulary_learning` table

**Migration code in ProdyDatabase.kt:**
```kotlin
val MIGRATION_8_9: Migration = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create word_usages table
        // Add indices
        // Add columns to vocabulary_learning
    }
}
```

## Feature Highlights

### 1. Smart Word Detection
- Handles 50+ irregular verbs
- Detects plurals, verb forms, comparatives
- Word boundary matching (no partial matches)
- Extracts full sentences for context

### 2. Contextual Suggestions
- Analyzes content topics
- Suggests relevant learned words
- Non-intrusive (only highly relevant)
- 3-day cooldown per word
- Maximum 3 suggestions

### 3. Celebration System
- Subtle, encouraging messages
- Bonus discipline points (+3 per usage)
- Daily caps to prevent farming
- Tracks celebration history
- Inline celebration badges

### 4. Progress Tracking
- Words learned vs. words used
- Weekly growth chart
- Usage percentage
- Total bonus points earned
- Words needing practice

### 5. Review Screen
- Clean, card-based UI
- Interactive word chips
- Growth visualization
- Categorized word lists
- Statistics overview

## Design Principles

1. **Non-intrusive**: Suggestions appear only when highly relevant
2. **Encouraging**: Celebrations are positive and motivating
3. **Subtle**: No popups or interruptions
4. **Helpful**: Suggestions enhance writing, don't distract
5. **Smart**: Understands context and word forms
6. **Trackable**: Full analytics and progress tracking

## Usage Example

```kotlin
// 1. User writes journal entry with word "ephemeral"
val content = "Today felt ephemeral, like a fleeting moment..."

// 2. System detects the word
val learnedWords = vocabularyDao.getLearnedWords().first()
val detectedWords = vocabularyDetector.detectLearnedWords(content, learnedWords)
// Result: [WordUsage(word="ephemeral", usedIn="Today felt ephemeral...", ...)]

// 3. Celebrate usage
val celebrations = celebrationService.processWordUsages(
    journalEntryId = 123,
    wordUsages = detectedWords,
    userId = "local"
)
// Result: [VocabularyCelebration(message="You used 'ephemeral' in context! +3 Discipline", ...)]

// 4. Award points and update statistics
// - VocabularyLearningEntity updated with usedInContext=true
// - WordUsageEntity created
// - Bonus discipline points awarded
```

## Future Enhancements

1. **Advanced Suggestions**
   - ML-based relevance scoring
   - Synonym suggestions
   - Context-aware definitions

2. **Gamification**
   - Word usage streaks
   - Vocabulary mastery badges
   - Weekly word challenges

3. **Analytics**
   - Most used words
   - Vocabulary diversity score
   - Usage patterns over time

4. **Social Features**
   - Share vocabulary achievements
   - Word usage leaderboards
   - Peer vocabulary recommendations

## Testing Recommendations

1. **Unit Tests**
   - Word form generation
   - Relevance scoring
   - Sentence extraction

2. **Integration Tests**
   - Full detection pipeline
   - Database operations
   - Celebration flow

3. **UI Tests**
   - Suggestion display
   - Celebration badges
   - Review screen navigation

## Performance Considerations

1. **Debounced Analysis**: 500ms debounce prevents excessive processing
2. **Indexed Queries**: All queries use database indices
3. **Lazy Loading**: Suggestions load only when needed
4. **Caching**: Recent words cached to avoid repeated queries
5. **Batch Operations**: Multiple usages processed together

## Accessibility

- All interactive elements have content descriptions
- High contrast colors for readability
- Keyboard navigation support
- Screen reader compatible
- Dismissable suggestions

## Error Handling

- Graceful degradation if detection fails
- Error messages in snackbars
- Automatic retry on network errors
- Fallback to basic mode if needed

---

**Version:** 1.0
**Last Updated:** 2026-01-10
**Database Version:** 9
