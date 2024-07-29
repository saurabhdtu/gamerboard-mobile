package com.gamerboard.live.data.remote

import com.gamerboard.live.models.test.TestSuite

interface ITestSuiteDataSource {
   suspend fun getVideoSuite(packageName : String) : TestSuite
   suspend fun getImageSuite() : TestSuite
}