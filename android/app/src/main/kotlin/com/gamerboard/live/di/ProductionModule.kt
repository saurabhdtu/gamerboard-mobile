package com.gamerboard.live.di

import com.gamerboard.live.repository.ApiClientModule
import com.gamerboard.live.service.screencapture.FileAndDataSyncModule
import com.gamerboard.live.test.di.fakeRecorderModule
import com.gamerboard.logger.LoggerModule

val productionModule = listOf(
    FileAndDataSyncModule,
    LoggerModule,
    ApiClientModule,
    prefsModule,
    logModule,
    logProcessorModule,
    fakeRecorderModule,
    appDatabaseModule,
    coroutineContextModule,
    ocrModule,
    repositoryModule,
    logPublisherModule,
    slackModule,
    cacheModule,
    modelParameterManagerModule,
    poolModule
)