package com.gamerboard.logging.di

import com.gamerboard.logging.data.LogRepository
import com.gamerboard.logging.data.LogRepositoryImpl
import com.gamerboard.logging.data.source.FileSystemLogDataSource
import com.gamerboard.logging.data.source.LocalLogDataSource
import com.gamerboard.logging.data.source.LogDataSource
import com.gamerboard.logging.database.LogDao
import com.gamerboard.logging.database.LogDatabase
import org.koin.dsl.module

internal val repositoryModule = module {
    single<LogRepository> {
        LogRepositoryImpl(get())
    }
    single <LogDataSource>{
        LocalLogDataSource(get())
    }
    single <LogDao>{
        get<LogDatabase>().logDao()
    }
    single<LogDatabase> {
        LogDatabase.build(get())
    }
}

