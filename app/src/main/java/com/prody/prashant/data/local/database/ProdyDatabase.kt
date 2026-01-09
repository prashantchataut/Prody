package com.prody.prashant.data.local.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
        WeeklyTrialEntity::class
    ],
    version = 5,
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
                .addMigrations(MIGRATION_4_5)
                .fallbackToDestructiveMigration(prefix = "v5_to_v6")
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
