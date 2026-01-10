# Quiet Mode - User Flows

## Flow 1: Manual Activation

```
Home Screen
    |
    v
[Need Things Simple?] Toggle Card
    |
    | User taps
    v
Confirmation Dialog
"I need things simple right now"
    |
    |--- Hidden: Streaks, XP, achievements...
    |--- Kept: Journal, wisdom, messages...
    |
    +--[Yes, simplify]-------> Quiet Mode ENABLED
    |                              |
    |                              v
    |                          [Quiet Mode Active] Indicator appears
    |                              |
    |                              v
    |                          Gamification elements disappear
    |                          Colors become muted
    |                          UI simplifies
    |
    +--[I'm okay]-----------> Stays normal (records choice)
```

## Flow 2: Auto-Suggestion

```
User writes stressed journals
    |
    | Keywords: overwhelmed, exhausted, can't cope
    | Moods: anxious, sad, confused (3+)
    | Trend: declining intensity
    |
    v
Background Analysis
    |
    | Stress score >= 50
    | 2+ signals detected
    | 3+ days since last suggestion
    |
    v
Suggestion Dialog appears
"It looks like things have been heavy lately"
    |
    +--[Yes, simplify]-------> Quiet Mode ENABLED
    |                              |
    |                              v
    |                          Records suggestion shown
    |                          3-day cooldown starts
    |
    +--[I'm okay, thanks]----> Records suggestion shown
                               3-day cooldown starts
                               Doesn't ask again soon
```

## Flow 3: Using Quiet Mode

```
Quiet Mode Active
    |
    +---[Indicator at top]
    |       |
    |       +--[X button]----> Exit dialog
    |                              |
    |                              +--[Bring everything back]---> DISABLED
    |                              +--[Keep Quiet Mode]---------> Stays active
    |
    +---[Home Screen]
    |       |
    |       +---[Toggle card] --> Quick disable
    |       +---[Journal] -------> Full access
    |       +---[Wisdom] --------> Simplified view
    |       +---[No streaks]
    |       +---[No XP bar]
    |       +---[No leaderboard]
    |
    +---[Profile Screen]
    |       |
    |       +---[Basic info only]
    |       +---[No banner]
    |       +---[No badges]
    |       +---[Settings with toggle]
    |
    +---[Stats Screen]
    |       |
    |       +---[Simple stats only]
    |       +---[No detailed analytics]
    |
    +---[Journal Screen]
            |
            +---[Full journal features]
            +---[No AI suggestions]
            +---[Muted colors]
```

## Flow 4: 7-Day Check-In

```
Quiet Mode enabled for 7+ days
    |
    v
Check-In Dialog appears
"How are you feeling?"
    |
    +--[Bring everything back]
    |       |
    |       v
    |   Quiet Mode DISABLED
    |   All features return
    |   Regular theme restores
    |   Records check-in shown
    |
    +--[Keep it simple]
            |
            v
        Stays in Quiet Mode
        Records check-in shown
        Next check-in in 7 days
```

## Flow 5: Exit from Settings

```
Settings Screen
    |
    v
[Quiet Mode] Toggle (active)
    |
    | User taps
    v
Immediately toggles OFF
    |
    v
All features return
Theme reverts
User can always re-enable
```

## UI States Visual Guide

### Normal Mode
```
┌─────────────────────────────────┐
│  HOME                      🔔   │
│                                 │
│  ┌──────────────────┐          │
│  │ 🔥 5-day streak  │          │
│  │ ⚡ 250 XP        │          │
│  └──────────────────┘          │
│                                 │
│  ┌──────────────────┐          │
│  │ 🏆 Leaderboard   │          │
│  │ You're #15       │          │
│  └──────────────────┘          │
│                                 │
│  ┌──────────────────┐          │
│  │ ✍️ Journal        │          │
│  └──────────────────┘          │
│                                 │
│  ┌──────────────────┐          │
│  │ 💡 Daily Wisdom  │          │
│  └──────────────────┘          │
└─────────────────────────────────┘
```

### Quiet Mode Active
```
┌─────────────────────────────────┐
│  HOME                      🔔   │
│                                 │
│  ┌──────────────────────────┐  │
│  │ 🧘 Quiet Mode Active  [X]│  │
│  │ All your data is safe    │  │
│  └──────────────────────────┘  │
│                                 │
│  ┌──────────────────┐          │
│  │ ✍️ Journal        │          │
│  └──────────────────┘          │
│                                 │
│  ┌──────────────────┐          │
│  │ 💡 Daily Wisdom  │          │
│  └──────────────────┘          │
│                                 │
│  ┌──────────────────┐          │
│  │ 💌 Future Message│          │
│  └──────────────────┘          │
│                                 │
│  [No streaks, no XP,            │
│   no leaderboard,               │
│   no achievements,              │
│   cleaner interface]            │
└─────────────────────────────────┘
```

## Color Changes Visual

### Normal Mode Colors
```
Background: #F0F4F3 (light) / #0D2826 (dark)
Surface:    #FFFFFF (light) / #142E2B (dark)
Accent:     #36F97F (vibrant green)
Text:       #1A1A1A (light) / #FFFFFF (dark)
```

### Quiet Mode Colors
```
Background: #F5F7F6 (softer) / #0A1E1C (deeper)
Surface:    #FAFBFA (softer) / #0F2624 (deeper)
Accent:     #4FB87F (muted green)
Text:       #3A3A3A (softer) / #EBEDED (softer)
```

## Dialog Examples

### Suggestion Dialog
```
┌─────────────────────────────────────────┐
│              🧘                          │
│                                          │
│   It looks like things have been        │
│   heavy lately                          │
│                                          │
│   Would you like to simplify the app    │
│   for a bit?                            │
│                                          │
│   ┌───────────────────────────┐         │
│   │ Hidden temporarily:       │         │
│   │ • Streaks & XP           │         │
│   │ • Leaderboard            │         │
│   │ • Achievements           │         │
│   └───────────────────────────┘         │
│                                          │
│   ┌───────────────────────────┐         │
│   │ Still here for you:       │         │
│   │ • Your journal           │         │
│   │ • Daily wisdom           │         │
│   │ • AI support             │         │
│   └───────────────────────────┘         │
│                                          │
│   [     Yes, simplify     ]             │
│   [   I'm okay, thanks    ]             │
└─────────────────────────────────────────┘
```

### Check-In Dialog (7 days)
```
┌─────────────────────────────────────────┐
│              🧘                          │
│                                          │
│   How are you feeling?                  │
│                                          │
│   You've been in Quiet Mode for         │
│   7 days. Want to bring everything      │
│   back, or keep things simple a bit     │
│   longer?                                │
│                                          │
│   [ Bring everything back ]             │
│   [    Keep it simple     ]             │
└─────────────────────────────────────────┘
```

### Exit Dialog
```
┌─────────────────────────────────────────┐
│              🧘                          │
│                                          │
│   Exit Quiet Mode?                      │
│                                          │
│   This will bring back:                 │
│   • Streaks & XP                        │
│   • Leaderboard                         │
│   • Achievements                        │
│   • Celebrations                        │
│                                          │
│   You can always turn it back on.       │
│                                          │
│   [ Bring everything back ]             │
│   [   Keep Quiet Mode     ]             │
└─────────────────────────────────────────┘
```

## Timing Reference

### Auto-Suggestion Cooldown
```
Suggestion shown
     |
     v
[3 days pass]
     |
     v
Can suggest again
```

### Check-In Schedule
```
Quiet Mode enabled
     |
     v
[7 days pass]
     |
     v
Check-in shown
     |
     +--[Keep it simple]
     |       |
     |       v
     |   [7 more days]
     |       |
     |       v
     |   Another check-in
     |
     +--[Exit]
             |
             v
         Back to normal
```

## State Transitions

```
┌──────────┐
│  Normal  │
│   Mode   │
└────┬─────┘
     │
     ├──[User toggle]──────────┐
     │                         │
     ├──[Auto-suggest]─────────┤
     │                         │
     │                    ┌────▼────┐
     │                    │  Quiet  │
     │                    │   Mode  │
     │                    └────┬────┘
     │                         │
     │                         ├──[User toggle]
     │                         │
     │                         ├──[Exit from indicator]
     │                         │
     │                         ├──[Settings toggle]
     │                         │
     │                         ├──[7-day check-in: exit]
     │                         │
     └─────────────────────────┘
```

## Integration Points

### HomeScreen Integration
```kotlin
@Composable
fun HomeScreen(quietModeManager: QuietModeManager) {
    val isQuietMode by quietModeManager.isQuietModeEnabled().collectAsState(false)

    Column {
        // Show indicator when active
        AnimatedVisibility(isQuietMode) {
            QuietModeIndicator(onExit = { /*...*/ })
        }

        // Toggle card (always visible)
        QuietModeToggle(isQuietMode, onToggle = { /*...*/ })

        // Conditional sections
        if (!isQuietMode) {
            StreakSection()
            XPSection()
            LeaderboardSection()
        }

        // Always visible
        JournalSection()
        WisdomSection()
    }
}
```

## Error States

### No Journal Entries
```
shouldSuggestQuietMode()
    |
    v
No entries found
    |
    v
Returns: shouldSuggest = false
Reason: "Insufficient entries"
```

### Too Soon Since Last Suggestion
```
shouldSuggestQuietMode()
    |
    v
Only 1 day since last suggestion
    |
    v
Returns: shouldSuggest = false
Reason: "Too soon since last suggestion"
```

### Already in Quiet Mode
```
shouldSuggestQuietMode()
    |
    v
Quiet Mode already active
    |
    v
Returns: shouldSuggest = false
Reason: "Already in Quiet Mode"
```

---

## Key User Experience Principles

1. **Respect**: Never nag, always give choice
2. **Clarity**: Clear what changes, what stays
3. **Safety**: Emphasize data is safe
4. **Warmth**: Caring tone, not clinical
5. **Control**: Easy to enable, easy to disable
6. **Simplicity**: Minimal friction in all flows

**Remember**: Every interaction should make the user feel cared for, not judged.
