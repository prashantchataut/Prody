package com.kairos.app.di

import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kairos.app.data.local.dao.*
import com.kairos.app.data.local.database.DatabaseSeeder
import com.kairos.app.data.local.database.KairosDatabase
import com.kairos.app.data.repository.DailyPlanRepositoryImpl
import com.kairos.app.domain.recommendation.ExplainableRecommendationRanker
import com.kairos.app.domain.notification.NotificationDeliveryPolicy
import com.kairos.app.domain.repository.DailyPlanRepository
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

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): KairosDatabase {
        return KairosDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideJournalDao(database: KairosDatabase): JournalDao {
        return database.journalDao()
    }

    @Provides
    @Singleton
    fun provideFutureMessageDao(database: KairosDatabase): FutureMessageDao {
        return database.futureMessageDao()
    }

    @Provides
    @Singleton
    fun provideVocabularyDao(database: KairosDatabase): VocabularyDao {
        return database.vocabularyDao()
    }

    @Provides
    @Singleton
    fun provideVocabularyLearningDao(database: KairosDatabase): VocabularyLearningDao {
        return database.vocabularyLearningDao()
    }

    @Provides
    @Singleton
    fun provideQuoteDao(database: KairosDatabase): QuoteDao {
        return database.quoteDao()
    }

    @Provides
    @Singleton
    fun provideDailyContentDao(database: KairosDatabase): DailyContentDao {
        return database.dailyContentDao()
    }

    @Provides
    @Singleton
    fun provideRecommendationRanker(): ExplainableRecommendationRanker {
        return ExplainableRecommendationRanker()
    }

    @Provides
    @Singleton
    fun provideNotificationDeliveryPolicy(): NotificationDeliveryPolicy {
        return NotificationDeliveryPolicy()
    }

    @Provides
    @Singleton
    fun provideDailyPlanRepository(
        implementation: DailyPlanRepositoryImpl
    ): DailyPlanRepository = implementation

    @Provides
    @Singleton
    fun provideProverbDao(database: KairosDatabase): ProverbDao {
        return database.proverbDao()
    }

    @Provides
    @Singleton
    fun provideIdiomDao(database: KairosDatabase): IdiomDao {
        return database.idiomDao()
    }

    @Provides
    @Singleton
    fun providePhraseDao(database: KairosDatabase): PhraseDao {
        return database.phraseDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: KairosDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideChallengeDao(database: KairosDatabase): ChallengeDao {
        return database.challengeDao()
    }

    @Provides
    @Singleton
    fun provideSeedDao(database: KairosDatabase): SeedDao {
        return database.seedDao()
    }

    @Provides
    @Singleton
    fun provideMissionDao(database: KairosDatabase): MissionDao {
        return database.missionDao()
    }

    // ============================================================================
    // NEW DAOs FOR DAILY ENGAGEMENT FEATURES
    // ============================================================================

    @Provides
    @Singleton
    fun provideSavedWisdomDao(database: KairosDatabase): SavedWisdomDao {
        return database.savedWisdomDao()
    }

    @Provides
    @Singleton
    fun provideMicroEntryDao(database: KairosDatabase): MicroEntryDao {
        return database.microEntryDao()
    }

    @Provides
    @Singleton
    fun provideWeeklyDigestDao(database: KairosDatabase): WeeklyDigestDao {
        return database.weeklyDigestDao()
    }

    @Provides
    @Singleton
    fun provideDailyRitualDao(database: KairosDatabase): DailyRitualDao {
        return database.dailyRitualDao()
    }

    @Provides
    @Singleton
    fun provideFutureMessageReplyDao(database: KairosDatabase): FutureMessageReplyDao {
        return database.futureMessageReplyDao()
    }

    @Provides
    @Singleton
    fun provideLearningPathDao(database: KairosDatabase): LearningPathDao {
        return database.learningPathDao()
    }

    @Provides
    @Singleton
    fun provideDeepDiveDao(database: KairosDatabase): DeepDiveDao {
        return database.deepDiveDao()
    }

    @Provides
    @Singleton
    fun provideSocialDao(database: KairosDatabase): SocialDao {
        return database.socialDao()
    }

    @Provides
    @Singleton
    fun provideHavenDao(database: KairosDatabase): HavenDao {
        return database.havenDao()
    }

    @Provides
    @Singleton
    fun provideYearlyWrappedDao(database: KairosDatabase): YearlyWrappedDao {
        return database.yearlyWrappedDao()
    }

    @Provides
    @Singleton
    fun provideCollaborativeMessageDao(database: KairosDatabase): CollaborativeMessageDao {
        return database.collaborativeMessageDao()
    }

    @Provides
    @Singleton
    fun provideDualStreakDao(database: KairosDatabase): DualStreakDao {
        return database.dualStreakDao()
    }

    @Provides
    @Singleton
    fun provideWordUsageDao(database: KairosDatabase): WordUsageDao {
        return database.wordUsageDao()
    }

    @Provides
    @Singleton
    fun provideMonthlyLetterDao(database: KairosDatabase): MonthlyLetterDao {
        return database.monthlyLetterDao()
    }

    @Provides
    @Singleton
    fun provideMessageAnniversaryDao(database: KairosDatabase): MessageAnniversaryDao {
        return database.messageAnniversaryDao()
    }

    // ============================================================================
    // SOUL LAYER INTELLIGENCE DAO
    // ============================================================================

    @Provides
    @Singleton
    fun provideSoulLayerDao(database: KairosDatabase): SoulLayerDao {
        return database.soulLayerDao()
    }

    // ============================================================================
    // MIRROR EVOLUTION DAOs (Haven Memory + Evidence Locker)
    // ============================================================================

    @Provides
    @Singleton
    fun provideHavenMemoryDao(database: KairosDatabase): HavenMemoryDao {
        return database.havenMemoryDao()
    }

    @Provides
    @Singleton
    fun provideEvidenceDao(database: KairosDatabase): EvidenceDao {
        return database.evidenceDao()
    }
// ============================================================================
    // PRIVACY, SECURITY & MONITORING PROVIDERS
    // ============================================================================
// ============================================================================
    // LEARNING PATH PROVIDERS
    // ============================================================================

    /**
     * Provides PathContentProvider object singleton.
     * Required because PathContentProvider is a Kotlin object and cannot use @Inject constructor.
     */
    @Provides
    @Singleton
    fun providePathContentProvider(): com.kairos.app.domain.learning.PathContentProvider {
        return com.kairos.app.domain.learning.PathContentProvider
    }

    // ============================================================================
    // SERIALIZATION PROVIDERS
    // ============================================================================

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .serializeNulls()
            .create()
    }

    // ============================================================================
    // FIREBASE AUTH PROVIDERS
    // ============================================================================

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            android.util.Log.e("AppModule", "FirebaseAuth initialization failed", e)
            throw IllegalStateException(
                "Firebase Auth unavailable. Ensure google-services.json is configured. Error: ${e.message}", e
            )
        }
    }
}
