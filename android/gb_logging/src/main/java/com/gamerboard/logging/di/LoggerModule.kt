package com.gamerboard.logging.di

import com.gamerboard.logging.LogManager
import org.koin.dsl.module

internal val loggerModule = module {
    single {
        LogManager()
    }
}