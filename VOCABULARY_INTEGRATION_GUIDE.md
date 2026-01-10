# Vocabulary in Context - NewJournalEntryScreen Integration Guide

This guide provides step-by-step instructions for integrating the Vocabulary in Context system into the NewJournalEntryScreen.

## Step 1: Update NewJournalEntryViewModel Dependencies

Add the vocabulary services to the ViewModel constructor:

```kotlin
@HiltViewModel
class NewJournalEntryViewModel @Inject constructor(
    // ... existing dependencies
    private val vocabularyDetector: VocabularyDetector,
    private val celebrationService: VocabularyCelebrationService,
    private val suggestionEngine: VocabularySuggestionEngine,
    private val vocabularyDao: VocabularyDao,
    private val wordUsageDao: WordUsageDao
) : ViewModel() {
    // ...
}
```

## Step 2: Update UI State

Add vocabulary-related fields to the UI state:

```kotlin
data class NewJournalEntryUiState(
    // ... existing fields

    // Vocabulary suggestions
    val vocabularySuggestions: List<VocabularySuggestion> = emptyList(),
    val showVocabularySuggestions: Boolean = false,

    // Vocabulary celebrations
    val vocabularyCelebrations: List<VocabularyCelebration> = emptyList(),
    val showVocabularyCelebrations: Boolean = false,

    // For tracking detected words
    val detectedWordCount: Int = 0
)
```

## Step 3: Add Content Analysis with Debounce

In the ViewModel's init block or when content changes:

```kotlin
private val contentFlow = MutableStateFlow("")

init {
    // ... existing init code

    // Analyze content for vocabulary suggestions with debounce
    viewModelScope.launch {
        contentFlow
            .debounce(500) // Wait 500ms after user stops typing
            .collectLatest { content ->
                if (content.length >= 100) {
                    analyzeContentForSuggestions(content)
                }
            }
    }
}

fun updateContent(content: String) {
    _uiState.update { it.copy(content = content) }
    contentFlow.value = content
}

private suspend fun analyzeContentForSuggestions(content: String) {
    try {
        val suggestions = suggestionEngine.getSuggestions(
            content = content,
            userId = "local" // TODO: Get from auth service
        )

        _uiState.update {
            it.copy(
                vocabularySuggestions = suggestions,
                showVocabularySuggestions = suggestions.isNotEmpty()
            )
        }
    } catch (e: Exception) {
        // Log error but don't interrupt user
        Log.e("VocabularySuggestions", "Failed to get suggestions", e)
    }
}
```

## Step 4: Add Word Insertion Helper

Add a method to insert suggested words into content:

```kotlin
fun insertWordAtCursor(word: String) {
    val currentContent = _uiState.value.content
    val newContent = "$currentContent $word "
    _uiState.update { it.copy(content = newContent) }
}

fun dismissSuggestions() {
    _uiState.update {
        it.copy(
            showVocabularySuggestions = false,
            vocabularySuggestions = emptyList()
        )
    }
}
```

## Step 5: Update Save Entry Method

Detect and celebrate vocabulary usage when saving:

```kotlin
fun saveEntry() {
    viewModelScope.launch {
        try {
            _uiState.update { it.copy(isSaving = true) }

            // ... existing save logic to create journal entry
            val savedEntryId = journalDao.insertEntry(journalEntry)

            // Detect vocabulary usage
            val learnedWords = vocabularyDao.getLearnedWords().first()
            if (learnedWords.isNotEmpty()) {
                val wordUsages = vocabularyDetector.detectLearnedWords(
                    content = _uiState.value.content,
                    learnedWords = learnedWords
                )

                if (wordUsages.isNotEmpty()) {
                    // Process celebrations and award points
                    val celebrations = celebrationService.processWordUsages(
                        journalEntryId = savedEntryId,
                        wordUsages = wordUsages,
                        userId = "local" // TODO: Get from auth service
                    )

                    // Award skill points for each celebration
                    celebrations.forEach { celebration ->
                        if (celebration.bonusPoints > 0) {
                            // Award discipline points using your existing skill system
                            skillService.awardDisciplineXp(celebration.bonusPoints)

                            // Mark points as claimed
                            celebrationService.claimBonusPoints(
                                celebration.usageId,
                                celebration.bonusPoints
                            )
                        }
                    }

                    _uiState.update {
                        it.copy(
                            vocabularyCelebrations = celebrations,
                            showVocabularyCelebrations = celebrations.isNotEmpty(),
                            detectedWordCount = wordUsages.size
                        )
                    }
                }
            }

            _uiState.update {
                it.copy(
                    isSaving = false,
                    isSaved = true
                )
            }

        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isSaving = false,
                    error = "Failed to save entry: ${e.message}"
                )
            }
        }
    }
}
```

## Step 6: Update UI - Add Suggestion Section

In NewJournalEntryScreen.kt, add the vocabulary suggestion section after the content input field:

```kotlin
@Composable
fun NewJournalEntryScreen(
    onNavigateBack: () -> Unit,
    onEntrySaved: () -> Unit,
    viewModel: NewJournalEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        // ... existing scaffold code
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // ... existing UI elements (title, templates, mood, content)

            // ADD VOCABULARY SUGGESTIONS HERE
            // (After content input field, before media attachments)
            VocabularySuggestionSection(
                suggestions = uiState.vocabularySuggestions,
                onSuggestionClick = { word ->
                    viewModel.insertWordAtCursor(word.word)
                    viewModel.dismissSuggestions()
                },
                onDismiss = { viewModel.dismissSuggestions() }
            )

            // ... rest of existing UI (media, voice, etc.)
        }
    }
}
```

## Step 7: Update Session Result Card

Enhance the session result to show vocabulary achievements:

```kotlin
// In SessionResultCard or create a new VocabularySessionResult component

@Composable
fun EnhancedSessionResultCard(
    sessionResult: SessionResult,
    vocabularyCelebrations: List<VocabularyCelebration>,
    onDismiss: () -> Unit
) {
    // ... existing session result UI

    // Add vocabulary section
    if (vocabularyCelebrations.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Vocabulary Growth",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            vocabularyCelebrations.forEach { celebration ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = "Used \"${celebration.word.word}\" (+${celebration.bonusPoints} Discipline)",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
```

## Step 8: Optional - Add Live Celebration Badges

For real-time feedback (optional), add celebration badges that appear while typing:

```kotlin
// In NewJournalEntryScreen, at the top level

LaunchedEffect(uiState.vocabularyCelebrations) {
    if (uiState.vocabularyCelebrations.isNotEmpty()) {
        // Show celebration for 3 seconds
        delay(3000)
        viewModel.dismissCelebrations()
    }
}

// Add to UI at top of screen
Box(modifier = Modifier.fillMaxWidth()) {
    uiState.vocabularyCelebrations.firstOrNull()?.let { celebration ->
        VocabularyCelebrationBadge(
            word = celebration.word.word,
            bonusPoints = celebration.bonusPoints,
            onDismiss = { viewModel.dismissCelebrations() },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
```

## Step 9: Testing Checklist

Before deploying, test the following scenarios:

- [ ] Suggestions appear after typing 100+ characters
- [ ] Suggestions are relevant to content topic
- [ ] Clicking suggestion inserts word into content
- [ ] Dismissing suggestions works correctly
- [ ] Word detection works for different forms (run/running/ran)
- [ ] Celebrations appear after saving entry
- [ ] Bonus points are awarded correctly
- [ ] Daily caps prevent point farming
- [ ] Recently used words don't appear in suggestions
- [ ] Database migration completes successfully
- [ ] Performance is smooth with debouncing

## Step 10: Performance Optimization

Add these optimizations if needed:

```kotlin
// Cache learned words to avoid repeated queries
private val learnedWordsCache = MutableStateFlow<List<VocabularyEntity>>(emptyList())
private var lastCacheUpdate = 0L

suspend fun getLearnedWords(): List<VocabularyEntity> {
    val now = System.currentTimeMillis()
    if (now - lastCacheUpdate > 5 * 60 * 1000) { // 5 minutes
        learnedWordsCache.value = vocabularyDao.getLearnedWords().first()
        lastCacheUpdate = now
    }
    return learnedWordsCache.value
}

// Limit suggestion updates
private var lastSuggestionUpdate = 0L

private suspend fun analyzeContentForSuggestions(content: String) {
    val now = System.currentTimeMillis()
    if (now - lastSuggestionUpdate < 1000) return // Max once per second
    lastSuggestionUpdate = now

    // ... rest of analysis
}
```

## Common Issues and Solutions

### Issue: Suggestions not appearing
**Solution:** Check that:
- Content length is >= 100 characters
- User has learned words in vocabulary
- Debounce delay has elapsed (500ms)

### Issue: Wrong word forms detected
**Solution:**
- Add irregular verb forms to VocabularyDetectorImpl
- Check word boundary regex is correct

### Issue: Points not awarded
**Solution:** Ensure:
- celebrationService.claimBonusPoints() is called
- Skill service integration is correct
- Database transaction completes

### Issue: Performance lag while typing
**Solution:**
- Increase debounce delay
- Cache learned words
- Limit suggestion frequency

## Additional Features to Consider

1. **Word Usage History**
   - Show where words were used in past entries
   - Timeline of vocabulary application

2. **Vocabulary Insights**
   - Weekly vocabulary report
   - Most used words
   - Vocabulary diversity score

3. **Smart Prompts**
   - Suggest journal prompts that use learned words
   - "Use these 3 words in today's entry" challenge

4. **Vocabulary Sharing**
   - Share vocabulary achievements
   - Export vocabulary growth chart

## Resources

- **Main Documentation:** VOCABULARY_IN_CONTEXT_SYSTEM.md
- **Database Schema:** ProdyDatabase.kt (Migration 8->9)
- **UI Components:** ui/components/VocabularySuggestionChip.kt
- **Domain Logic:** domain/vocabulary/*

## Support

For questions or issues:
1. Check VOCABULARY_IN_CONTEXT_SYSTEM.md for architecture details
2. Review test cases for expected behavior
3. Check database logs for migration issues
4. Profile app performance if experiencing lag

---

**Integration Version:** 1.0
**Last Updated:** 2026-01-10
**Tested On:** Database Version 9
