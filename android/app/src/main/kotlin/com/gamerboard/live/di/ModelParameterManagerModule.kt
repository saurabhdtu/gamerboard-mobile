package com.gamerboard.live.di

import com.gamerboard.live.repository.ModelParameterManager
import org.koin.dsl.module

val modelParameterManagerModule = module {
    single {
        ModelParameterManager(get())
    }
}