package com.gamerboard.live.logger

import android.content.Context
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.gamerboard.live.MainActivity
import com.gamerboard.live.models.db.AppDatabase
import com.gamerboard.live.utils.FeatureFlag
import com.gamerboard.live.utils.FeatureHelper
import com.gamerboard.logger.agent.OldLoggerAgent
import com.gamerboard.logging.Logger
import com.gamerboard.logging.LoggingAgent
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@RunWith(AndroidJUnit4::class)
@LargeTest
class LogFeatureFlagTest {

    companion object{
        private val TAG = LogFeatureFlagTest::class.java.simpleName
    }
    private lateinit var db: AppDatabase
    private val loggingAgentFactory: LoggingAgent.Factory by inject(LoggingAgent.Factory::class.java)

    @Test
    fun enableNewLoggingFeatureFlag_returnCorrectFlag() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        FeatureHelper.enableNewLoggingFlag(context)
        Assert.assertEquals(FeatureHelper.getLoggingFlag(context), FeatureFlag.NEW_LOGGING)
    }

    @Test
    fun whenFeatureSetToOldLogging_injectedCorrectLogger() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            Assert.assertEquals(loggingAgentFactory is OldLoggerAgent.Factory, true)
        }
    }

    @Test
    fun whenFeatureSetToNewLogging_injectedCorrectLogger() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        FeatureHelper.disableNewLoggingFlag(context)
        FeatureHelper.enableNewLoggingFlag(context)
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->

            Assert.assertEquals(loggingAgentFactory is Logger.Factory, true)
        }
    }



}