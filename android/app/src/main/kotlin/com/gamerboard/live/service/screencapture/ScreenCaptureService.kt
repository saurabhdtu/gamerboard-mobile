package com.gamerboard.live.service.screencapture

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import androidx.annotation.Keep
import androidx.core.app.NotificationCompat
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import com.gamerboard.live.*
import com.gamerboard.live.caching.BitmapCache
import com.gamerboard.live.caching.GameValueCache
import com.gamerboard.live.common.*
import com.gamerboard.live.gamestatemachine.MachineManager
import com.gamerboard.live.gamestatemachine.MachineManager.saveMachine
import com.gamerboard.live.gamestatemachine.games.GameHelper
import com.gamerboard.live.gamestatemachine.games.updateGameIdActive
import com.gamerboard.live.gamestatemachine.stateMachine.*
import com.gamerboard.live.models.FeedBackFrom
import com.gamerboard.live.pool.BufferedBitmapPool
import com.gamerboard.live.pool.PoolManager
import com.gamerboard.live.repository.ApiClient
import com.gamerboard.live.repository.GameRepository
import com.gamerboard.live.repository.ModelParameterManager
import com.gamerboard.live.repository.SessionManager
import com.gamerboard.live.service.screencapture.BufferFPS.MAX_FPS
import com.gamerboard.live.service.screencapture.ui.*
import com.gamerboard.live.testtool.TestBufferImageProcessor
import com.gamerboard.live.type.BgmiGroups
import com.gamerboard.live.type.BgmiLevels
import com.gamerboard.live.type.BgmiMaps
import com.gamerboard.live.utils.*
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.LogHelper
import com.gamerboard.logger.UploadLogWorker
import com.gamerboard.logger.log
import com.gamerboard.logger.logWithCategory
import com.gamerboard.logger.logWithIdentifier
import com.gamerboard.logger.logger
import com.gamerboard.logger.loggerWithIdentifier
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import io.flutter.embedding.engine.FlutterEngine
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.min


/**
 * Created by saurabh.lahoti on 03/08/21
 */

@Keep
class ScreenCaptureService : Service() {
    private val bitmapCache: BitmapCache by inject()
    private var originalBgmiName: String? = null
    private var originalGameId: String? = null
    private var mBuilder: NotificationCompat.Builder? = null
    private var image: Image? = null
    private var imageProcessor: ImageProcessor? = null
    private var capturedImageBuffer: CapturedImageBuffer<ImageBufferObject>? = null
    private var bufferImageProcessor: BufferImageProcessor? = null
    private var imageProcessorScope: CoroutineScope? = null
    private val LOG_TAG = "ScreenCaptureService:"
    private var serviceUIHelper: ServiceUIHelper? = null
    private var notificationManager: NotificationManager? = null
    private var intent: Intent? = null
    private var isServiceRunning = false
    private val prefsHelper: PrefsHelper by inject()
    private val dataSync: FileAndDataSync by inject()
    private var mediaProjection: MediaProjection? = null
    private var mediaProjectionCallback: MediaProjectionCallback? = null
    private var windowManager: WindowManager? = null
    private var display = Point()
    private var densityDPI = 0
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var broadcastReceiver: LocalBroadcastReceiver? = null
    private var screenConfigurationListener: ScreenConfigurationListener? = null
    private val serviceConnection = ServiceConnection()
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var imageCount = 0
    private var packageDetectionHelper: PackageDetectionHelper? = null

    //private val logger: Logger by inject()
    private val apiClient: ApiClient by inject()
    private val gameValueCache: GameValueCache by inject()
    private val logHelper: LogHelper by inject()
    private val modelParameterManager: ModelParameterManager by inject()
    private val poolManager: PoolManager by inject()

    private var lastRun: Long = System.currentTimeMillis()

    //    private var videoRecorder: VideoRecorder? = null
    private var sessionId = 0
    private var videoRecorder: VideoRecorder? = null


    //private var maxWidthCapture = 1280
    private var maxHeightCapture = 720
    var redeliveredIntent = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        this.intent = intent

        // this will finish the game if exists.
        StateMachine.machine.transition(
            Event.GameCompleted(
                reason = "Service was started!", executeInBackground = true
            )
        )
        //StateMachine.machine.transition(Event.ServiceStopped(stoppedVia = "Service was started!"))

        init()
        bindNotification()
        setUpProjection()
        MachineConstants.machineLabelProcessor.resetUIScreens()
        return START_STICKY
    }

    private fun init() {
        log("Service method: init()")

        val modelData = modelParameterManager.load()


        MachineConstants.loadConstants(
            prefsHelper.getString(
                SharedPreferenceKeys.LAST_LAUNCHED_GAME
            ) ?: SupportedGames.BGMI.packageName
        )
        if (notificationManager == null || windowManager == null) {
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            imageCount = 0
            if (windowManager != null) {
                mediaProjectionManager =
                    getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                if (broadcastReceiver != null) {
                    unregisterBroadcastListeners()
                }
                broadcastReceiver = LocalBroadcastReceiver()
                screenConfigurationListener = ScreenConfigurationListener()
                registerReceiver(
                    broadcastReceiver, IntentFilter(BroadcastFilters.SERVICE_COM)
                )
                registerReceiver(
                    screenConfigurationListener, IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
                )
                log("registered broadcast receivers")
            }
        } else {
            redeliveredIntent = true
            mediaProjection?.stop()
        }
    }

    private fun unregisterBroadcastListeners() {
        try {
            unregisterReceiver(broadcastReceiver)
            unregisterReceiver(screenConfigurationListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bindNotification() {
        try {
            log("Started notification creation")
            val customNotification = createNotification()
            customNotification.flags = Notification.FLAG_ONGOING_EVENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    AppNotificationID.FOREGROUND_CAPTURE_SERVICE,
                    customNotification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                )
            } else {
                startForeground(
                    AppNotificationID.FOREGROUND_CAPTURE_SERVICE, customNotification
                )
            }
            log("Created notification")
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun createNotification(): Notification {
        if (mBuilder == null) {
            mBuilder =
                NotificationCompat.Builder(this, AppNotificationChannel.FOREGROUND_CAPTURE_SERVICE)
            val notificationIntent = Intent(
                this, MainActivity::class.java
            )
            notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var channel: NotificationChannel? = null
                channel =
                    notificationManager!!.getNotificationChannel(AppNotificationChannel.FOREGROUND_CAPTURE_SERVICE)
                if (channel == null) {
                    channel = NotificationChannel(
                        AppNotificationChannel.FOREGROUND_CAPTURE_SERVICE,
                        "Gamerboard-LIVE service",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    notificationManager!!.createNotificationChannel(channel)
                }
            }
            val stopServiceIntent = Intent(BroadcastFilters.SERVICE_COM)
            stopServiceIntent.putExtra("action", "stop")
            val pStopServiceIntent = PendingIntent.getBroadcast(
                this, 100, stopServiceIntent, PendingIntent.FLAG_IMMUTABLE
            )
            mBuilder!!.setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setContentTitle("Gamerboard running in background")
                .setContentText("Press stop to stop Gamerboard").setContentIntent(pendingIntent)
                .setChannelId(AppNotificationChannel.FOREGROUND_CAPTURE_SERVICE).setOngoing(true)
                .addAction(R.drawable.ic_stop, getString(R.string.stop), pStopServiceIntent)
                .setOnlyAlertOnce(true).setAutoCancel(false)
        } else {
            val style =
                NotificationCompat.InboxStyle().setBigContentTitle("Service running in background")
                    .setSummaryText("Captured: $imageCount")
            mBuilder?.setStyle(style)
        }

        return mBuilder!!.build()
    }

    suspend fun createSessionInDatabase() {
        var sessionId: Int? = isRestorable()
        val appRestarted = this.intent?.getBooleanExtra(IntentKeys.APP_RESTARTED, false) == true

        if (sessionId == null || appRestarted.not()) {
            log {
                it.setMessage("Session created")
                it.addContext("id", sessionId)
            }
            MachineManager.clearMachine()
            sessionId = (System.currentTimeMillis() / 1000).toInt()
        } else {
            log {
                it.setMessage("Session restored")
                it.addContext("id", sessionId)
            }
            Log.d(SessionManager.tag, "session restored")
            MachineManager.restoreMachine()
        }
        SessionManager.createSession(sessionId.toString())

        log {
            it.setMessage("Session added to DB.")
            it.addContext("id", sessionId)
        }
        this.sessionId = sessionId
    }

    private suspend fun isRestorable(): Int? {

        val temp = SessionManager.getSessionID()
        Log.d(SessionManager.tag, "isRestorable=>$temp")
        log {
            it.setMessage("Session Value")
            it.addContext("value", temp)
        }
        if (!temp.isNullOrEmpty() && temp.contains(":")) {
            val oldSessionId = temp.split(":")[0].toInt()
            val timeStamp = temp.split(":")[1].toLong()
            val diff = System.currentTimeMillis() - timeStamp
            Log.d(SessionManager.tag, " timestamp: $timeStamp; difference: $diff")
            if (diff < 40000) {
                try {
                    Log.d(SessionManager.tag, "sessionRestored")
                    log("Session restored: $oldSessionId")
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                return oldSessionId
            }
        }
        SessionManager.clearSession()
        return null
    }


    @SuppressLint("WrongConstant")
    private fun setUpProjection() {
        log("Started projection setup")

        try {

            if (intent != null && mediaProjectionManager != null && windowManager != null && intent!!.hasExtra(
                    ServiceIntentKeys.GAME_PROFILE_ID
                )
            ) {
                originalGameId = intent!!.getStringExtra(ServiceIntentKeys.GAME_PROFILE_ID)
                originalBgmiName = intent!!.getStringExtra(ServiceIntentKeys.GAME_USERNAME)
                val onBoarding = originalGameId == null

                // you set these for the machine
                StateMachine.machine.transition(Event.UnInitializedUser("start of session"))
                StateMachine.machine.transition(Event.UnVerifyUser("start of session"))
                StateMachine.machine.transition(
                    Event.SetOriginalGameProfile(
                        originalGameId, originalBgmiName
                    )
                )
                StateMachine.machine.transition(Event.SetOnBoarding(onBoarding))

                // check the state machine state instead of directly listening to `onBoarding` flag
                val shouldRunOnBoarding = when (val state = StateMachine.machine.state) {
                    is UserDetails -> state.unVerifiedUserDetails.onBoarding
                    is OnBoardingInfoProvider -> state.onBoarding
                    else -> onBoarding
                }

                if (shouldRunOnBoarding == true) {
                    log("Started running tutorial!")
                    if (prefsHelper.getString(SharedPreferenceKeys.RUN_TUTORIAL) != "${OnBoardingStep.COMPLETED}") prefsHelper.putString(
                        SharedPreferenceKeys.RUN_TUTORIAL, "${OnBoardingStep.PENDING}"
                    )
                    if (prefsHelper.getString(SharedPreferenceKeys.RUN_TUTORIAL_IN_GAME) != "${OnBoardingStep.COMPLETED}") prefsHelper.putString(
                        SharedPreferenceKeys.RUN_TUTORIAL_IN_GAME, "${OnBoardingStep.PENDING}"
                    )
                }
                serviceUIHelper =
                    ServiceUIHelper(this.applicationContext, windowManager!!, apiClient)
                packageDetectionHelper = PackageDetectionHelper.getInstance(
                    this.applicationContext,
                    intent!!.getBooleanExtra(ServiceIntentKeys.IGNORE_PERMISSION, false),
                    serviceUIHelper
                )
                isServiceRunning = true
                Log.d(SessionManager.tag, "setupProjection() $isServiceRunning")
                CoroutineScope(Dispatchers.IO).launch {
                    createSessionInDatabase()
                }
                clear()
                display = serviceUIHelper?.calculateDisplayDimens() ?: Point(Point(1920, 1080))
                display = getMaxAllowedDisplay(display)

                // to update the onBoarding flags
                serviceUIHelper?.checkOnBoarding()

                capturedImageBuffer = CapturedImageBuffer()
                registerProjection()


                serviceUIHelper?.inflateBubbleLayout()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d(SessionManager.tag, "restored")
                    val result = isRestorable()
                    Log.d(SessionManager.tag, "restart layout")
                    withContext(Dispatchers.Main) {
                        if (result != null) {
                            serviceUIHelper = ServiceUIHelper(
                                this@ScreenCaptureService.applicationContext,
                                windowManager!!,
                                apiClient
                            )
                            EventUtils.instance().logAnalyticsEvent("app_restart", mapOf())
                            serviceUIHelper?.inflateRestartServiceLayout(this@ScreenCaptureService)
                        } else {
                            stopSelf()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(SessionManager.tag, "setupProjection exception")
            stop(forceClearSession = true, before = {
                // this will finish the game if exists.
                StateMachine.machine.transition(Event.ServiceStopped("Service stopped: Setup projection stop on exception"))
                StateMachine.machine.transition(
                    Event.GameCompleted(
                        "Service stopped: Setup projection stop on exception",
                        executeInBackground = true
                    )
                )
            })
            logException(e)
        }
    }

    private fun registerMediaProjectionCallback(mediaProjection: MediaProjection?) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            mediaProjectionCallback?.let {
                mediaProjection?.registerCallback(it, null)
            }
        }
    }

    private var screenGetNextRunning = true
    private fun startGetNextImage() {
        CoroutineScope(Dispatchers.IO).launch {
            while (screenGetNextRunning) {
                delay((1000 / MAX_FPS).toLong())
                if (image == null) getNextImage()
                if (System.currentTimeMillis() - lastRun > 3000) {
                    lastRun = System.currentTimeMillis()
                    updateSession()
                }
            }
        }
    }

    private fun getMaxAllowedDisplay(display: Point): Point {
        val dis = Point()/*val widthOfScreen = max(display.x, display.y)
        dis.x = min(widthOfScreen, maxWidthCapture)
        dis.y = ((display.y.toFloat() / display.x.toFloat()) * dis.x).toInt()*/
        val heightOfScreen = min(display.x, display.y)
        dis.y = min(heightOfScreen, maxHeightCapture) // x, 720
        dis.x = ((display.x.toFloat() / display.y.toFloat()) * dis.y).toInt()
        return dis
    }

    private fun clear() {
        try {
            virtualDisplay?.release()
            imageReader?.close()
        } catch (e: Exception) {
            logException(e)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return serviceConnection
    }

    private suspend fun getPendingTasks(): List<WorkInfo> {
        try {
            return WorkManager.getInstance(applicationContext).getWorkInfos(
                WorkQuery.Builder.fromStates(
                    listOf(
                        WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED
                    )
                ).build()
            ).get()
        } catch (ex: Exception) {
            logException(ex)
        }
        return emptyList()
    }

    private suspend fun stopAllPendingTasks() {
        try {
            getPendingTasks()
                .filter { workInfo -> workInfo.tags.contains(UploadLogWorker.TAG).not() }
                .forEach {
                    WorkManager.getInstance(applicationContext).cancelWorkById(it.id)
                }
        } catch (ex: Exception) {
            WorkManager.getInstance(applicationContext).cancelAllWork()
            ex.printStackTrace()
            Firebase.crashlytics.recordException(ex)
        }

    }

    private suspend fun waitForTasksToFinish() {
        var waitForWorkerToFinish = 10

        delay(1000)

        var pendingTasks = getPendingTasks()

        log {
            it.setCategory(LogCategory.D)
            it.setMessage("There are pending tasks. It will wait until all tasks are finished or timeout before closing the  service")
            it.addContext("pending_tasks", pendingTasks.size)
            it.addContext("wait_for_time", waitForWorkerToFinish.times(5))
            it.addContext("pending_tasks", pendingTasks.map { it.tags })
        }
        while (pendingTasks.isNotEmpty() && waitForWorkerToFinish > 0) {
            pendingTasks = getPendingTasks()

            log {
                it.setCategory(LogCategory.D)
                it.setMessage("Waiting for task to finish before stopping the service.")
                it.addContext("pending_tasks", pendingTasks.map { it.tags })
            }
            delay(5000)
            waitForWorkerToFinish--
        }

        log(message = "Tasks are not finished on time waited too long. Terminating all tasks.")
        stopAllPendingTasks()
    }

    fun stop(forceClearSession: Boolean, before: () -> Unit = {}, after: () -> Unit = {}) {
        log("Service stopped. forceClearSession: $forceClearSession")
        //Hide the ui immediately
        isServiceRunning = false
        Log.d(SessionManager.tag, "stop() $isServiceRunning")
        serviceUIHelper?.clearResources()
        serviceUIHelper?.onBoardingUI?.clearAllOverlays()
        windowManager = null
        if (sessionId > 0) {
            deregisterProjection()
            before()
            logHelper.completeLogging()
            try {
                bitmapCache.clear()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            PackageDetectionHelper.instance?.stop()
            CoroutineScope(Dispatchers.IO).launch {
                if (forceClearSession)
                    SessionManager.clearSession()
                waitForTasksToFinish()
                CoroutineScope(Dispatchers.Main).launch {
                    terminate()
                    after()
                }
            }
        }

    }


    private suspend fun terminate(
    ) {
        try {
            isServiceRunning = false
            mediaProjectionManager = null
            bufferImageProcessor = null
            stopForeground(STOP_FOREGROUND_REMOVE)
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun registerProjection() {
        mediaProjectionCallback = MediaProjectionCallback()
        imageProcessor = ImageProcessor(
            this,
            display,
            sessionId
        )
        startBufferImageProcessor()
        densityDPI = intent!!.getIntExtra(ServiceIntentKeys.DISPLAY_DENSITY, 0)
        mediaProjection = mediaProjectionManager!!.getMediaProjection(-1, intent!!)
        registerMediaProjectionCallback(mediaProjection)
        imageReader = ImageReader.newInstance(display.x, display.y, PixelFormat.RGBA_8888, 1)
        imageReader?.setOnImageAvailableListener(ImageReaderListener(), null)

        log {
            it.addContext("density", densityDPI)
            it.addContext("display", display)
            it.setMessage("Created VD")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            imageReader?.surface?.setFrameRate(
                15.0f, Surface.FRAME_RATE_COMPATIBILITY_FIXED_SOURCE
            )
        }
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "rooter-vd",
            display.x,
            display.y,
            densityDPI,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader!!.surface,
            null,
            null
        )
        screenGetNextRunning = true
        serviceUIHelper?.updateIconStatus(serviceUIHelper?.currentProfileStatus!!)
        //                videoRecorder = VideoRecorder(this, display)
        videoRecorder?.setMediaProjection(mediaProjection, densityDPI)
        if (debugMachine == DEBUGGER.DISABLED) {
            startGetNextImage()
        } else setupTestGetNextImage()
    }

    private fun deregisterProjection() {
        screenGetNextRunning = false
        stopImageProcessor()
        imageProcessor?.stop()
        imageReader?.close()
        virtualDisplay?.release()
        mediaProjection?.unregisterCallback(mediaProjectionCallback)
        mediaProjection = null
        mediaProjectionCallback = null
        imageProcessor = null
    }

    private fun checkNotOnForeground(): Boolean {
        val onForeground = MainActivity.gbOnForeground
        return !onForeground
    }

    override fun onDestroy() {
        unregisterBroadcastListeners()
        if (isServiceRunning) {
            log("Screen capture service, onDestroy called!")
            Log.d(SessionManager.tag, "destroy()")
            stop(forceClearSession = true)
        }
        super.onDestroy()
    }

    private fun startBufferImageProcessor() {
        assert(imageProcessor != null)
        assert(capturedImageBuffer != null)
        bufferImageProcessor = BufferImageProcessor(imageProcessor, capturedImageBuffer!!)
        imageProcessorScope = CoroutineScope(Dispatchers.IO)

        imageProcessorScope!!.launch {
//            GameRepository.mockGameSubmissionUIFromServerResponse(this@ScreenCaptureService, apiClient, serviceUIHelper)
            bufferImageProcessor?.startBuffer(this, getTotalMemory(true).toLong())
        }
        /*CoroutineScope(Dispatchers.IO).launch {
            GamerboardApp.instance.prefsHelper.putString(
                SharedPreferenceKeys.RUN_TUTORIAL, "${OnBoardingStep.COMPLETED}"
            )
            delay(5000)
            withContext(Dispatchers.Main) {
                serviceUIHelper?.actionOnScreen(screenOfType = ScreenOfType.PRE_PROFILE_VERIFICATION,
                    map = mapOf(
                        "fetchedCharacterId" to "Jhansisa[ad",
                        "fetchedNumericId" to "987654321"
                    ),
                    callback = object : CallBack<Boolean> {
                        override fun onDone(t: Boolean) {

                        }
                    })
            }
        }*/
    }

    private fun getTotalMemory(total: Boolean): Float {
        val actManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        return (if (total) memInfo.totalMem.toFloat() else memInfo.availMem.toFloat())
    }

    private fun stopImageProcessor() {
        try {
            bufferImageProcessor?.stopBuffer()
            imageProcessorScope?.cancel()
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.stackTraceToString())
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private suspend fun updateSession() {
        if (isServiceRunning) {
            logHelper.logMemoryUsage()
            SessionManager.updateSession(sessionId.toString())
            saveMachine()
        }
    }

    private var failedCaptures = 0
    private fun getNextImage() {
        if (isServiceRunning) if (checkNotOnForeground() && serviceUIHelper?.orientation == ScreenOrientation.LANDSCAPE
            && this@ScreenCaptureService.windowManager != null
            && packageDetectionHelper?.canRecord() == true
        ) {
            try {
                image = imageReader?.acquireNextImage()
                if (image != null) {

                    if (failedCaptures > 250) {
                        log {
                            it.setMessage("X No [Img]")
                            it.addContext("times", failedCaptures)
                        }
                        failedCaptures = 0
                    }
                    capturedImageBuffer?.addFrame(
                        getBufferObject(
                            image!!,
                            image!!.planes[0].pixelStride,
                            image!!.planes[0].rowStride
                        )
                    )
                    image?.close()
                    image = null
                } else failedCaptures += 1
            } catch (ex: Exception) {
                image?.close()
                image = null
                //getNextImage()
                logException(ex)
            }
        }
    }


    private fun getBufferObject(
        image: Image, pixelStride: Int, rowStride: Int
    ): ImageBufferObject {
        val width = rowStride / pixelStride
        val height = display.y

        poolManager.initBufferedBitmapPool(width, height)

        val fileName = System.currentTimeMillis()


        poolManager.bufferedBitmapPool?.let { capturedImageBuffer?.cleanUnusedFromPool(it) }

// Create a new Bitmap with the same width and height.
        val gray = try {
            poolManager.bufferedBitmapPool?.get(
                BufferedBitmapPool.Param(
                    image.planes[0].buffer,
                    width,
                    height
                )
            )
        } catch (ex: IndexOutOfBoundsException) {
            null
        }

        if (gray == null) {
            Log.e(
                "ScreenCaptureService",
                "${gray}, Circular SIze ${capturedImageBuffer?.size()}, ${poolManager.bufferedBitmapPool?.elements()}"
            )
        }
// Create a Canvas to draw the image onto the Bitmap.

        return ImageBufferObject(
            pool = poolManager.bufferedBitmapPool,
            bitmap = gray, fileName = fileName
        ).also { it.name = "$fileName.jpg" }

        /* return if (bufferImageProcessor?.currentBucket == MachineConstants.gameConstants.gameScreenBucket()) {
             val filePath =
                 "${getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/engine/$fileName.jpg"
             ImageBufferObject(
                 bitmap = null, fileName = fileName, path = filePath
             ).also {
                 dataSync.saveBitmap(gray, filePath = filePath)
                 it.name = "$fileName.jpg"
             }
         } else {
             ImageBufferObject(
                 bitmap = gray, fileName = fileName
             ).also { it.name = "$fileName.jpg" }
         }*/

    }

    inner class MediaProjectionCallback : MediaProjection.Callback() {
        override fun onStop() {

            /*// this will finish the game if exists.
            StateMachine.machine.transition(Event.GameCompleted("Stop from media projection callback!"))
            StateMachine.machine.transition(Event.ServiceStopped(stoppedVia = "Service was stopped!"))*/
            log("MediaProjectionCallback: onStop()")
            if (!redeliveredIntent && isServiceRunning) {
                serviceUIHelper?.setServiceBlockLayout()
                deregisterProjection()
            }//stop(forceClearSession = false)
            else redeliveredIntent = false
        }
    }

    inner class ImageReaderListener : ImageReader.OnImageAvailableListener {
        override fun onImageAvailable(reader: ImageReader?) {
            /* val image = reader?.acquireNextImage()
             image?.planes?.first()*/
        }
    }

    inner class ServiceConnection : Binder() {
        fun getService(): ScreenCaptureService {
            return this@ScreenCaptureService
        }
    }

    inner class LocalBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.getStringExtra("action")
            if (action != null) {
                when (action) {
                    "stop" -> {
                        onStopEvent()
                    }

                    "check_status" -> {
                        if (sessionId > 0) {
                            val intent = Intent(BroadcastFilters.RESPONSE_CAPTURE_SERVICE)
                            intent.putExtra("action", "service_status")
                            intent.putExtra("status", isServiceRunning)
                            sendBroadcast(intent)
                        }
                    }

                    "system_event" -> {
                        val record = intent.getBooleanExtra("record", false)
                        val currentPackage = intent.getStringExtra("package")
                        /*screenRecordingEnabled = record
                        currentForegroundApp = currentPackage ?: "Un-Known"*/
                    }

                    "profile_verified" -> {
                        val verified = intent.getBooleanExtra("state", false)
                        val fetchedBgmiId = intent.getStringExtra("fetchedNumericId")
                        val fetchedCharacterId = intent.getStringExtra("fetchedCharacterId")
                        serviceUIHelper?.setVerifiedUserStatus(
                            verified, fetchedBgmiId, fetchedCharacterId
                        )
                    }

                    "pre_profile_verification" -> {
                        val fetchedBgmiId = intent.getStringExtra("fetchedNumericId")
                        val fetchedCharacterId = intent.getStringExtra("fetchedCharacterId")
                        val rect: Rect? = intent.getParcelableExtra("rect")
                        val enabledPrefill =
                            prefsHelper.getBoolean(SharedPreferenceKeys.GAME_ID_VERIFICATION)
                        if (enabledPrefill) {
                            rect?.let { r ->
                                getBitmapAndDoVerification(r, fetchedBgmiId, fetchedCharacterId)
                            }
                        } else
                            serviceUIHelper?.preProfileVerification(
                                fetchedBgmiId,
                                fetchedCharacterId,
                                null
                            )
                    }

                    "register_projection" -> {
                        registerProjection()
                    }

                    "recording_status" -> {
                        val recordingStatus = intent.getIntExtra("status", 0)
                        serviceUIHelper?.setRecordingStatus(
                            recordingStatus, intent.getStringExtra("package")
                        )
                    }

                    "kills_fetched_overlay" -> {
                        logWithCategory(
                            "Shown kills fetched overlay",
                            category = LogCategory.ENGINE
                        )
                        val kills = intent.getStringExtra("kills")
                        serviceUIHelper?.showKillsFetchedOverlay(kills)
                    }

                    "screen_loader" -> {
                        val show = intent.getBooleanExtra("show", false)
                        val message = intent.getStringExtra("message")
                        val onScreenName = MachineConstants.ScreenName.values()[intent.getIntExtra(
                            "on_screen",
                            MachineConstants.ScreenName.OTHER.ordinal
                        )]
                        //showToast("Wait till we are fetching results  $show")
                        serviceUIHelper?.loaderOnScreen(
                            show,
                            message = message,
                            screen = onScreenName
                        )
                    }

                    "create_session" -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            SessionManager.clearSession()
                            createSessionInDatabase()
                        }
                    }

                    "start_record_game_video" -> {
                        EventUtils.instance().logAnalyticsEvent(
                            Events.GAME_STARTED, mapOf("game" to "com.pubg.imobile")
                        )
                        val gameId = intent.getStringExtra("game_id") ?: return
                        videoRecorder?.startRecordingGame(gameId)
                    }

                    "stop_record_game_video" -> {
                        videoRecorder?.stopRecordingGame()
                        /* serviceUIHelper?.actionOnScreen(
                             screenOfType = ScreenOfType.FIRST_GAME_END, map = mapOf()
                         )*/
                        packageDetectionHelper?.startedFetchingResult()
                    }

                    "remove_record_game_video" -> {
                        videoRecorder?.deleteRecording()
                    }

                    "first_home_screen" -> {
                        //showToast("Wait till we are fetching results  $show")
                        serviceUIHelper?.actionOnScreen(screenOfType = ScreenOfType.HOME)
                    }

                    "first_game_screen" -> {
                        //showToast("Wait till we are fetching results  $show")
                        serviceUIHelper?.actionOnScreen(screenOfType = ScreenOfType.IN_GAME)
                    }

                    "finished_incomplete_game" -> {
                        MachineConstants.machineInputValidator.clear()
                        val message = intent.getStringExtra("message")
                        //showToast("Wait till we are fetching results  $message")
                        resetAndClearImageBuffer()
                        serviceUIHelper?.gameFailed(
                            GameRepository.GameFailureReason.GAME_NOT_COMPLETED, message
                        )
                    }

                    "warn_user_to_verify" -> {
                        serviceUIHelper?.gameFailed(
                            GameRepository.GameFailureReason.STARTED_WITHOUT_VERIFYING
                        )
                    }

                    "finished_game_unverified" -> {
                        serviceUIHelper?.gameFailed(
                            GameRepository.GameFailureReason.ENDED_WITHOUT_VERIFYING
                        )
                    }

                    "game_failure" -> {
                        val error = GameRepository.GameFailureReason.values()[intent.getIntExtra(
                            "error", GameRepository.GameFailureReason.OTHER.ordinal
                        )]
                        serviceUIHelper?.gameFailed(
                            error
                        )
                    }

                    "new_login" -> {
                        serviceUIHelper?.actionOnScreen(screenOfType = ScreenOfType.ESPORT_LOGIN)
                    }

                    "alert_esport_username" -> {
                        if (StateMachine.machine.state is VerifiedUser) {
                            val originalGameUsername = intent.getStringExtra("originalUserName")
                            val fetchedGameUserName = intent.getStringExtra("fetchedUserName")
                            serviceUIHelper?.actionOnScreen(screenOfType = ScreenOfType.USERNAME_UPDATE_ALERT,
                                map = mapOf(
                                    "originalUserName" to originalGameUsername,
                                    "fetchedUserName" to fetchedGameUserName
                                ),
                                callback = object : CallBack<Boolean> {
                                    override fun onDone(t: Boolean) {
                                        if (t) {
                                            updateGameIdActive = 0
                                            fetchedGameUserName?.let {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    GameRepository.updateGameProfile(it, apiClient)
                                                }
                                            }
                                        } else {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                delay(2000)
                                                updateGameIdActive = 0
                                            }
                                            StateMachine.machine.transition(
                                                Event.SetOriginalGameProfile(
                                                    originalGameId, originalBgmiName
                                                )
                                            )
                                        }
                                    }

                                })
                        }
                    }

                    "alert_game_id_mismatch" -> {
                        val originalUserId = intent.getStringExtra("originalUserId")
                        val fetchedUserId = intent.getStringExtra("fetchedUserId")
                        serviceUIHelper?.actionOnScreen(screenOfType = ScreenOfType.USER_ID_MISMATCH,
                            map = mapOf(
                                "originalUserId" to originalUserId,
                                "fetchedUserId" to fetchedUserId
                            ),
                            callback = object : CallBack<Boolean> {
                                override fun onDone(t: Boolean) {
                                    if (t) {
                                        updateGameIdActive = 0
                                    } else {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            delay(2000)
                                            updateGameIdActive = 0
                                        }
                                    }
                                }

                            })
                    }


                    "feedback" -> {
                        val feedBackFrom =
                            FeedBackFrom.values()[intent.getIntExtra("feedback_from", 0)]
                        FeedbackUtils(
                            this@ScreenCaptureService, apiClient
                        ).getFeedback(feedBackFrom)
                    }

                    "game_ended_broadcast" -> {
                        gameValueCache.clear()
                        packageDetectionHelper?.endedFetchingResult()
                        val gameId = intent.getStringExtra("game_id")
                        resetAndClearImageBuffer()
                        if (StateMachine.machine.state !is UserDetails) {
                            log("State is not with user details, game_ended_broadcast")

                            logWithIdentifier(identifier = gameId) {
                                it.setMessage("Game not completed!")
                                it.addContext(
                                    "reason",
                                    GameRepository.GameFailureReason.GAME_NOT_COMPLETED
                                )
                                it.setCategory(LogCategory.ICM)
                            }
                            logHelper.completeLogging()
                            GameHelper.onGameFinished(gameId)
                            return
                        }

                        EventUtils.instance().logAnalyticsEvent(
                            Events.GAME_COMPLETED,
                            mapOf("game" to MachineConstants.currentGame.gameName.lowercase())
                        )

                        if (context == null) {

                            val message =
                                "pushGameOrProfile Context was found to be null for gameId ${gameId}!"
                            logWithCategory(
                                loggerWithIdentifier(gameId),
                                message = message,
                                category = LogCategory.ENGINE
                            )
                            logWithIdentifier(identifier = gameId) {
                                it.setMessage("Submit failed: pushGameOrProfile Context was found to be null")
                                it.addContext(
                                    "reason",
                                    GameRepository.GameFailureReason.COULD_NOT_COMPLETE_SUBMIT_REQUEST
                                )
                                it.setCategory(LogCategory.CME)
                            }
                            logHelper.completeLogging()
                            FirebaseCrashlytics.getInstance()
                                .log(message)
                            GameHelper.onGameFinished(gameId)
                            return
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            GameRepository.pushGameOrProfile(
                                gameId,
                                context,
                                apiClient,
                                serviceUIHelper,
                                0,
                                packageDetectionHelper?.getWildPackages()
                            )
                            GameHelper.onGameFinished(gameId)
                        }
                    }

                    "memory_critical" -> {
                        bitmapCache.clear()
                        val criticalMem =
                            intent.getFloatExtra("memory", Float.MAX_VALUE).memoryToMB()
                        Log.d("MEMORY", "reached critical")
                        FirebaseCrashlytics.getInstance().log("reached critical $criticalMem")
                        CoroutineScope(Dispatchers.IO).launch {
                            val initialFPS = MAX_FPS
                            val initialCPS = BufferFPS.CPS
                            MAX_FPS = 0.3f
                            BufferFPS.CPS = Int.MAX_VALUE.toFloat()
                            Log.d("MEMORY", "lowered FPS")
                            log("lowered FPS")
                            FirebaseCrashlytics.getInstance().log("lowered FPS")
                            bufferImageProcessor?.clearBuffer(null)
                            val currentMem = getTotalMemory(false).memoryToMB()
                            Log.d("MEMORY", "available memory $currentMem")
                            log("MEMORYavailable memory $currentMem")
                            FirebaseCrashlytics.getInstance().log("available memory $currentMem")
                            while ((currentMem - criticalMem) < (if (BuildConfig.DEBUG) 100 else 50)) {
                                delay(100)
                            }
                            Log.d("MEMORY", "memory in control. increasing FPS to normal")
                            log("MEMORY memory in control. increasing FPS to normal")
                            FirebaseCrashlytics.getInstance()
                                .log("memory in control. increasing FPS to normal")
                            MAX_FPS = initialFPS
                            BufferFPS.CPS = initialCPS
                        }

                    }

                    "show_verification_error" -> {
                        val message = intent.getStringExtra("message") ?: ""
                        serviceUIHelper?.showGameIdVerificationError(message)
                    }

                    "update_profile" -> {
                        val gameUserName = intent.getStringExtra("game_user_name")
                        CoroutineScope(Dispatchers.IO).launch {
                            GameRepository.updateGameProfile(gameUserName, apiClient)
                        }
                    }
                }
            }
        }

        private fun getBitmapAndDoVerification(
            r: Rect,
            fetchedBgmiId: String?,
            fetchedCharacterId: String?,
        ) {
            var bitmap: Bitmap? = null
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    var i = 0;
                    while (bitmap == null && i < 5) {
                        val image = imageReader?.acquireNextImage()
                        if (image != null) {
                            val planes = image.planes
                            val buffer = planes[0].buffer
                            val pixelStride = planes[0].pixelStride
                            val rowStride = planes[0].rowStride
                            val rowPadding =
                                rowStride - pixelStride * image.width

                            // Create a byte array to hold the pixel data
                            val data = ByteArray(buffer.remaining())
                            buffer.get(data)

                            // Create a Bitmap from the pixel data
                            bitmap = Bitmap.createBitmap(
                                image.width + rowPadding / pixelStride,
                                image.height,
                                Bitmap.Config.ARGB_8888
                            )
                            bitmap?.copyPixelsFromBuffer(ByteBuffer.wrap(data))
                            bitmap?.also {
                                bitmap = it.cropBitmap(r)
                                it.recycle()
                            }
                            image.close()
                        }
                        if (bitmap != null) {
                            withContext(Dispatchers.Main) {
                                bitmap?.let {
                                    serviceUIHelper?.preProfileVerification(
                                        fetchedBgmiId,
                                        fetchedCharacterId,
                                        bitmap = it
                                    )
                                }
                            }
                            break
                        }
                        i++
                        delay(250)
                    }

                } catch (e: Exception) {
                    logException(e)
                }
            }
        }
    }

    private fun onStopEvent() {
        poolManager.bufferedBitmapPool?.clean()
        poolManager.bitmapPool?.clean()
        stop(forceClearSession = true, before = {
            // if no game is about to finish, i.e on the result screens
            if (StateMachine.machine.state !is State.FetchResult) {
                log {
                    it.setMessage("Service Stopped from notification!")
                    it.addContext("reason", GameRepository.GameFailureReason.CLOSED_SERVICE)
                    it.setCategory(LogCategory.CME)
                }
                logHelper.completeLogging()
            }
            // this will finish the game if exists.
            StateMachine.machine.transition(Event.ServiceStopped("Service stopped: Action stop Service stopped manually!"))
            StateMachine.machine.transition(
                Event.GameCompleted(
                    "Service stopped: Action stop Service stopped manually!",
                    executeInBackground = true
                )
            )
            unregisterBroadcastListeners()
        })
    }

    private fun resetAndClearImageBuffer() {
        bufferImageProcessor?.stopBuffer()
        CoroutineScope(Dispatchers.IO).launch {
            delay(4000)
            if (isServiceRunning) bufferImageProcessor?.startBuffer(
                this, getTotalMemory(true).toLong()
            )
        }
    }

    inner class ScreenConfigurationListener : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                serviceUIHelper?.orientation = ScreenOrientation.LANDSCAPE
            } else {
                serviceUIHelper?.orientation = ScreenOrientation.PORTRAIT
            }
        }

    }

    fun mockGame() {
        val gameSubmit = SubmitBGMIGameMutation(
            finalTier = BgmiLevels.GOLD_FIVE,
            initialTier = BgmiLevels.GOLD_FIVE,
            kills = 0,
            rank = 20,
            group = BgmiGroups.solo,
            map = BgmiMaps.livik,
            playedAt = Date()
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = apiClient.mutation(gameSubmit).execute()
                Log.d("apollo", Gson().toJson(result.data))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

/////////////////////////////////
    /*
    private fun setupMethodChannel() {
        if (FlutterEngineHolder.flutterEngine == null) {
            val callbackHandle =
                (applicationContext as GamerboardApp).prefsHelper.getLong(SharedPreferenceKeys.KEY_CALLBACK_HANDLE)
            if (callbackHandle == 0L) {
                Log.e(LOG_TAG, "Fatal: no callback registered")
                return
            }
            try {
                val callbackInfo =
                    FlutterCallbackInformation.lookupCallbackInformation(callbackHandle)
                if (callbackInfo == null) {
                    Log.e(LOG_TAG, "Fatal: failed to find callback")
                    return
                }

                FlutterEngineHolder.flutterEngine = FlutterEngine(this)

                val args = DartExecutor.DartCallback(
                    assets, FlutterMain.findAppBundlePath(this)!!, callbackInfo
                )
                FlutterEngineHolder.flutterEngine!!.dartExecutor.executeDartCallback(args)
            } catch (ex: Exception) {
                logException(ex)
            }
            //IsolateHolderService.setBackgroundFlutterEngine(sBackgroundFlutterEngine)
        }
        mBackgroundChannel = MethodChannel(
            FlutterEngineHolder.flutterEngine!!.dartExecutor.binaryMessenger,
            PlatformChannels.BG_PLUGIN_SERVICE
        )
        mBackgroundChannel?.setMethodCallHandler { call, result ->
            when (call.method) {
                "log_api" -> {
                    log {
                        it.addContext("args", call.argument<Any>("args")!!)
                        it.setMessage("log api")
                        it.setCategory(LogCategory.M)
                    }
                    result.success(null)
                }

                "init_isolate" -> {
                    result.success(
                        mapOf(
                            "api_endpoint" to BuildConfig.API_ENDPOINT,
                            "auth_token" to prefsHelper.getString(SharedPreferenceKeys.AUTH_TOKEN),
                            "build_version_code" to BuildConfig.VERSION_CODE
                        )
                    )
                }
            }
        }
        mBackgroundChannel?.invokeMethod("initialize", null)


    }
    */
// Testing

    var idx = 0
    var bucketsArray: ArrayList<String> = arrayListOf("temp")
    private var bucket = bucketsArray[0]

    private fun setupTestGetNextImage() {
        assert(capturedImageBuffer != null)
        val testBufferImageProcessor =
            TestBufferImageProcessor(this, imageProcessor, capturedImageBuffer!!, logger())

        CoroutineScope(Dispatchers.IO).launch {
            while (screenGetNextRunning) {
                delay((1000 / MAX_FPS).toLong())
                testGetNextImage(testBufferImageProcessor)
                if (System.currentTimeMillis() - lastRun > 3000) {
                    lastRun = System.currentTimeMillis()
                    updateSession()
                }
            }
        }
    }

    private suspend fun testGetNextImage(testBufferImageProcessor: TestBufferImageProcessor) {
        if (isServiceRunning) if (/*checkScreenRecording() &&*/ checkNotOnForeground() && serviceUIHelper?.orientation == ScreenOrientation.LANDSCAPE && this@ScreenCaptureService.windowManager != null) {
            try {
                val image = testBufferImageProcessor.acquireLatestImage(bucket)
                if (image != null) {
                    // Add latest Image to the buffer queue.
                    capturedImageBuffer?.addFrame(image)
                } else {
                    Log.d("testImagesCapture", "Process complete!")
                    idx++
                    if (idx < bucketsArray.size) {
                        bucket = bucketsArray[idx]
                        testBufferImageProcessor.resetImageIndex()
                    } else {
                        Log.e(
                            "testImagesCapture", "Resetting"
                        )
                        idx = 0
                        bucket = bucketsArray[idx]
                        testBufferImageProcessor.resetImageIndex()
                        //onComplete()
                        //stopImageProcessor()
                    }
                }
            } catch (ex: Exception) {
                logException(ex)
            }
        }
    }

/////////////////////////////////
}

object FlutterEngineHolder {
    var flutterEngine: FlutterEngine? = null
}