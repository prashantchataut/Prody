package com.prody.prashant.di

import com.prody.prashant.data.repository.JournalRepositoryImpl
import com.prody.prashant.data.repository.VocabularyRepositoryImpl
import com.prody.prashant.domain.repository.JournalRepository
import com.prody.prashant.domain.repository.VocabularyRepository
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
}
