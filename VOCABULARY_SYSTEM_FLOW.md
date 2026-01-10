# Vocabulary in Context System - Flow Diagrams

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        Prody App                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────────┐         ┌──────────────────┐             │
│  │  UI Layer        │         │  Domain Layer    │             │
│  ├──────────────────┤         ├──────────────────┤             │
│  │ - ReviewScreen   │────────▶│ - Detector       │             │
│  │ - Suggestion     │         │ - Celebration    │             │
│  │   Chips          │         │ - Suggestion     │             │
│  │ - Celebration    │         │   Engine         │             │
│  │   Badges         │         └──────────────────┘             │
│  └──────────────────┘                   │                       │
│           │                              │                       │
│           │                              ▼                       │
│           │                   ┌──────────────────┐             │
│           │                   │  Data Layer      │             │
│           │                   ├──────────────────┤             │
│           └──────────────────▶│ - WordUsageDao   │             │
│                               │ - VocabularyDao  │             │
│                               │ - Entities       │             │
│                               └──────────────────┘             │
│                                        │                         │
│                                        ▼                         │
│                               ┌──────────────────┐             │
│                               │  Room Database   │             │
│                               │  (Version 10)    │             │
│                               └──────────────────┘             │
└─────────────────────────────────────────────────────────────────┘
```

## User Flow: Writing a Journal Entry

```
┌─────────────────────────────────────────────────────────────────┐
│ Step 1: User Opens New Journal Entry                            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 2: User Starts Typing (100+ characters)                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │  500ms Debounce │
                    └─────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 3: Content Analysis                                        │
│  - Detect topics (emotions, work, relationships, etc.)          │
│  - Score learned words for relevance                            │
│  - Filter by cooldown (not used in last 3 days)                 │
│  - Select top 3 suggestions                                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │  Suggestions    │
                    │  Available?     │
                    └─────────────────┘
                      │              │
                  Yes │              │ No
                      ▼              ▼
         ┌──────────────────┐   ┌────────────────┐
         │ Show Suggestion  │   │ Continue       │
         │ Chips            │   │ Writing        │
         └──────────────────┘   └────────────────┘
                      │
                      ▼
         ┌──────────────────┐
         │ User Clicks or   │
         │ Dismisses        │
         └──────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 4: User Completes Entry & Clicks Save                      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 5: Word Detection                                          │
│  - Get all learned words from database                          │
│  - Detect words in content (with word forms)                    │
│  - Extract sentences for context                                │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │  Words Found?   │
                    └─────────────────┘
                      │              │
                  Yes │              │ No
                      ▼              ▼
         ┌──────────────────┐   ┌────────────────┐
         │ Process          │   │ Save Entry     │
         │ Celebrations     │   │ & Exit         │
         └──────────────────┘   └────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 6: Celebration Processing                                  │
│  - Check daily caps (max 6 pts per word)                        │
│  - Create WordUsageEntity                                       │
│  - Update VocabularyLearningEntity                              │
│  - Award bonus Discipline points                                │
│  - Generate celebration message                                 │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 7: Show Celebrations                                       │
│  - Display celebration badges                                   │
│  - Show in session result card                                  │
│  - Update review screen data                                    │
└─────────────────────────────────────────────────────────────────┘
```

## Word Detection Process

```
Input: "Today I felt ephemeral, like a fleeting moment."
Learned Words: ["ephemeral", "resilient", "ambiguous"]

┌─────────────────────────────────────────────────────────────────┐
│ Step 1: Generate Word Forms                                     │
│  ephemeral → [ephemeral, ephemerals]                           │
│  resilient → [resilient, resilients, resiliently]              │
│  ambiguous → [ambiguous, ambiguously]                          │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 2: Create Word Boundary Regex                              │
│  \b(ephemeral|resilient|ambiguous|...)\b                       │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 3: Find Matches                                            │
│  Match found: "ephemeral" at position [14, 22]                  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 4: Extract Sentence                                        │
│  "Today I felt ephemeral, like a fleeting moment."             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Output: WordUsage                                               │
│  - word: VocabularyEntity(id=123, word="ephemeral", ...)       │
│  - usedIn: "Today I felt ephemeral, like a fleeting moment."   │
│  - position: 14..22                                             │
│  - matchedForm: "ephemeral"                                     │
└─────────────────────────────────────────────────────────────────┘
```

## Suggestion Relevance Scoring

```
Content: "I'm feeling stressed about work and deadlines..."

┌─────────────────────────────────────────────────────────────────┐
│ Step 1: Topic Detection                                         │
│  Keywords found:                                                │
│   - "feeling" → emotions (score: 0.2)                           │
│   - "stressed" → emotions (score: 0.2)                          │
│   - "work" → work (score: 0.3)                                  │
│   - "deadlines" → work (score: 0.3)                             │
│                                                                  │
│  Topics identified:                                             │
│   1. work (score: 0.6)                                          │
│   2. emotions (score: 0.4)                                      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 2: Score Learned Words                                     │
│                                                                  │
│  Word: "anxious" (adjective)                                    │
│   - Category: emotions ✓                                        │
│   - Definition matches: "feeling, worried" (2/5 keywords)       │
│   - Topic relevance: 0.4 * 0.4 = 0.16                          │
│   - POS boost: 1.2 (adjective)                                  │
│   - Final score: 0.16 * 1.2 = 0.19 ❌ (below 0.6 threshold)   │
│                                                                  │
│  Word: "overwhelmed" (adjective)                                │
│   - Category: emotions ✓                                        │
│   - Definition matches: "stressed, burdened" (3/5 keywords)     │
│   - Topic relevance: 0.6 * 0.4 = 0.24                          │
│   - POS boost: 1.2 (adjective)                                  │
│   - Final score: 0.24 * 1.2 = 0.29 ❌ (below threshold)       │
│                                                                  │
│  Word: "prioritize" (verb)                                      │
│   - Category: work ✓                                            │
│   - Definition matches: "task, deadline, manage" (4/6 keywords) │
│   - Topic relevance: 0.67 * 0.6 = 0.40                         │
│   - POS boost: 1.1 (verb)                                       │
│   - Category boost: 1.5                                         │
│   - Final score: 0.40 * 1.1 * 1.5 = 0.66 ✓                    │
│                                                                  │
│  Word: "efficacy" (noun)                                        │
│   - Category: work ✓                                            │
│   - Definition matches: "effectiveness, productivity" (3/6)      │
│   - Topic relevance: 0.50 * 0.6 = 0.30                         │
│   - POS boost: 0.9 (noun)                                       │
│   - Category boost: 1.5                                         │
│   - Final score: 0.30 * 0.9 * 1.5 = 0.41 ❌                   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 3: Filter & Sort                                           │
│  - Remove recently used words (last 3 days)                     │
│  - Sort by relevance score descending                           │
│  - Take top 3 suggestions                                       │
│                                                                  │
│  Result: ["prioritize" (0.66)]                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Database Relationships

```
┌─────────────────────┐
│  VocabularyEntity   │
│  ─────────────────  │
│  id (PK)            │◀─────────┐
│  word               │          │
│  definition         │          │
│  partOfSpeech       │          │
│  ...                │          │
└─────────────────────┘          │
         ▲                       │ FK: wordId
         │                       │
         │ FK: wordId            │
         │                       │
┌─────────────────────┐   ┌──────────────────────┐
│VocabularyLearning   │   │  WordUsageEntity     │
│Entity               │   │  ──────────────────  │
│─────────────────────│   │  id (PK)             │
│wordId (PK)          │   │  wordId (FK) ────────┘
│userId (PK)          │   │  journalEntryId (FK) ─────┐
│easeFactor           │   │  usedInSentence      │    │
│interval             │   │  matchedForm         │    │
│...                  │   │  detectedAt          │    │
│usedInContext ──NEW  │   │  celebrated          │    │
│lastUsedAt ─────NEW  │   │  bonusPointsAwarded  │    │
│timesUsed ──────NEW  │   │  ...                 │    │
└─────────────────────┘   └──────────────────────┘    │
                                                       │
                                                       │ FK: journalEntryId
                                                       │
                                        ┌──────────────▼────────┐
                                        │  JournalEntryEntity   │
                                        │  ───────────────────  │
                                        │  id (PK)              │
                                        │  userId               │
                                        │  content              │
                                        │  ...                  │
                                        └───────────────────────┘
```

## Celebration Point Flow

```
┌─────────────────────────────────────────────────────────────────┐
│ Word Used: "ephemeral" (learned 5 days ago)                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Check Daily Usage                                               │
│  Query: usages today for "ephemeral"                            │
│  Result: 0 usages                                               │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Calculate Points                                                │
│  Base points: 3                                                 │
│  Daily usage: 0                                                 │
│  Daily cap: 6 points max                                        │
│  Award: 3 points ✓                                              │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Create WordUsageEntity                                          │
│  bonusPointsAwarded: 3                                          │
│  pointsClaimed: false                                           │
│  celebrated: false                                              │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Update VocabularyLearningEntity                                 │
│  usedInContext: true                                            │
│  lastUsedAt: 1736524800000                                      │
│  timesUsed: 1 (increment)                                       │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Award Discipline XP                                             │
│  skillService.awardDisciplineXp(3)                              │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Mark Points Claimed                                             │
│  wordUsageDao.markPointsClaimed(usageId, 3)                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Generate Celebration                                            │
│  message: "You used 'ephemeral' in context! +3 Discipline"     │
│  bonusPoints: 3                                                 │
│  usageContext: "Today I felt ephemeral..."                     │
└─────────────────────────────────────────────────────────────────┘
```

## Review Screen Data Flow

```
User Opens Review Screen
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│ Load Data in Parallel                                           │
│                                                                  │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────┐ │
│  │ Words Learned    │  │ Words Used       │  │ Growth Data  │ │
│  │ This Week        │  │ In Context       │  │ (12 weeks)   │ │
│  └──────────────────┘  └──────────────────┘  └──────────────┘ │
│           │                      │                     │         │
└───────────┼──────────────────────┼─────────────────────┼────────┘
            │                      │                     │
            ▼                      ▼                     ▼
┌─────────────────────────────────────────────────────────────────┐
│ Calculate Statistics                                            │
│  - Total words learned: 47                                      │
│  - Words learned this week: 5                                   │
│  - Words used in context: 23                                    │
│  - Usage percentage: 49%                                        │
│  - Total bonus points: 69                                       │
└─────────────────────────────────────────────────────────────────┘
            │
            ▼
┌─────────────────────────────────────────────────────────────────┐
│ Categorize Words                                                │
│                                                                  │
│  Used in Context (23):                                          │
│   ✓ ephemeral, ✓ resilient, ✓ ambiguous, ...                  │
│                                                                  │
│  Not Yet Used (24):                                             │
│   ○ perspicacious, ○ ubiquitous, ○ sanguine, ...               │
│                                                                  │
│  Needs Practice (5):                                            │
│   ⚠ eloquent, ⚠ tenacious, ⚠ pragmatic, ...                   │
└─────────────────────────────────────────────────────────────────┘
            │
            ▼
┌─────────────────────────────────────────────────────────────────┐
│ Generate Growth Chart                                           │
│                                                                  │
│  Week 1: ■■■ learned, ■■ used                                  │
│  Week 2: ■■ learned, ■ used                                     │
│  Week 3: ■■■■ learned, ■■■ used                                │
│  ...                                                            │
│  Week 12: ■■■■■ learned, ■■■■ used                             │
└─────────────────────────────────────────────────────────────────┘
            │
            ▼
┌─────────────────────────────────────────────────────────────────┐
│ Display Review Screen                                           │
│  - Statistics cards                                             │
│  - Interactive word chips                                       │
│  - Growth visualization                                         │
│  - Navigation to word details                                   │
└─────────────────────────────────────────────────────────────────┘
```

## Component Dependencies

```
NewJournalEntryScreen
    │
    ├─ NewJournalEntryViewModel
    │   ├─ VocabularyDetector (detect words)
    │   ├─ VocabularySuggestionEngine (get suggestions)
    │   ├─ VocabularyCelebrationService (process celebrations)
    │   ├─ VocabularyDao (get learned words)
    │   └─ WordUsageDao (track usages)
    │
    └─ UI Components
        ├─ VocabularySuggestionSection
        │   └─ VocabularySuggestionChip
        ├─ VocabularyCelebrationBadge
        └─ InlineVocabularyCelebrations

VocabularyReviewScreen
    │
    ├─ VocabularyReviewViewModel
    │   ├─ VocabularyDao (get learned words)
    │   ├─ VocabularyLearningDao (get learning progress)
    │   ├─ WordUsageDao (get usage statistics)
    │   ├─ VocabularyCelebrationService (get unused words)
    │   └─ VocabularySuggestionEngine (get practice words)
    │
    └─ UI Components
        ├─ StatisticsSection
        ├─ WordsUsedSection
        ├─ WordsNotUsedSection
        ├─ WordsNeedingPracticeSection
        └─ GrowthChartSection
```

---

**Visual Flow Version**: 1.0
**Last Updated**: 2026-01-10
