package com.gamerboard.live.test.constants

import com.gamerboard.live.BuildConfig

object TestConfig {
    fun shouldRunTest(): Boolean = BuildConfig.IS_TEST
    fun shouldTestVision() = TestConfig.shouldRunTest() && BuildConfig.TEST_VISION
}