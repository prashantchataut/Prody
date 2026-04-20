package com.prody.prashant.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ProdyDatabaseMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        ProdyDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    private val allMigrations = arrayOf(
        ProdyDatabase.MIGRATION_4_5,
        ProdyDatabase.MIGRATION_5_6,
        ProdyDatabase.MIGRATION_6_7,
        ProdyDatabase.MIGRATION_7_8,
        ProdyDatabase.MIGRATION_8_9,
        ProdyDatabase.MIGRATION_9_10,
        ProdyDatabase.MIGRATION_10_11,
        ProdyDatabase.MIGRATION_11_12,
        ProdyDatabase.MIGRATION_12_13,
        ProdyDatabase.MIGRATION_13_14,
        ProdyDatabase.MIGRATION_14_15,
        ProdyDatabase.MIGRATION_15_16,
        ProdyDatabase.MIGRATION_16_17,
        ProdyDatabase.MIGRATION_17_18,
        ProdyDatabase.MIGRATION_18_19,
        ProdyDatabase.MIGRATION_19_20
    )

    @Test
    fun migrate4To20_preservesJournalAndFutureMessageData() {
        val dbName = "migration-fixture-v4-to-v20.db"

        helper.createDatabase(dbName, 4).apply {
            seedVersion4Data(this)
            close()
        }

        helper.runMigrationsAndValidate(dbName, 20, true, *allMigrations).use { migratedDb ->
            assertSingleRowWithValue(
                migratedDb,
                "journal_entries",
                "content",
                "Deep journal content"
            )
            assertSingleRowWithValue(
                migratedDb,
                "future_messages",
                "content",
                "Message to my future self"
            )
        }
    }

    @Test
    fun migrate17To20_preservesHavenReplyAndStreakData() {
        val dbName = "migration-fixture-v17-to-v20.db"

        helper.createDatabase(dbName, 17).apply {
            seedVersion17SensitiveData(this)
            close()
        }

        helper.runMigrationsAndValidate(
            dbName,
            20,
            true,
            ProdyDatabase.MIGRATION_17_18,
            ProdyDatabase.MIGRATION_18_19,
            ProdyDatabase.MIGRATION_19_20
        ).use { migratedDb ->
            assertSingleRowWithValue(migratedDb, "streak_data", "currentStreak", 12)
            assertSingleRowWithValue(migratedDb, "haven_sessions", "messagesJson", "[]")
            assertSingleRowWithValue(migratedDb, "future_message_replies", "replyContent", "I made it.")
        }
    }

    @Test
    fun encryptedDatabase_openCloseMigrateAndRestore() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "encrypted-migration-test.db"
        val passphrase = "ProdyTestPassphrase-2026"
        val supportFactory = SupportFactory(SQLiteDatabase.getBytes(passphrase.toCharArray()))

        val encryptedHelper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            ProdyDatabase::class.java.canonicalName,
            supportFactory
        )

        encryptedHelper.createDatabase(dbName, 17).apply {
            seedVersion17SensitiveData(this)
            close()
        }

        encryptedHelper.runMigrationsAndValidate(
            dbName,
            20,
            true,
            ProdyDatabase.MIGRATION_17_18,
            ProdyDatabase.MIGRATION_18_19,
            ProdyDatabase.MIGRATION_19_20
        ).close()

        val roomDb = Room.databaseBuilder(context, ProdyDatabase::class.java, dbName)
            .openHelperFactory(supportFactory)
            .addMigrations(*allMigrations)
            .build()

        roomDb.openHelper.readableDatabase.query(
            "SELECT COUNT(*) FROM future_message_replies WHERE replyContent = 'I made it.'"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(1, cursor.getInt(0))
        }
        roomDb.close()

        val dbFile = context.getDatabasePath(dbName)
        val backupFile = File(context.cacheDir, "$dbName.bak")
        dbFile.copyTo(backupFile, overwrite = true)
        assertTrue(dbFile.delete())
        backupFile.copyTo(dbFile, overwrite = true)

        val restoredDb = Room.databaseBuilder(context, ProdyDatabase::class.java, dbName)
            .openHelperFactory(supportFactory)
            .addMigrations(*allMigrations)
            .build()

        restoredDb.openHelper.readableDatabase.query(
            "SELECT COUNT(*) FROM haven_sessions WHERE userId = 'user_sensitive'"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(1, cursor.getInt(0))
        }
        restoredDb.close()
    }

    private fun seedVersion4Data(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            INSERT INTO journal_entries (
                userId, title, content, mood, moodIntensity, buddhaResponse, tags,
                isBookmarked, wordCount, createdAt, updatedAt, aiEmotionLabel, aiThemes,
                aiInsight, aiSummary, aiInsightGenerated, aiSnippet, aiQuestion,
                aiSuggestion, aiContentHash, attachedPhotos, attachedVideos,
                voiceRecordingUri, voiceRecordingDuration, templateId, syncStatus,
                lastSyncedAt, serverVersion, isDeleted
            ) VALUES (
                'user_1', 'Migration Test Journal', 'Deep journal content', 'peaceful', 4, NULL,
                '[]', 0, 120, 1710000000, 1710000000, NULL, NULL, NULL, NULL, 0, NULL,
                NULL, NULL, NULL, '[]', '[]', NULL, 0, NULL, 'pending', NULL, 1, 0
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO future_messages (
                userId, title, content, deliveryDate, isDelivered, isRead, category,
                attachedGoal, createdAt, deliveredAt, attachedPhotos, attachedVideos,
                voiceRecordingUri, voiceRecordingDuration, syncStatus, lastSyncedAt,
                serverVersion, isDeleted
            ) VALUES (
                'user_1', 'Future Note', 'Message to my future self', 1730000000, 0, 0,
                'reflection', NULL, 1710000000, NULL, '[]', '[]', NULL, 0,
                'pending', NULL, 1, 0
            )
            """.trimIndent()
        )
    }

    private fun seedVersion17SensitiveData(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            INSERT INTO streak_data (
                userId, currentStreak, longestStreak, lastActiveDate, freezesAvailable,
                freezesUsedThisMonth, lastFreezeResetMonth, totalDaysActive,
                streakBrokenCount, createdAt, updatedAt
            ) VALUES (
                'user_sensitive', 12, 20, 1710000000, 2,
                0, 202401, 44,
                1, 1710000000, 1710000000
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO haven_sessions (
                userId, sessionType, startedAt, endedAt, messagesJson, techniquesUsedJson,
                moodBefore, moodAfter, isCompleted, userRating, keyInsightsJson,
                suggestedExercisesJson, followUpScheduled, containedCrisisDetection,
                syncStatus, lastSyncedAt, isDeleted
            ) VALUES (
                'user_sensitive', 'check_in', 1710000000, 1710000300, '[]', '[]',
                2, 4, 1, 5, '[]',
                '[]', 0, 0,
                'pending', NULL, 0
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO future_messages (
                userId, title, content, deliveryDate, isDelivered, isRead, category,
                attachedGoal, createdAt, deliveredAt, attachedPhotos, attachedVideos,
                voiceRecordingUri, voiceRecordingDuration, isFavorite, replyJournalEntryId,
                readAt, syncStatus, lastSyncedAt, serverVersion, isDeleted
            ) VALUES (
                'user_sensitive', 'Future Anchor', 'Protected payload', 1730000000, 0, 0,
                'personal', NULL, 1710000000, NULL, '[]', '[]', NULL, 0, 0,
                NULL, NULL, 'pending', NULL, 1, 0
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO future_message_replies (
                userId, originalMessageId, replyContent, promptShown, reactionMood,
                chainedMessageId, repliedAt, savedAsJournalId, syncStatus,
                lastSyncedAt, isDeleted
            ) VALUES (
                'user_sensitive', 1, 'I made it.', 'How did this feel?', 'grateful',
                NULL, 1710000400, NULL, 'pending',
                NULL, 0
            )
            """.trimIndent()
        )
    }

    private fun assertSingleRowWithValue(
        db: SupportSQLiteDatabase,
        table: String,
        column: String,
        expected: Any
    ) {
        db.query("SELECT $column FROM $table LIMIT 1").use { cursor ->
            assertTrue("Expected at least one row in $table", cursor.moveToFirst())
            when (expected) {
                is Int -> assertEquals(expected, cursor.getInt(0))
                is Long -> assertEquals(expected, cursor.getLong(0))
                is String -> assertEquals(expected, cursor.getString(0))
                else -> throw IllegalArgumentException("Unsupported expected type: ${expected::class.java}")
            }
        }
    }
}
