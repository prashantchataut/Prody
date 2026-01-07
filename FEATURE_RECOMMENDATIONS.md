# Feature Enhancement Recommendations for Prody

## Critical Fixes (Priority 1 - Must Fix)

### 1. AI API Key Configuration
**Issue**: The Gemini AI mentor doesn't work because `local.properties` doesn't exist.
**Solution**:
- Create `local.properties` with your Gemini API key: `AI_API_KEY=your_key_here`
- OR use OpenRouter as an alternative provider
- The fallback to local BuddhaWisdom works, but users miss out on personalized AI responses

### 2. Database Migration Strategy
**Issue**: Uses `fallbackToDestructiveMigration()` which wipes ALL user data on schema changes.
**Solution**: Implement proper Room migrations for each version change to preserve user data.

---

## High-Impact Feature Recommendations (Priority 2)

### 1. Offline-First AI Caching
**Impact**: High | **Effort**: Medium
- Cache AI responses locally with timestamps
- Implement intelligent prefetching of common wisdom responses
- Show "Last generated" timestamps when using cached content
- Reduces API costs and improves reliability

### 2. Widget Support for Home Screen
**Impact**: High | **Effort**: Medium
- Daily word widget with quick "Mark as Learned" action
- Streak counter widget with motivational message
- Quick journal entry widget (opens app to new entry)
- Increases daily engagement without opening the app

### 3. Push Notifications for Engagement
**Impact**: High | **Effort**: Low
- Streak reminder at customizable time (default: 8 PM)
- "Future letter arrived!" notification when scheduled messages unlock
- Weekly progress summary notification
- Achievement unlock celebrations

### 4. Data Export/Import Feature
**Impact**: Medium | **Effort**: Low
- Export journal entries as PDF or markdown
- Export vocabulary progress as CSV
- Cloud backup to Google Drive
- Cross-device sync preparation

### 5. Spaced Repetition Optimization
**Impact**: Medium | **Effort**: Medium
- Current SM-2 implementation is solid, but add visual review schedule
- Show "Next review in X days" on vocabulary cards
- Add "Review Queue" widget showing due words count
- Weekly learning analytics graph

---

## Engagement Feature Recommendations (Priority 3)

### 6. Social Features Enhancement
**Impact**: Medium | **Effort**: High
- Anonymous journal sharing with community (opt-in)
- "Send encouragement" to streak leaders
- Weekly community challenges with collective goals
- Share achievements to social media

### 7. Personalization Deep Dive
**Impact**: Medium | **Effort**: Medium
- Learning style quiz (visual/auditory/reading preferences)
- Custom vocabulary categories (user-created)
- Mood-based content recommendations
- Time-of-day optimized content delivery

### 8. Progress Visualization
**Impact**: Medium | **Effort**: Low
- Calendar heatmap for activity (like GitHub contributions)
- Mood trend line graph over time
- Word mastery progression chart
- Monthly/yearly retrospectives

### 9. Voice Features
**Impact**: Medium | **Effort**: Medium
- Voice-to-text journal entries (already partially implemented)
- Audio pronunciation for all vocabulary
- Buddha wisdom read aloud (TTS is ready)
- Voice-guided meditation timer

### 10. Accessibility Improvements
**Impact**: Medium | **Effort**: Low
- Screen reader optimization (partially done, needs audit)
- High contrast theme option
- Larger text scaling support
- Reduced motion option for animations

---

## Technical Debt Recommendations

### Database Performance
- **Done**: Added indices to 5 entities (VocabularyEntity, QuoteEntity, ProverbEntity, IdiomEntity, PhraseEntity)
- **Done**: Added foreign key constraints to challenge entities
- **Done**: Added @Transaction annotations for atomic operations
- **Pending**: Consider adding indices to JournalEntryEntity for `createdAt` and `mood` columns

### ViewModel Memory Leaks
- **Done**: Fixed 4 ViewModels with parallel flow observer issues
  - NewJournalEntryViewModel
  - HomeViewModel
  - StatsViewModel
  - (ProfileViewModel already used combine correctly)

### Code Cleanup
- **Done**: Removed 6 unused files (BiometricManager, PrivacyLockManager, ShareProfileUtil, ChallengeRepository, UserRepository, QuoteRepository)

---

## Implementation Priority Order

1. **Week 1**: AI API key fix, Push notifications setup
2. **Week 2**: Widget support, Data export
3. **Week 3**: Progress visualization, Accessibility audit
4. **Week 4**: Social features MVP, Voice improvements
5. **Ongoing**: Spaced repetition optimization, Personalization

---

## Metrics to Track

- Daily Active Users (DAU)
- Streak completion rate
- Journal entries per user per week
- Words learned retention rate (7-day, 30-day)
- AI response satisfaction (add thumbs up/down)
- Feature usage heatmap
