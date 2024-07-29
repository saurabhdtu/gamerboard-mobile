package com.gamerboard.live.testtool

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Point
import android.util.Log
import com.gamerboard.live.caching.BitmapCache
import com.gamerboard.live.caching.GameValueCache
import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.gamestatemachine.games.updateGameIdActive
import com.gamerboard.live.gamestatemachine.stateMachine.DEBUGGER
import com.gamerboard.live.gamestatemachine.stateMachine.Event
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.gamestatemachine.stateMachine.MachineResult
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachine
import com.gamerboard.live.gamestatemachine.stateMachine.debugMachine
import com.gamerboard.live.models.ImageResultJsonFlat
import com.gamerboard.live.repository.ApiClient
import com.gamerboard.live.repository.ModelParameterManager
import com.gamerboard.live.service.screencapture.ImageBufferObject
import com.gamerboard.live.service.screencapture.ImageProcessor
import com.gamerboard.live.service.screencapture.LabelingResult
import com.gamerboard.live.service.screencapture.MLKitOCR
import com.gamerboard.live.service.screencapture.ui.ServiceManager
import com.gamerboard.logger.gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject

/**
 * Created by saurabh.lahoti on 12/06/22
 */
class TestProfileOnImages(private val ctx: Context,
                          private val testVisionCall: Boolean = false,
                          private val ignoreUsernameMismatch: Boolean = false) {

    private val testFolder = "testImages/profileTest"
    private lateinit var expectedResults: ArrayList<ExpectedProfileResult>
    private lateinit var imageProcessor: ImageProcessor
    var positiveCounter = 0
    private val mlKitOCR = MLKitOCR()
    private val bitmapCache: BitmapCache by inject(BitmapCache::class.java)
    private val gameValueCache  : GameValueCache by inject(GameValueCache::class.java)
    private val modelParameterManager: ModelParameterManager by inject(ModelParameterManager::class.java)
    private val apiClient: ApiClient by inject(ApiClient::class.java)
    suspend fun startTest() {
        withContext(Dispatchers.Main) {
            ServiceManager.checkAndDownloadModel(
                modelParameterManager,
                SupportedGames.BGMI.packageName,
                {
                    if (it == null) {
                        MachineConstants.loadConstants(SupportedGames.BGMI.packageName)
                        debugMachine = DEBUGGER.DIRECT_HANDLE
                        StateMachine.machine.transition(Event.SetOnBoarding(false))
                        StateMachine.machine.transition(Event.UnVerifyUser("started test"))
                        expectedResults = gson.fromJson(
                            String(
                                ctx.assets.open("$testFolder/expected-results.json").readBytes()
                            ), object : TypeToken<ArrayList<ExpectedProfileResult>>() {}.type
                        )
//        Log.d("aaa", imagesArrayList.toString())
                        Log.d(TAGProfileTest, "test started")
                        CoroutineScope(Dispatchers.IO).launch {
                            processTestCase(0)
                        }
                    }
                },
                ctx,
                apiClient
            )
        }
    }

    private suspend fun processTestCase(index: Int) {
        if (index < expectedResults.size) {
            val expectedResult = expectedResults[index]
            Log.d(TAGProfileTest, "test: $index, testId: ${expectedResult.testId}")
            val imageName = "${expectedResult.testId}.jpg"
            val stream =
                ctx.assets.open("$testFolder/$imageName")
            val bitmap = BitmapFactory.decodeStream(stream)
            stream.close()
            val imageBufferObject = ImageBufferObject(
                bitmap = bitmap, fileName = index.toLong()
            )
            imageBufferObject.name = imageName
            imageProcessor = ImageProcessor(
                ctx,
                display = Point(bitmap.width, bitmap.height),
                sessionId = 1
            )
            MachineConstants.machineInputValidator.clear()
            bitmapCache.clear()
            gameValueCache.clear()
            updateGameIdActive = 0
            StateMachine.machine.transition(Event.UnInitializedUser("next test case"))
            StateMachine.machine.transition(Event.SetOnBoarding(false))
            StateMachine.machine.transition(
                Event.SetOriginalGameProfile(
                    expectedResult.userId.toString(),
                    expectedResult.username
                )
            )
            /*StateMachine.machine.transition(
                Event.SetOriginalGameProfile(
                    expectedResults[index].userId.toString(),
                    expectedResults[index].username
                )
            )*/
            StateMachine.machine.transition(Event.OnHomeScreenDirectlyFromGameEnd("Game test $index"))
            labelImageAndDoLocalOCR(
                imageBufferObject,
                imageName,
                index.toLong(),
                expectedResult
            )
            return processTestCase(index + 1)
        } else {
            Log.d(
                TAGProfileTest,
                "test ended: Total matches: ${(positiveCounter / expectedResults.size.toFloat()) * 100}%"
            )
        }
        return
    }

    private suspend fun labelImageAndDoLocalOCR(
        imageBufferObject: ImageBufferObject,
        fileName: String,
        fileId: Long,
        expectedProfileResult: ExpectedProfileResult
    ) {
        val labelingResult =
            imageProcessor.testProcessImageFromReader(imageBufferObject, fileName.contains("gray"))
        labelingResult?.apply {
            /*imageProcessor.processLabels(
                labelingResult.outputs,
                labelingResult.source,
                labelingResult.ttLabelling,
                fileId
            )
            BufferProcessorController.controllerBufferProcessor(outputs)*/
            val flatResult =
                imageProcessor.testPerformOCR(this.outputs, labelingResult.source, fileId)
            val validateProfileResults =
                MachineConstants.machineInputValidator.validateProfile(arrayListOf(flatResult))
            if (validateProfileResults?.size == 2) {
                if (validateProfileResults[0] != null && validateProfileResults[1] != null) {
                    val result = expectedProfileResult.matchResult(
                        validateProfileResults[1],
                        validateProfileResults[0],
                        ignoreUsernameMismatch
                    )
                    if (result)
                        positiveCounter++
                    else {
                        validateWithVision(labelingResult, expectedProfileResult)
                    }
                } else {
                    validateWithVision(labelingResult, expectedProfileResult)
                }
            } else {
                Log.d(
                    TAGProfileTest,
                    "Failed test case: No result"
                )
            }
        }
    }

    private suspend fun validateWithVision(
        labelingResult: LabelingResult,
        expectedProfileResult: ExpectedProfileResult
    ) {
        if (testVisionCall.not())
            return
        mlKitOCR.visionCallForSingleImage(
            labelingResult.source,
            labelingResult.outputs
        )
        val validateVision =
            MachineConstants.machineInputValidator.validateProfile(
                arrayListOf(
                    ImageResultJsonFlat(
                        1, "filename", labelingResult.outputs
                    )
                )
            )
        if (validateVision?.size == 2) {
            validateVision[0].let { id ->
                validateVision[1].let { charId ->
                    val result = expectedProfileResult.matchResult(
                        charId,
                        id,
                        ignoreUsernameMismatch
                    )
                    if (result)
                        positiveCounter++
                }
            }
            return
        } else {
            Log.d(
                TAGProfileTest,
                "Failed: with vision call"
            )
        }

    }

    data class ExpectedProfileResult(
        var testId: String,
        var username: String,
        var userId: Long
    ) {
        fun matchResult(
            username: MachineResult,
            userId: MachineResult,
            ignoreUsernameMismatch: Boolean
        ): Boolean {
            if (ignoreUsernameMismatch || MachineConstants.machineInputValidator.compareAndMatchGameUsername(
                    username.charId ?: "",
                    this.username
                )
            ) {
                if (MachineConstants.machineInputValidator.compareAndMatchGameUserId(
                        userId.id ?: "",
                        this.userId.toString()
                    )
                ) {
                    Log.d(TAGProfileTest, "Test passed")
                    return true
                } else {
                    Log.e(
                        TAGProfileTest,
                        "Observed userId: ${userId.id}; Expected userId ${this.userId}"
                    )
                }
            } else {
                Log.e(
                    TAGProfileTest,
                    "Observed username: ${username.charId}; Expected username ${this.username}"
                )
            }
            return false
        }
    }

}

private val TAGProfileTest = "ProfileTestTool"