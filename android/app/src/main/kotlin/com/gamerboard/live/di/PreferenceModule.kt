package com.gamerboard.live.di

import com.gamerboard.live.common.PrefsHelper
import org.koin.dsl.module
import kotlin.math.sin

val prefsModule  = module {
    single {
        PrefsHelper(get())
    }
}