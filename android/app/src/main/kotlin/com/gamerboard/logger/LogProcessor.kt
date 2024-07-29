package com.gamerboard.logger

import com.gamerboard.logging.LogManager
import com.gamerboard.logging.model.LogEntry
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class LogProcessor : KoinComponent {

    private val logManager: LogManager by inject()

    init {
        logManager.markAllUnread()
    }

    abstract suspend fun publish(messages: List<LogEntry>, onComplete : (ids : List<String>) -> Unit , failed : (ids  : List<String>)-> Unit) : Boolean
    suspend fun process() {
        val logMessages = logManager.getLogEntries()
        if (logMessages.isEmpty()) return
        publish(logMessages, onComplete = {
            logManager.clear(it)
        }, failed = {
            logManager.markUnread(it)
        })
    }
}