package com.prody.prashant.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database Migration from Version 13 to Version 14
 *
 * Adds Personalized Learning Paths feature with 7 new tables:
 * - learning_paths
 * - learning_lessons
 * - learning_reflections
 * - path_recommendations
 * - path_progress_checkpoints
 * - learning_notes
 * - path_badges
 *
 * INSTRUCTIONS:
 * 1. Copy this file to: app/src/main/java/com/prody/prashant/data/local/database/
 * 2. In ProdyDatabase.kt companion object, add this migration
 * 3. In buildDatabase(), add to .addMigrations(..., MIGRATION_13_14)
 */
val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) {

        // ==================== LEARNING PATHS ====================
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

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_paths_user ON learning_paths(userId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_paths_type ON learning_paths(pathType)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_paths_user_type ON learning_paths(userId, pathType)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_paths_active ON learning_paths(isActive)")

        // ==================== LEARNING LESSONS ====================
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

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_lessons_path ON learning_lessons(pathId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_lessons_path_order ON learning_lessons(pathId, orderIndex)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_lessons_completed ON learning_lessons(isCompleted)")

        // ==================== LEARNING REFLECTIONS ====================
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

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_reflections_lesson ON learning_reflections(lessonId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_reflections_path ON learning_reflections(pathId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_reflections_user ON learning_reflections(userId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_reflections_user_created ON learning_reflections(userId, createdAt)")

        // ==================== PATH RECOMMENDATIONS ====================
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

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_recommendations_user ON path_recommendations(userId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_recommendations_type ON path_recommendations(pathType)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_recommendations_user_created ON path_recommendations(userId, createdAt)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_recommendations_dismissed ON path_recommendations(isDismissed)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_recommendations_accepted ON path_recommendations(isAccepted)")

        // ==================== PATH PROGRESS CHECKPOINTS ====================
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

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_checkpoints_path ON path_progress_checkpoints(pathId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_checkpoints_user ON path_progress_checkpoints(userId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_checkpoints_user_path ON path_progress_checkpoints(userId, pathId)")

        // ==================== LEARNING NOTES ====================
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

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_notes_lesson ON learning_notes(lessonId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_notes_path ON learning_notes(pathId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_notes_user ON learning_notes(userId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_notes_user_created ON learning_notes(userId, createdAt)")

        // ==================== PATH BADGES ====================
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

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_badges_path ON path_badges(pathId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_badges_user ON path_badges(userId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_badges_user_earned ON path_badges(userId, earnedAt)")
    }
}
