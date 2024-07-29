package com.gamerboard.logging.data

import com.gamerboard.logging.data.source.LogDataSource
import com.gamerboard.logging.model.LogEntry
import com.gamerboard.logging.serializer.LogMessage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class LogRepositoryImpl(
    private val dataSource: LogDataSource,
) : LogRepository {
    private val lock: Mutex = Mutex()
    override suspend fun log(identifier: String?, tag: String, message: LogMessage) {
        lock.withLock {
            dataSource.log(
                LogEntry(
                    identifier = identifier,
                    message = message.serialize(),
                )
            )
        }
    }

    override suspend fun rewriteFailedEntry(entry: LogEntry) {
        lock.withLock {
            dataSource.rewriteFailedEntry(entry)
        }
    }

    override suspend fun readInChunks(
        chunkSize: Int,
        callback: suspend (List<LogEntry>) -> List<LogEntry>
    ) {
        dataSource.readInChunks(chunkSize, callback)
    }

    override suspend fun getLogs(): List<LogEntry> {
        return dataSource.getLogs()
    }

    override suspend fun getLogsAndClear(): List<LogEntry> {
        return dataSource.getLogsAndClear()
    }

    override suspend fun clear() {
        dataSource.delete()
    }

    override suspend fun clear(ids: List<String>) {
        dataSource.delete(ids)
    }

    override suspend fun getCount(): Long {
        return dataSource.getCount()
    }

    override suspend fun markAllUnread() {
        dataSource.markAllUnread()
    }

    override suspend fun markUnread(ids: List<String>) {
        dataSource.markUnread(ids)
    }

    override suspend fun markRead(ids: List<String>) {
        dataSource.markRead(ids)
    }


}