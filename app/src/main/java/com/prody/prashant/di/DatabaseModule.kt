package com.prody.prashant.di

import android.content.Context
import androidx.room.Room
import com.prody.prashant.data.local.database.DatabaseCallback
import com.prody.prashant.data.local.database.ProdyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabaseCallback(
        dbProvider: Provider<ProdyDatabase>
    ): DatabaseCallback {
        return DatabaseCallback(dbProvider)
    }

    @Provides
    @Singleton
    fun provideProdyDatabase(
        @ApplicationContext context: Context,
        callback: DatabaseCallback
    ): ProdyDatabase {
        return Room.databaseBuilder(
            context,
            ProdyDatabase::class.java,
            "prody_database"
        )
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
    }
}
