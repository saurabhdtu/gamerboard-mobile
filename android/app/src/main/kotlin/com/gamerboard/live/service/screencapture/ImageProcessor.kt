package com.gamerboard.live.service.screencapture

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.common.BroadcastFilters
import com.gamerboard.live.common.RemoteConfigConstants
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.gamestatemachine.games.GameHelper
import com.gamerboard.live.gamestatemachine.games.LabelHelper
import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.stateMachine.CurrentGameProvider
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachine
import com.gamerboard.live.gamestatemachine.stateMachine.VisionEvent
import com.gamerboard.live.gamestatemachine.stateMachine.VisionParams
import com.gamerboard.live.gamestatemachine.stateMachine.VisionStateMachine
import com.gamerboard.live.models.ImageResultJson
import com.gamerboard.live.models.ImageResultJsonFlat
import com.gamerboard.live.models.ImageSavingConfig
import com.gamerboard.live.models.Resolution
import com.gamerboard.live.models.TFResult
import com.gamerboard.live.pool.PoolManager
import com.gamerboard.live.utils.logException
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.gson
import com.gamerboard.logger.log
import com.gamerboard.logger.logWithIdentifier
import com.gamerboard.logger.loggerWithIdentifier
import com.gamerboard.logger.model.OcrInfoMessage
import com.gamerboard.logging.LoggingAgent
import com.google.firebase.perf.metrics.AddTrace
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.tensorflow.lite.Interpreter
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.max
import kotlin.math.min

/**
 * Created by saurabh.lahoti on 16/08/21
 */
open class ImageProcessor(
    var ctx: Context,
    var display: Point,
    val sessionId: Int,
) : KoinComponent {
    private var scaledBmpCanvas: Canvas? = null
    private var scaledBmp: Bitmap? = null
    private var lastRuntime: Long = System.currentTimeMillis()
    private val BMP_DIMEN = 320
    private var interpreter: Interpreter? = null
    private val dataSync: FileAndDataSync by inject()
    private val poolManager: PoolManager by inject()


    private var labellingInputBuffer =
        ByteBuffer.allocateDirect(1 * BMP_DIMEN * BMP_DIMEN * 3).order(ByteOrder.nativeOrder())
    private val loggingAgent: LoggingAgent get() = loggerWithIdentifier(GameHelper.getOriginalGameId())


    private val mlKitOCR = MLKitOCR()
    private val textRecognizer: TextRecognizer
    private var deviceId: String? = null
    var imageIdx = 0
    private val interpreterOptions: Interpreter.Options = Interpreter.Options().setCancellable(true)

    private var imageSavingValues = gson.fromJson(
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigConstants.SAVE_IMAGES),
        ImageSavingConfig::class.java
    )

    private val imageDirectory = File(
        "${ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/images/"
    )

    init {
        val modelFile = FileAndDataSync.getModelFile(
            ctx, Uri.parse(MachineConstants.gameConstants.gameModelURL()).lastPathSegment
        )
        interpreter = Interpreter(modelFile, interpreterOptions)
        TFProcessor.running = true
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        deviceId = (GamerboardApp.instance).prefsHelper.getString(SharedPreferenceKeys.UUID)
        imageIdx = 0

    }


    private var logChain = StringBuilder()

    @AddTrace(name = "processImageFromReader", enabled = true)
    fun processImageFromReader(imageBufferObject: ImageBufferObject?): LabelingResult? {
        try {
            if (imageBufferObject == null) return null
            try {
                if (imageBufferObject.bitmap != null)
                    return labelImage(imageBufferObject.bitmap)
                else {
                    if (imageBufferObject.path != null) {
                        val bmp = dataSync.getBitmapFromFile(imageBufferObject.path!!)
                        imageBufferObject.path = null
                        if (bmp != null)
                            return labelImage(bmp)
                    }
                    return null
                }
            } catch (ex: RuntimeException) {
                logException(ex, logger = loggingAgent)
                // imageBufferObject.bitmap?.recycle()
                /*  imageBufferObject.bitmap?.let { imageBufferObject.pool?.putBack(it) }*/
                imageBufferObject.path?.let { File(it).delete() }
                return null
            }
        } catch (e: Exception) {
            logException(e, logger = loggingAgent)
            return null
        }
    }

    @AddTrace(name = "labelImage", enabled = true)
    private fun labelImage(bitmap: Bitmap): LabelingResult? {
        //dataSync.createImageFile(bitmap, "${System.currentTimeMillis()}.jpg")
        if (scaledBmp == null) {
            scaledBmp = Bitmap.createScaledBitmap(bitmap, BMP_DIMEN, BMP_DIMEN, true)
            scaledBmpCanvas = Canvas(scaledBmp!!)
        } else {
            scaledBmpCanvas?.drawBitmap(bitmap, null, Rect(0, 0, BMP_DIMEN, BMP_DIMEN), null)
            Log.i("ImageProcessor", "scaledBmp $scaledBmp")
        }

        logChain.append("Scaled-> ")
        labellingInputBuffer.clear()
        for (y in 0 until BMP_DIMEN) {
            for (x in 0 until BMP_DIMEN) {
                val px = scaledBmp!!.getPixel(x, y)
                val r = Color.red(px)
                val g = Color.green(px)
                val b = Color.blue(px)
                labellingInputBuffer.put(r.toByte())
                labellingInputBuffer.put(g.toByte())
                labellingInputBuffer.put(b.toByte())
            }
        }
        labellingInputBuffer.position(0)
        val labellingStartTime = System.currentTimeMillis()
        val outputs = TFProcessor.interpret(
            interpreter,
            labellingInputBuffer,
            loggingAgent,
            Resolution(bitmap.width, bitmap.height)
        )
        val labellingEndTime = System.currentTimeMillis()

        if (outputs.isNotEmpty()) {
            val ttLabelling = labellingEndTime - labellingStartTime
            logChain.append("Good Image |")

            return LabelingResult(outputs, bitmap, ttLabelling)
        }
        logChain.append("Bad Image->")
        //logger.log("Labelling confidence below threshold for $fileName")
        //imageBufferObject.bitmap?.let { imageBufferObject.pool?.putBack(it) }
        return null
    }


    suspend fun processLabels(
        `outputs`: List<TFResult>,
        bitmap: Bitmap,
        ttLabelling: Long,
        fileId: Long,
        isTest: Boolean = true,
    ) {
        val fileName = "$fileId.jpg"

        BufferProcessorController.controllerBufferProcessor(outputs)
        Log.d("label_debug", outputs.toString())
        if (MachineConstants.machineLabelProcessor.getBucket(
                LabelUtils.getListOfLabels(outputs)
            ) == MachineConstants.gameConstants.homeScreenBucket()
        ) {
            ctx.sendBroadcast(Intent(BroadcastFilters.SERVICE_COM).apply {
                putExtra(
                    "action", "show_tutorial"
                )
            })
        }
        val ocrStartTime = System.currentTimeMillis()

        val shouldPerformOcr: Boolean = MachineConstants.machineLabelProcessor.shouldRunOcr(outputs)


        var saveImage = false
        // this prevents processing of images from third part apps, cheating.
        if (shouldPerformOcr) {
            saveImage = true
            performOCR(outputs, bitmap, fileName = null)
        }

        val endTime = System.currentTimeMillis()
        logChain.append("Ocr(${endTime - ocrStartTime})-> ")

        val result = ImageResultJson(
            endTime - ocrStartTime,
            fileName,
            outputs,
            null
        )
        val flatResult = LabelUtils.flattenResultJson(result)


        if (!shouldPerformOcr) outputs.forEach {
            val textLabels = LabelUtils.getListOfLabels(flatResult.labels)
            it.ocr = "No ocr performed for label:  $textLabels"
            LabelUtils.testLogGrey("No ocr performed for label:  $textLabels")
        }

        val preBucket = LabelUtils.getListOfLabels(flatResult.labels)
        MachineConstants.machineLabelProcessor.processInputBuffer(flatResult)


        if (!isTest) {
            if (saveImage) saveScreen(bitmap, fileId, outputs, preBucket)
            saveResult(result)
            saveLabel(bitmap, fileId, preBucket)
            try {
                // bitmap.recycle()
            } catch (e: java.lang.Exception) {
                logException(e)
            }
        }
    }

    private fun saveLabel(
        bitmap: Bitmap, fileId: Long, preBucket: List<Int>,
    ) {
        val bucket = MachineConstants.machineLabelProcessor.getBucket(preBucket)


        if (!imageDirectory.exists()) {
            imageDirectory.mkdirs()
        }

        if (!imageSavingValues.save) {
            return
        }
        val gameId =
            if (StateMachine.machine.state is CurrentGameProvider) (StateMachine.machine.state as CurrentGameProvider).activeGame.gameId else sessionId.toString()
        gameId?.let {
            for (i in imageSavingValues.allowedLabels) {
                if (MachineConstants.machineLabelProcessor.getBucketFromString(i) == bucket && imageDirectory.listFiles { imageDirectory ->
                        imageDirectory.name.startsWith(
                            i
                        )
                    }.size < imageSavingValues.maxImages && System.currentTimeMillis() - lastRuntime > imageSavingValues.timeGap) {
                    if (i == "homeScreen" && preBucket.containsAll(MachineConstants.gameConstants.myProfileScreen()) || i != "homeScreen") {
                        dataSync.captureImageForUpload(
                            bitmap, "${i}_${fileId}.jpg", quality = 90, it
                        )
                        lastRuntime = System.currentTimeMillis()
                    }
                }
            }
        }
    }


    private fun saveScreen(
        bitmap: Bitmap, fileId: Long, outputs: List<TFResult>, preBucket: List<Int>,
    ) {
        val bucket = MachineConstants.machineLabelProcessor.getBucket(preBucket)
        if ((bucket != MachineConstants.gameConstants.resultRankRating()) && bucket != MachineConstants.gameConstants.resultRankKills()) return

        if (StateMachine.machine.state !is CurrentGameProvider) {
            log("Current game is not active, Machine state is not current game provider")
            return
        }

        val vision = VisionStateMachine.visionImageSaver
        val visionState = (vision.state as VisionParams)
        val gameId = (StateMachine.machine.state as CurrentGameProvider).activeGame.gameId

        if (visionState.recordKills && (bucket == MachineConstants.gameConstants.resultRankKills())) {
            val file = "rank_kills_screen_${fileId}.jpg"

            vision.transition(
                VisionEvent.StartRecordingVisionImages(
                    gameId = gameId!!, reason = "Received kills screen!"
                )
            )
//            if (MachineConstants.currentGame == SupportedGames.BGMI) {
//                vision.transition(VisionEvent.ReceivedKillsScreen(file, outputs))
//            }
            vision.transition(VisionEvent.ReceivedKillsScreen(file, outputs))

            dataSync.captureImageForUse(bitmap, file, quality = 90)

//            if (FirebaseRemoteConfig.getInstance()
//                    .getBoolean(RemoteConfigConstants.SAVE_KILL_IMG)) {
//                dataSync.copyFileForUpload(source, file)
//            }
        }

        if (visionState.recordRatings && (bucket == MachineConstants.gameConstants.resultRankRating())) {
            val file = "rank_ratings_screen_${fileId}.jpg"

            vision.transition(
                VisionEvent.StartRecordingVisionImages(
                    gameId = gameId!!, reason = "Received ratings screen!"
                )
            )
            vision.transition(VisionEvent.ReceivedRatingsScreen(file, outputs))
            dataSync.captureImageForUse(bitmap, file, quality = 90)
        }
    }

    private fun saveResult(result: ImageResultJson) {
        val ocr =
            result.labels.joinToString { if (it.ocr.contains("No ocr performed")) "" else it.ocr + ", " }
        try {
            val labelValue = StringBuilder()
            for (i in 0 until result.labels.size)
                labelValue.append("[&&]" + result.labels[i].label.toString())
            log(loggingAgent) {
                it.setCategory(LogCategory.OCR)
                it.setMessage("$labelValue[-]$ocr")
            }
        } catch (e: Exception) {
            log(loggingAgent, "result labels is empty")
        }
        if (ocr.isNotEmpty()) {
            logChain.append("Result->${ocr} and verified=${result.verifiedProfile}->Saved DB|")
            log(loggingAgent, logChain.toString())
            /*logger.log(result.labels[0].label.toString() + "[.]" + ocr, category = LogCategory.OCR)*/
        }
        logChain.clear()
    }

    fun stop() {
        labellingInputBuffer.clear()
        logChain.clear()
        interpreter?.setCancelled(true)
        TFProcessor.running = false
        interpreter?.close()

        if (scaledBmp?.isRecycled != true) {
            scaledBmp?.recycle()
        }
        scaledBmpCanvas = null
        poolManager.bitmapPool?.clean()
    }


    @AddTrace(name = "performOCR", enabled = true)
    private suspend fun performOCR(
        tfResults: List<TFResult>, original: Bitmap, fileName: String?
    ) {
        try {
            var maxW = 0
            var maxH = 0
            val tfCoordinates = HashMap<TFResult, Pair<Int, Int>>()
            for (tfResult in tfResults) {
                if (tfResult.shouldSkip()) continue
                val bounding = tfResult.getBoundingBox()
                maxW = max(maxW, tfResult.evaluatedWidth(bounding, original)) + 10
                val h = min(bounding.height(), original.height - bounding.top)
                val pair = Pair(maxH, maxH + h)
                maxH += max(32, h)
                tfCoordinates[tfResult] = pair
                maxH += 20
            }

            poolManager.initBitmapPool(maxW, maxH)

            val bitmap: Bitmap =
                poolManager.getBitmapFromBitmapPool() ?: return

            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.BLACK)
            var height = 0f

            val findGameInfo =
                tfResults.firstOrNull { MachineConstants.gameConstants.getGameInfoLabelFilter(it.label.toFloat()) }

            val gameInfoBoundingBox = findGameInfo?.getBoundingBox()

            val bucket = MachineConstants.machineLabelProcessor.getBucket(
                LabelUtils.getListOfLabels(tfResults)
            )
            for (tfResult in tfResults) {
                if (tfResult.shouldSkip()) continue
                val croppedBitmap =
                    LabelHelper.getCroppedImage(tfResult, original, gameInfoBoundingBox)
                canvas.drawBitmap(croppedBitmap, 0f, height, null)
                labelForIndividualOCR(tfResult, croppedBitmap)
                height += croppedBitmap.height + 20
                // croppedBitmap.recycle()
            }
            if(BuildConfig.DEBUG) fileName?.let { dataSync.saveBitmap(bitmap, name = it) }
            val response = textRecognizer.process(bitmap, 0).await().textBlocks.toMutableList()




            poolManager.bitmapPool?.putBack(bitmap)

            response.sortWith(compareBy { it.boundingBox?.top })

            for (tfResult in tfResults) {

                if (tfResult.shouldPerformIndividualOcr()) continue
                if (tfResult.shouldSkip()) continue

                val horizontalList = LabelHelper.validateTextBlocks(
                    response,
                    tfCoordinates[tfResult]!!,
                )
                tfResult.ocr =
                    LabelHelper.readTextFromTextBlocks(loggingAgent, tfResult, horizontalList)
            }
            logWithIdentifier(GameHelper.getOriginalGameId()) { builder ->
                builder.setMessage("Local ocr result")
                builder.setOcrInfo(
                    OcrInfoMessage(
                        local = OcrInfoMessage.Local(
                            ocrTextList = tfResults.map {
                                OcrInfoMessage.Ocr(
                                    it.ocr,
                                    it.label.toInt()
                                )
                            }
                        )
                    )
                )
                builder.setCategory(LogCategory.D)
            }

        } catch (e: Exception) {
            //exception for invalid bitmap crops
            e.printStackTrace()
            logException(e)
        }
        return
    }


    private suspend fun labelForIndividualOCR(tfResult: TFResult, bitmap: Bitmap) {
        if (!tfResult.shouldPerformIndividualOcr()) {
            return
        }
        val label = tfResult.label.toFloat()

        if (label == 1f) {
            Log.e("labelForIndividualOcr", "$bitmap")
        }

        if (MachineConstants.machineInputValidator.shouldPerformVisionCallForLabel(label)) {
            MachineConstants.machineInputValidator.processVisionForLabel(label)
            mlKitOCR.visionCallForSingleImage(bitmap, tfResult)
            MachineConstants.machineInputValidator.processedVisionForLabel(label)
        } else {

            val textRecognizerResponse = textRecognizer.process(bitmap, 0).await()

            val response = textRecognizerResponse.textBlocks.toMutableList()

            response.sortWith(compareBy { it.boundingBox?.top })

            tfResult.ocr =
                LabelHelper.readTextFromTextBlocks(loggingAgent, tfResult, response)

            if (label == 1f) {
                Log.e("labelForIndividualOcr", "$bitmap ${tfResult.ocr}")

                saveBitmap(tfResult, bitmap)
            }
        }
    }

    /**
     * Debug Purpose only
     */
    private fun saveBitmap(
        tfResult: TFResult,
        croppedBitmap: Bitmap
    ) {
        if (BuildConfig.DEBUG) {
            File(
                ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "labels/${GamerboardApp.sessionId}"
            ).let {
                if (it.exists().not()) {
                    it.mkdirs()
                }
                val imageSave = File(it, "${sessionId}_${tfResult.ocr}.png")
                val stream = imageSave.outputStream()
                croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()
            }
        }
    }


    ////////////////////////
// Testing
    @AddTrace(name = "processImageFromReader", enabled = true)
    fun testProcessImageFromReader(
        imageBufferObject: ImageBufferObject, isGray: Boolean = false,
    ): LabelingResult? {
        try {
            var original = imageBufferObject.bitmap!!.copy(
                imageBufferObject.bitmap.config, imageBufferObject.bitmap.isMutable
            )
            var gray: Bitmap? = null
            try {
                log(loggingAgent, "Copied buffer to bitmap")
                if (isGray.not()) {
                    gray = Bitmap.createBitmap(
                        display.x, display.y, Bitmap.Config.ARGB_8888
                    )
                    var c: Canvas? = Canvas(gray)
                    val paint = Paint()
                    var cm: ColorMatrix? = ColorMatrix()
                    cm?.setSaturation(0f)
                    var f: ColorMatrixColorFilter? = cm?.let { ColorMatrixColorFilter(it) }
                    paint.colorFilter = f
                    //dataSync.createImageFile(b!!, "${imageIdx++}.jpg")
                    c?.drawBitmap(original, 0f, 0f, paint)
                    cm = null
                    f = null
                    c = null
                }
                val response = labelImage(gray ?: original)
                if (!isGray) {
                    //imageBufferObject.bitmap?.let { imageBufferObject.pool?.putBack(it) }
                    original = null
                    gray = null
                }
                return response
            } catch (ex: RuntimeException) {
                logException(ex, logger = loggingAgent)
//                gray?.recycle()
                //imageBufferObject.bitmap.let { imageBufferObject.pool?.putBack(it) }
                original = null
                return null
            }
            //dataSync.createImageFile(bmp, System.currentTimeMillis().toString()+".jpg")
        } catch (e: Exception) {
            logException(e, logger = loggingAgent)
            return null
        }
    }


    suspend fun testPerformOCR(
        outputs: List<TFResult>, bitmap: Bitmap, fileId: Long,
    ): ImageResultJsonFlat {
        val fileName = "$fileId.jpg"

        BufferProcessorController.controllerBufferProcessor(outputs)

        // this prevents processing of images from third part apps, cheating.
//        dataSync.saveBitmap(bitmap)
        performOCR(outputs, bitmap, fileName)

        val result = ImageResultJson(
            0,
            fileName,
            outputs,
            null
        )


        /*
        val flatResult =
         outputs.forEach {
             val textLabels = LabelUtils.getListOfLabels(flatResult.labels)
             it.ocr = "No ocr performed for label:  $textLabels"
             LabelUtils.testLogGrey("No ocr performed for label:  $textLabels")
         }*/


        return LabelUtils.flattenResultJson(result)
//        val preBucket = getFromLabels(flatResult.labels)
//        MachineLabelProcessor.processInputBuffer(flatResult)


//        saveScreen(bitmap, fileId, outputs, preBucket)
//        saveResult(bitmap, result, fileId)
        /*try {
            bitmap.recycle()
        } catch (e: java.lang.Exception) {
            logException(e)
        }*/
    }
}


//////////////////////////////////////
