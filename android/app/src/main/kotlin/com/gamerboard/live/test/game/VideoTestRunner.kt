package com.gamerboard.live.test.game

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Environment
import android.util.Log
import com.gamerboard.live.MainActivity
import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.models.db.AppDatabase
import com.gamerboard.live.models.db.Game
import com.gamerboard.live.models.test.TestDataModel
import com.gamerboard.live.repository.ApiClient
import com.gamerboard.live.repository.ModelParameterManager
import com.gamerboard.live.repository.TestSuiteRepository
import com.gamerboard.live.service.screencapture.FileAndDataSync
import com.gamerboard.live.service.screencapture.VideoTestObj
import com.gamerboard.live.test.recorder.AbstractFakeScreenRecorder
import com.gamerboard.live.test.ui.FrameViewerDialogHelper
import com.gamerboard.live.utils.ignoreException
import com.gamerboard.live.utils.removeAndCreate
import com.gamerboard.logging.LogManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.util.LinkedList
import kotlin.math.roundToInt


class VideoTestRunner(val activity: Activity) : KoinComponent {

    private lateinit var testQueue: LinkedList<TestDataModel>


    private val context: Context by inject()
    private val testSuiteRepository: TestSuiteRepository by inject()
    private val screenRecorder: AbstractFakeScreenRecorder by inject()
    private var gameObserver: GameObserver? = null
    private val database: AppDatabase by inject()
    private val logManager : LogManager by inject()

    private val specificTestsToRun = arrayListOf<String>()
    private var testVideoUiHelper: FrameViewerDialogHelper? = null
    private val apiClient: ApiClient by inject()
    private val modelParameterManager: ModelParameterManager by inject()

    lateinit var onNextRun : (Intent) -> Unit

    companion object {
        private val TAG = VideoTestRunner::class.java.simpleName
    }

    init {
      /*  testVideoUiHelper = FrameViewerDialogHelper()
        testVideoUiHelper?.show(activity)*/
    }

    /**
     * Compare game from database and ground truth from test configuration
     */
    private fun compareGames() {
        CoroutineScope(Dispatchers.IO).launch {
            val allGames = database.getGamesDao().getListOfAllGames()
            val testCases = setupWithPackage(SupportedGames.FREEFIRE.packageName)
            val autoDebuggingHelper = TestAutoDebuggingHelper(testCases.size)
            testCases.forEach { testModel ->
                val games =
                    allGames.filter { it.userId == testModel.name && it.squadScoring != null }
                if (games.isNotEmpty()) {
                    val outputData = File.createTempFile(testModel.name, ".txt")
                    outputData.appendText(Gson().toJson(games))
                    autoDebuggingHelper.setTestModel(testModel)
                    autoDebuggingHelper.compareResult(
                        groundTruthFilePath = testModel.groundTruth.path,
                        outputFilePath = outputData.path,
                        currentVideoTest = VideoTestObj(
                            videoTestName = testModel.description,
                            videoTestFilePath = testModel.videoFile.path,
                            groundTruthFilePath = testModel.groundTruth.path,
                            outputDataFilePath = outputData.path
                        )
                    )
                }
            }
            autoDebuggingHelper.uploadResultFile()
        }
    }

    private suspend fun setupWithPackage(packageName: String): List<TestDataModel> {
        downloadModelFile(packageName)
        MachineConstants.loadConstants(packageName)
        val test = testSuiteRepository.getSuite(packageName).testData.filter { test ->
                specificTestsToRun.isEmpty() || specificTestsToRun.firstOrNull {
                    test.name == it
                } != null
            }
        return arrayListOf<TestDataModel>().apply {
            addAll(test.map { it.copy() })
            // addAll(test.map { it.copy() })
        }.mapIndexed { index, testDataModel ->
            testDataModel.index = index
            testDataModel
        }
    }

    private suspend fun downloadModelFile(packageName: String) {

        modelParameterManager.getModelParam(apiClient, packageName)

        val gameModel = MachineConstants.gameConstants.gameModelURL()
        val fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(gameModel)
        if (!FileAndDataSync.getModelFile(context, fileRef.name).exists()) {
            val tempFile = File("${context.getExternalFilesDir(null)?.path}/temp_file.tmp")
            fileRef.getFile(tempFile).addOnProgressListener { task ->
                val percentage =
                    ((task.bytesTransferred.toFloat() / task.totalByteCount.toFloat()) * 100).roundToInt()
                println("Downloading Model ${percentage}%")
            }.await()
            tempFile.renameTo(FileAndDataSync.getModelFile(context, fileRef.name))
        }

    }

    fun runAllGameTests(onComplete: () -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            SupportedGames.values().forEach {
                testGameVideos(it.packageName, onComplete)
            }
        }
    }

    fun runBGMIVideoTest(onComplete: () -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            testGameVideos(SupportedGames.BGMI.packageName, onComplete)
        }
    }

    fun runFFVideoTest(onComplete: () -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            testGameVideos(SupportedGames.FREEFIRE.packageName, onComplete)
        }
    }

    private suspend fun testGameVideos(packageName: String, onComplete: () -> Unit = {}) {
        logManager.newJob()
        //Load remote config in case the test ran before loading the whole application.
        //It also fixes the remote config failure when running from android automation tests
        try {
            val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
            remoteConfig.fetchAndActivate().await()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }


        val games = database.getGamesDao().getListOfAllGames()

        val benchmark = Benchmark("$packageName test")
        val testDataFiles = setupWithPackage(packageName)

        /* val debuggingHelper = TestAutoDebuggingHelper(games.size)
         games.forEach { game ->
             compareResult(debuggingHelper, games = arrayListOf(game), testDataModel = testDataFiles.first { it.name == game.userId })
         }
         debuggingHelper.uploadResultFile()
         return*/

        testQueue = LinkedList<TestDataModel>()
        val lastIndex = activity.intent.extras?.getInt("game_id") ?: -1
        val passed = activity.intent.extras?.getInt("passed") ?: 0
        val failed = activity.intent.extras?.getInt("failed") ?: 0
        Log.i(TAG, "Last Index ${lastIndex}")
        testDataFiles.forEachIndexed { index, testDataFile ->
            if ((testDataFile.index ?: 0) > lastIndex) {
                testQueue.add(testDataFile)
            }
        }
        Log.e(TAG, "running ${testQueue.size}")

        val testAutoDebuggingHelper = TestAutoDebuggingHelper(
            testDataFiles.size,
            lastPassedCount = passed,
            lastFailedCount = failed
        )
        testAutoDebuggingHelper.currentTest = lastIndex + 1
        log("Test Result Passed ${passed}/${failed}")
        var isFinished = false

        observeGame(testAutoDebuggingHelper, testQueue.poll()) {
            isFinished = it
        }
        while (!isFinished) {
            delay(5000)
        }
        if (testQueue.isEmpty()) {
            testVideoUiHelper?.setTitleText("Test Completed ${passed}/ ${testDataFiles.size}  passed , ${failed}/${testDataFiles.size} Failed")
            testAutoDebuggingHelper.uploadResultFile()
            onComplete()
        }
        benchmark.finish()
    }

    fun finish() {
        try {
            gameObserver?.unregisterReceiver()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    val testJson = "[{\"username\":\"Punk2móU5vBa\",\"kills\":13}]"
    private fun observeGame(
        testAutoDebuggingHelper: TestAutoDebuggingHelper,
        testDataModel: TestDataModel?,
        isFinished: (finished: Boolean) -> Unit,
    ): Boolean {
        // logger.log("============ Start Observing ${testDataModel} =================")
        if (testDataModel == null) {
            isFinished(true)
            return true
        }
        testAutoDebuggingHelper.setTestModel(testDataModel)
        screenRecorder.setDataModel(testDataModel)

        val gameObserver = GameObserver(activity)
        gameObserver.start(testDataModel, screenRecorder)
        CoroutineScope(Dispatchers.Main).launch {
            testVideoUiHelper?.setTitleText(
                testDataModel.name
            )
        }
        testVideoUiHelper?.setListener(object : FrameViewerDialogHelper.Listener{
            override fun onPause() {
            }

            override fun onSeek() {
            }

        })
        screenRecorder.setOnTrackFrameCallback(object :
            AbstractFakeScreenRecorder.TrackFrameCallback {
            override fun onTrack(currentFrame: Int, totalFrames: Int) {
                val currentFrameInfo =
                    "${currentFrame}/${totalFrames}"
                log("progress $currentFrameInfo")

                if (testQueue.size == 8 && currentFrame.div(1000 * 1000) >= 180) {
                    Log.e("screen recorder", "About to finish")
                }
                CoroutineScope(Dispatchers.Main).launch {
                    testVideoUiHelper?.setFrameText(
                        currentFrameInfo + " " + " Passed ${testAutoDebuggingHelper.passedCount} / Failed ${testAutoDebuggingHelper.failedCount}"
                    )
                }
            }
        })



        gameObserver.setCallback(object : GameObserver.Callback() {
            override fun onFinish(gameIds: List<String>) {
                CoroutineScope(Dispatchers.IO).launch {
                    log("============ Game  Finished ${gameIds} =================")
                    //delay 10s to avoid immediate termination of process
                    delay(2000)
                    val games = arrayListOf<Game>()
                    gameIds.forEach {
                        games.add(database.getGamesDao().getGameById(it).last())
                    }
                    compareResult(testAutoDebuggingHelper, testDataModel, games).let {
                        CoroutineScope(Dispatchers.Main).launch {
                            testVideoUiHelper?.setTestPassed(it)
                            logManager
                            delay(2000)
                            logManager.cancel()
                            val intent = Intent(activity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.putExtra("game_id", testDataModel.index)
                            intent.putExtra("passed", testAutoDebuggingHelper.passedCount)
                            intent.putExtra("failed", testAutoDebuggingHelper.failedCount)
                            testVideoUiHelper?.close()
                            activity.finishAffinity()
                            activity.startActivity(intent)
                        }
                    }

                }
            }


            override fun onError(error: GameObserver.Error) {
                super.onError(error)
                when (error) {
                    is GameObserver.Error.StateError -> {
                        testAutoDebuggingHelper.logError("[Error] : Test stopped as game couldn't finish with GameEndedState. Current State is ${error.state} ")
                    }
                }
            }

            override fun onObserveGame(game: Game) {
                super.onObserveGame(game)
                Log.i(TAG, "Observed Game ${game}")
            }

            override fun onFrameAdded(frame: Bitmap) {
                super.onFrameAdded(frame)
                CoroutineScope(Dispatchers.Main).launch {
                    testVideoUiHelper?.setFrame(frame)
                }
            }
        })
        return false
    }

    private fun log(message : String){
        Log.i(TAG, message)
    }

    private fun compareResult(
        testAutoDebuggingHelper: TestAutoDebuggingHelper,
        testDataModel: TestDataModel,
        games: List<Game>,
    ): Boolean {
        val logFile = createOutputFile(testDataModel)
        ignoreException {
            val outputData = Json.encodeToString(games)
            logFile.writeText(outputData)
        }

        return testAutoDebuggingHelper.compareResult(
            testDataModel.groundTruth.path,
            logFile.path,
            VideoTestObj(
                videoTestName = testDataModel.description,
                videoTestFilePath = testDataModel.videoFile.path,
                groundTruthFilePath = testDataModel.groundTruth.path,
                outputDataFilePath = logFile.path
            )
        )
    }


    fun match(rec: List<Rect>) {

    }

    private fun createOutputFile(testDataModel: TestDataModel): File {
        val outputFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "${testDataModel.name}_games.txt"
        )
        outputFile.removeAndCreate()
        return outputFile
    }

}