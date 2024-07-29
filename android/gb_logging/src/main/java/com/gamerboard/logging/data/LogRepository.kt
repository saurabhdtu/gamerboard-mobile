package com.gamerboard.logging.data

import com.gamerboard.logging.model.LogEntry
import com.gamerboard.logging.serializer.LogMessage

internal interface LogRepository {
    suspend fun log(identifier: String?, tag: String, message: LogMessage)
    suspend fun rewriteFailedEntry(entry: LogEntry)

    suspend fun readInChunks(
        chunkSize: Int,
        callback: suspend (List<LogEntry>) -> List<LogEntry>
    )

    suspend fun getLogs(): List<LogEntry>
    suspend fun getLogsAndClear(): List<LogEntry>
    suspend fun clear()
    suspend fun getCount(): Long
    suspend fun markAllUnread()
    suspend fun clear(ids: List<String>)
    suspend fun markUnread(ids: List<String>)
    suspend fun markRead(ids: List<String>)
}