package com.gamerboard.live.test.game

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.Log
import com.gamerboard.framedecoder.decoder.Frame
import com.gamerboard.live.common.BroadcastFilters
import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.gamestatemachine.games.GameHelper
import com.gamerboard.live.gamestatemachine.stateMachine.Event
import com.gamerboard.live.gamestatemachine.stateMachine.GameStartInfo
import com.gamerboard.live.gamestatemachine.stateMachine.State
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachine
import com.gamerboard.live.models.db.Game
import com.gamerboard.live.models.test.TestDataModel
import com.gamerboard.live.pool.BitmapFactory
import com.gamerboard.live.pool.BufferedBitmapPool
import com.gamerboard.live.repository.ApiClient
import com.gamerboard.live.service.screencapture.BufferImageProcessor
import com.gamerboard.live.service.screencapture.CapturedImageBuffer
import com.gamerboard.live.service.screencapture.ImageBufferObject
import com.gamerboard.live.service.screencapture.ImageProcessor
import com.gamerboard.live.service.screencapture.ScreenRecorder
import com.gamerboard.live.utils.getTotalMemory
import com.gamerboard.live.utils.ignoreException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import kotlin.coroutines.CoroutineContext


class GameObserver(
    val activity: Activity,
    private val shouldClearResult: Boolean = true,

    ) : KoinComponent {

    private var gameIdToIgnore: String? = null

    private var screenWidth: Int? = null
    private var screenHeight: Int? = null
    private var stopJob: Job? = null
    private val apiClient: ApiClient by inject()
    private val androidContext: Context by inject<Context>()
    private val ioDispatcher: CoroutineContext by inject(named("io"))


    private var imageProcessor: ImageProcessor? = null
    private var capturedImageBuffer: CapturedImageBuffer<ImageBufferObject>? = null
    private var bufferImageProcessor: BufferImageProcessor? = null
    private var screenRecorder: ScreenRecorder? = null
    private var testDataModel: TestDataModel? = null
    private var job: Job? = null
    private var callback: Callback? = null
    private var observedGames = arrayListOf<String>()
    private var benchmark: Benchmark? = null
    private var bufferedBitmapPool: BufferedBitmapPool? = null

    companion object {
        private val TAG = GameObserver::class.java.simpleName
    }

    init {
    }


    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun registerReceiver() {
        androidContext.registerReceiver(
            gameBroadcastReceiver,
            IntentFilter(BroadcastFilters.SERVICE_COM)
        )
    }

    fun start(
        testDataModel: TestDataModel,
        screenRecorder: ScreenRecorder,
    ) {
        val displayMetrics: DisplayMetrics = activity.resources.displayMetrics

        // logger.log(StateMachine.machine.state.toString())
        // logger.log(VisionStateMachine.visionImageSaver.state.toString())

        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels
        benchmark = Benchmark(testDataModel.name)
        this.screenRecorder = screenRecorder
        this.testDataModel = testDataModel
        capturedImageBuffer = CapturedImageBuffer()
        resetState()

        registerReceiver()
        this.screenRecorder?.starRecording()
        processFrames()

    }

    private fun resetState() {
        testDataModel?.let { testDataModel ->

            StateMachine.machine.transition(
                Event.UnVerifyUser("Reset")
            )
            StateMachine.machine.transition(
                Event.UnInitializedUser("Reset")
            )
            val input = testDataModel.input.first()

            val verifyUserState = Event.SetOriginalGameProfile(
                originalGameId = testDataModel.name,
                originalGameUserName = input.username
            )
            StateMachine.machine.transition(verifyUserState)
            StateMachine.machine.transition(Event.SetOnBoarding(false))
            StateMachine.machine.transition(Event.VerifyUser(input.username, input.username))

            //Quick fix for freefire due to cropped videos
            if (testDataModel.packageName == SupportedGames.FREEFIRE.packageName) {
                StateMachine.machine.transition(
                    Event.EnteredGame(
                        GameStartInfo(
                            System.currentTimeMillis().toString(),
                            System.currentTimeMillis().toString()
                        )
                    )
                )
            }
        }

    }


    private fun processFrames() {
        screenRecorder?.setOnCaptureFrameCallback(object : ScreenRecorder.FrameCaptureCallback {
            override fun onCapture(frame: Frame) {
                Log.i("FRAME", "Freane ${frame}")
                if (imageProcessor == null) {
                    val newSize = Point(frame.width, frame.width)
                    CoroutineScope(ioDispatcher + SupervisorJob()).launch {
                        startProcessor(this, newSize)
                    }
                }
                if (screenRecorder?.currentState == ScreenRecorder.State.Started) {
                    addFrame(frame)
                }
            }

            override fun onComplete() {
//                stop()
            }
        })

    }

    fun logDebugInfo(frameCount: String) {

    }


    private fun addFrame(frame: Frame) {
        val newBounds = fitWithinBounds(
            Rect(0, 0, frame.width, frame.height),
            Rect(0, 0, screenWidth ?: frame.width, screenHeight ?: frame.height)
        )
        if (bufferedBitmapPool == null) {
            bufferedBitmapPool = BufferedBitmapPool(
                8,
               BitmapFactory(frame.width, frame.height)
            )
        }
        val frameBitmap = try {
            bufferedBitmapPool?.get(BufferedBitmapPool.Param(frame.byteBuffer, frame.width, frame.height))
        } catch (ex: IndexOutOfBoundsException) {
            null
        }

        val imageBufferObject = ImageBufferObject(
            bufferedBitmapPool,
            frameBitmap,
            fileName = System.currentTimeMillis()
        )

        if (frameBitmap != null) {
            callback?.onFrameAdded(frameBitmap)
        }
        imageBufferObject.name = "${System.currentTimeMillis()}.jpg"
        try {
            capturedImageBuffer?.addFrame(imageBufferObject)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        log("capturedImageBuffer state ${capturedImageBuffer?.size()}")


    }

    private fun log(message: String) {
        Log.i(TAG, message)
    }

    private fun fitWithinBounds(videoRect: Rect, bounds: Rect): Rect {
        val videoAspectRatio = videoRect.width().toFloat() / videoRect.height()
        val boundsAspectRatio = bounds.width().toFloat() / bounds.height()
        val width: Int
        val height: Int
        val x: Int
        val y: Int
        if (videoAspectRatio > boundsAspectRatio) {
            // Video is wider than the bounds, scale to fit width
            width = bounds.width()
            height = (bounds.width() / videoAspectRatio).toInt()
            x = bounds.left
            y = bounds.top + (bounds.height() - height) / 2
        } else {
            // Video is taller than the bounds, scale to fit height
            height = bounds.height()
            width = (bounds.height() * videoAspectRatio).toInt()
            x = bounds.left + (bounds.width() - width) / 2
            y = bounds.top
        }
        return Rect(x, y, x + width, y + height)
    }


    private suspend fun startProcessor(
        coroutineScope: CoroutineScope,
        size: Point,
    ) {
        imageProcessor = initImageProcessor(size)

        bufferImageProcessor =
            BufferImageProcessor(imageProcessor, capturedImageBuffer!!)
        bufferImageProcessor?.startBuffer(
            coroutineScope,
            androidContext.getTotalMemory(false).toLong()
        )
    }

    private val gameBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent == null) return
            val action = intent.getStringExtra("action")
            val gameId = intent.getStringExtra("game_id")

            gameId?.let {
                CoroutineScope(ioDispatcher).launch {
                    Log.i(TAG, "Game Count ${gameId}")
                    //onGameId(gameId)
                }
            }
            when (action) {
                "game_ended_broadcast" -> {
                    GameHelper.onGameFinished(gameId)
                    CoroutineScope(ioDispatcher).launch {
                        onGameEnded(gameId)
                    }
                }

                else -> {
                    if (StateMachine.machine.state is State.WarnedNotFinishedGame
                        || StateMachine.machine.state is State.WarnedDidNotPlayGame
                        || StateMachine.machine.state is State.WarnedToVerify
                    ) {
                        if (capturedImageBuffer?.size() == 0 && screenRecorder?.currentState == ScreenRecorder.State.Completed) {
                            CoroutineScope(Dispatchers.Main).launch {
                                screenRecorder?.currentState = ScreenRecorder.State.Stopped
                                callback?.onError(Error.StateError(StateMachine.machine.state))
                                delay(5000) //Delay 5 secs if any of processing is pending by image processor let it complete first
                                stop()
                            }
                        }
                        /*if(screenRecorder?.currentState == ScreenRecorder.State.Completed){
                            finish()
                        }*/
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (screenRecorder?.currentState == ScreenRecorder.State.Completed) {
                                screenRecorder?.currentState = ScreenRecorder.State.Stopped
                                delay(20000)
                                finish()
                                stop()
                            }
                        }
                    }
                }
            }
        }

    }

    private fun isRecordingFinished() =
        arrayListOf(
            ScreenRecorder.State.Completed,
            ScreenRecorder.State.Stopped,
            ScreenRecorder.State.Idle
        ).contains(
            screenRecorder?.currentState
        )

    private suspend fun onGameEnded(gameId: String?) {
        gameId?.let {
            if (gameIdToIgnore != gameId) {
                observedGames.add(it)
            }
        }

        testDataModel?.let { testDataModel ->
            log("Final State ${StateMachine.machine.state}")
        }
        log("Final State 2 ${StateMachine.machine.state}")

        resetState()

    }


    private fun initImageProcessor(size: Point): ImageProcessor {
        return TestImageProcessor(
            androidContext,
            display = Point(1680, 720),
            sessionId = 1,
            apiClient = apiClient
        )
    }

    fun finish() {
        callback?.onFinish(observedGames)

    }

    fun stop() {
        ignoreException { job?.cancel("Game observer stopped") }

        ignoreException {
            imageProcessor?.stop()
        }
        ignoreException { screenRecorder?.stopRecording() }
        ignoreException { bufferImageProcessor?.clear() }


        ignoreException { bufferImageProcessor?.stopBuffer() }

        unregisterReceiver()
        callback?.onStop()
        callback = null
        imageProcessor = null
        bufferImageProcessor = null
        capturedImageBuffer = null
        benchmark?.finish()
        bufferedBitmapPool?.clean()

    }

    fun unregisterReceiver() {
        ignoreException { androidContext.unregisterReceiver(gameBroadcastReceiver) }
    }

    sealed class Error {
        class StateError(val state: State) : Error()
    }

    abstract class Callback {
        open fun onObserveGame(game: Game) {}
        open fun onFrameAdded(frame: Bitmap) {}
        open fun onFinish(gameIds: List<String>) {}
        open fun onError(error: Error) {}
        open fun onStop() {}
    }
}


suspend fun ignoreSuspendedException(block: () -> Unit) {
    try {
        block()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}