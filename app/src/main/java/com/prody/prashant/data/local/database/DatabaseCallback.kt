package com.prody.prashant.data.local.database

import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class DatabaseCallback @Inject constructor(
    private val dbProvider: Provider<ProdyDatabase>
) : RoomDatabase.Callback() {

    private val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        applicationScope.launch {
            try {
                val database = dbProvider.get()
                DatabaseSeeder.seedDatabase(database)
            } catch (e: Exception) {
                Log.e("DatabaseCallback", "Error seeding database on create", e)
            }
        }
    }
}
