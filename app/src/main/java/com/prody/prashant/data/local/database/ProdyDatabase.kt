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
        // Collaborative Future Messages
        CollaborativeMessageEntity::class,
        ReceivedCollaborativeMessageEntity::class,
        MessageContactEntity::class,
        MessageOccasionEntity::class,
        // Haven Personal Therapist
        HavenSessionEntity::class,
        HavenExerciseEntity::class,
        // Deep Dive Days
        DeepDiveEntity::class,
        // Social Accountability Circles
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
    exportSchema = true
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
    abstract fun savedWisdomDao(): SavedWisdomDao
    abstract fun microEntryDao(): MicroEntryDao
    abstract fun weeklyDigestDao(): WeeklyDigestDao
    abstract fun dailyRitualDao(): DailyRitualDao
    abstract fun futureMessageReplyDao(): FutureMessageReplyDao
    abstract fun messageAnniversaryDao(): MessageAnniversaryDao
    abstract fun dualStreakDao(): DualStreakDao
    abstract fun wordUsageDao(): WordUsageDao
    abstract fun monthlyLetterDao(): MonthlyLetterDao
    abstract fun havenDao(): HavenDao
    abstract fun yearlyWrappedDao(): YearlyWrappedDao
    abstract fun collaborativeMessageDao(): CollaborativeMessageDao
    abstract fun learningPathDao(): LearningPathDao
    abstract fun deepDiveDao(): DeepDiveDao
    abstract fun socialDao(): SocialDao
    abstract fun soulLayerDao(): SoulLayerDao
    abstract fun havenMemoryDao(): HavenMemoryDao
    abstract fun evidenceDao(): EvidenceDao

    companion object {
        private const val TAG = "ProdyDatabase"
        const val DATABASE_NAME = "prody_database"

        @Volatile
        private var INSTANCE: ProdyDatabase? = null

        val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS player_skills (id INTEGER PRIMARY KEY AUTOINCREMENT, userId TEXT UNIQUE, clarityXp INTEGER NOT NULL DEFAULT 0, disciplineXp INTEGER NOT NULL DEFAULT 0, courageXp INTEGER NOT NULL DEFAULT 0, dailyClarityXp INTEGER NOT NULL DEFAULT 0, dailyDisciplineXp INTEGER NOT NULL DEFAULT 0, dailyCourageXp INTEGER NOT NULL DEFAULT 0, dailyResetDate INTEGER NOT NULL DEFAULT 0, tokens INTEGER NOT NULL DEFAULT 0, createdAt INTEGER NOT NULL DEFAULT 0, updatedAt INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS processed_rewards (id INTEGER PRIMARY KEY AUTOINCREMENT, rewardKey TEXT UNIQUE, processedAt INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS daily_missions (id INTEGER PRIMARY KEY AUTOINCREMENT, userId TEXT NOT NULL, missionType TEXT NOT NULL, targetCount INTEGER NOT NULL DEFAULT 0, currentProgress INTEGER NOT NULL DEFAULT 0, xpReward INTEGER NOT NULL DEFAULT 0, isCompleted INTEGER NOT NULL DEFAULT 0, completedAt INTEGER, createdAt INTEGER NOT NULL DEFAULT 0, updatedAt INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS weekly_trials (id INTEGER PRIMARY KEY AUTOINCREMENT, userId TEXT NOT NULL, trialType TEXT NOT NULL, description TEXT NOT NULL, targetCount INTEGER NOT NULL DEFAULT 0, currentProgress INTEGER NOT NULL DEFAULT 0, xpReward INTEGER NOT NULL DEFAULT 0, isCompleted INTEGER NOT NULL DEFAULT 0, completedAt INTEGER, startedAt INTEGER NOT NULL DEFAULT 0, endsAt INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_daily_missions_user ON daily_missions(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_weekly_trials_user ON weekly_trials(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_processed_rewards_key ON processed_rewards(rewardKey)")
            }
        }

        val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE player_skills ADD COLUMN weeklyClarityXp INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE player_skills ADD COLUMN weeklyDisciplineXp INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE player_skills ADD COLUMN weeklyCourageXp INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE player_skills ADD COLUMN weeklyResetDate INTEGER NOT NULL DEFAULT 0")
                db.execSQL("CREATE TABLE IF NOT EXISTS streak_data (id INTEGER PRIMARY KEY AUTOINCREMENT, userId TEXT NOT NULL, currentStreak INTEGER NOT NULL DEFAULT 0, longestStreak INTEGER NOT NULL DEFAULT 0, lastActiveDate INTEGER NOT NULL DEFAULT 0, freezesAvailable INTEGER NOT NULL DEFAULT 2, freezesUsedThisMonth INTEGER NOT NULL DEFAULT 0, lastFreezeResetMonth INTEGER NOT NULL DEFAULT 0, totalDaysActive INTEGER NOT NULL DEFAULT 0, streakBrokenCount INTEGER NOT NULL DEFAULT 0, createdAt INTEGER NOT NULL DEFAULT 0, updatedAt INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_streak_data_user ON streak_data(userId)")
                db.execSQL("CREATE TABLE IF NOT EXISTS daily_activity (id INTEGER PRIMARY KEY AUTOINCREMENT, userId TEXT NOT NULL DEFAULT 'local', date INTEGER NOT NULL, hasJournalEntry INTEGER NOT NULL DEFAULT 0, hasMicroEntry INTEGER NOT NULL DEFAULT 0, hasBloom INTEGER NOT NULL DEFAULT 0, hasFutureMessage INTEGER NOT NULL DEFAULT 0, hasFlashcardSession INTEGER NOT NULL DEFAULT 0, hasWisdomEngagement INTEGER NOT NULL DEFAULT 0, clarityXpEarned INTEGER NOT NULL DEFAULT 0, disciplineXpEarned INTEGER NOT NULL DEFAULT 0, courageXpEarned INTEGER NOT NULL DEFAULT 0, streakDayNumber INTEGER NOT NULL DEFAULT 0, usedMindfulBreak INTEGER NOT NULL DEFAULT 0, createdAt INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_daily_activity_user_date ON daily_activity(userId, date)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_daily_activity_date ON daily_activity(date)")
                db.execSQL("CREATE TABLE IF NOT EXISTS mindful_break_usage (id INTEGER PRIMARY KEY AUTOINCREMENT, userId TEXT NOT NULL DEFAULT 'local', usedAt INTEGER NOT NULL DEFAULT 0, preservedStreak INTEGER NOT NULL DEFAULT 0, missedDate INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_mindful_break_user ON mindful_break_usage(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_mindful_break_time ON mindful_break_usage(usedAt)")
                db.execSQL("ALTER TABLE daily_seeds ADD COLUMN state TEXT NOT NULL DEFAULT 'planted'")
                db.execSQL("ALTER TABLE daily_seeds ADD COLUMN rewardXp INTEGER NOT NULL DEFAULT 15")
                db.execSQL("ALTER TABLE daily_seeds ADD COLUMN rewardTokens INTEGER NOT NULL DEFAULT 5")
                db.execSQL("ALTER TABLE daily_seeds ADD COLUMN variations TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE daily_seeds ADD COLUMN keywords TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE daily_seeds ADD COLUMN keyPhrase TEXT")
            }
        }

        val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE future_messages ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE future_messages ADD COLUMN replyJournalEntryId INTEGER")
                db.execSQL("ALTER TABLE future_messages ADD COLUMN readAt INTEGER")
                db.execSQL("CREATE TABLE IF NOT EXISTS message_anniversaries (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', originalMessageId INTEGER NOT NULL, yearsAgo INTEGER NOT NULL, originalContent TEXT NOT NULL, category TEXT NOT NULL, originalCreatedAt INTEGER NOT NULL, anniversaryDate INTEGER NOT NULL, notifiedAt INTEGER, isRead INTEGER NOT NULL DEFAULT 0, readAt INTEGER, hasReflection INTEGER NOT NULL DEFAULT 0, reflectionJournalId INTEGER, createdAt INTEGER NOT NULL, isDeleted INTEGER NOT NULL DEFAULT 0, FOREIGN KEY(originalMessageId) REFERENCES future_messages(id) ON DELETE CASCADE)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_message_anniversaries_user ON message_anniversaries(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_message_anniversaries_original_message ON message_anniversaries(originalMessageId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_message_anniversaries_anniversary_date ON message_anniversaries(anniversaryDate)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_message_anniversaries_user_date ON message_anniversaries(userId, anniversaryDate)")
            }
        }

        val MIGRATION_7_8: Migration = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS dual_streaks (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', wisdomStreakCurrent INTEGER NOT NULL DEFAULT 0, wisdomStreakLongest INTEGER NOT NULL DEFAULT 0, wisdomLastMaintainedDate INTEGER NOT NULL DEFAULT 0, wisdomGracePeriodUsed INTEGER NOT NULL DEFAULT 0, wisdomGracePeriodUsedDate INTEGER NOT NULL DEFAULT 0, wisdomGracePeriodResetDate INTEGER NOT NULL DEFAULT 0, reflectionStreakCurrent INTEGER NOT NULL DEFAULT 0, reflectionStreakLongest INTEGER NOT NULL DEFAULT 0, reflectionLastMaintainedDate INTEGER NOT NULL DEFAULT 0, reflectionGracePeriodUsed INTEGER NOT NULL DEFAULT 0, reflectionGracePeriodUsedDate INTEGER NOT NULL DEFAULT 0, reflectionGracePeriodResetDate INTEGER NOT NULL DEFAULT 0, createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_dual_streaks_user ON dual_streaks(userId)")
            }
        }

        val MIGRATION_8_9: Migration = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS word_usages (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', wordId INTEGER NOT NULL, journalEntryId INTEGER NOT NULL, usedInSentence TEXT NOT NULL, matchedForm TEXT NOT NULL, positionStart INTEGER NOT NULL, positionEnd INTEGER NOT NULL, detectedAt INTEGER NOT NULL, celebrated INTEGER NOT NULL DEFAULT 0, celebratedAt INTEGER, bonusPointsAwarded INTEGER NOT NULL DEFAULT 0, pointsClaimed INTEGER NOT NULL DEFAULT 0, FOREIGN KEY(wordId) REFERENCES vocabulary(id) ON DELETE CASCADE, FOREIGN KEY(journalEntryId) REFERENCES journal_entries(id) ON DELETE CASCADE)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_word_usages_user ON word_usages(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_word_usages_word ON word_usages(wordId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_word_usages_journal ON word_usages(journalEntryId)")
                db.execSQL("ALTER TABLE vocabulary_learning ADD COLUMN usedInContext INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE vocabulary_learning ADD COLUMN lastUsedAt INTEGER")
                db.execSQL("ALTER TABLE vocabulary_learning ADD COLUMN timesUsed INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_9_10: Migration = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS monthly_letters (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', month INTEGER NOT NULL, year INTEGER NOT NULL, greeting TEXT NOT NULL, entriesCount INTEGER NOT NULL DEFAULT 0, microEntriesCount INTEGER NOT NULL DEFAULT 0, totalWords INTEGER NOT NULL DEFAULT 0, activeDays INTEGER NOT NULL DEFAULT 0, averageWordsPerEntry INTEGER NOT NULL DEFAULT 0, mostActiveWeek TEXT, topThemes TEXT NOT NULL DEFAULT '', themesAnalysis TEXT NOT NULL DEFAULT '', recurringWords TEXT NOT NULL DEFAULT '', moodJourney TEXT NOT NULL DEFAULT '', dominantMood TEXT, moodAnalysis TEXT NOT NULL DEFAULT '', moodTrend TEXT NOT NULL DEFAULT 'stable', patternObservation TEXT NOT NULL DEFAULT '', buddhaWisdom TEXT, achievedMilestones TEXT NOT NULL DEFAULT '', upcomingMilestones TEXT NOT NULL DEFAULT '', streakInfo TEXT, entriesChangePercent INTEGER NOT NULL DEFAULT 0, wordsChangePercent INTEGER NOT NULL DEFAULT 0, comparisonNote TEXT, highlightEntryId INTEGER, highlightQuote TEXT, highlightReason TEXT, closingMessage TEXT NOT NULL DEFAULT '', encouragementNote TEXT, generatedAt INTEGER NOT NULL, isRead INTEGER NOT NULL DEFAULT 0, readAt INTEGER, isFavorite INTEGER NOT NULL DEFAULT 0, sharedAt INTEGER, syncStatus TEXT NOT NULL DEFAULT 'pending', lastSyncedAt INTEGER, isDeleted INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_monthly_letters_user_month_year ON monthly_letters(userId, month, year)")
            }
        }

        val MIGRATION_10_11: Migration = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS yearly_wrapped (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', year INTEGER NOT NULL, generatedAt INTEGER NOT NULL, isViewed INTEGER NOT NULL DEFAULT 0, viewedAt INTEGER, totalJournalEntries INTEGER NOT NULL DEFAULT 0, totalMicroEntries INTEGER NOT NULL DEFAULT 0, totalWordsWritten INTEGER NOT NULL DEFAULT 0, averageWordsPerEntry INTEGER NOT NULL DEFAULT 0, longestEntry INTEGER NOT NULL DEFAULT 0, longestEntryId INTEGER, activeDaysCount INTEGER NOT NULL DEFAULT 0, longestStreak INTEGER NOT NULL DEFAULT 0, totalMeditationMinutes INTEGER NOT NULL DEFAULT 0, bloomsCompleted INTEGER NOT NULL DEFAULT 0, vocabularyWordsLearned INTEGER NOT NULL DEFAULT 0, vocabularyWordsUsed INTEGER NOT NULL DEFAULT 0, idiomsExplored INTEGER NOT NULL DEFAULT 0, proverbsDiscovered INTEGER NOT NULL DEFAULT 0, futureMessagesWritten INTEGER NOT NULL DEFAULT 0, futureMessagesReceived INTEGER NOT NULL DEFAULT 0, mostDistantMessage INTEGER NOT NULL DEFAULT 0, mostActiveMonth INTEGER, mostActiveDay TEXT, mostActiveTimeOfDay TEXT, firstEntryDate INTEGER, lastEntryDate INTEGER, averageMood REAL NOT NULL DEFAULT 0, moodTrend TEXT NOT NULL DEFAULT 'stable', mostCommonMood TEXT, moodVariety INTEGER NOT NULL DEFAULT 0, brightestMonth INTEGER, mostReflectiveMonth INTEGER, moodEvolution TEXT NOT NULL DEFAULT '[]', topThemesJson TEXT NOT NULL DEFAULT '[]', growthAreasJson TEXT NOT NULL DEFAULT '[]', challengesOvercomeJson TEXT NOT NULL DEFAULT '[]', keyMomentsJson TEXT NOT NULL DEFAULT '[]', patternsJson TEXT NOT NULL DEFAULT '[]', openingNarrative TEXT, yearSummaryNarrative TEXT, growthStoryNarrative TEXT, moodJourneyNarrative TEXT, lookingAheadNarrative TEXT, milestoneNarrative TEXT, shareableCardsJson TEXT NOT NULL DEFAULT '[]', isShared INTEGER NOT NULL DEFAULT 0, sharedAt INTEGER, isFavorite INTEGER NOT NULL DEFAULT 0, viewCompletionPercent INTEGER NOT NULL DEFAULT 0, slidesViewed TEXT NOT NULL DEFAULT '[]', syncStatus TEXT NOT NULL DEFAULT 'pending', lastSyncedAt INTEGER, isDeleted INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_yearly_wrapped_user_year ON yearly_wrapped(userId, year)")
            }
        }

        val MIGRATION_11_12: Migration = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS collaborative_messages (id TEXT PRIMARY KEY NOT NULL, userId TEXT NOT NULL DEFAULT 'local', recipientId TEXT, recipientContact TEXT, recipientName TEXT NOT NULL, title TEXT NOT NULL, content TEXT NOT NULL, deliveryDate INTEGER NOT NULL, occasion TEXT, isDelivered INTEGER NOT NULL DEFAULT 0, isRead INTEGER NOT NULL DEFAULT 0, createdAt INTEGER NOT NULL DEFAULT 0, deliveredAt INTEGER, readAt INTEGER, attachedPhotosJson TEXT NOT NULL DEFAULT '[]', voiceRecordingUri TEXT, voiceRecordingDuration INTEGER NOT NULL DEFAULT 0, cardTheme TEXT NOT NULL DEFAULT 'default', cardBackgroundColor TEXT, status TEXT NOT NULL DEFAULT 'pending', deliveryMethod TEXT NOT NULL DEFAULT 'in_app', retryCount INTEGER NOT NULL DEFAULT 0, syncStatus TEXT NOT NULL DEFAULT 'pending', lastSyncedAt INTEGER, isDeleted INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS received_collaborative_messages (id TEXT PRIMARY KEY NOT NULL, senderId TEXT NOT NULL, senderName TEXT NOT NULL, title TEXT NOT NULL, content TEXT NOT NULL, deliveredAt INTEGER NOT NULL, isRead INTEGER NOT NULL DEFAULT 0, readAt INTEGER, attachedPhotosJson TEXT NOT NULL DEFAULT '[]', voiceRecordingUri TEXT, voiceRecordingDuration INTEGER NOT NULL DEFAULT 0, cardTheme TEXT NOT NULL, occasion TEXT, isFavorite INTEGER NOT NULL DEFAULT 0, replyMessageId TEXT, syncStatus TEXT NOT NULL DEFAULT 'pending', lastSyncedAt INTEGER, isDeleted INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS message_contacts (id TEXT PRIMARY KEY NOT NULL, userId TEXT NOT NULL DEFAULT 'local', displayName TEXT NOT NULL, contactMethod TEXT NOT NULL, contactValue TEXT NOT NULL, avatarUrl TEXT, messagesSent INTEGER NOT NULL DEFAULT 0, lastMessageAt INTEGER, isFavorite INTEGER NOT NULL DEFAULT 0, syncStatus TEXT NOT NULL DEFAULT 'pending', lastSyncedAt INTEGER, createdAt INTEGER NOT NULL DEFAULT 0, isDeleted INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS message_occasions (id TEXT PRIMARY KEY NOT NULL, userId TEXT NOT NULL DEFAULT 'local', contactId TEXT NOT NULL, occasionType TEXT NOT NULL, date INTEGER NOT NULL, isRecurring INTEGER NOT NULL DEFAULT 1, reminderDaysBefore INTEGER NOT NULL DEFAULT 7, lastNotifiedYear INTEGER, syncStatus TEXT NOT NULL DEFAULT 'pending', lastSyncedAt INTEGER, createdAt INTEGER NOT NULL DEFAULT 0, isDeleted INTEGER NOT NULL DEFAULT 0)")
            }
        }

        val MIGRATION_12_13: Migration = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS haven_sessions (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', sessionType TEXT NOT NULL, startedAt INTEGER NOT NULL, endedAt INTEGER, messagesJson TEXT NOT NULL DEFAULT '[]', techniquesUsedJson TEXT NOT NULL DEFAULT '[]', moodBefore INTEGER, moodAfter INTEGER, isCompleted INTEGER NOT NULL DEFAULT 0, userRating INTEGER, keyInsightsJson TEXT, suggestedExercisesJson TEXT, followUpScheduled INTEGER, containedCrisisDetection INTEGER NOT NULL DEFAULT 0, syncStatus TEXT NOT NULL DEFAULT 'pending', lastSyncedAt INTEGER, isDeleted INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS haven_exercises (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', exerciseType TEXT NOT NULL, completedAt INTEGER NOT NULL, durationSeconds INTEGER NOT NULL DEFAULT 0, notes TEXT, exerciseDataJson TEXT, fromSessionId INTEGER, wasCompleted INTEGER NOT NULL DEFAULT 1, completionRate REAL NOT NULL DEFAULT 1.0, helpfulness INTEGER, syncStatus TEXT NOT NULL DEFAULT 'pending', isDeleted INTEGER NOT NULL DEFAULT 0)")
            }
        }

        val MIGRATION_13_14: Migration = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS learning_paths (id TEXT PRIMARY KEY NOT NULL, userId TEXT NOT NULL DEFAULT 'local', pathType TEXT NOT NULL, title TEXT NOT NULL, description TEXT NOT NULL, totalLessons INTEGER NOT NULL, completedLessons INTEGER NOT NULL DEFAULT 0, currentLessonId TEXT, startedAt INTEGER NOT NULL, lastAccessedAt INTEGER NOT NULL, completedAt INTEGER, isActive INTEGER NOT NULL DEFAULT 1, progressPercentage REAL NOT NULL DEFAULT 0, estimatedMinutesTotal INTEGER NOT NULL, difficultyLevel TEXT NOT NULL DEFAULT 'beginner', iconEmoji TEXT NOT NULL DEFAULT '📚', colorTheme TEXT NOT NULL DEFAULT '#6366F1')")
                db.execSQL("CREATE TABLE IF NOT EXISTS learning_lessons (id TEXT PRIMARY KEY NOT NULL, pathId TEXT NOT NULL, orderIndex INTEGER NOT NULL, title TEXT NOT NULL, lessonType TEXT NOT NULL, contentJson TEXT NOT NULL, estimatedMinutes INTEGER NOT NULL, isCompleted INTEGER NOT NULL DEFAULT 0, completedAt INTEGER, userNotesJson TEXT, quizScore INTEGER, unlockRequirement TEXT, isLocked INTEGER NOT NULL DEFAULT 1)")
                db.execSQL("CREATE TABLE IF NOT EXISTS learning_reflections (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, lessonId TEXT NOT NULL, pathId TEXT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', promptText TEXT NOT NULL, userResponse TEXT NOT NULL, aiInsight TEXT, createdAt INTEGER NOT NULL, wordCount INTEGER NOT NULL DEFAULT 0, mood TEXT, isBookmarked INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS path_recommendations (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', pathType TEXT NOT NULL, reason TEXT NOT NULL, confidenceScore REAL NOT NULL, basedOnEntriesJson TEXT NOT NULL DEFAULT '[]', basedOnPatternsJson TEXT NOT NULL DEFAULT '[]', createdAt INTEGER NOT NULL, isDismissed INTEGER NOT NULL DEFAULT 0, isAccepted INTEGER NOT NULL DEFAULT 0, dismissedAt INTEGER, acceptedAt INTEGER)")
                db.execSQL("CREATE TABLE IF NOT EXISTS path_progress_checkpoints (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, pathId TEXT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', checkpointType TEXT NOT NULL, lessonId TEXT, description TEXT NOT NULL, xpEarned INTEGER NOT NULL DEFAULT 0, tokensEarned INTEGER NOT NULL DEFAULT 0, achievedAt INTEGER NOT NULL, celebrationShown INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS learning_notes (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, lessonId TEXT NOT NULL, pathId TEXT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', noteContent TEXT NOT NULL, highlightedText TEXT, noteColor TEXT NOT NULL DEFAULT '#FFF59D', createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS path_badges (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, pathId TEXT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', badgeType TEXT NOT NULL, badgeName TEXT NOT NULL, badgeDescription TEXT NOT NULL, badgeIcon TEXT NOT NULL, earnedAt INTEGER NOT NULL, isDisplayed INTEGER NOT NULL DEFAULT 1, rarity TEXT NOT NULL DEFAULT 'common')")
            }
        }

        val MIGRATION_14_15: Migration = object : Migration(14, 15) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS deep_dives (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', theme TEXT NOT NULL, scheduledDate INTEGER NOT NULL, isCompleted INTEGER NOT NULL DEFAULT 0, completedAt INTEGER, openingReflection TEXT, coreResponse TEXT, keyInsight TEXT, commitmentStatement TEXT, moodBefore INTEGER, moodAfter INTEGER, aiThemeContext TEXT, aiPrompts TEXT, aiReflectionSummary TEXT, aiFollowUpSuggestions TEXT, durationMinutes INTEGER NOT NULL DEFAULT 0, sessionStartedAt INTEGER, currentStep TEXT NOT NULL DEFAULT 'not_started', promptVariation INTEGER NOT NULL DEFAULT 0, isScheduledNotificationSent INTEGER NOT NULL DEFAULT 0, reminderSentAt INTEGER, createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL, syncStatus TEXT NOT NULL DEFAULT 'pending', lastSyncedAt INTEGER, isDeleted INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS accountability_circles (id TEXT PRIMARY KEY NOT NULL, name TEXT NOT NULL, description TEXT, createdBy TEXT NOT NULL, createdAt INTEGER NOT NULL, inviteCode TEXT NOT NULL, isActive INTEGER NOT NULL DEFAULT 1, memberCount INTEGER NOT NULL DEFAULT 1, colorTheme TEXT NOT NULL DEFAULT 'default', iconEmoji TEXT NOT NULL DEFAULT '🌟', allowNudges INTEGER NOT NULL DEFAULT 1, allowChallenges INTEGER NOT NULL DEFAULT 1, maxMembers INTEGER NOT NULL DEFAULT 10, lastActivityAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS circle_members (id TEXT PRIMARY KEY NOT NULL, circleId TEXT NOT NULL, userId TEXT NOT NULL, displayName TEXT NOT NULL, avatarUrl TEXT, joinedAt INTEGER NOT NULL, role TEXT NOT NULL DEFAULT 'member', isActive INTEGER NOT NULL DEFAULT 1, lastActiveAt INTEGER NOT NULL, currentStreak INTEGER NOT NULL DEFAULT 0, totalEntries INTEGER NOT NULL DEFAULT 0, lastEntryAt INTEGER)")
                db.execSQL("CREATE TABLE IF NOT EXISTS circle_updates (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, circleId TEXT NOT NULL, userId TEXT NOT NULL, updateType TEXT NOT NULL, content TEXT NOT NULL, metadata TEXT, createdAt INTEGER NOT NULL, reactionsJson TEXT NOT NULL DEFAULT '{}', reactionCount INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS circle_nudges (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, circleId TEXT NOT NULL, fromUserId TEXT NOT NULL, fromDisplayName TEXT NOT NULL, toUserId TEXT NOT NULL, nudgeType TEXT NOT NULL, message TEXT, createdAt INTEGER NOT NULL, isRead INTEGER NOT NULL DEFAULT 0, respondedAt INTEGER)")
                db.execSQL("CREATE TABLE IF NOT EXISTS circle_challenges (id TEXT PRIMARY KEY NOT NULL, circleId TEXT NOT NULL, title TEXT NOT NULL, description TEXT NOT NULL, startDate INTEGER NOT NULL, endDate INTEGER NOT NULL, targetType TEXT NOT NULL, targetValue INTEGER NOT NULL, createdBy TEXT NOT NULL, createdAt INTEGER NOT NULL, participantsJson TEXT NOT NULL DEFAULT '[]', progressJson TEXT NOT NULL DEFAULT '{}', isActive INTEGER NOT NULL DEFAULT 1, completedByJson TEXT NOT NULL DEFAULT '[]')")
                db.execSQL("CREATE TABLE IF NOT EXISTS circle_privacy_settings (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', circleId TEXT NOT NULL, shareStreakCount INTEGER NOT NULL DEFAULT 1, shareEntryCount INTEGER NOT NULL DEFAULT 1, shareMeditationStats INTEGER NOT NULL DEFAULT 1, shareChallengeParticipation INTEGER NOT NULL DEFAULT 1, allowNudgesFromMembers INTEGER NOT NULL DEFAULT 1, showOnlineStatus INTEGER NOT NULL DEFAULT 0, updatedAt INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS circle_notifications (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', circleId TEXT NOT NULL, notificationType TEXT NOT NULL, title TEXT NOT NULL, message TEXT NOT NULL, actionType TEXT, actionData TEXT, isRead INTEGER NOT NULL DEFAULT 0, createdAt INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS circle_member_stats_cache (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, circleId TEXT NOT NULL, currentStreak INTEGER, longestStreak INTEGER, totalEntries INTEGER, totalWords INTEGER, meditationMinutes INTEGER, lastActiveAt INTEGER, lastUpdated INTEGER NOT NULL)")
            }
        }

        val MIGRATION_15_16: Migration = object : Migration(15, 16) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE player_skills ADD COLUMN unlockedPerkIds TEXT NOT NULL DEFAULT '[]'")
                db.execSQL("ALTER TABLE player_skills ADD COLUMN perkFreezeTokens INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE player_skills ADD COLUMN perkFreezeTokensUsed INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE achievements ADD COLUMN isHidden INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE achievements ADD COLUMN isSecret INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE achievements ADD COLUMN xpReward INTEGER NOT NULL DEFAULT 50")
            }
        }

        val MIGRATION_16_17: Migration = object : Migration(16, 17) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS surfaced_memories (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', journalEntryId INTEGER NOT NULL, surfaceReason TEXT NOT NULL, surfaceContext TEXT NOT NULL, surfacedAt INTEGER NOT NULL, wasInteractedWith INTEGER NOT NULL DEFAULT 0, interactionType TEXT, interactedAt INTEGER, memoryPreview TEXT NOT NULL, originalMood TEXT, originalDate INTEGER NOT NULL, yearsAgo INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS user_context_cache (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', userArchetype TEXT NOT NULL, trustLevel TEXT NOT NULL, engagementLevel TEXT NOT NULL, dominantMood TEXT, moodTrend TEXT NOT NULL, emotionalEnergy TEXT NOT NULL, isStruggling INTEGER NOT NULL DEFAULT 0, isThriving INTEGER NOT NULL DEFAULT 0, stressSignalsJson TEXT NOT NULL DEFAULT '[]', recentThemesJson TEXT NOT NULL DEFAULT '[]', recurringPatternsJson TEXT NOT NULL DEFAULT '[]', recentWinsJson TEXT NOT NULL DEFAULT '[]', recurringChallengesJson TEXT NOT NULL DEFAULT '[]', totalEntries INTEGER NOT NULL DEFAULT 0, daysWithPrody INTEGER NOT NULL DEFAULT 0, daysSinceLastEntry INTEGER NOT NULL DEFAULT 0, averageWordsPerEntry INTEGER NOT NULL DEFAULT 0, preferredTone TEXT NOT NULL DEFAULT 'WARM', preferredJournalTime TEXT, computedAt INTEGER NOT NULL, validUntil INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS notification_history (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', notificationType TEXT NOT NULL, decision TEXT NOT NULL, decisionReason TEXT, title TEXT, body TEXT, scheduledAt INTEGER NOT NULL, sentAt INTEGER, wasOpened INTEGER NOT NULL DEFAULT 0, openedAt INTEGER, resultedInAction INTEGER NOT NULL DEFAULT 0, actionType TEXT, actionAt INTEGER, userArchetypeAtTime TEXT, wasUserStruggling INTEGER NOT NULL DEFAULT 0, hourOfDay INTEGER NOT NULL, dayOfWeek INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS detected_patterns (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', patternType TEXT NOT NULL, patternDescription TEXT NOT NULL, confidence REAL NOT NULL, supportingEvidence TEXT NOT NULL, firstDetectedAt INTEGER NOT NULL, lastConfirmedAt INTEGER NOT NULL, occurrenceCount INTEGER NOT NULL DEFAULT 1, isActive INTEGER NOT NULL DEFAULT 1, wasShownToUser INTEGER NOT NULL DEFAULT 0, shownAt INTEGER, userFeedback TEXT, feedbackAt INTEGER, detectedAt INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS buddha_interactions (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', interactionType TEXT NOT NULL, contextMood TEXT, contextMoodIntensity INTEGER, responseWisdomStyle TEXT, responseLength INTEGER NOT NULL DEFAULT 0, wasHelpful INTEGER, helpfulnessRating INTEGER, wasExpanded INTEGER NOT NULL DEFAULT 0, wasSaved INTEGER NOT NULL DEFAULT 0, wasShared INTEGER NOT NULL DEFAULT 0, timeSpentViewingMs INTEGER, interactedAt INTEGER NOT NULL, journalEntryId INTEGER, journalWordCount INTEGER)")
                db.execSQL("CREATE TABLE IF NOT EXISTS haven_insights (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', sessionId INTEGER NOT NULL, sessionType TEXT NOT NULL, insightType TEXT NOT NULL, insightContent TEXT NOT NULL, confidence REAL NOT NULL DEFAULT 0.5, therapeuticApproachUsed TEXT, wasEffective INTEGER, moodBefore INTEGER, moodAfter INTEGER, moodImprovement INTEGER, createdAt INTEGER NOT NULL, wasUsedInFutureSession INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS temporal_content_history (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', contentType TEXT NOT NULL, contentId TEXT, contentPreview TEXT NOT NULL, timeOfDay TEXT NOT NULL, dayOfWeek INTEGER NOT NULL, seasonalContext TEXT, shownAt INTEGER NOT NULL, wasEngaged INTEGER NOT NULL DEFAULT 0, engagementType TEXT, engagedAt INTEGER)")
                db.execSQL("CREATE TABLE IF NOT EXISTS first_week_progress (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', startedAt INTEGER NOT NULL, graduatedAt INTEGER, isGraduated INTEGER NOT NULL DEFAULT 0, day1ProgressJson TEXT NOT NULL DEFAULT '{}', day2ProgressJson TEXT NOT NULL DEFAULT '{}', day3ProgressJson TEXT NOT NULL DEFAULT '{}', day4ProgressJson TEXT NOT NULL DEFAULT '{}', day5ProgressJson TEXT NOT NULL DEFAULT '{}', day6ProgressJson TEXT NOT NULL DEFAULT '{}', day7ProgressJson TEXT NOT NULL DEFAULT '{}', completedMilestonesJson TEXT NOT NULL DEFAULT '[]', celebrationsShownJson TEXT NOT NULL DEFAULT '[]', totalEntriesInFirstWeek INTEGER NOT NULL DEFAULT 0, totalWordsInFirstWeek INTEGER NOT NULL DEFAULT 0, featuresExploredJson TEXT NOT NULL DEFAULT '[]', longestStreakInFirstWeek INTEGER NOT NULL DEFAULT 0, totalXpEarned INTEGER NOT NULL DEFAULT 0, totalTokensEarned INTEGER NOT NULL DEFAULT 0, updatedAt INTEGER NOT NULL)")
            }
        }

        val MIGRATION_17_18: Migration = object : Migration(17, 18) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS haven_memories (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', fact TEXT NOT NULL, factDate INTEGER, category TEXT NOT NULL DEFAULT 'general', status TEXT NOT NULL DEFAULT 'pending', followUpDate INTEGER, sourceSessionId INTEGER, sourceMessage TEXT, followedUpAt INTEGER, followUpResponse TEXT, outcome TEXT, importance INTEGER NOT NULL DEFAULT 1, notificationSent INTEGER NOT NULL DEFAULT 0, notificationSentAt INTEGER, createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL, syncStatus TEXT NOT NULL DEFAULT 'pending', isDeleted INTEGER NOT NULL DEFAULT 0)")
            }
        }

        val MIGRATION_18_19: Migration = object : Migration(18, 19) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS evidence (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'local', evidenceType TEXT NOT NULL, content TEXT NOT NULL, secondaryContent TEXT, sourceType TEXT, sourceType TEXT, sourceId INTEGER, thenDate INTEGER, nowDate INTEGER, daysApart INTEGER, witnessOutcome TEXT, predictionAccurate INTEGER, rarity TEXT NOT NULL DEFAULT 'common', isViewed INTEGER NOT NULL DEFAULT 0, viewedAt INTEGER, isPinned INTEGER NOT NULL DEFAULT 0, collectedAt INTEGER NOT NULL, createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL, syncStatus TEXT NOT NULL DEFAULT 'pending', lastSyncedAt INTEGER, isDeleted INTEGER NOT NULL DEFAULT 0)")
            }
        }

        val MIGRATION_19_20: Migration = object : Migration(19, 20) {
            override fun migrate(db: SupportSQLiteDatabase) {
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
                val secureDbManager = SecureDatabaseManager(context)
                val supportFactory = secureDbManager.createSQLCipherSupportFactory()
                
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

        private class SecureDatabaseCallback(
            private val context: Context,
            private val secureDbManager: SecureDatabaseManager
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d(TAG, "Secure database created successfully - initiating data seeding")
                INSTANCE?.let { database ->
                    DatabaseSeeder.seedDatabase(database)
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                Log.d(TAG, "Secure database opened")
                
                val databaseFile = context.getDatabasePath(DATABASE_NAME)
                runBlocking {
                    val isIntegrityValid = secureDbManager.verifyDatabaseIntegrity(databaseFile)
                    if (!isIntegrityValid) {
                        Log.e(TAG, "Database integrity check failed!")
                    }
                }
            }

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                Log.w(TAG, "Secure database destructive migration performed - data was cleared")
                secureDbManager.clearDatabaseEncryption()
                INSTANCE?.let { database ->
                    DatabaseSeeder.seedDatabase(database)
                }
            }
        }
    }
}
