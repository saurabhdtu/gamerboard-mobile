package com.gamerboard.logging

import com.gamerboard.logging.coroutine.executor.MainIoExecutor
import com.gamerboard.logging.data.LogRepository
import com.gamerboard.logging.serializer.LogMessage
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class Logger(override val identifier: String?) : LoggingAgent(identifier), KoinComponent {
    protected val logManager: LogManager by inject()


    override fun log(message: LogMessage) {
        logManager.addLog(identifier = identifier, message = message)
    }



    class Factory : LoggingAgent.Factory {
        override fun create(identifier: String?): LoggingAgent {
            return Logger(identifier)
        }
    }
}