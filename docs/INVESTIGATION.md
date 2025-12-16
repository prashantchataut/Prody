# Prody Codebase Investigation Report

## Executive Summary

This document provides a comprehensive investigation of the Prody Android codebase, mapping its architecture, identifying issues, and cataloging features for the UI/UX renaissance.

---

## 1. Directory Map

```
/app/src/main/java/com/prody/prashant/
├── data/                          # Data layer
│   ├── ai/                        # AI services (Gemini, OpenRouter, Buddha)
│   │   ├── GeminiService.kt       # Gemini API wrapper
│   │   ├── OpenRouterService.kt   # OpenRouter API integration
│   │   ├── BuddhaAiService.kt     # High-level AI service with caching
│   │   └── BuddhaAiRepository.kt  # AI repository pattern
│   ├── cache/                     # Caching layer
│   │   └── AiCacheManager.kt      # TTL-based AI response caching
│   ├── local/                     # Local persistence
│   │   ├── database/              # Room database
│   │   │   └── ProdyDatabase.kt   # Main database (19 entities)
│   │   ├── dao/                   # Data Access Objects
│   │   ├── entity/                # Database entities
│   │   └── preferences/           # DataStore preferences
│   │       └── PreferencesManager.kt
│   └── repository/                # Repository implementations
│       ├── VocabularyRepositoryImpl.kt
│       └── JournalRepositoryImpl.kt
├── di/                            # Dependency injection (Hilt)
│   ├── AppModule.kt               # Singleton providers
│   └── RepositoryModule.kt        # Repository bindings
├── domain/                        # Business logic
│   ├── gamification/              # XP, levels, achievements
│   │   └── GamificationService.kt
│   ├── identity/                  # Banners, titles, ranks
│   │   └── ProdyBanners.kt
│   └── model/                     # Domain models
├── notification/                  # Push notifications
│   └── NotificationScheduler.kt
├── ui/                            # Presentation layer
│   ├── components/                # Reusable composables
│   │   ├── ProdyButton.kt         # Button variants (526 lines)
│   │   ├── ProdyCard.kt           # Card variants (592 lines, some unused)
│   │   ├── ProdyHeader.kt         # Header system (562 lines)
│   │   ├── EmptyState.kt          # Empty/loading/error states
│   │   ├── AnimationComponents.kt # Shimmer, animations
│   │   ├── BannerRenderer.kt      # Profile banner display
│   │   └── CompactBannerStrip.kt  # Compact banner view
│   ├── navigation/                # Navigation
│   │   ├── ProdyNavigation.kt     # NavHost, routes, transitions
│   │   └── BottomNavItem.kt       # Bottom nav items
│   ├── screens/                   # Screen composables
│   │   ├── home/                  # Home dashboard
│   │   ├── journal/               # Journal CRUD
│   │   ├── vocabulary/            # Vocabulary & flashcards
│   │   ├── quotes/                # Quote display
│   │   ├── profile/               # Profile & settings
│   │   ├── stats/                 # Statistics
│   │   ├── futuremessage/         # Future messages
│   │   ├── meditation/            # Meditation timer
│   │   ├── challenges/            # Community challenges
│   │   ├── onboarding/            # Onboarding flow
│   │   └── flashcard/             # Flashcard component
│   └── theme/                     # Design system
│       ├── Color.kt               # Color palette (490+ lines)
│       ├── Type.kt                # Typography (636 lines)
│       ├── Shape.kt               # Shape definitions
│       ├── Dimensions.kt          # Spacing & sizing
│       └── Theme.kt               # Material3 theme
├── util/                          # Utilities
│   └── ShareProfileUtil.kt        # Profile sharing
├── debug/                         # Debug utilities
│   └── CrashHandler.kt            # Crash handling
├── ProdyApplication.kt            # Application class (@HiltAndroidApp)
└── MainActivity.kt                # Entry point
```

---

## 2. Screen List (All Routes)

| Route | Screen | File | ViewModel |
|-------|--------|------|-----------|
| `onboarding` | OnboardingScreen | `/screens/onboarding/OnboardingScreen.kt` | OnboardingViewModel |
| `home` | HomeScreen | `/screens/home/HomeScreen.kt` | HomeViewModel |
| `journal` | JournalListScreen | `/screens/journal/JournalScreen.kt` | JournalViewModel |
| `journal/new` | NewJournalEntryScreen | `/screens/journal/NewJournalEntryScreen.kt` | NewJournalEntryViewModel |
| `journal/{entryId}` | JournalDetailScreen | `/screens/journal/JournalDetailScreen.kt` | JournalDetailViewModel |
| `future_message` | FutureMessageListScreen | `/screens/futuremessage/FutureMessageScreen.kt` | FutureMessageViewModel |
| `future_message/write` | WriteMessageScreen | `/screens/futuremessage/WriteMessageScreen.kt` | WriteMessageViewModel |
| `stats` | StatsScreen | `/screens/stats/StatsScreen.kt` | StatsViewModel |
| `profile` | ProfileScreen | `/screens/profile/ProfileScreen.kt` | ProfileViewModel |
| `settings` | SettingsScreen | `/screens/profile/SettingsScreen.kt` | SettingsViewModel |
| `vocabulary` | VocabularyListScreen | `/screens/vocabulary/VocabularyListScreen.kt` | VocabularyListViewModel |
| `vocabulary/{wordId}` | VocabularyDetailScreen | `/screens/vocabulary/VocabularyDetailScreen.kt` | VocabularyDetailViewModel |
| `quotes` | QuotesScreen | `/screens/quotes/QuotesScreen.kt` | QuotesViewModel |
| `meditation` | MeditationTimerScreen | `/screens/meditation/MeditationTimerScreen.kt` | MeditationTimerViewModel |
| `challenges` | ChallengesScreen | `/screens/challenges/ChallengesScreen.kt` | ChallengesViewModel |

**Bottom Navigation:** Home, Journal, Stats, Profile

---

## 3. Architecture Overview

### DI Framework: Hilt
- Application: `ProdyApplication.kt` (`@HiltAndroidApp`)
- Modules: `AppModule.kt`, `RepositoryModule.kt`
- Provides: Database, DAOs, Services, Managers, Repositories

### Persistence
- **Room Database**: 19 entities (journal, vocabulary, quotes, proverbs, idioms, phrases, users, challenges, achievements, streaks, leaderboard)
- **DataStore**: User preferences, theme, notifications, AI settings

### AI Integration
- **Gemini API**: Primary AI service for wisdom content
- **OpenRouter**: Alternative API provider
- **Buddha AI Service**: High-level wrapper with caching
- **Cache TTLs**: 1 hour to 7 days depending on content type

### Gamification System
- **Points**: Activity-based rewards (5-50 points per action)
- **Levels**: 10 levels based on total points
- **Streaks**: Daily activity tracking with milestones
- **Achievements**: 7 categories with rarity tiers
- **Titles**: Newcomer to Legend progression

---

## 4. What Looks Wrong (Per Screen)

### Home Screen (`HomeScreen.kt`)
- **Line 1781**: No flashcard navigation despite route existing
- Heavy animations in lazy list may cause jank
- Buddha thought card has complex nested boxes

### Profile Screen (`ProfileScreen.kt`)
- Excessive animations (glows, pulses on every element)
- 2400+ lines - should be decomposed
- Achievement filtering logic duplicated

### Stats Screen
- Needs verification - may have incomplete data visualization

### Onboarding
- Needs verification for completion flow

### Settings Screen
- Needs verification for all toggles functionality

---

## 5. What Is Unfunctional (Per Feature)

### Potentially Unfunctional
1. **AiDebugScreen**: Defined but not reachable via navigation
2. **Flashcard route**: Not in navigation graph (may be accessed via vocabulary)
3. **Share functionality**: Button exists but needs verification
4. **Bookmark functionality**: Button exists in Buddha card but no persistence visible

### Needs Verification
1. **Notification scheduling**: NotificationScheduler exists but runtime behavior unknown
2. **Leaderboard**: Entity exists but no visible UI integration
3. **Challenges**: Screen exists but backend integration unclear
4. **Vocabulary TTS**: TextToSpeechManager provided but usage unclear

---

## 6. What Appears Suppressed/Unreachable

### Unreachable Code
1. **AiDebugScreen** (`/screens/profile/AiDebugScreen.kt`)
   - No route defined in `ProdyNavigation.kt`
   - Contains valuable debug info (cache stats, latency)
   - Should be accessible in debug builds

### Unused Components
1. **ProdyCard.kt** variants:
   - `ProdyClickableCard()` - defined but unused
   - `ProdyElevatedCard()` - defined but unused
   - `ProdyGradientCard()` - defined but unused
   - `ProdyOutlinedCard()` - defined but unused
   - `ProdyFeaturedCard()` - defined but unused
   - `ProdyPremiumCard()` - defined but unused
   - `ProdyPremiumGradientCard()` - defined but unused
   - `ProdyNotificationCard()` - defined but unused
   - **Impact**: ~370 lines of well-documented dead code

2. **Typography styles** (Type.kt):
   - `QuoteTextStyle` - defined but not referenced in screens
   - `CaptionTextStyle` - redundant with `labelSmall`
   - `OverlineTextStyle` - Material 3 deprecated but defined

### Suppressed Features
1. **Dynamic colors**: Toggle exists but may not be fully implemented
2. **Haptic feedback**: Toggle exists, implementation unclear

---

## 7. Font Configuration

### Font Files Present
All required fonts exist in `/app/src/main/res/font/`:
- Poppins: 9 weights (thin to black)
- Playfair Display: 5 variants (regular, medium, semibold, bold, italic)

### Font Loading Strategy
- **Strategy**: `FontLoadingStrategy.Blocking`
- **Error handling**: `safeFont()` wrapper with try-catch
- **Fallback**: System fonts (SansSerif, Serif) if all weights fail
- **Logging**: Warnings for partial loads, errors for complete failure

### Potential Issues
- Blocking strategy may cause UI jank during scroll on low-end devices
- Consider async loading for non-critical weights

---

## 8. Design Tokens Location

| Token Type | File | Purpose |
|------------|------|---------|
| Colors | `/ui/theme/Color.kt` | 490+ color definitions |
| Typography | `/ui/theme/Type.kt` | Material3 + custom styles |
| Shapes | `/ui/theme/Shape.kt` | Corner radii, custom shapes |
| Dimensions | `/ui/theme/Dimensions.kt` | Spacing, sizes, elevations |
| Theme | `/ui/theme/Theme.kt` | Light/dark theme composition |

---

## 9. Gamification Logic Location

| Component | File | Lines |
|-----------|------|-------|
| Point system | `/domain/gamification/GamificationService.kt` | ~200 |
| Achievements | `/screens/profile/ProfileScreen.kt` | Inline definitions |
| Level calculation | Helper functions in screens | Duplicated |
| Streak tracking | `PreferencesManager.kt` + GamificationService | Split |
| Banners/Titles | `/domain/identity/ProdyBanners.kt` | ~100 |

### Point Values
- Journal entry: 50 points
- Word learned: 15 points
- Quote read: 5 points
- Proverb explored: 8 points
- Future letter sent: 50 points
- Future letter received: 30 points
- Daily check-in: 5 points
- Streak bonus: 2 points per day
- Review completed: 15 points
- Buddha conversation: 15 points
- **Daily cap**: 500 points

---

## 10. AI Services Location

| Service | File | Purpose |
|---------|------|---------|
| GeminiService | `/data/ai/GeminiService.kt` | Low-level Gemini API |
| OpenRouterService | `/data/ai/OpenRouterService.kt` | Alternative AI provider |
| BuddhaAiService | `/data/ai/BuddhaAiService.kt` | High-level wrapper |
| BuddhaAiRepository | `/data/ai/BuddhaAiRepository.kt` | Repository pattern |
| AiCacheManager | `/data/cache/AiCacheManager.kt` | Response caching |

### AI Features
- Daily wisdom generation
- Journal prompts (mood-specific)
- Quote explanations
- Vocabulary context
- Streak celebrations
- Weekly summaries
- Pattern analysis

---

## 11. Build Configuration Summary

### SDK Versions
- Compile SDK: 35
- Min SDK: 24
- Target SDK: 35

### Key Dependencies
- Compose BOM: 2024.11.00
- Room: 2.6.1
- Hilt: 2.52
- Gemini AI: 0.9.0
- Navigation Compose: Latest
- Coroutines: 1.9.0

### API Keys
- Configured via `local.properties`
- `AI_API_KEY` for Gemini
- `OPENROUTER_API_KEY` for OpenRouter

---

## 12. Recommendations Priority

### High Priority
1. Remove/document unused card components
2. Decompose large screen files (Profile: 2400+ lines)
3. Centralize level/points calculation (currently duplicated)

### Medium Priority
1. Add AiDebugScreen to navigation (debug builds only)
2. Review font loading strategy for performance
3. Consolidate color aliases to reduce confusion

### Low Priority
1. Remove unused typography styles
2. Document Color.kt organization
3. Add component usage examples

---

*Investigation completed: December 2024*
*Codebase version: 1.0.0*
