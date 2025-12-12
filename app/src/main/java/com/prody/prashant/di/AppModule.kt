package com.prody.prashant.di

import android.content.Context
import androidx.room.Room
import com.prody.prashant.data.ai.GeminiService
import com.prody.prashant.data.local.dao.*
import com.prody.prashant.data.local.database.ProdyDatabase
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.data.backup.BackupManager
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

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ProdyDatabase {
        return Room.databaseBuilder(
            context,
            ProdyDatabase::class.java,
            ProdyDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
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
}
