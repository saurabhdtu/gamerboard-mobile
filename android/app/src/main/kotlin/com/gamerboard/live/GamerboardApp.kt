package com.gamerboard.live

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ComponentCallbacks2
import android.content.Intent
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.gamerboard.live.common.BroadcastFilters
import com.gamerboard.live.common.IntentKeys
import com.gamerboard.live.common.PrefsHelper
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.di.productionModule
import com.gamerboard.live.utils.FeatureHelper
import com.gamerboard.live.utils.memoryToMB
import com.gamerboard.logger.log
import com.gamerboard.logging.GBLog
import com.gamerboard.logging.LoggingAgent
import com.gamerboard.logging.utils.UuidHelper
import com.onesignal.OSNotificationOpenedResult
import com.onesignal.OneSignal
import io.branch.referral.Branch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.io.File
import java.util.Date


/**
 * Created by saurabh.lahoti on 13/08/21
 */

open class GamerboardApp : MultiDexApplication(), ComponentCallbacks2 {
    val deviceId: String
        @SuppressLint("HardwareIds")
        get() = PrefsHelper(this).getString(
            SharedPreferenceKeys.UUID
        ) ?: Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    private val loggingAgentFactory: LoggingAgent.Factory by inject()

    companion object {
        lateinit var instance: GamerboardApp
        val sessionId: String by lazy {
            UuidHelper.identifier()
        }
        var result: OSNotificationOpenedResult? = null
    }

    private fun getAvailableMem(): Float {
        val actManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        return memInfo.availMem.toFloat().memoryToMB()
    }

    override fun onTrimMemory(level: Int) {
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
                Log.d("MEMORY", "trim memory ui hidden: ${getAvailableMem()}")
                log("MEMORY trim memory ui hidden: ${getAvailableMem()}")
            }

            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> {
                Log.d("MEMORY", "trim memory ui hidden: ${getAvailableMem()}")
                log("MEMORY trim memory ui hidden: ${getAvailableMem()}")
            }

            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> {
                Log.d("MEMORY", "trim memory running low: ${getAvailableMem()}")
                log("MEMORY trim memory running low: ${getAvailableMem()}")
            }

            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> {
                Log.d("MEMORY", "trim memory running critical: ${getAvailableMem()}")
                log("MEMORY trim memory running critical: ${getAvailableMem()}")
                freeUpMemoryFromStateMachine()
            }

            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> {
                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */
                Log.d("MEMORY", "trim memory background")
                log("MEMORY trim memory background")
            }

            ComponentCallbacks2.TRIM_MEMORY_MODERATE -> {
                Log.d("MEMORY", "trim memory moderate")
                log("MEMORY trim memory moderate")
            }

            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                Log.d("MEMORY", "trim memory complete: ")
                log("MEMORY trim memory complete: ${getAvailableMem()}")
                freeUpMemoryFromStateMachine()
            }

            else -> {
                /*
                  Release any non-critical data structures.

                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
            }
        }
        super.onTrimMemory(level)
    }

    private fun freeUpMemoryFromStateMachine() {
        sendBroadcast(Intent(BroadcastFilters.SERVICE_COM).apply {
            putExtra(
                "action",
                "memory_critical"
            )
            putExtra("memory", getAvailableMem())
        })
    }

    val prefsHelper: PrefsHelper by lazy { PrefsHelper(this) }
    override fun onCreate() {
        super.onCreate()
        instance = this
        // Branch logging for debugging
        if (BuildConfig.DEBUG) Branch.enableTestMode()
        // Branch object initialization
        Branch.getAutoInstance(this)
        FeatureHelper.enableNewLoggingFlag(this)
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(this)
        OneSignal.setAppId(BuildConfig.ONE_SIGNAL_KEY)
        OneSignal.promptForPushNotifications()
        OneSignal.setNotificationOpenedHandler { result ->
            if (result.notification.launchURL == null) {
                GamerboardApp.result = result
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra(IntentKeys.NOTIFICATION_CLICKED, true)
                if (result.notification.additionalData != null) intent.putExtra(
                    "metaData", result.notification.additionalData.toString()
                )
            }
        }

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            androidContext(this@GamerboardApp)
            modules(
                productionModule
            )
        }
        GBLog.init(loggingAgentFactory)


        var sessionCount = prefsHelper.getInt(SharedPreferenceKeys.SESSION_COUNT)
        prefsHelper.putInt(SharedPreferenceKeys.SESSION_COUNT, ++sessionCount)

        if (prefsHelper.getString(SharedPreferenceKeys.UUID) == null) {
            val deviceID = Settings.Secure.getString(
                this.contentResolver, Settings.Secure.ANDROID_ID
            )
            prefsHelper.putString(
                SharedPreferenceKeys.UUID, deviceID
            )
            prefsHelper.putString(SharedPreferenceKeys.APP_STATE, "installed_new")
            prefsHelper.putInt(SharedPreferenceKeys.LAST_VERSION, BuildConfig.VERSION_CODE)
        } else {
            if (prefsHelper.getInt(SharedPreferenceKeys.LAST_VERSION) == BuildConfig.VERSION_CODE) {
                prefsHelper.putString(SharedPreferenceKeys.APP_STATE, "installed_same")
            } else {
                prefsHelper.putString(SharedPreferenceKeys.APP_STATE, "installed_updated")
                prefsHelper.putInt(SharedPreferenceKeys.LAST_VERSION, BuildConfig.VERSION_CODE)
            }
        }

    }

    override fun onTerminate() {
        super.onTerminate()
        GBLog.terminate()
    }

    val stackTraceFile by lazy {
        File(
            applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "stack_trace_${Date()}.txt"
        ).apply {
            if (exists().not()) {
                createNewFile()
            }
        }
    }

    fun writeStacktrace(message: String) {
        stackTraceFile.appendText(message)
        stackTraceFile.appendText("\n")
    }

}