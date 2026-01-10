package com.prody.prashant.di

import com.prody.prashant.domain.vocabulary.DetectionConfig
import com.prody.prashant.domain.vocabulary.VocabularyDetector
import com.prody.prashant.domain.vocabulary.VocabularyDetectorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for vocabulary-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object VocabularyModule {

    @Provides
    @Singleton
    fun provideDetectionConfig(): DetectionConfig {
        return DetectionConfig(
            caseSensitive = false,
            matchWordForms = true,
            minWordLength = 3
        )
    }

    @Provides
    @Singleton
    fun provideVocabularyDetector(
        config: DetectionConfig
    ): VocabularyDetector {
        return VocabularyDetectorImpl(config)
    }
}
