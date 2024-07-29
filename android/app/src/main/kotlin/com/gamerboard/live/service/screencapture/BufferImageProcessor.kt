package com.gamerboard.live.service.screencapture

import android.graphics.Bitmap
import android.util.Log
import com.gamerboard.live.gamestatemachine.games.GameHelper
import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.stateMachine.*
import com.gamerboard.live.models.TFResult
import com.gamerboard.live.pool.PoolManager
import com.gamerboard.live.service.screencapture.BufferFPS.CPS
import com.gamerboard.live.service.screencapture.BufferFPS.MAX_FPS
import com.gamerboard.live.service.screencapture.BufferFPS.processBuffer
import com.gamerboard.live.service.screencapture.BufferFPS.systemMemory
import com.gamerboard.live.utils.logException
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.log
import com.gamerboard.logger.logWithCategory
import com.gamerboard.logger.logWithIdentifier
import com.gamerboard.logger.loggerWithIdentifier
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import org.koin.java.KoinJavaComponent.inject
import java.io.File

class BufferImageProcessor(
    private val imageProcessor: ImageProcessor?,
    private val capturedImageBuffer: CapturedImageBuffer<ImageBufferObject>,
    var currentBucket: List<Int> = MachineConstants.gameConstants.unknownScreenBucket(),
) {
    private var processedCount: Int = 0
    private var flag = true
    private var logChain = StringBuilder()
    private val poolManager: PoolManager by inject(PoolManager::class.java)

    suspend fun startBuffer(coroutineScope: CoroutineScope, memory: Long?) {
        flag = true
        systemMemory = memory?.let { (memory / (1024 * 1024 * 1024)).toInt() } ?: Int.MAX_VALUE
        CapturedImageBuffer.MAX_BUFFER_SIZE = if (systemMemory > 4) 8 else 4

        logWithIdentifier(GameHelper.getOriginalGameId()) {
            it.setMessage("system-RAM")
            it.addContext("memory", systemMemory)
        }
        while (flag) {
            try {
                VisionStateMachine.visionImageSaver
                if (StateMachine.machine.state !is VerifiedUser) {
                    StateMachine.machine
                }
                coroutineScope.ensureActive()
                getImageFromBuffer()
            } catch (ex: CancellationException) {
                stopBuffer()
            } catch (e: Exception) {
                e.printStackTrace()

                logWithIdentifier(GameHelper.getOriginalGameId()) {
                    it.setMessage("Error while picking images from buffer for process")
                    it.addContext("error", e.message)
                    it.addContext("code", e.cause)
                    it.addContext("trace", e.stackTrace)
                    it.addContext("bufferSize", capturedImageBuffer.size())
                }

                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun stopBuffer() {
        flag = false
        clearBuffer(capturedImageBuffer.copyFramesAsArray())
    }


    private var lastRetry = 0

    @Throws(Exception::class)
    private suspend fun getImageFromBuffer() {

        val images = capturedImageBuffer.copyFramesAsArray()

        if (images.size == 0) {
            lastRetry++
            delay(RETRY_TIME)
            images.clear()
            return
        }

        if (lastRetry > 250) {
            logChain.append("Bfr=0, ${lastRetry}x${RETRY_TIME}ms |")
            logChain.append("fps:$MAX_FPS, cps:$CPS, Bfr>0 |")
        }
        lastRetry = 0

        if (processBuffer == ProcessBuffer.PickOldest) {
            logChain.append("pickOld |")
            images.forEach { image ->
                val labelingResult = sendForProcess(image)
                labelingResult?.apply {
                    if (labelingResult.outputs.isNotEmpty()) {
                        processedCount++
                        //logger.oneTimeLog(TAG_TEST_CAPTURE, 2, "TP: $processedCount Img")
                        imageProcessor?.processLabels(
                            labelingResult.outputs,
                            labelingResult.source,
                            labelingResult.ttLabelling,
                            image.fileName,
                            false
                        )
                        logWithCategory(
                            loggerWithIdentifier(GameHelper.getOriginalGameId()),
                            message = logChain.toString(),
                            category = LogCategory.SM
                        )
                        logChain.clear()
                    }
                }
                if (labelingResult == null)
                    logChain.append("X ${image.fileName} ? X |")
                tryRecycle(labelingResult)

            }

            CPS = Int.MAX_VALUE.toFloat()
            MAX_FPS =
                if (capturedImageBuffer.size() == CapturedImageBuffer.MAX_BUFFER_SIZE) 2.0f else 3.0f
            processBuffer = ProcessBuffer.PickOldest
            // process next with no delay
        }

        if (processBuffer == ProcessBuffer.PickLatest) {
            var shouldWait = false

            logChain.append("pickLatest |")

            val latestImage: ImageBufferObject = images.last()
            latestImage.apply {
                val labelingResult = sendForProcess(images)
                labelingResult?.apply {
                    if (labelingResult.outputs.isNotEmpty()) {
                        val bucket = MachineConstants.machineLabelProcessor.getBucket(
                            LabelUtils.getListOfLabels(outputs)
                        )

                        if (bucket == MachineConstants.gameConstants.gameScreenBucket()) {
                            images.forEach {
                                tryRecycle(it, null)
                            }
                            images.clear()
                            shouldWait = true
                        } else if (bucket == MachineConstants.gameConstants.resultRankRating() || bucket == MachineConstants.gameConstants.resultRankKills()
                            || bucket == MachineConstants.gameConstants.unknownScreenBucket()
                        ) {
                            processBuffer = ProcessBuffer.PickOldest
                        }

                        processedCount++
                        logWithCategory(
                            loggerWithIdentifier(GameHelper.getOriginalGameId()),
                            message = logChain.toString(),
                            category = LogCategory.SM
                        )
                        logChain.clear()
                    }
                }
                if (labelingResult == null) {
                    processBuffer = ProcessBuffer.PickOldest
                    logChain.append("X ${latestImage.fileName} ? X |")
                }
                tryRecycle(labelingResult)
            }
            if (shouldWait)
                delay((1000 / CPS).toLong())
        }
        clearBuffer(images)
        return
    }

    fun clear() {
        logChain.clear()
    }

    fun clearBuffer(images: ArrayList<ImageBufferObject>?) {
        var listOfImages = images
        if (images == null)
            listOfImages = capturedImageBuffer.copyFramesAsArray()
        listOfImages?.forEach {
            tryRecycle(it, null)
        }
        listOfImages?.clear()

        poolManager.bufferedBitmapPool?.let { capturedImageBuffer.cleanUnusedFromPool(it) }

        logChain.clear()
    }


    private fun tryRecycle(labelingResult: LabelingResult?) {
        labelingResult?.outputs?.forEach { it.clear() }
        // tryRecycle(null, labelingResult?.source)
    }

    private fun tryRecycle(obj: ImageBufferObject?, bitmap: Bitmap?) {
        try {
            obj?.bitmap?.let { obj.pool?.putBack(it) }
            obj?.path?.let { File(it).delete() }
        } catch (e: Exception) {
            Log.d("memory_leak", "Bitmap already recycled!")
        }
    }

    private fun sendForProcess(imageBufferObject: ImageBufferObject): LabelingResult? {
        try {
            val labelingResult = if (debugMachine != DEBUGGER.DISABLED) {
                imageProcessor?.testProcessImageFromReader(imageBufferObject)
            } else {
                imageProcessor?.processImageFromReader(imageBufferObject)
            }

            if (labelingResult != null) {
                val original =
                    LabelUtils.getListOfLabels(labelingResult.outputs)
                currentBucket = original
                printBucket(MachineConstants.machineLabelProcessor.getBucket(original), original)
            } else printBucket(MachineConstants.gameConstants.unknownScreenBucket())

            return labelingResult
        } catch (ex: Exception) {
            logException(ex)
        }
        return null
    }

    private fun sendForProcess(images: ArrayList<ImageBufferObject>): LabelingResult? {
        var labelingResult: LabelingResult? = null
        var idx = images.size - 1
        while (idx >= 0 && labelingResult == null) {
            labelingResult = if (debugMachine != DEBUGGER.DISABLED) {
                imageProcessor?.testProcessImageFromReader(images[idx])
            } else {
                imageProcessor?.processImageFromReader(images[idx])
            }
            idx--
            if (labelingResult == null && idx >= 0) {
                logChain.append("Bfr <<1 ${images[idx].fileName} |")
            } else if (labelingResult == null) {
                logChain.append("X ${images[idx].fileName} ? X |")
            }
        }

        if (labelingResult != null) {
            val original =
                LabelUtils.getListOfLabels(labelingResult.outputs)
            printBucket(MachineConstants.machineLabelProcessor.getBucket(original), original)
        } else printBucket(MachineConstants.gameConstants.unknownScreenBucket())

        return labelingResult
    }


    private fun printBucket(
        cur: List<Int>,
        original: List<Int> = MachineConstants.gameConstants.unknownScreenBucket(),
    ) {
        MachineConstants.machineLabelProcessor.shouldShoLoader(cur, original)
        Log.d("buffer_size", capturedImageBuffer.size().toString())
        when (cur) {
            MachineConstants.gameConstants.resultRankRating() -> {
                logChain.append("X[] Rank Rating X |")
                log {
                    it.setCategory(LogCategory.SM)
                    it.setBucketName("Rank Rating")
                }
            }
//            MachineConstants.gameConstants.rating() -> {
//                logChain += "X[] Rating X |"
//            }
            MachineConstants.gameConstants.resultRankKills() -> {
                logChain.append("X[] Rank Kills X |")
                log {
                    it.setCategory(LogCategory.SM)
                    it.setBucketName("Rank Kills")
                }
            }

            MachineConstants.gameConstants.homeScreenBucket() -> {
                logChain.append("X[] Home X |")
                log {
                    it.setCategory(LogCategory.SM)
                    it.setBucketName("Home")
                }
            }

            MachineConstants.gameConstants.waitingScreenBucket() -> {
                logChain.append("X[] Waiting X |")
                log {
                    it.setCategory(LogCategory.SM)
                    it.setBucketName("Waiting")
                }
            }

            MachineConstants.gameConstants.loginScreenBucket() -> {
                logChain.append("X[] Login X |")
                log {
                    it.setCategory(LogCategory.SM)
                    it.setBucketName("Login")
                }
            }

            MachineConstants.gameConstants.gameScreenBucket() -> {
                logChain.append("X[] Game X |")
                log {
                    it.setCategory(LogCategory.SM)
                    it.setBucketName("Game")
                }
            }

            MachineConstants.gameConstants.gameEndScreen() -> {
                logChain.append("X[] Game End X |")
                log {
                    it.setCategory(LogCategory.SM)
                    it.setBucketName("Game end")
                }
            }

            else -> {
                logChain.append("X[] Un-Known? X |")
                log {
                    it.setCategory(LogCategory.SM)
                    it.setBucketName("Un-Known")
                }
            }
        }
    }

    companion object {
        private const val RETRY_TIME: Long = 100
    }
}


data class LabelingResult(
    val outputs: List<TFResult>,
    var source: Bitmap,
    val ttLabelling: Long = 0L,
)

enum class ProcessBuffer { PickOldest, PickLatest }
object BufferFPS {
    var CPS: Float = 2.0f
    var MAX_FPS: Float = 2.0f
    var processBuffer: ProcessBuffer = ProcessBuffer.PickOldest
    var systemMemory = Int.MAX_VALUE
}

object BufferProcessorController {
    fun controllerBufferProcessor(labels: List<TFResult>) {

        when (MachineConstants.machineLabelProcessor.getBucket(LabelUtils.getListOfLabels(labels))) {

            (MachineConstants.gameConstants.homeScreenBucket()) -> {
                CPS = Int.MAX_VALUE.toFloat()
                MAX_FPS = 1.0f
                processBuffer = ProcessBuffer.PickOldest
                //showToast("Process with Default FPS, fetch oldest!     FPS:$MAX_FPS, CPS:$CPS", 0)
            }

            (MachineConstants.gameConstants.gameScreenBucket()) -> {

                if (StateMachine.machine.state is State.GameStarted) {
                    CPS = 3.0f
                    MAX_FPS = if (systemMemory > 4) 3.0f else 2.0f
                    processBuffer = ProcessBuffer.PickLatest
                    //showToast("Increase FPS, reduce processing for In-Game, fetch latest! FPS:$MAX_FPS, CPS:$CPS", 1)
                }
            }

            (MachineConstants.gameConstants.resultRankRating()) -> {
                CPS = Int.MAX_VALUE.toFloat()
                MAX_FPS = 3.0f
                processBuffer = ProcessBuffer.PickOldest
                //showToast("Process whole array, for Result until start  FPS:$MAX_FPS, CPS:$CPS", 2)
            }

            (MachineConstants.gameConstants.resultRankKills()) -> {
                CPS = Int.MAX_VALUE.toFloat()
                MAX_FPS = 3.0f
                processBuffer = ProcessBuffer.PickOldest
                //showToast("Process whole array, for Result until start  FPS:$MAX_FPS, CPS:$CPS", 2)
            }

            else -> {

            }
        }
    }
}