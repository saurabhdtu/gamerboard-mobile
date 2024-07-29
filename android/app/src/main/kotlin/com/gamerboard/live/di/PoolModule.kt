package com.gamerboard.live.di

import com.gamerboard.live.pool.PoolManager
import org.koin.dsl.module

val poolModule = module {
    single <PoolManager>{
        PoolManager()
    }
}