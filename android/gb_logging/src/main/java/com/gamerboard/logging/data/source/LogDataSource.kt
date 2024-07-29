package com.gamerboard.logging.data.source

import com.gamerboard.logging.model.LogEntry

internal interface LogDataSource {
    suspend fun log(message: LogEntry)
    suspend fun rewriteFailedEntry(message: LogEntry)
    suspend fun getLogs(): List<LogEntry>
    suspend fun readInChunks(
        chunkSize: Int,
        callback: suspend (List<LogEntry>) -> List<LogEntry>
    )

    suspend fun getCount(): Long
    suspend fun getLogsAndClear(): List<LogEntry>
    suspend fun delete()
    suspend fun markAllUnread()
    suspend fun delete(ids: List<String>)
    suspend fun markUnread(ids: List<String>)
    suspend fun markRead(ids: List<String>)
}