package com.gamerboard.live.di

import com.gamerboard.live.caching.BitmapCache
import com.gamerboard.live.caching.GameValueCache
import org.koin.dsl.module

val cacheModule =  module {
    single{
        BitmapCache()
    }

    single {
        GameValueCache()
    }
}