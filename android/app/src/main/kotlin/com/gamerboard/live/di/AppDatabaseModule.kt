package com.gamerboard.live.di

import com.gamerboard.live.models.db.getDatabase
import org.koin.dsl.module

val appDatabaseModule = module {
    single {
        getDatabase(get())
    }
}