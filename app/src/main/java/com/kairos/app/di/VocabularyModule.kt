package com.kairos.app.di

import com.kairos.app.domain.vocabulary.DetectionConfig
import com.kairos.app.domain.vocabulary.VocabularyDetector
import com.kairos.app.domain.vocabulary.VocabularyDetectorImpl
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
