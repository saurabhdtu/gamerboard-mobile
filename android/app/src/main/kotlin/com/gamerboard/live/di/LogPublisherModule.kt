package com.gamerboard.live.di

import com.gamerboard.logger.GooglePubSubLogProcessor
import com.gamerboard.logger.LogProcessor
import org.koin.dsl.module

val logProcessorModule = module {
    single <LogProcessor>{
        GooglePubSubLogProcessor(get())
    }
}