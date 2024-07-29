package com.gamerboard.logging.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.gamerboard.logging.model.LogEntry


@Dao
internal interface LogDao {
    @Insert
    fun log(log: LogEntry)

    @Query("SELECT * FROM logs WHERE read != 1  ORDER BY read ASC  limit 500")
    fun getAll() : List<LogEntry>

    @Query("SELECT * FROM logs WHERE read == 0  ORDER BY read ASC  limit :pageSize OFFSET :page")
    fun getPaged(page : Int = 0, pageSize : Int) : List<LogEntry>

    @Query("SELECT COUNT(id) FROM logs WHERE read != 1  ORDER BY read ASC limit 500")
    fun getCount() : Long
    @Query("UPDATE logs SET read = 1 WHERE id in (:ids)")
    fun setRead(ids : List<String>)

    @Query("UPDATE logs SET read = 0 WHERE id in (:ids)")
    fun setUnread(ids : List<String>)

    @Query("DELETE FROM logs WHERE id in (:ids)")
    fun delete(ids : List<String>)

    @Query("DELETE FROM logs")
    fun delete()

    @Query("DELETE FROM logs WHERE read = 1")
    fun deleteRead()
    @Query("UPDATE logs SET read = 0")
    fun setAllUnread()
    @Transaction
    fun getLogsAndClear() : List<LogEntry> {
        val getAll = getAll()
        delete(getAll.map { it.id })
        return getAll
    }
}