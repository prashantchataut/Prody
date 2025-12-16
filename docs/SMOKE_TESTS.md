# Smoke Tests - Navigation Flow Verification

## Executive Summary

This document outlines the expected navigation flows and verification steps for all major features in the Prody application.

---

## Navigation Structure

### Bottom Navigation Items
| Tab | Route | Screen | ViewModel |
|-----|-------|--------|-----------|
| Home | `home` | HomeScreen | HomeViewModel |
| Journal | `journal` | JournalListScreen | JournalViewModel |
| Stats | `stats` | StatsScreen | StatsViewModel |
| Profile | `profile` | ProfileScreen | ProfileViewModel |

---

## Flow 1: Onboarding to Home

### Route: `onboarding` -> `home`

**Steps:**
1. Fresh install / clear app data
2. App launches to OnboardingScreen
3. Swipe through 5 onboarding pages
4. Tap "Get Started" on final page
5. Navigate to HomeScreen

**Expected Behavior:**
- Smooth page transitions with animations
- Progress indicator shows current page
- "Get Started" button appears on page 5
- Home screen loads with daily content

**Files Involved:**
- `/ui/screens/onboarding/OnboardingScreen.kt`
- `/ui/screens/home/HomeScreen.kt`
- `/ui/navigation/ProdyNavigation.kt`

---

## Flow 2: Home to Profile/Stats/Leaderboard

### Route: Bottom Navigation

**Steps:**
1. From Home, tap "Profile" in bottom nav
2. Profile screen loads with user stats
3. Tap "Stats" in bottom nav
4. Stats screen shows charts and leaderboard
5. Scroll down to leaderboard section

**Expected Behavior:**
- Staggered entry animations on each screen
- Stats animate counting up
- Leaderboard shows top 3 podium
- Pull-to-refresh updates data

**Files Involved:**
- `/ui/screens/profile/ProfileScreen.kt`
- `/ui/screens/stats/StatsScreen.kt`
- `/ui/navigation/BottomNavItem.kt`

---

## Flow 3: Journal List + New Entry Save

### Route: `journal` -> `journal/new` -> `journal`

**Steps:**
1. Navigate to Journal tab
2. Tap FAB (floating action button) or "New Entry"
3. Select mood from selector
4. Write journal content
5. Tap Save
6. Return to journal list

**Expected Behavior:**
- Mood selector displays all mood options
- Text input accepts multi-line entry
- Buddha prompt appears (if AI enabled)
- Save persists to Room database
- New entry appears in list with mood indicator

**Files Involved:**
- `/ui/screens/journal/JournalScreen.kt`
- `/ui/screens/journal/NewJournalEntryScreen.kt`
- `/data/local/dao/JournalDao.kt`

---

## Flow 4: Vocabulary + Flashcards

### Route: `vocabulary` -> `vocabulary/{wordId}` -> Flashcard

**Steps:**
1. From Home, navigate to Vocabulary section
2. View vocabulary list
3. Tap on a word to see detail
4. Start flashcard session
5. Review cards (swipe or tap)
6. Complete session

**Expected Behavior:**
- Word list shows difficulty stars
- Detail shows definition, pronunciation, examples
- Flashcard flips on tap
- Progress tracked per word
- Session stats displayed at end

**Files Involved:**
- `/ui/screens/vocabulary/VocabularyListScreen.kt`
- `/ui/screens/vocabulary/VocabularyDetailScreen.kt`
- `/ui/screens/flashcard/FlashcardScreen.kt`

---

## Flow 5: Write to Future + Inbox

### Route: `future_message` -> `future_message/write` -> Inbox

**Steps:**
1. Navigate to Future Messages (from Home tile)
2. View "Pending" tab for scheduled messages
3. Tap "Write New Message"
4. Set delivery date (future)
5. Write message content
6. Save message
7. Check "Delivered" tab for past messages

**Expected Behavior:**
- Pending messages show countdown
- Date picker allows future dates only
- Message saves with delivery date
- Delivered messages reveal on/after date
- Timeline visualization shows history

**Files Involved:**
- `/ui/screens/futuremessage/FutureMessageScreen.kt`
- `/ui/screens/futuremessage/WriteMessageScreen.kt`
- `/data/local/dao/FutureMessageDao.kt`

---

## Flow 6: Settings Configuration

### Route: `profile` -> `settings`

**Steps:**
1. Navigate to Profile
2. Tap Settings icon
3. Toggle theme (light/dark/system)
4. Toggle notifications
5. Configure Buddha AI settings
6. Return to Profile

**Expected Behavior:**
- Theme changes apply immediately
- Notification toggles persist
- Buddha AI feature toggles persist
- All preferences save to DataStore

**Files Involved:**
- `/ui/screens/profile/SettingsScreen.kt`
- `/data/local/preferences/PreferencesManager.kt`

---

## Flow 7: Quotes/Wisdom Content

### Route: `quotes` (via Home tile)

**Steps:**
1. From Home, tap Quotes tile
2. View Quotes tab
3. Expand a quote for AI explanation
4. Switch to Proverbs tab
5. Switch to Idioms tab
6. Switch to Phrases tab

**Expected Behavior:**
- Tabs switch content smoothly
- Quote expansion animates
- AI explanation loads (if enabled)
- Each content type displays correctly

**Files Involved:**
- `/ui/screens/quotes/QuotesScreen.kt`
- `/data/ai/BuddhaAiService.kt`

---

## Flow 8: Meditation Timer

### Route: `meditation` (via Home tile)

**Steps:**
1. From Home, tap Meditation tile
2. Select duration (5/10/15/20/30 min)
3. Start timer
4. Observe breathing animation
5. Pause/resume if needed
6. Complete session

**Expected Behavior:**
- Duration selector works
- Timer counts down
- Breathing animation syncs with timer
- Session stats saved
- Wisdom card shown at end

**Files Involved:**
- `/ui/screens/meditation/MeditationTimerScreen.kt`

---

## Flow 9: Challenges

### Route: `challenges` (via Home tile)

**Steps:**
1. From Home, tap Challenges tile
2. View featured challenge
3. Browse active challenges
4. View challenge details (bottom sheet)
5. Check leaderboard within challenge

**Expected Behavior:**
- Featured challenge displays with shimmer
- Active challenges show progress
- Detail sheet shows milestones
- Leaderboard shows participants

**Files Involved:**
- `/ui/screens/challenges/ChallengesScreen.kt`

---

## Accessibility Smoke Test

### Checklist:
- [ ] All interactive elements have 48dp minimum touch target
- [ ] Content descriptions present on icons
- [ ] Screen reader announces content correctly
- [ ] Contrast ratios meet WCAG AA
- [ ] Focus navigation works in order

---

## Dark Mode Smoke Test

### Verify on Each Screen:
- [ ] Background colors are dark (not just inverted)
- [ ] Text remains readable
- [ ] Icons have appropriate tint
- [ ] Cards have subtle elevation
- [ ] No harsh white elements

---

## Performance Smoke Test

### Observe:
- [ ] Screen transitions are smooth (no dropped frames)
- [ ] List scrolling is smooth (60fps target)
- [ ] Animations don't stutter
- [ ] Memory usage stable during navigation
- [ ] No ANR warnings

---

## Conclusion

All navigation flows are defined in `/ui/navigation/ProdyNavigation.kt` with proper route definitions. The navigation structure supports:
- Type-safe route parameters
- Back stack management
- Deep linking capability
- Animation transitions

---

*Document Version: 1.0*
*Last Updated: December 2024*
