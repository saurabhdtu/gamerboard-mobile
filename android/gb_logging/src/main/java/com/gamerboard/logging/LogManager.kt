package com.gamerboard.logging

import com.gamerboard.logging.coroutine.executor.MainIoExecutor
import com.gamerboard.logging.data.LogRepository
import com.gamerboard.logging.model.LogEntry
import com.gamerboard.logging.serializer.LogMessage
import com.gamerboard.logging.serializer.StringMessage
import com.gamerboard.logging.utils.TagHelper
import com.gamerboard.logging.utils.now
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class LogManager : MainIoExecutor() {
    private val logRepository: LogRepository by inject(LogRepository::class.java)

    fun addLog(message: LogMessage, identifier: String? = null) {
        try{
            ensureActive()
            launch {
                logRepository.log(identifier, tag = TagHelper.getTag(), message = message)
            }
        }catch (ex : Exception){
            ex.printStackTrace()
        }
    }

    suspend fun getLogEntries(): List<LogEntry> {
        return logRepository.getLogs()
    }

    suspend fun getUnreadCount(): Long {
        return logRepository.getCount()
    }

    suspend fun getLogsAndClear(): List<LogEntry> {
        return logRepository.getLogsAndClear()
    }

    suspend fun readInChunks(
        chunkSize: Int,
        callback: suspend (List<LogEntry>) -> List<LogEntry>
    ){
        logRepository.readInChunks(chunkSize, callback)
    }

    fun clear(ids: List<String>) {
        launch {
            logRepository.clear(ids)
        }
    }

    fun markUnread(ids: List<String>) {
        launch {
            logRepository.markUnread(ids)
        }
    }

    fun markRead(ids: List<String>) {
        launch {
            logRepository.markRead(ids)
        }
    }
    fun markAllUnread() {
        launch {
            logRepository.markAllUnread()
        }
    }

    fun clear() {
        launch {
            logRepository.clear()
        }
    }

    fun rewriteFailedEntry(entry: LogEntry) {
        launch {
            logRepository.rewriteFailedEntry(entry)
        }
    }

}