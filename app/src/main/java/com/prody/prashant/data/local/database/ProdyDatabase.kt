package com.prody.prashant.data.local.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.prody.prashant.data.local.dao.*
import com.prody.prashant.data.local.entity.*

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
        ChallengeLeaderboardEntity::class
    ],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 2, to = 3)
    ]
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

    companion object {
        const val DATABASE_NAME = "prody_database"

        /**
         * Migration from version 3 to 4.
         * Adds new columns to user_profile and achievements tables for identity system.
         */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add celebrationMessage column to achievements table
                try {
                    database.execSQL(
                        "ALTER TABLE achievements ADD COLUMN celebrationMessage TEXT NOT NULL DEFAULT ''"
                    )
                } catch (e: Exception) {
                    // Column might already exist
                }

                // Add new columns to user_profile table
                try {
                    database.execSQL(
                        "ALTER TABLE user_profile ADD COLUMN futureLettersSent INTEGER NOT NULL DEFAULT 0"
                    )
                } catch (e: Exception) {
                    // Column might already exist
                }

                try {
                    database.execSQL(
                        "ALTER TABLE user_profile ADD COLUMN futureLettersReceived INTEGER NOT NULL DEFAULT 0"
                    )
                } catch (e: Exception) {
                    // Column might already exist
                }

                try {
                    database.execSQL(
                        "ALTER TABLE user_profile ADD COLUMN buddhaConversations INTEGER NOT NULL DEFAULT 0"
                    )
                } catch (e: Exception) {
                    // Column might already exist
                }

                try {
                    database.execSQL(
                        "ALTER TABLE user_profile ADD COLUMN preferences TEXT NOT NULL DEFAULT '{}'"
                    )
                } catch (e: Exception) {
                    // Column might already exist
                }

                // Update default values for existing columns
                try {
                    database.execSQL(
                        "UPDATE user_profile SET bannerId = 'default_dawn' WHERE bannerId = 'default'"
                    )
                } catch (e: Exception) {
                    // Update might fail if no rows match
                }

                try {
                    database.execSQL(
                        "UPDATE user_profile SET titleId = 'seeker' WHERE titleId = 'newcomer'"
                    )
                } catch (e: Exception) {
                    // Update might fail if no rows match
                }
            }
        }
    }
}
