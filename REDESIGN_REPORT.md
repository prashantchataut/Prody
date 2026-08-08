# Kairos Redesign Report — focused self-help, infinite personalized content

Date: 2026-08-08
Branch: `arena/019fe05a-kairos`

## 1. Critical audit (what was actually wrong)

I read the product brief (PRODUCT.md, DESIGN.md) and then the code. The brief is good;
the codebase is a Prody experiment farm wearing a Kairos coat. Verdicts:

| Claim in the brief | Reality found in code |
|---|---|
| "Focused over crowded" | 40 routes in the navigation graph; the legacy `HomeScreen` dashboard (with an "Explore" section offering Meditation, Challenges, Missions, Learning, Deep Dive, Vocabulary, Quick Note, Daily Ritual) still existed in the tree. |
| "Useful without AI" | AI paths existed (`getAiWordOfTheDay`) but were dead code — called by nobody. |
| "Trustworthy over theatrical" | A "Buddha" AI personality, Witness Mode ("THE VAULT"), Haven therapeutic chat, XP/rarity/casino chrome, emoji-as-icon in 27 files, `MagicalEffects`/`VoidEffect`/`PremiumEffects` ambient loops, Poppins branding in 18 files. |
| "Explainable recommendations" | Two competing engines: a good deterministic ranker (used by the daily plan) and a legacy `ContentRecommendationEngine` using `Math.random()` with a "recently shown" tracker that returned `emptySet()` — i.e., fake freshness. |
| "Spaced repetition" | A solid SM-2 + Leitner engine existed — but the only "review" screen was a stats page. There was no actual learning loop (no flashcards, no quiz, no grading). |
| "Unlimited, personalized content" | 47 words and 55 quotes total. A user exhausts the entire catalog in ~2 months of daily use. No generation, no families, no top-up. |

The deepest product flaw: **the app had a learning system in the data layer and a
dashboard in the UI layer.** The user never got to experience the SRS engine because
nothing surfaced it.

## 2. Decisions (confirmed with the user)

1. **Hybrid learning system** — flashcards with self-grading, then a definition quiz for weak cards, then a summary (the "vocabulary app" pattern).
2. **Full purge** — only Today / Learn (vocabulary) / Reflect (journal) / Library (quotes) + Profile/Settings remain navigable.
3. **Infinite content = offline-first + optional AI** — expanded curated catalogs, real derived word families, idempotent expansion for existing installs, and an optional Gemini top-up for new words.
4. **Self-help core = the trio** — vocabulary, quotes, journaling. No new features.

## 3. What was removed

- **Navigation purge**: 25 routes removed from the graph — Haven (home/chat/exercise), Missions, Challenges, Collaborative (home/compose/sent/received), Learning Paths (home/path/lesson), Deep Dive (home/session), Weekly Digest, Future Messages (list/write/reply/time-capsule), Stats, Meditation, Global Search, Wisdom Collection, Micro Journal, Daily Ritual. 26 imports removed.
- **Legacy Home dashboard deleted** (`HomeScreen.kt`, `HomeDashboardComponents.kt`, `HomeViewModel.kt`) — the "Discover"/Explore surface and its 1000+ lines of dashboard cards, XP/streak fire, quick-action tiles.
- **Dead debug AI overlay deleted** (`AiProofModeDebugInfo.kt`) that referenced the deleted dashboard.
- **Gimmick copy fixed in primary surfaces**: ALL-CAPS eyebrows (`WORD`/`THOUGHT`/`VOCABULARY`/`JOURNAL`/`WORDS & IDEAS`) → sentence case, per DESIGN.md.

The secondary feature *source trees* remain in the repo but are unreachable from the UI —
deleting them wholesale would require removing their Room entities, DAOs, and DI modules,
which is a separate, riskier migration. That is tracked as next-step work.

## 4. What was built

### 4.1 Infinite, honest content
- `data/content/ExpandedVocabularyContent.kt` — 43 curated roots across 8 categories, each with verified derived families → **+117 vocabulary rows** (real English, difficulty-clamped, origin-tagged).
- `data/content/ExpandedQuoteContent.kt` — **+77 attributed quotes** with categories, tags, and reflection prompts.
- `domain/vocabulary/WordFamilyExpander.kt` — deterministic expansion of roots into genuine family members; unit-tested.
- `data/local/database/CatalogExpansionManager.kt` — idempotent, existence-checked expansion for existing installs (runs off the startup path; safe to re-run; no version markers to drift).
- `DatabaseSeeder` now seeds the expanded catalog on fresh installs, deduplicated by word/quote text.

### 4.2 Personalization algorithms
- `domain/recommendation/PersonalizationProfile.kt` — builds per-user category/source affinity from explicit feedback (saved, more-like-this, less-like-this, dismissed, too easy/hard) with exponential time-decay so taste changes; produces a difficulty delta and a confidence score. Unit-tested.
- `domain/recommendation/PersonalizedStudyQueue.kt` — explainable study-queue builder: due spaced-repetition reviews first (with overdue boosts), then fresh words matched to the profile, then a diversity fill; every card carries a human-readable reason. Unit-tested.

### 4.3 The learning system (the headline)
- `ui/screens/vocabulary/VocabularySessionScreen.kt` + `VocabularySessionViewModel.kt` — hybrid session:
  1. **Flashcards**: word → reveal (definition, example, synonyms) → self-grade **Again / Hard / Good / Easy** (mapped to the SM-2 quality scale and persisted through `processWordReview`).
  2. **Quiz**: weak cards are re-encoded as definition-matching multiple choice with A–D options.
  3. **Summary**: first-try recall, quiz accuracy, duration, and an honest note on how scheduling works.
- Queue composition: due reviews first, new words second, AI top-up when the queue is too small and a key is configured (offline-safe — a short queue, never an error).
- The Learn tab gained a glass **Practice session** launcher (due-count badge + "New words" action), and the list ViewModel now exposes the live due-review count.

### 4.4 Liquid glass polish
- The session card uses `KairosGlassSurface` (specular highlight, gradient border, soft shadow) for the floating card and compact controls, with matte reading surfaces for definitions — exactly the hierarchy DESIGN.md prescribes.
- Bottom navigation/rail already use the restrained glass capsule; no changes needed there.
- Today's "Word"/"Thought" moment labels and all primary eyebrows are now sentence case.

## 5. Verification

- `bash ./scripts/verify_kairos.sh` — **passes** (504 Kotlin files checked: package boundaries, ambiguous imports, R references, manifest components, achievement catalogue, DI duplicates).
- Unit tests added for all new pure algorithms (`WordFamilyExpanderTest`, `PersonalizationProfileTest`, `PersonalizedStudyQueueTest`).
- ⚠️ This sandbox has no JDK/Android SDK, so `./gradlew testDebugUnitTest` and `assembleDebug` could not be executed here. The new Kotlin was written to match existing patterns and checked by the static verifier, but a CI build is the required gate before shipping.

## 6. Honest next steps (not done, deliberately)

1. **Retire secondary source trees** — remove purged feature packages + their entities/DAOs/DI modules once the DB migration path is agreed (needs a compiled build + upgrade test).
2. **AI quote generation** — the daily-plan quote path is local-only; wiring optional Gemini quote generation (with schema validation like the word path) is the remaining "hybrid" half.
3. **On-device verification** — cold start, session flow, upgrade from a v17-v23 database, light/dark, large-font, tablet.
4. **Mastery → "learned" semantics** — decide whether a word becomes "learned" on first Good/Easy (current behavior in the session) or on SM-2 mastery; align Learn-tab filters.
5. **Recommended daily session size** — currently fixed at 10; make it adaptive to the user's historical session length.

## 7. Wave two — save/like model and interests setup (2026-08-08)

Follow-up requested by the user: replace the "learned" mechanic with explicit
saving/liking, recommend more of what the user likes, and give first-time users a
proper interests setup.

### Save/like is now the primary taste signal
- **Every surface records SAVED/UNSAVED**: practice-session cards (new heart button),
  Today's word and quote panels (save hearts, replacing "Mark learned"), the Learn
  list rows, the word detail screen (replacing the Wisdom Quest gimmick), and the
  Library quote hearts.
- New `ContentInteractionType.UNSAVED` with a negative weight in the
  personalization profile, so un-saving a word pulls recommendations away from
  that category.
- Grading a card only schedules the next review (SM-2); it no longer claims the
  word is "learned". The Learn tab filters are now All / New (never studied) /
  Saved (liked), and overview metrics count saves.
- `TodayProgressRepository` gained `setWordSaved` / `setQuoteSaved` so Today's
  hearts persist in the database.

### Recommendations follow likes
- The practice queue and the daily plan already score candidates with category
  affinity; because saves now feed `PersonalizationProfile` from every surface,
  liking a word or quote directly promotes more content from that theme.
- The daily plan now ranks the word against **word interests** and the quote
  against **quote interests** (separate preference sets), so a stoic-quote lover
  isn't force-fed business words.

### Interests setup
- Onboarding step 2 now collects: vocabulary pace, cards-per-session (3/5/10),
  **word interests** (8 categories), and **quote themes** (12 categories).
- New `Settings → Interests & setup` screen re-opens the same choices any time
  (`InterestsSetupScreen` + ViewModel, route `interests_setup`), so taste can
  change — this is what makes recommendations honest over time.
- New preferences: `preferred_word_categories` (falls back to the legacy wisdom
  categories for existing installs) and `practice_session_size`.

### Notes
- The legacy `VocabularyListScreen` and the Wisdom Quest block on the word detail
  screen were removed (dead UI after the purge).
- `bash scripts/verify_kairos.sh` passes (503 Kotlin files). A real Gradle build
  and on-device pass are still required before shipping.

## 8. Skills used (this task)

Installed from skills.sh / the open skills ecosystem into `.agents/skills/`:
- **find-skills** (vercel-labs/skills) — discovery of the skills ecosystem
- **frontend-design** (anthropics/skills) — design-system discipline for the liquid-glass surface
- **improve-codebase-architecture** (mattpocock/skills) — module/shallow-interface framing for the purge
- **grill-me**, **grill-with-docs** (mattpocock/skills) — critical review posture
- **tdd** (mattpocock/skills) — red-green discipline for the new algorithms
- **handoff** (mattpocock/skills) — handoff/verification framing

Repo-bundled skills applied:
- **ui-ux-pro-max**, **mobile-android-design** (Compose/M3/accessibility rules, liquid-glass hierarchy)
- **kotlin-specialist** (idiomatic Kotlin, coroutines/Flow state)
- **test-master**, **code-reviewer** (test seams and self-review checklist)
- **clutter-management**, **debugging-wizard**, **incremental-implementation**, **source-driven-development**, **error-handling**, **secure-code-guardian**, **database-optimizer**, **android-jetpack-compose**, **android-kotlin-development**, **karpathy-guidelines**
