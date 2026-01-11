package com.prody.prashant.di

import com.prody.prashant.data.repository.CollaborativeMessageRepositoryImpl
import com.prody.prashant.data.repository.DailyRitualRepositoryImpl
import com.prody.prashant.data.repository.DeepDiveRepositoryImpl
import com.prody.prashant.data.repository.FutureMessageReplyRepositoryImpl
import com.prody.prashant.data.repository.GamificationRepositoryImpl
import com.prody.prashant.data.repository.JournalRepositoryImpl
import com.prody.prashant.data.repository.MicroEntryRepositoryImpl
import com.prody.prashant.data.repository.MonthlyLetterRepositoryImpl
import com.prody.prashant.data.repository.SocialRepositoryImpl
import com.prody.prashant.data.repository.SoulLayerRepositoryImpl
import com.prody.prashant.data.repository.VocabularyRepositoryImpl
import com.prody.prashant.data.repository.WeeklyDigestRepositoryImpl
import com.prody.prashant.data.repository.WisdomCollectionRepositoryImpl
import com.prody.prashant.data.repository.YearlyWrappedRepositoryImpl
import com.prody.prashant.domain.repository.CollaborativeMessageRepository
import com.prody.prashant.domain.repository.DailyRitualRepository
import com.prody.prashant.domain.repository.DeepDiveRepository
import com.prody.prashant.domain.repository.FutureMessageReplyRepository
import com.prody.prashant.domain.repository.GamificationRepository
import com.prody.prashant.domain.repository.JournalRepository
import com.prody.prashant.domain.repository.MicroEntryRepository
import com.prody.prashant.domain.repository.MonthlyLetterRepository
import com.prody.prashant.domain.repository.SocialRepository
import com.prody.prashant.domain.repository.SoulLayerRepository
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

    @Binds
    @Singleton
    abstract fun bindSoulLayerRepository(
        impl: SoulLayerRepositoryImpl
    ): SoulLayerRepository

    @Binds
    @Singleton
    abstract fun bindMicroEntryRepository(
        impl: MicroEntryRepositoryImpl
    ): MicroEntryRepository

    @Binds
    @Singleton
    abstract fun bindMonthlyLetterRepository(
        impl: MonthlyLetterRepositoryImpl
    ): MonthlyLetterRepository

    @Binds
    @Singleton
    abstract fun bindSocialRepository(
        impl: SocialRepositoryImpl
    ): SocialRepository

    @Binds
    @Singleton
    abstract fun bindWisdomCollectionRepository(
        impl: WisdomCollectionRepositoryImpl
    ): WisdomCollectionRepository

    @Binds
    @Singleton
    abstract fun bindDailyRitualRepository(
        impl: DailyRitualRepositoryImpl
    ): DailyRitualRepository

    @Binds
    @Singleton
    abstract fun bindDeepDiveRepository(
        impl: DeepDiveRepositoryImpl
    ): DeepDiveRepository

    @Binds
    @Singleton
    abstract fun bindFutureMessageReplyRepository(
        impl: FutureMessageReplyRepositoryImpl
    ): FutureMessageReplyRepository
}
