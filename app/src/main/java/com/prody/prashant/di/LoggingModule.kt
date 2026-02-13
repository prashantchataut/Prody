package com.prody.prashant.di

import com.prody.prashant.core.logging.AppLogger
import com.prody.prashant.core.logging.CrashAnalytics
import com.prody.prashant.core.logging.LogcatCrashAnalytics
import com.prody.prashant.core.logging.PiiRedactor
import com.prody.prashant.core.logging.StructuredAppLogger
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggingModule {

    @Binds
    @Singleton
    abstract fun bindAppLogger(impl: StructuredAppLogger): AppLogger

    @Binds
    @Singleton
    abstract fun bindCrashAnalytics(impl: LogcatCrashAnalytics): CrashAnalytics

    companion object {
        @Provides
        @Singleton
        fun providePiiRedactor(): PiiRedactor = PiiRedactor()
    }
}
