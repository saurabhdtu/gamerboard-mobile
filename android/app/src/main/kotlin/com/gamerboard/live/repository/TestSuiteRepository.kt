package com.gamerboard.live.repository

import com.gamerboard.live.data.remote.ITestSuiteDataSource
import com.gamerboard.live.models.test.TestSuite
import org.koin.java.KoinJavaComponent.inject

class TestSuiteRepository {
    private val remoteDataSource : ITestSuiteDataSource by inject(ITestSuiteDataSource::class.java)


    /**
     * Fetches test suite from remote data source.
     * Note: We can cache the data here so it may not request the data again for testing locally.
     * For test automation its ok to fetch new data.
     */
    suspend fun getSuite(packageName : String) : TestSuite{
        return remoteDataSource.getVideoSuite(packageName)
    }

}