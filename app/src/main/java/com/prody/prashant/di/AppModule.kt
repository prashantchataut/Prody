package com.prody.prashant.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.prody.prashant.data.ai.BuddhaAiService
import com.prody.prashant.data.ai.GeminiService
import com.prody.prashant.data.ai.OpenRouterService
import com.prody.prashant.data.local.dao.*
import com.prody.prashant.data.local.database.DatabaseSeeder
import com.prody.prashant.data.local.database.ProdyDatabase
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.data.backup.BackupManager
import com.prody.prashant.data.cache.AiCacheManager
import com.prody.prashant.data.moderation.ContentModerationManager
import com.prody.prashant.data.monitoring.PerformanceMonitor
import com.prody.prashant.data.network.NetworkConnectivityManager
import com.prody.prashant.data.onboarding.AiOnboardingManager
import com.prody.prashant.data.security.EncryptionManager
import com.prody.prashant.data.security.SecurityPreferences
import com.prody.prashant.data.sync.SyncManager
import com.prody.prashant.util.TextToSpeechManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val TAG = "AppModule"

    @Volatile
    private var databaseInstance: ProdyDatabase? = null

    /**
     * Database callback for initialization tasks, seeding, and logging.
     * Seeds the database with initial wisdom content on first creation.
     */
    private val databaseCallback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d(TAG, "Database created successfully - initiating data seeding")
            // Seed the database with initial content
            databaseInstance?.let { database ->
                DatabaseSeeder.seedDatabase(database)
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Log.d(TAG, "Database opened")
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            Log.w(TAG, "Destructive migration performed - re-seeding database")
            // Re-seed the database after destructive migration
            databaseInstance?.let { database ->
                DatabaseSeeder.seedDatabase(database)
            }
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ProdyDatabase {
        return databaseInstance ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                ProdyDatabase::class.java,
                ProdyDatabase.DATABASE_NAME
            )
                .addMigrations(ProdyDatabase.MIGRATION_4_5, ProdyDatabase.MIGRATION_5_6)
                .fallbackToDestructiveMigration()
                .addCallback(databaseCallback)
                .build()
            databaseInstance = instance
            instance
        }
    }

    @Provides
    @Singleton
    fun provideJournalDao(database: ProdyDatabase): JournalDao {
        return database.journalDao()
    }

    @Provides
    @Singleton
    fun provideFutureMessageDao(database: ProdyDatabase): FutureMessageDao {
        return database.futureMessageDao()
    }

    @Provides
    @Singleton
    fun provideVocabularyDao(database: ProdyDatabase): VocabularyDao {
        return database.vocabularyDao()
    }

    @Provides
    @Singleton
    fun provideVocabularyLearningDao(database: ProdyDatabase): VocabularyLearningDao {
        return database.vocabularyLearningDao()
    }

    @Provides
    @Singleton
    fun provideQuoteDao(database: ProdyDatabase): QuoteDao {
        return database.quoteDao()
    }

    @Provides
    @Singleton
    fun provideProverbDao(database: ProdyDatabase): ProverbDao {
        return database.proverbDao()
    }

    @Provides
    @Singleton
    fun provideIdiomDao(database: ProdyDatabase): IdiomDao {
        return database.idiomDao()
    }

    @Provides
    @Singleton
    fun providePhraseDao(database: ProdyDatabase): PhraseDao {
        return database.phraseDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: ProdyDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideChallengeDao(database: ProdyDatabase): ChallengeDao {
        return database.challengeDao()
    }

    @Provides
    @Singleton
    fun provideSeedDao(database: ProdyDatabase): SeedDao {
        return database.seedDao()
    }

    @Provides
    @Singleton
    fun provideMissionDao(database: ProdyDatabase): MissionDao {
        return database.missionDao()
    }

    // ============================================================================
    // NEW DAOs FOR DAILY ENGAGEMENT FEATURES
    // ============================================================================

    @Provides
    @Singleton
    fun provideSavedWisdomDao(database: ProdyDatabase): SavedWisdomDao {
        return database.savedWisdomDao()
    }

    @Provides
    @Singleton
    fun provideMicroEntryDao(database: ProdyDatabase): MicroEntryDao {
        return database.microEntryDao()
    }

    @Provides
    @Singleton
    fun provideWeeklyDigestDao(database: ProdyDatabase): WeeklyDigestDao {
        return database.weeklyDigestDao()
    }

    @Provides
    @Singleton
    fun provideDailyRitualDao(database: ProdyDatabase): DailyRitualDao {
        return database.dailyRitualDao()
    }

    @Provides
    @Singleton
    fun provideFutureMessageReplyDao(database: ProdyDatabase): FutureMessageReplyDao {
        return database.futureMessageReplyDao()
    }

    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideTextToSpeechManager(
        @ApplicationContext context: Context
    ): TextToSpeechManager {
        return TextToSpeechManager(context)
    }

    @Provides
    @Singleton
    fun provideBackupManager(
        @ApplicationContext context: Context,
        journalDao: JournalDao,
        futureMessageDao: FutureMessageDao,
        vocabularyDao: VocabularyDao,
        userDao: UserDao,
        preferencesManager: PreferencesManager
    ): BackupManager {
        return BackupManager(
            context,
            journalDao,
            futureMessageDao,
            vocabularyDao,
            userDao,
            preferencesManager
        )
    }

    @Provides
    @Singleton
    fun provideGeminiService(): GeminiService {
        return GeminiService()
    }

    @Provides
    @Singleton
    fun provideOpenRouterService(): OpenRouterService {
        return OpenRouterService()
    }

    @Provides
    @Singleton
    fun provideAiCacheManager(
        @ApplicationContext context: Context
    ): AiCacheManager {
        return AiCacheManager(context)
    }

    @Provides
    @Singleton
    fun provideBuddhaAiService(
        geminiService: GeminiService,
        aiCacheManager: AiCacheManager
    ): BuddhaAiService {
        return BuddhaAiService(geminiService, aiCacheManager)
    }

    // ============================================================================
    // PRIVACY, SECURITY & MONITORING PROVIDERS
    // ============================================================================

    @Provides
    @Singleton
    fun provideEncryptionManager(
        @ApplicationContext context: Context
    ): EncryptionManager {
        return EncryptionManager(context)
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityManager(
        @ApplicationContext context: Context
    ): NetworkConnectivityManager {
        return NetworkConnectivityManager(context)
    }

    @Provides
    @Singleton
    fun provideSyncManager(
        @ApplicationContext context: Context,
        networkManager: NetworkConnectivityManager,
        preferencesManager: PreferencesManager
    ): SyncManager {
        return SyncManager(context, networkManager, preferencesManager)
    }

    @Provides
    @Singleton
    fun providePerformanceMonitor(
        @ApplicationContext context: Context
    ): PerformanceMonitor {
        return PerformanceMonitor(context)
    }

    @Provides
    @Singleton
    fun provideAiOnboardingManager(
        @ApplicationContext context: Context
    ): AiOnboardingManager {
        return AiOnboardingManager(context)
    }

    @Provides
    @Singleton
    fun provideContentModerationManager(
        @ApplicationContext context: Context
    ): ContentModerationManager {
        return ContentModerationManager(context)
    }

    @Provides
    @Singleton
    fun provideSecurityPreferences(
        @ApplicationContext context: Context
    ): SecurityPreferences {
        return SecurityPreferences(context)
    }
}
