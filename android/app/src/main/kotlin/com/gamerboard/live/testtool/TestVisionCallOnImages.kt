package com.gamerboard.live.testtool

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Environment
import android.util.Log
import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.stateMachine.DEBUGGER
import com.gamerboard.live.gamestatemachine.stateMachine.Event
import com.gamerboard.live.gamestatemachine.stateMachine.GameStartInfo
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.gamestatemachine.stateMachine.State
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachine
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants.UNKNOWN
import com.gamerboard.live.gamestatemachine.stateMachine.debugMachine
import com.gamerboard.live.models.TFResult
import com.gamerboard.live.models.db.Game
import com.gamerboard.live.models.db.GameInfo
import com.gamerboard.live.caching.BitmapCache
import com.gamerboard.live.caching.GameValueCache
import com.gamerboard.live.common.PrefsHelper
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.repository.ApiClient
import com.gamerboard.live.repository.ModelParameterManager
import com.gamerboard.live.service.screencapture.ImageBufferObject
import com.gamerboard.live.service.screencapture.ImageProcessor
import com.gamerboard.live.service.screencapture.LabelingResult
import com.gamerboard.live.service.screencapture.MLKitOCR
import com.gamerboard.live.service.screencapture.ui.ServiceManager
import com.gamerboard.live.slack.SlackClient
import com.gamerboard.live.slack.data.SlackRequestBody
import com.gamerboard.live.utils.CsvHelper
import com.gamerboard.logger.gson
import com.gamerboard.live.utils.asGrayScale
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by saurabh.lahoti on 12/06/22
 */
class TestVisionCallOnImages(private val ctx: Context, private val testLocalOCR: Boolean = false) {

    private val bitmapCache: BitmapCache by inject(BitmapCache::class.java)
    private val gameValueCache  : GameValueCache by inject(GameValueCache::class.java)
    private val modelParameterManager: ModelParameterManager by inject(ModelParameterManager::class.java)
    private val mlKitOCR : MLKitOCR by inject(MLKitOCR::class.java)
    private val apiClient: ApiClient by inject(ApiClient::class.java)
    private val slackClient: SlackClient by inject(SlackClient::class.java)
    private val prefsHelper: PrefsHelper by inject(PrefsHelper::class.java)

    private lateinit var expectedResults: ArrayList<ExpectedResult>
    private lateinit var imageProcessor: ImageProcessor

    private val testFolder = "testImages"
    private var groundTruth: ArrayList<ExpectedResult> = arrayListOf()

    private var positiveCounter = 0
    private val csvHelper = CsvHelper()
    private var accuracyMap: HashMap<String, String> = hashMapOf()
    private var testWidth = 201

    private fun writeToExpectResultJsonFile(expecte: List<ExpectedResult>) {
        File(
            ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "expected-result.json"
        ).apply {
            if (exists()) {
                delete()
            }
            createNewFile()
            appendText(Json.encodeToString(expecte))
        }
    }


    suspend fun runTest() {
        positiveCounter = 0
        startProcessing()
        File(
            ctx.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS),
            "all_test_result_${Date()}.json"
        ).also {
            if (it.exists().not()) {
                it.createNewFile()
            }
            it.appendText(gson.toJson(accuracyMap))
        }
    }
    val sessionId = System.currentTimeMillis()

    private suspend fun startProcessing() {

        val error = ServiceManager.checkAndDownloadModelAwait(
            modelParameterManager,
            SupportedGames.BGMI.packageName,
            ctx,
            apiClient
        )
        if (error != null) {
            return
        }

        MachineConstants.loadConstants(SupportedGames.BGMI.packageName)

        initiateStateMachine()
        expectedResults = expectedResults()

        Log.d(TAGVisionTest, "test started")

        expectedResults.forEachIndexed { index, _ ->
            processTestCase(index)
        }

        val accuracy = "${(positiveCounter / expectedResults.size.toFloat()) * 100}%"

        accuracyMap[testWidth.toString()] = accuracy

        Log.d(TAGVisionTest, "test ended: Total matches: $accuracy")

        writeToExpectResultJsonFile(groundTruth)

        generateResult()

        postResultToSlack(accuracy)
    }

    private fun postResultToSlack(accuracy: String) {
        try {
            slackClient.post(
                SlackRequestBody(
                    emptyList(),
                    "TestVisionCallOnImage ${prefsHelper.getString(SharedPreferenceKeys.KILL_ALGO_FLAG)} Result: Accuracy : ${accuracy}, Passed $positiveCounter/${expectedResults.size}, "
                )
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun generateResult() {
        File(
            ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "result_${Date()}.csv"
        ).apply {
            createNewFile()
            appendText(csvHelper.generateCSV())
        }


    }

    private fun expectedResults(): ArrayList<ExpectedResult> =
        gson.fromJson(
            String(
                ctx.assets.open("$testFolder/expected-results.json").readBytes()
            ), object : TypeToken<ArrayList<ExpectedResult>>() {}.type
        )

    private fun initiateStateMachine() {
        debugMachine = DEBUGGER.DIRECT_HANDLE
        if (testLocalOCR) {

            StateMachine.machine.transition(
                Event.SetOriginalGameProfile(
                    "55501298194",
                    "RONIN"
                )
            )
            StateMachine.machine.transition(Event.SetOnBoarding(false))
            StateMachine.machine.transition(
                Event.VerifyUser(
                    gameProfileId = "55501298194",
                    gameCharId = "RONIN"
                )
            )
        }
    }

    //1684438260777
    //1684438313618
    private suspend fun processTestCase(index: Int) {
        val filterOnly = arrayListOf<String>("1680347121612" )
        bitmapCache.clear()
        gameValueCache.clear()
        Log.d(TAGVisionTest, "test: ${expectedResults[index].gameId}")
        val testImagesFolder =
            ctx.assets.list("$testFolder/visionTest/${expectedResults[index].gameId}")
        /* if (!filterOnly.contains(expectedResults[index].gameId.toString()) && filterOnly.isNotEmpty()) {
             return
         }*/
        testImagesFolder?.let {
            val bitmaps = arrayListOf<Bitmap>()
            val tfResults = arrayListOf<List<TFResult>>()
            for (i in testImagesFolder.indices) {
                val stream =
                    ctx.assets.open("$testFolder/visionTest/${expectedResults[index].gameId}/${testImagesFolder[i]}")
                val bitmap = BitmapFactory.decodeStream(stream)
                stream.close()
                val fileId = System.currentTimeMillis()
                val imageBufferObject = ImageBufferObject(
                    bitmap = bitmap.asGrayScale(), fileName = fileId
                )
                imageBufferObject.name = testImagesFolder[i]
                val sessionId = if(expectedResults[index].gameId.toString().length > 5)  expectedResults[index].gameId.toString()
                    .substring(4, expectedResults[index].gameId.toString().length).toIntOrNull()
                    ?: 0 else expectedResults[index].gameId
                imageProcessor = ImageProcessor(
                    ctx,
                    display = Point(bitmap.width, bitmap.height),
                    sessionId = sessionId.toInt()
                )
                MachineConstants.machineInputValidator.clear()
                StateMachine.machine.transition(Event.OnHomeScreenDirectlyFromGameEnd("Game test $index"))
                labelImageAndDoLocalOCR(imageBufferObject, testImagesFolder[i], fileId)?.let {
                    bitmaps.add(it.source)
                    tfResults.add(it.outputs)
                }
            }
//                StateMachine.machine.transition(Event.OnGameResultScreen())
            if (bitmaps.isNotEmpty() && tfResults.isNotEmpty())
                visionOCR(bitmaps, tfResults, index)
            else {
                Log.d(TAGVisionTest, "empty labels")
                processTestCase(index + 1)
            }
        }
        val accuracy = "${(positiveCounter / expectedResults.size.toFloat()) * 100}%"

        csvHelper.addField(
            "Accuracy ",
            accuracy
        )


    }

    private suspend fun labelImageAndDoLocalOCR(
        imageBufferObject: ImageBufferObject,
        fileName: String,
        filedId: Long,
    ): LabelingResult? {
        val labelingResult =
            imageProcessor.testProcessImageFromReader(imageBufferObject, fileName.contains("gray"))
        if (testLocalOCR) {
            StateMachine.machine.transition(
                Event.EnteredGame(GameStartInfo(filedId.toString(), fileName))
            )
            labelingResult?.apply {
                imageProcessor.processLabels(
                    labelingResult.outputs,
                    labelingResult.source,
                    labelingResult.ttLabelling,
                    filedId
                )
            }
        }
        return labelingResult
    }

    private suspend fun visionOCR(
        bitmaps: ArrayList<Bitmap>,
        tfResults: List<List<TFResult>>,
        testCaseIndex: Int,
    ) = suspendCoroutine<Unit> {
        val observedGame: Game
        if (testLocalOCR && StateMachine.machine.state is State.FetchResult) {
            observedGame =
                (StateMachine.machine.state as State.FetchResult).activeGame
            MachineConstants.machineInputValidator.getProcessedRankData()?.let {
                MachineConstants.machineInputValidator.updateGameWithRank(it, observedGame)
            }
        } else {
            observedGame = testGame(testCaseIndex)
        }
        csvHelper.addField("Game Id", expectedResults[testCaseIndex].gameId.toString())
        csvHelper.addField("Kills", expectedResults[testCaseIndex].kills.toString())
        csvHelper.addField("Rank", expectedResults[testCaseIndex].rank.toString())
        csvHelper.addField("Team Rank", expectedResults[testCaseIndex].teamRank.toString())
        csvHelper.addField("LOO kill", observedGame.kills)
        csvHelper.addField("LOO Rank", observedGame.rank)
        csvHelper.addField("LOO Team Rank", observedGame.teamRank)
        csvHelper.addField("LOO GameInfo", observedGame.gameInfo)
        CoroutineScope(Dispatchers.IO).launch {
            mlKitOCR.queryOcr(
                bitmaps,
                tfResults,
                arrayListOf(),
                observedGame,
                "53618111",
                "jarvisFriday",
                arrayListOf(),
                { finalGame ->

                    csvHelper.addField("Vision kill", finalGame?.kills)
                    csvHelper.addField("Vision Rank", finalGame?.rank)
                    csvHelper.addField("Vision Team Rank", finalGame?.teamRank)
                    csvHelper.addField("Vision GameInfo", finalGame?.gameInfo)
                    csvHelper.addField("Vision Game Json", Json.encodeToString(finalGame))
                    addGroundTruth(expectedResults[testCaseIndex].gameId, observedGame, finalGame)

                    StateMachine.machine.transition(Event.GameCompleted("Test done"))
                    val errors = finalGame?.let {
                        expectedResults[testCaseIndex].matchResult(
                            it
                        )
                    } ?: emptyList()
                    csvHelper.addField("Errors", errors.joinToString(";\n"))
                    val matchResult = errors.isEmpty()
                    if (matchResult)
                        Log.i(
                            TAGVisionTest,
                            "Observed game matched with expected: $matchResult"
                        ) else {
                        Log.e(
                            TAGVisionTest,
                            "Observed game matched with expected: $matchResult"
                        )
                    }
                    if (matchResult) positiveCounter++
                    /* if (matchResult.not())
                         Log.d(
                             TAGVisionTest,
                             "Observed game: $observedGame;\n\nVision game: $finalGame"
                         )*/
                    it.resume(Unit)
                },
                retry = 3
            )
        }

    }
    //1684441111704
    //1684427910007

    private fun addGroundTruth(
        observedGame1: Long,
        observedGame: Game,
        finalGame: Game?,
    ) {
        val gameInfo = Json.decodeFromString<GameInfo>(observedGame.gameInfo ?: "")
        groundTruth.add(
            ExpectedResult(
                gameId = observedGame1,
                initialTier = finalGame?.initialTier,
                finalTier = finalGame?.finalTier,
                mode = gameInfo.mode,
                group = gameInfo.group,
                type = gameInfo.type,
                view = gameInfo.view,
                kills = observedGame.kills?.toIntOrNull() ?: -1,
                rank = observedGame.rank?.toIntOrNull() ?: -1,
                teamRank = observedGame.teamRank?.toIntOrNull() ?: -1,
                username = ""
            )
        )
    }

    @Serializable
    data class ExpectedResult(
        var gameId: Long,
        var initialTier: String? = null,
        var finalTier: String? = null,
        var rank: Int,
        var teamRank: Int,
        var kills: Int,
        var mode: String?,
        var group: String?,
        var type: String?,
        var view: String?,
        var username: String?,
        var squadScoring: String? = null,
    ) {
        fun matchResult(observedGame: Game): List<String> {
            val expectedGame = Game(
                1,
                "11",
                true,
                rank.toString(),
                Gson().toJson(
                    GameInfo(
                        type ?: UNKNOWN,
                        view ?: UNKNOWN,
                        group ?: UNKNOWN,
                        mode ?: UNKNOWN
                    )
                ).toString(),
                kills.toString(),
                teamRank.toString(),
                initialTier,
                finalTier,
                null,
                null,
                gameId.toString(),
                0,
                null,
                null,
                null,
                squadScoring
            )
            val errors = arrayListOf<String>()

            /*

                        if(expectedGame.squadScoring.isNullOrEmpty() && expectedGame.kills != observedGame.kills){
                            Log.d(
                                TAGVisionTest,
                                "Observed kills: ${observedGame.kills}; Expected kills ${expectedGame.kills}"
                            )
                            errors.add("Observed kills: ${observedGame.kills}; Expected kills ${expectedGame.kills}")
                        }

                        if(!expectedGame.squadScoring.isNullOrEmpty() && !observedGame.squadScoring.isNullOrEmpty()){
                            val expectedSA =
                                MachineConstants.machineInputValidator.getSquadScoringArray(
                                    expectedGame.squadScoring!!
                                )
                            val observedSA =
                                MachineConstants.machineInputValidator.getSquadScoringArray(
                                    observedGame.squadScoring!!
                                )

                            if (observedSA.isEmpty()) {
                                Log.d(
                                    TAGVisionTest,
                                    "Observed squadScoring: ${observedGame.squadScoring}; Expected squadScoring ${expectedGame.squadScoring}"
                                )
                                errors.add("Observed squadScoring: ${observedGame.squadScoring}; Expected squadScoring ${expectedGame.squadScoring}")

                            }

                            if (expectedSA[0].jsonObject["kills"]?.equals(observedSA[0].jsonObject["kills"]) != true) {
                                Log.d(
                                    TAGVisionTest,
                                    "Observed squadScoring: ${observedGame.squadScoring}; Expected squadScoring ${expectedGame.squadScoring}"
                                )
                                errors.add("Observed squadScoring: ${observedGame.squadScoring}; Expected squadScoring ${expectedGame.squadScoring}")
                            }
                        }


                        return errors*/

            if ((expectedGame.rank == null && observedGame.teamRank == null).not() && isCorrectRank(
                    expectedGame,
                    observedGame
                )
            ) {
                if (LabelUtils.editDistance(
                        observedGame.initialTier?.lowercase() ?: "",
                        expectedGame.initialTier?.lowercase() ?: ""
                    ) < 3
                ) {
                    if (LabelUtils.editDistance(
                            observedGame.finalTier?.lowercase() ?: "",
                            expectedGame.finalTier?.lowercase() ?: ""
                        ) < 3
                    ) {
                        if (expectedGame.squadScoring.isNullOrEmpty()) {
                            if (observedGame.kills.equals(expectedGame.kills)) {
                                if (Gson().fromJson(
                                        observedGame.gameInfo?.lowercase(),
                                        GameInfo::class.java
                                    )
                                        .equals(
                                            Gson().fromJson(
                                                expectedGame.gameInfo!!.lowercase(),
                                                GameInfo::class.java
                                            )
                                        )
                                ) {
                                    return emptyList()
                                } else {
                                    Log.d(
                                        TAGVisionTest,
                                        "Observed gameInfo: ${observedGame.gameInfo?.lowercase()}; Expected gameInfo ${expectedGame.gameInfo?.lowercase()}"
                                    )
                                    //   errors.add("Observed gameInfo: ${observedGame.gameInfo?.lowercase()}; Expected gameInfo ${expectedGame.gameInfo?.lowercase()}")
                                }
                            } else {
                                Log.d(
                                    TAGVisionTest,
                                    "Observed kills: ${observedGame.kills}; Expected kills ${expectedGame.kills}"
                                )
                                errors.add("Observed kills: ${observedGame.kills}; Expected kills ${expectedGame.kills}")
                            }
                        } else {
                            if (observedGame.squadScoring.isNullOrEmpty()) {
                                Log.d(
                                    TAGVisionTest,
                                    "Observed squadScoring: ${observedGame.squadScoring}; Expected squadScoring ${expectedGame.squadScoring}"
                                )
                                errors.add("Observed squadScoring: ${observedGame.squadScoring}; Expected squadScoring ${expectedGame.squadScoring}")
                            } else {
                                val expectedSA =
                                    MachineConstants.machineInputValidator.getSquadScoringArray(
                                        expectedGame.squadScoring!!
                                    )
                                val observedSA =
                                    MachineConstants.machineInputValidator.getSquadScoringArray(
                                        observedGame.squadScoring!!
                                    )
                                if (observedSA.isEmpty()) {
                                    Log.d(
                                        TAGVisionTest,
                                        "Observed squadScoring: ${observedGame.squadScoring}; Expected squadScoring ${expectedGame.squadScoring}"
                                    )
                                    errors.add("Observed squadScoring: ${observedGame.squadScoring}; Expected squadScoring ${expectedGame.squadScoring}")
                                } else
                                    if (expectedSA[0].jsonObject["kills"]?.equals(observedSA[0].jsonObject["kills"]) == true) {
                                        if (observedGame.kills.equals(expectedGame.kills)) {
                                            if (Gson().fromJson(
                                                    observedGame.gameInfo?.lowercase(),
                                                    GameInfo::class.java
                                                )
                                                    .equals(
                                                        Gson().fromJson(
                                                            expectedGame.gameInfo!!.lowercase(),
                                                            GameInfo::class.java
                                                        )
                                                    )
                                            ) {
                                                return emptyList()
                                            } else {
                                                Log.d(
                                                    TAGVisionTest,
                                                    "Observed gameInfo: ${observedGame.gameInfo?.lowercase()}; Expected gameInfo ${expectedGame.gameInfo?.lowercase()}"
                                                )
                                                //errors.add("Observed gameInfo: ${observedGame.gameInfo?.lowercase()}; Expected gameInfo ${expectedGame.gameInfo?.lowercase()}")
                                            }
                                        } else {
                                            Log.d(
                                                TAGVisionTest,
                                                "Observed kills: ${observedGame.kills}; Expected kills ${expectedGame.kills}"
                                            )
                                            errors.add("Observed kills: ${observedGame.kills}; Expected kills ${expectedGame.kills}")
                                        }
                                    } else {
                                        Log.d(
                                            TAGVisionTest,
                                            "Observed squadScoring: ${observedGame.squadScoring}; Expected squadScoring ${expectedGame.squadScoring}"
                                        )
                                        errors.add("Observed squadScoring: ${observedGame.squadScoring}; Expected squadScoring ${expectedGame.squadScoring}")
                                    }
                            }
                        }

                    } else {
                        Log.d(
                            TAGVisionTest,
                            "Observed finalTier: ${observedGame.finalTier}; Expected finalTier ${expectedGame.finalTier}"
                        )
                        //  errors.add("Observed finalTier: ${observedGame.finalTier}; Expected finalTier ${expectedGame.finalTier}")
                    }
                } else {
                    Log.d(
                        TAGVisionTest,
                        "Observed initialTier: ${observedGame.initialTier}; Expected initialTier ${expectedGame.initialTier}"
                    )
                    //  errors.add("Observed initialTier: ${observedGame.initialTier}; Expected initialTier ${expectedGame.initialTier}")
                }
            } else {
                Log.d(
                    TAGVisionTest,
                    "Observed rank: ${observedGame.rank}; Expected rank ${expectedGame.rank}\nObserved teamRank: ${observedGame.teamRank}; Expected teamRank ${expectedGame.teamRank}"
                )
                errors.add("Observed rank: ${observedGame.rank}; Expected rank ${expectedGame.rank}\nObserved teamRank: ${observedGame.teamRank}; Expected teamRank ${expectedGame.teamRank}")
            }
            return errors
        }

        private fun isCorrectRank(expectedGame: Game, observedGame: Game): Boolean {
            /*(
                    ((if (expectedGame.teamRank == "-1") immutableListOf(
                        StateMachineStringConstants.UNKNOWN,
                        null,
                        "-1"
             Observed game matched with expected
                    ) else immutableListOf(expectedGame.teamRank)).contains(observedGame.teamRank)) ||
                            observedGame.rank.equals(expectedGame.rank))*/
            return (observedGame.rank.isNotNullOrUnknown() && observedGame.rank == expectedGame.rank) || (observedGame.teamRank.isNotNullOrUnknown() && observedGame.teamRank == expectedGame.teamRank)
        }

        private fun String?.isNotNullOrUnknown(): Boolean {
            return this != "-1" && this != StateMachineStringConstants.UNKNOWN && this != null
        }
    }
}

private val TAGVisionTest = "TAGVisionTest"

fun testGame(index: Int): Game = Game(
    1,
    "11",
    true,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    index.toString(),
    0,
    null,
    null,
    null
)
