package com.gamerboard.live.models.db

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.apollographql.apollo3.api.and
import com.gamerboard.live.utils.FFMpegUtil
import com.gamerboard.live.utils.logException
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Created by saurabh.lahoti on 25/08/21
 */
@SuppressLint("RestrictedApi")
@Database(
    entities = [Session::class, Game::class],
    version = 7
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun getGamesDao(): GameDao
}

fun getDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java, "gamerboard"
    ).addMigrations(MIGRATION_1_4)
        .addMigrations(MIGRATION_2_4)
        .addMigrations(MIGRATION_3_4)
        .addMigrations(MIGRATION_4_5)
        .addMigrations(MIGRATION_5_6)
        .addMigrations(MIGRATION_6_7)
        .build()
}

val MIGRATION_1_4 = object : Migration(1, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `user_games` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` TEXT, `valid` INTEGER, `rank` TEXT, `gameInfo` TEXT, `kills` TEXT, `teamRank` TEXT, `initialTier` TEXT, `finalTier` TEXT, `endTimestamp` TEXT, `startTimeStamp` TEXT, `gameId` TEXT, `synced` INTEGER, `metaInfoJson` TEXT )")
    }
}

val MIGRATION_2_4 = object : Migration(2, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `user_games` ADD COLUMN `initialTier` TEXT")
        database.execSQL("ALTER TABLE `user_games` ADD COLUMN `finalTier` TEXT")
        database.execSQL("ALTER TABLE `user_games` ADD COLUMN `metaInfoJson` TEXT")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `user_games` ADD COLUMN `serverGameId` INTEGER")
        database.execSQL("ALTER TABLE `user_games` ADD COLUMN `serverUserId` INTEGER")
    }
}


val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `user_games` ADD COLUMN `metaInfoJson` TEXT")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `user_games` ADD COLUMN `squadScoring` TEXT DEFAULT NULL")
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            database.execSQL("DROP TABLE `sessions`")
        } catch (e: Exception) {
            logException(e)
        }
        try {
            database.execSQL("DROP TABLE `captures`")
        } catch (e: Exception) {
            logException(e)
        }
        database.execSQL("CREATE TABLE IF NOT EXISTS `sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sessionId` TEXT UNIQUE, `createdAt` INTEGER NOT NULL)")
    }
}