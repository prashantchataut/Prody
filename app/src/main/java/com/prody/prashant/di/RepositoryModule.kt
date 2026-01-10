package com.prody.prashant.di

import com.prody.prashant.data.repository.DailyRitualRepositoryImpl
import com.prody.prashant.data.repository.FutureMessageReplyRepositoryImpl
import com.prody.prashant.data.repository.JournalRepositoryImpl
import com.prody.prashant.data.repository.MicroEntryRepositoryImpl
import com.prody.prashant.data.repository.VocabularyRepositoryImpl
import com.prody.prashant.data.repository.WeeklyDigestRepositoryImpl
import com.prody.prashant.data.repository.WisdomCollectionRepositoryImpl
import com.prody.prashant.domain.repository.DailyRitualRepository
import com.prody.prashant.domain.repository.FutureMessageReplyRepository
import com.prody.prashant.domain.repository.JournalRepository
import com.prody.prashant.domain.repository.MicroEntryRepository
import com.prody.prashant.domain.repository.VocabularyRepository
import com.prody.prashant.domain.repository.WeeklyDigestRepository
import com.prody.prashant.domain.repository.WisdomCollectionRepository
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

    // ============================================================================
    // NEW REPOSITORIES FOR DAILY ENGAGEMENT FEATURES
    // ============================================================================

    @Binds
    @Singleton
    abstract fun bindWisdomCollectionRepository(
        impl: WisdomCollectionRepositoryImpl
    ): WisdomCollectionRepository

    @Binds
    @Singleton
    abstract fun bindMicroEntryRepository(
        impl: MicroEntryRepositoryImpl
    ): MicroEntryRepository

    @Binds
    @Singleton
    abstract fun bindDailyRitualRepository(
        impl: DailyRitualRepositoryImpl
    ): DailyRitualRepository

    @Binds
    @Singleton
    abstract fun bindWeeklyDigestRepository(
        impl: WeeklyDigestRepositoryImpl
    ): WeeklyDigestRepository

    @Binds
    @Singleton
    abstract fun bindFutureMessageReplyRepository(
        impl: FutureMessageReplyRepositoryImpl
    ): FutureMessageReplyRepository
}
