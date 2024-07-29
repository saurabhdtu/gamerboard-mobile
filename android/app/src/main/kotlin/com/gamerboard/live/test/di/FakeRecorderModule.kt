package com.gamerboard.live.test.di

import com.gamerboard.live.test.recorder.AbstractFakeScreenRecorder
import com.gamerboard.live.test.recorder.FakeScreenRecorder
import org.koin.dsl.module

val fakeRecorderModule = module{
    factory<AbstractFakeScreenRecorder>{
        FakeScreenRecorder()
    }
}