package com.gamerboard.live.di

import com.gamerboard.live.service.screencapture.MLKitOCR
import org.koin.dsl.module

val ocrModule = module {
    factory {
        MLKitOCR()
    }
}