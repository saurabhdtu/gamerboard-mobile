package com.gamerboard.logging.data.source

import android.util.Log
import com.gamerboard.logging.database.LogDao
import com.gamerboard.logging.model.LogEntry
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlin.math.ceil

internal class LocalLogDataSource(private val dao: LogDao) : LogDataSource {
    companion object {
        private val TAG = LocalLogDataSource::class.java.simpleName
    }

    override suspend fun log(message: LogEntry) {
        dao.log(message)
    }

    override suspend fun rewriteFailedEntry(message: LogEntry) {
        //Do nothing
    }

    override suspend fun getLogs(): List<LogEntry> {
        return dao.getAll().also {
            dao.setRead(it.map { it.id })
        }
    }

    override suspend fun readInChunks(
        chunkSize: Int,
        callback: suspend (List<LogEntry>) -> List<LogEntry>,
    ) {
        try{
            val count = dao.getCount()
            val pages = ceil(count / chunkSize.toFloat()).toInt()
            for (i in 0 until pages) {
                val successfulEntries = callback(dao.getPaged(0, chunkSize))
                successfulEntries.chunked(100){ chunkedEntries ->
                    dao.delete(chunkedEntries.map { it.id })
                }
            }
        }catch (ex : Exception){
            Firebase.crashlytics.recordException(ex)
        }
    }
    override suspend fun getCount(): Long {
        return dao.getCount()
    }

    override suspend fun getLogsAndClear(): List<LogEntry> {
        return dao.getLogsAndClear()
    }

    override suspend fun delete() {
        return dao.deleteRead()
    }

    override suspend fun delete(ids: List<String>) {
        return dao.delete(ids)
    }

    override suspend fun markAllUnread() {
        dao.setAllUnread()
    }

    override suspend fun markUnread(ids: List<String>) {
        dao.setUnread(ids)
    }

    override suspend fun markRead(ids: List<String>) {
        dao.setRead(ids)
    }
}