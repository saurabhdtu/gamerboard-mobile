package com.gamerboard.logging.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gamerboard.logging.model.LogEntry
import com.gamerboard.logging.model.converters.DateConverter

@Database(entities = [LogEntry::class], version = 4, exportSchema = false)
@TypeConverters(DateConverter::class)
internal abstract class LogDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao

    companion object {
        fun build(applicationContext: Context): LogDatabase {
            return Room.databaseBuilder(
                applicationContext,
                LogDatabase::class.java, "gb_log"
            ).fallbackToDestructiveMigration()
                .build()
        }
    }
}