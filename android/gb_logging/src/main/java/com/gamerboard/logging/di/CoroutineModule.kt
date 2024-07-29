package com.gamerboard.logging.di

import com.gamerboard.logging.coroutine.executor.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

internal val coroutineModule = module {
    single<MainDispatcher> {
        MainDispatcher()
    }
    factory <CoroutineDispatcher>{
        Dispatchers.IO
    }
}