package org.don.onlineTrade.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SearchHistoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
}
