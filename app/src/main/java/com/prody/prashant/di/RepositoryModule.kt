package com.prody.prashant.di

import com.prody.prashant.data.repository.CollaborativeMessageRepositoryImpl
import com.prody.prashant.data.repository.GamificationRepositoryImpl
import com.prody.prashant.data.repository.JournalRepositoryImpl
import com.prody.prashant.data.repository.MicroEntryRepositoryImpl
import com.prody.prashant.data.repository.VocabularyRepositoryImpl
import com.prody.prashant.data.repository.WeeklyDigestRepositoryImpl
import com.prody.prashant.data.repository.YearlyWrappedRepositoryImpl
import com.prody.prashant.domain.repository.CollaborativeMessageRepository
import com.prody.prashant.domain.repository.GamificationRepository
import com.prody.prashant.domain.repository.JournalRepository
import com.prody.prashant.domain.repository.MicroEntryRepository
import com.prody.prashant.domain.repository.VocabularyRepository
import com.prody.prashant.domain.repository.WeeklyDigestRepository
import com.prody.prashant.domain.repository.WisdomCollectionRepository
import com.prody.prashant.domain.repository.YearlyWrappedRepository
import com.prody.prashant.domain.summary.WeeklySummaryEngine
import com.prody.prashant.domain.summary.WeeklySummaryEngineImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides repository bindings.
 * Uses @Binds to connect interface implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVocabularyRepository(
        impl: VocabularyRepositoryImpl
    ): VocabularyRepository

    @Binds
    @Singleton
    abstract fun bindJournalRepository(
        impl: JournalRepositoryImpl
    ): JournalRepository

    @Binds
    @Singleton
    abstract fun bindGamificationRepository(
        impl: GamificationRepositoryImpl
    ): GamificationRepository

    @Binds
    @Singleton
    abstract fun bindWeeklyDigestRepository(
        impl: WeeklyDigestRepositoryImpl
    ): WeeklyDigestRepository

    @Binds
    @Singleton
    abstract fun bindWeeklySummaryEngine(
        impl: WeeklySummaryEngineImpl
    ): WeeklySummaryEngine

    @Binds
    @Singleton
    abstract fun bindCollaborativeMessageRepository(
        impl: CollaborativeMessageRepositoryImpl
    ): CollaborativeMessageRepository

    @Binds
    @Singleton
    abstract fun bindYearlyWrappedRepository(
        impl: YearlyWrappedRepositoryImpl
    ): YearlyWrappedRepository
}
