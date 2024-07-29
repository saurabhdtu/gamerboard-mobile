package com.gamerboard.logger.agent

import com.gamerboard.logger.model.GameLogMessage
import com.gamerboard.logging.LogManager
import com.gamerboard.logging.LoggingAgent
import com.gamerboard.logging.serializer.LogMessage
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class DebugLogger(override val identifier: String?) : LoggingAgent(identifier), KoinComponent {
    protected val logManager: LogManager by inject()


    override fun log(message: LogMessage) {
        if(message is GameLogMessage){
            logManager.addLog(identifier = message.timestamp, message = message)
            return
        }
        logManager.addLog(identifier = identifier, message = message)
    }



    class Factory : LoggingAgent.Factory {
        override fun create(identifier: String?): LoggingAgent {
            return DebugLogger(identifier)
        }
    }
}