package com.gamerboard.logger.agent

import com.gamerboard.logging.model.LogEntry
import com.google.api.core.ApiFuture

interface LogPublisher {
    fun prepare()
    fun clean()
    suspend fun publishMessages(env: String, messages: List<LogEntry>) : List<PublishResult>
}

sealed interface Result{
    data class Error(val ex : Throwable) : Result
    data class Success(val future : ApiFuture<String>, val logEntry: LogEntry) : Result
}

data class  PublishResult(val logEntry: LogEntry, val error : String)