package com.gamerboard.live.utils

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.MainActivity
import com.gamerboard.live.common.GAME_PACKAGES
import com.gamerboard.live.common.RemoteConfigConstants
import com.gamerboard.live.common.SYSTEM_PACKAGES
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.service.screencapture.ui.ServiceUIHelper
import com.gamerboard.logger.gson
import com.gamerboard.logger.log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import java.lang.ref.WeakReference
import java.util.SortedMap
import java.util.TreeMap
import kotlin.math.min

class PackageDetectionHelper private constructor(
    ctx: Context,
    private val askAdditionalPermission: Boolean,
    private val serviceUIHelper: ServiceUIHelper?
) : KoinComponent {

    private val LOG_TAG = PackageDetectionHelper::class.simpleName
    private val QUERY_INTERVAL_SEC = 6000
    private var screenRecordingEnabled = false
    val prefsHelper = (ctx.applicationContext as GamerboardApp).prefsHelper
    val context= WeakReference(ctx)
    private var lastPackageCheck: Long = System.currentTimeMillis()
    private var lastPermissionCheck: Long = System.currentTimeMillis()
    private var serviceStartTime: Long = System.currentTimeMillis()
    private var RESTRICTED_PACKAGES = arrayOf<String>()
    private val usageStatsManager =
        ctx.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    var actualPackage: String? = null

    init {
        loadRestrictedPackagesIfEmpty()
    }


    private var fetchingResultsStartTS: Long = 0
    private var fetchingResultsEndTS: Long = 0
    private var packageMapDuringResults = hashMapOf<String, Long>()

    companion object {

        var instance: PackageDetectionHelper? = null
        fun getInstance(
            ctx: Context,
            askAdditionalPermission: Boolean,
            serviceUIHelper: ServiceUIHelper?
        ): PackageDetectionHelper {
            val inst =
                (instance ?: PackageDetectionHelper(ctx, askAdditionalPermission, serviceUIHelper))
            instance = inst
            return inst
        }
    }


    private fun reset() {
        fetchingResultsStartTS = 0
        fetchingResultsEndTS = 0
        packageMapDuringResults.clear()
    }

    fun stop(){
        context.clear()
        instance = null
    }

    fun startedFetchingResult() {
        log {
            it.setMessage("Started fetching result")
        }
        fetchingResultsStartTS = System.currentTimeMillis()
    }

    fun endedFetchingResult() {
        log {
            it.setMessage("ended fetching result")
        }
        fetchingResultsEndTS = System.currentTimeMillis()
        Log.d(
            LOG_TAG, packageMapDuringResults.toString()
        )
    }

    fun getWildPackages(): List<Pair<String, Long>> {
        queryPackages()?.let { evaluatePackagesDuringResultScreens(it) }
        var list = packageMapDuringResults.entries.sortedByDescending { it.value }
            .map { Pair(it.key, it.value / 1000) }.toList()
        if (list.isNotEmpty())
            list = list.subList(0, min(3, list.size))
        reset()
        log {
            it.setMessage("getWildPackages: $list")
        }
        return list
    }

    private fun evaluatePackagesDuringResultScreens(packages: List<UsageStats>) {

        serviceStartTime =
            maxOf(
                packages.firstOrNull { it.packageName == MachineConstants.currentGame.packageName }?.lastTimeUsed
                    ?: serviceStartTime, serviceStartTime
            )
        if (fetchingResultsStartTS > 0)
            packages.filter {
                it.lastTimeUsed > serviceStartTime
                        && (it.lastTimeUsed < (if (fetchingResultsEndTS == 0L) System.currentTimeMillis() else fetchingResultsEndTS) + 10000L)
                        && mutableListOf<String>().apply {
                    this.addAll(SYSTEM_PACKAGES)
                    this.addAll(GAME_PACKAGES)
                    context.get()?.packageName?.let { pckg -> this.add(pckg) }
                }.toTypedArray().isPartOf(it.packageName).not()
            }.let { filtered ->
                filtered.forEach {
                    packageMapDuringResults[it.packageName] = it.totalTimeInForeground
                    Log.d(
                        LOG_TAG,
                        "total: ${filtered.size}; packageName: ${it.packageName}; firstTimeStamp:${it.firstTimeStamp}; lastTimeUsed:${it.lastTimeUsed};  totalTimeInForeground:${it.totalTimeInForeground}"
                    )
                }
            }
        log {
            it.setMessage("evaluatePackagesDuringResultScreens: $packageMapDuringResults")
        }
    }

    private fun queryPackages(): MutableList<UsageStats>? {
        val time = System.currentTimeMillis()
        return usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, time - QUERY_INTERVAL_SEC, time
        )
    }

    fun canRecord(): Boolean {
        return BuildConfig.IS_TEST || (!askAdditionalPermission
            || ((System.currentTimeMillis() - lastPackageCheck < 6000) || checkScreenRecording()))
        /*if (System.currentTimeMillis() - lastPackageCheck > 6000)
            checkScreenRecording()
        return true*/

    }

    private fun loadRestrictedPackagesIfEmpty() {
        if (RESTRICTED_PACKAGES.isEmpty()) {
            RESTRICTED_PACKAGES = gson.fromJson(
                FirebaseRemoteConfig.getInstance()
                    .getString(RemoteConfigConstants.RESTRICTED_PACKAGES), Array<String>::class.java
            )
        }

    }

    private fun checkScreenRecording(): Boolean {
        var tempRecordingStatus = false
        lastPackageCheck = System.currentTimeMillis()
        if (System.currentTimeMillis() - lastPermissionCheck > QUERY_INTERVAL_SEC && !appUsageAccessGranted()) {
            //stopping the service
            lastPermissionCheck = System.currentTimeMillis()
            CoroutineScope(Dispatchers.Main).launch {
                context.get()?.let {
                    UiUtils.showToast(
                        it, "App usage stats permission not available", null
                    )
                }
            }
            return false
        }
        val logString = ""

        queryPackages()?.let { queryResult ->
            if (queryResult.isNotEmpty()) {
                val mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
                for (usageStats in queryResult) {
                    mySortedMap[usageStats.lastTimeUsed] = usageStats
                }
                evaluatePackagesDuringResultScreens(queryResult)
                val observedPackage = mySortedMap[mySortedMap.lastKey()]?.packageName.toString()

                logString.plus("obsv_pckg: $observedPackage | ")


                if (RESTRICTED_PACKAGES.isPartOf(observedPackage)) {
                    tempRecordingStatus = false
                    actualPackage = observedPackage
                } else {
                    if (GAME_PACKAGES.isPartOf(observedPackage) && screenRecordingEnabled) {
                        logString.plus("is game | ")
                        actualPackage = observedPackage
//                            logger.log(logString)
                        return screenRecordingEnabled
                    } else {
                        tempRecordingStatus = gbAppOnOverlay(
                            observedPackage
                        )
                        if (tempRecordingStatus.not()) {
                            // if the obtained package is
                            //1. Not the BGMI game on screen
                            //2. BGMI is active on screen but the observed package is of some other app due to floating windows
                            val time = System.currentTimeMillis()
                            usageStatsManager.let { usm ->
                                val queryEvents = usm.queryEvents(
                                    time - QUERY_INTERVAL_SEC, time
                                )
                                val map = hashMapOf<String, HashSet<String>?>()
                                val packages = mutableListOf<String>()
                                var mostRecentActive: String? = null
                                var mostRecentActiveTS = 0L
                                var mostRecentInactive: String? = null
                                var mostRecentInactiveTS = 0L
                                while (queryEvents.hasNextEvent()) {
                                    val event = UsageEvents.Event()
                                    queryEvents.getNextEvent(event)
                                    if (event.packageName != null) {
                                        //identify active packages
                                        packages.add(event.packageName)
                                        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) || event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                                            if (map["active"] == null) {
                                                map["active"] = hashSetOf(event.packageName)
                                            } else {
                                                map["active"]?.add(event.packageName)
                                            }
                                            if (event.timeStamp > mostRecentActiveTS) {
                                                mostRecentActive = event.packageName
                                                mostRecentActiveTS = event.timeStamp
                                            }
                                            logString.plus("pckg: ${event.packageName}; ts: ${event.timeStamp}; A | ")
                                            Log.d(
                                                "package-test",
                                                "pckg: ${event.packageName}; ts: ${event.timeStamp}; active"
                                            )
                                        }//identify inactive packages
                                        else {
                                            if (map["inactive"] == null) {
                                                map["inactive"] = hashSetOf(event.packageName)
                                            } else {
                                                map["inactive"]?.add(event.packageName)
                                            }
                                            if (event.timeStamp > mostRecentInactiveTS) {
                                                mostRecentInactive = event.packageName
                                                mostRecentInactiveTS = event.timeStamp
                                            }
                                            Log.d(
                                                "package-test",
                                                "pckg: ${event.packageName}; ts: ${event.timeStamp}; IA"
                                            )
                                            logString.plus("pckg: ${event.packageName}; ts: ${event.timeStamp}; IA | ")
                                        }
                                    }
                                }
                                logString.plus("MR-active $mostRecentActive MR-inactive $mostRecentInactive | ")
                                if (GAME_PACKAGES.contains(mostRecentActive)) {
                                    logString.plus("is game")
                                    tempRecordingStatus = true
                                    actualPackage = mostRecentActive
                                } else {
                                    if (map.isEmpty() || (packages.contains(GAME_PACKAGES.first())
                                            .not() && packages.contains(observedPackage).not())
                                    ) {
                                        // if no events are triggered or some random events are triggered excluding the concerned packages because of interaction
                                        //with notification or elsewhere
                                        tempRecordingStatus = screenRecordingEnabled
                                    } else {
                                        if (RESTRICTED_PACKAGES.isPartOf(mostRecentActive) || (mostRecentActive == mostRecentInactive && !GAME_PACKAGES.contains(
                                                mostRecentActive
                                            ))
                                        ) {
                                            actualPackage = mostRecentActive
                                            tempRecordingStatus = false
                                        } else {
                                            if (SYSTEM_PACKAGES.isPartOf(observedPackage)) {
                                                logString.plus("is system pkg")
                                                tempRecordingStatus = screenRecordingEnabled
                                            } else {
                                                // in case of notification interactions
                                                //                                    Log.d("package-test", Gson().toJson(map))
                                                //if the active package is valid package or inactive package is the observed package
                                                //inactive should contain the observed package, at the same time observed package should  not be present in the active packages or active packages should be empty
                                                //sometimes its found that when switching between apps observed package comes in inactive pckgs and then in active pckgs with
                                                //some little difference in TS, the latest being in the active one. So
                                                val result =
                                                    map["active"]?.contains(GAME_PACKAGES.first()) == true || (map["inactive"]?.contains(
                                                        observedPackage
                                                    ) == true && (map["active"] == null || map["active"]?.isEmpty() == true || map["active"]?.contains(
                                                        observedPackage
                                                    ) == false))
                                                if (map["active"] != null || map["inactive"] != null) {
                                                    actualPackage = if (result.not()) {
                                                        observedPackage
                                                    } else {
                                                        GAME_PACKAGES.first()
                                                    }
                                                    tempRecordingStatus = result
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        } else actualPackage = observedPackage
                    }
                }
                logString.plus("Act_pckg: $actualPackage | obsv_pckg: $observedPackage | ")
                Log.d("package-test", "Act_pckg: $actualPackage | obsv_pckg: $observedPackage")
            } else {
                tempRecordingStatus = screenRecordingEnabled
                logString.plus("stats empty | ")
            }
        }
        context.get()?.sendBroadcast(Intent().apply {
            putExtra("action", "recording_status")
            putExtra("status", if (screenRecordingEnabled) 1 else 0)
            putExtra("package", if (screenRecordingEnabled) actualPackage else 0)
        })
        screenRecordingEnabled = tempRecordingStatus
        logString.plus("recordingEnabled:$screenRecordingEnabled | ")
        Log.d("package-test", "recordingEnabled:$screenRecordingEnabled")
//        if (screenRecordingEnabled.not())
        log(logString)

        return screenRecordingEnabled

    }

    private fun appUsageAccessGranted(): Boolean {
        val result = try {
            val applicationInfo = GamerboardApp.instance.packageManager.getApplicationInfo(
                GamerboardApp.instance.packageName, 0
            )
            val appOpsManager: AppOpsManager? =
                GamerboardApp.instance.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager?
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOpsManager?.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid,
                    applicationInfo.packageName
                )
            } else {
                appOpsManager?.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid,
                    applicationInfo.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        log {
            it.setMessage("App usage permission result")
            it.addContext("orientation", serviceUIHelper?.orientation?.name)
            it.addContext("permission_granted", result)
        }
        return result
    }

    private fun gbAppOnOverlay(pkg: String): Boolean {
        val onForeground = MainActivity.gbOnForeground
        return (pkg == BuildConfig.APPLICATION_ID && !onForeground)
    }

}

private fun <T> Array<T>.isPartOf(observedPackage: String?): Boolean {
    var found = false
    this.forEach {
        if (!found && observedPackage?.contains(it.toString()) == true) {
            found = true
        }
    }
    return found
}
