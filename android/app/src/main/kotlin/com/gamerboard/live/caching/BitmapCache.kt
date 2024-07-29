package com.gamerboard.live.caching

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.util.SparseArray
import androidx.core.util.containsKey
import com.gamerboard.live.common.PrefsHelper
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.gamestatemachine.games.LabelImageProcessor
import com.gamerboard.live.gamestatemachine.games.bgmi.processor.BGMIKillPerFrameLabelImageProcessor
import com.gamerboard.live.gamestatemachine.games.bgmi.processor.BGMIKillPerGameLabelImageProcessor
import com.gamerboard.live.gamestatemachine.games.bgmi.processor.ContrastLabelImageProcessor
import com.gamerboard.live.gamestatemachine.games.bgmi.processor.DigitContrastLabelImageProcessor
import com.gamerboard.live.gamestatemachine.games.bgmi.processor.InvalidLabelProcessorException
import com.gamerboard.live.models.Resolution
import com.gamerboard.live.models.TFResult
import com.gamerboard.live.utils.ArrayExtensions.entries
import com.gamerboard.live.utils.FeatureKillAlgoFlag
import org.koin.java.KoinJavaComponent.inject

class BitmapCache {

    private val croppedImagesPerLabel = SparseArray<Bitmap>()
    private val prefsHelper: PrefsHelper by inject(PrefsHelper::class.java)

    private val killAlgoFlag: String by lazy {
        val defaultValue = FeatureKillAlgoFlag.BASELINE.key
        prefsHelper.getString(SharedPreferenceKeys.KILL_ALGO_FLAG) ?: defaultValue
    }

    private val imageProcessors = Array(2) { index ->
        when (index) {
            0 -> {
                when (killAlgoFlag) {
                    FeatureKillAlgoFlag.PER_FRAME.key -> BGMIKillPerFrameLabelImageProcessor(100)
                    FeatureKillAlgoFlag.PER_GAME.key -> BGMIKillPerGameLabelImageProcessor()
                    else -> null
                }
            }

            else -> {
                DigitContrastLabelImageProcessor(1000)
            }
        }
    }
    private val contrastLabelImageProcessor: LabelImageProcessor = ContrastLabelImageProcessor(3)

    private var resolution: Resolution? = null

    companion object {
        private val TAG = BitmapCache::class.java.simpleName
    }

    private fun createLabelImageFromBitmap(tfResult: TFResult, from: Bitmap): Bitmap {
        val bounding = tfResult.getBoundingBox()
        val width = tfResult.evaluatedWidth(bounding, from)
        val height = tfResult.evaluatedHeight(bounding, from)
        val canvas = getCanvasForLabel(tfResult, width, height)
        canvas.drawColor(Color.BLACK)
        val srcRect = Rect(
            bounding.left,
            bounding.top,
            bounding.left + width,
            bounding.top + height
        )
        canvas.drawBitmap(
            from, srcRect, Rect(0, 0, width, height), null
        )

        return croppedImagesPerLabel[tfResult.label]!!
    }


    private fun getCanvasForLabel(
        tfResult: TFResult,
        width: Int,
        height: Int
    ): Canvas {
        if (resolution?.width != tfResult.resolution.width || resolution?.height != tfResult.resolution.height) {
            clear()
        }
        resolution = tfResult.resolution

        createBitmapIfNotPresent(tfResult.label, width, height)

        return Canvas(croppedImagesPerLabel[tfResult.label]!!)
    }

    private fun createBitmapIfNotPresent(
        cacheKey: Int,
        width: Int,
        height: Int
    ) {
        if (croppedImagesPerLabel.containsKey(cacheKey).not()) {
            croppedImagesPerLabel[cacheKey] = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888
            )
        }
    }


    suspend fun createLabelImageFromBitmap(
        tfResult: TFResult,
        from: Bitmap,
        gameInfoRect: Rect? = null
    ): Bitmap {

        if (gameInfoRect == null) {
            createLabelImageFromBitmap(tfResult, from)
        } else {
            processGameInfoLabelImage(tfResult, from, gameInfoRect)
        }

        var croppedBitmap = croppedImagesPerLabel[tfResult.label]!!

        croppedBitmap = contrastLabelImageProcessor.newLabelImage(tfResult, croppedBitmap)


        try {
            imageProcessors.forEach {processor ->
                if (processor?.shouldRun(tfResult) == true) {
                    createBitmapIfNotPresent(
                        tfResult.label * processor.keyMultiplier,
                        croppedBitmap.width + 50,
                        croppedBitmap.height
                    )

                    val outBitmap = croppedImagesPerLabel[tfResult.label * processor.keyMultiplier]!!

                    processor.newLabelImage(tfResult, croppedBitmap, outBitmap)

                    croppedBitmap = outBitmap
                }
            }

        } catch (e: InvalidLabelProcessorException) {
            e.printStackTrace()
        }

        return croppedBitmap
    }

    //Note: Need to abstract that inside BGMIRankLabelImageProcessor.
    //So the processors can be run in list. Currently the issue is this function depends on gameInfoRect
    //which is total different label from this tfResult label.
    private fun processGameInfoLabelImage(
        tfResult: TFResult,
        from: Bitmap,
        gameInfoRect: Rect
    ) {
        val bounding = tfResult.getBoundingBox()
        val width = tfResult.evaluatedWidth(bounding, from) + 100
        val height = tfResult.evaluatedHeight(bounding, from)
        val otherHeight = (gameInfoRect.top - bounding.top)
        val ratio: Float = height.toFloat() / otherHeight


        val rankWidth = gameInfoRect.left - bounding.left + 8

        val otherRankWidth = Math.abs(bounding.right - gameInfoRect.left + 100)

        val scaledHeight = height - gameInfoRect.height()

        val canvas = getCanvasForLabel(tfResult, width, height)

        canvas.drawColor(Color.BLACK)

        //eg : Draw #1
        drawRankPart(bounding, gameInfoRect, height, rankWidth, canvas, from)

        //eg : Draw /55
        drawOtherRankPart(
            gameInfoRect,
            bounding,
            height,
            rankWidth,
            otherRankWidth,
            ratio,
            scaledHeight,
            canvas,
            from
        )
    }


    private fun drawOtherRankPart(
        gameInfoRect: Rect,
        bounding: Rect,
        height: Int,
        rankWidth: Int,
        otherRankWidth: Int,
        ratio: Float,
        scaledHeight: Int,
        canvas: Canvas,
        from: Bitmap
    ) {
        val offsetY = -20
        val spacing = 5

        val otherRankPartSrcRect = Rect(
            gameInfoRect.left + 10,
            bounding.top,
            gameInfoRect.left + 10 + Math.abs(bounding.right - gameInfoRect.left + 100),
            bounding.top + height
        )
        val otherRankPartDstRect = Rect(
            rankWidth + spacing + 10,
            offsetY,
            rankWidth + spacing + (otherRankWidth * ratio).toInt() + 10,
            (scaledHeight * ratio).toInt() + spacing + gameInfoRect.height()
        )
        canvas.drawBitmap(
            from,
            otherRankPartSrcRect,
            otherRankPartDstRect,
            null
        )
    }

    private fun drawRankPart(
        bounding: Rect,
        gameInfoRect: Rect,
        height: Int,
        rankWidth: Int,
        canvas: Canvas,
        from: Bitmap
    ) {
        val rankSrcRect = Rect(
            bounding.left,
            bounding.top,
            bounding.left + gameInfoRect.left - bounding.left + 8,
            bounding.top + height
        )
        val rankDstRect = Rect(
            0,
            0,
            rankWidth + 10,
            height
        )
        canvas.drawBitmap(
            from,
            rankSrcRect, rankDstRect, null
        )
    }

    fun clear() {

        croppedImagesPerLabel.entries().forEach {
            try {
                it.second.recycle()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        croppedImagesPerLabel.clear()
    }
}