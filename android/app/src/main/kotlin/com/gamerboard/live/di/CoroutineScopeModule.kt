package com.gamerboard.live.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext


val coroutineContextModule = module {
    factory<CoroutineContext>(qualifier = named("io")){
        Dispatchers.IO
    }
    factory<CoroutineContext>(qualifier = named("main")){
        Dispatchers.Main
    }
}


