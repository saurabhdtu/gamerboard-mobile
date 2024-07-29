package com.gamerboard.live.testtool

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Environment
import android.util.Log
import com.benasher44.uuid.Uuid
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.MainActivity
import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.gamestatemachine.games.bgmi.processor.BGMIKillPerFrameLabelImageProcessor
import com.gamerboard.live.gamestatemachine.stateMachine.DEBUGGER
import com.gamerboard.live.gamestatemachine.stateMachine.Event
import com.gamerboard.live.gamestatemachine.stateMachine.GameEndInfo
import com.gamerboard.live.gamestatemachine.stateMachine.GameStartInfo
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.gamestatemachine.stateMachine.State
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachine
import com.gamerboard.live.gamestatemachine.stateMachine.debugMachine
import com.gamerboard.live.models.TFResult
import com.gamerboard.live.models.db.Game
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
import com.gamerboard.live.utils.asGrayScale
import com.gamerboard.live.utils.landscaped
import com.gamerboard.logger.gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.io.InputStreamReader
import java.util.Date
import kotlin.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.roundToInt

class TestKillsOcrOnImages {

    private val apiClient: ApiClient by inject(ApiClient::class.java)
    private val mlkitOcr: MLKitOCR by inject(MLKitOCR::class.java)
    private val context: Context by inject(Context::class.java)
    private val bitmapCache: BitmapCache by inject(BitmapCache::class.java)
    private val modelParameterManager: ModelParameterManager by inject(ModelParameterManager::class.java)
    private val gameValueCache  : GameValueCache by inject(GameValueCache::class.java)
    private val slackClient: SlackClient by inject(SlackClient::class.java)
    private val prefsHelper: PrefsHelper by inject(PrefsHelper::class.java)

    private val testImagesFolder: String = "testImages/test2"
    private val csvHelper = CsvHelper()
    private lateinit var imageProcessor: ImageProcessor
    private var accuracyMap: HashMap<String, String> = hashMapOf()

    companion object {
        private val TAG = TestKillsOcrOnImages::class.java.simpleName
    }

    private var testWidth = 201
    private var groundTruth: JsonObject? = null
    suspend fun runTest() {
        val downloadPath = ServiceManager.checkAndDownloadModelAwait(
            modelParameterManager = modelParameterManager,
            packageName = SupportedGames.BGMI.packageName,
            context = context,
            apiClient = apiClient
        )

        assert(downloadPath == null)

        try{
            val groundTruthJson =
                InputStreamReader(context.assets.open("$testImagesFolder/ground_truth.json")).readText()
            groundTruth = JsonParser.parseString(groundTruthJson).asJsonObject
        }catch (ex : Exception){
            ex.printStackTrace()
        }


        debugMachine = DEBUGGER.DIRECT_HANDLE

        initStateMachine()

        /*(0 .. 10).forEach {
            testWidth += ((it - 5) * 2f).roundToInt()
            bitmapCache.killLabelBgWeight = testWidth

        }*/
        startProcessing()

        File(
            context.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS),
            "test_result_${Date()}.json"
        ).also {
            if (it.exists().not()) {
                it.createNewFile()
            }
            it.appendText(gson.toJson(accuracyMap))
        }
    }

    private suspend fun startProcessing() {
        val testImagePaths = getTestImagesPath()
        var passedCount = 0
        val filteredTest = arrayListOf<String>().map { "$it" }
        ignored = 0
        testImagePaths.forEachIndexed { index, fileName ->
            bitmapCache.clear()
            gameValueCache.clear()
            MachineConstants.loadConstants(SupportedGames.BGMI.packageName)
            passedCount = process(filteredTest, fileName, index, passedCount, testImagePaths)
            // textProcessFromLabelImage(fileName, passedCount)
            BGMIKillPerFrameLabelImageProcessor.writePixelValuesToFile(context)
        }
        val accuracy = (passedCount / (testImagePaths.size.toFloat() - ignored)) * 100

        csvHelper.addField(
            "Accuracy for ${testWidth}",
            "$accuracy%"
        )

        Log.d(
            TAG,
            "test ended: Accuracy: ${accuracy}% for $testWidth width"
        )
        accuracyMap[testWidth.toString()] = accuracy.toString()
        writeResult()

     //       postResultToSlack(accuracy.toString(), passedCount, (testImagePaths.size.toFloat() - ignored).roundToInt())
    }

    private fun postResultToSlack(accuracy: String, positiveCounter : Int, total : Int) {
        try {
            slackClient.post(
                SlackRequestBody(
                    emptyList(),
                    "TestVisionCallOnImage ${prefsHelper.getString(SharedPreferenceKeys.KILL_ALGO_FLAG)} Result: Accuracy : ${accuracy}, Passed $positiveCounter/${total}, "
                )
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private suspend fun textProcessFromLabelImage(fileName: String, passedCount: Int) {
        val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val bitmp = context.assets.open("$testImagesFolder/$fileName").let { inputStream ->
            BitmapFactory.decodeStream(inputStream).also {
                inputStream.close()
            }
        }

        val textRecognizerResponse = textRecognizer.process(bitmp, 0).await()

        val response = textRecognizerResponse.textBlocks.toMutableList()

        val newBitmap = Bitmap.createBitmap(bitmp.width, bitmp.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        val colorFilter: ColorFilter = ColorMatrixColorFilter(createThresholdMatrix(150))
        val paint = Paint()
        paint.colorFilter = colorFilter
        canvas.drawBitmap(bitmp, 0f, 0f, paint)


        val paint2 = Paint()
        val mode: PorterDuff.Mode = PorterDuff.Mode.MULTIPLY
        paint2.setXfermode(PorterDuffXfermode(mode))

        canvas.drawBitmap(bitmp, 0f, 0f, paint2)

        val textRecognizerResponse2 = textRecognizer.process(newBitmap, 0).await()

        val response2 = textRecognizerResponse2.textBlocks.toMutableList()
        Log.i(MainActivity::class.java.simpleName, "Test ${response2}")
        saveBitmap(response2.first().text, newBitmap)


    }

    private fun createThresholdMatrix(threshold: Int): ColorMatrix {
        return ColorMatrix(
            floatArrayOf(
                85f, 85f, 85f, 0f, -255f * threshold,
                85f, 85f, 85f, 0f, -255f * threshold,
                85f, 85f, 85f, 0f, -255f * threshold,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    private val sessionId = Uuid.randomUUID().toString()
    private fun saveBitmap(
        ocr: String,
        croppedBitmap: Bitmap
    ) {
        File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "labels/${GamerboardApp.sessionId}"
        ).let {
            if (it.exists().not()) {
                it.mkdirs()
            }
            val imageSave = File(it, "${sessionId}_${ocr}.png")
            val stream = imageSave.outputStream()
            croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
        }
    }

    private var ignored = 0
    private suspend fun TestKillsOcrOnImages.process(
        filteredTest: List<String>,
        fileName: String,
        index: Int,
        passedCount: Int,
        testImagePaths: List<String>
    ): Int {
        var passedCount1 = passedCount


        if (filteredTest.isEmpty() || filteredTest.contains(fileName)) {
            Log.d(TAG, "Current Test: ${fileName}}")

            val bitmaps = arrayListOf<Bitmap>()
            val tfResults = arrayListOf<List<TFResult>>()

            resetStateMachine(index)

            if (fileName.contains("png") || fileName.contains("jpg")) {
                performOcr(fileName,fileName, bitmaps, tfResults)
            } else {
                val parentPath = "$testImagesFolder/$fileName"
                val filesList = context.assets.list(parentPath)
                filesList?.forEach { name ->
                    performOcr(fileName,"$fileName/$name", bitmaps, tfResults)
                }
            }
            Log.i(TAG, "Bitmap List Count ${bitmaps.size}")

            if (bitmaps.isEmpty()) {
                ignored++
            } else {
                val passed = visionOCR(fileName, bitmaps, tfResults, index)
                if (passed)
                    passedCount1++

                val logMessage =
                    "Current Test: ${index} ${fileName}} : Passed : ${passed} , passedCount : ${passedCount1}/${testImagePaths.size}"
                Log.i(TAG, logMessage)
            }

            StateMachine.machine.transition(Event.GameCompleted("Test done"))

        }
        MachineConstants.machineInputValidator.clear()

        return passedCount1
    }

    private fun resetStateMachine(index: Int) {
        MachineConstants.machineInputValidator.clear()
        StateMachine.machine.transition(Event.OnHomeScreenDirectlyFromGameEnd("Game test $index"))
    }

    private suspend fun TestKillsOcrOnImages.performOcr(
        parentFile: String,
        name: String,
        bitmaps: ArrayList<Bitmap>,
        tfResults: ArrayList<List<TFResult>>
    ) {
        val readBitmap = readBitmaps(name)

        val fileId = System.currentTimeMillis()
        val imageBufferObject = ImageBufferObject(
            bitmap = readBitmap.asGrayScale().landscaped(), fileName = fileId
        )
        imageBufferObject.name = parentFile
        imageProcessor = ImageProcessor(
            context,
            display = Point(readBitmap.width, readBitmap.height),
            sessionId = parentFile.split("_").lastOrNull()?.toIntOrNull() ?: 1
        )
        labelImageAndDoLocalOCR(imageBufferObject, name, fileId)?.let {
            bitmaps.add(it.source)
            tfResults.add(it.outputs)
        }
    }

    private suspend fun TestKillsOcrOnImages.funTest(
        fileName: String,
        index: Int,
        passedCount: Int,
    ): Int {


        return passedCount
    }

    private fun writeResult() {
        File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "result_${Date()}.csv"
        ).apply {
            createNewFile()
            appendText(csvHelper.generateCSV())
        }
    }

    private suspend fun labelImageAndDoLocalOCR(
        imageBufferObject: ImageBufferObject,
        fileName: String,
        filedId: Long,
    ): LabelingResult? {
        val labelingResult =
            imageProcessor.testProcessImageFromReader(imageBufferObject, fileName.contains("gray"))

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
        return labelingResult
    }

    private suspend fun visionOCR(
        gameName: String,
        bitmaps: ArrayList<Bitmap>,
        tfResults: ArrayList<List<TFResult>>,
        testCaseIndex: Int,

        ) = suspendCoroutine { continuation ->


        val observedGame: Game
        if (StateMachine.machine.state is State.FetchResult) {
            observedGame =
                (StateMachine.machine.state as State.FetchResult).activeGame
            MachineConstants.machineInputValidator.getProcessedRankData()?.let {
                MachineConstants.machineInputValidator.updateGameWithRank(it, observedGame)
            }
        } else {
            observedGame = testGame(testCaseIndex)
        }
        csvHelper.addField("LOO kill", observedGame.kills)
        CoroutineScope(Dispatchers.IO).launch {
            mlkitOcr.queryOcr(
                bitmaps,
                tfResults,
                arrayListOf(),
                observedGame,
                "53618111",
                "jarvisFriday",
                arrayListOf(),
                { finalGame ->

                    val (squadKillsPassed, testPassed) = onCompleteGame(
                        gameName,
                        finalGame,
                        observedGame
                    )

                    continuation.resume(testPassed || squadKillsPassed)
                },
                retry = 3
            )
        }
    }

    private fun onCompleteGame(
        gameName: String,
        finalGame: Game?,
        observedGame: Game
    ): Pair<Boolean, Boolean> {
        csvHelper.addField("Vision kill", finalGame?.kills)
        csvHelper.addField("Vision GameInfo", finalGame?.gameInfo)
        csvHelper.addField("Vision Game Json", Json.encodeToString(finalGame))
        StateMachine.machine.transition(Event.GameCompleted("Test done"))
        StateMachine.machine.transition(Event.GameEnded(GameEndInfo("")))
        StateMachine.machine.transition(Event.OnHomeScreenDirectlyFromGameEnd("ggg"))


        val expectedKills =
            finalGame?.squadScoring?.let {
                Log.e(TAG, "Squad scoring ${it}")
                parseKillsFromJsonSquadScoring(it)
            }?.toIntOrNull()
        val expectedObservedKills =
            observedGame.squadScoring?.let {
                Log.e(TAG, "observedGame Squad scoring ${it}")
                parseKillsFromJsonSquadScoring(it)
            }?.toIntOrNull()

        val groundData =
            if (groundTruth?.has(gameName) != null) groundTruth?.get(gameName)?.asString?.toIntOrNull()
                ?: 1 else 1
        val squadKillsPassed = (finalGame?.squadScoring != null && expectedKills == groundData)
        val testPassed = finalGame?.kills?.toIntOrNull() == groundData

        if (expectedKills != null) {
            Log.e(
                TAG,
                "Squad Kill ${expectedKills}, expectedObservedKills ${expectedObservedKills}"
            )
        } else {
            Log.e(
                TAG,
                "Final Kills ${finalGame?.kills}, Ocr Kills ${observedGame.kills}"
            )
        }
        return Pair(squadKillsPassed, testPassed)
    }


    private fun parseKillsFromJsonSquadScoring(it: String) = try {
        MachineConstants.machineInputValidator.getSquadScoringArray(it)
            .firstOrNull()?.jsonObject?.get("kills")?.toString()
            ?.replace("\"", "")
    } catch (ex: Exception) {
        ex.printStackTrace()
        "-1"
    }

    private fun readBitmaps(fileName: String): Bitmap {
        return context.assets.open("$testImagesFolder/$fileName").let { inputStream ->
            BitmapFactory.decodeStream(inputStream).also {
                inputStream.close()
            }.asGrayScale()
        }
    }

    private fun getTestImagesPath(): List<String> {
        return context.assets.list(testImagesFolder)?.toList() ?: emptyList()
    }

    private fun initStateMachine() {
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