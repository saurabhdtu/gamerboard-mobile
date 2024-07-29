package com.gamerboard.live

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.amplitude.api.Amplitude
import com.example.otpless_flutter.OtplessFlutterPlugin
import com.gamerboard.live.common.*
import com.gamerboard.live.gamestatemachine.showToast
import com.gamerboard.live.gamestatemachine.stateMachine.Event
import com.gamerboard.live.gamestatemachine.stateMachine.State
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachine
import com.gamerboard.live.gamestatemachine.stateMachine.VisionStateMachine
import com.gamerboard.live.models.FeedBackFrom
import com.gamerboard.live.models.UTMParams
import com.gamerboard.live.models.db.AppDatabase
import com.gamerboard.live.models.db.Game
import com.gamerboard.live.repository.ApiClient
import com.gamerboard.live.repository.GameManager
import com.gamerboard.live.repository.SessionManager
import com.gamerboard.live.service.screencapture.*
import com.gamerboard.live.service.screencapture.ui.OnBoardingStep
import com.gamerboard.live.service.screencapture.ui.ServiceManager
import com.gamerboard.live.test.constants.TestConfig
import com.gamerboard.live.test.game.VideoTestRunner
import com.gamerboard.live.testtool.TestProfileOnImages
import com.gamerboard.live.utils.*
import com.gamerboard.logger.ILogger
import com.gamerboard.logger.UploadLogService
import com.gamerboard.logger.UploadLogWorker
import com.gamerboard.logger.agent.OldLoggerAgent
import com.gamerboard.logger.log
import com.gamerboard.logger.logFlutter
import com.gamerboard.logging.GBLog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.scottyab.rootbeer.RootBeer
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.io.File
import java.util.*
import kotlin.system.exitProcess


open class MainActivity : FlutterActivity(), MethodChannel.MethodCallHandler {
    private var testVideo: VideoTestRunner? = null
    private lateinit var methodChannel: MethodChannel
    private lateinit var localMethodChannel: MethodChannel
    private val prefsHelper: PrefsHelper by inject()
    val logger: ILogger by inject()
    val apiClient: ApiClient by inject()
    val appDatabase: AppDatabase by inject()
    private var serviceManager: ServiceManager? = null
    private var sensorHelper: SensorHelper? = null

    var result: MethodChannel.Result? = null
    var path: String? = null
    private var nativeToFlutterReceiver = NativeBroadcastListener()
    private var downloadCompleteReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context, intent: Intent) {
            Log.e("download_complete", intent.data.toString())
            prefsHelper.getString(SharedPreferenceKeys.LATEST_DOWNLOADED_VERSION)?.let {
                UiUtils.showToast(
                    ctxt, "$it available in Downloads folder", null
                )
                apkFile(it)?.let { f ->
                    openFile(f.path, null)
                }
            }
        }
    }

    private var activityLaunched = false


    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {/*requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )*/

        Amplitude.getInstance().initialize(this, BuildConfig.AMPLITUDE_API_KEY)
            .enableForegroundTracking(this.application).enableLogging(true)
        super.onCreate(savedInstanceState)
        sensorHelper = SensorHelper(this)
        sensorHelper?.startListening()


        if (checkRootDevice().not()) {
            serviceManager = ServiceManager(this, this, null)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) window.attributes.layoutInDisplayCutoutMode =
                LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            val deviceID = prefsHelper.getString(SharedPreferenceKeys.UUID)
            deviceID?.let {
                Log.e("DEVICE-ID>>>", it)
                FirebaseCrashlytics.getInstance().setUserId(it)
                Amplitude.getInstance().deviceId = deviceID
            }
            activityLaunched = true
            when (prefsHelper.getString(SharedPreferenceKeys.APP_STATE)) {
                "installed_new" -> {
                    EventUtils.instance()
                        .logAnalyticsEvent(Events.APP_LAUNCHED, mapOf("type" to "fresh_install"))
                    log("app-launched: installed_new")
                    prefsHelper.putString(SharedPreferenceKeys.APP_STATE, "installed_same")
                }

                "installed_same" -> {
                    EventUtils.instance()
                        .logAnalyticsEvent(Events.APP_LAUNCHED, mapOf("type" to "normal"))
                    log("app-launched: installed_same")
                }

                "installed_updated" -> {
                    EventUtils.instance()
                        .logAnalyticsEvent(Events.APP_LAUNCHED, mapOf("type" to "app_updated"))
                    log("app-launched: installed_updated")
                    prefsHelper.putString(SharedPreferenceKeys.APP_STATE, "installed_same")
                }
            }
            val map = mapOf(
                "system_arch_1" to System.getProperty("os.arch"), "system_arch_2" to Build.CPU_ABI
            )
            Log.d("architecture", map.toString())
            EventUtils.instance().logUserProperties(map)


            val currentUser = Firebase.auth.currentUser
            if (currentUser == null) {
                Firebase.auth.signInAnonymously().addOnCompleteListener(this) { task ->
                    if (task.isSuccessful.not()) {
                        Log.w("SignIn", "signInAnonymously:failure", task.exception)
                        showToast("Sign In failed!", force = false)
                    }
                }
            }
            registerReceiver(
                nativeToFlutterReceiver, IntentFilter(BroadcastFilters.NATIVE_TO_FLUTTER)
            )
            registerReceiver(
                downloadCompleteReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
            setupAttributionData()
        }
       /*   TestProfileOnImages(GamerboardApp.instance,
              testVisionCall = true,
              ignoreUsernameMismatch = true
          ).apply {
               CoroutineScope(Dispatchers.IO).launch {
                   startTest()
               }
           }*/
        UploadLogWorker.start(context)

        if (TestConfig.shouldRunTest()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            testVideo = VideoTestRunner(this)
            testVideo?.runBGMIVideoTest {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    Toast.makeText(this@MainActivity, "Test Completed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun checkRootDevice(): Boolean {/*if(BuildConfig.DEBUG)
            return false*/
        if (BuildConfig.DEBUG) return false

        val rootBeer = RootBeer(this)
        val isRoot = rootBeer.isRooted || isEmulator()
        if (isRoot) {
            Toast.makeText(this, "Root device detected", Toast.LENGTH_LONG).show()
            AlertDialog.Builder(this).setTitle("Alert")
                .setMessage("Your device is rooted. Gamerboard application will not work on rooted device")
                .setCancelable(false).setPositiveButton(
                    "Ok"
                ) { dialog, id ->
                    exitProcess(0)
                }.show()
        }
        return isRoot
    }

    private fun setupAttributionData() {
        Firebase.dynamicLinks.getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                if (pendingDynamicLinkData != null) {
                    val deepLink = pendingDynamicLinkData.link
                    val utm = pendingDynamicLinkData.utmParameters
                    val redir = pendingDynamicLinkData.redirectUrl
                    val cts = pendingDynamicLinkData.clickTimestamp
                    if (prefsHelper.getString(SharedPreferenceKeys.UTM_PARAMS) == null) {
                        val utmParams = UTMParams(
                            utmCampaign = utm.getString("utm_campaign"),
                            utmSource = utm.getString("utm_source"),
                            utmMedium = utm.getString("utm_medium")
                        )
                        prefsHelper.putString(
                            SharedPreferenceKeys.UTM_PARAMS, Json.encodeToString(utmParams)
                        )
                    }

                    if (BuildConfig.DEBUG) UiUtils.showToast(
                        this@MainActivity, """
                            DL:$deepLink,
                            UTM:$utm,
                            re-direct:$redir,
                            cts:$cts
                        """.trimIndent(), Toast.LENGTH_LONG
                    )
                }
            }.addOnFailureListener(this) { e ->
                UiUtils.showToast(
                    this@MainActivity, "getDynamicLink:onFailure $e", null
                )
            }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (isBranchDeepLinkClicked()) {
            methodChannel.invokeMethod("branch_deep_link_clicked", null)
        }
        handleIntent()
        handleOtpLessIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            runGameEndLooper()
        }
    }

    private suspend fun runGameEndLooper() {
        if (StateMachine.machine.state !is State.FetchResult) return
        if (VisionStateMachine.visionImageSaver.state.ready) StateMachine.machine.transition(
            Event.GameCompleted(
                reason = "On Start, user switched the app!"
            )
        )
        else {
            delay(20)
            runGameEndLooper()
        }
    }

    override fun onDestroy() {
        CoroutineScope(Dispatchers.IO).launch {
            runGameEndLooper()
        }
        try {
            unregisterReceiver(nativeToFlutterReceiver)
            unregisterReceiver(downloadCompleteReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        gbOnForeground = false
        super.onDestroy()
    }

    companion object {
        var gbOnForeground: Boolean = false
        var currentVideoTest: VideoTestObj? = null
        var videoTests: ArrayList<VideoTestObj>? = null
    }

    override fun onResume() {
        super.onResume()
        gbOnForeground = true
        testVideo?.finish()
    }

    override fun onPause() {
        super.onPause()
        activityLaunched = false
        gbOnForeground = false

        //showToast("Query Auto ML?")
        //Machine.stateMachine.transition(GameEvent.OnGameEnd)
    }

    override fun onStop() {
        super.onStop()
        gbOnForeground = false
        sensorHelper?.stopListening()
        testVideo?.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            when (requestCode) {
                IntentRequestCode.OPEN_FILE_RC -> {
                    handleOpenFileRcResult(resultCode)
                }

                PermissionRequestCode.INSTALL_APP_UNKNOWN_SOURCE -> {
                    handleInstallAppUnknownSourceResult()
                }

                IntentRequestCode.TEST_VDO_COMPLETION -> {
                    handleVideoCompletionResult()
                }
            }


        } catch (ex: Exception) {
            logException(ex)
        }
    }

    private fun handleOpenFileRcResult(resultCode: Int) {
        result?.success(resultCode)
        result = null
    }

    private fun handleInstallAppUnknownSourceResult() {
        if (path == null) {
            result?.success(true)
            result = null
        } else {
            openFile(path, result)
        }
    }

    private fun handleVideoCompletionResult() {
        CoroutineScope(Dispatchers.IO).launch {

            val bundle = intent.getBundleExtra("Flag")

            if (currentVideoTest == null || videoTests == null) {
                showToast("Test videos were null, returning!", force = true)
                return@launch
            }

            // delay to wait for the auto ml query to finish
            delay(10 * 1000)
            showToast(
                "Comparing results for: ${currentVideoTest!!.videoTestName}", force = true
            )

            /*val currentVideoTest: VideoTestObj = Json.decodeFromString(data.getStringExtra(VideoTestConstants.CURRENT_VIDEO_TEST)!!)
                val videoTests: ArrayList<VideoTestObj> = Json.decodeFromString(data.getStringExtra(VideoTestConstants.VIDEO_TESTS)!!)*/

            FileAndDataSync(this@MainActivity.applicationContext).logCurrentSessionGamesToFile(
                outputFile = currentVideoTest!!.outputDataFilePath
            )

            // compare the results
            AutoDebuggingHelper().compareResult(
                currentVideoTest!!.groundTruthFilePath,
                currentVideoTest!!.outputDataFilePath,
                currentVideoTest!!
            )

            // create new session
            val localBroadcastManager = LocalBroadcastManager.getInstance(context)
            val localIntent = Intent("create_session")
            localBroadcastManager.sendBroadcast(localIntent)

            // continue for other tests
            AutoDebuggingHelper().callForVideoTestOnFile(
                (activity as Context), videoTests!!
            )
        }
    }

    @SuppressLint("HardwareIds")
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        methodChannel = MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger, PlatformChannels.APPLICATION_CHANNEL
        ).apply { setMethodCallHandler(this@MainActivity) }
        localMethodChannel = MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger, PlatformChannels.LOCAL_CHANNEL
        ).apply { setMethodCallHandler(this@MainActivity) }

        flutterEngine.plugins.add(ScreenCapturePlugin())
        super.configureFlutterEngine(flutterEngine)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.onRequestPermissionResult(permissions, grantResults, this)
    }

    private fun isBranchDeepLinkClicked() =
        intent.action == Intent.ACTION_VIEW && ((intent.data?.toString()
            ?.contains("gamerboard.test-app.link") == true || intent.data?.toString()
            ?.contains("gamerboard.app.link") == true) || intent.data?.scheme?.lowercase()
            .equals("gamerboard.live"))


    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        this.result = result
        when (call.method) {
            "get_device_info" -> {
                val deviceId = prefsHelper.getString(
                    SharedPreferenceKeys.UUID,
                )
                Log.d("device-id", "$deviceId")
                prefsHelper.getString(SharedPreferenceKeys.UTM_PARAMS)?.let {
                    val utmParams = Json.decodeFromString<UTMParams>(it)
                    EventUtils.instance().logUserProperties(
                        hashMapOf(
                            "utm_campaign" to utmParams.utmCampaign,
                            "utm_source" to utmParams.utmSource,
                            "utm_medium" to utmParams.utmMedium
                        )
                    )
                    prefsHelper.putString(SharedPreferenceKeys.UTM_PARAMS, null)
                }
                result.success(
                    hashMapOf(
                        "deviceId" to deviceId,
                        "deviceModel" to Build.MODEL.lowercase(),
                        "deviceBrand" to Build.BRAND.lowercase(),
                        "deviceManufacturer" to Build.MANUFACTURER.lowercase(),
                        "deviceProduct" to Build.PRODUCT.lowercase(),
                        "appVersion" to BuildConfig.VERSION_CODE,
                        "versionName" to BuildConfig.VERSION_NAME,
                        "appPackage" to BuildConfig.APPLICATION_ID,
                        "smartlookKey" to BuildConfig.SMARTLOOK_KEY
                    )
                )
            }

            "download_apk" -> {
                val url = call.argument<String>("url")
                url?.let { download(it) }
            }

            "get_game_history" -> {
                var games: List<Game>?
                CoroutineScope(Dispatchers.IO).launch {
                    val gamesDao = appDatabase.getGamesDao()
                    games = gamesDao.getListOfAllGames()
                    games?.also { fetchedGames ->
                        withContext(Dispatchers.Main) {
                            result.success(Json.encodeToString(fetchedGames))
                        }
                    }
                }
            }

            "close_app" -> {
                finish()
            }

            "log" -> {
                val message = call.argument<String>("message")
                message?.let { logFlutter(it) }
            }

            "feature_flags" -> {
                val isNewProfileVerificationUI = call.argument<Boolean>("profile_nudge") ?: false
                prefsHelper.putBoolean(
                    SharedPreferenceKeys.NEW_PROFILE_VERIFY_UI,
                    isNewProfileVerificationUI
                )

                val enableNewLoggingApi = call.argument<Boolean>("new_loging_mode") ?: false
                switchLoggingApi(enableNewLoggingApi)

                val ffmaxFlag = call.argument<Boolean>("ffmax_flag")
                prefsHelper.putBoolean(
                    SharedPreferenceKeys.FFMAX_FLAG, ffmaxFlag
                )

                val killAlgo = call.argument<String>("kill_algo")
                prefsHelper.putString(
                    SharedPreferenceKeys.KILL_ALGO_FLAG, killAlgo
                )

                val gameIdVerification = call.argument<Boolean>("game_id_verification")
                prefsHelper.putBoolean(
                    SharedPreferenceKeys.GAME_ID_VERIFICATION, gameIdVerification
                )

                val onlyVerification = call.argument<Boolean>("show_verification_only")
                prefsHelper.putBoolean(
                    SharedPreferenceKeys.SHOW_VERIFICATION_ONLY, onlyVerification
                )
            }

            "setup_current_game" -> {
                val currentGameName = call.argument<String>("game_name")
                prefsHelper.putString(
                    SharedPreferenceKeys.CURRENT_GAME_NAME, currentGameName
                )
            }

            "open_file" -> {
                val path = call.argument<String>("path")
                this.path = path
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && packageManager.canRequestPackageInstalls()
                        .not()
                ) {
                    val unknownAppSourceIntent =
                        Intent().setAction(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                            .setData(Uri.parse(String.format("package:%s", packageName)))
                    startActivityForResult(
                        unknownAppSourceIntent, PermissionRequestCode.INSTALL_APP_UNKNOWN_SOURCE
                    )
                } else {
                    openFile(path, result)
                }
            }

            "app_config" -> {
                result.success(
                    hashMapOf(
                        "API_ENDPOINT" to BuildConfig.API_ENDPOINT,
                        "FLAGSMITH_KEY" to BuildConfig.FLAGSMITH_KEY,
                        "MAP_API_KEY" to BuildConfig.MAP_API_KEY,
                    )
                )
            }

            "user_profile" -> {
                val userId = call.argument<String>("user_id")
                userId?.let {
                    prefsHelper.putString(SharedPreferenceKeys.USER_ID, it)
                    EventUtils.instance().pushUserIdentity(it)
                }
            }

            "user_login" -> {
                CoroutineScope(Dispatchers.IO).launch {
                    clearOnBoardingPreferences()
                    call.argument<String>("auth_token")?.let {
                        prefsHelper.putString(SharedPreferenceKeys.AUTH_TOKEN, it)
                    }
                }
            }

            "open_url" -> {
                val url = call.argument<String>("url")
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }

            "analytics" -> {
                val mode = call.argument<String>("analytics_mode")
                val data = call.argument<HashMap<String, Any>>("data")
                when (mode) {
                    "event" -> {
                        data?.let {
                            EventUtils.instance().logAnalyticsEvent(
                                data["event_name"] as String,
                                data["properties"] as HashMap<String, Any>
                            )
                        }
                    }

                    "user_properties" -> {
                        data?.let {
                            EventUtils.instance().logUserProperties(it as HashMap<String, Any?>)
                        }
                    }
                }
            }

            "share" -> {
                val content = call.argument<String>("content")
                val targetPackage = call.argument<String?>("package")
                val sendIntent: Intent = Intent(Intent.ACTION_SEND).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Intent.EXTRA_TEXT, content)
                    type = "text/plain"
                }
                try {
                    if (targetPackage != null) {
                        sendIntent.setPackage("com.whatsapp")
                        startActivity(sendIntent)
                    } else {
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        startActivity(shareIntent)
                    }
                } catch (e: Exception) {
                    if (e is ActivityNotFoundException) UiUtils.showToast(
                        this, "App not found", null
                    )
                    logException(e)
                }
            }

            "feedback" -> {
                requestFeedback()
            }

            "logout" -> {
                GameManager.clearAllGames()
            }

            "test_app_restart" -> {
                val list = mutableListOf<Int>()
                while (true) {
                    list.add(1)
                }
            }
        }
    }

    /**
     * Logging feature toggle.
     */
    private fun switchLoggingApi(enable: Boolean) {
        if (enable) {
            FeatureHelper.enableNewLoggingFlag(this@MainActivity)
            GBLog.instance.setCustomFactory(com.gamerboard.logging.Logger.Factory())
            UploadLogService.start(this@MainActivity)
        } else {
            FeatureHelper.disableNewLoggingFlag(this@MainActivity)
            GBLog.instance.setCustomFactory(OldLoggerAgent.Factory())
            UploadLogService.stop(this@MainActivity)
        }
    }

    private fun openFile(path: String?, result: MethodChannel.Result?) {
        if (path != null) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                val file = File(path)
                val fileUri: Uri? = try {
                    FileProvider.getUriForFile(
                        this@MainActivity, "${packageName}.fileprovider", file
                    )
                } catch (e: IllegalArgumentException) {
                    Log.e(
                        "File Selector", "The selected file can't be shared"
                    )
                    null
                }
                val myIntent = Intent(Intent.ACTION_VIEW).apply {
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    setDataAndType(
                        fileUri,
                        "application/vnd.android.package-archive"/*getFileContentType(path)*/
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                try {
                    startActivityForResult(
                        myIntent, IntentRequestCode.OPEN_FILE_RC
                    )
                } catch (e: Exception) {
                    result?.success(-100)
                }
            }

        }
    }

    private suspend fun clearOnBoardingPreferences() {
        prefsHelper.putString(
            SharedPreferenceKeys.RUN_TUTORIAL, "${OnBoardingStep.NOT_STARTED}"
        )
        prefsHelper.putString(
            SharedPreferenceKeys.RUN_TUTORIAL_IN_GAME, "${OnBoardingStep.NOT_STARTED}"
        )
        SessionManager.clearSession()
    }

    override fun onFlutterUiDisplayed() {
        super.onFlutterUiDisplayed()
        handleIntent()
    }

    private fun handleOtpLessIntent(intent: Intent) {
        //Otpless flutter plugin setup
        val plugin = flutterEngine?.plugins?.get(OtplessFlutterPlugin::class.java)
        if (plugin is OtplessFlutterPlugin) {
            plugin.onNewIntent(intent)
        }
    }

    override fun onBackPressed() {

        val plugin = flutterEngine?.plugins?.get(OtplessFlutterPlugin::class.java)
        if (plugin is OtplessFlutterPlugin) {
            if (plugin.onBackPressed()) return
        }
        // handle other cases
        super.onBackPressed()
    }

    fun handleIntent() {
        if (intent.hasExtra(IntentKeys.LAUNCH_GAME) && prefsHelper.getBoolean(IntentKeys.LAUNCH_GAME)) {
            prefsHelper.putBoolean(IntentKeys.LAUNCH_GAME, false)
            val packageName = (applicationContext as GamerboardApp).prefsHelper.getString(
                SharedPreferenceKeys.LAST_LAUNCHED_GAME
            )
            localMethodChannel.invokeMethod(
                "launch_game",
                mapOf(
                    "package" to packageName,
                    "app_restarted" to intent.extras?.getBoolean(IntentKeys.APP_RESTARTED)
                ),
            )
        } else if (GamerboardApp.result != null/*intent.hasExtra(IntentKeys.NOTIFICATION_CLICKED)*/) {
            val result = GamerboardApp.result!!
            if (result.notification.additionalData != null) {
                val metaData = result.notification.additionalData
                EventUtils.instance()
                    .logAnalyticsEvent(Events.NOTIFICATION_CLICKED, JSONObject(metaData.toString()))
                methodChannel.invokeMethod(
                    "notification_clicked", mapOf(
                        "metaData" to metaData.toString(), "fresh_launch" to activityLaunched
                    )
                )
            }
        } else if (intent.hasExtra(IntentKeys.RESTART_CLICKED)) {
            prefsHelper.putBoolean(IntentKeys.RESTART_CLICKED, false)

            localMethodChannel.invokeMethod(
                "restart_clicked", intent.getBooleanExtra(IntentKeys.RESTART_CLICKED, false)
            )

            serviceManager?.checkPermissionsForService(null, null, false, null)

            // from here we initialize the user
            StateMachine.machine.transition(Event.SetOriginalGameProfile(null, null))
            StateMachine.machine.transition(Event.SetOnBoarding(false))

        } else {
            methodChannel.invokeMethod("normal_launch", null)
        }
        GamerboardApp.result = null
        activityLaunched = false
    }

    private fun download(path: String) {
        val uri = Uri.parse(path)
        val fileName: String? = uri.lastPathSegment?.split("/")?.last()
        fileName?.let {
            prefsHelper.putString(SharedPreferenceKeys.LATEST_DOWNLOADED_VERSION, it)
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs()
            apkFile(it)?.let { f ->
                openFile(f.path, null)
                return
            }
            val mgr = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            mgr.enqueue(
                DownloadManager.Request(uri).setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
                ).setAllowedOverRoaming(true).setTitle(it)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDescription("Downloading..").setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS, it
                    )
            )
        }
    }

    private fun apkFile(fileName: String): File? {
        val apkFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName
        )
        return if (apkFile.exists() && apkFile.length() > 100000) {
            apkFile
        } else {
            null
        }
    }

    inner class NativeBroadcastListener : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val ks = intent?.extras?.keySet()
            val map = hashMapOf<String, String>()
            ks?.iterator()?.let {
                while (it.hasNext()) {
                    val key = it.next()
                    intent.getStringExtra(key)?.let { value ->
                        map[key] = value
                    }
                }
            }

            intent?.getStringExtra("action").let { action ->
                intent?.getStringExtra("method")?.let { method ->
                    when (action) {
                        "local_method" -> {
                            Log.i("refresh_call", "reached main activity broadcast receiver")
                            this@MainActivity.localMethodChannel.invokeMethod(method, map)
                        }

                        "global_method" -> {
                            this@MainActivity.methodChannel.invokeMethod(
                                method, map
                            )
                        }

                        "feedback" -> {
                            requestFeedback()
                        }

                        "notification_clicked" -> {
                            setIntent(intent)
                            handleIntent()
                        }
                    }
                }
            }
        }
    }

    fun requestFeedback() {
        FeedbackUtils(
            this, apiClient
        ).getFeedback(
            FeedBackFrom.BACK_TO_GB_FROM_GAME
        )
    }
}