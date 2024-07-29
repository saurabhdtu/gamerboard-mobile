package com.gamerboard.live.di

import com.gamerboard.live.data.remote.FirebaseTestSuiteDataSource
import com.gamerboard.live.data.remote.ITestSuiteDataSource
import com.gamerboard.live.repository.TestSuiteRepository
import org.koin.dsl.module

val repositoryModule = module{
    single<ITestSuiteDataSource>{
        FirebaseTestSuiteDataSource()
    }
    single<TestSuiteRepository>{
        TestSuiteRepository()
    }
}