package com.gamerboard.logging

import com.gamerboard.logging.coroutine.executor.MainIoExecutor
import com.gamerboard.logging.model.LogEntry
import com.gamerboard.logging.serializer.LogMessage
import com.gamerboard.logging.utils.UuidHelper
import java.util.UUID

abstract class LoggingAgent(open val identifier: String?) : MainIoExecutor() {

    abstract fun log(message : LogMessage)

    interface   Factory {
        fun create(identifier : String? = UuidHelper.identifier()) : LoggingAgent
    }
}