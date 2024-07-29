package com.gamerboard.live.service.screencapture.ui

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.R
import com.gamerboard.live.common.BroadcastFilters
import com.gamerboard.live.common.IntentKeys
import com.gamerboard.live.common.IntentRequestCode
import com.gamerboard.live.common.PermissionRequestCode
import com.gamerboard.live.gamestatemachine.showToast
import com.gamerboard.live.gamestatemachine.stateMachine.Event
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachine
import com.gamerboard.live.repository.ApiClient
import com.gamerboard.live.repository.ModelParameterManager
import com.gamerboard.live.service.screencapture.AutoDebuggingHelper
import com.gamerboard.live.service.screencapture.FileAndDataSync
import com.gamerboard.live.service.screencapture.ScreenCaptureService
import com.gamerboard.live.service.screencapture.VideoTestObj
import com.gamerboard.live.utils.*
import com.gamerboard.logger.Logger
import com.gamerboard.logger.UploadLogService
import com.gamerboard.logger.log
import com.google.firebase.storage.FirebaseStorage
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.roundToInt


/**
 * Created by saurabh.lahoti on 04/08/21
 */
class ServiceManager(
    var activity: Activity?,
    var ctx: Context?,
    val localMethodChannel: MethodChannel?,
) : LifecycleObserver, KoinComponent {

    private var callBackHandle: Long? = null
    private var mCallbackDispatcherHandle: Long? = null
    private var askAdditionalPermission = false
    private var appRestarted = false
    private var gameProfileName: String? = null

    private val modelParameterManager: ModelParameterManager by inject()

    val logger: Logger by inject()

    var serviceRunning = false
    var gameProfileId: String? = null
    var packageName: String? = null

    companion object {
        fun checkAndDownloadModel(
                    modelParameterManager: ModelParameterManager,
                    packageName: String,
                    callback: (String?) -> Unit,
                context: Context?,
                apiClient: ApiClient?
                ) {

                var gameModel: String? = null
                CoroutineScope(Dispatchers.Main).launch {
                val success = modelParameterManager.getModelParam(apiClient, packageName)
                if (!success) {
                    if (context is Activity && context.isFinishing.not()) {
                        UiUtils.showToast(
                            context,
                            context.resources.getString(R.string.unable_to_connect_to_server),
                            Toast.LENGTH_LONG
                        )
                    }
                    return@launch
                }

                gameModel = MachineConstants.gameConstants.gameModelURL()

//            val gameModel = (when (packageName) {
//                SupportedGames.BGMI.packageName ->
//                    SupportedGames.BGMI
//
//                SupportedGames.FREEFIRE.packageName ->
//                    SupportedGames.FREEFIRE
//
//                else -> SupportedGames.BGMI
//            }).modelURL
                val fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(gameModel!!)
                context?.let {
                    if (FileAndDataSync.getModelFile(it, fileRef.name).exists())
                        callback.invoke(null)
                    else {
                        val tempFile = File("${it.getExternalFilesDir(null)?.path}/temp_file.tmp")
                        var dialog: Dialog? = null
                        if (context is Activity && context.isFinishing.not()) {
                            dialog = DialogHelper.showDownloadProgress(it, dismissible = true)
                        }
                        fileRef.getFile(tempFile).addOnSuccessListener { _ ->
                            if (dialog?.isShowing == true && (context is Activity && context.isFinishing.not()))
                                dialog.dismiss()
                            tempFile.renameTo(FileAndDataSync.getModelFile(it, fileRef.name))
                            callback.invoke(null)
                        }.addOnFailureListener { ex ->
                            if (dialog?.isShowing == true)
                                dialog.dismiss()
                            callback.invoke(ex.message)
                        }.addOnProgressListener { task ->
                            val percentage =
                                ((task.bytesTransferred.toFloat() / task.totalByteCount.toFloat()) * 100).roundToInt()
                            dialog?.findViewById<ProgressBar>(R.id.progressbar)?.progress =
                                percentage
                            dialog?.findViewById<TextView>(R.id.tv_progress)?.text =
                                "$percentage%"
                        }
                    }
                }
            }
        }

        suspend fun checkAndDownloadModelAwait(
            modelParameterManager: ModelParameterManager,
            packageName: String,
            context: Context?,
            apiClient: ApiClient?
        ) = suspendCoroutine { continuation ->
            checkAndDownloadModel(
                modelParameterManager = modelParameterManager,
                packageName = packageName,
                context = context,
                apiClient = apiClient,
                callback = {
                    continuation.resume(it)
                })
        }
    }

    private val permissionManager: PermissionManager? by lazy {
        activity?.let {
            PermissionManager(
                it,
                this
            )
        }
    }

    private val serviceStatusReceiver = ServiceBroadcastReceiver()
    private val serviceStatusFilter = IntentFilter(BroadcastFilters.RESPONSE_CAPTURE_SERVICE)
    private val sessionManager = GamerboardApp.instance.prefsHelper

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resume() {
        activity?.registerReceiver(serviceStatusReceiver, serviceStatusFilter)

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pause() {
        activity?.unregisterReceiver(serviceStatusReceiver)
    }

    fun checkPermissionsForService(
        gameProfileId: String?,
        gameProfileName: String?,
        askAdditionalPermission: Boolean?,
        packageName: String?,
        appRestarted : Boolean = false
    ) {
        this.appRestarted = appRestarted
        val apiClient: ApiClient by inject()
        this.gameProfileId = gameProfileId ?: this.gameProfileId
        this.gameProfileName = gameProfileName ?: this.gameProfileName
        this.packageName = packageName ?: this.packageName
        if (this.askAdditionalPermission != askAdditionalPermission && askAdditionalPermission != null) {
            this.askAdditionalPermission = askAdditionalPermission
            permissionManager?.askAdditionalPermission = askAdditionalPermission
        }
        /*UserHandler.fetchedBGMINumericId = null
        UserHandler.originalBGBICharacterID = null*/

        log("Started permission check for service")
        try {
            this.packageName?.let { pckg ->
                activity?.let { act ->
                    checkAndDownloadModel(modelParameterManager, pckg, { msg ->
                        if (msg == null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && PermissionHelper.getPermissionStatus(
                                    sessionManager,
                                    act,
                                    Manifest.permission.FOREGROUND_SERVICE
                                ) != PermissionHelper.GRANTED
                            ) {
                                log("Requested foreground permission")
                                PermissionHelper.requestPermissions(
                                    arrayOf(Manifest.permission.FOREGROUND_SERVICE),
                                    PermissionRequestCode.FOREGROUND_SERVICE,
                                    act
                                )
                                return@checkAndDownloadModel
                            }
                            if (permissionManager?.checkPermissions() == true) {
                                if (!isGameInstalled(act, pckg))
                                    return@checkAndDownloadModel
                                checkServiceStatus {
                                    if (!it) {
                                        AlertDialog.Builder(act)
                                            .setTitle("Battery permission")
                                            .setMessage(R.string.allow_consumption_in_background)
                                            .setPositiveButton(
                                                R.string.grant_permission
                                            ) { dialog, _ ->
                                                if (act.isFinishing.not()) {
                                                    dialog.dismiss()
                                                }
                                                act.getSystemService(Context.MEDIA_PROJECTION_SERVICE)
                                                    ?.let { service ->
                                                        val mpm = service as MediaProjectionManager
                                                        val intent = mpm.createScreenCaptureIntent()

                                                        act.startActivityForResult(
                                                            intent,
                                                            IntentRequestCode.MEDIA_PROJECTION_RC
                                                        )
                                                    }
                                            }.create().apply {
                                                try {
                                                    getButton(DialogInterface.BUTTON_POSITIVE)?.isAllCaps =
                                                        false
                                                } catch (ex: Exception) {
                                                    logException(ex)
                                                }
                                                if(!act.isFinishing) {
                                                    show()
                                                }
                                            }
                                    } else {
                                        if (MachineConstants.isGameInitialized() &&
                                            MachineConstants.currentGame.packageName != pckg
                                        ) {
                                            stopService("changed game")
                                            checkPermissionsForService(
                                                gameProfileId,
                                                gameProfileName,
                                                askAdditionalPermission,
                                                packageName
                                            )
                                        } else {
                                            launchGame(act, pckg)
                                        }
                                    }
                                }
                            }
                        } else {
                            UiUtils.showToast(act, msg, null)
                        }
                    }, act, apiClient)
                }
            }
        } catch (ex: Exception) {
            logException(ex)
        }
    }

    fun checkServiceStatus(callback: (Boolean) -> Unit) {
        val intent = Intent(BroadcastFilters.SERVICE_COM)
        intent.putExtra("action", "check_status")
        ctx?.sendBroadcast(intent)
        CoroutineScope(Dispatchers.Main).launch {
            delay(300)
            if (!serviceRunning) {
                callback(false)
            } else {
                callback(true)
                serviceRunning = false
            }
        }
    }

    fun stopService(stoppedVia: String) {

        ctx?.apply {
            stopService(Intent(ctx, ScreenCaptureService::class.java))
            UploadLogService.stop(this)
        }
        // this will finish the game if exists.
        //StateMachine.machine.transition(Event.ServiceStopped(stoppedVia))
        StateMachine.machine.transition(Event.GameCompleted(stoppedVia, executeInBackground = true))
    }

    private fun startService(data: Intent) {
        try {
            val bundle = data.extras
            bundle?.let {
                val bundleKeySet: Set<String> = it.keySet() // string key set

                for (key in bundleKeySet) { // traverse and print pairs
                    Log.i(key, " : " + it.get(key))
                }
            }

            ctx?.let {
                val serviceIntent = Intent(ctx, ScreenCaptureService::class.java)
                serviceIntent.putExtras(data.extras!!)
                serviceIntent.putExtra(
                    ServiceIntentKeys.DISPLAY_DENSITY,
                    ctx!!.resources.displayMetrics.densityDpi
                )
                if (!BuildConfig.IS_TEST) {
                    serviceIntent.putExtra(ServiceIntentKeys.GAME_PROFILE_ID, gameProfileId)
                    serviceIntent.putExtra(ServiceIntentKeys.GAME_USERNAME, gameProfileName)
                } else {
                    // for video testing
                    serviceIntent.putExtra(ServiceIntentKeys.GAME_PROFILE_ID, "55501298194")
                }
                serviceIntent.putExtra(
                    ServiceIntentKeys.CALLBACK_DISPATCHER_HANDLE_KEY,
                    mCallbackDispatcherHandle
                )
                serviceIntent.putExtra(
                    ServiceIntentKeys.IGNORE_PERMISSION,
                    askAdditionalPermission
                )
                serviceIntent.putExtra(IntentKeys.APP_RESTARTED , appRestarted)
                serviceIntent.putExtra(ServiceIntentKeys.CALLBACK_HANDLE, callBackHandle)
                appRestarted = false

                if (!BuildConfig.IS_TEST && !isGameInstalled(ctx!!, packageName!!))
                    return
                ContextCompat.startForegroundService(ctx!!, serviceIntent)

                // this will finish the game if exists.
                //StateMachine.machine.transition(Event.ServiceStopped(stoppedVia = "Service was re initialized!"))
                StateMachine.machine.transition(
                    Event.GameCompleted(
                        "Service was re initialized!",
                        executeInBackground = true
                    )
                )

                log("Service started")

                if (!BuildConfig.IS_TEST) launchGame(it, packageName!!)
                else {
                    StateMachine.machine.transition(Event.VerifyUser("test", "test"))
                    // create a fresh test file
                    val fileFailedTest =
                        File("${ctx!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/tests_output/failed_tests.txt")
                    if (fileFailedTest.exists())
                        fileFailedTest.delete()

                    // for video testing
                    val files =
                        File("${ctx!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/video_testing").listFiles()
                            ?: return
                    val videoTests = arrayListOf<VideoTestObj>()
                    for (file in files) {
                        val desc = File(file.path, "desc.txt").readText()
                        val videoTestObj = VideoTestObj(
                            videoTestName = desc,
                            videoTestFilePath = "${file.path}/video.mp4",
                            groundTruthFilePath = "${file.path}/ground_truth.json",
                            outputDataFilePath = "${file.path}/outputData.json"
                        )
                        videoTests.add(videoTestObj)
                    }
                    videoTests.sortBy { video -> video.videoTestFilePath }

                    val singleTest = ""
                    if (singleTest.isNotEmpty()) {
                        videoTests.removeIf { videoToRemove ->
                            !videoToRemove.videoTestFilePath.contains(
                                singleTest
                            )
                        }
                    }
                    AutoDebuggingHelper().callForVideoTestOnFile((activity as Context), videoTests)
                }
            }

        } catch (e: Exception) {
            log(e.toString())
        }
    }

    private fun isGameInstalled(ctx: Context, packageName: String): Boolean {
        return try {
            ctx.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            showToast("Install game to proceed!", force = true)
            false
        }
    }

    private fun launchGame(ctx: Context, packageName: String) {
        val pm: PackageManager = ctx.packageManager
        try {
            val intent = pm.getLaunchIntentForPackage(packageName)
            activity?.startActivity(intent)
            EventUtils.instance()
                .logAnalyticsEvent(Events.GAME_LAUNCHED, mapOf("package" to packageName))
            log {
                it.setMessage("Launched game")
                it.addContext("game", packageName)
            }
            activity?.finish()
        } catch (ex: Exception) {
            logException(ex)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IntentRequestCode.MEDIA_PROJECTION_RC && resultCode == Activity.RESULT_OK) {
            if (data != null && data.extras != null && ctx != null) {
                log { builder ->
                    builder.setMessage("onActivityResult for media projection request")
                    builder.addContext("extras", data.extras.toString())
                }

                startService(data)

                ctx?.let { context ->
                    UploadLogService.start(context)
                }
            }
        }
        permissionManager?.onActivityResult(requestCode, resultCode, data)
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PermissionRequestCode.FOREGROUND_SERVICE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkPermissionsForService(gameProfileId, gameProfileName, null, packageName)
        }
    }

    inner class ServiceBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getStringExtra("action")) {
                BroadcastFilters.RESPONSE_CAPTURE_SERVICE -> {
                    localMethodChannel?.let {
                        localMethodChannel.invokeMethod(
                            "service_status",
                            hashMapOf(
                                "is_service_active" to intent.getBooleanExtra(
                                    "is_service_active",
                                    false
                                )
                            )
                        )
                    }
                }

                "service_status" -> {
                    serviceRunning = intent.getBooleanExtra("status", false)
                }
            }
        }
    }
}

object ServiceIntentKeys {
    const val DISPLAY_DENSITY = "display_density"
    const val CALLBACK_DISPATCHER_HANDLE_KEY = "callback_dispatcher_handle"
    const val CALLBACK_HANDLE = "callback_handle"
    const val GAME_PROFILE_ID = "GAME_PROFILE_ID"
    const val GAME_USERNAME = "GAME_USERNAME"
    const val IGNORE_PERMISSION = "IGNORE_PERMISSION"
}
