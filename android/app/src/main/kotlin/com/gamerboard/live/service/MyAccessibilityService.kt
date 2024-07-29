package com.gamerboard.live.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.MainActivity
import com.gamerboard.live.common.BroadcastFilters
import com.gamerboard.live.common.GAME_PACKAGES
import com.gamerboard.live.common.SYSTEM_PACKAGES
import com.gamerboard.logger.Logger
import com.gamerboard.logger.log
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


/**
 * Created by saurabh.lahoti on 27/10/21
 */
class MyAccessibilityService : AccessibilityService(), KoinComponent {
    val LOG_TAG = "MyAccessibilityService"
    var recordScreen = false
    private val logger: Logger by inject()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            var canIgnore = false
            val eventPackage = it.packageName
            log("onAccessibilityEvent: A event package $eventPackage")
            if (eventPackage != null) {
                for (pck in SYSTEM_PACKAGES)
                    if (eventPackage.contains(pck)) {
                        canIgnore = true
                        break
                    }

                log("onAccessibilityEvent: B canIgnore=$canIgnore  recordScreen=$recordScreen")

                if (GAME_PACKAGES.contains(eventPackage) || (recordScreen && canIgnore) || gbAppOnOverlay(
                        eventPackage as String
                    )
                ) {
                    recordScreen = true
                    sendBroadcast(Intent(BroadcastFilters.SERVICE_COM).apply {
                        putExtra("action", "system_event")
                        putExtra("record", recordScreen)
                        putExtra("package", eventPackage as String)
                        log("onAccessibilityEvent: Recording $recordScreen")

//                    putExtra("package", eventPackage)
                    })
                } else {
                    recordScreen = false
                    sendBroadcast(Intent(BroadcastFilters.SERVICE_COM).apply {
                        putExtra("action", "system_event")
                        putExtra("record", recordScreen)
                        putExtra("package", "Un-Known")

                        log("onAccessibilityEvent: Recording $recordScreen")
                    })
                }
            }
        }
        Log.i(
            LOG_TAG,
            "eventType: ${event?.eventType}; package:${event?.packageName}; action:${event?.action}; count:${event?.itemCount}"
        )
    }

    private fun gbAppOnOverlay(pkg: String): Boolean {
        val onForeground = MainActivity.gbOnForeground
        return (pkg == BuildConfig.APPLICATION_ID && !onForeground)
    }


    override fun onInterrupt() {

    }
}