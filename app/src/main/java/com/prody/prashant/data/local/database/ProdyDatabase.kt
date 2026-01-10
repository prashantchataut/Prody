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
 * - Wisdom Collection (saved quotes, words, proverbs)
 * - Micro-journaling entries
 * - Weekly digests
 * - Daily rituals
 * - Future message replies (time capsule chains)
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
 * - Version 6: Daily Engagement Features
 *              Added SavedWisdomEntity (Wisdom Collection)
 *              Added MicroEntryEntity (Micro-journaling)
 *              Added WeeklyDigestEntity (Weekly summaries)
 *              Added DailyRitualEntity (Daily ritual tracking)
 *              Added FutureMessageReplyEntity (Time capsule chains)
 *              Added WritingCompanionSuggestionEntity (AI Writing Companion tracking)
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
        // New entities for Daily Engagement Features
        SavedWisdomEntity::class,
        MicroEntryEntity::class,
        WeeklyDigestEntity::class,
        DailyRitualEntity::class,
        FutureMessageReplyEntity::class,
        WritingCompanionSuggestionEntity::class
    ],
    version = 6,
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

        val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // SavedWisdomEntity - Wisdom Collection
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS saved_wisdom (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        type TEXT NOT NULL,
                        content TEXT NOT NULL,
                        author TEXT,
                        secondaryContent TEXT,
                        sourceId INTEGER,
                        tags TEXT NOT NULL DEFAULT '',
                        theme TEXT,
                        savedAt INTEGER NOT NULL DEFAULT 0,
                        lastShownAt INTEGER,
                        timesShown INTEGER NOT NULL DEFAULT 0,
                        timesViewed INTEGER NOT NULL DEFAULT 0,
                        lastViewedAt INTEGER,
                        userNote TEXT,
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        lastSyncedAt INTEGER,
                        isDeleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_saved_wisdom_userId ON saved_wisdom(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_saved_wisdom_type ON saved_wisdom(type)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_saved_wisdom_savedAt ON saved_wisdom(savedAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_saved_wisdom_userId_type ON saved_wisdom(userId, type)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_saved_wisdom_sourceId_type ON saved_wisdom(sourceId, type)")

                // MicroEntryEntity - Micro-journaling
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS micro_entries (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        content TEXT NOT NULL,
                        mood TEXT,
                        moodIntensity INTEGER,
                        createdAt INTEGER NOT NULL DEFAULT 0,
                        expandedToEntryId INTEGER,
                        expandedAt INTEGER,
                        captureContext TEXT,
                        locationContext TEXT,
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        lastSyncedAt INTEGER,
                        isDeleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_micro_entries_userId ON micro_entries(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_micro_entries_createdAt ON micro_entries(createdAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_micro_entries_userId_createdAt ON micro_entries(userId, createdAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_micro_entries_expandedToEntryId ON micro_entries(expandedToEntryId)")

                // WeeklyDigestEntity - Weekly summaries
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS weekly_digests (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        weekStartDate INTEGER NOT NULL,
                        weekEndDate INTEGER NOT NULL,
                        entriesCount INTEGER NOT NULL DEFAULT 0,
                        microEntriesCount INTEGER NOT NULL DEFAULT 0,
                        totalWordsWritten INTEGER NOT NULL DEFAULT 0,
                        activeDays INTEGER NOT NULL DEFAULT 0,
                        averageWordsPerEntry INTEGER NOT NULL DEFAULT 0,
                        dominantMood TEXT,
                        moodTrend TEXT NOT NULL DEFAULT 'stable',
                        moodDistribution TEXT NOT NULL DEFAULT '',
                        topThemes TEXT NOT NULL DEFAULT '',
                        recurringPatterns TEXT NOT NULL DEFAULT '',
                        buddhaReflection TEXT,
                        entriesChangePercent INTEGER NOT NULL DEFAULT 0,
                        wordsChangePercent INTEGER NOT NULL DEFAULT 0,
                        previousWeekEntriesCount INTEGER NOT NULL DEFAULT 0,
                        previousWeekWordsWritten INTEGER NOT NULL DEFAULT 0,
                        highlightEntryId INTEGER,
                        highlightQuote TEXT,
                        isRead INTEGER NOT NULL DEFAULT 0,
                        readAt INTEGER,
                        generatedAt INTEGER NOT NULL DEFAULT 0,
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        lastSyncedAt INTEGER,
                        isDeleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_weekly_digests_userId ON weekly_digests(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_weekly_digests_weekStartDate ON weekly_digests(weekStartDate)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_weekly_digests_userId_weekStartDate ON weekly_digests(userId, weekStartDate)")

                // DailyRitualEntity - Daily ritual tracking
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS daily_rituals (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        date INTEGER NOT NULL,
                        morningCompleted INTEGER NOT NULL DEFAULT 0,
                        morningCompletedAt INTEGER,
                        morningIntention TEXT,
                        morningMood TEXT,
                        morningWisdomId INTEGER,
                        eveningCompleted INTEGER NOT NULL DEFAULT 0,
                        eveningCompletedAt INTEGER,
                        eveningDayRating TEXT,
                        eveningReflection TEXT,
                        eveningMood TEXT,
                        createdAt INTEGER NOT NULL DEFAULT 0,
                        updatedAt INTEGER NOT NULL DEFAULT 0,
                        expandedToJournalId INTEGER,
                        expandedToMicroEntryId INTEGER,
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        lastSyncedAt INTEGER,
                        isDeleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_daily_rituals_userId ON daily_rituals(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_daily_rituals_date ON daily_rituals(date)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_daily_rituals_userId_date ON daily_rituals(userId, date)")

                // FutureMessageReplyEntity - Time capsule chains
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS future_message_replies (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        originalMessageId INTEGER NOT NULL,
                        replyContent TEXT NOT NULL,
                        promptShown TEXT,
                        reactionMood TEXT,
                        chainedMessageId INTEGER,
                        repliedAt INTEGER NOT NULL DEFAULT 0,
                        savedAsJournalId INTEGER,
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        lastSyncedAt INTEGER,
                        isDeleted INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY (originalMessageId) REFERENCES future_messages(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_future_message_replies_userId ON future_message_replies(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_future_message_replies_originalMessageId ON future_message_replies(originalMessageId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_future_message_replies_userId_originalMessageId ON future_message_replies(userId, originalMessageId)")

                // WritingCompanionSuggestionEntity - AI Writing Companion tracking
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS writing_suggestions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'local',
                        journalEntryId INTEGER,
                        suggestionType TEXT NOT NULL,
                        suggestionText TEXT NOT NULL,
                        triggerContext TEXT NOT NULL,
                        timeOfDay TEXT,
                        currentMood TEXT,
                        recentThemes TEXT,
                        wasAccepted INTEGER NOT NULL DEFAULT 0,
                        wasDismissed INTEGER NOT NULL DEFAULT 0,
                        acceptedAt INTEGER,
                        dismissedAt INTEGER,
                        shownAt INTEGER NOT NULL DEFAULT 0,
                        syncStatus TEXT NOT NULL DEFAULT 'pending',
                        isDeleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_writing_suggestions_userId ON writing_suggestions(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_writing_suggestions_journalEntryId ON writing_suggestions(journalEntryId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_writing_suggestions_suggestionType ON writing_suggestions(suggestionType)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_writing_suggestions_shownAt ON writing_suggestions(shownAt)")
            }
        }

        fun getInstance(context: Context): ProdyDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): ProdyDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ProdyDatabase::class.java,
                DATABASE_NAME
            )
                .addMigrations(MIGRATION_4_5, MIGRATION_5_6)
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback())
                .build()
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
    }
}
