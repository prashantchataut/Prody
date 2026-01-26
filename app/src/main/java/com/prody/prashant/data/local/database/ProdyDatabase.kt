package com.prody.prashant.data.local.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.prody.prashant.data.local.dao.*
import com.prody.prashant.data.local.entity.*
import com.prody.prashant.data.security.SecureDatabaseManager
import kotlinx.coroutines.runBlocking
import net.sqlcipher.database.SupportFactory

/**
 * Prody Room Database
 *
 * Central database for all Prody app data including:
 * - User profile and preferences
 * - Achievements and progress tracking
 * - Vocabulary and learning progress
 * - Quotes, proverbs, idioms, phrases
 * - Journal entries
 * - Future messages (letters to future self)
 * - Community challenges and leaderboards
 * - Profile loadouts and cosmetics (Identity system)
 * - Game skill system (Clarity, Discipline, Courage)
 * - Daily missions and weekly trials
 * - Enhanced streak system with Mindful Breaks
 *
 * Migration Strategy:
 * - For development: Uses fallbackToDestructiveMigration()
 * - For production: Implement proper migrations before release
 *
 * Schema Version History:
 * - Version 1: Initial schema with all core entities
 * - Version 2: Added userId fields for multi-user/Google Auth support
 *              Added sync metadata (syncStatus, lastSyncedAt, serverVersion)
 *              Added auth fields to UserProfileEntity (odUserId, email, photoUrl, etc.)
 *              Added indices for efficient userId queries
 *              Changed VocabularyLearningEntity to composite key (wordId, userId)
 * - Version 3: Added aiSummary field to JournalEntryEntity for short AI summaries
 * - Version 4: Added SeedEntity for Seed -> Bloom mechanic (daily wisdom application tracking)
 * - Version 5: Gamification 3.0 - Real game systems
 *              Added PlayerSkillsEntity (Clarity/Discipline/Courage skill XP + tokens)
 *              Added ProcessedRewardEntity (idempotency for anti-exploit)
 *              Added DailyMissionEntity (3 daily missions per day)
 *              Added WeeklyTrialEntity (weekly boss challenges)
 * - Version 6: Gamification 4.0 - Enhanced systems
 *              Added weekly XP tracking to PlayerSkillsEntity
 *              Added StreakDataEntity with Mindful Break support (2 freezes/month)
 *              Added DailyActivityEntity for detailed activity tracking
 *              Added MindfulBreakUsageEntity for freeze audit trail
 *              Enhanced SeedEntity with state field (planted/growing/bloomed)
 *              Added content matching helpers to SeedEntity
 * - Version 7: Time Capsule Reveal Enhancement
 *              Added MessageAnniversaryEntity for anniversary tracking
 *              Added isFavorite, replyJournalEntryId, readAt to FutureMessageEntity
 *              Enhanced Future Message experience with emotional reveal
 * - Version 8: Dual Streak System
 *              Added DualStreakEntity for Wisdom and Reflection streaks
 *              Two independent streaks with grace periods (one skip per 14 days)
 *              Wisdom Streak: Quick daily engagement (viewing wisdom)
 *              Reflection Streak: Deep engagement (journaling, reflection)
 * - Version 9: Vocabulary in Context
 *              Added WordUsageEntity for tracking vocabulary usage in journal entries
 *              Added usedInContext, lastUsedAt, timesUsed to VocabularyLearningEntity
 *              Celebrates when learned words are used in journal entries
 *              Awards bonus discipline points for vocabulary application
 * - Version 10: Monthly Growth Letters
 *              Added MonthlyLetterEntity for personalized monthly reflection letters
 * - Version 11: Yearly Wrapped
 *              Added YearlyWrappedEntity for end-of-year celebration and insights
 *              Comprehensive yearly summary with stats, mood journey, themes, growth areas
 *              Spotify Wrapped-style experience with shareable cards
 * - Version 12: Collaborative Future Messages
 *              Added CollaborativeMessageEntity for sending future messages to loved ones
 *              Added ReceivedCollaborativeMessageEntity for received collaborative messages
 *              Added MessageContactEntity for managing message recipients
 *              Added MessageOccasionEntity for occasion-based message reminders
 * - Version 13: Haven Personal Therapist
 *              Added HavenSessionEntity for therapeutic conversation sessions
 *              Added HavenExerciseEntity for guided therapeutic exercises
 *              Encrypted storage for sensitive mental health conversations
 *              Crisis detection and support resource integration
 * - Version 14: Personalized Learning Paths
 *              Added LearningPathEntity for structured learning journeys
 *              Added LearningLessonEntity for individual lessons
 *              Added LearningReflectionEntity for lesson reflections
 *              Added PathRecommendationEntity for AI-curated path suggestions
 *              Added PathProgressCheckpointEntity for milestone tracking
 *              Added LearningNoteEntity for user notes
 *              Added PathBadgeEntity for achievements
 * - Version 15: Deep Dive Days & Social Accountability Circles
 *              Added DeepDiveEntity for structured deep reflection sessions
 *              Added CircleEntity for accountability circles
 *              Added CircleMemberEntity for circle membership
 *              Added CircleUpdateEntity for activity feed updates
 *              Added CircleNudgeEntity for encouragement nudges
 *              Added CircleChallengeEntity for group challenges
 *              Added CirclePrivacySettingsEntity for privacy controls
 *              Added CircleNotificationEntity for circle notifications
 *              Added CircleMemberStatsCacheEntity for cached member stats
 * - Version 16: Gamification 5.0 - Enhanced Skills & Achievements
 *              Added perk tracking to PlayerSkillsEntity (unlockedPerkIds, perkFreezeTokens)
 *              Added MYTHIC rarity tier to achievements
 *              Added isHidden, isSecret, xpReward to AchievementEntity
 *              Updated skill system from 10 to 20 levels with perk unlocks
 * - Version 17: Soul Layer Intelligence System
 *              Added SurfacedMemoryEntity for memory surfacing tracking
 *              Added UserContextCacheEntity for user context caching
 *              Added NotificationHistoryEntity for notification intelligence
 *              Added DetectedPatternEntity for pattern detection
 *              Added BuddhaInteractionEntity for Buddha AI tracking
 *              Added HavenInsightEntity for Haven therapeutic insights
 *              Added TemporalContentHistoryEntity for temporal content tracking
 *              Added FirstWeekProgressEntity for first week journey tracking
 * - Version 18: Mirror Evolution - Haven Memory (THE VAULT) for Witness Mode
 *              Added HavenMemoryEntity for storing facts/truths from conversations
 *              Enables "Witness Mode" callback: Haven remembers and follows up
 *              Categories: exam, deadline, commitment, event, goal, health
 *              Status tracking: pending, followed_up, resolved, expired, dismissed
 * - Version 19: Mirror Evolution - Evidence Locker
 *              Added EvidenceEntity for THE LOCKER feature
 *              Replaces XP/Plants with concrete evidence of growth:
 *              - Receipts (Mirror contradictions)
 *              - Witness (Haven follow-ups completed)
 *              - Prophecy (Future Message predictions verified)
 *              - Breakthrough (Journal insights)
 *              - Streak (Milestone achievements)
 * - Version 20: Mirror Evolution - Sealed Pods (Future Message Prophecy)
 *              Added prediction, predictionVerified, predictionVerifiedAt to FutureMessageEntity
 *              Enables "Prophecy" evidence drops when predictions are verified
 */
@Database(
    entities = [
        JournalEntryEntity::class,
        FutureMessageEntity::class,
        VocabularyEntity::class,
        VocabularyLearningEntity::class,
        QuoteEntity::class,
        ProverbEntity::class,
        IdiomEntity::class,
        PhraseEntity::class,
        UserProfileEntity::class,
        AchievementEntity::class,
        UserStatsEntity::class,
        StreakHistoryEntity::class,
        LeaderboardEntryEntity::class,
        PeerInteractionEntity::class,
        MotivationalMessageEntity::class,
        ChallengeEntity::class,
        ChallengeMilestoneEntity::class,
        ChallengeParticipationEntity::class,
        ChallengeLeaderboardEntity::class,
        ProfileLoadoutEntity::class,
        PinnedBadgeEntity::class,
        SeedEntity::class,
        PlayerSkillsEntity::class,
        ProcessedRewardEntity::class,
        DailyMissionEntity::class,
        WeeklyTrialEntity::class,
        // Gamification 4.0 entities
        StreakDataEntity::class,
        DailyActivityEntity::class,
        MindfulBreakUsageEntity::class,
        // Time Capsule Enhancement entities
        MessageAnniversaryEntity::class,
        // Daily Engagement entities
        MicroEntryEntity::class,
        SavedWisdomEntity::class,
        WeeklyDigestEntity::class,
        DailyRitualEntity::class,
        FutureMessageReplyEntity::class,
        WritingCompanionSuggestionEntity::class,
        // Dual Streak System
        DualStreakEntity::class,
        // Vocabulary in Context entities
        WordUsageEntity::class,
        // Monthly Growth Letters
        MonthlyLetterEntity::class,
        // Personalized Learning Paths
        LearningPathEntity::class,
        LearningLessonEntity::class,
        LearningReflectionEntity::class,
        PathRecommendationEntity::class,
        PathProgressCheckpointEntity::class,
        LearningNoteEntity::class,
        PathBadgeEntity::class,
        // Yearly Wrapped
        YearlyWrappedEntity::class,
        // Collaborative Future Messages (Feature 13)
        CollaborativeMessageEntity::class,
        ReceivedCollaborativeMessageEntity::class,
        MessageContactEntity::class,
        MessageOccasionEntity::class,
        // Haven Personal Therapist (Feature 10)
        HavenSessionEntity::class,
        HavenExerciseEntity::class,
        // Deep Dive Days (Feature 8)
        DeepDiveEntity::class,
        // Social Accountability Circles (Feature 11)
        CircleEntity::class,
        CircleMemberEntity::class,
        CircleUpdateEntity::class,
        CircleNudgeEntity::class,
        CircleChallengeEntity::class,
        CirclePrivacySettingsEntity::class,
        CircleNotificationEntity::class,
        CircleMemberStatsCacheEntity::class,
        // Soul Layer Intelligence System
        SurfacedMemoryEntity::class,
        UserContextCacheEntity::class,
        NotificationHistoryEntity::class,
        DetectedPatternEntity::class,
        BuddhaInteractionEntity::class,
        HavenInsightEntity::class,
        TemporalContentHistoryEntity::class,
        FirstWeekProgressEntity::class,
        // Mirror Evolution: Haven Memory (THE VAULT) for Witness Mode
        HavenMemoryEntity::class,
        // Mirror Evolution: Evidence Locker
        EvidenceEntity::class
    ],
    version = 20,
    exportSchema = true // Enable for migration verification
)
abstract class ProdyDatabase : RoomDatabase() {

    abstract fun journalDao(): JournalDao
    abstract fun futureMessageDao(): FutureMessageDao
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun vocabularyLearningDao(): VocabularyLearningDao
    abstract fun quoteDao(): QuoteDao
    abstract fun proverbDao(): ProverbDao
    abstract fun idiomDao(): IdiomDao
    abstract fun phraseDao(): PhraseDao
    abstract fun userDao(): UserDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun profileLoadoutDao(): ProfileLoadoutDao
    abstract fun seedDao(): SeedDao
    abstract fun missionDao(): MissionDao

    // New DAOs for Daily Engagement Features
    abstract fun savedWisdomDao(): SavedWisdomDao
    abstract fun microEntryDao(): MicroEntryDao
    abstract fun weeklyDigestDao(): WeeklyDigestDao
    abstract fun dailyRitualDao(): DailyRitualDao
    abstract fun futureMessageReplyDao(): FutureMessageReplyDao
    abstract fun messageAnniversaryDao(): MessageAnniversaryDao

    // Dual Streak System DAO
    abstract fun dualStreakDao(): DualStreakDao

    // Vocabulary in Context DAO
    abstract fun wordUsageDao(): WordUsageDao

    // Monthly Growth Letters DAO
    abstract fun monthlyLetterDao(): MonthlyLetterDao

    // Haven Personal Therapist DAO
    abstract fun havenDao(): HavenDao

    // Yearly Wrapped DAO
    abstract fun yearlyWrappedDao(): YearlyWrappedDao

    // Collaborative Messages DAO
    abstract fun collaborativeMessageDao(): CollaborativeMessageDao

    // Personalized Learning Paths DAO
    abstract fun learningPathDao(): LearningPathDao

    // Deep Dive Days DAO
    abstract fun deepDiveDao(): DeepDiveDao

    // Social Accountability Circles DAO
    abstract fun socialDao(): SocialDao

    // Soul Layer Intelligence DAO
    abstract fun soulLayerDao(): SoulLayerDao

    // Haven Memory DAO (THE VAULT) for Witness Mode
    abstract fun havenMemoryDao(): HavenMemoryDao

    // Evidence DAO (THE LOCKER) for Evidence Drops
    abstract fun evidenceDao(): EvidenceDao

    companion object {
        private const val TAG = "ProdyDatabase"
        const val DATABASE_NAME = "prody_database"

        @Volatile
        private var INSTANCE: ProdyDatabase? = null

        val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS player_skills (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        userId TEXT UNIQUE,
                        clarityXp INTEGER NOT NULL DEFAULT 0,
                        disciplineXp INTEGER NOT NULL DEFAULT 0,
                        courageXp INTEGER NOT NULL DEFAULT 0,
                        dailyClarityXp INTEGER NOT NULL DEFAULT 0,
                        dailyDisciplineXp INTEGER NOT NULL DEFAULT 0,
                        dailyCourageXp INTEGER NOT NULL DEFAULT 0,
                        dailyResetDate INTEGER NOT NULL DEFAULT 0,
                        tokens INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL DEFAULT 0,
                        updatedAt INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS processed_rewards (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        rewardKey TEXT UNIQUE,
                        processedAt INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS daily_missions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        userId TEXT NOT NULL,
                        missionType TEXT NOT NULL,
                        targetCount INTEGER NOT NULL DEFAULT 0,
                        currentProgress INTEGER NOT NULL DEFAULT 0,
                        xpReward INTEGER NOT NULL DEFAULT 0,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        completedAt INTEGER,
                        createdAt INTEGER NOT NULL DEFAULT 0,
                        updatedAt INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS weekly_trials (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        userId TEXT NOT NULL,
                        trialType TEXT NOT NULL,
                        description TEXT NOT NULL,
                        targetCount INTEGER NOT NULL DEFAULT 0,
                        currentProgress INTEGER NOT NULL DEFAULT 0,
                        xpReward INTEGER NOT NULL DEFAULT 0,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        completedAt INTEGER,
                        startedAt INTEGER NOT NULL DEFAULT 0,
                        endsAt INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_daily_missions_user ON daily_missions(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_weekly_trials_user ON weekly_trials(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_processed_rewards_key ON processed_rewards(rewardKey)")
            }
        }

        /**
         * Migration 5 -> 6: Gamification 4.0 - Enhanced systems
         *
         * Changes:
         * - Add weekly XP tracking to player_skills
         * - Create streak_data table with Mindful Break support
         * - Create daily_activity table for detailed tracking
         * - Create mindful_break_usage table for audit trail
         * - Add new fields to daily_seeds (state, variations, keywords, etc.)
         */
        val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add weekly XP tracking columns to player_skills
                db.execSQL("ALTER TABLE player_skills ADD COLUMN weeklyClarityXp INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE player_skills ADD COLUMN weeklyDisciplineXp INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE player_skills ADD COLUMN weeklyCourageXp INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE player_skills ADD COLUMN weeklyResetDate INTEGER NOT NULL DEFAULT 0")

                // Create streak_data table with Mindful Break support
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS streak_data (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        userId TEXT NOT NULL,
                        currentStreak INTEGER NOT NULL DEFAULT 0,
                        longestStreak INTEGER NOT NULL DEFAULT 0,
                        lastActiveDate INTEGER NOT NULL DEFAULT 0,
                        freezesAvailable INTEGER NOT NULL DEFAULT 2,
                        freezesUsedThisMonth INTEGER NOT NULL DEFAULT 0,
                        lastFreezeResetMonth INTEGER NOT NULL DEFAULT 0,
                        totalDaysActive INTEGER NOT NULL DEFAULT 0,
                        streakBrokenCount INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL DEFAULT 0,
                        updatedAt INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_streak_data_user ON streak_data(userId)")

                // Create daily_activity table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS daily_activity (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        userId TEXT NOT NULL DEFAULT 'local',
                        date INTEGER NOT NULL,
                        hasJournalEntry INTEGER NOT NULL DEFAULT 0,
                        hasMicroEntry INTEGER NOT NULL DEFAULT 0,
                        hasBloom INTEGER NOT NULL DEFAULT 0,
                        hasFutureMessage INTEGER NOT NULL DEFAULT 0,
                        hasFlashcardSession INTEGER NOT NULL DEFAULT 0,
                        hasWisdomEngagement INTEGER NOT NULL DEFAULT 0,
                        clarityXpEarned INTEGER NOT NULL DEFAULT 0,
                        disciplineXpEarned INTEGER NOT NULL DEFAULT 0,
                        courageXpEarned INTEGER NOT NULL DEFAULT 0,
                        streakDayNumber INTEGER NOT NULL DEFAULT 0,
                        usedMindfulBreak INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_daily_activity_user_date ON daily_activity(userId, date)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_daily_activity_date ON daily_activity(date)")

                // Create mindful_break_usage table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS mindful_break_usage (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        userId TEXT NOT NULL DEFAULT 'local',
                        usedAt INTEGER NOT NULL DEFAULT 0,
                        preservedStreak INTEGER NOT NULL DEFAULT 0,
                        missedDate INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_mindful_break_user ON mindful_break_usage(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_mindful_break_time ON mindful_break_usage(usedAt)")

                // Add new fields to daily_seeds for enhanced Seed -> Bloom
                db.execSQL("ALTER TABLE daily_seeds ADD COLUMN state TEXT NOT NULL DEFAULT 'planted'")
                db.execSQL("ALTER TABLE daily_seeds ADD COLUMN rewardXp INTEGER NOT NULL DEFAULT 15")
                db.execSQL("ALTER TABLE daily_seeds ADD COLUMN rewardTokens INTEGER NOT NULL DEFAULT 5")
                db.execSQL("ALTER TABLE daily_seeds ADD COLUMN variations TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE daily_seeds ADD COLUMN keywords TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE daily_seeds ADD COLUMN keyPhrase TEXT")
            }
        }

        /**
         * Migration 6 -> 7: Time Capsule Reveal Enhancement
         *
         * Changes:
         * - Add isFavorite, replyJournalEntryId, readAt to future_messages
         * - Create message_anniversaries table for anniversary tracking
         */
        val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add new fields to future_messages
                db.execSQL("ALTER TABLE future_messages ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE future_messages ADD COLUMN replyJournalEntryId INTEGER")
                db.execSQL("ALTER TABLE future_messages ADD COLUMN readAt INTEGER")

                // Create message_anniversaries table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS message_anniversaries (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        originalMessageId INTEGER NOT NULL,
                        yearsAgo INTEGER NOT NULL,
                        originalContent TEXT NOT NULL,
                        category TEXT NOT NULL,
                        originalCreatedAt INTEGER NOT NULL,
                        anniversaryDate INTEGER NOT NULL,
                        notifiedAt INTEGER,
                        isRead INTEGER NOT NULL DEFAULT 0,
                        readAt INTEGER,
                        hasReflection INTEGER NOT NULL DEFAULT 0,
                        reflectionJournalId INTEGER,
                        createdAt INTEGER NOT NULL,
                        isDeleted INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(originalMessageId) REFERENCES future_messages(id) ON DELETE CASCADE
                    )
                """.trimIndent())

                // Create indices for message_anniversaries
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_message_anniversaries_user ON message_anniversaries(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_message_anniversaries_original_message ON message_anniversaries(originalMessageId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_message_anniversaries_anniversary_date ON message_anniversaries(anniversaryDate)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_message_anniversaries_user_date ON message_anniversaries(userId, anniversaryDate)")
            }
        }

        /**
         * Migration 7 -> 8: Dual Streak System
         *
         * Changes:
         * - Create dual_streaks table for Wisdom and Reflection streaks
         * - Two independent streaks with grace periods (one skip per 14 days)
         */
        val MIGRATION_7_8: Migration = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create dual_streaks table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS dual_streaks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        wisdomStreakCurrent INTEGER NOT NULL DEFAULT 0,
                        wisdomStreakLongest INTEGER NOT NULL DEFAULT 0,
                        wisdomLastMaintainedDate INTEGER NOT NULL DEFAULT 0,
                        wisdomGracePeriodUsed INTEGER NOT NULL DEFAULT 0,
                        wisdomGracePeriodUsedDate INTEGER NOT NULL DEFAULT 0,
                        wisdomGracePeriodResetDate INTEGER NOT NULL DEFAULT 0,
                        reflectionStreakCurrent INTEGER NOT NULL DEFAULT 0,
                        reflectionStreakLongest INTEGER NOT NULL DEFAULT 0,
                        reflectionLastMaintainedDate INTEGER NOT NULL DEFAULT 0,
                        reflectionGracePeriodUsed INTEGER NOT NULL DEFAULT 0,
                        reflectionGracePeriodUsedDate INTEGER NOT NULL DEFAULT 0,
                        reflectionGracePeriodResetDate INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """.trimIndent())

                // Create unique index on userId
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_dual_streaks_user ON dual_streaks(userId)")
            }
        }

        /**
         * Migration 8 -> 9: Vocabulary in Context
         *
         * Changes:
         * - Create word_usages table for tracking vocabulary usage in journal entries
         * - Add usedInContext, lastUsedAt, timesUsed to vocabulary_learning table
         */
        val MIGRATION_8_9: Migration = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create word_usages table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS word_usages (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        wordId INTEGER NOT NULL,
                        journalEntryId INTEGER NOT NULL,
                        usedInSentence TEXT NOT NULL,
                        matchedForm TEXT NOT NULL,
                        positionStart INTEGER NOT NULL,
                        positionEnd INTEGER NOT NULL,
                        detectedAt INTEGER NOT NULL,
                        celebrated INTEGER NOT NULL DEFAULT 0,
                        celebratedAt INTEGER,
                        bonusPointsAwarded INTEGER NOT NULL DEFAULT 0,
                        pointsClaimed INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(wordId) REFERENCES vocabulary(id) ON DELETE CASCADE,
                        FOREIGN KEY(journalEntryId) REFERENCES journal_entries(id) ON DELETE CASCADE
                    )
                """.trimIndent())

                // Create indices for word_usages
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_word_usages_user ON word_usages(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_word_usages_word ON word_usages(wordId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_word_usages_journal ON word_usages(journalEntryId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_word_usages_user_word ON word_usages(userId, wordId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_word_usages_user_detected ON word_usages(userId, detectedAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_word_usages_celebrated ON word_usages(celebrated)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_word_usages_user_celebrated ON word_usages(userId, celebrated)")

                // Add new fields to vocabulary_learning table
                db.execSQL("ALTER TABLE vocabulary_learning ADD COLUMN usedInContext INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE vocabulary_learning ADD COLUMN lastUsedAt INTEGER")
                db.execSQL("ALTER TABLE vocabulary_learning ADD COLUMN timesUsed INTEGER NOT NULL DEFAULT 0")
            }
        }

        /**
         * Migration 9 -> 10: Monthly Growth Letters
         *
         * Changes:
         * - Create monthly_letters table for personalized monthly reflection letters
         */
        val MIGRATION_9_10: Migration = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create monthly_letters table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS monthly_letters (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        month INTEGER NOT NULL,
                        year INTEGER NOT NULL,
                        greeting TEXT NOT NULL,
                        entriesCount INTEGER NOT NULL DEFAULT 0,
                        microEntriesCount INTEGER NOT NULL DEFAULT 0,
                        totalWords INTEGER NOT NULL DEFAULT 0,
                        activeDays INTEGER NOT NULL DEFAULT 0,
                        averageWordsPerEntry INTEGER NOT NULL DEFAULT 0,
                        mostActiveWeek TEXT,
                        topThemes TEXT NOT NULL DEFAULT '',
                        themesAnalysis TEXT NOT NULL DEFAULT '',
                        recurringWords TEXT NOT NULL DEFAULT '',
                        moodJourney TEXT NOT NULL DEFAULT '',
                        dominantMood TEXT,
                        moodAnalysis TEXT NOT NULL DEFAULT '',
                        moodTrend TEXT NOT NULL DEFAULT 'stable',
                        patternObservation TEXT NOT NULL DEFAULT '',
                        buddhaWisdom TEXT,
                        achievedMilestones TEXT NOT NULL DEFAULT '',
                        upcomingMilestones TEXT NOT NULL DEFAULT '',
                        streakInfo TEXT,
                        entriesChangePercent INTEGER NOT NULL DEFAULT 0,
                        wordsChangePercent INTEGER NOT NULL DEFAULT 0,
                        comparisonNote TEXT,
                        highlightEntryId INTEGER,
                        highlightQuote TEXT,
                        highlightReason TEXT,
                        closingMessage TEXT NOT NULL DEFAULT '',
                        encouragementNote TEXT,
                        generatedAt INTEGER NOT NULL,
                        isRead INTEGER NOT NULL DEFAULT 0,
                        readAt INTEGER,
                        isFavorite INTEGER NOT NULL DEFAULT 0,
                        sharedAt INTEGER,
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        lastSyncedAt INTEGER,
                        isDeleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                // Create indices for monthly_letters
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_monthly_letters_user ON monthly_letters(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_monthly_letters_month_year ON monthly_letters(month, year)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_monthly_letters_user_month_year ON monthly_letters(userId, month, year)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_monthly_letters_generated ON monthly_letters(generatedAt)")
            }
        }

        /**
         * Migration 10 -> 11: Yearly Wrapped
         *
         * Changes:
         * - Create yearly_wrapped table for end-of-year celebration
         */
        val MIGRATION_10_11: Migration = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create yearly_wrapped table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS yearly_wrapped (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        year INTEGER NOT NULL,
                        generatedAt INTEGER NOT NULL,
                        isViewed INTEGER NOT NULL DEFAULT 0,
                        viewedAt INTEGER,
                        totalJournalEntries INTEGER NOT NULL DEFAULT 0,
                        totalMicroEntries INTEGER NOT NULL DEFAULT 0,
                        totalWordsWritten INTEGER NOT NULL DEFAULT 0,
                        averageWordsPerEntry INTEGER NOT NULL DEFAULT 0,
                        longestEntry INTEGER NOT NULL DEFAULT 0,
                        longestEntryId INTEGER,
                        activeDaysCount INTEGER NOT NULL DEFAULT 0,
                        longestStreak INTEGER NOT NULL DEFAULT 0,
                        totalMeditationMinutes INTEGER NOT NULL DEFAULT 0,
                        bloomsCompleted INTEGER NOT NULL DEFAULT 0,
                        vocabularyWordsLearned INTEGER NOT NULL DEFAULT 0,
                        vocabularyWordsUsed INTEGER NOT NULL DEFAULT 0,
                        idiomsExplored INTEGER NOT NULL DEFAULT 0,
                        proverbsDiscovered INTEGER NOT NULL DEFAULT 0,
                        futureMessagesWritten INTEGER NOT NULL DEFAULT 0,
                        futureMessagesReceived INTEGER NOT NULL DEFAULT 0,
                        mostDistantMessage INTEGER NOT NULL DEFAULT 0,
                        mostActiveMonth INTEGER,
                        mostActiveDay TEXT,
                        mostActiveTimeOfDay TEXT,
                        firstEntryDate INTEGER,
                        lastEntryDate INTEGER,
                        averageMood REAL NOT NULL DEFAULT 0,
                        moodTrend TEXT NOT NULL DEFAULT 'stable',
                        mostCommonMood TEXT,
                        moodVariety INTEGER NOT NULL DEFAULT 0,
                        brightestMonth INTEGER,
                        mostReflectiveMonth INTEGER,
                        moodEvolution TEXT NOT NULL DEFAULT '[]',
                        topThemesJson TEXT NOT NULL DEFAULT '[]',
                        growthAreasJson TEXT NOT NULL DEFAULT '[]',
                        challengesOvercomeJson TEXT NOT NULL DEFAULT '[]',
                        keyMomentsJson TEXT NOT NULL DEFAULT '[]',
                        patternsJson TEXT NOT NULL DEFAULT '[]',
                        openingNarrative TEXT,
                        yearSummaryNarrative TEXT,
                        growthStoryNarrative TEXT,
                        moodJourneyNarrative TEXT,
                        lookingAheadNarrative TEXT,
                        milestoneNarrative TEXT,
                        shareableCardsJson TEXT NOT NULL DEFAULT '[]',
                        isShared INTEGER NOT NULL DEFAULT 0,
                        sharedAt INTEGER,
                        isFavorite INTEGER NOT NULL DEFAULT 0,
                        viewCompletionPercent INTEGER NOT NULL DEFAULT 0,
                        slidesViewed TEXT NOT NULL DEFAULT '[]',
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        lastSyncedAt INTEGER,
                        isDeleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                // Create indices for yearly_wrapped
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_yearly_wrapped_user ON yearly_wrapped(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_yearly_wrapped_year ON yearly_wrapped(year)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_yearly_wrapped_user_year ON yearly_wrapped(userId, year)")
            }
        }

        /**
         * Migration 11 -> 12: Collaborative Future Messages
         *
         * Changes:
         * - Create collaborative_messages table for sent messages
         * - Create received_collaborative_messages table for received messages
         * - Create message_contacts table for managing recipients
         * - Create message_occasions table for occasion-based reminders
         */
        val MIGRATION_11_12: Migration = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create collaborative_messages table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS collaborative_messages (
                        id TEXT PRIMARY KEY NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        recipientId TEXT,
                        recipientContact TEXT,
                        recipientName TEXT NOT NULL,
                        title TEXT NOT NULL,
                        content TEXT NOT NULL,
                        deliveryDate INTEGER NOT NULL,
                        occasion TEXT,
                        isDelivered INTEGER NOT NULL DEFAULT 0,
                        isRead INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL DEFAULT 0,
                        deliveredAt INTEGER,
                        readAt INTEGER,
                        attachedPhotosJson TEXT NOT NULL DEFAULT '[]',
                        voiceRecordingUri TEXT,
                        voiceRecordingDuration INTEGER NOT NULL DEFAULT 0,
                        cardTheme TEXT NOT NULL DEFAULT 'default',
                        cardBackgroundColor TEXT,
                        status TEXT NOT NULL DEFAULT 'pending',
                        deliveryMethod TEXT NOT NULL DEFAULT 'in_app',
                        retryCount INTEGER NOT NULL DEFAULT 0,
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        lastSyncedAt INTEGER,
                        isDeleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_collaborative_messages_user ON collaborative_messages(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_collaborative_messages_delivery ON collaborative_messages(deliveryDate)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_collaborative_messages_recipient ON collaborative_messages(recipientId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_collaborative_messages_status ON collaborative_messages(status)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_collaborative_messages_occasion ON collaborative_messages(occasion)")

                // Create received_collaborative_messages table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS received_collaborative_messages (
                        id TEXT PRIMARY KEY NOT NULL,
                        senderId TEXT NOT NULL,
                        senderName TEXT NOT NULL,
                        title TEXT NOT NULL,
                        content TEXT NOT NULL,
                        deliveredAt INTEGER NOT NULL,
                        isRead INTEGER NOT NULL DEFAULT 0,
                        readAt INTEGER,
                        attachedPhotosJson TEXT NOT NULL DEFAULT '[]',
                        voiceRecordingUri TEXT,
                        voiceRecordingDuration INTEGER NOT NULL DEFAULT 0,
                        cardTheme TEXT NOT NULL,
                        occasion TEXT,
                        isFavorite INTEGER NOT NULL DEFAULT 0,
                        replyMessageId TEXT,
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        lastSyncedAt INTEGER,
                        isDeleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_received_collab_messages_sender ON received_collaborative_messages(senderId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_received_collab_messages_delivered ON received_collaborative_messages(deliveredAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_received_collab_messages_read ON received_collaborative_messages(isRead)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_received_collab_messages_favorite ON received_collaborative_messages(isFavorite)")

                // Create message_contacts table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS message_contacts (
                        id TEXT PRIMARY KEY NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        displayName TEXT NOT NULL,
                        contactMethod TEXT NOT NULL,
                        contactValue TEXT NOT NULL,
                        avatarUrl TEXT,
                        messagesSent INTEGER NOT NULL DEFAULT 0,
                        lastMessageAt INTEGER,
                        isFavorite INTEGER NOT NULL DEFAULT 0,
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        lastSyncedAt INTEGER,
                        createdAt INTEGER NOT NULL DEFAULT 0,
                        isDeleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_message_contacts_user ON message_contacts(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_message_contacts_method ON message_contacts(contactMethod)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_message_contacts_value ON message_contacts(contactValue)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_message_contacts_favorite ON message_contacts(isFavorite)")

                // Create message_occasions table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS message_occasions (
                        id TEXT PRIMARY KEY NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        contactId TEXT NOT NULL,
                        occasionType TEXT NOT NULL,
                        date INTEGER NOT NULL,
                        isRecurring INTEGER NOT NULL DEFAULT 1,
                        reminderDaysBefore INTEGER NOT NULL DEFAULT 7,
                        lastNotifiedYear INTEGER,
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        lastSyncedAt INTEGER,
                        createdAt INTEGER NOT NULL DEFAULT 0,
                        isDeleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_message_occasions_user ON message_occasions(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_message_occasions_contact ON message_occasions(contactId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_message_occasions_type ON message_occasions(occasionType)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_message_occasions_date ON message_occasions(date)")
            }
        }

        /**
         * Migration 12 -> 13: Haven Personal Therapist
         *
         * Changes:
         * - Create haven_sessions table for therapeutic conversations
         * - Create haven_exercises table for guided exercises
         */
        val MIGRATION_12_13: Migration = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create haven_sessions table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS haven_sessions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        sessionType TEXT NOT NULL,
                        startedAt INTEGER NOT NULL,
                        endedAt INTEGER,
                        messagesJson TEXT NOT NULL DEFAULT '[]',
                        techniquesUsedJson TEXT NOT NULL DEFAULT '[]',
                        moodBefore INTEGER,
                        moodAfter INTEGER,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        userRating INTEGER,
                        keyInsightsJson TEXT,
                        suggestedExercisesJson TEXT,
                        followUpScheduled INTEGER,
                        containedCrisisDetection INTEGER NOT NULL DEFAULT 0,
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        lastSyncedAt INTEGER,
                        isDeleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_sessions_user ON haven_sessions(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_sessions_type ON haven_sessions(sessionType)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_sessions_started ON haven_sessions(startedAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_sessions_completed ON haven_sessions(isCompleted)")

                // Create haven_exercises table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS haven_exercises (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        exerciseType TEXT NOT NULL,
                        completedAt INTEGER NOT NULL,
                        durationSeconds INTEGER NOT NULL DEFAULT 0,
                        notes TEXT,
                        exerciseDataJson TEXT,
                        fromSessionId INTEGER,
                        wasCompleted INTEGER NOT NULL DEFAULT 1,
                        completionRate REAL NOT NULL DEFAULT 1.0,
                        helpfulness INTEGER,
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        isDeleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_exercises_user ON haven_exercises(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_exercises_type ON haven_exercises(exerciseType)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_exercises_session ON haven_exercises(fromSessionId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_exercises_completed ON haven_exercises(completedAt)")
            }
        }

        /**
         * Migration 13 -> 14: Personalized Learning Paths
         *
         * Changes:
         * - Create learning_paths table
         * - Create learning_lessons table
         * - Create learning_reflections table
         * - Create path_recommendations table
         * - Create path_progress_checkpoints table
         * - Create learning_notes table
         * - Create path_badges table
         */
        val MIGRATION_13_14: Migration = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create learning_paths table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS learning_paths (
                        id TEXT PRIMARY KEY NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        pathType TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        totalLessons INTEGER NOT NULL,
                        completedLessons INTEGER NOT NULL DEFAULT 0,
                        currentLessonId TEXT,
                        startedAt INTEGER NOT NULL,
                        lastAccessedAt INTEGER NOT NULL,
                        completedAt INTEGER,
                        isActive INTEGER NOT NULL DEFAULT 1,
                        progressPercentage REAL NOT NULL DEFAULT 0,
                        estimatedMinutesTotal INTEGER NOT NULL,
                        difficultyLevel TEXT NOT NULL DEFAULT 'beginner',
                        iconEmoji TEXT NOT NULL DEFAULT '📚',
                        colorTheme TEXT NOT NULL DEFAULT '#6366F1'
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_learning_paths_user ON learning_paths(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_learning_paths_type ON learning_paths(pathType)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_learning_paths_user_type ON learning_paths(userId, pathType)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_learning_paths_active ON learning_paths(isActive)")

                // Create learning_lessons table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS learning_lessons (
                        id TEXT PRIMARY KEY NOT NULL,
                        pathId TEXT NOT NULL,
                        orderIndex INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        lessonType TEXT NOT NULL,
                        contentJson TEXT NOT NULL,
                        estimatedMinutes INTEGER NOT NULL,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        completedAt INTEGER,
                        userNotesJson TEXT,
                        quizScore INTEGER,
                        unlockRequirement TEXT,
                        isLocked INTEGER NOT NULL DEFAULT 1
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_learning_lessons_path ON learning_lessons(pathId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_learning_lessons_order ON learning_lessons(pathId, orderIndex)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_learning_lessons_completed ON learning_lessons(isCompleted)")

                // Create learning_reflections table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS learning_reflections (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        lessonId TEXT NOT NULL,
                        pathId TEXT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        promptText TEXT NOT NULL,
                        userResponse TEXT NOT NULL,
                        aiInsight TEXT,
                        createdAt INTEGER NOT NULL,
                        wordCount INTEGER NOT NULL DEFAULT 0,
                        mood TEXT,
                        isBookmarked INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_learning_reflections_lesson ON learning_reflections(lessonId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_learning_reflections_path ON learning_reflections(pathId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_learning_reflections_user ON learning_reflections(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_learning_reflections_user_created ON learning_reflections(userId, createdAt)")

                // Create path_recommendations table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS path_recommendations (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        pathType TEXT NOT NULL,
                        reason TEXT NOT NULL,
                        confidenceScore REAL NOT NULL,
                        basedOnEntriesJson TEXT NOT NULL DEFAULT '[]',
                        basedOnPatternsJson TEXT NOT NULL DEFAULT '[]',
                        createdAt INTEGER NOT NULL,
                        isDismissed INTEGER NOT NULL DEFAULT 0,
                        isAccepted INTEGER NOT NULL DEFAULT 0,
                        dismissedAt INTEGER,
                        acceptedAt INTEGER
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_path_recommendations_user ON path_recommendations(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_path_recommendations_type ON path_recommendations(pathType)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_path_recommendations_user_created ON path_recommendations(userId, createdAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_path_recommendations_dismissed ON path_recommendations(isDismissed)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_path_recommendations_accepted ON path_recommendations(isAccepted)")

                // Create path_progress_checkpoints table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS path_progress_checkpoints (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        pathId TEXT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        checkpointType TEXT NOT NULL,
                        lessonId TEXT,
                        description TEXT NOT NULL,
                        xpEarned INTEGER NOT NULL DEFAULT 0,
                        tokensEarned INTEGER NOT NULL DEFAULT 0,
                        achievedAt INTEGER NOT NULL,
                        celebrationShown INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_path_checkpoints_path ON path_progress_checkpoints(pathId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_path_checkpoints_user ON path_progress_checkpoints(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_path_checkpoints_user_path ON path_progress_checkpoints(userId, pathId)")

                // Create learning_notes table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS learning_notes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        lessonId TEXT NOT NULL,
                        pathId TEXT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        noteContent TEXT NOT NULL,
                        highlightedText TEXT,
                        noteColor TEXT NOT NULL DEFAULT '#FFF59D',
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_learning_notes_lesson ON learning_notes(lessonId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_learning_notes_path ON learning_notes(pathId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_learning_notes_user ON learning_notes(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_learning_notes_user_created ON learning_notes(userId, createdAt)")

                // Create path_badges table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS path_badges (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        pathId TEXT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        badgeType TEXT NOT NULL,
                        badgeName TEXT NOT NULL,
                        badgeDescription TEXT NOT NULL,
                        badgeIcon TEXT NOT NULL,
                        earnedAt INTEGER NOT NULL,
                        isDisplayed INTEGER NOT NULL DEFAULT 1,
                        rarity TEXT NOT NULL DEFAULT 'common'
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_path_badges_path ON path_badges(pathId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_path_badges_user ON path_badges(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_path_badges_user_earned ON path_badges(userId, earnedAt)")
            }
        }

        /**
         * Migration 14 -> 15: Deep Dive Days & Social Accountability Circles
         *
         * Changes:
         * - Create deep_dives table for structured reflection sessions
         * - Create accountability_circles table
         * - Create circle_members table
         * - Create circle_updates table
         * - Create circle_nudges table
         * - Create circle_challenges table
         * - Create circle_privacy_settings table
         * - Create circle_notifications table
         * - Create circle_member_stats_cache table
         */
        val MIGRATION_14_15: Migration = object : Migration(14, 15) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create deep_dives table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS deep_dives (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        theme TEXT NOT NULL,
                        scheduledDate INTEGER NOT NULL,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        completedAt INTEGER,
                        openingReflection TEXT,
                        coreResponse TEXT,
                        keyInsight TEXT,
                        commitmentStatement TEXT,
                        moodBefore INTEGER,
                        moodAfter INTEGER,
                        aiThemeContext TEXT,
                        aiPrompts TEXT,
                        aiReflectionSummary TEXT,
                        aiFollowUpSuggestions TEXT,
                        durationMinutes INTEGER NOT NULL DEFAULT 0,
                        sessionStartedAt INTEGER,
                        currentStep TEXT NOT NULL DEFAULT 'not_started',
                        promptVariation INTEGER NOT NULL DEFAULT 0,
                        isScheduledNotificationSent INTEGER NOT NULL DEFAULT 0,
                        reminderSentAt INTEGER,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        lastSyncedAt INTEGER,
                        isDeleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_deep_dives_user ON deep_dives(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_deep_dives_scheduled ON deep_dives(scheduledDate)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_deep_dives_user_scheduled ON deep_dives(userId, scheduledDate)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_deep_dives_theme ON deep_dives(theme)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_deep_dives_completed ON deep_dives(isCompleted)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_deep_dives_user_completed ON deep_dives(userId, isCompleted)")

                // Create accountability_circles table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS accountability_circles (
                        id TEXT PRIMARY KEY NOT NULL,
                        name TEXT NOT NULL,
                        description TEXT,
                        createdBy TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        inviteCode TEXT NOT NULL,
                        isActive INTEGER NOT NULL DEFAULT 1,
                        memberCount INTEGER NOT NULL DEFAULT 1,
                        colorTheme TEXT NOT NULL DEFAULT 'default',
                        iconEmoji TEXT NOT NULL DEFAULT '🌟',
                        allowNudges INTEGER NOT NULL DEFAULT 1,
                        allowChallenges INTEGER NOT NULL DEFAULT 1,
                        maxMembers INTEGER NOT NULL DEFAULT 10,
                        lastActivityAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circles_created_by ON accountability_circles(createdBy)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_circles_invite_code ON accountability_circles(inviteCode)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circles_active ON accountability_circles(isActive)")

                // Create circle_members table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS circle_members (
                        id TEXT PRIMARY KEY NOT NULL,
                        circleId TEXT NOT NULL,
                        userId TEXT NOT NULL,
                        displayName TEXT NOT NULL,
                        avatarUrl TEXT,
                        joinedAt INTEGER NOT NULL,
                        role TEXT NOT NULL DEFAULT 'member',
                        isActive INTEGER NOT NULL DEFAULT 1,
                        lastActiveAt INTEGER NOT NULL,
                        currentStreak INTEGER NOT NULL DEFAULT 0,
                        totalEntries INTEGER NOT NULL DEFAULT 0,
                        lastEntryAt INTEGER
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_members_circle ON circle_members(circleId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_members_user ON circle_members(userId)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_circle_members_circle_user ON circle_members(circleId, userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_members_active ON circle_members(isActive)")

                // Create circle_updates table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS circle_updates (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        circleId TEXT NOT NULL,
                        userId TEXT NOT NULL,
                        updateType TEXT NOT NULL,
                        content TEXT NOT NULL,
                        metadata TEXT,
                        createdAt INTEGER NOT NULL,
                        reactionsJson TEXT NOT NULL DEFAULT '{}',
                        reactionCount INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_updates_circle ON circle_updates(circleId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_updates_user ON circle_updates(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_updates_type ON circle_updates(updateType)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_updates_created ON circle_updates(createdAt)")

                // Create circle_nudges table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS circle_nudges (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        circleId TEXT NOT NULL,
                        fromUserId TEXT NOT NULL,
                        fromDisplayName TEXT NOT NULL,
                        toUserId TEXT NOT NULL,
                        nudgeType TEXT NOT NULL,
                        message TEXT,
                        createdAt INTEGER NOT NULL,
                        isRead INTEGER NOT NULL DEFAULT 0,
                        respondedAt INTEGER
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_nudges_circle ON circle_nudges(circleId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_nudges_from ON circle_nudges(fromUserId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_nudges_to ON circle_nudges(toUserId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_nudges_read ON circle_nudges(isRead)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_nudges_created ON circle_nudges(createdAt)")

                // Create circle_challenges table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS circle_challenges (
                        id TEXT PRIMARY KEY NOT NULL,
                        circleId TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        startDate INTEGER NOT NULL,
                        endDate INTEGER NOT NULL,
                        targetType TEXT NOT NULL,
                        targetValue INTEGER NOT NULL,
                        createdBy TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        participantsJson TEXT NOT NULL DEFAULT '[]',
                        progressJson TEXT NOT NULL DEFAULT '{}',
                        isActive INTEGER NOT NULL DEFAULT 1,
                        completedByJson TEXT NOT NULL DEFAULT '[]'
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_challenges_circle ON circle_challenges(circleId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_challenges_created_by ON circle_challenges(createdBy)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_challenges_active ON circle_challenges(isActive)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_challenges_start ON circle_challenges(startDate)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_challenges_end ON circle_challenges(endDate)")

                // Create circle_privacy_settings table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS circle_privacy_settings (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        circleId TEXT NOT NULL,
                        shareStreakCount INTEGER NOT NULL DEFAULT 1,
                        shareEntryCount INTEGER NOT NULL DEFAULT 1,
                        shareMeditationStats INTEGER NOT NULL DEFAULT 1,
                        shareChallengeParticipation INTEGER NOT NULL DEFAULT 1,
                        allowNudgesFromMembers INTEGER NOT NULL DEFAULT 1,
                        showOnlineStatus INTEGER NOT NULL DEFAULT 0,
                        updatedAt INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_circle_privacy_user_circle ON circle_privacy_settings(userId, circleId)")

                // Create circle_notifications table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS circle_notifications (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        circleId TEXT NOT NULL,
                        notificationType TEXT NOT NULL,
                        title TEXT NOT NULL,
                        message TEXT NOT NULL,
                        actionType TEXT,
                        actionData TEXT,
                        isRead INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_notifications_user ON circle_notifications(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_notifications_circle ON circle_notifications(circleId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_notifications_read ON circle_notifications(isRead)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_notifications_created ON circle_notifications(createdAt)")

                // Create circle_member_stats_cache table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS circle_member_stats_cache (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL,
                        circleId TEXT NOT NULL,
                        currentStreak INTEGER,
                        longestStreak INTEGER,
                        totalEntries INTEGER,
                        totalWords INTEGER,
                        meditationMinutes INTEGER,
                        lastActiveAt INTEGER,
                        lastUpdated INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_circle_stats_cache_user_circle ON circle_member_stats_cache(userId, circleId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_circle_stats_cache_updated ON circle_member_stats_cache(lastUpdated)")
            }
        }

        /**
         * Migration 15 -> 16: Gamification 5.0 - Enhanced Skills & Achievements
         *
         * Changes:
         * - Add perk tracking fields to player_skills (unlockedPerkIds, perkFreezeTokens, perkFreezeTokensUsed)
         * - Add hidden/secret achievement support to achievements
         * - Add direct XP rewards to achievements with MYTHIC rarity tier
         *
         * The 20-level skill system with perks:
         * - Each skill (Clarity, Discipline, Courage) now has 20 levels
         * - Perks unlock at levels 2, 3, 5, 7, 10, 12, 15, 17, 20
         * - Discipline perks at L5 and L12 grant streak freeze tokens
         *
         * Achievement rarity tiers and XP rewards:
         * - Common: 50 XP
         * - Uncommon: 100 XP
         * - Rare: 200 XP
         * - Epic: 350 XP
         * - Legendary: 500 XP
         * - Mythic: 750 XP (NEW)
         */
        val MIGRATION_15_16: Migration = object : Migration(15, 16) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add perk tracking fields to player_skills
                db.execSQL("ALTER TABLE player_skills ADD COLUMN unlockedPerkIds TEXT NOT NULL DEFAULT '[]'")
                db.execSQL("ALTER TABLE player_skills ADD COLUMN perkFreezeTokens INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE player_skills ADD COLUMN perkFreezeTokensUsed INTEGER NOT NULL DEFAULT 0")

                // Add hidden/secret achievement support and XP rewards to achievements
                db.execSQL("ALTER TABLE achievements ADD COLUMN isHidden INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE achievements ADD COLUMN isSecret INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE achievements ADD COLUMN xpReward INTEGER NOT NULL DEFAULT 50")
            }
        }

        /**
         * Migration 16 -> 17: Soul Layer Intelligence System
         *
         * Changes:
         * - Create surfaced_memories table for memory surfacing tracking
         * - Create user_context_cache table for user context caching
         * - Create notification_history table for notification intelligence
         * - Create detected_patterns table for pattern detection
         * - Create buddha_interactions table for Buddha AI tracking
         * - Create haven_insights table for Haven therapeutic insights
         * - Create temporal_content_history table for temporal content tracking
         * - Create first_week_progress table for first week journey tracking
         */
        val MIGRATION_16_17: Migration = object : Migration(16, 17) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create surfaced_memories table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS surfaced_memories (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        journalEntryId INTEGER NOT NULL,
                        surfaceReason TEXT NOT NULL,
                        surfaceContext TEXT NOT NULL,
                        surfacedAt INTEGER NOT NULL,
                        wasInteractedWith INTEGER NOT NULL DEFAULT 0,
                        interactionType TEXT,
                        interactedAt INTEGER,
                        memoryPreview TEXT NOT NULL,
                        originalMood TEXT,
                        originalDate INTEGER NOT NULL,
                        yearsAgo INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_surfaced_memories_user ON surfaced_memories(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_surfaced_memories_entry ON surfaced_memories(journalEntryId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_surfaced_memories_surfaced ON surfaced_memories(surfacedAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_surfaced_memories_user_surfaced ON surfaced_memories(userId, surfacedAt)")

                // Create user_context_cache table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_context_cache (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        userArchetype TEXT NOT NULL,
                        trustLevel TEXT NOT NULL,
                        engagementLevel TEXT NOT NULL,
                        dominantMood TEXT,
                        moodTrend TEXT NOT NULL,
                        emotionalEnergy TEXT NOT NULL,
                        isStruggling INTEGER NOT NULL DEFAULT 0,
                        isThriving INTEGER NOT NULL DEFAULT 0,
                        stressSignalsJson TEXT NOT NULL DEFAULT '[]',
                        recentThemesJson TEXT NOT NULL DEFAULT '[]',
                        recurringPatternsJson TEXT NOT NULL DEFAULT '[]',
                        recentWinsJson TEXT NOT NULL DEFAULT '[]',
                        recurringChallengesJson TEXT NOT NULL DEFAULT '[]',
                        totalEntries INTEGER NOT NULL DEFAULT 0,
                        daysWithPrody INTEGER NOT NULL DEFAULT 0,
                        daysSinceLastEntry INTEGER NOT NULL DEFAULT 0,
                        averageWordsPerEntry INTEGER NOT NULL DEFAULT 0,
                        preferredTone TEXT NOT NULL DEFAULT 'WARM',
                        preferredJournalTime TEXT,
                        computedAt INTEGER NOT NULL,
                        validUntil INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_user_context_cache_user ON user_context_cache(userId)")

                // Create notification_history table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS notification_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        notificationType TEXT NOT NULL,
                        decision TEXT NOT NULL,
                        decisionReason TEXT,
                        title TEXT,
                        body TEXT,
                        scheduledAt INTEGER NOT NULL,
                        sentAt INTEGER,
                        wasOpened INTEGER NOT NULL DEFAULT 0,
                        openedAt INTEGER,
                        resultedInAction INTEGER NOT NULL DEFAULT 0,
                        actionType TEXT,
                        actionAt INTEGER,
                        userArchetypeAtTime TEXT,
                        wasUserStruggling INTEGER NOT NULL DEFAULT 0,
                        hourOfDay INTEGER NOT NULL,
                        dayOfWeek INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_notification_history_user ON notification_history(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_notification_history_type ON notification_history(notificationType)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_notification_history_sent ON notification_history(sentAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_notification_history_user_sent ON notification_history(userId, sentAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_notification_history_user_type ON notification_history(userId, notificationType)")

                // Create detected_patterns table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS detected_patterns (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        patternType TEXT NOT NULL,
                        patternDescription TEXT NOT NULL,
                        confidence REAL NOT NULL,
                        supportingEvidence TEXT NOT NULL,
                        firstDetectedAt INTEGER NOT NULL,
                        lastConfirmedAt INTEGER NOT NULL,
                        occurrenceCount INTEGER NOT NULL DEFAULT 1,
                        isActive INTEGER NOT NULL DEFAULT 1,
                        wasShownToUser INTEGER NOT NULL DEFAULT 0,
                        shownAt INTEGER,
                        userFeedback TEXT,
                        feedbackAt INTEGER,
                        detectedAt INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_detected_patterns_user ON detected_patterns(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_detected_patterns_type ON detected_patterns(patternType)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_detected_patterns_user_type ON detected_patterns(userId, patternType)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_detected_patterns_detected ON detected_patterns(detectedAt)")

                // Create buddha_interactions table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS buddha_interactions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        interactionType TEXT NOT NULL,
                        contextMood TEXT,
                        contextMoodIntensity INTEGER,
                        responseWisdomStyle TEXT,
                        responseLength INTEGER NOT NULL DEFAULT 0,
                        wasHelpful INTEGER,
                        helpfulnessRating INTEGER,
                        wasExpanded INTEGER NOT NULL DEFAULT 0,
                        wasSaved INTEGER NOT NULL DEFAULT 0,
                        wasShared INTEGER NOT NULL DEFAULT 0,
                        timeSpentViewingMs INTEGER,
                        interactedAt INTEGER NOT NULL,
                        journalEntryId INTEGER,
                        journalWordCount INTEGER
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_buddha_interactions_user ON buddha_interactions(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_buddha_interactions_type ON buddha_interactions(interactionType)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_buddha_interactions_interacted ON buddha_interactions(interactedAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_buddha_interactions_user_interacted ON buddha_interactions(userId, interactedAt)")

                // Create haven_insights table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS haven_insights (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        sessionId INTEGER NOT NULL,
                        sessionType TEXT NOT NULL,
                        insightType TEXT NOT NULL,
                        insightContent TEXT NOT NULL,
                        confidence REAL NOT NULL DEFAULT 0.5,
                        therapeuticApproachUsed TEXT,
                        wasEffective INTEGER,
                        moodBefore INTEGER,
                        moodAfter INTEGER,
                        moodImprovement INTEGER,
                        createdAt INTEGER NOT NULL,
                        wasUsedInFutureSession INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_insights_user ON haven_insights(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_insights_session ON haven_insights(sessionId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_insights_created ON haven_insights(createdAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_insights_user_created ON haven_insights(userId, createdAt)")

                // Create temporal_content_history table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS temporal_content_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        contentType TEXT NOT NULL,
                        contentId TEXT,
                        contentPreview TEXT NOT NULL,
                        timeOfDay TEXT NOT NULL,
                        dayOfWeek INTEGER NOT NULL,
                        seasonalContext TEXT,
                        shownAt INTEGER NOT NULL,
                        wasEngaged INTEGER NOT NULL DEFAULT 0,
                        engagementType TEXT,
                        engagedAt INTEGER
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_temporal_content_user ON temporal_content_history(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_temporal_content_type ON temporal_content_history(contentType)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_temporal_content_shown ON temporal_content_history(shownAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_temporal_content_user_type_shown ON temporal_content_history(userId, contentType, shownAt)")

                // Create first_week_progress table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS first_week_progress (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        startedAt INTEGER NOT NULL,
                        graduatedAt INTEGER,
                        isGraduated INTEGER NOT NULL DEFAULT 0,
                        day1ProgressJson TEXT NOT NULL DEFAULT '{}',
                        day2ProgressJson TEXT NOT NULL DEFAULT '{}',
                        day3ProgressJson TEXT NOT NULL DEFAULT '{}',
                        day4ProgressJson TEXT NOT NULL DEFAULT '{}',
                        day5ProgressJson TEXT NOT NULL DEFAULT '{}',
                        day6ProgressJson TEXT NOT NULL DEFAULT '{}',
                        day7ProgressJson TEXT NOT NULL DEFAULT '{}',
                        completedMilestonesJson TEXT NOT NULL DEFAULT '[]',
                        celebrationsShownJson TEXT NOT NULL DEFAULT '[]',
                        totalEntriesInFirstWeek INTEGER NOT NULL DEFAULT 0,
                        totalWordsInFirstWeek INTEGER NOT NULL DEFAULT 0,
                        featuresExploredJson TEXT NOT NULL DEFAULT '[]',
                        longestStreakInFirstWeek INTEGER NOT NULL DEFAULT 0,
                        totalXpEarned INTEGER NOT NULL DEFAULT 0,
                        totalTokensEarned INTEGER NOT NULL DEFAULT 0,
                        updatedAt INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_first_week_progress_user ON first_week_progress(userId)")
            }
        }

        /**
         * Migration 17 -> 18: Mirror Evolution - Haven Memory (THE VAULT) for Witness Mode
         *
         * Changes:
         * - Create haven_memories table for storing facts/truths from conversations
         * - This enables Haven's "Witness Mode" callback feature where Haven
         *   remembers things the user mentioned and follows up on them
         *
         * Example use cases:
         * - User mentions "I have an exam on Friday"
         * - Haven stores this as a memory
         * - After the date passes, Haven asks "How did the exam go?"
         */
        val MIGRATION_17_18: Migration = object : Migration(17, 18) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create haven_memories table for THE VAULT (Witness Mode)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS haven_memories (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        fact TEXT NOT NULL,
                        factDate INTEGER,
                        category TEXT NOT NULL DEFAULT 'general',
                        status TEXT NOT NULL DEFAULT 'pending',
                        followUpDate INTEGER,
                        sourceSessionId INTEGER,
                        sourceMessage TEXT,
                        followedUpAt INTEGER,
                        followUpResponse TEXT,
                        outcome TEXT,
                        importance INTEGER NOT NULL DEFAULT 1,
                        notificationSent INTEGER NOT NULL DEFAULT 0,
                        notificationSentAt INTEGER,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        isDeleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                // Create indices for haven_memories
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_memories_user ON haven_memories(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_memories_status ON haven_memories(status)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_memories_fact_date ON haven_memories(factDate)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_memories_created ON haven_memories(createdAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_memories_user_status ON haven_memories(userId, status)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_haven_memories_category ON haven_memories(category)")
            }
        }

        /**
         * Migration 18 -> 19: Mirror Evolution - Evidence Locker
         *
         * Changes:
         * - Create evidence table for THE LOCKER feature
         * - Evidence replaces XP/Plants with concrete proof of growth:
         *   - Receipt: Mirror found a contradiction with past entry
         *   - Witness: Haven followed up on something user mentioned
         *   - Prophecy: Future Message prediction was verified
         *   - Breakthrough: Journal insight was captured
         *   - Streak: Milestone achievement was reached
         */
        val MIGRATION_18_19: Migration = object : Migration(18, 19) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create evidence table for THE LOCKER
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS evidence (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        evidenceType TEXT NOT NULL,
                        content TEXT NOT NULL,
                        secondaryContent TEXT,
                        sourceType TEXT,
                        sourceId INTEGER,
                        thenDate INTEGER,
                        nowDate INTEGER,
                        daysApart INTEGER,
                        witnessOutcome TEXT,
                        predictionAccurate INTEGER,
                        rarity TEXT NOT NULL DEFAULT 'common',
                        isViewed INTEGER NOT NULL DEFAULT 0,
                        viewedAt INTEGER,
                        isPinned INTEGER NOT NULL DEFAULT 0,
                        collectedAt INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        lastSyncedAt INTEGER,
                        isDeleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                // Create indices for evidence table
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_evidence_user ON evidence(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_evidence_type ON evidence(evidenceType)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_evidence_user_type ON evidence(userId, evidenceType)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_evidence_collected ON evidence(collectedAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_evidence_source ON evidence(sourceType, sourceId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_evidence_rarity ON evidence(rarity)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_evidence_viewed ON evidence(isViewed)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_evidence_pinned ON evidence(isPinned)")
            }
        }

        /**
         * Migration 19 -> 20: Mirror Evolution - Sealed Pods (Future Message Prophecy)
         *
         * Changes:
         * - Add prediction field to future_messages for user predictions
         * - Add predictionVerified field to track prediction accuracy
         * - Add predictionVerifiedAt field for verification timestamp
         * - Enables "Prophecy" evidence drops when predictions are verified
         *
         * Example use cases:
         * - User writes "I predict I will have finished my degree" in a future message
         * - When delivered, user can verify if the prediction was accurate
         * - If verified, a "Prophecy" evidence drop is created for the Locker
         */
        val MIGRATION_19_20: Migration = object : Migration(19, 20) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add prediction fields to future_messages for Prophecy feature
                db.execSQL("ALTER TABLE future_messages ADD COLUMN prediction TEXT")
                db.execSQL("ALTER TABLE future_messages ADD COLUMN predictionVerified INTEGER")
                db.execSQL("ALTER TABLE future_messages ADD COLUMN predictionVerifiedAt INTEGER")
            }
        }

        fun getInstance(context: Context): ProdyDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

private fun buildDatabase(context: Context): ProdyDatabase {
            return try {
                // Initialize secure database manager
                val secureDbManager = SecureDatabaseManager(context)
                
                // Create SQLCipher support factory with secure passphrase
                val supportFactory = runBlocking {
                    secureDbManager.createSQLCipherSupportFactory()
                }
                
                Room.databaseBuilder(
                    context.applicationContext,
                    ProdyDatabase::class.java,
                    DATABASE_NAME
                )
                    .openHelperFactory(supportFactory)
                    .addMigrations(
                        MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8,
                        MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12,
                        MIGRATION_12_13, MIGRATION_13_14, MIGRATION_14_15, MIGRATION_15_16,
                        MIGRATION_16_17, MIGRATION_17_18, MIGRATION_18_19, MIGRATION_19_20
                    )
                    .fallbackToDestructiveMigration()
                    .addCallback(SecureDatabaseCallback(context, secureDbManager))
                    .build()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create secure database, falling back to unencrypted", e)
                // Fallback to unencrypted database for development
                Room.databaseBuilder(
                    context.applicationContext,
                    ProdyDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(
                        MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8,
                        MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12,
                        MIGRATION_12_13, MIGRATION_13_14, MIGRATION_14_15, MIGRATION_15_16,
                        MIGRATION_16_17, MIGRATION_17_18, MIGRATION_18_19, MIGRATION_19_20
                    )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
            }
        }

/**
         * Database callback for initialization tasks
         */
        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d(TAG, "Database created successfully")
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                Log.d(TAG, "Database opened")
            }

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                Log.w(TAG, "Destructive migration performed - data was cleared")
            }
        }

        /**
         * Secure Database callback with encryption verification
         */
        private class SecureDatabaseCallback(
            private val context: Context,
            private val secureDbManager: SecureDatabaseManager
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d(TAG, "Secure database created successfully")
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                Log.d(TAG, "Secure database opened")
                
                // Verify database integrity
                runBlocking {
                    val databaseFile = context.getDatabasePath(DATABASE_NAME)
                    val isIntegrityValid = secureDbManager.verifyDatabaseIntegrity(databaseFile)
                    
                    if (!isIntegrityValid) {
                        Log.e(TAG, "Database integrity check failed!")
                        // Handle integrity failure appropriately
                    }
                }
            }

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                Log.w(TAG, "Secure database destructive migration performed - data was cleared")
                
                // Clear encryption data after destructive migration
                runBlocking {
                    secureDbManager.clearDatabaseEncryption()
                }
            }
        }
    }
}
