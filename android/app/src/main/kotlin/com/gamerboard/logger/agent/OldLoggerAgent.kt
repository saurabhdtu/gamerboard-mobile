package com.gamerboard.logger.agent

import com.gamerboard.logger.model.GameLogMessage
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.Logger
import com.gamerboard.logger.PlatformType
import com.gamerboard.logger.gson
import com.gamerboard.logging.LoggingAgent
import com.gamerboard.logging.serializer.LogMessage
import com.google.gson.Gson
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OldLoggerAgent : LoggingAgent("old"), KoinComponent {
    private val logger: Logger by inject()
    override fun log(message: LogMessage) {
        if (message is GameLogMessage) {

            val category = getCategoryByDescription(message.category) ?: LogCategory.D
            val platform = getPlatformByDescription(message.platform) ?: PlatformType.A
            if (category == LogCategory.AUTOML_IMAGE) {
                logger.log(
                    message = gson.toJson(
                        hashMapOf(
                            Pair("automl", message.ocrInfo?.vision?.visionImage)
                        )
                    ),
                    category = category,
                    logToConsole = true,
                    commitLog = false,
                    platform = platform
                )
            } else {
                val gameObjCategories = arrayListOf(
                    LogCategory.CM.description,
                    LogCategory.CME.description,
                    LogCategory.ICM.description
                )
                val finalMessage = JSONObject()
                message.context.forEach { hashMap ->
                  hashMap.forEach { (key, value) ->
                      finalMessage.put(key, value)
                  }
                }
                if (gameObjCategories.contains(message.category)) {
                    finalMessage.put("gameId", finalMessage.optString("gameId"))
                    finalMessage.put("reason", finalMessage.optString("reason"))
                    finalMessage.put("game", finalMessage.optString("game"))
                    logger.log(
                        message = finalMessage.toString(),
                        category = category,
                        logToConsole = true,
                        commitLog = false,
                        platform = platform
                    )
                } else {
                    logger.log(
                        message = message.message ?: "NA",
                        category = category,
                        logToConsole = true,
                        commitLog = false,
                        platform = platform
                    )
                }

            }
        }
    }

    fun getCategoryByDescription(description: String): LogCategory? {
        return LogCategory.values().find { it.description == description }
    }

    fun getPlatformByDescription(description: String): PlatformType? {
        return PlatformType.values().find { it.description == description }
    }

    class Factory : LoggingAgent.Factory {
        override fun create(identifier: String?): LoggingAgent {
            return OldLoggerAgent()
        }

    }
}