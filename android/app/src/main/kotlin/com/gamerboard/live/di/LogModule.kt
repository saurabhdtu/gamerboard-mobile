package com.gamerboard.live.di

import com.gamerboard.live.utils.FeatureFlag
import com.gamerboard.live.utils.FeatureHelper
import com.gamerboard.logger.LogHelper
import com.gamerboard.logger.Logger
import com.gamerboard.logger.agent.LogPublisher
import com.gamerboard.logger.agent.OldLoggerAgent
import com.gamerboard.logger.agent.PubSubClient
import com.gamerboard.logging.LoggingAgent
import org.koin.dsl.module

val logModule = module {
    single<LogHelper> {
        LogHelper.createInstance(get())
    }
    single<Logger> {
        Logger(get())
    }
    single<LoggingAgent.Factory> {
        if (FeatureHelper.getLoggingFlag(get()) == FeatureFlag.OLD) {
            OldLoggerAgent.Factory()
        } else {
            com.gamerboard.logging.Logger.Factory()
        }
    }


}

val logPublisherModule = module {
    single<LogPublisher> {
        PubSubClient(get())
    }
}